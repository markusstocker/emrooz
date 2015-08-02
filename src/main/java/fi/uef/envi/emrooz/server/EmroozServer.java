/*
 * Copyright (C) 2015 see CREDITS.txt
 * All rights reserved.
 */

package fi.uef.envi.emrooz.server;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import fi.uef.envi.emrooz.rest.Connection;

/**
 * <p>
 * Title: EmroozServer
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

public class EmroozServer {

	private static final String SERVER_URI = "http://127.0.0.1/";
	private static final int SERVER_PORT = 8080;

	private static final URI BASE_URI = UriBuilder.fromUri(SERVER_URI)
			.port(SERVER_PORT).build();

	private static HttpServer getServer() throws IllegalArgumentException,
			NullPointerException, IOException {

		ResourceConfig rc = new PackagesResourceConfig(
				"fi.uef.envi.emrooz.rest");
		HttpServer server = GrizzlyServerFactory.createHttpServer(BASE_URI, rc);

		return server;
	}

	public static void main(String[] args) throws IllegalArgumentException,
			NullPointerException, IOException {
		HttpServer server = getServer();
		Connection.init();
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("Server started\n");
			sb.append("WADL at " + BASE_URI + "application.wadl\n");
			sb.append("Press enter to stop the server...\n");
			System.out.println(sb);
			System.in.read();
		} finally {
			Connection.shutdown();
			server.shutdownNow();
		}
	}

}
