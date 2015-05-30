/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.ObservationValueVisitor;
import static fi.uef.envi.emrooz.vocabulary.SSN.ObservationValue;

/**
 * <p>
 * Title: ObservationValueDouble
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

public class ObservationValueDouble extends ObservationValue {

	private Double value;

	public ObservationValueDouble(URI id) {
		this(id, ObservationValue);
	}

	public ObservationValueDouble(URI id, Double value) {
		this(id, ObservationValue, value);
	}

	public ObservationValueDouble(URI id, URI type) {
		this(id, type, Double.NaN);
	}

	public ObservationValueDouble(URI id, URI type, Double value) {
		super(id, type);

		this.value = value;
		
		addType(ObservationValue);
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getValue() {
		return value;
	}

	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

	public void accept(ObservationValueVisitor visitor) {
		visitor.visit(this);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + types.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		ObservationValueDouble other = (ObservationValueDouble) obj;

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

		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;

		return true;
	}

	public String toString() {
		return "ObservationValueDouble [id = " + id + "; type = " + type
				+ "; types = " + types + "; value = " + value + "]";
	}

}
