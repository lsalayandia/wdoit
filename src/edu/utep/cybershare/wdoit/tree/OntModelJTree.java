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

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoit.action.OntologyTreeDeleteKeyListener;
import edu.utep.cybershare.wdoit.action.OntologyTreeDragGestureListener;
import edu.utep.cybershare.wdoit.action.OntologyTreeDropTargetListener;
import edu.utep.cybershare.wdoit.action.OntologyTreeSelectionListener;

/**
 * @author Leonardo Salayandia
 * 
 */
public class OntModelJTree extends JTree {
	private static final long serialVersionUID = 1L;
	private TreePath bookmarkedPath = null;

	/**
	 * 
	 * @param treeType
	 */
	public OntModelJTree() {
		super(new OntModelTreeModel());
		this.setRootVisible(false);
		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.setEditable(false); // for now, tree nodes cannot be edited
		this.setDragEnabled(false); // automatic drag disabled
		this.setDropMode(DropMode.ON);

		ToolTipManager.sharedInstance().registerComponent(this);
		this.setCellRenderer(new OntModelJTreeCellRenderer());

		// set up listeners for drag and drop operations
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY_OR_MOVE,
				new OntologyTreeDragGestureListener());
		this.setDropTarget(new DropTarget(this,
				DnDConstants.ACTION_COPY_OR_MOVE,
				new OntologyTreeDropTargetListener()));

		// set up mouse listeners
		this.addMouseListener(new OntologyTreeSelectionListener());

		// set up key listeners
		this.addKeyListener(new OntologyTreeDeleteKeyListener());

		// expand all the tree by default
		for (int i = 0; i < this.getRowCount(); i++) {
			this.expandRow(i);
		}
	}

	/**
	 * Set the tree for harvest concept selection or not. If set, multiple
	 * selection is enabled
	 * 
	 * @param b
	 */
	public void setHarvestSelection(boolean b) {
		this.getSelectionModel().setSelectionMode(
				(b) ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
						: TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	/**
	 * Gets the concept selected in the tree, if any
	 * 
	 * @return The selected concept, or null if there is no selection
	 */
	public OntClass getSelectedValue() {
		OntClass selectedConcept = null;
		TreePath path = this.getSelectionPath();
		if (path != null) {
			Object selectedObject = path.getLastPathComponent();
			if (selectedObject instanceof OntClass) {
				selectedConcept = (OntClass) selectedObject;
			}
		}
		return selectedConcept;
	}

	/**
	 * Bookmark the currently selected value.
	 */
	public void setSelectedValueBookmark() {
		bookmarkedPath = this.getSelectionPath();
	}

	/**
	 * Restore a previously bookmarked value as the selected value.
	 */
	public void restoreSelectedValueBookmark() {
		this.setSelectionPath(bookmarkedPath);
	}

	/**
	 * Gets the root concept of the tree
	 * 
	 * @return The root concept of the tree
	 */
	public OntClass getRoot() {
		OntClass ans = null;
		Object root = this.getModel().getRoot();
		if (root instanceof OntClass) {
			ans = (OntClass) root;
		}
		return ans;
	}

	/**
	 * 
	 * @param root
	 */
	public void setRoot(OntClass root) {
		OntModelTreeModel model = (OntModelTreeModel) this.getModel();
		model.setRoot(root);
		this.refresh();
	}

	/**
	 * 
	 * @param ontmodel
	 */
	public void setOntModel(OntModel ontmodel) {
		OntModelTreeModel model = (OntModelTreeModel) this.getModel();
		model.setOntModel(ontmodel);
		this.refresh();
	}

	/**
	 * Clears the current selection of this tree if any. Note: Bookmarks the
	 * current selection before it clears it
	 */
	public void clearSelection() {
		super.clearSelection();
	}

	/**
	 * Removes the currently selected concept. If there is no selection, then
	 * does nothing
	 */
	public void removeSelectedConcept() {
		TreePath selectedPath = this.getSelectionPath();
		if (selectedPath != null) {
			OntClass selectedConcept = this.getSelectedValue();
			if (selectedConcept != null) {
				TreePath parentPath = selectedPath.getParentPath();
				Object parentConcept = parentPath.getLastPathComponent();

				OntModelTreeModel treemodel = (OntModelTreeModel) this
						.getModel();
				treemodel.fireTreeNodeRemoved(parentConcept, parentPath,
						selectedConcept);
			}
		}
	}

	/**
	 * Refreshes the tree structure, expanding every branch in the tree
	 */
	public void refresh() {
		OntModelTreeModel model = (OntModelTreeModel) this.getModel();
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
		if (value instanceof OntClass) {
			text = WDO.getClassQName((OntClass) value);
		} else {
			text = value.toString();
		}
		return text;
	}
}