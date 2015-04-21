/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.utils.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.Registration;
import fi.uef.envi.emrooz.Rollover;

/**
 * <p>
 * Title: RegistrationTest
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

public class RegistrationTest {

	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	private String ns = "http://example.org#";
	private URI s1 = vf.createURI(ns + "s1");
	private URI p1 = vf.createURI(ns + "p1");
	private URI f1 = vf.createURI(ns + "f1");
	private Rollover r1 = Rollover.MINUTE;
	private String id1 = DigestUtils.sha1Hex(s1.stringValue() + "-"
			+ p1.stringValue() + "-" + f1.stringValue());
	private URI s2 = vf.createURI(ns + "s2");
	private URI p2 = vf.createURI(ns + "p2");
	private URI f2 = vf.createURI(ns + "f2");
	private Rollover r2 = Rollover.HOUR;
	private String id2 = DigestUtils.sha1Hex(s2.stringValue() + "-"
			+ p2.stringValue() + "-" + f2.stringValue());

	@Test
	public void test1() {
		Registration r = new Registration(s1, p1, f1, r1);

		assertEquals(id1, r.getId());
	}

	@Test
	public void test2() {
		Registration r = new Registration(s1, p1, f1, r1);

		assertEquals(s1, r.getSensor());
	}

	@Test
	public void test3() {
		Registration r = new Registration(s1, p1, f1, r1);

		assertEquals(p1, r.getProperty());
	}

	@Test
	public void test4() {
		Registration r = new Registration(s1, p1, f1, r1);

		assertEquals(f1, r.getFeature());
	}

	@Test
	public void test5() {
		Registration r = new Registration(s1, p1, f1, r1);

		assertEquals(r1, r.getRollover());
	}

	@Test
	public void test6() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s1, p1, f1, r1);

		assertEquals(e, a);
	}

	@Test
	public void test7() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s1, p1, f1, r1);

		assertEquals(e.hashCode(), a.hashCode());
	}

	@Test
	public void test8() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s2, p1, f1, r1);

		assertNotEquals(e, a);
	}

	@Test
	public void test9() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s2, p1, f1, r1);

		assertNotEquals(e.hashCode(), a.hashCode());
	}

	@Test
	public void test10() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s1, p2, f1, r1);

		assertNotEquals(e, a);
	}

	@Test
	public void test11() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s1, p2, f1, r1);

		assertNotEquals(e.hashCode(), a.hashCode());
	}

	@Test
	public void test12() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s1, p1, f2, r1);

		assertNotEquals(e, a);
	}

	@Test
	public void test13() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s1, p1, f2, r1);

		assertNotEquals(e.hashCode(), a.hashCode());
	}

	@Test
	public void test14() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s1, p1, f1, r2);

		assertNotEquals(e, a);
	}

	@Test
	public void test15() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s1, p1, f1, r2);

		assertNotEquals(e.hashCode(), a.hashCode());
	}

	@Test
	public void test17() {
		Registration r = new Registration(s2, p2, f2, r2);

		assertEquals(id2, r.getId());
	}

	@Test
	public void test18() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s2, p1, f1, r1);

		assertNotEquals(e.getId(), a.getId());
	}

	@Test
	public void test19() {
		Registration e = new Registration(s1, p1, f1, r1);
		Registration a = new Registration(s1, p1, f1, r2);

		assertEquals(e.getId(), a.getId());
	}

	@Test
	public void test20() {
		Registration r = new Registration("http://example.org#s1",
				"http://example.org#p1", "http://example.org#f1", "MINUTE");

		assertEquals(id1, r.getId());
	}
}
