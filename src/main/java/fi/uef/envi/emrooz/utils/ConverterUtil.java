/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	public static void toStatements(byte[] byteArray, Set<Statement> statements) {
		RDFParser rdfParser = Rio.createParser(RDFFormat.BINARY);
		StatementCollector collector = new StatementCollector(statements);
		rdfParser.setRDFHandler(collector);

		try {
			rdfParser.parse(new ByteArrayInputStream(byteArray), null);
		} catch (RDFParseException | RDFHandlerException | IOException e) {
			e.printStackTrace();
		}
	}

}
