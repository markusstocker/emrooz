/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: SDMXDimension
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

public class SDMXDimension extends AbstractVocabulary  {

	/** http://purl.org/linked-data/sdmx/2009/dimension */
	public static final URI ns = _("http://purl.org/linked-data/sdmx/2009/dimension");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}
	
	/** http://purl.org/linked-data/sdmx/2009/dimension#timePeriod */
	public static final URI timePeriod = _("timePeriod");
	
}
