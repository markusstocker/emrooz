/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

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

public class SSN extends AbstractVocabulary {
	
	/** http://purl.oclc.org/NET/ssnx/ssn */
	public static final URI ns = _("http://purl.oclc.org/NET/ssnx/ssn");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}

	/** http://purl.oclc.org/NET/ssnx/ssn#Observation */
	public static final URI Observation = _("Observation");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#Sensor */
	public static final URI Sensor = _("Sensor");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#SensingDevice */
	public static final URI SensingDevice = _("SensingDevice");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#Property */
	public static final URI Property = _("Property");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#FeatureOfInterest */
	public static final URI FeatureOfInterest = _("FeatureOfInterest");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#SensorOutput */
	public static final URI SensorOutput = _("SensorOutput");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#ObservationValue */
	public static final URI ObservationValue = _("ObservationValue");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#MeasurementCapability */
	public static final URI MeasurementCapability = _("MeasurementCapability");

	/** http://purl.oclc.org/NET/ssnx/ssn#MeasurementProperty */
	public static final URI MeasurementProperty = _("MeasurementProperty");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#Frequency */
	public static final URI Frequency = _("Frequency");

	/** http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest */
	public static final URI featureOfInterest = _("featureOfInterest");

	/** http://purl.oclc.org/NET/ssnx/ssn#observedProperty */
	public static final URI observedProperty = _("observedProperty");

	/** http://purl.oclc.org/NET/ssnx/ssn#observedBy */
	public static final URI observedBy = _("observedBy");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#observes */
	public static final URI observes = _("observes");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#isPropertyOf */
	public static final URI isPropertyOf = _("isPropertyOf");

	/** http://purl.oclc.org/NET/ssnx/ssn#observationResultTime */
	public static final URI observationResultTime = _("observationResultTime");

	/** http://purl.oclc.org/NET/ssnx/ssn#observationResult */
	public static final URI observationResult = _("observationResult");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementCapability */
	public static final URI hasMeasurementCapability = _("hasMeasurementCapability");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementProperty */
	public static final URI hasMeasurementProperty = _("hasMeasurementProperty");
	
	/** http://purl.oclc.org/NET/ssnx/ssn#hasValue */
	public static final URI hasValue = _("hasValue");

}
