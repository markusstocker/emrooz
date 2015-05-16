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
 * Title: QUDTUnit
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

public class QUDTUnit {

	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	
	public static final String ns = "http://qudt.org/vocab/unit"; 
	
	/** http://qudt.org/vocab/unit#Hertz */
	public static final URI Hertz = _("Hertz");
	
	private static URI _(String fragment) {
		return vf.createURI(ns + "#" + fragment);
	}
}
