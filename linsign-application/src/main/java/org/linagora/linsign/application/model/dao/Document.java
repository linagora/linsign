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

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

	@JsonProperty("title")
	public String title;
	@JsonProperty("issuer")
	public String issuer;
	@JsonProperty("creationDate")
	public Timestamp creationDate;
	@JsonProperty("fileContent")
	public byte[] fileContent;
	@JsonProperty("reference")
	public String reference;

	@JsonCreator
	public Document(@JsonProperty("title") String title, @JsonProperty("issuer") String issuer,
			@JsonProperty("createAt") Timestamp createAt, @JsonProperty("fileContent") byte[] fileContent,
			@JsonProperty("reference") String reference) {
		super();
		this.title = title;
		this.issuer = issuer;
		this.creationDate = createAt;
		this.fileContent = fileContent;
		this.reference = reference;
	}
	
	public Document(Document doc) {
		super();
		this.title = doc.title;
		this.issuer = doc.issuer;
		this.creationDate = doc.creationDate;
		this.fileContent = doc.fileContent;
		this.reference = doc.reference;
	}

	public Document() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		return "Document [title=" + title + ", issuer=" + issuer + ", creationDate=" + creationDate + ", fileContent="
				+ fileContent.length + ", reference=" + reference + "]";
	}

}
