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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.GraphConstants;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.IndividualEdge;
import edu.utep.cybershare.wdoit.graphics.IndividualNode;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Listens cell changes in a Workflow graph model, and updates the corresponding
 * Saw Individual position coordinates. Nodes may change their positions, edges
 * may change the position of their labels.
 * 
 * @author Leonardo Salayandia
 */
public class WorkflowPositionListener implements GraphModelListener {

	@Override
	public void graphChanged(GraphModelEvent e) {
		GraphModelChange change = e.getChange();
		Object[] cellsChanged = change.getChanged();
		if (cellsChanged != null) {
			for (int i = 0; i < cellsChanged.length; i++) {
				if (cellsChanged[i] != null) {
					// if an Individual node changed, update its position
					if (cellsChanged[i] instanceof IndividualNode) {
						IndividualNode node = (IndividualNode) cellsChanged[i];
						OntClass ind = node.getIndividual();
						Rectangle2D rect = GraphConstants.getBounds(node
								.getAttributes());
						if (rect != null && ind != null) {
							State.getInstance().setIndividualCoordinate(
									ind,
									new Point((int) rect.getCenterX(),
											(int) rect.getCenterY()));
							WdoView wdoView = (WdoView) WdoApp.getApplication()
									.getMainView();
							wdoView.updateOWLDocHierarchy();
						}
					}
					// if an Individual edge changed, update its label position
					else if (cellsChanged[i] instanceof IndividualEdge) {
						IndividualEdge edge = (IndividualEdge) cellsChanged[i];
						Point2D coord = GraphConstants.getLabelPosition(edge
								.getAttributes());
						OntClass ind = edge.getIndividual();
						if (coord != null && ind != null) {
							State.getInstance().setIndividualCoordinate(
									ind,
									new Point((int) coord.getX(), (int) coord
											.getY()));
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
