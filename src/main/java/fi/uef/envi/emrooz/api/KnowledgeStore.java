/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.api;

import java.io.File;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.parser.ParsedQuery;

import fi.uef.envi.emrooz.entity.ssn.Sensor;

/**
 * <p>
 * Title: KnowledgeStore
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

public interface KnowledgeStore extends Store {

	public void load(File file);

	public void addSensor(Sensor sensor);

	public Set<Sensor> getSensors();

	public QueryHandler<BindingSet> createQueryHandler(
			QueryHandler<Statement> other, ParsedQuery query);

}
