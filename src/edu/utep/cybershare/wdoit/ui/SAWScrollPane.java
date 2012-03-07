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
package edu.utep.cybershare.wdoit.ui;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoit.action.WorkflowCompositionNavigatorListener;
import edu.utep.cybershare.wdoit.action.WorkflowCreateRelationListener;
import edu.utep.cybershare.wdoit.action.WorkflowDeleteKeyListener;
import edu.utep.cybershare.wdoit.action.WorkflowDropTargetListener;
import edu.utep.cybershare.wdoit.action.WorkflowEditLabelListener;
import edu.utep.cybershare.wdoit.action.WorkflowPositionListener;
import edu.utep.cybershare.wdoit.action.WorkflowRemoveRelationListener;
import edu.utep.cybershare.wdoit.action.WorkflowSelectionListener;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.graphics.GPCellViewFactory;
import edu.utep.cybershare.wdoit.graphics.IndividualEdge;
import edu.utep.cybershare.wdoit.graphics.IndividualNode;

/**
 * @author Leonardo Salayandia
 * 
 */
public class SAWScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private JGraph sawGraph;
	private GraphModel model;
	private WorkflowDeleteKeyListener deleteKeyListener;
	private WorkflowDropTargetListener dropTargetListener;
	private WorkflowPositionListener posListener;
	private WorkflowRemoveRelationListener removeRelationListener;
	private WorkflowCreateRelationListener createRelationListener;
	private WorkflowEditLabelListener editLabelListener;
	private WorkflowCompositionNavigatorListener compositionNavigatorListener;
	private WorkflowSelectionListener selectionListener;

	public SAWScrollPane() {
		this.sawGraph = null;
		this.model = null;
		this.deleteKeyListener = new WorkflowDeleteKeyListener();
		this.dropTargetListener = new WorkflowDropTargetListener();
		this.posListener = new WorkflowPositionListener();
		this.selectionListener = new WorkflowSelectionListener();
		this.removeRelationListener = new WorkflowRemoveRelationListener();
		this.createRelationListener = new WorkflowCreateRelationListener();
		// this.editLabelListener = new WorkflowEditLabelListener(); //TODO:
		// This is intended to be able to modify labels directly on the graph,
		// but it is causing issues with the creating new label names that
		// include the types of the individual automatically.
		this.compositionNavigatorListener = new WorkflowCompositionNavigatorListener();
	}

	/**
	 * Create the workflow graph for the specified workflow ontmodel.
	 * 
	 * @param workflow
	 * @return
	 */
	public static JGraph createWorkflowGraph(OntModel workflow) {
		JGraph ans = null;
		if (workflow != null) {
			// create the workflow graph
			HashMap<OntClass, DefaultGraphCell> cellMap = new HashMap<OntClass, DefaultGraphCell>();
			State state = State.getInstance();
			// get the method individuals, and create a node for each
			for (Iterator<OntClass> i = state.listMethodIndividuals(workflow); i
					.hasNext();) {
				OntClass ind = i.next();
				// all method individuals should be anonymous,
				// do not include semantic abstract workflow individuals in the
				// workflow graph
				// if (ind.isAnon() && !SAW.isSemanticAbstractWorkflowType(ind))
				// {
				if (!SAW.isSemanticAbstractWorkflowType(ind)) {
					IndividualNode indNode = new IndividualNode(ind, true);
					cellMap.put(ind, indNode);
				}
			}
			// get the pmlp:source individuals, and create a node for each
			for (Iterator<OntClass> i = state
					.listPMLSourceIndividuals(workflow); i.hasNext();) {
				OntClass ind = i.next();
				// exception, InferenceEngine is a subclass of Source, but we
				// only want to display Source
				// if (!SAW.isInferenceEngineType(ind)) {
				// NOTE: This if statement is no longer needed becase
				// InferenceEngines are OWL instances and pmlp:Sources are
				// classes
				IndividualNode indNode = new IndividualNode(ind, true);
				cellMap.put(ind, indNode);
				// }
			}
			// get the data individuals, and create an edge for each
			for (Iterator<OntClass> i = state.listDataIndividuals(workflow); i
					.hasNext();) {
				OntClass data = i.next();
				// get source of edge
				DefaultGraphCell srcNode = null;
				OntClass src = SAW.getIsOutputOf(data);
				if (src != null) {
					srcNode = cellMap.get(src);
				}
				// get target of edge
				DefaultGraphCell targetNode = null;
				OntClass target = SAW.getIsInputTo(data);
				if (target != null) {
					targetNode = cellMap.get(target);
				}
				// create edge
				IndividualEdge dataCell = new IndividualEdge(data, srcNode,
						targetNode);
				cellMap.put(data, dataCell);
			}
			// set GUI
			DefaultGraphCell[] cells = new DefaultGraphCell[cellMap.size()];
			cellMap.values().toArray(cells);

			DefaultGraphModel model = new DefaultGraphModel();
			ans = new JGraph(model);
			ans.getGraphLayoutCache().setFactory(new GPCellViewFactory());
			ans.getGraphLayoutCache().insert(cells);
		}
		return ans;
	}

	protected void setWorkflow(JGraph graph, WorkflowPopupMenu workflowPopupMenu) {
		this.sawGraph = graph;
		if (this.sawGraph == null) {
			this.model = null;
		} else {
			this.model = this.sawGraph.getModel();
			// Control-drag should clone selection
			this.sawGraph.setCloneable(false);
			// Do not allow labels to be edited in the graph
			this.sawGraph.setEditable(true);
			// Enable edit without final RETURN keystroke
			this.sawGraph.setInvokesStopCellEditing(true);
			// When over a cell, jump to its default port (we only have one,
			// anyway)
			this.sawGraph.setJumpToDefaultPort(true);
			// Set graph to be connectable through GUI
			this.sawGraph.setConnectable(true);
			// set graph to be movable
			this.sawGraph.setMoveable(true);
			// set graph to be disconnectable through GUI
			this.sawGraph.setDisconnectable(true);
			// add listeners
			this.sawGraph.setDropTarget(new DropTarget(this,
					DnDConstants.ACTION_COPY, dropTargetListener));
			this.sawGraph.addKeyListener(deleteKeyListener);
			this.sawGraph.addMouseListener(selectionListener);
			this.sawGraph.addMouseListener(compositionNavigatorListener);
			this.addModelListeners();
			// set pop up menu
			this.sawGraph.setComponentPopupMenu(workflowPopupMenu);
		}
		this.setViewportView(sawGraph);
		// update state
		this.selectionListener.updateSelectedInstance(sawGraph);
	}

	/**
	 * Deactivate model listeners
	 */
	private void addModelListeners() {
		if (this.model != null) {
			this.model.addGraphModelListener(posListener);
			this.model.addGraphModelListener(removeRelationListener);
			this.model.addGraphModelListener(createRelationListener);
			this.model.addGraphModelListener(editLabelListener);
		}
	}

	/**
	 * Reactivate model listerners
	 */
	private void removeModelListeners() {
		if (this.model != null) {
			this.model.removeGraphModelListener(posListener);
			this.model.removeGraphModelListener(removeRelationListener);
			this.model.removeGraphModelListener(createRelationListener);
			this.model.removeGraphModelListener(editLabelListener);
		}
	}

	/**
	 * Adds a cell to the current workflow The cell usually represents a
	 * workflow node or a workflow edge
	 * 
	 * @param cell
	 */
	protected void addWorkflowCell(DefaultGraphCell cell) {
		if (sawGraph != null && cell != null) {
			this.removeModelListeners(); // turn off listeners
			this.sawGraph.getGraphLayoutCache().insert(cell);
			this.addModelListeners(); // turn listeners back on
			this.setViewportView(sawGraph);
			// update state
			this.selectionListener.updateSelectedInstance(sawGraph);
		}
	}

	/**
	 * Replace an existing node for another. For example, replace a source by a
	 * method, or a source for another source.
	 * 
	 * @param replacerNode
	 * @param replaceeNode
	 */
	protected void replaceWorkflowNode(IndividualNode replacerNode,
			IndividualNode replaceeNode) {
		if (replacerNode != null && replaceeNode != null && sawGraph != null) {
			// turn off listeners
			this.removeModelListeners();
			GraphLayoutCache cache = sawGraph.getGraphLayoutCache();
			// move incoming edges of replacee node to point to replacer node
			List incomingEdges = cache.getIncomingEdges(replaceeNode, null,
					true, false);
			if (incomingEdges != null) {
				for (Iterator i = incomingEdges.iterator(); i.hasNext();) {
					IndividualEdge edge = (IndividualEdge) i.next();
					ConnectionSet cs = new ConnectionSet(edge,
							replacerNode.getChildAt(0), false);
					this.sawGraph.getGraphLayoutCache().edit(null, cs, null,
							null);
				}
			}
			// move outgoing edges of replacee node to point to replacer node
			List outgoingEdges = cache.getOutgoingEdges(replaceeNode, null,
					true, false);
			if (outgoingEdges != null) {
				for (Iterator i = outgoingEdges.iterator(); i.hasNext();) {
					IndividualEdge edge = (IndividualEdge) i.next();
					ConnectionSet cs = new ConnectionSet(edge,
							replacerNode.getChildAt(0), true);
					this.sawGraph.getGraphLayoutCache().edit(null, cs, null,
							null);
				}
			}
			// update disconnectable property of replacer node
			replacerNode.setDisconnectable();
			this.sawGraph.getGraphLayoutCache().editCell(replacerNode,
					replacerNode.getAttributes());
			// remove replacee node
			Object[] removeCells = { replaceeNode };
			this.sawGraph.getGraphLayoutCache().remove(removeCells);
			// turn listeners back on
			this.addModelListeners();
			// update state
			this.selectionListener.updateSelectedInstance(sawGraph);
			// refresh UI
			this.setViewportView(sawGraph);
		}
	}

	/**
	 * Adds two node cell (typically representing either a Source or a Sink),
	 * and attaches the new cell to the specified edge. The end of the edge to
	 * which the new cell is attached is determined by the source boolean
	 * 
	 * @param edge
	 * @param sourceOrSink
	 * @param source
	 */
	protected void addSourceOrSink(IndividualEdge edge,
			DefaultGraphCell sourceOrSink, boolean source) {
		if (edge != null && sourceOrSink != null && sawGraph != null) {
			// turn off listeners
			this.removeModelListeners();
			GraphLayoutCache cache = this.sawGraph.getGraphLayoutCache();
			// update the disconnectable property of the current vertices before
			// adding the new vertex
			Object[] nodes = cache.getCells(false, true, false, false);
			for (int i = 0; i < nodes.length; i++) {
				IndividualNode indNode = (IndividualNode) nodes[i];
				indNode.setDisconnectable();
				this.sawGraph.getGraphLayoutCache().editCell(indNode,
						indNode.getAttributes());
			}
			// add new vertex and connect the edge on the specified side
			this.sawGraph.getGraphLayoutCache().insert(sourceOrSink);
			ConnectionSet cs = new ConnectionSet(edge,
					sourceOrSink.getChildAt(0), source);
			this.sawGraph.getGraphLayoutCache().edit(null, cs, null, null);
			// turn listeners back on
			this.addModelListeners();
			// update state
			this.selectionListener.updateSelectedInstance(sawGraph);
			// refresh UI
			this.setViewportView(sawGraph);
		}
	}
}
