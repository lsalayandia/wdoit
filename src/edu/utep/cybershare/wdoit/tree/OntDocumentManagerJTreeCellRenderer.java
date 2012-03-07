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
package edu.utep.cybershare.wdoit.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jdesktop.application.ResourceMap;

import edu.utep.cybershare.wdoapi.Workspace;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;

/**
 * Sets the text, icon and tool tip text for the cells of the workspace tree.
 * 
 * @author Leonardo Salayandia
 */
public class OntDocumentManagerJTreeCellRenderer extends
		DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	final private Icon ontologyIcon;
	final private Icon workflowIcon;
	final private Icon unclassifiedIcon;
	final private String modifiedMark;
	final private String ontologiesNodeText;
	final private String workflowsNodeText;

	public OntDocumentManagerJTreeCellRenderer() {
		ResourceMap rm = WdoApp.getApplication().getContext()
				.getResourceMap(OntDocumentManagerJTree.class);
		ontologyIcon = rm.getIcon("ontDocumentManagerTree.ontology.icon");
		workflowIcon = rm.getIcon("ontDocumentManagerTree.workflow.icon");
		unclassifiedIcon = rm
				.getIcon("ontDocumentManagerTree.unclassified.icon");
		modifiedMark = rm.getString("ontDocumentManagerTree.modifiedMark");
		ontologiesNodeText = rm
				.getString("ontDocumentManagerTree.ontologies.text");
		workflowsNodeText = rm
				.getString("ontDocumentManagerTree.workflows.text");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);

		if (value != null && value instanceof String) {
			String uri = (String) value;
			setToolTipText(uri);
			State state = State.getInstance();
			if (uri.equals(Workspace.ONTOLOGIES_NODE_ID)) {
				setText(ontologiesNodeText);
			} else if (uri.equals(Workspace.WORKFLOWS_NODE_ID)) {
				setText(workflowsNodeText);
			} else {
				setText((state.isModified(uri)) ? Workspace.shortURI(uri)
						+ modifiedMark : Workspace.shortURI(uri));
			}
			if (state.isOntology(uri)) {
				setIcon(ontologyIcon);
			} else if (state.isWorkflow(uri)) {
				setIcon(workflowIcon);
			} else {
				setIcon(null);
			}
		} else {
			setToolTipText(null);
			setIcon(unclassifiedIcon);
		}

		return this;
	}
}
