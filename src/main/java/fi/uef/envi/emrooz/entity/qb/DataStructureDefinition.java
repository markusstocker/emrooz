/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;

import static fi.uef.envi.emrooz.vocabulary.QB.DataStructureDefinition;

/**
 * <p>
 * Title: DataStructureDefinition
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

public class DataStructureDefinition extends AbstractEntity {

	private Set<ComponentSpecification> components;

	public DataStructureDefinition(URI id) {
		this(id, DataStructureDefinition);
	}

	public DataStructureDefinition(URI id, ComponentSpecification... components) {
		this(id, DataStructureDefinition, components);
	}

	public DataStructureDefinition(URI id, URI type,
			ComponentSpecification... components) {
		super(id, type);

		this.components = new HashSet<ComponentSpecification>();

		addType(DataStructureDefinition);
		addComponents(components);
	}

	public void addComponents(ComponentSpecification... components) {
		for (ComponentSpecification component : components)
			addComponent(component);
	}

	public void addComponent(ComponentSpecification component) {
		if (component == null)
			return;

		this.components.add(component);
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
		result = prime * result + components.hashCode();

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

		DataStructureDefinition other = (DataStructureDefinition) obj;

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

		if (!components.equals(other.components))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "DataStructureDefinition [id = " + id + "; type = " + type
				+ "; types = " + types + "; components = " + components + "]";
	}

}
