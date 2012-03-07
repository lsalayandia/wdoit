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
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoapi.util.Name;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.ui.components.NameTextField;
import edu.utep.cybershare.wdoit.ui.components.SourceComboBox;

/**
 * GUI to edit properties of Source/Sink instances included in a SAW
 * 
 * @author Leonardo Salayandia
 */
public class EditSAWSource extends JDialog {

	private static final long serialVersionUID = 1L;
	private javax.swing.ButtonGroup buttonGroup;
	private javax.swing.JButton cancelButton;
	private javax.swing.JScrollPane commentScrollPane;
	private javax.swing.JTextArea commentTextArea;
	private javax.swing.JLabel commentLabel;
	private javax.swing.JPanel defineSourcePanel;
	private javax.swing.JLabel nameLabel;
	private NameTextField nameTextField;
	private javax.swing.JButton okButton;
	private javax.swing.JRadioButton op1RadioButton;
	private javax.swing.JRadioButton op2RadioButton;
	private javax.swing.JComboBox referenceComboBox;
	private javax.swing.JPanel referenceSourcePanel;
	private SourceComboBox typeComboBox;
	private javax.swing.JLabel typeLabel;

	private OntClass selectedSourceInd; // SAW instance selected for edit
	private boolean requiredFieldsSet; // property to indicate that the required
										// fields have valid values set

	/** Creates new form EditSAWSource */
	public EditSAWSource(Frame parent) {
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
	 * @param selectedInd
	 */
	public void initWindow(OntClass selectedInd) {
		if (State.getInstance().getSelectedWorkflow()
				.isInBaseModel(selectedInd)) {
			buttonGroup.setSelected(op1RadioButton.getModel(), true);
			selectedSourceInd = selectedInd;
			// set label for selected source
			nameTextField.setText(SAW.getSAWInstanceLabel(selectedSourceInd));
			// set comment for selected source
			commentTextArea.setText(SAW
					.getSAWInstanceComment(selectedSourceInd));
			// set value selected on source type combo box
			typeComboBox.setSelectedItem(SAW
					.getSAWInstanceType(selectedSourceInd));
		} else {
			buttonGroup.setSelected(op2RadioButton.getModel(), true);

		}

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
		String name = this.nameTextField.getText();
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
		state.setIndividualLabel(selectedSourceInd, nameTextField.getText());
		// update comment
		state.setIndividualComment(selectedSourceInd, commentTextArea.getText());
		// update type
		state.setIndividualType(selectedSourceInd,
				(OntClass) typeComboBox.getSelectedItem());
		// update GUI
		setVisible(false);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditSAWSource.class);
		wdoView.setMessage(resourceMap.getString("ok.Action.msg"));
	}

	@Action
	public void cancel() {
		setVisible(false);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditSAWSource.class);
		wdoView.setMessage(resourceMap.getString("cancel.Action.msg"));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		buttonGroup = new javax.swing.ButtonGroup();
		nameTextField = new NameTextField();
		typeComboBox = new SourceComboBox();
		commentScrollPane = new javax.swing.JScrollPane();
		commentTextArea = new javax.swing.JTextArea();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		commentLabel = new javax.swing.JLabel();
		nameLabel = new javax.swing.JLabel();
		typeLabel = new javax.swing.JLabel();
		op1RadioButton = new javax.swing.JRadioButton();
		op2RadioButton = new javax.swing.JRadioButton();
		defineSourcePanel = new javax.swing.JPanel();
		referenceSourcePanel = new javax.swing.JPanel();
		referenceComboBox = new javax.swing.JComboBox();

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(EditSAWSource.class);

		setTitle(resourceMap.getString("window.title"));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("EditSAWSource"); // NOI18N
		setModal(true);

		typeComboBox.setName("typeComboBox"); // NOI18N

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(EditSAWSource.class, this);
		okButton.setAction(actionMap.get("ok"));
		okButton.setName("okButton"); // NOI18N

		cancelButton.setAction(actionMap.get("cancel"));
		cancelButton.setName("cancelButton"); // NOI18N

		typeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		typeLabel.setText(resourceMap.getString("typeLabel.text")); // NOI18N
		typeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		typeLabel.setName("typeLabel"); // NOI18N

		OptionChangeListener ocl = new OptionChangeListener();

		op1RadioButton.setText(resourceMap.getString("op1RadioButton.text")); // NOI18N
		op1RadioButton.setName("op1RadioButton"); // NOI18N
		op1RadioButton.addChangeListener(ocl);

		op2RadioButton.setText(resourceMap.getString("op2RadioButton.text")); // NOI18N
		op2RadioButton.setName("op2RadioButton"); // NOI18N
		op2RadioButton.setEnabled(false);
		op2RadioButton.addChangeListener(ocl);

		buttonGroup.add(op1RadioButton);
		buttonGroup.add(op2RadioButton);
		buttonGroup.setSelected(op1RadioButton.getModel(), true);

		defineSourcePanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("defineSourcePanel.border.title"))); // NOI18N
		defineSourcePanel.setName("defineSourcePanel"); // NOI18N

		nameTextField.setText(resourceMap.getString("nameTextField.text")); // NOI18N
		nameTextField.setName("nameTextField"); // NOI18N
		nameTextField.addKeyListener(new RequiredTextFieldListener());

		nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		nameLabel.setText(resourceMap.getString("nameLabel.text")); // NOI18N
		nameLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
		nameLabel.setName("nameLabel"); // NOI18N

		commentLabel.setText(resourceMap.getString("commentLabel.text")); // NOI18N
		commentLabel.setName("commentLabel"); // NOI18N

		commentScrollPane.setName("commentScrollPane"); // NOI18N

		commentTextArea.setColumns(20);
		commentTextArea.setRows(5);
		commentTextArea.setLineWrap(true);
		commentTextArea.setWrapStyleWord(true);
		commentTextArea.setName("commentTextArea"); // NOI18N
		commentScrollPane.setViewportView(commentTextArea);

		javax.swing.GroupLayout defineSourcePanelLayout = new javax.swing.GroupLayout(
				defineSourcePanel);
		defineSourcePanel.setLayout(defineSourcePanelLayout);
		defineSourcePanelLayout
				.setHorizontalGroup(defineSourcePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								defineSourcePanelLayout
										.createSequentialGroup()
										.addGroup(
												defineSourcePanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																defineSourcePanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				nameLabel)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				nameTextField,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				396,
																				Short.MAX_VALUE))
														.addComponent(
																commentLabel,
																javax.swing.GroupLayout.Alignment.LEADING))
										.addContainerGap())
						.addComponent(commentScrollPane,
								javax.swing.GroupLayout.DEFAULT_SIZE, 441,
								Short.MAX_VALUE));
		defineSourcePanelLayout
				.setVerticalGroup(defineSourcePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								defineSourcePanelLayout
										.createSequentialGroup()
										.addGroup(
												defineSourcePanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(nameLabel)
														.addComponent(
																nameTextField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																20,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(commentLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												commentScrollPane,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		referenceSourcePanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("referenceSourcePanel.border.title"))); // NOI18N
		referenceSourcePanel.setName("referenceSourcePanel"); // NOI18N

		referenceComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
		referenceComboBox.setName("referenceComboBox"); // NOI18N

		javax.swing.GroupLayout referenceSourcePanelLayout = new javax.swing.GroupLayout(
				referenceSourcePanel);
		referenceSourcePanel.setLayout(referenceSourcePanelLayout);
		referenceSourcePanelLayout
				.setHorizontalGroup(referenceSourcePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(referenceComboBox,
								javax.swing.GroupLayout.Alignment.TRAILING, 0,
								441, Short.MAX_VALUE));
		referenceSourcePanelLayout.setVerticalGroup(referenceSourcePanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(referenceComboBox,
						javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE));

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
														referenceSourcePanel,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addGroup(
														javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addComponent(
																		typeLabel)
																.addGap(5, 5, 5)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addComponent(
																										op1RadioButton)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addComponent(
																										op2RadioButton))
																				.addComponent(
																						typeComboBox,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						353,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.TRAILING)
																				.addComponent(
																						cancelButton,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						Short.MAX_VALUE)
																				.addComponent(
																						okButton,
																						javax.swing.GroupLayout.Alignment.LEADING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						65,
																						Short.MAX_VALUE)))
												.addComponent(
														defineSourcePanel,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(typeLabel)
												.addComponent(
														typeComboBox,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(okButton))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(op1RadioButton)
												.addComponent(op2RadioButton)
												.addComponent(
														cancelButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														23,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(defineSourcePanel,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(referenceSourcePanel,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		pack();
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
			nameTextField.requestFocus(); // set the name text box as the
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
	 * Listener class for selecting/deselecting the option radio buttons
	 * 
	 * @author Leonardo Salayandia
	 */
	private class OptionChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JRadioButton option = (JRadioButton) e.getSource();
			String optionName = option.getName();
			if (optionName.equals("op1RadioButton")) {
				nameTextField.setEnabled(option.isSelected());
				commentTextArea.setEnabled(option.isSelected());
				referenceComboBox.setEnabled(!option.isSelected());
			} else if (optionName.equals("op2RadioButton")) {
				nameTextField.setEnabled(!option.isSelected());
				commentTextArea.setEnabled(!option.isSelected());
				referenceComboBox.setEnabled(option.isSelected());
			}
		}
	}
}
