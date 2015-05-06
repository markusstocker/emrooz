/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.ObservationValueVisitor;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: ObservationValueDouble
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

public class ObservationValueDouble extends ObservationValue {

	private Double value;

	public ObservationValueDouble(URI id) {
		this(id, SSN.ObservationValue);
	}

	public ObservationValueDouble(URI id, Double value) {
		this(id, SSN.ObservationValue, value);
	}

	public ObservationValueDouble(URI id, URI type) {
		this(id, type, Double.NaN);
	}

	public ObservationValueDouble(URI id, URI type, Double value) {
		super(id, type);

		this.value = value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getValue() {
		return value;
	}

	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

	public void accept(ObservationValueVisitor visitor) {
		visitor.visit(this);
	}

	public int hashCode() {
		return 31 * (id.hashCode() + type.hashCode() + value.hashCode());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof ObservationValueDouble))
			return false;

		ObservationValueDouble other = (ObservationValueDouble) obj;

		if (other.id.equals(id) && other.type.equals(type)
				&& other.value.equals(value))
			return true;

		return false;
	}

	public String toString() {
		return "ObservationValueDouble [id = " + id + "; type = " + type
				+ "; value = " + value + "]";
	}

}
