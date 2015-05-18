/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import static fi.uef.envi.emrooz.EmroozOptions.ROWKEY_DATETIME_PATTERN;

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
 * Title: CassandraRequestHandler
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

public abstract class CassandraRequestHandler {

	private Frequency frequency;
	private MeasurementPropertyVisitor measurementPropertyVisitor;
	private DateTimeFormatter dtfRowKey = DateTimeFormat
			.forPattern(ROWKEY_DATETIME_PATTERN);

	private static final Logger log = Logger
			.getLogger(CassandraRequestHandler.class.getName());

	public CassandraRequestHandler() {
		this.measurementPropertyVisitor = new HandlerMeasurementPropertyVisitor();
	}

	protected String getRowKey(Sensor specification, DateTime time) {
		if (specification == null || time == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Parameters cannot be null [specification = "
						+ specification + "; time = " + time + "]");
			return null;
		}

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

		return DigestUtils.sha1Hex(sensorId.stringValue() + "-"
				+ propertyId.stringValue() + "-" + featureId.stringValue())
				+ "-" + dtfRowKey.print(time);
	}

	protected Rollover getRollover(Sensor specification) {
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
						return Rollover.MINUTE;
					} else if (numericValue > 1) {
						return Rollover.HOUR;
					} else if (numericValue > 0.01) {
						return Rollover.DAY;
					} else if (numericValue > 0.0001) {
						return Rollover.MONTH;
					} else {
						return Rollover.YEAR;
					}
				}
			}
		}

		return null;
	}

	private class HandlerMeasurementPropertyVisitor implements
			MeasurementPropertyVisitor {

		@Override
		public void visit(Frequency entity) {
			frequency = entity;
		}

	}

}
