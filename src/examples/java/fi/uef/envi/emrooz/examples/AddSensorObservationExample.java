/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.EntityFactory;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;

/**
 * <p>
 * Title: AddSensorObservationExample
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

public class AddSensorObservationExample {

	public static void main(String[] args) {
		EntityFactory f = EntityFactory.getInstance("http://example.org#");

		Repository r = new SailRepository(new MemoryStore());
		SesameKnowledgeStore ks = new SesameKnowledgeStore(r);

		CassandraDataStore ds = new CassandraDataStore();

		Emrooz emrooz = new Emrooz(ks, ds);

		ks.addSensor(f.createSensor("thermometer", "temperature", "air", 1.0));

		emrooz.add(f.createSensorObservation("thermometer", "temperature",
				"air", 7.6, "2015-04-21T01:00:00.000+03:00"));

		emrooz.add(f.createSensorObservation("thermometer", "temperature",
				"air", 7.4, "2015-04-21T01:30:00.000+03:00"));

		emrooz.close();
	}

}
