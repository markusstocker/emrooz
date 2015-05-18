/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.sesame;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.api.QueryHandler;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.query.SensorObservationQuery;

/**
 * <p>
 * Title: SesameQueryHandler
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

public class SesameQueryHandler implements QueryHandler<BindingSet> {

	private Repository repo;
	private RepositoryConnection conn;
	private QueryHandler<Statement> other;
	private SensorObservationQuery query;

	private static final Logger log = Logger.getLogger(SesameQueryHandler.class
			.getName());

	public SesameQueryHandler(QueryHandler<Statement> other,
			SensorObservationQuery query) {
		if (other == null)
			throw new RuntimeException("[other = null]");

		this.other = other;
		this.query = query;

		try {
			this.repo = new SailRepository(new MemoryStore());
			this.repo.initialize();
			this.conn = repo.getConnection();
		} catch (RepositoryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}
	}

	@Override
	public ResultSet<BindingSet> evaluate() {
		try {
			ResultSet<Statement> rs = other.evaluate();

			while (rs.hasNext()) {
				conn.add(rs.next());
			}

			TupleQuery tupleQuery = conn.prepareTupleQuery(
					QueryLanguage.SPARQL, query.getQueryString());

			return new SesameBindingResultSet(tupleQuery.evaluate());
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			conn.close();
			repo.shutDown();
		} catch (RepositoryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}
	}

	private class SesameBindingResultSet implements ResultSet<BindingSet> {

		private TupleQueryResult result;

		public SesameBindingResultSet(TupleQueryResult result) {
			this.result = result;
		}

		@Override
		public boolean hasNext() {
			try {
				return result.hasNext();
			} catch (QueryEvaluationException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public BindingSet next() {
			try {
				return result.next();
			} catch (QueryEvaluationException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void close() {
			try {
				result.close();
			} catch (QueryEvaluationException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
