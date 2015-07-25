/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import fi.uef.envi.emrooz.entity.ComponentPropertyValueVisitor;

/**
 * <p>
 * Title: ComponentPropertyValueLong
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

public class ComponentPropertyValueLong extends
		ComponentPropertyValuePrimitive {

	private Long value;

	public ComponentPropertyValueLong(Long value) {
		this.value = value;
	}

	public void accept(ComponentPropertyValueVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Long getValue() {
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

		ComponentPropertyValueLong other = (ComponentPropertyValueLong) obj;

		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ComponentPropertyValueLong [value = " + value + "]";
	}

}
