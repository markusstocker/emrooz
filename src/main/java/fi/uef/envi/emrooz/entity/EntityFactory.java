/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity;

import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.ObservationValue;
import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.entity.time.TemporalEntity;

/**
 * <p>
 * Title: EntityFactory
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

public class EntityFactory {

	private String ns;
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	private static final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();

	public EntityFactory(String ns) {
		if (ns == null)
			throw new NullPointerException("[ns = null]");

		this.ns = ns;
	}

	public Sensor createSensor(String fragment) {
		return new Sensor(vf.createURI(ns + fragment));
	}

	public Sensor createSensor(URI id) {
		return new Sensor(id);
	}

	public Sensor createSensor(URI id, URI type) {
		return new Sensor(id, type);
	}

	public Property createProperty(String fragment) {
		return new Property(vf.createURI(ns + fragment));
	}

	public Property createProperty(URI id) {
		return new Property(id);
	}

	public Property createProperty(URI id, URI type) {
		return new Property(id, type);
	}

	public FeatureOfInterest createFeatureOfInterest(String fragment) {
		return new FeatureOfInterest(vf.createURI(ns + fragment));
	}

	public FeatureOfInterest createFeatureOfInterest(URI id) {
		return new FeatureOfInterest(id);
	}

	public FeatureOfInterest createFeatureOfInterest(URI id, URI type) {
		return new FeatureOfInterest(id, type);
	}

	public SensorObservation createSensorObservation(Sensor sensor,
			Property property, FeatureOfInterest feature,
			ObservationValue result, TemporalEntity resultTime) {
		return new SensorObservation(randomUUID(), sensor, property, feature,
				new SensorOutput(randomUUID(), result), resultTime);
	}

	public SensorObservation createSensorObservation(Sensor sensor,
			Property property, FeatureOfInterest feature, Double result,
			DateTime resultTime) {
		return new SensorObservation(randomUUID(), sensor, property, feature,
				new SensorOutput(randomUUID(), new ObservationValueDouble(
						randomUUID(), result)), new Instant(randomUUID(),
						resultTime));
	}

	/**
	 * The sensor, property, and feature string are relative to the namespace of
	 * this factory.
	 * 
	 * @param sensor
	 * @param property
	 * @param feature
	 * @param result
	 * @param resultTime
	 * @return SensorObservation
	 */
	public SensorObservation createSensorObservation(String sensor,
			String property, String feature, Double result, DateTime resultTime) {
		return new SensorObservation(randomUUID(), new Sensor(vf.createURI(ns
				+ sensor)), new Property(vf.createURI(ns + property)),
				new FeatureOfInterest(vf.createURI(ns + feature)),
				new SensorOutput(randomUUID(), new ObservationValueDouble(
						randomUUID(), result)), new Instant(randomUUID(),
						resultTime));
	}

	/**
	 * The sensor, property, and feature string are relative to the namespace of
	 * this factory. The result time must be ISO date time,
	 * yyyy-MM-ddThh:mm:ss.SSS+/-hh:mm
	 * 
	 * @param sensor
	 * @param property
	 * @param feature
	 * @param result
	 * @param resultTime
	 * @return SensorObservation
	 */
	public SensorObservation createSensorObservation(String sensor,
			String property, String feature, Double result, String resultTime) {
		return new SensorObservation(randomUUID(), new Sensor(vf.createURI(ns
				+ sensor)), new Property(vf.createURI(ns + property)),
				new FeatureOfInterest(vf.createURI(ns + feature)),
				new SensorOutput(randomUUID(), new ObservationValueDouble(
						randomUUID(), result)), new Instant(randomUUID(),
						dtf.parseDateTime(resultTime)));
	}

	public static EntityFactory getInstance(String ns) {
		return new EntityFactory(ns);
	}

	private URI randomUUID() {
		return vf.createURI(ns + UUID.randomUUID().toString());
	}

}
