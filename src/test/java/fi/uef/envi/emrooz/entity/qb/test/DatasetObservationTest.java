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

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.qb.Dataset;
import fi.uef.envi.emrooz.entity.qb.DatasetObservation;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.QB;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;

/**
 * <p>
 * Title: DatasetObservationTest
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
public class DatasetObservationTest {

	@Test
	@FileParameters("src/test/resources/DatasetObservationTest.csv")
	public void testDatasetObservation(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI datasetId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI frequency1Id,
			double frequency1Value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI instantId1,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime value1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI datasetId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI frequency2Id,
			double frequency2Value,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI instantId2,
			@ConvertParam(value = ParamsConverterTest.StringToDateTimeConverter.class) DateTime value2,
			String assertType) {
		if (type1 == null)
			type1 = QB.Observation;
		if (type2 == null)
			type2 = QB.Observation;

		DatasetObservation o1 = new DatasetObservation(id1, type1, new Dataset(
				datasetId1, new QuantityValue(frequency1Id, frequency1Value,
						new Unit(QUDTUnit.Hertz))), new Instant(instantId1,
				value1));
		DatasetObservation o2 = new DatasetObservation(id2, type2, new Dataset(
				datasetId2, new QuantityValue(frequency2Id, frequency2Value,
						new Unit(QUDTUnit.Hertz))), new Instant(instantId2,
				value2));

		Set<URI> types1 = new HashSet<URI>();
		types1.add(type1);
		types1.add(QB.Observation);

		Set<URI> types2 = new HashSet<URI>();
		types2.add(type2);
		types2.add(QB.Observation);

		assertEquals(types1, o1.getTypes());
		assertEquals(types2, o2.getTypes());
		assertEquals(type1, o1.getType());
		assertEquals(type2, o2.getType());

		if (assertType.equals("assertEquals")) {
			assertEquals(o1, o2);
			assertEquals(o1.hashCode(), o2.hashCode());
			return;
		}

		assertNotEquals(o1, o2);
		assertNotEquals(o1.hashCode(), o2.hashCode());
	}

}
