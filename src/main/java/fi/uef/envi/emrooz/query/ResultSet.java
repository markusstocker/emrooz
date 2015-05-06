/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 * <p>
 * Title: EmroozResultSet
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

public class ResultSet {

	private SensorObservationQuery query;
	private Set<Statement> statements;
	private boolean isEmpty = false;
	private Repository repo;
	private RepositoryConnection conn;
	private TupleQueryResult result;
	private BindingSet next;

	private static final Logger log = Logger.getLogger(ResultSet.class
			.getName());

	public ResultSet(SensorObservationQuery query, Set<Statement> statements) {
		this.query = query;
		this.statements = statements;

		try {
			initialize();
		} catch (RepositoryException | MalformedQueryException
				| QueryEvaluationException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}
	}

	public boolean hasNext() {
		if (isEmpty)
			return false;
		if (result == null)
			return false;

		boolean hasNext = false;

		try {
			hasNext = result.hasNext();

			if (hasNext)
				next = result.next();
			else
				next = null;
		} catch (QueryEvaluationException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}

		return hasNext;
	}

	public BindingSet next() {
		return next;
	}

	public void close() {
		try {
			try {
				if (result != null)
					result.close();
			} catch (QueryEvaluationException e) {
				if (log.isLoggable(Level.SEVERE))
					log.severe(e.getMessage());
			} finally {
				if (conn != null)
					conn.close();
				if (repo != null)
					repo.shutDown();
			}
		} catch (RepositoryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}
	}

	private void initialize() throws RepositoryException,
			MalformedQueryException, QueryEvaluationException {
		if (query == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to initialize result set [query = null]");
			return;
		}

		if (statements == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Cannot initialize result set [statements = null]");
			return;
		}

		if (statements.isEmpty()) {
			isEmpty = true;
			return;
		}

		repo = new SailRepository(new MemoryStore());
		repo.initialize();
		conn = repo.getConnection();

		for (Statement statement : statements) {
			conn.add(statement);
		}

		result = conn.prepareTupleQuery(QueryLanguage.SPARQL,
				query.getQueryString()).evaluate();
	}

}
