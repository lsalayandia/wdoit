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

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.action.OntDocumentManagerSelectionListener;

/**
 * @author Leonardo Salayandia
 * 
 */
public class OntDocumentManagerJTree extends JTree {
	private static final long serialVersionUID = 1L;

	public OntDocumentManagerJTree() {
		super(new OntDocumentManagerTreeModel());
		this.setRootVisible(true);
		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.setEditable(false);
		this.setToolTipText(WdoApp.getApplication().getContext()
				.getResourceMap(OntDocumentManagerJTree.class)
				.getString("ontDocumentManagerTree.shortDescription"));
		this.setCellRenderer(new OntDocumentManagerJTreeCellRenderer());
		this.addMouseListener(new OntDocumentManagerSelectionListener());

		// expand all the tree by default
		for (int i = 0; i < this.getRowCount(); i++) {
			this.expandRow(i);
		}
	}

	/**
	 * Gets the URI of the selected model in the tree, if any
	 * 
	 * @return The string representing the URI of the selected model, or null if
	 *         there is no selection
	 */
	public String getSelectedValue() {
		String selected = null;
		TreePath path = this.getSelectionPath();
		if (path != null) {
			Object selectedObject = path.getLastPathComponent();
			if (selectedObject instanceof String) {
				selected = (String) selectedObject;
			}
		}
		return selected;
	}

	/**
	 * Set the specified URI as the selected node in the tree (if it exists)
	 * 
	 * @param uri
	 */
	public void setSelectedValue(String uri) {
		OntDocumentManagerTreeModel model = (OntDocumentManagerTreeModel) this
				.getModel();
		TreePath treePath = model.getPath(uri);
		if (treePath != null) {
			this.setSelectionPath(treePath);
		} else {
			this.clearSelection();
		}
	}

	/**
	 * Refreshes the tree structure, expanding every branch in the tree
	 */
	public void refresh() {
		OntDocumentManagerTreeModel model = (OntDocumentManagerTreeModel) this
				.getModel();
		Object root = model.getRoot();
		if (root != null) {
			model.fireTreeStructureChanged(root, new TreePath(root));
			for (int i = 0; i < this.getRowCount(); i++) {
				this.expandRow(i);
			}
		}
	}

	/**
	 * Converts a tree node value into text format. Overridden to return the
	 * string for an OntClass First, try to get a label for the class then the
	 * URI
	 */
	public String convertValueToText(Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		String text;
		if (value instanceof String) {
			text = (String) value;
		} else {
			text = value.toString();
		}
		return text;
	}
}
