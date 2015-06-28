/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * <p>
 * Title: AbstractVocabulary
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

public abstract class AbstractVocabulary {

	protected static String ns;
	
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();

	protected static URI _(String s) {
		if (s.startsWith("http"))
			return vf.createURI(s);
		
		if (s.contains("#"))
			return vf.createURI(ns + s);
		
		return vf.createURI(ns + "#" + s);
	}

}
