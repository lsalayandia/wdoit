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

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;

import javax.swing.ActionMap;
import javax.swing.JPanel;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jgraph.JGraph;

import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.action.WorkflowDropTargetListener;
import edu.utep.cybershare.wdoit.action.WorkflowSelectionListener;

/**
 * 
 * @author Leonardo Salayandia
 * 
 */
public class RunsJPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private javax.swing.JButton addJButton;
	private javax.swing.JButton propertiesJButton;
	private javax.swing.JButton removeJButton;
	private javax.swing.JList runsJList;
	private javax.swing.JScrollPane runsJScrollPane;
	private javax.swing.JTextField runsJTextField;
	private javax.swing.JPanel sawJPanel;
	private javax.swing.JScrollPane sawJScrollPane;

	private JGraph sawGraph;
	private WorkflowDropTargetListener dropTargetListener;
	private WorkflowSelectionListener selectionListener;

	private boolean runSelected;
	private boolean runModified;

	public RunsJPanel() {
		super();

		initComponents();
		runSelected = false;
		runModified = false;
		this.dropTargetListener = new WorkflowDropTargetListener();
		this.selectionListener = new WorkflowSelectionListener();
		this.setWorkflow(null, null);

	}

	private void initComponents() {
		runsJScrollPane = new javax.swing.JScrollPane();
		runsJList = new javax.swing.JList();
		addJButton = new javax.swing.JButton();
		removeJButton = new javax.swing.JButton();
		sawJScrollPane = new javax.swing.JScrollPane();
		sawJPanel = new javax.swing.JPanel();
		runsJTextField = new javax.swing.JTextField();
		propertiesJButton = new javax.swing.JButton();

		this.setName("runsTab"); // NOI18N

		runsJScrollPane.setName("runsJScrollPane"); // NOI18N

		runsJList.setModel(new javax.swing.AbstractListModel() {
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return 0;
			}

			public Object getElementAt(int i) {
				return null;
			}
		});
		runsJList.setName("runsJList"); // NOI18N
		runsJScrollPane.setViewportView(runsJList);

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(RunsJPanel.class, this);
		addJButton.setAction(actionMap.get("addRun"));
		addJButton.setName("addJButton"); // NOI18N

		removeJButton.setAction(actionMap.get("removeRun"));
		removeJButton.setName("removeJButton"); // NOI18N

		propertiesJButton.setAction(actionMap.get("showRunProperties"));
		propertiesJButton.setName("propertiesJButton"); // NOI18N

		sawJScrollPane.setName("sawJScrollPane"); // NOI18N

		sawJPanel.setName("sawJPanel"); // NOI18N

		javax.swing.GroupLayout sawJPanelLayout = new javax.swing.GroupLayout(
				sawJPanel);
		sawJPanel.setLayout(sawJPanelLayout);
		sawJPanelLayout.setHorizontalGroup(sawJPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 579,
				Short.MAX_VALUE));
		sawJPanelLayout.setVerticalGroup(sawJPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 487,
				Short.MAX_VALUE));

		sawJScrollPane.setViewportView(sawJPanel);

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(RunsJPanel.class);
		runsJTextField.setEditable(false);
		runsJTextField.setText(resourceMap.getString("runsJTextField.text")); // NOI18N
		runsJTextField.setName("runsJTextField"); // NOI18N

		javax.swing.GroupLayout runsTabLayout = new javax.swing.GroupLayout(
				this);
		this.setLayout(runsTabLayout);
		runsTabLayout
				.setHorizontalGroup(runsTabLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								runsTabLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												runsTabLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addGroup(
																runsTabLayout
																		.createSequentialGroup()
																		.addComponent(
																				addJButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				90,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				removeJButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				100,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				propertiesJButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				90,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(
																runsJScrollPane))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												runsTabLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																runsJTextField,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																438,
																Short.MAX_VALUE)
														.addComponent(
																sawJScrollPane,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																438,
																Short.MAX_VALUE))
										.addContainerGap()));
		runsTabLayout
				.setVerticalGroup(runsTabLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								runsTabLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												runsTabLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																runsJScrollPane,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																457,
																Short.MAX_VALUE)
														.addComponent(
																sawJScrollPane,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																457,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												runsTabLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																runsJTextField,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																addJButton,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																removeJButton,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																propertiesJButton,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));
	}

	// // Property methods ////

	/**
	 * Check whether the runSelected property is enabled
	 * 
	 * @return
	 */
	public boolean isRunSelected() {
		return runSelected;
	}

	/**
	 * Set the runSelected property according to runJList's state
	 */
	private void setRunSelected() {
		boolean oldval = runSelected;
		runSelected = !this.runsJList.isSelectionEmpty();
		if (runSelected != oldval) {
			this.firePropertyChange("runSelected", oldval, runSelected);
		}
	}

	/**
	 * Check whether the runModified property is enabled
	 * 
	 * @return
	 */
	public boolean isRunModified() {
		return runModified;
	}

	/**
	 * Set the runModified property according to State
	 */
	public void setRunModified() {

	}

	/**
	 * Update all properties of this panel
	 */
	public void updateProperties() {
		setRunSelected();
		setRunModified();
	}

	// // end property methods ////

	// // action methods ////

	@Action
	public void addRun() {

		this.updateProperties();
	}

	@Action(enabledProperty = "runSelected")
	public void removeRun() {

		this.updateProperties();
	}

	@Action(enabledProperty = "runSelected")
	public void showRunProperties() {

		this.updateProperties();
	}

	// // end of action methods ////

	/**
	 * Set the workflow graph to display in this panel
	 */
	protected void setWorkflow(JGraph graph, WorkflowPopupMenu workflowPopupMenu) {
		this.sawGraph = graph;
		if (this.sawGraph != null) {
			// Control-drag should clone selection
			this.sawGraph.setCloneable(false);
			// Do not allow labels to be edited in the graph
			this.sawGraph.setEditable(false);
			// Enable edit without final RETURN keystroke
			this.sawGraph.setInvokesStopCellEditing(false);
			// When over a cell, jump to its default port (we only have one,
			// anyway)
			this.sawGraph.setJumpToDefaultPort(true);
			// Set graph to be connectable through GUI
			this.sawGraph.setConnectable(false);
			// set graph to be movable
			this.sawGraph.setMoveable(false);
			// set graph to be disconnectable through GUI
			this.sawGraph.setDisconnectable(false);
			// add listeners
			this.sawGraph.setDropTarget(new DropTarget(this,
					DnDConstants.ACTION_COPY, dropTargetListener));
			this.sawGraph.addMouseListener(selectionListener);
			// set pop up menu
			this.sawGraph.setComponentPopupMenu(workflowPopupMenu);
		}
		this.sawJScrollPane.setViewportView(sawGraph);
		// update state
		this.selectionListener.updateSelectedInstance(sawGraph);
	}

	/**
	 * Set the runs to display in this panel
	 */
	protected void setRuns() {
		runsJList.setModel(new javax.swing.AbstractListModel() {
			private static final long serialVersionUID = 1L;
			String[] strings = { "Run 1", "Run 2", "Run 3", "Run 4", "Run 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		// TODO: Add selection listener
		runsJScrollPane.setViewportView(runsJList);
	}

}
