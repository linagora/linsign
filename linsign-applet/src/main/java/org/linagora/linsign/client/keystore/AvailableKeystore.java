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
package org.linagora.linsign.client.keystore;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public enum AvailableKeystore {
	
	ALL(KeystoreType.BROWSER, KeystoreType.PKCS12, KeystoreType.JKS, KeystoreType.PKCS11),
	NONE();

		AvailableKeystore(final KeystoreType... keystoreTypes) {
			this.availableSigner = new HashSet<KeystoreType>();
			if(null != keystoreTypes) {
				for(KeystoreType st : keystoreTypes) {
					this.availableSigner.add(st);
				}
			}
		}
		
		/**
		 * @param csvKeystoreList csv list of keystore, can not be empty
		 * @return
		 */public static Set<KeystoreType> fromString(String csvKeystoreList) {
			 
			//we prefer linked list to keep the sequence order (better than hashset)
			//Set will care of unicity
			if(csvKeystoreList==null) return new LinkedHashSet<KeystoreType>();
			LinkedHashSet<KeystoreType> myAvailableSigners = new LinkedHashSet<KeystoreType>();
				
			String[] items = csvKeystoreList.split(",");
			KeystoreType st = null;
			
			for (int i = 0; i < items.length; i++) {
				st = KeystoreType.fromString(items[i]); //can send java.lang.IllegalArgumentException
				myAvailableSigners.add(st);
			}
			
			return myAvailableSigners;
		}
		
		
		private final Set<KeystoreType> availableSigner;
		
		public Set<KeystoreType> getAvailableSigner() {
			return Collections.unmodifiableSet(this.availableSigner);
		}
		
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	} 
	
