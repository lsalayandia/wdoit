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

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ActionMap;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoapi.util.Name;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.ui.components.NameTextField;

/**
 * Dialog box to edit a WDO Concept.
 * 
 * @author Leonardo Salayandia
 */
public class EditWDOConcept extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	private javax.swing.JButton cancelButton;
	private javax.swing.JScrollPane commentJScrollPane;
	private javax.swing.JTextArea commentJTextArea;
	private javax.swing.JLabel commentLabel;
	private javax.swing.JLabel nameJLabel;
	private NameTextField nameJTextField;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel uriJLabel;
	private javax.swing.JTextField uriJTextField;

	private OntClass selectedClass; // class that is being edited, or parent of
									// new class being added
	private OntClass newClass; // the new class that resulted from the add
								// operation
	private boolean requiredFieldsSet; // property to indicate that the required
										// fields have valid values set
	private boolean newWDOConcept; // property to indicate that the concept
									// being edited is a new concept

	/** Creates new form EditWDOConcept */
	public EditWDOConcept(Frame parent) {
		super(parent);

		// initialize properties
		this.selectedClass = null;
		this.newClass = null;
		this.requiredFieldsSet = false;
		this.newWDOConcept = false;

		this.addWindowListener(new ActivatedWindowListener());

		initComponents();
		getRootPane().setDefaultButton(okButton);
	}

	/**
	 * Set the text fields of this form, triggers the required fields set
	 * property
	 * 
	 * @param uri
	 * @param label
	 * @param comment
	 */
	private void setTextFields(String uri, String label, String comment) {
		this.uriJTextField.setText(uri);
		this.nameJTextField.setText(label);
		this.commentJTextArea.setText(comment);
		setRequiredFieldsSet();
	}

	/**
	 * Initialize window to add a new WDO concept, or to edit an existing
	 * concept. If newConcept = true, selectedCls will become the parent of the
	 * new class. If newConcept = false, the selectedCls is the class being
	 * edited.
	 * 
	 * @param newConcept
	 * @param selectedCls
	 */
	public void initWindow(boolean newConcept, OntClass selectedCls) {
		boolean oldval = newWDOConcept;
		newWDOConcept = newConcept;
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditWDOConcept.class);
		if (newWDOConcept) {
			setTitle(resourceMap.getString("add.title"));
		} else {
			setTitle(resourceMap.getString("edit.title"));
		}
		this.firePropertyChange("newWDOConcept", oldval, newWDOConcept);

		selectedClass = selectedCls;
		newClass = null;
		if (isNewWDOConcept() || selectedClass == null) {
			setTextFields("", "", "");
		} else {
			setTextFields(selectedClass.getURI(),
					WDO.getClassLabel(selectedClass),
					WDO.getClassComment(selectedClass));
		}
	}

	/**
	 * Check the status of the newWDOConcept property
	 * 
	 * @return
	 */
	public boolean isNewWDOConcept() {
		return newWDOConcept;
	}

	/**
	 * Get the class that is being edited by this form
	 * 
	 * @return
	 */
	public OntClass getSelectedClass() {
		return selectedClass;
	}

	/**
	 * Get the new class that resulted from the Add concept operation.
	 * 
	 * @return New class or null if add operation failed.
	 */
	public OntClass getNewClass() {
		return newClass;
	}

	/**
	 * Check whether all the required fields of this form are set
	 * 
	 * @return
	 */
	public boolean isRequiredFieldsSet() {
		return requiredFieldsSet;
	}

	/**
	 * Set the corresponding property that reflects whether the required fields
	 * of this form are set.
	 */
	private void setRequiredFieldsSet() {
		boolean oldval = requiredFieldsSet;
		String name = this.nameJTextField.getText();
		requiredFieldsSet = (name != null && !name.isEmpty() && Name
				.isValid(name));
		if (requiredFieldsSet != oldval) {
			this.firePropertyChange("requiredFieldsSet", oldval,
					requiredFieldsSet);
		}
	}

	@Action(enabledProperty = "requiredFieldsSet")
	public void ok() {
		if (selectedClass != null) {
			State state = State.getInstance();
			if (isNewWDOConcept()) {
				newClass = null;
				if (state.isDataSubClass(selectedClass)) {
					newClass = state.createDataSubClass(selectedClass,
							this.nameJTextField.getText());
				} else if (state.isMethodSubClass(selectedClass)) {
					newClass = state.createMethodSubClass(selectedClass,
							this.nameJTextField.getText());
				}
				if (newClass != null) {
					state.setClassComment(newClass,
							this.commentJTextArea.getText());
				}
			} else {
				String comment = this.commentJTextArea.getText();
				if (comment == null)
					comment = "";
				state.setClassComment(selectedClass, comment);
				String name = this.nameJTextField.getText();
				if (name == null)
					name = "";
				state.setClassLabel(selectedClass, name);
				newClass = null;
			}
		}
		setVisible(false);
	}

	@Action
	public void cancel() {
		newClass = null;
		setVisible(false);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		commentJScrollPane = new javax.swing.JScrollPane();
		commentJTextArea = new javax.swing.JTextArea();
		commentLabel = new javax.swing.JLabel();
		nameJTextField = new NameTextField();
		nameJLabel = new javax.swing.JLabel();
		uriJTextField = new javax.swing.JTextField();
		uriJLabel = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditWDOConcept.class);
		setTitle(resourceMap.getString("edit.title"));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("EditWDOConcept"); // NOI18N
		setModal(true);

		commentJScrollPane.setName("commentJScrollPane"); // NOI18N

		commentJTextArea.setColumns(20);
		commentJTextArea.setRows(5);
		commentJTextArea.setName("commentJTextArea"); // NOI18N
		commentJTextArea.setLineWrap(true);
		commentJTextArea.setWrapStyleWord(true);
		commentJScrollPane.setViewportView(commentJTextArea);

		commentLabel.setText(resourceMap.getString("commentLabel.text")); // NOI18N
		commentLabel.setName("commentLabel"); // NOI18N

		nameJTextField.setText(resourceMap.getString("nameJTextField.text")); // NOI18N
		nameJTextField.setName("nameJTextField"); // NOI18N
		nameJTextField.addKeyListener(new RequiredTextFieldListener());

		nameJLabel.setText(resourceMap.getString("nameJLabel.text")); // NOI18N
		nameJLabel.setName("nameJLabel"); // NOI18N

		uriJTextField.setEditable(false);
		uriJTextField.setText(resourceMap.getString("uriJTextField.text")); // NOI18N
		uriJTextField.setName("uriJTextField"); // NOI18N

		uriJLabel.setText(resourceMap.getString("uriJLabel.text")); // NOI18N
		uriJLabel.setName("uriJLabel"); // NOI18N

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(EditWDOConcept.class, this);
		okButton.setAction(actionMap.get("ok"));
		okButton.setName("okButton"); // NOI18N

		cancelButton.setAction(actionMap.get("cancel"));
		cancelButton.setName("cancelButton"); // NOI18N

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														commentJScrollPane,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														374, Short.MAX_VALUE)
												.addComponent(commentLabel)
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING)
																				.addGroup(
																						javax.swing.GroupLayout.Alignment.LEADING,
																						layout.createSequentialGroup()
																								.addComponent(
																										uriJLabel)
																								.addGap(11,
																										11,
																										11)
																								.addComponent(
																										uriJTextField,
																										javax.swing.GroupLayout.DEFAULT_SIZE,
																										258,
																										Short.MAX_VALUE))
																				.addGroup(
																						javax.swing.GroupLayout.Alignment.LEADING,
																						layout.createSequentialGroup()
																								.addComponent(
																										nameJLabel)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addComponent(
																										nameJTextField,
																										javax.swing.GroupLayout.DEFAULT_SIZE,
																										258,
																										Short.MAX_VALUE)))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		18,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING,
																				false)
																				.addComponent(
																						okButton,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						cancelButton,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE))))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														uriJTextField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(uriJLabel)
												.addComponent(
														okButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														23,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														nameJTextField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(nameJLabel)
												.addComponent(
														cancelButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														23,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(commentLabel)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(commentJScrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										157, Short.MAX_VALUE).addContainerGap()));

		pack();
	}

	/**
	 * Listener class that triggers the set required fields property when a text
	 * field changes
	 * 
	 * @author Leonardo Salayandia
	 */
	private class RequiredTextFieldListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			setRequiredFieldsSet();
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}

	/**
	 * Listener class that sets the focus to the name text box when the
	 * EditWDOConcept window becomes active
	 * 
	 * @author Leonardo Salayandia
	 */
	private class ActivatedWindowListener implements WindowListener {
		@Override
		public void windowActivated(WindowEvent e) {
			nameJTextField.requestFocus(); // set the label text box as the
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
}
