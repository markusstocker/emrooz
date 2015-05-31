/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.query.SensorObservationQuery;
import fi.uef.envi.emrooz.query.SensorObservationQueryRewriter;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;
import fi.uef.envi.emrooz.test.ParamsConverterTest;

/**
 * <p>
 * Title: SensorObservationQueryRewriterTest
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
public class SensorObservationQueryRewriterTest {

	@Test
	@FileParameters("src/test/resources/SensorObservationQueryRewriterTest.csv")
	public void test1(
			String kb,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime timeFrom,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime timeTo,
			@ConvertParam(value = ParamsConverterTest.StringToSensorObservationQueryCollection.class) Set<SensorObservationQuery> e,
			String assertType) throws RepositoryException, RDFParseException,
			IOException {
		SailRepository rp = new SailRepository(new MemoryStore());
		rp.initialize();
		RepositoryConnection rc = rp.getConnection();
		rc.add(new File(kb), null, RDFFormat.RDFXML);

		SensorObservationQueryRewriter rw = new SensorObservationQueryRewriter(
				new SesameKnowledgeStore(rp));

		Set<SensorObservationQuery> a = rw.rewrite(SensorObservationQuery
				.create(sensorId, propertyId, featureId, timeFrom, timeTo));

		if (assertType.equals("assertEquals")) {
			assertEquals(e, a);
			return;
		}

		assertNotEquals(e, a);

		rc.close();
	}
}
