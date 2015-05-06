/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.entity.EntityFactory;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;

/**
 * <p>
 * Title: AddSensorObservationExample
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

public class AddSensorObservationExample {

	public static void main(String[] args) {
		Emrooz emrooz = new Emrooz();

		EntityFactory f = EntityFactory.getInstance("http://example.org#");

		SensorObservation observation = f.createSensorObservation(
				"thermometer", "temperature", "air", 7.6,
				"2015-04-21T01:00:00.000+03:00");

		// Make sure to have register the sensor
		emrooz.add(observation);

		emrooz.close();
	}

}
