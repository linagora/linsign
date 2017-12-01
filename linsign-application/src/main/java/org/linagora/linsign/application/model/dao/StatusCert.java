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
package org.linagora.linsign.application.model.dao;

public enum StatusCert {

	TRUSTED_CERT("TRUSTED_CERT"),
	EXPIRED_CERT("EXPIRED_CERT"),
	NOT_TRUSTED_CERT("NOT_TRUSTED_CERT"),
	REVOKED_CERT("REVOKED_CERT"),
	UNKNOWN_REVOCATION_CERT("UNKNOWN_REVOCATION_CERT"),
	NOT_YET_VERIFIED_CERT("NOT_YET_VERIFIED_CERT");
	
	private final String status;
	
	private StatusCert(final String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
