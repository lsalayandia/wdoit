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

import java.awt.Rectangle;
import java.awt.Point;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;

/**
 * @author Leonardo Salayandia
 * 
 */
public class IndividualNode extends DefaultGraphCell {
	private static final long serialVersionUID = 1L;
	private OntClass ind;

	public IndividualNode(OntClass ind, boolean selectable) {
		this.ind = ind;

		// set node name
		this.updateName();

		ResourceMap resourceMap = Application.getInstance(WdoApp.class)
				.getContext().getResourceMap(IndividualNode.class);

		// compute node coord
		Point locCoord = SAW.getInstanceCoordinate(ind);
		double w = resourceMap.getDouble("workflowConceptSize.width");
		double h = resourceMap.getDouble("workflowConceptSize.height");
		double x = 0;
		double y = 0;
		if (locCoord != null) {
			x = locCoord.getX() - (w / 2);
			y = locCoord.getY() - (h / 2);
		}
		// set common attributes
		AttributeMap attr = this.getAttributes();
		GraphConstants.setBounds(attr, new Rectangle.Double(x, y, w, h));
		GraphConstants.setChildrenSelectable(attr, false);
		GraphConstants.setOpaque(attr, true);
		GraphConstants.setBorderColor(attr,
				resourceMap.getColor("workflowNode.border.color"));
		GraphConstants.setResize(attr, false); // can't resize nodes,
												// standardized size, currently
												// size info not stored on mbw
		GraphConstants.setEditable(attr, false); // can't edit text directly on
													// graph
		GraphConstants.setConnectable(attr, false); // can't connect edges,
													// connection done through
													// node overlapping
		GraphConstants.setSelectable(attr, true); // select enabled for dragging
		GraphConstants.setSizeable(attr, false);
		// set attributes according to individual type
		if (SAW.isPMLSourceType(ind)) {
			GPCellViewFactory.setViewClass(attr,
					"edu.utep.cybershare.wdoit.graphics.JGraphEllipseView");
			GraphConstants.setGradientColor(attr,
					resourceMap.getColor("workflowNode.SourceSink.color"));
		} else {
			GraphConstants.setGradientColor(attr,
					resourceMap.getColor("workflowNode.Method.color"));
		}
		this.addPort();
		this.setDisconnectable(); // set the disconnectable property of this
									// node
	}

	/**
	 * Set the disconnectable property of this node according to its type and
	 * status. The node can be disconnectable if its a method node, or if its a
	 * source/sink node that has more than one connected edge.
	 */
	public void setDisconnectable() {
		if (SAW.isPMLSourceType(ind)) {
			GraphConstants.setDisconnectable(this.getAttributes(),
					SAW.getInputOutputCardinality(ind) > 1);
		} else {
			GraphConstants.setDisconnectable(this.getAttributes(), true);
		}
	}

	/**
	 * 
	 * @return
	 */
	public OntClass getIndividual() {
		return ind;
	}

	public boolean equals(Object o) {
		boolean ans = false;
		if (o instanceof IndividualNode) {
			ans = ((IndividualNode) o).getIndividual().equals(ind);
		}
		return ans;
	}

	public void updateName() {
		if (ind != null) {
			State state = State.getInstance();
			if (state.isShowWorkflowTypes()) {
				this.setUserObject(SAW.getSAWInstanceQName(this.ind));
			} else {
				this.setUserObject(SAW.getSAWInstanceLabel(this.ind));
			}
		}
	}
}
