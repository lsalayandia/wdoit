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

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jgraph.JGraph;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.IndividualEdge;
import edu.utep.cybershare.wdoit.graphics.IndividualNode;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Listens for delete-key pressed in the Workflow graph. Attempts to delete any
 * selected workflow node in the state, and updates the GUI accordingly.
 * 
 * @author Leonardo Salayandia
 */
public class WorkflowDeleteKeyListener implements KeyListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			JGraph relationsGraph = (JGraph) e.getComponent();
			Object[] selectedCells = relationsGraph.getSelectionCells();

			// if there are selected cells in the workflow graph
			if (selectedCells != null) {
				State state = State.getInstance();
				int count = 0;
				for (int i = 0; i < selectedCells.length; i++) {
					OntClass ind = null;
					// if a workflow edge is deleted
					if (selectedCells[i] instanceof IndividualEdge) {
						IndividualEdge indEdge = (IndividualEdge) selectedCells[i];
						ind = indEdge.getIndividual();
						state.removeIndividual(ind);
						count++;
					}
					// if a workflow node is deleted
					else if (selectedCells[i] instanceof IndividualNode) {
						IndividualNode indNode = (IndividualNode) selectedCells[i];
						ind = indNode.getIndividual();
						// if a workflow method node is deleted
						if (ind != null && !SAW.isPMLSourceType(ind)) {
							Point coord = SAW.getInstanceCoordinate(ind);
							// create pml:source replacements for this method
							// node for all attached edges on the Outputs end
							OntClass[] dataInds = SAW.listHasOutput(ind);
							if (dataInds != null) {
								for (int j = 0; j < dataInds.length; j++) {
									state.removeHasOutput(ind, dataInds[j]);
									OntClass sourceInd = state
											.createSourceSinkIndividual(null,
													coord);
									state.addHasOutput(sourceInd, dataInds[j]);
								}
							}
							// create pml:source replacements for this method
							// node for all attached edges on the GetsInputFrom
							// end
							dataInds = SAW.listHasInput(ind);
							if (dataInds != null) {
								for (int j = 0; j < dataInds.length; j++) {
									state.removeHasInput(ind, dataInds[j]);
									OntClass sinkInd = state
											.createSourceSinkIndividual(null,
													coord);
									state.addHasInput(sinkInd, dataInds[j]);
								}
							}
							state.removeIndividual(ind);
							count++;
						}
					}
				}
				if (count > 0) {
					WdoView wdoView = (WdoView) WdoApp.getApplication()
							.getMainView();
					wdoView.setWorkflow();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {

	}
}
