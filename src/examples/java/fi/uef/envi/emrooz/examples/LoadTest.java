/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.vocabulary.DUL;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: LoadTest
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

public class LoadTest {

	private final ValueFactory vf = new ValueFactoryImpl();

	private final String host = "localhost";
	private final String ns = "http://example.org#";

	private final URI sensor = vf.createURI(ns + "s1");
	private final URI property = vf.createURI(ns + "p1");
	private final URI feature = vf.createURI(ns + "f1");

	private long nextFragment = 0L;

	private final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();

	private void run() {
//		load();
		 query();
	}

	private void query() {
		String query = "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
				+ "select ?time ?value "
				+ "where {"
				+ "["
				+ "ssn:observedBy <"
				+ sensor.stringValue()
				+ "> ;"
				+ "ssn:observedProperty <"
				+ property.stringValue()
				+ "> ;"
				+ "ssn:featureOfInterest <"
				+ feature.stringValue()
				+ "> ;"
				+ "ssn:observationResultTime [ time:inXSDDateTime ?time ] ;"
				+ "ssn:observationResult [ dul:hasRegion [ dul:hasRegionDataValue ?value ] ]"
				+ "]"
				+ "filter (?time >= \"2015-03-01T06:00:00.000+02:00\"^^xsd:dateTime "
				+ "&& ?time < \"2015-03-01T06:01:00.000+02:00\"^^xsd:dateTime)"
				+ "}" + "order by asc(?time)";

		Emrooz emrooz = new Emrooz(host);

		long ms1 = System.currentTimeMillis();

		List<BindingSet> results = emrooz.getSensorObservations(query);

		for (BindingSet result : results) {
			System.out.println(result.getValue("time") + " "
					+ result.getValue("value"));
		}

		long ms2 = System.currentTimeMillis();

		System.out.println("Result set size: " + results.size());
		System.out.println("Elapsed time: " + (ms2 - ms1) / 1000 + " s");

		emrooz.close();
	}

	private void load() {
		Random r = new Random();
		DateTime start = dtf.parseDateTime("2015-03-01T00:00:00.000+02:00");
		DateTime end = dtf.parseDateTime("2015-03-02T00:00:00.000+02:00");
		DateTime time = start;

		Emrooz emrooz = new Emrooz(host);

		emrooz.register(sensor, property, feature, "DAY");

		int numOfObservations = 0;
		int numOfStatements = 0;

		long ms1 = System.currentTimeMillis();

		while (time.isBefore(end)) {
			Set<Statement> statements = getObservation(sensor, property,
					feature, time, r.nextDouble());
			emrooz.addSensorObservation(statements);
			numOfObservations++;
			numOfStatements += statements.size();
			time = time.plusSeconds(1);
		}

		long ms2 = System.currentTimeMillis();

		emrooz.close();

		System.out.println("Number of observations: " + numOfObservations);
		System.out
				.println("Number of statements (triples): " + numOfStatements);
		System.out.println("Elapsed time: " + (ms2 - ms1) / 1000 + " s");
	}

	private Set<Statement> getObservation(URI sensor, URI property,
			URI feature, DateTime time, Double value) {
		Set<Statement> ret = new HashSet<Statement>();

		URI observationId = vf.createURI(ns + nextFragment++);
		URI resultTimeId = vf.createURI(ns + nextFragment++);
		URI outputId = vf.createURI(ns + nextFragment++);
		URI valueId = vf.createURI(ns + nextFragment++);

		ret.add(vf.createStatement(observationId, SSN.observedBy, sensor));
		ret.add(vf.createStatement(observationId, SSN.observedProperty,
				property));
		ret.add(vf.createStatement(observationId, SSN.featureOfInterest,
				feature));
		ret.add(vf.createStatement(observationId, SSN.observationResultTime,
				resultTimeId));
		ret.add(vf.createStatement(resultTimeId, Time.inXSDDateTime, vf
				.createLiteral(ISODateTimeFormat.dateTime().print(time),
						XMLSchema.DATETIME)));
		ret.add(vf.createStatement(observationId, SSN.observationResult,
				outputId));
		ret.add(vf.createStatement(outputId, DUL.hasRegion, valueId));
		ret.add(vf.createStatement(valueId, DUL.hasRegionDataValue,
				vf.createLiteral(value)));

		return ret;
	}

	public static void main(String[] args) {
		LoadTest app = new LoadTest();
		app.run();
	}

}
