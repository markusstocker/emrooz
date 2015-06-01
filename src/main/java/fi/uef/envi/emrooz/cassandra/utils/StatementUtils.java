/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra.utils;

import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.binary.BinaryRDFWriter;
import org.openrdf.rio.helpers.StatementCollector;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.utils.Bytes;

/**
 * <p>
 * Title: StatementUtils
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

public class StatementUtils {

	public static byte[] toByteArray(Set<Statement> statements) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		RDFHandler rdfHandler = new BinaryRDFWriter(os);

		try {
			rdfHandler.startRDF();

			for (Statement statement : statements) {
				rdfHandler.handleStatement(statement);
			}

			rdfHandler.endRDF();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}

		return os.toByteArray();
	}

	public static Set<Statement> toStatements(byte[] bytes)
			throws RDFParseException, RDFHandlerException, IOException {
		if (bytes.length == 0)
			return Collections.emptySet();

		Set<Statement> ret = new HashSet<Statement>();
		RDFParser rdfParser = Rio.createParser(RDFFormat.BINARY);
		StatementCollector collector = new StatementCollector(ret);
		rdfParser.setRDFHandler(collector);
		rdfParser.parse(new ByteArrayInputStream(bytes), null);

		return Collections.unmodifiableSet(ret);
	}

	public static Iterator<Statement> toStatements(Iterator<Row> iterator)
			throws RDFParseException, RDFHandlerException, IOException {
		if (!iterator.hasNext()) {
			return Collections.emptyIterator();
		}

		Set<Statement> ret = new HashSet<Statement>();
		RDFParser rdfParser = Rio.createParser(RDFFormat.BINARY);
		StatementCollector collector = new StatementCollector(ret);
		rdfParser.setRDFHandler(collector);

		while (iterator.hasNext()) {
			rdfParser.parse(
					new ByteArrayInputStream(Bytes.getArray(iterator.next()
							.getBytes(DATA_TABLE_ATTRIBUTE_3))), null);
		}

		return Collections.unmodifiableSet(ret).iterator();
	}

}
