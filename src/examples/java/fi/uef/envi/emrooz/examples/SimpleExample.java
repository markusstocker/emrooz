/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.Emrooz;

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
	private static final String time = "2015-03-13T19:45:00.000+02:00";
	private static final String from = "2015-03-13T19:35:00.000+02:00";
	private static final String to = "2015-03-13T20:00:00.000+02:00";
	private static final String ns = "http://example.org#";

	public static void main(String[] args) {
		ValueFactory vf = new ValueFactoryImpl();
		DateTimeFormatter dtf = ISODateTimeFormat.dateTime().withOffsetParsed();

		Emrooz emrooz = new Emrooz(host);

		Set<Statement> in = new HashSet<Statement>();
		in.add(vf.createStatement(vf.createURI(ns + "s3"),
				vf.createURI(ns + "p1"), vf.createLiteral(1.0)));
		in.add(vf.createStatement(vf.createURI(ns + "s3"),
				vf.createURI(ns + "p2"), vf.createLiteral("test")));

		URI sensor = vf.createURI(ns + "s3");
		URI property = vf.createURI(ns + "p3");
		URI feature = vf.createURI(ns + "f3");

//		emrooz.register(sensor, property, feature, "DAY");

//		emrooz.addSensorObservation(sensor, property, feature,
//				dtf.parseDateTime(time), in);

		Set<Statement> out = emrooz.getSensorObservations(sensor, property, feature,
				dtf.parseDateTime(from), dtf.parseDateTime(to));

		for (Statement statement : out) {
			System.out.println(statement);
		}

		emrooz.close();
	}

}
