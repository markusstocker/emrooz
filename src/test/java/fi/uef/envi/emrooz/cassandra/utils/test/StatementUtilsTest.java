/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra.utils.test;

import java.io.IOException;
import java.util.Set;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fi.uef.envi.emrooz.cassandra.utils.StatementUtils;
import fi.uef.envi.emrooz.test.ParamsConverterTest;

/**
 * <p>
 * Title: StatementUtilsTest
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

@RunWith(JUnitParamsRunner.class)
public class StatementUtilsTest {

	@Test
	@FileParameters("src/test/resources/StatementUtilsTest-testToByteArray.csv")
	public void testToByteArray(
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> e)
			throws RDFParseException, RDFHandlerException, IOException {
		assertEquals(e,
				StatementUtils.toStatements(StatementUtils.toByteArray(e)));
	}

}
