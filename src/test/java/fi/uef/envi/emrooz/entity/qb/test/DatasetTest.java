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
import fi.uef.envi.emrooz.entity.qb.DataStructureDefinition;
import fi.uef.envi.emrooz.entity.qb.Dataset;
import fi.uef.envi.emrooz.entity.qb.DimensionProperty;
import fi.uef.envi.emrooz.test.ParamsConverterTest;
import fi.uef.envi.emrooz.vocabulary.QB;

/**
 * <p>
 * Title: DatasetTest
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
public class DatasetTest {

	@Test
	@FileParameters("src/test/resources/DatasetTest.csv")
	public void testDataset(
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI structureId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI componentId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId1,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI id2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI type2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI structureId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI componentId2,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI propertyId2,
			String assertType) {
		if (type1 == null)
			type1 = QB.DataSet;
		if (type2 == null)
			type2 = QB.DataSet;

		Dataset d1 = new Dataset(id1, type1);
		Dataset d2 = new Dataset(id2, type2);

		DataStructureDefinition s1 = new DataStructureDefinition(structureId1,
				new ComponentSpecification(componentId1, new DimensionProperty(
						propertyId1)));
		DataStructureDefinition s2 = new DataStructureDefinition(structureId2,
				new ComponentSpecification(componentId2, new DimensionProperty(
						propertyId2)));

		d1.setStructure(s1);
		d2.setStructure(s2);

		Set<URI> types1 = new HashSet<URI>();
		types1.add(type1);
		types1.add(QB.DataSet);

		Set<URI> types2 = new HashSet<URI>();
		types2.add(type2);
		types2.add(QB.DataSet);

		assertEquals(types1, d1.getTypes());
		assertEquals(types2, d2.getTypes());
		assertEquals(type1, d1.getType());
		assertEquals(type2, d2.getType());

		if (assertType.equals("assertEquals")) {
			assertEquals(d1, d2);
			assertEquals(d1.hashCode(), d2.hashCode());
			return;
		}

		assertNotEquals(d1, d2);
		assertNotEquals(d1.hashCode(), d2.hashCode());
	}
}
