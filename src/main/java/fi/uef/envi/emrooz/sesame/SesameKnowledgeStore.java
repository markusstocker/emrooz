/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.sesame;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

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

public class SesameKnowledgeStore {

	private Repository repository;
	private RepositoryConnection connection;
	
	private static final Logger log = Logger.getLogger(SesameKnowledgeStore.class.getName());
	
	public SesameKnowledgeStore(Repository repository) {
		if (repository == null)
			throw new RuntimeException("[repository = null]");
		
		this.repository = repository;
		
		try {
			this.repository.initialize();
			this.connection = this.repository.getConnection();
		} catch (RepositoryException e) {
			if (log.isLoggable(Level.SEVERE))
				log.severe(e.getMessage());
		}
	}
	
}
