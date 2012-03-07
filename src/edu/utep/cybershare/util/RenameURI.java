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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;

import edu.utep.cybershare.wdoapi.Workspace;

/**
 * 
 * Renames a specified URI and updates related WDO and SAW files. Since there
 * are URI dependencies between WDO and SAW files, updating the URI in one may
 * affect other related files. It is assumed that all related files are included
 * in the specified directory.
 * 
 * @author Leonardo Salayandia
 * 
 */
public class RenameURI {
	static final String OUTPUT_DIRNAME = "updatedfiles";

	/**
	 * Renames a specified URI and updates related WDO and SAW files. Usage:
	 * RenameURI <<old_uri>> <<new_uri>> <<dir_name>> dir_name is the full path
	 * of the directory where the WDO and SAW documents are located. old_uri
	 * refers to the URI to be updated. new_uri refers to the new value of the
	 * URI to be updated. It is assumed that the directory specified contains a
	 * set of related WDO and SAW documents. Updated files will be written to a
	 * predetermined sub directory within the specified dir_name. The
	 * differentiating part of each URI, i.e., the ending part of each URI,
	 * determines the name of each updated file.
	 * 
	 * @param args
	 **/
	public static void main(String[] args) {

		try {
			if (args.length == 0 || args.length > 3) {
				System.out
						.println("Renames a specified URI and updates related WDO and SAW files.");
				System.out
						.println("Usage: <<old_uri>> <<new_uri>> <<dir_name>>");
				return;
			}
			// get old URI
			String old_uri = args[0];
			if (old_uri.endsWith(UpdateBaseURI.URI_SEPARATOR)) {
				old_uri = old_uri.substring(0, old_uri.length() - 1);
			}
			// get new URI
			String new_uri = args[1];
			if (new_uri.endsWith(UpdateBaseURI.URI_SEPARATOR)) {
				new_uri = new_uri.substring(0, new_uri.length() - 1);
			}
			// get directory name
			File dir = new File(args[2]);
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

			// get URI and content from each file found
			String[] files_uri = new String[files.length];
			String[] files_content = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				String[] temp = UpdateBaseURI.getUriAndContent(files[i]);
				files_uri[i] = temp[0];
				files_content[i] = temp[1];
			}

			// replace URIs on set of files
			for (int i = 0; i < files.length; i++) {
				files_content[i] = (files_uri[i] != null) ? UpdateBaseURI
						.replaceURIs(files_content[i], old_uri, new_uri) : null;
				if (old_uri.equalsIgnoreCase(files_uri[i])) {
					files_uri[i] = new_uri;
				}
			}

			// create output directory
			File out_dir = new File(dir.getPath() + File.separator
					+ OUTPUT_DIRNAME);
			out_dir.mkdir();

			// write updated content to new set of files in output directory
			for (int i = 0; i < files.length; i++) {
				if (files_uri[i] != null) {
					File new_file = new File(out_dir.getPath() + File.separator
							+ Workspace.shortURI(files_uri[i]));
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
}
