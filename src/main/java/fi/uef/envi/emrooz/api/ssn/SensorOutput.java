/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api.ssn;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.api.AbstractEntity;
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

		this.value = value;
	}

	public int hashCode() {
		return 31 * (id.hashCode() + type.hashCode() + value.hashCode());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SensorOutput))
			return false;

		SensorOutput other = (SensorOutput) obj;

		if (other.id.equals(id) && other.type.equals(type)
				&& other.value.equals(value))
			return true;

		return false;
	}

}
