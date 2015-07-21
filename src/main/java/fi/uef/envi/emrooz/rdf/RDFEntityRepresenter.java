/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.rdf;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;

import fi.uef.envi.emrooz.entity.ComponentPropertyValueVisitor;
import fi.uef.envi.emrooz.entity.Entity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.MeasurementPropertyVisitor;
import fi.uef.envi.emrooz.entity.ObservationValueVisitor;
import fi.uef.envi.emrooz.entity.TemporalEntityVisitor;
import fi.uef.envi.emrooz.entity.qb.AttributeProperty;
import fi.uef.envi.emrooz.entity.qb.ComponentProperty;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValue;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueDouble;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueEntity;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueString;
import fi.uef.envi.emrooz.entity.qb.ComponentSpecification;
import fi.uef.envi.emrooz.entity.qb.DataStructureDefinition;
import fi.uef.envi.emrooz.entity.qb.Dataset;
import fi.uef.envi.emrooz.entity.qb.DatasetObservation;
import fi.uef.envi.emrooz.entity.qb.DimensionProperty;
import fi.uef.envi.emrooz.entity.qb.MeasureProperty;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.MeasurementProperty;
import fi.uef.envi.emrooz.entity.ssn.ObservationValue;
import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.entity.time.TemporalEntity;
import fi.uef.envi.emrooz.vocabulary.DUL;
import fi.uef.envi.emrooz.vocabulary.QB;
import fi.uef.envi.emrooz.vocabulary.QUDTSchema;
import fi.uef.envi.emrooz.vocabulary.SDMXDimension;
import fi.uef.envi.emrooz.vocabulary.SDMXMetadata;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: RDFEntityRepresenter
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

public class RDFEntityRepresenter {

	private Set<Statement> statements;
	private URI componentPropertyId;
	private URI id;
	private final EntityVisitor entityVisitor;
	private final ObservationValueVisitor observationValueVisitor;
	private final MeasurementPropertyVisitor measurementPropertyVisitor;
	private final TemporalEntityVisitor temporalEntityVisitor;
	private final ComponentPropertyValueVisitor componentPropertyValueVisitor;
	private final static ValueFactory vf = ValueFactoryImpl.getInstance();
	private final static DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();
	private final static Logger log = Logger
			.getLogger(RDFEntityRepresenter.class.getName());

	public RDFEntityRepresenter() {
		entityVisitor = new RepresenterEntityVisitor();
		observationValueVisitor = new RepresenterObservationValueVisitor();
		measurementPropertyVisitor = new RepresenterMeasurementPropertyVisitor();
		temporalEntityVisitor = new RepresenterTemporalEntityVisitor();
		componentPropertyValueVisitor = new RepresenterComponentPropertyValueVisitor();
	}

	public Set<Statement> createRepresentation(Entity entity) {
		if (entity == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		statements = new HashSet<Statement>();

		entity.accept(entityVisitor);

		ret.addAll(statements);

		return Collections.unmodifiableSet(ret);
	}

	public Set<Statement> createRepresentation(DatasetObservation observation) {
		if (observation == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		Set<URI> types = observation.getTypes();
		Dataset dataset = observation.getDataset();
		Map<ComponentProperty, ComponentPropertyValue> components = observation
				.getComponents();

		URI id = observation.getId();
		URI datasetId = dataset.getId();

		for (URI type : types)
			ret.add(_statement(id, RDF.TYPE, type));

		ret.add(_statement(id, QB.dataSet, datasetId));
		ret.addAll(createRepresentation(dataset));

		// Assigned this hear, because createRepresentation(dataset) above will
		// otherwise override this.id
		this.id = id;

		for (Map.Entry<ComponentProperty, ComponentPropertyValue> entry : components
				.entrySet()) {
			ComponentProperty componentProperty = entry.getKey();
			ComponentPropertyValue value = entry.getValue();

			componentPropertyId = componentProperty.getId();

			statements = new HashSet<Statement>();

			value.accept(componentPropertyValueVisitor);

			ret.addAll(statements);
			ret.addAll(createRepresentation(componentProperty));
		}

		return Collections.unmodifiableSet(ret);
	}

	public DatasetObservation createDatasetObservation(Set<Statement> statements) {
		if (statements == null)
			return null;
		if (statements.isEmpty())
			return null;

		URI id = _getId(statements, QB.Observation);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract observation id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		URI datasetId = _getObjectId(statements, id, QB.dataSet);

		if (datasetId == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract dataset id for observation [id = "
						+ id + "; statements = " + statements + "]");
			return null;
		}

		Dataset dataset = createDataset(_matchSubject(statements, datasetId));

		if (dataset == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to create dataset [datasetId = " + datasetId
						+ "; statements = " + statements + "]");
			return null;
		}

		URI temporalEntityId = _getObjectId(statements, id,
				SDMXDimension.timePeriod);

		TemporalEntity temporalEntity = createTemporalEntity(_matchSubject(
				statements, temporalEntityId));

		DatasetObservation ret = new DatasetObservation(id, _getType(
				statements, id, QB.Observation), dataset, temporalEntity);
		ret.addTypes(_getTypes(statements, id));

		Set<URI> componentPropertyIds = _getPredicateIds(statements, id,
				QB.ComponentProperty);

		for (URI componentPropertyId : componentPropertyIds) {
			if (componentPropertyId.equals(SDMXDimension.timePeriod))
				continue;

			ComponentProperty property = createComponentProperty(_matchSubject(
					statements, componentPropertyId));

			Value object = _getObject(statements, id, componentPropertyId);

			if (object instanceof URI) {
				ret.addComponent(
						property,
						createComponentPropertyValueEntity(_matchSubject(
								statements, (URI) object)));
			} else if (object instanceof Literal) {
				Literal literal = (Literal) object;
				URI datatype = literal.getDatatype();

				if (datatype.equals(XMLSchema.DOUBLE)) {
					ret.addComponent(
							property,
							new ComponentPropertyValueDouble(literal
									.doubleValue()));
				} else if (datatype.equals(XMLSchema.STRING)) {
					ret.addComponent(
							property,
							new ComponentPropertyValueString(literal
									.stringValue()));
				} else {
					if (log.isLoggable(Level.WARNING))
						log.warning("Failed to create primitive component property value; unrecognized datatype [datatype = "
								+ datatype + "]");
				}
			} else {
				throw new RuntimeException("Unsupported object type [object = "
						+ object + "; id = " + id + "; componentPropertyId = "
						+ componentPropertyId + "; statements = " + statements
						+ "]");
			}
		}

		return ret;
	}

	public ComponentPropertyValueEntity createComponentPropertyValueEntity(
			Set<Statement> statements) {
		if (statements == null)
			return null;
		if (statements.isEmpty())
			return null;

		if (_getId(statements, Time.TemporalEntity) != null) {
			return new ComponentPropertyValueEntity(
					createTemporalEntity(statements));
		}

		if (_getId(statements, QUDTSchema.QuantityValue) != null) {
			return new ComponentPropertyValueEntity(
					createQuantityValue(statements));
		}

		if (log.isLoggable(Level.WARNING))
			log.warning("Failed to create component property value entity [statements = "
					+ statements + "]");

		return null;
	}

	public Set<Statement> createRepresentation(Dataset dataset) {
		if (dataset == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		URI id = dataset.getId();
		Set<URI> types = dataset.getTypes();

		for (URI type : types)
			ret.add(_statement(id, RDF.TYPE, type));

		DataStructureDefinition structure = dataset.getStructure();

		if (structure != null) {
			ret.add(_statement(id, QB.structure, structure.getId()));
			ret.addAll(createRepresentation(structure));
		}

		Map<AttributeProperty, ComponentPropertyValue> components = dataset
				.getComponents();

		this.id = id;

		for (Map.Entry<AttributeProperty, ComponentPropertyValue> entry : components
				.entrySet()) {
			AttributeProperty componentProperty = entry.getKey();
			ComponentPropertyValue value = entry.getValue();

			componentPropertyId = componentProperty.getId();

			statements = new HashSet<Statement>();

			value.accept(componentPropertyValueVisitor);

			ret.addAll(statements);
			ret.addAll(createRepresentation(componentProperty));
		}

		return Collections.unmodifiableSet(ret);
	}

	public Dataset createDataset(Set<Statement> statements) {
		if (statements == null)
			return null;
		if (statements.isEmpty())
			return null;

		URI id = _getId(statements, QB.DataSet);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract dataset id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		URI frequencyId = _getObjectId(statements, id, SDMXMetadata.freq);

		if (frequencyId == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract frequency id attribute property for dataset [frequencyId = null; id = "
						+ id + "; statements = " + statements + "]");
			return null;
		}

		Dataset ret = new Dataset(id, _getType(statements, id, QB.DataSet),
				createQuantityValue(_matchSubject(statements, frequencyId)));
		ret.addTypes(_getTypes(statements, id));

		URI structureId = _getObjectId(statements, id, QB.structure);

		if (structureId != null) {
			ret.setStructure(createDataStructureDefinition(_matchSubject(
					statements, structureId)));
		}

		// Get all possible other component properties attached to this dataset
		// (in addition to the mandatory SDMXMetadata FREQ property)
		Set<URI> componentPropertyIds = _getPredicateIds(statements, id,
				QB.ComponentProperty);

		for (URI componentPropertyId : componentPropertyIds) {
			if (componentPropertyId.equals(SDMXMetadata.freq))
				continue;

			AttributeProperty property = createAttributeProperty(_matchSubject(
					statements, componentPropertyId));

			Value object = _getObject(statements, id, componentPropertyId);

			if (object instanceof URI) {
				ret.addComponent(
						property,
						createComponentPropertyValueEntity(_matchSubject(
								statements, (URI) object)));
			} else if (object instanceof Literal) {
				Literal literal = (Literal) object;
				URI datatype = literal.getDatatype();

				if (datatype.equals(XMLSchema.DOUBLE)) {
					ret.addComponent(
							property,
							new ComponentPropertyValueDouble(literal
									.doubleValue()));
				} else if (datatype.equals(XMLSchema.STRING)) {
					ret.addComponent(
							property,
							new ComponentPropertyValueString(literal
									.stringValue()));
				} else {
					if (log.isLoggable(Level.WARNING))
						log.warning("Failed to create primitive component property value; unrecognized datatype [datatype = "
								+ datatype + "]");
				}
			} else {
				throw new RuntimeException("Unsupported object type [object = "
						+ object + "; id = " + id + "; componentPropertyId = "
						+ componentPropertyId + "; statements = " + statements
						+ "]");
			}
		}

		return ret;
	}

	public Set<Statement> createRepresentation(DataStructureDefinition structure) {
		if (structure == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		URI id = structure.getId();
		Set<URI> types = structure.getTypes();

		for (URI type : types)
			ret.add(_statement(id, RDF.TYPE, type));

		Set<ComponentSpecification> components = structure.getComponents();

		for (ComponentSpecification component : components) {
			ret.add(_statement(id, QB.component, component.getId()));
			ret.addAll(createRepresentation(component));
		}

		return Collections.unmodifiableSet(ret);
	}

	public DataStructureDefinition createDataStructureDefinition(
			Set<Statement> statements) {
		if (statements == null)
			return null;
		if (statements.isEmpty())
			return null;

		URI id = _getId(statements, QB.DataStructureDefinition);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract data structure definition id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		DataStructureDefinition ret = new DataStructureDefinition(id, _getType(
				statements, id, QB.DataStructureDefinition));
		ret.addTypes(_getTypes(statements, id));

		Set<URI> componentIds = _getObjectIds(statements, id, QB.component);

		for (URI componentId : componentIds) {
			ret.addComponent(createComponentSpecification(_matchSubject(
					statements, componentId)));
		}

		return ret;
	}

	public Set<Statement> createRepresentation(ComponentSpecification component) {
		if (component == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		URI id = component.getId();
		Set<URI> types = component.getTypes();

		for (URI type : types)
			ret.add(_statement(id, RDF.TYPE, type));

		ComponentProperty property = component.getProperty();
		URI propertyId = property.getId();

		ret.add(_statement(id, QB.componentProperty, propertyId));

		Set<URI> propertyTypes = property.getTypes();

		if (propertyTypes.contains(QB.DimensionProperty))
			ret.add(_statement(id, QB.dimension, propertyId));
		else if (propertyTypes.contains(QB.MeasureProperty))
			ret.add(_statement(id, QB.measure, propertyId));
		else if (propertyTypes.contains(QB.AttributeProperty))
			ret.add(_statement(id, QB.attribute, propertyId));
		else {
			if (log.isLoggable(Level.WARNING))
				log.warning("Failed to identify component property type [propertyTypes = "
						+ propertyTypes + "; component = " + component + "]");
		}

		ret.addAll(createRepresentation(property));

		boolean isRequired = component.isRequired();

		if (isRequired)
			ret.add(_statement(id, QB.componentRequired,
					vf.createLiteral("true", XMLSchema.BOOLEAN)));
		else
			ret.add(_statement(id, QB.componentRequired,
					vf.createLiteral("false", XMLSchema.BOOLEAN)));

		Integer order = component.getOrder();

		if (component.getOrder() > -1)
			ret.add(_statement(id, QB.order,
					vf.createLiteral(order.toString(), XMLSchema.INT)));

		URI attachment = component.getComponentAttachment();

		if (attachment != null)
			ret.add(_statement(id, QB.componentAttachment, attachment));

		return Collections.unmodifiableSet(ret);
	}

	public ComponentSpecification createComponentSpecification(
			Set<Statement> statements) {
		if (statements == null)
			return null;
		if (statements.isEmpty())
			return null;

		URI id = _getId(statements, QB.ComponentSpecification);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract component specification id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		URI propertyId = _getObjectId(statements, id, QB.componentProperty);

		if (propertyId == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract component property id [propertyId = null; id = "
						+ id + "; statements = " + statements + "]");
			return null;
		}

		System.out.println(propertyId + " " + statements);
		System.out.println(_matchSubject(statements, propertyId));

		ComponentProperty property = createComponentProperty(_matchSubject(
				statements, propertyId));

		if (property == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to create component property [property = null; propertyId = "
						+ propertyId
						+ "; id = "
						+ id
						+ "; statements = "
						+ statements + "]");
			return null;
		}

		ComponentSpecification ret = new ComponentSpecification(id, _getType(
				statements, id, QB.ComponentSpecification), property);
		ret.addTypes(_getTypes(statements, id));

		Value required = _getObject(statements, id, QB.componentRequired);

		if (required != null) {
			if (required.stringValue().equalsIgnoreCase("true"))
				ret.setRequired(true);
			else
				ret.setRequired(false);
		}

		Value order = _getObject(statements, id, QB.order);

		if (order != null) {
			ret.setOrder(Integer.valueOf(order.stringValue()));
		}

		URI attachment = _getObjectId(statements, id, QB.componentAttachment);

		if (attachment != null) {
			ret.setComponentAttachment(attachment);
		}

		return ret;
	}

	public Set<Statement> createRepresentation(ComponentProperty property) {
		if (property == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		URI id = property.getId();
		Set<URI> types = property.getTypes();

		for (URI type : types)
			ret.add(_statement(id, RDF.TYPE, type));

		return Collections.unmodifiableSet(ret);
	}

	public ComponentProperty createComponentProperty(Set<Statement> statements) {
		if (statements == null)
			return null;
		if (statements.isEmpty())
			return null;

		URI id = _getId(statements, QB.ComponentProperty);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract component property id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		Set<URI> types = _getTypes(statements, id);

		if (types.contains(QB.DimensionProperty))
			return createDimensionProperty(statements);
		else if (types.contains(QB.MeasureProperty))
			return createMeasureProperty(statements);
		else if (types.contains(QB.AttributeProperty))
			return createAttributeProperty(statements);
		else {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to create component property; cannot determine type [types = "
						+ types + "; statements = " + statements + "]");
		}

		return null;
	}

	public Set<Statement> createRepresentation(DimensionProperty property) {
		if (property == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		URI id = property.getId();
		Set<URI> types = property.getTypes();

		for (URI type : types)
			ret.add(_statement(id, RDF.TYPE, type));

		return Collections.unmodifiableSet(ret);
	}

	public DimensionProperty createDimensionProperty(Set<Statement> statements) {
		if (statements == null)
			return null;
		if (statements.isEmpty())
			return null;

		URI id = _getId(statements, QB.DimensionProperty);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract dimension property id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		DimensionProperty ret = new DimensionProperty(id);
		ret.addTypes(_getTypes(statements, id));

		return ret;
	}

	public Set<Statement> createRepresentation(MeasureProperty property) {
		if (property == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		URI id = property.getId();
		Set<URI> types = property.getTypes();

		for (URI type : types)
			ret.add(_statement(id, RDF.TYPE, type));

		return Collections.unmodifiableSet(ret);
	}

	public MeasureProperty createMeasureProperty(Set<Statement> statements) {
		if (statements == null)
			return null;
		if (statements.isEmpty())
			return null;

		URI id = _getId(statements, QB.MeasureProperty);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract measure property id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		MeasureProperty ret = new MeasureProperty(id);
		ret.addTypes(_getTypes(statements, id));

		return ret;
	}

	public Set<Statement> createRepresentation(AttributeProperty property) {
		if (property == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		URI id = property.getId();
		Set<URI> types = property.getTypes();

		for (URI type : types)
			ret.add(_statement(id, RDF.TYPE, type));

		return Collections.unmodifiableSet(ret);
	}

	public AttributeProperty createAttributeProperty(Set<Statement> statements) {
		if (statements == null)
			return null;
		if (statements.isEmpty())
			return null;

		URI id = _getId(statements, QB.AttributeProperty);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract attribute property id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		AttributeProperty ret = new AttributeProperty(id);
		ret.addTypes(_getTypes(statements, id));

		return ret;
	}

	public Set<Statement> createRepresentation(SensorObservation observation) {
		if (observation == null)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		Sensor sensor = observation.getSensor();
		Property property = observation.getProperty();
		FeatureOfInterest feature = observation.getFeatureOfInterest();
		SensorOutput result = observation.getObservationResult();
		TemporalEntity resultTime = observation.getObservationResultTime();

		URI id = observation.getId();
		URI sensorId = sensor.getId();
		URI propertyId = property.getId();
		URI featureId = feature.getId();
		URI resultId = result.getId();
		URI resultTimeId = resultTime.getId();

		ret.add(_statement(id, RDF.TYPE, observation.getType()));
		ret.add(_statement(id, SSN.observedBy, sensorId));
		ret.add(_statement(id, SSN.observedProperty, propertyId));
		ret.add(_statement(id, SSN.featureOfInterest, featureId));
		ret.add(_statement(id, SSN.observationResult, resultId));
		ret.add(_statement(id, SSN.observationResultTime, resultTimeId));

		ret.addAll(createRepresentation(sensor));
		ret.addAll(createRepresentation(property));
		ret.addAll(createRepresentation(feature));
		ret.addAll(createRepresentation(result));
		ret.addAll(createRepresentation(resultTime));

		return Collections.unmodifiableSet(ret);
	}

	public Set<SensorObservation> createSensorObservations(
			Set<Statement> statements) {
		if (statements == null)
			return Collections.emptySet();
		if (statements.isEmpty())
			return Collections.emptySet();

		Set<SensorObservation> ret = new HashSet<SensorObservation>();

		Set<URI> ids = _getIds(statements, SSN.Observation);

		for (URI id : ids) {
			ret.add(createSensorObservation(_matchSubject(statements, id)));
		}

		return Collections.unmodifiableSet(ret);
	}

	public SensorObservation createSensorObservation(Set<Statement> statements) {
		if (statements == null)
			return null;

		URI id = _getId(statements, SSN.Observation);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract observation id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		Sensor sensor = createSensor(statements);
		Property property = createProperty(statements);
		FeatureOfInterest feature = createFeatureOfInterest(statements);
		SensorOutput result = createSensorOutput(statements);
		TemporalEntity resultTime = createTemporalEntity(statements);

		SensorObservation ret = new SensorObservation(id, _getType(statements,
				id, SSN.Observation), sensor, property, feature, result,
				resultTime);
		ret.addTypes(_getTypes(statements, id));

		return ret;
	}

	public Set<Statement> createRepresentation(Sensor sensor) {
		Set<Statement> ret = new HashSet<Statement>();

		if (sensor == null)
			return Collections.unmodifiableSet(ret);

		URI sensorId = sensor.getId();

		ret.add(_statement(sensorId, RDF.TYPE, SSN.Sensor));
		ret.add(_statement(sensorId, RDF.TYPE, sensor.getType()));

		for (Property property : sensor.getObservedProperties()) {
			ret.add(_statement(sensorId, SSN.observes, property.getId()));
			ret.addAll(createRepresentation(property));
		}

		for (MeasurementCapability capability : sensor
				.getMeasurementCapabilities()) {
			ret.add(_statement(sensorId, SSN.hasMeasurementCapability,
					capability.getId()));
			ret.addAll(createRepresentation(capability));
		}

		return Collections.unmodifiableSet(ret);
	}

	public Set<Sensor> createSensors(Set<Statement> statements) {
		if (statements.isEmpty())
			return Collections.emptySet();

		Set<Sensor> ret = new HashSet<Sensor>();
		Set<URI> ids = _getIds(statements, SSN.Sensor);

		for (URI id : ids) {
			ret.add(createSensor(_matchSubject(statements, id)));
		}

		return Collections.unmodifiableSet(ret);
	}

	public Sensor createSensor(Set<Statement> statements) {
		URI id = _getId(statements, SSN.Sensor);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract sensor id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		Sensor ret = new Sensor(id, _getType(statements, id, SSN.Sensor));
		ret.addTypes(_getTypes(statements, id));

		Set<URI> propertyIds = _getObjectIds(statements, id, SSN.observes);

		for (URI propertyId : propertyIds) {
			Property property = createProperty(_matchSubject(statements,
					propertyId));

			if (property != null) {
				ret.addObservedProperty(property);

				FeatureOfInterest feature = createFeatureOfInterest(_matchSubject(
						statements,
						_getObjectId(statements, property.getId(),
								SSN.isPropertyOf)));

				if (feature != null)
					property.addPropertyOf(feature);
			}
		}

		Set<URI> measurementCapabilityIds = _getObjectIds(statements, id,
				SSN.hasMeasurementCapability);

		for (URI measurementCapabilityId : measurementCapabilityIds) {
			ret.addMeasurementCapability(createMeasurementCapability(_matchSubject(
					statements, measurementCapabilityId)));
		}

		return ret;
	}

	public Set<Statement> createRepresentation(MeasurementCapability capability) {
		Set<Statement> ret = new HashSet<Statement>();

		if (capability == null)
			return Collections.unmodifiableSet(ret);

		URI capabilityId = capability.getId();

		ret.add(_statement(capabilityId, RDF.TYPE, SSN.MeasurementCapability));
		ret.add(_statement(capabilityId, RDF.TYPE, capability.getType()));

		Set<MeasurementProperty> properties = capability
				.getMeasurementProperties();

		for (MeasurementProperty property : properties) {
			ret.add(_statement(capabilityId, SSN.hasMeasurementProperty,
					property.getId()));
			ret.addAll(createRepresentation(property));
		}

		return Collections.unmodifiableSet(ret);
	}

	public MeasurementCapability createMeasurementCapability(
			Set<Statement> statements) {
		URI id = _getId(statements, SSN.MeasurementCapability);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract measurement capability id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		MeasurementCapability ret = new MeasurementCapability(id, _getType(
				statements, id, SSN.MeasurementCapability));
		ret.addTypes(_getTypes(statements, id));

		Set<URI> measurementPropertyIds = _getObjectIds(statements, id,
				SSN.hasMeasurementProperty);

		for (URI measurementPropertyId : measurementPropertyIds) {
			ret.addMeasurementProperty(createMeasurementProperty(_matchSubject(
					statements, measurementPropertyId)));
		}

		return ret;
	}

	public Set<Statement> createRepresentation(MeasurementProperty property) {
		if (property == null)
			return Collections.emptySet();

		statements = new HashSet<Statement>();

		property.accept(measurementPropertyVisitor);

		return Collections.unmodifiableSet(statements);
	}

	public MeasurementProperty createMeasurementProperty(
			Set<Statement> statements) {
		URI id = _getId(statements, SSN.MeasurementProperty);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract measurement property id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		Set<URI> types = _getTypes(statements, id);

		if (types.contains(SSN.Frequency))
			return createFrequency(statements);

		if (log.isLoggable(Level.WARNING))
			log.warning("Failed to create measurement property, unrecognized type [id = "
					+ id
					+ "; types = "
					+ types
					+ "; statements = "
					+ statements + "]");

		return null;
	}

	public Set<Statement> createRepresentation(Frequency frequency) {
		Set<Statement> ret = new HashSet<Statement>();

		if (frequency == null)
			return Collections.unmodifiableSet(ret);

		URI frequencyId = frequency.getId();

		ret.add(_statement(frequencyId, RDF.TYPE, SSN.MeasurementProperty));
		ret.add(_statement(frequencyId, RDF.TYPE, SSN.Frequency));
		ret.add(_statement(frequencyId, RDF.TYPE, frequency.getType()));

		QuantityValue value = frequency.getQuantityValue();

		if (value != null) {
			ret.add(_statement(frequencyId, SSN.hasValue, value.getId()));
			ret.addAll(createRepresentation(value));
		}

		return Collections.unmodifiableSet(ret);
	}

	public Frequency createFrequency(Set<Statement> statements) {
		URI id = _getId(statements, SSN.Frequency);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract frequency id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		Frequency ret = new Frequency(id, _getType(statements, id,
				SSN.Frequency, SSN.MeasurementProperty));
		ret.addTypes(_getTypes(statements, id));

		QuantityValue value = createQuantityValue(_matchSubject(statements,
				_getObjectId(statements, id, SSN.hasValue)));

		if (value != null) {
			ret.setQuantityValue(value);
		}

		return ret;
	}

	public Set<Statement> createRepresentation(QuantityValue value) {
		Set<Statement> ret = new HashSet<Statement>();

		if (value == null)
			return Collections.unmodifiableSet(ret);

		URI valueId = value.getId();

		Literal literal = _literal(value.getNumericValue());

		ret.add(_statement(valueId, RDF.TYPE, QUDTSchema.QuantityValue));
		ret.add(_statement(valueId, RDF.TYPE, value.getType()));
		ret.add(_statement(valueId, DUL.hasRegionDataValue, literal));
		ret.add(_statement(valueId, QUDTSchema.numericValue, literal));

		Unit unit = value.getUnit();

		if (unit != null) {
			ret.add(_statement(valueId, QUDTSchema.unit, unit.getId()));
			ret.addAll(createRepresentation(unit));
		}

		return Collections.unmodifiableSet(ret);
	}

	public QuantityValue createQuantityValue(Set<Statement> statements) {
		URI id = _getId(statements, QUDTSchema.QuantityValue);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract quantity value id [id = null; statements = "
						+ statements + "]");
			return null;
		}

		QuantityValue ret = new QuantityValue(id, _getType(statements, id,
				QUDTSchema.QuantityValue));
		ret.addTypes(_getTypes(statements, id));

		Double value = null;

		for (Statement statement : statements) {
			if (statement.getSubject().equals(id)
					&& statement.getPredicate().equals(QUDTSchema.numericValue)) {
				value = Double.valueOf(statement.getObject().stringValue());
				break;
			}
		}

		if (value == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Failed to extract quantity value [value = null; statements = "
						+ statements + "]");
		} else {
			ret.setNumericValue(value);
		}

		Unit unit = createUnit(_matchSubject(statements,
				_getObjectId(statements, id, QUDTSchema.unit)));

		if (unit == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Failed to extract unit [unit = null; statements = "
						+ statements + "]");
		} else {
			ret.setUnit(unit);
		}

		return ret;
	}

	public Set<Statement> createRepresentation(Unit unit) {
		Set<Statement> ret = new HashSet<Statement>();

		if (unit == null)
			return Collections.unmodifiableSet(ret);

		URI unitId = unit.getId();

		ret.add(_statement(unitId, RDF.TYPE, QUDTSchema.Unit));
		ret.add(_statement(unitId, RDF.TYPE, unit.getType()));

		return Collections.unmodifiableSet(ret);
	}

	public Unit createUnit(Set<Statement> statements) {
		URI id = _getId(statements, QUDTSchema.Unit);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract unit id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		Unit ret = new Unit(id, _getType(statements, id, QUDTSchema.Unit));
		ret.addTypes(_getTypes(statements, id));

		return ret;
	}

	public Set<Statement> createRepresentation(Property property) {
		Set<Statement> ret = new HashSet<Statement>();

		if (property == null)
			return Collections.unmodifiableSet(ret);

		URI propertyId = property.getId();

		ret.add(_statement(propertyId, RDF.TYPE, SSN.Property));
		ret.add(_statement(propertyId, RDF.TYPE, property.getType()));

		Set<FeatureOfInterest> features = property.getPropertiesOf();

		for (FeatureOfInterest feature : features) {
			ret.add(_statement(propertyId, SSN.isPropertyOf, feature.getId()));
			ret.addAll(createRepresentation(feature));
		}

		return Collections.unmodifiableSet(ret);
	}

	public Property createProperty(Set<Statement> statements) {
		URI id = _getId(statements, SSN.Property);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract property id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		Property ret = new Property(id, _getType(statements, id, SSN.Property));
		ret.addTypes(_getTypes(statements, id));

		Set<URI> featureIds = _getObjectIds(statements, id, SSN.isPropertyOf);

		for (URI featureId : featureIds) {
			ret.addPropertyOf(createFeatureOfInterest(_matchSubject(statements,
					featureId)));
		}

		return ret;
	}

	public Set<Statement> createRepresentation(FeatureOfInterest feature) {
		Set<Statement> ret = new HashSet<Statement>();

		if (feature == null)
			return Collections.unmodifiableSet(ret);

		ret.add(_statement(feature.getId(), RDF.TYPE, SSN.FeatureOfInterest));
		ret.add(_statement(feature.getId(), RDF.TYPE, feature.getType()));

		return Collections.unmodifiableSet(ret);
	}

	public FeatureOfInterest createFeatureOfInterest(Set<Statement> statements) {
		URI id = _getId(statements, SSN.FeatureOfInterest);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract feature id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		FeatureOfInterest ret = new FeatureOfInterest(id, _getType(statements,
				id, SSN.FeatureOfInterest));
		ret.addTypes(_getTypes(statements, id));

		return ret;
	}

	public Set<Statement> createRepresentation(SensorOutput result) {
		Set<Statement> ret = new HashSet<Statement>();

		if (result == null)
			return Collections.unmodifiableSet(ret);

		URI id = result.getId();

		ObservationValue value = result.getValue();
		URI valueId = value.getId();

		ret.add(_statement(id, RDF.TYPE, SSN.SensorOutput));
		ret.add(_statement(id, RDF.TYPE, result.getType()));
		ret.add(_statement(id, SSN.hasValue, valueId));
		ret.addAll(createRepresentation(value));

		return Collections.unmodifiableSet(ret);
	}

	public SensorOutput createSensorOutput(Set<Statement> statements) {
		URI id = _getId(statements, SSN.SensorOutput);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract output id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		SensorOutput ret = new SensorOutput(id, _getType(statements, id,
				SSN.SensorOutput), createObservationValue(statements));
		ret.addTypes(_getTypes(statements, id));

		return ret;
	}

	public Set<Statement> createRepresentation(ObservationValue value) {
		if (value == null)
			return Collections.emptySet();

		statements = new HashSet<Statement>();

		value.accept(observationValueVisitor);

		return Collections.unmodifiableSet(statements);
	}

	public ObservationValue createObservationValue(Set<Statement> statements) {
		return createObservationValueDouble(statements);
	}

	public Set<Statement> createRepresentation(ObservationValueDouble value) {
		Set<Statement> ret = new HashSet<Statement>();

		if (value == null)
			return Collections.unmodifiableSet(ret);

		URI id = value.getId();

		ret.add(_statement(id, RDF.TYPE, SSN.ObservationValue));
		ret.add(_statement(id, RDF.TYPE, value.getType()));
		ret.add(_statement(id, DUL.hasRegionDataValue,
				vf.createLiteral(value.getValue())));

		return Collections.unmodifiableSet(ret);
	}

	public ObservationValueDouble createObservationValueDouble(
			Set<Statement> statements) {
		URI id = _getId(statements, SSN.ObservationValue);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract observation value id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		Double value = null;

		for (Statement statement : statements) {
			if (statement.getSubject().equals(id)
					&& statement.getPredicate().equals(DUL.hasRegionDataValue)) {
				value = Double.valueOf(statement.getObject().stringValue());
				break;
			}
		}

		if (value == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Failed to extract observation value [value = null; statements = "
						+ statements + "]");
		}

		ObservationValueDouble ret = new ObservationValueDouble(id, _getType(
				statements, id, SSN.ObservationValue), value);
		ret.addTypes(_getTypes(statements, id));

		return ret;
	}

	public Set<Statement> createRepresentation(TemporalEntity resultTime) {
		if (resultTime == null)
			return Collections.emptySet();

		statements = new HashSet<Statement>();

		resultTime.accept(temporalEntityVisitor);

		return Collections.unmodifiableSet(statements);
	}

	public TemporalEntity createTemporalEntity(Set<Statement> statements) {
		return createInstant(statements);
	}

	public Set<Statement> createRepresentation(Instant instant) {
		Set<Statement> ret = new HashSet<Statement>();

		if (instant == null)
			return Collections.unmodifiableSet(ret);

		URI id = instant.getId();

		Set<URI> types = instant.getTypes();

		for (URI type : types)
			ret.add(_statement(id, RDF.TYPE, type));

		ret.add(_statement(id, Time.inXSDDateTime, vf.createLiteral(
				dtf.print(instant.getValue()), XMLSchema.DATETIME)));

		return Collections.unmodifiableSet(ret);
	}

	public Instant createInstant(Set<Statement> statements) {
		URI id = _getId(statements, Time.Instant);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract instant id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		DateTime value = null;

		for (Statement statement : statements) {
			if (statement.getSubject().equals(id)
					&& statement.getPredicate().equals(Time.inXSDDateTime)) {
				value = dtf
						.parseDateTime((statement.getObject().stringValue()));
				break;
			}
		}

		if (value == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Failed to extract instant value [value = null; statements = "
						+ statements + "]");
		}

		Instant ret = new Instant(id, _getType(statements, id, Time.Instant,
				Time.TemporalEntity), value);
		ret.addTypes(_getTypes(statements, id));

		return ret;
	}

	private static Literal _literal(Double value) {
		return vf.createLiteral(value);
	}

	private static Statement _statement(URI s, URI p, Value o) {
		return vf.createStatement(s, p, o);
	}

	private static Set<URI> _getIds(Set<Statement> statements, URI type) {
		Set<URI> ret = new HashSet<URI>();

		for (Statement statement : statements) {
			if (statement.getPredicate().equals(RDF.TYPE)
					&& statement.getObject().equals(type))
				ret.add(vf.createURI(statement.getSubject().stringValue()));
		}

		return Collections.unmodifiableSet(ret);
	}

	private static URI _getId(Set<Statement> statements, URI type) {
		for (Statement statement : statements) {
			if (statement.getPredicate().equals(RDF.TYPE)
					&& statement.getObject().equals(type))
				return vf.createURI(statement.getSubject().stringValue());
		}

		return null;
	}

	private static URI _getType(Set<Statement> statements, URI id, URI... types) {
		URI ret = null;
		List<URI> collection = Arrays.asList(types);

		if (collection.size() > 0)
			ret = collection.get(0);

		if (id == null)
			return ret;

		for (Statement statement : statements) {
			if (statement.getSubject().equals(id)
					&& statement.getPredicate().equals(RDF.TYPE)
					&& !collection.contains(statement.getObject()))
				return vf.createURI(statement.getObject().stringValue());
		}

		return ret;
	}

	private static Set<URI> _getTypes(Set<Statement> statements, URI id) {
		if (id == null)
			return Collections.emptySet();

		Set<URI> ret = new HashSet<URI>();

		for (Statement statement : statements) {
			if (statement.getSubject().equals(id)
					&& statement.getPredicate().equals(RDF.TYPE))
				ret.add(vf.createURI(statement.getObject().stringValue()));
		}

		return ret;
	}

	private static Set<URI> _getPredicateIds(Set<Statement> statements,
			URI subject, URI type) {
		if (subject == null)
			return Collections.emptySet();

		Set<URI> ret = new HashSet<URI>();

		for (Statement statement : statements) {
			Resource s = statement.getSubject();

			if (!s.equals(subject))
				continue;

			URI p = statement.getPredicate();

			if (type != null) {
				if (_getTypes(statements, p).contains(type))
					ret.add(p);
			} else {
				ret.add(p);
			}
		}

		return ret;
	}

	private static URI _getObjectId(Set<Statement> statements, URI subject,
			URI predicate) {
		Value ret = _getObject(statements, subject, predicate);

		if (ret == null)
			return null;

		return vf.createURI(ret.stringValue());
	}

	private static Value _getObject(Set<Statement> statements, URI subject,
			URI predicate) {
		for (Statement statement : statements) {
			if (statement.getSubject().equals(subject)
					&& statement.getPredicate().equals(predicate))
				return statement.getObject();
		}

		return null;
	}

	private static Set<URI> _getObjectIds(Set<Statement> statements,
			URI subject, URI predicate) {
		Set<URI> ret = new HashSet<URI>();

		for (Statement statement : statements) {
			if (statement.getSubject().equals(subject)
					&& statement.getPredicate().equals(predicate))
				ret.add(vf.createURI(statement.getObject().stringValue()));
		}

		return Collections.unmodifiableSet(ret);
	}

	private static Set<Statement> _matchSubject(Set<Statement> statements,
			URI subject) {
		if (statements.isEmpty())
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();

		for (Statement statement : statements) {
			if (statement.getSubject().equals(subject)) {
				ret.add(statement);
				Value object = statement.getObject();
				if (object instanceof URI)
					ret.addAll(_matchSubject(statements,
							vf.createURI(object.stringValue())));
			}
		}

		return Collections.unmodifiableSet(ret);
	}

	private class RepresenterEntityVisitor implements EntityVisitor {

		@Override
		public void visit(SensorObservation entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(Sensor entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(Property entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(FeatureOfInterest entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(SensorOutput entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(ObservationValue entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(ObservationValueDouble entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(MeasurementCapability entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(Frequency entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(TemporalEntity entity) {
			statements.addAll(createRepresentation(entity));
		}

		@Override
		public void visit(Instant entity) {
			statements.addAll(createRepresentation(entity));
		}

		@Override
		public void visit(QuantityValue entity) {
			statements.addAll(createRepresentation(entity));
		}

		@Override
		public void visit(Unit entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(Dataset entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(DataStructureDefinition entity) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void visit(ComponentSpecification entity) {
			statements.addAll(createRepresentation(entity));
		}

		@Override
		public void visit(DimensionProperty entity) {
			statements.addAll(createRepresentation(entity));
		}

		@Override
		public void visit(MeasureProperty entity) {
			statements.addAll(createRepresentation(entity));
		}

		@Override
		public void visit(AttributeProperty entity) {
			statements.addAll(createRepresentation(entity));
		}

		@Override
		public void visit(DatasetObservation entity) {
			throw new UnsupportedOperationException();
		}

	}

	private class RepresenterObservationValueVisitor implements
			ObservationValueVisitor {

		@Override
		public void visit(ObservationValueDouble entity) {
			statements.addAll(createRepresentation(entity));
		}

		@Override
		public void visit(QuantityValue entity) {
			statements.addAll(createRepresentation(entity));
		}

	}

	private class RepresenterMeasurementPropertyVisitor implements
			MeasurementPropertyVisitor {

		@Override
		public void visit(Frequency entity) {
			statements.addAll(createRepresentation(entity));
		}

	}

	private class RepresenterTemporalEntityVisitor implements
			TemporalEntityVisitor {

		@Override
		public void visit(Instant entity) {
			statements.addAll(createRepresentation(entity));
		}
	}

	private class RepresenterComponentPropertyValueVisitor implements
			ComponentPropertyValueVisitor {

		@Override
		public void visit(ComponentPropertyValueEntity value) {
			if (id == null || componentPropertyId == null)
				throw new NullPointerException("[id = " + id
						+ "; componentPropertyId = " + componentPropertyId
						+ "]");

			Entity entity = value.getValue();

			Set<Statement> ret = new HashSet<Statement>();

			ret.add(_statement(id, componentPropertyId, entity.getId()));
			ret.addAll(createRepresentation(entity));

			statements.addAll(ret);
		}

		@Override
		public void visit(ComponentPropertyValueString value) {
			if (id == null || componentPropertyId == null)
				throw new NullPointerException("[id = " + id
						+ "; componentPropertyId = " + componentPropertyId
						+ "]");

			statements.add(_statement(id, componentPropertyId,
					vf.createLiteral(value.getValue(), XMLSchema.STRING)));
		}

		@Override
		public void visit(ComponentPropertyValueDouble value) {
			if (id == null || componentPropertyId == null)
				throw new NullPointerException("[id = " + id
						+ "; componentPropertyId = " + componentPropertyId
						+ "]");

			statements.add(_statement(id, componentPropertyId, vf
					.createLiteral(value.getValue().toString(),
							XMLSchema.DOUBLE)));
		}
	}

}
