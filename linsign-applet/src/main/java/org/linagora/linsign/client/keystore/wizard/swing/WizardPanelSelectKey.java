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
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.linagora.linsign.client.applet.MessageConstants;
import org.linagora.linsign.client.keystore.KeyStoreEntry;
import org.linagora.linsign.client.keystore.KeyStoreUtils;
import org.linagora.linsign.client.keystore.filters.KeyUsageSignatureFilter;
import org.linagora.linsign.client.keystore.filters.KeystoreFilters;
import org.linagora.linsign.exceptions.KeystoreAccessException;

public class WizardPanelSelectKey extends JDialog {

	//result selected entry
	private KeyStoreEntry kseSelected;    

	/** Creates a new instance of WizardPanelSelectKey */


	private JLabel mChooseCertFileLabel = new JLabel();


	private JButton mSignButton = new JButton();
	private JButton mCancelButton = new JButton();


	private boolean mResult = false;

	private KeyStoreTable keyStoreTable = null;



	/**
	 * Initializes the dialog - creates and initializes its GUI controls.
	 * @throws KeyStoreException 
	 * @throws KeystoreAccessException 
	 */

	public WizardPanelSelectKey(KeyStore allkeys, KeystoreFilters filters) throws KeystoreAccessException {
		this(allkeys,filters,null,true); 
	}

	public WizardPanelSelectKey(KeyStore allkeys) throws KeystoreAccessException {
		this(allkeys,null,null,true); //no filters on keys
	}

	/**
	 * Initializes the dialog - creates and initializes its GUI controls.
	 * @throws KeyStoreException 
	 * @throws KeystoreAccessException 
	 */
	public WizardPanelSelectKey(KeyStore allkeys, KeystoreFilters filters,JFrame parentFrame,boolean modal) throws KeystoreAccessException {

		super(parentFrame,modal);

		// Initialize the dialog
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(640, 480));


		this.setBackground(SystemColor.control);
		this.setTitle(MessageConstants.getmessage("selectkey.title"));
		this.setResizable(false);

		// Center the dialog in the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = this.getSize();
		int centerPosX = (screenSize.width - dialogSize.width) / 2;
		int centerPosY = (screenSize.height - dialogSize.height) / 2;
		setLocation(centerPosX, centerPosY);

		//get the entries to display with filters
		keyStoreTable = new KeyStoreTable(allkeys, filters);


		// Initialize certificate keystore file label

		if(keyStoreTable.getNumberEntries()>0)
			mChooseCertFileLabel.setText(MessageConstants.getmessage("selectkey.select"));
		else
			mChooseCertFileLabel.setText(MessageConstants.getmessage("selectkey.noselect"));	


		mChooseCertFileLabel.setBounds(new Rectangle(10, 5, 500, 15));
		mChooseCertFileLabel.setFont(new Font("Dialog", 0, 12));


		// Add the initialized components into the dialog's content pane
		this.getContentPane().add(mChooseCertFileLabel, null);

		keyStoreTable.setVisible(true);
		JScrollPane scroll =  new JScrollPane(keyStoreTable);
		scroll.setVisible(true);

		scroll.setAlignmentX(0.0f);
		scroll.setBounds(new Rectangle(10, 50, dialogSize.width-50, dialogSize.height -150));
		this.getContentPane().add(scroll);	

		// Initialize sign button
		mSignButton.setText(MessageConstants.getmessage("selectkey.submit"));
		mSignButton.setBounds(new Rectangle((dialogSize.width/2) - 150,dialogSize.height-100,100,25));
		mSignButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signButton_actionPerformed();
			}
		});

		// Initialize cancel button
		mCancelButton.setText(MessageConstants.getmessage("selectkey.cancel"));
		mCancelButton.setBounds(new Rectangle((dialogSize.width/2) + 50,dialogSize.height-100,100,25));

		mCancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed();
			}
		});




		this.getContentPane().add(mSignButton, null);
		this.getContentPane().add(mCancelButton, null);
		this.getRootPane().setDefaultButton(mSignButton);


	}


	/**
	 * Called when the sign button is pressed. Closses the dialog and sets the
	 * result flag to true to indicate that the user is confirmed the
	 * information entered in the dialog.
	 */
	private void signButton_actionPerformed() {
		mResult = true;
		setVisible(false);
	}

	/**
	 * Called when the cancel button is pressed. Closses the dialog and sets the
	 * result flag to false that indicates that the dialog is canceled.
	 */
	private void cancelButton_actionPerformed() {
		mResult = false;
		setVisible(false);
	}



	/**
	 * Shows the dialog and allows the user to choose a PFX file and enter a
	 * password.
	 * 
	 * @return true if the user click sign button or false if the user cancel
	 *         the dialog.
	 */
	public boolean run() {

		//setModal(true);
		setVisible(true);

		return mResult;
	}



	/**
	 * to test the swing component
	 * @param args
	 * @throws KeyStoreException
	 * @throws KeystoreAccessException 
	 * @throws KeyStoreException 
	 */
	public static void main(String[] args) throws KeystoreAccessException, KeyStoreException {

		//change locale for the user
		//MessageConstants.reloadBundleWithLocale(new Locale("fr"));
		MessageConstants.reloadBundleWithLocale(new Locale("en"));

		List<KeyStore> ksl = KeyStoreUtils.getMSKeyStore();

		KeystoreFilters filters =  new KeystoreFilters();

		filters.add(new KeyUsageSignatureFilter(true));
		//filters.add(new IssuerDnCertificateFilter("AdminCA1"));
		filters.setEnabled(true);

		for(KeyStore ks : ksl){
			WizardPanelSelectKey we = new WizardPanelSelectKey(ks,filters);
			System.out.println(we.run());
			System.out.println(we.getKeyStoreTable().getSelectedEntry());
		}

	}


	public KeyStoreTable getKeyStoreTable() {
		return keyStoreTable;
	}

}
