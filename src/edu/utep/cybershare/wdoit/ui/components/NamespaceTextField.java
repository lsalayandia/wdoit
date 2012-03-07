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

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.Document;

import edu.utep.cybershare.wdoapi.util.Namespace;
import edu.utep.cybershare.wdoapi.util.Namespace.NS_FORMAT;

/**
 * Textfield that takes a namespace as input. The namespace can be used for a
 * WDO or SAW document. The textfield checks that the namespace is valid,
 * prevents loss of focus if not and changes font color to red. The textfield
 * can be initialized to expect a full formatted namespace (i.e., of the form
 * protocol://body), or just the body portion of the namespace.
 * 
 * @author Leonardo Salayandia
 */
public class NamespaceTextField extends JTextField {
	private static final long serialVersionUID = 1L;

	/**
	 * Specify the format in which to expect the namespace
	 * 
	 * @param format
	 */
	public NamespaceTextField(NS_FORMAT format) {
		this.setInputVerifier(new NamespaceVerifier(format));
		this.addKeyListener(new NamespaceKeyListener(this.getForeground()));
	}

	public NamespaceTextField() {
		this.setInputVerifier(new NamespaceVerifier(NS_FORMAT.FULL));
		this.addKeyListener(new NamespaceKeyListener(this.getForeground()));
	}

	public NamespaceTextField(String text) {
		super(text);
		this.setInputVerifier(new NamespaceVerifier(NS_FORMAT.FULL));
		this.addKeyListener(new NamespaceKeyListener(this.getForeground()));
	}

	public NamespaceTextField(int columns) {
		super(columns);
		this.setInputVerifier(new NamespaceVerifier(NS_FORMAT.FULL));
		this.addKeyListener(new NamespaceKeyListener(this.getForeground()));
	}

	public NamespaceTextField(String text, int columns) {
		super(text, columns);
		this.setInputVerifier(new NamespaceVerifier(NS_FORMAT.FULL));
		this.addKeyListener(new NamespaceKeyListener(this.getForeground()));
	}

	public NamespaceTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		this.setInputVerifier(new NamespaceVerifier(NS_FORMAT.FULL));
		this.addKeyListener(new NamespaceKeyListener(this.getForeground()));
	}

	private class NamespaceVerifier extends InputVerifier {
		private NS_FORMAT nsformat;

		NamespaceVerifier(NS_FORMAT format) {
			this.nsformat = format;
		}

		@Override
		public boolean verify(JComponent input) {
			JTextField textField = (JTextField) input;
			String namespace = textField.getText();

			// as a secondary option to accommodate copy/paste operations that
			// include the protocol, check the namespace assuming FULL format
			if (!Namespace.isValid(namespace, nsformat)
					&& !Namespace.isValid(namespace, NS_FORMAT.FULL)) {
				textField.setForeground(Color.RED);
				return false;
			}
			return true;
		}
	}

	private class NamespaceKeyListener implements KeyListener {
		private Color defaultForeground = Color.BLACK;

		NamespaceKeyListener(Color defaultForeground) {
			this.defaultForeground = defaultForeground;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			e.getComponent().setForeground(this.defaultForeground);
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
}
