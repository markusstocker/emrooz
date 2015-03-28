/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.test.utils;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import fi.uef.envi.emrooz.SensorObservationExtractor;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: SensorObservationExtractorTest
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

public class SensorObservationExtractorTest {

	private final ValueFactory vf = ValueFactoryImpl.getInstance();
	private final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();

	private final String ns = "http://example.org#";

	private final URI o1 = vf.createURI(ns + "o1");
	private final URI s1 = vf.createURI(ns + "s1");
	private final URI p1 = vf.createURI(ns + "p1");
	private final URI f1 = vf.createURI(ns + "f1");
	private final DateTime t1 = dtf
			.parseDateTime("2015-02-28T01:01:01.001+02:00");

	@Test
	public void test1() {
		Set<Statement> s = new HashSet<Statement>();
		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertNull(ex.getSensor());
	}

	@Test
	public void test2() {
		Set<Statement> s = new HashSet<Statement>();
		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertNull(ex.getProperty());
	}

	@Test
	public void test3() {
		Set<Statement> s = new HashSet<Statement>();
		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertNull(ex.getFeature());
	}

	@Test
	public void test4() {
		Set<Statement> s = new HashSet<Statement>();
		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertNull(ex.getResultTime());
	}

	@Test
	public void test5() {
		Set<Statement> s = new HashSet<Statement>();
		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertEquals(s, ex.getStatements());
	}

	@Test
	public void test6() {
		Set<Statement> s = new HashSet<Statement>();
		s.add(vf.createStatement(o1, SSN.observedBy, s1));

		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertEquals(s1, ex.getSensor());
	}

	@Test
	public void test7() {
		Set<Statement> s = new HashSet<Statement>();
		s.add(vf.createStatement(o1, SSN.observedProperty, p1));

		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertEquals(p1, ex.getProperty());
	}
	
	@Test
	public void test8() {
		Set<Statement> s = new HashSet<Statement>();
		s.add(vf.createStatement(o1, SSN.featureOfInterest, f1));

		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertEquals(f1, ex.getFeature());
	}

	@Test
	public void test9() {
		Set<Statement> s = new HashSet<Statement>();
		URI rt1 = vf.createURI(ns + "rt1");
		s.add(vf.createStatement(o1, SSN.observationResultTime, rt1));
		s.add(vf.createStatement(rt1, Time.inXSDDateTime,
				vf.createLiteral(dtf.print(t1), XMLSchema.DATETIME)));

		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertEquals(t1, ex.getResultTime());
	}
	
	@Test
	public void test10() {
		Set<Statement> s = new HashSet<Statement>();
		s.add(vf.createStatement(o1, SSN.observedBy, s1));
		s.add(vf.createStatement(o1, SSN.observedProperty, p1));

		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertEquals(s1, ex.getSensor());
		assertEquals(p1, ex.getProperty());
	}
	
	@Test
	public void test11() {
		Set<Statement> s = new HashSet<Statement>();
		s.add(vf.createStatement(o1, SSN.observedBy, s1));
		s.add(vf.createStatement(o1, SSN.observedProperty, p1));
		s.add(vf.createStatement(o1, SSN.featureOfInterest, f1));
		URI rt1 = vf.createURI(ns + "rt1");
		s.add(vf.createStatement(o1, SSN.observationResultTime, rt1));
		s.add(vf.createStatement(rt1, Time.inXSDDateTime,
				vf.createLiteral(dtf.print(t1), XMLSchema.DATETIME)));

		SensorObservationExtractor ex = new SensorObservationExtractor(s);

		assertEquals(s1, ex.getSensor());
		assertEquals(p1, ex.getProperty());
		assertEquals(f1, ex.getFeature());
		assertEquals(t1, ex.getResultTime());
	}

}
