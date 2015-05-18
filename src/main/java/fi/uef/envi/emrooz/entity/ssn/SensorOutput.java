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
		final int prime = 31;
		int result = 1;

		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		SensorOutput other = (SensorOutput) obj;

		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;

		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;

		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;

		return true;
	}

	public String toString() {
		return "SensorOutput [id = " + id + "; type = " + type + "; value = "
				+ value + "]";
	}

}
