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
package edu.utep.cybershare.wdoit.graphics;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;

/**
 * @author Leonardo Salayandia
 * 
 */
public class OntClassNode extends DefaultGraphCell {
	private static final long serialVersionUID = 1L;

	private OntClass cls;

	public OntClassNode(OntClass cls, Point centerCoord, boolean selectable) {
		this.cls = cls;
		this.setUserObject(WDO.getClassQName(cls));

		GPCellViewFactory.setViewClass(this.getAttributes(),
				"edu.utep.cybershare.wdoit.graphics.JGraphRoundRectView");

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(OntClassNode.class);

		double w = resourceMap.getDouble("conceptSize.width");
		double h = resourceMap.getDouble("conceptSize.height");
		double x = centerCoord.getX() - (w / 2);
		double y = centerCoord.getY() - (h / 2);
		GraphConstants.setBounds(this.getAttributes(), new Rectangle2D.Double(
				x, y, w, h));

		GraphConstants.setOpaque(this.getAttributes(), true);
		GraphConstants.setSelectable(this.getAttributes(), selectable);
		GraphConstants.setSizeable(this.getAttributes(), false);

		State state = State.getInstance();
		if (state.isMethodSubClass(cls)) {
			GraphConstants.setBackground(this.getAttributes(),
					resourceMap.getColor("node.Method.background.color"));
			GraphConstants.setBorderColor(this.getAttributes(),
					resourceMap.getColor("node.Method.border.color"));
		} else {
			GraphConstants.setBackground(this.getAttributes(),
					resourceMap.getColor("node.Data.background.color"));
			GraphConstants.setBorderColor(this.getAttributes(),
					resourceMap.getColor("node.Data.border.color"));
		}

		this.addPort();
	}

	public OntClass getOntClass() {
		return cls;
	}
}
