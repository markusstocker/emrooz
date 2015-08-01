/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.rest;

import java.io.File;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.QueryType;
import fi.uef.envi.emrooz.api.DataStore;
import fi.uef.envi.emrooz.api.KnowledgeStore;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.qb.Dataset;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;

/**
 * <p>
 * Title: Connection
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

public class Connection {

	private static Emrooz emrooz;

	private static String KNOWLEDGE_STORE_FILE_DIRECTORY = "/tmp/ks";

	public static void init() {
		Repository r = new SailRepository(new NativeStore(new File(
				KNOWLEDGE_STORE_FILE_DIRECTORY)));
		KnowledgeStore ks = new SesameKnowledgeStore(r);

		DataStore ds = new CassandraDataStore();

		emrooz = new Emrooz(ks, ds);
	}

	public static Set<FeatureOfInterest> getFeaturesOfInterest() {
		return emrooz.getFeaturesOfInterest();
	}

	public static Set<Property> getProperties() {
		return emrooz.getProperties();
	}

	public static Set<Sensor> getSensors() {
		return emrooz.getSensors();
	}

	public static Set<Dataset> getDatasets() {
		return emrooz.getDatasets();
	}

	public static ResultSet<BindingSet> evaluate(String sensorId,
			String propertyId, String featureId, String from, String to) {
		StringBuffer query = new StringBuffer();

		query.append("prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#> ");
		query.append("prefix time: <http://www.w3.org/2006/time#> ");
		query.append("prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> ");
		query.append("select ?time ?value ");

		if (sensorId == null)
			query.append("?sensorId ");
		if (propertyId == null)
			query.append("?propertyId ");
		if (featureId == null)
			query.append("?featureId ");

		query.append("where { ");
		query.append("[ ");

		if (sensorId == null)
			query.append("ssn:observedBy ?sensorId ; ");
		else
			query.append("ssn:observedBy <" + sensorId + "> ; ");

		if (propertyId == null)
			query.append("ssn:observedProperty ?propertyId ; ");
		else
			query.append("ssn:observedProperty <" + propertyId + "> ; ");

		if (featureId == null)
			query.append("ssn:featureOfInterest ?featureId ; ");
		else
			query.append("ssn:featureOfInterest <" + featureId + "> ; ");

		query.append("ssn:observationResultTime [ time:inXSDDateTime ?time ] ; ");
		query.append("ssn:observationResult [ ssn:hasValue [ dul:hasRegionDataValue ?value ] ] ");
		query.append("] ");
		query.append("filter (?time >= \"" + from + "\"^^xsd:dateTime && ");
		query.append("?time < \"" + to + "\"^^xsd:dateTime) ");
		query.append("} order by asc (?time)");
		
		return emrooz.evaluate(QueryType.SENSOR_OBSERVATION, query.toString());
	}

	public static void shutdown() {
		emrooz.close();
	}

}
