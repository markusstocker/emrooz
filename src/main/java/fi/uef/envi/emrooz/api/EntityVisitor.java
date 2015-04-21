/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import fi.uef.envi.emrooz.api.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.api.ssn.ObservationValue;
import fi.uef.envi.emrooz.api.ssn.ObservationValueDouble;
import fi.uef.envi.emrooz.api.ssn.Property;
import fi.uef.envi.emrooz.api.ssn.Sensor;
import fi.uef.envi.emrooz.api.ssn.SensorObservation;
import fi.uef.envi.emrooz.api.ssn.SensorOutput;
import fi.uef.envi.emrooz.api.time.Instant;
import fi.uef.envi.emrooz.api.time.TemporalEntity;

/**
 * <p>
 * Title: EntityVisitor
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

public interface EntityVisitor {

	public void visit(SensorObservation entity);
	
	public void visit(Sensor entity);
	
	public void visit(Property entity);
	
	public void visit(FeatureOfInterest entity);
	
	public void visit(SensorOutput entity);
	
	public void visit(ObservationValue entity);
	
	public void visit(ObservationValueDouble entity);
	
	public void visit(TemporalEntity entity);
	
	public void visit(Instant entity);
	
}
