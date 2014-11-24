/**
 * Copyright 2013-2014 Linagora, Université Joseph Fourier, Floralis
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

package net.roboconf.core.model.parsing;

/**
 * A block for orphan comments (VS in-line comments, which are stored somewhere else).
 * @author Vincent Zurczak - Linagora
 */
public class BlockComment extends AbstractIgnorableInstruction {

	/**
	 * Constructor.
	 * @param declaringFile the definition file
	 * @param content the block's content
	 */
	public BlockComment( FileDefinition declaringFile, String content ) {
		super( declaringFile, content );
	}

	/*
	 * (non-Javadoc)
	 * @see net.roboconf.core.model.parsing.AbstractBlock#getInstructionType()
	 */
	@Override
	public int getInstructionType() {
		return AbstractBlock.COMMENT;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Comment region";
	}
}