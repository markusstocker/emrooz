/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.vocabulary.EV;
import fi.uef.envi.emrooz.vocabulary.QB;
import fi.uef.envi.emrooz.vocabulary.SDMXDimension;
import fi.uef.envi.emrooz.vocabulary.SDMXMetadata;
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

	private Map<URI, ComponentSpecification> components;

	private static final Logger log = Logger
			.getLogger(DataStructureDefinition.class.getName());

	public DataStructureDefinition(URI id) {
		this(id, DataStructureDefinition);
	}

	public DataStructureDefinition(URI id, ComponentSpecification... components) {
		this(id, DataStructureDefinition, components);
	}

	public DataStructureDefinition(URI id, URI type,
			ComponentSpecification... components) {
		super(id, type);

		this.components = new HashMap<URI, ComponentSpecification>();

		addType(DataStructureDefinition);
		addComponents(components);

		// This component specification is added by default to any data
		// structure definition and is for the dimension property used for the
		// temporal location of observations of the dataset. The dimension is
		// attached to observations; it is an observation component.
		ComponentSpecification timePeriodComponentSpecification = new ComponentSpecification(
				EV.timePeriodComponentSpecification, new DimensionProperty(
						SDMXDimension.timePeriod));
		timePeriodComponentSpecification.setRequired(true);
		timePeriodComponentSpecification.setOrder(0);
		timePeriodComponentSpecification.setComponentAttachment(QB.Observation);
		addComponent(timePeriodComponentSpecification);

		// This component specification is added by default to any data
		// structure definition and is for the attribute property used for the
		// sampling frequency of observations of the dataset. The attribute is
		// attached to the dataset; it is a dataset component.
		ComponentSpecification freqComponentSpecification = new ComponentSpecification(
				EV.freqComponentSpecification, new AttributeProperty(
						SDMXMetadata.freq));
		freqComponentSpecification.setRequired(true);
		freqComponentSpecification.setOrder(0);
		freqComponentSpecification.setComponentAttachment(QB.DataSet);
		addComponent(freqComponentSpecification);
	}

	public void addComponents(ComponentSpecification... components) {
		for (ComponentSpecification component : components)
			addComponent(component);
	}

	public void addComponent(ComponentSpecification component) {
		if (component == null)
			return;

		URI id = component.getId();

		if (components.containsKey(id)) {
			if (log.isLoggable(Level.INFO))
				log.info("Component already specified in data structure definition [component = "
						+ component + "; components = " + components + "]");
			return;
		}

		components.put(id, component);
	}

	public Set<ComponentSpecification> getComponents() {
		return Collections.unmodifiableSet(new HashSet<ComponentSpecification>(
				components.values()));
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
