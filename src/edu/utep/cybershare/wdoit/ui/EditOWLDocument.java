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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ActionMap;
import javax.swing.JDialog;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.hp.hpl.jena.ontology.OntModel;

import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;

/**
 * GUI to edit comments for OWL documents. Should be able to edit comments on
 * WDOs and SAWs only. Comments for other types of OWL documents can be seen but
 * not edited.
 * 
 * @author Leonardo Salayandia
 */
public class EditOWLDocument extends JDialog {

	private static final long serialVersionUID = 1L;
	private javax.swing.JButton cancelButton;
	private javax.swing.JScrollPane commentJScrollPane;
	private javax.swing.JTextArea commentJTextArea;
	private javax.swing.JLabel commentLabel;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel uriJLabel;
	private javax.swing.JTextField uriJTextField;

	private boolean editableOWLDocument;

	/** Creates new form EditWDOConcept */
	public EditOWLDocument(Frame parent) {
		super(parent);

		// initialize properties
		editableOWLDocument = false;
		this.addWindowListener(new ActivatedWindowListener());

		initComponents();
		getRootPane().setDefaultButton(okButton);
	}

	public boolean getEditableOWLDocument() {
		return editableOWLDocument;
	}

	private void setEditableOWLDocument(boolean b) {
		boolean oldval = editableOWLDocument;
		editableOWLDocument = b;
		if (oldval != b) {
			this.firePropertyChange("editableOWLDocument", oldval, b);
		}
	}

	/**
	 * Initialize the window to edit the ontmodel marked as selected in State
	 * 
	 * @param
	 */
	public void initWindow() {
		State state = State.getInstance();
		OntModel temp = state.getSelectedOWLDocument();
		if (temp != null) {
			String uri = state.getOntModelURI(temp);
			uriJTextField.setText(uri);
			commentJTextArea.setText(state.getOWLDocumentComment(temp));
			this.setEditableOWLDocument(state.getBaseWDOURI().equals(uri)
					|| state.isWorkflow(uri));
		}
	}

	@Action(enabledProperty = "editableOWLDocument")
	public void ok() {
		State state = State.getInstance();
		state.setSelectedOWLDocumentComment(commentJTextArea.getText());
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditOWLDocument.class);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		wdoView.setMessage(resourceMap.getString("ok.Action.msg"));
		wdoView.updateOWLDocHierarchy();
		this.setVisible(false);
	}

	@Action
	public void cancel() {
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditOWLDocument.class);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		wdoView.setMessage(resourceMap.getString("cancel.Action.msg"));
		this.setVisible(false);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		commentJScrollPane = new javax.swing.JScrollPane();
		commentJTextArea = new javax.swing.JTextArea();
		commentLabel = new javax.swing.JLabel();
		uriJTextField = new javax.swing.JTextField();
		uriJLabel = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditOWLDocument.class);
		setTitle(resourceMap.getString("edit.title"));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("EditOWLDocument"); // NOI18N
		setModal(true);

		commentJScrollPane.setName("commentJScrollPane"); // NOI18N

		commentJTextArea.setColumns(20);
		commentJTextArea.setRows(5);
		commentJTextArea.setLineWrap(true);
		commentJTextArea.setWrapStyleWord(true);
		commentJTextArea.setName("commentJTextArea"); // NOI18N
		commentJScrollPane.setViewportView(commentJTextArea);

		commentLabel.setText(resourceMap.getString("commentLabel.text")); // NOI18N
		commentLabel.setName("commentLabel"); // NOI18N

		uriJTextField.setText(resourceMap.getString("uriJTextField.text")); // NOI18N
		uriJTextField.setEnabled(false);
		uriJTextField.setName("uriJTextField"); // NOI18N

		uriJLabel.setText(resourceMap.getString("uriJLabel.text")); // NOI18N
		uriJLabel.setName("uriJLabel"); // NOI18N

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(EditOWLDocument.class, this);
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
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(
														commentJScrollPane,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														374, Short.MAX_VALUE)
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(
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
																										Short.MAX_VALUE)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																										18,
																										javax.swing.GroupLayout.PREFERRED_SIZE))
																				.addGroup(
																						layout.createSequentialGroup()
																								.addComponent(
																										commentLabel)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
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
												.addComponent(okButton))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														cancelButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														23,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(commentLabel))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(commentJScrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										120, Short.MAX_VALUE).addContainerGap()));

		pack();
	}

	/**
	 * Listener class that sets the focus to the label text box when the
	 * EditWDOConcept window becomes active
	 * 
	 * @author Leonardo Salayandia
	 */
	private class ActivatedWindowListener implements WindowListener {
		@Override
		public void windowActivated(WindowEvent e) {
			EditOWLDocument window = (EditOWLDocument) e.getComponent();
			window.commentJTextArea.requestFocus(); // set the label text box as
													// the initial focus
													// component
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
