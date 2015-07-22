/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.api.KnowledgeStore;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.EntityFactory;
import fi.uef.envi.emrooz.entity.qb.ComponentProperty;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;
import fi.uef.envi.emrooz.vocabulary.SDMXDimension;

/**
 * <p>
 * Title: AddSensorObservationExample.java
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

public class AddDatasetObservationExample {

	public static void main(String[] args) {
		EntityFactory f = EntityFactory.getInstance("http://example.org#");

		KnowledgeStore ks = new SesameKnowledgeStore(new SailRepository(
				new MemoryStore()));

		ComponentProperty timePeriod = f
				.createDimensionProperty(SDMXDimension.timePeriod);
		ComponentProperty temperature = f.createMeasureProperty("temperature");
		ComponentProperty humidity = f.createMeasureProperty("humidity");

		ks.addDataset(f.createDataset(
				"d1",
				1.0,
				f.createDataStructureDefinition("s1",
						f.createComponentSpecification(timePeriod),
						f.createComponentSpecification(temperature),
						f.createComponentSpecification(humidity))));

		Emrooz emrooz = new Emrooz(ks, new CassandraDataStore());

		emrooz.add(f.createDatasetObservation(
				"d1",
				f.createComponent(
						timePeriod,
						f.createComponentPropertyValue("2015-04-21T01:30:00.000+03:00")),
				f.createComponent(
						temperature,
						f.createComponentPropertyValue(7.4,
								f.createUnit(QUDTUnit.DegreeCelsius))),
				f.createComponent(
						humidity,
						f.createComponentPropertyValue(84.0,
								f.createUnit(QUDTUnit.RelativeHumidity)))));

		emrooz.close();
	}
}
