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
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.IndividualEdge;
import edu.utep.cybershare.wdoit.graphics.IndividualNode;
import edu.utep.cybershare.wdoit.tree.OntClassTransferable;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Listens for Drop actions on the workflow graph. If the drop action is
 * accepted, it creates the corresponding new node/edge on the workflow graph,
 * and updates the GUI accordingly.
 * 
 * @author Leonardo Salayandia
 */
public class WorkflowDropTargetListener implements DropTargetListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	@Override
	public void drop(DropTargetDropEvent dtde) {
		if (!dtde.isLocalTransfer()
				|| !dtde.isDataFlavorSupported(OntClassTransferable.ONTCLASS_FLAVOR)) {
			dtde.rejectDrop();
		} else {
			dtde.acceptDrop(DnDConstants.ACTION_MOVE);
			State state = State.getInstance();
			try {
				OntClass cls = (OntClass) dtde.getTransferable()
						.getTransferData(OntClassTransferable.ONTCLASS_FLAVOR);
				Point mousePosition = dtde.getLocation();

				WdoView wdoView = (WdoView) WdoApp.getApplication()
						.getMainView();
				// if cls is wdo:Method subclass, create individual, graph node,
				// and update GUI
				if (state.isMethodSubClass(cls)) {
					OntClass ind = state.createNodeIndividual(cls, null,
							mousePosition);
					IndividualNode indNode = new IndividualNode(ind, true);
					wdoView.addWorkflowCell(indNode);
				}
				// if cls is wdo:Data subclass, create individual, graph edge,
				// and update GUI
				else {
					ResourceMap resourceMap = Application
							.getInstance(WdoApp.class).getContext()
							.getResourceMap(IndividualNode.class);
					double w = resourceMap
							.getDouble("workflowConceptSize.width");
					Point srcPosition = new Point(mousePosition.x,
							mousePosition.y);
					Point sinkPosition = new Point(
							(int) (mousePosition.x + (w * 2)), mousePosition.y);
					OntClass ind = state.createDataIndividual(cls, null, null,
							null, srcPosition, sinkPosition);
					OntClass fromInd = SAW.getIsOutputOf(ind);
					OntClass toInd = SAW.getIsInputTo(ind);

					IndividualNode fromIndNode = new IndividualNode(fromInd,
							true);
					IndividualNode toIndNode = new IndividualNode(toInd, true);

					wdoView.addWorkflowCell(fromIndNode);
					wdoView.addWorkflowCell(toIndNode);
					IndividualEdge indEdge = new IndividualEdge(ind,
							fromIndNode, toIndNode);
					wdoView.addWorkflowCell(indEdge);
				}
				dtde.dropComplete(true);

			} catch (Exception e) {
				dtde.dropComplete(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent
	 * )
	 */
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	@Override
	public void dragExit(DropTargetEvent dte) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent
	 * )
	 */
	@Override
	public void dragOver(DropTargetDragEvent dtde) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {

	}
}
