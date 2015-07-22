/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query;

import fi.uef.envi.emrooz.api.Query;

/**
 * <p>
 * Title: ObservationQuery
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

public abstract class ObservationQuery implements Query {

	protected boolean isDatsetObservationQuery = false;
	protected boolean isSensorObservationQuery = false;
	
	public boolean isDatasetObservationQuery() {
		return isDatsetObservationQuery;
	}
	
	public boolean isSensorObservationQuery() {
		return isSensorObservationQuery;
	}
	
}
