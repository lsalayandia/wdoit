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
package edu.utep.cybershare.wdoapi;

import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
//import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;

/**
 * @author Leonardo Salayandia
 * 
 */
public class WDO {
	private final Workspace workspace;

	protected WDO(Workspace wkspc) {
		this.workspace = wkspc;
	}

	// private void setSubClassOrder(OntClass cls, String order) {
	// if (cls != null) {
	// OntModel ontmodel = cls.getOntModel();
	// DatatypeProperty hasSubclassOrder =
	// ontmodel.getDatatypeProperty(WDO_Metamodel.HAS_SUBCLASS_ORDER_URI);
	// if (hasSubclassOrder != null) {
	// cls.removeAll(hasSubclassOrder);
	// cls.addProperty(hasSubclassOrder, order);
	// }
	// }
	// }
	//
	// private String getSubClassOrder(OntClass cls) {
	// String ans = null;
	// if (cls != null) {
	// OntModel ontmodel = cls.getOntModel();
	// DatatypeProperty hasSubclassOrder =
	// ontmodel.getDatatypeProperty(WDO_Metamodel.HAS_SUBCLASS_ORDER_URI);
	// if (hasSubclassOrder != null) {
	// Statement stmt = cls.getProperty(hasSubclassOrder);
	// if (stmt != null) {
	// ans = stmt.getString();
	// }
	// }
	// }
	// return ans;
	// }
	//
	// private String reorderSubClasses(String order, String refURI, String
	// moveURI) {
	// String newOrder = order;
	// if (newOrder != null) {
	// String[] elems = newOrder.split(",");
	// int refIndex = -1;
	// if (refURI != null) {
	// for (int i=0; i<elems.length; i++) {
	// if (elems[i].equals(refURI)) {
	// refIndex = i;
	// break;
	// }
	// }
	// }
	//
	// }
	//
	// return newOrder;
	// }

	/**
	 * Create a new Wdo class and set it as a subclass of the specified
	 * superclass.
	 * 
	 * @param superClass
	 *            The class to set as a superclass of the new class
	 * @param uri
	 *            The URI to assign to the new ontclass
	 * @return The newly created subclass
	 */
	public OntClass createSubClass(OntClass superClass, String uri) {
		OntClass newclass = null;
		if (superClass != null) {
			// format relative URI to absolute URI
			String absURI;
			if (uri.indexOf("#") < 0) {
				absURI = this.workspace.getOntModelURI(this.workspace
						.getBaseWDO()) + "#" + uri;
			} else {
				absURI = uri;
			}
			newclass = workspace.getBaseWDO().createClass(absURI);
			if (newclass != null) {
				newclass.setSuperClass(superClass);
			}
			String tempURI = absURI.substring(0, absURI.indexOf("#"));
			this.workspace.setModified(tempURI, true);
		}
		return newclass;
	}

	/**
	 * Create a new ontclass and set it as a direct subclass of the wdo:Data
	 * ontclass
	 * 
	 * @param uri
	 *            The URI of the new class
	 * @return The ontclass created
	 */
	public OntClass createDataSubClass(String uri) {
		OntClass data = workspace.getBaseWDO().getOntClass(
				WDO_Metamodel.DATA_URI);
		return this.createSubClass(data, uri);
	}

	/**
	 * Create a new ontlcass and set it as a direct subclass of the wdo:Method
	 * ontclass
	 * 
	 * @param uri
	 *            The URI of the new class
	 * @return The ontclass created.
	 */
	public OntClass createMethodSubClass(String uri) {
		OntClass method = workspace.getBaseWDO().getOntClass(
				WDO_Metamodel.METHOD_URI);
		return this.createSubClass(method, uri);
	}

	/**
	 * Checks if a given ontclass is a subclass of wdo:Data Note: It also checks
	 * for substitute classes that may have been used, for example, when
	 * harvesting from other ontologies.
	 * 
	 * @param ontclass
	 *            The OntClass to check
	 * @return true if ontclass is a subclass of wdo:Data, false otherwise
	 */
	public boolean isDataSubClass(OntClass ontclass) {
		boolean ans = false;
		if (ontclass != null) {
			OntModel wdo = workspace.getBaseWDO();
			OntClass dataclass = wdo.getOntClass(WDO_Metamodel.DATA_URI);
			// check to see if there is a "substitute class" in the base wdo for
			// the specified class
			// these classes are the ones that are used when harvesting from
			// other ontologies, and have "see also" statement
			OntClass temp = wdo.getOntClass(ontclass.getURI());
			if (temp != null) {
				ans = temp.hasSuperClass(dataclass, false);
			} else {
				ans = ontclass.hasSuperClass(dataclass, false);
			}
		}
		return ans;
	}

	/**
	 * Checks if a given ontclass is a subclass of wdo:Method Note: It also
	 * checks for substitute classes that may have been used, for example, when
	 * harvesting from other ontologies.
	 * 
	 * @param ontclass
	 *            The OntClass to check
	 * @return true if ontclass is a subclass of wdo:Method, false otherwise
	 */
	public boolean isMethodSubClass(OntClass ontclass) {
		boolean ans = false;
		if (ontclass != null) {
			OntModel wdo = workspace.getBaseWDO();
			OntClass methodclass = wdo.getOntClass(WDO_Metamodel.METHOD_URI);
			// check to see if there is a "substitute class" in the base wdo for
			// the specified class
			// these classes are the ones that are used when harvesting from
			// other ontologies, and have "see also" statement
			OntClass temp = wdo.getOntClass(ontclass.getURI());
			if (temp != null) {
				ans = temp.hasSuperClass(methodclass, false);
			} else {
				ans = ontclass.hasSuperClass(methodclass, false);
			}
		}
		return ans;
	}

	/**
	 * List all non-anonymous subclasses of wdo:Data included in the base Wdo
	 * 
	 * @return an iterator over OntClasses
	 */
	public Iterator<OntClass> listDataSubClasses() {
		OntModel wdo = workspace.getBaseWDO();
		if (wdo != null) {
			OntClass datacls = wdo.getOntClass(WDO_Metamodel.DATA_URI);
			return listSubClasses(datacls);
		} else {
			return listSubClasses(null); // return empty iterator, not null
		}
	}

	/**
	 * List all non-anonymous subclasses of wdo:Method included in the base Wdo
	 * 
	 * @return an iterator over OntClasses
	 */
	public Iterator<OntClass> listMethodSubClasses() {
		OntModel wdo = workspace.getBaseWDO();
		if (wdo != null) {
			OntClass methodcls = wdo.getOntClass(WDO_Metamodel.METHOD_URI);
			return listSubClasses(methodcls);
		} else {
			return listSubClasses(null); // return an empty iterator, not null
		}
	}

	/**
	 * List all non-anonymous subclasses of cls in ontmodel
	 * 
	 * @param cls
	 * @return
	 */
	static public Iterator<OntClass> listSubClasses(OntClass cls) {
		ArrayList<OntClass> subclasses = new ArrayList<OntClass>();
		if (cls != null) {
			for (ExtendedIterator<OntClass> i = cls.listSubClasses(false); i
					.hasNext();) {
				OntClass tmp = i.next();
				if (!tmp.isAnon()) {
					subclasses.add(tmp);
				}
			}
		}
		return subclasses.iterator();
	}

	/**
	 * Move cls from being a direct subclass of oldSupercls to being a direct
	 * subclass of newSupercls.
	 * 
	 * @param subcls
	 * @param oldSupercls
	 * @param newSupercls
	 * @return
	 */
	public boolean moveSubClass(OntClass oldSupercls, OntClass newSupercls,
			OntClass subcls) {
		boolean ans = false;
		if (subcls != null && oldSupercls != null && newSupercls != null) {
			if (!newSupercls.hasSubClass(subcls, true)
					&& !subcls.hasSubClass(newSupercls, true)) {
				oldSupercls.removeSubClass(subcls);
				newSupercls.addSubClass(subcls);
				workspace.setModified(
						workspace.getOntModelURI(workspace.getBaseWDO()), true);
				ans = true;
			}
		}
		return ans;
	}

	/**
	 * Add cls to be a direct subclass of newSupercls
	 * 
	 * @param cls
	 * @param newSupercls
	 * @return
	 */
	public boolean addSubClass(OntClass newSupercls, OntClass cls) {
		boolean ans = false;
		if (cls != null && newSupercls != null) {
			if (!cls.hasSubClass(newSupercls, false)
					&& !newSupercls.hasSubClass(cls, false)) {
				newSupercls.addSubClass(cls);
				workspace.setModified(
						workspace.getOntModelURI(workspace.getBaseWDO()), true);
				ans = true;
			}
		}
		return ans;
	}

	/**
	 * Get the ontclass specified.
	 * 
	 * @param uri
	 *            The URI of the ontclass to get.
	 * @return The ontclass specified, if it exists. Otherwise, returns null.
	 */
	public OntClass getOntClass(String uri) {
		return workspace.getBaseWDO().getOntClass(uri);
	}

	/**
	 * Remove the specified class from all the ontologies loaded in the
	 * workspace.
	 * 
	 * @param cls
	 *            The class to remove
	 */
	public void removeClassFromWorkspace(final OntClass cls) {
		Selector selector = new Selector() {
			public RDFNode getObject() {
				return null;
			}

			public Property getPredicate() {
				return null;
			}

			public Resource getSubject() {
				return null;
			}

			public boolean isSimple() {
				return false;
			}

			public boolean test(Statement stmt) {
				return (stmt.getSubject().equals(cls) || stmt.getObject()
						.equals(cls));
			}
		};

		for (Iterator<OntModel> wdos = workspace.listOntologies(); wdos
				.hasNext();) {
			OntModel wdo = wdos.next();
			StmtIterator statements = wdo.listStatements(selector);
			if (statements != null && statements.hasNext()) {
				wdo.remove(statements);
				workspace.setModified(workspace.getOntModelURI(wdo), true);
			}
		}
	}

	/**
	 * Create the relation: subjectCls -> predicateProp -> objectCls, in the
	 * base Wdo. It also checks if there are declared inverse properties of the
	 * specified property and creates the corresponding inverse relations as
	 * well.
	 * 
	 * @param subjectCls
	 *            The class that forms the subject of the relation
	 * @param property
	 *            The property that forms the predicate of the relation
	 * @param objectCls
	 *            The class that forms the object of the relation
	 */
	public void addRelation(OntClass subjectCls, OntProperty property,
			OntClass objectCls) {
		if (subjectCls != null && property != null && objectCls != null) {
			boolean modified = false;
			// create relation if it does not exist
			if (!this.relationExists(subjectCls, property, objectCls)) {
				AllValuesFromRestriction av = workspace.getBaseWDO()
						.createAllValuesFromRestriction(null, property,
								objectCls);
				subjectCls.addSuperClass(av);
				modified = true;
			}
			// create inverse relation with properties that are defined as
			// inverse of the specified property
			for (ExtendedIterator<? extends OntProperty> i = property
					.listInverse(); i.hasNext();) {
				OntProperty invProperty = i.next();
				if (!relationExists(objectCls, invProperty, subjectCls)) {
					AllValuesFromRestriction avInv = workspace.getBaseWDO()
							.createAllValuesFromRestriction(null, invProperty,
									subjectCls);
					objectCls.addSuperClass(avInv);
					modified = true;
				}
			}
			// create inverse relation with properties that the property
			// specified defines as inverses
			for (ExtendedIterator<? extends OntProperty> i = property
					.listInverseOf(); i.hasNext();) {
				OntProperty invProperty = i.next();
				if (!relationExists(objectCls, invProperty, subjectCls)) {
					AllValuesFromRestriction avInv = workspace.getBaseWDO()
							.createAllValuesFromRestriction(null, invProperty,
									subjectCls);
					objectCls.addSuperClass(avInv);
					modified = true;
				}
			}
			if (modified) {
				workspace.setModified(
						workspace.getOntModelURI(workspace.getBaseWDO()), true);
			}
		}
	}

	/**
	 * Removes the relation: subjectCls -> predicateProp -> objectCls, in the
	 * base Wdo. It also checks if there are declared inverse properties of the
	 * specified property and removes the corresponding inverse relations as
	 * well.
	 * 
	 * @param subjectCls
	 *            The class that forms the subject of the relation
	 * @param property
	 *            The property that forms the predicate of the relation
	 * @param objectCls
	 *            The class that forms the object of the relation
	 */
	public void removeRelation(OntClass subjectCls, OntProperty property,
			OntClass objectCls) {
		if (subjectCls != null && property != null && objectCls != null) {
			// remove the relation specified
			for (ExtendedIterator<OntClass> i = subjectCls
					.listSuperClasses(true); i.hasNext();) {
				OntClass supercls = i.next();
				if (supercls.isRestriction()) {
					Restriction res = supercls.asRestriction();
					if (res.isAllValuesFromRestriction()) {
						AllValuesFromRestriction av = res
								.asAllValuesFromRestriction();
						if (av.getAllValuesFrom().equals(objectCls)
								&& av.getOnProperty().equals(property)) {
							this.removeClassFromWorkspace(av);
						}
					}
				}
			}
			// remove inverse relation with properties that are defined as
			// inverse of the specified property
			for (ExtendedIterator<? extends OntProperty> i = property
					.listInverse(); i.hasNext();) {
				OntProperty invProperty = i.next();
				for (ExtendedIterator<OntClass> j = objectCls
						.listSuperClasses(true); j.hasNext();) {
					OntClass supercls = j.next();
					if (supercls.isRestriction()) {
						Restriction res = supercls.asRestriction();
						if (res.isAllValuesFromRestriction()) {
							AllValuesFromRestriction av = res
									.asAllValuesFromRestriction();
							if (av.getAllValuesFrom().equals(subjectCls)
									&& av.getOnProperty().equals(invProperty)) {
								this.removeClassFromWorkspace(av);
							}
						}
					}
				}
			}
			// remove inverse relation with properties that the property
			// specified defines as inverses
			for (ExtendedIterator<? extends OntProperty> i = property
					.listInverseOf(); i.hasNext();) {
				OntProperty invProperty = i.next();
				for (ExtendedIterator<OntClass> j = objectCls
						.listSuperClasses(true); j.hasNext();) {
					OntClass supercls = j.next();
					if (supercls.isRestriction()) {
						Restriction res = supercls.asRestriction();
						if (res.isAllValuesFromRestriction()) {
							AllValuesFromRestriction av = res
									.asAllValuesFromRestriction();
							if (av.getAllValuesFrom().equals(subjectCls)
									&& av.getOnProperty().equals(invProperty)) {
								this.removeClassFromWorkspace(av);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * List the classes that are related to this class through the specified
	 * property.
	 * 
	 * @param cls
	 *            The class for which to find related classes
	 * @param property
	 *            The property on which to find relationships
	 * @return A list of related classes
	 */
	public Iterator<OntClass> listRelatedClasses(OntClass cls,
			OntProperty property) {
		ArrayList<OntClass> relatedClasses = new ArrayList<OntClass>();
		if (cls != null && property != null) {
			OntResource ontres = this.workspace.getBaseWDO()
					.getOntResource(cls);
			if (ontres.canAs(OntClass.class)) {
				cls = ontres.asClass();
				// list all superclasses
				for (ExtendedIterator<OntClass> i = cls.listSuperClasses(true); i
						.hasNext();) {
					OntClass superc = i.next();
					// filter restriction classes
					if (superc.isRestriction()) {
						Restriction superr = superc.asRestriction();
						// filter AllValuesFromRestriction classes
						if (superr.isAllValuesFromRestriction()) {
							AllValuesFromRestriction superavr = superr
									.asAllValuesFromRestriction();
							// filter AllValuesFromRestriction classes with
							// specified Property
							if (property.equals(superavr.getOnProperty())) {
								Resource relatedr = superavr.getAllValuesFrom();
								// filter related resources that are OntClasses
								if (relatedr.canAs(OntClass.class)) {
									OntClass relatedClass = (OntClass) relatedr
											.as(OntClass.class);
									if (relatedClass != null) {
										relatedClasses.add(relatedClass);
									}
								}
							}
						}
					}
				}
			}
		}

		return relatedClasses.iterator();
	}

	/**
	 * Check if two classes are related by the specified property
	 * 
	 * @param subjectCls
	 *            The subject class of the relation
	 * @param property
	 *            The property of the relation
	 * @param objectCls
	 *            The object class of the relation
	 * @return True if the specified relation exists, false otherwise
	 */
	public boolean relationExists(OntClass subjectCls, OntProperty property,
			OntClass objectCls) {
		boolean ans = false;
		for (Iterator<OntClass> i = this.listRelatedClasses(subjectCls,
				property); i.hasNext();) {
			if (i.next().equals(objectCls)) {
				ans = true;
				break;
			}
		}
		return ans;
	}

	/**
	 * Check if two classes are related by the specified property
	 * 
	 * @param subjectCls
	 *            The subject class of the relation
	 * @param property
	 *            The property of the relation
	 * @param objectCls
	 *            The object class of the relation
	 * @param direct
	 *            If true, check for direct relation, if false, check for
	 *            indirect relation as well (i.e., a super class of the
	 * @return
	 */
	public boolean relationExists(OntClass subjectCls, OntProperty property,
			OntClass objectCls, boolean direct) {
		boolean ans = false;
		ArrayList<OntClass> directlyRelatedClasses = new ArrayList<OntClass>();
		// search among directly related classes first
		for (Iterator<OntClass> i = this.listRelatedClasses(subjectCls,
				property); i.hasNext();) {
			OntClass relatedCls = i.next();
			if (relatedCls.equals(objectCls)) {
				ans = true;
				break;
			} else {
				directlyRelatedClasses.add(relatedCls);
			}
		}
		// if not found and direct=false, search among super classes of directly
		// related classes
		if (!ans && !direct) {
			for (Iterator<OntClass> i = directlyRelatedClasses.iterator(); i
					.hasNext();) {
				OntClass relatedCls = i.next();
				for (ExtendedIterator<OntClass> j = relatedCls
						.listSuperClasses(false); j.hasNext();) {
					OntClass relatedSuperCls = j.next();
					if (relatedSuperCls.equals(subjectCls)) {
						ans = true;
						break;
					}
				}
				if (ans) {
					break;
				}
			}
		}
		return ans;
	}

	/**
	 * Changing the name of a class or resource is quite tricky because this
	 * involves creating a new class and then fixing all relationships we have
	 * opted for changing label names only - the resource name stays in tact
	 * This method is preferred over directly calling OntClass.setLabel because
	 * it uses the default "EN" language setting and because it sets the
	 * modified bit in the workspace for the updated OntModel.
	 * 
	 * @author Aida Gandara
	 * @param cls
	 *            the concept that will be changed
	 * @param label
	 *            the label for the concept
	 */
	public void setClassLabel(OntClass cls, String label) {
		if (cls != null) {
			// TODO Should there be a check here?
			String prev = WDO.getClassLabel(cls);
			if ((prev == null && label != null)
					|| (prev != null && !prev.equals(label))) {
				cls.setLabel(label, "EN");
				workspace.setModified(
						workspace.getOntModelURI(cls.getOntModel()), true);
			}
		}
	}

	/**
	 * Gets the label from a class. This method is preferred over directly
	 * calling OntClass.getLabel because it checks the default "EN" language
	 * first, and if not found, does a second attempt with null for the language
	 * setting. The second attempt is useful for classes that are created and
	 * labeled through other programs that may not use the "EN" language
	 * setting.
	 * 
	 * @param cls
	 * @return
	 */
	public static String getClassLabel(OntClass cls) {
		String ans = null;
		if (cls != null) {
			ans = cls.getLabel("EN");
			if (ans == null || ans.isEmpty()) {
				ans = cls.getLabel(null);
			}
		}
		return ans;
	}

	/**
	 * Sets the comment for the given class. This method is preferred over
	 * OntResource.setComment because it uses the default "EN" language setting
	 * and because it sets the modified bit in the workspace
	 * 
	 * @param cls
	 * @param comment
	 */
	public void setClassComment(OntClass cls, String comment) {
		if (cls != null) {
			String prev = WDO.getClassComment(cls);
			if ((prev == null && comment != null)
					|| (prev != null && !comment.equals(prev))) {
				cls.setComment(comment, "EN");
				workspace.setModified(
						workspace.getOntModelURI(cls.getOntModel()), true);
			}
		}
	}

	/**
	 * Gets the comment for the given class. This method is preferred over
	 * OntResource.getComment because it attempts twice to get the comment: 1)
	 * using this application's default "EN" language setting, and 2) not using
	 * the language setting for comments created using other applications.
	 * 
	 * @param cls
	 * @return
	 */
	public static String getClassComment(OntClass cls) {
		String ans = null;
		if (cls != null) {
			ans = cls.getComment("EN");
			if (ans == null || ans.isEmpty()) {
				ans = cls.getComment(null);
			}
		}
		return ans;
	}

	/**
	 * Calculates the next data node number to be used to create a new concept
	 * in a Wdo This is counted from the basewdo model looking for the max
	 * number (#) from d#
	 */
	public int findMaxDataNodeNo() {
		int maxDataNodeNo = 0;
		OntModel basewdo = this.workspace.getBaseWDO();
		if (basewdo != null) {
			for (ExtendedIterator<OntClass> j = basewdo.listNamedClasses(); j
					.hasNext();) {
				OntClass wdoclass = j.next();
				// if a subclass of method
				if (isDataSubClass(wdoclass) && basewdo.isInBaseModel(wdoclass)) {
					// get the local name
					String classname = wdoclass.getLocalName();
					String newstr[] = classname.split("d");
					if (newstr.length == 2) {
						Integer numeric = Integer.valueOf(newstr[1]);
						if (numeric != null && numeric > maxDataNodeNo)
							maxDataNodeNo = numeric;
					}
				}
			}
		}
		return maxDataNodeNo;
	}

	/**
	 * Calculates the next method node number to be used to create a new concept
	 * in a Wdo This is counted from the basewdo model looking for the max
	 * number (#) from m#
	 */
	public int findMaxMethodNodeNo() {
		int maxMethodNodeNo = 0;
		OntModel basewdo = this.workspace.getBaseWDO();
		if (basewdo != null) {
			for (ExtendedIterator<OntClass> j = basewdo.listNamedClasses(); j
					.hasNext();) {
				OntClass wdoclass = j.next();
				// if a subclass of method
				if (isMethodSubClass(wdoclass)
						&& basewdo.isInBaseModel(wdoclass)) {
					// get the local name
					String classname = wdoclass.getLocalName();
					String newstr[] = classname.split("m");
					if (newstr.length == 2) {
						Integer numeric = Integer.valueOf(newstr[1]);
						if (numeric != null && numeric > maxMethodNodeNo)
							maxMethodNodeNo = numeric;
					}
				}
			}
		}
		return maxMethodNodeNo;
	}

	/**
	 * Returns the local name to be used for the specified class. The following
	 * rules are considered: 1) If the class has a label, use the label 2) If
	 * the class is an anonymous class, use its anon id 3) If the class is
	 * non-anonymous, use the local name portion of its URI
	 * 
	 * @param cls
	 *            Class for which to return its local name
	 * @return A string representing the local name of the class
	 */
	public static String getClassQName(OntClass cls) {
		String ans = "";
		if (cls != null) {
			ans = WDO.getClassLabel(cls);
			if (ans == null || ans.isEmpty()) {
				if (cls.isAnon()) {
					ans = cls.getId().getLabelString();
				} else {
					ans = cls.getURI();
					int idx = ans.lastIndexOf("#");
					if (idx > 0) {
						ans = ans.substring(idx + 1);
					}
				}
			}
		}
		return ans;
	}
}
