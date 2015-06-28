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
import java.util.UUID;
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
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.io.AbstractSensorObservationReader;

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
	private static final int CARBON_DIOXIDE_COL = 22;
	private static final int WATER_COL = 23;
	private static final int METHANE_COL = 32;

	private static final ValueFactory vf = ValueFactoryImpl.getInstance();

	private final Property massFraction = new Property(
			vf.createURI("http://sweet.jpl.nasa.gov/2.3/propFraction.owl#MassFraction"));
	private final FeatureOfInterest carbonDioxide = new FeatureOfInterest(
			vf.createURI("http://sweet.jpl.nasa.gov/2.3/matrCompound.owl#CO2"));
	private final FeatureOfInterest water = new FeatureOfInterest(
			vf.createURI("http://sweet.jpl.nasa.gov/2.3/matrCompound.owl#H2O"));
	private final FeatureOfInterest methane = new FeatureOfInterest(
			vf.createURI("http://sweet.jpl.nasa.gov/2.3/matrCompound.owl#CH4"));

	private Sensor carbonDioxideAnalyzer;
	private Sensor waterAnalyzer;
	private Sensor methaneAnalyzer;
	private String ns;

	private SensorObservation next;
	private Queue<File> files;
	private Queue<SensorObservation> observations;

	private static final Logger log = Logger
			.getLogger(GHGSensorObservationReader.class.getName());

	public GHGSensorObservationReader(File file, Sensor carbonDioxideAnalyzer,
			Sensor waterAnalyzer, Sensor methaneAnalyzer, String ns) {
		if (file == null)
			throw new NullPointerException("[file = null]");
		if (carbonDioxideAnalyzer == null)
			throw new NullPointerException("[carbonDioxideAnalyzer = null]");
		if (waterAnalyzer == null)
			throw new NullPointerException("[waterAnalyzer = null]");
		if (methaneAnalyzer == null)
			throw new NullPointerException("[methaneAnalyzer = null]");
		if (ns == null)
			throw new NullPointerException("[ns = null]");

		this.carbonDioxideAnalyzer = carbonDioxideAnalyzer;
		this.waterAnalyzer = waterAnalyzer;
		this.methaneAnalyzer = methaneAnalyzer;
		this.ns = ns;

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

				observations.add(getSensorObservation(carbonDioxideAnalyzer,
						massFraction, carbonDioxide, dateTime,
						Double.valueOf(cols[CARBON_DIOXIDE_COL])));
				observations.add(getSensorObservation(waterAnalyzer,
						massFraction, water, dateTime,
						Double.valueOf(cols[WATER_COL])));
				observations.add(getSensorObservation(methaneAnalyzer,
						massFraction, methane, dateTime,
						Double.valueOf(cols[METHANE_COL])));
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
			considerFile(file);
			return;
		}

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
				new SensorOutput(_id(),
						new ObservationValueDouble(_id(), value)), new Instant(
						_id(), dateTime));
	}

	private URI _id() {
		return vf.createURI(ns + "#" + UUID.randomUUID().toString());
	}
}
