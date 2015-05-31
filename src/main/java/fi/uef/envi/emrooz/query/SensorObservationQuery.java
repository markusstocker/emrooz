/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query;

import java.util.List;

import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

import fi.uef.envi.emrooz.api.Query;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: SensorObservationQuery
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

public class SensorObservationQuery implements Query {

	private URI sensorId;
	private URI propertyId;
	private URI featureId;
	private DateTime timeFrom;
	private DateTime timeTo;

	private static SPARQLParser sparqlParser = new SPARQLParser();
	private static StatementPatternCollector collector = new StatementPatternCollector();
	private static SparqlQueryModelVisitor visitor = new SparqlQueryModelVisitor();

	private SensorObservationQuery() {

	}

	public URI getSensorId() {
		return sensorId;
	}

	public URI getPropertyId() {
		return propertyId;
	}

	public URI getFeatureOfInterestId() {
		return featureId;
	}

	public DateTime getTimeFrom() {
		return timeFrom;
	}

	public DateTime getTimeTo() {
		return timeTo;
	}

	public boolean isFullySpecified() {
		if (sensorId == null)
			return false;
		if (propertyId == null)
			return false;
		if (featureId == null)
			return false;

		return true;
	}

	private void setSensorId(URI id) {
		this.sensorId = id;
	}

	private void setPropertyId(URI id) {
		this.propertyId = id;
	}

	private void setFeatureOfInterestId(URI id) {
		this.featureId = id;
	}

	private void setTimeFrom(DateTime time) {
		this.timeFrom = time;
	}

	private void setTimeTo(DateTime time) {
		this.timeTo = time;
	}

	public static SensorObservationQuery create(URI sensorId, URI propertyId,
			URI featureId, DateTime timeFrom, DateTime timeTo) {
		SensorObservationQuery ret = new SensorObservationQuery();

		ret.setSensorId(sensorId);
		ret.setPropertyId(propertyId);
		ret.setFeatureOfInterestId(featureId);
		ret.setTimeFrom(timeFrom);
		ret.setTimeTo(timeTo);

		return ret;
	}

	public static SensorObservationQuery create(String query) {
		try {
			return create(sparqlParser.parseQuery(query, null));
		} catch (MalformedQueryException e) {
			throw new RuntimeException(e);
		}
	}

	public static SensorObservationQuery create(ParsedQuery query) {
		if (query == null)
			throw new RuntimeException("[query = null]");

		SensorObservationQuery ret = new SensorObservationQuery();

		TupleExpr expr = query.getTupleExpr();

		expr.visit(collector);

		URI sensorId = null;
		URI propertyId = null;
		URI featureId = null;
		Var inXSDDateTimeVar = null;

		List<StatementPattern> patterns = collector.getStatementPatterns();

		for (StatementPattern pattern : patterns) {
			Value predicate = pattern.getPredicateVar().getValue();

			if (predicate == null)
				continue;

			if (!(predicate instanceof URI))
				continue;

			URI p = (URI) predicate;

			Var object = pattern.getObjectVar();

			if (p.equals(SSN.observedBy)) {
				Value o = object.getValue();
				if (o == null) {
					sensorId = null;
				} else {
					if (o instanceof URI)
						sensorId = (URI) o;
				}
			} else if (p.equals(SSN.observedProperty)) {
				Value o = object.getValue();
				if (o == null) {
					propertyId = null;
				} else {
					if (o instanceof URI)
						propertyId = (URI) o;
				}
			} else if (p.equals(SSN.featureOfInterest)) {
				Value o = object.getValue();
				if (o == null) {
					featureId = null;
				} else {
					if (o instanceof URI)
						featureId = (URI) o;
				}
			} else if (p.equals(Time.inXSDDateTime)) {
				inXSDDateTimeVar = object;
			}
		}

		if (inXSDDateTimeVar == null)
			throw new RuntimeException(
					"Cannot create query, failed to determine XSD date time variable [inXSDDateTimeVar = null; queryString = "
							+ query + "]");

		visitor.setInXSDDateTimeVar(inXSDDateTimeVar);

		try {
			expr.visit(visitor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		DateTime timeFrom = visitor.getTimeFrom();
		DateTime timeTo = visitor.getTimeTo();

		if (timeFrom == null)
			throw new RuntimeException(
					"Cannot create query, failed to determine time interval [timeFrom = null; queryString = "
							+ query + "]");
		if (timeTo == null)
			throw new RuntimeException(
					"Cannot create query, failed to determine time interval [timeTo = null; queryString = "
							+ query + "]");

		ret.setSensorId(sensorId);
		ret.setPropertyId(propertyId);
		ret.setFeatureOfInterestId(featureId);
		ret.setTimeFrom(timeFrom);
		ret.setTimeTo(timeTo);

		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result
				+ ((sensorId == null) ? 0 : sensorId.hashCode());
		result = prime * result
				+ ((propertyId == null) ? 0 : propertyId.hashCode());
		result = prime * result
				+ ((featureId == null) ? 0 : featureId.hashCode());
		result = prime * result
				+ ((timeFrom == null) ? 0 : timeFrom.hashCode());
		result = prime * result + ((timeTo == null) ? 0 : timeTo.hashCode());

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

		SensorObservationQuery other = (SensorObservationQuery) obj;

		if (sensorId == null) {
			if (other.sensorId != null)
				return false;
		} else if (!sensorId.equals(other.sensorId))
			return false;

		if (propertyId == null) {
			if (other.propertyId != null)
				return false;
		} else if (!propertyId.equals(other.propertyId))
			return false;

		if (featureId == null) {
			if (other.featureId != null)
				return false;
		} else if (!featureId.equals(other.featureId))
			return false;

		if (timeFrom == null) {
			if (other.timeFrom != null)
				return false;
		} else if (!timeFrom.equals(other.timeFrom))
			return false;

		if (timeTo == null) {
			if (other.timeTo != null)
				return false;
		} else if (!timeTo.equals(other.timeTo))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "SensorObservationQuery [sensorId = " + sensorId
				+ "; propertyId = " + propertyId + "; featureId = " + featureId
				+ "; timeFrom = " + timeFrom + "; timeTo = " + timeTo + "]";
	}
}
