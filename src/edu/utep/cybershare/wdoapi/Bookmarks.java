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

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/*
 * Bookmarks Class
 * Author: agandara1
 * Category: Class
 * Description:  * class that manages the default bookmark file for Wdo 
 * the bookmark file is an RDF file so the Jena RDF api is
 * being used to access & manipulate the file
 *     
 */
public class Bookmarks {

	static public final String BOOKMARK_FILE = "bookmarks.xml";
	static public final String BOOKMARK_NAMESPACE = "http://bookmark";
	static public final String PROPERTY_HASBOOKMARK = "hasbookmark";
	static public final String URI_S = "uri_s";
	static public final String LIST_HEADER = "Project > ";
	static public final String FILE_HEADER = "file:";
	static public final String LIB_HEADER = "lib/";

	static private Model bookmarkModel = null;

	/**
	 * Constructor makes call to read the bookmark file into the bookmarkModel
	 * 
	 * @param none
	 * @return none
	 */
	/**
	 * Bookmarks Author: agandara1 Category: constructor Description: intializes
	 * the bookmarkModel by reading the bookmark file Throws: IOException
	 * 
	 */
	public Bookmarks() throws IOException {

		if (bookmarkModel == null)
			readBookmarks(BOOKMARK_FILE);
	}

	/**
	 * resetBookmarks Author: agandara1 Category: public method Description:
	 * resets the bookmark by closing the model and re-reading the bookmark file
	 * Throws: IOException
	 * 
	 */
	public void resetBookmarks() throws IOException {

		if (bookmarkModel != null)
			bookmarkModel.close();

		readBookmarks(BOOKMARK_FILE);
	}

	/**
	 * readBookmarks Author: agandara1 Category: private method Description:
	 * reads the contents of the bookmark file into the bookmarkModel Params:
	 * fileName - the name of the file to be read from Throws: IOException NOTE:
	 * this is a private method because it will ONLY read from the relative lib
	 * directory
	 * 
	 */
	private void readBookmarks(String fileName) throws IOException {

		// initialize the file
		// create an empty model
		bookmarkModel = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		String longFileName = FILE_HEADER + LIB_HEADER + fileName;
		InputStream inStream = FileManager.get().open(longFileName);

		if (inStream != null) {
			// read the rdf file into the bookmarkModel
			bookmarkModel.read(inStream, "");
			inStream.close();
		} else {
			// if not found, look for it in the jar (Webstart)
			ClassLoader cl = this.getClass().getClassLoader();
			inStream = cl.getResourceAsStream(BOOKMARK_FILE);
			if (inStream != null) {
				// read the rdf file into the bookmarkModel
				bookmarkModel.read(inStream, null);
				inStream.close();
			}
		}
	}

	/**
	 * writeBookmarks Author: agandara1 Category: private method Description:
	 * writes the current list of bookmarks in the bookmarkmodel to the
	 * bookmarks file Params: fileName - the file to write too, by default these
	 * are found in the lib directory Throws: IOException, FileNotFoundException
	 * NOTE: this is a private method because it will only write to the relative
	 * lib directory
	 * 
	 */
	private void writeBookmarks(String fileName) throws FileNotFoundException,
			IOException {

		if (bookmarkModel == null)
			return;

		// open the export file and
		String outFileName = LIB_HEADER + fileName;
		OutputStream outStream = new FileOutputStream(outFileName);
		if (outStream != null) {
			// save the file down
			bookmarkModel.write(outStream);
			outStream.close();
		}
	}

	/**
	 * getBookmarkLists Author: agandara1 Category: public method Description:
	 * gets the list of bookmark lists in the bookmark file Return: List of
	 * bookmark lists NOTE: these are used to support groups of bookmarks for -
	 * say for a project
	 * 
	 */
	public ArrayList<String> getBookmarkLists() {
		ArrayList<String> bookmarklists = null;

		if (bookmarkModel == null)
			return bookmarklists;

		Property hasbookmark = bookmarkModel.getProperty(BOOKMARK_NAMESPACE
				+ "#" + PROPERTY_HASBOOKMARK);

		// assures the model has nodes with hasbookmark properties
		if (hasbookmark != null) {
			// get the list of objects that have hasbookmark property - these
			// are lists
			ResIterator iter = bookmarkModel
					.listSubjectsWithProperty(hasbookmark);
			if (iter.hasNext()) {
				bookmarklists = new ArrayList<String>();
				// traverse the objects (lists)
				while (iter.hasNext()) {
					Resource node = iter.nextResource();
					String bml_s = node.toString();
					// URI_S objects are considered top-level and not a list
					// so this is skipped
					// otherwise, this object is added to the list
					if (!bml_s.equals(BOOKMARK_NAMESPACE + "#" + URI_S))
						bookmarklists.add(LIST_HEADER + bml_s);
				}
			}
		}

		return bookmarklists;
	}

	/**
	 * getBookmarks Author: agandara1 Category: public method Description: gets
	 * the bookmarks for a given list Params: bookmarkList - the property or
	 * list name to search for Returns: List of bookmarks for the the list name
	 * requested
	 * 
	 */
	public ArrayList<String> getBookmarks(String bookmarkList) {

		// get the bookmark model read in
		ArrayList<String> bookmarks = null;
		if (bookmarkModel == null)
			return bookmarks;

		Property hasbookmark = bookmarkModel.getProperty(BOOKMARK_NAMESPACE
				+ "#" + PROPERTY_HASBOOKMARK);
		boolean done = false;

		// checks that there are bookmark lists in the file
		if (hasbookmark != null) {
			// get all bookmark lists
			ResIterator iter = bookmarkModel
					.listSubjectsWithProperty(hasbookmark);
			if (iter.hasNext()) {
				while (iter.hasNext() && !done) {
					Resource node = iter.nextResource();
					// see if it the list requested
					if (node.toString().equals(bookmarkList)) {
						// if so, get the bookmarks for that list
						bookmarks = new ArrayList<String>();
						StmtIterator siter = node.listProperties(hasbookmark);
						while (siter.hasNext()) {
							Statement st = siter.nextStatement();
							RDFNode onode = st.getObject();
							String url = onode.toString();
							// add this bookmark to the return list
							bookmarks.add(url);
						}
						// stop once top level searched for list is found
						done = true;
					}
				}
			}
		}

		return bookmarks;
	}

	/**
	 * addBookmark Author: agandara1 Category: public method Description: add a
	 * bookmark to the bookmark file Params: currentWorkspace - the object that
	 * contains the list of open ontologies, wdo and workflows Throws:
	 * IOException, FileNotFoundException
	 * 
	 */
	public void addBookmark(String bookmarkURL) throws FileNotFoundException,
			IOException {

		// initialize the bookmarkModel
		if (bookmarkModel == null)
			readBookmarks(BOOKMARK_FILE);

		Property hasbookmark = bookmarkModel.getProperty(BOOKMARK_NAMESPACE
				+ "#" + PROPERTY_HASBOOKMARK);
		boolean done = false;

		// checks that there are bookmark lists in the file
		if (hasbookmark != null) {
			// get all bookmark lists
			ResIterator iter = bookmarkModel
					.listSubjectsWithProperty(hasbookmark);
			if (iter.hasNext()) {
				while (iter.hasNext() && !done) {
					Resource node = iter.nextResource();
					// find the list of top-level bookmarks (URI_S)
					if (node.toString()
							.equals(BOOKMARK_NAMESPACE + "#" + URI_S)) {
						// add bookmark as a property of the top-level URI node
						node.addProperty(hasbookmark, bookmarkURL);
						// stop once top level uri's are found and bookmark is
						// added
						done = true;
					}
				}
			}
			// if done == false then this failed
		}
		// save down the model to the file
		writeBookmarks(BOOKMARK_FILE);
		return;
	}

	/**
	 * getNonListBookmarks Author: agandara1 Category: public method
	 * Description: Searches obtains a list of bookmark strings from the
	 * bookmark file, that are NOT in a list. Top-level uri's Return: String of
	 * bookmarks
	 * 
	 */
	public ArrayList<String> getNonListBookmarks() {
		ArrayList<String> bookmarks = null;

		bookmarks = getBookmarks(BOOKMARK_NAMESPACE + "#" + URI_S);

		return bookmarks;
	}

}
