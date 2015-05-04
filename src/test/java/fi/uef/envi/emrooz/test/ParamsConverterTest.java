/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.test;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

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

}
