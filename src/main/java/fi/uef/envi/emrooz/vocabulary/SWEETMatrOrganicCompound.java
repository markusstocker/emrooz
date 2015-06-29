/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: SWEETMatrOrganicCompound
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

public class SWEETMatrOrganicCompound extends AbstractVocabulary {

	public static final URI ns = _("http://sweet.jpl.nasa.gov/2.3/matrOrganicCompound.owl");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}
	
	/** http://sweet.jpl.nasa.gov/2.3/matrOrganicCompound.owl#CH4 */
	public static final URI CH4 = _("CH4");
	
	/** http://sweet.jpl.nasa.gov/2.3/matrOrganicCompound.owl#Methane */
	public static final URI Methane = _("Methane");
	
}
