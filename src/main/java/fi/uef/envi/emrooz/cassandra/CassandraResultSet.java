/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.utils.Bytes;

import fi.uef.envi.emrooz.api.ResultSet;

/**
 * <p>
 * Title: CassandraResultSet
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

public class CassandraResultSet implements ResultSet<Statement> {

	private Iterator<Iterator<Row>> results;
	private Iterator<Statement> statements;

	public CassandraResultSet(Iterator<Iterator<Row>> results) {
		this.results = results;
		this.statements = Collections.emptyIterator();
	}

	@Override
	public boolean hasNext() {
		getStatementIterator();

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

	private void getStatementIterator() {
		if (!results.hasNext())
			return;

		if (!statements.hasNext()) {
			statements = toStatements(results.next());
			if (!statements.hasNext())
				getStatementIterator();
		}
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
						new ByteArrayInputStream(Bytes.getArray(iterator.next()
								.getBytes(DATA_TABLE_ATTRIBUTE_3))), null);
			}
		} catch (RDFParseException | RDFHandlerException | IOException e) {
			e.printStackTrace();
		}

		return Collections.unmodifiableSet(ret).iterator();
	}

}
