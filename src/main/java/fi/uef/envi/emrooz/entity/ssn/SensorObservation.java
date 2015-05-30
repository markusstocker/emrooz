/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.time.TemporalEntity;
import static fi.uef.envi.emrooz.vocabulary.SSN.Observation;

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
		this(id, Observation, sensor, property, feature);
	}

	public SensorObservation(URI id, URI type, Sensor sensor,
			Property property, FeatureOfInterest feature) {
		this(id, type, sensor, property, feature, null, null);
	}

	public SensorObservation(URI id, Sensor sensor, Property property,
			FeatureOfInterest feature, SensorOutput result,
			TemporalEntity resultTime) {
		this(id, Observation, sensor, property, feature, result, resultTime);
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

		addType(Observation);
		
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

	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + types.hashCode();
		result = prime * result + ((sensor == null) ? 0 : sensor.hashCode());
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		result = prime * result + ((feature == null) ? 0 : feature.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result
				+ ((resultTime == null) ? 0 : resultTime.hashCode());

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		SensorObservation other = (SensorObservation) obj;

		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;

		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;

		if (!types.equals(other.types))
			return false;

		if (sensor == null) {
			if (other.sensor != null)
				return false;
		} else if (!sensor.equals(other.sensor))
			return false;

		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;

		if (feature == null) {
			if (other.feature != null)
				return false;
		} else if (!feature.equals(other.feature))
			return false;

		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;

		if (resultTime == null) {
			if (other.resultTime != null)
				return false;
		} else if (!resultTime.equals(other.resultTime))
			return false;

		return true;
	}

	public String toString() {
		return "SensorObservation [id = " + id + "; type = " + type
				+ "; types = " + types + "; sensor = " + sensor
				+ "; property = " + property + "; feature = " + feature
				+ "; observationResult = " + result
				+ "; observationResultTime = " + resultTime + "]";
	}
}
