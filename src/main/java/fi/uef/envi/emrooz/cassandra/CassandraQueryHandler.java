/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.TupleQueryResultHandler;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import fi.uef.envi.emrooz.Rollover;
import fi.uef.envi.emrooz.api.QueryHandler;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.query.SensorObservationQuery;

/**
 * <p>
 * Title: CassandraQueryHandler
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

public class CassandraQueryHandler extends CassandraRequestHandler implements
		QueryHandler<Statement> {

	private Session session;
	private PreparedStatement sensorObservationSelectStatement;
	private Map<SensorObservationQuery, Sensor> queries;

	private static final Logger log = Logger
			.getLogger(CassandraQueryHandler.class.getName());

	public CassandraQueryHandler(Session session,
			PreparedStatement sensorObservationSelectStatement,
			Map<SensorObservationQuery, Sensor> queries) {
		if (session == null)
			throw new NullPointerException("[session = null]");
		if (sensorObservationSelectStatement == null)
			throw new NullPointerException(
					"[sensorObservationSelectStatement = null]");
		if (queries == null)
			throw new NullPointerException("[queries = null]");

		this.session = session;
		this.sensorObservationSelectStatement = sensorObservationSelectStatement;
		this.queries = queries;
	}

	@Override
	public void evaluate(TupleQueryResultHandler handler) {
		throw new UnsupportedOperationException(
				"Query evaluation with result handler not supported");
	}

	@Override
	public void close() {
		// Nothing to close
	}

	@Override
	public ResultSet<Statement> evaluate() {
		Set<Iterator<Row>> results = new HashSet<Iterator<Row>>();
	
		for (Map.Entry<SensorObservationQuery, Sensor> entry : queries.entrySet()) {
			results.addAll(getSensorObservations(entry.getKey(), entry.getValue()));
		}

		return new CassandraResultSet(results.iterator());
	}
	
	private Set<Iterator<Row>> getSensorObservations(SensorObservationQuery query, Sensor specification) {
		URI sensorId = query.getSensorId();
		URI propertyId = query.getPropertyId();
		URI featureId = query.getFeatureOfInterestId();
		DateTime timeFrom = query.getTimeFrom();
		DateTime timeTo = query.getTimeTo();
		
		if (sensorId == null || propertyId == null || featureId == null
				|| timeFrom == null || timeTo == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("At least one parameter is null; returned empty set [sensorId = "
						+ sensorId
						+ "; propertyId = "
						+ propertyId
						+ "; featureId = "
						+ featureId
						+ "; timeFrom = "
						+ timeFrom
						+ "; timeTo = "
						+ timeTo + "]");
			return Collections.emptySet();
		}

		Rollover rollover = getRollover(specification);

		if (rollover == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Registration rollover is null [specification = "
						+ specification + "]");
			return Collections.emptySet();
		}

		DateTime time = timeFrom;
		Set<Iterator<Row>> results = new HashSet<Iterator<Row>>();

		while (time.isBefore(timeTo)) {
			Iterator<Row> it = getSensorObservations(
					getRowKey(specification, time), time, timeTo);

			if (it != null)
				results.add(it);

			if (rollover.equals(Rollover.YEAR))
				time = time.year().roundFloorCopy().plusYears(1);
			else if (rollover.equals(Rollover.MONTH))
				time = time.monthOfYear().roundFloorCopy().plusMonths(1);
			else if (rollover.equals(Rollover.DAY))
				time = time.dayOfMonth().roundFloorCopy().plusDays(1);
			else if (rollover.equals(Rollover.HOUR))
				time = time.hourOfDay().roundFloorCopy().plusHours(1);
			else if (rollover.equals(Rollover.MINUTE))
				time = time.minuteOfHour().roundFloorCopy().plusMinutes(1);
			else
				throw new RuntimeException("Unsupported rollover [rollover = "
						+ rollover + "]");
		}
	
		return Collections.unmodifiableSet(results);
	}

	private Iterator<Row> getSensorObservations(String rowKey,
			DateTime timeFrom, DateTime timeTo) {
		if (timeFrom == null || timeTo == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Parameters cannot be null; returned empty result set [timeFrom = "
						+ timeFrom + "; timeTo = " + timeTo + "]");

			return null;
		}

		return getSensorObservations(rowKey, timeFrom.toDate(), timeTo.toDate());
	}

	private Iterator<Row> getSensorObservations(String rowKey,
			Date columnNameFrom, Date columnNameTo) {
		if (rowKey == null || columnNameFrom == null || columnNameTo == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Returned empty result set [rowKey = " + rowKey
						+ "; columnNameFrom = " + columnNameFrom
						+ "; columnNameTo = " + columnNameTo + "]");

			return null;
		}

		return session.execute(
				new BoundStatement(sensorObservationSelectStatement).bind(
						rowKey, columnNameFrom, columnNameTo)).iterator();
	}

}
