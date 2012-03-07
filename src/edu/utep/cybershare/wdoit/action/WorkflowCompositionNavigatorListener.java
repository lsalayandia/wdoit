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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.jgraph.JGraph;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.Ontology;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.IndividualNode;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Listens for mouse double-clicks in the Workflow graph. If a method node is
 * selected, and that method is detailed by another workflow, navigate to that
 * workflow
 * 
 * @author Leonardo Salayandia
 */
public class WorkflowCompositionNavigatorListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			JGraph mbwGraph = (JGraph) e.getSource();
			Object[] selectedCells = mbwGraph.getSelectionCells();

			if (selectedCells != null && selectedCells.length == 1) {
				// exactly one cell selected
				if (selectedCells[0] instanceof IndividualNode) {
					IndividualNode indNode = (IndividualNode) selectedCells[0];
					OntClass methodInd = indNode.getIndividual();
					// the selected cell is a method node
					if (!SAW.isPMLSourceType(methodInd)) {
						Ontology mbwOnt = SAW.getDetailedBy(methodInd);
						// the selected method node is detailed by an mbw
						if (mbwOnt != null) {
							// navigate to the mbw that details the selected
							// workflow
							State.getInstance().setSelectedWorkflow(
									mbwOnt.getURI());
							WdoView wdoView = (WdoView) WdoApp.getApplication()
									.getMainView();
							wdoView.updateSelectedOWLDoc();
						}
					}
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}
}
