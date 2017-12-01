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
package org.linagora.linsign.sddss.dao;

import java.awt.*;
import java.io.*;

import org.bouncycastle.util.encoders.Base64;

public class PDFVisibleProperties implements Serializable {

	private static final long serialVersionUID = -8455629152999679288L;

	@Override
	public String toString() {
		return "PDFVisibleProperties{" + "reference=" + reference +",pdfSignaturePageNb=" + pdfSignaturePageNb + ", pdfSignatureX1="
				+ pdfSignatureX1 + ", pdfSignatureY1=" + pdfSignatureY1 + ", pdfSignatureText='" + pdfSignatureText
				+ '\'' + ", pdfSignatureFont=" + pdfSignatureFont + ", pdfSignatureOrientation="
				+ pdfSignatureOrientation + '}';
	}

	private String reference;

	private int pdfSignaturePageNb;

	private int pdfSignatureX1;

	private int pdfSignatureY1;

	private String pdfSignatureText;

	private Font pdfSignatureFont;

	private int pdfSignatureOrientation;

	public PDFVisibleProperties() {
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public int getPdfSignaturePageNb() {
		return pdfSignaturePageNb;
	}

	public void setPdfSignaturePageNb(int pdfSignaturePageNb) {
		this.pdfSignaturePageNb = pdfSignaturePageNb;
	}

	public int getPdfSignatureX1() {
		return pdfSignatureX1;
	}

	public void setPdfSignatureX1(int pdfSignatureX1) {
		this.pdfSignatureX1 = pdfSignatureX1;
	}

	public int getPdfSignatureY1() {
		return pdfSignatureY1;
	}

	public void setPdfSignatureY1(int pdfSignatureY1) {
		this.pdfSignatureY1 = pdfSignatureY1;
	}

	public String getPdfSignatureText() {
		return pdfSignatureText;
	}

	public void setPdfSignatureText(String pdfSignatureText) {
		this.pdfSignatureText = pdfSignatureText;
	}

	public Font getPdfSignatureFont() {
		return pdfSignatureFont;
	}

	public void setPdfSignatureFont(Font pdfSignatureFont) {
		this.pdfSignatureFont = pdfSignatureFont;
	}

	public int getPdfSignatureOrientation() {
		return pdfSignatureOrientation;
	}

	public void setPdfSignatureOrientation(int pdfSignatureOrientation) {
		this.pdfSignatureOrientation = pdfSignatureOrientation;
	}

	public String encodePDFMarkProperties() throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream so = new ObjectOutputStream(bo);
		so.writeObject(this);
		so.flush();
		String encodedPdfVisibleProperties = new String(Base64.encode(bo.toByteArray()));

		return encodedPdfVisibleProperties;
	}
}