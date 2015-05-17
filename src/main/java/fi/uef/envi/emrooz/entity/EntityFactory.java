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

import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.MeasurementProperty;
import fi.uef.envi.emrooz.entity.ssn.ObservationValue;
import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.entity.time.TemporalEntity;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;
import fi.uef.envi.emrooz.vocabulary.SSN;

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

	public Sensor createSensor(String sensorFragment, String propertyFragment,
			String featureFragment, Double frequency) {
		return createSensor(
				vf.createURI(ns + sensorFragment),
				createProperty(propertyFragment,
						createFeatureOfInterest(featureFragment)),
				createMeasurementCapability(createFrequency(createQuantityValue(
						frequency, createUnit(QUDTUnit.Hertz)))));
	}

	public Sensor createSensor(URI sensor, URI property, URI feature,
			Double frequency) {
		return createSensor(
				sensor,
				createProperty(property, createFeatureOfInterest(feature)),
				createMeasurementCapability(createFrequency(createQuantityValue(
						frequency, createUnit(QUDTUnit.Hertz)))));
	}

	public Sensor createSensor(String fragment) {
		return createSensor(vf.createURI(ns + fragment));
	}

	public Sensor createSensor(String fragment, Property property,
			MeasurementCapability... capabilities) {
		return createSensor(vf.createURI(ns + fragment), property, capabilities);
	}

	public Sensor createSensor(URI id) {
		return createSensor(id, SSN.Sensor);
	}

	public Sensor createSensor(URI id, Property property,
			MeasurementCapability... capabilities) {
		return createSensor(id, SSN.Sensor, property, capabilities);
	}

	public Sensor createSensor(URI id, URI type) {
		return createSensor(id, type, null);
	}

	public Sensor createSensor(URI id, URI type, Property property,
			MeasurementCapability... capabilities) {
		return new Sensor(id, type, property, capabilities);
	}

	public Property createProperty(String fragment) {
		return createProperty(vf.createURI(ns + fragment));
	}

	public Property createProperty(String fragment, FeatureOfInterest feature) {
		return createProperty(vf.createURI(ns + fragment), feature);
	}

	public Property createProperty(URI id) {
		return createProperty(id, SSN.Property);
	}

	public Property createProperty(URI id, FeatureOfInterest feature) {
		return createProperty(id, SSN.Property, feature);
	}

	public Property createProperty(URI id, URI type) {
		return createProperty(id, type, null);
	}

	public Property createProperty(URI id, URI type, FeatureOfInterest feature) {
		return new Property(id, type, feature);
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

	public MeasurementCapability createMeasurementCapability() {
		return createMeasurementCapability(randomUUID());
	}

	public MeasurementCapability createMeasurementCapability(
			MeasurementProperty... properties) {
		return createMeasurementCapability(randomUUID(), properties);
	}

	public MeasurementCapability createMeasurementCapability(URI id,
			MeasurementProperty... properties) {
		return new MeasurementCapability(id, properties);
	}

	public Frequency createFrequency() {
		return createFrequency(randomUUID(), null);
	}

	public Frequency createFrequency(QuantityValue value) {
		return createFrequency(randomUUID(), value);
	}

	public Frequency createFrequency(URI id, QuantityValue value) {
		return new Frequency(id, value);
	}

	public QuantityValue createQuantityValue() {
		return createQuantityValue(randomUUID(), null, null);
	}

	public QuantityValue createQuantityValue(Double value, Unit unit) {
		return createQuantityValue(randomUUID(), value, unit);
	}

	public QuantityValue createQuantityValue(URI id, Double value, Unit unit) {
		return new QuantityValue(id, value, unit);
	}

	public Unit createUnit(URI id) {
		return new Unit(id);
	}

	public static EntityFactory getInstance(String ns) {
		return new EntityFactory(ns);
	}

	private URI randomUUID() {
		return vf.createURI(ns + UUID.randomUUID().toString());
	}

}
