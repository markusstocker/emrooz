/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.io.query;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.QueryType;
import fi.uef.envi.emrooz.cassandra.CassandraDataStore;
import fi.uef.envi.emrooz.io.licor.GHGSensorObservationReader;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;

/**
 * <p>
 * Title: CLIQueryExecution
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

public class CLIQueryExecution {

	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public static void main(String[] args) {
		if (args.length == 0)
			help();

		boolean isSensorQuery = false;
		File queryFileName = null;
		File knowledgeStoreFile = null;
		String dataStoreHost = "localhost";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-sq")) {
				queryFileName = new File(args[++i]);
				isSensorQuery = true;
			} else if (args[i].equals("-dq")) {
				queryFileName = new File(args[++i]);
				isSensorQuery = false;
			} else if (args[i].equals("-ks")) {
				knowledgeStoreFile = new File(args[++i]);
			} else if (args[i].equals("-ds")) {
				dataStoreHost = args[++i];
			}
		}

		if (queryFileName == null || knowledgeStoreFile == null)
			help();

		Emrooz e = new Emrooz(new SesameKnowledgeStore(new SailRepository(
				new MemoryStore(knowledgeStoreFile))), new CassandraDataStore(
				dataStoreHost));

		long start = System.currentTimeMillis();

		try {
			if (isSensorQuery)
				e.evaluate(QueryType.SENSOR_OBSERVATION,
						FileUtils.readFileToString(queryFileName),
						new SPARQLResultsTSVWriter(System.out));
			else
				e.evaluate(QueryType.DATASET_OBSERVATION,
						FileUtils.readFileToString(queryFileName),
						new SPARQLResultsTSVWriter(System.out));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		long end = System.currentTimeMillis();

		e.close();

		summary(start, end);
	}

	private static void help() {
		StringBuffer sb = new StringBuffer();

		sb.append(GHGSensorObservationReader.class.getName() + LINE_SEPARATOR);
		sb.append("Arguments:" + LINE_SEPARATOR);
		sb.append("  -sq/-dq  [file name]  SPARQL query file name"
				+ LINE_SEPARATOR);
		sb.append("  -ks [directory name]  Knowledge store data directory (e.g. /tmp/ks)"
				+ LINE_SEPARATOR);
		sb.append("  -ds [host name]       Data store host name (default: localhost)"
				+ LINE_SEPARATOR);

		System.out.println(sb);

		System.exit(0);
	}

	private static void summary(long start, long end) {
		StringBuffer sb = new StringBuffer();

		sb.append("Query executed in " + ((end - start) / 1000) + "."
				+ ((end - start) % 1000) + " seconds");

		System.out.println(sb);
	}
}
