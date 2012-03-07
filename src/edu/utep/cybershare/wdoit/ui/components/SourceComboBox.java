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
package edu.utep.cybershare.wdoit.ui.components;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;
import edu.utep.cybershare.wdoit.context.State;

/**
 * Combo Box that shows the class pmlp:Source, and its children.
 * 
 * @author Leonardo Salayandia
 */
public class SourceComboBox extends JComboBox {
	private static final long serialVersionUID = 1L;
	Vector<OntClass> options;
	HashMap<OntClass, Integer> indentLevels;

	public SourceComboBox() {
		super();
		options = new Vector<OntClass>();
		indentLevels = new HashMap<OntClass, Integer>();

		State state = State.getInstance();
		OntModel basewdo = state.getBaseWDO();
		OntClass srcCls = basewdo.getOntClass(WDO_Metamodel.SOURCE_URI);
		addChildren(srcCls, 0);

		setModel(new DefaultComboBoxModel(options));
		setRenderer(new SourceListCellRenderer(indentLevels));
	}

	private void addChildren(OntClass cls, int indentLevel) {
		options.add(cls);
		indentLevels.put(cls, indentLevel);
		indentLevel++;
		HashMap<String, OntClass> children = new HashMap<String, OntClass>();
		for (ExtendedIterator<OntClass> i = cls.listSubClasses(true); i
				.hasNext();) {
			OntClass childcls = i.next();
			children.put(WDO.getClassQName(childcls), childcls);

		}
		if (!children.isEmpty()) {
			Object[] childrenArray = new String[children.size()];
			children.keySet().toArray(childrenArray);
			Arrays.sort(childrenArray);
			for (int i = 0; i < childrenArray.length; i++) {
				addChildren(children.get(childrenArray[i]), indentLevel);
			}
		}
	}

	/**
	 * Cell renderer for Source List
	 * 
	 * @author Leonardo Salayandia
	 */
	private class SourceListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final String INDENT_SPACER = "    ";
		HashMap<OntClass, Integer> indentLevels;

		protected SourceListCellRenderer(HashMap<OntClass, Integer> indentLevels) {
			super();
			this.indentLevels = indentLevels;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			OntClass cls = (OntClass) value;
			if (index < 0) {
				setText(WDO.getClassQName(cls));
			} else {
				String indent = "";
				for (int i = 0; i < indentLevels.get(cls); i++) {
					indent = indent + INDENT_SPACER;
				}
				setText(indent + WDO.getClassQName(cls));
			}
			setToolTipText(WDO.getClassComment(cls));
			return this;
		}
	}
}
