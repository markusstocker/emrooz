/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.io.licor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openrdf.model.URI;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.io.AbstractSensorObservationReader;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;
import fi.uef.envi.emrooz.vocabulary.SWEETMatrCompound;
import fi.uef.envi.emrooz.vocabulary.SWEETMatrOrganicCompound;
import fi.uef.envi.emrooz.vocabulary.SWEETPropMass;

/**
 * <p>
 * Title: GHGSensorObservationReader
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

public class GHGSensorObservationReader extends AbstractSensorObservationReader {

	private static final String COLUMN_SEPARATOR = "\t";
	private static final int DATA_ROW = 8;
	private static final int TIMEZONE_ROW = 6;
	private static final int TIMEZONE_COL = 1;
	private static final int DATE_COL = 6;
	private static final int TIME_COL = 7;
	private static final int CARBON_DIOXIDE_COL = 10; // Density in mmol m-3
	private static final int WATER_COL = 12; // Density in mmol m-3
	private static final int METHANE_COL = 33; // Density in mmol m-3
	private static final double SAMPLING_FREQUENCY = 10.0;

	// The gas analyzers make in-situ *density* [mmol m-3] measurement of the
	// gas (CO2, CH4, H2O) (Source:
	// http://www.licor.com/env/pdf/gas_analyzers/7700/7700_brochure.pdf)

	private static final FeatureOfInterest carbonDioxide = new FeatureOfInterest(
			SWEETMatrCompound.CarbonDioxide);
	private static final FeatureOfInterest water = new FeatureOfInterest(
			SWEETMatrCompound.Water);
	private static final FeatureOfInterest methane = new FeatureOfInterest(
			SWEETMatrOrganicCompound.Methane);
	private static final Property density = new Property(SWEETPropMass.Density,
			carbonDioxide, water, methane);

	private Sensor carbonDioxideAndWaterAnalyzer;
	private Sensor methaneAnalyzer;

	private SensorObservation next;
	private Queue<File> files;
	private Queue<SensorObservation> observations;

	private static final Logger log = Logger
			.getLogger(GHGSensorObservationReader.class.getName());

	public GHGSensorObservationReader(File file, URI ns,
			Sensor carbonDioxideAndWaterAnalyzer, Sensor methaneAnalyzer) {
		super(ns);

		if (file == null)
			throw new NullPointerException("[file = null]");
		if (carbonDioxideAndWaterAnalyzer == null)
			throw new NullPointerException("[carbonDioxideAnalyzer = null]");
		if (methaneAnalyzer == null)
			throw new NullPointerException("[methaneAnalyzer = null]");

		this.carbonDioxideAndWaterAnalyzer = carbonDioxideAndWaterAnalyzer;
		this.methaneAnalyzer = methaneAnalyzer;

		this.files = new LinkedList<File>();
		this.observations = new LinkedList<SensorObservation>();

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

			List<String> lines = readFile(file);

			DateTimeZone dateTimeZone = getDateTimeZone(lines.get(TIMEZONE_ROW));

			for (int i = DATA_ROW; i < lines.size(); i++) {
				String line = lines.get(i);
				String[] cols = line.split(COLUMN_SEPARATOR);

				if (cols.length == 0) {
					if (log.isLoggable(Level.WARNING))
						log.warning("Empty data line [line = " + line
								+ "; file = " + file + "]");
					continue;
				}

				DateTime dateTime = getDateTime(cols[DATE_COL], cols[TIME_COL],
						dateTimeZone);

				observations.add(getSensorObservation(
						carbonDioxideAndWaterAnalyzer, density, carbonDioxide,
						dateTime, Double.valueOf(cols[CARBON_DIOXIDE_COL])));
				observations.add(getSensorObservation(
						carbonDioxideAndWaterAnalyzer, density, water,
						dateTime, Double.valueOf(cols[WATER_COL])));
				observations.add(getSensorObservation(methaneAnalyzer, density,
						methane, dateTime, Double.valueOf(cols[METHANE_COL])));
			}

			next = observations.poll();
			return true;
		}

		return false;
	}

	@Override
	public SensorObservation next() {
		return next;
	}

	private void listFiles(File file) {
		if (file.isFile()) {
			// It is a file
			considerFile(file);
			return;
		}

		// It is a directory
		File[] lof = file.listFiles();
		Arrays.sort(lof);

		for (File f : lof)
			considerFile(f);
	}

	private void considerFile(File file) {
		String fileName = file.getName();

		if (FilenameUtils.getExtension(fileName).equals("ghg"))
			files.add(file);
	}

	private List<String> readFile(File file) {
		List<String> ret = new ArrayList<String>();

		String fileName = file.getName();
		String fileBaseName = FilenameUtils.getBaseName(fileName);
		String dataFileName = fileBaseName + ".data";

		try {
			String line;
			ZipFile zip = new ZipFile(file);

			try {
				for (Enumeration<? extends ZipEntry> e = zip.entries(); e
						.hasMoreElements();) {
					ZipEntry entry = e.nextElement();

					String zipEntryName = entry.getName();

					if (!zipEntryName.equals(dataFileName))
						continue;

					try (BufferedReader br = new BufferedReader(
							new InputStreamReader(zip.getInputStream(entry)))) {
						while ((line = br.readLine()) != null) {
							ret.add(line);
						}
					}

				}
			} catch (IOException e2) {
				throw new RuntimeException(e2);
			} finally {
				zip.close();
			}
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}

		if (ret.isEmpty()) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Failed to read lines of file [fileName = "
						+ fileName + "; dataFileName = " + dataFileName + "]");
		}

		return ret;
	}

	private DateTimeZone getDateTimeZone(String line) {
		if (line == null)
			return null;
		if (line.length() == 0)
			return null;

		DateTimeZone ret = null;
		String[] cols = line.split(COLUMN_SEPARATOR);
		String col = cols[TIMEZONE_COL];

		Pattern p = Pattern.compile("(GMT\\+)(\\d+)");
		Matcher m = p.matcher(col);

		if (m.find()) {
			String hoursOffset = m.group(2);
			ret = DateTimeZone.forOffsetHours(Integer.valueOf(hoursOffset));
		} else {
			if (log.isLoggable(Level.WARNING))
				log.warning("Failed to read date time zone [col = " + col + "]");
		}

		return ret;
	}

	private DateTime getDateTime(String date, String time, DateTimeZone zone) {
		// Example: date = 2015-01-06; time = 16:59:47:000
		String[] dateEl = date.split("-");
		String[] timeEl = time.split(":");

		int year = Integer.valueOf(dateEl[0]);
		int month = Integer.valueOf(dateEl[1]);
		int day = Integer.valueOf(dateEl[2]);
		int hour = Integer.valueOf(timeEl[0]);
		int min = Integer.valueOf(timeEl[1]);
		int sec = Integer.valueOf(timeEl[2]);
		int msec = Integer.valueOf(timeEl[3]);

		return new DateTime(year, month, day, hour, min, sec, msec, zone);
	}

	private SensorObservation getSensorObservation(Sensor sensor,
			Property property, FeatureOfInterest feature, DateTime dateTime,
			Double value) {
		return new SensorObservation(_id(), sensor, property, feature,
				new SensorOutput(_id(), new QuantityValue(_id(), value,
						new Unit(QUDTUnit.MilliMolePerCubicMeter))),
				new Instant(_id(), dateTime));
	}

	public static void main(String[] args) {
		if (args.length == 0)
			help();

		File file = null;
		URI ns = null;
		URI carbonDioxideAndWaterAnalyzerId = null;
		URI methaneAnalyzerId = null;
		File knowledgeStoreFile = null;
		String dataStoreHost = "localhost";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f"))
				file = new File(args[++i]);
			if (args[i].equals("-ns"))
				ns = vf.createURI(args[++i]);
			if (args[i].equals("-ca"))
				carbonDioxideAndWaterAnalyzerId = vf.createURI(args[++i]);
			if (args[i].equals("-ma"))
				methaneAnalyzerId = vf.createURI(args[++i]);
			if (args[i].equals("-ks"))
				knowledgeStoreFile = new File(args[++i]);
			if (args[i].equals("-ds"))
				dataStoreHost = args[++i];
		}

		if (file == null || ns == null
				|| carbonDioxideAndWaterAnalyzerId == null
				|| methaneAnalyzerId == null || knowledgeStoreFile == null)
			help();

		Sensor carbonDioxideAndWaterAnalyzer = new Sensor(
				carbonDioxideAndWaterAnalyzerId, density,
				new MeasurementCapability(_id(ns), new Frequency(_id(ns),
						new QuantityValue(_id(ns), SAMPLING_FREQUENCY,
								new Unit(QUDTUnit.Hertz)))));
		Sensor methaneAnalyzer = new Sensor(methaneAnalyzerId, density,
				new MeasurementCapability(_id(ns), new Frequency(_id(ns),
						new QuantityValue(_id(ns), SAMPLING_FREQUENCY,
								new Unit(QUDTUnit.Hertz)))));

		SesameKnowledgeStore ks = new SesameKnowledgeStore(new SailRepository(
				new MemoryStore(knowledgeStoreFile)));
		ks.addSensor(carbonDioxideAndWaterAnalyzer);
		ks.addSensor(methaneAnalyzer);

		CassandraDataStore ds = new CassandraDataStore(dataStoreHost);

		Emrooz e = new Emrooz(ks, ds);

		long start = System.currentTimeMillis();

		status("Processing: " + file);

		GHGSensorObservationReader reader = new GHGSensorObservationReader(
				file, ns, carbonDioxideAndWaterAnalyzer, methaneAnalyzer);

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

		sb.append(GHGSensorObservationReader.class.getName() + LINE_SEPARATOR);
		sb.append("Arguments:" + LINE_SEPARATOR);
		sb.append("  -f  [file name]       Name of the *.ghg file or a directory containing files (10 Hz)"
				+ LINE_SEPARATOR);
		sb.append("  -ns [URI]             Name space for sensor observations (e.g. http://example.org)"
				+ LINE_SEPARATOR);
		sb.append("  -ca [URI]             The URI identifier of the CO2/H2O analyzer"
				+ LINE_SEPARATOR);
		sb.append("  -ma [URI]             The URI identifier of the CH4 analyzer"
				+ LINE_SEPARATOR);
		sb.append("  -ks [directory name]  Knowledge store data directory (e.g. /tmp/ks)"
				+ LINE_SEPARATOR);
		sb.append("  -ds [host name]       Data store host name (default: localhost)"
				+ LINE_SEPARATOR);

		System.out.println(sb);

		System.exit(0);
	}
	
}
