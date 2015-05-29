/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import org.openrdf.query.BindingSet;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.api.KnowledgeStore;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.EntityFactory;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;

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
		// String sparql = sparql1(); // Fully specified query
		String sparql = sparql2(); // Minimally specified query

		EntityFactory f = EntityFactory.getInstance("http://example.org#");

		Repository r = new SailRepository(new MemoryStore());
		KnowledgeStore ks = new SesameKnowledgeStore(r);
		ks.addSensor(f.createSensor("thermometer", "temperature", "air", 1.0));
		ks.addSensor(f.createSensor("hygrometer", "humidity", "air", 1.0));

		Emrooz emrooz = new Emrooz(ks, new CassandraDataStore());

		ResultSet<BindingSet> results = emrooz.evaluate(sparql);

		while (results.hasNext()) {
			System.out.println(results.next());
		}

		results.close();

		emrooz.close();
	}

	private static String sparql1() {
		return "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
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
	}

	private static String sparql2() {
		return "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
				+ "select ?sensor ?property ?time ?value "
				+ "where {"
				+ "["
				+ "ssn:observedBy ?sensor ;"
				+ "ssn:observedProperty ?property ;"
				+ "ssn:featureOfInterest <http://example.org#air> ;"
				+ "ssn:observationResultTime [ time:inXSDDateTime ?time ] ;"
				+ "ssn:observationResult [ ssn:hasValue [ dul:hasRegionDataValue ?value ] ]"
				+ "]"
				+ "filter (?time >= \"2015-04-21T00:00:00.000+03:00\"^^xsd:dateTime && ?time < \"2015-04-21T02:00:00.000+03:00\"^^xsd:dateTime)"
				+ "} order by ?sensor desc (?time)";
	}

}
