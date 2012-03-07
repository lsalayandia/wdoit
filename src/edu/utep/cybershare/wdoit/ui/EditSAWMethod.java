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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ActionMap;
import javax.swing.JDialog;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Ontology;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoapi.util.Name;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.ui.components.MethodSourceComboBox;
import edu.utep.cybershare.wdoit.ui.components.NameTextField;

/**
 * GUI to edit properties of Method instances included in a SAW
 * 
 * @author Leonardo Salayandia
 */
public class EditSAWMethod extends JDialog {

	private static final long serialVersionUID = 1L;
	private String DETAILED_BY_NONE_OPTION;

	private javax.swing.JButton cancelButton;
	private javax.swing.JScrollPane commentJScrollPane;
	private javax.swing.JTextArea commentJTextArea;
	private javax.swing.JLabel commentLabel;
	private javax.swing.JComboBox detailedByJComboBox;
	private javax.swing.JLabel detailedByJLabel;
	private javax.swing.JLabel infEngineJLabel;
	private javax.swing.JTextField infEngineJTextField;
	private javax.swing.JLabel nameJLabel;
	private NameTextField nameJTextField;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel typeJLabel;
	private javax.swing.JTextField typeJTextField;
	private javax.swing.JLabel sourceJLabel;
	private MethodSourceComboBox sourceComboBox;

	private OntClass selectedMethodInd; // SAW instance selected for edit

	private boolean requiredFieldsSet; // property to indicate that the required
										// fields have valid values set

	/** Creates new form EditSAWMethod */
	public EditSAWMethod(Frame parent) {
		super(parent);

		// initialize properties
		requiredFieldsSet = false;

		this.addWindowListener(new ActivatedWindowListener());

		initComponents();
		getRootPane().setDefaultButton(okButton);
	}

	/**
	 * Initialize window to edit a SAW instance.
	 * 
	 * @param selectedMethodInd
	 */
	public void initWindow(OntClass selectedInd) {
		selectedMethodInd = selectedInd;
		typeJTextField.setText(WDO.getClassQName(SAW
				.getSAWInstanceType(selectedMethodInd)));
		nameJTextField.setText(SAW.getSAWInstanceLabel(selectedMethodInd));
		commentJTextArea.setText(SAW.getSAWInstanceComment(selectedMethodInd));

		Individual ie = SAW.getInferenceEngine(selectedMethodInd);
		infEngineJTextField.setText((ie == null) ? null : ie.getURI());

		Individual src = SAW.getPMLSource(selectedMethodInd);
		sourceComboBox.setSelectedSource(src);

		// set list of options for detailedby combo box:
		// DETAILED_BY_NONE_OPTION + SAWs currently loaded in the workspace,
		// except currently selected SAW
		// detailedBy references to self are not expected.
		ArrayList<String> optionsAL = new ArrayList<String>();
		optionsAL.add(DETAILED_BY_NONE_OPTION);
		State state = State.getInstance();
		String sawURI = state.getSelectedOWLDocumentURI();
		for (Iterator<String> iter = state.listWorkflowURIs(); iter.hasNext();) {
			String tempURI = iter.next();
			if (!sawURI.equals(tempURI)) {
				optionsAL.add(tempURI);
			}
		}
		Object[] options = new String[optionsAL.size()];
		optionsAL.toArray(options);
		detailedByJComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				options));

		// set value selected on detailed by combo box
		Ontology sawOnt = SAW.getDetailedBy(selectedMethodInd);
		detailedByJComboBox
				.setSelectedItem((sawOnt == null) ? DETAILED_BY_NONE_OPTION
						: sawOnt.getURI());

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
		state.setIndividualLabel(selectedMethodInd, nameJTextField.getText());
		// update comment
		state.setIndividualComment(selectedMethodInd,
				commentJTextArea.getText());
		// update inference engine
		String ieURI = this.infEngineJTextField.getText();
		if (ieURI == null || ieURI.isEmpty()) {
			state.setInferenceEngine(selectedMethodInd, null);
		} else {
			Individual ieInd = state.getSelectedWorkflow().getIndividual(ieURI);
			if (ieInd == null) {
				ieInd = state.createInferenceEngineIndividual(ieURI);
				// TODO Need to device a way to discard InferenceEngine
				// instances that are no longer in use
			}
			state.setInferenceEngine(selectedMethodInd, ieInd);
		}
		// update detailed by
		String detailedBy = (String) detailedByJComboBox.getSelectedItem();
		if (detailedBy.equals(DETAILED_BY_NONE_OPTION)) {
			state.removeDetailedBy(selectedMethodInd);
		} else {
			state.setDetailedBy(selectedMethodInd,
					SAW.getSAWSAWInstance(state.getOntModel(detailedBy)));
		}
		// update Source
		String srcURI = this.sourceComboBox.getSelectedSourceURI();
		if (srcURI == null || srcURI.isEmpty()) {
			state.setPMLSource(selectedMethodInd, null);
		} else {
			Individual srcInd = state.getSelectedWorkflow().getIndividual(
					srcURI);
			if (srcInd == null) {
				srcInd = state.createPMLSourceInstance(srcURI);
				// TODO need to device a way to discard unused instances
			}
			state.setPMLSource(selectedMethodInd, srcInd);
		}

		// update GUI
		setVisible(false);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditSAWMethod.class);
		wdoView.setMessage(resourceMap.getString("ok.Action.msg"));
	}

	@Action
	public void cancel() {
		setVisible(false);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditSAWMethod.class);
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
		infEngineJLabel = new javax.swing.JLabel();
		typeJTextField = new javax.swing.JTextField();
		infEngineJTextField = new javax.swing.JTextField();
		detailedByJLabel = new javax.swing.JLabel();
		detailedByJComboBox = new javax.swing.JComboBox();
		sourceJLabel = new javax.swing.JLabel();
		sourceComboBox = new MethodSourceComboBox();

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditSAWMethod.class);

		DETAILED_BY_NONE_OPTION = resourceMap
				.getString("detailedBy.none.option");

		setTitle(resourceMap.getString("window.title"));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("EditSAWMethod"); // NOI18N
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
				.getContext().getActionMap(EditSAWMethod.class, this);
		okButton.setAction(actionMap.get("ok"));
		okButton.setName("okButton"); // NOI18N

		cancelButton.setAction(actionMap.get("cancel"));
		cancelButton.setName("cancelButton"); // NOI18N

		commentLabel.setText(resourceMap.getString("commentLabel.text")); // NOI18N
		commentLabel.setName("commentLabel"); // NOI18N

		nameJLabel.setText(resourceMap.getString("nameJLabel.text")); // NOI18N
		nameJLabel.setName("nameJLabel"); // NOI18N

		typeJLabel.setText(resourceMap.getString("typeJLabel.text")); // NOI18N
		typeJLabel.setName("typeJLabel"); // NOI18N

		infEngineJLabel.setText(resourceMap.getString("infEngineJLabel.text")); // NOI18N
		infEngineJLabel.setName("infEngineJLabel"); // NOI18N

		typeJTextField.setEditable(false);
		typeJTextField.setName("typeJTextField"); // NOI18N

		infEngineJTextField.setName("infEngineJTextField"); // NOI18N

		detailedByJLabel
				.setText(resourceMap.getString("detailedByJLabel.text")); // NOI18N
		detailedByJLabel.setName("detailedByJLabel"); // NOI18N

		detailedByJComboBox.setName("detailedByJComboBox"); // NOI18N

		sourceJLabel.setText(resourceMap.getString("sourceJLabel.text")); // NOI18N
		sourceJLabel.setName("sourceJLabel"); // NOI18N

		sourceComboBox.setName("sourceComboBox");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.LEADING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						typeJLabel)
																				.addComponent(
																						nameJLabel))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						typeJTextField,
																						0,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						nameJTextField,
																						0,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
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
												.addGroup(
														javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addComponent(
																		infEngineJLabel)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		infEngineJTextField,
																		0,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE))
												.addGroup(
														javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addComponent(
																		sourceJLabel)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		sourceComboBox,
																		0,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE))
												.addGroup(
														javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addComponent(
																		detailedByJLabel)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		detailedByJComboBox,
																		0,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE))
												.addComponent(
														commentLabel,
														javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														commentJScrollPane,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														323, Short.MAX_VALUE))
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
														sourceComboBox,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(sourceJLabel))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														infEngineJTextField,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(infEngineJLabel))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														detailedByJComboBox,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(detailedByJLabel))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(commentLabel)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(commentJScrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										97, Short.MAX_VALUE).addContainerGap()));
		this.setMinimumSize(new Dimension(300, 300));

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
	 * Listener class that sets the focus to the label text box when the window
	 * becomes active
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
