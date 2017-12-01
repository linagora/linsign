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
package org.linagora.linsign.sddss.keystore.filter;

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
import java.util.Iterator;

import org.linagora.linsign.sddss.keystore.KeyStoreEntry;

public class KeystoreFilters {

	private ArrayList<KeystoreEntryFilter> filters;

	private boolean enabled = false;

	public KeystoreFilters() {
		filters = new ArrayList<KeystoreEntryFilter>();
		enabled = true;
	}

	/**
	 * add a filter on the keystore
	 * 
	 * @param filter
	 */
	public void add(KeystoreEntryFilter filter) {
		filters.add(filter);
		enabled = true;
	}

	/**
	 * check if one keystore entry can be accepted with all declared Filters
	 * 
	 * @param cert
	 *            one keystore entry
	 * @return true if acccepted
	 */
	public boolean acceptMyEntry(KeyStoreEntry cert) {

		if (!enabled)
			return true;

		for (Iterator<KeystoreEntryFilter> it = filters.listIterator(); it.hasNext();) {
			KeystoreEntryFilter filter = it.next();
			if (!filter.accept(cert)) {
				return false;
			}
		}
		return true;
	}

	public void setEnabled(boolean activation) {
		enabled = activation;
	}

}
