/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.io;

import java.util.UUID;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import fi.uef.envi.emrooz.api.SensorObservationReader;

/**
 * <p>
 * Title: AbstractSensorObservationReader
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

public abstract class AbstractSensorObservationReader implements
		SensorObservationReader {

	private URI ns;
	protected static final ValueFactory vf = ValueFactoryImpl.getInstance();
	protected static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public AbstractSensorObservationReader(URI ns) {
		if (ns == null)
			throw new NullPointerException("[ns = null]");
		
		this.ns = ns;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"This sensor observation reader those not support removing sensor observations from the iterator.");
	}
	
	protected URI _id() {
		return _id(ns);
	}
	
	protected static URI _id(URI ns) {
		String s = ns.stringValue();

		if (s.endsWith("#"))
			return vf.createURI(ns + UUID.randomUUID().toString());

		return vf.createURI(ns + "#" + UUID.randomUUID().toString());
	}

}
