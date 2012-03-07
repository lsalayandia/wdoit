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

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import org.jgraph.JGraph;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.tree.OntClassTransferable;

/**
 * Listens for Drop actions on the Wdo relations graph. If the drop action is
 * accepted, it creates a new relation, and updates the GUI accordingly.
 * 
 * @author Leonardo Salayandia TODO Need to update to the new GUI
 */
public class WDORelationsDropTargetListener implements DropTargetListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent
	 * )
	 */
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		if (isDragOk(dtde))
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
		else
			dtde.rejectDrag();
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
		if (isDragOk(dtde))
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
		else
			dtde.rejectDrag();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	@Override
	public void drop(DropTargetDropEvent dtde) {
		if (!dtde.isLocalTransfer()) {
			dtde.rejectDrop();
		} else if (!dtde
				.isDataFlavorSupported(OntClassTransferable.ONTCLASS_FLAVOR)) {
			dtde.rejectDrop();
		} else if ((dtde.getDropAction() & DnDConstants.ACTION_COPY) == 0) {
			dtde.rejectDrop();
		} else {
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			State state = State.getInstance();
			try {
				OntClass cls = (OntClass) dtde.getTransferable()
						.getTransferData(OntClassTransferable.ONTCLASS_FLAVOR);
				OntClass centerCls = state.getSelectedClass();

				JGraph wdorelations = (JGraph) dtde.getDropTargetContext()
						.getComponent();
				if (wdorelations.getMousePosition().x <= wdorelations
						.getCenterPoint().getX()) {
					state.addLeftRelation(centerCls, cls);
				} else {
					state.addRightRelation(centerCls, cls);
				}
				// update GUI
				OntologyTreeRelationSelectionListener
						.setRelationConcept(centerCls);
				dtde.dropComplete(true);

			} catch (Exception e) {
				dtde.dropComplete(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		if (isDragOk(dtde))
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
		else
			dtde.rejectDrag();
	}

	/*
	 * Check if the current drag is ok to accept.
	 */
	private boolean isDragOk(DropTargetDragEvent dtde) {
		boolean ans = false;

		try {
			State state = State.getInstance();

			OntClass cls = (OntClass) dtde.getTransferable().getTransferData(
					OntClassTransferable.ONTCLASS_FLAVOR);
			OntClass centerCls = state.getSelectedClass();

			// if center class is wdo:Method and new class is wdo:Method, drag
			// is not ok
			if (state.isMethodSubClass(centerCls)
					&& state.isMethodSubClass(cls))
				ans = false;
			// if center class is wdo:Data and new class is wdo:Data, drag is
			// not ok
			else if (state.isDataSubClass(centerCls)
					&& state.isDataSubClass(cls))
				ans = false;
			else
				ans = true;
		} catch (Exception e) {
			// flavor not supported, error getting data dragged from other
			// applications
			ans = false;
		}

		return ans;
	}
}
