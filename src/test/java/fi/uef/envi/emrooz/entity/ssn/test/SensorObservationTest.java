/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn.test;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: SensorObservationTest
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
public class SensorObservationTest {

	@Test
	@FileParameters("src/test/resources/SensorObservationTest.csv")
	public void testSensorObservation(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorType1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyType1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureType1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorOutputId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorOutputType1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI observationValueId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI observationValueType1,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double observationValue1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI resultTimeId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI resultTimeType1,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime resultTime1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorType2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyType2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureType2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorOutputId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorOutputType2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI observationValueId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI observationValueType2,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double observationValue2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI resultTimeId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI resultTimeType2,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime resultTime2,
			String assertType) {
		if (type1 == null)
			type1 = SSN.Observation;
		if (type2 == null)
			type2 = SSN.Observation;
		if (sensorType1 == null)
			sensorType1 = SSN.Sensor;
		if (sensorType2 == null)
			sensorType2 = SSN.Sensor;
		if (propertyType1 == null)
			propertyType1 = SSN.Property;
		if (propertyType2 == null)
			propertyType2 = SSN.Property;
		if (featureType1 == null)
			featureType1 = SSN.FeatureOfInterest;
		if (featureType2 == null)
			featureType2 = SSN.FeatureOfInterest;
		if (sensorOutputType1 == null)
			sensorOutputType1 = SSN.SensorOutput;
		if (sensorOutputType2 == null)
			sensorOutputType2 = SSN.SensorOutput;
		if (observationValueType1 == null)
			observationValueType1 = SSN.ObservationValue;
		if (observationValueType2 == null)
			observationValueType2 = SSN.ObservationValue;
		if (resultTimeType1 == null)
			resultTimeType1 = Time.Instant;
		if (resultTimeType2 == null)
			resultTimeType2 = Time.Instant;

		SensorObservation o1 = new SensorObservation(id1, type1, new Sensor(
				sensorId1, sensorType1), new Property(propertyId1,
				propertyType1), new FeatureOfInterest(featureId1, featureType1));
		SensorObservation o2 = new SensorObservation(id2, type2, new Sensor(
				sensorId2, sensorType2), new Property(propertyId2,
				propertyType2), new FeatureOfInterest(featureId2, featureType2));

		o1.setObservationResult(new SensorOutput(sensorOutputId1,
				sensorOutputType1, new ObservationValueDouble(
						observationValueId1, observationValueType1,
						observationValue1)));
		o1.setObservationResultTime(new Instant(resultTimeId1, resultTimeType1,
				resultTime1));

		o2.setObservationResult(new SensorOutput(sensorOutputId2,
				sensorOutputType2, new ObservationValueDouble(
						observationValueId2, observationValueType2,
						observationValue2)));
		o2.setObservationResultTime(new Instant(resultTimeId2, resultTimeType2,
				resultTime2));

		if (assertType.equals("assertEquals")) {
			assertEquals(o1, o2);
			assertEquals(o1.hashCode(), o2.hashCode());
			return;
		}

		assertNotEquals(o1, o2);
		assertNotEquals(o1.hashCode(), o2.hashCode());
	}

}
