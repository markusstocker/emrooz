/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn.test;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: ObservationValueDoubleTest
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
public class ObservationValueDoubleTest {

	@Test
	@FileParameters("src/test/resources/ObservationValueDoubleTest.csv")
	public void testObservationValueDouble(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToDoubleConverter.class) Double value2,
			String assertType) {
		if (type1 == null)
			type1 = SSN.ObservationValue;
		if (type2 == null)
			type2 = SSN.ObservationValue;

		ObservationValueDouble ov1 = new ObservationValueDouble(id1, type1);
		ObservationValueDouble ov2 = new ObservationValueDouble(id2, type2);

		ov1.setValue(value1);
		ov2.setValue(value2);

		if (assertType.equals("assertEquals")) {
			assertEquals(ov1, ov2);
			assertEquals(ov1.hashCode(), ov2.hashCode());
			return;
		}

		assertNotEquals(ov1, ov2);
		assertNotEquals(ov1.hashCode(), ov2.hashCode());
	}

}
