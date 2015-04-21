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

The following sections describe how to add and query observations with Emrooz.

### Initialize

You can create an Emrooz instance to `localhost` as follows

    public static void main(String[] args) {
      // Initialize ...
      Emrooz emrooz = new Emrooz();

      // ... and remember to close
      emrooz.close();
    }

### Register

As a first step, we need to register sensors we are using. Such registration take a `Sensor`, a `Property`, a `Feature`, and a `Rollover`. When Emrooz stores sensor observations in sequence, the rollover tells Emrooz after what period it should start a new row. Rows should not be _too long_, meaning that if your sensor samples at high frequency, say 10 or 100 Hz, you should consider `Rollover.DAY`. For even higher frequencies you can set rollover to `HOUR` or even `MINUTE`. For sensor sampling at lower frequency you can set rollover to `MONTH` or `YEAR`.

A sensor registration is done as follows:

    EntityFactory f = EntityFactory.getInstance("http://example.org#");

    Sensor sensor = f.createSensor("thermometer");
    Property property = f.createProperty("temperature");
    FeatureOfInterest feature = f.createFeatureOfInterest("air");
 
    emrooz.register(sensor, property, feature, Rollover.DAY);

You can find a [complete example](https://github.com/markusstocker/emrooz/blob/master/src/examples/java/fi/uef/envi/emrooz/examples/SensorRegistrationExample.java) in the sources.

### Add

Emrooz supports the addition of sensor observations in several forms. The most straightforward approach is to use the available API classes to create sensor observations. Here is an example:

    EntityFactory f = EntityFactory.getInstance("http://example.org#");

    // Create a sensor observation made by the thermometer for temperature of air on April 21, 2015 at 1 am
    SensorObservation observation = f.createSensorObservation(
      "thermometer", "temperature", "air", 7.6,
      "2015-04-21T01:00:00.000+03:00");

    // And store the observation
    emrooz.add(observation);

You can find a [complete example](https://github.com/markusstocker/emrooz/blob/master/src/examples/java/fi/uef/envi/emrooz/examples/AddSensorObservationExample.java) in the sources.

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
        ssn:featureOfInterest <http://example.org#air> ;
        ssn:observationResultTime [ time:inXSDDateTime ?time ] ;
        ssn:observationResult [ ssn:hasValue [ dul:hasRegionDataValue ?value ] ]
      ]
      filter (?time >= "2015-04-14T06:00:00.000+03:00"^^xsd:dateTime 
        && ?time < "2015-04-14T18:00:00.000+03:00"^^xsd:dateTime)
    }
    order by asc(?time)

The Java code to execute the SPARQL query. Read the SPARQL query from file or, as suggested in this example, set it as value of the `String query` variable.

    String query = "...";

    // Execute the query
    List<BindingSet> results = emrooz.getSensorObservations(query);

    // Process the results by printing time and value to `System.out`
    for (BindingSet result : results) {
      System.out.println(result.getValue("time") + " " + result.getValue("value"));
    }

You can find a [complete example](https://github.com/markusstocker/emrooz/blob/master/src/examples/java/fi/uef/envi/emrooz/examples/QuerySensorObservationsExample.java) in the sources.

### Drop

If you want to start over with a fresh database, you need to execute Cassandra `bin/cqlsh` and the command `drop keyspace emrooz;`.



