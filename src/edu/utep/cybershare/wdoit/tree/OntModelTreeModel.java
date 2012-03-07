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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;

/**
 * @author Leonardo Salayandia
 * 
 */
public class OntModelTreeModel implements TreeModel {
	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
	private Object root;
	private OntModel ontmodel;
	private Filter<OntClass> subClassFilter;
	private Filter<OntClass> rootClassFilter;

	public OntModelTreeModel() {
		super();
		this.ontmodel = null;
		this.root = new String("Thing"); // by default, the tree model is set to
											// read a concept tree, not a wdo
											// tree
		this.subClassFilter = new ConceptHierarchyFilter(ontmodel);
		this.rootClassFilter = new RootClassFilter(ontmodel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return root;
	}

	protected void setRoot(OntClass root) {
		this.root = root;
		this.subClassFilter = new WDOHierarchyFilter();
	}

	protected void setOntModel(OntModel ontmodel) {
		this.ontmodel = ontmodel;
		this.subClassFilter = new ConceptHierarchyFilter(ontmodel);
		this.rootClassFilter = new RootClassFilter(ontmodel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.
	 * TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.
	 * TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}

	private Iterator<OntClass> setChildrenIterator(Object parent, boolean sort) {
		Iterator<OntClass> ans = null;
		// if parent is owl:Thing
		if (parent instanceof String && ontmodel != null) {
			ans = ontmodel.listClasses().filterDrop(this.rootClassFilter);
		}
		// else if parent is not owl:Thing
		else if (parent instanceof OntClass) {
			OntClass cls = (OntClass) parent;
			if (cls.canAs(OntClass.class)) {
				ans = cls.listSubClasses(true).filterDrop(this.subClassFilter);
			}
		}

		// sort iterator if requested
		if (sort) {
			ans = sortIterator(ans);
		}

		return ans;
	}

	private Iterator<OntClass> sortIterator(Iterator<OntClass> iter) {
		Iterator<OntClass> ans = iter;
		if (iter != null) {
			// dump iterator into hash map (note: any duplicates are replaced in
			// hashmap)
			HashMap<String, OntClass> concepts = new HashMap<String, OntClass>();
			for (; iter.hasNext();) {
				OntClass cls = (OntClass) iter.next();
				concepts.put(cls.getURI(), cls);
			}
			// create an array of string with sorted keys of the hashmap
			String[] conceptsURI = concepts.keySet().toArray(new String[0]);
			Arrays.sort(conceptsURI);

			// fill a vector according to the sorted array, and return the
			// resulting iterator
			Vector<OntClass> sortedConcepts = new Vector<OntClass>();
			for (String conceptURI : conceptsURI) {
				sortedConcepts.add(concepts.get(conceptURI));
			}
			ans = sortedConcepts.iterator();
		}
		return ans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		OntClass ans = null;
		Iterator<OntClass> iter = this.setChildrenIterator(parent, true);
		if (iter != null) {
			Vector<OntClass> children = new Vector<OntClass>();
			for (; iter.hasNext();) {
				OntClass child = (OntClass) iter.next();
				children.add(child);
			}
			if (index >= 0 && index < children.size()) {
				ans = children.get(index);
			}
		}
		return ans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		int ans = -1;
		Iterator<OntClass> iter = this.setChildrenIterator(parent, false);
		if (iter != null) {
			ans = 0;
			for (; iter.hasNext();) {
				iter.next();
				ans++;
			}
		}
		return ans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 * java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {
		int index = -1;
		Iterator<OntClass> iter = this.setChildrenIterator(parent, true);
		if (iter != null && child instanceof OntClass) {
			boolean found = false;
			while (iter.hasNext() && !found) {
				index++;
				OntClass temp = (OntClass) iter.next();
				found = temp.equals(child);
			}
		}
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		Iterator<OntClass> iter = setChildrenIterator(node, false);
		if (iter == null) {
			return true;
		} else {
			return !iter.hasNext();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
	 * java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {

	}

	protected void fireTreeNodeInserted(Object source, TreePath path,
			OntClass child) {
		TreeModelEvent e = new TreeModelEvent(source, path,
				new int[] { getIndexOfChild(source, child) },
				new Object[] { child });
		for (TreeModelListener tml : treeModelListeners) {
			tml.treeNodesInserted(e);
		}
	}

	protected void fireTreeNodeRemoved(Object source, TreePath path,
			OntClass child) {
		TreeModelEvent e = new TreeModelEvent(source, path,
				new int[] { getIndexOfChild(source, child) },
				new Object[] { child });
		for (TreeModelListener tml : treeModelListeners) {
			tml.treeNodesRemoved(e);
		}
	}

	protected void fireTreeStructureChanged(Object source, TreePath path) {
		TreeModelEvent e = new TreeModelEvent(source, path);
		for (TreeModelListener tml : treeModelListeners) {
			tml.treeStructureChanged(e);

		}
	}

	/**
	 * Filter used to display a WDO hierarchy tree, i.e., rooted on wdo:data or
	 * wdo:method Includes classes from all submodels, i.e., including the ones
	 * imported from other ontologies Filters out the upper-level wdo classes
	 */
	private class WDOHierarchyFilter extends Filter<OntClass> {
		@Override
		public boolean accept(OntClass cls) {
			if (cls.isAnon())
				return true; // drop anon classes
			if (cls.getNameSpace().startsWith(WDO_Metamodel.WDO_URI))
				return true; // drop UL classes
			if (cls.getNameSpace().startsWith(WDO_Metamodel.PMLP_URI))
				return true;
			if (cls.getNameSpace().startsWith(WDO_Metamodel.OWL_URI))
				return true;
			return false;
		}

	}

	/**
	 * Filter used to display a general hierarchy tree for an ontmodel, i.e.,
	 * rooted on owl:thing If the tree is for a general ontology, includes
	 * classes only from the specified ontmodel, i.e., not imported classes If
	 * it is a wdo tree, it includes imported classes
	 */
	private class ConceptHierarchyFilter extends Filter<OntClass> {
		private OntModel ontmodel;

		ConceptHierarchyFilter(OntModel ontmodel) {
			this.ontmodel = ontmodel;
		}

		@Override
		public boolean accept(OntClass cls) {
			if (cls.isAnon())
				return true; // drop anon classes
			if (root instanceof String && ontmodel != null
					&& !ontmodel.isInBaseModel(cls))
				return true; // drop classes that are not in the base ontmodel
			return false;
		}
	}

	/**
	 * Filter used to process the root classes of both hierarchies rooted on
	 * owl:thing, and hierarchies rooted on wdo-ul
	 */
	private class RootClassFilter extends Filter<OntClass> {
		private OntModel ontmodel;

		RootClassFilter(OntModel ontmodel) {
			this.ontmodel = ontmodel;
		}

		public boolean accept(final OntClass cls) {
			// if ontclass is anonymous, drop it
			if (cls.isAnon())
				return true;
			// if ontclass is wdo:data, do not drop it
			if (cls.getURI().equals(WDO_Metamodel.DATA_URI))
				return false;
			// if ontclass is wdo:method, do not drop it
			if (cls.getURI().equals(WDO_Metamodel.METHOD_URI))
				return false;
			// if ontclass is not in the base ontmodel, drop it
			if (ontmodel != null && !ontmodel.isInBaseModel(cls))
				return true;
			// if ontclass does not have superclasses, do not drop it
			if (!cls.hasSuperClass())
				return false;
			try {
				// if it has superclasses and at least one is non-anonymous,
				// drop it
				for (ExtendedIterator<OntClass> iter = cls
						.listSuperClasses(true); iter.hasNext();) {
					Resource temp = (Resource) iter.next();
					if (!temp.isAnon()) {
						return true;
					}
				}
			} catch (ConversionException ex) {
				// assuming this exception happens when the superclass is
				// defined in another ontmodel.
				// in this case, do not drop the class because it could still be
				// a root class with respect to this model.
				// notice: if cls has more than one superclass and the first in
				// the iterator is the external one, it won't be dropped,
				// eventhough it should.
			}
			return false;
		}
	}
}
