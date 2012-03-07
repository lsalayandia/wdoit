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
package edu.utep.cybershare.wdoit.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;

/**
 * Sets the tool tip of the JTrees so that it displays the comments set for each
 * Concept node. NOTE: In the future, this class may include code to render
 * different node icons depending on the Concept type, i.e., data or method.
 * also, variations of icons can be used depending on whether a concept is
 * imported or declared locally.
 * 
 * @author Leonardo Salayandia
 * 
 */
public class OntModelJTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	private Icon conceptIcon;
	private Icon dataIcon;
	// private Icon dataComposedIcon;
	private Icon methodIcon;

	// private Icon methodDetailedIcon;

	public OntModelJTreeCellRenderer() {

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(OntModelJTree.class);
		conceptIcon = resourceMap.getIcon("concept.icon");
		dataIcon = resourceMap.getIcon("data.icon");
		// dataComposedIcon = resourceMap.getIcon("dataComposed.icon");
		methodIcon = resourceMap.getIcon("method.icon");
		// methodDetailedIcon = resourceMap.getIcon("methodDetailed.icon");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		if (value != null && value instanceof OntClass) {
			OntClass cls = (OntClass) value;
			setText(WDO.getClassQName(cls));
			setToolTipText(WDO.getClassComment(cls));

			State state = State.getInstance();
			if (state.isDataSubClass(cls)) {
				setIcon(dataIcon);
			} else if (state.isMethodSubClass(cls)) {
				setIcon(methodIcon);
			} else {
				setIcon(conceptIcon);
			}
		} else {
			setToolTipText(null);
		}

		return this;
	}
}
