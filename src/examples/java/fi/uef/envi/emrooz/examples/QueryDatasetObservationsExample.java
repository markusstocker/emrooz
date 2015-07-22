/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import org.openrdf.query.BindingSet;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.QueryType;
import fi.uef.envi.emrooz.api.KnowledgeStore;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.EntityFactory;
import fi.uef.envi.emrooz.entity.qb.ComponentProperty;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;
import fi.uef.envi.emrooz.vocabulary.SDMXDimension;

/**
 * <p>
 * Title: QueryDatasetObservationsExample
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

public class QueryDatasetObservationsExample {

	public static void main(String[] args) {
		String sparql = sparql1();

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

		ResultSet<BindingSet> results = emrooz.evaluate(
				QueryType.DATASET_OBSERVATION, sparql);

		while (results.hasNext()) {
			System.out.println(results.next());
		}

		results.close();

		emrooz.close();
	}

	private static String sparql1() {
		return "prefix qb: <http://purl.org/linked-data/cube#>"
				+ "prefix sdmx-dimension: <http://purl.org/linked-data/sdmx/2009/dimension#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
				+ "prefix ex: <http://example.org#>"
				+ "select ?time ?temperature ?humidity "
				+ "where {"
				+ "["
				+ "qb:dataSet ex:d1 ;"
				+ "sdmx-dimension:timePeriod [ time:inXSDDateTime ?time ] ;"
				+ "ex:temperature [ dul:hasRegionDataValue ?temperature ] ;"
				+ "ex:humidity [ dul:hasRegionDataValue ?humidity ]"
				+ "]"
				+ "filter (?time >= \"2015-04-21T00:00:00.000+03:00\"^^xsd:dateTime && ?time < \"2015-04-21T02:00:00.000+03:00\"^^xsd:dateTime)"
				+ "} order by desc (?time)";
	}

}
