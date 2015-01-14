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

package net.roboconf.integration.test;

import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.io.File;

import javax.inject.Inject;

import net.roboconf.core.internal.tests.TestUtils;
import net.roboconf.core.model.beans.Instance;
import net.roboconf.core.model.beans.Instance.InstanceStatus;
import net.roboconf.core.model.helpers.InstanceHelpers;
import net.roboconf.dm.management.ManagedApplication;
import net.roboconf.dm.management.Manager;
import net.roboconf.integration.test.internal.IntegrationTestsUtils;
import net.roboconf.integration.test.internal.IntegrationTestsUtils.MyMessageProcessor;
import net.roboconf.integration.test.internal.MyHandler;
import net.roboconf.integration.test.internal.MyTargetResolver;
import net.roboconf.pax.probe.AbstractTest;
import net.roboconf.pax.probe.DmWithAgentInMemoryTest;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * Test agent termination.
 * <p>
 * Start two in-memory agents, one (1) depending on the other (2).
 * We kill the VM of (2). The DM should notify (1) that (2) was killed.
 * (1)'s state should be impacted and switch to the "starting" state.
 * </p>
 *
 * @author Vincent Zurczak - Linagora
 */
@ExamReactorStrategy( PerClass.class )
public class AgentTerminationTest extends DmWithAgentInMemoryTest {

	private static final String APP_LOCATION = "my.app.location";

	@Inject
	protected Manager manager;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();


	@ProbeBuilder
	public TestProbeBuilder probeConfiguration( TestProbeBuilder probe ) {

		// We need to specify the classes we need
		// and that come from external modules.
		probe.addTest( AbstractTest.class );
		probe.addTest( DmWithAgentInMemoryTest.class );
		probe.addTest( TestUtils.class );
		probe.addTest( TemporaryFolder.class );

		probe.addTest( MyHandler.class );
		probe.addTest( MyTargetResolver.class );
		probe.addTest( IntegrationTestsUtils.class );
		probe.addTest( MyMessageProcessor.class );

		return probe;
	}


	@Override
	@Configuration
	public Option[] config() {

		String appLocation = null;
		try {
			File resourcesDirectory = TestUtils.findTestFile( "/simple", getClass());
			appLocation = resourcesDirectory.getAbsolutePath();

		} catch( Exception e ) {
			// nothing
		}

		return OptionUtils.combine(
				super.config(),
				systemProperty( APP_LOCATION ).value( appLocation ));
	}


	@Override
	public void run() throws Exception {
		Assume.assumeTrue( IntegrationTestsUtils.rabbitMqIsRunning());

		// Update the manager
		this.manager.setConfigurationDirectoryLocation( this.folder.newFolder().getAbsolutePath());
		this.manager.reconfigure();

		// Load the application
		String appLocation = System.getProperty( APP_LOCATION );
		ManagedApplication ma = this.manager.loadNewApplication( new File( appLocation ));
		Assert.assertNotNull( ma );
		Assert.assertEquals( 1, this.manager.getAppNameToManagedApplication().size());

		Instance mysql = InstanceHelpers.findInstanceByPath( ma.getApplication(), "/MySQL VM/MySQL" );
		Instance app = InstanceHelpers.findInstanceByPath( ma.getApplication(), "/App VM/App" );
		Assert.assertNotNull( mysql );
		Assert.assertNotNull( app );

		this.manager.changeInstanceState( ma, mysql.getParent(), InstanceStatus.DEPLOYED_STARTED );
		this.manager.changeInstanceState( ma, app.getParent(), InstanceStatus.DEPLOYED_STARTED );
		Thread.sleep( 800 );

		this.manager.changeInstanceState( ma, mysql, InstanceStatus.DEPLOYED_STARTED );
		this.manager.changeInstanceState( ma, app, InstanceStatus.DEPLOYED_STARTED );
		Thread.sleep( 300 );

		Assert.assertEquals( InstanceStatus.DEPLOYED_STARTED, mysql.getParent().getStatus());
		Assert.assertEquals( InstanceStatus.DEPLOYED_STARTED, mysql.getStatus());
		Assert.assertEquals( InstanceStatus.DEPLOYED_STARTED, app.getParent().getStatus());
		Assert.assertEquals( InstanceStatus.DEPLOYED_STARTED, app.getStatus());

		// Kill the VM of MySQL. The App should be "starting" because one of its dependencies is missing.
		this.manager.changeInstanceState( ma, mysql.getParent(), InstanceStatus.NOT_DEPLOYED );
		Thread.sleep( 300 );
		Assert.assertEquals( InstanceStatus.NOT_DEPLOYED, mysql.getStatus());
		Assert.assertEquals( InstanceStatus.NOT_DEPLOYED, mysql.getParent().getStatus());
		Assert.assertEquals( InstanceStatus.DEPLOYED_STARTED, app.getParent().getStatus());
		Assert.assertEquals( InstanceStatus.STARTING, app.getStatus());

		// Undeploy them all
		this.manager.undeployAll( ma, null );
		Thread.sleep( 300 );
		for( Instance inst : InstanceHelpers.getAllInstances( ma.getApplication()))
			Assert.assertEquals( inst.getName(), InstanceStatus.NOT_DEPLOYED, inst.getStatus());
	}
}