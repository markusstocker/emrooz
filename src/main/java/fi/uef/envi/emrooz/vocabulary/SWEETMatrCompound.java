/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: SWEETMatrCompound
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

public class SWEETMatrCompound extends AbstractVocabulary {

	public static final URI ns = _("http://sweet.jpl.nasa.gov/2.3/matrCompound.owl");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}
	
	public static final URI CO2 = _("CO2");
	
	public static final URI CarbonDioxide = _("CarbonDioxide");
	
	public static final URI H2O = _("H2O");
	
	public static final URI Water = _("Water");
	
}
