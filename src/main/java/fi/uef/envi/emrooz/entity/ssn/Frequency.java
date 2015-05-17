/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.ssn;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.MeasurementPropertyVisitor;
import fi.uef.envi.emrooz.entity.qudt.QuantityValue;
import fi.uef.envi.emrooz.vocabulary.SSN;

/**
 * <p>
 * Title: Frequency
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

public class Frequency extends MeasurementProperty {

	private QuantityValue value;

	public Frequency(URI id) {
		this(id, SSN.Frequency, null);
	}
	
	public Frequency(URI id, QuantityValue value) {
		this(id, SSN.Frequency, value);
	}

	public Frequency(URI id, URI type, QuantityValue value) {
		super(id, type);
		
		setQuantityValue(value);
	}

	public void setQuantityValue(QuantityValue value) {
		this.value = value;
	}

	public QuantityValue getQuantityValue() {
		return value;
	}

	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}
	
	public void accept(MeasurementPropertyVisitor visitor) {
		visitor.visit(this);
	}

	public int hashCode() {
		return 31 * (id.hashCode() + type.hashCode());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Frequency))
			return false;

		Frequency other = (Frequency) obj;

		if (other.id.equals(id) && other.type.equals(type))
			return true;

		return false;
	}

	public String toString() {
		return "Frequency [id = " + id + "; type = " + type + "; value = "
				+ value + "]";
	}

}
