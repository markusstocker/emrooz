/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.cassandra;

import static fi.uef.envi.emrooz.EmroozOptions.ROWKEY_DATETIME_PATTERN;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.Registration;
import fi.uef.envi.emrooz.Rollover;

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

	protected Map<String, Registration> registrations;
	protected Map<URI, Map<URI, Map<URI, String>>> registrationIdsMap;

	private DateTimeFormatter dtfRowKey = DateTimeFormat
			.forPattern(ROWKEY_DATETIME_PATTERN);
	private static final Logger log = Logger
			.getLogger(CassandraRequestHandler.class.getName());

	public CassandraRequestHandler(Map<String, Registration> registrations,
			Map<URI, Map<URI, Map<URI, String>>> registrationIdsMap) {
		if (registrations == null)
			throw new NullPointerException("[registrations = null]");
		if (registrationIdsMap == null)
			throw new NullPointerException("[registrationIdsMap = null]");

		this.registrations = registrations;
		this.registrationIdsMap = registrationIdsMap;
	}

	protected String getRowKey(URI sensor, URI property, URI feature,
			DateTime time) {
		if (sensor == null || property == null || feature == null
				|| time == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Parameters cannot be null [sensor = " + sensor
						+ "; propery = " + property + "; feature = " + feature
						+ "; time = " + time + "]");

			return null;
		}

		String registrationId = getCachedRegistrationId(sensor, property,
				feature);

		if (registrationId == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Registration id not found in cache [sensor = "
						+ sensor + "; propery = " + property + "; feature = "
						+ feature + "]");

			return null;
		}

		Registration registration = registrations.get(registrationId);

		if (registration == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Registration not found [sensor = " + sensor
						+ "; propery = " + property + "; feature = " + feature
						+ "]");

			return null;
		}

		Rollover rollover = registration.getRollover();

		if (rollover == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Registration rollover is null [registration = "
						+ registration + "]");

			return null;
		}

		if (rollover.equals(Rollover.YEAR))
			time = time.year().roundFloorCopy();
		else if (rollover.equals(Rollover.MONTH))
			time = time.monthOfYear().roundFloorCopy();
		else if (rollover.equals(Rollover.DAY))
			time = time.dayOfMonth().roundFloorCopy();
		else if (rollover.equals(Rollover.HOUR))
			time = time.hourOfDay().roundFloorCopy();
		else if (rollover.equals(Rollover.MINUTE))
			time = time.minuteOfHour().roundFloorCopy();
		else
			throw new RuntimeException("Unsupported rollover [rollover = "
					+ rollover + "]");

		return registrationId + "-" + dtfRowKey.print(time);
	}

	protected String getCachedRegistrationId(URI sensor, URI property,
			URI feature) {
		Map<URI, Map<URI, String>> m1 = registrationIdsMap.get(sensor);

		if (m1 == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Sensor not registered [sensor = " + sensor
						+ "; registrationIdsMap = " + registrationIdsMap + "]");

			return null;
		}

		Map<URI, String> m2 = m1.get(property);

		if (m2 == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Property not registered [sensor = " + sensor
						+ "; property = " + property
						+ "; registrationIdsMap = " + registrationIdsMap + "]");

			return null;
		}

		String ret = m2.get(feature);

		if (ret == null) {
			if (log.isLoggable(Level.SEVERE))
				log.severe("Feature not registered [sensor = " + sensor
						+ "; property = " + property + "; feature = " + feature
						+ "; registrationIdsMap = " + registrationIdsMap + "]");

			return null;
		}

		return ret;
	}

}
