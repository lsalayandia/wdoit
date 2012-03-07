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
public class Method {
	private final Workspace workspace;

	protected Method(Workspace wkspc) {
		this.workspace = wkspc;
	}

	/**
	 * Create the relation: method hasInput data.
	 * 
	 * @param method
	 *            The wdo:Method subclass that forms the subject of the relation
	 * @param data
	 *            The wdo:Data subclass that forms the object of the relation
	 */
	public void addHasInputRelation(OntClass method, OntClass data) {
		if (workspace.Wdo.isDataSubClass(data)
				&& workspace.Wdo.isMethodSubClass(method)) {
			OntProperty hasInput = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.HAS_INPUT_URI);
			workspace.Wdo.addRelation(method, hasInput, data);
		}
	}

	/**
	 * Remove the relation: method hasInput data.
	 * 
	 * @param method
	 *            The wdo:Method subclass that forms the subject of the relation
	 * @param data
	 *            The wdo:Data subclass that forms the object of the relation
	 */
	public void removeHasInputRelation(OntClass method, OntClass data) {
		if (workspace.Wdo.isDataSubClass(data)
				&& workspace.Wdo.isMethodSubClass(method)) {
			OntProperty hasInput = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.HAS_INPUT_URI);
			workspace.Wdo.removeRelation(method, hasInput, data);
		}
	}

	/**
	 * Create the relation: method hasOutput data.
	 * 
	 * @param method
	 *            The wdo:Method subclass that forms the subject of the relation
	 * @param data
	 *            the wdo:Data subclass that forms the object of the relation
	 */
	public void addHasOutputRelation(OntClass method, OntClass data) {
		if (workspace.Wdo.isDataSubClass(data)
				&& workspace.Wdo.isMethodSubClass(method)) {
			OntProperty outputs = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.HAS_OUTPUT_URI);
			workspace.Wdo.addRelation(method, outputs, data);
		}
	}

	/**
	 * Remove the relation: method hasOutput data.
	 * 
	 * @param method
	 *            The wdo:Method subclass that forms the subject of the relation
	 * @param data
	 *            the wdo:Data subclass that forms the object of the relation
	 */
	public void removeHasOutputRelation(OntClass method, OntClass data) {
		if (workspace.Wdo.isDataSubClass(data)
				&& workspace.Wdo.isMethodSubClass(method)) {
			OntProperty outputs = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.HAS_OUTPUT_URI);
			workspace.Wdo.removeRelation(method, outputs, data);
		}
	}

	/**
	 * List the classes that are related to the specified wdo:Method subclass
	 * through wdo:hasInput
	 * 
	 * @param method
	 *            The wdo:Method subclass for which to find related classes
	 * @return A list of related classes
	 */
	public Iterator<OntClass> listHasInputData(OntClass method) {
		if (workspace.Wdo.isMethodSubClass(method)) {
			OntProperty hasInput = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.HAS_INPUT_URI);
			return workspace.Wdo.listRelatedClasses(method, hasInput);
		} else {
			// return an empty iterator
			return workspace.Wdo.listRelatedClasses(null, null);
		}
	}

	/**
	 * List the classes that are related to the specified wdo:Method subclass
	 * through wdo:hasOutput
	 * 
	 * @param method
	 *            The wdo:Method subclass for which to find related classes
	 * @return A list of related classes
	 */
	public Iterator<OntClass> listHasOutputData(OntClass method) {
		if (workspace.Wdo.isMethodSubClass(method)) {
			OntProperty hasOutput = workspace.getBaseWDO().getOntProperty(
					WDO_Metamodel.HAS_OUTPUT_URI);
			return workspace.Wdo.listRelatedClasses(method, hasOutput);
		} else {
			// return an empty iterator
			return workspace.Wdo.listRelatedClasses(null, null);
		}
	}
}
