/**
 * Copyright 2014-2016 Linagora, Université Joseph Fourier, Floralis
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

package net.roboconf.dm.rest.services.internal.resources.impl;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.roboconf.core.model.beans.Application;
import net.roboconf.dm.management.ManagedApplication;
import net.roboconf.dm.management.Manager;
import net.roboconf.dm.management.exceptions.CommandException;
import net.roboconf.dm.rest.services.internal.RestServicesUtils;
import net.roboconf.dm.rest.services.internal.resources.IApplicationCommandsResource;

/**
 * @author Amadou Diarra - Université Joseph Fourier
 */
@Path( IApplicationCommandsResource.PATH )
public class ApplicationCommandsResource implements IApplicationCommandsResource {

	private final Logger logger = Logger.getLogger( getClass().getName());
	private final Manager manager;


	/**
	 * Constructor.
	 * @param manager the manager
	 */
	public ApplicationCommandsResource( Manager manager ) {
		this.manager = manager;
	}


	@Override
	public List<String> listCommands( String app ) {

		this.logger.fine("Request: list all the commands in the " + app + " application.");
		List<String> result;
		Application application = this.manager.applicationMngr().findApplicationByName( app );
		if( application == null )
			result = new ArrayList<>( 0 );
		else
			result = this.manager.commandsMngr().listCommands( application );

		return result;
	}


	@Override
	public Response executeCommand( String app, String commandName ) {

		this.logger.fine("Request: execute command " + commandName + " in the " + app + " application.");
		Response response = Response.ok().build();
		try {
			Application application = this.manager.applicationMngr().findApplicationByName( app );
			if( application == null )
				response = Response.status( Status.NOT_FOUND ).entity( "Application " + app + " does not exist." ).build();
			else
				this.manager.commandsMngr().execute( application, commandName );

		} catch( NoSuchFileException e ) {
			response = RestServicesUtils.handleException(
					this.logger, Status.NOT_FOUND,
					"Command " + commandName + " does not exist.", e ).build();

		} catch( CommandException e ) {
			response = RestServicesUtils.handleException(
					this.logger, Status.CONFLICT,
					"Command " + commandName + " encountered an error during its execution.", e ).build();

		} catch( Exception e ) {
			response = RestServicesUtils.handleException( this.logger, Status.INTERNAL_SERVER_ERROR, null, e ).build();
		}

		return response;
	}


	@Override
	public Response createOrUpdateCommand(String app, String commandName, String commandText) {

		this.logger.fine("Request: create or update command " + commandName + " in the " + app + " application.");
		Response response = Response.ok().build();
		try {
			ManagedApplication ma = this.manager.applicationMngr().findManagedApplicationByName( app );
			if( ma == null )
				response = Response.status( Status.NOT_FOUND ).entity( "Application " + app + " does not exist." ).build();
			else
				this.manager.commandsMngr().createOrUpdateCommand( ma.getApplication(), commandName, commandText );

		} catch( Exception e ) {
			response = RestServicesUtils.handleException( this.logger, Status.INTERNAL_SERVER_ERROR, null, e ).build();
		}

		return response;
	}


	@Override
	public Response deleteCommand(String app, String commandName) {

		this.logger.fine("Request: delete the command " + commandName + " in the " + app + " application.");
		Response response = Response.ok().build();
		try {
			ManagedApplication ma = this.manager.applicationMngr().findManagedApplicationByName( app );
			if( ma==null )
				response = Response.status( Status.NOT_FOUND ).entity( "Application " + app + " does not exist." ).build();
			else
				this.manager.commandsMngr().deleteCommand( ma.getApplication(), commandName );

		} catch( IOException e ) {
			response = RestServicesUtils.handleException( this.logger, Status.INTERNAL_SERVER_ERROR, null, e ).build();
		}

		return response;
	}


	@Override
	public Response getCommandInstructions(String app, String commandName) {

		this.logger.fine("Request: get instructions from " + commandName + " in the " + app + " application.");
		Response response;
		try {
			ManagedApplication ma = this.manager.applicationMngr().findManagedApplicationByName( app );
			String res = this.manager.commandsMngr().getCommandInstructions( ma.getApplication(), commandName);
			if( res.isEmpty())
				response = Response.status( Status.NO_CONTENT ).build();
			else
				response = Response.ok(res).build();

		} catch( IOException e ) {
			response = RestServicesUtils.handleException( this.logger, Status.INTERNAL_SERVER_ERROR, null, e ).build();
		}

		return response;
	}
}
