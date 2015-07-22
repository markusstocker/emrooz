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
import fi.uef.envi.emrooz.entity.time.TemporalEntity;
import fi.uef.envi.emrooz.vocabulary.QB;
import fi.uef.envi.emrooz.vocabulary.SDMXDimension;
import static fi.uef.envi.emrooz.vocabulary.QB.Observation;

/**
 * <p>
 * Title: DatasetObservation
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

public class DatasetObservation extends AbstractEntity {

	private URI datasetId;
	private Map<ComponentProperty, ComponentPropertyValue> components;

	public DatasetObservation(URI id, URI datasetId, TemporalEntity timePeriod) {
		this(id, QB.DataSet, datasetId, timePeriod);
	}

	public DatasetObservation(URI id, URI type, URI datasetId,
			TemporalEntity timePeriod) {
		this(id, type, datasetId, new Component(new DimensionProperty(
				SDMXDimension.timePeriod), new ComponentPropertyValueEntity(
				timePeriod)));
	}

	public DatasetObservation(URI id, URI datasetId, Component... components) {
		this(id, QB.DataSet, datasetId, components);
	}

	public DatasetObservation(URI id, URI type, URI datasetId,
			Component... components) {
		super(id, type);

		if (datasetId == null)
			throw new NullPointerException("[datasetId = null]");

		this.datasetId = datasetId;
		this.components = new HashMap<ComponentProperty, ComponentPropertyValue>();

		addType(Observation);
		addComponents(components);
	}

	public void addComponents(Component... components) {
		for (Component component : components)
			addComponent(component);
	}

	public void addComponent(Component component) {
		addComponent(component.getComponentProperty(),
				component.getComponentPropertyValue());
	}

	public void addComponent(ComponentProperty property,
			ComponentPropertyValue value) {
		if (property == null || value == null)
			return;

		components.put(property, value);
	}

	public URI getDatasetId() {
		return datasetId;
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
		result = prime * result + datasetId.hashCode();
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

		DatasetObservation other = (DatasetObservation) obj;

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

		if (!datasetId.equals(other.datasetId))
			return false;

		if (!components.equals(other.components))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "DatasetObservation [id = " + id + "; type = " + type
				+ "; types = " + types + "; datasetId = " + datasetId
				+ "; components = " + components + "]";
	}

}
