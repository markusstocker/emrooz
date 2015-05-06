/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: AbstractEntity
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

public abstract class AbstractEntity implements Entity {

	protected URI id;
	protected URI type;
	
	public AbstractEntity(URI id, URI type) {
		if (id == null)
			throw new NullPointerException("[id = null]");
		if (type == null)
			throw new NullPointerException("[type = null]");
		
		this.id = id;
		this.type = type;
	}
	
	public URI getId() {
		return id;
	}
	
	public URI getType() {
		return type;
	}
	
}
