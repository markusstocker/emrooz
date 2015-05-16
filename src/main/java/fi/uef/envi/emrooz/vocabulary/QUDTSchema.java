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
 * Title: QUDTSchema
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

public class QUDTSchema {

	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	
	public static final String ns = "http://qudt.org/schema/qudt"; 
	
	/** http://qudt.org/schema/qudt#QuantityValue */
	public static final URI QuantityValue = _("QuantityValue");
	
	/** http://qudt.org/schema/qudt#unit **/
	public static final URI unit = _("unit");
	
	/** http://qudt.org/schema/qudt#numericValue **/
	public static final URI numericValue = _("numericValue");
	
	private static URI _(String fragment) {
		return vf.createURI(ns + "#" + fragment);
	}
}
