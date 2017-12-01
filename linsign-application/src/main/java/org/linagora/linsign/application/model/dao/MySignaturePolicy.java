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

import eu.europa.esig.dss.wsclient.signature.DigestAlgorithm;
import eu.europa.esig.dss.wsclient.signature.SignatureForm;
import eu.europa.esig.dss.wsclient.signature.SignatureLevel;
import eu.europa.esig.dss.wsclient.signature.SignaturePackaging;

public class MySignaturePolicy {

	private SignatureForm signatureForm;
	private SignatureLevel signatureLevel;
	private DigestAlgorithm signatureAlgo;
	private SignaturePackaging signaturePackaging;

	public MySignaturePolicy(SignaturePackaging signaturePackaging, SignatureForm signatureForm,
			SignatureLevel signatureLevel, DigestAlgorithm signatureAlgo) {
		this.signatureForm = signatureForm;
		this.signatureLevel = signatureLevel;
		this.signatureAlgo = signatureAlgo;
		this.signaturePackaging = signaturePackaging;
	}

	public String getSignatureFormString() {
		return signatureForm.toString();
	}

	public SignatureForm getSignatureForm() {
		return signatureForm;
	}

	public void setSignatureForm(SignatureForm signatureForm) {
		this.signatureForm = signatureForm;
	}

	public String getSignatureLevelString() {
		return signatureLevel.toString();
	}

	public SignatureLevel getSignatureLevel() {
		return signatureLevel;
	}

	public void setSignatureLevel(SignatureLevel signatureLevel) {
		this.signatureLevel = signatureLevel;
	}

	public DigestAlgorithm getSignatureAlgo() {
		return signatureAlgo;
	}

	public void setSignatureAlgo(DigestAlgorithm signatureAlgo) {
		this.signatureAlgo = signatureAlgo;
	}

	public SignaturePackaging getSignaturePackaging() {
		return signaturePackaging;
	}

	public void setSignaturePackaging(SignaturePackaging signaturePackaging) {
		this.signaturePackaging = signaturePackaging;
	}

	@Override
	public String toString() {
		return "MySignaturePolicy [signatureForm=" + signatureForm + ", signatureLevel=" + signatureLevel
				+ ", signatureAlgo=" + signatureAlgo + ", signaturePackaging=" + signaturePackaging + "]";
	}

}
