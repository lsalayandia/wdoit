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
package edu.utep.cybershare.wdoit.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;

import dataAnnotation.configuration.Configuration;
import dataAnnotation.configuration.InputData;
import dataAnnotation.configuration.OutputData;
import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoapi.util.Namespace;

/**
 * @author Leonardo Salayandia
 * 
 */
public class ProvenanceAnnotatorGenerator {

	HashMap<OntClass, DataProperties> dataBindings;
	HashMap<OntClass, MethodProperties> methodBindings;
	HashMap<OntClass, MethodProperties> sourceBindings;

	public ProvenanceAnnotatorGenerator() {
		dataBindings = new HashMap<OntClass, DataProperties>();
		methodBindings = new HashMap<OntClass, MethodProperties>();
		sourceBindings = new HashMap<OntClass, MethodProperties>();
	}

	public void addData(OntClass dataInd) {
		dataBindings.put(dataInd, new DataProperties());
	}

	public void removeData(OntClass dataInd) {
		dataBindings.remove(dataInd);
	}

	public DataProperties getDataProperties(OntClass dataInd) {
		return dataBindings.get(dataInd);
	}

	public void addMethod(OntClass methodInd) {
		methodBindings.put(methodInd, new MethodProperties());
	}

	public void removeMethod(OntClass methodInd) {
		methodBindings.remove(methodInd);
	}

	public MethodProperties getMethodProperties(OntClass methodInd) {
		return methodBindings.get(methodInd);
	}

	public void addSource(OntClass sourceInd) {
		sourceBindings.put(sourceInd, new MethodProperties());
	}

	public void removeSource(OntClass sourceInd) {
		sourceBindings.remove(sourceInd);
	}

	public MethodProperties getSourceProperties(OntClass sourceInd) {
		return sourceBindings.get(sourceInd);
	}

	/**
	 * Check that all required properties have been set to proceed with
	 * generation process
	 * 
	 * @return
	 */
	public boolean isRequiredPropertiesSet() {

		for (Iterator<DataProperties> i = dataBindings.values().iterator(); i
				.hasNext();) {
			if (!i.next().isRequiredPropertiesSet()) {
				return false;
			}
		}
		for (Iterator<MethodProperties> i = methodBindings.values().iterator(); i
				.hasNext();) {
			if (!i.next().isRequiredPropertiesSet()) {
				return false;
			}
		}
		for (Iterator<MethodProperties> i = sourceBindings.values().iterator(); i
				.hasNext();) {
			if (!i.next().isRequiredPropertiesSet()) {
				return false;
			}
		}
		return true;
	}

	private void setConfiguration(String scriptPath, String pmlOutputPath,
			String credentials, boolean targetSystem) {

		// process sources
		for (Iterator<OntClass> i = this.sourceBindings.keySet().iterator(); i
				.hasNext();) {
			boolean passByValue = false;
			OntClass sourceInd = i.next();
			Configuration config = new Configuration();
			config.setMethodName(SAW.getSAWInstanceQName(sourceInd)
					.substring(0));
			config.setRule("http://inference-web.org/registry/DPR/Told.owl#Told");
			config.setSource(sourceInd.getURI());
			MethodProperties sourceProperties = getSourceProperties(sourceInd);
			if (sourceProperties != null) {
				String engineURI = sourceProperties.getEngineURI();
				if (engineURI != null && !engineURI.isEmpty()) {
					config.setEngine(engineURI);
				} else {
					// TODO throw an exception or show error/warning message
				}
			}
			config.setPMLOutputDir(pmlOutputPath);

			if (credentials != null && !credentials.isEmpty()) {
				config.setCIServerCredentials(credentials);
			}

			// set output data for each source
			OntClass[] outputInds = SAW.listHasOutput(sourceInd);
			if (outputInds != null) {
				for (int j = 0; j < outputInds.length; j++) {
					OutputData outData = new OutputData();
					outData.setSaveAsURL(true);
					outData.setName(SAW.getSAWInstanceQName(outputInds[j])
							.substring(0));
					outData.setInformationInstance(outputInds[j].getURI());
					DataProperties dataProperties = this
							.getDataProperties(outputInds[j]);
					if (dataProperties != null) {
						if (!passByValue)
							passByValue = dataProperties.isEmbeddedData();
						outData.setIsParameter(dataProperties.isEmbeddedData());
						outData.setOutput(dataProperties
								.getExternalDataFilename());
						// the format specified on the DA wizard has preference
						String formatURI = dataProperties.getFormatURI();
						if (formatURI != null && !formatURI.isEmpty()) {
							outData.setFormat(formatURI);
						}
						// if no format specified in the wizard GUI, try the one
						// stored on the SAW
						else {
							Individual formatInd = SAW.getFormat(outputInds[j]);
							if (formatInd != null && !formatInd.isAnon()) {
								outData.setFormat(formatInd.getURI());
							} else {
								// TODO throw exception or show warning/error
								// message
							}
						}
					}
					config.addOutputData(outData);
				}
			}
			String temp = SAW.getSAWInstanceQName(sourceInd);
			String configPath = config.saveConfigFile(scriptPath);
			config.saveScriptWebStart(scriptPath, configPath, targetSystem,
					passByValue);
		}

		// process methods
		for (Iterator<OntClass> i = this.methodBindings.keySet().iterator(); i
				.hasNext();) {
			OntClass methodInd = i.next();
			Boolean passByValue = false;

			// only consider methods that have at least one output
			OntClass[] outputInds = SAW.listHasOutput(methodInd);

			if (outputInds != null && outputInds.length > 0) {
				Configuration config = new Configuration();
				config.setMethodName(SAW.getSAWInstanceQName(methodInd)
						.substring(0));
				if (!methodInd.isAnon()) {
					config.setRule(methodInd.getURI());
				} else {
					// old SAWs used to have anonymous instances of methods,
					// which cannot be used here
					String errorMsg = "The SAW you are trying to create Data Annotators for is an older version no loger supported"
							+ '\n'
							+ "We suggest you update your WDO-It! application in order to have full support."
							+ '\n' + '\n' + "This wizard will now close.";
					JOptionPane.showMessageDialog(new JFrame(), errorMsg,
							"WDO-It! Warning", JOptionPane.WARNING_MESSAGE);
					System.exit(0);
				}
				MethodProperties methodProperties = getMethodProperties(methodInd);
				if (methodProperties != null) {
					String engineURI = methodProperties.getEngineURI();
					if (engineURI != null && !engineURI.isEmpty()) {
						config.setEngine(engineURI);
					} else {
						// TODO throw an exception or show error/warning message
					}
				}
				config.setPMLOutputDir(pmlOutputPath);

				if (credentials != null) {
					config.setCIServerCredentials(credentials);
				}

				// set output data for each method
				for (int j = 0; j < outputInds.length; j++) {
					OutputData outData = new OutputData();
					outData.setSaveAsURL(true);
					outData.setName(SAW.getSAWInstanceQName(outputInds[j])
							.substring(0));
					outData.setInformationInstance(outputInds[j].getURI());
					DataProperties dataProperties = this
							.getDataProperties(outputInds[j]);
					if (dataProperties != null) {
						outData.setIsParameter(dataProperties.isEmbeddedData());

						if (!passByValue)
							passByValue = dataProperties.isEmbeddedData();

						outData.setOutput(dataProperties
								.getExternalDataFilename());
						// the format specified on the DA wizard has preference
						String formatURI = dataProperties.getFormatURI();
						if (formatURI != null && !formatURI.isEmpty()) {
							outData.setFormat(formatURI);
						}
						// if no format specified in the wizard GUI, try the one
						// stored on the SAW
						else {
							Individual formatInd = SAW.getFormat(outputInds[j]);
							if (formatInd != null && !formatInd.isAnon()) {
								outData.setFormat(formatInd.getURI());
							} else {
								// TODO throw exception or show warning/error
								// message
							}
						}
						config.addOutputData(outData);
					}
				}

				// set input data for each method
				OntClass[] inputInds = SAW.listHasInput(methodInd);
				if (inputInds != null) {
					for (int j = 0; j < inputInds.length; j++) {
						InputData inData = new InputData();
						inData.setName(SAW.getSAWInstanceQName(inputInds[j])
								.substring(0));
						inData.setURI(inputInds[j].getURI());
						config.addInputData(inData);
					}
				}

				String temp = SAW.getSAWInstanceQName(methodInd);
				String configPath = config.saveConfigFile(scriptPath);
				config.saveScriptWebStart(scriptPath, configPath, targetSystem,
						passByValue);
			}
		}
	}

	private static String formatLocalPathAsURL(String path) {
		final char URL_FILE_SEPARATOR = '/';
		String ans = path.substring(0);
		ans = ans.replace('\\', URL_FILE_SEPARATOR);
		ans = Namespace.NS_PROTOCOLS.file.toString()
				+ Namespace.PROTOCOL_BODY_SEPARATOR.toString() + ans;
		// end URL with /
		if (ans.charAt(ans.length() - 1) != URL_FILE_SEPARATOR) {
			ans = ans + URL_FILE_SEPARATOR;
		}
		return ans;
	}

	private static String formatAsUnixPath(String path) {
		final char UNIX_FILE_SEPARATOR = '/';
		String ans = path.replace('\\', UNIX_FILE_SEPARATOR);
		// end path with /
		if (ans.charAt(ans.length() - 1) != UNIX_FILE_SEPARATOR) {
			ans = ans + UNIX_FILE_SEPARATOR;
		}
		return ans;
	}

	private static String formatAsWinPath(String path) {
		final char WIN_FILE_SEPARATOR = '\\';
		String ans = path.replace('/', WIN_FILE_SEPARATOR);
		// end path with \
		if (ans.charAt(ans.length() - 1) != WIN_FILE_SEPARATOR) {
			ans = ans + WIN_FILE_SEPARATOR;
		}
		return ans;
	}

	private static void generateEnvironmentScriptUNIX(String scriptsPath,
			String pmlOutputPath, String dataOutputPath) {
		final char UNIX_PATH_SEPARATOR = ':';
		final String ENV_SCRIPT_NAME = "environmentVariables.sh";

		String filename = (scriptsPath.endsWith(File.separator)) ? scriptsPath
				+ ENV_SCRIPT_NAME : scriptsPath + File.separator
				+ ENV_SCRIPT_NAME;
		File scriptFile = new File(filename);

		String scriptContent;
		scriptContent = "#!/bin/sh\n";
		scriptContent += "# Auto-generated script\n\n";

		if (pmlOutputPath.startsWith("http")) {
			scriptContent += "export PML_BASEPATH=" + pmlOutputPath + "\n";
			scriptContent += "export PML_BASEURL=" + pmlOutputPath + "\n";
		} else {
			scriptContent += "export PML_BASEPATH=\""
					+ formatAsUnixPath(pmlOutputPath) + "\"\n";
			scriptContent += "export PML_BASEURL=\""
					+ formatLocalPathAsURL(pmlOutputPath) + "\"\n";
		}
		scriptContent += "export DATA_BASEPATH=\""
				+ formatAsUnixPath(dataOutputPath) + "\"\n";
		scriptContent += "export DATA_BASEURL=\""
				+ formatLocalPathAsURL(dataOutputPath) + "\"\n";
		scriptContent += "export MAPPINGS_PATH=\""
				+ formatAsUnixPath(scriptsPath + "/mappings/mappings.xml")
				+ "\"\n";
		scriptContent += "export CONFIG_SCRIPTS=\""
				+ formatAsUnixPath(scriptsPath) + "\"\n";
		scriptContent += "export PATH=$PATH" + UNIX_PATH_SEPARATOR
				+ "$CONFIG_SCRIPTS\n";

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(scriptFile));
			out.write(scriptContent);
			out.close();

		} catch (Exception e) {
			e.toString();
		}
	}

	private static void generateEnvironmentScriptWindows(String scriptsPath,
			String pmlOutputPath, String dataOutputPath) {
		final char WINDOWS_PATH_SEPARATOR = ';';
		final String ENV_SCRIPT_NAME = "environmentVariables.bat";

		String filename = (scriptsPath.endsWith(File.separator)) ? scriptsPath
				+ ENV_SCRIPT_NAME : scriptsPath + File.separator
				+ ENV_SCRIPT_NAME;
		File scriptFile = new File(filename);

		String scriptContent;
		scriptContent = ":: Auto-generated script\n\n";
		if (pmlOutputPath.startsWith("http")) {
			scriptContent += "set PML_BASEPATH=" + pmlOutputPath + "\n";
			scriptContent += "set PML_BASEURL=" + pmlOutputPath + "\n";
		} else {
			scriptContent += "set PML_BASEPATH="
					+ formatAsWinPath(pmlOutputPath) + "\n";
			String temp = "set PML_BASEURL="
					+ formatLocalPathAsURL(pmlOutputPath) + "\n";
			temp = temp.substring(0, 22) + temp.substring(23);
			scriptContent += temp;
		}
		scriptContent += "set DATA_BASEPATH=" + formatAsWinPath(dataOutputPath)
				+ "\n";
		String temp2 = "set DATA_BASEURL="
				+ formatLocalPathAsURL(dataOutputPath) + "\n";
		temp2 = temp2.substring(0, 22) + temp2.substring(23);
		scriptContent += temp2;
		scriptContent += "set MAPPINGS_PATH=" + formatAsWinPath(scriptsPath)
				+ "mappings/mappings.xml\n";
		scriptContent += "set CONFIG_SCRIPTS=" + formatAsWinPath(scriptsPath)
				+ "\n";
		scriptContent += "set PATH=%PATH%" + WINDOWS_PATH_SEPARATOR
				+ "%CONFIG_SCRIPTS%\n";

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(scriptFile));
			out.write(scriptContent);
			out.close();

		} catch (Exception e) {
			e.toString();
		}
	}

	/**
	 * Generate provenance data annotators as shell scripts to run on a
	 * Linux/UNIX/Mac platform
	 * 
	 * @param pmlOutputPath
	 * @param credentials
	 */
	public void generateShellScriptAnnotators(String scriptsPath,
			String pmlOutputPath, String dataOutputPath, String credentials) {
		File scriptsDir = new File(scriptsPath);
		scriptsDir.mkdirs();

		File pmlOutputDir = new File(pmlOutputPath);
		pmlOutputDir.mkdirs();

		File dataOutputDir = new File(dataOutputPath);
		dataOutputDir.mkdirs();

		setConfiguration(scriptsPath, pmlOutputPath, credentials, false);
		generateEnvironmentScriptUNIX(scriptsPath, pmlOutputPath,
				dataOutputPath);
	}

	/**
	 * Generate provenance data annotators as batch scripts to run on a Windows
	 * platform
	 * 
	 * @param pmlOutputPath
	 * @param credentials
	 */
	public void generateBatchScriptAnnotators(String scriptsPath,
			String pmlOutputPath, String dataOutputPath, String credentials) {
		setConfiguration(scriptsPath, pmlOutputPath, credentials, true);
		generateEnvironmentScriptWindows(scriptsPath, pmlOutputPath,
				dataOutputPath);
	}

	/**
	 * Generate provenance annotators as Java classes to include in a Java
	 * application
	 * 
	 * @param dirPath
	 */
	public void generateJavaAnnotators(String scriptOutputPath,
			String pmlOutputPath, String credentials) {

	}

	/**
	 * Generate provenance annotators as MoML actors to include in a Kepler
	 * workflow
	 * 
	 * @param dirPath
	 */
	public void generateMoMLAnnotators(String scriptOutputPath,
			String pmlOutputPath, String credentials) {

	}

	/**
	 * Additional properties required for each data individual of a SAW to
	 * generate a provenance data annotator
	 * 
	 * @author Leonardo Salayandia
	 */
	public class DataProperties {
		Individual format = null;
		String formatURI = null;
		boolean embeddedData = false;
		String externalDataFilename = null;

		public Individual getFormat() {
			return format;
		}

		public void setFormat(Individual format) {
			this.format = format;
		}

		public String getFormatURI() {
			return formatURI;
		}

		public void setFormatURI(String uri) {
			this.formatURI = (uri.equalsIgnoreCase("Choose Format")) ? null
					: uri;
		}

		public boolean isEmbeddedData() {
			return embeddedData;
		}

		public void setEmbeddedData(boolean b) {
			this.embeddedData = b;
		}

		public String getExternalDataFilename() {
			return externalDataFilename;
		}

		public void setExternalDataFilename(String filename) {
			this.externalDataFilename = filename;
		}

		public boolean isRequiredPropertiesSet() {
			return (formatURI != null && !formatURI.isEmpty());
		}
	}

	/**
	 * Additional properties required for each method and source individual of a
	 * SAW to generate a provenance data annotator
	 * 
	 * @author Leonardo Salayandia
	 */
	public class MethodProperties {
		Individual engine = null;
		String engineURI = null;

		public Individual getEngine() {
			return engine;
		}

		public void setEngine(Individual engine) {
			this.engine = engine;
		}

		public String getEngineURI() {
			return engineURI;
		}

		public void setEngineURI(String engineURI) {
			this.engineURI = (engineURI.equalsIgnoreCase("Choose Engine")) ? null
					: engineURI;
		}

		public boolean isRequiredPropertiesSet() {
			return (engineURI != null && !engineURI.isEmpty());
		}
	}
}
