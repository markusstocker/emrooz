/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.io;

import fi.uef.envi.emrooz.api.SensorObservationReader;

/**
 * <p>
 * Title: AbstractSensorObservationReader
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

public abstract class AbstractSensorObservationReader implements
		SensorObservationReader {

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"This sensor observation reader those not support removing sensor observations from the iterator.");
	}

}
