/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qudt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.test.ParamsConverterTest;

/**
 * <p>
 * Title: UnitTest
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
public class UnitTest {

	@Test
	@FileParameters("src/test/resources/UnitTest.csv")
	public void testUnit(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			String assertType) {
		Unit u1 = new Unit(id1);
		Unit u2 = new Unit(id2);

		if (assertType.equals("assertEquals")) {
			assertEquals(u1, u2);
			assertEquals(u1.hashCode(), u2.hashCode());
			return;
		}

		assertNotEquals(u1, u2);
		assertNotEquals(u1.hashCode(), u2.hashCode());
	}
	
}
