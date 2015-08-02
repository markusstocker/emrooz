/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.openrdf.query.BindingSet;

import fi.uef.envi.emrooz.api.ResultSet;
import fi.uef.envi.emrooz.vocabulary.SDMXDimension;

/**
 * <p>
 * Title: ListDatasetObservations
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

@Path("/observations/dataset/list")
public class ListDatasetObservations {

	@GET
	@Produces("text/csv")
	public Response getTextCsv(@QueryParam("dataset") String datasetId,
			@QueryParam("from") String from, @QueryParam("to") String to) {
		if (datasetId == null)
			return Response.ok("dataset is null", "text/plain").build();
		if (from == null)
			return Response.ok("from is null", "text/plain").build();
		if (to == null)
			return Response.ok("to is null", "text/plain").build();

		ResultSet<BindingSet> rs = Connection.evaluate(datasetId, from, to);

		Set<String> properties = new HashSet<String>();
		Map<String, Map<String, String>> results = new LinkedHashMap<String, Map<String, String>>();

		while (rs.hasNext()) {
			BindingSet bs = rs.next();

			String id = bs.getBinding("id").getValue().stringValue();
			String time = bs.getBinding("time").getValue().stringValue();
			String property = bs.getBinding("property").getValue()
					.stringValue();
			String value = bs.getBinding("value").getValue().stringValue();

			if (!property.equals(SDMXDimension.timePeriod.stringValue()))
				properties.add(property);
			
			Map<String, String> result = results.get(id);
			
			if (result == null) {
				result = new HashMap<String, String>();
				results.put(id, result);
			}
			
			result.put(property, value);
			result.put(SDMXDimension.timePeriod.stringValue(), time);
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("time");
		
		for (String property : properties)
			sb.append("," + property.substring(property.indexOf("#")+1)); // TODO Problematic if # does not exist
		
		sb.append("\n");
		
		for (Map.Entry<String, Map<String, String>> result : results.entrySet()) {
			Map<String, String> values = result.getValue();
			
			sb.append(values.get(SDMXDimension.timePeriod.stringValue()));
			
			for (String property : properties)
				sb.append("," + values.get(property));
			
			sb.append("\n");
 		}
		

		return Response.ok(sb.toString(), "text/csv").build();
	}
}
