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

import org.openrdf.model.Resource;
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
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import fi.uef.envi.emrooz.api.KnowledgeStore;
import fi.uef.envi.emrooz.api.QueryHandler;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.qudt.Unit;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.entity.ssn.MeasurementCapability;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.rdf.RDFEntityRepresenter;
import fi.uef.envi.emrooz.vocabulary.QUDTSchema;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: SesameKnowledgeStore
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

public class SesameKnowledgeStore implements KnowledgeStore {

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

	@Override
	public void addSensor(Sensor sensor) {
		URI sensorId = sensor.getId();

		try {
			if (!connection.hasStatement(sensorId, RDF.TYPE, SSN.Sensor, false,
					new Resource[] {})) {
				load(representer.createRepresentation(sensor));
				return;
			}
		} catch (RepositoryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to check if sensor exists in knowledge store [sensor = "
						+ sensor + "]");
		}

		if (log.isLoggable(Level.INFO)) {
			log.info("Sensor already exists in knowledge store [sensor = "
					+ sensor + "]");
		}
	}

	@Override
	public Set<Sensor> getSensors() {
		return Collections.unmodifiableSet(sensors);
	}

	@Override
	public SesameQueryHandler createQueryHandler(QueryHandler<Statement> other,
			ParsedQuery query) {
		return new SesameQueryHandler(other, query);
	}

	@Override
	public void close() {
		try {
			connection.close();
		} catch (RepositoryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}
	}

	@Override
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
				+ "where {"
				+ "?sensorId rdf:type ssn:Sensor ."
				+ "?sensorId ssn:observes ?propertyId ."
				+ "?propertyId rdf:type ssn:Property ."
				+ "?propertyId ssn:isPropertyOf ?featureId ."
				+ "?featureId rdf:type ssn:FeatureOfInterest ."
				+ "optional {"
				+ "?sensorId ssn:hasMeasurementCapability ?measCapabilityId ."
				+ "?measCapabilityId rdf:type ssn:MeasurementCapability ."
				+ "?measCapabilityId ssn:hasMeasurementProperty ?measPropertyId ."
				+ "?measPropertyId rdf:type ssn:Frequency ."
				+ "?measPropertyId ssn:hasValue ?valueId ."
				+ "?valueId rdf:type qudt:QuantityValue ."
				+ "?valueId qudt:unit unit:Hertz ."
				+ "?valueId qudt:numericValue ?value ." + "} }";

		try {
			TupleQuery query = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, sparql);
			TupleQueryResult rs = query.evaluate();

			while (rs.hasNext()) {
				BindingSet bs = rs.next();

				URI sensorId = _uri(bs.getValue("sensorId"));
				URI propertyId = _uri(bs.getValue("propertyId"));
				URI featureId = _uri(bs.getValue("featureId"));

				Sensor sensor = new Sensor(sensorId);
				Property property = new Property(propertyId);
				FeatureOfInterest feature = new FeatureOfInterest(featureId);

				property.setPropertyOf(feature);
				sensor.setObservedProperty(property);

				if (bs.getValue("measCapabilityId") != null) {
					// Measurement capability is set optional. For applications
					// the frequency must be set, otherwise Cassandra doesn't
					// know when to rollover. However, for testing purposes it
					// is convenient not to have to specify the measurement
					// capability for each sensor.
					URI measCapabilityId = _uri(bs.getValue("measCapabilityId"));
					URI measPropertyId = _uri(bs.getValue("measPropertyId"));
					URI valueId = _uri(bs.getValue("valueId"));
					Double value = Double.valueOf(bs.getValue("value")
							.stringValue());

					MeasurementCapability measCapability = new MeasurementCapability(
							measCapabilityId);
					Frequency measProperty = new Frequency(measPropertyId);
					QuantityValue quantityValue = new QuantityValue(valueId);

					sensor.addMeasurementCapability(measCapability);
					measCapability.addMeasurementProperty(measProperty);
					measProperty.setQuantityValue(quantityValue);
					quantityValue.setNumericValue(value);
					quantityValue.setUnit(new Unit(QUDTUnit.Hertz));
				}

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
