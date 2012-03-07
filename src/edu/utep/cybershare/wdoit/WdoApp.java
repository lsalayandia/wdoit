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

package edu.utep.cybershare.wdoit;

import javax.swing.ImageIcon;

import edu.utep.cybershare.wdoit.ui.WdoView;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class WdoApp extends SingleFrameApplication {

	@Resource
	private String initURI;

	@Override
	protected void initialize(java.lang.String[] args) {
		initURI = (args.length == 1) ? args[0] : null;
	}

	/**
	 * At startup create and show the main frame of the application.
	 */
	@Override
	protected void startup() {
		WdoView wdoView = new WdoView(this, initURI);
		// set the application's icon
		ImageIcon imageIcon = getApplication().getContext().getResourceMap()
				.getImageIcon("Application.icon");
		wdoView.getFrame().setIconImage(imageIcon.getImage());
		;
		show(wdoView);
	}

	/**
	 * This method is to initialize the specified window by injecting resources.
	 * Windows shown in our application come fully initialized from the GUI
	 * builder, so this additional configuration is not needed.
	 */
	@Override
	protected void configureWindow(java.awt.Window root) {
	}

	/**
	 * A convenient static getter for the application instance.
	 * 
	 * @return the instance of WdoApp
	 */
	public static WdoApp getApplication() {
		return Application.getInstance(WdoApp.class);
	}

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args) {
		launch(WdoApp.class, args);
	}
}
