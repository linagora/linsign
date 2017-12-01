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
package org.linagora.linsign.client.keystore.wizard.swing;

/*
 * #%L
 * signature-client
 * %%
 * Copyright (C) 2013 - 2015 gSafe
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.awt.Dimension;
import java.security.KeyStore;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.linagora.linsign.client.applet.MessageConstants;
import org.linagora.linsign.client.keystore.KeyStoreEntry;
import org.linagora.linsign.client.keystore.KeyStoreUtils;
import org.linagora.linsign.client.keystore.filters.KeystoreFilters;
import org.linagora.linsign.exceptions.KeystoreAccessException;

/**Table that contains the certificates of a keystore
 */
public class KeyStoreTable extends JTable implements ListSelectionListener{
    
    protected List<KeyStoreEntry>  tableEntries;
    private KeyStoreEntry selectedKSEntry;
    private KeyStore ks;
    
    private static String[] columnNames;
    
    
    /** Creates a new instance of KeyStoreTable, loaded keystore. 
     * @throws KeystoreAccessException */
    public KeyStoreTable(KeyStore ks, KeystoreFilters filters) throws KeystoreAccessException{
        super();
        
    	//reload column n ames in case of change of language by the user
        columnNames =  new String[3];
    	columnNames[0]=MessageConstants.getmessage("selectkey.column.subject");
    	columnNames[1]=MessageConstants.getmessage("selectkey.column.issuer");
    	columnNames[2]=MessageConstants.getmessage("selectkey.column.expirationdate");
        
        
        this.ks = ks;
        this.tableEntries = KeyStoreUtils.getTableEntries(this.ks, filters);
        
        ListSelectionModel rowSM = this.getSelectionModel();
        rowSM.addListSelectionListener(this);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setModel(this.tableModel);
        this.getColumnModel().getColumn(0).setPreferredWidth(200);
        this.getColumnModel().getColumn(1).setPreferredWidth(200);
        this.getColumnModel().getColumn(2).setPreferredWidth(100);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.setPreferredScrollableViewportSize(new Dimension(550,100));
        
        if(tableEntries.size()!=0)
        this.setRowSelectionInterval(0,0); //default selection first entry
    }
    
    
    private AbstractTableModel tableModel = new AbstractTableModel() {
        public String getColumnName(int col) {
            return columnNames[col].toString();
        }
        
        public int getRowCount() {
            return tableEntries.size();
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }
        public Object getValueAt(int row, int col) {
            KeyStoreEntry entry = tableEntries.get(row);
            switch(col){
                case 0: return entry.getSubjectCN();
                case 1: return entry.getIssuerCN();
                case 2: return entry.getExpirationDate();
            }
            return null;
        }
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    
    //ListSelectionListener interface
    public void valueChanged(ListSelectionEvent e) {
        this.repaint(); // bug resolved
        
        //The getValueIsAdjusting method returns true if the user is
        //still manipulating the selection.
        if (e.getValueIsAdjusting()) return;
        ListSelectionModel lsm =
                (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty()) {
            this.selectedKSEntry = null;
        } else {
            KeyStoreEntry kse = tableEntries.get(lsm.getMinSelectionIndex());
            this.selectedKSEntry = kse;
            
        }
    }
    
    /**return null if no key is selected.*/
    public KeyStoreEntry getSelectedEntry(){
        if(this.selectedKSEntry==null){
            return null;
        }
        return this.selectedKSEntry;
    }
    
    public int getNumberEntries(){
    		return tableEntries.size();
    	}
}

