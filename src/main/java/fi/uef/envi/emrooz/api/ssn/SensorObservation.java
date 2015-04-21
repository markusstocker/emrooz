/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api.ssn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.api.AbstractEntity;
import fi.uef.envi.emrooz.api.time.TemporalEntity;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: SensorObservation
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

public class SensorObservation extends AbstractEntity {

	private Sensor sensor;
	private Property property;
	private FeatureOfInterest feature;
	private SensorOutput result;
	private TemporalEntity resultTime;

	private static final Logger log = Logger.getLogger(SensorObservation.class
			.getName());

	public SensorObservation(URI id, Sensor sensor, Property property,
			FeatureOfInterest feature) {
		this(id, SSN.Observation, sensor, property, feature);
	}

	public SensorObservation(URI id, URI type, Sensor sensor,
			Property property, FeatureOfInterest feature) {
		this(id, type, sensor, property, feature, null, null);
	}

	public SensorObservation(URI id, Sensor sensor, Property property,
			FeatureOfInterest feature, SensorOutput result,
			TemporalEntity resultTime) {
		this(id, SSN.Observation, sensor, property, feature, result, resultTime);
	}

	public SensorObservation(URI id, URI type, Sensor sensor,
			Property property, FeatureOfInterest feature, SensorOutput result,
			TemporalEntity resultTime) {
		super(id, type);

		if (sensor == null)
			throw new NullPointerException("[sensor = null]");
		if (property == null)
			throw new NullPointerException("[property = null]");
		if (feature == null)
			throw new NullPointerException("[feature = null]");

		this.sensor = sensor;
		this.property = property;
		this.feature = feature;

		if (result != null)
			setObservationResult(result);
		if (resultTime != null)
			setObservationResultTime(resultTime);
	}

	public Sensor getSensor() {
		return sensor;
	}

	public Property getProperty() {
		return property;
	}

	public FeatureOfInterest getFeatureOfInterest() {
		return feature;
	}

	public void setObservationResult(SensorOutput result) {
		if (result == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("[result = null; observation = " + toString() + "]");
		}

		this.result = result;
	}

	public SensorOutput getObservationResult() {
		return result;
	}

	public void setObservationResultTime(TemporalEntity resultTime) {
		if (resultTime == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("[resultTime = null; observation = " + toString()
						+ "]");
		}

		this.resultTime = resultTime;
	}

	public TemporalEntity getObservationResultTime() {
		return resultTime;
	}

	public int hashCode() {
		return 31 * (id.hashCode() + type.hashCode() + sensor.hashCode()
				+ property.hashCode() + feature.hashCode() + result.hashCode() + resultTime
					.hashCode());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SensorObservation))
			return false;

		SensorObservation other = (SensorObservation) obj;

		if (other.id.equals(id) && other.type.equals(type)
				&& other.sensor.equals(sensor)
				&& other.property.equals(property)
				&& other.feature.equals(feature) && other.result.equals(result)
				&& other.resultTime.equals(resultTime))
			return true;

		return false;
	}

	public String toString() {
		return "SensorObservation [id = " + id + "; type = " + type
				+ "; sensor = " + sensor + "; property = " + property
				+ "; feature = " + feature + "; observationResult = " + result
				+ "; observationResultTime = " + resultTime + "]";
	}
}
