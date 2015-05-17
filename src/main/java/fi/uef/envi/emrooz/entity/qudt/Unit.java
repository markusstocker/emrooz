/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity.qudt;

import org.openrdf.model.URI;

import fi.uef.envi.emrooz.entity.AbstractEntity;
import fi.uef.envi.emrooz.entity.EntityVisitor;
import fi.uef.envi.emrooz.vocabulary.QUDTSchema;

/**
 * <p>
 * Title: Unit
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

public class Unit extends AbstractEntity {

	public Unit(URI id) {
		super(id, QUDTSchema.Unit);
	}

	@Override
	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public int hashCode() {
		return 31 * (id.hashCode() + type.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Unit))
			return false;

		Unit other = (Unit) obj;

		if (other.id.equals(id) && other.type.equals(type))
			return true;

		return false;
	}

	@Override
	public String toString() {
		return "Unit [id = " + id + "; type = " + type + "]";
	}

}
