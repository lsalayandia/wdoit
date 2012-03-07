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

import edu.utep.cybershare.wdoapi.util.Name;

/**
 * Textfield that takes a name as input. The name can be used for a WDO class or
 * a SAW instance. The textfield checks that the name is valid, prevents loss of
 * focus if not and changes font color to red.
 * 
 * @author Leonardo Salayandia
 */
public class NameTextField extends JTextField {
	private static final long serialVersionUID = 1L;

	public NameTextField() {
		this.setInputVerifier(new NameVerifier());
		this.addKeyListener(new NameKeyListener(this.getForeground()));
	}

	public NameTextField(String text) {
		super(text);
		this.setInputVerifier(new NameVerifier());
		this.addKeyListener(new NameKeyListener(this.getForeground()));
	}

	public NameTextField(int columns) {
		super(columns);
		this.setInputVerifier(new NameVerifier());
		this.addKeyListener(new NameKeyListener(this.getForeground()));
	}

	public NameTextField(String text, int columns) {
		super(text, columns);
		this.setInputVerifier(new NameVerifier());
		this.addKeyListener(new NameKeyListener(this.getForeground()));
	}

	public NameTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		this.setInputVerifier(new NameVerifier());
		this.addKeyListener(new NameKeyListener(this.getForeground()));
	}

	private class NameVerifier extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			JTextField textField = (JTextField) input;
			String namespace = textField.getText();
			if (!Name.isValid(namespace)) {
				textField.setForeground(Color.RED);
				return false;
			}
			return true;
		}
	}

	private class NameKeyListener implements KeyListener {
		private Color defaultForeground = Color.BLACK;

		NameKeyListener(Color defaultForeground) {
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
