/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;

import fi.uef.envi.emrooz.Emrooz;

/**
 * <p>
 * Title: VariableFeatureExample
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

public class VariableFeatureExample {

	ValueFactory vf = new ValueFactoryImpl();
	DateTimeFormatter dtf = ISODateTimeFormat.dateTime().withOffsetParsed();

	String queryFrom = "2015-03-13T18:00:00.000+02:00";
	String queryTo = "2015-03-13T18:03:00.000+02:00";

	String ns = "http://example.org";

	void run() {
		Emrooz emrooz = new Emrooz();

		String query = "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
				+ "select ?time ?value "
				+ "where {"
				+ "?o1 ssn:observedBy <http://example.org#s1> ."
				+ "?o1 ssn:observedProperty <http://example.org#p1> ."
				+ "?o1 ssn:featureOfInterest ?f1 ."
				+ "?f1 <http://example.org#a1> \"a1\" ."
				+ "?o1 ssn:observationResultTime [ time:inXSDDateTime ?time ] ."
				+ "?o1 ssn:observationResult [ dul:hasRegion [ dul:hasRegionDataValue ?value ] ] ."
				+ "filter (?time >= \"2015-03-13T18:00:00.000+02:00\"^^xsd:dateTime "
				+ "&& ?time < \"2015-03-13T18:03:00.000+02:00\"^^xsd:dateTime)"
				+ "}" + "order by asc(?time)";

		Set<Statement> statements = new HashSet<Statement>();
		statements.add(_statement(_uri("f1"), _uri("a1"),
				_literal("a1", XMLSchema.STRING)));

		List<BindingSet> results = emrooz.getSensorObservations(query, statements);

		for (BindingSet result : results) {
			System.out.println(result.getValue("time") + " "
					+ result.getValue("value"));
		}

		emrooz.close();
	}

	Statement _statement(URI s, URI p, Value o) {
		return vf.createStatement(s, p, o);
	}

	URI _uri(String fragment) {
		return _uri(ns, fragment);
	}

	URI _uri(String ns, String fragment) {
		return vf.createURI(ns + "#" + fragment);
	}

	Literal _literal(String value, URI type) {
		return vf.createLiteral(value, type);
	}

	public static void main(String[] args) {
		VariableFeatureExample app = new VariableFeatureExample();
		app.run();
	}

}
