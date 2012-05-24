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
package edu.utep.cybershare.wdoit.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Point;
import java.io.*;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

//import edu.utep.cybershare.ciclient.ciconnect.CIClient;
import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoapi.Workspace;
import edu.utep.cybershare.wdoapi.Bookmarks;
import edu.utep.cybershare.wdoapi.WorkspaceFile;
import edu.utep.cybershare.wdoapi.util.Namespace;

/**
 * Maintains state of the application. This class is the main interaction point
 * with the Wdo-API.
 * 
 * @author Leonardo Salayandia
 */
public class State {
	public static final String WDODEFAULT_WKSPC_FILE = "wdoDefaultWorkspace.xml";
	private static State instance = null; // WDO-It has one state instance only
	private boolean showWorkflowTypes; // show workflow types in workflow graph
	private int wdoNextDataNum; // data concept counter labeler
	private int wdoNextMethodNum; // method concept counter labeler
	private Workspace workspace;
	private OntModel selectedOWLDocument; // document currently highlighted in
											// the Workspace hierarchy (either a
											// workflow or an ontology)
	private OntModel selectedOntology; // ontology being edited
	private OntModel selectedWorkflow; // workflow being edited
	private OntClass selectedClass; // class being edited
	private OntClass selectedIndividual; // individual being edited
//	private CIClient connectedCIServer; // a connection to a CI Server

	private String loadURI; // URI of an owl document to load

	public static State getInstance() {
		if (instance == null) {
			instance = new State();
		}
		return instance;
	}

	private State() {
		// bookmarks are separate from the workspace. They are not reset if the
		// workspace is reset
		// bookmarks = null;
		showWorkflowTypes = true;
		resetWorkspace();
//		connectedCIServer = null; // no server connection initially
	}

	/**
	 * Replaces the existing workspace with a new, empty workspace. All loaded
	 * ontologies will be lost.
	 */
	public void resetWorkspace() {
		selectedClass = null;
		selectedOWLDocument = null;
		selectedOntology = null;
		selectedWorkflow = null;
		wdoNextDataNum = 0; // 0 means it is not initialized
		wdoNextMethodNum = 0; // 0 means it is not initialized
		workspace = null;
		// bookmarks are separate from the workspace. They are not reset if the
		// workspace is reset
		// don't think I need to reset the ci server connections
		loadURI = null;
	}

	/**
	 * Checks whether any owl document loaded in the workspace has been modified
	 * 
	 * @return
	 */
	public boolean isModified() {
		if (workspace == null) {
			return false;
		}
		for (Iterator<String> i = this.listOntologyURIs(); i.hasNext();) {
			if (isModified(i.next())) {
				return true;
			}
		}
		for (Iterator<String> i = this.listWorkflowURIs(); i.hasNext();) {
			if (isModified(i.next())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the loaded owl document for the given URI has changed
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isModified(String uri) {
		boolean ans = false;
		if (uri != null && workspace != null) {
			ans = this.workspace.isModified(uri);
		}
		return ans;
	}

	/**
	 * Set whether to display workflow types on SAW graphs
	 * 
	 * @param b
	 */
	public void setShowWorkflowTypes(boolean b) {
		showWorkflowTypes = b;
	}

	/**
	 * Check whether the flag is set to display workflow types on SAW graphs
	 * 
	 * @return
	 */
	public boolean isShowWorkflowTypes() {
		return showWorkflowTypes;
	}

	/**
	 * Get predetermined URI to load.
	 * 
	 * @return
	 */
	public String getURIToLoad() {
		return loadURI;
	}

	/**
	 * Set a predetermined URI to be loaded.
	 * 
	 * @param uri
	 */
	public void setURIToLoad(String uri) {
		if (Namespace.isValid(uri)) {
			this.loadURI = uri;
		} else {
			this.loadURI = null;
		}
	}

	/**
	 * List the URIs of the owl documents that are children of the OWL document
	 * specified by URI according to the hierarchy maintained by the workspace
	 * class
	 * 
	 * @param ontmodelURI
	 * @return
	 */
	public Iterator<String> listOWLDocumentChildren(String ontmodelURI) {
		Iterator<String> ans = null;
		if (workspace != null) {
			ans = workspace.listOWLDocumentChildren(ontmodelURI);
		}
		return ans;
	}

	/**
	 * Get the URI of the owl document that is the parent of the OWL document
	 * specified by URI according to the hierarchy maintained by the workspace
	 * class
	 * 
	 * @param ontmodelURI
	 * @return
	 */
	public String getOWLDocumentParent(String ontmodelURI) {
		String ans = null;
		if (workspace != null) {
			ans = workspace.getOWLDocumentParent(ontmodelURI);
		}
		return ans;
	}

	/**
	 * Initiates the workspace with an empty Wdo with the given name space
	 * 
	 * @param ns Name space of the new Wdo
	 * @param url Location where the new WDO will be stored
	 * @return The new Wdo ontmodel, or null if the workspace was already
	 *         initiated.
	 */
	public OntModel createWDO(String ns, String url) throws Exception {
		if (workspace == null && ns != null) {
			// namespace verification already in place, no need to check. Leo.
			workspace = new Workspace();
			OntModel ontmodel = workspace.createBaseWDO(ns, url);
			setSelectedOntology(ontmodel);
			wdoNextDataNum = 1;
			wdoNextMethodNum = 1;
			selectedClass = null;
			selectedIndividual = null;
			return ontmodel;
		}
		return null;
	}

	/**
	 * Load an ontology or workflow into the workspace from the location
	 * specified by the URL
	 * 
	 * @param url
	 *            The location of the ontology or workflow to load
	 * @param uri
	 *            The URI to use as base for the ontology or workflow to load
	 * @throws Exception
	 */
	public OntModel openOWL(String url, String uri) throws Exception {
		OntModel ontmodel = null;
		if (url != null && uri != null) {
			if (workspace == null) {
				workspace = new Workspace();
			}
			ontmodel = workspace.loadOWL(url, uri);
			if (SAW.isSAW(ontmodel)) {
				this.setSelectedOntology((OntModel) null); // clear the
															// previously set
															// ontology
				this.setSelectedWorkflow(ontmodel);
			} else {
				this.setSelectedWorkflow((OntModel) null); // clear the
															// previously set
															// workflow
				this.setSelectedOntology(ontmodel);
			}
			// If Data and Method URI counters not initialized, initialize now.
			// Is there a way to verify that the numbers are correct? For now
			// not checking.
			if (wdoNextDataNum == 0) {
				wdoNextDataNum = workspace.Wdo.findMaxDataNodeNo();
				wdoNextDataNum++;
			}
			if (wdoNextMethodNum == 0) {
				wdoNextMethodNum = workspace.Wdo.findMaxMethodNodeNo();
				wdoNextMethodNum++;
			}
		}
		return ontmodel;
	}

	/**
	 * Reprocesses the loaded ontologies and workflows loaded in the workspace
	 * to rebuild the ontmodel hierarchy structure. Note that the import
	 * relations between ontmodel documents should be resolved and binded before
	 * calling this method.
	 */
	public void updateOntModelHierarchy() {
		if (workspace != null) {
			workspace.updateOntModelHierarchy();
		}
	}

	/**
	 * List URIs of ontologies loaded in the current workspace.
	 * 
	 * @return An iterator over the list of ontology URIs.
	 */
	public Iterator<String> listOntologyURIs() {
		if (workspace != null) {
			return workspace.listOntologyURIs();
		} else {
			return null;
		}
	}

	/**
	 * Get the Wdo that unifies all the Wdo-related concepts of all the loaded
	 * ontologies in the workspace
	 * 
	 * @return The base ontology of the current workspace
	 */
	public OntModel getBaseWDO() {
		OntModel ans = null;
		if (workspace != null) {
			ans = workspace.getBaseWDO();
		}
		return ans;
	}

	/**
	 * Get the URI of the base Wdo ontology
	 * 
	 * @return The string that represents the URI of the base Wdo of the
	 *         workspace
	 */
	public String getBaseWDOURI() {
		String ans = null;
		if (workspace != null) {
			ans = workspace.getOntModelURI(getBaseWDO());
		}
		return ans;
	}

	/**
	 * Set a comment for the selected OWL Document
	 * 
	 * @param comment
	 *            The comment string to set for the selected ontology
	 */
	public void setSelectedOWLDocumentComment(String comment) {
		workspace.setOntModelComment(getSelectedOWLDocument(), comment);
	}

	/**
	 * Returns the comment for the owl document specified.
	 * 
	 * @param ontmodel
	 * @return
	 */
	public String getOWLDocumentComment(OntModel ontmodel) {
		return workspace.getOntModelComment(ontmodel);
	}

	/**
	 * Returns the comment for the selected OWL Document
	 * 
	 * @return The string comment for the selected OWL Document
	 */
	public String getSelectedOWLDocumentComment() {
		return getOWLDocumentComment(getSelectedOWLDocument());
	}

	/**
	 * Get the URI of the currently selected OWL document
	 * 
	 * @return
	 */
	public String getSelectedOWLDocumentURI() {
		String ans = null;
		if (selectedOWLDocument != null) {
			ans = workspace.getOntModelURI(selectedOWLDocument);
		}
		return ans;
	}

	/**
	 * Get the owl document that is currently marked as selected
	 * 
	 * @return
	 */
	public OntModel getSelectedOWLDocument() {
		return selectedOWLDocument;
	}

	/**
	 * Get the ontology that is currently marked as selected
	 * 
	 * @return the selected ontology
	 */
	public OntModel getSelectedOntology() {
		return selectedOntology;
	}

	/**
	 * Get the URI of the ontology that is currently marked as selected
	 * 
	 * @return The string that represents the URI of the selected ontology
	 */
	public String getSelectedOntologyURI() {
		return workspace.getOntModelURI(selectedOntology);
	}

	/**
	 * 
	 * @param ontmodel
	 * @return
	 */
	public String getOntModelURI(OntModel ontmodel) {
		return workspace.getOntModelURI(ontmodel);
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	public OntModel getOntModel(String uri) {
		OntModel ans = null;
		if (workspace != null) {
			ans = workspace.getOntModel(uri);
		}
		return ans;
	}

	/**
	 * Mark the ontology or workflow that has the specified URI as the selected
	 * OWL document
	 * 
	 * @param uri
	 */
	public void setSelectedOWLDocument(String uri) {
		if (uri != null) {
			if (this.isWorkflow(uri)) {
				this.setSelectedOntology((String) null);
				this.setSelectedWorkflow(uri);
			} else {
				this.setSelectedWorkflow((String) null);
				this.setSelectedOntology(uri);
			}
		}
	}

	/**
	 * Mark the ontology that has the specified URI as the selected ontology
	 * 
	 * @param uri
	 *            The URI of the ontology to mark as selected
	 */
	public void setSelectedOntology(String uri) {
		OntModel ontmodel = null;
		if (uri != null) {
			ontmodel = workspace.getOntModel(uri);
		}
		this.setSelectedOntology(ontmodel);
	}

	/**
	 * Mark ontmodel as the selected ontology
	 * 
	 * @param ontmodel
	 */
	public void setSelectedOntology(OntModel ontmodel) {
		this.selectedOntology = ontmodel;
		this.selectedOWLDocument = ontmodel;
	}

	/**
	 * Checks whether the given uri corresponds to a loaded OntModel ontology.
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isOntology(String uri) {
		boolean ans = false;
		if (workspace != null) {
			ans = workspace.isOntology(uri);
		}
		return ans;
	}

	/**
	 * Get the class that is currently marked as selected
	 * 
	 * @return The selected class
	 */
	public OntClass getSelectedClass() {
		return selectedClass;
	}

	/**
	 * Mark the specified class as the selected class. Note, if there is a
	 * substitute class for cls in the base WDO, use that as the selected class.
	 * 
	 * @param cls
	 *            The class to mark as selected
	 */
	public void setSelectedClass(OntClass cls) {
		if (cls != null) {
			OntModel wdo = this.getBaseWDO();
			OntClass temp = wdo.getOntClass(cls.getURI());
			selectedClass = (temp != null) ? temp : cls;
		} else {
			selectedClass = null;
		}
	}

	/**
	 * Get the individual that is currently marked as selected
	 * 
	 * @return
	 */
	public OntClass getSelectedIndividual() {
		return selectedIndividual;
	}

	/**
	 * Mark the specified individual as the selected individual.
	 * 
	 * @param ind
	 */
	public void setSelectedIndividual(OntClass ind) {
		selectedIndividual = ind;
	}

	/**
	 * List the instances of the type determined by the class marked as
	 * selected. Search for instances in all the workflows loaded in the
	 * workspace.
	 * 
	 * @return
	 */
	public Iterator<OntClass> listInstancesOfSelectedClass() {
		if (selectedClass == null) {
			return null;
		} else {
			ArrayList<OntClass> ans = new ArrayList<OntClass>();
			for (Iterator<String> i = this.listWorkflowURIs(); i.hasNext();) {
				OntModel ontmodel = workspace.getOntModel(i.next());
				Iterator<OntClass> tempiter = SAW.listSAWInstances(ontmodel,
						selectedClass, false);
				if (tempiter != null) {
					for (; tempiter.hasNext();) {
						ans.add(tempiter.next());
					}
				}
			}
			return ans.iterator();
		}
	}

	/**
	 * List all non-anonymous subclasses of wdo:Data included in the baseWDO
	 * 
	 * @return An iterator over the classes
	 */
	public Iterator<OntClass> listDataSubClasses() {
		return workspace.Wdo.listDataSubClasses();
	}

	/**
	 * List all non-anonymous subclasses of wdo:Method included in the baseWDO
	 * 
	 * @return An iterator over the classes
	 */
	public Iterator<OntClass> listMethodSubClasses() {
		return workspace.Wdo.listMethodSubClasses();
	}

	/**
	 * Creates a new wdo:Data subclass in the ontology marked as selected, with
	 * the specified label, and sets it to be the direct child of the specified
	 * superclass. If superclass is null, sets the new subclass to be a direct
	 * child of wdo:Data The actual URI of the class is automatically generated
	 * If superclass is not null but it is not a child of wdo:Data, does not
	 * create the new class
	 * 
	 * @param superclass
	 *            Superclass of the class being created
	 * @param label
	 *            of the class being created
	 */
	public OntClass createDataSubClass(OntClass superclass, String label) {
		String classURI = "d" + wdoNextDataNum++;
		OntClass newClass;
		if (superclass == null) {
			newClass = workspace.Wdo.createDataSubClass(classURI);
		} else {
			newClass = workspace.Wdo.createSubClass(superclass, classURI);
		}
		workspace.Wdo.setClassLabel(newClass, label);
		setSelectedClass(newClass);
		return newClass;
	}

	/**
	 * Creates a new wdo:Method subclass in the ontology marked as selected,
	 * with the specified label, and sets it to be the direct child of the
	 * specified superclass. If the superclass is null, sets the new subclass to
	 * be a direct child of wdo:Method If the superclass is not null but it is
	 * not a child of wdo:Method, does not create the new class
	 * 
	 * @param superclass
	 *            Superclass of the class being created
	 * @param uri
	 *            URI of the class being created
	 */
	public OntClass createMethodSubClass(OntClass superclass, String label) {
		String classURI = "m" + wdoNextMethodNum++;
		OntClass newClass;
		if (superclass == null) {
			newClass = workspace.Wdo.createMethodSubClass(classURI);
		} else {
			newClass = workspace.Wdo.createSubClass(superclass, classURI);
		}
		workspace.Wdo.setClassLabel(newClass, label);
		setSelectedClass(newClass);
		return newClass;
	}

	/**
	 * Add cls to be a direct subclass of newSupercls
	 * 
	 * @param superclass
	 * @param subclass
	 * @return
	 */
	public boolean addSubClass(OntClass superclass, OntClass subclass) {
		return workspace.Wdo.addSubClass(superclass, subclass);
	}

	/**
	 * Move subclass from being a direct subclass of oldSupercls to being a
	 * direct subclass of newSupercls.
	 * 
	 * @param oldsuperclass
	 * @param newsuperclass
	 * @param subclass
	 * @return
	 */
	public boolean moveSubClass(OntClass oldsuperclass, OntClass newsuperclass,
			OntClass subclass) {
		return workspace.Wdo.moveSubClass(oldsuperclass, newsuperclass,
				subclass);
	}

	/**
	 * Set a class label
	 * 
	 * @param cls
	 * @param label
	 */
	public void setClassLabel(OntClass cls, String label) {
		workspace.Wdo.setClassLabel(cls, label);
	}

	/**
	 * Set a class comment
	 * 
	 * @param cls
	 * @param comment
	 */
	public void setClassComment(OntClass cls, String comment) {
		workspace.Wdo.setClassComment(cls, comment);
	}

	/**
	 * Checks if the selected class is a subclass of the wdo:Data class
	 * 
	 * @param ontclass
	 *            Class to check
	 * @return True if the class is a subclass of wdo:Data, false otherwise
	 */
	public boolean isDataSubClass(OntClass ontclass) {
		boolean ans = false;
		if (workspace != null) {
			ans = workspace.Wdo.isDataSubClass(ontclass);
		}
		return ans;
	}

	/**
	 * Checks if the selected class is a subclass of the wdo:Method class
	 * 
	 * @param ontclass
	 *            Class to check
	 * @return True if the class is a subclass of wdo:Method, false otherwise
	 */
	public boolean isMethodSubClass(OntClass ontclass) {
		boolean ans = false;
		if (workspace != null) {
			ans = workspace.Wdo.isMethodSubClass(ontclass);
		}
		return ans;
	}

	/**
	 * Removes the specified class from all the ontologies loaded in the
	 * workspace
	 * 
	 * @param cls
	 *            The class to remove
	 */
	public void removeClassFromWorkspace(OntClass cls) {
		if (cls != null) {
			if (cls.equals(selectedClass)) {
				selectedClass = null;
			}
			workspace.Wdo.removeClassFromWorkspace(cls);
		}
	}

	/**
	 * Removes the specified class from the base WDO. The class has to be a WDO
	 * class, i.e., a subclass of wdo:Data or wdo:Method.
	 * 
	 * @param cls
	 */
	public void removeSelectedWDOClass() throws Exception {
		// check if there is a selected class
		if (selectedClass == null) {
			throw new Exception("No concept selected for removal.");
		}
		// check if the selected class is a wdo class
		if (!isDataSubClass(selectedClass) && !isMethodSubClass(selectedClass)) {
			throw new Exception("Non-WDO concept selected for removal.");
		}
		// check if the selected class is defined in the base WDO
		OntModel wdo = getBaseWDO();
		if (!wdo.isInBaseModel(selectedClass)) {
			throw new Exception(
					"Concept selected for removal not defined in base WDO.");
		}
		// check if the selected class has subclasses that are defined in the
		// base WDO
		for (ExtendedIterator<OntClass> i = selectedClass.listSubClasses(false); i
				.hasNext();) {
			OntClass temp = i.next();
			if (wdo.isInBaseModel(temp)) {
				throw new Exception(
						"Concept selected for removal has children.");
			}
		}
		// check if the selected class has WDO relations
		if (listLeftClasses(selectedClass).hasNext()
				|| listRightClasses(selectedClass).hasNext()) {
			throw new Exception(
					"Concept selected has relations to other concepts.");
		}
		// check if the selected class is being instantiated in a loaded
		// workflow
		if (this.listInstancesOfSelectedClass().hasNext()) {
			throw new Exception(
					"Concept selected is being used in a loaded workflow.");
		}
		this.workspace.Wdo.removeClassFromWorkspace(selectedClass);
		selectedClass = null;
	}

	/**
	 * Adds a relation between the two classes specified. If centerCls is a
	 * wdo:Data subclass and leftCls is a wdo:Method subclass, adds the relation
	 * centerCls wdo:isOutputOf leftCls. If centerCls is a wdo:Method subclass
	 * and leftCls is a wdo:Data subclass, adds the relation centerCls
	 * wdo:getsInputFrom leftCls. Otherwise it does nothing
	 * 
	 * @param centerCls
	 *            class that is rendered on the center in the Wdo relation panel
	 * @param leftCls
	 *            class that is rendered on the left in the Wdo relation panel
	 */
	public void addLeftRelation(OntClass centerCls, OntClass leftCls) {
		if (isDataSubClass(centerCls) && isMethodSubClass(leftCls)) {
			workspace.Data.addIsOutputOfRelation(centerCls, leftCls);
		} else if (isMethodSubClass(centerCls) && isDataSubClass(leftCls)) {
			workspace.Method.addHasInputRelation(centerCls, leftCls);
		}
	}

	/**
	 * Removes a relation between the two classes specified. If centerCls is a
	 * wdo:Data subclass and leftCls is a wdo:Method subclass, removes relation
	 * centerCls wdo:isOutputOf leftCls. If centerCls is a wdo:Method subclass
	 * and leftCls is a wdo:Data subclass, removes the relation centerCls
	 * wdo:getsInputFrom leftCls. Otherwise it does nothing
	 * 
	 * @param centerCls
	 *            center class of the relation to remove as rendered in the Wdo
	 *            relation panel
	 * @param leftCls
	 *            left class of the relation to remove as rendered in the Wdo
	 *            relation panel
	 */
	public void removeLeftRelation(OntClass centerCls, OntClass leftCls) {
		if (isDataSubClass(centerCls) && isMethodSubClass(leftCls)) {
			workspace.Data.removeIsOutputOfRelation(centerCls, leftCls);
		} else if (isMethodSubClass(centerCls) && isDataSubClass(leftCls)) {
			workspace.Method.removeHasInputRelation(centerCls, leftCls);
		}
	}

	/**
	 * Adds a relation between the two classes specified. If centerCls is a
	 * wdo:Data subclass and rightCls is a wdo:Method subclass, adds the
	 * relation centerCls wdo:isInputTo rightCls. If centerCls is a wdo:Method
	 * subclass and rightCls is a wdo:Data subclass, adds the relation centerCls
	 * wdo:outputs rightCls. Otherwise does nothing
	 * 
	 * @param centerCls
	 *            class that is rendered on the center in the Wdo relation panel
	 * @param rightCls
	 *            class that is rendered on the right in the Wdo relation panel
	 */
	public void addRightRelation(OntClass centerCls, OntClass rightCls) {
		if (isDataSubClass(centerCls) && isMethodSubClass(rightCls)) {
			workspace.Data.addIsInputToRelation(centerCls, rightCls);
		} else if (isMethodSubClass(centerCls) && isDataSubClass(rightCls)) {
			workspace.Method.addHasOutputRelation(centerCls, rightCls);
		}
	}

	/**
	 * Removes a relation between the two classes specified. If centerCls is a
	 * wdo:Data subclass and rightCls is a wdo:Method subclass, removes the
	 * relation centerCls wdo:isInputTo rightCls. If centerCls is a wdo:Method
	 * subclass and rightCls is a wdo:Data subclass, removes the relation
	 * centerCls wdo:outputs rightCls. Otherwise does nothing
	 * 
	 * @param centerCls
	 *            center class of the relation to remove as rendered in the Wdo
	 *            relation panel
	 * @param rightCls
	 *            right class of the relation to remove as rendered in the Wdo
	 *            relation panel
	 */
	public void removeRightRelation(OntClass centerCls, OntClass rightCls) {
		if (isDataSubClass(centerCls) && isMethodSubClass(rightCls)) {
			workspace.Data.removeIsInputToRelation(centerCls, rightCls);
		} else if (isMethodSubClass(centerCls) && isDataSubClass(rightCls)) {
			workspace.Method.removeHasOutputRelation(centerCls, rightCls);
		}
	}

	/**
	 * Returns a list of related classes that are typically rendered to the left
	 * side of the specified class in a workflow graph. If cls is a subclass of
	 * wdo:Data, returns related classes through property wdo:isOutputOf. If cls
	 * is a subclass of wdo:Method, returns related classes through property
	 * wdo:getsInputFrom. Returns empty list otherwise.
	 * 
	 * @param cls
	 *            Class for which to list input related classes
	 * @return List of related classes
	 */
	public Iterator<OntClass> listLeftClasses(OntClass cls) {
		if (isDataSubClass(cls)) {
			return workspace.Data.listIsOutputOfMethods(cls);
		} else if (isMethodSubClass(cls)) {
			return workspace.Method.listHasInputData(cls);
		} else {
			// return empty iterator
			return workspace.Wdo.listRelatedClasses(null, null);
		}
	}

	/**
	 * Returns a list of related classes that are typically rendered to the
	 * right side of the specified class in a workflow graph. If cls is a
	 * subclass of wdo:Data, returns related classes through property
	 * wdo:inInputTo. If cls is a subclass of wdo:Method, returns related
	 * classes through property wdo:outputs. Returns empty list otherwise.
	 * 
	 * @param cls
	 *            Class for which to list output related classes
	 * @return List of related classes
	 */
	public Iterator<OntClass> listRightClasses(OntClass cls) {
		if (isDataSubClass(cls)) {
			return workspace.Data.listIsInputToMethods(cls);
		} else if (isMethodSubClass(cls)) {
			return workspace.Method.listHasOutputData(cls);
		} else {
			// return empty iterator
			return workspace.Wdo.listRelatedClasses(null, null);
		}
	}

	/**
	 * Create a workflow for the target class specified.
	 * 
	 * @param uri The URI for the new workflow
	 * @param url The location where the new workflow will be stored.          
	 * @return An OntModel for the newly created workflow.
	 */
	public OntModel createWorkflow(String ns, String url) {
		// namespace verification already in place, no need to check. Leo.
		OntModel ontmodel = workspace.Saw.createSAW(ns, url);
		this.setSelectedWorkflow(ontmodel);
		return ontmodel;
	}

	/**
	 * Checks whether the given uri corresponds to a loaded OntModel workflow.
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isWorkflow(String uri) {
		boolean ans = false;
		if (workspace != null) {
			ans = workspace.isWorkflow(uri);
		}
		return ans;
	}

	/**
	 * List URIs of workflows loaded in the current workspace.
	 * 
	 * @return An iterator over the list of workflow URIs.
	 */
	public Iterator<String> listWorkflowURIs() {
		if (workspace != null) {
			return workspace.listWorkflowURIs();
		} else {
			return null;
		}
	}

	/**
	 * Get the workflow that is currently marked as selected
	 * 
	 * @return the selected workflow
	 */
	public OntModel getSelectedWorkflow() {
		return selectedWorkflow;
	}

	/**
	 * Mark the workflow that has the specified URI as the selected workflow
	 * 
	 * @param uri
	 *            The URI of the ontology to mark as selected
	 */
	public void setSelectedWorkflow(String uri) {
		OntModel ontmodel = null;
		if (uri != null) {
			ontmodel = workspace.getOntModel(uri);
		}
		this.setSelectedWorkflow(ontmodel);
	}

	/**
	 * Mark ontmodel as the selected workflow
	 * 
	 * @param ontmodel
	 */
	public void setSelectedWorkflow(OntModel ontmodel) {
		this.selectedWorkflow = ontmodel;
		this.selectedOWLDocument = ontmodel;
	}

	/**
	 * Get the URI of the workflow that is currently marked as selected
	 * 
	 * @return The string that represents the URI of the selected workflow
	 */
	public String getSelectedWorkflowURI() {
		String ans = null;
		if (workspace != null) {
			ans = workspace.getOntModelURI(selectedWorkflow);
		}
		return ans;
	}

	/**
	 * Set the label of an individual
	 * 
	 * @param ind
	 * @param label
	 */
	public void setIndividualLabel(OntClass ind, String label) {
		workspace.Saw.setSAWInstanceLabel(ind, label);
	}

	/**
	 * Set the comment of an individual
	 * 
	 * @param ind
	 * @param comment
	 */
	public void setIndividualComment(OntClass ind, String comment) {
		workspace.Saw.setSAWInstanceComment(ind, comment);
	}

	/**
	 * Set the type of an individual
	 * 
	 * @param ind
	 * @param type
	 */
	public void setIndividualType(OntClass ind, OntClass type) {
		workspace.Saw.setSAWInstanceType(ind, type);
	}

	/**
	 * Set the coordinate for an individual.
	 * 
	 * @param ind
	 * @param coord
	 */
	public void setIndividualCoordinate(OntClass ind, Point coord) {
		workspace.Saw.setInstanceCoordinate(ind, coord);
	}

	public void setIsInputTo(OntClass dataInd, OntClass methodInd) {
		workspace.Saw.setIsInputTo(this.getSelectedWorkflow(), dataInd,
				methodInd);
	}

	public void removeIsInputTo(OntClass dataInd, OntClass methodInd) {
		workspace.Saw.removeIsInputTo(this.getSelectedWorkflow(), dataInd,
				methodInd);
	}

	public void setIsOutputOf(OntClass dataInd, OntClass methodInd) {
		workspace.Saw.setIsOutputOf(this.getSelectedWorkflow(), dataInd,
				methodInd);
	}

	public void removeIsOutputOf(OntClass dataInd, OntClass methodInd) {
		workspace.Saw.removeIsOutputOf(this.getSelectedWorkflow(), dataInd,
				methodInd);
	}

	public void addHasInput(OntClass methodInd, OntClass dataInd) {
		workspace.Saw.addHasInput(this.getSelectedWorkflow(), methodInd,
				dataInd);
	}

	public void removeHasInput(OntClass methodInd, OntClass dataInd) {
		workspace.Saw.removeHasInput(this.getSelectedWorkflow(), methodInd,
				dataInd);
	}

	public void addHasOutput(OntClass methodInd, OntClass dataInd) {
		workspace.Saw.addHasOutput(this.getSelectedWorkflow(), methodInd,
				dataInd);
	}

	public void removeHasOutput(OntClass methodInd, OntClass dataInd) {
		workspace.Saw.removeHasOutput(this.getSelectedWorkflow(), methodInd,
				dataInd);
	}

	public OntClass createSourceSinkIndividual(String uri, Point coord) {
		return workspace.Saw.createSourceSAWInstance(
				this.getSelectedWorkflow(), uri, coord);
	}

	public OntClass createNodeIndividual(OntClass nodeType, String uri,
			Point coord) {
		return workspace.Saw.createNodeSAWInstance(this.getSelectedWorkflow(),
				nodeType, uri, coord);
	}

	public OntClass createDataIndividual(OntClass dataType, String uri,
			OntClass fromMethod, OntClass toMethod, Point fromCoord,
			Point toCoord) {
		return workspace.Saw.createDataIndividual(this.getSelectedWorkflow(),
				dataType, uri, fromMethod, toMethod, fromCoord, toCoord);
	}

	public Individual createInferenceEngineIndividual(String uri) {
		return workspace.Saw.createInferenceEngineIndividual(
				this.getSelectedWorkflow(), uri);
	}

	public Individual createPMLSourceInstance(String uri) {
		return workspace.Saw.createSourceOWLInstance(
				this.getSelectedWorkflow(), uri);
	}

	public Individual createFormatIndividual(String uri) {
		return workspace.Saw.createFormatIndividual(this.getSelectedWorkflow(),
				uri);
	}

	public void setFormat(OntClass dataInd, Individual formatInd) {
		workspace.Saw.setFormat(dataInd, formatInd);
	}

	public void setInferenceEngine(OntClass methodInd, Individual ieInd) {
		workspace.Saw.setInferenceEngine(methodInd, ieInd);
	}

	public void setPMLSource(OntClass methodInd, Individual srcInd) {
		workspace.Saw.setPMLSource(methodInd, srcInd);
	}

	/**
	 * @see SAW.setDetailedBy(Individual, Individual)
	 * @param methodInd
	 * @param sawInd
	 */
	public void setDetailedBy(OntClass methodInd, OntClass sawInd) {
		workspace.Saw.setDetailedBy(methodInd, sawInd);
	}

	/**
	 * @see SAW.removeDetailedBy(Individual)
	 * @param methodInd
	 */
	public void removeDetailedBy(OntClass methodInd) {
		workspace.Saw.removeDetailedBy(methodInd);
	}

	/**
	 * List the instances of wdo:Method and its subclasses that are declared in
	 * the selected workflow.
	 * 
	 * @return
	 */
	public Iterator<OntClass> listMethodIndividuals() {
		return listMethodIndividuals(selectedWorkflow);
	}

	/**
	 * List the instances of wdo:Method and its subclasses that are declared in
	 * the workflow specified.
	 * 
	 * @param workflow
	 * @return
	 */
	public Iterator<OntClass> listMethodIndividuals(OntModel workflow) {
		Iterator<OntClass> iter = null;
		if (workflow != null) {
			iter = workspace.Saw.listMethodSAWInstances(workflow);
		}
		return iter;
	}

	/**
	 * List the instances of wdo:Data and its subclasses that are declared in
	 * the selected workflow.
	 * 
	 * @return
	 */
	public Iterator<OntClass> listDataIndividuals() {
		return listDataIndividuals(selectedWorkflow);
	}

	/**
	 * List the instances of wdo:Data and its subclasses that are declared in
	 * the workflow specified.
	 * 
	 * @param workflow
	 * @return
	 */
	public Iterator<OntClass> listDataIndividuals(OntModel workflow) {
		Iterator<OntClass> iter = null;
		if (workflow != null) {
			iter = workspace.Saw.listDataSAWInstances(workflow);
		}
		return iter;
	}

	/**
	 * List the instances of pmlp:Source and its subclasses that are declared in
	 * the selected workflow.
	 * 
	 * @return
	 */
	public Iterator<OntClass> listPMLSourceIndividuals() {
		return listPMLSourceIndividuals(selectedWorkflow);
	}

	/**
	 * Lists the instances of pmlp:Source and its subclasses that are declared
	 * in the workflow specified.
	 * 
	 * @param workflow
	 * @return
	 */
	public Iterator<OntClass> listPMLSourceIndividuals(OntModel workflow) {
		Iterator<OntClass> iter = null;
		if (workflow != null) {
			iter = workspace.Saw.listSourceSAWInstances(workflow);
		}
		return iter;
	}

	/**
	 * 
	 * @param ind
	 */
	public void removeIndividual(OntClass ind) {
		this.workspace.Saw.removeInstance(selectedWorkflow, ind);
	}

	/**
	 * Returns a known location (URL) for the give ontmodel
	 * 
	 * @param ontmodel
	 * @return A string representation of a URL, or null is a location is not
	 *         found for the given ontmodel
	 */
	public String getOWLDocumentURL(OntModel ontmodel) {
		String ans = null;
		if (workspace != null) {
			ans = workspace.getOntModelURL(ontmodel);
		}
		return ans;
	}

	/**
	 * Saves the specified ontmodel to the specified location (URL). If a
	 * location is not specified, tries to resolve to a previously referenced
	 * location for that ontmodel.
	 * 
	 * @param ontmodel
	 * @param location
	 * @throws Exception
	 *             if URL is not specified and not previously referenced, or if
	 *             a file could not be created at that location
	 */
	public void saveOWLDocument(OntModel ontmodel, String url) throws Exception {
		this.workspace.saveOntModelToFile(ontmodel, url);
	}

	/**
	 * getBookmarks - returns the bookmark list. If the list is empty - there is
	 * an attempt to read the default bookmark file
	 * 
	 * @author Aida Gandara
	 */
	public Bookmarks getBookmarks() {
		/**
		 * PORTED TO NEW CODE if(bookmarks == null) bookmarks = new Bookmarks();
		 * 
		 * return bookmarks;
		 **/

		return null;
	}

	/*
	 * LoadWorkspaceFile - reads uses the workspaceFileIO object to open up up a
	 * workspace file and load it into the workspace First the current workspace
	 * is closed
	 * 
	 * @author agandara1
	 * 
	 * @param filename the file to be loaded
	 * 
	 * // public void loadWorkspaceFile( String fileName ) throws Exception{ //
	 * close the current workspace // get the uris for the given workspace file
	 * PORTED TO NEW FRAMEWORK ArrayList<String> urLList =
	 * workspaceFileIO.readWorkspaceFile(fileName); ListIterator<String>
	 * listIterator = urLList.listIterator(); MainUI mainUI =
	 * MainUI.getInstance(); if(urLList != null){ // set the current
	 * workspaceFileName = fileName;
	 * 
	 * // load the uris - one by one while(listIterator.hasNext()){ String url =
	 * listIterator.next(); String lcurl = url.toLowerCase();
	 * if(lcurl.startsWith(FILE_HEADER)){ OpenFileURI(url, mainUI); } else
	 * OpenURIAction.openOWLAction(url, url, mainUI); } } }
	 * 
	 * private void OpenFileURI(String uri, MainUI mainUI){
	 * 
	 * // SIMILAR to load file for OpenFileAction class // NOTE - expects the
	 * last part to be the filename // used for the uri int idx1 =
	 * uri.lastIndexOf("/") + 1; if(idx1 < 0) idx1 = uri.lastIndexOf("\\") + 1;
	 * if(idx1 < 0) idx1 = FILE_HEADER.length(); int idx2 = uri.lastIndexOf("."
	 * + OWL_FILE_EXTENSION); if (idx2 < 0) idx2 = uri.length()-1; // if neither
	 * / or \ or .owl are found we use the entire // string except the
	 * FILE_HEADER String url = HTTP_HEADER + uri.substring(idx1, idx2);
	 * OpenURIAction.openOWLAction(url, uri, mainUI); }
	 */

	/**
	 * saveWorkspaceToFile - saves the workspace to the specified file
	 * 
	 * @author agandara1
	 * @param filename
	 *            the file to be saved to
	 */
	public void saveWorkspaceToFile(String fileName) throws IOException {
		// save to the given file
		WorkspaceFile.exportWorkspace(workspace, fileName);
	}

	/**
	 * saveWorkspaceToFile - saves the workspace to the specified file
	 * 
	 * @author agandara1
	 * @param filename
	 *            the file to be saved to
	 */
	public void saveToDefaultWorkspaceFile() throws IOException {
		// set exportFile to PATH/lib/wdoDefaultWorkspace.xml
		String exportFile = "file:lib/" + WDODEFAULT_WKSPC_FILE;
		WorkspaceFile.exportWorkspace(workspace, exportFile);
	}

	/**
	 * isWorkspaceEmpty - return true if the workspace is empty
	 * 
	 * @author agandara1
	 */
	public boolean isWorkspaceEmpty() {
		return (workspace == null);
	}

//	public CIClient getSelectedCIServer() {
//		return connectedCIServer;
//	}
//
//	/**
//	 * Set the connected server value to the one the system is or is not
//	 * connected to (also used to clear the setting)
//	 * 
//	 * @param ontmodel
//	 */
//	public void setSelectedCIServer(CIClient aClient) {
//		this.connectedCIServer = aClient;
//	}

}
