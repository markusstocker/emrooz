/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import static fi.uef.envi.emrooz.vocabulary.SSN.Sensor;

/**
 * <p>
 * Title: Sensor
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

public class Sensor extends AbstractEntity {

	private Map<URI, Property> properties;
	private Map<URI, MeasurementCapability> capabilities;

	public Sensor(URI id) {
		this(id, Sensor);
	}

	public Sensor(URI id, URI type) {
		this(id, type, new Property[] {});
	}

	public Sensor(URI id, Property... properties) {
		this(id, Sensor, properties);
	}

	public Sensor(URI id, URI type, Property... properties) {
		this(id, type, properties, new MeasurementCapability[] {});
	}

	public Sensor(URI id, Property property,
			MeasurementCapability... capabilities) {
		this(id, Sensor, property, capabilities);
	}

	public Sensor(URI id, URI type, Property property,
			MeasurementCapability... capabilities) {
		this(id, type, new Property[] { property }, capabilities);
	}

	public Sensor(URI id, Property[] properties,
			MeasurementCapability... capabilities) {
		this(id, Sensor, properties, capabilities);
	}

	public Sensor(URI id, URI type, Property[] properties,
			MeasurementCapability... capabilities) {
		super(id, type);

		this.properties = new HashMap<URI, Property>();
		this.capabilities = new HashMap<URI, MeasurementCapability>();

		addType(Sensor);
		addObservedProperty(properties);
		addMeasurementCapability(capabilities);
	}

	public void addObservedProperty(Property... properties) {
		if (properties == null)
			return;

		for (Property property : properties) {
			addObservedProperty(property);
		}
	}

	public void addObservedProperty(Property property) {
		if (property == null)
			return;

		properties.put(property.getId(), property);
	}

	public void addMeasurementCapability(MeasurementCapability... capabilities) {
		if (capabilities == null)
			return;

		for (MeasurementCapability capability : capabilities) {
			if (capability == null)
				continue;

			URI capabilityId = capability.getId();

			if (!this.capabilities.containsKey(capabilityId))
				this.capabilities.put(capabilityId, capability);
		}
	}

	public Set<MeasurementCapability> getMeasurementCapabilities() {
		return Collections.unmodifiableSet(new HashSet<MeasurementCapability>(
				capabilities.values()));
	}

	public Set<Property> getObservedProperties() {
		return Collections.unmodifiableSet(new HashSet<Property>(properties
				.values()));
	}

	public Property getObservedProperty(URI propertyId) {
		return properties.get(propertyId);
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
		result = prime * result
				+ ((properties.isEmpty()) ? 0 : properties.hashCode());
		result = prime * result
				+ ((capabilities.isEmpty()) ? 0 : capabilities.hashCode());

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Sensor other = (Sensor) obj;

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

		if (!properties.equals(other.properties))
			return false;

		if (!capabilities.equals(other.capabilities))
			return false;

		return true;
	}

	public String toString() {
		return "Sensor [id = " + id + "; type = " + type + "; types = " + types
				+ "; properties = " + properties + "; capabilities = "
				+ capabilities + "]";
	}

}
