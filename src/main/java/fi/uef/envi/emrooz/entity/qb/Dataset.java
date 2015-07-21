/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;

import static fi.uef.envi.emrooz.vocabulary.SDMXMetadata.freq;
import static fi.uef.envi.emrooz.vocabulary.QB.DataSet;

/**
 * <p>
 * Title: Dataset
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

public class Dataset extends AbstractEntity {

	private DataStructureDefinition structure;
	private Map<ComponentProperty, ComponentPropertyValue> components;

	public Dataset(URI id, QuantityValue frequency) {
		this(id, DataSet, frequency);
	}

	public Dataset(URI id, QuantityValue frequency,
			DataStructureDefinition structure) {
		this(id, DataSet, frequency, structure);
	}

	public Dataset(URI id, URI type, QuantityValue frequency) {
		this(id, type, frequency, null);
	}

	public Dataset(URI id, URI type, QuantityValue frequency,
			DataStructureDefinition structure) {
		super(id, type);

		this.components = new HashMap<ComponentProperty, ComponentPropertyValue>();

		addType(DataSet);
		setStructure(structure);
		addComponent(new AttributeProperty(freq),
				new ComponentPropertyValueEntity(frequency));
	}

	public void addComponent(ComponentProperty property,
			ComponentPropertyValue value) {
		if (property == null || value == null)
			return;

		components.put(property, value);
	}

	public Set<ComponentProperty> getComponentProperties() {
		return Collections.unmodifiableSet(components.keySet());
	}

	public Map<ComponentProperty, ComponentPropertyValue> getComponents() {
		return Collections.unmodifiableMap(components);
	}

	public ComponentPropertyValue getComponentPropertyValue(
			ComponentProperty property) {
		return components.get(property);
	}

	public void setStructure(DataStructureDefinition structure) {
		this.structure = structure;
	}

	public DataStructureDefinition getStructure() {
		return structure;
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
				+ ((structure == null) ? 0 : structure.hashCode());
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

		Dataset other = (Dataset) obj;

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

		if (structure == null) {
			if (other.structure != null)
				return false;
		} else if (!structure.equals(other.structure))
			return false;

		if (!components.equals(other.components))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "Dataset [id = " + id + "; type = " + type + "; types = "
				+ types + "; structure = " + structure + "; components = "
				+ components + "]";
	}

}
