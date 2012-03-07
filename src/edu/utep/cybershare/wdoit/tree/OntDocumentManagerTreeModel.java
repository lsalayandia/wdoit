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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import edu.utep.cybershare.wdoit.context.State;

/**
 * @author Leonardo Salayandia
 * 
 */
public class OntDocumentManagerTreeModel implements TreeModel {
	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}

	public TreePath getPath(String uri) {
		TreePath ans = null;
		if (uri != null) {
			Vector<String> reversedPath = new Vector<String>();
			reversedPath.add(uri);
			State state = State.getInstance();
			String parent = state.getOWLDocumentParent(uri);
			while (parent != null) {
				reversedPath.add(parent);
				parent = state.getOWLDocumentParent(parent);
			}
			Object[] path = new String[reversedPath.size()];
			for (int i = reversedPath.size() - 1, j = 0; i >= 0; i--, j++) {
				path[j] = reversedPath.get(i);
			}
			ans = new TreePath(path);
		}
		return ans;
	}

	private Vector<String> listChildren(String uri) {
		Vector<String> ans = new Vector<String>();
		if (uri != null && !uri.isEmpty()) {
			State state = State.getInstance();
			Iterator<String> iter = state.listOWLDocumentChildren(uri);
			if (iter != null) {
				for (; iter.hasNext();) {
					String child = iter.next();
					if (child != null && !child.isEmpty())
						ans.add(child);
				}
			}
		}
		return ans;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent != null && parent instanceof String) {
			String parentURI = (String) parent;
			Vector<String> children = this.listChildren(parentURI);
			String[] childrenArray = children.toArray(new String[0]);
			if (index >= 0 && index < childrenArray.length) {
				Arrays.sort(childrenArray);
				return childrenArray[index];
			}
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		int count = 0;
		if (parent != null && parent instanceof String) {
			String parentURI = (String) parent;
			Vector<String> children = this.listChildren(parentURI);
			count = children.size();
		}
		return count;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent != null && parent instanceof String && child != null
				&& child instanceof String) {
			String parentURI = (String) parent;
			String childURI = (String) child;
			Vector<String> children = this.listChildren(parentURI);
			String[] childrenArray = children.toArray(new String[0]);
			Arrays.sort(childrenArray);
			for (int index = 0; index < childrenArray.length; index++) {
				if (childURI.equals(childrenArray[index]))
					return index;
			}
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		State state = State.getInstance();
		String root = state.getBaseWDOURI();
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
	}

	protected void fireTreeStructureChanged(Object source, TreePath path) {
		TreeModelEvent e = new TreeModelEvent(source, path);
		for (TreeModelListener tml : treeModelListeners) {
			tml.treeStructureChanged(e);
		}
	}

}
