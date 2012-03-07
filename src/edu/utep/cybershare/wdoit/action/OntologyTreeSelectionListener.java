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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.tree.OntModelJTree;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Listens for mouse clicks on an Ontology Tree. If the mouse location during a
 * mouse click does not point to a tree node, clears any current node
 * selections. Sets the resulting (Data or Method class) selection of this tree
 * as the State's selected class (if no selection, set selected class to null).
 * Clears any selections of other trees in the GUI
 * 
 * @author Leonardo Salayandia
 */
public class OntologyTreeSelectionListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		OntModelJTree tree = (OntModelJTree) e.getSource();

		Point mouseLocation = e.getPoint();
		if (tree.getPathForLocation(mouseLocation.x, mouseLocation.y) == null) {
			tree.clearSelection();
		}

		OntClass selectedClass = tree.getSelectedValue();
		State state = State.getInstance();
		if (selectedClass == null) {
			state.setSelectedClass(null);
		} else if (state.isDataSubClass(selectedClass)
				|| state.isMethodSubClass(selectedClass)) {
			state.setSelectedClass(tree.getSelectedValue());
		}

		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		wdoView.clearOtherHierarchySelections(tree);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
