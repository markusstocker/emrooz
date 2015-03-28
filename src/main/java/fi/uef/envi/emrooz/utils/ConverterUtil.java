/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
 * Title: ConverterUtil
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

public class ConverterUtil {

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

	public static Set<Statement> toStatements(List<Row> rows) {
		Set<Statement> ret = new HashSet<Statement>();
		RDFParser rdfParser = Rio.createParser(RDFFormat.BINARY);
		StatementCollector collector = new StatementCollector(ret);
		rdfParser.setRDFHandler(collector);

		try {
			for (Row row : rows) {
				rdfParser.parse(
						new ByteArrayInputStream(Bytes.getArray(row
								.getBytes("value"))), null);
			}
		} catch (RDFParseException | RDFHandlerException | IOException e) {
			e.printStackTrace();
		}
		
		return Collections.unmodifiableSet(ret);
	}

}
