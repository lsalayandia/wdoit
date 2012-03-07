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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * Combo box that shows a list of Individuals. Each Individual has the following
 * attributes: 1. A URI (String) 2. A friendly name (String) 3. A description
 * (String)
 * 
 * The combo box will show the listing of individuals using their friendly names
 * and in ascending order The combo box has auto-completion functionality to
 * facilitate selection in large lists of individuals
 * 
 * @author Leonardo Salayandia
 */
public class IndividualComboBox extends JComboBox {
	private static final long serialVersionUID = 1L;

	public IndividualComboBox() {
		super();
		initializeComboBox();
	}

	public IndividualComboBox(Individual[] individuals) {
		super();
		initializeComboBox();
		setIndividuals(individuals);
	}

	public IndividualComboBox(Vector<Individual> individuals) {
		super();
		initializeComboBox();
		setIndividuals(individuals);
	}

	private void initializeComboBox() {
		setEditable(true);
		setRenderer(new IndividualListCellRenderer());
		new BinaryLookup(this);
	}

	public void setIndividuals(Vector<Individual> individuals) {
		Individual[] temp = new Individual[individuals.size()];
		individuals.toArray(temp);
		setIndividuals(temp);
	}

	public void setIndividuals(Individual[] individuals) {
		Arrays.sort(individuals);
		this.setModel(new DefaultComboBoxModel(individuals));
	}

	protected void addIndividual(Individual ind) {
		if (ind != null) {
			DefaultComboBoxModel model = (DefaultComboBoxModel) getModel();
			Vector<Individual> newmodel = new Vector<Individual>();
			for (int i = 0; i < model.getSize(); i++) {
				newmodel.add((Individual) model.getElementAt(i));
			}
			newmodel.add(ind);
			setIndividuals(newmodel);
			setSelectedItem(ind);
		}
	}

	public int containsElement(IndividualComboBox.Individual ind) {
		int contains = -1;
		DefaultComboBoxModel model = (DefaultComboBoxModel) getModel();

		for (int i = 0; i < model.getSize(); i++) {
			if (((IndividualComboBox.Individual) model.getElementAt(i))
					.equals(ind)) {
				contains = i;
				break;
			}
		}
		return contains;
	}

	/**
	 * Item used to populate the IndividualComboBox model.
	 * 
	 * @author Leonardo Salayandia
	 */
	public class Individual implements Comparable<Individual> {
		private String uri;
		private String friendlyName;
		private String description;

		public Individual(String uri, String name, String desc) {
			this.uri = uri;
			friendlyName = name;
			description = desc;
		}

		public String getURI() {
			return uri;
		}

		public String getName() {
			return friendlyName;
		}

		public String getDescription() {
			return description;
		}

		@Override
		public boolean equals(Object o) {
			if (o != null && o instanceof Individual) {
				Individual ind = (Individual) o;
				return getURI().equalsIgnoreCase(ind.getURI());
			}
			return false;
		}

		@Override
		public String toString() {
			return (friendlyName == null || friendlyName.isEmpty()) ? getURI()
					: getName();
		}

		@Override
		public int compareTo(Individual o) {
			if (o == null)
				return 1;
			int ans = toString().compareTo(o.toString());
			// if friendly names are the same and URIs are not the same, compare
			// based on URIs
			if (ans == 0 && !equals(o)) {
				ans = getURI().compareTo(o.getURI());
			}
			return ans;
		}
	}

	/**
	 * Cell renderer for Individual List
	 * 
	 * @author Leonardo Salayandia
	 */
	private class IndividualListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			Individual ind = (Individual) value;
			setText(ind.toString());
			setToolTipText(ind.getURI() + " : " + ind.getDescription());

			return this;
		}
	}

	/**
	 * Inside JComboBox: adding automatic completion Code from:
	 * http://www.orbital-computer.de/JComboBox/
	 * 
	 * @author Thomas Bierhance, thomas@orbital-computer.de
	 */
	private class BinaryLookup extends PlainDocument {
		private static final long serialVersionUID = 1L;
		private IndividualComboBox comboBox;
		private JTextComponent editor;
		// flag to indicate if setSelectedItem has been called
		// subsequent calls to remove/insertString should be ignored
		private boolean selecting;
		private boolean hitBackspace;
		private boolean hitBackspaceOnSelection;

		protected BinaryLookup(IndividualComboBox cb) {
			comboBox = cb;
			editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
			editor.setDocument(this);

			selecting = false;

			comboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!selecting)
						highlightCompletedText(0);
				}
			});

			editor.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (comboBox.isDisplayable())
						comboBox.setPopupVisible(true);
					hitBackspace = false;
					switch (e.getKeyCode()) {
					// determine if the pressed key is backspace (needed by the
					// remove method)
					case KeyEvent.VK_BACK_SPACE:
						hitBackspace = true;
						hitBackspaceOnSelection = editor.getSelectionStart() != editor
								.getSelectionEnd();
						break;
					// ignore delete key
					case KeyEvent.VK_DELETE:
						e.consume();
						UIManager.getLookAndFeel().provideErrorFeedback(
								comboBox);
						break;
					}
				}
			});

			editor.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					highlightCompletedText(0);
				}

				@Override
				public void focusLost(FocusEvent e) {
					comboBox.setPopupVisible(false);
				}
			});

			setPrototypeValue();
			// handle initially selected object
			Object selected = comboBox.getSelectedItem();
			if (selected != null) {
				setText(selected.toString());
			}
			highlightCompletedText(0);
		}

		private void setPrototypeValue() {
			JList list = getListBox();
			Object value = getPrototypeValue(list);
			comboBox.setPrototypeDisplayValue(value);
			list.setPrototypeCellValue(value);
		}

		private Object getPrototypeValue(JList list) {
			Object prototypeValue = null;
			double prototypeWidth = 0;
			ListCellRenderer renderer = comboBox.getRenderer();
			for (int i = 0; i < comboBox.getModel().getSize(); i++) {
				Object value = comboBox.getModel().getElementAt(i);
				Component c = renderer.getListCellRendererComponent(list,
						value, i, false, false);
				double width = c.getPreferredSize().getWidth();
				if (width > prototypeWidth) {
					prototypeWidth = width;
					prototypeValue = value;
				}
			}
			return prototypeValue;
		}

		private JList getListBox() {
			JList listBox;
			try {
				Field field = JComponent.class.getDeclaredField("ui");
				field.setAccessible(true);
				BasicComboBoxUI ui = (BasicComboBoxUI) field.get(comboBox);
				field = BasicComboBoxUI.class.getDeclaredField("listBox");
				field.setAccessible(true);
				listBox = (JList) field.get(ui);
			} catch (NoSuchFieldException nsfe) {
				throw new RuntimeException(nsfe);
			} catch (IllegalAccessException iae) {
				throw new RuntimeException(iae);
			}
			return listBox;
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			// return immediately when selecting an item
			if (selecting)
				return;
			if (hitBackspace) {
				// user hit backspace => move selection backwards
				if (offs > 0) {
					if (hitBackspaceOnSelection)
						offs--;
				} else {
					// user hit backspace with the cursor positioned on the
					// start => beep
					UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
				}
				highlightCompletedText(offs);
			} else {
				super.remove(offs, len);
			}
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			// return immediately when selecting an item
			if (selecting)
				return;
			// insert the string into the document
			super.insertString(offs, str, a);
			// lookup and select a matching item
			Object item = lookupItem(getText(0, getLength()));
			if (item != null) {
				setSelectedItem(item);
			} else {
				// keep old item selected if there is no match
				item = comboBox.getSelectedItem();
				// imitate no insert (later on offs will be incremented by
				// str.length(): selection won't move forward)
				offs = offs - str.length();
				// provide feedback to the user that input has been received but
				// cannot be accepted
				UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
			}
			setText(item.toString());
			// select the completed part
			highlightCompletedText(offs + str.length());
		}

		private void setText(String text) {
			try {
				// remove all text and insert the completed string
				super.remove(0, getLength());
				super.insertString(0, text, null);
			} catch (BadLocationException e) {
				throw new RuntimeException(e.toString());
			}
		}

		private void highlightCompletedText(int start) {
			editor.setCaretPosition(getLength());
			editor.moveCaretPosition(start);
		}

		private void setSelectedItem(Object item) {
			selecting = true;
			comboBox.getModel().setSelectedItem(item);
			selecting = false;
		}

		private Object binaryLookup(String pattern) {
			int bottom = 0;
			int top = comboBox.getModel().getSize() - 1;
			int pos = 0;
			Object item = null;
			// search for a matching item
			while (bottom <= top) {
				pos = (bottom + top) >> 1;
				item = comboBox.getModel().getElementAt(pos);
				// int compare = compareStartIgnoreCase(item.toString(),
				// pattern);
				int compare = compareStart(item.toString(), pattern);
				if (compare == 0) {
					break;
				} else if (compare > 0) {
					bottom = pos + 1;
				} else {
					top = pos - 1;
				}
			}
			// if no item matches bottom is greater than top
			if (bottom > top)
				return null;
			// search for the _first_ matching item
			for (int i = bottom; i < pos; i++) {
				Object anItem = comboBox.getModel().getElementAt(i);
				// if (startsWithIgnoreCase(anItem.toString(), pattern)) {
				if (anItem.toString().startsWith(pattern)) {
					return anItem;
				}
			}
			return item;
		}

		private Object lookupItem(String pattern) {
			Object selectedItem = comboBox.getModel().getSelectedItem();
			// only search for a different item if the currently selected does
			// not match
			// if (selectedItem != null &&
			// startsWithIgnoreCase(selectedItem.toString(), pattern)) {
			if (selectedItem != null
					&& selectedItem.toString().startsWith(pattern)) {
				return selectedItem;
			}
			return binaryLookup(pattern);
		}

		// private boolean startsWithIgnoreCase(String str1, String str2) {
		// return str1.toUpperCase().startsWith(str2.toUpperCase());
		// }
		//
		// private int compareStartIgnoreCase(String str1, String str2) {
		// char[] ch1 = str1.toCharArray();
		// char[] ch2 = str2.toCharArray();
		// for (int i=0; i < (ch2.length < ch1.length ? ch2.length :
		// ch1.length); i++) {
		// int diff = Character.toUpperCase(ch2[i]) -
		// Character.toUpperCase(ch1[i]);
		// if (diff != 0) return diff;
		// }
		// if (ch1.length < ch2.length) return 1;
		// return 0;
		// }

		private int compareStart(String str1, String str2) {
			char[] ch1 = str1.toCharArray();
			char[] ch2 = str2.toCharArray();
			for (int i = 0; i < (ch2.length < ch1.length ? ch2.length
					: ch1.length); i++) {
				int diff = ch2[i] - ch1[i];
				if (diff != 0)
					return diff;
			}
			if (ch1.length < ch2.length)
				return 1;
			return 0;
		}
	}
}
