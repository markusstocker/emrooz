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
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.QUDTSchema;
import fi.uef.envi.emrooz.vocabulary.SSN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * <p>
 * Title: SensorTest
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
public class SensorTest {

	@Test
	@FileParameters("src/test/resources/SensorTest-1.csv")
	public void testSensor1(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			String assertType) {
		if (type1 == null)
			type1 = SSN.Sensor;
		if (type2 == null)
			type2 = SSN.Sensor;

		Sensor s1 = new Sensor(id1, type1);
		Sensor s2 = new Sensor(id2, type2);

		if (assertType.equals("assertEquals")) {
			assertEquals(s1, s2);
			assertEquals(s1.hashCode(), s2.hashCode());
			return;
		}

		assertNotEquals(s1, s2);
		assertNotEquals(s1.hashCode(), s2.hashCode());
	}

	@Test
	@FileParameters("src/test/resources/SensorTest-2.csv")
	public void testSensor2(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyType1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureType1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measCapaId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measCapaType1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropType1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueType1,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyType2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureType2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measCapaId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measCapaType2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropType2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueType2,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId2,
			String assertType) {
		if (type1 == null)
			type1 = SSN.Sensor;
		if (type2 == null)
			type2 = SSN.Sensor;
		if (propertyType1 == null)
			propertyType1 = SSN.Property;
		if (propertyType2 == null)
			propertyType2 = SSN.Property;
		if (featureType1 == null)
			featureType1 = SSN.FeatureOfInterest;
		if (featureType2 == null)
			featureType2 = SSN.FeatureOfInterest;
		if (measCapaType1 == null)
			measCapaType1 = SSN.MeasurementCapability;
		if (measCapaType2 == null)
			measCapaType2 = SSN.MeasurementCapability;
		if (measPropType1 == null)
			measPropType1 = SSN.Frequency;
		if (measPropType2 == null)
			measPropType2 = SSN.Frequency;
		if (valueType1 == null)
			valueType1 = QUDTSchema.QuantityValue;
		if (valueType2 == null)
			valueType2 = QUDTSchema.QuantityValue;

		Sensor s1 = new Sensor(id1, type1,
				new Property(propertyId1, propertyType1, new FeatureOfInterest(
						featureId1, featureType1)),
				new MeasurementCapability(measCapaId1, measCapaType1,
						new Frequency(measPropId1, measPropType1,
								new QuantityValue(valueId1, valueType1, value1,
										new Unit(unitId1)))));
		Sensor s2 = new Sensor(id2, type2,
				new Property(propertyId2, propertyType2, new FeatureOfInterest(
						featureId2, featureType2)),
				new MeasurementCapability(measCapaId2, measCapaType2,
						new Frequency(measPropId2, measPropType2,
								new QuantityValue(valueId2, valueType2, value2,
										new Unit(unitId2)))));

		if (assertType.equals("assertEquals")) {
			assertEquals(s1, s2);
			assertEquals(s1.hashCode(), s2.hashCode());
			return;
		}

		assertNotEquals(s1, s2);
		assertNotEquals(s1.hashCode(), s2.hashCode());
	}

}
