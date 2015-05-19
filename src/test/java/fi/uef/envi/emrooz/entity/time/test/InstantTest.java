/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.time.test;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.joda.time.DateTime;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.Time;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * <p>
 * Title: InstantTest
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
public class InstantTest {

	@Test
	@FileParameters("src/test/resources/InstantTest.csv")
	public void testInstant(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime value1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime value2,
			String assertType) {
		if (type1 == null)
			type1 = Time.Instant;
		if (type2 == null)
			type2 = Time.Instant;

		Instant i1 = new Instant(id1, type1, value1);
		Instant i2 = new Instant(id2, type2, value2);

		if (assertType.equals("assertEquals")) {
			assertEquals(i1, i2);
			assertEquals(i1.hashCode(), i2.hashCode());
			return;
		}

		assertNotEquals(i1, i2);
		assertNotEquals(i1.hashCode(), i2.hashCode());
	}

}
