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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

//import edu.utep.cybershare.ciclient.ciconnect.CIKnownServerTable;
//import edu.utep.cybershare.ciclient.ciui.CIGetResourceSaveLocationDialog;
import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoapi.Workspace;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.export.ProvenanceAnnotatorGenerator;
import edu.utep.cybershare.wdoit.export.ProvenanceAnnotatorGenerator.DataProperties;
import edu.utep.cybershare.wdoit.export.ProvenanceAnnotatorGenerator.MethodProperties;
import edu.utep.cybershare.wdoit.ui.components.FormatComboBox;
import edu.utep.cybershare.wdoit.ui.components.EngineComboBox;
import edu.utep.cybershare.wdoit.ui.components.IndividualComboBox;
import edu.utep.cybershare.wdoit.ui.components.IndividualList;

public class CreateProvenanceAnnotators extends JDialog {

	private static final long serialVersionUID = 1L;

	private static String CIServerCreds;

	private OntModel selectedWorkflow;

	private JButton cancelButton;
	private JButton nextButton;
	private JButton prevButton;
	private JButton generateButton;
	private JButton helpButton;
	private JTabbedPane tabbedPane;
	private JPanel environmentTab;
	private JPanel bindingsTab;

	private JPanel baseDirPanel;
	private JLabel baseDirPathLabel;
	private JTextField baseDirPath;
	private JButton browseBaseDirButton;
	private JLabel annotatorOutputPathLabel;
	private JTextField annotatorOutputPath;
	private JButton annotatorOutputPathButton;
	private JTextField pmlOutputPath;
	private JLabel pmlOutputPathLabel;
	private JButton pmlOutputPathButton;

	private JRadioButton aaOption1;
	private JRadioButton aaOption2;
	private JRadioButton aaOption3;
	private JRadioButton aaOption4;
	private JTextField aaPath;
	private JButton browseAAPathButton;
	private DataAnnotatorFilter dataAnnotatorFilter;
	private ButtonGroup annotatorAgentGroup;
	private JPanel annotatorAgentPanel;

	private ButtonGroup targetSystemGroup;
	private JPanel targetSystemPanel;
	private JRadioButton tsOption1;
	private JRadioButton tsOption2;
	private JRadioButton tsOption3;
	private JRadioButton tsOption4;

	private EngineComboBox sourceEngine;
	private JLabel sourceEngineLabel;
	private IndividualList sourcesList;
	private JPanel sourcesPanel;
	private JScrollPane sourcesScrollPane;

	private FormatComboBox dataFormat;
	private JLabel dataFormatLabel;
	private IndividualList dataList;
	private JPanel dataPanel;
	private JScrollPane dataScrollPane;
	private JTextField dataFileName;
	private JLabel dataFileNameLabel;
	private javax.swing.JPanel dataLocationPanel;
	private javax.swing.ButtonGroup datalocGroup;
	private javax.swing.JRadioButton datalocOption1;
	private javax.swing.JRadioButton datalocOption2;
	private JButton browseDataFileNameButton;

	private EngineComboBox methodEngine;
	private JLabel methodEngineLabel;
	private IndividualList methodsList;
	private JPanel methodsPanel;
	private JScrollPane methodsScrollPane;
	private JButton updateSAWButton;

	private ProvenanceAnnotatorGenerator gen;

	private boolean prevWizardStep; // property to indicate whether there is a
									// previous step in the wizard
	private boolean nextWizardStep; // property to indicate whether there is a
									// next step in the wizard
	private boolean requiredFieldsSet; // property to indicate whether all
										// required fields have been set

	/**
	 * Creates new form CreateProvenanceAnnotators
	 */
	public CreateProvenanceAnnotators(Frame parent) {
		super(parent);

		prevWizardStep = false;
		nextWizardStep = true;
		requiredFieldsSet = false;
		dataAnnotatorFilter = new DataAnnotatorFilter();

		initComponents();
		this.setModal(true);
	}

	/**
	 * Override setVisible method Force tabbedPane to be set to the initial tab
	 * when the window becomes visible
	 */
	@Override
	public void setVisible(boolean b) {
		if (b) {
			this.tabbedPane.setSelectedIndex(0);
		}
		super.setVisible(b);
	}

	// **************************
	// Property methods
	// **************************

	public boolean getPrevWizardStep() {
		return prevWizardStep;
	}

	private void setPrevWizardStep() {
		boolean oldval = prevWizardStep;
		prevWizardStep = (tabbedPane.getSelectedIndex() != 0);
		if (oldval != prevWizardStep) {
			this.firePropertyChange("prevWizardStep", oldval, prevWizardStep);
		}
	}

	public boolean getNextWizardStep() {
		return nextWizardStep;
	}

	private void setNextWizardStep() {
		boolean oldval = nextWizardStep;
		nextWizardStep = (tabbedPane.getSelectedIndex() < (tabbedPane
				.getTabCount() - 1));
		if (oldval != nextWizardStep) {
			this.firePropertyChange("nextWizardStep", oldval, nextWizardStep);
		}
	}

	public boolean getRequiredFieldsSet() {
		return requiredFieldsSet;
	}

	private void setRequiredFieldsSet() {
		boolean oldval = requiredFieldsSet;

		// have output dirs been set?
		String basedir = this.baseDirPath.getText();
		String annotatordir = this.annotatorOutputPath.getText();
		String pmldir = this.pmlOutputPath.getText();
		requiredFieldsSet = (basedir != null && !basedir.isEmpty()
				&& annotatordir != null && !annotatordir.isEmpty()
				&& pmldir != null && !pmldir.isEmpty());

		// if aa option 3 is selected, has path been set?
		if (requiredFieldsSet) {
			if (annotatorAgentGroup.isSelected(aaOption3.getModel())) {
				String aaPath = this.aaPath.getText();
				requiredFieldsSet = (aaPath != null && !aaPath.isEmpty());
			}
		}

		// have all binding properties been set?
		if (requiredFieldsSet) {
			requiredFieldsSet = gen.isRequiredPropertiesSet();
		}

		if (oldval != requiredFieldsSet) {
			this.firePropertyChange("requiredFieldsSet", oldval,
					requiredFieldsSet);
		}
	}

	// *************************
	// Init and Action methods
	// **************************

	/**
	 * Initialize the window to create Provenance Annotators for a selected SAW
	 * 
	 * @param
	 */
	public void initWindow() {
		// set properties
		setRequiredFieldsSet();
		setNextWizardStep();
		setPrevWizardStep();

		// set the selected workflow and update the title accordingly
		State state = State.getInstance();
		selectedWorkflow = state.getSelectedWorkflow();
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(CreateProvenanceAnnotators.class);
		setTitle(resourceMap.getString("title.text",
				Workspace.shortURI(state.getOntModelURI(selectedWorkflow))));

		// reset output directories
		baseDirPath.setText("");
		annotatorOutputPath.setText("");
		pmlOutputPath.setText("");

		// set default annotator agent
		this.annotatorAgentGroup.setSelected(aaOption1.getModel(), true);

		// set default target system
		targetSystemGroup.setSelected(tsOption1.getModel(), true);

		// initialize generator
		gen = new ProvenanceAnnotatorGenerator();

		// set the source individuals of the workflow
		Vector<OntClass> sources = new Vector<OntClass>();
		for (Iterator<OntClass> iter = state.listPMLSourceIndividuals(); iter
				.hasNext();) {
			OntClass src = iter.next();
			if (SAW.isSource(src)) {
				sources.add(src);
				gen.addSource(src);
			}
		}
		sourcesList.setModel(sources);
		sourcesScrollPane.setViewportView(sourcesList);

		// set the data individuals of the workflow
		Vector<OntClass> data = new Vector<OntClass>();
		for (Iterator<OntClass> iter = state.listDataIndividuals(); iter
				.hasNext();) {
			OntClass dataInd = iter.next();
			data.add(dataInd);
			gen.addData(dataInd);
		}

		dataList.setModel(data);
		dataScrollPane.setViewportView(dataList);

		// set the method individuals of the workflow
		Vector<OntClass> methods = new Vector<OntClass>();
		for (Iterator<OntClass> iter = state.listMethodIndividuals(); iter
				.hasNext();) {
			OntClass ind = iter.next();
			if (!SAW.isSemanticAbstractWorkflowType(ind)) {
				methods.add(ind);
				gen.addMethod(ind);
			}
		}
		methodsList.setModel(methods);
		methodsScrollPane.setViewportView(methodsList);
	}

	@Action
	public void cancel() {
		this.setVisible(false);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(CreateProvenanceAnnotators.class);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		wdoView.setMessage(resourceMap.getString("cancel.Action.msg"));
	}

	@Action(enabledProperty = "requiredFieldsSet")
	public void generate() {

		File dataF = new File(annotatorOutputPath.getText()); // create data
																// folder if not
																// present
		if (!dataF.exists()) {
			dataF.mkdir();
		}

		File mappingsF = new File(baseDirPath.getText().trim() + "\\mappings"); // create
																				// mappings
																				// folder
																				// if
																				// not
																				// present
		if (!mappingsF.exists()) {
			mappingsF.mkdir();
		}

		if (CIServerCreds == null || CIServerCreds.isEmpty()) {
			File pmlF = new File(pmlOutputPath.getText()); // create pml folder
															// if not present
			if (!pmlF.exists()) {
				pmlF.mkdir();
			}
		}

		if (tsOption1.isSelected()) {
			gen.generateShellScriptAnnotators(baseDirPath.getText().trim(),
					pmlOutputPath.getText(), annotatorOutputPath.getText(),
					CIServerCreds);
		} else if (tsOption2.isSelected()) {
			gen.generateBatchScriptAnnotators(baseDirPath.getText().trim(),
					pmlOutputPath.getText(), annotatorOutputPath.getText(),
					CIServerCreds);
		}
		JOptionPane.showMessageDialog(new JFrame(),
				"Configuration files and  scripts created successfully.",
				"WDO-It!", JOptionPane.INFORMATION_MESSAGE);
	}

	@Action(enabledProperty = "nextWizardStep")
	public void next() {
		tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
	}

	@Action(enabledProperty = "prevWizardStep")
	public void prev() {
		tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() - 1);
	}

	@Action
	public void browseBaseDir() {
		String defaultDirString = baseDirPath.getText();
		JFileChooser fc = new JFileChooser(
				(defaultDirString == null || defaultDirString.isEmpty()) ? null
						: defaultDirString);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(CreateProvenanceAnnotators.class);
		fc.setDialogTitle(resourceMap.getString("browseBaseDir.Action.title"));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String basedir = fc.getSelectedFile().getAbsolutePath();
			baseDirPath.setText(basedir);

			String temp = annotatorOutputPath.getText();
			if (temp == null || temp.isEmpty()) {
				annotatorOutputPath.setText(basedir
						+ System.getProperty("file.separator")
						+ resourceMap.getString("annotatorOutputPath.default"));
			}

			temp = pmlOutputPath.getText();
			if (temp == null || temp.isEmpty()) {
				pmlOutputPath.setText(basedir
						+ System.getProperty("file.separator")
						+ resourceMap.getString("pmlOutputPath.default"));
			}
		}
	}

	@Action
	public void browseAnnotatorOutputPath() {
		String defaultdir = annotatorOutputPath.getText();
		if (defaultdir == null || defaultdir.isEmpty()) {
			defaultdir = baseDirPath.getText();
		}
		JFileChooser fc = new JFileChooser(
				(defaultdir == null || defaultdir.isEmpty()) ? null
						: defaultdir);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(CreateProvenanceAnnotators.class);
		fc.setDialogTitle(resourceMap
				.getString("browseAnnotatorOutputPath.Action.title"));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			String basedir = fc.getSelectedFile().getAbsolutePath();
			annotatorOutputPath.setText(basedir);
		}
	}

	@Action
	public void browsePMLOutputPath() {

		// String path = CIGetResourceSaveLocationDialog.showDialog(this,
		// this,""); //URI
		// AIDA: I replaced this line based on your edits to the wdo project
		//		String path = CIGetResourceSaveLocationDialog.showDialog(this, this,
		//				"test", "pmlj"); // URI
		//		if (path.equals("Local Filesystem")) {
		String path = "";
		String defaultdir = pmlOutputPath.getText();
		if (defaultdir == null || defaultdir.isEmpty()) {
			defaultdir = baseDirPath.getText();
		}
		JFileChooser fc = new JFileChooser(
				(defaultdir == null || defaultdir.isEmpty()) ? null
						: defaultdir);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext()
				.getResourceMap(CreateProvenanceAnnotators.class);
		fc.setDialogTitle(resourceMap
				.getString("browsePMLOutputPath.Action.title"));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			path = fc.getSelectedFile().getAbsolutePath();
		}
		//		}

		//		int CIServerID = CIKnownServerTable.getInstance()
		//				.ciGetServerEntryFromURL(path);
		//
		//		if (CIServerID != -1) {
		//			String username = CIKnownServerTable.getInstance()
		//					.ciGetServerUsername(CIServerID);
		//			String pass = CIKnownServerTable.getInstance().ciGetServerPassword(
		//					CIServerID);
		//
		//			CIServerCreds = username + "," + pass;
		//			path = CIKnownServerTable.getInstance().ciGetServerURL(CIServerID);
		//		}
		pmlOutputPath.setText(path);
	}

	@Action
	public void browseAAPath() {
		String defaultdir = aaPath.getText();
		if (defaultdir == null || defaultdir.isEmpty()) {
			defaultdir = baseDirPath.getText();
		}
		JFileChooser fc = new JFileChooser(
				(defaultdir == null || defaultdir.isEmpty()) ? null
						: defaultdir);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(CreateProvenanceAnnotators.class);
		fc.setDialogTitle(resourceMap.getString("browseAAPath.Action.title"));
		fc.setFileFilter(dataAnnotatorFilter);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			aaPath.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}

	@Action
	public void browseDataFileName() {
		String defaultdir = dataFileName.getText();
		if (defaultdir == null || defaultdir.isEmpty()) {
			defaultdir = baseDirPath.getText();
		}
		JFileChooser fc = new JFileChooser(
				(defaultdir == null || defaultdir.isEmpty()) ? null
						: defaultdir);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(false);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(CreateProvenanceAnnotators.class);
		fc.setDialogTitle(resourceMap
				.getString("browseDataFileName.Action.title"));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			dataFileName.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}

	@Action
	public void updateSAW() {

	}

	@Action
	public void help() {

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		CIServerCreds = null;
		aaOption1 = new JRadioButton();
		aaOption2 = new JRadioButton();
		aaOption3 = new JRadioButton();
		aaOption4 = new JRadioButton();
		aaPath = new JTextField();
		annotatorAgentGroup = new ButtonGroup();
		annotatorAgentPanel = new JPanel();
		annotatorOutputPath = new JTextField();
		annotatorOutputPathLabel = new JLabel();
		annotatorOutputPathButton = new JButton();
		baseDirPanel = new JPanel();
		baseDirPath = new JTextField();
		baseDirPathLabel = new JLabel();
		bindingsTab = new JPanel();
		browseAAPathButton = new JButton();
		browseBaseDirButton = new JButton();
		browseDataFileNameButton = new JButton();
		cancelButton = new JButton();
		dataPanel = new JPanel();
		dataScrollPane = new JScrollPane();
		dataList = new IndividualList();
		dataFormatLabel = new JLabel();
		dataFormat = new FormatComboBox();
		dataFileName = new JTextField();
		dataFileNameLabel = new JLabel();
		dataLocationPanel = new javax.swing.JPanel();
		datalocGroup = new javax.swing.ButtonGroup();
		datalocOption1 = new javax.swing.JRadioButton();
		datalocOption2 = new javax.swing.JRadioButton();
		environmentTab = new JPanel();
		generateButton = new JButton();
		helpButton = new JButton();
		pmlOutputPath = new JTextField();
		pmlOutputPathButton = new JButton();
		pmlOutputPathLabel = new JLabel();
		prevButton = new JButton();
		tabbedPane = new JTabbedPane();
		targetSystemGroup = new ButtonGroup();
		targetSystemPanel = new JPanel();
		tsOption1 = new JRadioButton();
		tsOption2 = new JRadioButton();
		tsOption3 = new JRadioButton();
		tsOption4 = new JRadioButton();
		sourcesPanel = new JPanel();
		sourcesScrollPane = new JScrollPane();
		sourcesList = new IndividualList();
		sourceEngineLabel = new JLabel();
		sourceEngine = new EngineComboBox();
		methodsPanel = new JPanel();
		methodsScrollPane = new JScrollPane();
		methodsList = new IndividualList();
		methodEngineLabel = new JLabel();
		methodEngine = new EngineComboBox();
		nextButton = new JButton();
		updateSAWButton = new JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setMinimumSize(new java.awt.Dimension(620, 650));
		setName("CreateProvenanceAnnotators"); // NOI18N

		tabbedPane.setName("tabbedPane"); // NOI18N
		tabbedPane.addChangeListener(new TabChangeListener(this));

		environmentTab.setName("environmentTab"); // NOI18N

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(CreateProvenanceAnnotators.class);
		baseDirPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("baseDirPanel.border.title"))); // NOI18N
		baseDirPanel.setName("baseDirPanel"); // NOI18N

		baseDirPathLabel
				.setText(resourceMap.getString("baseDirPathLabel.text"));
		baseDirPathLabel.setName("baseDirPathLabel");
		baseDirPathLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);

		PathSetListener pathSetListener = new PathSetListener();
		baseDirPath.setName("baseDirPath"); // NOI18N
		baseDirPath.getDocument().addDocumentListener(pathSetListener);
		baseDirPath.setToolTipText(resourceMap
				.getString("baseDirPath.shortDescription"));

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext()
				.getActionMap(CreateProvenanceAnnotators.class, this);
		browseBaseDirButton.setAction(actionMap.get("browseBaseDir"));
		browseBaseDirButton.setName("browseBaseDirButton"); // NOI18N

		annotatorOutputPathLabel.setText(resourceMap
				.getString("annotatorOutputPathLabel.text"));
		annotatorOutputPathLabel.setName("annotatorOutputPathLabel");
		annotatorOutputPathLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);

		annotatorOutputPath.setName("annotatorOutputPath"); // NOI18N
		annotatorOutputPath.getDocument().addDocumentListener(pathSetListener);
		annotatorOutputPath.setToolTipText(resourceMap
				.getString("annotatorOutputPath.shortDescription"));

		annotatorOutputPathButton.setAction(actionMap
				.get("browseAnnotatorOutputPath"));
		annotatorOutputPathButton.setName("annotatorOutputPathButton"); // NOI18N

		pmlOutputPathLabel.setText(resourceMap
				.getString("pmlOutputPathLabel.text"));
		pmlOutputPathLabel.setName("pmlOutputPathLabel");
		pmlOutputPathLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);

		pmlOutputPath.setName("pmlOutputPath");
		pmlOutputPath.getDocument().addDocumentListener(pathSetListener);
		pmlOutputPath.setToolTipText(resourceMap
				.getString("pmlOutputPath.shortDescription"));

		pmlOutputPathButton.setAction(actionMap.get("browsePMLOutputPath"));
		pmlOutputPathButton.setName("pmlOutputPathButton");

		javax.swing.GroupLayout baseDirPanelLayout = new javax.swing.GroupLayout(
				baseDirPanel);
		baseDirPanel.setLayout(baseDirPanelLayout);
		baseDirPanelLayout
				.setHorizontalGroup(baseDirPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								baseDirPanelLayout
										.createSequentialGroup()
										.addGroup(
												baseDirPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																baseDirPathLabel,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																annotatorOutputPathLabel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																pmlOutputPathLabel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												baseDirPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																baseDirPath,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																549,
																Short.MAX_VALUE)
														.addComponent(
																annotatorOutputPath,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																549,
																Short.MAX_VALUE)
														.addComponent(
																pmlOutputPath,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																549,
																Short.MAX_VALUE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												baseDirPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																browseBaseDirButton)
														.addComponent(
																annotatorOutputPathButton)
														.addComponent(
																pmlOutputPathButton))));
		baseDirPanelLayout
				.setVerticalGroup(baseDirPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								baseDirPanelLayout
										.createSequentialGroup()
										.addGroup(
												baseDirPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																baseDirPathLabel)
														.addComponent(
																baseDirPath,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																browseBaseDirButton))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												baseDirPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																annotatorOutputPathLabel)
														.addComponent(
																annotatorOutputPath,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																annotatorOutputPathButton))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												baseDirPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																pmlOutputPathLabel)
														.addComponent(
																pmlOutputPath,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																pmlOutputPathButton))));

		annotatorAgentPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("annotatorAgentPanel.border.title"))); // NOI18N
		annotatorAgentPanel.setName("annotatorAgentPanel"); // NOI18N

		aaOption1.setText(resourceMap.getString("aaOption1.text")); // NOI18N
		aaOption1.setName("aaOption1"); // NOI18N

		aaOption2.setText(resourceMap.getString("aaOption2.text")); // NOI18N
		aaOption2.setName("aaOption2"); // NOI18N
		aaOption2.setEnabled(false);

		aaOption3.setText(resourceMap.getString("aaOption3.text")); // NOI18N
		aaOption3.setName("aaOption3"); // NOI18N
		aaOption3.addChangeListener(new AnnotatorAgentOption3ChangeListener());
		aaOption3.setEnabled(false);

		aaOption4.setText(resourceMap.getString("aaOption4.text")); // NOI18N
		aaOption4.setName("aaOption4"); // NOI18N
		aaOption4.setEnabled(false);

		aaPath.setText(resourceMap.getString("aaPath.text")); // NOI18N
		aaPath.setEnabled(false);
		aaPath.setName("aaPath"); // NOI18N
		aaPath.getDocument().addDocumentListener(pathSetListener);

		browseAAPathButton.setAction(actionMap.get("browseAAPath"));
		browseAAPathButton.setEnabled(false);
		browseAAPathButton.setName("browseAAPathButton"); // NOI18N

		javax.swing.GroupLayout annotatorAgentPanelLayout = new javax.swing.GroupLayout(
				annotatorAgentPanel);
		annotatorAgentPanel.setLayout(annotatorAgentPanelLayout);
		annotatorAgentPanelLayout
				.setHorizontalGroup(annotatorAgentPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								annotatorAgentPanelLayout
										.createSequentialGroup()
										.addGap(21, 21, 21)
										.addComponent(
												aaPath,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												200, Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(browseAAPathButton))
						.addGroup(
								annotatorAgentPanelLayout
										.createSequentialGroup()
										.addGroup(
												annotatorAgentPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(aaOption1)
														.addComponent(aaOption2)
														.addComponent(aaOption3)
														.addComponent(aaOption4))
										.addContainerGap(86, Short.MAX_VALUE)));
		annotatorAgentPanelLayout
				.setVerticalGroup(annotatorAgentPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								annotatorAgentPanelLayout
										.createSequentialGroup()
										.addComponent(aaOption1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(aaOption2)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(aaOption4)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(aaOption3)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												annotatorAgentPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																aaPath,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																browseAAPathButton))));

		annotatorAgentGroup.add(aaOption1);
		annotatorAgentGroup.add(aaOption2);
		annotatorAgentGroup.add(aaOption3);
		annotatorAgentGroup.add(aaOption4);
		annotatorAgentGroup.setSelected(aaOption1.getModel(), true);

		targetSystemPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("targetSystemPanel.border.title"))); // NOI18N
		targetSystemPanel.setName("targetSystemPanel"); // NOI18N

		tsOption1.setText(resourceMap.getString("tsOption1.text")); // NOI18N
		tsOption1.setName("tsOption1"); // NOI18N
		tsOption1.setMnemonic(1); // NOI18N

		tsOption2.setText(resourceMap.getString("tsOption2.text")); // NOI18N
		tsOption2.setName("tsOption2"); // NOI18N
		tsOption2.setMnemonic(2); // NOI18N

		tsOption3.setText(resourceMap.getString("tsOption3.text")); // NOI18N
		tsOption3.setName("tsOption3"); // NOI18N
		tsOption3.setMnemonic(3); // NOI18N
		tsOption3.setEnabled(false);

		tsOption4.setText(resourceMap.getString("tsOption4.text")); // NOI18N
		tsOption4.setName("tsOption4"); // NOI18N
		tsOption4.setMnemonic(4); // NOI18N
		tsOption4.setEnabled(false);

		javax.swing.GroupLayout targetSystemPanelLayout = new javax.swing.GroupLayout(
				targetSystemPanel);
		targetSystemPanel.setLayout(targetSystemPanelLayout);
		targetSystemPanelLayout
				.setHorizontalGroup(targetSystemPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								targetSystemPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												targetSystemPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(tsOption1)
														.addComponent(tsOption2)
														.addComponent(tsOption3)
														.addComponent(tsOption4))
										.addContainerGap(298, Short.MAX_VALUE)));
		targetSystemPanelLayout
				.setVerticalGroup(targetSystemPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								targetSystemPanelLayout
										.createSequentialGroup()
										.addComponent(tsOption1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(tsOption2)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(tsOption3)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(tsOption4)));

		targetSystemGroup.add(tsOption1);
		targetSystemGroup.add(tsOption2);
		targetSystemGroup.add(tsOption3);
		targetSystemGroup.add(tsOption4);
		targetSystemGroup.setSelected(tsOption1.getModel(), true);

		javax.swing.GroupLayout environmentTabLayout = new javax.swing.GroupLayout(
				environmentTab);
		environmentTab.setLayout(environmentTabLayout);
		environmentTabLayout
				.setHorizontalGroup(environmentTabLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								environmentTabLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												environmentTabLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																baseDirPanel,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																targetSystemPanel,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																annotatorAgentPanel,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap()));
		environmentTabLayout
				.setVerticalGroup(environmentTabLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								environmentTabLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												baseDirPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												annotatorAgentPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												targetSystemPanel,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												110, Short.MAX_VALUE)
										.addContainerGap()));

		tabbedPane
				.addTab(resourceMap
						.getString("environmentTab.TabConstraints.tabTitle"),
						environmentTab); // NOI18N

		bindingsTab.setName("bindingsTab"); // NOI18N

		sourcesPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("sourcesPanel.border.title"))); // NOI18N
		sourcesPanel.setName("sourcesPanel"); // NOI18N

		sourcesScrollPane.setName("sourcesScrollPane"); // NOI18N

		sourcesList.addListSelectionListener(new SourceListListener());
		sourcesList.setName("sourcesList"); // NOI18N
		sourcesList.setCellRenderer(new SourceCellRenderer());
		sourcesScrollPane.setViewportView(sourcesList);

		sourceEngineLabel.setText(resourceMap
				.getString("sourceEngineLabel.text")); // NOI18N
		sourceEngineLabel.setName("sourceEngineLabel"); // NOI18N

		sourceEngine.setName("sourceEngine"); // NOI18N
		sourceEngine.setEnabled(false);
		sourceEngine.addActionListener(new SourceEngineListener());

		javax.swing.GroupLayout sourcesPanelLayout = new javax.swing.GroupLayout(
				sourcesPanel);
		sourcesPanel.setLayout(sourcesPanelLayout);
		sourcesPanelLayout
				.setHorizontalGroup(sourcesPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								sourcesPanelLayout
										.createSequentialGroup()
										.addComponent(
												sourcesScrollPane,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												170,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(sourceEngineLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												sourceEngine,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												250, Short.MAX_VALUE)));
		sourcesPanelLayout.setVerticalGroup(sourcesPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(sourcesScrollPane,
						javax.swing.GroupLayout.DEFAULT_SIZE, 130,
						Short.MAX_VALUE)
				.addComponent(sourceEngine,
						javax.swing.GroupLayout.DEFAULT_SIZE, 20, 20)
				.addComponent(sourceEngineLabel));

		dataPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("dataPanel.border.title"))); // NOI18N
		dataPanel.setName("dataPanel"); // NOI18N

		dataScrollPane.setName("dataScrollPane"); // NOI18N

		dataList.addListSelectionListener(new DataListListener());
		dataList.setName("dataList"); // NOI18N
		dataList.setCellRenderer(new DataCellRenderer());
		dataScrollPane.setViewportView(dataList);

		dataFormatLabel.setText(resourceMap.getString("dataFormatLabel.text")); // NOI18N
		dataFormatLabel.setName("dataFormatLabel"); // NOI18N

		dataFormat.setName("dataFormat"); // NOI18N
		dataFormat.setEnabled(false);
		dataFormat.addActionListener(new DataFormatListener());

		dataFileNameLabel.setText(resourceMap
				.getString("dataFileNameLabel.text"));
		dataFileNameLabel.setName("dataFileNameLabel");

		dataFileName.setName("dataFileName");
		dataFileName.setEnabled(false);
		dataFileName.getDocument().addDocumentListener(
				new DataFileNameListener());

		dataLocationPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("dataLocationPanel.border.title"))); // NOI18N
		dataLocationPanel.setName("dataLocationPanel"); // NOI18N

		datalocOption1.setText(resourceMap.getString("datalocOption1.text")); // NOI18N
		datalocOption1.setName("datalocOption1"); // NOI18N
		datalocOption1
				.addChangeListener(new DataLocationOption1ChangeListener());
		datalocOption1.setEnabled(false);

		datalocOption2.setText(resourceMap.getString("datalocOption2.text")); // NOI18N
		datalocOption2.setName("datalocOption2"); // NOI18N
		datalocOption2.setEnabled(false);

		browseDataFileNameButton.setAction(actionMap.get("browseDataFileName"));
		browseDataFileNameButton.setName("browseDataFileNameButton"); // NOI18N
		browseDataFileNameButton.setEnabled(false);

		javax.swing.GroupLayout dataLocationPanelLayout = new javax.swing.GroupLayout(
				dataLocationPanel);
		dataLocationPanel.setLayout(dataLocationPanelLayout);
		dataLocationPanelLayout
				.setHorizontalGroup(dataLocationPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								dataLocationPanelLayout
										.createSequentialGroup()
										.addGap(21, 21, 21)
										.addComponent(dataFileNameLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												dataFileName,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												200, Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(browseDataFileNameButton))
						.addGroup(
								dataLocationPanelLayout
										.createSequentialGroup()
										.addGroup(
												dataLocationPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																datalocOption2)
														.addComponent(
																datalocOption1))
										.addContainerGap(150, Short.MAX_VALUE)));
		dataLocationPanelLayout
				.setVerticalGroup(dataLocationPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								dataLocationPanelLayout
										.createSequentialGroup()
										.addComponent(datalocOption2)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(datalocOption1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												dataLocationPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																dataFileNameLabel)
														.addComponent(
																dataFileName)
														.addComponent(
																browseDataFileNameButton))));

		datalocGroup.add(datalocOption1);
		datalocGroup.add(datalocOption2);
		datalocGroup.setSelected(datalocOption1.getModel(), true);

		javax.swing.GroupLayout dataPanelLayout = new javax.swing.GroupLayout(
				dataPanel);
		dataPanel.setLayout(dataPanelLayout);
		dataPanelLayout
				.setHorizontalGroup(dataPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								dataPanelLayout
										.createSequentialGroup()
										.addComponent(
												dataScrollPane,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												170,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												dataPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																dataPanelLayout
																		.createSequentialGroup()
																		.addGap(6,
																				6,
																				6)
																		.addComponent(
																				dataFormatLabel)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				dataFormat,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				250,
																				Short.MAX_VALUE))
														.addComponent(
																dataLocationPanel))));
		dataPanelLayout
				.setVerticalGroup(dataPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								dataPanelLayout
										.createSequentialGroup()
										.addGroup(
												dataPanelLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																dataFormat,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																dataFormatLabel))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(dataLocationPanel))
						.addComponent(dataScrollPane));

		methodsPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("methodsPanel.border.title"))); // NOI18N
		methodsPanel.setName("methodsPanel"); // NOI18N

		methodsScrollPane.setName("methodsScrollPane"); // NOI18N

		methodsList.addListSelectionListener(new MethodsListListener());
		methodsList.setName("methodsList"); // NOI18N
		methodsList.setCellRenderer(new MethodCellRenderer());
		methodsScrollPane.setViewportView(methodsList);

		methodEngineLabel.setText(resourceMap
				.getString("methodEngineLabel.text")); // NOI18N
		methodEngineLabel.setName("methodEngineLabel"); // NOI18N

		methodEngine.setName("methodEngine"); // NOI18N
		methodEngine.setEnabled(false);
		methodEngine.addActionListener(new MethodEngineListener());

		javax.swing.GroupLayout methodsPanelLayout = new javax.swing.GroupLayout(
				methodsPanel);
		methodsPanel.setLayout(methodsPanelLayout);
		methodsPanelLayout
				.setHorizontalGroup(methodsPanelLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								methodsPanelLayout
										.createSequentialGroup()
										.addComponent(
												methodsScrollPane,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												170,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(methodEngineLabel)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												methodEngine,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												250, Short.MAX_VALUE)));
		methodsPanelLayout.setVerticalGroup(methodsPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(methodsScrollPane,
						javax.swing.GroupLayout.DEFAULT_SIZE, 130,
						Short.MAX_VALUE)
				.addComponent(methodEngine,
						javax.swing.GroupLayout.DEFAULT_SIZE, 20, 20)
				.addComponent(methodEngineLabel));

		updateSAWButton.setAction(actionMap.get("updateSAW"));
		updateSAWButton.setName("updateSAWButton"); // NOI18N
		updateSAWButton.setEnabled(false);

		javax.swing.GroupLayout bindingsTabLayout = new javax.swing.GroupLayout(
				bindingsTab);
		bindingsTab.setLayout(bindingsTabLayout);
		bindingsTabLayout
				.setHorizontalGroup(bindingsTabLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								bindingsTabLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												bindingsTabLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																sourcesPanel,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																methodsPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																bindingsTabLayout
																		.createSequentialGroup()
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				551,
																				Short.MAX_VALUE)
																		.addComponent(
																				updateSAWButton))
														.addComponent(
																dataPanel,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap()));
		bindingsTabLayout
				.setVerticalGroup(bindingsTabLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								bindingsTabLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												sourcesPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												150,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										// 179
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												dataPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(20, 20, 20)
										.addComponent(
												methodsPanel,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGroup(
												bindingsTabLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																bindingsTabLayout
																		.createSequentialGroup()
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				15,
																				Short.MAX_VALUE)
																		.addComponent(
																				updateSAWButton)
																		.addContainerGap()))));

		tabbedPane.addTab(
				resourceMap.getString("bindingsTab.TabConstraints.tabTitle"),
				bindingsTab); // NOI18N

		cancelButton.setName("cancelButton"); // NOI18N
		cancelButton.setPreferredSize(new java.awt.Dimension(55, 23));
		cancelButton.setAction(actionMap.get("cancel"));

		generateButton.setName("generateButton"); // NOI18N
		generateButton.setPreferredSize(new java.awt.Dimension(55, 23));
		generateButton.setAction(actionMap.get("generate"));

		nextButton.setName("nextButton"); // NOI18N
		nextButton.setPreferredSize(new java.awt.Dimension(55, 23));
		nextButton.setAction(actionMap.get("next"));

		helpButton.setName("helpButton"); // NOI18N
		helpButton.setPreferredSize(new java.awt.Dimension(55, 23));
		helpButton.setAction(actionMap.get("help"));
		helpButton.setEnabled(false);

		prevButton.setName("prevButton"); // NOI18N
		prevButton.setPreferredSize(new java.awt.Dimension(55, 23));
		prevButton.setAction(actionMap.get("prev"));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(helpButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										85,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED,
										148, Short.MAX_VALUE)
								.addComponent(prevButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										85,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(nextButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										85,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(generateButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										85,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(cancelButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										85,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap())
				.addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE,
						590, Short.MAX_VALUE));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addComponent(tabbedPane)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(helpButton)
												.addComponent(cancelButton)
												.addComponent(
														generateButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(nextButton)
												.addComponent(prevButton))
								.addContainerGap()));

		pack();
	}

	// ************************************
	// Utility methods
	// ***********************************

	private class SourceCellRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			OntClass ind = (OntClass) value;
			setText(SAW.getSAWInstanceQName(ind));
			MethodProperties props = gen.getSourceProperties(ind);
			setForeground(props.isRequiredPropertiesSet() ? Color.BLUE
					: Color.RED);
			setBackground(isSelected ? list.getSelectionBackground() : list
					.getBackground());
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}

	private class DataCellRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			OntClass ind = (OntClass) value;
			setText(SAW.getSAWInstanceQName(ind));
			DataProperties props = gen.getDataProperties(ind);
			setForeground(props.isRequiredPropertiesSet() ? Color.BLUE
					: Color.RED);
			setBackground(isSelected ? list.getSelectionBackground() : list
					.getBackground());
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}

	private class MethodCellRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			OntClass ind = (OntClass) value;
			setText(SAW.getSAWInstanceQName(ind));
			MethodProperties props = gen.getMethodProperties(ind);
			setForeground(props.isRequiredPropertiesSet() ? Color.BLUE
					: Color.RED);
			setBackground(isSelected ? list.getSelectionBackground() : list
					.getBackground());
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}

	/**
	 * Listener class for tab change
	 * 
	 * @author Leonardo Salayandia
	 */
	private class TabChangeListener implements ChangeListener {
		private CreateProvenanceAnnotators gui;

		protected TabChangeListener(CreateProvenanceAnnotators gui) {
			super();
			this.gui = gui;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			gui.setNextWizardStep();
			gui.setPrevWizardStep();
		}
	}

	/**
	 * Listener class for path change
	 * 
	 * @author Leonardo Salayandia
	 */
	private class PathSetListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			setRequiredFieldsSet();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			setRequiredFieldsSet();
		}
	}

	/**
	 * Listener class for selecting/deselecting option 3 of the Annotator Agent
	 * Option group
	 * 
	 * @author Leonardo Salayandia
	 */
	private class AnnotatorAgentOption3ChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JRadioButton option = (JRadioButton) e.getSource();
			if (option.isEnabled()) {
				aaPath.setEnabled(option.isSelected());
				browseAAPathButton.setEnabled(option.isSelected());
			} else {
				aaPath.setEnabled(false);
				browseAAPathButton.setEnabled(false);
			}
			setRequiredFieldsSet();
		}
	}

	/**
	 * Listener class for selecting/deselecting option 1 of the data location
	 * option group
	 * 
	 * @author Leonardo Salayandia
	 */
	private class DataLocationOption1ChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JRadioButton option = (JRadioButton) e.getSource();

			OntClass dataInd = (OntClass) dataList.getSelectedValue();
			if (dataInd != null) {
				if (option.isSelected()) {
					// enable dependent GUI components
					dataFileName.setEnabled(true);
					browseDataFileNameButton.setEnabled(true);

					// set embedded data property for data annotator
					DataProperties props = gen.getDataProperties(dataInd);
					props.setEmbeddedData(false);
				} else {
					dataFileName.setEnabled(false);
					browseDataFileNameButton.setEnabled(false);

					// set embedded data property for data annotator
					DataProperties props = gen.getDataProperties(dataInd);
					props.setEmbeddedData(true);
				}
			}
		}
	}

	/**
	 * Filter class for data annotator agent jar file
	 * 
	 * @author Leonardo Salayandia
	 */
	private class DataAnnotatorFilter extends FileFilter {
		private String filename;
		private String desc;

		protected DataAnnotatorFilter() {
			super();
			ResourceMap resourceMap = Application.getInstance(WdoApp.class)
					.getContext()
					.getResourceMap(CreateProvenanceAnnotators.class);
			filename = resourceMap.getString("browseAAPath.Action.filename");
			desc = resourceMap.getString("browseAAPath.Action.filenameDesc");
		}

		@Override
		public boolean accept(File pathname) {
			if (pathname.isDirectory())
				return true;

			return (pathname.getName().endsWith(filename));
		}

		@Override
		public String getDescription() {
			return desc;
		}
	}

	private class SourceEngineListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			OntClass sourceInd = (OntClass) sourcesList.getSelectedValue();
			if (sourceInd != null) {
				IndividualComboBox.Individual sourceEngineInd = (IndividualComboBox.Individual) sourceEngine
						.getSelectedItem();
				MethodProperties props = gen.getSourceProperties(sourceInd);
				props.setEngineURI(sourceEngineInd.getURI());
				setRequiredFieldsSet();
				sourcesList.repaint();
			}
		}
	}

	private class DataFormatListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			OntClass dataInd = (OntClass) dataList.getSelectedValue();
			if (dataInd != null) {
				IndividualComboBox.Individual formatInd = (IndividualComboBox.Individual) dataFormat
						.getSelectedItem();
				DataProperties props = gen.getDataProperties(dataInd);
				props.setFormatURI(formatInd.getURI());
				setRequiredFieldsSet();
				dataList.repaint();
			}
		}
	}

	private class MethodEngineListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			OntClass methodInd = (OntClass) methodsList.getSelectedValue();
			if (methodInd != null) {
				IndividualComboBox.Individual methodEngineInd = (IndividualComboBox.Individual) methodEngine
						.getSelectedItem();
				MethodProperties props = gen.getMethodProperties(methodInd);
				props.setEngineURI(methodEngineInd.getURI());
				setRequiredFieldsSet();
				methodsList.repaint();
			}
		}
	}

	/**
	 * Listener class for path change
	 * 
	 * @author Leonardo Salayandia
	 */
	private class DataFileNameListener implements DocumentListener {

		private void updateProps() {
			OntClass dataInd = (OntClass) dataList.getSelectedValue();
			if (dataInd != null) {
				DataProperties props = gen.getDataProperties(dataInd);
				props.setExternalDataFilename(dataFileName.getText());
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateProps();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateProps();
		}
	}

	/**
	 * Listener to update fields when selection to sources list changes
	 * 
	 * @author Leonardo Salayandia
	 */
	private class SourceListListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			OntClass src = (OntClass) sourcesList.getSelectedValue();
			if (src != null) {
				// enable GUI components for this section
				sourceEngine.setEnabled(true);

				MethodProperties props = gen.getSourceProperties(src);
				String engineURI = (props == null) ? null : props
						.getEngineURI();
				if (engineURI == null || engineURI.isEmpty()) {
					sourceEngine.setSelectedIndex(0);
				} else {
					IndividualComboBox.Individual engineInd = sourceEngine.new Individual(
							engineURI, Workspace.shortURI(engineURI), engineURI);
					sourceEngine.setSelectedItem(engineInd);
				}
			} else {
				// disable GUI components for this section
				sourceEngine.setEnabled(false);
			}
		}
	}

	/**
	 * Listener to update fields when selection to data list changes
	 * 
	 * @author Leonardo Salayandia
	 */
	private class DataListListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			OntClass dataInd = (OntClass) dataList.getSelectedValue();
			if (dataInd != null) {
				// enable GUI components for this section
				dataFormat.setEnabled(true);
				datalocOption1.setEnabled(true);
				datalocOption2.setEnabled(true);
				dataFileName.setEnabled(true);
				browseDataFileNameButton.setEnabled(true);

				DataProperties props = gen.getDataProperties(dataInd);
				String formatURI = null;
				String filename = null;
				boolean embeddedData = false; // default option is to link data
												// from external file
				// if data annotator properties set, get them
				if (props != null) {
					formatURI = props.getFormatURI();
					embeddedData = props.isEmbeddedData();
					filename = props.getExternalDataFilename();
				}
				// if format not found in data annotator properties, try to get
				// it from SAW
				if (formatURI == null || formatURI.isEmpty()) {
					Individual ind = SAW.getFormat(dataInd);
					formatURI = (ind != null) ? ind.getURI() : null;
				}
				// set GUI components
				if (formatURI != null && !formatURI.isEmpty()) {
					dataFormat
							.setSelectedItem(dataFormat.new Individual(
									formatURI, Workspace.shortURI(formatURI),
									formatURI));
				} else {
					dataFormat.setSelectedIndex(0);
				}
				if (embeddedData) {
					datalocGroup.setSelected(datalocOption2.getModel(), true);
				} else {
					datalocGroup.setSelected(datalocOption1.getModel(), true);
				}
				dataFileName.setText(filename);
			} else {
				// disable GUI components for this section
				dataFormat.setEnabled(false);
				datalocOption1.setEnabled(false);
				datalocOption2.setEnabled(false);
				dataFileName.setEnabled(false);
				browseDataFileNameButton.setEnabled(false);
			}
		}
	}

	/**
	 * Listener to update fields when selection to methods list changes
	 * 
	 * @author Leonardo Salayandia
	 */
	private class MethodsListListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			OntClass methodInd = (OntClass) methodsList.getSelectedValue();
			if (methodInd != null) {
				// enable GUI components for this section
				methodEngine.setEnabled(true);

				MethodProperties props = gen.getMethodProperties(methodInd);
				String engineURI = (props == null) ? null : props
						.getEngineURI();
				if (engineURI == null || engineURI.isEmpty()) {
					methodEngine.setSelectedIndex(0);
				} else {
					IndividualComboBox.Individual engineInd = methodEngine.new Individual(
							engineURI, Workspace.shortURI(engineURI), engineURI);
					methodEngine.setSelectedItem(engineInd);
				}
			} else {
				// disable GUI components for this section
				methodEngine.setEnabled(false);
			}
		}
	}
}
