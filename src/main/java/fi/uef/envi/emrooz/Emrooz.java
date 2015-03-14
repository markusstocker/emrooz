/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.binary.BinaryRDFWriter;
import org.openrdf.rio.helpers.StatementCollector;

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

	private Cluster cluster;
	private Session session;
	private RDFParser rdfParser;

	private boolean isConnected = false;

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

		this.rdfParser = Rio.createParser(RDFFormat.BINARY);
		this.cluster = Cluster.builder().addContactPoint(host).build();
		
		initialize();
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
	
	public void connect() {
		session = cluster.connect(keyspace);
		isConnected = true;
	}

	public void addSensorObservation(String rowKey, DateTime columnName,
			Set<Statement> columnValue) {
		addSensorObservation(rowKey, TimeUUID.toUUID(columnName), columnValue);
	}

	public void addSensorObservation(String rowKey, UUID columnName,
			Set<Statement> columnValue) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		RDFHandler rdfHandler = new BinaryRDFWriter(os);

		rdfParser.setRDFHandler(rdfHandler);

		try {
			rdfHandler.startRDF();

			for (Statement statement : columnValue) {
				rdfHandler.handleStatement(statement);
			}

			rdfHandler.endRDF();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}

		addSensorObservation(rowKey, columnName, os.toByteArray());
	}

	public void addSensorObservation(String rowKey, UUID columnName,
			byte[] columnValue) {
		if (!isConnected)
			connect();

		PreparedStatement statement = session.prepare("INSERT INTO " + keyspace
				+ "." + dataTable + " (key,column1,value) VALUES (?, ?, ?)");
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement.bind(rowKey, columnName,
				ByteBuffer.wrap(columnValue)));
	}

	public Set<Statement> getSensorObservations(String rowKey,
			DateTime timeFrom, DateTime timeTo) {
		return getSensorObservations(rowKey, TimeUUID.toUUID(timeFrom),
				TimeUUID.toUUID(timeTo));
	}

	public Set<Statement> getSensorObservations(String rowKey,
			UUID columnNameFrom, UUID columnNameTo) {
		if (!isConnected)
			connect();

		Set<Statement> ret = new HashSet<Statement>();
		StatementCollector collector = new StatementCollector(ret);
		rdfParser.setRDFHandler(collector);

		PreparedStatement statement = session.prepare("SELECT value FROM "
				+ keyspace + "." + dataTable
				+ " WHERE key=? AND column1>=? AND column1<?");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet results = session.execute(boundStatement.bind(rowKey,
				columnNameFrom, columnNameTo));

		for (Row row : results) {
			ByteBuffer value = row.getBytes("value");
			byte[] bytes = Bytes.getArray(value);

			ByteArrayInputStream is = new ByteArrayInputStream(bytes);

			try {
				rdfParser.parse(is, null);
			} catch (RDFParseException | RDFHandlerException | IOException e) {
				e.printStackTrace();
			}
		}

		return Collections.unmodifiableSet(ret);
	}

	public void close() {
		cluster.close();
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
		TableMetadata tableMetadata = keyspaceMetadata.getTable(dataTable);

		if (tableMetadata == null) {
			session.execute("CREATE TABLE "
					+ keyspace
					+ "."
					+ dataTable
					+ " (key ascii,column1 timeuuid,value blob,PRIMARY KEY (key, column1)) WITH COMPACT STORAGE AND read_repair_chance = 0.0 AND dclocal_read_repair_chance = 0.1 AND gc_grace_seconds = 864000 AND bloom_filter_fp_chance = 0.01 AND caching = { 'keys' : 'ALL', 'rows_per_partition' : 'NONE' } AND comment = '' AND compaction = { 'class' : 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy' } AND compression = { 'sstable_compression' : 'org.apache.cassandra.io.compress.LZ4Compressor' } AND default_time_to_live = 0 AND speculative_retry = 'NONE' AND min_index_interval = 128 AND max_index_interval = 2048;");
		}
	}

}
