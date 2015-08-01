/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;

import fi.uef.envi.emrooz.api.ResultSet;

/**
 * <p>
 * Title: ListSensorObservations
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

@Path("/observations/sensor/list")
public class ListSensorObservations {

	@GET
	@Produces("text/csv")
	public Response getTextCsv(@QueryParam("sensor") String sensorId,
			@QueryParam("property") String propertyId,
			@QueryParam("feature") String featureId,
			@QueryParam("from") String from, @QueryParam("to") String to) {
		if (from == null)
			return Response.ok("from is null", "text/plain").build();
		if (to == null)
			return Response.ok("to is null", "text/plain").build();
		
		ResultSet<BindingSet> rs = Connection.evaluate(sensorId, propertyId,
				featureId, from, to);

		StringBuffer sb = new StringBuffer();

		sb.append("time,value,sensor,property,feature" + "\n");

		while (rs.hasNext()) {
			BindingSet bs = rs.next();

			sb.append(bs.getBinding("time").getValue().stringValue());
			sb.append("," + bs.getBinding("value").getValue().stringValue());

			Binding b = null;

			b = bs.getBinding("sensorId");

			if (b == null)
				sb.append("," + sensorId);
			else
				sb.append("," + b.getValue().stringValue());

			b = bs.getBinding("propertyId");

			if (b == null)
				sb.append("," + propertyId);
			else
				sb.append("," + b.getValue().stringValue());

			b = bs.getBinding("featureId");

			if (b == null)
				sb.append("," + featureId);
			else
				sb.append("," + b.getValue().stringValue());

			sb.append("\n");
		}

		return Response.ok(sb.toString(), "text/csv").build();
	}
}
