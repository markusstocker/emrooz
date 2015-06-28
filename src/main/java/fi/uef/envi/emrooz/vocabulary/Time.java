/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: Time
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

public class Time extends AbstractVocabulary {

	public static final URI ns = _("http://www.w3.org/2006/time");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}
	
	/** http://www.w3.org/2006/time#Instant **/
	public static final URI Instant = _("Instant");
	
	/** http://www.w3.org/2006/time#inXSDDateTime **/
	public static final URI inXSDDateTime = _("inXSDDateTime");

	/** http://www.w3.org/2006/time#hasBeginning **/
	public static final URI hasBeginning = _("hasBeginning");

	/** http://www.w3.org/2006/time#hasEnd **/
	public static final URI hasEnd = _("hasEnd");
	
}
