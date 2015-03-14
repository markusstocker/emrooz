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
	private static final String key = "1-1-1-20150313";
	private static final String time = "2015-03-13T19:30:00.000+02:00";
	private static final String from = "2015-03-13T19:00:00.000+02:00";
	private static final String to = "2015-03-13T20:00:00.000+02:00";
	private static final String ns = "http://example.org#";

	public static void main(String[] args) {
		ValueFactory vf = new ValueFactoryImpl();
		DateTimeFormatter dtf = ISODateTimeFormat.dateTime().withOffsetParsed();

		Emrooz emrooz = new Emrooz(host);
		emrooz.connect();

		Set<Statement> in = new HashSet<Statement>();
		in.add(vf.createStatement(vf.createURI(ns + "s1"),
				vf.createURI(ns + "p1"), vf.createLiteral(1.0)));
		in.add(vf.createStatement(vf.createURI(ns + "s1"),
				vf.createURI(ns + "p2"), vf.createLiteral("test")));

		emrooz.addSensorObservation(key, dtf.parseDateTime(time), in);

		Set<Statement> out = emrooz.getSensorObservations(key,
				dtf.parseDateTime(from), dtf.parseDateTime(to));

		for (Statement statement : out) {
			System.out.println(statement);
		}

		emrooz.close();
	}

}
