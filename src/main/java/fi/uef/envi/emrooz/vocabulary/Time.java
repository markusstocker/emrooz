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

public class Time {

	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	
	public static final URI ns = vf.createURI("http://www.w3.org/2006/time");
	
	/** http://www.w3.org/2006/time#inXSDDateTime **/
	public static final URI inXSDDateTime = _("inXSDDateTime");

	/** http://www.w3.org/2006/time#hasBeginning **/
	public static final URI hasBeginning = _("hasBeginning");

	/** http://www.w3.org/2006/time#hasEnd **/
	public static final URI hasEnd = _("hasEnd");

	private static URI _(String fragment) {
		return vf.createURI(ns + "#" + fragment);
	}
	
}
