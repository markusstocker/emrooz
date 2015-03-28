/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.test.utils;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.utils.ConverterUtil;

/**
 * <p>
 * Title: ConverterUtilTest
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

public class ConverterUtilTest {

	String ns = "http://example.org#";
	ValueFactory vf = new ValueFactoryImpl();

	@Test
	public void test1() {
//		Set<Statement> s = new HashSet<Statement>();
//		s.add(vf.createStatement(vf.createURI(ns + "s1"),
//				vf.createURI(ns + "p1"), vf.createLiteral("t1")));
//
//		byte[] b = ConverterUtil.toByteArray(s);
//
//		Set<Statement> a = new HashSet<Statement>();
//
//		ConverterUtil.toStatements(b, a);
//
//		Set<Statement> e = new HashSet<Statement>();
//		e.add(vf.createStatement(vf.createURI(ns + "s1"),
//				vf.createURI(ns + "p1"), vf.createLiteral("t1")));
//
//		assertEquals(e, a);
	}

	@Test
	public void test2() {
//		Set<Statement> s = new HashSet<Statement>();
//		s.add(vf.createStatement(vf.createURI(ns + "s1"),
//				vf.createURI(ns + "p1"), vf.createLiteral("t2")));
//
//		byte[] b = ConverterUtil.toByteArray(s);
//
//		Set<Statement> a = new HashSet<Statement>();
//
//		ConverterUtil.toStatements(b, a);
//
//		Set<Statement> e = new HashSet<Statement>();
//		e.add(vf.createStatement(vf.createURI(ns + "s1"),
//				vf.createURI(ns + "p1"), vf.createLiteral("t1")));
//
//		assertNotEquals(e, a);
	}

}
