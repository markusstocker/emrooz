/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.rdf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;

import fi.uef.envi.emrooz.entity.ObservationValueVisitor;
import fi.uef.envi.emrooz.entity.TemporalEntityVisitor;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.ObservationValue;
import fi.uef.envi.emrooz.entity.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;
import fi.uef.envi.emrooz.entity.ssn.SensorObservation;
import fi.uef.envi.emrooz.entity.ssn.SensorOutput;
import fi.uef.envi.emrooz.entity.time.Instant;
import fi.uef.envi.emrooz.entity.time.TemporalEntity;
import fi.uef.envi.emrooz.vocabulary.DUL;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: RDFEntityRepresenter
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

public class RDFEntityRepresenter {

	private Set<Statement> statements;
	private final ObservationValueVisitor observationValueVisitor;
	private final TemporalEntityVisitor temporalEntityVisitor;
	private final static ValueFactory vf = ValueFactoryImpl.getInstance();
	private final static DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();
	private final static Logger log = Logger
			.getLogger(RDFEntityRepresenter.class.getName());

	public RDFEntityRepresenter() {
		observationValueVisitor = new RepresenterObservationValueVisitor();
		temporalEntityVisitor = new RepresenterTemporalEntityVisitor();
	}

	public Set<Statement> createRepresentation(SensorObservation observation) {
		Set<Statement> ret = new HashSet<Statement>();

		Sensor sensor = observation.getSensor();
		Property property = observation.getProperty();
		FeatureOfInterest feature = observation.getFeatureOfInterest();
		SensorOutput result = observation.getObservationResult();
		TemporalEntity resultTime = observation.getObservationResultTime();

		URI id = observation.getId();
		URI sensorId = sensor.getId();
		URI propertyId = property.getId();
		URI featureId = feature.getId();
		URI resultId = result.getId();
		URI resultTimeId = resultTime.getId();

		ret.add(_statement(id, RDF.TYPE, observation.getType()));
		ret.add(_statement(id, SSN.observedBy, sensorId));
		ret.add(_statement(id, SSN.observedProperty, propertyId));
		ret.add(_statement(id, SSN.featureOfInterest, featureId));
		ret.add(_statement(id, SSN.observationResult, resultId));
		ret.add(_statement(id, SSN.observationResultTime, resultTimeId));

		ret.addAll(createRepresentation(sensor));
		ret.addAll(createRepresentation(property));
		ret.addAll(createRepresentation(feature));
		ret.addAll(createRepresentation(result));
		ret.addAll(createRepresentation(resultTime));

		return Collections.unmodifiableSet(ret);
	}

	public SensorObservation createSensorObservation(Set<Statement> statements) {
		if (statements == null)
			return null;

		URI id = _getId(SSN.Observation, statements);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract observation id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		URI type = _getType(id, SSN.Observation, statements);

		Sensor sensor = createSensor(statements);
		Property property = createProperty(statements);
		FeatureOfInterest feature = createFeatureOfInterest(statements);
		SensorOutput result = createSensorOutput(statements);
		TemporalEntity resultTime = createTemporalEntity(statements);

		return new SensorObservation(id, type, sensor, property, feature,
				result, resultTime);
	}

	public Set<Statement> createRepresentation(Sensor sensor) {
		Set<Statement> ret = new HashSet<Statement>();

		ret.add(_statement(sensor.getId(), RDF.TYPE, SSN.Sensor));
		ret.add(_statement(sensor.getId(), RDF.TYPE, sensor.getType()));

		return Collections.unmodifiableSet(ret);
	}

	public Sensor createSensor(Set<Statement> statements) {
		URI id = _getId(SSN.Sensor, statements);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract sensor id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		return new Sensor(id, _getType(id, SSN.Sensor, statements));
	}

	public Set<Statement> createRepresentation(Property property) {
		Set<Statement> ret = new HashSet<Statement>();

		ret.add(_statement(property.getId(), RDF.TYPE, SSN.Property));
		ret.add(_statement(property.getId(), RDF.TYPE, property.getType()));

		return Collections.unmodifiableSet(ret);
	}

	public Property createProperty(Set<Statement> statements) {
		URI id = _getId(SSN.Property, statements);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract property id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		return new Property(id, _getType(id, SSN.Property, statements));
	}

	public Set<Statement> createRepresentation(FeatureOfInterest feature) {
		Set<Statement> ret = new HashSet<Statement>();

		ret.add(_statement(feature.getId(), RDF.TYPE, SSN.FeatureOfInterest));
		ret.add(_statement(feature.getId(), RDF.TYPE, feature.getType()));

		return Collections.unmodifiableSet(ret);
	}

	public FeatureOfInterest createFeatureOfInterest(Set<Statement> statements) {
		URI id = _getId(SSN.FeatureOfInterest, statements);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract feature id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		return new FeatureOfInterest(id, _getType(id, SSN.FeatureOfInterest,
				statements));
	}

	public Set<Statement> createRepresentation(SensorOutput result) {
		Set<Statement> ret = new HashSet<Statement>();

		URI id = result.getId();

		ObservationValue value = result.getValue();
		URI valueId = value.getId();

		ret.add(_statement(id, RDF.TYPE, SSN.SensorOutput));
		ret.add(_statement(id, RDF.TYPE, result.getType()));
		ret.add(_statement(id, SSN.hasValue, valueId));
		ret.addAll(createRepresentation(value));

		return Collections.unmodifiableSet(ret);
	}

	public SensorOutput createSensorOutput(Set<Statement> statements) {
		URI id = _getId(SSN.SensorOutput, statements);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract output id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		return new SensorOutput(id, _getType(id, SSN.SensorOutput, statements),
				createObservationValue(statements));
	}

	public Set<Statement> createRepresentation(ObservationValue value) {
		statements = new HashSet<Statement>();

		value.accept(observationValueVisitor);

		return Collections.unmodifiableSet(statements);
	}

	public ObservationValue createObservationValue(Set<Statement> statements) {
		return createObservationValueDouble(statements);
	}

	public Set<Statement> createRepresentation(ObservationValueDouble value) {
		Set<Statement> ret = new HashSet<Statement>();

		URI id = value.getId();

		ret.add(_statement(id, RDF.TYPE, SSN.ObservationValue));
		ret.add(_statement(id, RDF.TYPE, value.getType()));
		ret.add(_statement(id, DUL.hasRegionDataValue,
				vf.createLiteral(value.getValue())));

		return Collections.unmodifiableSet(ret);
	}

	public ObservationValueDouble createObservationValueDouble(
			Set<Statement> statements) {
		URI id = _getId(SSN.ObservationValue, statements);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract observation value id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		Double value = null;

		for (Statement statement : statements) {
			if (statement.getSubject().equals(id)
					&& statement.getPredicate().equals(DUL.hasRegionDataValue)) {
				value = Double.valueOf(statement.getObject().stringValue());
				break;
			}
		}

		if (value == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Failed to extract observation value [value = null; statements = "
						+ statements + "]");
		}

		return new ObservationValueDouble(id, _getType(id,
				SSN.ObservationValue, statements), value);
	}

	public Set<Statement> createRepresentation(TemporalEntity resultTime) {
		statements = new HashSet<Statement>();

		resultTime.accept(temporalEntityVisitor);

		return Collections.unmodifiableSet(statements);
	}

	public TemporalEntity createTemporalEntity(Set<Statement> statements) {
		return createInstant(statements);
	}

	public Set<Statement> createRepresentation(Instant instant) {
		Set<Statement> ret = new HashSet<Statement>();

		URI id = instant.getId();

		ret.add(_statement(id, RDF.TYPE, Time.Instant));
		ret.add(_statement(id, RDF.TYPE, instant.getType()));
		ret.add(_statement(id, Time.inXSDDateTime, vf.createLiteral(
				dtf.print(instant.getValue()), XMLSchema.DATETIME)));

		return Collections.unmodifiableSet(ret);
	}

	public Instant createInstant(Set<Statement> statements) {
		URI id = _getId(Time.Instant, statements);

		if (id == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Failed to extract instant id [id = null; statements = "
						+ statements + "]");

			return null;
		}

		DateTime value = null;

		for (Statement statement : statements) {
			if (statement.getSubject().equals(id)
					&& statement.getPredicate().equals(Time.inXSDDateTime)) {
				value = dtf
						.parseDateTime((statement.getObject().stringValue()));
				break;
			}
		}

		if (value == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Failed to extract instant value [value = null; statements = "
						+ statements + "]");
		}

		return new Instant(id, _getType(id, Time.Instant, statements), value);
	}

	private static Statement _statement(URI s, URI p, Value o) {
		return vf.createStatement(s, p, o);
	}

	private static URI _getId(URI type, Set<Statement> statements) {
		for (Statement statement : statements) {
			if (statement.getPredicate().equals(RDF.TYPE)
					&& statement.getObject().equals(type))
				return vf.createURI(statement.getSubject().stringValue());
		}

		return null;
	}

	private static URI _getType(URI id, URI type, Set<Statement> statements) {
		if (id == null)
			return type;

		for (Statement statement : statements) {
			if (statement.getSubject().equals(id)
					&& statement.getPredicate().equals(RDF.TYPE)
					&& !statement.getObject().equals(type))
				return vf.createURI(statement.getObject().stringValue());
		}

		return type;
	}

	private class RepresenterObservationValueVisitor implements
			ObservationValueVisitor {

		@Override
		public void visit(ObservationValueDouble entity) {
			statements.addAll(createRepresentation(entity));
		}

	}

	private class RepresenterTemporalEntityVisitor implements
			TemporalEntityVisitor {

		@Override
		public void visit(Instant entity) {
			statements.addAll(createRepresentation(entity));
		}
	}

}
