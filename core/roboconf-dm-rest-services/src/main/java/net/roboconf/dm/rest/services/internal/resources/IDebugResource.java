/**
 * Copyright 2014-2015 Linagora, Université Joseph Fourier, Floralis
 *
 * The present code is developed in the scope of the joint LINAGORA -
 * Université Joseph Fourier - Floralis research program and is designated
 * as a "Result" pursuant to the terms and conditions of the LINAGORA
 * - Université Joseph Fourier - Floralis research program. Each copyright
 * holder of Results enumerated here above fully & independently holds complete
 * ownership of the complete Intellectual Property rights applicable to the whole
 * of said Results, and may freely exploit it in any manner which does not infringe
 * the moral rights of the other copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.roboconf.dm.rest.services.internal.resources;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.roboconf.dm.rest.commons.Diagnostic;
import net.roboconf.dm.rest.commons.UrlConstants;
import net.roboconf.dm.rest.services.internal.resources.impl.DebugResource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * The REST API to debug various things.
 * <p>
 * Implementing classes have to define the "Path" annotation
 * on the class. Use {@link #PATH}.
 * </p>
 *
 * @author Vincent Zurczak - Linagora
 * @author Pierre Bourret - Université Joseph Fourier
 */
public interface IDebugResource {

	String PATH = "/" + UrlConstants.DEBUG;
	String FAKE_APP_NAME = "Fake Application";


	/**
	 * Loads a target.properties file and creates a fake application to test it.
	 * @param uploadedInputStream the uploaded file
	 * @param fileDetail the file details
	 * @return a response
	 */
	@POST
	@Path("/test-target")
	@Consumes( MediaType.MULTIPART_FORM_DATA )
	Response createTestForTargetProperties(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail );


	/**
	 * Checks the DM is correctly connected with the messaging server.
	 * <p>
	 * This method acts as followed:
	 * <ol>
	 * <li>If no message is provided ({@code null}), it generates a unique UUID.</li>
	 * <li>It sends the message to the messaging server. The recipient is the DM itself.</li>
	 * <li>It waits for the messaging server to propagate the message to the DM. A wait timeout can be specified by the
	 * {@code timeout} parameter (default is 1000ms, max
	 * {@value DebugResource#MAXIMUM_TIMEOUT} ms). Then:
	 * <ul>
	 * <li>If the message is received before the timeout expires, a positive {@code 200 OK} response is
	 * returned.</li>
	 * <li>Otherwise, a negative {@code 408 Request Time-out} response is returned.</li>
	 * <li>If any unexpected error occurs, a {@code 500 Internal error} response is returned.</li>
	 * </ul>
	 * </li>
	 * </ol>
	 *
	 * @param message a customized message content.
	 * @param timeout the timeout in milliseconds (ms) to wait before considering the message is lost.
	 * @return the response to the messaging server connection check.
	 */
	@GET
	@Path("/check-dm")
	Response checkMessagingConnectionForTheDm( @QueryParam("message") @DefaultValue("") String message,
	                                           @QueryParam("timeout") @DefaultValue("1000") long timeout );


	/**
	 * Checks the DM can correctly exchange with an agent through the messaging server.
	 * <p>
	 * This method acts as followed:
	 * <ol>
	 * <li>If no message is provided ({@code null}), it generates a unique UUID.</li>
	 * <li>It sends the 'PING' message to the specified {@code root-instance-name}.</li>
	 * <li>It waits for the agent to respond a 'PONG' to the DM. A wait timeout can be specified by the {@code timeout}
	 * parameter (default is 1000ms, max
	 * {@value DebugResource#MAXIMUM_TIMEOUT} ms). Then:
	 * <ul>
	 * <li>If the message is received before the timeout expires, a positive {@code 200 OK} response is
	 * returned.</li>
	 * <li>Otherwise, a negative {@code 408 Request Time-out} response is returned.</li>
	 * <li>If any unexpected error occurs, a {@code 500 Internal error} response is returned.</li>
	 * </ul>
	 * </li>
	 * </ol>
	 *
	 * @param applicationName  the name of the application holding the targeted agent.
	 * @param rootInstanceName the identifier of the targeted agent.
	 * @param message          a customized message content.
	 * @param timeout          the timeout in milliseconds (ms) to wait before considering the message is lost.
	 * @return the response to the agent connection check.
	 */
	@GET
	@Path("/check-agent")
	Response checkMessagingConnectionWithAgent( @QueryParam("application-name") String applicationName,
	                                            @QueryParam("root-instance-name") String rootInstanceName,
	                                            @QueryParam("message") @DefaultValue("") String message,
	                                            @QueryParam("timeout") @DefaultValue("1000") long timeout );


	/**
	 * Runs a diagnostic for a given instance.
	 * <p>
	 * The diagnostic is based on the information hold by the DM, and not by the agent.
	 * </p>
	 *
	 * @return a response (with a diagnostic in case of code 200)
	 */
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	@Path("/diagnose-instance")
	Response diagnoseInstance( @QueryParam("application-name") String applicationName,
	                           @QueryParam("instance-path") String instancePath );


	/**
	 * Runs a diagnostic for a given application.
	 * <p>
	 * The diagnostic is based on the information hold by the DM, and not by the agent.
	 * </p>
	 *
	 * @return a list of diagnostics (one per instance in the application)
	 */
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	@Path("/diagnose-application")
	List<Diagnostic> diagnoseApplication( @QueryParam("application-name") String applicationName );
}
