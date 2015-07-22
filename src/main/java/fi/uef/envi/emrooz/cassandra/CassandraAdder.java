/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE;
import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_1;
import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_2;
import static fi.uef.envi.emrooz.EmroozOptions.DATA_TABLE_ATTRIBUTE_3;
import static fi.uef.envi.emrooz.EmroozOptions.KEYSPACE;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import com.carmatech.cassandra.TimeUUID;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import fi.uef.envi.emrooz.cassandra.utils.StatementUtils;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.entity.ssn.Frequency;

/**
 * <p>
 * Title: CassandraAdder
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

public class CassandraAdder extends CassandraRequestHandler {

	private Session session;
	private PreparedStatement insertStatement;

	private static final Logger log = Logger.getLogger(CassandraAdder.class
			.getName());

	public CassandraAdder(Session session) {
		if (session == null)
			throw new NullPointerException("[session = null]");

		this.session = session;
		this.insertStatement = this.session.prepare("INSERT INTO " + KEYSPACE
				+ "." + DATA_TABLE + " (" + DATA_TABLE_ATTRIBUTE_1 + ","
				+ DATA_TABLE_ATTRIBUTE_2 + "," + DATA_TABLE_ATTRIBUTE_3
				+ ") VALUES (?, ?, ?)");
	}

	public void addSensorObservation(URI sensorId, URI propertyId,
			URI featureId, Frequency frequency, DateTime resultTime,
			Set<Statement> statements) {
		addSensorObservation(
				getRowKey(sensorId, propertyId, featureId, frequency,
						resultTime), resultTime, statements);
	}

	private void addSensorObservation(String rowKey, DateTime resultTime,
			Set<Statement> columnValue) {
		addSensorObservation(rowKey, TimeUUID.toUUID(resultTime), columnValue);
	}

	private void addSensorObservation(String rowKey, UUID resultTime,
			Set<Statement> columnValue) {
		addSensorObservation(rowKey, resultTime,
				StatementUtils.toByteArray(columnValue));
	}

	private void addSensorObservation(String rowKey, UUID columnName,
			byte[] columnValue) {
		if (rowKey == null || columnName == null || columnValue == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("At least one parameter is null (possibly the byte[] columnValue [rowKey = "
						+ rowKey + "; columnName = " + columnName + "]");
			return;
		}

		session.execute(new BoundStatement(insertStatement).bind(rowKey,
				columnName, ByteBuffer.wrap(columnValue)));
	}

	public void addDatasetObservation(URI datasetId, QuantityValue frequency,
			DateTime timePeriod, Set<Statement> statements) {
		addDatasetObservation(getRowKey(datasetId, frequency, timePeriod),
				timePeriod, statements);
	}

	private void addDatasetObservation(String rowKey, DateTime timePeriod,
			Set<Statement> columnValue) {
		addDatasetObservation(rowKey, TimeUUID.toUUID(timePeriod), columnValue);
	}

	private void addDatasetObservation(String rowKey, UUID timePeriod,
			Set<Statement> columnValue) {
		addDatasetObservation(rowKey, timePeriod,
				StatementUtils.toByteArray(columnValue));
	}

	private void addDatasetObservation(String rowKey, UUID columnName,
			byte[] columnValue) {
		if (rowKey == null || columnName == null || columnValue == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("At least one parameter is null (possibly the byte[] columnValue [rowKey = "
						+ rowKey + "; columnName = " + columnName + "]");
			return;
		}

		session.execute(new BoundStatement(insertStatement).bind(rowKey,
				columnName, ByteBuffer.wrap(columnValue)));
	}

}
