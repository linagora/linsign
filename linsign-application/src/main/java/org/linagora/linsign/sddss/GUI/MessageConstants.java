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
package org.linagora.linsign.sddss.GUI;

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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * give message for the swing utilities use messages.properties
 *
 */
public class MessageConstants {

	public static final String MESSAGE_PROPERTY = "messages";
	private static ResourceBundle bundle;

	static {
		// default in french
		bundle = ResourceBundle.getBundle(MESSAGE_PROPERTY);
	}

	public static String getmessage(String key) {
		return bundle.getString(key);
	}

	public static String getmessage(String key, Object... args) {
		return MessageFormat.format(bundle.getString(key), args);
	}

	public static void reloadBundleWithLocale(Locale locale) {
		bundle = ResourceBundle.getBundle(MESSAGE_PROPERTY, locale);
	}

}