/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.io.licor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openrdf.model.URI;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.qb.ComponentProperty;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueDouble;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueInteger;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueLong;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueString;
import fi.uef.envi.emrooz.entity.qb.Dataset;
import fi.uef.envi.emrooz.entity.qb.DatasetObservation;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;

/**
 * <p>
 * Title: GHGDatasetObservationReader
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project: Emrooz
 * </p>
 * <p>
 * Copyright: Copyright (C) 2015
 * </p>
 * 
 * @author Markus Stocker
 */

public class GHGDatasetObservationReader extends
		AbstractGHGObservationReader<DatasetObservation> {

	private DatasetObservation next;

	private Queue<DatasetObservation> observations;

	private URI gasDatasetId;
	private URI biometDatasetId;

	private static final Logger log = Logger
			.getLogger(GHGDatasetObservationReader.class.getName());

	public GHGDatasetObservationReader(File file, URI ns, URI gasDatasetId,
			URI biometDatasetId) {
		super(ns);

		if (file == null)
			throw new NullPointerException("[file = null]");
		if (gasDatasetId == null)
			throw new NullPointerException("[gasDatasetId = null]");
		if (biometDatasetId == null)
			throw new NullPointerException("[biometDatasetId = null]");

		this.gasDatasetId = gasDatasetId;
		this.biometDatasetId = biometDatasetId;
		this.observations = new LinkedList<DatasetObservation>();

		listFiles(file);
	}

	@Override
	public boolean hasNext() {
		if (!observations.isEmpty()) {
			next = observations.poll();
			return true;
		}

		if (!files.isEmpty()) {
			File file = files.poll();

			if (log.isLoggable(Level.INFO))
				log.info("Processing file [file = " + file + "]");

			String fileName = file.getName();
			String fileBaseName = FilenameUtils.getBaseName(fileName);
			String gasDataFileName = fileBaseName + ".data";
			String biometDataFileName = fileBaseName + "-biomet.data";

			processFile(file, fileName, gasDataFileName, gasDatasetId,
					gasProperties, gasPropertyTypes, GAS_ANALYZER_TIMEZONE_ROW,
					GAS_ANALYZER_TIMEZONE_COL, GAS_ANALYZER_DATA_ROW,
					GAS_ANALYZER_DATE_COL, GAS_ANALYZER_TIME_COL);

			processFile(file, fileName, biometDataFileName, biometDatasetId,
					biometProperties, biometPropertyTypes, BIOMET_TIMEZONE_ROW,
					BIOMET_TIMEZONE_COL, BIOMET_DATA_ROW, BIOMET_DATE_COL,
					BIOMET_TIME_COL);

			next = observations.poll();

			return true;
		}

		return false;
	}

	@Override
	public DatasetObservation next() {
		return next;
	}

	private void processFile(File file, String fileName, String dataFileName,
			URI datasetId, ComponentProperty[] properties, String[] types,
			int timeZoneRow, int timeZoneCol, int dataRow, int dateCol,
			int timeCol) {
		List<String> lines = readFile(file, fileName, dataFileName);
		DateTimeZone dateTimeZone = getDateTimeZone(lines.get(timeZoneRow),
				timeZoneCol);

		for (int i = dataRow; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] cols = line.split(COLUMN_SEPARATOR);

			if (cols.length == 0) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Empty data line [line = " + line + "; file = "
							+ file + "]");
				continue;
			}

			DateTime dateTime = getDateTime(cols[dateCol], cols[timeCol],
					dateTimeZone);

			observations.add(getDatasetObservation(datasetId, dateTime, cols,
					properties, types, dataFileName, i));
		}
	}

	private DatasetObservation getDatasetObservation(URI datasetId,
			DateTime dateTime, String[] cols, ComponentProperty[] properties,
			String[] types, String dataFileName, int nrow) {
		DatasetObservation ret = new DatasetObservation(_id(), datasetId,
				new Instant(_id(), dateTime));

		for (int i = 1; i < cols.length; i++) {
			String type = null;
			ComponentProperty property = null;
			String value = null;

			try {
				type = types[i];
				property = properties[i];
				value = cols[i];
			} catch (ArrayIndexOutOfBoundsException e) {
				log.severe("Index out of bounds [i = " + i + "; length = "
						+ cols.length + "]");
				continue;
			}

			value = value.replaceAll("\\s", "");

			if (type.equals("Double")) {
				try {
					ret.addComponent(
							property,
							new ComponentPropertyValueDouble(Double
									.valueOf(value)));
				} catch (NumberFormatException e) {
					ret.addComponent(property,
							new ComponentPropertyValueDouble(Double.NaN));
				}
			} else if (type.equals("String")) {
				ret.addComponent(property, new ComponentPropertyValueString(
						value));
			} else if (type.equals("Long")) {
				try {
					ret.addComponent(property, new ComponentPropertyValueLong(
							Long.valueOf(value)));
				} catch (NumberFormatException e) {
					ret.addComponent(property,
							new ComponentPropertyValueDouble(Double.NaN));
				}
			} else if (type.equals("Integer")) {
				try {
					ret.addComponent(
							property,
							new ComponentPropertyValueInteger(Integer
									.valueOf(value)));
				} catch (NumberFormatException e) {
					ret.addComponent(property,
							new ComponentPropertyValueDouble(Double.NaN));
				}
			} else {
				if (log.isLoggable(Level.SEVERE))
					log.severe("Unsupported component property value type [type = "
							+ type + "]");
			}
		}

		return ret;
	}

	public static void main(String[] args) {
		if (args.length == 0)
			help();

		File file = null;
		URI ns = null;
		URI gasDatasetId = null;
		URI biometDatasetId = null;
		File knowledgeStoreFile = null;
		String dataStoreHost = "localhost";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f"))
				file = new File(args[++i]);
			if (args[i].equals("-ns"))
				ns = vf.createURI(args[++i]);
			if (args[i].equals("-gd"))
				gasDatasetId = vf.createURI(args[++i]);
			if (args[i].equals("-bd"))
				biometDatasetId = vf.createURI(args[++i]);
			if (args[i].equals("-ks"))
				knowledgeStoreFile = new File(args[++i]);
			if (args[i].equals("-ds"))
				dataStoreHost = args[++i];
		}

		if (file == null || ns == null || gasDatasetId == null
				|| biometDatasetId == null || knowledgeStoreFile == null)
			help();

		SesameKnowledgeStore ks = new SesameKnowledgeStore(new SailRepository(
				new NativeStore(knowledgeStoreFile)));
		ks.addDataset(new Dataset(gasDatasetId, new QuantityValue(_id(ns),
				GAS_ANALYZER_SAMPLING_FREQUENCY, new Unit(QUDTUnit.Hertz))));
		ks.addDataset(new Dataset(biometDatasetId, new QuantityValue(_id(ns),
				BIOMET_SAMPLING_FREQUENCY, new Unit(QUDTUnit.Hertz))));

		CassandraDataStore ds = new CassandraDataStore(dataStoreHost);

		Emrooz e = new Emrooz(ks, ds);

		long start = System.currentTimeMillis();

		status("Processing: " + file);

		GHGDatasetObservationReader reader = new GHGDatasetObservationReader(
				file, ns, gasDatasetId, biometDatasetId);

		long numOfObservations = 0;

		while (reader.hasNext()) {
			e.add(reader.next());
			numOfObservations++;
		}

		long end = System.currentTimeMillis();

		e.close();

		summary(start, end, numOfObservations, dataStoreHost);
	}

	private static void help() {
		StringBuffer sb = new StringBuffer();

		sb.append(GHGDatasetObservationReader.class.getName() + LINE_SEPARATOR);
		sb.append("Arguments:" + LINE_SEPARATOR);
		sb.append("  -f  [file name]       Name of the *.ghg file or a directory containing files"
				+ LINE_SEPARATOR);
		sb.append("  -ns [URI]             Name space for dataset observations (e.g. http://example.org)"
				+ LINE_SEPARATOR);
		sb.append("  -gd [URI]             The URI identifier for the gas dataset"
				+ LINE_SEPARATOR);
		sb.append("  -bd [URI]             The URI identifier for the biomet dataset"
				+ LINE_SEPARATOR);
		sb.append("  -ks [directory name]  Knowledge store data directory (e.g. /tmp/ks)"
				+ LINE_SEPARATOR);
		sb.append("  -ds [host name]       Data store host name (default: localhost)"
				+ LINE_SEPARATOR);

		System.out.println(sb);

		System.exit(0);
	}

}
