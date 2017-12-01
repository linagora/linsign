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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

import org.linagora.linsign.application.langue.SelectorLanguage;
import org.linagora.linsign.application.langue.Text;

@SuppressWarnings("serial")
public class CertificatTableModel extends AbstractTableModel implements PropertyChangeListener {

	private final int NUMBER_COLUMN_TABLE = 6;
	private final CertificatManager manager;
	private final SelectorLanguage selectorLangue;

	public CertificatTableModel(CertificatManager manager, SelectorLanguage selectorLangue) {
		super();
		this.manager = manager;
		this.selectorLangue = selectorLangue;
		manager.propertyChangeSupport.addPropertyChangeListener(this);
		for (CertificatView object : manager.getCertificat()) {
			object.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == manager) {
			// OK, not the cleanest thing, just to get the gist of it.
			if (evt.getPropertyName().equals("objects")) {
				((CertificatView) evt.getNewValue()).getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			fireTableDataChanged();
		} else if (evt.getSource() instanceof CertificatView) {
			int index = manager.getCertificat().indexOf(evt.getSource());
			fireTableRowsUpdated(index, index);
		}
	}

	@Override
	public int getColumnCount() {
		return NUMBER_COLUMN_TABLE;
	}

	@Override
	public int getRowCount() {
		return manager.getCertificat().size();
	}

	public CertificatView getValueAt(int row) {
		return manager.getCertificat().get(row);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return getValueAt(rowIndex).isSelected();
		case 1:
			return getValueAt(rowIndex).getName();
		case 2:
			return getValueAt(rowIndex).getIdCert();
		case 3:
			return getValueAt(rowIndex).printDateStartFormat();
		case 4:
			return getValueAt(rowIndex).printDateEndFormat();
		case 5:
			return getValueAt(rowIndex).getSerialNumber();
		}
		return null;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			getValueAt(rowIndex).setSelected(Boolean.TRUE.equals(value));
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0:
			return Boolean.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
			return String.class;
		case 4:
			return String.class;
		case 5:
			return String.class;
		}
		return Object.class;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "";
		case 1:
			return selectorLangue.printText(Text.NAME_CN);
		case 2:
			return selectorLangue.printText(Text.CERTIFICATE_ID);
		case 3:
			return selectorLangue.printText(Text.DATE_START_VALIDITY);
		case 4:
			return selectorLangue.printText(Text.DATE_END_VALIDITY);
		case 5:
			return selectorLangue.printText(Text.SERIAL_NUMBER);
		}
		return null;
	}
}
