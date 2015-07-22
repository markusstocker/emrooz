/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query;

import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static Logger log = Logger.getLogger(QueryFactory.class.getName());

	public static ParsedQuery createParsedQuery(String query) {
		try {
			return new SPARQLParser().parseQuery(query, null);
		} catch (MalformedQueryException e) {
			throw new RuntimeException(e);
		}
	}

	public static ObservationQuery createObservationQuery(ParsedQuery query) {
		SensorObservationQuery ret1 = createSensorObservationQuery(query);

		if (ret1.isSensorObservationQuery())
			return ret1;

		DatasetObservationQuery ret2 = createDatasetObservationQuery(query);

		if (ret2.isDatasetObservationQuery())
			return ret2;

		if (log.isLoggable(Level.SEVERE))
			log.severe("Failed to determine observation query time, sensor or dataset observation [query = "
					+ query + "]");

		return null;
	}

	public static SensorObservationQuery createSensorObservationQuery(
			ParsedQuery query) {
		return SensorObservationQuery.create(query);
	}

	public static SensorObservationQuery createSensorObservationQuery(
			String query) {
		return createSensorObservationQuery(createParsedQuery(query));
	}

	public static DatasetObservationQuery createDatasetObservationQuery(
			ParsedQuery query) {
		return DatasetObservationQuery.create(query);
	}

	public static DatasetObservationQuery createDatasetObservationQuery(
			String query) {
		return createDatasetObservationQuery(createParsedQuery(query));
	}

}
