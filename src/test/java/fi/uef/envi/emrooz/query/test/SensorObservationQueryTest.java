/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;
import java.util.Set;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.query.SensorObservationQuery;
import fi.uef.envi.emrooz.test.ParamsConverterTest;

/**
 * <p>
 * Title: SensorObservationQueryTest
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
public class SensorObservationQueryTest {

	@Test
	@FileParameters("src/test/resources/SensorObservationQueryTest-1.csv")
	public void testSensorObservationQuery1(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime timeFrom,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime timeTo,
			@ConvertParam(value = ParamsConverterTest.StringToSensorObservationQueryCollection.class) Set<SensorObservationQuery> e,
			String assertType) {
		Set<SensorObservationQuery> a = new HashSet<SensorObservationQuery>();
		a.add(SensorObservationQuery.create(sensorId, propertyId, featureId,
				timeFrom, timeTo));

		if (assertType.equals("assertEquals")) {
			assertEquals(e, a);
			return;
		}

		assertNotEquals(e, a);
	}

	@Test
	@FileParameters("src/test/resources/SensorObservationQueryTest-2.csv")
	public void testSensorObservationQuery2(
			String query,
			@ConvertParam(value = ParamsConverterTest.StringToSensorObservationQueryCollection.class) Set<SensorObservationQuery> e,
			String assertType) {
		Set<SensorObservationQuery> a = new HashSet<SensorObservationQuery>();
		a.add(SensorObservationQuery.create(query));

		if (assertType.equals("assertEquals")) {
			assertEquals(e, a);
			return;
		}

		assertNotEquals(e, a);
	}

}
