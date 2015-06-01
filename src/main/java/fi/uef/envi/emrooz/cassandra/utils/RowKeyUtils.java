/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra.utils;

import static fi.uef.envi.emrooz.EmroozOptions.ROWKEY_DATETIME_PATTERN;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.Rollover;
import fi.uef.envi.emrooz.entity.MeasurementPropertyVisitor;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.MeasurementProperty;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
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
	private Map<Sensor, String> sha1HexCache;
	private Map<Sensor, Rollover> rolloverCache;
	private Frequency frequency;
	private MeasurementPropertyVisitor measurementPropertyVisitor;

	private static final Logger log = Logger.getLogger(RowKeyUtils.class
			.getName());

	public RowKeyUtils() {
		this.dtfRowKey = DateTimeFormat.forPattern(ROWKEY_DATETIME_PATTERN);
		this.sha1HexCache = new HashMap<Sensor, String>();
		this.rolloverCache = new HashMap<Sensor, Rollover>();
		this.measurementPropertyVisitor = new HandlerMeasurementPropertyVisitor();
	}

	public String getRowKey(Sensor specification, DateTime time) {
		if (specification == null || time == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Parameters cannot be null [specification = "
						+ specification + "; time = " + time + "]");
			return null;
		}

		Rollover rollover = getRollover(specification);

		if (rollover == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to compute rollover for specification [specification = "
						+ specification + "]");
			return null;
		}

		if (rollover.equals(Rollover.YEAR))
			time = time.year().roundFloorCopy();
		else if (rollover.equals(Rollover.MONTH))
			time = time.monthOfYear().roundFloorCopy();
		else if (rollover.equals(Rollover.DAY))
			time = time.dayOfMonth().roundFloorCopy();
		else if (rollover.equals(Rollover.HOUR))
			time = time.hourOfDay().roundFloorCopy();
		else if (rollover.equals(Rollover.MINUTE))
			time = time.minuteOfHour().roundFloorCopy();
		else
			throw new RuntimeException("Unsupported rollover [rollover = "
					+ rollover + "]");

		return getSha1Hex(specification) + "-" + dtfRowKey.print(time);
	}

	public Rollover getRollover(Sensor specification) {
		Rollover ret = rolloverCache.get(specification);

		if (ret != null)
			return ret;

		Set<MeasurementCapability> measCapabilities = specification
				.getMeasurementCapabilities();

		for (MeasurementCapability measCapability : measCapabilities) {
			Set<MeasurementProperty> measProperties = measCapability
					.getMeasurementProperties();

			for (MeasurementProperty measProperty : measProperties) {
				frequency = null;
				measProperty.accept(measurementPropertyVisitor);

				if (frequency != null) {
					QuantityValue quantityValue = frequency.getQuantityValue();
					Double numericValue = quantityValue.getNumericValue();
					Unit unit = quantityValue.getUnit();

					if (!unit.getId().equals(QUDTUnit.Hertz)) {
						if (log.isLoggable(Level.SEVERE))
							log.severe("Unrecognized unit [unit = " + unit
									+ "; specification = " + specification
									+ "]");
						return null;
					}

					if (numericValue > 100) {
						ret = Rollover.MINUTE;
						break;
					} else if (numericValue > 1) {
						ret = Rollover.HOUR;
						break;
					} else if (numericValue > 0.01) {
						ret = Rollover.DAY;
						break;
					} else if (numericValue > 0.0001) {
						ret = Rollover.MONTH;
						break;
					} else {
						ret = Rollover.YEAR;
						break;
					}
				}
			}
		}

		if (ret == null) {
			throw new NullPointerException(
					"Could not determine rollover for specification [specification = "
							+ specification + "]");
		}

		rolloverCache.put(specification, ret);

		return ret;
	}

	private String getSha1Hex(Sensor specification) {
		String ret = sha1HexCache.get(specification);

		if (ret != null)
			return ret;

		URI sensorId = specification.getId();

		Property property = specification.getObservedProperty();

		if (property == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Property in specification is null [property = "
						+ property + "; specification = " + specification + "]");

			return null;
		}

		URI propertyId = property.getId();

		FeatureOfInterest feature = property.getPropertyOf();

		if (feature == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Feature of property in specification is null [feature = "
						+ feature
						+ "; property = "
						+ property
						+ "; specification = " + specification + "]");

			return null;
		}

		URI featureId = feature.getId();

		ret = DigestUtils.sha1Hex(sensorId.stringValue() + "-"
				+ propertyId.stringValue() + "-" + featureId.stringValue());

		sha1HexCache.put(specification, ret);

		return ret;
	}

	private class HandlerMeasurementPropertyVisitor implements
			MeasurementPropertyVisitor {

		@Override
		public void visit(Frequency entity) {
			frequency = entity;
		}

	}
}
