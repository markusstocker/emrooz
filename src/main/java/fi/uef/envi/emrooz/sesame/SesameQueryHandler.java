/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.sesame;

import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
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
			throw new RuntimeException(e);
		}
	}

	@Override
	public ResultSet<BindingSet> evaluate() {
		try {
			return new SesameResultSet(getTupleQuery().evaluate());
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void evaluate(TupleQueryResultHandler handler) {
		try {
			getTupleQuery().evaluate(handler);
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException | TupleQueryResultHandlerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			conn.close();
			repo.shutDown();
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		}
	}

	private TupleQuery getTupleQuery() throws RepositoryException,
			MalformedQueryException {
		ResultSet<Statement> rs = other.evaluate();

		while (rs.hasNext()) {
			conn.add(rs.next());
		}

		return conn.prepareTupleQuery(QueryLanguage.SPARQL,
				query.getQueryString());
	}

}
