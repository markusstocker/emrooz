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

import fi.uef.envi.emrooz.entity.qb.ComponentSpecification;
import fi.uef.envi.emrooz.entity.qb.DimensionProperty;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.QB;

/**
 * <p>
 * Title: ComponentSpecificationTest
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
public class ComponentSpecificationTest {

	@Test
	@FileParameters("src/test/resources/ComponentSpecificationTest.csv")
	public void testComponentSpecification(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId1,
			boolean required1,
			int order1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId2,
			boolean required2, int order2, String assertType) {
		if (type1 == null)
			type1 = QB.ComponentSpecification;
		if (type2 == null)
			type2 = QB.ComponentSpecification;

		ComponentSpecification s1 = new ComponentSpecification(id1, type1,
				new DimensionProperty(propertyId1), required1, order1);
		ComponentSpecification s2 = new ComponentSpecification(id2, type2,
				new DimensionProperty(propertyId2), required2, order2);

		Set<URI> types1 = new HashSet<URI>();
		types1.add(type1);
		types1.add(QB.ComponentSpecification);

		Set<URI> types2 = new HashSet<URI>();
		types2.add(type2);
		types2.add(QB.ComponentSpecification);

		assertEquals(types1, s1.getTypes());
		assertEquals(types2, s2.getTypes());
		assertEquals(type1, s1.getType());
		assertEquals(type2, s2.getType());

		if (assertType.equals("assertEquals")) {
			assertEquals(s1, s2);
			assertEquals(s1.hashCode(), s2.hashCode());
			return;
		}

		assertNotEquals(s1, s2);
		assertNotEquals(s1.hashCode(), s2.hashCode());
	}

}
