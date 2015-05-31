/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.sesame.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.rdf.RDFEntityRepresenter;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;
import fi.uef.envi.emrooz.test.ParamsConverterTest;

/**
 * <p>
 * Title: SesameKnowledgeStoreTest
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
public class SesameKnowledgeStoreTest {

	@Test
	@FileParameters("src/test/resources/SesameKnowledgeStoreTest-testGetSensors.csv")
	public void testGetSensors(
			String kb,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statements,
			String assertType) throws RepositoryException, RDFParseException,
			IOException {
		SesameKnowledgeStore ks = new SesameKnowledgeStore(new SailRepository(new MemoryStore()));
		ks.load(new File(kb));
		
		RDFEntityRepresenter er = new RDFEntityRepresenter();
		Set<Sensor> e = er.createSensors(statements);

		Set<Sensor> a = ks.getSensors();

		if (assertType.equals("assertEquals")) {
			assertTrue(CollectionUtils.isEqualCollection(e, a));
			return;
		}

		assertFalse(CollectionUtils.isEqualCollection(e, a));
		
		ks.close();
	}
}
