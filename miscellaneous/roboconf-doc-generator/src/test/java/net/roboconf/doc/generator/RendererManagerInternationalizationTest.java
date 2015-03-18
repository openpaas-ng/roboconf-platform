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

package net.roboconf.doc.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import net.roboconf.core.utils.Utils;
import net.roboconf.doc.generator.RenderingManager.Renderer;

import org.junit.Test;

/**
 * @author Vincent Zurczak - Linagora
 */
public class RendererManagerInternationalizationTest extends AbstractTestForRendererManager {

	@Test
	public void testHtml_en_US() throws Exception {

		Assert.assertEquals( 0, this.outputDir.listFiles().length );
		Map<String,String> options = new HashMap<String,String> ();
		options.put( DocConstants.OPTION_LOCALE, "en_US" );

		this.rm.render( this.outputDir, this.alr.getApplication(), this.applicationDirectory, Renderer.HTML, options );
		checkInternationalization( "Components" );
	}


	@Test
	public void testHtml_fr_FR() throws Exception {

		Assert.assertEquals( 0, this.outputDir.listFiles().length );
		Map<String,String> options = new HashMap<String,String> ();
		options.put( DocConstants.OPTION_LOCALE, "fr_FR" );

		this.rm.render( this.outputDir, this.alr.getApplication(), this.applicationDirectory, Renderer.HTML, options );
		checkInternationalization( "Composants" );
	}


	@Test
	public void testHtml_unsupportedLocale() throws Exception {

		Assert.assertEquals( 0, this.outputDir.listFiles().length );
		Map<String,String> options = new HashMap<String,String> ();
		options.put( DocConstants.OPTION_LOCALE, "oops" );

		this.rm.render( this.outputDir, this.alr.getApplication(), this.applicationDirectory, Renderer.HTML, options );
		checkInternationalization( "Components" );
	}


	/**
	 * Verifies assertions about the generated file.
	 * @param textToSearch
	 */
	private void checkInternationalization( String textToSearch ) throws Exception {

		File f = new File( this.outputDir, "roboconf.html" );
		Assert.assertTrue( f.exists());

		String s = Utils.readFileContent( f );
		verifyContent( s );
		Assert.assertTrue( s.contains( textToSearch ));
	}
}
