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
package org.linagora.linsign.application.view.document;

import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;

import org.linagora.linsign.application.model.dao.Document;

public class DocumentView {
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private DocumentManager manager;

	private Document docIss;
	private boolean selected;

	public DocumentView(Document docIss) {
		this.docIss = docIss;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	public DocumentManager getManager() {
		return manager;
	}

	public void setManager(DocumentManager manager) {
		this.manager = manager;
		propertyChangeSupport.firePropertyChange("manager", null, manager);
	}

	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			if (selected) {
				manager.setAsSelected(this);
			}
			propertyChangeSupport.firePropertyChange("Selected", !selected, selected);
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public String getTitle() {
		return docIss.getTitle();
	}

	public void setName(String value) {
		this.docIss.setTitle(value);
		propertyChangeSupport.firePropertyChange("Title", null, value);
	}

	public String getIssuer() {
		return docIss.getIssuer();
	}

	public void setIssuer(String issuer) {
		this.docIss.setIssuer(issuer);
		;
	}

	public Timestamp getCreateAt() {
		return this.docIss.getCreationDate();
	}

	public void setCreateAt(Timestamp createAt) {
		this.docIss.setCreationDate(createAt);
	}

	public byte[] getFile() {
		return this.docIss.getFileContent();
	}

	public void setFile(byte[] file) {
		this.docIss.setFileContent(file);
	}

	public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

}
