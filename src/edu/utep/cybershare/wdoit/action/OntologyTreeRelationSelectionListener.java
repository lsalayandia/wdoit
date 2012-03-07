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
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.tree.OntModelJTree;

//import edu.utep.cybershare.wdoit.ui.MainUI;

/**
 * Listens for double-clicks on an ontology tree, sets the corresponding
 * selected concept as "selected" in the State, and displays the corresponding
 * Wdo relations for that selected concept.
 * 
 * @author Leonardo Salayandia TODO Need to update to new GUI
 */
public class OntologyTreeRelationSelectionListener implements MouseListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		OntModelJTree tree = (OntModelJTree) e.getSource();
		OntClass cls = tree.getSelectedValue();

		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
				&& cls != null) {
			setRelationConcept(cls);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

	}

	/**
	 * Set the specified class as the selected class, and update the GUI to
	 * display its relations.
	 * 
	 * @param cls
	 *            The class to mark as selected.
	 */
	public static void setRelationConcept(OntClass cls) {
		// set the selected class
		State state = State.getInstance();
		// MainUI mainUI = MainUI.getInstance();
		if (state.isDataSubClass(cls) || state.isMethodSubClass(cls)) {
			// update state selected class
			state.setSelectedClass(cls);

			// update GUI
			// mainUI.setSelectedRelationConcept(cls);
			if (cls != null) {
				// String leftEdgeLabel = "isInputTo";
				// String rightEdgeLabel = "outputs";
				if (state.isDataSubClass(cls)) {
					// leftEdgeLabel = "outputs";
					// rightEdgeLabel = "isInputTo";
				}

				for (Iterator<OntClass> i = state.listLeftClasses(cls); i
						.hasNext();) {
					// mainUI.addClassToLeft(i.next(), leftEdgeLabel);
				}
				for (Iterator<OntClass> i = state.listRightClasses(cls); i
						.hasNext();) {
					// mainUI.addClassToRight(i.next(), rightEdgeLabel);
				}

				// mainUI.setVerboseText("Viewing relations for " +
				// WDO.getLocalName(cls) + ".");
			}
		} else {
			if (cls != null) {
				// mainUI.setVerboseText("Class: " + WDO.getLocalName(cls) +
				// " is not a Wdo:Method or Wdo:Data class.");
			} else {
				// mainUI.setVerboseText("No concept selected.");
			}
		}
	}
}
