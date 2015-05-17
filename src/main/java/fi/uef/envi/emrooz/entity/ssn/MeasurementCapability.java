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
		return 31 * (id.hashCode() + type.hashCode());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof MeasurementCapability))
			return false;

		MeasurementCapability other = (MeasurementCapability) obj;

		if (other.id.equals(id) && other.type.equals(type))
			return true;

		return false;
	}

	public String toString() {
		return "MeasurementCapability [id = " + id + "; type = " + type
				+ "; properties = " + properties + "]";
	}

}
