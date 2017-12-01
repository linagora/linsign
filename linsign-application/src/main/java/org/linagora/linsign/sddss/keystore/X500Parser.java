/**
 *  LinSign - Electronic signature application
 *  
 *  Copyright © 2008--2017 LINAGORA, www.linagora.com
 *  
 *  SPDX-License-Identifier: AGPL-3.0
 *  
 *  This file is part of LinSign.
 *  
 *  LinSign is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of
 *  the License, or (at your option) any later version.
 *  
 *  LinSign is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with LinSign.  If not, see <http://www.gnu.org/licenses/agpl.html>.
 */
package org.linagora.linsign.sddss.keystore;

/*
 * #%L
 * signature-client
 * %%
 * Copyright (C) 2013 - 2015 gSafe
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import javax.security.auth.x500.X500Principal;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * X500 Parser, returns the CN (Common Name) of a given X500Principal.
 *
 */
public class X500Parser {

	X500Principal principal;
	List<X500Element> x500elements;

	/** Creates a new instance of X500Parser */
	public X500Parser(X500Principal principal) {
		this.principal = principal;
		String principalString = principal.toString();
		// parsing

		this.x500elements = new ArrayList<X500Element>();

		int from = 0;
		while (from < principalString.length() - 1) {
			int comaIndex = principalString.indexOf(",", from);
			if (comaIndex < 0) {
				comaIndex = principalString.length();
			}
			String entry = principalString.substring(from, comaIndex).trim();
			int equalIndex = entry.indexOf("=");
			if (equalIndex < 0) {
				System.err.println("Warning : X500Principal Parsing : no " + "equal found in X500 entry");
				break;
			}
			String key = entry.substring(0, equalIndex).trim();
			String value = entry.substring(equalIndex + 1).trim();
			this.x500elements.add(new X500Element(key, value));
			from = comaIndex + 1;
		}

	}

	/** Returns the first 'CN' value, or the whole DN if CN is not found. */
	public String getCN() {
		for (Iterator<X500Element> it = this.x500elements.iterator(); it.hasNext();) {
			X500Element x500el = it.next();
			if (x500el.key.toUpperCase().equals("CN")) {
				return x500el.value;
			}
		}
		return this.principal.toString();
	}

	public String toString() {
		String out = new String();
		for (Iterator<X500Element> it = this.x500elements.iterator(); it.hasNext();) {
			X500Element x500el = it.next();
			String key = x500el.key;
			String value = x500el.value;
			out = out + key + "=" + value + "\n";
		}
		return out;
	}

	class X500Element {
		public String key;
		public String value;

		public X500Element(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}
}
