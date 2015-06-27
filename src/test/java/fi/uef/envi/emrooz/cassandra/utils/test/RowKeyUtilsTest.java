/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.Rollover;
import fi.uef.envi.emrooz.cassandra.utils.RowKeyUtils;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.test.ParamsConverterTest;

/**
 * <p>
 * Title: RowKeyUtilsTest
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
public class RowKeyUtilsTest {

	@Test
	@FileParameters("src/test/resources/RowKeyUtilsTest-testGetRowKey.csv")
	public void testGetRowKey(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime time,
			String e, String assertType) {
		String a = new RowKeyUtils().getRowKey(sensorId, propertyId, featureId,
				new Frequency(measPropId, new QuantityValue(valueId, value,
						new Unit(unitId))), time);

		if (assertType.equals("assertEquals")) {
			assertEquals(e, a);
			return;
		}

		assertNotEquals(e, a);
	}

	@Test
	@FileParameters("src/test/resources/RowKeyUtilsTest-testGetRollover.csv")
	public void testGetRollover(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId,
			String rollover, String assertType) {
		Rollover a = new RowKeyUtils().getRollover(sensorId, propertyId,
				featureId, new Frequency(measPropId, new QuantityValue(valueId,
						value, new Unit(unitId))));

		Rollover e = Rollover.valueOf(rollover);

		if (assertType.equals("assertEquals")) {
			assertEquals(e, a);
			return;
		}

		assertNotEquals(e, a);
	}

}
