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

import fi.uef.envi.emrooz.vocabulary.QB;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: DatasetObservationQuery
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

public class DatasetObservationQuery extends ObservationQuery {

	private URI datasetId;
	private DateTime timeFrom;
	private DateTime timeTo;

	private static SPARQLParser sparqlParser = new SPARQLParser();
	private static StatementPatternCollector collector = new StatementPatternCollector();
	private static SparqlQueryModelVisitor visitor = new SparqlQueryModelVisitor();

	private DatasetObservationQuery() {

	}

	public URI getDatasetId() {
		return datasetId;
	}

	public DateTime getTimeFrom() {
		return timeFrom;
	}

	public DateTime getTimeTo() {
		return timeTo;
	}

	public boolean isFullySpecified() {
		if (datasetId == null)
			return false;

		return true;
	}

	private void setDatasetId(URI id) {
		this.datasetId = id;
	}

	private void setTimeFrom(DateTime time) {
		this.timeFrom = time;
	}

	private void setTimeTo(DateTime time) {
		this.timeTo = time;
	}

	public static DatasetObservationQuery create(URI datasetId,
			DateTime timeFrom, DateTime timeTo) {
		DatasetObservationQuery ret = new DatasetObservationQuery();

		ret.setDatasetId(datasetId);
		ret.setTimeFrom(timeFrom);
		ret.setTimeTo(timeTo);

		return ret;
	}

	public static DatasetObservationQuery create(String query) {
		try {
			return create(sparqlParser.parseQuery(query, null));
		} catch (MalformedQueryException e) {
			throw new RuntimeException(e);
		}
	}

	public static DatasetObservationQuery create(ParsedQuery query) {
		if (query == null)
			throw new RuntimeException("[query = null]");

		DatasetObservationQuery ret = new DatasetObservationQuery();

		TupleExpr expr = query.getTupleExpr();

		expr.visit(collector);

		URI datasetId = null;
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

			if (p.equals(QB.dataSet)) {
				Value o = object.getValue();
				if (o == null) {
					datasetId = null;
				} else {
					if (o instanceof URI)
						datasetId = (URI) o;
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

		ret.setDatasetId(datasetId);
		ret.setTimeFrom(timeFrom);
		ret.setTimeTo(timeTo);

		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result
				+ ((datasetId == null) ? 0 : datasetId.hashCode());
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

		DatasetObservationQuery other = (DatasetObservationQuery) obj;

		if (datasetId == null) {
			if (other.datasetId != null)
				return false;
		} else if (!datasetId.equals(other.datasetId))
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
		return "DatasetObservationQuery [datasetId = " + datasetId
				+ "; timeFrom = " + timeFrom + "; timeTo = " + timeTo + "]";
	}

}
