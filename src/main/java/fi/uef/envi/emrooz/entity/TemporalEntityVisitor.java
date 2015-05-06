/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity;

import fi.uef.envi.emrooz.entity.time.Instant;

/**
 * <p>
 * Title: TemporalEntityVisitor
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

public interface TemporalEntityVisitor {

	public void visit(Instant entity);
	
}
