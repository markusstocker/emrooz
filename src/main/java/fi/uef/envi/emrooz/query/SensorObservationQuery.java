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

public class SensorObservationQuery {

	private String queryString;
	private URI sensorId;
	private URI propertyId;
	private URI featureId;
	private DateTime timeFrom;
	private DateTime timeTo;

	private static SPARQLParser parser = new SPARQLParser();
	private static StatementPatternCollector collector = new StatementPatternCollector();
	private static SparqlQueryModelVisitor visitor = new SparqlQueryModelVisitor();

	private SensorObservationQuery() {

	}

	public String getQueryString() {
		return queryString;
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

	private void setQueryString(String query) {
		this.queryString = query;
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

	public static SensorObservationQuery parse(String queryString) {
		SensorObservationQuery ret = new SensorObservationQuery();

		if (queryString == null) {
			throw new RuntimeException("Cannot parse query, query is null");
		}

		ret.setQueryString(queryString);

		ParsedQuery query;

		try {
			query = parser.parseQuery(queryString, null);
		} catch (MalformedQueryException e) {
			throw new RuntimeException(e);
		}

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
				if (o != null) {
					if (o instanceof URI)
						sensorId = (URI) o;
				}
			} else if (p.equals(SSN.observedProperty)) {
				Value o = object.getValue();
				if (o != null) {
					if (o instanceof URI)
						propertyId = (URI) o;
				}
			} else if (p.equals(SSN.featureOfInterest)) {
				Value o = object.getValue();
				if (o != null) {
					if (o instanceof URI)
						featureId = (URI) o;
				}
			} else if (p.equals(Time.inXSDDateTime)) {
				inXSDDateTimeVar = object;
			}
		}

		if (sensorId == null)
			throw new RuntimeException(
					"Cannot parse query, failed to determine sensor [sensorId = null; queryString = "
							+ queryString + "]");
		if (propertyId == null)
			throw new RuntimeException(
					"Cannot parse query, failed to determine property [propertyId = null; queryString = "
							+ queryString + "]");
		if (featureId == null)
			throw new RuntimeException(
					"Cannot parse query, failed to determine feature [featureId = null; queryString = "
							+ queryString + "]");
		if (inXSDDateTimeVar == null)
			throw new RuntimeException(
					"Cannot parse query, failed to determine XSD date time variable [inXSDDateTimeVar = null; queryString = "
							+ queryString + "]");

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
					"Cannot parse query, failed to determine time interval [timeFrom = null; queryString = "
							+ queryString + "]");
		if (timeTo == null)
			throw new RuntimeException(
					"Cannot parse query, failed to determine time interval [timeTo = null; queryString = "
							+ queryString + "]");

		ret.setSensorId(sensorId);
		ret.setPropertyId(propertyId);
		ret.setFeatureOfInterestId(featureId);
		ret.setTimeFrom(timeFrom);
		ret.setTimeTo(timeTo);

		return ret;
	}
}
