/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import java.io.File;
import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.QueryType;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.entity.EntityFactory;
import fi.uef.envi.emrooz.entity.qb.ComponentProperty;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;
import fi.uef.envi.emrooz.vocabulary.QUDTUnit;
import fi.uef.envi.emrooz.vocabulary.SDMXDimension;

/**
 * <p>
 * Title: CompletePersistentExample
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

public class CompletePersistentExample {

	static Emrooz e;
	static Random r = new Random();
	static DateTimeFormatter dtf = ISODateTimeFormat.dateTime()
			.withOffsetParsed();
	static EntityFactory f = EntityFactory.getInstance("http://example.org#");

	public static void main(String[] args) {
		e = new Emrooz(new SesameKnowledgeStore(new SailRepository(
				new MemoryStore(new File("/tmp/ks")))),
				new CassandraDataStore());

		add(); // Comment to query again after adding the data
		query();

		e.close();
	}

	private static void query() {
		querySensorObservations();
		queryDatasetObservations();
	}

	private static void querySensorObservations() {
		String sparql = "prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>"
				+ "select ?time ?sensor ?property ?feature ?value "
				+ "where {"
				+ "["
				+ "ssn:observedBy ?sensor ;"
				+ "ssn:observedProperty ?property ;"
				+ "ssn:featureOfInterest ?feature ;"
				+ "ssn:observationResultTime [ time:inXSDDateTime ?time ] ;"
				+ "ssn:observationResult [ ssn:hasValue [ dul:hasRegionDataValue ?value ] ]"
				+ "]"
				+ "filter (?time >= \"2015-05-18T00:00:30.000+03:00\"^^xsd:dateTime && ?time < \"2015-05-18T00:00:35.000+03:00\"^^xsd:dateTime)"
				+ "} order by ?sensor asc (?time)";

		System.out.println("== QUERY SENSOR OBSERVATIONS ==");

		long start = System.currentTimeMillis();

		// In addition to the SPARQL writer, there are also CSV, JSON, and XML
		// writers
		e.evaluate(QueryType.SENSOR_OBSERVATION, sparql,
				new SPARQLResultsTSVWriter(System.out));

		long end = System.currentTimeMillis();

		System.out.println("Time (s): " + elapsed(start, end));
	}

	private static void queryDatasetObservations() {
		String sparql = "prefix qb: <http://purl.org/linked-data/cube#>"
				+ "prefix sdmx-dimension: <http://purl.org/linked-data/sdmx/2009/dimension#>"
				+ "prefix time: <http://www.w3.org/2006/time#>"
				+ "prefix qudt: <http://qudt.org/schema/qudt#>"
				+ "prefix ex: <http://example.org#>"
				+ "select ?time ?temperature ?temperatureUnit ?humidity ?humidityUnit ?vibration ?vibrationUnit ?carbonDioxide ?carbonDioxideUnit ?waterVapor ?waterVaporUnit "
				+ "where {"
				+ "["
				+ "qb:dataSet ex:d1 ;"
				+ "sdmx-dimension:timePeriod [ time:inXSDDateTime ?time ] ;"
				+ "ex:temperature [ qudt:numericValue ?temperature; qudt:unit ?temperatureUnit ] ;"
				+ "ex:humidity [ qudt:numericValue ?humidity; qudt:unit ?humidityUnit ] ;"
				+ "ex:vibration [ qudt:numericValue ?vibration; qudt:unit ?vibrationUnit ] ;"
				+ "ex:carbonDioxide [ qudt:numericValue ?carbonDioxide; qudt:unit ?carbonDioxideUnit ] ;"
				+ "ex:waterVapor [ qudt:numericValue ?waterVapor; qudt:unit ?waterVaporUnit ]"
				+ "]"
				+ "filter (?time >= \"2015-05-18T00:00:30.000+03:00\"^^xsd:dateTime && ?time < \"2015-05-18T00:00:35.000+03:00\"^^xsd:dateTime)"
				+ "} order by asc (?time)";

		System.out.println("== QUERY DATASET OBSERVATIONS ==");

		long start = System.currentTimeMillis();

		e.evaluate(QueryType.DATASET_OBSERVATION, sparql,
				new SPARQLResultsTSVWriter(System.out));

		long end = System.currentTimeMillis();

		System.out.println("Time (s): " + elapsed(start, end));
	}

	private static void add() {
		addSensorObservations();
		addDatasetObservations();
	}

	private static void addSensorObservations() {
		e.add(f.createSensor("aThermometer", "temperature", "air", 1.0));
		e.add(f.createSensor("aHygrometer", "humidity", "air", 1.0));
		e.add(f.createSensor("aAccelerometer", "vibration", "pavement", 1.0));
		e.add(f.createSensor("aGasAnalyzer", "moleFraction", new String[] {
				"CO2", "H2O" }, 1.0));

		DateTime now = dtf.parseDateTime("2015-05-18T00:00:00.000+03:00");

		System.out.println("== ADD SENSOR OBSERVATIONS ==");
		long start = System.currentTimeMillis();

		for (int i = 0; i < 120; i++) {
			String time = dtf.print(now.plusSeconds(i));

			e.add(f.createSensorObservation("aThermometer", "temperature",
					"air", r.nextDouble(), time));
			e.add(f.createSensorObservation("aHygrometer", "humidity", "air",
					r.nextDouble(), time));
			e.add(f.createSensorObservation("aAccelerometer", "vibration",
					"pavement", r.nextDouble(), time));
			e.add(f.createSensorObservation("aGasAnalyzer", "moleFraction",
					"CO2", r.nextDouble(), time));
			e.add(f.createSensorObservation("aGasAnalyzer", "moleFraction",
					"H2O", r.nextDouble(), time));
		}

		long end = System.currentTimeMillis();
		System.out.println("Time (s): " + elapsed(start, end));
	}

	private static void addDatasetObservations() {
		ComponentProperty timePeriod = f
				.createDimensionProperty(SDMXDimension.timePeriod);
		ComponentProperty temperature = f.createMeasureProperty("temperature");
		ComponentProperty humidity = f.createMeasureProperty("humidity");
		ComponentProperty vibration = f.createMeasureProperty("vibration");
		ComponentProperty carbonDioxide = f
				.createMeasureProperty("carbonDioxide");
		ComponentProperty waterVapor = f.createMeasureProperty("waterVapor");

		e.add(f.createDataset(
				"d1",
				1.0,
				f.createDataStructureDefinition("s1",
						f.createComponentSpecification(timePeriod),
						f.createComponentSpecification(temperature),
						f.createComponentSpecification(humidity),
						f.createComponentSpecification(vibration),
						f.createComponentSpecification(carbonDioxide),
						f.createComponentSpecification(waterVapor))));

		DateTime now = dtf.parseDateTime("2015-05-18T00:00:00.000+03:00");

		System.out.println("== ADD DATASET OBSERVATIONS ==");
		long start = System.currentTimeMillis();

		for (int i = 0; i < 120; i++) {
			String time = dtf.print(now.plusSeconds(i));

			e.add(f.createDatasetObservation(
					"d1",
					f.createComponent(timePeriod,
							f.createComponentPropertyValue(time)),
					f.createComponent(
							temperature,
							f.createComponentPropertyValue(r.nextDouble(),
									f.createUnit(QUDTUnit.DegreeCelsius))),
					f.createComponent(
							humidity,
							f.createComponentPropertyValue(r.nextDouble(),
									f.createUnit(QUDTUnit.RelativeHumidity))),
					f.createComponent(
							vibration,
							f.createComponentPropertyValue(r.nextDouble(), f
									.createUnit(QUDTUnit.MeterPerSecondSquared))),
					f.createComponent(
							carbonDioxide,
							f.createComponentPropertyValue(
									r.nextDouble(),
									f.createUnit(QUDTUnit.MilliMolePerCubicMeter))),
					f.createComponent(
							waterVapor,
							f.createComponentPropertyValue(
									r.nextDouble(),
									f.createUnit(QUDTUnit.MilliMolePerCubicMeter)))));
		}

		long end = System.currentTimeMillis();
		System.out.println("Time (s): " + elapsed(start, end));
	}

	private static String elapsed(long start, long end) {
		return (end - start) / 1000 + "."
				+ String.format("%03d", (end - start) % 1000);
	}

}
