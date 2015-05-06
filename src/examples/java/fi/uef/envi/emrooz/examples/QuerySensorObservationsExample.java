/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import org.openrdf.query.BindingSet;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.query.QueryFactory;
import fi.uef.envi.emrooz.query.ResultSet;
import fi.uef.envi.emrooz.query.SensorObservationQuery;

/**
 * <p>
 * Title: QuerySensorObservationsExample
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

public class QuerySensorObservationsExample {

	public static void main(String[] args) {
		String sparql = "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
				+ "select ?time ?value "
				+ "where {"
				+ "["
				+ "ssn:observedBy <http://example.org#thermometer> ;"
				+ "ssn:observedProperty <http://example.org#temperature> ;"
				+ "ssn:featureOfInterest <http://example.org#air> ;"
				+ "ssn:observationResultTime [ time:inXSDDateTime ?time ] ;"
				+ "ssn:observationResult [ ssn:hasValue [ dul:hasRegionDataValue ?value ] ]"
				+ "]"
				+ "filter (?time >= \"2015-04-21T00:00:00.000+03:00\"^^xsd:dateTime && ?time < \"2015-04-21T02:00:00.000+03:00\"^^xsd:dateTime)"
				+ "} order by desc (?time)";

		Emrooz emrooz = new Emrooz();

		SensorObservationQuery query = QueryFactory
				.createSensorObservationQuery(sparql);

		ResultSet results = emrooz.evaluate(query);

		while (results.hasNext()) {
			BindingSet result = results.next();
			System.out.println(result.getValue("time") + " "
					+ result.getValue("value"));
		}

		results.close();

		emrooz.close();
	}

}
