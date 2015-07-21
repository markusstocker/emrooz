/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: QB
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

public class QB extends AbstractVocabulary {
	
	/** http://purl.org/linked-data/cube */
	public static final URI ns = _("http://purl.org/linked-data/cube");
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}

	/** http://purl.org/linked-data/cube#Observation */
	public static final URI Observation = _("Observation");
	
	/** http://purl.org/linked-data/cube#DataSet */
	public static final URI DataSet = _("DataSet");
	
	/** http://purl.org/linked-data/cube#DataStructureDefinition */
	public static final URI DataStructureDefinition = _("DataStructureDefinition");

	/** http://purl.org/linked-data/cube#ComponentSpecification */
	public static final URI ComponentSpecification = _("ComponentSpecification");
	
	/** http://purl.org/linked-data/cube#ComponentProperty */
	public static final URI ComponentProperty = _("ComponentProperty");
	
	/** http://purl.org/linked-data/cube#DimensionProperty */
	public static final URI DimensionProperty = _("DimensionProperty");
	
	/** http://purl.org/linked-data/cube#MeasureProperty */
	public static final URI MeasureProperty = _("MeasureProperty");
	
	/** http://purl.org/linked-data/cube#AttributeProperty */
	public static final URI AttributeProperty = _("AttributeProperty");
	
	/** http://purl.org/linked-data/cube#dataSet */
	public static final URI dataSet = _("dataSet");
	
	/** http://purl.org/linked-data/cube#structure */
	public static final URI structure = _("structure");
	
	/** http://purl.org/linked-data/cube#component */
	public static final URI component = _("component");
	
	/** http://purl.org/linked-data/cube#componentProperty */
	public static final URI componentProperty = _("componentProperty");
	
	/** http://purl.org/linked-data/cube#dimension */
	public static final URI dimension = _("dimension");
	
	/** http://purl.org/linked-data/cube#measure */
	public static final URI measure = _("measure");
	
	/** http://purl.org/linked-data/cube#attribute */
	public static final URI attribute = _("attribute");
	
	/** http://purl.org/linked-data/cube#componentRequired */
	public static final URI componentRequired = _("componentRequired");
	
	/** http://purl.org/linked-data/cube#order */
	public static final URI order = _("order");
	
	/** http://purl.org/linked-data/cube#componentAttachment */
	public static final URI componentAttachment = _("componentAttachment");

}
