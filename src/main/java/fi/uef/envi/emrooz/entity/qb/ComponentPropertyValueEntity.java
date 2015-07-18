/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

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

	private Entity entity;

	public ComponentPropertyValueEntity(Entity entity) {
		if (entity == null)
			throw new NullPointerException("[entity = null]");

		this.entity = entity;
	}

	public void accept(ComponentPropertyValueVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Entity getValue() {
		return entity;
	}

	@Override
	public int hashCode() {
		return 31 + ((entity == null) ? 0 : entity.hashCode());
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

		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ComponentPropertyValueEntity [entity = " + entity + "]";
	}

}
