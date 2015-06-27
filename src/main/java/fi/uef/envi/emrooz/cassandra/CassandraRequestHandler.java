/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import org.joda.time.DateTime;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.Rollover;
import fi.uef.envi.emrooz.cassandra.utils.RowKeyUtils;
import fi.uef.envi.emrooz.entity.ssn.Frequency;

/**
 * <p>
 * Title: CassandraRequestHandler
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

public abstract class CassandraRequestHandler {

	private RowKeyUtils rowKeyUtils;

	public CassandraRequestHandler() {
		this.rowKeyUtils = new RowKeyUtils();
	}

	protected String getRowKey(URI sensorId, URI propertyId, URI featureId,
			Frequency frequency, DateTime time) {
		return rowKeyUtils.getRowKey(sensorId, propertyId, featureId,
				frequency, time);
	}

	protected Rollover getRollover(URI sensorId, URI propertyId, URI featureId,
			Frequency frequency) {
		return rowKeyUtils.getRollover(sensorId, propertyId, featureId,
				frequency);
	}

}
