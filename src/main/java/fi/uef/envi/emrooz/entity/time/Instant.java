/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.time;

import org.joda.time.DateTime;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.TemporalEntityVisitor;
import static fi.uef.envi.emrooz.vocabulary.Time.Instant;

/**
 * <p>
 * Title: Instant
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

public class Instant extends TemporalEntity {

	private DateTime value;

	public Instant(URI id, DateTime value) {
		this(id, Instant, value);
	}

	public Instant(URI id, URI type, DateTime value) {
		super(id, type);

		addType(Instant);
		
		if (value == null)
			throw new NullPointerException("[value = null]");

		this.value = value;
	}

	public DateTime getValue() {
		return value;
	}

	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

	public void accept(TemporalEntityVisitor visitor) {
		visitor.visit(this);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + types.hashCode();
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

		Instant other = (Instant) obj;

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

		if (!types.equals(other.types))
			return false;

		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;

		return true;
	}

	public String toString() {
		return "Instant [id = " + id + "; type = " + type + "; types = "
				+ types + "; value = " + value + "]";
	}

}
