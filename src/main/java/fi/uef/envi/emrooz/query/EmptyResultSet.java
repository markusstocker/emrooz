/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query;

import fi.uef.envi.emrooz.api.ResultSet;

/**
 * <p>
 * Title: EmptyResultSet<T>
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

public class EmptyResultSet<T> implements ResultSet<T> {

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		return null;
	}

	@Override
	public void close() {
		// Nothing to close
	}

}
