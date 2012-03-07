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
package edu.utep.cybershare.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

import edu.utep.cybershare.wdoapi.Workspace;

/**
 * Updates the base section of the URIs of a set of related WDO and SAW
 * documents. This is useful when changing the location of where WDO and SAW
 * documents are published. Ideally, the URI of the document should match its
 * URL.
 * 
 * @author Leonardo Salayandia
 */
public class UpdateBaseURI {
	static final char[] URI_DELIMITERS = { '\"', '#' };
	static final String URI_SEPARATOR = "/";
	static final String OUTPUT_DIRNAME = "updatedfiles";

	/**
	 * Updates the base section of the URIs of a set of related WDO and SAW
	 * documents. Usage: UpdateBaseURI <<dir_name>> <<base_URI>> dir_name is the
	 * full path of the directory where the WDO and SAW documents are located.
	 * base_URI refers to the common part of the URI for all WDO and SAW
	 * documents to be updated. It is assumed that the directory specified
	 * contains a set of related WDO and SAW documents. Updated files will be
	 * written to a predetermined sub directory within the specified dir_name.
	 * The differentiating part of each URI, i.e., the ending part of each URI,
	 * determines the name of each new file.
	 * 
	 * @param args
	 **/
	public static void main(String[] args) {

		try {
			if (args.length == 0 || args.length > 2) {
				System.out
						.println("Updates the base section of the URIs of a set of related WDO and SAW documents.");
				System.out.println("Usage: <<dir_name>> <<base_URI>>");
				return;
			}
			File dir = new File(args[0]);

			// get files from the specified directory
			FileFilter filter = new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.isFile() && pathname.canRead());
				}
			};
			File[] files = dir.listFiles(filter);
			if (files == null || files.length == 0) {
				System.out
						.println("Invalid directory path or directory does not contain files.");
				return;
			}

			// get URIs and content from each file found
			String[] uris = new String[files.length];
			String[] files_content = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				String[] temp = getUriAndContent(files[i]);
				uris[i] = temp[0];
				files_content[i] = temp[1];
			}

			// construct replacement URIs from baseURI
			String new_baseURI = args[1];
			String[] new_uris = new String[files.length];
			if (new_baseURI.endsWith(URI_SEPARATOR)) {
				new_baseURI = new_baseURI
						.substring(0, new_baseURI.length() - 1);
			}
			for (int i = 0; i < files.length; i++) {
				new_uris[i] = (uris[i] != null) ? new_baseURI + URI_SEPARATOR
						+ Workspace.shortURI(uris[i]) : null;
				System.out.println("URI: " + uris[i] + " updated to new URI: "
						+ new_uris[i]);
			}

			// replace URIs on set of files
			for (int i = 0; i < files.length; i++) {
				for (int j = 0; j < files.length; j++) {
					files_content[j] = (uris[i] != null) ? replaceURIs(
							files_content[j], uris[i], new_uris[i]) : null;
				}
			}

			// create output directory
			File out_dir = new File(dir.getPath() + File.separator
					+ OUTPUT_DIRNAME);
			out_dir.mkdir();

			// write updated content to new set of files in output directory
			for (int i = 0; i < files.length; i++) {
				if (uris[i] != null) {
					File new_file = new File(out_dir.getPath() + File.separator
							+ Workspace.shortURI(new_uris[i]));
					BufferedWriter output = new BufferedWriter(new FileWriter(
							new_file));
					output.write(files_content[i]);
					output.close();
				}
			}
			System.out.println("New files saved at: " + out_dir.getPath());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Reads a text file, assuming it is an OWL document, and returns a string
	 * array of length 2. The first element contains the file's URI. The second
	 * element contains the content of the file.
	 * 
	 * @param file
	 * @return A two element string array with the first element containing the
	 *         URI or null if not found, and the second element containing the
	 *         content of the file or null if it cannot read.
	 */
	static String[] getUriAndContent(File file) {
		String[] uriAndContent = { null, null };
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
				StringBuilder file_content = new StringBuilder();
				Stack<String> stack = new Stack<String>();
				String line = null;
				Boolean found = false;
				while ((line = input.readLine()) != null) {
					// identify URI of document
					if (!found) {
						if (line.indexOf("<rdf:Description") >= 0)
							stack.push(line);
						if (line.indexOf("</rdf:Description>") >= 0)
							stack.pop();
						if (line.indexOf("http://www.w3.org/2002/07/owl#Ontology") >= 0) {
							String temp = stack.pop();
							if (temp != null) {
								int i1 = temp.indexOf("\"");
								int i2 = temp.indexOf("\"", i1 + 1);
								if (i1 >= 0 && i2 > i1) {
									uriAndContent[0] = temp.substring(i1 + 1,
											i2);
									found = true;
								}
							}
						}
					}
					file_content.append(line);
					file_content.append(System.getProperty("line.separator"));
				}
				uriAndContent[1] = file_content.toString();
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return uriAndContent;
	}

	/**
	 * Replace all the instances of a URI for a new URI on the specified content
	 * 
	 * @param content
	 * @param uri
	 * @param newuri
	 * @return
	 */
	static String replaceURIs(String content, String uri, String newuri) {
		StringBuilder temp = new StringBuilder();
		int uri_length = uri.length();
		int idx0 = 0;
		int idx1 = content.indexOf(uri);
		while (idx1 >= 0) {
			// confirm that URI is a proper match
			boolean found = false;
			int i = 0;
			while (!found && i < URI_DELIMITERS.length) {
				found = (content.charAt(idx1 + uri_length) == URI_DELIMITERS[i]);
				i++;
			}
			// if proper match found, replace uri and advance indices to next
			// match
			if (found) {
				temp.append(content.substring(idx0, idx1));
				temp.append(newuri);
				idx0 = idx1 + uri_length;
				idx1 = content.indexOf(uri, idx0);
			}
			// no proper match found, look for next possibility
			else {
				idx1 = content.indexOf(uri, idx1 + 1);
			}
		}
		// add ending part of the original content
		temp.append(content.substring(idx0));

		return temp.toString();
	}
}
