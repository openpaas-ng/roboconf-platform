/**
 * Copyright 2015 Linagora, Université Joseph Fourier, Floralis
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

package net.roboconf.target.docker.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;
import net.roboconf.core.utils.ProgramUtils;
import net.roboconf.core.utils.Utils;

/**
 * @author Vincent Zurczak - Linagora
 * @author Pierre-Yves Gibello - Linagora
 */
public final class DockerTestUtils {

	public static final String DOCKER_TCP_PORT = "4243";


	/**
	 * Private empty constructor.
	 */
	private DockerTestUtils() {
		// nothing
	}


	/**
	 * Checks that Docker is installed and configures it if necessary.
	 * @throws Exception
	 */
	public static void checkDockerIsInstalled() throws Exception {

		Logger logger = Logger.getLogger( DockerTestUtils.class.getName());
		List<String> command = Arrays.asList( "docker", "version" );
		int exitCode = ProgramUtils.executeCommand( logger, command, null, null );
		if( exitCode != 0 )
			throw new Exception( "Docker is not installed." );

		checkOrUpdateDockerTcpConfig( logger );
	}


	/**
	 * Checks that Docker is configured to listen on the right TCP port.
	 * <p>
	 * If not, try to change Docker config and restart (may require root access).
	 * </p>
	 *
	 * @param logger
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void checkOrUpdateDockerTcpConfig( Logger logger )
	throws IOException, InterruptedException {

		File dockerConf = new File( "/etc/default/docker" );
		if( ! dockerConf.exists())
			dockerConf = new File( "/etc/default/docker.io" );

		if( ! dockerConf.exists() || ! dockerConf.canRead())
			throw new IOException( "The docker configuration file could not be found or is not readable." );

		// Look for the expected port in the configuration file
		BufferedReader reader = null;
		boolean ok = false;
		try {
			reader = new BufferedReader( new InputStreamReader( new FileInputStream( dockerConf ), "UTF-8" ));
			String line;
			while( ! ok && (line = reader.readLine()) != null) {
				if( line.indexOf("#") < 0
					&& line.indexOf("DOCKER_OPTS") >= 0
					&& (line.indexOf("-H=tcp:") > 0 || line.indexOf("-H tcp:") > 0)
					&& line.indexOf( ":" + DOCKER_TCP_PORT ) > 0)
						ok = true;
			}

		} finally {
			Utils.closeQuietly( reader );
		}

		// If not present, try to update the file
		if( ! ok ) {
			if( ! dockerConf.canWrite()) {
				logger.severe( "There is no TCP configuration for port " + DOCKER_TCP_PORT + " in " + dockerConf );
				logger.info( "Update the file " + dockerConf + " with DOCKER_OPTS=\"-H tcp://localhost:" + DOCKER_TCP_PORT + " -H unix:///var/run/docker.sock\"" );
				throw new IOException( "The Docker configuration is missing TCP configuration." );
			}

			OutputStreamWriter writer = null;
			try {
				writer = new OutputStreamWriter( new FileOutputStream( dockerConf, true ),"UTF-8" );
				writer.append("DOCKER_OPTS=\"-H tcp://localhost:" + DOCKER_TCP_PORT + " -H unix:///var/run/docker.sock\"\n");

			} finally {
				Utils.closeQuietly( writer );
			}

			List<String> command = Arrays.asList( "docker", "restart" );
			int exitCode = ProgramUtils.executeCommand( logger, command, null, null );
			Assert.assertEquals( 0, exitCode );
		}
	}
}