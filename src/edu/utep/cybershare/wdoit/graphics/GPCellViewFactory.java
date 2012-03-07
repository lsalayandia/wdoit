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

import java.util.Map;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexView;

/**
 * A default view factory for a JGraph. This simple factory associate a given
 * cell class to a cell view. This is a javabean, just parameter it correctly in
 * order it meets your requirements (else subclass it or subclass
 * DefaultCellViewFactory). You can also recover the gpConfiguration of that
 * javabean via an XML file via XMLEncoder/XMLDecoder.
 * 
 * @author rvalyi, license of this file: LGPL as stated by the Free Software
 *         Foundation
 */
@SuppressWarnings("serial")
public class GPCellViewFactory extends DefaultCellViewFactory {

	public static final String VIEW_CLASS_KEY = "viewClassKey";

	@SuppressWarnings("unchecked")
	public static final void setViewClass(Map map, String viewClass) {
		map.put(VIEW_CLASS_KEY, viewClass);
	}

	protected VertexView createVertexView(Object v) {
		try {
			DefaultGraphCell cell = (DefaultGraphCell) v;
			String viewClass = (String) cell.getAttributes()
					.get(VIEW_CLASS_KEY);

			VertexView view = (VertexView) Thread.currentThread()
					.getContextClassLoader().loadClass(viewClass).newInstance();
			view.setCell(v);
			return view;
		} catch (Exception ex) {
		}
		return super.createVertexView(v);
	}
}
