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
import java.util.ArrayList;
import java.util.List;

public class CertificatManager {
	PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private List<CertificatView> listCertificat = new ArrayList<CertificatView>();

	public void addObject(CertificatView object) {
		listCertificat.add(object);
		object.setManager(this);
		propertyChangeSupport.firePropertyChange("objects", null, object);
	}

	public List<CertificatView> getCertificat() {
		return listCertificat;
	}

	public void setAsSelected(CertificatView certificat) {
		for (CertificatView o : listCertificat) {
			o.setSelected(certificat == o);
		}
	}
}
