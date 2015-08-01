/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.rest;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import fi.uef.envi.emrooz.entity.ssn.FeatureOfInterest;

/**
 * <p>
 * Title: ListFeatures
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

@Path("/features/list")
public class ListFeatures {

	private ObjectMapper mapper = new ObjectMapper();

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTextPlain() {
		Set<FeatureOfInterest> features = Connection.getFeaturesOfInterest();

		StringBuffer sb = new StringBuffer();

		for (FeatureOfInterest feature : features) {
			sb.append(feature.getId() + "\n");
		}

		return Response.ok(sb.toString(), MediaType.TEXT_PLAIN).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApplicationJson() throws JsonProcessingException {
		Set<FeatureOfInterest> features = Connection.getFeaturesOfInterest();

		ArrayNode node = mapper.createArrayNode();

		for (FeatureOfInterest feature : features) {
			node.add(feature.getId().stringValue());
		}

		return Response.ok(mapper.writeValueAsString(node),
				MediaType.APPLICATION_JSON).build();
	}

}
