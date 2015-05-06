/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: Entity
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

public interface Entity {

	public URI getId();
	
	public URI getType();
	
	public void accept(EntityVisitor visitor);
	
}
