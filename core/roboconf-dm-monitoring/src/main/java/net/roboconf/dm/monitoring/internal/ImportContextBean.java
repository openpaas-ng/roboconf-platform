/**
 * Copyright 2013-2015 Linagora, Université Joseph Fourier, Floralis
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

package net.roboconf.dm.monitoring.internal;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Context bean for an imported variable.
 *
 * @author Pierre Bourret - Université Joseph Fourier
 */
public class ImportContextBean {
	String component;
	InstanceContextBean instance;
	final Set<VariableContextBean> variables = new LinkedHashSet<VariableContextBean>();

	public String getComponent() {
		return this.component;
	}

	public InstanceContextBean getInstance() {
		return this.instance;
	}

	public Set<VariableContextBean> getVariables() {
		return this.variables;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("import from " + this.instance.path + ':');
		for (final Iterator<VariableContextBean> i = this.variables.iterator(); i.hasNext(); ) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
