/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz;

import org.apache.commons.codec.digest.DigestUtils;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * <p>
 * Title: Registration
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

public class Registration {

	private String id;
	private URI sensor;
	private URI property;
	private URI feature;
	private Rollover rollover;
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();

	public Registration(String sensor, String property, String feature,
			String rollover) {
		this(vf.createURI(sensor), vf.createURI(property), vf
				.createURI(feature), Rollover.valueOf(rollover));
	}

	public Registration(URI sensor, URI property, URI feature, Rollover rollover) {
		if (sensor == null || property == null || feature == null
				|| rollover == null) {
			throw new NullPointerException(
					"Error in creating registration [sensor = " + sensor
							+ "; property = " + property + "; feature = "
							+ feature + "; rollover = " + rollover + "]");
		}

		this.id = DigestUtils.sha1Hex(sensor.stringValue() + "-"
				+ property.stringValue() + "-" + feature.stringValue());
		this.sensor = sensor;
		this.property = property;
		this.feature = feature;
		this.rollover = rollover;
	}

	public String getId() {
		return id;
	}

	public URI getSensor() {
		return sensor;
	}

	public URI getProperty() {
		return property;
	}

	public URI getFeature() {
		return feature;
	}

	public Rollover getRollover() {
		return rollover;
	}

	public int hashCode() {
		return 31 * sensor.hashCode() * property.hashCode()
				* feature.hashCode() * rollover.hashCode();
	}

	public boolean equals(Object other) {
		if (!(other instanceof Registration))
			return false;

		Registration o = (Registration) other;

		if (o.getSensor().equals(sensor) && o.getProperty().equals(property)
				&& o.getFeature().equals(feature)
				&& o.getRollover().equals(rollover))
			return true;

		return false;
	}

	public String toString() {
		return "Registration [id = " + id + "; sensor = " + sensor
				+ "; property = " + property + "; feature = " + feature
				+ "; rollover = " + rollover + "]";
	}
}
