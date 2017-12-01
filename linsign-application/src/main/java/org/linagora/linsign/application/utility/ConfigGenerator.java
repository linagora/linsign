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
package org.linagora.linsign.application.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigGenerator {

	private String userAgent;
	private String skinURL;
	private String wsLinSignURL;
	private String issServiceURL;
	private String idSignature;
	private String langue;
	private String[] pdfVisiblePropertiesBase64;
	private String clientId;
	private String clientPassword;
	private String policyReference;
	private String signerNameConstraints;

	public ConfigGenerator() {
	}

	public ConfigGenerator(String pathConfig) throws IOException {
		Properties prop = new Properties();
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(new FileInputStream(pathConfig), "UTF-8");
			prop.load(in);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
		}

		this.userAgent = prop.getProperty("userAgent");
		this.skinURL = prop.getProperty("urlSkin");
		this.wsLinSignURL = prop.getProperty("wsLinSignURL");
		this.issServiceURL = prop.getProperty("issService");
		this.idSignature = prop.getProperty("idSignature");
		this.langue = prop.getProperty("language");
		this.pdfVisiblePropertiesBase64 = prop.getProperty("pdf").split(",");
		this.clientId = prop.getProperty("clientId");
		this.clientPassword = prop.getProperty("clientPassword");
		this.policyReference = prop.getProperty("policyReference");
		this.signerNameConstraints = prop.getProperty("signerNameConstraints");

	}

	public String getIdSignature() {
		return idSignature;
	}

	public void setIdSignature(String idSignature) {
		this.idSignature = idSignature;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getLangue() {
		return langue;
	}

	public void setLangue(String langue) {
		this.langue = langue;
	}

	public String getIssServiceURL() {
		return issServiceURL;
	}

	public void setIssServiceURL(String issServiceURL) {
		this.issServiceURL = issServiceURL;
	}

	public String getWsLinSignURL() {
		return wsLinSignURL;
	}

	public void setWsLinSignURL(String wsLinSignURL) {
		this.wsLinSignURL = wsLinSignURL;
	}

	public String getSkinURL() {
		return skinURL;
	}

	public void setSkinURL(String skinURL) {
		this.skinURL = skinURL;
	}

	public String[] getPdfVisiblePropertiesBase64() {
		return pdfVisiblePropertiesBase64;
	}

	public void setPdfVisiblePropertiesBase64(String[] pdfVisiblePropertiesBase64) {
		this.pdfVisiblePropertiesBase64 = pdfVisiblePropertiesBase64;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientPassword() {
		return clientPassword;
	}

	public void setClientPassword(String clientPassword) {
		this.clientPassword = clientPassword;
	}

	public String getPolicyReference() {
		return policyReference;
	}

	public void setPolicyReference(String policyReference) {
		this.policyReference = policyReference;
	}

	public String getSignerNameConstraints() {
		return signerNameConstraints;
	}

	public void setSignerNameConstraints(String signerNameConstraints) {
		this.signerNameConstraints = signerNameConstraints;
	}

	@Override
	public String toString() {
		return "ConfigGenerator [userAgent=" + userAgent + ", skinURL=" + skinURL + ", wsLinSignURL=" + wsLinSignURL
				+ ", issServiceURL=" + issServiceURL + ", idSignature=" + idSignature + ", langue=" + langue
				+ ", pdfVisiblePropertiesBase64=" + pdfVisiblePropertiesBase64 + ", clientId=" + clientId
				+ ", clientPassword=" + clientPassword + ", policyReference=" + policyReference
				+ ", signerNameConstraints=" + signerNameConstraints + "]";
	}
}
