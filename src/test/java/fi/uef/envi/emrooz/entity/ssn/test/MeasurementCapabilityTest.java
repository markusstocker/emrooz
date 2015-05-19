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
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.QUDTSchema;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: MeasurementCapabilityTest
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
public class MeasurementCapabilityTest {

	@Test
	@FileParameters("src/test/resources/MeasurementCapabilityTest.csv")
	public void testMeasurementCapability(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropType1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueType1,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropType2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueType2,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId2,
			String assertType) {
		if (type1 == null)
			type1 = SSN.MeasurementCapability;
		if (type2 == null)
			type2 = SSN.MeasurementCapability;
		if (measPropType1 == null)
			measPropType1 = SSN.Frequency;
		if (measPropType2 == null)
			measPropType2 = SSN.Frequency;
		if (valueType1 == null)
			valueType1 = QUDTSchema.QuantityValue;
		if (valueType2 == null)
			valueType2 = QUDTSchema.QuantityValue;

		MeasurementCapability c1 = new MeasurementCapability(id1, type1,
				new Frequency(measPropId1, measPropType1, new QuantityValue(
						valueId1, valueType1, value1, new Unit(unitId1))));
		MeasurementCapability c2 = new MeasurementCapability(id2, type2,
				new Frequency(measPropId2, measPropType2, new QuantityValue(
						valueId2, valueType2, value2, new Unit(unitId2))));

		if (assertType.equals("assertEquals")) {
			assertEquals(c1, c2);
			assertEquals(c1.hashCode(), c2.hashCode());
			return;
		}

		assertNotEquals(c1, c2);
		assertNotEquals(c1.hashCode(), c2.hashCode());
	}
}
