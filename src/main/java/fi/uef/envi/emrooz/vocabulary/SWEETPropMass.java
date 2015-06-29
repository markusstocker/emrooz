/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: SWEETPropMass
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

public class SWEETPropMass extends AbstractVocabulary {

	public static final URI ns = _("http://sweet.jpl.nasa.gov/2.3/propMass.owl");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}
	
	/** http://sweet.jpl.nasa.gov/2.3/propMass.owl#Density */
	public static final URI Density = _("Density");
	
}
