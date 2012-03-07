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

import java.awt.Cursor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

/**
 * Listens for drag events under process initiated on an Ontology Tree. Updates
 * the cursor icon accordingly to reflect current drag state.
 * 
 * @author Leonardo Salayandia
 */
public class OntologyTreeDragSourceListener implements DragSourceListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent
	 * )
	 */
	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent
	 * )
	 */
	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		updateCursor(dsde);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
	 */
	@Override
	public void dragExit(DragSourceEvent dse) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent
	 * )
	 */
	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		updateCursor(dsde);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.
	 * DragSourceDragEvent)
	 */
	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		updateCursor(dsde);
	}

	/*
	 * Update the cursor according to drag state
	 */
	private void updateCursor(DragSourceDragEvent dsde) {
		Cursor cursor = DragSource.DefaultMoveDrop;
		if (dsde.getUserAction() == DnDConstants.ACTION_COPY) {
			cursor = DragSource.DefaultCopyDrop;
		}
		// // need to be able to move it off the tree and into the workflow
		// canvas area
		// Cursor cursor = DragSource.DefaultCopyNoDrop;
		// OntModelJTree tree = (OntModelJTree)
		// dsde.getDragSourceContext().getComponent();
		//
		// WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		//
		// if (tree.getMousePosition() != null) {
		// if (dsde.getUserAction() == DnDConstants.ACTION_COPY) {
		// cursor = DragSource.DefaultCopyDrop;
		// }
		// else if (dsde.getUserAction() == DnDConstants.ACTION_MOVE ||
		// dsde.getUserAction() == DnDConstants.ACTION_NONE) {
		// cursor = DragSource.DefaultMoveDrop;
		// }
		// }
		dsde.getDragSourceContext().setCursor(cursor);
	}
}
