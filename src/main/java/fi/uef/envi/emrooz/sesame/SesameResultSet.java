/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.sesame;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import fi.uef.envi.emrooz.api.ResultSet;

/**
 * <p>
 * Title: SesameResultSet
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

public class SesameResultSet implements ResultSet<BindingSet> {

	private TupleQueryResult result;

	public SesameResultSet(TupleQueryResult result) {
		this.result = result;
	}

	@Override
	public boolean hasNext() {
		try {
			return result.hasNext();
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public BindingSet next() {
		try {
			return result.next();
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			result.close();
		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		}
	}
	
}
