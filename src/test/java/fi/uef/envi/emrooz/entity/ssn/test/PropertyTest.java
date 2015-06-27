/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: PropertyTest
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
public class PropertyTest {

	@Test
	@FileParameters("src/test/resources/PropertyTest-1.csv")
	public void testProperty1(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			String assertType) {
		if (type1 == null)
			type1 = SSN.Property;
		if (type2 == null)
			type2 = SSN.Property;

		Property p1 = new Property(id1, type1);
		Property p2 = new Property(id2, type2);

		if (assertType.equals("assertEquals")) {
			assertEquals(p1, p2);
			assertEquals(p1.hashCode(), p2.hashCode());
			return;
		}

		assertNotEquals(p1, p2);
		assertNotEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	@FileParameters("src/test/resources/PropertyTest-2.csv")
	public void testProperty2(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI feature1Id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI feature2Id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI feature1Id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI feature2Id2,
			String assertType) {
		Property p1 = new Property(id1, new FeatureOfInterest(feature1Id1),
				new FeatureOfInterest(feature2Id1));
		Property p2 = new Property(id2, new FeatureOfInterest(feature1Id2),
				new FeatureOfInterest(feature2Id2));

		if (assertType.equals("assertEquals")) {
			assertEquals(p1, p2);
			assertEquals(p1.hashCode(), p2.hashCode());
			return;
		}

		assertNotEquals(p1, p2);
		assertNotEquals(p1.hashCode(), p2.hashCode());
	}

}
