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
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoapi.SAW;

/**
 * GUI component to list individuals sorted by their Local Name
 * 
 * @author Leonardo Salayandia
 */
public class IndividualList extends JList {
	private static final long serialVersionUID = 1L;

	public IndividualList() {
		super();
		this.setCellRenderer(new IndividualListCellRenderer());
	}

	/**
	 * Sets the model of the list. Sorts the list being passed using the local
	 * name of Individuals as the sorting key
	 * 
	 * @param list
	 */
	public void setModel(Vector<OntClass> list) {
		// get the local names of individuals to use as key to sort the list
		HashMap<String, OntClass> temp = new HashMap<String, OntClass>();
		for (Iterator<OntClass> i = list.iterator(); i.hasNext();) {
			OntClass ind = i.next();
			temp.put(ind.getURI(), ind);

		}

		// sort by local name
		String[] sortedNames = new String[temp.size()];
		temp.keySet().toArray(sortedNames);
		Arrays.sort(sortedNames);
		OntClass[] sortedList = new OntClass[temp.size()];
		for (int i = 0; i < sortedList.length; i++) {
			sortedList[i] = temp.get(sortedNames[i]);
		}
		// set model with sorted list
		setModel(new IndividualListModel(sortedList));
	}

	/**
	 * Model for Individual list
	 * 
	 * @author Leonardo Salayandia
	 */
	private class IndividualListModel extends AbstractListModel {
		private static final long serialVersionUID = 1L;
		OntClass[] list;

		protected IndividualListModel(OntClass[] list) {
			super();
			this.list = list;
		}

		@Override
		public int getSize() {
			return list.length;
		}

		@Override
		public Object getElementAt(int index) {
			return list[index];
		}
	}

	/**
	 * Cell renderer for Individual List
	 * 
	 * @author Leonardo Salayandia
	 */
	private class IndividualListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		protected IndividualListCellRenderer() {
			super();
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			OntClass ind = (OntClass) value;
			setText(SAW.getSAWInstanceQName(ind));
			setToolTipText(ind.getURI());
			return this;
		}
	}
}
