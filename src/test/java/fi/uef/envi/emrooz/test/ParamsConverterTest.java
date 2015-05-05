/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.ConvertParam;
import junitparams.converters.ParamConverter;

/**
 * <p>
 * Title: ParamsConverterTest
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

@RunWith(JUnitParamsRunner.class)
public class ParamsConverterTest {

	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	private static final DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();
	private static final Logger log = Logger
			.getLogger(ParamsConverterTest.class.getName());

	@Test
	@Parameters({ "http://example.org#s1" })
	public void testEqualsStringToURI(
			@ConvertParam(value = StringToURIConverter.class) URI actual) {
		assertEquals(vf.createURI("http://example.org#s1"), actual);
	}

	@Test
	@Parameters({ "http://example.org#s1" })
	public void testNotEqualsStringToURI(
			@ConvertParam(value = StringToURIConverter.class) URI actual) {
		assertNotEquals(vf.createURI("http://example.org#s2"), actual);
	}

	@Test
	@Parameters({ "" })
	public void testEqualsEmptyToURI(
			@ConvertParam(value = StringToURIConverter.class) URI actual) {
		assertEquals(null, actual);
	}

	@Test
	@Parameters({ "0.0" })
	public void testEqualsStringToDouble(
			@ConvertParam(value = StringToDoubleConverter.class) Double actual) {
		assertEquals(Double.valueOf("0.0"), actual);
	}

	@Test
	@Parameters({ "0.0" })
	public void testNotEqualsStringToDouble(
			@ConvertParam(value = StringToDoubleConverter.class) Double actual) {
		assertNotEquals(Double.valueOf("0.5"), actual);
	}

	@Test
	@Parameters({ "" })
	public void testEqualsEmptyToDouble(
			@ConvertParam(value = StringToDoubleConverter.class) Double actual) {
		assertEquals(Double.valueOf("NaN"), actual);
	}

	@Test
	@Parameters({ "2015-04-05T00:00:00.000+03:00" })
	public void testEqualsStringToDateTime(
			@ConvertParam(value = StringToDateTimeConverter.class) DateTime actual) {
		assertEquals(dtf.parseDateTime("2015-04-05T00:00:00.000+03:00"), actual);
	}

	@Test
	@Parameters({ "2015-04-05T00:00:00.000+03:00" })
	public void testNotEqualsStringToDateTime(
			@ConvertParam(value = StringToDateTimeConverter.class) DateTime actual) {
		assertNotEquals(dtf.parseDateTime("2015-04-05T01:00:00.000+03:00"),
				actual);
	}

	@Test
	@Parameters({ "" })
	public void testEqualsEmptyToDateTime(
			@ConvertParam(value = StringToDateTimeConverter.class) DateTime actual) {
		assertEquals(null, actual);
	}

	@Test
	@Parameters({ "(http://example.org#s1 http://example.org#p1 http://example.org#o1);(http://example.org#s2 http://example.org#p2 http://example.org#o2)" })
	public void testEqualsStringToStatements1(
			@ConvertParam(value = StringToStatementsConverter.class) Set<Statement> actual) {
		Set<Statement> expected = new HashSet<Statement>();
		expected.add(vf.createStatement(vf.createURI("http://example.org#s1"),
				vf.createURI("http://example.org#p1"),
				vf.createURI("http://example.org#o1")));
		expected.add(vf.createStatement(vf.createURI("http://example.org#s2"),
				vf.createURI("http://example.org#p2"),
				vf.createURI("http://example.org#o2")));

		assertEquals(expected, actual);
	}

	@Test
	@Parameters({ "(http://example.org#s1 http://example.org#p1 \"0.0\"^^<http://www.w3.org/2001/XMLSchema#double>)" })
	public void testEqualsStringToStatements2(
			@ConvertParam(value = StringToStatementsConverter.class) Set<Statement> actual) {
		Set<Statement> expected = new HashSet<Statement>();
		expected.add(vf.createStatement(vf.createURI("http://example.org#s1"),
				vf.createURI("http://example.org#p1"),
				vf.createLiteral("0.0", XMLSchema.DOUBLE)));

		assertEquals(expected, actual);
	}

	@Test
	@Parameters({ "(http://example.org#s1 http://example.org#p1 \"2015-04-21T17:01:00.000+03:00\"^^<http://www.w3.org/2001/XMLSchema#dateTime>)" })
	public void testEqualsStringToStatements3(
			@ConvertParam(value = StringToStatementsConverter.class) Set<Statement> actual) {
		Set<Statement> expected = new HashSet<Statement>();
		expected.add(vf.createStatement(vf.createURI("http://example.org#s1"),
				vf.createURI("http://example.org#p1"), vf.createLiteral(
						"2015-04-21T17:01:00.000+03:00", XMLSchema.DATETIME)));

		assertEquals(expected, actual);
	}

	@Test
	@Parameters({ "(http://example.org#s1 http://example.org#p1 http://example.org#o1);(http://example.org#s2 http://example.org#p2 http://example.org#o2)" })
	public void testNotEqualsStringToStatements1(
			@ConvertParam(value = StringToStatementsConverter.class) Set<Statement> actual) {
		Set<Statement> expected = new HashSet<Statement>();
		expected.add(vf.createStatement(vf.createURI("http://example.org#s1"),
				vf.createURI("http://example.org#p1"),
				vf.createURI("http://example.org#o1")));
		expected.add(vf.createStatement(vf.createURI("http://example.org#s3"),
				vf.createURI("http://example.org#p2"),
				vf.createURI("http://example.org#o2")));

		assertNotEquals(expected, actual);
	}

	@Test
	@Parameters({ "(http://example.org#s1 http://example.org#p1 \"2015-04-21T17:01:00.000+03:00\"^^<http://www.w3.org/2001/XMLSchema#dateTime>)" })
	public void testNotEqualsStringToStatements3(
			@ConvertParam(value = StringToStatementsConverter.class) Set<Statement> actual) {
		Set<Statement> expected = new HashSet<Statement>();
		expected.add(vf.createStatement(vf.createURI("http://example.org#s1"),
				vf.createURI("http://example.org#p1"), vf.createLiteral(
						"2015-04-21T17:00:00.000+03:00", XMLSchema.DATETIME)));

		assertNotEquals(expected, actual);
	}

	public static class StringToURIConverter implements ParamConverter<URI> {
		public URI convert(Object param, String options) {
			String value = param.toString();

			if (value.isEmpty())
				return null;

			return vf.createURI(value);
		}

	}

	public static class StringToDoubleConverter implements
			ParamConverter<Double> {
		public Double convert(Object param, String options) {
			String value = param.toString();

			if (value.isEmpty())
				return Double.NaN;

			return Double.valueOf(value);
		}
	}

	public static class StringToDateTimeConverter implements
			ParamConverter<DateTime> {
		public DateTime convert(Object param, String options) {
			String value = param.toString();

			if (value.isEmpty())
				return null;

			return dtf.parseDateTime(value);
		}
	}

	public static class StringToStatementsConverter implements
			ParamConverter<Set<Statement>> {
		public Set<Statement> convert(Object param, String options) {
			String value = param.toString();

			if (value.isEmpty())
				return Collections.emptySet();

			Set<Statement> ret = new HashSet<Statement>();

			String label;
			String datatype;
			String[] statements = value.split(";");

			for (String statement : statements) {
				if (statement.startsWith("(") && statement.endsWith(")")) {
					statement = statement.substring(1, statement.length() - 1);
				} else {
					if (log.isLoggable(Level.WARNING))
						log.warning("Statement should start and end with rounded parenthesis [statement = "
								+ statement
								+ "; statements = "
								+ statements
								+ "]");
					continue;
				}

				String[] resources = statement.split(" ");
				int length = resources.length;

				if (length != 3) {
					if (log.isLoggable(Level.WARNING))
						log.warning("Expected three resources [resources.length = "
								+ length
								+ "; statement = "
								+ statement
								+ "; statements = " + statements + "]");
					continue;
				}

				Resource s = vf.createURI(resources[0]);
				URI p = vf.createURI(resources[1]);

				Value o = null;
				String v = resources[2];

				if (v.contains("^^")) {
					label = v.substring(v.indexOf("\"") + 1,
							v.lastIndexOf("\""));
					datatype = v.substring(v.indexOf("<") + 1,
							v.lastIndexOf(">"));

					if (datatype.equals(XMLSchema.DATETIME.stringValue())) {
						o = vf.createLiteral(label, XMLSchema.DATETIME);
					} else if (datatype.equals(XMLSchema.DOUBLE.stringValue())) {
						o = vf.createLiteral(label, XMLSchema.DOUBLE);
					} else {
						if (log.isLoggable(Level.WARNING)) {
							log.warning("Unrecognized datatype [datatype = "
									+ datatype + "; statement = " + statement
									+ "; statements = " + statements + "]");
							continue;
						}
					}
				} else {
					o = vf.createURI(v);
				}

				ret.add(vf.createStatement(s, p, o));
			}

			return Collections.unmodifiableSet(ret);
		}
	}

}
