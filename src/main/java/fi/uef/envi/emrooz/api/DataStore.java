/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import java.util.Set;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;

import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.query.SensorObservationQuery;

/**
 * <p>
 * Title: DataStore
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

public interface DataStore {

	public void addSensorObservation(Sensor specification, DateTime resultTime,
			Set<Statement> statements);

	public QueryHandler<?> createQueryHandler(Sensor specification,
			SensorObservationQuery query);
	
	public void close();
	
}
