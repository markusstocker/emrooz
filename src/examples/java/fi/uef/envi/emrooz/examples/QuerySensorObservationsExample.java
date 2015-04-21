/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import java.util.List;

import org.openrdf.query.BindingSet;

import fi.uef.envi.emrooz.Emrooz;

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
		String query = "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
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
				+ "}";

		Emrooz emrooz = new Emrooz();

		List<BindingSet> results = emrooz.getSensorObservations(query);

		for (BindingSet result : results) {
			System.out.println(result.getValue("time") + " "
					+ result.getValue("value"));
		}

		emrooz.close();
	}

}
