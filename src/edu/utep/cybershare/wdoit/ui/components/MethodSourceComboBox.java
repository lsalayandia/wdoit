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
package edu.utep.cybershare.wdoit.ui.components;

import java.util.Vector;

import javax.swing.ComboBoxModel;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;
import edu.utep.cybershare.ws.RDFStore.RDFStore;
import edu.utep.cybershare.ws.RDFStore.RDFStore_Service;

/**
 * Combo Box that shows queried pmlp:Sources from a triple store.
 * 
 * @author Leonardo Salayandia
 */
public class MethodSourceComboBox extends IndividualComboBox {
	private static final long serialVersionUID = 1L;
	private static String SOURCE_NONE_OPTION = "";

	public MethodSourceComboBox() {
		super();
		setIndividuals(queryPublicationSources());
	}

	private Vector<Individual> queryPublicationSources() {
		Vector<Individual> pubs = new Vector<Individual>();
		pubs.add(new Individual(SOURCE_NONE_OPTION, SOURCE_NONE_OPTION,
				SOURCE_NONE_OPTION));

		RDFStore_Service service = new RDFStore_Service();
		RDFStore proxy = service.getRDFStoreHttpPort();
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX pml-sparql: <http://trust.utep.edu/sparql-pml#>"
				+ "PREFIX owl: <"
				+ WDO_Metamodel.OWL_URI
				+ "#>"
				+ "PREFIX wdo: <"
				+ WDO_Metamodel.WDO_URI
				+ "#>"
				+ "PREFIX pmlj: <"
				+ WDO_Metamodel.PMLJ_URI
				+ "#>"
				+ "PREFIX pmlp: <"
				+ WDO_Metamodel.PMLP_URI
				+ "#>"
				+ "PREFIX ds: <"
				+ WDO_Metamodel.DS_URI
				+ "#>"
				+ "SELECT DISTINCT ?uri ?name WHERE { "
				+ "?uri a pmlp:Publication . "
				+ "?uri pmlp:hasName ?name . "
				+ "}";

		String temp = proxy.doQuery(query);
		ResultSet results = ResultSetFactory.fromXML(temp);
		if (results != null) {
			RDFVisitor tmpVisitor = new RDFVisitor() {
				@Override
				public Object visitBlank(Resource r, AnonId id) {
					return null;
				}

				@Override
				public Object visitLiteral(Literal l) {
					return l.getString();
				}

				@Override
				public Object visitURI(Resource r, String uri) {
					return null;
				}
			};
			while (results.hasNext()) {
				QuerySolution result = results.next();
				String uri = result.get("?uri").toString();
				String name = (String) result.get("?name")
						.visitWith(tmpVisitor);

				pubs.add(new Individual(uri, name, name));
			}
		}
		return pubs;
	}

	public void setSelectedSource(com.hp.hpl.jena.ontology.Individual ind) {
		if (ind != null && !ind.isAnon()) {
			Individual item = new Individual(ind.getURI(), ind.getLabel(null),
					ind.getComment(null));
			int idx = this.containsElement(item);
			if (idx >= 0) {
				this.setSelectedIndex(idx);
			} else {
				this.addIndividual(item);
			}
		} else {
			this.setSelectedItem(SOURCE_NONE_OPTION);
		}
	}

	public String getSelectedSourceURI() {
		String ans = null;
		Individual ind = (Individual) this.getSelectedItem();
		if (ind != null) {
			ans = ind.getURI();
		}
		return ans;
	}
}
