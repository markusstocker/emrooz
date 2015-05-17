/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.sesame;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import fi.uef.envi.emrooz.cassandra.CassandraQueryHandler;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.query.SensorObservationQuery;
import fi.uef.envi.emrooz.rdf.RDFEntityRepresenter;
import fi.uef.envi.emrooz.vocabulary.QUDTSchema;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: KnowledgeStore
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

public class SesameKnowledgeStore {

	private Repository repository;
	private RepositoryConnection connection;
	private Set<Sensor> sensors;
	private ValueFactory vf;
	private RDFEntityRepresenter representer;

	private static final Logger log = Logger
			.getLogger(SesameKnowledgeStore.class.getName());

	public SesameKnowledgeStore(Repository repository) {
		if (repository == null)
			throw new RuntimeException("[repository = null]");

		this.repository = repository;

		try {
			this.repository.initialize();
			this.connection = this.repository.getConnection();
		} catch (RepositoryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}

		this.vf = connection.getValueFactory();
		this.representer = new RDFEntityRepresenter();

		loadSensors();
	}

	public void register(Sensor sensor) {
		load(representer.createRepresentation(sensor));
	}

	public Set<Sensor> getSensors() {
		return Collections.unmodifiableSet(sensors);
	}

	public void load(File file) {
		load(file, null);
	}

	public void load(Set<Statement> statements) {
		if (statements == null)
			return;

		for (Statement statement : statements) {
			try {
				connection.add(statement);
			} catch (RepositoryException e) {
				if (log.isLoggable(Level.SEVERE))
					log.severe(e.getMessage());
			}
		}

		loadSensors();
	}

	public void load(File file, String baseURI) {
		load(file, baseURI, RDFFormat.RDFXML);
	}

	public void load(File file, String baseURI, RDFFormat format) {
		try {
			connection.add(file, baseURI, format);
		} catch (RDFParseException | RepositoryException | IOException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}

		loadSensors();
	}

	public SesameQueryHandler createQueryHandler(
			CassandraQueryHandler cassandraQueryHandler,
			SensorObservationQuery query) {
		return new SesameQueryHandler(cassandraQueryHandler, query);
	}

	public void close() {
		try {
			connection.close();
		} catch (RepositoryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}
	}

	private void loadSensors() {
		sensors = new HashSet<Sensor>();

		String sparql = "prefix ssn: <"
				+ SSN.ns
				+ "#>"
				+ "prefix qudt: <"
				+ QUDTSchema.ns
				+ "#>"
				+ "prefix rdf: <"
				+ RDF.NAMESPACE
				+ ">"
				+ "prefix unit: <"
				+ QUDTUnit.ns
				+ "#>"
				+ "select ?sensorId ?propertyId ?featureId ?measCapabilityId ?measPropertyId ?valueId ?value "
				+ "where {" + "?sensorId rdf:type ssn:Sensor ."
				+ "?sensorId ssn:observes ?propertyId ."
				+ "?propertyId rdf:type ssn:Property ."
				+ "?propertyId ssn:isPropertyOf ?featureId ."
				+ "?featureId rdf:type ssn:FeatureOfInterest ."
				+ "?sensorId ssn:hasMeasurementCapability ?measCapabilityId ."
				+ "?measCapabilityId rdf:type ssn:MeasurementCapability ."
				+ "?capabilityId ssn:hasMeasurementProperty ?measPropertyId ."
				+ "?measPropertyId rdf:type ssn:Frequency ."
				+ "?measPropertyId ssn:hasValue ?valueId ."
				+ "?valueId rdf:type qudt:QuantityValue ."
				+ "?valueId qudt:unit unit:Hertz ."
				+ "?valueId qudt:numericValue ?value ." + "}";

		try {
			TupleQuery query = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, sparql);
			TupleQueryResult rs = query.evaluate();

			while (rs.hasNext()) {
				BindingSet bs = rs.next();

				URI sensorId = _uri(bs.getValue("sensorId"));
				URI propertyId = _uri(bs.getValue("propertyId"));
				URI featureId = _uri(bs.getValue("featureId"));
				URI measCapabilityId = _uri(bs.getValue("measCapabilityId"));
				URI measPropertyId = _uri(bs.getValue("measPropertyId"));
				URI valueId = _uri(bs.getValue("valueId"));
				Double value = Double.valueOf(bs.getValue("value")
						.stringValue());

				Sensor sensor = new Sensor(sensorId);
				Property property = new Property(propertyId);
				FeatureOfInterest feature = new FeatureOfInterest(featureId);
				MeasurementCapability measCapability = new MeasurementCapability(
						measCapabilityId);
				Frequency measProperty = new Frequency(measPropertyId);
				QuantityValue quantityValue = new QuantityValue(valueId);

				property.setPropertyOf(feature);
				sensor.setObservedProperty(property);
				sensor.addMeasurementCapability(measCapability);
				measCapability.addMeasurementProperty(measProperty);
				measProperty.setQuantityValue(quantityValue);
				quantityValue.setNumericValue(value);
				quantityValue.setUnit(new Unit(QUDTUnit.Hertz));

				sensors.add(sensor);
			}

		} catch (RepositoryException | MalformedQueryException
				| QueryEvaluationException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}

		if (log.isLoggable(Level.INFO))
			log.info("Loaded sensors (" + sensors.size() + ") {" + sensors
					+ "}");
	}

	private URI _uri(Value value) {
		return vf.createURI(value.stringValue());
	}

}
