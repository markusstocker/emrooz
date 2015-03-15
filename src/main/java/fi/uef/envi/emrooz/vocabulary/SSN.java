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
 * Title: SSN
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

public class SSN {
	
	private static final ValueFactory vf = ValueFactoryImpl.getInstance();
	
	/** http://purl.oclc.org/NET/ssnx/ssn */
	public static final URI ns = vf.createURI("http://purl.oclc.org/NET/ssnx/ssn");

	/** http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest */
	public static final URI featureOfInterest = _("featureOfInterest");

	/** http://purl.oclc.org/NET/ssnx/ssn#observedProperty */
	public static final URI observedProperty = _("observedProperty");

	/** http://purl.oclc.org/NET/ssnx/ssn#observedBy */
	public static final URI observedBy = _("observedBy");

	/** http://purl.oclc.org/NET/ssnx/ssn#observationResultTime */
	public static final URI observationResultTime = _("observationResultTime");

	/** http://purl.oclc.org/NET/ssnx/ssn#observationResult */
	public static final URI observationResult = _("observationResult");

	private static URI _(String fragment) {
		return vf.createURI(ns + "#" + fragment);
	}
}
