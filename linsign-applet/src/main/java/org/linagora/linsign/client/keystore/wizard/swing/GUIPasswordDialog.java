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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.linagora.linsign.client.applet.MessageConstants;


public class GUIPasswordDialog extends JDialog implements ActionListener{
    
    private JPasswordField passField;
    private JButton okButton;
    private JButton cancelButton;
    public boolean chooseOK;
    private String message;
    private String description;
    private Frame parentFrame;
    private JLabel messageLabel; 
    
    public char[] getPassword(){
        return passField.getPassword();
    }
    
    public GUIPasswordDialog(String description){
    	this(description,null,null,true);
    }
    
    public GUIPasswordDialog(String description,String message){
    	this(description,message,null,true);
    }
    
    public GUIPasswordDialog(String description,String message,JFrame parentFrame,boolean modal){
        super(parentFrame, "Enter password", modal);
        this.parentFrame = parentFrame;
        this.message = message;
        this.description = description;
        
		this.setBackground(SystemColor.control);
		//this.setResizable(false);
             
        Box box = Box.createVerticalBox();
        box.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Dialog", 1, 12));
        descriptionLabel.setBackground(this.getBackground());
        descriptionLabel.setPreferredSize(new Dimension(300,15));
        
        if(message!=null){
	        messageLabel = new JLabel(message);
	        messageLabel.setFont(new Font("Dialog", 0, 12));
	        messageLabel.setBackground(this.getBackground());
	        messageLabel.setForeground(Color.RED);
	        messageLabel.setPreferredSize(new Dimension(300,15));
        }
        
        passField = new JPasswordField(10);
        passField.setEchoChar('*');
        passField.setActionCommand("OK");
        passField.addActionListener(this);
        passField.setAlignmentX(0.5f);
        passField.addKeyListener(this.keyListener);
        
        
        JPanel buttons = new JPanel();
        
        okButton = new JButton(MessageConstants.getmessage("GUIPasswordDialog.OK"));
        okButton.setActionCommand("OK");
        okButton.addActionListener(this);
        okButton.addKeyListener(this.keyListener);
        
        cancelButton = new JButton(MessageConstants.getmessage("GUIPasswordDialog.Cancel"));
        cancelButton.setActionCommand("CANCEL");
        cancelButton.addActionListener(this);
        cancelButton.addKeyListener(this.keyListener);
        
        buttons.add(okButton);
        buttons.add(cancelButton);
        
        
        // Layout
        box.add(Box.createVerticalStrut(3));
        if(message!=null){
	        box.add(messageLabel);
	        box.add(Box.createVerticalStrut(8));
        }
        box.add(descriptionLabel);
        box.add(Box.createVerticalStrut(5));
        box.add(passField);
        box.add(Box.createVerticalStrut(5));
        box.add(buttons);
        
        //this.setDefaultCloseOperation(this.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                ;
            }
        });
        
        this.addKeyListener(keyListener);
        
        this.getContentPane().add(box);
        this.pack();
        this.setLocationRelativeTo(this.parentFrame);
    }
    
    KeyListener keyListener = (new KeyAdapter() {
        public void keyPressed(KeyEvent e){
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE || (e.getSource() == cancelButton && e.getKeyCode() == KeyEvent.VK_ENTER)){
            	//cancel focus + enter
            	chooseOK = false;
                setVisible(false);
            }
            if (e.getSource() == okButton && e.getKeyCode() == KeyEvent.VK_ENTER){
            	//ok focus + enter
                chooseOK = true;
                setVisible(false);
            }
            if (e.getKeyCode() == KeyEvent.VK_ENTER&&passField.getPassword().length>0){
            	//press enter in field
                chooseOK = true;
                setVisible(false);
            }
            
        }
    });
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource().equals(okButton)) { //Process the password.
            chooseOK = true;
            this.setVisible(false);
        } else if (e.getSource().equals(cancelButton)){
        	chooseOK = false;
        	this.setVisible(false);
        }
    }
    
    
	public boolean run() {
		//setModal(true);
		this.setVisible(true);
		return chooseOK;
	}
    
    
    public static void main(String[] args) {
		GUIPasswordDialog g = new GUIPasswordDialog("Enter password");
		System.out.println(g.run());
		System.out.println(g.getPassword());
		System.exit(0);
	}

}
 