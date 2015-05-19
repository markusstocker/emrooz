/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn.test;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: SensorOutputTest
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
public class SensorOutputTest {

	@Test
	@FileParameters("src/test/resources/SensorOutputTest.csv")
	public void testSensorOutput(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId,
			String assertType) {
		if (type1 == null)
			type1 = SSN.SensorOutput;
		if (type2 == null)
			type2 = SSN.SensorOutput;

		SensorOutput so1 = new SensorOutput(id1, type1);
		SensorOutput so2 = new SensorOutput(id2, type2);

		so1.setValue(new ObservationValueDouble(valueId));
		so2.setValue(new ObservationValueDouble(valueId));

		if (assertType.equals("assertEquals")) {
			assertEquals(so1, so2);
			assertEquals(so1.hashCode(), so2.hashCode());
			return;
		}

		assertNotEquals(so1, so2);
		assertNotEquals(so1.hashCode(), so2.hashCode());
	}

}
