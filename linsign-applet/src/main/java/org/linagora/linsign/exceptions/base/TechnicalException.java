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
package org.linagora.linsign.exceptions.base;

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

import org.linagora.linsign.exceptions.config.ExceptionMessage;

public class TechnicalException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8354967136329252884L;

	public TechnicalException() {
		super();
	}

	public TechnicalException(String message) {
		super(message);
	}

	public TechnicalException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
	/**
	 * special constructor when no ressource bundle is available
	 * this is the case when we have a service which is not defined with spring
	 * the spring context is loaded and the ressource bundle is given.
	 * @param cause the cause that produces exception.
	 * @param exceptionCode exception id, must exists in exceptions.properties file.
	 * @param args elements to bind to compose the final message. 
	 * @param defaultMessage the default message if necessary.
	 */
	public TechnicalException(Throwable cause, String exceptionCode, Object[] args) {
		this(buildMessage(exceptionCode, args), cause);
	}
	
	
	
	/**
	 * Exception message builder.
	 * @param messageRessource contains exceptions data (exceptions.properties).
	 * @param exceptionCode exception id, must exists in exceptions.properties file.
	 * @param args elements to bind to compose the final message. 
	 * @param defaultMessage the default message if necessary.
	 * @return the complete message of exception identified with exceptionCode
	 */
	private static String buildMessage(String exceptionCode, Object[] args) {
		return ExceptionMessage.getmessage(exceptionCode, args);
	}
}
