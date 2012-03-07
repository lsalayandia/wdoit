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

import javax.swing.tree.TreePath;

import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.tree.OntDocumentManagerJTree;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Updates GUI based on the owl document selected in the workspace tree.
 * 
 * @author Leonardo Salayandia
 */
public class OntDocumentManagerSelectionListener implements MouseListener {

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
		OntDocumentManagerJTree tree = (OntDocumentManagerJTree) e.getSource();
		Point mouseLocation = e.getPoint();
		State state = State.getInstance();
		String uri = null;
		TreePath path = tree.getPathForLocation(mouseLocation.x,
				mouseLocation.y);
		// if nothing selected, selected the base wdo (default)
		if (path == null) {
			uri = state.getBaseWDOURI();
		} else {
			String temp = (String) path.getLastPathComponent();
			// if an ontology or a workflow selected, choose it as the selected
			// uri, otherwise choose the default
			uri = (state.isOntology(temp) || state.isWorkflow(temp)) ? temp
					: state.getBaseWDOURI();
		}
		state.setSelectedOWLDocument(uri);
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		wdoView.updateSelectedOWLDoc();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
