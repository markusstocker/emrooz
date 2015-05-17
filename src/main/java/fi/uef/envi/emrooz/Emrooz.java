/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.TemporalEntityVisitor;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.entity.time.TemporalEntity;
import fi.uef.envi.emrooz.query.ResultSet;
import fi.uef.envi.emrooz.query.SensorObservationQuery;
import fi.uef.envi.emrooz.rdf.RDFEntityRepresenter;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;

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

	private SesameKnowledgeStore ks;
	private CassandraDataStore ds;

	private Map<URI, Map<URI, Map<URI, Sensor>>> sensors;

	private DateTime instant = null;
	private final TemporalEntityVisitor temporalEntityVisitor;
	private final RDFEntityRepresenter representer;

	private static final Logger log = Logger.getLogger(Emrooz.class.getName());

	public Emrooz(SesameKnowledgeStore ks, CassandraDataStore ds) {
		if (ks == null)
			throw new NullPointerException(
					"Knowledge store cannot be null [ks = null]");
		if (ds == null)
			throw new NullPointerException(
					"Data store cannot be null [ds = null]");

		this.ks = ks;
		this.ds = ds;

		this.sensors = new HashMap<URI, Map<URI, Map<URI, Sensor>>>();
		this.temporalEntityVisitor = new EmroozTemporalEntityVisitor();
		this.representer = new RDFEntityRepresenter();

		sensors();
	}

	public void addSensorObservation(Set<Statement> statements) {
		add(representer.createSensorObservation(statements));
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

		Sensor specification = getSpecification(sensor.getId(),
				property.getId(), feature.getId());

		if (specification == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No specification found [sensor = " + sensor
						+ "; property = " + property + "; feature = " + feature
						+ "]");
			return;
		}

		ds.addSensorObservation(specification, resultTime, statements);
	}

	public ResultSet evaluate(SensorObservationQuery query) {
		URI sensorId = query.getSensorId();

		if (sensorId == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No specified sensor in query [query = " + query
						+ "]");

			return ResultSet.empty();
		}

		URI propertyId = query.getPropertyId();

		if (propertyId == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No specified property in query [query = " + query
						+ "]");

			return ResultSet.empty();
		}

		URI featureId = query.getFeatureOfInterestId();

		if (featureId == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No specified feature in query [query = " + query
						+ "]");

			return ResultSet.empty();
		}

		Sensor specification = getSpecification(sensorId, propertyId, featureId);

		if (specification == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("No specification found [sensorId = " + sensorId
						+ "; propertyId = " + propertyId + "; featureId = "
						+ featureId + "]");
			return ResultSet.empty();
		}

		return new ResultSet(ks.createQueryHandler(
				ds.createQueryHandler(specification, query), query));
	}

	// public void evaluate(SensorObservationQuery query,
	// TupleQueryResultHandler handler) {
	// new fi.uef.envi.emrooz.query.ResultSet(query, getSensorObservations(
	// query.getSensorId(), query.getPropertyId(),
	// query.getFeatureOfInterestId(), query.getTimeFrom(),
	// query.getTimeTo()), handler);
	// }

	public void close() {
		ks.close();
		ds.close();
	}

	private void sensors() {
		sensors.clear();

		Set<Sensor> sensors = ks.getSensors();

		for (Sensor sensor : sensors) {
			URI sensorId = sensor.getId();

			Map<URI, Map<URI, Sensor>> m1 = this.sensors.get(sensorId);

			if (m1 == null) {
				m1 = new HashMap<URI, Map<URI, Sensor>>();
				this.sensors.put(sensorId, m1);
			}

			Property property = sensor.getObservedProperty();

			if (property == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Sensor must specify the observed property [sensor = "
							+ sensor + "]");
				continue;
			}

			URI propertyId = property.getId();

			Map<URI, Sensor> m2 = m1.get(propertyId);

			if (m2 == null) {
				m2 = new HashMap<URI, Sensor>();
				m1.put(propertyId, m2);
			}

			FeatureOfInterest feature = property.getPropertyOf();

			if (feature == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Property must specify the feature [sensor = "
							+ sensor + "]");
				continue;
			}

			m2.put(feature.getId(), sensor);
		}
	}

	private Sensor getSpecification(URI sensorId, URI propertyId, URI featureId) {
		Map<URI, Map<URI, Sensor>> m1 = sensors.get(sensorId);

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

		Map<URI, Sensor> m2 = m1.get(propertyId);

		if (m2 == null) {
			sensors();
			m1 = sensors.get(sensorId);
			m2 = m1.get(propertyId);
			if (m2 == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Failed to resolve sensor specification for property [sensorId = "
							+ sensorId
							+ "; propertyId = "
							+ propertyId
							+ "; sensors = " + sensors + "]");
				return null;
			}
		}

		Sensor specification = m2.get(featureId);

		if (specification == null) {
			sensors();
			m1 = sensors.get(sensorId);
			m2 = m1.get(propertyId);
			specification = m2.get(featureId);
			if (specification == null) {
				if (log.isLoggable(Level.WARNING))
					log.warning("Failed to resolve sensor specification for feature [sensorId = "
							+ sensorId
							+ "; propertyId = "
							+ propertyId
							+ "; featureId = "
							+ featureId
							+ "; sensors = "
							+ sensors + "]");
				return null;
			}
		}

		return specification;
	}

	private class EmroozTemporalEntityVisitor implements TemporalEntityVisitor {

		@Override
		public void visit(Instant entity) {
			instant = entity.getValue();
		}

	}

	// private URI resolve(StatementPattern pattern,
	// List<StatementPattern> patterns, Set<Statement> graph) {
	// // Try to resolve resource subject by first getting the joined triple
	// // patterns matching resource subject and then execute a SPARQL query
	// // with the triple patterns over graph to resolve resource
	//
	// Set<StatementPattern> basicGraphPattern = new
	// HashSet<StatementPattern>();
	//
	// Var findVar = pattern.getObjectVar();
	//
	// find(findVar, patterns, basicGraphPattern);
	//
	// GraphPattern gp = new GraphPattern();
	//
	// for (StatementPattern bgp : basicGraphPattern) {
	// gp.addRequiredSP(bgp.getSubjectVar(), bgp.getPredicateVar(),
	// bgp.getObjectVar());
	// }
	//
	// TupleExpr query = new Projection(gp.buildTupleExpr(),
	// new ProjectionElemList(new ProjectionElem(findVar.getName())));
	//
	// try {
	// Repository repo = new SailRepository(new MemoryStore());
	// repo.initialize();
	//
	// SailRepositoryConnection conn = (SailRepositoryConnection) repo
	// .getConnection();
	//
	// for (Statement statement : graph) {
	// conn.add(statement);
	// }
	//
	// ParsedTupleQuery tp = new ParsedTupleQuery(query);
	// SailTupleQuery q = new SailTupleQuery(tp, conn);
	//
	// TupleQueryResult r = q.evaluate();
	//
	// while (r.hasNext()) {
	//
	// }
	//
	// conn.close();
	// } catch (RepositoryException | MalformedQueryException
	// | QueryEvaluationException e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }

	// private void find(Var s, List<StatementPattern> patterns,
	// Set<StatementPattern> ret) {
	// for (StatementPattern pattern : patterns) {
	// if (!s.equals(pattern.getSubjectVar()))
	// continue;
	//
	// ret.add(pattern);
	//
	// find(pattern.getObjectVar(), patterns, ret);
	// }
	// }

}
