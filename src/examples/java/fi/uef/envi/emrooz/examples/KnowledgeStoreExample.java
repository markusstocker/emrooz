/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.examples;

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;

/**
 * <p>
 * Title: KnowledgeStoreExample
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

public class KnowledgeStoreExample {

	public static void main(String[] args) {
		String dir = "/tmp/emrooz/ks";
		String data = "src/examples/resources/kb.rdf";

		Repository repository = new SailRepository(new MemoryStore(
				new File(dir)));

		SesameKnowledgeStore store = new SesameKnowledgeStore(repository);

		// Depending on the repository, this is required only once
		store.load(new File(data));

		store.close();
	}

}
