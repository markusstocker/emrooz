/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import com.carmatech.cassandra.TimeUUID;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.utils.Bytes;

import fi.uef.envi.emrooz.utils.ConverterUtil;

/**
 * <p>
 * Title: Emrooz
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

public class Emrooz {
	private String host = "127.0.0.1";
	private String keyspace = "emrooz";
	private String dataTable = "data";
	private String registrationsTable = "registrations";

	private Cluster cluster;
	private Session session;
	private Map<String, Map<String, String>> registrations;

	private static final Logger log = Logger.getLogger(Emrooz.class.getName());

	public Emrooz() {
		this(null);
	}

	public Emrooz(String host) {
		this(host, null);
	}

	public Emrooz(String host, String keyspace) {
		if (host != null)
			this.host = host;
		if (keyspace != null)
			this.keyspace = keyspace;

		this.cluster = Cluster.builder().addContactPoint(host).build();
		this.registrations = new HashMap<String, Map<String, String>>();

		initialize();
		connect();
		registrations();
	}

	public String getHost() {
		return host;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public String getDataTable() {
		return dataTable;
	}

	public void register(URI sensor, URI property, URI feature, String rollover) {
		String s = sensor.stringValue();
		String p = property.stringValue();
		String f = feature.stringValue();

		String registrationId = getRegistrationId(s, p, f);

		if (registrations.containsKey(registrationId)) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Registrations exists [sensor = " + s
						+ "; property = " + p + "; feature = " + f + "]");

			return;
		}

		PreparedStatement statement = session.prepare("INSERT INTO " + keyspace
				+ "." + registrationsTable
				+ " (id,sensor,property,feature,rollover) VALUES (?,?,?,?,?)");
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement.bind(registrationId, s, p, f, rollover));

		registrations();
	}

	public void addSensorObservation(Set<Statement> statements) {
		
	}
	
	public void addSensorObservation(URI sensor, URI property, URI feature,
			DateTime columnName, Set<Statement> columnValue) {
		addSensorObservation(getRowKey(sensor, property, feature, columnName),
				columnName, columnValue);
	}

	public void addSensorObservation(String rowKey, DateTime columnName,
			Set<Statement> columnValue) {
		addSensorObservation(rowKey, TimeUUID.toUUID(columnName), columnValue);
	}

	public void addSensorObservation(String rowKey, UUID columnName,
			Set<Statement> columnValue) {
		addSensorObservation(rowKey, columnName,
				ConverterUtil.toByteArray(columnValue));
	}

	public void addSensorObservation(String rowKey, UUID columnName,
			byte[] columnValue) {
		PreparedStatement statement = session.prepare("INSERT INTO " + keyspace
				+ "." + dataTable + " (key,column1,value) VALUES (?, ?, ?)");
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement.bind(rowKey, columnName,
				ByteBuffer.wrap(columnValue)));
	}

	public Set<Statement> getSensorObservations(URI sensor, URI property,
			URI feature, DateTime timeFrom, DateTime timeTo) {
		// TODO Get set of row keys for the time range
		return getSensorObservations(
				getRowKey(sensor, property, feature, timeFrom), timeFrom,
				timeTo);
	}

	public Set<Statement> getSensorObservations(String rowKey,
			DateTime timeFrom, DateTime timeTo) {
		return getSensorObservations(rowKey, TimeUUID.toUUID(timeFrom),
				TimeUUID.toUUID(timeTo));
	}

	public Set<Statement> getSensorObservations(String rowKey,
			UUID columnNameFrom, UUID columnNameTo) {
		Set<Statement> statements = new HashSet<Statement>();

		PreparedStatement statement = session.prepare("SELECT value FROM "
				+ keyspace + "." + dataTable
				+ " WHERE key=? AND column1>=? AND column1<?");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet results = session.execute(boundStatement.bind(rowKey,
				columnNameFrom, columnNameTo));

		for (Row row : results) {
			ConverterUtil.toStatements(Bytes.getArray(row.getBytes("value")),
					statements);
		}

		return Collections.unmodifiableSet(statements);
	}

	public void close() {
		cluster.close();
	}

	private void registrations() {
		registrations.clear();

		ResultSet rows = session
				.execute("SELECT id,sensor,property,feature,rollover FROM "
						+ keyspace + "." + registrationsTable);

		for (Row row : rows) {
			String id = row.getString("id");
			String sensor = row.getString("sensor");
			String property = row.getString("property");
			String feature = row.getString("feature");
			String rollover = row.getString("rollover");

			Map<String, String> m = new HashMap<String, String>();
			registrations.put(id, m);

			m.put("sensor", sensor);
			m.put("property", property);
			m.put("feature", feature);
			m.put("rollover", rollover);
		}
	}

	private void initialize() {
		Session session = cluster.connect();
		Metadata metadata = cluster.getMetadata();
		KeyspaceMetadata keyspaceMetadata = metadata.getKeyspace(keyspace);

		if (keyspaceMetadata == null) {
			session.execute("CREATE KEYSPACE "
					+ keyspace
					+ " WITH REPLICATION = { 'class' : 'org.apache.cassandra.locator.SimpleStrategy', 'replication_factor': '1' } AND DURABLE_WRITES = true;");
		}

		session = cluster.connect(keyspace);
		metadata = cluster.getMetadata();
		keyspaceMetadata = metadata.getKeyspace(keyspace);
		TableMetadata dataTableMetadata = keyspaceMetadata.getTable(dataTable);

		if (dataTableMetadata == null) {
			session.execute("CREATE TABLE "
					+ keyspace
					+ "."
					+ dataTable
					+ " (key ascii,column1 timeuuid,value blob,PRIMARY KEY (key, column1)) WITH COMPACT STORAGE AND read_repair_chance = 0.0 AND dclocal_read_repair_chance = 0.1 AND gc_grace_seconds = 864000 AND bloom_filter_fp_chance = 0.01 AND caching = { 'keys' : 'ALL', 'rows_per_partition' : 'NONE' } AND comment = '' AND compaction = { 'class' : 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy' } AND compression = { 'sstable_compression' : 'org.apache.cassandra.io.compress.LZ4Compressor' } AND default_time_to_live = 0 AND speculative_retry = 'NONE' AND min_index_interval = 128 AND max_index_interval = 2048;");
		}

		TableMetadata registrationsTableMetadata = keyspaceMetadata
				.getTable(registrationsTable);

		if (registrationsTableMetadata == null) {
			session.execute("CREATE TABLE "
					+ keyspace
					+ "."
					+ registrationsTable
					+ " (id ascii PRIMARY KEY, sensor ascii, property ascii, feature ascii, rollover ascii);");
		}
	}

	private String getRowKey(URI sensor, URI property, URI feature,
			DateTime time) {
		String s = sensor.stringValue();
		String p = property.stringValue();
		String f = feature.stringValue();

		String registrationId = getRegistrationId(s, p, f);

		Map<String, String> registration = registrations.get(registrationId);

		if (registration == null)
			throw new RuntimeException("Registration not found [sensor = " + s
					+ "; propery = " + p + "; feature = " + f + "]");

		String rollover = registration.get("rollover");

		if (rollover == null)
			throw new RuntimeException(
					"Registration rollover is null [registration = "
							+ registration + "]");

		if (rollover.equals("YEAR"))
			time = time.year().roundFloorCopy();
		else if (rollover.equals("MONTH"))
			time = time.monthOfYear().roundFloorCopy();
		else if (rollover.equals("DAY"))
			time = time.dayOfMonth().roundFloorCopy();
		else if (rollover.equals("HOUR"))
			time = time.hourOfDay().roundFloorCopy();
		else if (rollover.equals("MINUTE"))
			time = time.minuteOfHour().roundFloorCopy();
		else
			throw new RuntimeException("Unsupported rollover [rollover = "
					+ rollover + "]");

		return registrationId + "-"
				+ DateTimeFormat.forPattern("yyyyMMddHHmmss").print(time);
	}

	private String getRegistrationId(String s, String p, String f) {
		return DigestUtils.sha1Hex(s + "-" + p + "-" + f);
	}

	private void connect() {
		session = cluster.connect(keyspace);
	}

}
