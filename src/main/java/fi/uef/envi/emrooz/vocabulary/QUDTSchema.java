/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.vocabulary;

import org.openrdf.model.URI;

/**
 * <p>
 * Title: QUDTSchema
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

public class QUDTSchema extends AbstractVocabulary {
	
	public static final URI ns = _("http://qudt.org/schema/qudt"); 
	
	static {
		AbstractVocabulary.ns = ns.stringValue();
	}
	
	/** http://qudt.org/schema/qudt#QuantityValue */
	public static final URI QuantityValue = _("QuantityValue");
	
	/** http://qudt.org/schema/qudt#Unit */
	public static final URI Unit = _("Unit");
	
	/** http://qudt.org/schema/qudt#unit **/
	public static final URI unit = _("unit");
	
	/** http://qudt.org/schema/qudt#numericValue **/
	public static final URI numericValue = _("numericValue");

}
