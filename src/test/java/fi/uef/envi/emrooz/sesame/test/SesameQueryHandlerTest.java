/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.sesame.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;

import fi.uef.envi.emrooz.api.QueryHandler;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.sesame.SesameQueryHandler;
import fi.uef.envi.emrooz.test.ParamsConverterTest;

/**
 * <p>
 * Title: SesameQueryHandlerTest
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

@RunWith(JUnitParamsRunner.class)
public class SesameQueryHandlerTest {

	@Test
	@FileParameters("src/test/resources/SesameQueryHandlerTest.csv")
	public void testSesameQueryHandler(
			String query,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statements,
			@ConvertParam(value = ParamsConverterTest.StringToBindingMapSet.class) Set<Map<String, String>> e,
			String assertType) throws MalformedQueryException {
		SPARQLParser p = new SPARQLParser();
		ParsedQuery q = p.parseQuery(query, null);

		SesameQueryHandler h = new SesameQueryHandler(new ThisQueryHandler(statements), q);
		ResultSet<BindingSet> r = h.evaluate();
		
		Set<Map<String, String>> a = new HashSet<Map<String, String>>();
		
		while (r.hasNext()) {
			BindingSet bs = r.next();
			Map<String, String> m = new HashMap<String, String>();
			a.add(m);
			
			Iterator<Binding> it = bs.iterator();
			
			while (it.hasNext()) {
				Binding b = it.next();
				
				m.put(b.getName(), b.getValue().stringValue());
			}
		}
		
		if (assertType.equals("assertEquals")) {
			assertTrue(CollectionUtils.isEqualCollection(e, a));
			return;
		}

		assertFalse(CollectionUtils.isEqualCollection(e, a));
	}

	private class ThisQueryHandler implements QueryHandler<Statement> {

		Set<Statement> statements;
		
		public ThisQueryHandler(Set<Statement> statements) {
			this.statements = statements;
		}
		
		@Override
		public ResultSet<Statement> evaluate() {
			return new ThisResultSet(statements.iterator());
		}

		@Override
		public void evaluate(TupleQueryResultHandler handler) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() {
			// Nothing to close
		}

	}
	
	private class ThisResultSet implements ResultSet<Statement> {

		Iterator<Statement> iterator;
		
		public ThisResultSet(Iterator<Statement> iterator) {
			this.iterator = iterator;
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Statement next() {
			return iterator.next();
		}

		@Override
		public void close() {
			// Nothing to close
		}
		
	}

}
