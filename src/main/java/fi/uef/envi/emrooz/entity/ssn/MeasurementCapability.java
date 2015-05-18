/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: MeasurementCapability
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

public class MeasurementCapability extends AbstractEntity {

	private Set<MeasurementProperty> properties;

	public MeasurementCapability(URI id, MeasurementProperty... properties) {
		this(id, SSN.MeasurementCapability, properties);
	}

	public MeasurementCapability(URI id, URI type,
			MeasurementProperty... properties) {
		super(id, type);

		this.properties = new HashSet<MeasurementProperty>();

		addMeasurementProperty(properties);
	}

	public void addMeasurementProperty(MeasurementProperty... properties) {
		if (properties == null)
			return;

		for (MeasurementProperty property : properties) {
			if (property == null)
				continue;

			this.properties.add(property);
		}
	}

	public Set<MeasurementProperty> getMeasurementProperties() {
		return Collections.unmodifiableSet(properties);
	}

	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((properties.isEmpty()) ? 0 : properties.hashCode());

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		MeasurementCapability other = (MeasurementCapability) obj;

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

		if (!properties.equals(other.properties))
			return false;

		return true;
	}

	public String toString() {
		return "MeasurementCapability [id = " + id + "; type = " + type
				+ "; properties = " + properties + "]";
	}

}
