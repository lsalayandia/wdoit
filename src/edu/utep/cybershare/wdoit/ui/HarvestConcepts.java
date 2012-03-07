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
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.tree.TreePath;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.tree.OntModelJTree;

/**
 * Panel to harvest concepts from ontologies
 * 
 * @author Leonardo Salayandia
 */
public class HarvestConcepts extends JDialog {
	private static final long serialVersionUID = 1L;
	private ButtonGroup buttonGroup;
	private JButton cancelButton;
	private OntModelJTree conceptTree;
	private JRadioButton dataButton;
	private JButton doneButton;
	private JRadioButton methodButton;
	private JScrollPane scrollPane;

	/** Creates new form HarvestConcepts */
	public HarvestConcepts(Frame parent) {
		super(parent);

		this.addWindowListener(new ActivatedWindowListener());

		initComponents();
		getRootPane().setDefaultButton(doneButton);
	}

	/**
	 * Initialize window to harvest concepts.
	 * 
	 * @param selectedInd
	 */
	public void initWindow(String ontURI) {
		State state = State.getInstance();
		OntModel ontmodel = state.getOntModel(ontURI);
		if (ontmodel != null) {
			conceptTree = new OntModelJTree();
			conceptTree.setOntModel(ontmodel);
			conceptTree.setHarvestSelection(true);
			scrollPane.setViewportView(conceptTree);
		} else {
			conceptTree = null;
		}
	}

	@Action
	public void done() {
		String statusMsg = "";

		TreePath[] selectionPaths = conceptTree.getSelectionPaths();
		ArrayList<OntClass> dataConcepts = new ArrayList<OntClass>();
		ArrayList<OntClass> methodConcepts = new ArrayList<OntClass>();
		ArrayList<OntClass> harvestedConcepts = new ArrayList<OntClass>();
		State state = State.getInstance();
		if (selectionPaths == null) {
			statusMsg = "No concepts selected for harvesting.";
		} else {
			// get the selected concepts and identify those that are already wdo
			// concepts
			for (int i = 0; i < selectionPaths.length; i++) {
				OntClass cls = (OntClass) selectionPaths[i]
						.getLastPathComponent();
				if (state.isDataSubClass(cls)) {
					dataConcepts.add(cls);
				} else if (state.isMethodSubClass(cls)) {
					methodConcepts.add(cls);
				} else {
					harvestedConcepts.add(cls);
				}
			}
			// of selected concepts that are not wdo concepts, identify
			// subconcepts
			ArrayList<OntClass> harvestedSubConcepts = new ArrayList<OntClass>();
			for (Iterator<OntClass> i = harvestedConcepts.iterator(); i
					.hasNext();) {
				OntClass conceptX = i.next();
				for (Iterator<OntClass> j = harvestedConcepts.iterator(); j
						.hasNext();) {
					OntClass conceptY = j.next();
					if (!conceptX.equals(conceptY)
							&& conceptX.hasSubClass(conceptY)) {
						harvestedSubConcepts.add(conceptY);
					}
				}
			}
			// select the selected wdo superclass, i.e., wdo:Data or wdo:Method
			OntClass supercls = state.getBaseWDO().getOntClass(
					(dataButton.isSelected()) ? WDO_Metamodel.DATA_URI
							: WDO_Metamodel.METHOD_URI);
			if (supercls == null) {
				statusMsg = "Error harvesting concepts. WDO superclass not found.";
			} else {
				int count = 0;
				for (Iterator<OntClass> i = harvestedConcepts.iterator(); i
						.hasNext();) {
					OntClass cls = i.next();
					if (!harvestedSubConcepts.contains(cls)
							&& state.addSubClass(supercls, cls)) {
						count++;
					}
				}
				statusMsg = count + " concepts successfully harvested.";
			}
		}
		setVisible(false);
		state.setSelectedOntology(state.getBaseWDO());
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		wdoView.setMessage(statusMsg);
	}

	@Action
	public void cancel() {
		setVisible(false);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(HarvestConcepts.class);
		wdoView.setMessage(resourceMap.getString("cancel.Action.msg"));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		buttonGroup = new ButtonGroup();
		scrollPane = new JScrollPane();
		dataButton = new JRadioButton();
		methodButton = new JRadioButton();
		doneButton = new JButton();
		cancelButton = new JButton();

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(HarvestConcepts.class);
		setTitle(resourceMap.getString("window.title"));
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setName("HarvestConcepts"); // NOI18N
		setModal(true);
		setMinimumSize(new java.awt.Dimension(510, 310));

		scrollPane.setName("scrollPane"); // NOI18N

		dataButton.setText(resourceMap.getString("dataButton.text")); // NOI18N
		dataButton.setName("dataButton"); // NOI18N

		methodButton.setText(resourceMap.getString("methodButton.text")); // NOI18N
		methodButton.setName("methodButton"); // NOI18N

		buttonGroup.add(dataButton);
		buttonGroup.add(methodButton);
		buttonGroup.setSelected(dataButton.getModel(), true);

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(HarvestConcepts.class, this);
		doneButton.setAction(actionMap.get("done"));
		doneButton.setName("doneButton"); // NOI18N

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
								.addComponent(dataButton)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(methodButton)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										6, Short.MAX_VALUE)
								.addComponent(cancelButton)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(doneButton).addContainerGap())
				.addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE,
						499, Short.MAX_VALUE));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addComponent(scrollPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										304, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																				.addComponent(
																						doneButton)
																				.addComponent(
																						cancelButton))
																.addContainerGap())
												.addGroup(
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
																.addComponent(
																		dataButton)
																.addComponent(
																		methodButton)))));
	}

	/**
	 * Listener class that sets the focus to the concept tree when the window
	 * becomes active
	 * 
	 * @author Leonardo Salayandia
	 */
	private class ActivatedWindowListener implements WindowListener {
		@Override
		public void windowActivated(WindowEvent e) {
			if (conceptTree != null) {
				conceptTree.requestFocus(); // set the concept tree as the
											// initial focus component
			} else {
				cancelButton.requestFocus();
			}
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
