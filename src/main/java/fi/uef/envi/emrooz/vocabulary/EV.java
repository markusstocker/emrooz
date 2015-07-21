/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: EV
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

public class EV extends AbstractVocabulary {
	
	/** http://envi.uef.fi/emrooz */
	public static final URI ns = _("http://envi.uef.fi/emrooz");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}

	/** http://envi.uef.fi/emrooz#freqComponentSpecification */
	public static final URI freqComponentSpecification = _("freqComponentSpecification");
	
	/** http://envi.uef.fi/emrooz#timePeriodComponentSpecification */
	public static final URI timePeriodComponentSpecification = _("timePeriodComponentSpecification");
	
	/** http://envi.uef.fi/emrooz#defaultDataStructureDefinition */
	public static final URI defaultDataStructureDefinition = _("defaultDataStructureDefinition");

}
