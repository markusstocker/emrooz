/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;
import java.util.Set;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.qb.MeasureProperty;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.QB;

/**
 * <p>
 * Title: MeasurePropertyTest
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
public class MeasurePropertyTest {

	@Test
	@FileParameters("src/test/resources/MeasurePropertyTest.csv")
	public void testDimensionProperty(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			String assertType) {
		if (type1 == null)
			type1 = QB.MeasureProperty;
		if (type2 == null)
			type2 = QB.MeasureProperty;

		MeasureProperty p1 = new MeasureProperty(id1, type1);
		MeasureProperty p2 = new MeasureProperty(id2, type2);
		
		Set<URI> types1 = new HashSet<URI>();
		types1.add(type1);
		types1.add(QB.MeasureProperty);
		types1.add(QB.ComponentProperty);
		
		Set<URI> types2 = new HashSet<URI>();
		types2.add(type2);
		types2.add(QB.MeasureProperty);
		types2.add(QB.ComponentProperty);
		
		assertEquals(types1, p1.getTypes());
		assertEquals(types2, p2.getTypes());
		assertEquals(type1, p1.getType());
		assertEquals(type2, p2.getType());
		
		if (assertType.equals("assertEquals")) {
			assertEquals(p1, p2);
			assertEquals(p1.hashCode(), p2.hashCode());
			return;
		}

		assertNotEquals(p1, p2);
		assertNotEquals(p1.hashCode(), p2.hashCode());
	}
}
