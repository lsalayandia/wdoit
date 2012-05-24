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
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

import com.hp.hpl.jena.ontology.OntModel;

import edu.utep.cybershare.wdoapi.Workspace;
import edu.utep.cybershare.wdoapi.util.Namespace;
import edu.utep.cybershare.wdoit.WdoApp;
import edu.utep.cybershare.wdoit.context.State;
import edu.utep.cybershare.wdoit.ui.WdoView;

/**
 * Implements the task of saving OWL documents. The class is initiated with a
 * list of ontmodels to save, and a corresponding list of locations (URLs) to
 * save to. URLs can be set to null, in which case it will attempt to save to a
 * previously known location for that given ontmodel. If a previously known
 * location cannot be determined, the user will be prompted for a file to save
 * to. The ontmodel list and the url list are assumed to be of the same size.
 * 
 * @author Leonardo Salayandia
 */
public class SaveOWLDocumentTask extends Task<Void, Void> {
	private List<OntModel> documentsToSave;
	private List<String> urlsToSave;
	private boolean saveAs;
	private String saveFileDialogTitle;
	private String fileExtension;
	private String currentDir;
	private JFileChooser fileChooser;
	private int count;

	public SaveOWLDocumentTask(List<OntModel> ontmodels, List<String> urls,
			boolean saveAs) {
		super(WdoApp.getApplication());
		this.setUserCanCancel(false);
		ResourceMap rm = getResourceMap();
		this.setTitle(rm.getString("SaveOWLDocumentTask.title"));
		this.setDescription(rm.getString("SaveOWLDocumentTask.description"));
		saveFileDialogTitle = rm
				.getString("SaveOWLDocumentTask.saveFileDialogTitle");
		fileExtension = rm.getString("SaveOWLDocumentTask.fileExtension");
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(
				rm.getString("SaveOWLDocumentTask.fileExtensionDescription"),
				fileExtension.split(","));
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(fileFilter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		currentDir = "";
		documentsToSave = ontmodels;
		urlsToSave = urls;
		this.saveAs = saveAs;
		count = 0;
	}

	@Override
	protected Void doInBackground() throws Exception {
		State state = State.getInstance();
		Iterator<OntModel> docIter = documentsToSave.iterator();
		Iterator<String> urlIter = urlsToSave.iterator();
		WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
		for (; docIter.hasNext();) {

			OntModel ontmodel = docIter.next();
			String uri = state.getOntModelURI(ontmodel);

			String url = saveAs ? promptForFile(uri) : urlIter.next();
			try {
				state.saveOWLDocument(ontmodel, url);
				if (url.startsWith(Namespace.NS_PROTOCOLS.http.toString())) {
					// upload temp file created to web server
					File file = new File(System.getProperty("user.home") + File.separator + "wdoit.temp");
					wdoView.getAlfrescoClient().updateFile(url, file);					
				}
			} catch (Exception ex) {
				// ex.printStackTrace();
				url = promptForFile(uri);
				state.saveOWLDocument(ontmodel, url);
				fileChooser.setDialogTitle(saveFileDialogTitle + "(" + uri
						+ ")");
			}
			count++;
		}
		return null;
	}

	private String promptForFile(String uri) {
		String url = null;
		fileChooser.setDialogTitle(saveFileDialogTitle + "(" + uri + ")");
		if (!currentDir.isEmpty()) {
			fileChooser.setCurrentDirectory(new File(currentDir));
		}
		// set a default selected file
		String filename = Workspace.shortURI(uri);
		if (!filename.toLowerCase().endsWith("." + fileExtension)) {
			filename = filename + "." + fileExtension;
		}
		fileChooser.setSelectedFile(new File(filename));
		if (fileChooser.showSaveDialog(WdoApp.getApplication().getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile != null) {
				filename = selectedFile.getName();
				String absolutePath = selectedFile.getAbsolutePath();
				currentDir = absolutePath.substring(0,
						absolutePath.indexOf(filename));
				url = "file:" + absolutePath;
			}
		}
		return url;
	}

	@Override
	protected void succeeded(Void ignored) {
		if (count == 0) {
			setMessage(getResourceMap().getString("SaveOWLDocumentTask.failed"));
		} else {
			WdoView wdoView = (WdoView) WdoApp.getApplication().getMainView();
			wdoView.updateSelectedOWLDoc();
			setMessage(getResourceMap().getString(
					"SaveOWLDocumentTask.succeeded"));
		}
	}

	@Override
	protected void failed(Throwable e) {
		setMessage(getResourceMap().getString("SaveOWLDocumentTask.failed")
				+ " " + e.getMessage());
	}

	@Override
	protected void cancelled() {
		setMessage(getResourceMap().getString("SaveOWLDocumentTask.failed"));
	}
}
