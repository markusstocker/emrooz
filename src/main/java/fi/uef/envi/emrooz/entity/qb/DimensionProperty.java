/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.EntityVisitor;

import static fi.uef.envi.emrooz.vocabulary.QB.DimensionProperty;
import static fi.uef.envi.emrooz.vocabulary.QB.ComponentProperty;

/**
 * <p>
 * Title: DimensionProperty
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

public class DimensionProperty extends ComponentProperty {

	public DimensionProperty(URI id) {
		this(id, DimensionProperty);
	}

	public DimensionProperty(URI id, URI type) {
		super(id, type);

		addType(DimensionProperty);
		addType(ComponentProperty);
	}

	@Override
	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + types.hashCode();

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		DimensionProperty other = (DimensionProperty) obj;

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

		return true;
	}

	@Override
	public String toString() {
		return "DimensionProperty [id = " + id + "; type = " + type
				+ "; types = " + types + "]";
	}

}
