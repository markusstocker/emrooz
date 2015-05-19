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

import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.QUDTSchema;

/**
 * <p>
 * Title: QuantityValueTest
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
public class QuantityValueTest {

	@Test
	@FileParameters("src/test/resources/QuantityValueTest.csv")
	public void testQuantityValue(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId2,
			String assertType) {
		if (type1 == null)
			type1 = QUDTSchema.QuantityValue;
		if (type2 == null)
			type2 = QUDTSchema.QuantityValue;
		
		QuantityValue v1 = new QuantityValue(id1, type1, value1, new Unit(unitId1));
		QuantityValue v2 = new QuantityValue(id2, type2, value2, new Unit(unitId2));

		if (assertType.equals("assertEquals")) {
			assertEquals(v1, v2);
			assertEquals(v1.hashCode(), v2.hashCode());
			return;
		}

		assertNotEquals(v1, v2);
		assertNotEquals(v1.hashCode(), v2.hashCode());
	}
}
