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

import fi.uef.envi.emrooz.entity.qb.ComponentProperty;
import fi.uef.envi.emrooz.entity.qb.MeasureProperty;
import fi.uef.envi.emrooz.io.AbstractObservationReader;

/**
 * <p>
 * Title: AbstractGHGObservationReader
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

public abstract class AbstractGHGObservationReader<T> extends
		AbstractObservationReader<T> {

	private static final String licorNs = "http://www.licor.com/property#";

	protected static final String COLUMN_SEPARATOR = "\\t+";

	protected static final double GAS_ANALYZER_SAMPLING_FREQUENCY = 10.0;
	protected static final int GAS_ANALYZER_DATA_ROW = 8;
	protected static final int GAS_ANALYZER_TIMEZONE_ROW = 6;
	protected static final int GAS_ANALYZER_TIMEZONE_COL = 1;
	protected static final int GAS_ANALYZER_SECONDS_COL = 1;
	protected static final int GAS_ANALYZER_NANOSECONDS_COL = 2;
	protected static final int GAS_ANALYZER_SEQUENCE_NUMBER_COL = 3;
	protected static final int GAS_ANALYZER_DIAGNOSTIC_VALUE_COL = 4;
	protected static final int GAS_ANALYZER_DIAGNOSTIC_VALUE_2_COL = 5;
	protected static final int GAS_ANALYZER_DATE_COL = 6;
	protected static final int GAS_ANALYZER_TIME_COL = 7;
	protected static final int GAS_ANALYZER_CARBON_DIOXIDE_ABSORPTANCE_COL = 8;
	protected static final int GAS_ANALYZER_WATER_VAPOR_ABSORPTANCE_COL = 9;
	protected static final int GAS_ANALYZER_CARBON_DIOXIDE_MOLAR_CONCENTRATION_COL = 10;
	protected static final int GAS_ANALYZER_CARBON_DIOXIDE_MASS_CONCENTRATION_COL = 11;
	protected static final int GAS_ANALYZER_WATER_VAPOR_MOLAR_CONCENTRATION_COL = 12;
	protected static final int GAS_ANALYZER_WATER_VAPOR_MASS_CONCENTRATION_COL = 13;
	protected static final int GAS_ANALYZER_TEMPERATURE_COL = 14;
	protected static final int GAS_ANALYZER_PRESSURE_COL = 15;
	protected static final int GAS_ANALYZER_WIND_SPEED_U_COL = 16;
	protected static final int GAS_ANALYZER_WIND_SPEED_V_COL = 17;
	protected static final int GAS_ANALYZER_WIND_SPEED_W_COL = 18;
	protected static final int GAS_ANALYZER_SONIC_TEMPERATURE_COL = 19;
	protected static final int GAS_ANALYZER_COOLER_VOLTAGE_COL = 20;
	protected static final int GAS_ANALYZER_INPUT_VOLTAGE_SMART_FLUX_COL = 21;
	protected static final int GAS_ANALYZER_CARBON_DIOXIDE_MOLE_FRACTION_COL = 22;
	protected static final int GAS_ANALYZER_WATER_VAPOR_MOLE_FRACTION_COL = 23;
	protected static final int GAS_ANALYZER_DEW_POINT_COL = 24;
	protected static final int GAS_ANALYZER_CARBON_DIOXIDE_ANALYZER_SIGNAL_STRENGTH_COL = 25;
	protected static final int GAS_ANALYZER_WATER_VAPOR_ANALYZER_SAMPLE_COL = 26;
	protected static final int GAS_ANALYZER_WATER_VAPOR_ANALYZER_REFERENCE_COL = 27;
	protected static final int GAS_ANALYZER_CARBON_DIOXIDE_ANALYZER_SAMPLE_COL = 28;
	protected static final int GAS_ANALYZER_CARBON_DIOXIDE_ANALYZER_REFERENCE_COL = 29;
	protected static final int GAS_ANALYZER_METHANE_ANALYZER_SECONDS_COLR = 30;
	protected static final int GAS_ANALYZER_METHANE_NANOSECONDS_COL = 31;
	protected static final int GAS_ANALYZER_METHANE_MOLE_FRACTION_COL = 32;
	protected static final int GAS_ANALYZER_METHANE_MOLAR_CONCENTRATION_COL = 33;
	protected static final int GAS_ANALYZER_METHANE_ANALYZER_TEMPERATURE_COL = 34;
	protected static final int GAS_ANALYZER_METHANE_ANALYZER_PRESSURE_COL = 35;
	protected static final int GAS_ANALYZER_METHANE_ANALYZER_SIGNAL_STRENGTH_COL = 36;
	protected static final int GAS_ANALYZER_METHANE_ANALYZER_DIAGNOSTIC_VALUE_COL = 37;
	protected static final int GAS_ANALYZER_METHANE_ANALYZER_DROP_RATE = 38;
	protected static final int GAS_ANALYZER_CHECK = 39;

	protected static final double BIOMET_SAMPLING_FREQUENCY = 0.0167;
	protected static final int BIOMET_DATA_ROW = 6;
	protected static final int BIOMET_TIMEZONE_ROW = 4;
	protected static final int BIOMET_TIMEZONE_COL = 1;
	protected static final int BIOMET_DATE_COL = 1;
	protected static final int BIOMET_TIME_COL = 2;
	protected static final int BIOMET_LOGGER_TEMPERATURE_COL = 3;
	protected static final int BIOMET_LOGGER_INPUT_VOLTAGE_COL = 4;
	protected static final int BIOMET_PHOTOSYNTHETIC_PHOTON_FLUX_DENSITY_COL = 5;
	protected static final int BIOMET_RAINFALL_COL = 6;
	protected static final int BIOMET_GLOBAL_SOLAR_RADIATION_COL = 7;
	protected static final int BIOMET_RELATIVE_HUMIDITY_COL = 8;
	protected static final int BIOMET_SURFACE_NET_RADIATION_COL = 9;
	protected static final int BIOMET_SOIL_HEAT_FLUX_1_COL = 10;
	protected static final int BIOMET_SOIL_HEAT_FLUX_2_COL = 11;
	protected static final int BIOMET_SOIL_HEAT_FLUX_3_COL = 12;
	protected static final int BIOMET_SOIL_HEAT_FLUX_SENS_1_COL = 13;
	protected static final int BIOMET_SOIL_HEAT_FLUX_SENS_2_COL = 14;
	protected static final int BIOMET_SOIL_HEAT_FLUX_SENS_3_COL = 15;
	protected static final int BIOMET_SOIL_WATER_CONTENT_1_COL = 16;
	protected static final int BIOMET_SOIL_WATER_CONTENT_2_COL = 17;
	protected static final int BIOMET_SOIL_WATER_CONTENT_3_COL = 18;
	protected static final int BIOMET_AIR_TEMPERATURE_COL = 19;
	protected static final int BIOMET_SOIL_TEMPERATURE_1_COL = 20;
	protected static final int BIOMET_SOIL_TEMPERATURE_2_COL = 21;
	protected static final int BIOMET_SOIL_TEMPERATURE_3_COL = 22;
	protected static final int BIOMET_CHECK = 23;

	protected ComponentProperty[] gasProperties;
	protected ComponentProperty[] biometProperties;
	protected String[] gasPropertyTypes;
	protected String[] biometPropertyTypes;

	protected Queue<File> files;

	private static final Logger log = Logger
			.getLogger(AbstractGHGObservationReader.class.getName());

	public AbstractGHGObservationReader(URI ns) {
		super(ns);

		this.files = new LinkedList<File>();
		this.gasProperties = new ComponentProperty[] { _("datah"),
				_("seconds"), _("nanoseconds"), _("sequenceNumber"),
				_("diagnosticValue"), _("diagnosticValue2"), _("date"),
				_("time"), _("carbonDioxideAbsorptance"),
				_("waterVaporAbsorptance"),
				_("carbonDioxideMolarConcentration"),
				_("carbonDioxideMassConcentration"),
				_("waterVaporMolarConcentration"),
				_("waterVaporMassConcentration"), _("temperature"),
				_("pressure"), _("windSpeedU"), _("windSpeedV"),
				_("windSpeedW"), _("sonicTemperature"), _("coolerVoltage"),
				_("inputVoltageSmartFlux"), _("carbonDioxideMoleFraction"),
				_("waterVaporMoleFraction"), _("dewPoint"),
				_("carbonDioxideAnalyzerSignalStrength"),
				_("waterVaporAnalyzerSample"),
				_("waterVaporAnalyzerReference"),
				_("carbonDioxideAnalyzerSample"),
				_("carbonDioxideAnalyzerReference"),
				_("methaneAnalyzerSeconds"), _("methaneAnalyzerNanoseconds"),
				_("methaneMoleFraction"), _("methaneMolarConcentration"),
				_("methaneAnalyzerTemperature"), _("methaneAnalyzerPressure"),
				_("methaneAnalyzerSignalStrength"),
				_("methaneAnalyzerDiagnosticValue"),
				_("methaneAnalyzerDropRate"), _("check") };
		this.gasPropertyTypes = new String[] { "String", "Long", "Integer",
				"Long", "Integer", "Integer", "String", "String", "Double",
				"Double", "Double", "Double", "Double", "Double", "Double",
				"Double", "Double", "Double", "Double", "Double", "Double",
				"Double", "Double", "Double", "Double", "Double", "Double",
				"Double", "Double", "Double", "Long", "Integer", "Double",
				"Double", "Double", "Double", "Double", "Integer", "Double",
				"Integer" };
		this.biometProperties = new ComponentProperty[] { _("datah"),
				_("date"), _("time"), _("loggerTemperature"),
				_("loggerInputVoltage"), _("photosyntheticPhotonFluxDensity"),
				_("rainfall"), _("globalSolarRadiation"),
				_("relativeHumidity"), _("surfaceNetRadiation"),
				_("soilHeatFlux1"), _("soilHeatFlux2"), _("soilHeatFlux3"),
				_("soilHeatFluxSens1"), _("soilHeatFluxSens2"),
				_("soilHeatFluxSens3"), _("soilWaterContent1"),
				_("soilWaterContent2"), _("soilWaterContent3"),
				_("airTemperature"), _("soilTemperature1"),
				_("soilTemperature2"), _("soilTemperature3"), _("check") };
		this.biometPropertyTypes = new String[] { "String", "String", "String",
				"Double", "Double", "Double", "Double", "Double", "Double",
				"Double", "Double", "Double", "Double", "Double", "Double",
				"Double", "Double", "Double", "Double", "Double", "Double",
				"Double", "Double", "Integer" };
	}

	protected void listFiles(File file) {
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

	protected void considerFile(File file) {
		String fileName = file.getName();
				
		if (FilenameUtils.getExtension(fileName).equals("ghg"))
			files.add(file);
	}

	protected List<String> readFile(File file, String fileName,
			String dataFileName) {
		List<String> ret = new ArrayList<String>();

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

	protected DateTimeZone getDateTimeZone(String line, int timeZoneCol) {
		if (line == null)
			return null;
		if (line.length() == 0)
			return null;

		DateTimeZone ret = null;
		String[] cols = line.split(COLUMN_SEPARATOR);
		String col = cols[timeZoneCol];

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

	protected DateTime getDateTime(String date, String time, DateTimeZone zone) {
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

	private MeasureProperty _(String f) {
		return new MeasureProperty(vf.createURI(licorNs + f));
	}

}
