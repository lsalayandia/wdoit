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
package edu.utep.cybershare.wdoit.task;

import java.util.EventObject;

import javax.swing.JOptionPane;

import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Application.ExitListener;

import edu.utep.cybershare.wdoit.context.State;

/**
 * Checks state to see if things have changed and asks for confirmation before
 * exiting the application
 * 
 * @author Leonardo Salayandia
 * 
 */
public class ConfirmExit implements ExitListener {

	private FrameView frameView;
	private String confirmExitTitle;
	private String confirmExitText;

	public ConfirmExit(FrameView frameView) {
		this.frameView = frameView;
		ResourceMap rm = frameView.getResourceMap();
		this.confirmExitTitle = rm.getString("confirmExitDialogTitle.text");
		this.confirmExitText = rm.getString("confirmExit.text");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdesktop.application.Application.ExitListener#canExit(java.util.
	 * EventObject)
	 */
	@Override
	public boolean canExit(EventObject e) {
		State state = State.getInstance();
		if (state.isModified()) {
			int option = JOptionPane.showConfirmDialog(frameView.getFrame(),
					confirmExitText, confirmExitTitle,
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			return option == JOptionPane.YES_OPTION;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdesktop.application.Application.ExitListener#willExit(java.util.
	 * EventObject)
	 */
	@Override
	public void willExit(EventObject e) {

	}

}
