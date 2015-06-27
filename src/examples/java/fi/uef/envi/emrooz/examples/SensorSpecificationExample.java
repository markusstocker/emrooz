/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.api.KnowledgeStore;
import fi.uef.envi.emrooz.entity.EntityFactory;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;

/**
 * <p>
 * Title: SensorSpecificationExample
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

public class SensorSpecificationExample {

	static EntityFactory f = EntityFactory.getInstance("http://example.org#");

	public static void main(String[] args) {
		// For non-volatile stores check
		// http://rdf4j.org/sesame/2.8/docs/programming.docbook?view#section-repository-api
		// Example: new SailRepository(new MemoryStore(new File("/tmp/ks")))
		Repository r = new SailRepository(new MemoryStore());
		KnowledgeStore ks = new SesameKnowledgeStore(r);

		// Loads this KB, which may contain sensor specifications
//		ks.load(new File("src/examples/resources/kb.rdf"));

		// Load sensor specifications programmatically
//		ks.addSensor(sensor1());
//		ks.addSensor(sensor2());
//		ks.addSensor(sensor3());
		ks.addSensor(sensor4());

		ks.close();
	}

	private static Sensor sensor1() {
		return f.createSensor("thermometer", "temperature", "air", 1.0);
	}

	private static Sensor sensor2() {
		return f.createSensor("aSoilMoistureSensor", f.createProperty(
				"waterContent", f.createFeatureOfInterest("soil")), f
				.createMeasurementCapability(f.createFrequency(f
						.createQuantityValue(0.00167,
								f.createUnit(QUDTUnit.Hertz)))));
	}

	private static Sensor sensor3() {
		Sensor sensor = f.createSensor("aAnemometer");

		Property property = f.createProperty("speed");
		sensor.setObservedProperty(property);

		FeatureOfInterest feature = f.createFeatureOfInterest("wind");
		property.addPropertyOf(feature);

		MeasurementCapability capability = f.createMeasurementCapability();
		sensor.addMeasurementCapability(capability);

		Frequency frequency = f.createFrequency();
		capability.addMeasurementProperty(frequency);

		QuantityValue value = f.createQuantityValue();
		frequency.setQuantityValue(value);

		Unit unit = f.createUnit(QUDTUnit.Hertz);

		value.setNumericValue(0.0167);
		value.setUnit(unit);

		return sensor;
	}

	private static Sensor sensor4() {
		Sensor sensor = f.createSensor("aGasAnalyzer");

		Property property = f.createProperty("moleFraction");
		sensor.setObservedProperty(property);

		property.addPropertyOf(f.createFeatureOfInterest("CO2"));
		property.addPropertyOf(f.createFeatureOfInterest("H2O"));

		MeasurementCapability capability = f.createMeasurementCapability();
		sensor.addMeasurementCapability(capability);

		Frequency frequency = f.createFrequency();
		capability.addMeasurementProperty(frequency);

		QuantityValue value = f.createQuantityValue();
		frequency.setQuantityValue(value);

		Unit unit = f.createUnit(QUDTUnit.Hertz);

		value.setNumericValue(0.0167);
		value.setUnit(unit);

		return sensor;
	}

}
