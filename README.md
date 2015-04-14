# Emrooz

Emrooz is a database for sensor data, specifically [SSN](http://www.w3.org/2005/Incubator/ssn/ssnx/ssn) observations. It supports the persistence and [SPARQL](http://www.w3.org/TR/rdf-sparql-query/) retrieval of SSN observations. Emrooz builds on [Apache Cassandra](http://cassandra.apache.org/) and [Sesame](http://rdf4j.org/).
 
## Requirements

* [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (Java 7u25 or later)
* [Apache Cassandra](http://cassandra.apache.org/) ([2.1.3](http://archive.apache.org/dist/cassandra/2.1.3/))
* [Emrooz](https://github.com/markusstocker/emrooz/releases)

## Installation

* Uncompress Cassandra on your system and execute '`bin/cassandra -f`' (you may want to read [getting started with Cassandra](http://wiki.apache.org/cassandra/GettingStarted), in particular Step 3)
* Create a new Java project using your favorite IDE, such as [Eclipse](http://www.eclipse.org/)
* Unzip Emrooz and import the JAR libraries into your project

## Usage

### Create

You can create an Emrooz instance to `localhost` as follows

    public static void main(String[] args) {
      Emrooz emrooz = new Emrooz();
    }

### Add

Sensor observations are added using the `addSensorObservation` method

    emrooz.addSensorObservation(statements);

The method takes a set of `org.openrdf.model.Statement`. Statements are [RDF](http://www.w3.org/TR/2004/REC-rdf-primer-20040210/) triples and they need to conform [SSN observations](http://www.w3.org/2005/Incubator/ssn/wiki/SSN_Observation) with observation result time conforming [OWL-Time](http://www.w3.org/TR/owl-time/). Let's look at an example:

    // First we need to define a namespace for our URIs, identifiers
    String ns = "http://example.org#";

    // Get an instance of `org.openrdf.model.ValueFactory`
    ValueFactory vf = ValueFactoryImpl.getInstance();

    // Next create the required `org.openrdf.model.URI` URIs
    // Identifier for the observation
    URI observationId = vf.createURI(ns + "o1");
    // Identifier for the sensor, some thermometer
    URI sensorId = vf.createURI(ns + "thermometer");
    // Identifier for the property, temperature
    URI propertyId = vf.createURI(ns + "temperature");
    // Identifier for the feature, i.e. the phenomenon observed, here ambient air
    URI featureId = vf.createURI(ns + "ambientAir");
    // And a few more identifiers for the time, sensor output, and its value
    URI resultTimeId = vf.createURI(ns + "rt1");
    URI outputId = vf.createURI(ns + "so1");
    URI valueId = vf.createURI(ns + "ov1");
    // Finally, we need two `org.openrdf.model.Literal` literals
    // One for the time of the observation, here April 14, 2015, at noon in Finnish timezone
    Literal time = vf.createLiteral("2015-04-14T12:00:00.000+03:00", XMLSchema.DATETIME);
    // And one for the value measured by the thermometer for ambient air temperature at noon on April 14, 2015
    Literal value = vf.createLiteral("7.8", XMLSchema.DOUBLE);

    // Next, initialize and populate the set of statements
    Set<Statement> statements = new HashSet<Statement>();

    // Statement with `fi.uef.envi.emrooz.vocabulary.SSN.observedBy` property
    statements.add(vf.createStatement(observationId, SSN.observedBy, sensorId));
    // Statement with `fi.uef.envi.emrooz.vocabulary.SSN.observedProperty` property
    statements.add(vf.createStatement(observationId, SSN.observedProperty, propertyId));
    // Statement with `fi.uef.envi.emrooz.vocabulary.SSN.featureOfInterest` property
    statements.add(vf.createStatement(observationId, SSN.featureOfInterest, featureId));
    // Statement with `fi.uef.envi.emrooz.vocabulary.SSN.observationResultTime` property
    statements.add(vf.createStatement(observationId, SSN.observationResultTime, resultTimeId));
    // Statement with `fi.uef.envi.emrooz.vocabulary.Time.inXSDDateTime` property
    statements.add(vf.createStatement(resultTimeId, Time.inXSDDateTime, time));
    // Statement with `fi.uef.envi.emrooz.vocabulary.SSN.observationResult` property
    statements.add(vf.createStatement(observationId, SSN.observationResult, outputId));
    // Statement with `fi.uef.envi.emrooz.vocabulary.DUL.hasRegion` property
    statements.add(vf.createStatement(outputId, DUL.hasRegion, valueId));
    // Statement with `fi.uef.envi.emrooz.vocabulary.DUL.hasRegionDataValue` property
    statements.add(vf.createStatement(valueId, DUL.hasRegionDataValue, value));

    // Next, register the sensor with property of feature observed
    // `Rollover` is an additional required parameter, and `DAY` is fine for this example
    // Note that you need to register a sensor only once
    emrooz.register(sensorId, propertyId, featureId, Rollover.DAY);

    // Finally, we are ready to instruct Emrooz to persist the SSN observation
    emrooz.addSensorObservation(statements);

    // Remember to close the Emrooz instance
    emrooz.close();

### Query

SSN observations can be retrieved using SPARQL. However, queries need to specify the sensor, property, feature as well as a time interval. The following lines demonstrate how the observations we just persisted can be retrieved.

First, the SPARQL query.

    prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#>
    prefix time: <http://www.w3.org/2006/time#>
    prefix dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>
    select ?time ?value
    where {
      [
        ssn:observedBy <http://example.org#thermometer> ;
        ssn:observedProperty <http://example.org#temperature> ;
        ssn:featureOfInterest <http://example.org#ambientAir> ;
        ssn:observationResultTime [ time:inXSDDateTime ?time ] ;
        ssn:observationResult [ dul:hasRegion [ dul:hasRegionDataValue ?value ] ]
      ]
      filter (?time >= "2015-04-14T06:00:00.000+03:00"^^xsd:dateTime 
        && ?time < "2015-04-14T18:00:00.000+03:00"^^xsd:dateTime)
    }
    order by asc(?time)

The Java code to execute the SPARQL query. Read the SPARQL query from file or, as suggested in this example, set it as value of the `String query` variable.

    // Create an Emrooz instance
    Emrooz emrooz = new Emrooz();

    // The string for the SPARQL query above
    String query = "...";

    // Execute the query
    List<BindingSet> results = emrooz.getSensorObservations(query);

    // Process the results by printing time and value to `System.out`
    for (BindingSet result : results) {
      System.out.println(result.getValue("time") + " " + result.getValue("value"));
    }

    // Remember to close
    emrooz.close();

### Drop

If you want to start over with a fresh database, you need to execute Cassandra `bin/cqlsh` and the command `drop keyspace emrooz;`.



