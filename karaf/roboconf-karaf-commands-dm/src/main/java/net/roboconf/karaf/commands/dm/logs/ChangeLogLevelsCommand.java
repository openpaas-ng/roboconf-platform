/**
 * Copyright 2015-2016 Linagora, Université Joseph Fourier, Floralis
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

package net.roboconf.karaf.commands.dm.logs;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.roboconf.core.model.beans.Instance;
import net.roboconf.core.model.beans.Instance.InstanceStatus;
import net.roboconf.core.model.helpers.InstanceHelpers;
import net.roboconf.core.utils.Utils;
import net.roboconf.dm.management.ManagedApplication;
import net.roboconf.dm.management.Manager;
import net.roboconf.messaging.api.messages.from_dm_to_agent.MsgCmdChangeLogLevel;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

/**
 * @author Vincent Zurczak - Linagora
 */
@Command( scope = "roboconf", name = "set-log-level", description="Changes the level of the Roboconf loggers for one or several agents" )
@Service
public class ChangeLogLevelsCommand implements Action {

	@Argument( index = 0, name = "log level", description = "The log level (Java Logging)", required = true, multiValued = false )
	@Completion( LogLevelCompleter.class )
	String logLevelAsString;

	@Argument( index = 1, name = "application", description = "The application's name", required = true, multiValued = false )
	@Completion( ApplicationCompleter.class )
	String applicationName;

	@Argument( index = 2, name = "scoped instance's path", description = "The scoped instance's path", required = false, multiValued = false )
	@Completion( ScopedInstanceCompleter.class )
	String scopedInstancePath;

	@Reference
	Manager manager;

	// Other fields
	final Logger logger = Logger.getLogger( getClass().getName());
	PrintStream out = System.out;


    @Override
    public Object execute() throws Exception {

    	// Get the log level
    	boolean isValidLevel = true;
    	Level logLevel = null;
    	try {
			logLevel = Level.parse( this.logLevelAsString );

		} catch( Exception e ) {
			Utils.logException( this.logger, e );
			isValidLevel = false;
		}

    	// Prepare the checks
    	List<Instance> scopedInstances = new ArrayList<> ();
    	ManagedApplication ma = null;
    	Instance scopedInstance;

    	// Check...
    	if( ! isValidLevel )
    		this.out.println( "Invalid log level: " + this.logLevelAsString + ". Only " + " are allowed." );

    	else if(( ma = this.manager.applicationMngr().findManagedApplicationByName( this.applicationName )) == null )
    		this.out.println( "Unknown application: " + this.applicationName + "." );

    	else if( this.scopedInstancePath == null )
    		scopedInstances.addAll( InstanceHelpers.findAllScopedInstances( ma.getApplication()));

    	else if(( scopedInstance = InstanceHelpers.findInstanceByPath( ma.getApplication(), this.scopedInstancePath )) == null )
    		this.out.println( "There is no " + this.scopedInstancePath + " instance in " + this.applicationName + "." );

    	else if( ! InstanceHelpers.isTarget( scopedInstance ))
    		this.out.println( "Instance " + this.scopedInstancePath + " is not a scoped instance in " + this.applicationName + "." );

    	else
    		scopedInstances.add( scopedInstance );

    	// Send messages
    	for( Instance inst : scopedInstances ) {

    		if( inst.getStatus() == InstanceStatus.NOT_DEPLOYED ) {
    			StringBuilder sb = new StringBuilder( "No message will be sent to " );
    			sb.append( this.scopedInstancePath );
    			sb.append( ", the associated agent is not marked as deployed." );
    			this.out.println( sb.toString());

    			continue;
    		}

    		if( this.logger.isLoggable( Level.FINE )) {
    			StringBuilder sb = new StringBuilder( "Sending a change log level message (level = " );
    			sb.append( this.logLevelAsString );
    			sb.append( ") to " );
    			sb.append( this.scopedInstancePath );
    			sb.append( " @ " );
    			sb.append( this.applicationName );
    			this.logger.fine( sb.toString());
    		}

    		MsgCmdChangeLogLevel message = new MsgCmdChangeLogLevel( logLevel );
    		this.manager.messagingMngr().sendMessageSafely( ma, inst, message );
    	}

    	return null;
    }
}
