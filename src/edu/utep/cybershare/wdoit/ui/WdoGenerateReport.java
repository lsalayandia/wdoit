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
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jgraph.JGraph;

import com.hp.hpl.jena.ontology.OntModel;

import edu.utep.cybershare.wdoapi.Workspace;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.export.SAWReportGenerator;
import edu.utep.cybershare.wdoit.export.WDOReportGenerator;

/**
 * Dialog box to generate report of OWL Documents loaded in the Workspace.
 * 
 * @author Leonardo Salayandia
 */
public class WdoGenerateReport extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton deselectAllButton;
	private javax.swing.JButton generateReportButton;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JLabel ontologiesLabel;
	private javax.swing.JList ontologyList;
	private javax.swing.JButton selectAllButton;
	private javax.swing.JList workflowList;
	private javax.swing.JLabel workflowsLabel;

	private boolean owlDocumentsSelected;

	/** Creates new form WdoGenerateReport */
	public WdoGenerateReport(Frame parent) {
		super(parent);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoGenerateReport.class);
		setTitle(resourceMap.getString("title"));
		this.setResizable(false);

		// initialize properties
		owlDocumentsSelected = false;

		initComponents();
		getRootPane().setDefaultButton(generateReportButton);
	}

	/**
	 * Check whether there are owl documents selected from the Ontology and
	 * Workflow lists
	 * 
	 * @return
	 */
	public boolean isOwlDocumentsSelected() {
		return owlDocumentsSelected;
	}

	/**
	 * Set the corresponding property that reflects whether the required fields
	 * of this form are set.
	 */
	private void setOwlDocumentsSelected() {
		boolean oldval = owlDocumentsSelected;
		owlDocumentsSelected = !(ontologyList.isSelectionEmpty() && workflowList
				.isSelectionEmpty());
		if (owlDocumentsSelected != oldval) {
			this.firePropertyChange("owlDocumentsSelected", oldval,
					owlDocumentsSelected);
		}
	}

	/**
	 * Deselects all items from the ontology and workflow lists
	 */
	@Action(enabledProperty = "owlDocumentsSelected")
	public void deselectAll() {
		ontologyList.clearSelection();
		workflowList.clearSelection();
	}

	/**
	 * Selects all items from the ontology and workflow lists
	 */
	@Action
	public void selectAll() {
		ontologyList.setSelectionInterval(ontologyList.getFirstVisibleIndex(),
				ontologyList.getLastVisibleIndex());
		workflowList.setSelectionInterval(workflowList.getFirstVisibleIndex(),
				workflowList.getLastVisibleIndex());
	}

	/**
	 * Cancels the operation and exits this window
	 */
	@Action
	public void cancel() {
		setVisible(false);
	}

	/**
	 * Proceed with generation of report for selected owl documents, and show
	 * report on html frame window
	 */
	@Action(enabledProperty = "owlDocumentsSelected")
	public void generate() {
		// hide this window
		this.setVisible(false);
		// ask for location to store the report files
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoGenerateReport.class);
		int returnVal = fc.showDialog(this,
				resourceMap.getString("fileChooser.title"));
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String dirName = fc.getSelectedFile().getAbsolutePath();
			// start index file for report
			String indexFilename = resourceMap
					.getString("reportStartingFileName");
			String indexFile = "<html><p>Ontologies:</p><ul>";
			try {
				// process the ontologies selected, create a report for each and
				// store file in the selected location
				if (!ontologyList.isSelectionEmpty()) {
					String templateText = loadOntologyReportTemplate(
							resourceMap.getString("ontologyTemplateFileName"),
							this.getClass().getClassLoader());
					Object[] selectedOntologies = ontologyList
							.getSelectedValues();
					WDOReportGenerator gen = new WDOReportGenerator(
							templateText);
					for (int i = 0; i < selectedOntologies.length; i++) {
						String selectedOntologyURI = selectedOntologies[i]
								.toString();
						String report = gen.GenerateReport(State.getInstance()
								.getOntModel(selectedOntologyURI),
								indexFilename);
						String filename = Workspace
								.shortURI(selectedOntologyURI) + ".html";
						FileWriter fw = new FileWriter(dirName + File.separator
								+ filename);
						BufferedWriter out = new BufferedWriter(fw);
						out.write(report);
						out.close();
						fw.close();
						// add reference for each of the ontology reports in the
						// index file of the report
						indexFile = indexFile + "<li><a href=\"" + filename
								+ "\">" + selectedOntologyURI + "</a></li>";
					}
				}
				// patch index file of the report to finilize ontology list and
				// start workflows list
				indexFile = indexFile + "</ul><p>Workflows:</p><ul>";
				// process the workflows selected, create a report for each and
				// store file in the selected location
				if (!workflowList.isSelectionEmpty()) {
					String templateText = loadOntologyReportTemplate(
							resourceMap.getString("workflowTemplateFileName"),
							this.getClass().getClassLoader());
					Object[] selectedWorkflows = workflowList
							.getSelectedValues();
					SAWReportGenerator gen = new SAWReportGenerator(
							templateText);
					for (int i = 0; i < selectedWorkflows.length; i++) {
						String selectedWorkflowURI = selectedWorkflows[i]
								.toString();
						String filename = Workspace
								.shortURI(selectedWorkflowURI) + ".html";
						String imgFilename = Workspace
								.shortURI(selectedWorkflowURI) + ".png";
						// write report
						OntModel selectedWorkflow = State.getInstance()
								.getOntModel(selectedWorkflowURI);
						String report = gen.GenerateReport(selectedWorkflow,
								imgFilename, indexFilename);
						FileWriter fw = new FileWriter(dirName + File.separator
								+ filename);
						BufferedWriter out = new BufferedWriter(fw);
						out.write(report);
						out.close();
						fw.close();
						// create image of workflow graph and write to file
						JGraph workflowGraph = SAWScrollPane
								.createWorkflowGraph(selectedWorkflow);
						createWorkflowPNG(workflowGraph, dirName
								+ File.separator + imgFilename);
						// add reference for each of the workflow reports in the
						// index file of the report
						indexFile = indexFile + "<li><a href=\"" + filename
								+ "\">" + selectedWorkflowURI + "</a></li>";
					}
				}
				// finalize index file of the report and write to file in the
				// selected location
				indexFile = indexFile + "</ul></html>";
				FileWriter fw = new FileWriter(dirName + File.separator
						+ indexFilename);
				BufferedWriter out = new BufferedWriter(fw);
				out.write(indexFile);
				out.close();
				fw.close();
			} catch (Exception ex) {
				WdoView wdoView = (WdoView) WdoApp.getApplication()
						.getMainView();
				wdoView.setMessage(resourceMap.getString("fail.text",
						ex.getMessage()));
			}
			// show HTML frame with index file of the report
			HTMLFrame htmlFrame = new HTMLFrame("file:" + dirName
					+ File.separator + indexFilename,
					resourceMap.getString("htmlFrame.title"), true);
			htmlFrame.setVisible(true);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {

		generateReportButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		jPanel1 = new javax.swing.JPanel();
		deselectAllButton = new javax.swing.JButton();
		selectAllButton = new javax.swing.JButton();
		ontologiesLabel = new javax.swing.JLabel();
		workflowsLabel = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		ontologyList = new javax.swing.JList();
		jScrollPane2 = new javax.swing.JScrollPane();
		workflowList = new javax.swing.JList();

		setName("WdoGenerateReport"); // NOI18N
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);

		ActionMap actionMap = Application.getInstance(WdoApp.class)
				.getContext().getActionMap(WdoGenerateReport.class, this);
		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(WdoGenerateReport.class);
		generateReportButton.setText(resourceMap
				.getString("generateReportButton.text")); // NOI18N
		generateReportButton.setName("generateReportButton"); // NOI18N
		generateReportButton.setAction(actionMap.get("generate"));

		cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
		cancelButton.setName("cancelButton"); // NOI18N
		cancelButton.setAction(actionMap.get("cancel"));

		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder(resourceMap
						.getString("jPanel1.border.title"))); // NOI18N
		jPanel1.setName("jPanel1"); // NOI18N

		deselectAllButton.setText(resourceMap
				.getString("deselectAllButton.text")); // NOI18N
		deselectAllButton.setName("deselectAllButton"); // NOI18N
		deselectAllButton.setAction(actionMap.get("deselectAll"));

		selectAllButton.setText(resourceMap.getString("selectAllButton.text")); // NOI18N
		selectAllButton.setName("selectAllButton"); // NOI18N
		selectAllButton.setAction(actionMap.get("selectAll"));

		ontologiesLabel.setText(resourceMap.getString("ontologiesLabel.text")); // NOI18N
		ontologiesLabel.setName("ontologiesLabel"); // NOI18N

		workflowsLabel.setText(resourceMap.getString("workflowsLabel.text")); // NOI18N
		workflowsLabel.setName("workflowsLabel"); // NOI18N

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		OwlDocumentListSelectionListener listSelectionListener = new OwlDocumentListSelectionListener(
				this);

		ontologyList.setModel(new OntologyListModel());
		ontologyList.setName("ontologyList"); // NOI18N
		ontologyList.addListSelectionListener(listSelectionListener);
		jScrollPane1.setViewportView(ontologyList);

		jScrollPane2.setName("jScrollPane2"); // NOI18N

		workflowList.setModel(new WorkflowListModel());
		workflowList.setName("workflowList"); // NOI18N
		workflowList.addListSelectionListener(listSelectionListener);
		jScrollPane2.setViewportView(workflowList);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout.createSequentialGroup()
										.addComponent(ontologiesLabel)
										.addGap(203, 203, 203)
										.addComponent(workflowsLabel)
										.addGap(207, 207, 207))
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addComponent(
												jScrollPane1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												242,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																jScrollPane2,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																263,
																Short.MAX_VALUE)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				selectAllButton,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				87,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				deselectAllButton)))));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel1Layout
										.createSequentialGroup()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																ontologiesLabel)
														.addComponent(
																workflowsLabel))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																212,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGroup(
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				jScrollPane2,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				212,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jPanel1Layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								deselectAllButton)
																						.addComponent(
																								selectAllButton))
																		.addGap(16,
																				16,
																				16)))));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				this.getContentPane());
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
														jPanel1,
														javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		cancelButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		109,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		generateReportButton)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jPanel1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										295,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(cancelButton)
												.addComponent(
														generateReportButton))
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		pack();
	}

	/**
	 * Create JPG image of the specified workflow graph
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void createWorkflowPNG(JGraph workflowGraph, String filename)
			throws Exception {
		if (workflowGraph != null && filename != null) {

			// temp dialog needed to force workflow graph to render so that it
			// can be captured
			JDialog tempDialog = new JDialog();
			tempDialog.add(workflowGraph);
			tempDialog.pack();
			tempDialog.setVisible(true);

			OutputStream out = new FileOutputStream(filename);
			ImageIO.write(workflowGraph.getImage(Color.WHITE, 5), "png", out);
			out.flush();
			out.close();

			tempDialog.dispose();
		}
	}

	/**
	 * Load the text of the specified template file
	 * 
	 * @param filename
	 * @param cl
	 * @return
	 */
	private static String loadOntologyReportTemplate(String filename,
			ClassLoader cl) throws Exception {
		String templateText = "";
		if (filename != null && !filename.isEmpty()) {
			BufferedReader buff = null;
			InputStream inStream = null;
			FileReader templatefile = null;

			// try loading from the jar file
			if (cl != null) {
				inStream = cl.getResourceAsStream(filename);
			}
			if (inStream != null) {
				InputStreamReader inStreamRdr = new InputStreamReader(inStream);
				buff = new BufferedReader(inStreamRdr);
			}

			// if could not load from the jar, try loading from the file system
			else {
				templatefile = new FileReader("lib" + File.separator + filename);
				buff = new BufferedReader(templatefile);
			}

			if (buff != null) {
				boolean eof = false;
				while (!eof) {
					String line = buff.readLine();
					if (line == null) {
						eof = true;
					} else {
						templateText = templateText + line + "\n";
					}
				}
				// remove last \n inserted in the loop
				templateText = templateText.substring(0,
						templateText.length() - 1);

				buff.close();
				if (templatefile != null) {
					templatefile.close();
				}
			}
		}

		return templateText;
	}

	/**
	 * List model class for ontology list
	 * 
	 * @author Leonardo Salayandia
	 */
	private class OntologyListModel extends AbstractListModel {
		private static final long serialVersionUID = 1L;
		private String[] ontologyList;

		protected OntologyListModel() {
			super();
			State state = State.getInstance();
			ArrayList<String> temp = new ArrayList<String>();
			for (Iterator<String> i = state.listOntologyURIs(); i.hasNext();) {
				String uri = i.next();
				if (!temp.contains(uri)) {
					temp.add(uri);
				}
			}
			ontologyList = new String[temp.size()];
			temp.toArray(ontologyList);
		}

		@Override
		public Object getElementAt(int index) {
			return ontologyList[index];
		}

		@Override
		public int getSize() {
			return ontologyList.length;
		}
	}

	/**
	 * List model class for workflow list
	 * 
	 * @author Leonardo Salayandia
	 */
	private class WorkflowListModel extends AbstractListModel {
		private static final long serialVersionUID = 1L;
		private String[] workflowList;

		protected WorkflowListModel() {
			State state = State.getInstance();
			ArrayList<String> temp = new ArrayList<String>();
			for (Iterator<String> i = state.listWorkflowURIs(); i.hasNext();) {
				String uri = i.next();
				if (!temp.contains(uri)) {
					temp.add(uri);
				}
			}
			workflowList = new String[temp.size()];
			temp.toArray(workflowList);
		}

		@Override
		public Object getElementAt(int index) {
			return workflowList[index];
		}

		@Override
		public int getSize() {
			return workflowList.length;
		}
	}

	/**
	 * Listener class that triggers the OwlDocumentSelected property when there
	 * is a change in the selection state of lists.
	 * 
	 * @author Leonardo Salayandia
	 */
	private class OwlDocumentListSelectionListener implements
			ListSelectionListener {
		private WdoGenerateReport window;

		OwlDocumentListSelectionListener(WdoGenerateReport window) {
			this.window = window;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			window.setOwlDocumentsSelected();
		}
	}
}
