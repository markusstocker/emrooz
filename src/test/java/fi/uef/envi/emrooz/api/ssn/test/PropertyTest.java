/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api.ssn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.api.ssn.Property;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: PropertyTest
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

public class PropertyTest {

	private static final String ns = "http://example.org#";
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();

	@Test
	public void test1() {
		Property p1 = new Property(vf.createURI(ns + "p1"));

		assertEquals(vf.createURI(ns + "p1"), p1.getId());
	}

	@Test
	public void test2() {
		Property p1 = new Property(vf.createURI(ns + "p1"));

		assertEquals(SSN.Property, p1.getType());
	}

	@Test
	public void test3() {
		Property p1 = new Property(vf.createURI(ns + "p1"), vf.createURI(ns
				+ "Property"));

		assertEquals(vf.createURI(ns + "Property"), p1.getType());
	}

	@Test
	public void test4() {
		Property p1 = new Property(vf.createURI(ns + "p1"));
		Property p2 = new Property(vf.createURI(ns + "p1"));

		assertEquals(p1, p2);
	}

	@Test
	public void test5() {
		Property p1 = new Property(vf.createURI(ns + "p1"));
		Property p2 = new Property(vf.createURI(ns + "p1"));

		assertEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	public void test6() {
		Property p1 = new Property(vf.createURI(ns + "p1"));
		Property p2 = new Property(vf.createURI(ns + "p2"));

		assertNotEquals(p1, p2);
	}

	@Test
	public void test7() {
		Property p1 = new Property(vf.createURI(ns + "p1"));
		Property p2 = new Property(vf.createURI(ns + "p2"));

		assertNotEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	public void test8() {
		Property p1 = new Property(vf.createURI(ns + "p1"));
		Property p2 = new Property(vf.createURI(ns + "p2"), vf.createURI(ns
				+ "Property"));

		assertNotEquals(p1, p2);
	}

	@Test
	public void test9() {
		Property p1 = new Property(vf.createURI(ns + "p1"));
		Property p2 = new Property(vf.createURI(ns + "p2"), vf.createURI(ns
				+ "Property"));

		assertNotEquals(p1.hashCode(), p2.hashCode());
	}

}
