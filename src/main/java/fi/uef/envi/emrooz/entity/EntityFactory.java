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

import fi.uef.envi.emrooz.entity.qb.AttributeProperty;
import fi.uef.envi.emrooz.entity.qb.Component;
import fi.uef.envi.emrooz.entity.qb.ComponentProperty;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValue;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueEntity;
import fi.uef.envi.emrooz.entity.qb.ComponentSpecification;
import fi.uef.envi.emrooz.entity.qb.DataStructureDefinition;
import fi.uef.envi.emrooz.entity.qb.Dataset;
import fi.uef.envi.emrooz.entity.qb.DatasetObservation;
import fi.uef.envi.emrooz.entity.qb.DimensionProperty;
import fi.uef.envi.emrooz.entity.qb.MeasureProperty;
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

		if (!ns.endsWith("#"))
			ns = ns.concat("#");

		this.ns = ns;
	}

	public Dataset createDataset(String datasetFragment, Double frequency) {
		return createDataset(datasetFragment,
				createQuantityValue(frequency, new Unit(QUDTUnit.Hertz)));
	}

	public Dataset createDataset(String datasetFragment, Double frequency,
			DataStructureDefinition structure) {
		return createDataset(datasetFragment,
				createQuantityValue(frequency, new Unit(QUDTUnit.Hertz)),
				structure);
	}

	public Dataset createDataset(String datasetFragment, QuantityValue frequency) {
		return createDataset(vf.createURI(ns + datasetFragment), frequency);
	}

	public Dataset createDataset(String datasetFragment,
			QuantityValue frequency, DataStructureDefinition structure) {
		return createDataset(vf.createURI(ns + datasetFragment), frequency,
				structure);
	}

	public Dataset createDataset(URI id, QuantityValue frequency) {
		return createDataset(id, frequency, null);
	}

	public Dataset createDataset(URI id, QuantityValue frequency,
			DataStructureDefinition structure) {
		return new Dataset(id, frequency, structure);
	}

	public DataStructureDefinition createDataStructureDefinition(
			ComponentSpecification... components) {
		return createDataStructureDefinition(randomUUID(), components);
	}

	public DataStructureDefinition createDataStructureDefinition(String fragment) {
		return createDataStructureDefinition(vf.createURI(ns + fragment));
	}

	public DataStructureDefinition createDataStructureDefinition(URI id) {
		return new DataStructureDefinition(id);
	}

	public DataStructureDefinition createDataStructureDefinition(
			String fragment, ComponentSpecification... components) {
		return createDataStructureDefinition(vf.createURI(ns + fragment),
				components);
	}

	public DataStructureDefinition createDataStructureDefinition(URI id,
			ComponentSpecification... components) {
		return new DataStructureDefinition(id, components);
	}

	public ComponentSpecification createComponentSpecification(
			ComponentProperty property) {
		return createComponentSpecification(randomUUID(), property);
	}

	public ComponentSpecification createComponentSpecification(
			ComponentProperty property, boolean required) {
		return createComponentSpecification(randomUUID(), property, required,
				-1);
	}

	public ComponentSpecification createComponentSpecification(
			ComponentProperty property, boolean required, int order) {
		return createComponentSpecification(randomUUID(), property, required,
				order);
	}

	public ComponentSpecification createComponentSpecification(URI id,
			ComponentProperty property) {
		return createComponentSpecification(id, property, false);
	}

	public ComponentSpecification createComponentSpecification(URI id,
			ComponentProperty property, boolean required) {
		return createComponentSpecification(id, property, false, -1);
	}

	public ComponentSpecification createComponentSpecification(URI id,
			ComponentProperty property, boolean required, int order) {
		return new ComponentSpecification(id, property, required, order);
	}

	public DimensionProperty createDimensionProperty(String fragment) {
		return createDimensionProperty(vf.createURI(ns + fragment));
	}

	public DimensionProperty createDimensionProperty(URI id) {
		return new DimensionProperty(id);
	}

	public MeasureProperty createMeasureProperty(String fragment) {
		return createMeasureProperty(vf.createURI(ns + fragment));
	}

	public MeasureProperty createMeasureProperty(URI id) {
		return new MeasureProperty(id);
	}

	public AttributeProperty createAttributeProperty(String fragment) {
		return createAttributeProperty(vf.createURI(ns + fragment));
	}

	public AttributeProperty createAttributeProperty(URI id) {
		return new AttributeProperty(id);
	}

	public DatasetObservation createDatasetObservation(String datasetFragment,
			Component... components) {
		return createDatasetObservation(vf.createURI(ns + datasetFragment),
				components);
	}

	public DatasetObservation createDatasetObservation(URI datasetId,
			Component... components) {
		return new DatasetObservation(randomUUID(), datasetId, components);
	}

	public Component createComponent(ComponentProperty property,
			ComponentPropertyValue value) {
		return new Component(property, value);
	}

	public ComponentPropertyValue createComponentPropertyValue(String dateTime) {
		return new ComponentPropertyValueEntity(new Instant(randomUUID(),
				dtf.parseDateTime(dateTime)));
	}

	public ComponentPropertyValue createComponentPropertyValue(double value,
			Unit unit) {
		return new ComponentPropertyValueEntity(new QuantityValue(randomUUID(),
				value, unit));
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

	public Sensor createSensor(String sensorFragment, String propertyFragment,
			String[] featuresFragment, Double frequency) {
		return createSensor(
				vf.createURI(ns + sensorFragment),
				createProperty(propertyFragment,
						createFeaturesOfInterest(featuresFragment)),
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

	public Property createProperty(String fragment, FeatureOfInterest[] features) {
		return createProperty(vf.createURI(ns + fragment), features);
	}

	public Property createProperty(URI id) {
		return createProperty(id, SSN.Property);
	}

	public Property createProperty(URI id, FeatureOfInterest feature) {
		return createProperty(id, SSN.Property, feature);
	}

	public Property createProperty(URI id, FeatureOfInterest[] features) {
		return createProperty(id, SSN.Property, features);
	}

	public Property createProperty(URI id, URI type) {
		return new Property(id, type);
	}

	public Property createProperty(URI id, URI type, FeatureOfInterest feature) {
		return new Property(id, type, feature);
	}

	public Property createProperty(URI id, URI type,
			FeatureOfInterest[] features) {
		return new Property(id, type, features);
	}

	public FeatureOfInterest createFeatureOfInterest(String fragment) {
		return new FeatureOfInterest(vf.createURI(ns + fragment));
	}

	public FeatureOfInterest[] createFeaturesOfInterest(String[] fragments) {
		FeatureOfInterest[] ret = new FeatureOfInterest[fragments.length];

		for (int i = 0; i < fragments.length; i++) {
			ret[i] = new FeatureOfInterest(vf.createURI(ns + fragments[i]));
		}

		return ret;
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

	public SensorObservation createSensorObservation(Sensor sensor,
			Property property, FeatureOfInterest feature, Double result,
			Unit unit, DateTime resultTime) {
		return new SensorObservation(randomUUID(), sensor, property, feature,
				new SensorOutput(randomUUID(), new QuantityValue(randomUUID(),
						result, unit)), new Instant(randomUUID(), resultTime));
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

	public SensorObservation createSensorObservation(String sensor,
			String property, String feature, Double result, Unit unit,
			DateTime resultTime) {
		return new SensorObservation(randomUUID(), new Sensor(vf.createURI(ns
				+ sensor)), new Property(vf.createURI(ns + property)),
				new FeatureOfInterest(vf.createURI(ns + feature)),
				new SensorOutput(randomUUID(), new QuantityValue(randomUUID(),
						result, unit)), new Instant(randomUUID(), resultTime));
	}

	public SensorObservation createSensorObservation(String sensor,
			String property, String feature, Double result, String unit,
			DateTime resultTime) {
		return new SensorObservation(randomUUID(), new Sensor(vf.createURI(ns
				+ sensor)), new Property(vf.createURI(ns + property)),
				new FeatureOfInterest(vf.createURI(ns + feature)),
				new SensorOutput(randomUUID(), new QuantityValue(randomUUID(),
						result, new Unit(vf.createURI(ns + unit)))),
				new Instant(randomUUID(), resultTime));
	}

	public SensorObservation createSensorObservation(String sensor,
			String property, String feature, Double result, URI unit,
			DateTime resultTime) {
		return new SensorObservation(randomUUID(), new Sensor(vf.createURI(ns
				+ sensor)), new Property(vf.createURI(ns + property)),
				new FeatureOfInterest(vf.createURI(ns + feature)),
				new SensorOutput(randomUUID(), new QuantityValue(randomUUID(),
						result, new Unit(unit))), new Instant(randomUUID(),
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

	public SensorObservation createSensorObservation(String sensor,
			String property, String feature, Double result, Unit unit,
			String resultTime) {
		return new SensorObservation(randomUUID(), new Sensor(vf.createURI(ns
				+ sensor)), new Property(vf.createURI(ns + property)),
				new FeatureOfInterest(vf.createURI(ns + feature)),
				new SensorOutput(randomUUID(), new QuantityValue(randomUUID(),
						result, unit)), new Instant(randomUUID(),
						dtf.parseDateTime(resultTime)));
	}

	public SensorObservation createSensorObservation(String sensor,
			String property, String feature, Double result, String unit,
			String resultTime) {
		return new SensorObservation(randomUUID(), new Sensor(vf.createURI(ns
				+ sensor)), new Property(vf.createURI(ns + property)),
				new FeatureOfInterest(vf.createURI(ns + feature)),
				new SensorOutput(randomUUID(), new QuantityValue(randomUUID(),
						result, new Unit(vf.createURI(ns + unit)))),
				new Instant(randomUUID(), dtf.parseDateTime(resultTime)));
	}

	public SensorObservation createSensorObservation(String sensor,
			String property, String feature, Double result, URI unit,
			String resultTime) {
		return new SensorObservation(randomUUID(), new Sensor(vf.createURI(ns
				+ sensor)), new Property(vf.createURI(ns + property)),
				new FeatureOfInterest(vf.createURI(ns + feature)),
				new SensorOutput(randomUUID(), new QuantityValue(randomUUID(),
						result, new Unit(unit))), new Instant(randomUUID(),
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
