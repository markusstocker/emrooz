/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

/**
 * <p>
 * Title: Component
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

public class Component {

	private ComponentProperty property;
	private ComponentPropertyValue value;

	public Component(ComponentProperty property, ComponentPropertyValue value) {
		if (property == null)
			throw new RuntimeException("[property = null]");
		if (value == null)
			throw new RuntimeException("[value = null]");

		this.property = property;
		this.value = value;
	}

	public ComponentProperty getComponentProperty() {
		return property;
	}

	public ComponentPropertyValue getComponentPropertyValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + property.hashCode();
		result = prime * result + value.hashCode();

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

		Component other = (Component) obj;

		if (!property.equals(other.property))
			return false;

		if (!value.equals(other.value))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "Component [property = " + property + "; value = " + value + "]";
	}

}
