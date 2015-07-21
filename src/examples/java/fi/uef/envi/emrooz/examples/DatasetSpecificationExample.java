/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.api.KnowledgeStore;
import fi.uef.envi.emrooz.entity.EntityFactory;
import fi.uef.envi.emrooz.entity.qb.DataStructureDefinition;
import fi.uef.envi.emrooz.entity.qb.Dataset;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;

/**
 * <p>
 * Title: DatasetSpecificationExample
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

public class DatasetSpecificationExample {

	static EntityFactory f = EntityFactory.getInstance("http://example.org#");

	public static void main(String[] args) {
		KnowledgeStore ks = new SesameKnowledgeStore(new SailRepository(
				new MemoryStore()));

		ks.addDataset(dataset1());
		ks.addDataset(dataset2());

		ks.close();
	}

	private static Dataset dataset1() {
		QuantityValue frequency = f.createQuantityValue();
		Unit unit = f.createUnit(QUDTUnit.Hertz);

		frequency.setNumericValue(10.0);
		frequency.setUnit(unit);

		return f.createDataset("d1", frequency);
	}

	private static Dataset dataset2() {
		QuantityValue frequency = f.createQuantityValue();
		Unit unit = f.createUnit(QUDTUnit.Hertz);

		frequency.setNumericValue(10.0);
		frequency.setUnit(unit);

		Dataset dataset = f.createDataset("d2", frequency);

		DataStructureDefinition structure = f.createDataStructureDefinition("s1");

		structure.addComponent(f.createComponentSpecification(f
				.createMeasureProperty("m1")));
		
		dataset.setStructure(structure);

		return dataset;
	}

}
