/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.time;

import org.joda.time.DateTime;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.entity.TemporalEntityVisitor;
import fi.uef.envi.emrooz.vocabulary.Time;

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
		this(id, Time.Instant, value);
	}

	public Instant(URI id, URI type, DateTime value) {
		super(id, type);

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
		return 31 * (id.hashCode() + type.hashCode() + value.hashCode());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Instant))
			return false;

		Instant other = (Instant) obj;

		if (other.id.equals(id) && other.type.equals(type)
				&& other.value.equals(value))
			return true;

		return false;
	}

	public String toString() {
		return "Instant [id = " + id + "; type = " + type + "; value = "
				+ value + "]";
	}

}
