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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.TupleQueryResult;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.vocabulary.DUL;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: SimpleExample
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

public class SimpleExample {

	private static final String host = "localhost";
	private static final String from = "2015-03-13T20:00:00.000+02:00";
	private static final String to = "2015-03-13T21:00:00.000+02:00";
	private static final String ns = "http://example.org#";

	private static final ValueFactory vf = new ValueFactoryImpl();

	public static void main(String[] args) {
		Random r = new Random();
		DateTimeFormatter dtf = ISODateTimeFormat.dateTime().withOffsetParsed();

		Emrooz emrooz = new Emrooz(host);

		URI sensor = vf.createURI(ns + "s1");
		URI property = vf.createURI(ns + "p1");
		URI feature = vf.createURI(ns + "f1");

		// emrooz.register(sensor, property, feature, "DAY");

		// Set<Statement> observation = getObservation(sensor, property,
		// feature,
		// dtf.parseDateTime("2015-03-13T19:20:00.000+02:00"),
		// r.nextDouble());

		// emrooz.addSensorObservation(observation);

		// Set<Statement> out = emrooz.getSensorObservations(sensor, property,
		// feature, dtf.parseDateTime(from), dtf.parseDateTime(to));

		// for (Statement statement : out) {
		// System.out.println(statement);
		// }

		String query = "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
				+ "select ?time ?value "
				+ "where {"
				+ "["
				+ "ssn:observedBy <http://example.org#s1> ;"
				+ "ssn:observedProperty <http://example.org#p1> ;"
				+ "ssn:featureOfInterest <http://example.org#f1> ;"
				+ "ssn:observationResultTime [ time:inXSDDateTime ?time ] ;"
				+ "ssn:observationResult [ dul:hasRegion [ dul:hasRegionDataValue ?value ] ]"
				+ "]"
				+ "filter (?time >= \"2015-03-13T20:00:00.000+02:00\"^^xsd:dateTime "
				+ "&& ?time < \"2015-03-13T21:00:00.000+02:00\"^^xsd:dateTime)"
				+ "}"
				+ "order by asc(?time)";

		List<BindingSet> results = emrooz.getSensorObservations(query);

		for (BindingSet result : results) {
			System.out.println(result.getValue("time") + " "
					+ result.getValue("value"));
		}

		emrooz.close();
	}

	private static Set<Statement> getObservation(URI sensor, URI property,
			URI feature, DateTime time, Double value) {
		Set<Statement> ret = new HashSet<Statement>();

		URI observationId = vf.createURI(ns + UUID.randomUUID().toString());
		URI resultTimeId = vf.createURI(ns + UUID.randomUUID().toString());
		URI outputId = vf.createURI(ns + UUID.randomUUID().toString());
		URI valueId = vf.createURI(ns + UUID.randomUUID().toString());

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

}
