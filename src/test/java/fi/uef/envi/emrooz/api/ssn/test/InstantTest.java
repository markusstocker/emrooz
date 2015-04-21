/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api.ssn.test;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.api.time.Instant;
import fi.uef.envi.emrooz.vocabulary.Time;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * <p>
 * Title: InstantTest
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

public class InstantTest {

	private static final String ns = "http://example.org#";
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	private static final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();

	@Test
	public void test1() {
		Instant i1 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));

		assertEquals(vf.createURI(ns + "i1"), i1.getId());
	}

	@Test
	public void test2() {
		Instant i1 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));

		assertEquals(Time.Instant, i1.getType());
	}

	@Test
	public void test3() {
		Instant i1 = new Instant(vf.createURI(ns + "i1"), vf.createURI(ns
				+ "Instant"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));

		assertEquals(vf.createURI(ns + "Instant"), i1.getType());
	}

	@Test
	public void test4() {
		Instant i1 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));

		assertEquals(dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"),
				i1.getValue());
	}

	@Test
	public void test5() {
		Instant i1 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));
		Instant i2 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));

		assertEquals(i1, i2);
	}

	@Test
	public void test6() {
		Instant i1 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));
		Instant i2 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));

		assertEquals(i1.hashCode(), i2.hashCode());
	}

	@Test
	public void test7() {
		Instant i1 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));
		Instant i2 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:01:00.000+03:00"));

		assertNotEquals(i1, i2);
	}

	@Test
	public void test8() {
		Instant i1 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:00:00.000+03:00"));
		Instant i2 = new Instant(vf.createURI(ns + "i1"),
				dtf.parseDateTime("2015-04-21T14:01:00.000+03:00"));

		assertNotEquals(i1.hashCode(), i2.hashCode());
	}

}
