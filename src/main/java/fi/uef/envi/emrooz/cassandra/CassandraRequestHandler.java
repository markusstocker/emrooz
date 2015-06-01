/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import org.joda.time.DateTime;
import fi.uef.envi.emrooz.Rollover;
import fi.uef.envi.emrooz.cassandra.utils.RowKeyUtils;
import fi.uef.envi.emrooz.entity.ssn.Sensor;

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

	protected String getRowKey(Sensor specification, DateTime time) {
		return rowKeyUtils.getRowKey(specification, time);
	}

	protected Rollover getRollover(Sensor specification) {
		return rowKeyUtils.getRollover(specification);
	}

}
