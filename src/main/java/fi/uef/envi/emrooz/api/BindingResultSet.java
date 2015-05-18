/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import org.openrdf.query.BindingSet;

/**
 * <p>
 * Title: BindingResultSet
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

public interface BindingResultSet extends ResultSet<BindingSet> {

	@Override
	public BindingSet next();
	
}
