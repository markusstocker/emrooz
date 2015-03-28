/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: StatementResourceExtractor
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

public class SensorObservationExtractor {

	private URI sensor;
	private URI property;
	private URI feature;
	private DateTime resultTime;
	private Set<Statement> statements;
	private DateTimeFormatter dtf;

	private final static Logger log = Logger
			.getLogger(SensorObservationExtractor.class.getName());

	public SensorObservationExtractor(Set<Statement> statements) {
		this.dtf = ISODateTimeFormat.dateTime().withOffsetParsed();
		this.statements = Collections.unmodifiableSet(statements);

		extract(statements);
	}

	public URI getSensor() {
		return sensor;
	}

	public URI getProperty() {
		return property;
	}

	public URI getFeature() {
		return feature;
	}

	public DateTime getResultTime() {
		return resultTime;
	}

	public Set<Statement> getStatements() {
		return statements;
	}

	private void extract(Set<Statement> statements) {
		if (statements == null)
			return;

		for (Statement statement : statements) {
			URI predicate = statement.getPredicate();
			Value object = statement.getObject();

			if (predicate.equals(SSN.observedBy)) {
				if (object instanceof URI)
					sensor = (URI) object;
				else {
					if (log.isLoggable(Level.SEVERE))
						log.severe("Expected URI object [object = " + object
								+ "; statement = " + statement + "]");
				}
			} else if (predicate.equals(SSN.observedProperty)) {
				if (object instanceof URI)
					property = (URI) object;
				else {
					if (log.isLoggable(Level.SEVERE))
						log.severe("Expected URI object [object = " + object
								+ "; statement = " + statement + "]");
				}
			} else if (predicate.equals(SSN.featureOfInterest)) {
				if (object instanceof URI)
					feature = (URI) object;
				else {
					if (log.isLoggable(Level.SEVERE))
						log.severe("Expected URI object [object = " + object
								+ "; statement = " + statement + "]");
				}
			} else if (predicate.equals(Time.inXSDDateTime)) {
				if (object instanceof Literal)
					resultTime = dtf.parseDateTime(object.stringValue());
				else {
					if (log.isLoggable(Level.SEVERE))
						log.severe("Expected Literal object [object = "
								+ object + "; statement = " + statement + "]");
				}
			}
		}
	}
}
