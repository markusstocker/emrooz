/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
	// The type given in the constructor
	protected URI type;
	// Includes the type given in the constructor as well as additional types
	protected Set<URI> types = new HashSet<URI>();

	public AbstractEntity(URI id, URI type) {
		if (id == null)
			throw new NullPointerException("[id = null]");
		if (type == null)
			throw new NullPointerException("[type = null]");

		this.id = id;
		this.type = type;

		addType(type);
	}

	@Override
	public void addType(URI type) {
		if (type == null)
			return;

		types.add(type);
	}

	@Override
	public void addTypes(Set<URI> types) {
		if (types == null || types.isEmpty())
			return;

		this.types.addAll(types);
	}

	@Override
	public void addTypes(URI... types) {
		addTypes(new HashSet<URI>(Arrays.asList(types)));
	}

	@Override
	public URI getId() {
		return id;
	}

	@Override
	public URI getType() {
		return type;
	}

	@Override
	public Set<URI> getTypes() {
		return Collections.unmodifiableSet(types);
	}

}
