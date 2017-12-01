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

public enum Status {
	
	PROCESS_START("Processus started", 1),
	APPLICATION_INIT("Initialisation of application succes", 2),
	SKIN_INIT("Find img for the skin", 3),
	KEYSTORE_INIT("Browser certificat found", 4),
	CERTIFICAT_SELECT("Certificat selected sucess", 5),
	CERTIFICAT_VALIDITY("Certificat is valided", 6),
	ISS_WS_GET_FILE_TO_SIGN("Get file to sign from ISS service", 7),
	LINSING_WS_SIGN_FILE("File signed by LinSign", 8),
	ISS_SIGNED_FILE_SOTCKED("File signed has been send to ISS service", 9),
	PROCESS_END("Processus succes", 10),
	
	USER_END_PROCESS("User has stoped the processus", 1000),
	
	ERROR_APPLICATION_INIT("Error with initialisation parameter",1001),
	ERROR_SKIN("Error to find the skin", 1002),
	ERROR_KEYSTORE_INIT("Browser certificat not found", 1003),
	ERROR_KEYSTORE("Error with the keystore selection", 1004),
	ERROR_CERTIFICAT_LIST("Error to list certificat user", 1005),
	ERROR_CERTIFICAT_VALIDITY("Unvalid certificat", 1006),
	ERROR_ISS_WS_GET_FILE_TO_SIGN("Error to find ISS service file", 1007),
	ERROR_LINSING_WS_SIGN_FILE("Error to sign File", 1008),
	ERROR_ISS_STOCK_SIGNED_FILE("Error to stock file in ISS service", 1009),
	ERROR_JSON_PARSE("Error to parse the json", 1010),
	ERROR_JSON_PARSE_FILE("Error to read the map", 1011),
	
	UNKNOW_ERROR("Unknow error", 1100),
	INTERNAL_ERROR("Application error", 1101);
	
	private final String text;
	private final int code;


	private Status(final String text, final int code) {
		this.text = text;
		this.code = code;
	}

	@Override
	public String toString() {
		return text;
	}

	public String getText() {
		return text;
	}

	public int getCode() {
		return code;
	}
	
	public String getName(){
		return this.name();
	}
	
	
}
