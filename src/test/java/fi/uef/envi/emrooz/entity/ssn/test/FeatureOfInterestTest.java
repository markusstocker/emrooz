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
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: FeatureOfInterestTest
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
public class FeatureOfInterestTest {

	@Test
	@FileParameters("src/test/resources/FeatureOfInterestTest.csv")
	public void testFeaturesOfInterest(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			String assertType) {
		if (type1 == null)
			type1 = SSN.FeatureOfInterest;
		if (type2 == null)
			type2 = SSN.FeatureOfInterest;

		FeatureOfInterest f1 = new FeatureOfInterest(id1, type1);
		FeatureOfInterest f2 = new FeatureOfInterest(id2, type2);

		if (assertType.equals("assertEquals")) {
			assertEquals(f1, f2);
			assertEquals(f1.hashCode(), f2.hashCode());
			return;
		}

		assertNotEquals(f1, f2);
		assertNotEquals(f1.hashCode(), f2.hashCode());
	}

}
