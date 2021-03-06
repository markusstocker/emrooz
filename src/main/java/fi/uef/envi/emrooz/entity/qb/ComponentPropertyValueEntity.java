/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import fi.uef.envi.emrooz.entity.ComponentPropertyValueVisitor;
import fi.uef.envi.emrooz.entity.Entity;

/**
 * <p>
 * Title: ComponentPropertyValueEntity
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

public class ComponentPropertyValueEntity extends ComponentPropertyValue {

	private Entity value;

	public ComponentPropertyValueEntity(Entity value) {
		if (value == null)
			throw new NullPointerException("[value = null]");

		this.value = value;
	}

	public void accept(ComponentPropertyValueVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Entity getValue() {
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

		ComponentPropertyValueEntity other = (ComponentPropertyValueEntity) obj;

		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ComponentPropertyValueEntity [value = " + value + "]";
	}

}
