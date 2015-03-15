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
 * Title: DUL
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

public class DUL {

	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	
	public static final URI ns = vf.createURI("http://www.loa-cnr.it/ontologies/DUL.owl");

	/** http://www.loa-cnr.it/ontologies/DUL.owl#hasRegionDataValue */
	public static final URI hasRegionDataValue = _("hasRegionDataValue");

	/** http://www.loa-cnr.it/ontologies/DUL.owl#hasRegion */
	public static final URI hasRegion = _("hasRegion");

	private static URI _(String fragment) {
		return vf.createURI(ns + "#" + fragment);
	}

}
