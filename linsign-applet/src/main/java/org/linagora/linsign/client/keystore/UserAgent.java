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

import java.io.File;

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



public class UserAgent {

	public static final String FIREFOX = "Firefox";
	public static final String IE = "MSIE";
	public static final String ICEWEASEL = "Iceweasel";
	
	public static final String SAFARI = "Safari"; 
	public static final String MAC = "Macintosh"; 
	
	
	public static boolean isFirefox(String useragent){
		boolean detected = false;
		if (useragent.indexOf(FIREFOX) != -1) {
			detected = true;
		}

		/* Check for debian's firefox, called iceweasel */
		if (useragent.indexOf(ICEWEASEL) != -1) {
			detected = true;
		}

		return detected;
	}
	public static boolean isInternetExplorer(String useragent){
		if (useragent.indexOf(IE)==-1) return false;
		else return true;
	}
	
	public static boolean isSafariMac(String useragent){
		if (useragent.indexOf(SAFARI)!=-1 && useragent.indexOf(MAC)!=-1) return true;
		else return false;
	}
	
	
	/**
	 * ckeck supported browser
	 * @param useragent
	 * @return
	 */
	public static boolean isBrowserSupported(String useragent){
		return (isFirefox(useragent) || isInternetExplorer(useragent)||isSafariMac(useragent));
		//return (isFirefox(useragent) || isInternetExplorer(useragent));
	}
	
}
