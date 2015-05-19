/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import java.io.File;
import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.EntityFactory;
import fi.uef.envi.emrooz.query.QueryFactory;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;

/**
 * <p>
 * Title: CompletePersistentExample
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

public class CompletePersistentExample {

	static Emrooz e;
	static Random r = new Random();
	static DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();
	static EntityFactory f = EntityFactory.getInstance("http://example.org#");

	public static void main(String[] args) {
		e = new Emrooz(new SesameKnowledgeStore(new SailRepository(
				new MemoryStore(new File("/tmp/ks")))),
				new CassandraDataStore());

		add(); // Comment to query again after adding the data
		query();

		e.close();
	}

	private static void query() {
		String sparql = "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
				+ "select ?time ?value "
				+ "where {"
				+ "["
				+ "ssn:observedBy <http://example.org#aThermometer> ;"
				+ "ssn:observedProperty <http://example.org#temperature> ;"
				+ "ssn:featureOfInterest <http://example.org#air> ;"
				+ "ssn:observationResultTime [ time:inXSDDateTime ?time ] ;"
				+ "ssn:observationResult [ ssn:hasValue [ dul:hasRegionDataValue ?value ] ]"
				+ "]"
				+ "filter (?time >= \"2015-05-18T00:00:30.000+03:00\"^^xsd:dateTime && ?time < \"2015-05-18T00:00:35.000+03:00\"^^xsd:dateTime)"
				+ "} order by asc (?time)";

		System.out.println("== QUERY ==");
		
		long start = System.currentTimeMillis();

		e.evaluate(QueryFactory.createSensorObservationQuery(sparql),
				new SPARQLResultsTSVWriter(System.out)); // There are also CSV,
															// JSON, and XML
															// writers

		long end = System.currentTimeMillis();

		System.out.println("Query time (s): " + elapsed(start, end));
	}

	private static void add() {
		e.add(f.createSensor("aThermometer", "temperature", "air", 1.0));
		e.add(f.createSensor("aAccelerometer", "vibration", "pavement", 1.0));

		DateTime now = dtf.parseDateTime("2015-05-18T00:00:00.000+03:00");

		System.out.println("== ADD ==");
		
		long start = System.currentTimeMillis();

		for (int i = 0; i < 120; i++) {
			e.add(f.createSensorObservation("aThermometer", "temperature",
					"air", r.nextDouble(),
					ISODateTimeFormat.dateTime().print(now.plusSeconds(i))));
			e.add(f.createSensorObservation("aAccelerometer", "vibration",
					"pavement", r.nextDouble(), dtf.print(now.plusSeconds(i))));
		}

		long end = System.currentTimeMillis();

		System.out.println("Add time (s): " + elapsed(start, end));
	}

	private static String elapsed(long start, long end) {
		return (end - start) / 1000 + "." + (end - start) % 1000;
	}

}
