/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.entity;

import fi.uef.envi.emrooz.entity.ssn.Frequency;

/**
 * <p>
 * Title: MeasurementPropertyVisitor
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

public interface MeasurementPropertyVisitor {
	
	public void visit(Frequency entity);
	
}
