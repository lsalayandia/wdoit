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

import edu.utep.cybershare.util.AlfrescoClient;
import edu.utep.cybershare.wdoit.ui.WFTalkFrame;

//import edu.utep.cybershare.ciclient.ciconnect.CIClient;
//import edu.utep.cybershare.ciclient.ciconnect.CIKnownServerTable;
//import edu.utep.cybershare.ciclient.ciconnect.CIServerCache;
//import edu.utep.cybershare.ciclient.ciui.CIDesktop;
//import edu.utep.cybershare.ciclient.ciui.CINewResourceNameDialog;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;
import edu.utep.cybershare.wdoapi.util.Namespace;
import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoapi.Workspace;
import edu.utep.cybershare.wdoapi.WorkspaceFile;
import edu.utep.cybershare.wdoapi.Bookmarks;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.action.OntologyTreeDragSourceListener;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.IndividualEdge;
import edu.utep.cybershare.wdoit.graphics.IndividualNode;
import edu.utep.cybershare.wdoit.task.ConfirmExit;
import edu.utep.cybershare.wdoit.task.LoadOWLDocumentTask;
import edu.utep.cybershare.wdoit.task.SaveOWLDocumentTask;
import edu.utep.cybershare.wdoit.tree.OntDocumentManagerJTree;
import edu.utep.cybershare.wdoit.tree.OntModelJTree;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import org.jgraph.graph.DefaultGraphCell;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;

import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;

/**
 * The application's main frame.
 */
public class WdoView extends FrameView {

	private javax.swing.JMenu bookmarksMenu;
	private javax.swing.JSeparator bookmarksMenuSeparator1;
	private javax.swing.JMenuItem bookmarksMenuaddBookmark;
	private OntModelJTree dataHierarchy;
	private javax.swing.JLabel dataLabel;
	private javax.swing.JSplitPane dataMethodSplitPane;
	private javax.swing.JPanel dataPanel;
	private javax.swing.JScrollPane dataScrollPane;
	private javax.swing.JMenuItem fileMenuCreateNewWDO;
	private javax.swing.JMenuItem fileMenuCreateNewWorkflow;
	private javax.swing.JMenuItem fileMenuExportAnnotator;
	private javax.swing.JMenuItem fileMenuHarvestConcepts;
	private javax.swing.JMenu fileMenuExportWorkflow;
	private javax.swing.JMenuItem fileMenuExit;
	private javax.swing.JMenuItem fileMenuOpenFile;
	private javax.swing.JMenuItem fileMenuOpenURI;
	private javax.swing.JMenuItem fileMenuSave;
	private javax.swing.JMenuItem fileMenuSaveAll;
	private javax.swing.JMenuItem fileMenuSaveAs;
	private javax.swing.JSeparator fileMenuSeparator1;
	private javax.swing.JSeparator fileMenuSeparator2;
	private javax.swing.JSeparator fileMenuSeparator3;
	private javax.swing.JSeparator fileMenuSeparator4;
	private javax.swing.JSeparator fileMenuSeparator5;
	private javax.swing.JMenuItem fileMenuCloseWorkspace;
	private javax.swing.JMenuItem fileMenuOpenWorkspace;
	private javax.swing.JMenuItem fileMenuSaveWorkspace;
	private javax.swing.JMenu fileMenuCIServer;
	private javax.swing.JMenuItem ciConnectToServer;
	private javax.swing.JMenuItem ciCreateProject;
	private javax.swing.JMenuItem ciCheckOutProject;
	private javax.swing.JMenuItem ciOpenProject;
	private javax.swing.JMenuItem ciAddSawToProject;
	private javax.swing.JMenuItem ciCheckInProject;
	private javax.swing.JMenuItem ciCloseProject;
	private javax.swing.JMenuItem ciSaveProject;
	private javax.swing.JMenuItem ciDisconnectFromServer;
	private javax.swing.JSeparator fileMenuCISeparator1;
	private javax.swing.JSeparator fileMenuCISeparator2;
	private OntDocumentManagerJTree loadedOWLDocs;
	private javax.swing.JLabel loadedOWLDocsLabel;
	private javax.swing.JPanel loadedOWLDocsPanel;
	private javax.swing.JScrollPane loadedOWLDocsScrollPane;
	private javax.swing.JPanel mainPanel;
	private javax.swing.JSplitPane mainSplitPane;
	private javax.swing.JMenuBar menuBar;
	private OntModelJTree methodHierarchy;
	private javax.swing.JLabel methodLabel;
	private javax.swing.JPanel methodPanel;
	private javax.swing.JScrollPane methodScrollPane;
	private javax.swing.JToggleButton maxTabButton;
	private javax.swing.JPanel ontologyTab;
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JLabel statusAnimationLabel;
	private javax.swing.JLabel statusMessageLabel;
	private javax.swing.JPanel statusPanel;
	private javax.swing.JTabbedPane tabbedPane;
	private javax.swing.JPanel tabbedSpacePanel;
	private javax.swing.JToolBar toolBar;
	private javax.swing.JButton toolBarAddConcept;
	private javax.swing.JButton toolBarEditConcept;
	private javax.swing.JButton toolBarEditInstance;
	private javax.swing.JButton toolBarCreateNewWDO;
	private javax.swing.JButton toolBarCreateNewWorkflow;
	private javax.swing.JButton toolBarOpenFile;
	private javax.swing.JButton toolBarRemoveConcept;
	private javax.swing.JButton toolBarSave;
	private javax.swing.JButton toolBarSaveAll;
//	private javax.swing.JButton toolBarWFTalk;
//	private javax.swing.JButton toolBarCIDesktop;
	private javax.swing.JToolBar.Separator toolBarSeparator1;
	private javax.swing.JToolBar.Separator toolBarSeparator2;
	private javax.swing.JMenu toolsMenu;
	private javax.swing.JMenuItem toolsMenuGenerateReport;
	private javax.swing.JMenu viewMenu;
	private javax.swing.JCheckBoxMenuItem viewMenuShowType;
	protected SAWScrollPane workflowScrollPane;
	private javax.swing.JPanel workflowTab;
	private RunsJPanel runsTab;
	private javax.swing.JSplitPane workspaceHorizontalSplitPane;

	private final Timer messageTimer;
	private final Timer busyIconTimer;
	private final Icon idleIcon;
	private final Icon[] busyIcons = new Icon[15];
	private int busyIconIndex = 0;

	private JDialog aboutBox;
	private JDialog createProvenanceAnnotatorsWindow;
	private JDialog editWDOConceptWindow;
	private JDialog editOWLDocumentWindow;
	private JDialog editSAWDataWindow;
	private JDialog editSAWMethodWindow;
	private JDialog editSAWSourceWindow;
	private JDialog harvestConceptWindow;

	private ConceptHierarchyPopupMenu conceptHierarchyPopupMenu;
	private OWLDocumentHierarchyPopupMenu owlDocHierarchyPopupMenu;
	private WorkflowPopupMenu workflowPopupMenu;

	// properties
	private boolean owlDocumentSelected; // there is an owl document selected in
											// the workspace tree
	private boolean selectedOWLDocumentModified; // the selected owl document
													// has been modified
	private boolean wdoConceptSelected; // there is a wdo concept selected in
										// the data or method trees
	private boolean sawInstanceSelected; // there is a saw instance selected in
											// the workflow graph
	private boolean sawMethodInstanceSelected; // there is a saw method instance
												// selected in the workflow
												// graph
	private boolean removableSawInstanceSelected; // there is a saw instance
													// selected that can be
													// manually removed from the
													// workflow graph
	private boolean workflowSelected; // the selected owl document is a workflow
	private boolean workspaceLoaded; // a workspace has been loaded
	private boolean workspaceModified; // at least one owl document in the
										// workspace has been modified
	private boolean ciServerConnected; // is the system currently connected to a
										// ci-server
	
	// alfresco client vars
	private AlfrescoClient aClient;
//	private String username, password;
//	private String selectedServerSTR;
//	private String selectedProjectSTR;
//	private String selectedOntologySTR = null;
//	private javax.swing.JLabel selectedProjectLabel;
//	private javax.swing.JLabel selectedServerLabel;

	
	public WdoView(SingleFrameApplication app, String initURI) {
		super(app);

		initComponents();

		// status bar initialization - message timeout, idle icon and busy
		// animation, etc
		ResourceMap resourceMap = getResourceMap();
		int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
		messageTimer = new Timer(messageTimeout, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statusMessageLabel.setText("");
			}
		});
		messageTimer.setRepeats(false);
		int busyAnimationRate = resourceMap
				.getInteger("StatusBar.busyAnimationRate");
		for (int i = 0; i < busyIcons.length; i++) {
			busyIcons[i] = resourceMap
					.getIcon("StatusBar.busyIcons[" + i + "]");
		}
		busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
				statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
			}
		});
		idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
		statusAnimationLabel.setIcon(idleIcon);
		progressBar.setVisible(false);

		// connecting action tasks to status bar via TaskMonitor
		TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
		taskMonitor
				.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
					public void propertyChange(
							java.beans.PropertyChangeEvent evt) {
						String propertyName = evt.getPropertyName();
						if ("started".equals(propertyName)) {
							if (!busyIconTimer.isRunning()) {
								statusAnimationLabel.setIcon(busyIcons[0]);
								busyIconIndex = 0;
								busyIconTimer.start();
							}
							progressBar.setVisible(true);
							progressBar.setIndeterminate(true);
						} else if ("done".equals(propertyName)) {
							busyIconTimer.stop();
							statusAnimationLabel.setIcon(idleIcon);
							progressBar.setVisible(false);
							progressBar.setValue(0);
						} else if ("message".equals(propertyName)) {
							String text = (String) (evt.getNewValue());
							statusMessageLabel.setText((text == null) ? ""
									: text);
							messageTimer.restart();
						} else if ("progress".equals(propertyName)) {
							int value = (Integer) (evt.getNewValue());
							progressBar.setVisible(true);
							progressBar.setIndeterminate(false);
							progressBar.setValue(value);
						}
					}
				});

		// ask for confirmation when exiting
		getApplication().addExitListener(new ConfirmExit(this));

		// initialize listeners and popup menus
		conceptHierarchyPopupMenu = new ConceptHierarchyPopupMenu(this);
		owlDocHierarchyPopupMenu = new OWLDocumentHierarchyPopupMenu(this);
		workflowPopupMenu = new WorkflowPopupMenu(this);

		// initialize properties
		owlDocumentSelected = false;
		selectedOWLDocumentModified = false;
		wdoConceptSelected = false;
		workflowSelected = false;
		workspaceLoaded = false;
		workspaceModified = false;
		sawInstanceSelected = false;
		sawMethodInstanceSelected = false;
		removableSawInstanceSelected = false;
		ciServerConnected = false;

		// if initial URI was provided, try to load it
		if (initURI != null) {
			State.getInstance().setURIToLoad(initURI);
			this.openOWLURI().execute();
		}
	}

	// /////////////////////////////// PROPERTY METHODS
	// ///////////////////////////
	// These are the setter and getter methods for the properties of this
	// window.
	// Properties are usually set based on the State of the application, not on
	// explicit input.
	// ////////////////////////////////////////////////////////////////////////////
	/**
	 * Check whether the owlDocumentSelected property is enabled
	 * 
	 * @return
	 */
	public boolean isOwlDocumentSelected() {
		return owlDocumentSelected;
	}

	/**
	 * Set the owlDocumentSelected property according to State
	 */
	private void setOwlDocumentSelected() {
		boolean oldval = owlDocumentSelected;
		State state = State.getInstance();
		owlDocumentSelected = (state.getSelectedOWLDocument() != null);
		if (owlDocumentSelected != oldval) {
			this.firePropertyChange("owlDocumentSelected", oldval,
					owlDocumentSelected);
		}
	}

	/**
	 * Check whether the selectedOWLDocumentModified property is enabled
	 * 
	 * @return
	 */
	public boolean isSelectedOWLDocumentModified() {
		return selectedOWLDocumentModified;
	}

	/**
	 * Set the selectedOWLDocumentModified property according to State
	 */
	private void setSelectedOWLDocumentModified() {
		boolean oldval = selectedOWLDocumentModified;
		selectedOWLDocumentModified = State.getInstance().isModified(
				loadedOWLDocs.getSelectedValue());
		if (selectedOWLDocumentModified != oldval) {
			this.firePropertyChange("selectedOWLDocumentModified", oldval,
					selectedOWLDocumentModified);
		}
	}

	/**
	 * Check whether the wdoConceptSelected property is enabled
	 * 
	 * @return
	 */
	public boolean isWdoConceptSelected() {
		return wdoConceptSelected;
	}

	/**
	 * Set the wdoConceptSelected property according to State
	 */
	private void setWdoConceptSelected() {
		boolean oldval = wdoConceptSelected;
		State state = State.getInstance();
		OntClass temp = state.getSelectedClass();
		wdoConceptSelected = (temp != null && (state.isDataSubClass(temp) || state
				.isMethodSubClass(temp)));
		if (wdoConceptSelected != oldval) {
			this.firePropertyChange("wdoConceptSelected", oldval,
					wdoConceptSelected);
		}
	}

	/**
	 * Check whether the sawInstanceSelected property is enabled
	 * 
	 * @return
	 */
	public boolean isSawInstanceSelected() {
		return sawInstanceSelected;
	}

	/**
	 * Set the sawInstanceSelected property according to State
	 */
	private void setSawInstanceSelected() {
		boolean oldval = sawInstanceSelected;
		sawInstanceSelected = (State.getInstance().getSelectedIndividual() != null);
		if (sawInstanceSelected != oldval) {
			this.firePropertyChange("sawInstanceSelected", oldval,
					sawInstanceSelected);
		}
	}

	/**
	 * Check whether the sawMethodInstanceSelected property is enabled
	 * 
	 * @return
	 */
	public boolean isSawMethodInstanceSelected() {
		return sawMethodInstanceSelected;
	}

	/**
	 * Set the sawMethodInstanceSelected property according to State
	 */
	private void setSawMethodInstanceSelected() {
		boolean oldval = sawMethodInstanceSelected;
		State state = State.getInstance();
		sawMethodInstanceSelected = state.isMethodSubClass(SAW
				.getSAWInstanceType(state.getSelectedIndividual()));
		if (sawMethodInstanceSelected != oldval) {
			this.firePropertyChange("sawMethodInstanceSelected", oldval,
					sawMethodInstanceSelected);
		}
	}

	/**
	 * Check whether the removableSawInstanceSelected property is enabled
	 * 
	 * @return
	 */
	public boolean isRemovableSawInstanceSelected() {
		return removableSawInstanceSelected;
	}

	/**
	 * Set the removableSawInstanceSelected property according to State
	 */
	private void setRemovableSawInstanceSelected() {
		boolean oldval = removableSawInstanceSelected;
		State state = State.getInstance();
		OntClass type = SAW.getSAWInstanceType(state.getSelectedIndividual());
		removableSawInstanceSelected = (state.isMethodSubClass(type) || state
				.isDataSubClass(type));
		if (removableSawInstanceSelected != oldval) {
			this.firePropertyChange("removableSawInstanceSelected", oldval,
					removableSawInstanceSelected);
		}
	}

	/**
	 * Check whether the workflowSelected property is enabled
	 * 
	 * @return
	 */
	public boolean isWorkflowSelected() {
		return workflowSelected;
	}

	/**
	 * Set the workflowSelected property according to State
	 */
	private void setWorkflowSelected() {
		boolean oldval = workflowSelected;
		workflowSelected = (State.getInstance().getSelectedWorkflow() != null);
		if (workflowSelected != oldval) {
			this.firePropertyChange("workflowSelected", oldval,
					workflowSelected);
		}
	}

	/**
	 * Check whether the workspaceEmpty property is enabled
	 * 
	 * @return
	 */
	public boolean isWorkspaceEmpty() {
		return !workspaceLoaded;
	}

	/**
	 * Check whether the workspaceLoaded property is enabled
	 * 
	 * @return
	 */
	public boolean isWorkspaceLoaded() {
		return workspaceLoaded;
	}

	/**
	 * Set the workspaceLoaded property according to State
	 */
	private void setWorkspaceLoaded() {
		boolean oldval = workspaceLoaded;
		workspaceLoaded = !(State.getInstance().isWorkspaceEmpty());
		if (workspaceLoaded != oldval) {
			this.firePropertyChange("workspaceLoaded", oldval, workspaceLoaded);
			this.firePropertyChange("workspaceEmpty", !oldval, !workspaceLoaded);
		}
	}

	/**
	 * Check whether the workspaceModified property is enabled
	 * 
	 * @return
	 */
	public boolean isWorkspaceModified() {
		return workspaceModified;
	}

	/**
	 * Set the workspaceModified property according to State
	 */
	private void setWorkspaceModified() {
		boolean oldval = workspaceModified;
		workspaceModified = State.getInstance().isModified();
		if (workspaceModified != oldval) {
			this.firePropertyChange("workspaceModified", oldval,
					workspaceModified);
		}
	}

	/**
	 * Check whether the ciServerDisconnected property is enabled
	 * 
	 * @return
	 */
	public boolean isCiServerDisconnected() {
		return !ciServerConnected;
	}

	/**
	 * Check whether the ciServerConnected property is enabled
	 * 
	 * @return
	 */
	public boolean isCiServerConnected() {
		return ciServerConnected;
	}

	/**
	 * Set the ciServerConnected property according to State
	 */
	private void setCiServerConnected() {
		boolean oldval = ciServerConnected;
		if (ciServerConnected != oldval) {
			this.firePropertyChange("ciServerConnected", oldval,
					ciServerConnected);
		}
	}

	// /////////// END - PROPERTY METHODS ////////////////

	// /////////////////////// ACTION METHODS
	// ///////////////////////////////////
	// These methods are bounded to the GUI components that trigger actions,
	// e.g., buttons and menu items
	// /////////////////////////////////////////////////////////////////////////
	@Action(enabledProperty = "workspaceLoaded")
	public void addWDOConcept() {
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		State state = State.getInstance();
		OntClass selectedCls = state.getSelectedClass();
		// if no class selected
		if (selectedCls == null) {
			// choose wdo:data if mouse over the data hierarchy
			if (this.dataHierarchy.getMousePosition() != null) {
				selectedCls = state.getBaseWDO().getOntClass(
						WDO_Metamodel.DATA_URI);
			}
			// choose wdo:method if mouse over the method hierarchy
			else if (this.methodHierarchy.getMousePosition() != null) {
				selectedCls = state.getBaseWDO().getOntClass(
						WDO_Metamodel.METHOD_URI);
			}
			// ask user if no class selected and mouse not over data or method
			// hierarchy
			else {
				Object[] options = {
						resourceMap
								.getString("addWDOConcept.Action.dialog.option0"),
						resourceMap
								.getString("addWDOConcept.Action.dialog.option1") };
				int option = JOptionPane
						.showOptionDialog(
								this.getFrame(),
								resourceMap
										.getString("addWDOConcept.Action.dialog"),
								resourceMap
										.getString("addWDOConcept.Action.dialog.title"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
				if (option == JOptionPane.YES_OPTION) {
					selectedCls = state.getBaseWDO().getOntClass(
							WDO_Metamodel.DATA_URI);
				} else if (option == JOptionPane.NO_OPTION) {
					selectedCls = state.getBaseWDO().getOntClass(
							WDO_Metamodel.METHOD_URI);
				}
			}
		}
		// if no class selected, operation has been canceled.
		if (selectedCls == null) {
			this.setMessage(resourceMap
					.getString("addWDOConcept.Action.cancel"));
			return;
		}
		// if a class has been selected, start add window
		EditWDOConcept window = this.getEditWDOConceptWindow();
		window.initWindow(true, selectedCls);
		WdoApp.getApplication().show(window);
		// if new class resulting from add window is not null then successful,
		// otherwise operation has been canceled.
		selectedCls = window.getNewClass();
		if (selectedCls != null) {
			// update selected value of hierarchy to point to the new class
			OntModelJTree hierarchy = (state.isDataSubClass(selectedCls)) ? this.dataHierarchy
					: this.methodHierarchy;
			TreePath path = hierarchy.getSelectionPath();
			if (path == null) {
				Object[] newpath = { hierarchy.getRoot(), selectedCls };
				path = new TreePath(newpath);
			} else {
				path = path.pathByAddingChild(selectedCls);
			}
			hierarchy.setSelectionPath(path);

			this.updateSelectedOWLDoc();
			this.setMessage(resourceMap.getString(
					"addWDOConcept.Action.success", selectedCls.getURI()));
		} else {
			this.setMessage(resourceMap
					.getString("addWDOConcept.Action.cancel"));
		}
	}

	/**
	 * closeWorkspace Author: agandara1 Category: action function Description:
	 * close the workspace, all open ontologies, wdo & workflows Enabled: if
	 * there are workspace files loaded
	 * 
	 */
	@Action(enabledProperty = "workspaceLoaded")
	public void closeWorkspace() {
		// check for saving the workspace before closing it
		boolean proceed = !this.isWorkspaceModified();
		// get appframework object
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		// we recommend user saves before doing this
		if (!proceed) {
			proceed = JOptionPane.showConfirmDialog(this.getComponent(),
					resourceMap.getString("closeWorkspace.Action.confirm"),
					resourceMap
							.getString("closeWorkspace.Action.confirm.title"),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION;
		}
		if (proceed) {
			// reset the workspace and gui variables
			State.getInstance().resetWorkspace();
			this.resetGUI();
			this.setMessage(resourceMap
					.getString("closeWorkspace.Action.success"));
		} else {
			this.setMessage(resourceMap
					.getString("closeWorkspace.Action.cancel"));
		}
	}

	/**
	 * openWorkspace Author: agandara1 Category: Action function Description:
	 * Prompt the user for a workspace file to load Enabled: Only if the
	 * workspace is empty
	 * 
	 */
	@Action(enabledProperty = "workspaceEmpty")
	public void openWorkspace() {

		// get the appframework resource object
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);

		// allow the user to select a workspace file
		JFileChooser chooser = new JFileChooser();
		String xmlExtension = resourceMap
				.getString("workspaceFileExtension.text");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Workflow Files (" + xmlExtension + ")", xmlExtension);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);

		int returnVal = chooser.showOpenDialog(this.getComponent());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			String selectedFileName = selectedFile.getAbsolutePath();
			if (selectedFileName != null) {
				try {
					// load the wdo, ontologies and workflows in the file
					loadWorkspaceFile(selectedFileName);
					this.updateSelectedOWLDoc();
					this.setMessage(resourceMap
							.getString("openWorkspace.Action.success"));
					return;
				} catch (Exception exc) {
					this.setMessage(resourceMap
							.getString("openWorkspace.Action.fail"));
				}
			}
		} else {
			this.setMessage(resourceMap
					.getString("openWorkspace.Action.cancel"));
		}
	}

	/**
	 * saveWorkspace Author: agandara1 Category: Action function Description:
	 * Prompt the user for a workspace file to save to Enabled: Only if there
	 * are files in the workspace
	 * 
	 */
	@Action(enabledProperty = "workspaceLoaded")
	public void saveWorkspace() {

		// get the current program state and appframework objects
		State state = State.getInstance();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);

		// the following is an iterative process where user MUST save down ALL
		// files before we
		// proceed with saving a workspace
		boolean proceed = !this.isWorkspaceModified();

		// if there are are files modified, we can not proceed - start iterative
		// process
		while (!proceed) {
			int value = JOptionPane.showConfirmDialog(this.getComponent(),
					"OWL Documents have changed. \n "
							+ "You must first save all modified files.",
					"Save all modified files?", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			// if user chooses not to save files, exit save workspace
			if ((value == JOptionPane.CANCEL_OPTION)) {
				this.setMessage(resourceMap
						.getString("saveWorkspace.Action.cancel"));
				return;
			}

			// if user chooses to save - then run a saveall
			if (value == JOptionPane.OK_OPTION) {
				ArrayList<OntModel> ontmodelList = new ArrayList<OntModel>();
				ArrayList<String> urlList = new ArrayList<String>();
				for (Iterator<String> i = state.listOntologyURIs(); i.hasNext();) {
					OntModel temp = state.getOntModel(i.next());
					if (temp != null) {
						ontmodelList.add(temp);
						urlList.add(state.getOWLDocumentURL(temp));
					}
				}
				for (Iterator<String> i = state.listWorkflowURIs(); i.hasNext();) {
					OntModel temp = state.getOntModel(i.next());
					if (temp != null) {
						ontmodelList.add(temp);
						urlList.add(state.getOWLDocumentURL(temp));
					}
				}
				if (!ontmodelList.isEmpty()) {
					// initialize the save call
//					Task<Void, Void> tsk = new SaveOWLDocumentTask(
//							ontmodelList, urlList, false);
					Task<Void, Void> tsk = new SaveOWLDocumentTask(
							ontmodelList, urlList);
					// since this is an action command - must specifically run
					// the task
					tsk.run();
				}
			}

			// assure that all files have been saved
			// thus: we can only proceed if there are NO
			// unsaved files - repeat the loop
			proceed = !this.isWorkspaceModified();
		}

		// get this far if we are to proceed and ALL files have been saved
		// Allow the user to select a workspace file name and save the file
		// there
		JFileChooser chooser = new JFileChooser();
		String xmlExtension = resourceMap
				.getString("workspaceFileExtension.text");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Workspace Files (" + xmlExtension + ")", xmlExtension);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		// prompt the user
		int returnVal = chooser.showSaveDialog(this.getComponent());

		// returnVal has the response saving or not
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// selectedFile has the file object
			File selectedFile = chooser.getSelectedFile();
			try {
				// need to get the exact name
				String fileName = selectedFile.getAbsolutePath();
				if (!fileName.endsWith("." + xmlExtension)) {
					fileName = fileName.concat("." + xmlExtension);
				}
				// save the file down to the workspace file
				// NOTE: the workspace file uses the current WORKSPACE
				// which is found in the state
				state.saveWorkspaceToFile(fileName);
				this.setMessage(resourceMap
						.getString("saveWorkspace.Action.success"));
			} catch (Exception err) {
				this.setMessage(resourceMap
						.getString("saveWorkspace.Action.fail")
						+ ":"
						+ err.getMessage());
			}
		} else {
			this.setMessage(resourceMap
					.getString("saveWorkspace.Action.cancel"));
		}
	}

	@Action(enabledProperty = "workspaceEmpty")
	public void createNewWDO() {
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		// Show a list of where this can be saved
//		CIKnownServerTable kst = CIKnownServerTable.getInstance();
//		Object[] options = new String[kst.ciKnownServerTableSize() + 1];
		Object[] options = new String[2];
		options[0] = resourceMap.getString("createNew.Action.locationOption1"); // default option, local file system
		options[1] = resourceMap.getString("createNew.Action.locationOption2"); // web server
//		int i = 0;
//		for (; i < kst.ciKnownServerTableSize(); i++)
//			options[i + 1] = (Object) kst.ciGetServerURL(i);

		String ns = null;
		String option = (String) JOptionPane.showInputDialog(this.getComponent(),
				resourceMap.getString("createNew.Action.locationConfirm"),
				resourceMap.getString("createNew.Action.locationTitle"), 
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (option != null && !option.isEmpty()) {
			if (option.equals((String) options[0])) {
				JFrame mainFrame = WdoApp.getApplication().getMainFrame();
				EditNamespace editNamespaceWindow = new EditNamespace(
						mainFrame,
						resourceMap.getString("createNewWDO.Action.confirm"));
				ns = editNamespaceWindow.getNamespace();
			} else {
				AlfrescoClient ac = getAlfrescoClient();
				ns = ac.createNode(null);
				// select the project and add a resource name
				// ns = CINewResourceNameDialog.showDialog(this.getComponent(),
				// this.getComponent(), ns);
//				ns = CINewResourceNameDialog.showDialog(this.getComponent(),
//						this.getComponent(), ns, CIClient.WDO_TYPE);
			}
		}

		if (ns != null && !ns.isEmpty()) {
			try {
				if (option.equals((String) options[1])) {
					State.getInstance().createWDO(ns, ns);
				}
				else {
					State.getInstance().createWDO(ns, null);	
				}
//				if (ns.toLowerCase().startsWith("http:"))
//					CIServerCache.getInstance().hashURL(ns);
				this.updateSelectedOWLDoc();
				this.setMessage(resourceMap
						.getString("createNewWDO.Action.success"));
			} catch (Exception ex) {
				this.setMessage(resourceMap.getString(
						"createNewWDO.Action.fail", ex.getMessage()));
			}
		} else {
			this.setMessage(resourceMap.getString("createNewWDO.Action.cancel"));
		}
	}

	@Action(enabledProperty = "workspaceLoaded")
	public void createNewWorkflow() {
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		// Show a list of where this can be saved
//		CIKnownServerTable kst = CIKnownServerTable.getInstance();
//		Object[] options = new String[kst.ciKnownServerTableSize() + 1];
		Object[] options = new String[2];
		options[0] = resourceMap.getString("createNew.Action.locationOption1"); // default option
		options[1] = resourceMap.getString("createNew.Action.locationOption2");
//		int i = 0;
//		options[0] = (Object) new String("Local Filesystem");
//		for (; i < kst.ciKnownServerTableSize(); i++)
//			options[i + 1] = (Object) kst.ciGetServerURL(i);
		
		String ns = null;
		String option = (String) JOptionPane.showInputDialog(this.getComponent(),
				resourceMap.getString("createNew.Action.locationConfirm"),
				resourceMap.getString("createNew.Action.locationTitle"), 
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		if (option != null && !option.isEmpty()) {
			if (option.equals((String) options[0])) {
				JFrame mainFrame = WdoApp.getApplication().getMainFrame();
				EditNamespace editNamespaceWindow = new EditNamespace(
						mainFrame,
						resourceMap
								.getString("createNewWorkflow.Action.confirm"));
				ns = editNamespaceWindow.getNamespace();
			} else {
				AlfrescoClient ac = getAlfrescoClient();
				ns = ac.createNode(null);
				// select the project and add a resource name
				// ns = CINewResourceNameDialog.showDialog(this.getComponent(),
				// this.getComponent(), ns);
				// AIDA, I replaced this line based on your edits to the wdo
				// project.
//				ns = CINewResourceNameDialog.showDialog(this.getComponent(),
//						this.getComponent(), ns, CIClient.SAW_TYPE);
			}
		}

		// TODO need to check for correct ns format, ex. no spaces allowed, no
		// special chars
		if (ns != null && !ns.isEmpty()) {
			State state = State.getInstance();
			if (option.equals((String) options[1])) {
				state.createWorkflow(ns, ns);
			}
			else {
				state.createWorkflow(ns, null);	
			}
//			if (ns.toLowerCase().startsWith("http:"))
//				CIServerCache.getInstance().hashURL(ns);
			this.updateSelectedOWLDoc();
			this.setMessage(resourceMap
					.getString("createNewWorkflow.Action.success"));
		} else {
			this.setMessage(resourceMap
					.getString("createNewWorkflow.Action.cancel"));
		}
	}

	@Action(enabledProperty = "workflowSelected")
	public void generatePMLAnnotator() {
		// Ask for confirmation of SAW to use for data annotators
		// set list of options for Select Workflow combo box:
		// SAWs currently loaded in the workspace, currently selected SAW as the
		// default
		// since, detailedBy references to self are not expected.
		HashMap<String, String> optionsHM = new HashMap<String, String>();
		State state = State.getInstance();
		for (Iterator<String> iter = state.listWorkflowURIs(); iter.hasNext();) {
			String uri = iter.next();
			optionsHM.put(Workspace.shortURI(uri), uri);
		}
		Object[] options = new String[optionsHM.size()];
		optionsHM.keySet().toArray(options);
		Arrays.sort(options);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		String option = (String) JOptionPane.showInputDialog(WdoApp
				.getApplication().getMainFrame(), "", resourceMap
				.getString("generatePMLAnnotator.Action.text.confirm"),
				JOptionPane.PLAIN_MESSAGE, null, options, Workspace
						.shortURI(state.getSelectedWorkflowURI()));

		// If a SAW was selected, initialize window for it, otherwise close
		// wizard
		if (option != null && !option.isEmpty()) {
			option = optionsHM.get(option); // get the URI
			if (!option.equals(state.getSelectedWorkflowURI())) {
				state.setSelectedWorkflow(option);
				updateSelectedOWLDoc();
			}
			CreateProvenanceAnnotators window = this
					.getCreateProvenanceAnnotatorsWindow();
			window.initWindow();
			WdoApp.getApplication().show(window);
		} else {
			this.setMessage(resourceMap
					.getString("generatePMLAnnotator.Action.cancel"));
		}
	}

	@Action(enabledProperty = "workspaceLoaded")
	public void generateReport() {
		JFrame mainFrame = WdoApp.getApplication().getMainFrame();
		WdoGenerateReport window = new WdoGenerateReport(mainFrame);
		window.setLocationRelativeTo(mainFrame);
		WdoApp.getApplication().show(window);
	}

	@Action(enabledProperty = "workspaceLoaded")
	public void harvestConcepts() {
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		// set list of options for harvest concepts from combo box:
		ArrayList<String> optionsAL = new ArrayList<String>();
		State state = State.getInstance();
		String basewdouri = state.getBaseWDOURI();
		for (Iterator<String> iter = state.listOntologyURIs(); iter.hasNext();) {
			String tempURI = iter.next();
			if (!basewdouri.equals(tempURI)) {
				optionsAL.add(tempURI);
			}
		}
		Object[] options = new String[optionsAL.size()];
		optionsAL.toArray(options);
		String option = (String) JOptionPane.showInputDialog(
				this.getComponent(), "",
				resourceMap.getString("harvestConcepts.Action.promptText"),
				JOptionPane.PLAIN_MESSAGE, null, options, null);
		if (option != null && !option.isEmpty()) {
			HarvestConcepts window = this.getHarvestConceptWindow();
			window.initWindow(option);
			WdoApp.getApplication().show(window);
			state.getBaseWDO().rebind();
			this.updateSelectedOWLDoc();
		} else {
			this.setMessage(resourceMap
					.getString("harvestConcepts.Action.cancel"));
		}
	}

	@Action(enabledProperty = "owlDocumentSelected")
	public void editOWLDocument() {
		EditOWLDocument window = this.getEditOWLDocumentWindow();
		window.initWindow();
		WdoApp.getApplication().show(window);
	}

	@Action(enabledProperty = "wdoConceptSelected")
	public void editWDOConcept() {
		EditWDOConcept window = this.getEditWDOConceptWindow();
		window.initWindow(false, State.getInstance().getSelectedClass());
		WdoApp.getApplication().show(window);
		this.updateSelectedOWLDoc();
	}

	@Action(enabledProperty = "sawInstanceSelected")
	public void editSAWInstance() {
		State state = State.getInstance();
		OntClass ind = state.getSelectedIndividual();
		if (SAW.isPMLSourceType(ind)) {
			EditSAWSource ess = this.getEditSAWSourceWindow();
			ess.initWindow(ind);
			WdoApp.getApplication().show(ess);
		} else if (SAW.isDataType(ind)) {
			EditSAWData esd = this.getEditSAWDataWindow();
			esd.initWindow(ind);
			WdoApp.getApplication().show(esd);
		} else if (SAW.isMethodType(ind)) {
			EditSAWMethod esm = this.getEditSAWMethodWindow();
			esm.initWindow(ind);
			WdoApp.getApplication().show(esm);
		}
		this.updateSelectedOWLDoc();
	}

	@Action(enabledProperty = "sawMethodInstanceSelected")
	public void editDetailedBy() {
		State state = State.getInstance();
		OntClass methodInd = state.getSelectedIndividual();
		if (SAW.isMethodType(methodInd)) {
			ResourceMap resourceMap = Application.getInstance(WdoApp.class)
					.getContext().getResourceMap(WdoView.class);
			// set list of options for detailedby combo box:
			// NONE_OPTION + NEW_OPTION + SAWs currently loaded in the
			// workspace, except currently selected SAW
			// since, detailedBy references to self are not expected.
			ArrayList<String> optionsAL = new ArrayList<String>();
			String DETAILED_BY_NONE_OPTION = resourceMap
					.getString("editDetailedBy.Action.Option.none");
			String DETAILED_BY_NEW_OPTION = resourceMap
					.getString("editDetailedBy.Action.Option.new");
			optionsAL.add(DETAILED_BY_NONE_OPTION);
			optionsAL.add(DETAILED_BY_NEW_OPTION);
			String sawURI = state.getSelectedOWLDocumentURI();
			for (Iterator<String> iter = state.listWorkflowURIs(); iter
					.hasNext();) {
				String tempURI = iter.next();
				if (!sawURI.equals(tempURI)) {
					optionsAL.add(tempURI);
				}
			}
			Object[] options = new String[optionsAL.size()];
			optionsAL.toArray(options);
			Ontology sawOnt = SAW.getDetailedBy(methodInd);
			String option = (String) JOptionPane.showInputDialog(
					this.getComponent(),
					"",
					resourceMap.getString("editDetailedBy.Action.text"),
					JOptionPane.PLAIN_MESSAGE,
					null,
					options,
					(sawOnt == null) ? DETAILED_BY_NONE_OPTION : sawOnt
							.getURI());

			if (option != null && !option.isEmpty()) {
				if (option.equalsIgnoreCase(DETAILED_BY_NONE_OPTION)) {
					state.removeDetailedBy(methodInd);
					this.setMessage(resourceMap
							.getString("editDetailedBy.Action.success"));
				} else if (option.equalsIgnoreCase(DETAILED_BY_NEW_OPTION)) {
					// add a new workflow, and set it as the detailed workflow
					// of this method
					String selectedWorkflowURI = state.getSelectedWorkflowURI();
					createNewWorkflow();
					// if the new workflow was created, then it is the new
					// selected workflow
					if (!selectedWorkflowURI.equals(state
							.getSelectedWorkflowURI())) {
						OntModel newsaw = state.getSelectedWorkflow();
						// restore previous selected workflow
						state.setSelectedWorkflow(selectedWorkflowURI);
						if (newsaw != null) {
							OntClass sawInd = SAW.getSAWSAWInstance(newsaw);
							state.setDetailedBy(methodInd, sawInd);
							this.setMessage(resourceMap
									.getString("editDetailedBy.Action.success"));
							updateSelectedOWLDoc();
						} else {
							this.setMessage(resourceMap
									.getString("editDetailedBy.Action.cancel"));
						}
					}
				}
				// set a new composition
				else {
					state.setDetailedBy(methodInd,
							SAW.getSAWSAWInstance(state.getOntModel(option)));
					this.setMessage(resourceMap
							.getString("editDetailedBy.Action.success"));
					updateSelectedOWLDoc();
				}
			} else {
				this.setMessage(resourceMap
						.getString("editDetailedBy.Action.cancel"));
			}
		}
	}

	@Action
	public Task<String, Void> openOWLFile() {
		return new LoadOWLDocumentTask(
				LoadOWLDocumentTask.Attempt.ASK_FILENAME, null);
	}

	@Action
	public Task<String, Void> openOWLURI() {
		State state = State.getInstance();
		// check to see if there is a uri that has been set to load
		String uri = state.getURIToLoad();
		// reset uri set to load, since it will be attempted here.
		state.setURIToLoad(null);
		return new LoadOWLDocumentTask(LoadOWLDocumentTask.Attempt.ASK_URI, uri);
	}

	@Action(enabledProperty = "wdoConceptSelected")
	public void removeWDOConcept() {
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		State state = State.getInstance();
		OntClass selectedClass = state.getSelectedClass();
		String localname = WDO.getClassQName(selectedClass);
		try {
			state.removeSelectedWDOClass();
			this.updateSelectedOWLDoc();
			this.updateProperties();
			this.setMessage(resourceMap.getString(
					"removeWDOConcept.Action.success", localname));
		} catch (Exception ex) {
			this.setMessage(resourceMap.getString(
					"removeWDOConcept.Action.fail", ex.getMessage()));
		}
	}

	@Action(enabledProperty = "selectedOWLDocumentModified")
	public Task<Void, Void> save() {
		State state = State.getInstance();
		OntModel ontmodel = state.getSelectedOWLDocument();
		if (ontmodel != null) {
			ArrayList<OntModel> ontmodelList = new ArrayList<OntModel>();
			ontmodelList.add(ontmodel);
			ArrayList<String> urlList = new ArrayList<String>();
			// if this is on a known server - just add the url
			// if it is in cache - then get server id
//			String uri = state.getOntModelURI(ontmodel);
//			int serverId = CIServerCache.getInstance().getServerFromURL(uri);
//			if (serverId >= 0) {
//				urlList.add(uri);
//			} else {
				urlList.add(state.getOWLDocumentURL(ontmodel));
//			}
//			return new SaveOWLDocumentTask(ontmodelList, urlList, false);
			return new SaveOWLDocumentTask(ontmodelList, urlList);
		}
		return null;
	}

	@Action(enabledProperty = "workspaceModified")
	public Task<Void, Void> saveAll() {
		State state = State.getInstance();
		ArrayList<OntModel> ontmodelList = new ArrayList<OntModel>();
		ArrayList<String> urlList = new ArrayList<String>();
		for (Iterator<String> i = state.listOntologyURIs(); i.hasNext();) {
			String uri = i.next();
			OntModel temp = state.getOntModel(uri);
			// only include ontologies that have been modified
			if (temp != null && state.isModified(uri)) {
				ontmodelList.add(temp);
//				int serverId = CIServerCache.getInstance()
//						.getServerFromURL(uri);
//				if (serverId >= 0) {
//					urlList.add(uri);
//				} else {
					urlList.add(state.getOWLDocumentURL(temp));
//				}
			}
		}
		for (Iterator<String> i = state.listWorkflowURIs(); i.hasNext();) {
			String uri = i.next();
			OntModel temp = state.getOntModel(uri);
			// only include workflows that have been modified
			if (temp != null && state.isModified(uri)) {
				ontmodelList.add(temp);
//				int serverId = CIServerCache.getInstance()
//						.getServerFromURL(uri);
//				if (serverId >= 0) {
//					urlList.add(uri);
//				} else {
					urlList.add(state.getOWLDocumentURL(temp));
//				}
			}
		}
		if (!ontmodelList.isEmpty()) {
//			return new SaveOWLDocumentTask(ontmodelList, urlList, false);
			return new SaveOWLDocumentTask(ontmodelList, urlList);
		} else {
			return null;
		}
	}

	@Action(enabledProperty = "owlDocumentSelected")
	public Task<Void, Void> saveAs() {
		State state = State.getInstance();
		OntModel ontmodel = state.getSelectedOWLDocument();
		if (ontmodel != null) {
			ArrayList<OntModel> ontmodelList = new ArrayList<OntModel>();
			ontmodelList.add(ontmodel);
			ArrayList<String> urlList = new ArrayList<String>();
			urlList.add(null);
			return new SaveOWLDocumentTask(ontmodelList, urlList);
			
//			ResourceMap resourceMap = Application.getInstance(WdoApp.class)
//					.getContext().getResourceMap(WdoView.class);
//			Object[] options = new String[2];
//			options[0] = resourceMap.getString("createNew.Action.locationOption1"); // default option
//			options[1] = resourceMap.getString("createNew.Action.locationOption2");
//			String ns = (String) JOptionPane.showInputDialog(this.getComponent(),
//					resourceMap.getString("createNew.Action.locationConfirm"),
//					resourceMap.getString("createNew.Action.locationTitle"), 
//					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
//			if (ns != null && !ns.isEmpty()) {
//				if (ns.equals((String) options[0])) {
//					urlList.add(null);
//				} else {
//					AlfrescoClient ac = getAlfrescoClient();
//					ns = ac.createNode();
//					urlList.add(ns);
//				}
//				return new SaveOWLDocumentTask(ontmodelList, urlList, true);
//			}			
		}
		return null;
	}

	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			aboutBox = new WdoAboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		WdoApp.getApplication().show(aboutBox);
	}

	@Action
	public void toggleMaxTabbedSpace() {
		if (this.maxTabButton.isSelected()) {
			mainSplitPane.setDividerLocation(0);
		} else {
			mainSplitPane.setDividerLocation(0.5);
		}
	}

	@Action
	public void toggleShowWorkflowTypes() {
		State state = State.getInstance();
		state.setShowWorkflowTypes(viewMenuShowType.isSelected());
		this.setWorkflow();
	}

	/**
	 * addBookmark Author: agandara1 Category: action function Description: adds
	 * the file to the list of bookmarks
	 * 
	 */
	@Action(enabledProperty = "owlDocumentSelected")
	public void addBookmark() {

		// get current state and appframework resource objects
		State state = State.getInstance();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);

		// determine the currently selected uri, to be added as a bookmark
		// and convert it to a url
		String selectedOWL = state.getSelectedOWLDocumentURI();
		OntModel selecom = state.getOntModel(selectedOWL);
		String selo = state.getOWLDocumentURL(selecom);

		// add the bookmark to the bookmark model and reset the menu
		if ((selo != null) && !selo.isEmpty()) {
			try {
				Bookmarks bm = new Bookmarks();
				bm.addBookmark(selo);
				resetBookmarksMenu();
			} catch (IOException e) {
				this.setMessage(resourceMap
						.getString("addBookmark.Action.fail")
						+ ":"
						+ e.getMessage());
			}
		} else
			this.setMessage(resourceMap.getString("addBookmark.Action.fail")
					+ ": could not determine URL.");
	}

	@Action(enabledProperty = "workflowSelected")
	public void showWFTalkWindow() {
		// TODO a finer property is needed to show when a SAW instance is
		// selected, similar to wdoConceptSelected
		WFTalkFrame.showWindow();
	}

	@Action
	public void showCIDesktop() {
//		CIDesktop.showWindow();
	}

	// ////////////////// END ACTION METHODS ////////////////////

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
//		selectedServerLabel = new javax.swing.JLabel("default");
//		selectedProjectLabel = new javax.swing.JLabel("default");
		
		mainPanel = new javax.swing.JPanel();
		mainSplitPane = new javax.swing.JSplitPane();
		workspaceHorizontalSplitPane = new javax.swing.JSplitPane();
		loadedOWLDocsPanel = new javax.swing.JPanel();
		loadedOWLDocsLabel = new javax.swing.JLabel();
		loadedOWLDocsScrollPane = new javax.swing.JScrollPane();
		loadedOWLDocs = new OntDocumentManagerJTree();
		dataMethodSplitPane = new javax.swing.JSplitPane();
		dataPanel = new javax.swing.JPanel();
		dataScrollPane = new javax.swing.JScrollPane();
		dataHierarchy = new OntModelJTree();
		dataLabel = new javax.swing.JLabel();
		methodPanel = new javax.swing.JPanel();
		methodScrollPane = new javax.swing.JScrollPane();
		methodHierarchy = new OntModelJTree();
		methodLabel = new javax.swing.JLabel();
		maxTabButton = new javax.swing.JToggleButton();
		tabbedPane = new javax.swing.JTabbedPane();
		tabbedSpacePanel = new javax.swing.JPanel();
		runsTab = new RunsJPanel();
		ontologyTab = new javax.swing.JPanel();
		workflowTab = new javax.swing.JPanel();
		workflowScrollPane = new SAWScrollPane();
		menuBar = new javax.swing.JMenuBar();
		javax.swing.JMenu fileMenu = new javax.swing.JMenu();
		fileMenuOpenFile = new javax.swing.JMenuItem();
		fileMenuOpenURI = new javax.swing.JMenuItem();
		fileMenuSeparator1 = new javax.swing.JSeparator();
		fileMenuCreateNewWDO = new javax.swing.JMenuItem();
		fileMenuCreateNewWorkflow = new javax.swing.JMenuItem();
		fileMenuSeparator2 = new javax.swing.JSeparator();
		fileMenuExportWorkflow = new javax.swing.JMenu();
		fileMenuExportAnnotator = new javax.swing.JMenuItem();
		fileMenuHarvestConcepts = new javax.swing.JMenuItem();
		fileMenuSeparator3 = new javax.swing.JSeparator();
		fileMenuSave = new javax.swing.JMenuItem();
		fileMenuSaveAs = new javax.swing.JMenuItem();
		fileMenuSaveAll = new javax.swing.JMenuItem();
		fileMenuSeparator4 = new javax.swing.JSeparator();
		fileMenuOpenWorkspace = new javax.swing.JMenuItem();
		fileMenuSaveWorkspace = new javax.swing.JMenuItem();
		fileMenuCloseWorkspace = new javax.swing.JMenuItem();
		fileMenuSeparator5 = new javax.swing.JSeparator();

		// Menu items specific for CI Server connection
		fileMenuCIServer = new javax.swing.JMenu();
		ciConnectToServer = new javax.swing.JMenuItem();
		ciCreateProject = new javax.swing.JMenuItem();
		ciCheckOutProject = new javax.swing.JMenuItem();
		ciCheckInProject = new javax.swing.JMenuItem();
		ciOpenProject = new javax.swing.JMenuItem();
		ciAddSawToProject = new javax.swing.JMenuItem();
		ciSaveProject = new javax.swing.JMenuItem();
		ciCloseProject = new javax.swing.JMenuItem();
		ciDisconnectFromServer = new javax.swing.JMenuItem();
		fileMenuCISeparator1 = new javax.swing.JSeparator();
		fileMenuCISeparator2 = new javax.swing.JSeparator();

		fileMenuExit = new javax.swing.JMenuItem();
		// bookmark menu
		bookmarksMenu = new javax.swing.JMenu();
		bookmarksMenuaddBookmark = new javax.swing.JMenuItem();
		bookmarksMenuSeparator1 = new javax.swing.JSeparator();
		// view menu
		viewMenu = new javax.swing.JMenu();
		viewMenuShowType = new javax.swing.JCheckBoxMenuItem();
		toolsMenu = new javax.swing.JMenu();
		toolsMenuGenerateReport = new javax.swing.JMenuItem();
		javax.swing.JMenu helpMenu = new javax.swing.JMenu();
		javax.swing.JMenuItem helpMenuAbout = new javax.swing.JMenuItem();
		statusPanel = new javax.swing.JPanel();
		javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
		statusMessageLabel = new javax.swing.JLabel();
		statusAnimationLabel = new javax.swing.JLabel();
		progressBar = new javax.swing.JProgressBar();
		toolBar = new javax.swing.JToolBar();
		toolBarAddConcept = new javax.swing.JButton();
		toolBarCreateNewWDO = new javax.swing.JButton();
		toolBarCreateNewWorkflow = new javax.swing.JButton();
		toolBarOpenFile = new javax.swing.JButton();
		toolBarRemoveConcept = new javax.swing.JButton();
		toolBarEditConcept = new javax.swing.JButton();
		toolBarEditInstance = new javax.swing.JButton();
		toolBarSave = new javax.swing.JButton();
		toolBarSaveAll = new javax.swing.JButton();
//		toolBarWFTalk = new javax.swing.JButton();
//		toolBarCIDesktop = new javax.swing.JButton();
		toolBarSeparator1 = new javax.swing.JToolBar.Separator();
		toolBarSeparator2 = new javax.swing.JToolBar.Separator();

		mainPanel.setName("mainPanel"); // NOI18N

		mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setName("mainSplitPane"); // NOI18N

		tabbedSpacePanel.setName("tabbedSpacePanel"); // NOI18N

		workspaceHorizontalSplitPane.setDividerLocation(200);
		workspaceHorizontalSplitPane.setName("workspaceHorizontalSplitPane"); // NOI18N

		loadedOWLDocsPanel.setBorder(new javax.swing.border.SoftBevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		loadedOWLDocsPanel.setName("loadedOWLDocsPanel"); // NOI18N

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		loadedOWLDocsLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		loadedOWLDocsLabel.setText(resourceMap
				.getString("loadedOWLDocsLabel.text")); // NOI18N
		loadedOWLDocsLabel.setName("loadedOWLDocsLabel"); // NOI18N

		loadedOWLDocsScrollPane.setName("loadedOWLDocsScrollPane"); // NOI18N

		loadedOWLDocs.setName("loadedOWLDocs"); // NOI18N
		loadedOWLDocsScrollPane.setViewportView(loadedOWLDocs);

		javax.swing.GroupLayout loadedOWLDocsPanelLayout = new javax.swing.GroupLayout(
				loadedOWLDocsPanel);
		loadedOWLDocsPanel.setLayout(loadedOWLDocsPanelLayout);
		loadedOWLDocsPanelLayout.setHorizontalGroup(loadedOWLDocsPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(loadedOWLDocsLabel,
						javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 193,
						Short.MAX_VALUE)
				.addComponent(loadedOWLDocsScrollPane,
						javax.swing.GroupLayout.DEFAULT_SIZE, 193,
						Short.MAX_VALUE));
		loadedOWLDocsPanelLayout
				.setVerticalGroup(loadedOWLDocsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								loadedOWLDocsPanelLayout
										.createSequentialGroup()
										.addComponent(
												loadedOWLDocsLabel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												14,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												loadedOWLDocsScrollPane,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												23, Short.MAX_VALUE)));

		workspaceHorizontalSplitPane.setLeftComponent(loadedOWLDocsPanel);

		dataMethodSplitPane.setDividerLocation(375);
		dataMethodSplitPane.setName("dataMethodSplitPane"); // NOI18N
		dataMethodSplitPane.setBorder(new javax.swing.border.EmptyBorder(0, 0,
				0, 0));

		dataPanel.setBorder(new javax.swing.border.SoftBevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		dataPanel.setName("dataPanel"); // NOI18N

		dataScrollPane.setName("dataScrollPane"); // NOI18N

		dataHierarchy.setName("dataHierarchy"); // NOI18N
		dataScrollPane.setViewportView(dataHierarchy);

		dataLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		dataLabel.setText(resourceMap.getString("dataLabel.text")); // NOI18N
		dataLabel.setName("dataLabel"); // NOI18N

		javax.swing.GroupLayout dataPanelLayout = new javax.swing.GroupLayout(
				dataPanel);
		dataPanel.setLayout(dataPanelLayout);
		dataPanelLayout.setHorizontalGroup(dataPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(dataLabel,
						javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 368,
						Short.MAX_VALUE)
				.addComponent(dataScrollPane,
						javax.swing.GroupLayout.DEFAULT_SIZE, 368,
						Short.MAX_VALUE));
		dataPanelLayout
				.setVerticalGroup(dataPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								dataPanelLayout
										.createSequentialGroup()
										.addComponent(
												dataLabel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												14,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												dataScrollPane,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												23, Short.MAX_VALUE)));

		dataMethodSplitPane.setLeftComponent(dataPanel);

		methodPanel.setBorder(new javax.swing.border.SoftBevelBorder(
				javax.swing.border.BevelBorder.RAISED));
		methodPanel.setName("methodPanel"); // NOI18N

		methodScrollPane.setName("methodScrollPane"); // NOI18N

		methodHierarchy.setName("methodHierarchy"); // NOI18N
		methodScrollPane.setViewportView(methodHierarchy);

		methodLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		methodLabel.setText(resourceMap.getString("methodLabel.text")); // NOI18N
		methodLabel.setName("methodLabel"); // NOI18N

		javax.swing.GroupLayout methodPanelLayout = new javax.swing.GroupLayout(
				methodPanel);
		methodPanel.setLayout(methodPanelLayout);
		methodPanelLayout.setHorizontalGroup(methodPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(methodLabel,
						javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 246,
						Short.MAX_VALUE)
				.addComponent(methodScrollPane,
						javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 246,
						Short.MAX_VALUE));
		methodPanelLayout
				.setVerticalGroup(methodPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								methodPanelLayout
										.createSequentialGroup()
										.addComponent(methodLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												methodScrollPane,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												23, Short.MAX_VALUE)));

		dataMethodSplitPane.setRightComponent(methodPanel);

		workspaceHorizontalSplitPane.setRightComponent(dataMethodSplitPane);

		mainSplitPane.setLeftComponent(workspaceHorizontalSplitPane);
		mainSplitPane.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(WdoView.class, this);
		maxTabButton.setAction(actionMap.get("toggleMaxTabbedSpace"));
		maxTabButton.setFocusable(false);
		maxTabButton.setName("maxTabButton"); // NOI18N

		tabbedPane.setMinimumSize(new java.awt.Dimension(60, 44));
		tabbedPane.setName("tabbedPane"); // NOI18N
		tabbedPane.setEnabled(false); // disabled at startup

		ontologyTab.setName("ontologyTab"); // NOI18N

		workflowTab.setName("workflowTab"); // NOI18N

		workflowScrollPane.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap.getString(
						"workflowScrollPane.border.title", ""))); // NOI18N
		workflowScrollPane.setName("workflowScrollPane"); // NOI18N

		javax.swing.GroupLayout workflowTabLayout = new javax.swing.GroupLayout(
				workflowTab);
		workflowTab.setLayout(workflowTabLayout);
		workflowTabLayout.setHorizontalGroup(workflowTabLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(workflowScrollPane,
						javax.swing.GroupLayout.DEFAULT_SIZE, 646,
						Short.MAX_VALUE));
		workflowTabLayout.setVerticalGroup(workflowTabLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(workflowScrollPane,
						javax.swing.GroupLayout.DEFAULT_SIZE, 347,
						Short.MAX_VALUE));

		tabbedPane.addTab(
				resourceMap.getString("workflowTab.TabConstraints.tabTitle"),
				workflowTab); // NOI18N

		runsTab.setName("runsTab");
		// disabled for now. Leo
		// tabbedPane.addTab(resourceMap.getString("runsTab.TabConstraints.tabTitle"),
		// runsTab);

		javax.swing.GroupLayout tabbedSpacePanelLayout = new javax.swing.GroupLayout(
				tabbedSpacePanel);
		tabbedSpacePanel.setLayout(tabbedSpacePanelLayout);
		tabbedSpacePanelLayout
				.setHorizontalGroup(tabbedSpacePanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								tabbedSpacePanelLayout
										.createSequentialGroup()
										.addContainerGap(669, Short.MAX_VALUE)
										.addComponent(
												maxTabButton,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												20,
												javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
								tabbedSpacePanelLayout
										.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(
												tabbedPane,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												685, Short.MAX_VALUE)));
		tabbedSpacePanelLayout.setVerticalGroup(tabbedSpacePanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						tabbedSpacePanelLayout
								.createSequentialGroup()
								.addComponent(maxTabButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										20,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(418, Short.MAX_VALUE))
				.addGroup(
						tabbedSpacePanelLayout.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(tabbedPane,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										435, Short.MAX_VALUE)));

		mainSplitPane.setRightComponent(tabbedSpacePanel);

		javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(
				mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				mainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 653,
				Short.MAX_VALUE));
		mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				mainSplitPane, javax.swing.GroupLayout.Alignment.TRAILING,
				javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE));

		menuBar.setName("menuBar"); // NOI18N

		fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
		fileMenu.setName("fileMenu"); // NOI18N

		fileMenuOpenFile.setAction(actionMap.get("openOWLFile"));
		fileMenuOpenFile.setName("fileMenuOpenFile"); // NOI18N
		fileMenu.add(fileMenuOpenFile);

		fileMenuOpenURI.setAction(actionMap.get("openOWLURI"));
		fileMenuOpenURI.setName("fileMenuOpenURI"); // NOI18N
		fileMenu.add(fileMenuOpenURI);

		fileMenuSeparator1.setName("fileMenuSeparator1"); // NOI18N
		fileMenu.add(fileMenuSeparator1);

		fileMenuCreateNewWDO.setAction(actionMap.get("createNewWDO"));
		fileMenuCreateNewWDO.setName("fileMenuCreateNewWDO"); // NOI18N
		fileMenu.add(fileMenuCreateNewWDO);

		fileMenuCreateNewWorkflow.setAction(actionMap.get("createNewWorkflow"));
		fileMenuCreateNewWorkflow.setName("fileMenuCreateNewWorkflow"); // NOI18N
		fileMenu.add(fileMenuCreateNewWorkflow);

		fileMenuSeparator2.setName("fileMenuSeparator2"); // NOI18N
		fileMenu.add(fileMenuSeparator2);

		fileMenuExportWorkflow.setText(resourceMap
				.getString("fileMenuExportWorkflow.text")); // NOI18N
		fileMenuExportWorkflow.setName("fileMenuExportWorkflow"); // NOI18N

		fileMenuExportAnnotator
				.setAction(actionMap.get("generatePMLAnnotator"));
		fileMenuExportAnnotator.setName("fileMenuExportAnnotator"); // NOI18N
		fileMenuExportWorkflow.add(fileMenuExportAnnotator);

		fileMenu.add(fileMenuExportWorkflow);

		fileMenuHarvestConcepts.setAction(actionMap.get("harvestConcepts"));
		fileMenuHarvestConcepts.setName("fileMenuHarvestConcepts"); // NOI18N

		fileMenu.add(fileMenuHarvestConcepts);

		// fileMenuExportWrapper.setAction(actionMap.get("generatePMLWrapper"));
		// fileMenuExportWrapper.setName("fileMenuExportWrapper"); // NOI18N
		// fileMenuExportWorkflow.add(fileMenuExportWrapper);

		fileMenuSeparator3.setName("fileMenuSeparator3"); // NOI18N
		fileMenu.add(fileMenuSeparator3);

		fileMenuSave.setAction(actionMap.get("save"));
		fileMenuSave.setName("fileMenuSave"); // NOI18N
		fileMenu.add(fileMenuSave);

		fileMenuSaveAs.setAction(actionMap.get("saveAs"));
		fileMenuSaveAs.setName("fileMenuSaveAs"); // NOI18N
		fileMenu.add(fileMenuSaveAs);

		fileMenuSaveAll.setAction(actionMap.get("saveAll"));
		fileMenuSaveAll.setName("fileMenuSaveAll"); // NOI18N
		fileMenu.add(fileMenuSaveAll);

		fileMenuSeparator4.setName("fileMenuSeparator4"); // NOI18N
		fileMenu.add(fileMenuSeparator4);

		fileMenuOpenWorkspace.setAction(actionMap.get("openWorkspace"));
		fileMenuOpenWorkspace.setName("fileMenuOpenWorkspace"); // NOI18N
		fileMenu.add(fileMenuOpenWorkspace);

		fileMenuSaveWorkspace.setAction(actionMap.get("saveWorkspace"));
		fileMenuSaveWorkspace.setName("fileMenuSaveWorkspace"); // NOI18N
		fileMenu.add(fileMenuSaveWorkspace);

		fileMenuCloseWorkspace.setAction(actionMap.get("closeWorkspace"));
		fileMenuCloseWorkspace.setName("fileMenuCloseWorkspace"); // NOI18N
		fileMenu.add(fileMenuCloseWorkspace);

		fileMenuCIServer
				.setText(resourceMap.getString("fileMenuCIServer.text")); // NOI18N
		fileMenuCIServer.setName("ciServerMenu");

		ciConnectToServer.setAction(actionMap.get("connectToCIServer"));
		ciConnectToServer.setName("ciConnectToServer"); // NOI18N
		fileMenuCIServer.add(ciConnectToServer);

		ciDisconnectFromServer.setAction(actionMap
				.get("disconnectFromCIServer"));
		ciDisconnectFromServer.setName("ciDisconnectFromServer"); // NOI18N
		fileMenuCIServer.add(ciDisconnectFromServer);

		fileMenuCIServer.add(fileMenuCISeparator1);

		ciCreateProject.setAction(actionMap.get("createCIProject"));
		ciCreateProject.setName("ciCreateProject"); // NOI18N
		fileMenuCIServer.add(ciCreateProject);

		ciOpenProject.setAction(actionMap.get("openCIProject"));
		ciOpenProject.setName("ciOpenProject"); // NOI18N
		fileMenuCIServer.add(ciOpenProject);

		ciAddSawToProject.setAction(actionMap.get("addSawToCIProject"));
		ciAddSawToProject.setName("ciAddSawToProject"); // NOI18N
		fileMenuCIServer.add(ciAddSawToProject);

		ciSaveProject.setAction(actionMap.get("saveCIProject"));
		ciSaveProject.setName("ciSaveProject"); // NOI18N
		fileMenuCIServer.add(ciSaveProject);

		ciCloseProject.setAction(actionMap.get("closeCIProject"));
		ciCloseProject.setName("ciCloseProject"); // NOI18N
		fileMenuCIServer.add(ciCloseProject);

		fileMenuCIServer.add(fileMenuCISeparator2);

		ciCheckOutProject.setAction(actionMap.get("checkOutCIProject"));
		ciCheckOutProject.setName("ciCheckOutProject"); // NOI18N
		fileMenuCIServer.add(ciCheckOutProject);

		ciCheckInProject.setAction(actionMap.get("checkInCIProject"));
		ciCheckInProject.setName("ciCheckInProject"); // NOI18N
		fileMenuCIServer.add(ciCheckInProject);

		// TODO: add this later, when more functionality is available
		// fileMenu.add(fileMenuCIServer);

		fileMenuSeparator5.setName("fileMenuSeparator5"); // NOI18N
		fileMenu.add(fileMenuSeparator5);

		fileMenuExit.setAction(actionMap.get("quit")); // NOI18N
		fileMenuExit.setToolTipText(resourceMap
				.getString("fileMenuExit.toolTipText")); // NOI18N
		fileMenuExit.setName("fileMenuExit"); // NOI18N
		fileMenu.add(fileMenuExit);

		menuBar.add(fileMenu);

		bookmarksMenu.setText(resourceMap.getString("bookmarksMenu.text")); // NOI18N
		bookmarksMenu.setName("bookmarksMenu"); // NOI18N

		bookmarksMenuaddBookmark.setAction(actionMap.get("addBookmark"));
		bookmarksMenuaddBookmark.setText(resourceMap
				.getString("bookmarksMenuaddBookmark.text")); // NOI18N
		bookmarksMenuaddBookmark.setName("bookmarksMenuaddBookmark"); // NOI18N
		bookmarksMenu.add(bookmarksMenuaddBookmark);

		bookmarksMenuSeparator1.setName("bookmarksMenuSeparator1"); // NOI18N
		bookmarksMenu.add(bookmarksMenuSeparator1);

		try {
			setupBookmarksMenu();
		} catch (IOException e) {
			// ignore exception here - menu is just not setup
		}

		menuBar.add(bookmarksMenu);

		viewMenu.setText(resourceMap.getString("viewMenu.text")); // NOI18N
		viewMenu.setName("viewMenu"); // NOI18N

		viewMenuShowType.setAction(actionMap.get("toggleShowWorkflowTypes")); // NOI18N
		viewMenuShowType.setSelected(true);
		viewMenuShowType.setName("viewMenuShowType"); // NOI18N
		viewMenu.add(viewMenuShowType);

		menuBar.add(viewMenu);

		toolsMenu.setText(resourceMap.getString("toolsMenu.text")); // NOI18N
		toolsMenu.setName("toolsMenu"); // NOI18N

		toolsMenuGenerateReport.setAction(actionMap.get("generateReport"));
		toolsMenuGenerateReport.setName("toolsMenuGenerateReport"); // NOI18N
		toolsMenu.add(toolsMenuGenerateReport);

		menuBar.add(toolsMenu);

		helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
		helpMenu.setName("helpMenu"); // NOI18N

		helpMenuAbout.setAction(actionMap.get("showAboutBox")); // NOI18N
		helpMenuAbout.setName("helpMenuAbout"); // NOI18N
		helpMenu.add(helpMenuAbout);

		menuBar.add(helpMenu);

		statusPanel.setName("statusPanel"); // NOI18N

		statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

		statusMessageLabel.setName("statusMessageLabel"); // NOI18N

		statusAnimationLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

		progressBar.setName("progressBar"); // NOI18N

		javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(
				statusPanel);
		statusPanel.setLayout(statusPanelLayout);
		statusPanelLayout
				.setHorizontalGroup(statusPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(statusPanelSeparator,
								javax.swing.GroupLayout.DEFAULT_SIZE, 653,
								Short.MAX_VALUE)
						.addGroup(
								statusPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(statusMessageLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												483, Short.MAX_VALUE)
										.addComponent(
												progressBar,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(statusAnimationLabel)
										.addContainerGap()));
		statusPanelLayout
				.setVerticalGroup(statusPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								statusPanelLayout
										.createSequentialGroup()
										.addComponent(
												statusPanelSeparator,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												2,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(
												statusPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																statusMessageLabel)
														.addComponent(
																statusAnimationLabel)
														.addComponent(
																progressBar,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(3, 3, 3)));

		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.setName("toolBar"); // NOI18N

		toolBarCreateNewWDO.setAction(actionMap.get("createNewWDO"));
		toolBarCreateNewWDO.setText(null);
		toolBarCreateNewWDO.setFocusable(false);
		toolBarCreateNewWDO.setMaximumSize(new java.awt.Dimension(27, 27));
		toolBarCreateNewWDO.setMinimumSize(new java.awt.Dimension(27, 27));
		toolBarCreateNewWDO.setName("toolBarCreateNewWDO"); // NOI18N
		toolBar.add(toolBarCreateNewWDO);

		toolBarCreateNewWorkflow.setAction(actionMap.get("createNewWorkflow"));
		toolBarCreateNewWorkflow.setText(null);
		toolBarCreateNewWorkflow.setFocusable(false);
		toolBarCreateNewWorkflow.setMaximumSize(new java.awt.Dimension(27, 27));
		toolBarCreateNewWorkflow.setMinimumSize(new java.awt.Dimension(27, 27));
		toolBarCreateNewWorkflow.setName("toolBarCreateNewWorkflow"); // NOI18N
		toolBar.add(toolBarCreateNewWorkflow);

		toolBarOpenFile.setAction(actionMap.get("openOWLFile"));
		toolBarOpenFile.setText(null);
		toolBarOpenFile.setFocusable(false);
		toolBarOpenFile.setMaximumSize(new java.awt.Dimension(27, 27));
		toolBarOpenFile.setMinimumSize(new java.awt.Dimension(27, 27));
		toolBarOpenFile.setName("toolBarOpenFile"); // NOI18N
		toolBar.add(toolBarOpenFile);

		toolBarSave.setAction(actionMap.get("save"));
		toolBarSave.setText(null);
		toolBarSave.setFocusable(false);
		toolBarSave.setMaximumSize(new java.awt.Dimension(27, 27));
		toolBarSave.setMinimumSize(new java.awt.Dimension(27, 27));
		toolBarSave.setName("toolBarSave"); // NOI18N
		toolBar.add(toolBarSave);

		toolBarSaveAll.setAction(actionMap.get("saveAll"));
		toolBarSaveAll.setText(null);
		toolBarSaveAll.setFocusable(false);
		toolBarSaveAll.setMaximumSize(new java.awt.Dimension(27, 27));
		toolBarSaveAll.setMinimumSize(new java.awt.Dimension(27, 27));
		toolBarSaveAll.setName("toolBarSaveAll"); // NOI18N
		toolBar.add(toolBarSaveAll);

		toolBarSeparator1.setName("toolBarSeparator1"); // NOI18N
		toolBar.add(toolBarSeparator1);

		toolBarAddConcept.setAction(actionMap.get("addWDOConcept"));
		toolBarAddConcept.setText(null);
		toolBarAddConcept.setFocusable(false);
		toolBarAddConcept.setMaximumSize(new java.awt.Dimension(27, 27));
		toolBarAddConcept.setMinimumSize(new java.awt.Dimension(27, 27));
		toolBarAddConcept.setName("toolBarAddConcept"); // NOI18N
		toolBar.add(toolBarAddConcept);

		toolBarRemoveConcept.setAction(actionMap.get("removeWDOConcept"));
		toolBarRemoveConcept.setText(null);
		toolBarRemoveConcept.setFocusable(false);
		toolBarRemoveConcept.setMaximumSize(new java.awt.Dimension(27, 27));
		toolBarRemoveConcept.setMinimumSize(new java.awt.Dimension(27, 27));
		toolBarRemoveConcept.setName("toolBarRemoveConcept"); // NOI18N
		toolBar.add(toolBarRemoveConcept);

		toolBarEditConcept.setAction(actionMap.get("editWDOConcept"));
		toolBarEditConcept.setText(null);
		toolBarEditConcept.setFocusable(false);
		toolBarEditConcept.setMaximumSize(new java.awt.Dimension(27, 27));
		toolBarEditConcept.setMinimumSize(new java.awt.Dimension(27, 27));
		toolBarEditConcept.setName("toolBarEditConcept"); // NOI18N
		toolBar.add(toolBarEditConcept);

		toolBarSeparator2.setName("toolBarSeparator2"); // NOI18N
		toolBar.add(toolBarSeparator2);

		toolBarEditInstance.setAction(actionMap.get("editSAWInstance"));
		toolBarEditInstance.setText(null);
		toolBarEditInstance.setFocusable(false);
		toolBarEditInstance.setMaximumSize(new java.awt.Dimension(27, 27));
		toolBarEditInstance.setMinimumSize(new java.awt.Dimension(27, 27));
		toolBarEditInstance.setName("toolBarEditInstance"); // NOI18N
		toolBar.add(toolBarEditInstance);

//		toolBarCIDesktop.setAction(actionMap.get("showCIDesktop"));
//		toolBarCIDesktop.setText(null);
//		toolBarCIDesktop.setFocusable(false);
//		toolBarCIDesktop.setMaximumSize(new java.awt.Dimension(27, 27));
//		toolBarCIDesktop.setMinimumSize(new java.awt.Dimension(27, 27));
//		toolBarCIDesktop.setName("toolBarCIDesktop"); // NOI18N
//		toolBar.add(toolBarCIDesktop);
//
//		toolBarWFTalk.setAction(actionMap.get("showWFTalkWindow"));
//		toolBarWFTalk.setText(null);
//		toolBarWFTalk.setFocusable(false);
//		toolBarWFTalk.setMaximumSize(new java.awt.Dimension(27, 27));
//		toolBarWFTalk.setMinimumSize(new java.awt.Dimension(27, 27));
//		toolBarWFTalk.setName("toolBarWFTalk"); // NOI18N
//		toolBar.add(toolBarWFTalk);

		setComponent(mainPanel);
		setMenuBar(menuBar);
		setStatusBar(statusPanel);
		setToolBar(toolBar);

		// set the drag sources of this application
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.addDragSourceListener(new OntologyTreeDragSourceListener());
	}

	// ////////////////// UTILITY METHODS /////////////////////////////
	// These methods update GUI components in this window,
	// and are usually called by Action, Task, and Listener methods
	// ////////////////////////////////////////////////////////////////
	/**
	 * Maintains the selected item in the tree specified, and clears selection
	 * of other concept hierarchy trees in this window.
	 * 
	 * @param tree
	 */
	public void clearOtherHierarchySelections(OntModelJTree tree) {
		OntModelJTree[] trees = { this.dataHierarchy, this.methodHierarchy };
		for (int i = 0; i < trees.length; i++) {
			if (!tree.equals(trees[i])) {
				trees[i].clearSelection();
			}
		}
		this.updateProperties();
	}

	private HarvestConcepts getHarvestConceptWindow() {
		if (harvestConceptWindow == null) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			harvestConceptWindow = new HarvestConcepts(mainFrame);
			harvestConceptWindow.setLocationRelativeTo(mainFrame);
		}
		return (HarvestConcepts) harvestConceptWindow;
	}

	/**
	 * Gets instance of the EditWDOConcept window. If first time called,
	 * initialize, otherwise just pass reference.
	 * 
	 * @return
	 */
	private EditWDOConcept getEditWDOConceptWindow() {
		if (editWDOConceptWindow == null) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			editWDOConceptWindow = new EditWDOConcept(mainFrame);
			editWDOConceptWindow.setLocationRelativeTo(mainFrame);
		}
		return (EditWDOConcept) editWDOConceptWindow;
	}

	/**
	 * Gets instance of the EditOWLDocument window. If first time called,
	 * initialize, otherwise just pass reference.
	 * 
	 * @return
	 */
	private EditOWLDocument getEditOWLDocumentWindow() {
		if (editOWLDocumentWindow == null) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			editOWLDocumentWindow = new EditOWLDocument(mainFrame);
			editOWLDocumentWindow.setLocationRelativeTo(mainFrame);
		}
		return (EditOWLDocument) editOWLDocumentWindow;
	}

	/**
	 * Gets instance of the EditSAWData window. If first time called,
	 * initialize, otherwise just pass reference.
	 * 
	 * @return
	 */
	private EditSAWData getEditSAWDataWindow() {
		if (editSAWDataWindow == null) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			editSAWDataWindow = new EditSAWData(mainFrame);
			editSAWDataWindow.setLocationRelativeTo(mainFrame);
		}
		return (EditSAWData) editSAWDataWindow;
	}

	/**
	 * Gets instance of the EditSAWMethod window. If first time called,
	 * initialize, otherwise just pass reference.
	 * 
	 * @return
	 */
	private EditSAWMethod getEditSAWMethodWindow() {
		if (editSAWMethodWindow == null) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			editSAWMethodWindow = new EditSAWMethod(mainFrame);
			editSAWMethodWindow.setLocationRelativeTo(mainFrame);
		}
		return (EditSAWMethod) editSAWMethodWindow;
	}

	/**
	 * Gets instance of the EditSAWSource window. If first time called,
	 * initialize, otherwise just pass reference.
	 * 
	 * @return
	 */
	private EditSAWSource getEditSAWSourceWindow() {
		if (editSAWSourceWindow == null) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			editSAWSourceWindow = new EditSAWSource(mainFrame);
			editSAWSourceWindow.setLocationRelativeTo(mainFrame);
		}
		return (EditSAWSource) editSAWSourceWindow;
	}

	/**
	 * Gets instance of the CreateProvenanceAnnotators window. If first time
	 * called, initialize, otherwise just pass reference.
	 * 
	 * @return
	 */
	private CreateProvenanceAnnotators getCreateProvenanceAnnotatorsWindow() {
		if (createProvenanceAnnotatorsWindow == null) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			createProvenanceAnnotatorsWindow = new CreateProvenanceAnnotators(
					mainFrame);
			createProvenanceAnnotatorsWindow.setLocationRelativeTo(mainFrame);
		}
		return (CreateProvenanceAnnotators) createProvenanceAnnotatorsWindow;
	}
	
	/**
	 * Gets instance of the AlfrescoClient window. If first time called, initialize.
	 * @return
	 */
	public AlfrescoClient getAlfrescoClient() {
		if (aClient == null) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			aClient = new AlfrescoClient(mainFrame);
			aClient.setLocationRelativeTo(mainFrame);
		}
		return aClient;
	}

	/**
	 * Set a message in the status bar
	 * 
	 * @param msg
	 */
	public void setMessage(String msg) {
		if (msg != null && !msg.isEmpty()) {
			statusMessageLabel.setText(msg);
			messageTimer.restart();
		}
	}

	/**
	 * Update all the properties of the GUI according to state
	 */
	public void updateProperties() {
		this.setOwlDocumentSelected();
		this.setSelectedOWLDocumentModified();
		this.setWdoConceptSelected();
		this.setSawInstanceSelected();
		this.setSawMethodInstanceSelected();
		this.setRemovableSawInstanceSelected();
		this.setWorkflowSelected();
		this.setWorkspaceLoaded();
		this.setWorkspaceModified();
		this.setCiServerConnected();
	}

	/**
	 * Reset the GUI components of this window that reflect the state of the
	 * workspace - This should be called after resetting the State's workspace.
	 */
	private void resetGUI() {
		this.updateProperties();
		this.loadedOWLDocs = new OntDocumentManagerJTree();
		this.loadedOWLDocsScrollPane.setViewportView(loadedOWLDocs);
		this.dataHierarchy = new OntModelJTree();
		this.dataScrollPane.setViewportView(dataHierarchy);
		this.methodHierarchy = new OntModelJTree();
		this.methodScrollPane.setViewportView(methodHierarchy);
		this.runsTab = new RunsJPanel();
		this.setWorkflow();
	}

	/**
	 * Update GUI components to reflect the currently selected OWL document -
	 * When a user clicks on a different node in the owl document tree (update
	 * concept hierarchy and ontology or workflow tabs) - Loading a new owl
	 * document (update hierarchies and graphs) - Closing the workspace (clear
	 * all)
	 */
	public void updateSelectedOWLDoc() {
		State state = State.getInstance();
		state.updateOntModelHierarchy();
		String selectedURI = state.getSelectedOWLDocumentURI();
		this.loadedOWLDocs.refresh();
		if (selectedURI == null) {
			this.resetGUI();
		} else {
			this.loadedOWLDocs.setSelectedValue(selectedURI);
			this.updateClassHierarchies();
			this.setWorkflow();
		}
		this.updateProperties();
		if (this.loadedOWLDocs.getComponentPopupMenu() == null) {
			this.loadedOWLDocs.setComponentPopupMenu(owlDocHierarchyPopupMenu);
		}
	}

	/**
	 * Update the GUI to reflect the currently selected workflow in the state.
	 */
	public void setWorkflow() {
		State state = State.getInstance();
		OntModel selectedWorkflow = state.getSelectedWorkflow();
		// update workflow tab
		this.workflowScrollPane.setWorkflow(
				SAWScrollPane.createWorkflowGraph(selectedWorkflow),
				workflowPopupMenu);
		// update workflow border title
		TitledBorder border = (TitledBorder) this.workflowScrollPane
				.getBorder();
		String title = (selectedWorkflow == null) ? "" : Workspace
				.shortURI(state.getSelectedWorkflowURI());
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		border.setTitle(resourceMap.getString(
				"workflowScrollPane.border.title", title));
		this.workflowScrollPane.repaint();

		// update runs tab
		// this.runsTab.setWorkflow(SAWScrollPane.createWorkflowGraph(selectedWorkflow),
		// workflowPopupMenu);
		// TODO update State to store runs and add param to setRuns method
		// accordingly
		// this.runsTab.setRuns();

		// enable/disable tabs accordingly
		this.tabbedPane.setEnabled(selectedWorkflow != null);

		// update properties
		this.updateProperties();
	}

	/**
	 * Updates the loaded OWL documents hierarchy. This alternative is faster
	 * than updateSelectedOWLDoc(), but it does not update other parts of the
	 * GUI. This is a good choice for non-structural changes to the OWL doc
	 * hierarchy, e.g., updating the modified bit of an OWL doc.
	 */
	public void updateOWLDocHierarchy() {
		this.loadedOWLDocs = new OntDocumentManagerJTree();
		this.loadedOWLDocs.setSelectedValue(State.getInstance()
				.getSelectedOWLDocumentURI());
		this.loadedOWLDocsScrollPane.setViewportView(loadedOWLDocs);
		this.updateProperties();
		State.getInstance().getSelectedClass();

	}

	/**
	 * Add a workflow cell to the current workflow graph. This is useful to
	 * update changes in the workflow graph without having to redraw all.
	 * 
	 * @param cell
	 */
	public void addWorkflowCell(DefaultGraphCell cell) {
		this.workflowScrollPane.addWorkflowCell(cell);
		this.updateOWLDocHierarchy();
	}

	/**
	 * Replace a workflow cell on the current workflow graph. This is useful to
	 * update changes in the workflow graph without having to redraw all.
	 * 
	 * @param replacerNode
	 * @param replaceeNode
	 */
	public void replaceWorkflowNode(IndividualNode replacerNode,
			IndividualNode replaceeNode) {
		this.workflowScrollPane.replaceWorkflowNode(replacerNode, replaceeNode);
		this.updateOWLDocHierarchy();
	}

	/**
	 * Add a source or a sink attached to the specified edge on the current
	 * workflow graph. This is useful to update changes in the workflow graph
	 * without having to redraw all.
	 * 
	 * @param edge
	 * @param sourceOrSink
	 * @param source
	 */
	public void addSourceOrSink(IndividualEdge edge,
			DefaultGraphCell sourceOrSink, boolean source) {
		this.workflowScrollPane.addSourceOrSink(edge, sourceOrSink, source);
		this.updateOWLDocHierarchy();
	}

	/**
	 * Update GUI concept hierarchies - Adding a new WDO concept - Removing a
	 * WDO concept
	 */
	private void updateClassHierarchies() {
		State state = State.getInstance();
		OntModel baseWDO = state.getBaseWDO();
		if (baseWDO == null) {
			this.dataHierarchy.setRoot(null);
			this.methodHierarchy.setRoot(null);
		} else {
			this.dataHierarchy.setSelectedValueBookmark();
			this.dataHierarchy.setOntModel(baseWDO);
			this.dataHierarchy.setRoot(baseWDO
					.getOntClass(WDO_Metamodel.DATA_URI));
			this.dataHierarchy.restoreSelectedValueBookmark();
			this.dataHierarchy
					.setComponentPopupMenu(this.conceptHierarchyPopupMenu);

			this.methodHierarchy.setSelectedValueBookmark();
			this.methodHierarchy.setRoot(baseWDO
					.getOntClass(WDO_Metamodel.METHOD_URI));
			this.methodHierarchy.restoreSelectedValueBookmark();
			this.methodHierarchy
					.setComponentPopupMenu(this.conceptHierarchyPopupMenu);
		}
		this.updateProperties();
	}

	/**
	 * loadWorkspaceToFile
	 * 
	 * @author agandara1 Category: utility function Parameters: fileName - path
	 *         to the workspace file that will be loaded Description: gets a
	 *         list of ontologies, wdo and workflow files from the specified
	 *         workspace file (fileName) and loads each one Throws: IOException
	 *         NOTES: assumes that this is being called by an action command
	 */
	private void loadWorkspaceFile(String fileName) throws IOException {
		// obtain the list of workspace files (wdo, ontologies, workflows)
		ArrayList<String> uriList = WorkspaceFile.readWorkspaceFile(fileName,
				this);
		// iterate through them
		ListIterator<String> listIterator = uriList.listIterator();
		if (uriList != null) {
			// load the uris - one by one
			while (listIterator.hasNext()) {
				String uri = listIterator.next();
				// setup the load task
				State.getInstance().setURIToLoad(uri);
				this.openOWLURI().execute();
			}
		}
	}

	/**
	 * setupBookmarksMenu
	 * 
	 * @author: agandara1 Category: utility function Description: Grabs the
	 *          top-level bookmarks from the bookmark file and adds them to the
	 *          bookmarks menu
	 */
	public void setupBookmarksMenu() throws IOException {

		// open up the file and get the list of bookmarks
		Bookmarks bm = new Bookmarks();
		ArrayList<String> bl = bm.getNonListBookmarks();
		// iterate through the list of bookmarks
		String abookmark = new String();
		Iterator<String> abl = bl.iterator();
		while (abl.hasNext()) {
			abookmark = (String) abl.next();
			// create menu items
			javax.swing.JMenuItem bookmarkMI = new javax.swing.JMenuItem();
			bookmarkMI.setText(abookmark);
			// set the listener code
			bookmarkMI.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					State.getInstance().setURIToLoad(e.getActionCommand());
					openOWLURI().execute();
				}
			});
			// add to menu
			bookmarksMenu.add(bookmarkMI);
		}
	}

	/**
	 * resetBookmarksMenu
	 * 
	 * @author: agandara1 Category: utility function Description: Reset the list
	 *          of bookmark menu items to those found in the bookmarks file
	 */
	public void resetBookmarksMenu() throws IOException {

		// appframework objects
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoView.class);
		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(WdoView.class, this);

		// clear what was previously there
		bookmarksMenu.removeAll();

		// setup default settings within the menu
		bookmarksMenuaddBookmark.setAction(actionMap.get("addBookmark"));
		bookmarksMenuaddBookmark.setText(resourceMap
				.getString("bookmarksMenuaddBookmark.text")); // NOI18N
		bookmarksMenuaddBookmark.setName("bookmarksMenuaddBookmark"); // NOI18N
		bookmarksMenu.add(bookmarksMenuaddBookmark);

		bookmarksMenuSeparator1.setName("bookmarksMenuSeparator1"); // NOI18N
		bookmarksMenu.add(bookmarksMenuSeparator1);

		// add the contents of the bookmarks file
		setupBookmarksMenu();
	}

	/**
	 * 
	 * @param actionName
	 * @return
	 */
	protected javax.swing.Action getAction(String actionName) {
		javax.swing.Action ans = null;
		if (actionName != null && !actionName.isEmpty()) {
			ActionMap actionMap = Application.getInstance(WdoApp.class)
					.getContext().getActionMap(WdoView.class, this);
			ans = actionMap.get(actionName);
		}
		return ans;
	}
	
	// /////////////// END UTILITY METHODS //////////////////////////
}
