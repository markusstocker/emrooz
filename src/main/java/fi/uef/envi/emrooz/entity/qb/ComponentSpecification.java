/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;

import static fi.uef.envi.emrooz.vocabulary.QB.ComponentSpecification;

/**
 * <p>
 * Title: ComponentSpecification
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

public class ComponentSpecification extends AbstractEntity {

	private ComponentProperty property;
	private Boolean required;
	private Integer order;
	private URI componentAttachment;

	public ComponentSpecification(URI id, ComponentProperty property) {
		this(id, ComponentSpecification, property);
	}

	public ComponentSpecification(URI id, URI type, ComponentProperty property) {
		this(id, type, property, false);
	}

	public ComponentSpecification(URI id, ComponentProperty property,
			Boolean required) {
		this(id, ComponentSpecification, property, required);
	}

	public ComponentSpecification(URI id, ComponentProperty property,
			Integer order) {
		this(id, ComponentSpecification, property, false, order);
	}

	public ComponentSpecification(URI id, URI type, ComponentProperty property,
			Boolean required) {
		this(id, type, property, required, -1);
	}

	public ComponentSpecification(URI id, URI type, ComponentProperty property,
			Integer order) {
		this(id, type, property, false, order);
	}

	public ComponentSpecification(URI id, ComponentProperty property,
			boolean required, int order) {
		this(id, ComponentSpecification, property, required, order);
	}

	public ComponentSpecification(URI id, URI type, ComponentProperty property,
			boolean required, int order) {
		super(id, type);

		if (property == null)
			throw new NullPointerException("[property = null]");

		this.property = property;

		setRequired(required);
		setOrder(order);
		addType(ComponentSpecification);
	}

	public ComponentProperty getProperty() {
		return property;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}
	
	public void setComponentAttachment(URI attachment) {
		this.componentAttachment = attachment;
	}
	
	public URI getComponentAttachment() {
		return componentAttachment;
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
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		result = prime * result
				+ ((required == null) ? 0 : required.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime
				* result
				+ ((componentAttachment == null) ? 0 : componentAttachment
						.hashCode());

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

		ComponentSpecification other = (ComponentSpecification) obj;

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

		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;

		if (required == null) {
			if (other.required != null)
				return false;
		} else if (!required.equals(other.required))
			return false;

		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;

		if (componentAttachment == null) {
			if (other.componentAttachment != null)
				return false;
		} else if (!componentAttachment.equals(other.componentAttachment))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ComponentSpecification [id = " + id + "; type = " + type
				+ "; types = " + types + "; property = " + property
				+ "; required = " + required + "; order = " + order
				+ "; componentAttachment = " + componentAttachment + "]";
	}

}
