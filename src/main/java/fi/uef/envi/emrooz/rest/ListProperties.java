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

import fi.uef.envi.emrooz.entity.ssn.Property;

/**
 * <p>
 * Title: ListProperties
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

@Path("/properties/list")
public class ListProperties {

	private ObjectMapper mapper = new ObjectMapper();

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTextPlain() {
		Set<Property> properties = Connection.getProperties();

		StringBuffer sb = new StringBuffer();

		for (Property property : properties) {
			sb.append(property.getId() + "\n");
		}

		return Response.ok(sb.toString(), MediaType.TEXT_PLAIN).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApplicationJson() throws JsonProcessingException {
		Set<Property> properties = Connection.getProperties();

		ArrayNode node = mapper.createArrayNode();

		for (Property property : properties) {
			node.add(property.getId().stringValue());
		}

		return Response.ok(mapper.writeValueAsString(node),
				MediaType.APPLICATION_JSON).build();
	}

}
