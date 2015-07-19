/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import fi.uef.envi.emrooz.query.SensorObservationQuery;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.ConversionFailedException;
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

	@Test
	@Parameters({ "(http://envi.uef.fi/emrooz#s1 http://envi.uef.fi/emrooz#p1 http://envi.uef.fi/emrooz#f1 2015-05-31T00:00:00.000+03:00 2015-05-31T01:00:00.000+03:00)" })
	public void testEqualsStringToSensorObservationQueryCollection1(
			@ConvertParam(value = StringToSensorObservationQueryCollection.class) Set<SensorObservationQuery> actual) {

		Set<SensorObservationQuery> expected = new HashSet<SensorObservationQuery>();

		expected.add(SensorObservationQuery.create(
				vf.createURI("http://envi.uef.fi/emrooz#s1"),
				vf.createURI("http://envi.uef.fi/emrooz#p1"),
				vf.createURI("http://envi.uef.fi/emrooz#f1"),
				dtf.parseDateTime("2015-05-31T00:00:00.000+03:00"),
				dtf.parseDateTime("2015-05-31T01:00:00.000+03:00")));

		assertEquals(expected, actual);
	}

	@Test
	@Parameters({ "(http://envi.uef.fi/emrooz#s1 http://envi.uef.fi/emrooz#p1 http://envi.uef.fi/emrooz#f1 2015-05-31T00:00:00.000+03:00 2015-05-31T01:00:00.000+03:00)" })
	public void testNotEqualsStringToSensorObservationQueryCollection1(
			@ConvertParam(value = StringToSensorObservationQueryCollection.class) Set<SensorObservationQuery> actual) {

		Set<SensorObservationQuery> expected = new HashSet<SensorObservationQuery>();

		expected.add(SensorObservationQuery.create(
				vf.createURI("http://envi.uef.fi/emrooz#s1"),
				vf.createURI("http://envi.uef.fi/emrooz#p1"),
				vf.createURI("http://envi.uef.fi/emrooz#f1"),
				dtf.parseDateTime("2015-05-31T01:00:00.000+03:00"),
				dtf.parseDateTime("2015-05-31T02:00:00.000+03:00")));

		assertNotEquals(expected, actual);
	}

	@Test
	@Parameters({ "(s=http://example.org#s p=http://example.org#p o=http://example.org#o)" })
	public void testEqualsStringToBindingMapSet1(
			@ConvertParam(value = StringToBindingMapSet.class) Set<Map<String, String>> actual) {

		Set<Map<String, String>> expected = new HashSet<Map<String, String>>();

		Map<String, String> m1 = new HashMap<String, String>();
		expected.add(m1);
		
		m1.put("s", "http://example.org#s");
		m1.put("p", "http://example.org#p");
		m1.put("o", "http://example.org#o");

		assertTrue(CollectionUtils.isEqualCollection(expected, actual));
	}
	
	@Test
	@Parameters({ "(s=http://example.org#s p=http://example.org#p o=http://example.org#q)" })
	public void testNotEqualsStringToBindingMapSet1(
			@ConvertParam(value = StringToBindingMapSet.class) Set<Map<String, String>> actual) {

		Set<Map<String, String>> expected = new HashSet<Map<String, String>>();

		Map<String, String> m1 = new HashMap<String, String>();
		expected.add(m1);
		
		m1.put("s", "http://example.org#s");
		m1.put("p", "http://example.org#p");
		m1.put("o", "http://example.org#o");

		assertFalse(CollectionUtils.isEqualCollection(expected, actual));
	}
	
	@Test
	@Parameters({ "(s=http://example.org#s1 p=http://example.org#p1 o=http://example.org#o1);(s=http://example.org#s2 p=http://example.org#p2 o=http://example.org#o2)" })
	public void testEqualsStringToBindingMapSet2(
			@ConvertParam(value = StringToBindingMapSet.class) Set<Map<String, String>> actual) {

		Set<Map<String, String>> expected = new HashSet<Map<String, String>>();

		Map<String, String> m1 = new HashMap<String, String>();
		expected.add(m1);
		
		m1.put("s", "http://example.org#s1");
		m1.put("p", "http://example.org#p1");
		m1.put("o", "http://example.org#o1");
		
		Map<String, String> m2 = new HashMap<String, String>();
		expected.add(m2);
		
		m2.put("s", "http://example.org#s2");
		m2.put("p", "http://example.org#p2");
		m2.put("o", "http://example.org#o2");

		assertTrue(CollectionUtils.isEqualCollection(expected, actual));
	}
	
	@Test
	@Parameters({ "(s=http://example.org#s1 p=http://example.org#p1 o=http://example.org#o1);(s=http://example.org#s2 p=http://example.org#p2 q=http://example.org#o2)" })
	public void testNotEqualsStringToBindingMapSet2(
			@ConvertParam(value = StringToBindingMapSet.class) Set<Map<String, String>> actual) {

		Set<Map<String, String>> expected = new HashSet<Map<String, String>>();

		Map<String, String> m1 = new HashMap<String, String>();
		expected.add(m1);
		
		m1.put("s", "http://example.org#s1");
		m1.put("p", "http://example.org#p1");
		m1.put("o", "http://example.org#o1");
		
		Map<String, String> m2 = new HashMap<String, String>();
		expected.add(m2);
		
		m2.put("s", "http://example.org#s2");
		m2.put("p", "http://example.org#p2");
		m2.put("o", "http://example.org#o2");

		assertFalse(CollectionUtils.isEqualCollection(expected, actual));
	}

	public static class StringToURIConverter implements ParamConverter<URI> {

		@Override
		public URI convert(Object param, String options) {
			String value = param.toString();

			if (value.isEmpty())
				return null;

			return vf.createURI(value);
		}

	}

	public static class StringToDoubleConverter implements
			ParamConverter<Double> {

		@Override
		public Double convert(Object param, String options) {
			String value = param.toString();

			if (value.isEmpty())
				return Double.NaN;

			return Double.valueOf(value);
		}

	}

	public static class StringToDateTimeConverter implements
			ParamConverter<DateTime> {

		@Override
		public DateTime convert(Object param, String options) {
			String value = param.toString();

			if (value.isEmpty())
				return null;

			return dtf.parseDateTime(value);
		}

	}

	public static class StringToStatementsConverter implements
			ParamConverter<Set<Statement>> {

		@Override
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
						log.warning("Expected three resources [length = "
								+ length + "; statement = " + statement
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
					} else if (datatype.equals(XMLSchema.STRING.stringValue())) {
						o = vf.createLiteral(label, XMLSchema.STRING);
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

	public static class StringToBindingMapSet implements
			ParamConverter<Set<Map<String, String>>> {

		@Override
		public Set<Map<String, String>> convert(Object param, String options)
				throws ConversionFailedException {
			String value = param.toString();

			if (value.isEmpty())
				return Collections.emptySet();

			Set<Map<String, String>> ret = new HashSet<Map<String, String>>();

			String[] bindingSets = value.split(";");

			for (String bindingSet : bindingSets) {
				if (bindingSet.startsWith("(") && bindingSet.endsWith(")")) {
					bindingSet = bindingSet.substring(1,
							bindingSet.length() - 1);
				} else {
					if (log.isLoggable(Level.WARNING))
						log.warning("Bindings should start and end with rounded parenthesis [bindingSet = "
								+ bindingSet
								+ "; bindingSets = "
								+ bindingSets
								+ "]");
					continue;
				}

				String[] bindings = bindingSet.split(" ");
				int length = bindings.length;

				if (length <= 0) {
					if (log.isLoggable(Level.WARNING))
						log.warning("Expected at least one binding [length = "
								+ length + "; bindings = " + bindings
								+ "; bindingSet = " + bindingSet
								+ "; bindingSets = " + bindingSets + "]");
					continue;
				}

				Map<String, String> m = new HashMap<String, String>();
				ret.add(m);

				for (String binding : bindings) {
					String[] values = binding.split("=");
					length = values.length;

					if (length != 2) {
						if (log.isLoggable(Level.WARNING))
							log.warning("Expected two values [length = "
									+ length + "; values = " + values
									+ "; bindings = " + bindings
									+ "; bindingSet = " + bindingSet
									+ "; bindingSets = " + bindingSets + "]");
						continue;
					}

					m.put(values[0], values[1]);
				}
			}

			return Collections.unmodifiableSet(ret);
		}

	}

	public static class StringToSensorObservationQueryCollection implements
			ParamConverter<Set<SensorObservationQuery>> {

		@Override
		public Set<SensorObservationQuery> convert(Object param, String options)
				throws ConversionFailedException {
			String value = param.toString();

			if (value.isEmpty())
				return Collections.emptySet();

			Set<SensorObservationQuery> ret = new HashSet<SensorObservationQuery>();

			String[] queries = value.split(";");

			for (String query : queries) {
				if (query.startsWith("(") && query.endsWith(")")) {
					query = query.substring(1, query.length() - 1);
				} else {
					if (log.isLoggable(Level.WARNING))
						log.warning("Query should start and end with rounded parenthesis [statement = "
								+ query + "; queries = " + queries + "]");
					continue;
				}

				String[] values = query.split(" ");
				int length = values.length;

				if (length != 5) {
					if (log.isLoggable(Level.WARNING))
						log.warning("Expected five values [length = " + length
								+ "; query = " + query + "; queries = "
								+ queries + "]");
					continue;
				}

				URI sensorId = null;
				URI propertyId = null;
				URI featureId = null;

				if (!values[0].equals("?"))
					sensorId = vf.createURI(values[0]);

				if (!values[1].equals("?"))
					propertyId = vf.createURI(values[1]);

				if (!values[2].equals("?"))
					featureId = vf.createURI(values[2]);

				DateTime timeFrom = dtf.parseDateTime(values[3]);
				DateTime timeTo = dtf.parseDateTime(values[4]);

				ret.add(SensorObservationQuery.create(sensorId, propertyId,
						featureId, timeFrom, timeTo));
			}

			return Collections.unmodifiableSet(ret);
		}

	}

}
