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

import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.TreePath;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.tree.OntClassTransferable;
import edu.utep.cybershare.wdoit.tree.OntModelJTree;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Listens for drop actions in an Ontology Tree. If a drop is accepted, it
 * initiates the corresponding state change action, and reflects changes in the
 * GUI.
 * 
 * Drops are valid for classes already in the same tree, i.e., to move or copy a
 * class within the hierarchy.
 * 
 * @author Leonardo Salayandia
 */
public class OntologyTreeDropTargetListener implements DropTargetListener {

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
			try {
				// setup drop
				Transferable trans = dtde.getTransferable();
				// get dropped item
				OntClass cls = (OntClass) trans
						.getTransferData(OntClassTransferable.ONTCLASS_FLAVOR);
				// figure out where it was dropped
				Point pt = dtde.getLocation();
				DropTargetContext dtc = dtde.getDropTargetContext();
				OntModelJTree tree = (OntModelJTree) dtc.getComponent();
				TreePath parentpath = tree.getPathForLocation(pt.x, pt.y);
				OntClass newparent = (parentpath == null) ? newparent = tree
						.getRoot() : (OntClass) parentpath
						.getLastPathComponent();

				// move or copy?
				String msg = "copy/move operation unsuccessful.";
				boolean ans = false;
				if (dtde.getDropAction() == DnDConstants.ACTION_COPY) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					if (State.getInstance().addSubClass(newparent, cls)) {
						ans = true;
						msg = cls.toString() + " added as a subclass of "
								+ newparent.toString();
					}
				} else {
					TreePath oldparentpath = tree.getSelectionPath();
					if (oldparentpath != null) {
						OntClass oldparent = (OntClass) oldparentpath
								.getParentPath().getLastPathComponent();
						dtde.acceptDrop(DnDConstants.ACTION_MOVE);
						if (State.getInstance().moveSubClass(oldparent,
								newparent, cls)) {
							ans = true;
							msg = "moved" + cls.toString()
									+ " from subclass of "
									+ oldparent.toString() + " to subclass of "
									+ newparent.toString();
						}
					}
				}
				dtde.dropComplete(ans);
				// update UI
				WdoView wdoView = (WdoView) WdoApp.getApplication()
						.getMainView();
				wdoView.updateSelectedOWLDoc();
				wdoView.setMessage(msg);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
