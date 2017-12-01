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
package org.linagora.linsign.application.exception;

@SuppressWarnings("serial")
public class LinSignException extends Exception {

	private final LinSignExceptionKind kind;

	public LinSignException(final String message) {
		super(LinSignExceptionKind.INTERNAL_SERVER_ERROR + " " + message);
		kind = LinSignExceptionKind.INTERNAL_SERVER_ERROR;
	}

	public LinSignException(final String message, final LinSignExceptionKind kind) {
		super(kind + " " + message);
		this.kind = kind;
	}

	public LinSignException(final LinSignExceptionKind kind) {
		super(kind + " " + kind.getMessage());
		this.kind = kind;
	}

	public LinSignException(final LinSignExceptionKind kind, final Throwable cause) {
		super(kind + " " + kind.getMessage(), cause);
		this.kind = kind;
	}

	public LinSignException(final String message, final LinSignExceptionKind kind, final Throwable cause) {
		super(kind + " " + message, cause);
		this.kind = kind;
	}

	public LinSignException(final String message, final Throwable cause) {
		super(LinSignExceptionKind.INTERNAL_SERVER_ERROR + " " + message, cause);
		kind = LinSignExceptionKind.INTERNAL_SERVER_ERROR;
	}

	public LinSignExceptionKind getExceptionKind() {
		return kind;
	}
}