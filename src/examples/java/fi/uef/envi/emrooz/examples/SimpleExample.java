/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.BindingSet;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.vocabulary.DUL;
import fi.uef.envi.emrooz.vocabulary.SSN;
import fi.uef.envi.emrooz.vocabulary.Time;

/**
 * <p>
 * Title: SimpleExample
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

public class SimpleExample {

	ValueFactory vf = new ValueFactoryImpl();
	DateTimeFormatter dtf = ISODateTimeFormat.dateTime().withOffsetParsed();

	String host = "localhost";

	String dataFrom = "2015-03-13T18:00:00.000+02:00";
	String dataTo = "2015-03-13T18:02:00.000+02:00";

	String queryFrom = "2015-03-13T18:00:59.000+02:00";
	String queryTo = "2015-03-13T18:01:10.000+02:00";

	String rollover = "MINUTE";

	int interval = 1;

	String ns = "http://example.org";

	boolean doInsert = false;

	URI sensorId = _uri("s1");
	URI propertyId = _uri("p1");
	URI featureId = _uri("f1");

	Emrooz emrooz = new Emrooz(host);

	void run() {
		insert();
		query();
		close();
	}

	void insert() {
		if (!doInsert)
			return;

		Random r = new Random();

		emrooz.register(sensorId, propertyId, featureId, rollover);

		DateTime dataTime = dtf.parseDateTime(dataFrom);

		while (dataTime.isBefore(dtf.parseDateTime(dataTo))) {
			Set<Statement> observation = getObservation(sensorId, propertyId,
					featureId, dataTime, r.nextDouble());

			emrooz.addSensorObservation(observation);

			dataTime = dataTime.plusSeconds(interval);
		}
	}

	void query() {
		String query = "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
				+ "select ?time ?value "
				+ "where {"
				+ "["
				+ "ssn:observedBy <http://example.org#s1> ;"
				+ "ssn:observedProperty <http://example.org#p1> ;"
				+ "ssn:featureOfInterest <http://example.org#f1> ;"
				+ "ssn:observationResultTime [ time:inXSDDateTime ?time ] ;"
				+ "ssn:observationResult [ dul:hasRegion [ dul:hasRegionDataValue ?value ] ]"
				+ "]" + "filter (?time >= \"" + queryFrom + "\"^^xsd:dateTime "
				+ "&& ?time < \"" + queryTo + "\"^^xsd:dateTime)" + "}"
				+ "order by asc(?time)";

		List<BindingSet> results = emrooz.getSensorObservations(query);

		for (BindingSet result : results) {
			System.out.println(result.getValue("time") + " "
					+ result.getValue("value"));
		}
	}

	void close() {
		emrooz.close();
	}

	Set<Statement> getObservation(URI sensorId, URI propertyId, URI featureId,
			DateTime time, Double value) {
		Set<Statement> ret = new HashSet<Statement>();

		URI observationId = _uri_uuid();
		URI resultTimeId = _uri_uuid();
		URI outputId = _uri_uuid();
		URI valueId = _uri_uuid();

		ret.add(_observedBy(observationId, sensorId));
		ret.add(_observedProperty(observationId, propertyId));
		ret.add(_featureOfInterest(observationId, featureId));
		ret.add(_resultTime(observationId, resultTimeId));
		ret.add(_inXSDDateTime(resultTimeId, time));
		ret.add(_observationResult(observationId, outputId));
		ret.add(_hasRegion(outputId, valueId));
		ret.add(_hasRegionDataValue(valueId, value));

		return ret;
	}

	Statement _observedBy(URI observationId, URI sensorId) {
		return _statement(observationId, SSN.observedBy, sensorId);
	}

	Statement _observedProperty(URI observationId, URI propertyId) {
		return _statement(observationId, SSN.observedProperty, propertyId);
	}

	Statement _featureOfInterest(URI observationId, URI featureId) {
		return _statement(observationId, SSN.featureOfInterest, featureId);
	}

	Statement _resultTime(URI observationId, URI resultTimeId) {
		return _statement(observationId, SSN.observationResultTime,
				resultTimeId);
	}

	Statement _inXSDDateTime(URI resultTimeId, DateTime time) {
		return _statement(
				resultTimeId,
				Time.inXSDDateTime,
				_literal(ISODateTimeFormat.dateTime().print(time),
						XMLSchema.DATETIME));
	}

	Statement _observationResult(URI observationId, URI outputId) {
		return _statement(observationId, SSN.observationResult, outputId);
	}

	Statement _hasRegion(URI outputId, URI valueId) {
		return _statement(outputId, DUL.hasRegion, valueId);
	}

	Statement _hasRegionDataValue(URI valueId, Double value) {
		return _statement(valueId, DUL.hasRegionDataValue,
				_literal(value.toString(), XMLSchema.DOUBLE));
	}

	Statement _statement(URI s, URI p, Value o) {
		return vf.createStatement(s, p, o);
	}

	URI _uri(String fragment) {
		return _uri(ns, fragment);
	}

	URI _uri(String ns, String fragment) {
		return vf.createURI(ns + "#" + fragment);
	}

	URI _uri_uuid() {
		return _uri(UUID.randomUUID().toString());
	}

	Literal _literal(String value, URI type) {
		return vf.createLiteral(value, type);
	}

	public static void main(String[] args) {
		SimpleExample app = new SimpleExample();
		app.run();
	}

}
