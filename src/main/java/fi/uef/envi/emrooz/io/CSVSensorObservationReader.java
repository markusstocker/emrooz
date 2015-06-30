/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.io;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
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

/**
 * <p>
 * Title:
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

public class CSVSensorObservationReader extends AbstractSensorObservationReader {

	private Iterator<String> lines;
	private URI sensorId;
	private URI propertyId;
	private URI featureId;
	private URI unitId;
	private SensorObservation next;

	private static final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();
	private static final Logger log = Logger
			.getLogger(CSVSensorObservationReader.class.getName());

	public CSVSensorObservationReader(File file, URI ns, URI sensorId,
			URI propertyId, URI featureId, URI unitId) {
		super(ns);

		if (file == null)
			throw new NullPointerException("[file = null]");
		if (sensorId == null)
			throw new NullPointerException("[sensorId = null]");
		if (propertyId == null)
			throw new NullPointerException("[propertyId = null]");
		if (featureId == null)
			throw new NullPointerException("[featureId = null]");
		if (unitId == null)
			throw new NullPointerException("[unitId = null]");

		this.sensorId = sensorId;
		this.propertyId = propertyId;
		this.featureId = featureId;
		this.unitId = unitId;

		try {
			lines = FileUtils.readLines(file).iterator();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext() {
		next = null;

		while (next == null) {
			if (!lines.hasNext())
				return false;

			String line = lines.next();

			String fields[] = line.split(",");

			if (fields.length != 6) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Expected line with six fields [line = " + line
							+ "]");
				continue;
			}

			URI sensorId = vf.createURI(fields[0]);
			URI propertyId = vf.createURI(fields[1]);
			URI featureId = vf.createURI(fields[2]);
			DateTime time = dtf.parseDateTime(fields[3]);
			Double value = Double.valueOf(fields[4]);
			URI unitId = vf.createURI(fields[5]);

			if (!this.sensorId.equals(sensorId)
					|| !this.propertyId.equals(propertyId)
					|| !this.featureId.equals(featureId)
					|| !this.unitId.equals(unitId)) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Expected [sensorId = " + this.sensorId
							+ "; propertyId = " + this.propertyId
							+ "; featureId = " + this.featureId + "; unitId = "
							+ this.unitId + "]");
				log.warning("Actual [sensorId = " + sensorId
						+ "; propertyId = " + propertyId + "; featureId = "
						+ featureId + "; unitId = " + unitId + "]");
				continue;
			}

			next = new SensorObservation(_id(), new Sensor(sensorId),
					new Property(propertyId), new FeatureOfInterest(featureId),
					new SensorOutput(_id(), new QuantityValue(_id(), value,
							new Unit(unitId))), new Instant(_id(), time));
		}

		if (next == null)
			return false;

		return true;
	}

	@Override
	public SensorObservation next() {
		return next;
	}

	public static void main(String[] args) {
		if (args.length == 0)
			help();

		File file = null;
		URI ns = null;
		URI sensorId = null;
		URI propertyId = null;
		URI featureId = null;
		Double samplingFrequency = null;
		URI unitId = null;
		File knowledgeStoreFile = null;
		String dataStoreHost = "localhost";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f"))
				file = new File(args[++i]);
			if (args[i].equals("-ns"))
				ns = vf.createURI(args[++i]);
			if (args[i].equals("-sid"))
				sensorId = vf.createURI(args[++i]);
			if (args[i].equals("-pid"))
				propertyId = vf.createURI(args[++i]);
			if (args[i].equals("-fid"))
				featureId = vf.createURI(args[++i]);
			if (args[i].equals("-sf"))
				samplingFrequency = Double.valueOf(args[++i]);
			if (args[i].equals("-uid"))
				unitId = vf.createURI(args[++i]);
			if (args[i].equals("-ks"))
				knowledgeStoreFile = new File(args[++i]);
			if (args[i].equals("-ds"))
				dataStoreHost = args[++i];
		}

		if (file == null || ns == null || sensorId == null
				|| propertyId == null || featureId == null
				|| samplingFrequency == null || unitId == null
				|| knowledgeStoreFile == null)
			help();

		SesameKnowledgeStore ks = new SesameKnowledgeStore(new SailRepository(
				new MemoryStore(knowledgeStoreFile)));
		ks.addSensor(new Sensor(sensorId, new Property(propertyId,
				new FeatureOfInterest(featureId)), new MeasurementCapability(
				_id(ns), new Frequency(_id(ns), new QuantityValue(_id(ns),
						samplingFrequency, new Unit(QUDTUnit.Hertz))))));

		CassandraDataStore ds = new CassandraDataStore(dataStoreHost);

		Emrooz e = new Emrooz(ks, ds);

		long start = System.currentTimeMillis();
		
		status("Processing: " + file);
		
		CSVSensorObservationReader reader = new CSVSensorObservationReader(
				file, ns, sensorId, propertyId, featureId, unitId);

		long numOfObservations = 0;
		
		while (reader.hasNext()) {
			e.add(reader.next());
		}
		
		long end = System.currentTimeMillis();

		e.close();
		
		summary(start, end, numOfObservations, dataStoreHost);
	}

	private static void help() {
		StringBuffer sb = new StringBuffer();

		sb.append(CSVSensorObservationReader.class.getName() + LINE_SEPARATOR);
		sb.append("Arguments:" + LINE_SEPARATOR);
		sb.append("  -f   [file name]       Name of the CSV file"
				+ LINE_SEPARATOR);
		sb.append("  -ns  [URI]             Name space for sensor observations (e.g. http://example.org)"
				+ LINE_SEPARATOR);
		sb.append("  -sid [URI]             The URI identifier for the sensor"
				+ LINE_SEPARATOR);
		sb.append("  -pid [URI]             The URI identifier for the property"
				+ LINE_SEPARATOR);
		sb.append("  -fid [URI]             The URI identifier for the feature"
				+ LINE_SEPARATOR);
		sb.append("  -sf  [number]          The sampling frequency [Hz]"
				+ LINE_SEPARATOR);
		sb.append("  -uid [URI]             The URI identifier for the unit"
				+ LINE_SEPARATOR);
		sb.append("  -ks  [directory name]  Knowledge store data directory (e.g. /tmp/ks)"
				+ LINE_SEPARATOR);
		sb.append("  -ds  [host name]       Data store host name (default: localhost)"
				+ LINE_SEPARATOR);

		System.out.println(sb);

		System.exit(0);
	}

}
