/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.query;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.openrdf.model.URI;

import fi.uef.envi.emrooz.api.KnowledgeStore;
import fi.uef.envi.emrooz.api.QueryRewriter;
import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;
import fi.uef.envi.emrooz.entity.ssn.Property;
import fi.uef.envi.emrooz.entity.ssn.Sensor;

/**
 * <p>
 * Title: SensorObservationQueryRewriter
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

public class SensorObservationQueryRewriter implements
		QueryRewriter<SensorObservationQuery> {

	private KnowledgeStore ks;

	public SensorObservationQueryRewriter(KnowledgeStore ks) {
		if (ks == null)
			throw new NullPointerException("[ks = null]");

		this.ks = ks;
	}

	@Override
	public Set<SensorObservationQuery> rewrite(SensorObservationQuery query) {
		if (query.isFullySpecified()) {
			Collections.singleton(query);
		}

		Set<SensorObservationQuery> ret = new HashSet<SensorObservationQuery>();

		URI sensorId = query.getSensorId();
		URI propertyId = query.getPropertyId();
		URI featureId = query.getFeatureOfInterestId();
		DateTime timeFrom = query.getTimeFrom();
		DateTime timeTo = query.getTimeTo();

		Set<Sensor> sensors = ks.getSensors();

		for (Sensor sensor : sensors) {
			Property property = sensor.getObservedProperty();
			FeatureOfInterest feature = property.getPropertyOf();

			URI thisSensorId = sensor.getId();
			URI thisPropertyId = property.getId();
			URI thisFeatureId = feature.getId();

			URI thatSensorId = sensorId;
			URI thatPropertyId = propertyId;
			URI thatFeatureId = featureId;

			if (thatSensorId == null)
				thatSensorId = thisSensorId;
			if (thatPropertyId == null)
				thatPropertyId = thisPropertyId;
			if (thatFeatureId == null)
				thatFeatureId = thisFeatureId;

			if (!(thatSensorId.equals(thisSensorId)
					&& thatPropertyId.equals(thisPropertyId) && thatFeatureId
						.equals(thisFeatureId)))
				continue;

			ret.add(SensorObservationQuery.create(thisSensorId, thisPropertyId,
					thisFeatureId, timeFrom, timeTo));
		}

		return Collections.unmodifiableSet(ret);
	}

}
