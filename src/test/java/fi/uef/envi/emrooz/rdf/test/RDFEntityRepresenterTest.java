/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.rdf.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;

import fi.uef.envi.emrooz.api.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.api.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.api.ssn.Property;
import fi.uef.envi.emrooz.api.ssn.Sensor;
import fi.uef.envi.emrooz.api.ssn.SensorObservation;
import fi.uef.envi.emrooz.api.ssn.SensorOutput;
import fi.uef.envi.emrooz.api.time.Instant;
import fi.uef.envi.emrooz.rdf.RDFEntityRepresenter;
import fi.uef.envi.emrooz.vocabulary.DUL;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: RDFEntityRepresenterTest
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

public class RDFEntityRepresenterTest {

	private RDFEntityRepresenter representer;
	private final static String ns = "http://example.org#";
	private final static ValueFactory vf = ValueFactoryImpl.getInstance();
	private final static DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();

	public RDFEntityRepresenterTest() {
		representer = new RDFEntityRepresenter();
	}

	@Test
	public void test1() {
		Set<Statement> a = representer.createRepresentation(new Instant(vf
				.createURI(ns + "t1"), dtf
				.parseDateTime("2015-04-21T17:00:00.000+03:00")));

		Set<Statement> e = new HashSet<Statement>();
		e.add(_statement(vf.createURI(ns + "t1"), RDF.TYPE, Time.Instant));
		e.add(_statement(vf.createURI(ns + "t1"), Time.inXSDDateTime, vf
				.createLiteral("2015-04-21T17:00:00.000+03:00",
						XMLSchema.DATETIME)));

		assertEquals(e, a);
	}

	@Test
	public void test2() {
		Set<Statement> a = representer.createRepresentation(new Instant(vf
				.createURI(ns + "t1"), dtf
				.parseDateTime("2015-04-21T17:00:00.000+03:00")));

		Set<Statement> e = new HashSet<Statement>();
		e.add(_statement(vf.createURI(ns + "t1"), RDF.TYPE, Time.Instant));
		e.add(_statement(vf.createURI(ns + "t1"), Time.inXSDDateTime, vf
				.createLiteral("2015-04-21T17:01:00.000+03:00",
						XMLSchema.DATETIME)));

		assertNotEquals(e, a);
	}

	@Test
	public void test3() {
		Set<Statement> a = representer
				.createRepresentation(new ObservationValueDouble(vf
						.createURI(ns + "ov1"), 0.0));

		Set<Statement> e = new HashSet<Statement>();
		e.add(_statement(vf.createURI(ns + "ov1"), RDF.TYPE,
				SSN.ObservationValue));
		e.add(_statement(vf.createURI(ns + "ov1"), DUL.hasRegionDataValue,
				vf.createLiteral(0.0)));

		assertEquals(e, a);
	}

	@Test
	public void test4() {
		Set<Statement> a = representer
				.createRepresentation(new SensorObservation(
						vf.createURI(ns + "o1"),
						new Sensor(vf.createURI(ns + "s1")),
						new Property(vf.createURI(ns + "p1")),
						new FeatureOfInterest(vf.createURI(ns + "f1")),
						new SensorOutput(vf.createURI(ns + "so1"),
								new ObservationValueDouble(vf.createURI(ns
										+ "ov1"), 0.0)),
						new Instant(vf.createURI(ns + "t1"), dtf
								.parseDateTime("2015-04-21T18:00:00.000+03:00"))));

		Set<Statement> e = new HashSet<Statement>();
		e.add(_statement(vf.createURI(ns + "o1"), RDF.TYPE, SSN.Observation));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observedBy,
				vf.createURI(ns + "s1")));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observedProperty,
				vf.createURI(ns + "p1")));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.featureOfInterest,
				vf.createURI(ns + "f1")));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observationResult,
				vf.createURI(ns + "so1")));
		e.add(_statement(vf.createURI(ns + "so1"), RDF.TYPE, SSN.SensorOutput));
		e.add(_statement(vf.createURI(ns + "so1"), SSN.hasValue,
				vf.createURI(ns + "ov1")));
		e.add(_statement(vf.createURI(ns + "ov1"), RDF.TYPE,
				SSN.ObservationValue));
		e.add(_statement(vf.createURI(ns + "ov1"), DUL.hasRegionDataValue,
				vf.createLiteral(0.0)));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observationResultTime,
				vf.createURI(ns + "t1")));
		e.add(_statement(vf.createURI(ns + "t1"), RDF.TYPE, Time.Instant));
		e.add(_statement(vf.createURI(ns + "t1"), Time.inXSDDateTime, vf
				.createLiteral("2015-04-21T18:00:00.000+03:00",
						XMLSchema.DATETIME)));

		assertEquals(e, a);
	}
	
	@Test
	public void test5() {
		Set<Statement> a = representer
				.createRepresentation(new SensorObservation(
						vf.createURI(ns + "o1"),
						new Sensor(vf.createURI(ns + "s1")),
						new Property(vf.createURI(ns + "p1")),
						new FeatureOfInterest(vf.createURI(ns + "f1")),
						new SensorOutput(vf.createURI(ns + "so1"),
								new ObservationValueDouble(vf.createURI(ns
										+ "ov1"), 0.0)),
						new Instant(vf.createURI(ns + "t1"), dtf
								.parseDateTime("2015-04-21T18:00:00.000+03:00"))));

		Set<Statement> e = new HashSet<Statement>();
		e.add(_statement(vf.createURI(ns + "o1"), RDF.TYPE, SSN.Observation));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observedBy,
				vf.createURI(ns + "s1")));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observedProperty,
				vf.createURI(ns + "p1")));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.featureOfInterest,
				vf.createURI(ns + "f1")));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observationResult,
				vf.createURI(ns + "so1")));
		e.add(_statement(vf.createURI(ns + "so1"), RDF.TYPE, SSN.SensorOutput));
		e.add(_statement(vf.createURI(ns + "so1"), SSN.hasValue,
				vf.createURI(ns + "ov1")));
		e.add(_statement(vf.createURI(ns + "ov1"), RDF.TYPE,
				SSN.ObservationValue));
		e.add(_statement(vf.createURI(ns + "ov1"), DUL.hasRegionDataValue,
				vf.createLiteral(0.1)));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observationResultTime,
				vf.createURI(ns + "t1")));
		e.add(_statement(vf.createURI(ns + "t1"), RDF.TYPE, Time.Instant));
		e.add(_statement(vf.createURI(ns + "t1"), Time.inXSDDateTime, vf
				.createLiteral("2015-04-21T18:00:00.000+03:00",
						XMLSchema.DATETIME)));

		assertNotEquals(e, a);
	}
	
	@Test
	public void test6() {
		Set<Statement> a = representer
				.createRepresentation(new SensorObservation(
						vf.createURI(ns + "o1"),
						new Sensor(vf.createURI(ns + "s1")),
						new Property(vf.createURI(ns + "p1")),
						new FeatureOfInterest(vf.createURI(ns + "f1")),
						new SensorOutput(vf.createURI(ns + "so1"),
								new ObservationValueDouble(vf.createURI(ns
										+ "ov1"), 0.0)),
						new Instant(vf.createURI(ns + "t1"), dtf
								.parseDateTime("2015-04-21T18:00:00.000+03:00"))));

		Set<Statement> e = new HashSet<Statement>();
		e.add(_statement(vf.createURI(ns + "o1"), RDF.TYPE, SSN.Observation));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observedBy,
				vf.createURI(ns + "s1")));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observedProperty,
				vf.createURI(ns + "p1")));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.featureOfInterest,
				vf.createURI(ns + "f1")));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observationResult,
				vf.createURI(ns + "so1")));
		e.add(_statement(vf.createURI(ns + "so1"), RDF.TYPE, SSN.SensorOutput));
		e.add(_statement(vf.createURI(ns + "so1"), SSN.hasValue,
				vf.createURI(ns + "ov1")));
		e.add(_statement(vf.createURI(ns + "ov1"), RDF.TYPE,
				SSN.ObservationValue));
		e.add(_statement(vf.createURI(ns + "ov1"), DUL.hasRegionDataValue,
				vf.createLiteral(0.0)));
		e.add(_statement(vf.createURI(ns + "o1"), SSN.observationResultTime,
				vf.createURI(ns + "t1")));
		e.add(_statement(vf.createURI(ns + "t1"), RDF.TYPE, Time.Instant));
		e.add(_statement(vf.createURI(ns + "t1"), Time.inXSDDateTime, vf
				.createLiteral("2015-04-21T18:01:00.000+03:00",
						XMLSchema.DATETIME)));

		assertNotEquals(e, a);
	}

	private Statement _statement(URI s, URI p, Value o) {
		return vf.createStatement(s, p, o);
	}

}
