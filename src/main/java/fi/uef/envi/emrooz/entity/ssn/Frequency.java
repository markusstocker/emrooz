/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.MeasurementPropertyVisitor;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: Frequency
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

public class Frequency extends MeasurementProperty {

	private QuantityValue value;

	public Frequency(URI id) {
		this(id, SSN.Frequency, null);
	}

	public Frequency(URI id, QuantityValue value) {
		this(id, SSN.Frequency, value);
	}

	public Frequency(URI id, URI type, QuantityValue value) {
		super(id, type);

		setQuantityValue(value);
	}

	public void setQuantityValue(QuantityValue value) {
		this.value = value;
	}

	public QuantityValue getQuantityValue() {
		return value;
	}

	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

	public void accept(MeasurementPropertyVisitor visitor) {
		visitor.visit(this);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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

		Frequency other = (Frequency) obj;

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

		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;

		return true;
	}

	public String toString() {
		return "Frequency [id = " + id + "; type = " + type + "; value = "
				+ value + "]";
	}

}
