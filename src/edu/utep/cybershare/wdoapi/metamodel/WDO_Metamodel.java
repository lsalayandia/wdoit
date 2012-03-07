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
package edu.utep.cybershare.wdoapi.metamodel;

import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Loads ontologies that are used by Wdo-It! in the appropriate order of
 * dependence. It also provides lookup URIs for classes and properties of the
 * Wdo ontology.
 * 
 * @author Leonardo Salayandia
 * 
 */
public class WDO_Metamodel {
	// ontologies
	static public final String OWL_URI = "http://www.w3.org/2002/07/owl";
	// static public final String OWL_FILE = "owl.owl";
	static public final String WDO_URI_VER1 = "http://trust.utep.edu/1.0/wdo.owl";
	static public final String WDO_URI = "http://trust.utep.edu/2.0/wdo.owl";
	static public final String WDO_FILE = "wdo.owl";
	static public final String PMLJ_URI = "http://inference-web.org/2.0/pml-justification.owl";
	static public final String PMLP_URI = "http://inference-web.org/2.0/pml-provenance.owl";
	static public final String PMLP_FILE = "pml-provenance.owl";
	static public final String DS_URI = "http://inference-web.org/2.0/ds.owl";
	static public final String DS_FILE = "ds.owl";
	// classes
	static public final String DATA_URI_VER1 = WDO_URI_VER1 + "#Data";
	static public final String DATA_URI = PMLP_URI + "#Information";
	static public final String METHOD_URI_VER1 = WDO_URI_VER1 + "#Method";
	static public final String METHOD_URI = PMLP_URI + "#MethodRule";
	static public final String WDOCLASS_URI = WDO_URI
			+ "#WorkflowDrivenOntology";
	static public final String SAW_URI = WDO_URI + "#SemanticAbstractWorkflow";
	static public final String COORD_URI = WDO_URI + "#RectangularCoordinate";
	static public final String SOURCE_URI = PMLP_URI + "#Source";
	static public final String INFERENCE_ENGINE_URI = PMLP_URI
			+ "#InferenceEngine";
	static public final String HAS_FORMAT_URI_VER1 = WDO_URI_VER1
			+ "#hasFormat";
	static public final String FORMAT_URI = PMLP_URI + "#Format";
	// data type properties
	static public final String X_URI = WDO_URI + "#hasXCoordinateValue";
	static public final String Y_URI = WDO_URI + "#hasYCoordinateValue";
	static public final String Z_URI = WDO_URI + "#hasZCoordinateValue";
	static public final String HAS_SUBCLASS_ORDER_URI = WDO_URI
			+ "#hasSubclassOrder";
	static public final String HAS_NAME_URI = PMLP_URI + "#hasName";
	static public final String HAS_PRETTY_NAME_URI = PMLP_URI
			+ "#hasPrettyName";
	// object properties
	static public final String HAS_FORMAT_URI = PMLP_URI + "#hasFormat";
	static public final String HAS_COORDINATE_URI = WDO_URI + "#hasCoordinate";
	static public final String HAS_DEFAULT_INSTANCE_URI = WDO_URI
			+ "#hasDefaultInstance";
	static public final String HAS_LABEL_COORDINATE_URI = WDO_URI
			+ "#hasLabelCoordinate";
	static public final String HAS_INPUT_URI = WDO_URI + "#hasInput";
	static public final String IS_INPUT_TO_URI = WDO_URI + "#isInputTo";
	static public final String HAS_OUTPUT_URI = WDO_URI + "#hasOutput";
	static public final String IS_OUTPUT_OF_URI = WDO_URI + "#isOutputOf";
	static public final String HAS_SOURCE_URI = WDO_URI + "#hasSource";
	static public final String HAS_SINK_FROM_URI = WDO_URI + "#hasSink";
	static public final String IS_ABSTRACTED_BY_URI = WDO_URI
			+ "#isAbstractedBy";
	static public final String IS_DETAILED_BY_URI = WDO_URI + "#isDetailedBy";
	static public final String IS_COMPOSED_OF_URI = WDO_URI + "#isComposedOf";
	static public final String IS_PART_OF_URI = WDO_URI + "#isPartOf";
	static public final String HAS_INFERENCE_ENGINE_URI = WDO_URI
			+ "#hasInferenceEngine";
	static public final String HAS_REPORT_URI = WDO_URI + "#hasReport";
	static public final String HAS_SUPPORTING_DOC_URI = WDO_URI
			+ "#hasSupportingDocumentation";
	static public final String PML_HAS_SOURCE_URI = PMLP_URI + "#hasSource";

	/**
	 * Load ontology model for the upper-level Wdo ontology
	 * 
	 * @param spec
	 */
	public static OntModel initialize(OntModelSpec spec, ClassLoader cl) {
		// Load ontologies in order of dependence: Wdo imports PMLP, PMLP
		// imports DS
		// Therefore, load DS, then PMLP, then Wdo
		// OntModel owl = ModelFactory.createOntologyModel(spec);
		OntModel ds = ModelFactory.createOntologyModel(spec);
		OntModel pmlp = ModelFactory.createOntologyModel(spec);
		OntModel wdo = ModelFactory.createOntologyModel(spec);

		// if files deployed within a jar, use the getResourceAsStream method to
		// read ontology
		// otherwise, read from file system directly
		// InputStream inStream = cl.getResourceAsStream(OWL_FILE);
		// if (inStream != null) {
		// owl.read(inStream, null);
		// }
		// else {
		// owl.read("file:lib/" + OWL_FILE, OWL_URI, null);
		// }

		InputStream inStream = cl.getResourceAsStream(DS_FILE);
		if (inStream != null) {
			ds.read(inStream, null);
		} else {
			ds.read("file:lib/" + DS_FILE, DS_URI, null);
		}

		inStream = cl.getResourceAsStream(PMLP_FILE);
		if (inStream != null) {
			pmlp.read(inStream, null);
		} else {
			pmlp.read("file:lib/" + PMLP_FILE, PMLP_URI, null);
		}
		pmlp.addSubModel(ds, true);

		inStream = cl.getResourceAsStream(WDO_FILE);
		if (inStream != null) {
			wdo.read(inStream, null);
		} else {
			wdo.read("file:lib/" + WDO_FILE, WDO_URI, null);
		}
		wdo.addSubModel(pmlp, true);

		OntDocumentManager docmgr = spec.getDocumentManager();
		// docmgr.addModel(OWL_URI, owl);
		docmgr.addModel(DS_URI, ds);
		docmgr.addModel(PMLP_URI, pmlp);
		docmgr.addModel(WDO_URI, wdo);

		// docmgr.addIgnoreImport(OWL_URI);
		docmgr.addIgnoreImport(DS_URI);
		docmgr.addIgnoreImport(PMLP_URI);
		docmgr.addIgnoreImport(WDO_URI);

		// docmgr.addAltEntry(OWL_URI, OWL_FILE);
		docmgr.addAltEntry(DS_URI, DS_FILE);
		docmgr.addAltEntry(PMLP_URI, PMLP_FILE);
		docmgr.addAltEntry(WDO_URI, WDO_FILE);

		return wdo;
	}
}
