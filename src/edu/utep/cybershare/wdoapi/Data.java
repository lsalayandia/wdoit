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

import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;

/**
 * @author Leonardo Salayandia
 * 
 */
public class Data {
	private final Workspace workspace;

	protected Data(Workspace wkspc) {
		this.workspace = wkspc;
	}

	/**
	 * Create the relation: data isInputTo method.
	 * 
	 * @param data
	 *            The wdo:Data subclass that forms the subject of the relation
	 * @param method
	 *            The wdo:Method subclass that forms the object of the relation
	 */
	public void addIsInputToRelation(OntClass data, OntClass method) {
		if (workspace.Wdo.isDataSubClass(data)
				&& workspace.Wdo.isMethodSubClass(method)) {
			OntProperty isInputTo = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.IS_INPUT_TO_URI);
			workspace.Wdo.addRelation(data, isInputTo, method);
		}
	}

	/**
	 * Remove the relation: data isInputTo method.
	 * 
	 * @param data
	 *            The wdo:Data subclass that forms the subject of the relation
	 * @param method
	 *            The wdo:Method subclass that forms the object of the relation
	 */
	public void removeIsInputToRelation(OntClass data, OntClass method) {
		if (workspace.Wdo.isDataSubClass(data)
				&& workspace.Wdo.isMethodSubClass(method)) {
			OntProperty isInputTo = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.IS_INPUT_TO_URI);
			workspace.Wdo.removeRelation(data, isInputTo, method);
		}
	}

	/**
	 * Create the relation: data isOutputOf method.
	 * 
	 * @param data
	 *            the wdo:Data subclass that forms the subject of the relation
	 * @param method
	 *            The wdo:Method subclass that forms the object of the relation
	 */
	public void addIsOutputOfRelation(OntClass data, OntClass method) {
		if (workspace.Wdo.isDataSubClass(data)
				&& workspace.Wdo.isMethodSubClass(method)) {
			OntProperty isOutputOf = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.IS_OUTPUT_OF_URI);
			workspace.Wdo.addRelation(data, isOutputOf, method);
		}
	}

	/**
	 * Remove the relation: data isOutputOf method.
	 * 
	 * @param data
	 *            the wdo:Data subclass that forms the subject of the relation
	 * @param method
	 *            the wdo:Method subclass that forms the object of the relation
	 */
	public void removeIsOutputOfRelation(OntClass data, OntClass method) {
		if (workspace.Wdo.isDataSubClass(data)
				&& workspace.Wdo.isMethodSubClass(method)) {
			OntProperty isOutputOf = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.IS_OUTPUT_OF_URI);
			workspace.Wdo.removeRelation(data, isOutputOf, method);
		}
	}

	/**
	 * List the classes that are related to the specified wdo:Data subclass
	 * through wdo:isInputTo
	 * 
	 * @param data
	 *            The wdo:Data subclass for which to find related classes
	 * @return A list of related classes
	 */
	public Iterator<OntClass> listIsInputToMethods(OntClass data) {
		if (workspace.Wdo.isDataSubClass(data)) {
			OntProperty isInputTo = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.IS_INPUT_TO_URI);
			return workspace.Wdo.listRelatedClasses(data, isInputTo);
		} else {
			// return an empty iterator
			return workspace.Wdo.listRelatedClasses(null, null);
		}
	}

	/**
	 * List the classes that are related to the specified wdo:Data subclass
	 * through wdo:isOutputOf
	 * 
	 * @param data
	 *            The wdo:Data subclass for which to find related classes
	 * @return A list of related classes
	 */
	public Iterator<OntClass> listIsOutputOfMethods(OntClass data) {
		if (workspace.Wdo.isDataSubClass(data)) {
			OntProperty isOutputOf = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.IS_OUTPUT_OF_URI);
			return workspace.Wdo.listRelatedClasses(data, isOutputOf);
		} else {
			// return an empty iterator
			return workspace.Wdo.listRelatedClasses(null, null);
		}
	}
}
