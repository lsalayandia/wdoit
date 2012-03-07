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

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphModelEvent.GraphModelChange;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.IndividualEdge;
import edu.utep.cybershare.wdoit.graphics.IndividualNode;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Listens for workflow node position changes to find node overlaps. When Method
 * and a Source nodes overlap, the Source node is replaced by the Method Node.
 * When two Source nodes overlap, the Source node that was dragged replaces the
 * other as long as both nodes are attached to the same side of their
 * corresponding edges, i.e., both are sources or both are sinks. The
 * corresponding relations are created in the Saw. NOTE: As per Paulo's request,
 * this property has been relaxed. Now you can use sources as sinks.
 * 
 * @author Leonardo Salayandia
 */
public class WorkflowCreateRelationListener implements GraphModelListener {

	@Override
	public void graphChanged(GraphModelEvent e) {
		DefaultGraphModel model = (DefaultGraphModel) e.getSource();
		GraphModelChange change = e.getChange();
		Object[] cellsChanged = change.getChanged();
		// filter for change node events only, not nodes inserted or nodes
		// removed
		if (cellsChanged != null && change.getInserted() == null
				&& change.getRemoved() == null) {
			// look for modified nodes, and see if conditions are met to make
			// new connection
			for (int i = 0; i < cellsChanged.length; i++) {
				if (cellsChanged[i] != null
						&& cellsChanged[i] instanceof IndividualNode) {
					IndividualNode indNode = (IndividualNode) cellsChanged[i];
					IndividualNode overlapNode = findOverlappingNode(model,
							indNode);
					if (overlapNode != null) {
						OntClass ind = indNode.getIndividual();
						OntClass overlapInd = overlapNode.getIndividual();
						// the dragged node is a method node and overlaps a
						// source node
						if (!SAW.isPMLSourceType(ind)
								&& SAW.isPMLSourceType(overlapInd)) {
							replaceNode(model, indNode, overlapNode);
							break;
						}
						// the dragged node is a source node and overlaps a
						// method node
						if (SAW.isPMLSourceType(ind)
								&& !SAW.isPMLSourceType(overlapInd)) {
							replaceNode(model, overlapNode, indNode);
							break;
						}
						if ((SAW.isSource(ind) && SAW.isSource(overlapInd))
								|| (SAW.isSink(ind) && SAW.isSink(overlapInd))) {
							replaceNode(model, indNode, overlapNode);
							break;
						}
						// change requested by Paulo: Being able to use a node
						// as a source and a sink.
						// the dragged node and the overlap node are both
						// sources or are both sinks
						if (SAW.isSource(ind) && SAW.isSink(overlapInd)) {
							OntClass[] data = SAW.listHasOutput(ind);
							boolean found_common_data = false;
							if (data != null) {
								for (int idx = 0; idx < data.length; idx++) {
									if (SAW.getIsInputTo(data[idx]).equals(
											overlapInd)) {
										found_common_data = true;
										break;
									}
								}
							}
							if (!found_common_data) {
								replaceNode(model, indNode, overlapNode);
							}
							break;

						}
						// change requested by Paulo: Being able to use a node
						// as a source and a sink.
						// the dragged node and the overlap node are both
						// sources or are both sinks
						if (SAW.isSink(ind) && SAW.isSource(overlapInd)) {
							OntClass[] data = SAW.listHasOutput(overlapInd);
							boolean found_common_data = false;
							if (data != null) {
								for (int idx = 0; idx < data.length; idx++) {
									if (SAW.getIsInputTo(data[idx]).equals(ind)) {
										found_common_data = true;
										break;
									}
								}
							}
							if (!found_common_data) {
								replaceNode(model, indNode, overlapNode);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Looks for an individual node that overlaps the position of the node
	 * specified in the graph model specified. It returns the first node that it
	 * finds, or null if non exists.
	 * 
	 * @param node
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static IndividualNode findOverlappingNode(DefaultGraphModel model,
			IndividualNode node) {
		IndividualNode ans = null;
		if (node != null && model != null) {
			Rectangle2D nodeRect = GraphConstants.getBounds(node
					.getAttributes());
			List<DefaultGraphCell> roots = model.getRoots();
			for (Iterator<DefaultGraphCell> i = roots.iterator(); i.hasNext();) {
				DefaultGraphCell root = i.next();
				if (!node.equals(root) && root instanceof IndividualNode) {
					Rectangle2D rootRect = GraphConstants.getBounds(root
							.getAttributes());
					Rectangle2D intersectRect = nodeRect
							.createIntersection(rootRect);
					if (!intersectRect.isEmpty()) {
						ans = (IndividualNode) root;
						break;
					}
				}
			}
		}
		return ans;
	}

	/**
	 * Update Saw relations for the replacer/replacee nodes, as well as the
	 * workflow graph view
	 * 
	 * @param model
	 * @param replacer
	 * @param replacee
	 */
	private void replaceNode(DefaultGraphModel model, IndividualNode replacer,
			IndividualNode replacee) {
		if (replacer != null && replacee != null) {
			Object port = replacee.getChildAt(0);
			if (port != null) {
				OntClass replacerInd = replacer.getIndividual();
				OntClass replaceeInd = replacee.getIndividual();
				State state = State.getInstance();
				for (@SuppressWarnings("rawtypes")
				Iterator iter = model.edges(port); iter.hasNext();) {
					IndividualEdge edge = (IndividualEdge) iter.next();
					if (edge != null) {
						OntClass data = edge.getIndividual();
						if (SAW.getIsInputTo(data).equals(replaceeInd)) {
							state.removeIsInputTo(data, replaceeInd);
							state.setIsInputTo(data, replacerInd);
						} else if (SAW.getIsOutputOf(data).equals(replaceeInd)) {
							state.removeIsOutputOf(data, replaceeInd);
							state.setIsOutputOf(data, replacerInd);
						}
					}
				}
				WdoView wdoView = (WdoView) WdoApp.getApplication()
						.getMainView();
				wdoView.replaceWorkflowNode(replacer, replacee);
				state.removeIndividual(replaceeInd);
			}
		}
	}
}
