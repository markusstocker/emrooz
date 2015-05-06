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

import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.entity.time.Instant;
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
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		Instant instantA = new Instant(id, value);
		Set<Statement> statementsA = representer.createRepresentation(instantA);
		Instant instantE = representer.createInstant(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(instantE, instantA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(instantE, instantA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testObservationValueDouble.csv")
	public void testObservationValueDouble(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		ObservationValueDouble observationValueA = new ObservationValueDouble(
				id, value);
		Set<Statement> statementsA = representer
				.createRepresentation(observationValueA);
		ObservationValueDouble observationValueE = representer
				.createObservationValueDouble(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(observationValueE, observationValueA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(observationValueE, observationValueA);
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
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		SensorObservation observationA = new SensorObservation(id, new Sensor(
				sensorId), new Property(propertyId), new FeatureOfInterest(
				featureId));
		observationA.setObservationResult(new SensorOutput(outputId,
				new ObservationValueDouble(valueId, value)));
		observationA.setObservationResultTime(new Instant(timeId, time));
		Set<Statement> statementsA = representer
				.createRepresentation(observationA);
		SensorObservation observationE = representer
				.createSensorObservation(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(observationE, observationA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(observationE, observationA);
	}
}
