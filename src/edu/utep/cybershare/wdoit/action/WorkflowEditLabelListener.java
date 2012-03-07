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
package edu.utep.cybershare.wdoit.action;

import java.util.Map;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.DefaultGraphCell;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.IndividualEdge;
import edu.utep.cybershare.wdoit.graphics.IndividualNode;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Listens for cell changes and updates label changes in the Saw
 * 
 * @author Leonardo Salayandia
 */
public class WorkflowEditLabelListener implements GraphModelListener {

	@Override
	public void graphChanged(GraphModelEvent e) {
		GraphModelChange change = e.getChange();
		Object[] cellsChanged = change.getChanged();
		Map attrMapChange = change.getAttributes();
		// filter for changes to attributes
		if (attrMapChange != null) {
			for (int i = 0; i < cellsChanged.length; i++) {
				if (cellsChanged[i] != null) {
					Map cellAttrMapChange = (Map) attrMapChange
							.get(cellsChanged[i]);
					if (cellAttrMapChange != null) {
						OntClass ind = null;
						if (cellsChanged[i] instanceof IndividualNode) {
							IndividualNode node = (IndividualNode) cellsChanged[i];
							ind = node.getIndividual();
						} else if (cellsChanged[i] instanceof IndividualEdge) {
							IndividualEdge edge = (IndividualEdge) cellsChanged[i];
							ind = edge.getIndividual();
						}
						// either an individual node or edge was modified
						if (ind != null) {
							DefaultGraphCell cell = (DefaultGraphCell) cellsChanged[i];
							String newlabel = (String) cell.getUserObject();

							// the label in the graph cell may contain the
							// concept type in it, remove it if so.
							String localName = SAW.getSAWInstanceQName(ind);
							String[] splitLocalName = localName.split(":", 2);
							if (newlabel.startsWith(splitLocalName[0] + ":")) {
								newlabel = newlabel.substring(splitLocalName[0]
										.length() + 1);
							}

							State.getInstance().setIndividualLabel(ind,
									newlabel);
							WdoView wdoView = (WdoView) WdoApp.getApplication()
									.getMainView();
							wdoView.updateOWLDocHierarchy();
						}
					}
				}
			}
		}
	}
}
