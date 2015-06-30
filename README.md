# Emrooz

Emrooz is a database for sensor data, specifically [SSN](http://www.w3.org/2005/Incubator/ssn/ssnx/ssn) observations. It supports the persistence and [SPARQL](http://www.w3.org/TR/rdf-sparql-query/) retrieval of SSN observations. Emrooz builds on [Apache Cassandra](http://cassandra.apache.org/) and [Sesame](http://rdf4j.org/).
 
## Requirements

* [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (Java 7u25 or later)
* [Apache Cassandra](http://cassandra.apache.org/) ([2.1.3](http://archive.apache.org/dist/cassandra/2.1.3/))
* [Emrooz](https://github.com/markusstocker/emrooz/releases)

## Installation

* Uncompress Cassandra on your system and execute '`bin/cassandra -f`' to start Cassandra. Make sure the system user has the necessary privileges. Wait for a few seconds until Cassandra tells you it is `Listening for thrift clients...`. For further information, you may want to read [getting started with Cassandra](http://wiki.apache.org/cassandra/GettingStarted), in particular Step 3.
* Run `bin/cqlsh` to see if you can get access to the Cassandra shell
* Uncompress Emrooz on your system

## Usage

Emrooz can be used from the command line (experimental) and programmatically. To start, and test if things work, you can try loading and querying the test data. To do so follow these steps:

* Navigate to the Emrooz `bin/` folder
* Run `load.sh` and `query.sh`

    ./load.sh -f ../resources/example-1.data \
                -ns http://example.org \
                -sid http://example.org#thermometer \
                -pid http://example.org#temperature \
                -fid http://example.org#air \
                -sf 0.000016 \
                -uid http://qudt.org/vocab/unit#DegreeCelsius \
                -ks /tmp/ks \
                -ds localhost

    ./query.sh -q ../resources/example-1.rq \
                 -ks /tmp/ks \
                 -ds localhost
                 
We first load the `resources/example-1.data` using the URIs for the sensor (`-sid`), the property (`-pid`), the feature (`-fid`), and the unit (`-uid`). These URIs correspond to those in the data, which is a CSV text file. We also specify the sampling frequency [Hz] of the sensor (`-sf`). Finally, we specify the directory to which the knowledge store is persisted (`-ks`) and the host on which the (Cassandra) data store runs.

Then we query the example data with the example query `example-1.rq` by specifying the knowledge and the data stores.

You should get a list of results.

## Programming

The following sections describe how to add and query observations programmatically in Emrooz. 

A [complete example](https://github.com/markusstocker/emrooz/blob/master/src/examples/java/fi/uef/envi/emrooz/examples/CompletePersistentExample.java) can be found in the sources.

### Initialize

You can create an Emrooz instance with a file-based persistent knowledge store and a data store on `localhost` as follows.

    public static void main(String[] args) {
      // Initialize ...
      Emrooz emrooz = new Emrooz(new SesameKnowledgeStore(new SailRepository(
				                 new MemoryStore(new File("/tmp/ks")))),
				                 new CassandraDataStore());

      // ... and remember to close
      emrooz.close();
    }
    
The knowledge store implementation is for [Sesame](http://rdf4j.org/). It is thus an RDF store. Sesame supports various types of stores, including volatile in-memory stores and persistent disk-based stores. For more information, check the [Sesame documentation](http://rdf4j.org/documentation.docbook?view).

### Sensor specification and registration

As a first step, we need to specify and register the sensors we are using. Sensor specifications include identifiers for a sensor, one or more properties, one or more features, and a sampling frequency [Hz].

Sensor specifications are managed by the knowledge store.

There are a couple of ways how to create a sensor specification. The easiest is using the `EntityFactory` as follows

    EntityFactory f = EntityFactory.getInstance("http://example.org#");

	emrooz.add(f.createSensor("aThermometer", "temperature", "air", 1.0));

You can find [sensor specification examples](https://github.com/markusstocker/emrooz/blob/master/src/examples/java/fi/uef/envi/emrooz/examples/SensorSpecificationExample.java) in the sources.

### Add sensor observations

Emrooz supports adding sensor observations in several forms. The following example uses the `EntityFactory`.

    EntityFactory f = EntityFactory.getInstance("http://example.org#");

    // Create and add a sensor observation made by the thermometer for temperature of air on April 21, 2015 at 1 am
    emrooz.add(f.createSensorObservation("thermometer", "temperature", "air", 
                                         7.6, "2015-04-21T01:00:00.000+03:00"));

The sources contain [further examples](https://github.com/markusstocker/emrooz/blob/master/src/examples/java/fi/uef/envi/emrooz/examples/AddSensorObservationExample.java).

### Query sensor observations

SSN observations can be retrieved using SPARQL. However, queries need to follow the structure of SSN observations and must specify a time interval. For performance reasons, it is good to also specify the sensor, property, and feature. However, if these are left undefined, then Emrooz will use the knowledge store to resolve undefined sensor, property, or feature. 

The following lines demonstrate how the observations we just persisted can be retrieved.

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
      filter (?time >= "2015-04-21T00:00:00.000+03:00"^^xsd:dateTime 
        && ?time < "2015-04-21T02:00:00.000+03:00"^^xsd:dateTime)
    }
    order by asc(?time)

The Java code to execute the SPARQL query. Read the SPARQL query from file or, as suggested in this example, set it as value of the `String query` variable.

    String query = "...";

    // Execute the query
    ResultSet<BindingSet> results = emrooz.evaluate(sparql);

    // Process the results by printing time and value to `System.out`
    while (results.hasNext()) {
	  Sstem.out.println(results.next());
    }

    results.close();

You can find a [complete example](https://github.com/markusstocker/emrooz/blob/master/src/examples/java/fi/uef/envi/emrooz/examples/QuerySensorObservationsExample.java) in the sources.

### Drop

If you want to start over with a fresh database, you need to execute Cassandra `bin/cqlsh` and the command `drop keyspace emrooz;`.