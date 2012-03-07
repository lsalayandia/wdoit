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
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoit.tree.OntClassTransferable;
import edu.utep.cybershare.wdoit.tree.OntModelJTree;

/**
 * Listens for drag gestures on Ontology Trees. When a copy or move drag gesture
 * is recognized, it initiates the drag action.
 * 
 * @author Leonardo Salayandia
 */
public class OntologyTreeDragGestureListener implements DragGestureListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.
	 * DragGestureEvent)
	 */
	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		DragGestureRecognizer dgr = (DragGestureRecognizer) dge.getSource();
		OntModelJTree tree = (OntModelJTree) dgr.getComponent();
		OntClass selectedClass = tree.getSelectedValue();
		if (selectedClass != null) {
			OntClassTransferable transfCls = new OntClassTransferable(
					selectedClass);
			Cursor cursor = (dge.getDragAction() == DnDConstants.ACTION_MOVE) ? DragSource.DefaultMoveDrop
					: DragSource.DefaultCopyDrop;
			dge.startDrag(cursor, transfCls);
		}
	}
}
