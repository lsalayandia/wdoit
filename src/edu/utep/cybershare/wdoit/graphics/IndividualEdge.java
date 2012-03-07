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

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoit.context.State;

/**
 * @author Leonardo Salayandia
 * 
 */
public class IndividualEdge extends DefaultEdge {
	private static final long serialVersionUID = 1L;
	private OntClass ind;

	public IndividualEdge(OntClass ind, DefaultGraphCell src,
			DefaultGraphCell target) {
		this.ind = ind;

		// set node name
		this.updateName();

		// set edge src
		if (src != null) {
			this.setSource(src.getChildAt(0));
		}

		// set edge target
		if (target != null) {
			this.setTarget(target.getChildAt(0));
		}

		// set other attributes
		AttributeMap attr = this.getAttributes();
		GraphConstants.setLineEnd(attr, GraphConstants.ARROW_CLASSIC);
		GraphConstants.setEndFill(attr, true);
		GraphConstants.setSelectable(attr, true);
		GraphConstants.setEditable(attr, false);
		GraphConstants.setBendable(attr, false);
		GraphConstants.setLabelAlongEdge(attr, true); // tilts text to match
														// flow of edge, and
														// allows to set x/y
														// coord of label

		// set position of label
		Point labelCoord = SAW.getInstanceCoordinate(ind);
		if (labelCoord != null) {
			GraphConstants.setLabelPosition(attr, labelCoord);
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
		if (o instanceof IndividualEdge) {
			ans = ((IndividualEdge) o).getIndividual().equals(ind);
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
