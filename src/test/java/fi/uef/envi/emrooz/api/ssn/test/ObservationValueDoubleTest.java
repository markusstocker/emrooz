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

import fi.uef.envi.emrooz.api.ssn.ObservationValue;
import fi.uef.envi.emrooz.api.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: ObservationValueDoubleTest
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

public class ObservationValueDoubleTest {

	private static final String ns = "http://example.org#";
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();

	@Test
	public void test1() {
		ObservationValue ov1 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.0);

		assertEquals(vf.createURI(ns + "ov1"), ov1.getId());
	}

	@Test
	public void test2() {
		ObservationValue ov1 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.0);

		assertEquals(SSN.ObservationValue, ov1.getType());
	}

	@Test
	public void test3() {
		ObservationValue ov1 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), vf.createURI(ns + "ObservationValue"), 0.0);

		assertEquals(vf.createURI(ns + "ObservationValue"), ov1.getType());
	}

	@Test
	public void test4() {
		ObservationValueDouble ov1 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.0);

		assertEquals(new Double(0.0), ov1.getValue());
	}

	@Test
	public void test5() {
		ObservationValueDouble ov1 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.0);
		ObservationValueDouble ov2 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.0);

		assertEquals(ov1, ov2);
	}

	@Test
	public void test6() {
		ObservationValueDouble ov1 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.0);
		ObservationValueDouble ov2 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.0);

		assertEquals(ov1.hashCode(), ov2.hashCode());
	}

	@Test
	public void test7() {
		ObservationValueDouble ov1 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.0);
		ObservationValueDouble ov2 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.1);

		assertNotEquals(ov1, ov2);
	}

	@Test
	public void test8() {
		ObservationValueDouble ov1 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.0);
		ObservationValueDouble ov2 = new ObservationValueDouble(vf.createURI(ns
				+ "ov1"), 0.1);

		assertNotEquals(ov1.hashCode(), ov2.hashCode());
	}

}
