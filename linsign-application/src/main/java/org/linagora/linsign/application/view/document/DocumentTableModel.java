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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import org.linagora.linsign.application.langue.SelectorLanguage;
import org.linagora.linsign.application.langue.Text;

@SuppressWarnings("serial")
public class DocumentTableModel extends AbstractTableModel implements PropertyChangeListener {

	private final int NUMBER_COLUMN_TABLE = 4;
	private final DocumentManager manager;
	private final SelectorLanguage selectorLangue;

	public DocumentTableModel(DocumentManager manager, SelectorLanguage selectorLangue) {
		super();
		this.manager = manager;
		this.selectorLangue = selectorLangue;
		manager.propertyChangeSupport.addPropertyChangeListener(this);
		for (DocumentView object : manager.getDocument()) {
			object.getPropertyChangeSupport().addPropertyChangeListener(this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == manager) {
			// OK, not the cleanest thing, just to get the gist of it.
			if (evt.getPropertyName().equals("objects")) {
				((DocumentView) evt.getNewValue()).getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			fireTableDataChanged();
		} else if (evt.getSource() instanceof DocumentView) {
			int index = manager.getDocument().indexOf(evt.getSource());
			fireTableRowsUpdated(index, index);
		}
	}

	@Override
	public int getColumnCount() {
		return NUMBER_COLUMN_TABLE;
	}

	@Override
	public int getRowCount() {
		return manager.getDocument().size();
	}

	public DocumentView getValueAt(int row) {
		return manager.getDocument().get(row);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return getValueAt(rowIndex).getTitle();
		case 1:
			return getValueAt(rowIndex).getIssuer();
		case 2:
			return getValueAt(rowIndex).getCreateAt();
		case 3:
			return selectorLangue.printText(Text.PREVIEW);
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
		return columnIndex == -1;	//Disable edit table value
	}

	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		case 2:
			return Date.class;
		case 3:
			return String.class;
		}
		return Object.class;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return selectorLangue.printText(Text.TITLE);
		case 1:
			return selectorLangue.printText(Text.TRANSMITTER);
		case 2:
			return selectorLangue.printText(Text.CREATED_AT);
		case 3:
			return selectorLangue.printText(Text.ACTION);
		}
		return null;
	}
}
