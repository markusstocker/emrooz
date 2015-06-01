/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import com.datastax.driver.core.Row;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.cassandra.utils.StatementUtils;

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
		try {
			getStatementIterator();
		} catch (RDFParseException | RDFHandlerException | IOException e) {
			throw new RuntimeException(e);
		}

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

	private void getStatementIterator() throws RDFParseException,
			RDFHandlerException, IOException {
		if (!results.hasNext())
			return; // Do not set statements to empty iterator; there may still
					// be statements even though there are no more results

		if (!statements.hasNext()) {
			statements = StatementUtils.toStatements(results.next());
			if (!statements.hasNext())
				getStatementIterator();
		}
	}

}
