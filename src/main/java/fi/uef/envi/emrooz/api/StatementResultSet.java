/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import org.openrdf.model.Statement;

/**
 * <p>
 * Title: StatementResultSet
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

public interface StatementResultSet extends ResultSet<Statement> {

	@Override
	public Statement next();
	
}
