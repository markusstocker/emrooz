/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.parser.ParsedQuery;

import fi.uef.envi.emrooz.api.DataStore;
import fi.uef.envi.emrooz.api.KnowledgeStore;
import fi.uef.envi.emrooz.api.QueryHandler;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.entity.ComponentPropertyValueVisitor;
import fi.uef.envi.emrooz.entity.Entity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.qb.AttributeProperty;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValue;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueDouble;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueEntity;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueInteger;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueLong;
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
import fi.uef.envi.emrooz.query.DatasetObservationQuery;
import fi.uef.envi.emrooz.query.EmptyResultSet;
import fi.uef.envi.emrooz.query.QueryFactory;
import fi.uef.envi.emrooz.query.SensorObservationQuery;
import fi.uef.envi.emrooz.query.SensorObservationQueryRewriter;
import fi.uef.envi.emrooz.rdf.RDFEntityRepresenter;
import fi.uef.envi.emrooz.vocabulary.SDMXDimension;
import fi.uef.envi.emrooz.vocabulary.SDMXMetadata;

/**
 * <p>
 * Title: Emrooz
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

public class Emrooz {

	private KnowledgeStore ks;
	private DataStore ds;

	private Map<URI, Map<URI, Sensor>> sensors;
	private Map<URI, Sensor> sensorsById;
	private Map<URI, Dataset> datasets;

	private DateTime instant = null;
	private Entity entity = null;
	private final EntityVisitor entityVisitor;
	private final ComponentPropertyValueVisitor componentPropertyValueVisitor;
	private final RDFEntityRepresenter representer;
	private SensorObservationQueryRewriter sensorObservationQueryRewriter;
	private Map<Sensor, Frequency> sensorFrequencyCache;
	private Map<URI, QuantityValue> datasetFrequencyCache;

	private static final Logger log = Logger.getLogger(Emrooz.class.getName());

	public Emrooz(KnowledgeStore ks, DataStore ds) {
		if (ks == null)
			throw new NullPointerException(
					"Knowledge store cannot be null [ks = null]");
		if (ds == null)
			throw new NullPointerException(
					"Data store cannot be null [ds = null]");

		this.ks = ks;
		this.ds = ds;

		this.sensors = new HashMap<URI, Map<URI, Sensor>>();
		this.sensorsById = new HashMap<URI, Sensor>();
		this.datasets = new HashMap<URI, Dataset>();
		this.entityVisitor = new EmroozEntityVisitor();
		this.componentPropertyValueVisitor = new EmroozComponentPropertyValueVisitor();
		this.representer = new RDFEntityRepresenter();
		this.sensorObservationQueryRewriter = new SensorObservationQueryRewriter(
				ks);
		this.sensorFrequencyCache = new HashMap<Sensor, Frequency>();
		this.datasetFrequencyCache = new HashMap<URI, QuantityValue>();

		sensors();
		datasets();
	}

	public void loadKnowledgeBase(File file) {
		ks.load(file);
		sensors();
		datasets();
	}

	public void add(Sensor sensor) {
		ks.addSensor(sensor);
		sensors();
	}

	public void add(Dataset dataset) {
		ks.addDataset(dataset);
		datasets();
	}

	public Sensor getSensorById(URI sensorId) {
		if (sensorId == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("[sensorId = null]");
			return null;
		}

		Sensor ret = sensorsById.get(sensorId);

		if (ret == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Cannot find sensor [sensorId = " + sensorId
						+ "; sensorsById = " + sensorsById + "]");
		}

		return ret;
	}

	public Dataset getDatasetById(URI datasetId) {
		if (datasetId == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("[datasetId = null]");
			return null;
		}

		Dataset ret = datasets.get(datasetId);

		if (ret == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Cannot find dataset [datasetId = " + datasetId
						+ "; datasets = " + datasets + "]");
		}

		return ret;
	}

	public void addSensorObservation(Set<Statement> statements) {
		add(representer.createSensorObservation(statements));
	}

	public void addSensorObservations(Set<Statement> statements) {
		add(representer.createSensorObservations(statements));
	}

	public void add(Set<SensorObservation> observations) {
		for (SensorObservation observation : observations)
			add(observation);
	}

	public void add(SensorObservation observation) {
		if (observation == null)
			return;

		instant = null;

		Sensor sensor = observation.getSensor();
		Property property = observation.getProperty();
		FeatureOfInterest feature = observation.getFeatureOfInterest();
		TemporalEntity temporalEntity = observation.getObservationResultTime();

		if (temporalEntity == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Temporal entity of observation is null [observation = "
						+ observation + "]");
			return;
		}

		temporalEntity.accept(entityVisitor);

		DateTime resultTime = instant;

		if (sensor == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Sensor of observation is null [observation = "
						+ observation + "]");
			return;
		}

		if (property == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Property of observation is null [observation = "
						+ observation + "]");
			return;
		}

		if (feature == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Feature of observation is null [observation = "
						+ observation + "]");
			return;
		}

		if (resultTime == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Result time of observation is null [observation = "
						+ observation + "]");

			return;
		}

		addSensorObservation(observation.getSensor(),
				observation.getProperty(), observation.getFeatureOfInterest(),
				instant, representer.createRepresentation(observation));
	}

	public void addSensorObservation(Sensor sensor, Property property,
			FeatureOfInterest feature, DateTime resultTime,
			Set<Statement> statements) {
		if (sensors.isEmpty())
			sensors();

		URI sensorId = sensor.getId();
		URI propertyId = property.getId();
		URI featureId = feature.getId();

		Sensor specification = getSensorSpecification(sensorId, propertyId);

		if (specification == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No specification found [sensorId = " + sensorId
						+ "; propertyId = " + propertyId + "; featureId = "
						+ featureId + "]");
			return;
		}

		Frequency frequency = getSensorFrequency(specification);

		if (frequency == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No frequency specified [specification = "
						+ specification + "]");
			return;
		}

		ds.addSensorObservation(sensorId, propertyId, featureId, frequency,
				resultTime, statements);
	}

	public void addDatasetObservation(Set<Statement> statements) {
		add(representer.createDatasetObservation(statements));
	}

	public void addDatasetObservations(Set<Statement> statements) {
		throw new UnsupportedOperationException(); // TODO
		// add(representer.createDatasetObservations(statements));
	}

	// public void add(Set<DatasetObservation> observations) {
	// for (DatasetObservation observation : observations)
	// add(observation);
	// }

	public void add(DatasetObservation observation) {
		if (observation == null)
			return;

		URI datasetId = observation.getDatasetId();
		ComponentPropertyValue componentPropertyValue = observation
				.getComponentPropertyValue(new DimensionProperty(
						SDMXDimension.timePeriod));

		if (componentPropertyValue == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to obtain timePeriod component property value of observation [observation = "
						+ observation + "]");
			return;
		}

		entity = null;

		componentPropertyValue.accept(componentPropertyValueVisitor);

		if (entity == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to obtain value entity for component property [componentPropertyValue = "
						+ componentPropertyValue
						+ "; observation = "
						+ observation + "]");
			return;
		}

		instant = null;

		entity.accept(entityVisitor);

		DateTime timePeriod = instant;

		if (timePeriod == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Time period of observation is null [observation = "
						+ observation + "]");

			return;
		}

		addDatasetObservation(datasetId, timePeriod,
				representer.createRepresentation(observation));
	}

	public void addDatasetObservation(URI datasetId, DateTime timePeriod,
			Set<Statement> statements) {
		if (datasets.isEmpty())
			datasets();

		Dataset specification = getDatasetSpecification(datasetId);

		if (specification == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No specification found [datasetId = " + datasetId
						+ "]");
			return;
		}

		QuantityValue frequency = getDatasetFrequency(specification);

		if (frequency == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No frequency specified [specification = "
						+ specification + "]");
			return;
		}

		ds.addDatasetObservation(datasetId, frequency, timePeriod, statements);
	}

	public ResultSet<BindingSet> evaluate(QueryType type, String query) {
		return evaluate(type, QueryFactory.createParsedQuery(query));
	}

	public void evaluate(QueryType type, String query,
			TupleQueryResultHandler handler) {
		evaluate(type, QueryFactory.createParsedQuery(query), handler);
	}

	private ResultSet<BindingSet> evaluate(QueryType type, ParsedQuery query) {
		if (type.equals(QueryType.SENSOR_OBSERVATION))
			return evaluate(query,
					QueryFactory.createSensorObservationQuery(query));
		if (type.equals(QueryType.DATASET_OBSERVATION))
			return evaluate(query,
					QueryFactory.createDatasetObservationQuery(query));

		if (log.isLoggable(Level.SEVERE))
			log.severe("Failed to deterine observation query type, sensor or dataset [query = "
					+ query + "]");

		return new EmptyResultSet<BindingSet>();
	}

	private void evaluate(QueryType type, ParsedQuery query,
			TupleQueryResultHandler handler) {
		if (type.equals(QueryType.SENSOR_OBSERVATION)) {
			evaluate(query, QueryFactory.createSensorObservationQuery(query),
					handler);
			return;
		}
		if (type.equals(QueryType.DATASET_OBSERVATION)) {
			evaluate(query, QueryFactory.createDatasetObservationQuery(query),
					handler);
			return;
		}

		if (log.isLoggable(Level.SEVERE))
			log.severe("Failed to deterine observation query type, sensor or dataset [query = "
					+ query + "]");
	}

	private ResultSet<BindingSet> evaluate(ParsedQuery original,
			SensorObservationQuery query) {
		QueryHandler<BindingSet> qh = createQueryHandler(original, query);

		if (qh == null)
			return new EmptyResultSet<BindingSet>();

		return qh.evaluate();
	}

	private ResultSet<BindingSet> evaluate(ParsedQuery original,
			DatasetObservationQuery query) {
		QueryHandler<BindingSet> qh = createQueryHandler(original, query);

		if (qh == null)
			return new EmptyResultSet<BindingSet>();

		return qh.evaluate();
	}

	private void evaluate(ParsedQuery original, SensorObservationQuery query,
			TupleQueryResultHandler handler) {
		QueryHandler<BindingSet> qh = createQueryHandler(original, query);

		if (qh == null)
			return;

		qh.evaluate(handler);
	}

	private void evaluate(ParsedQuery original, DatasetObservationQuery query,
			TupleQueryResultHandler handler) {
		QueryHandler<BindingSet> qh = createQueryHandler(original, query);

		if (qh == null)
			return;

		qh.evaluate(handler);
	}

	public void close() {
		ks.close();
		ds.close();
	}

	private QueryHandler<BindingSet> createQueryHandler(ParsedQuery original,
			SensorObservationQuery query) {
		if (log.isLoggable(Level.INFO))
			log.info("Query [query = " + query + "; original = "
					+ original.getSourceString() + "]");

		Map<SensorObservationQuery, Frequency> queriesMap = new HashMap<SensorObservationQuery, Frequency>();
		Set<SensorObservationQuery> rewrittenQueries = sensorObservationQueryRewriter
				.rewrite(query);

		if (rewrittenQueries.isEmpty()) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Rewritten queries is empty [query = " + query
						+ "]");

			return null;
		}

		if (log.isLoggable(Level.INFO))
			log.info("Rewritten queries (" + rewrittenQueries.size()
					+ ") [rewrittenQueries = " + rewrittenQueries + "]");

		for (SensorObservationQuery rewrittenQuery : rewrittenQueries) {
			URI sensorId = rewrittenQuery.getSensorId();

			if (sensorId == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("No specified sensor in rewrittenQuery [rewrittenQuery = "
							+ rewrittenQuery + "]");
				return null;
			}

			URI propertyId = rewrittenQuery.getPropertyId();

			if (propertyId == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("No specified property in rewrittenQuery [rewrittenQuery = "
							+ rewrittenQuery + "]");
				return null;
			}

			URI featureId = rewrittenQuery.getFeatureOfInterestId();

			if (featureId == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("No specified feature in rewrittenQuery [rewrittenQuery = "
							+ rewrittenQuery + "]");
				return null;
			}

			Sensor specification = getSensorSpecification(sensorId, propertyId);

			if (specification == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("No specification found [sensorId = "
							+ sensorId + "; propertyId = " + propertyId + "]");
				return null;
			}

			Frequency frequency = getSensorFrequency(specification);

			if (frequency == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("No frequency specified [specification = "
							+ specification + "]");
				return null;
			}

			queriesMap.put(rewrittenQuery, frequency);
		}

		return ks.createQueryHandler(
				ds.createSensorObservationQueryHandler(queriesMap), original);
	}

	private QueryHandler<BindingSet> createQueryHandler(ParsedQuery original,
			DatasetObservationQuery query) {
		if (log.isLoggable(Level.INFO))
			log.info("Query [query = " + query + "; original = "
					+ original.getSourceString() + "]");

		Map<DatasetObservationQuery, QuantityValue> queriesMap = new HashMap<DatasetObservationQuery, QuantityValue>();

		URI datasetId = query.getDatasetId();

		Dataset specification = getDatasetSpecification(datasetId);

		if (specification == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No specification found [datasetId = " + datasetId
						+ "]");
			return null;
		}

		QuantityValue frequency = getDatasetFrequency(specification);

		if (frequency == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No frequency specified [specification = "
						+ specification + "]");
			return null;
		}

		queriesMap.put(query, frequency);

		return ks.createQueryHandler(
				ds.createDatasetObservationQueryHandler(queriesMap), original);
	}

	private void sensors() {
		sensors.clear();
		sensorsById.clear();

		Set<Sensor> sensors = ks.getSensors();

		for (Sensor sensor : sensors) {
			URI sensorId = sensor.getId();

			sensorsById.put(sensorId, sensor);

			Map<URI, Sensor> m1 = this.sensors.get(sensorId);

			if (m1 == null) {
				m1 = new HashMap<URI, Sensor>();
				this.sensors.put(sensorId, m1);
			}

			Set<Property> properties = sensor.getObservedProperties();

			if (properties.isEmpty()) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Sensor must specify at least one observed property [sensor = "
							+ sensor + "]");
				continue;
			}

			for (Property property : properties) {
				m1.put(property.getId(), sensor);
			}
		}
	}

	private void datasets() {
		datasets.clear();

		Set<Dataset> datasets = ks.getDatasets();

		for (Dataset dataset : datasets) {
			URI datasetId = dataset.getId();

			this.datasets.put(datasetId, dataset);
		}
	}

	private Sensor getSensorSpecification(URI sensorId, URI propertyId) {
		Map<URI, Sensor> m1 = sensors.get(sensorId);

		if (m1 == null) {
			sensors();
			m1 = sensors.get(sensorId);
			if (m1 == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Failed to resolve sensor specification for sensor [sensorId = "
							+ sensorId + "; sensors = " + sensors + "]");
				return null;
			}
		}

		Sensor specification = m1.get(propertyId);

		if (specification == null) {
			// Load sensors and check again, perhaps there are new sensors
			sensors();
			m1 = sensors.get(sensorId);
			specification = m1.get(propertyId);
			if (specification == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Failed to resolve sensor specification for feature [sensorId = "
							+ sensorId
							+ "; propertyId = "
							+ propertyId
							+ "; sensors = " + sensors + "]");
				return null;
			}
		}

		return specification;
	}

	private Dataset getDatasetSpecification(URI datasetId) {
		Dataset specification = datasets.get(datasetId);

		if (specification == null) {
			datasets();
			specification = datasets.get(datasetId);
			if (specification == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Failed to resolve dataset specification [datasetId = "
							+ datasetId + "]");
				return null;
			}
		}

		return specification;
	}

	private Frequency getSensorFrequency(Sensor specification) {
		Frequency ret = sensorFrequencyCache.get(specification);

		if (ret != null)
			return ret;

		Set<MeasurementCapability> measCapabilities = specification
				.getMeasurementCapabilities();

		for (MeasurementCapability measCapability : measCapabilities) {
			Set<MeasurementProperty> measProperties = measCapability
					.getMeasurementProperties();

			for (MeasurementProperty measProperty : measProperties) {
				if (!(measProperty instanceof Frequency))
					continue;

				ret = (Frequency) measProperty;

				sensorFrequencyCache.put(specification, ret);

				return ret;
			}
		}

		return null;
	}

	private QuantityValue getDatasetFrequency(Dataset specification) {
		URI datasetId = specification.getId();

		QuantityValue ret = datasetFrequencyCache.get(datasetId);

		if (ret != null)
			return ret;

		for (Map.Entry<AttributeProperty, ComponentPropertyValue> component : specification
				.getComponents().entrySet()) {
			AttributeProperty property = component.getKey();

			if (!property.getId().equals(SDMXMetadata.freq))
				continue;

			ret = (QuantityValue) component.getValue().getValue();

			datasetFrequencyCache.put(datasetId, ret);

			return ret;
		}

		return null;
	}

	private class EmroozComponentPropertyValueVisitor implements
			ComponentPropertyValueVisitor {

		@Override
		public void visit(ComponentPropertyValueEntity value) {
			entity = value.getValue();
		}

		@Override
		public void visit(ComponentPropertyValueString value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(ComponentPropertyValueDouble value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(ComponentPropertyValueInteger value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visit(ComponentPropertyValueLong value) {
			// TODO Auto-generated method stub
			
		}

	}

	private class EmroozEntityVisitor implements EntityVisitor {

		@Override
		public void visit(SensorObservation entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(Sensor entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(Property entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(FeatureOfInterest entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(SensorOutput entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(ObservationValue entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(ObservationValueDouble entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(MeasurementCapability entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(Frequency entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(TemporalEntity entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(Instant entity) {
			instant = entity.getValue();
		}

		@Override
		public void visit(QuantityValue entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(Unit entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(Dataset entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(DataStructureDefinition entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(ComponentSpecification entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(DimensionProperty entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(MeasureProperty entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(AttributeProperty entity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void visit(DatasetObservation entity) {
			// TODO Auto-generated method stub

		}

	}

}
