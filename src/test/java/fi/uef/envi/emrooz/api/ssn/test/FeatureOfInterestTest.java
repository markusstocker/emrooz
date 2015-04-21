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

import fi.uef.envi.emrooz.api.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: FeatureOfInterestTest
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

public class FeatureOfInterestTest {

	private static final String ns = "http://example.org#";
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();

	@Test
	public void test1() {
		FeatureOfInterest f1 = new FeatureOfInterest(vf.createURI(ns + "f1"));

		assertEquals(vf.createURI(ns + "f1"), f1.getId());
	}

	@Test
	public void test2() {
		FeatureOfInterest f1 = new FeatureOfInterest(vf.createURI(ns + "f1"));

		assertEquals(SSN.FeatureOfInterest, f1.getType());
	}

	@Test
	public void test3() {
		FeatureOfInterest f1 = new FeatureOfInterest(vf.createURI(ns + "f1"),
				vf.createURI(ns + "FeatureOfInterest"));

		assertEquals(vf.createURI(ns + "FeatureOfInterest"), f1.getType());
	}

	@Test
	public void test4() {
		FeatureOfInterest f1 = new FeatureOfInterest(vf.createURI(ns + "f1"));
		FeatureOfInterest f2 = new FeatureOfInterest(vf.createURI(ns + "f1"));

		assertEquals(f1, f2);
	}

	@Test
	public void test5() {
		FeatureOfInterest f1 = new FeatureOfInterest(vf.createURI(ns + "f1"));
		FeatureOfInterest f2 = new FeatureOfInterest(vf.createURI(ns + "f1"));

		assertEquals(f1.hashCode(), f2.hashCode());
	}

	@Test
	public void test6() {
		FeatureOfInterest f1 = new FeatureOfInterest(vf.createURI(ns + "f1"));
		FeatureOfInterest f2 = new FeatureOfInterest(vf.createURI(ns + "f2"));

		assertNotEquals(f1, f2);
	}

	@Test
	public void test7() {
		FeatureOfInterest f1 = new FeatureOfInterest(vf.createURI(ns + "f1"));
		FeatureOfInterest f2 = new FeatureOfInterest(vf.createURI(ns + "f2"));

		assertNotEquals(f1.hashCode(), f2.hashCode());
	}

	@Test
	public void test8() {
		FeatureOfInterest f1 = new FeatureOfInterest(vf.createURI(ns + "f1"));
		FeatureOfInterest f2 = new FeatureOfInterest(vf.createURI(ns + "f2"),
				vf.createURI(ns + "FeatureOfInterest"));

		assertNotEquals(f1, f2);
	}

	@Test
	public void test9() {
		FeatureOfInterest f1 = new FeatureOfInterest(vf.createURI(ns + "f1"));
		FeatureOfInterest f2 = new FeatureOfInterest(vf.createURI(ns + "f2"),
				vf.createURI(ns + "FeatureOfInterest"));

		assertNotEquals(f1.hashCode(), f2.hashCode());
	}

}
