/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn.test;

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
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.QUDTSchema;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: FrequencyTest
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
public class FrequencyTest {

	@Test
	@FileParameters("src/test/resources/FrequencyTest.csv")
	public void testQuantityValue(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueType1,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueType2,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId2,
			String assertType) {
		if (type1 == null)
			type1 = SSN.Frequency;
		if (type2 == null)
			type2 = SSN.Frequency;
		if (valueType1 == null)
			valueType1 = QUDTSchema.QuantityValue;
		if (valueType2 == null)
			valueType2 = QUDTSchema.QuantityValue;

		Frequency f1 = new Frequency(id1, type1, new QuantityValue(valueId1,
				valueType1, value1, new Unit(unitId1)));
		Frequency f2 = new Frequency(id2, type2, new QuantityValue(valueId2,
				valueType2, value2, new Unit(unitId2)));

		if (assertType.equals("assertEquals")) {
			assertEquals(f1, f2);
			assertEquals(f1.hashCode(), f2.hashCode());
			return;
		}

		assertNotEquals(f1, f2);
		assertNotEquals(f1.hashCode(), f2.hashCode());
	}

}
