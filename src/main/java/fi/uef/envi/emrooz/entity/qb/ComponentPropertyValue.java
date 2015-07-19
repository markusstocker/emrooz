/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb;

import fi.uef.envi.emrooz.entity.ComponentPropertyValueVisitor;

/**
 * <p>
 * Title: ComponentPropertyValue
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

public abstract class ComponentPropertyValue {

	public abstract Object getValue();
	
	public abstract void accept(ComponentPropertyValueVisitor visitor);
	
}
