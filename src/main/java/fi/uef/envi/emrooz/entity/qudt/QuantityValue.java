/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qudt;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import static fi.uef.envi.emrooz.vocabulary.QUDTSchema.QuantityValue;

/**
 * <p>
 * Title: QuantityValue
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

public class QuantityValue extends AbstractEntity {

	public Double value;
	public Unit unit;

	public QuantityValue(URI id) {
		this(id, QuantityValue);
	}

	public QuantityValue(URI id, URI type) {
		this(id, type, null, null);
	}

	public QuantityValue(URI id, Double value, Unit unit) {
		this(id, QuantityValue, value, unit);
	}

	public QuantityValue(URI id, URI type, Double value, Unit unit) {
		super(id, type);

		addType(QuantityValue);
		setNumericValue(value);
		setUnit(unit);
	}

	public void setNumericValue(Double value) {
		this.value = value;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Double getNumericValue() {
		return value;
	}

	public Unit getUnit() {
		return unit;
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
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());

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

		QuantityValue other = (QuantityValue) obj;

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

		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "QuantityValue [id = " + id + "; type = " + type + "; types = "
				+ types + "; value = " + value + "; unit = " + unit + "]";
	}

}
