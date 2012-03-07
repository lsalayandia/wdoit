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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoit.action.WDORelationsDeleteKeyListener;
import edu.utep.cybershare.wdoit.action.WDORelationsDropTargetListener;
//import edu.utep.cybershare.wdoit.context.Styles;
import edu.utep.cybershare.wdoit.graphics.GPCellViewFactory;
import edu.utep.cybershare.wdoit.graphics.OntClassNode;

/**
 * @author Leonardo Salayandia
 * @deprecated part of the old gui. TODO Needs to be migrated to the new gui
 */
public class WDORelationsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String WORKFLOW_RELATIONS_TITLE = "Workflow Relations";
	private JLabel titleLabel;
	private JScrollPane relationsPane;
	private JGraph relationsGraph;
	private OntClassNode centerNode;
	private ArrayList<OntClassNode> inputNodes;
	private ArrayList<OntClassNode> outputNodes;
	private WDORelationsDeleteKeyListener deleteKeyListener;

	/**
	 * 
	 */
	public WDORelationsPanel() {
		this.relationsGraph = null;
		this.centerNode = null;
		this.centerNode = null;
		this.inputNodes = new ArrayList<OntClassNode>();
		this.outputNodes = new ArrayList<OntClassNode>();
		this.deleteKeyListener = new WDORelationsDeleteKeyListener();

		initializeComponents();
		createContentPane();
	}

	private void initializeComponents() {
		titleLabel = new JLabel();
		// titleLabel.setFont(Styles.SECTION_TITLE_FONT);
		// titleLabel.setForeground(Styles.FOREGROUND_COLOR);
		titleLabel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setText(WORKFLOW_RELATIONS_TITLE);

		relationsPane = new JScrollPane();
		relationsPane.setBorder(null);
		relationsPane
				.setHorizontalScrollBarPolicy(ScrollPaneLayout.HORIZONTAL_SCROLLBAR_ALWAYS);
		relationsPane
				.setVerticalScrollBarPolicy(ScrollPaneLayout.VERTICAL_SCROLLBAR_AS_NEEDED);
		relationsPane.setOpaque(true);
		relationsPane.setAutoscrolls(true);
		relationsPane.setAlignmentX(new Float(1));
		relationsPane.setAlignmentY(new Float(1));
		relationsPane.setViewportView(null);
	}

	private void createContentPane() {
		this.setLayout(new BorderLayout());
		// this.setBackground(Styles.BACKGROUND_COLOR);
		this.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		this.add(titleLabel, BorderLayout.NORTH);
		this.add(relationsPane, BorderLayout.CENTER);
		this.setMinimumSize(new Dimension(300, 200));
	}

	protected void setCenterClass(OntClass cls) {
		this.inputNodes.clear();
		this.outputNodes.clear();
		if (cls == null) {
			this.relationsGraph = null;
			this.centerNode = null;
			// update title text
			titleLabel.setText(WORKFLOW_RELATIONS_TITLE);
		} else {
			// update title text with the label
			String label = WDO.getClassQName(cls);
			titleLabel.setText(WORKFLOW_RELATIONS_TITLE + " for " + label);

			GraphModel model = new DefaultGraphModel();
			this.relationsGraph = new JGraph(model);
			this.relationsGraph.getGraphLayoutCache().setFactory(
					new GPCellViewFactory());

			// Control-drag should clone selection
			this.relationsGraph.setCloneable(false);
			// Do not allow labels to be edited in the graph
			this.relationsGraph.setEditable(false);
			// Enable edit without final RETURN keystroke
			this.relationsGraph.setInvokesStopCellEditing(true);
			// When over a cell, jump to its default port (we only have one,
			// anyway)
			this.relationsGraph.setJumpToDefaultPort(true);
			// Insert the cells via the cache, so they get selected
			Point centerPt = new Point(relationsPane.getSize().width / 2,
					relationsPane.getSize().height / 2);
			this.centerNode = new OntClassNode(cls, centerPt, false);
			this.relationsGraph.getGraphLayoutCache().insert(centerNode);
			this.relationsGraph.setDropEnabled(false);
			this.relationsGraph.setDropTarget(new DropTarget(this,
					DnDConstants.ACTION_COPY,
					new WDORelationsDropTargetListener()));
			this.relationsGraph.addKeyListener(deleteKeyListener);
		}
		this.relationsPane.setViewportView(relationsGraph);
	}

	protected void addClassToLeft(OntClass cls, String edgeLabel) {
		if (this.centerNode != null) {
			// Determine location of new node
			Point centerPt = new Point(relationsPane.getSize().width / 2,
					relationsPane.getSize().height / 2);
			int nodeIndex = this.inputNodes.size();
			int xOffset = 0;// (Styles.WORKFLOW_CONCEPT_WIDTH +
							// Styles.WORKFLOW_HOR_GAP) * -1;
			int yOffset;
			if (nodeIndex % 2 == 0) {
				yOffset = 0;// (Styles.WORKFLOW_CONCEPT_HEIGHT +
							// Styles.WORKFLOW_VERT_GAP) * (nodeIndex/2) * -1;
			} else {
				yOffset = 0;// (Styles.WORKFLOW_CONCEPT_HEIGHT +
							// Styles.WORKFLOW_VERT_GAP) * ((nodeIndex + 1)/2);
			}
			Point newLoc = new Point(centerPt.x + xOffset, centerPt.y + yOffset);
			OntClassNode newNode = new OntClassNode(cls, newLoc, true);
			this.inputNodes.add(newNode);

			// Create connecting edge
			DefaultEdge edge = new DefaultEdge(edgeLabel);// add relationship
															// name
			// Fetch the ports from the new vertices, and connect them with the
			// edge
			edge.setSource(newNode.getChildAt(0));
			edge.setTarget(this.centerNode.getChildAt(0));

			// Set Arrow Style for edge and make it un-selectable
			int arrow = GraphConstants.ARROW_CLASSIC;
			GraphConstants.setLineEnd(edge.getAttributes(), arrow);
			GraphConstants.setEndFill(edge.getAttributes(), true);
			GraphConstants.setSelectable(edge.getAttributes(), false);

			// Add new node and edge to existing graph
			this.relationsGraph.getGraphLayoutCache().insert(newNode);
			this.relationsGraph.getGraphLayoutCache().insert(edge);
		}
	}

	protected void addClassToRight(OntClass cls, String edgeLabel) {
		if (this.centerNode != null) {
			// Determine location of new node
			Point centerPt = new Point(relationsPane.getSize().width / 2,
					relationsPane.getSize().height / 2);
			int nodeIndex = this.outputNodes.size();
			int xOffset = 0;// (Styles.WORKFLOW_CONCEPT_WIDTH +
							// Styles.WORKFLOW_HOR_GAP);
			int yOffset;
			if (nodeIndex % 2 == 0) {
				yOffset = 0;// (Styles.WORKFLOW_CONCEPT_HEIGHT +
							// Styles.WORKFLOW_VERT_GAP) * (nodeIndex/2) * -1;
			} else {
				yOffset = 0;// (Styles.WORKFLOW_CONCEPT_HEIGHT +
							// Styles.WORKFLOW_VERT_GAP) * ((nodeIndex + 1)/2);
			}
			Point newLoc = new Point(centerPt.x + xOffset, centerPt.y + yOffset);
			OntClassNode newNode = new OntClassNode(cls, newLoc, true);
			this.outputNodes.add(newNode);

			// Create connecting edge
			DefaultEdge edge = new DefaultEdge(edgeLabel);// add relationship
															// name
			// Fetch the ports from the new vertices, and connect them with the
			// edge
			edge.setSource(this.centerNode.getChildAt(0));
			edge.setTarget(newNode.getChildAt(0));
			// Set Arrow Style for edge and make it un-selectable
			int arrow = GraphConstants.ARROW_CLASSIC;
			GraphConstants.setLineEnd(edge.getAttributes(), arrow);
			GraphConstants.setEndFill(edge.getAttributes(), true);
			GraphConstants.setSelectable(edge.getAttributes(), false);

			// Add new node and edge to existing graph
			this.relationsGraph.getGraphLayoutCache().insert(newNode);
			this.relationsGraph.getGraphLayoutCache().insert(edge);
		}
	}
}
