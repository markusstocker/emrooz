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
 * Title: QuantityValue
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

public class QuantityValue extends AbstractEntity {

	public QuantityValue(URI id) {
		this(id, QUDTSchema.QuantityValue);
	}
	
	public QuantityValue(URI id, URI type) {
		super(id, type);
	}

	@Override
	public void accept(EntityVisitor visitor) {
		visitor.visit(this);
	}

}
