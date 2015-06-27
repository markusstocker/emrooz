/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE;
import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_1;
import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_2;
import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_3;
import static fi.uef.envi.emrooz.EmroozOptions.HOST;
import static fi.uef.envi.emrooz.EmroozOptions.KEYSPACE;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;

import fi.uef.envi.emrooz.api.DataStore;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.query.SensorObservationQuery;

/**
 * <p>
 * Title: DataStore
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

public class CassandraDataStore implements DataStore {

	private Cluster cluster;
	private Session session;
	private String host;

	private CassandraAdder cassandraAdder;
	private PreparedStatement sensorObservationSelectStatement;

	private static final Logger log = Logger.getLogger(CassandraDataStore.class
			.getName());

	public CassandraDataStore() {
		this(HOST);
	}

	public CassandraDataStore(String host) {
		if (host == null)
			throw new NullPointerException(
					"Data store host cannot be null [host = null]");

		this.host = host;
		this.cluster = Cluster.builder().addContactPoint(this.host).build();

		initialize();
		connect();

		this.sensorObservationSelectStatement = session.prepare("SELECT "
				+ DATA_TABLE_ATTRIBUTE_3 + " FROM " + KEYSPACE + "."
				+ DATA_TABLE + " WHERE " + DATA_TABLE_ATTRIBUTE_1 + "=? AND "
				+ DATA_TABLE_ATTRIBUTE_2 + ">=minTimeuuid(?) AND "
				+ DATA_TABLE_ATTRIBUTE_2 + "<minTimeuuid(?)");

		this.cassandraAdder = new CassandraAdder(session);
	}

	@Override
	public void addSensorObservation(URI sensorId, URI propertyId,
			URI featureId, Frequency frequency, DateTime resultTime,
			Set<Statement> statements) {
		if (sensorId == null || propertyId == null || featureId == null
				|| frequency == null || resultTime == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("At least one parameter is null [sensorId = "
						+ sensorId + "; propertyId = " + propertyId
						+ "; featureId = " + featureId + "; frequency = "
						+ frequency + "; resultTime = " + resultTime + "]");
			return;
		}

		if (statements.isEmpty()) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Empty collection of statements [[sensorId = "
						+ sensorId + "; propertyId = " + propertyId
						+ "; featureId = " + featureId + "; frequency = "
						+ frequency + "; resultTime = " + resultTime
						+ "; statements = " + statements + "]");
			return;
		}

		cassandraAdder.addSensorObservation(sensorId, propertyId, featureId,
				frequency, resultTime, statements);
	}

	@Override
	public CassandraQueryHandler createQueryHandler(
			Map<SensorObservationQuery, Frequency> queries) {
		return new CassandraQueryHandler(session,
				sensorObservationSelectStatement, queries);
	}

	@Override
	public void close() {
		session.close();
		cluster.close();
	}

	private void initialize() {
		Session session = cluster.connect();
		Metadata metadata = cluster.getMetadata();
		KeyspaceMetadata keyspaceMetadata = metadata.getKeyspace(KEYSPACE);

		if (keyspaceMetadata == null) {
			session.execute("CREATE KEYSPACE "
					+ KEYSPACE
					+ " WITH REPLICATION = { 'class' : 'org.apache.cassandra.locator.SimpleStrategy', 'replication_factor': '1' } AND DURABLE_WRITES = true;");
		}

		session = cluster.connect(KEYSPACE);
		metadata = cluster.getMetadata();
		keyspaceMetadata = metadata.getKeyspace(KEYSPACE);
		TableMetadata dataTableMetadata = keyspaceMetadata.getTable(DATA_TABLE);

		if (dataTableMetadata == null) {
			session.execute("CREATE TABLE "
					+ KEYSPACE
					+ "."
					+ DATA_TABLE
					+ " ("
					+ DATA_TABLE_ATTRIBUTE_1
					+ " ascii,"
					+ DATA_TABLE_ATTRIBUTE_2
					+ " timeuuid,"
					+ DATA_TABLE_ATTRIBUTE_3
					+ " blob,PRIMARY KEY ("
					+ DATA_TABLE_ATTRIBUTE_1
					+ ", "
					+ DATA_TABLE_ATTRIBUTE_2
					+ ")) WITH COMPACT STORAGE AND read_repair_chance = 0.0 AND dclocal_read_repair_chance = 0.1 AND gc_grace_seconds = 864000 AND bloom_filter_fp_chance = 0.01 AND caching = { 'keys' : 'ALL', 'rows_per_partition' : 'NONE' } AND comment = '' AND compaction = { 'class' : 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy' } AND compression = { 'sstable_compression' : 'org.apache.cassandra.io.compress.LZ4Compressor' } AND default_time_to_live = 0 AND speculative_retry = 'NONE' AND min_index_interval = 128 AND max_index_interval = 2048;");
		}
	}

	private void connect() {
		session = cluster.connect(KEYSPACE);
	}

}
