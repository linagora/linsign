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
package org.linagora.linsign.application.view.certificat;

import java.beans.PropertyChangeSupport;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CertificatView {
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private CertificatManager manager;
	private String value;
	private boolean selected;

	private String idCert;
	private Date dateStart;
	private Date dateEnd;
	private BigInteger serialNumber;

	private final static DateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");

	public CertificatView(String value, String idCert, Date dateStart, Date dateEnd, BigInteger serialNumber) {
		this.value = value;
		this.idCert = idCert;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.serialNumber = serialNumber;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	public CertificatManager getManager() {
		return manager;
	}

	public void setManager(CertificatManager manager) {
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

	public void isAlone() {
		this.selected = true;
	}

	public boolean isSelected() {
		return selected;
	}

	public String getName() {
		return value;
	}

	public void setName(String value) {
		this.value = value;
		propertyChangeSupport.firePropertyChange("Name", null, value);
	}

	public String getIdCert() {
		return idCert;
	}

	public void setIdCert(String idCert) {
		this.idCert = idCert;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public String printDateStartFormat() {
		return targetFormat.format(dateStart);
	}

	public String printDateEndFormat() {
		return targetFormat.format(dateEnd);
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public BigInteger getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(BigInteger serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CertificatView other = (CertificatView) obj;
		if (idCert == null) {
			if (other.idCert != null)
				return false;
		} else if (!idCert.equals(other.idCert))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		}

		return true;
	}

}
