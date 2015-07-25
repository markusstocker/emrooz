/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity;

import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueDouble;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueEntity;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueInteger;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueLong;
import fi.uef.envi.emrooz.entity.qb.ComponentPropertyValueString;

/**
 * <p>
 * Title: ComponentPropertyValueVisitor
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

public interface ComponentPropertyValueVisitor {

	public void visit(ComponentPropertyValueEntity value);
	
	public void visit(ComponentPropertyValueString value);
	
	public void visit(ComponentPropertyValueDouble value);
	
	public void visit(ComponentPropertyValueInteger value);
	
	public void visit(ComponentPropertyValueLong value);
	
}
