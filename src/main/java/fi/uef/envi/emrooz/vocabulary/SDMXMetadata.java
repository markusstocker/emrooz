/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: SDMXMetadata
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

public class SDMXMetadata extends AbstractVocabulary  {

	/** http://purl.org/linked-data/sdmx/2009/metadata */
	public static final URI ns = _("http://purl.org/linked-data/sdmx/2009/metadata");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}
	
	/** http://purl.org/linked-data/sdmx/2009/metadata#freq */
	public static final URI freq = _("freq");
	
}
