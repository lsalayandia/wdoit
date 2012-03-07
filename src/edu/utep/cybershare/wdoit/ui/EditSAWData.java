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
import javax.swing.JDialog;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoapi.util.Name;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.ui.components.NameTextField;

/**
 * GUI to edit properties of Data instances included in a SAW
 * 
 * @author Leonardo Salayandia
 */
public class EditSAWData extends JDialog {

	private static final long serialVersionUID = 1L;
	private javax.swing.JButton cancelButton;
	private javax.swing.JScrollPane commentJScrollPane;
	private javax.swing.JTextArea commentJTextArea;
	private javax.swing.JLabel commentLabel;
	private javax.swing.JLabel formatJLabel;
	private javax.swing.JTextField formatJTextField;
	private javax.swing.JLabel nameJLabel;
	private NameTextField nameJTextField;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel typeJLabel;
	private javax.swing.JTextField typeJTextField;

	private OntClass selectedDataInd;
	private boolean requiredFieldsSet; // property to indicate that the required
										// fields have valid values set

	/** Creates new form EditWDOConcept */
	public EditSAWData(Frame parent) {
		super(parent);

		// initialize properties
		this.requiredFieldsSet = false;

		this.addWindowListener(new ActivatedWindowListener());

		initComponents();
		getRootPane().setDefaultButton(okButton);
	}

	/**
	 * Initialize window to edit a SAW instance.
	 * 
	 * @param selectedInd
	 */
	public void initWindow(OntClass selectedInd) {
		selectedDataInd = selectedInd;

		typeJTextField.setText(WDO.getClassQName(SAW
				.getSAWInstanceType(selectedDataInd)));
		nameJTextField.setText(SAW.getSAWInstanceLabel(selectedDataInd));
		commentJTextArea.setText(SAW.getSAWInstanceComment(selectedDataInd));

		Individual formatInd = SAW.getFormat(selectedDataInd);
		formatJTextField.setText((formatInd == null) ? null : formatInd
				.getURI());

		setRequiredFieldsSet();
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
		requiredFieldsSet = (Name.isValid(name));
		if (requiredFieldsSet != oldval) {
			this.firePropertyChange("requiredFieldsSet", oldval,
					requiredFieldsSet);
		}
	}

	@Action(enabledProperty = "requiredFieldsSet")
	public void ok() {
		State state = State.getInstance();
		// update label
		state.setIndividualLabel(selectedDataInd, nameJTextField.getText());
		// update comment
		state.setIndividualComment(selectedDataInd, commentJTextArea.getText());
		// update format
		String formatURI = formatJTextField.getText();
		if (formatURI == null || formatURI.isEmpty()) {
			state.setFormat(selectedDataInd, null);
		} else {
			Individual formatInd = state.getSelectedWorkflow().getIndividual(
					formatURI);
			if (formatInd == null) {
				formatInd = state.createFormatIndividual(formatURI);
				// TODO Need to device a way to discard Format instances that
				// are no longer in use
			}
			state.setFormat(selectedDataInd, formatInd);
		}

		// update GUI
		setVisible(false);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditSAWData.class);
		wdoView.setMessage(resourceMap.getString("ok.Action.msg"));
	}

	@Action
	public void cancel() {
		setVisible(false);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditSAWData.class);
		wdoView.setMessage(resourceMap.getString("cancel.Action.msg"));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		nameJTextField = new NameTextField();
		commentJScrollPane = new javax.swing.JScrollPane();
		commentJTextArea = new javax.swing.JTextArea();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		commentLabel = new javax.swing.JLabel();
		nameJLabel = new javax.swing.JLabel();
		typeJLabel = new javax.swing.JLabel();
		formatJLabel = new javax.swing.JLabel();
		typeJTextField = new javax.swing.JTextField();
		formatJTextField = new javax.swing.JTextField();

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditSAWData.class);

		setTitle(resourceMap.getString("window.title"));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("EditSAWData"); // NOI18N
		setModal(true);

		nameJTextField.setText(resourceMap.getString("nameJTextField.text")); // NOI18N
		nameJTextField.setName("nameJTextField"); // NOI18N
		nameJTextField.addKeyListener(new RequiredTextFieldListener());

		commentJScrollPane.setName("commentJScrollPane"); // NOI18N

		commentJTextArea.setColumns(20);
		commentJTextArea.setRows(5);
		commentJTextArea.setLineWrap(true);
		commentJTextArea.setWrapStyleWord(true);
		commentJTextArea.setName("commentJTextArea"); // NOI18N
		commentJScrollPane.setViewportView(commentJTextArea);

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(EditSAWData.class, this);
		okButton.setAction(actionMap.get("ok"));
		okButton.setName("okButton"); // NOI18N

		cancelButton.setAction(actionMap.get("cancel"));
		cancelButton.setName("cancelButton"); // NOI18N

		commentLabel.setText(resourceMap.getString("commentLabel.text")); // NOI18N
		commentLabel.setName("commentLabel"); // NOI18N

		nameJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		nameJLabel.setText(resourceMap.getString("nameJLabel.text")); // NOI18N
		nameJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		nameJLabel.setName("nameJLabel"); // NOI18N

		typeJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		typeJLabel.setText(resourceMap.getString("typeJLabel.text")); // NOI18N
		typeJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		typeJLabel.setName("typeJLabel"); // NOI18N

		formatJLabel.setText(resourceMap.getString("formatJLabel.text")); // NOI18N
		formatJLabel.setName("formatJLabel"); // NOI18N

		typeJTextField.setEditable(false);
		typeJTextField.setName("typeJTextField"); // NOI18N

		formatJTextField.setName("formatJTextField"); // NOI18N

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
														343, Short.MAX_VALUE)
												.addGroup(
														javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addGap(4, 4, 4)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						formatJLabel)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addGap(9,
																										9,
																										9)
																								.addGroup(
																										layout.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.TRAILING)
																												.addComponent(
																														typeJLabel)
																												.addComponent(
																														nameJLabel))
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addGroup(
																										layout.createParallelGroup(
																												javax.swing.GroupLayout.Alignment.TRAILING)
																												.addComponent(
																														nameJTextField,
																														javax.swing.GroupLayout.DEFAULT_SIZE,
																														226,
																														Short.MAX_VALUE)
																												.addComponent(
																														typeJTextField,
																														javax.swing.GroupLayout.DEFAULT_SIZE,
																														226,
																														Short.MAX_VALUE))
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
																														Short.MAX_VALUE)))
																				.addComponent(
																						formatJTextField,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						297,
																						Short.MAX_VALUE)))
												.addComponent(
														commentLabel,
														javax.swing.GroupLayout.Alignment.LEADING))
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
														typeJTextField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(okButton)
												.addComponent(typeJLabel))
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
												.addComponent(
														cancelButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														23,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(nameJLabel))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														formatJTextField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(formatJLabel))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(commentLabel)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(commentJScrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										164, Short.MAX_VALUE).addContainerGap()));

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
	 * Listener class that sets the focus to the name text box when the window
	 * becomes active
	 * 
	 * @author Leonardo Salayandia
	 */
	private class ActivatedWindowListener implements WindowListener {
		@Override
		public void windowActivated(WindowEvent e) {
			nameJTextField.requestFocus(); // set the name text box as the
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
