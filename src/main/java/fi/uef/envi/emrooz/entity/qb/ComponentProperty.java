/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;

/**
 * <p>
 * Title: ComponentProperty
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

public abstract class ComponentProperty extends AbstractEntity {

	public ComponentProperty(URI id, URI type) {
		super(id, type);
	}

}
