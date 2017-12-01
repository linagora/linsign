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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class CertificatRadioButtonCellEditorRenderer extends AbstractCellEditor
		implements TableCellRenderer, TableCellEditor, ActionListener {

	private JRadioButton radioButton;

	public CertificatRadioButtonCellEditorRenderer() {
		this.radioButton = new JRadioButton();
		radioButton.addActionListener(this);
		radioButton.setOpaque(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		radioButton.setSelected(Boolean.TRUE.equals(value));
		return radioButton;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		radioButton.setSelected(Boolean.TRUE.equals(value));
		return radioButton;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		stopCellEditing();
	}

	@Override
	public Object getCellEditorValue() {
		return radioButton.isSelected();
	}

}