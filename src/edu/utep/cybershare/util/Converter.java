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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;

/**
 * 
 * Updates WDOs and SAWs that use wdo.owl version 1.0 to use version 2.0.
 * 
 * @author Leonardo Salayandia
 * 
 */
public class Converter {

	final static int CURRENT_VERSION = 2;

	/**
	 * Converts WDO and SAW files that were created with previous versions of
	 * the wdo.owl ontology to use the current version. Usage: Converter
	 * in_filename [out_filename] If out filename not provided, creates a new
	 * file name based on the name of the input filename.
	 * 
	 * @param args
	 **/
	public static void main(String[] args) {
		int version = 0;
		try {
			if (args.length == 0 || args.length > 2) {
				System.out
						.println("Usage: Converter in_filename [out_filename]");
				return;
			}
			File infile = new File(args[0]);
			File outfile = null;
			if (args.length == 1) {
				String inputfilename = infile.getName();
				String inputfilepath = infile.getPath();
				String outputfilename = inputfilepath.substring(0,
						inputfilepath.indexOf(inputfilename))
						+ "updated_"
						+ inputfilename;
				outfile = new File(outputfilename);
			} else {
				outfile = new File(args[1]);
			}

			BufferedReader input = new BufferedReader(new FileReader(infile));
			BufferedWriter output = new BufferedWriter(new FileWriter(outfile));

			try {
				StringBuilder contents = new StringBuilder();
				String line = null;
				while ((line = input.readLine()) != null) {
					// identify version of OWL document
					if (line.indexOf(WDO_Metamodel.WDO_URI_VER1) >= 0)
						version = 1;
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
				if (version == 0) {
					System.out
							.println("Could not recognize version of infile.");
					return;
				} else if (version == CURRENT_VERSION) {
					System.out
							.println("Infile is already using the current version of wdo.owl.");
					return;
				}
				String current_content = null;
				if (version == 1) {
					System.out.println("Converting WDO/SAW ver1 to ver2.");
					String content_ver2 = convert1to2(contents.toString());
					current_content = content_ver2;
				}
				output.write(current_content);
			} finally {
				input.close();
				output.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Convert the OWL content that was produced with version 1 of wdo.owl to
	 * OWL content produced with version 2
	 * 
	 * @param content_ver1
	 * @return
	 */
	static String convert1to2(String content) {
		String temp = content.substring(0); // make copy so that string
											// parameter is not modified
		// replace wdo:Data URIs for pmlp:Information URIs
		temp = temp.replaceAll(WDO_Metamodel.DATA_URI_VER1,
				WDO_Metamodel.DATA_URI);

		// replace wdo:Method URIs for pmlp:MethodRule URIs
		temp = temp.replaceAll(WDO_Metamodel.METHOD_URI_VER1,
				WDO_Metamodel.METHOD_URI);

		// replace wdo:hasFormat URIs for pmlp:hasFormat URIs
		temp = temp.replaceAll(WDO_Metamodel.HAS_FORMAT_URI_VER1,
				WDO_Metamodel.HAS_FORMAT_URI);

		// update WDO URI
		temp = temp.replaceAll(WDO_Metamodel.WDO_URI_VER1,
				WDO_Metamodel.WDO_URI);

		// add pmlp namespace
		// inserts pmlp namespace betweeb the first and second namespace
		int index_first_ns = temp.indexOf("xmlns:");
		int index_end_first_ns = temp.indexOf(
				System.getProperty("line.separator"), index_first_ns + 1);
		int index_second_ns = temp.indexOf("xmlns:", index_first_ns + 1);
		String substring_between_ns = temp.substring(index_end_first_ns,
				index_second_ns);

		StringBuilder tempbuilder = new StringBuilder();

		tempbuilder.append(temp.substring(0, index_second_ns));
		tempbuilder.append("xmlns:pmlp=\"" + WDO_Metamodel.PMLP_URI + "\"");
		tempbuilder.append(substring_between_ns);
		tempbuilder.append(temp.substring(index_second_ns));

		return tempbuilder.toString();
	}
}
