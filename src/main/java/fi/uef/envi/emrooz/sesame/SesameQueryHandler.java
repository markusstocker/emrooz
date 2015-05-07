/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.sesame;

import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.sail.memory.MemoryStore;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.utils.Bytes;

import fi.uef.envi.emrooz.cassandra.CassandraQueryHandler;
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

public class SesameQueryHandler {

	private Repository repo;
	private RepositoryConnection conn;
	private TupleQueryResult result;
	private CassandraQueryHandler cassandraQueryHandler;
	private SensorObservationQuery query;

	private static final Logger log = Logger.getLogger(SesameQueryHandler.class
			.getName());

	public SesameQueryHandler(CassandraQueryHandler cassandraQueryHandler,
			SensorObservationQuery query) {
		if (cassandraQueryHandler == null)
			throw new RuntimeException("[cassandraQueryHandler = null]");

		this.cassandraQueryHandler = cassandraQueryHandler;
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

	public TupleQueryResult evaluate() {
		try {
			Set<Iterator<Row>> iterators = cassandraQueryHandler.evaluate();

			for (Iterator<Row> iterator : iterators) {
				conn.add(getStatements(iterator));
			}

			TupleQuery tupleQuery = conn.prepareTupleQuery(
					QueryLanguage.SPARQL, query.getQueryString());

			result = tupleQuery.evaluate();
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}

		// tupleQuery.evaluate(handler);

		return result;
	}

	public void close() {
		try {
			conn.close();
			repo.shutDown();
		} catch (RepositoryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}
	}

	private Set<Statement> getStatements(Iterator<Row> iterator) {
		Set<Statement> ret = new HashSet<Statement>();
		RDFParser rdfParser = Rio.createParser(RDFFormat.BINARY);
		StatementCollector collector = new StatementCollector(ret);
		rdfParser.setRDFHandler(collector);

		try {
			while (iterator.hasNext()) {
				rdfParser.parse(
						new ByteArrayInputStream(Bytes.getArray(iterator.next()
								.getBytes(DATA_TABLE_ATTRIBUTE_3))), null);
			}
		} catch (RDFParseException | RDFHandlerException | IOException e) {
			e.printStackTrace();
		}

		return Collections.unmodifiableSet(ret);
	}

}
