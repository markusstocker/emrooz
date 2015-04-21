/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api.ssn.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.api.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.api.ssn.SensorOutput;

/**
 * <p>
 * Title: SensorOutputTest
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

public class SensorOutputTest {

	private static final String ns = "http://example.org#";
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();

	@Test
	public void test1() {
		SensorOutput o1 = new SensorOutput(vf.createURI(ns + "o1"));

		assertEquals(vf.createURI(ns + "o1"), o1.getId());
	}

	@Test
	public void test2() {
		SensorOutput o1 = new SensorOutput(vf.createURI(ns + "o1"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));

		assertEquals(new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0),
				o1.getValue());
	}

	@Test
	public void test3() {
		SensorOutput o1 = new SensorOutput(vf.createURI(ns + "o1"),
				vf.createURI(ns + "ObservationValue"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));

		assertEquals(vf.createURI(ns + "ObservationValue"), o1.getType());
	}

	@Test
	public void test4() {
		SensorOutput o1 = new SensorOutput(vf.createURI(ns + "o1"));
		o1.setValue(new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));

		assertEquals(new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0),
				o1.getValue());
	}

	@Test
	public void test5() {
		SensorOutput o1 = new SensorOutput(vf.createURI(ns + "o1"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));
		SensorOutput o2 = new SensorOutput(vf.createURI(ns + "o1"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));

		assertEquals(o1, o2);
	}

	@Test
	public void test6() {
		SensorOutput o1 = new SensorOutput(vf.createURI(ns + "o1"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));
		SensorOutput o2 = new SensorOutput(vf.createURI(ns + "o1"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));

		assertEquals(o1.hashCode(), o2.hashCode());
	}
	
	@Test
	public void test7() {
		SensorOutput o1 = new SensorOutput(vf.createURI(ns + "o1"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));
		SensorOutput o2 = new SensorOutput(vf.createURI(ns + "o2"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));

		assertNotEquals(o1, o2);
	}

	@Test
	public void test8() {
		SensorOutput o1 = new SensorOutput(vf.createURI(ns + "o1"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));
		SensorOutput o2 = new SensorOutput(vf.createURI(ns + "o2"),
				new ObservationValueDouble(vf.createURI(ns + "v1"), 0.0));

		assertNotEquals(o1.hashCode(), o2.hashCode());
	}

}
