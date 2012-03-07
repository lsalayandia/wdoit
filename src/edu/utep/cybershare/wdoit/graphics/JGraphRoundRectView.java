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

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * This code comes from JGraph mycellview example
 * 
 * @author rvalyi, license of this file: LGPL as stated by the Free Software
 *         Foundation
 * 
 */
@SuppressWarnings("serial")
public class JGraphRoundRectView extends VertexView {
	public static transient ActivityRenderer renderer = new ActivityRenderer();

	public JGraphRoundRectView() {
		super();
	}

	public JGraphRoundRectView(Object cell) {
		super(cell);
	}

	/**
	 * Returns the intersection of the bounding rectangle and the straight line
	 * between the source and the specified point p. The specified point is
	 * expected not to intersect the bounds.
	 */
	// todo public Point2D getPerimeterPoint(Point source, Point p) {
	/**
	 * getArcSize calculates an appropriate arc for the corners of the rectangle
	 * for boundary size cases of width and height
	 */
	public static int getArcSize(int width, int height) {
		int arcSize;

		// The arc width of a activity rectangle is 1/5th of the larger
		// of the two of the dimensions passed in, but at most 1/2
		// of the smaller of the two. 1/5 because it looks nice and 1/2
		// so the arc can complete in the given dimension

		if (width <= height) {
			arcSize = height / 5;
			if (arcSize > (width / 2)) {
				arcSize = width / 2;
			}
		} else {
			arcSize = width / 5;
			if (arcSize > (height / 2)) {
				arcSize = height / 2;
			}
		}

		return arcSize;
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class ActivityRenderer extends VertexRenderer {

		/**
		 * Return a slightly larger preferred size than for a rectangle.
		 */
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			d.width += d.height / 5;
			return d;
		}

		public void paint(Graphics g) {
			int b = borderWidth;
			Graphics2D g2 = (Graphics2D) g;
			Dimension d = getSize();
			boolean tmp = selected;
			int roundRectArc = JGraphRoundRectView.getArcSize(d.width - b,
					d.height - b);
			if (super.isOpaque()) {
				g.setColor(super.getBackground());
				if (gradientColor != null && !preview) {
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(),
							getWidth(), getHeight(), gradientColor, true));
				}
				g.fillRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
						d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
			}
			try {
				setBorder(null);
				setOpaque(false);
				selected = false;
				super.paint(g);
			} finally {
				selected = tmp;
			}
			if (bordercolor != null) {
				g.setColor(bordercolor);
				g2.setStroke(new BasicStroke(b));
				g.drawRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
						d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
			}
			if (selected) {
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g.setColor(highlightColor);
				g.drawRoundRect(b / 2, b / 2, d.width - (int) (b * 1.5),
						d.height - (int) (b * 1.5), roundRectArc, roundRectArc);
			}
		}
	}

}