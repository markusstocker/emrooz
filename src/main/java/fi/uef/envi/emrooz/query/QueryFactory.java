/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query;

/**
 * <p>
 * Title: EmroozQueryFactory
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

public class QueryFactory {

	public static SensorObservationQuery createSensorObservationQuery(String query) {
		return SensorObservationQuery.parse(query);
	}
	
}
