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
package org.linagora.linsign.client.keystore.filters;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.linagora.linsign.client.keystore.KeyStoreEntry;

public class IssuerDnCertificateFilter extends KeystoreEntryFilter {

	private List<String> matchStrings;

	public IssuerDnCertificateFilter(List<String> matchDn) {
		
		this.matchStrings = new ArrayList<String>(matchDn.size());
		
		for (String onematch : matchDn) {
			matchStrings.add(onematch.toLowerCase());
		}
		
	}

	/**
	 * match the part of the dn of given CA
	 * if many put char :: as a separator
	 * @param matchDn
	 */
	public IssuerDnCertificateFilter(String matchDn){
		this(Arrays.asList(matchDn.split("::")));
	}
	
	
	public IssuerDnCertificateFilter(String[] matchDn){
		this(Arrays.asList(matchDn));
	}

	public boolean accept(KeyStoreEntry certificate) {
		boolean res = false;

		for (String str : matchStrings) {
			if (certificate.getIssuer().toLowerCase().indexOf(str) != -1) {
				res = true;
				break;
			}

		}

		return res;
	}
}
