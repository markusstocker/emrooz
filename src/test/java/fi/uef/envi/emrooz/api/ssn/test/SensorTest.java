/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api.ssn.test;

import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.api.ssn.Sensor;
import fi.uef.envi.emrooz.vocabulary.SSN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * <p>
 * Title: SensorTest
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

public class SensorTest {

	private static final String ns = "http://example.org#";
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();

	@Test
	public void test1() {
		Sensor s1 = new Sensor(vf.createURI(ns + "s1"));

		assertEquals(vf.createURI(ns + "s1"), s1.getId());
	}

	@Test
	public void test2() {
		Sensor s1 = new Sensor(vf.createURI(ns + "s1"));

		assertEquals(SSN.Sensor, s1.getType());
	}

	@Test
	public void test3() {
		Sensor s1 = new Sensor(vf.createURI(ns + "s1"), vf.createURI(ns
				+ "Sensor"));

		assertEquals(vf.createURI(ns + "Sensor"), s1.getType());
	}

	@Test
	public void test4() {
		Sensor s1 = new Sensor(vf.createURI(ns + "s1"));
		Sensor s2 = new Sensor(vf.createURI(ns + "s1"));

		assertEquals(s1, s2);
	}

	@Test
	public void test5() {
		Sensor s1 = new Sensor(vf.createURI(ns + "s1"));
		Sensor s2 = new Sensor(vf.createURI(ns + "s1"));

		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test
	public void test6() {
		Sensor s1 = new Sensor(vf.createURI(ns + "s1"));
		Sensor s2 = new Sensor(vf.createURI(ns + "s2"));

		assertNotEquals(s1, s2);
	}

	@Test
	public void test7() {
		Sensor s1 = new Sensor(vf.createURI(ns + "s1"));
		Sensor s2 = new Sensor(vf.createURI(ns + "s2"));

		assertNotEquals(s1.hashCode(), s2.hashCode());
	}

	@Test
	public void test8() {
		Sensor s1 = new Sensor(vf.createURI(ns + "s1"));
		Sensor s2 = new Sensor(vf.createURI(ns + "s2"), vf.createURI(ns
				+ "Sensor"));

		assertNotEquals(s1, s2);
	}

	@Test
	public void test9() {
		Sensor s1 = new Sensor(vf.createURI(ns + "s1"));
		Sensor s2 = new Sensor(vf.createURI(ns + "s2"), vf.createURI(ns
				+ "Sensor"));

		assertNotEquals(s1.hashCode(), s2.hashCode());
	}
}
