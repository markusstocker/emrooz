/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import fi.uef.envi.emrooz.sesame.SesameQueryHandler;

/**
 * <p>
 * Title: EmroozResultSet
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

public class ResultSet {

	private TupleQueryResult result;
	private SesameQueryHandler sesameQueryHandler;
	private boolean isEmpty = false;
	private BindingSet next;

	private static final Logger log = Logger.getLogger(ResultSet.class
			.getName());

	private ResultSet() {
		isEmpty = true;
	}
	
	public ResultSet(SesameQueryHandler sesameQueryHandler) {
		this.sesameQueryHandler = sesameQueryHandler;
		this.result = sesameQueryHandler.evaluate();
	}

	public boolean hasNext() {
		if (isEmpty)
			return false;

		boolean hasNext = false;

		try {
			hasNext = result.hasNext();

			if (hasNext)
				next = result.next();
			else
				next = null;
		} catch (QueryEvaluationException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}

		return hasNext;
	}

	public BindingSet next() {
		return next;
	}

	public void close() {
		try {
			if (result != null)
				result.close();
		} catch (QueryEvaluationException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		} finally {
			sesameQueryHandler.close();
		}
	}
	
	public static ResultSet empty() {
		return new ResultSet();
	}

}
