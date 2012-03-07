/*
Copyright (c) 2012, University of Texas at El Paso
All rights reserved.
Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:
- Redistributions of source code must retain the above copyright notice, this 
  list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */
package edu.utep.cybershare.wdoit.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.ActionMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JList;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import edu.utep.cybershare.wdoapi.util.Namespace;
import edu.utep.cybershare.wdoapi.util.Namespace.NS_FORMAT;
import edu.utep.cybershare.wdoapi.util.Namespace.NS_PROTOCOLS;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.ui.components.NamespaceTextField;

/**
 * Dialog box to edit a namespace
 * 
 * @author Leonardo Salayandia
 */
public class EditNamespace extends JDialog {

	private static final long serialVersionUID = 1L;

	private enum WARNING_MSGS {
		OK, INVALID_NS_SYNTAX, EXISTING_NS
	};

	private javax.swing.JButton cancelButton;
	private NamespaceTextField nsBody;
	private javax.swing.JComboBox nsProtocol;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel warningLabel;

	private String ns;

	/** Creates new form EditWDOConcept */
	public EditNamespace(Frame parent, String windowTitle) {
		super(parent);
		this.addWindowListener(new ActivatedWindowListener());
		this.initComponents(windowTitle);
		this.getRootPane().setDefaultButton(okButton);
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	/**
	 * Get the namespace that was set through the GUI
	 * 
	 * @return
	 */
	public String getNamespace() {
		return ns;
	}

	/**
	 * Set the warning message in this window
	 * 
	 * @param text
	 */
	private void setWarningMessage(WARNING_MSGS msg) {
		if (msg == WARNING_MSGS.OK) {
			warningLabel.setText(" ");
			return;
		}
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditNamespace.class);
		if (msg == WARNING_MSGS.INVALID_NS_SYNTAX) {
			warningLabel.setText(resourceMap
					.getString("warningLabel.text.invalidSyntax"));
		} else if (msg == WARNING_MSGS.EXISTING_NS) {
			warningLabel.setText(resourceMap
					.getString("warningLabel.text.nsTaken"));
		}
	}

	@Action
	public void ok() {
		String temp = nsBody.getText();

		// check if the namespace is already a valid namespace
		if (!Namespace.isValid(temp)) {
			// if not, construct the namespace from protocol and body specified
			temp = ((String) nsProtocol.getSelectedItem())
					+ Namespace.PROTOCOL_BODY_SEPARATOR + temp;
			// check again
			if (!Namespace.isValid(temp)) {
				setWarningMessage(WARNING_MSGS.INVALID_NS_SYNTAX);
				return;
			}
		}

		// check that the namespace is not already taken
		State state = State.getInstance();
		if (!state.isWorkspaceEmpty()) {
			for (Iterator<String> iter = state.listOntologyURIs(); iter
					.hasNext();) {
				if (iter.next().equalsIgnoreCase(temp)) {
					setWarningMessage(WARNING_MSGS.EXISTING_NS);
					return;
				}
			}
			for (Iterator<String> iter = state.listWorkflowURIs(); iter
					.hasNext();) {
				if (iter.next().equalsIgnoreCase(temp)) {
					setWarningMessage(WARNING_MSGS.EXISTING_NS);
					return;
				}
			}
		}

		// if good, set the ns variable
		this.ns = temp;

		// update GUI
		setVisible(false);
	}

	@Action
	public void cancel() {
		setVisible(false);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents(String windowTitle) {

		titleLabel = new javax.swing.JLabel();
		nsBody = new NamespaceTextField(NS_FORMAT.BODY_ONLY);
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		warningLabel = new javax.swing.JLabel();
		nsProtocol = new javax.swing.JComboBox();

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditNamespace.class);
		if (windowTitle == null || windowTitle.isEmpty()) {
			setTitle(resourceMap.getString("window.title"));
		} else {
			setTitle(windowTitle);
		}
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("EditNamespace"); // NOI18N
		setModal(true);
		setMinimumSize(new Dimension(400, 175));

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(EditNamespace.class, this);
		okButton.setAction(actionMap.get("ok"));
		okButton.setName("okButton"); // NOI18N

		cancelButton.setAction(actionMap.get("cancel"));
		cancelButton.setName("cancelButton"); // NOI18N

		titleLabel.setText(resourceMap.getString("titleLabel.text")); // NOI18N
		titleLabel.setName("titleLabel"); // NOI18N

		nsBody.setText(resourceMap.getString("nsBody.text")); // NOI18N
		nsBody.setName("nsBody"); // NOI18N
		nsBody.addPropertyChangeListener(new nsBodyPropertyChangeListener());

		warningLabel.setForeground(resourceMap
				.getColor("warningLabel.foreground")); // NOI18N
		warningLabel.setName("warningLabel");
		setWarningMessage(WARNING_MSGS.OK);

		NS_PROTOCOLS[] protocols = NS_PROTOCOLS.values();
		String[] options = new String[protocols.length];
		for (int i = 0; i < protocols.length; i++) {
			options[i] = protocols[i].toString();
		}
		nsProtocol.setModel(new javax.swing.DefaultComboBoxModel(options));
		nsProtocol.setRenderer(new nsProtocolListCellRenderer());
		nsProtocol.setName("nsProtocol"); // NOI18N

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														warningLabel,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														380, Short.MAX_VALUE)
												.addComponent(
														titleLabel,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														380, Short.MAX_VALUE)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		okButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		65,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		cancelButton))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		nsProtocol,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		68,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		nsBody,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		306,
																		Short.MAX_VALUE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(titleLabel)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														nsBody,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														nsProtocol,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(warningLabel, 14, 14,
										Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(cancelButton)
												.addComponent(okButton))
								.addContainerGap()));

		pack();
	}

	/**
	 * Listener class that sets the focus to the label text box when the window
	 * becomes active
	 * 
	 * @author Leonardo Salayandia
	 */
	private class ActivatedWindowListener implements WindowListener {
		@Override
		public void windowActivated(WindowEvent e) {
			EditNamespace window = (EditNamespace) e.getComponent();
			window.nsBody.requestFocus(); // set the nsBody text box as the
											// initial focus component
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}
	}

	/**
	 * Cell renderer for nsProtocol List
	 * 
	 * @author Leonardo Salayandia
	 */
	private class nsProtocolListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			String protocol = (String) value;
			setText(protocol.toLowerCase() + Namespace.PROTOCOL_BODY_SEPARATOR);
			return this;
		}
	}

	/**
	 * Listener to detect if the nsBody text field is showing an invalid
	 * namespace
	 * 
	 * @author Leonardo Salayandia
	 * 
	 */
	private class nsBodyPropertyChangeListener implements
			PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("foreground")) {
				Color color = (Color) evt.getNewValue();
				NamespaceTextField nsTextField = (NamespaceTextField) evt
						.getSource();
				EditNamespace window = (EditNamespace) nsTextField.getParent()
						.getParent().getParent().getParent();
				if (color == Color.RED) {
					String ns = nsTextField.getText();
					if (ns != null && !ns.isEmpty()) {
						window.setWarningMessage(WARNING_MSGS.INVALID_NS_SYNTAX);
					}
				} else {
					window.setWarningMessage(WARNING_MSGS.OK);
				}
			}
		}
	}
}
