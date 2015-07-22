/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra.utils;

import static fi.uef.envi.emrooz.EmroozOptions.ROWKEY_DATETIME_PATTERN;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.Rollover;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;

/**
 * <p>
 * Title: RowKeyUtils
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

public class RowKeyUtils {

	private DateTimeFormatter dtfRowKey;
	private Map<URI, Map<URI, Map<URI, String>>> shaCacheSensor;
	private Map<URI, Map<URI, Map<URI, Rollover>>> rolloverCacheSensor;
	private Map<URI, String> shaCacheDataset;
	private Map<URI, Rollover> rolloverCacheDataset;

	private static final Logger log = Logger.getLogger(RowKeyUtils.class
			.getName());

	public RowKeyUtils() {
		this.dtfRowKey = DateTimeFormat.forPattern(ROWKEY_DATETIME_PATTERN);
		this.shaCacheSensor = new HashMap<URI, Map<URI, Map<URI, String>>>();
		this.rolloverCacheSensor = new HashMap<URI, Map<URI, Map<URI, Rollover>>>();
		this.shaCacheDataset = new HashMap<URI, String>();
		this.rolloverCacheDataset = new HashMap<URI, Rollover>();
	}

	public String getRowKey(URI sensorId, URI propertyId, URI featureId,
			Frequency frequency, DateTime time) {
		if (sensorId == null || propertyId == null || featureId == null
				|| frequency == null || time == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Parameters cannot be null [sensorId = " + sensorId
						+ "; propertyId = " + propertyId + "; featureId = "
						+ featureId + "; frequency = " + frequency
						+ "; time = " + time + "]");
			return null;
		}

		Rollover rollover = getRollover(sensorId, propertyId, featureId,
				frequency);

		if (rollover == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to compute rollover [sensorId = " + sensorId
						+ "; propertyId = " + propertyId + "; featureId = "
						+ featureId + "; frequency = " + frequency + "]");
			return null;
		}

		String shaHex = getShaHex(sensorId, propertyId, featureId);
		String date = getDate(rollover, time);

		if (shaHex == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to compute SHA hex [sensorId = " + sensorId
						+ "; propertyId = " + propertyId + "; featureId = "
						+ featureId + "; frequency = " + frequency + "]");
			return null;
		}

		if (date == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to compute date for rollover and time [rollover = "
						+ rollover + "; time = " + time + "]");
			return null;
		}

		return shaHex + "-" + date;
	}

	public String getRowKey(URI datasetId, QuantityValue frequency,
			DateTime time) {
		if (datasetId == null || frequency == null || time == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Parameters cannot be null [datasetId = "
						+ datasetId + "; frequency = " + frequency
						+ "; time = " + time + "]");
			return null;
		}

		Rollover rollover = getRollover(datasetId, frequency);

		if (rollover == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Parameters cannot be null [datasetId = "
						+ datasetId + "; frequency = " + frequency + "]");
			return null;
		}

		String shaHex = getShaHex(datasetId);
		String date = getDate(rollover, time);

		if (shaHex == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to compute SHA hex [datasetId = "
						+ datasetId + "; frequency = " + frequency + "]");
			return null;
		}

		if (date == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to compute date for rollover and time [rollover = "
						+ rollover + "; time = " + time + "]");
			return null;
		}

		return shaHex + "-" + date;
	}

	public Rollover getRollover(URI sensorId, URI propertyId, URI featureId,
			Frequency frequency) {
		if (sensorId == null || propertyId == null || featureId == null
				|| frequency == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Parameters cannot be null [sensorId = " + sensorId
						+ "; propertyId = " + propertyId + "; featureId = "
						+ featureId + "; frequency = " + frequency + "]");
			return null;
		}

		Rollover ret = lookupRollover(sensorId, propertyId, featureId);

		if (ret != null)
			return ret;

		QuantityValue quantityValue = frequency.getQuantityValue();
		Double numericValue = quantityValue.getNumericValue();
		Unit unit = quantityValue.getUnit();

		if (!unit.getId().equals(QUDTUnit.Hertz)) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Unrecognized unit [unit = " + unit
						+ "; frequency = " + frequency + "; sensorId = "
						+ sensorId + "]");
			return null;
		}

		if (numericValue > 100) {
			ret = Rollover.MINUTE;
		} else if (numericValue > 1) {
			ret = Rollover.HOUR;
		} else if (numericValue > 0.01) {
			ret = Rollover.DAY;
		} else if (numericValue > 0.0001) {
			ret = Rollover.MONTH;
		} else {
			ret = Rollover.YEAR;
		}

		cacheRollover(sensorId, propertyId, featureId, ret);

		return ret;
	}

	public Rollover getRollover(URI datasetId, QuantityValue frequency) {
		if (datasetId == null || frequency == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Parameters cannot be null [datasetId = "
						+ datasetId + "; frequency = " + frequency + "]");
			return null;
		}

		Rollover ret = lookupRollover(datasetId);

		if (ret != null)
			return ret;

		Double numericValue = frequency.getNumericValue();
		Unit unit = frequency.getUnit();

		if (!unit.getId().equals(QUDTUnit.Hertz)) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Unrecognized unit [unit = " + unit
						+ "; frequency = " + frequency + "; datasetId = "
						+ datasetId + "]");
			return null;
		}

		if (numericValue > 100) {
			ret = Rollover.MINUTE;
		} else if (numericValue > 1) {
			ret = Rollover.HOUR;
		} else if (numericValue > 0.01) {
			ret = Rollover.DAY;
		} else if (numericValue > 0.0001) {
			ret = Rollover.MONTH;
		} else {
			ret = Rollover.YEAR;
		}

		cacheRollover(datasetId, ret);

		return ret;
	}

	private Rollover lookupRollover(URI sensorId, URI propertyId, URI featureId) {
		Map<URI, Map<URI, Rollover>> m1 = rolloverCacheSensor.get(sensorId);

		if (m1 == null)
			return null;

		Map<URI, Rollover> m2 = m1.get(propertyId);

		if (m2 == null)
			return null;

		return m2.get(featureId);
	}

	private Rollover lookupRollover(URI datasetId) {
		return rolloverCacheDataset.get(datasetId);
	}

	private void cacheRollover(URI sensorId, URI propertyId, URI featureId,
			Rollover rollover) {
		Map<URI, Map<URI, Rollover>> m1 = rolloverCacheSensor.get(sensorId);

		if (m1 == null) {
			m1 = new HashMap<URI, Map<URI, Rollover>>();
			rolloverCacheSensor.put(sensorId, m1);
		}

		Map<URI, Rollover> m2 = m1.get(propertyId);

		if (m2 == null) {
			m2 = new HashMap<URI, Rollover>();
			m1.put(propertyId, m2);
		}

		m2.put(featureId, rollover);
	}

	private void cacheRollover(URI datasetId, Rollover rollover) {
		rolloverCacheDataset.put(datasetId, rollover);
	}

	private String getDate(Rollover rollover, DateTime time) {
		if (rollover.equals(Rollover.YEAR))
			return dtfRowKey.print(time.year().roundFloorCopy());
		else if (rollover.equals(Rollover.MONTH))
			return dtfRowKey.print(time.monthOfYear().roundFloorCopy());
		else if (rollover.equals(Rollover.DAY))
			return dtfRowKey.print(time.dayOfMonth().roundFloorCopy());
		else if (rollover.equals(Rollover.HOUR))
			return dtfRowKey.print(time.hourOfDay().roundFloorCopy());
		else if (rollover.equals(Rollover.MINUTE))
			return dtfRowKey.print(time.minuteOfHour().roundFloorCopy());

		return null;
	}

	private String getShaHex(URI sensorId, URI propertyId, URI featureId) {
		String ret = lookupShaHex(sensorId, propertyId, featureId);

		if (ret != null)
			return ret;

		ret = DigestUtils.sha256Hex(sensorId.stringValue() + "-"
				+ propertyId.stringValue() + "-" + featureId.stringValue());

		cacheShaHex(sensorId, propertyId, featureId, ret);

		return ret;
	}

	private String getShaHex(URI datasetId) {
		String ret = lookupShaHex(datasetId);

		if (ret != null)
			return ret;

		ret = DigestUtils.sha256Hex(datasetId.stringValue());

		cacheShaHex(datasetId, ret);

		return ret;
	}

	private String lookupShaHex(URI sensorId, URI propertyId, URI featureId) {
		Map<URI, Map<URI, String>> m1 = shaCacheSensor.get(sensorId);

		if (m1 == null)
			return null;

		Map<URI, String> m2 = m1.get(propertyId);

		if (m2 == null)
			return null;

		return m2.get(featureId);
	}

	private String lookupShaHex(URI datasetId) {
		return shaCacheDataset.get(datasetId);
	}

	private void cacheShaHex(URI sensorId, URI propertyId, URI featureId,
			String shahex) {
		Map<URI, Map<URI, String>> m1 = shaCacheSensor.get(sensorId);

		if (m1 == null) {
			m1 = new HashMap<URI, Map<URI, String>>();
			shaCacheSensor.put(sensorId, m1);
		}

		Map<URI, String> m2 = m1.get(propertyId);

		if (m2 == null) {
			m2 = new HashMap<URI, String>();
			m1.put(propertyId, m2);
		}

		m2.put(featureId, shahex);
	}

	private void cacheShaHex(URI datasetId, String shahex) {
		shaCacheDataset.put(datasetId, shahex);
	}

}
