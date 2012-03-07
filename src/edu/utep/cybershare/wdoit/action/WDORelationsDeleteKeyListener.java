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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jgraph.JGraph;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.OntClassNode;

/**
 * Listens for delete-key pressed in the Wdo Relations graph. Attempts to delete
 * any selected relations in the state, and updates the GUI accordingly.
 * 
 * @author Leonardo Salayandia TODO need to update to the new GUI
 */
public class WDORelationsDeleteKeyListener implements KeyListener {

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

			// if there are selected cells in the relations graph
			if (selectedCells != null && selectedCells.length > 0) {
				State state = State.getInstance();
				OntClass centerCls = state.getSelectedClass();
				double centerX = relationsGraph.getCenterPoint().getX();
				for (int i = 0; i < selectedCells.length; i++) {
					OntClassNode clsNode = (OntClassNode) selectedCells[i];
					double x = relationsGraph.getCellBounds(clsNode)
							.getCenterX();
					OntClass cls = clsNode.getOntClass();
					if (x <= centerX) {
						state.removeLeftRelation(centerCls, cls);
					} else {
						state.removeRightRelation(centerCls, cls);
					}
				}
				OntologyTreeRelationSelectionListener.setRelationConcept(state
						.getSelectedClass());
				// MainUI.getInstance().setVerboseText("Relation(s) removed.");
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
