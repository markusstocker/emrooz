/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api.ssn;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.api.AbstractEntity;

/**
 * <p>
 * Title: ObservationValue
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

public abstract class ObservationValue extends AbstractEntity {

	public ObservationValue(URI id, URI type) {
		super(id, type);
	}

}
