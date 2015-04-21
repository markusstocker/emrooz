/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.rdf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;

import fi.uef.envi.emrooz.api.ObservationValueVisitor;
import fi.uef.envi.emrooz.api.TemporalEntityVisitor;
import fi.uef.envi.emrooz.api.ssn.ObservationValue;
import fi.uef.envi.emrooz.api.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.api.ssn.SensorObservation;
import fi.uef.envi.emrooz.api.ssn.SensorOutput;
import fi.uef.envi.emrooz.api.time.Instant;
import fi.uef.envi.emrooz.api.time.TemporalEntity;
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

	public RDFEntityRepresenter() {
		observationValueVisitor = new RepresenterObservationValueVisitor();
		temporalEntityVisitor = new RepresenterTemporalEntityVisitor();
	}

	public Set<Statement> createRepresentation(SensorObservation observation) {
		Set<Statement> ret = new HashSet<Statement>();

		URI id = observation.getId();
		URI sensorId = observation.getSensor().getId();
		URI propertyId = observation.getProperty().getId();
		URI featureId = observation.getFeatureOfInterest().getId();

		SensorOutput result = observation.getObservationResult();
		URI resultId = result.getId();

		TemporalEntity resultTime = observation.getObservationResultTime();
		URI resultTimeId = resultTime.getId();

		ret.add(_statement(id, RDF.TYPE, observation.getType()));
		ret.add(_statement(id, SSN.observedBy, sensorId));
		ret.add(_statement(id, SSN.observedProperty, propertyId));
		ret.add(_statement(id, SSN.featureOfInterest, featureId));
		ret.add(_statement(id, SSN.observationResult, resultId));
		ret.add(_statement(id, SSN.observationResultTime, resultTimeId));
		ret.addAll(createRepresentation(result));
		ret.addAll(createRepresentation(resultTime));

		return Collections.unmodifiableSet(ret);
	}

	public Set<Statement> createRepresentation(SensorOutput result) {
		Set<Statement> ret = new HashSet<Statement>();

		URI id = result.getId();

		ObservationValue value = result.getValue();
		URI valueId = value.getId();

		ret.add(_statement(id, RDF.TYPE, result.getType()));
		ret.add(_statement(id, SSN.hasValue, valueId));
		ret.addAll(createRepresentation(value));

		return Collections.unmodifiableSet(ret);
	}

	public Set<Statement> createRepresentation(ObservationValue value) {
		statements = new HashSet<Statement>();

		value.accept(observationValueVisitor);

		return Collections.unmodifiableSet(statements);
	}

	public Set<Statement> createRepresentation(ObservationValueDouble value) {
		Set<Statement> ret = new HashSet<Statement>();

		URI id = value.getId();

		ret.add(_statement(id, RDF.TYPE, value.getType()));
		ret.add(_statement(id, DUL.hasRegionDataValue,
				vf.createLiteral(value.getValue())));

		return Collections.unmodifiableSet(ret);
	}

	public Set<Statement> createRepresentation(TemporalEntity resultTime) {
		statements = new HashSet<Statement>();

		resultTime.accept(temporalEntityVisitor);

		return Collections.unmodifiableSet(statements);
	}

	public Set<Statement> createRepresentation(Instant instant) {
		Set<Statement> ret = new HashSet<Statement>();

		URI id = instant.getId();

		ret.add(_statement(id, RDF.TYPE, instant.getType()));
		ret.add(_statement(id, Time.inXSDDateTime, vf.createLiteral(
				dtf.print(instant.getValue()), XMLSchema.DATETIME)));

		return Collections.unmodifiableSet(ret);
	}

	private static Statement _statement(URI s, URI p, Value o) {
		return vf.createStatement(s, p, o);
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
