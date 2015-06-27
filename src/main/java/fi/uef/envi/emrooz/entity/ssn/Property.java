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
import static fi.uef.envi.emrooz.vocabulary.SSN.Property;

/**
 * <p>
 * Title: Property
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

public class Property extends AbstractEntity {

	private Set<FeatureOfInterest> features;

	public Property(URI id) {
		this(id, Property);
	}

	public Property(URI id, FeatureOfInterest feature) {
		this(id, Property, feature);
	}

	public Property(URI id, URI type) {
		this(id, type, null);
	}

	public Property(URI id, URI type, FeatureOfInterest feature) {
		super(id, type);

		features = new HashSet<FeatureOfInterest>();
		
		addType(Property);
		addPropertyOf(feature);
	}

	public void addPropertyOf(FeatureOfInterest feature) {
		if (feature == null)
			return;

		features.add(feature);
	}

	public Set<FeatureOfInterest> getPropertiesOf() {
		return Collections.unmodifiableSet(features);
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
		result = prime * result + features.hashCode();

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Property other = (Property) obj;

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

		if (!features.equals(other.features))
			return false;

		return true;
	}

	public String toString() {
		return "Property [id = " + id + "; type = " + type + "; types = "
				+ types + "; features = " + features + "]";
	}

}
