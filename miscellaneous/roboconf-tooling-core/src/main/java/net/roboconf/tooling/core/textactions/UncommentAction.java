/**
 * Copyright 2016-2017 Linagora, Université Joseph Fourier, Floralis
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

package net.roboconf.tooling.core.textactions;

import net.roboconf.core.dsl.ParsingConstants;
import net.roboconf.core.utils.Utils;

/**
 * @author Vincent Zurczak - Linagora
 */
public class UncommentAction extends AbstractCommentAction {

	@Override
	protected String processLine( String line ) {
		return uncommentLine( line );
	}


	/**
	 * Uncomments a line.
	 * @param line a non-null line
	 * @return a non-null line
	 */
	public static String uncommentLine( String line ) {

		String result = line;
		if( ! Utils.isEmptyOrWhitespaces( line )
				&& line.trim().startsWith( ParsingConstants.COMMENT_DELIMITER ))
			result = line.replaceFirst( "^(\\s*)" + ParsingConstants.COMMENT_DELIMITER + "+", "$1" );

		return result;
	}
}
