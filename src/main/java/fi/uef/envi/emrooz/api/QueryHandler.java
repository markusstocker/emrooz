/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import org.openrdf.query.TupleQueryResultHandler;

/**
 * <p>
 * Title: QueryHandler
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

public interface QueryHandler<T> {
	
	public ResultSet<T> evaluate();
	
	public void evaluate(TupleQueryResultHandler handler);
	
	public void close();
	
}
