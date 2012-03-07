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

import java.io.File;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.shared.NotFoundException;

import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.ui.EditNamespace;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Implements the task of loading an OWL document. The class can be initialized
 * to start by asking the user to select a file or to enter a web-addressable
 * URI
 * 
 * @author Leonardo Salayandia
 */
public class LoadOWLDocumentTask extends Task<String, Void> {
	static public enum Attempt {
		ASK_FILENAME, ASK_URI, SEARCH
	}

	private Attempt initialAttempt;
	private String openFileDialogTitle;
	private String openURIDialogTitle;
	private FileNameExtensionFilter fileFilter;
	private Stack<String> loadStack;
	private HashMap<String, String> owlDocsToLoad;
	private HashMap<String, Attempt> loadAttempt;
	private String currentDir;

	public LoadOWLDocumentTask(Attempt initialAttempt, String uri) {
		super(WdoApp.getApplication());
		this.setUserCanCancel(false);
		this.initialAttempt = (initialAttempt == Attempt.SEARCH) ? Attempt.ASK_FILENAME
				: initialAttempt;
		ResourceMap rm = getResourceMap();
		this.setTitle(rm.getString("LoadOWLDocumentTask.title"));
		this.setDescription(rm.getString("LoadOWLDocumentTask.description"));
		openFileDialogTitle = rm
				.getString("LoadOWLDocumentTask.openFileDialogTitle");
		openURIDialogTitle = rm
				.getString("LoadOWLDocumentTask.openURIDialogTitle");
		fileFilter = new FileNameExtensionFilter(
				rm.getString("LoadOWLDocumentTask.validFileExtensionDescription"),
				rm.getString("LoadOWLDocumentTask.validFileExtensions").split(
						","));
		owlDocsToLoad = new HashMap<String, String>();
		loadAttempt = new HashMap<String, Attempt>();
		currentDir = "";
		loadStack = new Stack<String>();
		setURIToLoad(uri);
	}

	@Override
	protected String doInBackground() throws Exception {
		String loadedURI = null;
		State state = State.getInstance();

		while (!loadStack.isEmpty()) {
			String uri = loadStack.pop();
			String url = owlDocsToLoad.get(uri);
			try {
				OntModel ontmodel = state.openOWL(url, uri);
				loadedURI = state.getOntModelURI(ontmodel);
			} catch (NotFoundException ex) {
				// dependent owl file was not found in the loaded workspace
				loadStack.push(uri);
				String newURI = ex.getMessage();
				newURI = newURI.substring(0, newURI.indexOf(" "));
				setURIToLoad(newURI);
			} catch (Exception ex) {
				// searching for owl file in the current dir and web failed
				loadStack.push(uri);
				setURIToLoad(uri);
			}
		}
		return loadedURI;
	}

	@Override
	protected void succeeded(String ontmodelURI) {
		// re-load owl docs tree and set loaded one
		if (ontmodelURI == null) {
			setMessage(getResourceMap().getString("LoadOWLDocumentTask.failed"));
		} else {
			WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
			wdoView.updateSelectedOWLDoc();
			setMessage(getResourceMap().getString(
					"LoadOWLDocumentTask.succeeded", ontmodelURI));
		}
	}

	@Override
	protected void failed(Throwable e) {
		setMessage(getResourceMap().getString("LoadOWLDocumentTask.failed")
				+ ": " + e.getMessage());
	}

	@Override
	protected void cancelled() {
		setMessage(getResourceMap().getString("LoadOWLDocumentTask.failed"));
	}

	/**
	 * Set the URI to load into the workspace based on the task's attempt status
	 * for that URI. The options are: 1) Show a dialog for the user to select a
	 * file, 2) Show a dialog for the user to enter a URI, or 3) Search the
	 * current directory and web for the OWL document that correponds to a URI.
	 * If a URI is not specified, a URI is computed from the name of the chosen
	 * file.
	 * 
	 * @param uri
	 */
	private void setURIToLoad(String uri) {
		Attempt attempt;
		if (uri == null)
			attempt = initialAttempt;
		else if (!loadAttempt.containsKey(uri))
			attempt = Attempt.SEARCH;
		else
			attempt = loadAttempt.get(uri);
		// ask for file to load and set URI from its name
		if (attempt == Attempt.ASK_FILENAME) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle((uri == null) ? openFileDialogTitle
					: openFileDialogTitle + "(" + uri + ")");
			chooser.setFileFilter(fileFilter);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setMultiSelectionEnabled(false);
			if (!currentDir.isEmpty()) {
				chooser.setCurrentDirectory(new File(currentDir));
			}
			if (chooser.showOpenDialog(WdoApp.getApplication().getMainFrame()) == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				if (selectedFile != null) {
					String filename = selectedFile.getName();
					String absolutePath = selectedFile.getAbsolutePath();
					currentDir = absolutePath.substring(0,
							absolutePath.indexOf(filename));
					String url = "file:" + absolutePath;
					if (uri == null) {
						int idx = filename.lastIndexOf('.');
						if (idx > 0 && idx < filename.length() - 1) {
							uri = filename.substring(0, idx);
						}
						uri = "http://" + filename;
					}
					owlDocsToLoad.put(uri, url);
					loadAttempt.put(uri, Attempt.SEARCH);
					loadStack.push(uri);
				}
			} else {
				// load operation canceled, empty load stack
				loadStack = new Stack<String>();
			}
		}
		// ask for URI to load
		else if (attempt == Attempt.ASK_URI) {
			JFrame mainFrame = WdoApp.getApplication().getMainFrame();
			EditNamespace editNamespaceWindow = new EditNamespace(mainFrame,
					openURIDialogTitle);
			uri = editNamespaceWindow.getNamespace();
			if (uri != null && !uri.isEmpty()) {
				owlDocsToLoad.put(uri, uri);
				loadAttempt.put(uri, Attempt.ASK_URI);
				loadStack.push(uri);
			} else {
				// load operation canceled, empty load stack
				loadStack = new Stack<String>();
			}
		}
		// search in current directory or web
		else if (attempt == Attempt.SEARCH) {
			String filename = currentDir + uri.replaceFirst("http://", "");
			File file = new File(filename);
			if (!file.exists()) {
				boolean found = false;
				String[] fileExtensions = fileFilter.getExtensions();
				for (int i = 0; i < fileExtensions.length; i++) {
					file = new File(filename + "." + fileExtensions[i]);
					if (file.exists()) {
						found = true;
						break;
					}
				}
				if (found) {
					String url = "file:" + file.getAbsolutePath();
					owlDocsToLoad.put(uri, url);
					loadAttempt.put(uri, Attempt.ASK_FILENAME);
					loadStack.push(uri);
				} else {
					// could not find file in current directory, try checking
					// the web
					owlDocsToLoad.put(uri, uri);
					loadAttempt.put(uri, Attempt.ASK_FILENAME);
					loadStack.push(uri);
				}
			}
		}
	}
}
