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

import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.qb.ComponentProperty;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueEntity;
import fi.uef.envi.emrooz.entity.qb.Dataset;
import fi.uef.envi.emrooz.entity.qb.DatasetObservation;
import fi.uef.envi.emrooz.entity.qb.MeasureProperty;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.rdf.RDFEntityRepresenter;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.QB;
import fi.uef.envi.emrooz.vocabulary.QUDTSchema;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

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
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testProperty-1.csv")
	public void testProperty1(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = SSN.Property;

		Property propertyA = new Property(id, type);
		Set<Statement> statementsA = representer
				.createRepresentation(propertyA);
		Property propertyE = representer.createProperty(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(propertyE, propertyA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(propertyE, propertyA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testProperty-2.csv")
	public void testProperty2(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI feature1Id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI feature2Id,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		Property propertyA = new Property(id,
				new FeatureOfInterest(feature1Id), new FeatureOfInterest(
						feature2Id));
		Set<Statement> statementsA = representer
				.createRepresentation(propertyA);
		Property propertyE = representer.createProperty(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(propertyE, propertyA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(propertyE, propertyA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testFeatureOfInterest.csv")
	public void testFeatureOfInterest(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = SSN.FeatureOfInterest;

		FeatureOfInterest featureA = new FeatureOfInterest(id, type);
		Set<Statement> statementsA = representer.createRepresentation(featureA);
		FeatureOfInterest featureE = representer
				.createFeatureOfInterest(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(featureE, featureA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(featureE, featureA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testSensorOutput.csv")
	public void testSensorOutput(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueType,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = SSN.SensorOutput;
		if (valueType == null)
			valueType = SSN.ObservationValue;

		SensorOutput outputA = new SensorOutput(id, type,
				new ObservationValueDouble(valueId, valueType, value));
		Set<Statement> statementsA = representer.createRepresentation(outputA);
		SensorOutput outputE = representer.createSensorOutput(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(outputE, outputA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(outputE, outputA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testFrequency.csv")
	public void testFrequency(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueType,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitType,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = SSN.Frequency;
		if (valueType == null)
			valueType = QUDTSchema.QuantityValue;
		if (unitType == null)
			unitType = QUDTSchema.Unit;

		Frequency frequencyA = new Frequency(id, type, new QuantityValue(
				valueId, valueType, value, new Unit(unitId, unitType)));
		Set<Statement> statementsA = representer
				.createRepresentation(frequencyA);
		Frequency frequencyE = representer.createFrequency(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(frequencyE, frequencyA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(frequencyE, frequencyA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testMeasurementCapability.csv")
	public void testMeasurementCapability(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropType,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueType,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitType,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = SSN.MeasurementCapability;
		if (measPropType == null)
			measPropType = SSN.Frequency;
		if (valueType == null)
			valueType = QUDTSchema.QuantityValue;
		if (unitType == null)
			unitType = QUDTSchema.Unit;

		MeasurementCapability capabilityA = new MeasurementCapability(id, type,
				new Frequency(measPropId, measPropType, new QuantityValue(
						valueId, valueType, value, new Unit(unitId, unitType))));
		Set<Statement> statementsA = representer
				.createRepresentation(capabilityA);
		MeasurementCapability capabilityE = representer
				.createMeasurementCapability(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(capabilityE, capabilityA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(capabilityE, capabilityA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testInstant.csv")
	public void testInstant(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime value,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = Time.Instant;

		Instant instantA = new Instant(id, type, value);
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

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testSensor-1.csv")
	public void testSensor1(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI featureId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measCapaId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI measPropId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI valueId,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		Sensor sensorA = new Sensor(id, new Property(propertyId,
				new FeatureOfInterest(featureId)), new MeasurementCapability(
				measCapaId, new Frequency(measPropId, new QuantityValue(
						valueId, value, new Unit(unitId)))));
		Set<Statement> statementsA = representer.createRepresentation(sensorA);
		Sensor sensorE = representer.createSensor(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(sensorE, sensorA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(sensorE, sensorA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testSensor-2.csv")
	public void testSensor2(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI property1Id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI property2Id,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		Sensor sensorA = new Sensor(id, new Property(property1Id),
				new Property(property2Id));
		Set<Statement> statementsA = representer.createRepresentation(sensorA);
		Sensor sensorE = representer.createSensor(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(sensorE, sensorA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(sensorE, sensorA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testQuantityValue.csv")
	public void testQuantityValue(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitType,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = QUDTSchema.QuantityValue;
		if (unitType == null)
			unitType = QUDTSchema.Unit;

		QuantityValue valueA = new QuantityValue(id, type, value, new Unit(
				unitId, unitType));
		Set<Statement> statementsA = representer.createRepresentation(valueA);
		QuantityValue valueE = representer.createQuantityValue(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(valueE, valueA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(valueE, valueA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testUnit.csv")
	public void testUnit(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = QUDTSchema.Unit;

		Unit unitA = new Unit(id, type);
		Set<Statement> statementsA = representer.createRepresentation(unitA);
		Unit unitE = representer.createUnit(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(unitE, unitA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(unitE, unitA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testDatasetObservation-1.csv")
	public void testDatasetObservation1(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI datasetId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI instantId,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime instantValue,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = QB.Observation;

		DatasetObservation observationA = new DatasetObservation(id, type,
				new Dataset(datasetId), new Instant(instantId, instantValue));
		Set<Statement> statementsA = representer
				.createRepresentation(observationA);
		DatasetObservation observationE = representer
				.createDatasetObservation(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(observationE, observationA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(observationE, observationA);
	}

	@Test
	@FileParameters("src/test/resources/RDFEntityRepresenterTest-testDatasetObservation-2.csv")
	public void testDatasetObservation2(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI datasetId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI instantId,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime instantValue,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI componentPropertyId,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI componentPropertyValueId,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI unitId,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statementsE,
			String assertType) {
		if (type == null)
			type = QB.Observation;

		DatasetObservation observationA = new DatasetObservation(id, type,
				new Dataset(datasetId), new Instant(instantId, instantValue));

		observationA.addComponent(new MeasureProperty(componentPropertyId),
				new ComponentPropertyValueEntity(new QuantityValue(
						componentPropertyValueId, value, new Unit(unitId))));

		Set<Statement> statementsA = representer
				.createRepresentation(observationA);
		DatasetObservation observationE = representer
				.createDatasetObservation(statementsE);

		if (assertType.equals("assertEquals")) {
			assertEquals(statementsE, statementsA);
			assertEquals(observationE, observationA);
			return;
		}

		assertNotEquals(statementsE, statementsA);
		assertNotEquals(observationE, observationA);
	}

}
