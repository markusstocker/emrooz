/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.Bytes;

import fi.uef.envi.emrooz.Rollover;
import fi.uef.envi.emrooz.api.QueryHandler;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.query.EmptyResultSet;
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
	private Sensor specification;
	private SensorObservationQuery query;

	private static final Logger log = Logger
			.getLogger(CassandraQueryHandler.class.getName());

	public CassandraQueryHandler(Session session,
			PreparedStatement sensorObservationSelectStatement,
			Sensor specification, SensorObservationQuery query) {
		if (session == null)
			throw new NullPointerException("[session = null]");
		if (sensorObservationSelectStatement == null)
			throw new NullPointerException(
					"[sensorObservationSelectStatement = null]");
		if (specification == null)
			throw new NullPointerException("[specification = null]");
		if (query == null)
			throw new NullPointerException("[query = null]");

		this.session = session;
		this.sensorObservationSelectStatement = sensorObservationSelectStatement;
		this.specification = specification;
		this.query = query;
	}

	@Override
	public ResultSet<Statement> evaluate() {
		return getSensorObservations(query.getSensorId(),
				query.getPropertyId(), query.getFeatureOfInterestId(),
				query.getTimeFrom(), query.getTimeTo());
	}

	@Override
	public void close() {
		// Nothing to close
	}

	private ResultSet<Statement> getSensorObservations(URI sensor,
			URI property, URI feature, DateTime timeFrom, DateTime timeTo) {
		if (sensor == null || property == null || feature == null
				|| timeFrom == null || timeTo == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("At least one parameter is null; returned empty set [sensor = "
						+ sensor
						+ "; property = "
						+ property
						+ "; feature = "
						+ feature
						+ "; timeFrom = "
						+ timeFrom
						+ "; timeTo = "
						+ timeTo + "]");
			return new EmptyResultSet<Statement>();
		}

		Rollover rollover = getRollover(specification);

		if (rollover == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Registration rollover is null [specification = "
						+ specification + "]");
			return new EmptyResultSet<Statement>();
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

		return new CassandraStatementResultSet(results.iterator());
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

	private static class CassandraStatementResultSet implements
			ResultSet<Statement> {

		private Iterator<Iterator<Row>> results;
		private Iterator<Statement> statements;

		private CassandraStatementResultSet(Iterator<Iterator<Row>> results) {
			this.results = results;
			nextStatementIterator();
		}

		@Override
		public boolean hasNext() {
			return statements.hasNext();
		}

		@Override
		public Statement next() {
			return statements.next();
		}

		@Override
		public void close() {
			// Nothing to close
		}

		private void nextStatementIterator() {
			if (!results.hasNext()) {
				statements = Collections.emptyIterator();
				return; // There are no results
			}

			statements = toStatements(results.next());

			if (!statements.hasNext())
				// The statements may be an empty iterator, continue till there
				// is a non-empty statement iterator or there are no more
				// results
				nextStatementIterator();
		}

		private Iterator<Statement> toStatements(Iterator<Row> iterator) {
			if (!iterator.hasNext()) {
				return Collections.emptyIterator();
			}

			Set<Statement> ret = new HashSet<Statement>();
			RDFParser rdfParser = Rio.createParser(RDFFormat.BINARY);
			StatementCollector collector = new StatementCollector(ret);
			rdfParser.setRDFHandler(collector);

			try {
				while (iterator.hasNext()) {
					rdfParser.parse(
							new ByteArrayInputStream(Bytes.getArray(iterator
									.next().getBytes(DATA_TABLE_ATTRIBUTE_3))),
							null);
				}
			} catch (RDFParseException | RDFHandlerException | IOException e) {
				e.printStackTrace();
			}

			return Collections.unmodifiableSet(ret).iterator();
		}

	}

}
