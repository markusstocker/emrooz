/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import fi.uef.envi.emrooz.entity.ComponentPropertyValueVisitor;

/**
 * <p>
 * Title: ComponentPropertyValueInteger
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

public class ComponentPropertyValueInteger extends
		ComponentPropertyValuePrimitive {

	private Integer value;

	public ComponentPropertyValueInteger(Integer value) {
		this.value = value;
	}

	public void accept(ComponentPropertyValueVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return 31 + ((value == null) ? 0 : value.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		ComponentPropertyValueInteger other = (ComponentPropertyValueInteger) obj;

		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ComponentPropertyValueInteger [value = " + value + "]";
	}

}
