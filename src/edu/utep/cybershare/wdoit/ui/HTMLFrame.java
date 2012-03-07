/* 
Copyright (c) 2005,
The Board of Trustees of the  Leland Stanford Junior University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

Neither the name of Stanford University nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, ANY IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.utep.cybershare.wdoit.ui;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

public class HTMLFrame extends JFrame implements HyperlinkListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JEditorPane _html;

	public HTMLFrame(String _site, String _title, boolean isURL) {
		this.setTitle(_title);
		this.setSize(700, 600);

		try {
			if (isURL) {
				URL url = new URL(_site);
				_html = new JEditorPane(url);
			} else {
				_html = new JEditorPane("text/html", _site);
			}
			_html.setEditable(false);
			_html.addHyperlinkListener(this);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.getViewport().add(_html);

			Container contentPane = this.getContentPane();
			contentPane.add(scrollPane);
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + e);
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			Cursor _c = _html.getCursor();
			Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
			JEditorPane _pane = (JEditorPane) event.getSource();
			if (event instanceof HTMLFrameHyperlinkEvent) {
				HTMLFrameHyperlinkEvent _evt = (HTMLFrameHyperlinkEvent) event;
				HTMLDocument _doc = (HTMLDocument) _pane.getDocument();
				_doc.processHTMLFrameHyperlinkEvent(_evt);
			} else {
				try {
					_pane.setPage(event.getURL());
				} catch (Throwable _t) {
					_t.printStackTrace();
				}
			}
			_html.setCursor(_c);
		}
	}
}
