/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api.ssn.test;

import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.api.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.api.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.api.ssn.Property;
import fi.uef.envi.emrooz.api.ssn.Sensor;
import fi.uef.envi.emrooz.api.ssn.SensorObservation;
import fi.uef.envi.emrooz.api.ssn.SensorOutput;
import fi.uef.envi.emrooz.api.time.Instant;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: SensorObservationTest
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

public class SensorObservationTest {

	private final static String ns = "http://example.org#";
	private final static ValueFactory vf = ValueFactoryImpl.getInstance();

	@Test
	public void test1() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")));

		assertEquals(vf.createURI(ns + "o1"), o1.getId());
	}

	@Test
	public void test2() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")));

		assertEquals(SSN.Observation, o1.getType());
	}

	@Test
	public void test3() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")));

		assertEquals(new Sensor(vf.createURI(ns + "s1")), o1.getSensor());
	}

	@Test
	public void test4() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")));

		assertEquals(new Property(vf.createURI(ns + "p1")), o1.getProperty());
	}

	@Test
	public void test5() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")));

		assertEquals(new FeatureOfInterest(vf.createURI(ns + "f1")),
				o1.getFeatureOfInterest());
	}

	@Test
	public void test6() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.5)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));

		assertEquals(new SensorOutput(vf.createURI(ns + "so1"),
				new ObservationValueDouble(vf.createURI(ns + "ov1"), 0.5)),
				o1.getObservationResult());
	}

	@Test
	public void test7() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.5)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));

		assertEquals(new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
				.dateTime().parseDateTime("2015-04-21T00:00:00.000+03:00")),
				o1.getObservationResultTime());
	}

	@Test
	public void test8() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.5)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));
		SensorObservation o2 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.5)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));

		assertEquals(o1, o2);
	}

	@Test
	public void test9() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.5)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));
		SensorObservation o2 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.0)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));

		assertNotEquals(o1, o2);
	}
	
	@Test
	public void test10() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.5)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));
		SensorObservation o2 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.5)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));

		assertEquals(o1.hashCode(), o2.hashCode());
	}

	@Test
	public void test11() {
		SensorObservation o1 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.5)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));
		SensorObservation o2 = new SensorObservation(vf.createURI(ns + "o1"),
				SSN.Observation, new Sensor(vf.createURI(ns + "s1")),
				new Property(vf.createURI(ns + "p1")), new FeatureOfInterest(
						vf.createURI(ns + "f1")), new SensorOutput(
						vf.createURI(ns + "so1"), new ObservationValueDouble(
								vf.createURI(ns + "ov1"), 0.0)),
				new Instant(vf.createURI(ns + "t1"), ISODateTimeFormat
						.dateTime().parseDateTime(
								"2015-04-21T00:00:00.000+03:00")));

		assertNotEquals(o1.hashCode(), o2.hashCode());
	}

}
