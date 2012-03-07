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
package edu.utep.cybershare.wdoapi;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

/**
 * WorkspaceFile Class Author: agandara1 Category: Utilitiy Class Description:
 * Class that reads and writes the contents of a workspace file NOTES: all
 * methods are static and files/models are read/written and closed in the same
 * call
 * 
 */
public class WorkspaceFile {

	static public final String WORKSPACE_NAMESPACE = "http://wdoworkspace#";
	static public final String PROPERTY_HASURI = "hasuri";

	/**
	 * exportWorkspace Author: agandara1 Category: static method Description:
	 * writes the files opened in a workspace to a workspace file, rdf format
	 * Params: currentWorkspace - the object that contains the list of open
	 * ontologies, wdo and workflows
	 * 
	 */
	static public void exportWorkspace(Workspace currentWorkspace,
			String exportFile) throws IOException {
		if (exportFile != null && !exportFile.isEmpty()) {

			// create the workspace object
			Model model = ModelFactory.createDefaultModel();
			model.setNsPrefix("ws", WORKSPACE_NAMESPACE);

			// the WORKSPACE workspace node in the rdf file is created in the
			// model
			Resource node = model.createResource(WORKSPACE_NAMESPACE
					+ "workspace1");
			Property prop = model.createProperty(WORKSPACE_NAMESPACE
					+ PROPERTY_HASURI);
			// traverse the list of ontologies loaded and
			// add the hasuri property for each opened file to the WORKSPACE
			// node

			for (Iterator<String> i = currentWorkspace.listOntologyURIs(); i
					.hasNext();) {
				String ontString = i.next();
				Boolean flag = false;
				if ((ontString.equals(WDO_Metamodel.DS_URI))
						|| (ontString.equals(WDO_Metamodel.WDO_URI))
						|| (ontString.equals(WDO_Metamodel.PMLP_URI)))
					flag = true;
				OntModel temp = currentWorkspace.getOntModel(ontString);
				if ((temp != null) && (!flag)) {
					String url = currentWorkspace.getOntModelURL(temp);
					node.addProperty(prop, url);
				}
			}

			for (Iterator<String> i = currentWorkspace.listWorkflowURIs(); i
					.hasNext();) {
				OntModel temp = currentWorkspace.getOntModel(i.next());
				if (temp != null) {

					String url = currentWorkspace.getOntModelURL(temp);
					node.addProperty(prop, url);
				}
			}
			// open the export file and
			// save the file down
			FileOutputStream fout = new FileOutputStream(exportFile);
			model.write(fout);
			model.close();
			fout.close();
		}
	}

	/**
	 * readWorkspaceFile Author: agandara1 Category: static method Description:
	 * reads the files listed in a workspace file and returns them Params:
	 * fileName - the path to the file to read caller - the calling object,
	 * needed for webstart loading Return: a list of url's that were read from
	 * the workspace file
	 * 
	 */
	static public ArrayList<String> readWorkspaceFile(String filename,
			Object caller) throws IOException {
		ArrayList<String> workspaceURLs = null;

		// initialize the file
		// create an empty model
		Model workspaceModel = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream inStream = FileManager.get().open("file:" + filename);
		if (inStream != null) {
			workspaceModel.read(inStream, "");
		} else {
			// if not found look in the jar
			// get the file from a webstart install
			ClassLoader cl = caller.getClass().getClassLoader();
			inStream = cl.getResourceAsStream(filename);
			if (inStream != null) {
				workspaceModel.read(inStream, null);
				inStream.close();
			}
		}

		if (workspaceModel == null)
			return workspaceURLs;

		Property hasuri = workspaceModel.getProperty(WORKSPACE_NAMESPACE
				+ PROPERTY_HASURI);

		// checks that there are uris in the file
		if (hasuri != null) {
			// get all uris
			ResIterator iter = workspaceModel.listSubjectsWithProperty(hasuri);
			if (iter.hasNext()) {
				// get the first one - should only have one
				Resource node = iter.nextResource();
				// create the list
				workspaceURLs = new ArrayList<String>();
				StmtIterator siter = node.listProperties(hasuri);
				while (siter.hasNext()) {
					Statement st = siter.nextStatement();
					RDFNode onode = st.getObject();
					String url = onode.toString();
					// add the url to a list
					workspaceURLs.add(url);
				}
			}
		}

		workspaceModel.close();

		return workspaceURLs;
	}
}
