/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.Rollover;
import fi.uef.envi.emrooz.api.EntityFactory;
import fi.uef.envi.emrooz.api.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.api.ssn.Property;
import fi.uef.envi.emrooz.api.ssn.Sensor;

/**
 * <p>
 * Title:
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

public class SensorRegistrationExample {

	public static void main(String[] args) {
		EntityFactory f = EntityFactory.getInstance("http://example.org#");

		Sensor sensor = f.createSensor("thermometer");
		Property property = f.createProperty("temperature");
		FeatureOfInterest feature = f.createFeatureOfInterest("air");

		Emrooz emrooz = new Emrooz();

		emrooz.register(sensor, property, feature, Rollover.DAY);

		emrooz.close();
	}

}
