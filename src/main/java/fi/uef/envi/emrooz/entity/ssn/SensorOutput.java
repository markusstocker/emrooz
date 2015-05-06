/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: SensorOutput
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

public class SensorOutput extends AbstractEntity {

	private ObservationValue value;

	private static final Logger log = Logger.getLogger(SensorOutput.class
			.getName());

	public SensorOutput(URI id) {
		this(id, SSN.SensorOutput);
	}

	public SensorOutput(URI id, ObservationValue value) {
		this(id, SSN.SensorOutput, value);
	}

	public SensorOutput(URI id, URI type) {
		this(id, type, null);
	}

	public SensorOutput(URI id, URI type, ObservationValue value) {
		super(id, type);

		if (value != null)
			setValue(value);
	}

	public void setValue(ObservationValue value) {
		if (value == null) {
			if (log.isLoggable(Level.WARNING))
				log.warning("[value = null; output = " + toString() + "]");
		}

		this.value = value;
	}

	public ObservationValue getValue() {
		return value;
	}

	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

	public int hashCode() {
		if (value == null)
			return 31 * (id.hashCode() + type.hashCode());

		return 31 * (id.hashCode() + type.hashCode() + value.hashCode());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SensorOutput))
			return false;

		SensorOutput other = (SensorOutput) obj;

		if (other.id.equals(id) && other.type.equals(type)) {
			if (other.value == null && value == null)
				return true;
			if (other.value.equals(value))
				return true;
		}

		return false;
	}

	public String toString() {
		return "SensorOutput [id = " + id + "; type = " + type + "; value = "
				+ value + "]";
	}

}
