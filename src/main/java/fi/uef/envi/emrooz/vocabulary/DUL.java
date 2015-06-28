/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

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

public class DUL extends AbstractVocabulary {

	public static final URI ns = _("http://www.loa-cnr.it/ontologies/DUL.owl");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}

	/** http://www.loa-cnr.it/ontologies/DUL.owl#hasRegionDataValue */
	public static final URI hasRegionDataValue = _("hasRegionDataValue");

	/** http://www.loa-cnr.it/ontologies/DUL.owl#hasRegion */
	public static final URI hasRegion = _("hasRegion");

}
