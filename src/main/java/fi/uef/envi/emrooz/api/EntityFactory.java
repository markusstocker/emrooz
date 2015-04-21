/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import java.util.UUID;

import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.api.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.api.ssn.ObservationValue;
import fi.uef.envi.emrooz.api.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.api.ssn.Property;
import fi.uef.envi.emrooz.api.ssn.Sensor;
import fi.uef.envi.emrooz.api.ssn.SensorObservation;
import fi.uef.envi.emrooz.api.ssn.SensorOutput;
import fi.uef.envi.emrooz.api.time.Instant;
import fi.uef.envi.emrooz.api.time.TemporalEntity;

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

	public EntityFactory(String ns) {
		if (ns == null)
			throw new NullPointerException("[ns = null]");

		this.ns = ns;
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

	public static EntityFactory getInstance(String ns) {
		return new EntityFactory(ns);
	}

	private URI randomUUID() {
		return vf.createURI(ns + UUID.randomUUID().toString());
	}

}
