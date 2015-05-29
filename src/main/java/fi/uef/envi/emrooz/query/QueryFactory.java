/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

/**
 * <p>
 * Title: EmroozQueryFactory
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

public class QueryFactory {

	public static ParsedQuery createParsedQuery(String query) {
		try {
			return new SPARQLParser().parseQuery(query, null);
		} catch (MalformedQueryException e) {
			throw new RuntimeException(e);
		}
	}

	public static SensorObservationQuery createSensorObservationQuery(
			ParsedQuery query) {
		return SensorObservationQuery.create(query);
	}

	public static SensorObservationQuery createSensorObservationQuery(
			String query) {
		return createSensorObservationQuery(createParsedQuery(query));
	}

}
