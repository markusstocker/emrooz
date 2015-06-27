/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.test;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.converters.ConvertParam;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import fi.uef.envi.emrooz.Emrooz;
import fi.uef.envi.emrooz.api.DataStore;
import fi.uef.envi.emrooz.api.QueryHandler;
import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.entity.ssn.Frequency;
import fi.uef.envi.emrooz.query.SensorObservationQuery;
import fi.uef.envi.emrooz.sesame.SesameKnowledgeStore;

/**
 * <p>
 * Title: EmroozTest
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
public class EmroozTest {

	@Test
	@FileParameters("src/test/resources/EmroozTest.csv")
	public void testEmrooz(
			String kb,
			@ConvertParam(value = ParamsConverterTest.StringToURIConverter.class) URI specificationId,
			@ConvertParam(value = ParamsConverterTest.StringToStatementsConverter.class) Set<Statement> statements,
			String query,
			@ConvertParam(value = ParamsConverterTest.StringToBindingMapSet.class) Set<Map<String, String>> e,
			String assertType) {
		Emrooz em = new Emrooz(new SesameKnowledgeStore(new SailRepository(
				new MemoryStore())), new ThisDataStore());
		em.loadKnowledgeBase(new File(kb));
		em.addSensorObservations(statements);
		ResultSet<BindingSet> rs = em.evaluate(query);

		Set<Map<String, String>> a = new HashSet<Map<String, String>>();

		while (rs.hasNext()) {
			BindingSet bs = rs.next();

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

		em.close();
	}

	private class ThisDataStore implements DataStore {

		Map<URI, Map<URI, Map<URI, Map<DateTime, Set<Statement>>>>> store;

		public ThisDataStore() {
			this.store = new HashMap<URI, Map<URI, Map<URI, Map<DateTime, Set<Statement>>>>>();
		}

		@Override
		public void addSensorObservation(URI sensorId, URI propertyId,
				URI featureId, Frequency frequency, DateTime resultTime,
				Set<Statement> statements) {
			Map<URI, Map<URI, Map<DateTime, Set<Statement>>>> m1 = store
					.get(sensorId);

			if (m1 == null) {
				m1 = new HashMap<URI, Map<URI, Map<DateTime, Set<Statement>>>>();
				store.put(sensorId, m1);
			}

			Map<URI, Map<DateTime, Set<Statement>>> m2 = m1.get(propertyId);

			if (m2 == null) {
				m2 = new HashMap<URI, Map<DateTime, Set<Statement>>>();
				m1.put(propertyId, m2);
			}

			Map<DateTime, Set<Statement>> m3 = m2.get(featureId);

			if (m3 == null) {
				m3 = new TreeMap<DateTime, Set<Statement>>();
				m2.put(featureId, m3);
			}

			m3.put(resultTime, statements);
		}

		@Override
		public QueryHandler<Statement> createQueryHandler(
				Map<SensorObservationQuery, Frequency> queries) {
			return new ThisQueryHandler(Collections.unmodifiableMap(store),
					queries);
		}

		@Override
		public void close() {
			// Nothing to close
		}

	}

	private class ThisQueryHandler implements QueryHandler<Statement> {

		private Map<URI, Map<URI, Map<URI, Map<DateTime, Set<Statement>>>>> store;
		private Map<SensorObservationQuery, Frequency> queries;

		public ThisQueryHandler(
				Map<URI, Map<URI, Map<URI, Map<DateTime, Set<Statement>>>>> store,
				Map<SensorObservationQuery, Frequency> queries) {
			this.store = store;
			this.queries = queries;
		}

		@Override
		public ResultSet<Statement> evaluate() {
			Set<Statement> ret = new HashSet<Statement>();

			for (Map.Entry<SensorObservationQuery, Frequency> entry : queries
					.entrySet()) {
				SensorObservationQuery query = entry.getKey();
				URI sensorId = query.getSensorId();
				URI propertyId = query.getPropertyId();
				URI featureId = query.getFeatureOfInterestId();
				DateTime timeFrom = query.getTimeFrom();
				DateTime timeTo = query.getTimeTo();

				Map<URI, Map<URI, Map<DateTime, Set<Statement>>>> m1 = store
						.get(sensorId);

				if (m1 == null)
					continue;

				Map<URI, Map<DateTime, Set<Statement>>> m2 = m1.get(propertyId);

				if (m2 == null)
					continue;

				Map<DateTime, Set<Statement>> m3 = m2.get(featureId);

				if (m3 == null)
					continue;

				for (Map.Entry<DateTime, Set<Statement>> e : m3.entrySet()) {
					DateTime dateTime = e.getKey();

					if ((dateTime.isEqual(timeFrom) || dateTime
							.isAfter(timeFrom)) && dateTime.isBefore(timeTo))
						ret.addAll(e.getValue());
				}
			}

			return new ThisResultSet(ret.iterator());
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

		private Iterator<Statement> iterator;

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
