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
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.IndividualEdge;
import edu.utep.cybershare.wdoit.graphics.IndividualNode;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Listens for changes in the edges of a Workflow graph model. When an edge that
 * has been disconnected from an end is identified, it removes its corresponding
 * Saw relation, and updates the Workflow graph to draw a sink or a source node
 * instead of a disconnected edge.
 * 
 * @author Leonardo Salayandia
 * 
 */
public class WorkflowRemoveRelationListener implements GraphModelListener {

	@Override
	public void graphChanged(GraphModelEvent e) {
		DefaultGraphModel model = (DefaultGraphModel) e.getSource();
		ConnectionSet connSet = e.getChange().getConnectionSet();
		if (connSet != null) {
			@SuppressWarnings("rawtypes")
			Set changedEdges = connSet.getChangedEdges();
			if (changedEdges != null) {
				for (@SuppressWarnings("rawtypes")
				Iterator i = changedEdges.iterator(); i.hasNext();) {
					IndividualEdge edge = (IndividualEdge) i.next();
					OntClass data = edge.getIndividual();
					State state = State.getInstance();

					// make changes to the source part of the edge
					DefaultPort srcPort = (DefaultPort) model.getSource(edge);
					if (srcPort == null) {
						OntClass srcMethodInd = SAW.getIsOutputOf(data);
						if (srcMethodInd != null) {
							boolean proceed = (SAW.isSource(srcMethodInd)) ? (SAW
									.getInputOutputCardinality(srcMethodInd) > 1)
									: true;
							if (proceed) {
								// remove the relation between the data and
								// method individuals in the saw
								state.removeIsOutputOf(data, srcMethodInd);
								// get the source-end coordinates of the edge to
								// create source individual
								@SuppressWarnings("rawtypes")
								List edgepoints = GraphConstants.getPoints(edge
										.getAttributes());
								Point2D coord = (Point2D) edgepoints.get(0);
								Point srcCoord = new Point((int) coord.getX(),
										(int) coord.getY());
								// create source individual to replace
								// disconnected method individual
								OntClass srcInd = state
										.createSourceSinkIndividual(null,
												srcCoord);
								state.setIsOutputOf(data, srcInd);
								// update graph
								IndividualNode srcIndNode = new IndividualNode(
										srcInd, true);
								WdoView wdoView = (WdoView) WdoApp
										.getApplication().getMainView();
								wdoView.addSourceOrSink(edge, srcIndNode, true);
							}
						}
					}
					// make changes to the target part of the edge, similar code
					// to source part
					DefaultPort targetPort = (DefaultPort) model
							.getTarget(edge);
					if (targetPort == null) {
						OntClass targetMethodInd = SAW.getIsInputTo(data);
						if (targetMethodInd != null) {
							boolean proceed = (SAW.isSink(targetMethodInd)) ? (SAW
									.getInputOutputCardinality(targetMethodInd) > 1)
									: true;
							if (proceed) {
								// remove the relation between the data and
								// method individuals in the mbw
								state.removeIsInputTo(data, targetMethodInd);
								// get the target-end coordinates of the edge to
								// create sink individual
								@SuppressWarnings("rawtypes")
								List edgepoints = GraphConstants.getPoints(edge
										.getAttributes());
								Point2D coord = (Point2D) edgepoints
										.get(edgepoints.size() - 1);
								Point targetCoord = new Point(
										(int) coord.getX(), (int) coord.getY());
								// create sink individual to replace
								// disconnected method individual
								OntClass targetInd = state
										.createSourceSinkIndividual(null,
												targetCoord);
								state.setIsInputTo(data, targetInd);
								// update graph
								IndividualNode targetIndNode = new IndividualNode(
										targetInd, true);
								WdoView wdoView = (WdoView) WdoApp
										.getApplication().getMainView();
								wdoView.addSourceOrSink(edge, targetIndNode,
										false);
							}
						}
					}
				}
			}
		}
	}
}
