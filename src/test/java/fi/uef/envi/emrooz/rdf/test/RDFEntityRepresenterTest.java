/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.rdf.test;

import java.util.Set;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.api.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.api.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.api.ssn.Property;
import fi.uef.envi.emrooz.api.ssn.Sensor;
import fi.uef.envi.emrooz.api.ssn.SensorObservation;
import fi.uef.envi.emrooz.api.ssn.SensorOutput;
import fi.uef.envi.emrooz.api.time.Instant;
import fi.uef.envi.emrooz.rdf.RDFEntityRepresenter;
import fi.uef.envi.emrooz.test.ParamsConverterTest;

/**
 * <p>
 * Title: RDFEntityRepresenterTest
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
public class RDFEntityRepresenterTest {

	private RDFEntityRepresenter representer;

	public RDFEntityRepresenterTest() {
		representer = new RDFEntityRepresenter();
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testInstant.csv")
	public void testInstant(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime value,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> expected,
			String assertType) {
		Set<Statement> actual = representer.createRepresentation(new Instant(
				id, value));

		if (assertType.equals("assertEquals")) {
			assertEquals(expected, actual);
			return;
		}

		assertNotEquals(expected, actual);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testObservationValueDouble.csv")
	public void testObservationValueDouble(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> expected,
			String assertType) {
		Set<Statement> actual = representer
				.createRepresentation(new ObservationValueDouble(id, value));

		if (assertType.equals("assertEquals")) {
			assertEquals(expected, actual);
			return;
		}

		assertNotEquals(expected, actual);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testSensorObservation.csv")
	public void testSensorObservation(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI sensorId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI outputId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI timeId,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime time,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> expected,
			String assertType) {
		SensorObservation observation = new SensorObservation(id, new Sensor(
				sensorId), new Property(propertyId), new FeatureOfInterest(
				featureId));
		observation.setObservationResult(new SensorOutput(outputId,
				new ObservationValueDouble(valueId, value)));
		observation.setObservationResultTime(new Instant(timeId, time));

		Set<Statement> actual = representer.createRepresentation(observation);

		if (assertType.equals("assertEquals")) {
			assertEquals(expected, actual);
			return;
		}

		assertNotEquals(expected, actual);
	}

}
