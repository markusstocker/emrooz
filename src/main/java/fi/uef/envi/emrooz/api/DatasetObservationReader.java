/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import fi.uef.envi.emrooz.entity.qb.DatasetObservation;

/**
 * <p>
 * Title: DatasetObservationReader
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

public interface DatasetObservationReader extends
		ObservationReader<DatasetObservation> {

	@Override
	public boolean hasNext();

	@Override
	public DatasetObservation next();

}
