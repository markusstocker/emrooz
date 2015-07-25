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

public class GHGSensorObservationReader extends
		AbstractGHGObservationReader<SensorObservation> {

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

			String fileName = file.getName();
			String fileBaseName = FilenameUtils.getBaseName(fileName);
			String dataFileName = fileBaseName + ".data";
			
			List<String> lines = readFile(file, fileName, dataFileName);

			DateTimeZone dateTimeZone = getDateTimeZone(
					lines.get(GAS_ANALYZER_TIMEZONE_ROW),
					GAS_ANALYZER_TIMEZONE_COL);

			for (int i = GAS_ANALYZER_DATA_ROW; i < lines.size(); i++) {
				String line = lines.get(i);
				String[] cols = line.split(COLUMN_SEPARATOR);

				if (cols.length == 0) {
					if (log.isLoggable(Level.WARNING))
						log.warning("Empty data line [line = " + line
								+ "; file = " + file + "]");
					continue;
				}

				DateTime dateTime = getDateTime(cols[GAS_ANALYZER_DATE_COL],
						cols[GAS_ANALYZER_TIME_COL], dateTimeZone);

				observations
						.add(getSensorObservation(
								carbonDioxideAndWaterAnalyzer,
								density,
								carbonDioxide,
								dateTime,
								Double.valueOf(cols[GAS_ANALYZER_CARBON_DIOXIDE_MOLAR_CONCENTRATION_COL])));
				observations
						.add(getSensorObservation(
								carbonDioxideAndWaterAnalyzer,
								density,
								water,
								dateTime,
								Double.valueOf(cols[GAS_ANALYZER_WATER_VAPOR_MOLAR_CONCENTRATION_COL])));
				observations
						.add(getSensorObservation(
								methaneAnalyzer,
								density,
								methane,
								dateTime,
								Double.valueOf(cols[GAS_ANALYZER_METHANE_MOLAR_CONCENTRATION_COL])));
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
						new QuantityValue(_id(ns),
								GAS_ANALYZER_SAMPLING_FREQUENCY, new Unit(
										QUDTUnit.Hertz)))));
		Sensor methaneAnalyzer = new Sensor(methaneAnalyzerId, density,
				new MeasurementCapability(_id(ns), new Frequency(_id(ns),
						new QuantityValue(_id(ns),
								GAS_ANALYZER_SAMPLING_FREQUENCY, new Unit(
										QUDTUnit.Hertz)))));

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
