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
import fi.uef.envi.emrooz.entity.TemporalEntityVisitor;
import fi.uef.envi.emrooz.entity.qb.Dataset;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.MeasurementProperty;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.entity.time.TemporalEntity;
import fi.uef.envi.emrooz.query.EmptyResultSet;
import fi.uef.envi.emrooz.query.QueryFactory;
import fi.uef.envi.emrooz.query.SensorObservationQuery;
import fi.uef.envi.emrooz.query.SensorObservationQueryRewriter;
import fi.uef.envi.emrooz.rdf.RDFEntityRepresenter;

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
	private Map<URI, Map<URI, Dataset>> datasets;
	private Map<URI, Dataset> datasetsById;

	private DateTime instant = null;
	private final TemporalEntityVisitor temporalEntityVisitor;
	private final RDFEntityRepresenter representer;
	private SensorObservationQueryRewriter sensorObservationQueryRewriter;
	private Map<Sensor, Frequency> frequencyCache;

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
		this.datasets = new HashMap<URI, Map<URI, Dataset>>();
		this.datasetsById = new HashMap<URI, Dataset>();
		this.temporalEntityVisitor = new EmroozTemporalEntityVisitor();
		this.representer = new RDFEntityRepresenter();
		this.sensorObservationQueryRewriter = new SensorObservationQueryRewriter(
				ks);
		this.frequencyCache = new HashMap<Sensor, Frequency>();

		sensors();
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

		temporalEntity.accept(temporalEntityVisitor);

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

		Sensor specification = getSpecification(sensorId, propertyId);

		if (specification == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No specification found [sensorId = " + sensorId
						+ "; propertyId = " + propertyId + "; featureId = "
						+ featureId + "]");
			return;
		}

		Frequency frequency = getFrequency(specification);

		if (frequency == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No frequency specified [specification = "
						+ specification + "]");
			return;
		}

		ds.addSensorObservation(sensorId, propertyId, featureId, frequency,
				resultTime, statements);
	}

	public ResultSet<BindingSet> evaluate(String query) {
		return evaluate(QueryFactory.createParsedQuery(query));
	}

	public void evaluate(String query, TupleQueryResultHandler handler) {
		evaluate(QueryFactory.createParsedQuery(query), handler);
	}

	private ResultSet<BindingSet> evaluate(ParsedQuery query) {
		return evaluate(query, QueryFactory.createSensorObservationQuery(query));
	}

	private void evaluate(ParsedQuery query, TupleQueryResultHandler handler) {
		evaluate(query, QueryFactory.createSensorObservationQuery(query),
				handler);
	}

	private ResultSet<BindingSet> evaluate(ParsedQuery original,
			SensorObservationQuery query) {
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

			Sensor specification = getSpecification(sensorId, propertyId);

			if (specification == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("No specification found [sensorId = "
							+ sensorId + "; propertyId = " + propertyId + "]");
				return null;
			}

			Frequency frequency = getFrequency(specification);

			if (frequency == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("No frequency specified [specification = "
							+ specification + "]");
				return null;
			}

			queriesMap.put(rewrittenQuery, frequency);
		}

		return ks.createQueryHandler(ds.createQueryHandler(queriesMap),
				original);
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
		datasetsById.clear();
		
		// TODO
	}

	private Sensor getSpecification(URI sensorId, URI propertyId) {
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

	private Frequency getFrequency(Sensor specification) {
		Frequency ret = frequencyCache.get(specification);

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

				frequencyCache.put(specification, ret);

				return ret;
			}
		}

		return null;
	}

	private class EmroozTemporalEntityVisitor implements TemporalEntityVisitor {

		@Override
		public void visit(Instant entity) {
			instant = entity.getValue();
		}

	}

}
