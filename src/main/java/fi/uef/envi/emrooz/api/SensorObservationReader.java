/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import java.util.Iterator;

import fi.uef.envi.emrooz.entity.ssn.SensorObservation;

/**
 * <p>
 * Title: SensorObservationReader
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

public interface SensorObservationReader extends Iterator<SensorObservation> {

	@Override
	public boolean hasNext();
	
	@Override
	public SensorObservation next();
	
}
