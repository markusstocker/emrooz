/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.ssn.Frequency;
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

public interface DataStore extends Store {

	public void addSensorObservation(URI sensorId, URI propertyId,
			URI featureId, Frequency frequency, DateTime resultTime,
			Set<Statement> statements);

	public QueryHandler<Statement> createQueryHandler(
			Map<SensorObservationQuery, Frequency> queries);

}
