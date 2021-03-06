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

import java.io.File;
import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;
import edu.utep.cybershare.wdoapi.util.Namespace;

//import edu.utep.cybershare.ws.RDFAggregater.RDFAggregater;
//import edu.utep.cybershare.ws.RDFAggregater.RDFAggregater_Service;

//import edu.utep.cybershare.ciclient.CIGet;
//import edu.utep.cybershare.ciclient.CIPut;
//import edu.utep.cybershare.ciclient.CIReturnObject;
//import edu.utep.cybershare.ciclient.CIUtils;
//import edu.utep.cybershare.ciclient.ciconnect.CIClient;
//import edu.utep.cybershare.ciclient.ciconnect.CIKnownServerTable;
//import edu.utep.cybershare.ciclient.ciconnect.CIServerCache;
//import edu.utep.cybershare.ciclient.ciui.CIGetProjectListDialog;
//import edu.utep.cybershare.ciclient.ciui.CIGetUP;

/**
 * @author Leonardo Salayandia
 * 
 */
public class Workspace {
	public static final String ONTOLOGIES_NODE_ID = "_ontologies";
	public static final String WORKFLOWS_NODE_ID = "_workflows";
	protected OntModelSpec ontModelSpec;

	private OntModel baseWDO;
	public OntModelHierarchy ontmodelHierarchy;
	public WDO Wdo;
	public SAW Saw;
	public Data Data;
	public Method Method;
//	private CIServerCache ciCache;

	/**
	 * Initialize the workspace
	 */
	public Workspace() {
		// initialize the ontology document manager
		OntDocumentManager docmgr = OntDocumentManager.getInstance();
		docmgr.reset(true);
		docmgr.clearCache();
		docmgr.setProcessImports(false);
		docmgr.setCacheModels(true);

		// initialize the file manager to cache ontologies
		FileManager fmgr = FileManager.get();
		fmgr.resetCache();
		fmgr.setModelCaching(true);
		docmgr.setFileManager(fmgr);

		// initialize the ontology specification to use for ontology models
		ontModelSpec = new OntModelSpec(OntModelSpec.OWL_DL_MEM_TRANS_INF);
		ontModelSpec.setDocumentManager(docmgr);

		WDO_Metamodel
				.initialize(ontModelSpec, this.getClass().getClassLoader());

		// initialize Wdo API objects
		Wdo = new WDO(this);
		Saw = new SAW(this);
		Data = new Data(this);
		Method = new Method(this);
//		ciCache = CIServerCache.getInstance();
	}

	/**
	 * Get the ontmodel for the Wdo of this workspace.
	 * 
	 * @return the base Wdo
	 */
	public OntModel getBaseWDO() {
		return baseWDO;
	}

	/**
	 * Create a new Wdo from scratch, and set it as the Base Wdo of this
	 * workspace.
	 * 
	 * @param uri
	 *            The URI for the new Wdo
	 * @return The new Base Wdo of this workspace
	 */
	public OntModel createBaseWDO(String uri, String url) throws NotFoundException {
		OntModel ontmodel = ModelFactory.createOntologyModel(ontModelSpec);

		Ontology ont = ontmodel.createOntology(uri);

		OntDocumentManager docmgr = ontModelSpec.getDocumentManager();
		docmgr.addModel(uri, ontmodel);
		docmgr.addIgnoreImport(uri);
		if (url != null) {
			docmgr.addAltEntry(uri, url);	
		}

		initializeBaseWDO(ontmodel);

		OntClass wdocls = Wdo.getOntClass(WDO_Metamodel.WDOCLASS_URI);
		ont.addRDFType(wdocls);

		setModified(uri, true);

		return baseWDO;
	}

	/**
	 * Loads an OWL document from the location specified. The OWL file may
	 * contain one of three types of documents: 1) An Wdo, i.e., a
	 * Workflow-Driven Ontology 2) A Workflow, i.e., an Model-Based Workflow
	 * (Saw) 3) A regular ontology
	 * 
	 * @param url
	 *            The URL that specifies the location from where to read the OWL
	 *            document
	 * @param uri
	 *            The URI to use as the document's URI
	 * @return The Jena-OntModel for the loaded OWL document
	 * @throws Exception
	 */
	public OntModel loadOWL(String url, String uri) throws NotFoundException {
		OntModel ontmodel = null;
		if (url != null && uri != null) {
			// check to see if URI is already loaded
			ontmodel = this.getOntModel(uri);
			// if the ontmodel is already loaded, check that all the
			// dependencies are loaded as well
			if (ontmodel != null) {
				setImports(ontmodel);
			}
			// if the ontmodel is not yet loaded
			else {
				// this cache is informational - keeping track
				// of the opened files/servers for saving later
//				if (url.toLowerCase().startsWith("http:"))
//					ciCache.hashURL(url);
				ontmodel = ModelFactory.createOntologyModel(ontModelSpec);
				ontmodel.read(url, uri, null);
				String docURI = this.getOntModelURI(ontmodel);
				OntDocumentManager docmgr = ontModelSpec.getDocumentManager();
				docmgr.addModel(docURI, ontmodel);
				docmgr.addIgnoreImport(docURI);
				docmgr.addAltEntry(docURI, url);
				// if the URI provided is different from the URI read from the
				// ontmodel document, reference both alternatives
				if (!docURI.equals(uri)) {
					docmgr.addModel(uri, ontmodel);
					docmgr.addIgnoreImport(uri);
					docmgr.addAltEntry(uri, url);
				}

				// if it is not a workflow, i.e., it is an ontology or WDO
				if (!isWorkflow(ontmodel)) {
					if (baseWDO == null && isWDO(ontmodel)) {
						initializeBaseWDO(ontmodel);
					} else {
						if (baseWDO != null) {
							// if the import closure of the base wdo does not
							// contain the uri being loaded, add it
							if (!baseWDO.listImportedOntologyURIs(true)
									.contains(docURI)) {
								Ontology docOnt = ontmodel.getOntology(docURI);
								Ontology baseOnt = baseWDO
										.getOntology(getOntModelURI(baseWDO));
								// if the base wdo did not know about this new
								// model, add it to its import list and set its
								// modified bit
								if (!baseOnt.imports(docOnt)) {
									baseOnt.addImport(docOnt);
									setModified(getOntModelURI(baseWDO), true);
								}
								baseWDO.addSubModel(ontmodel, true);
							}
							setImports(ontmodel);
						}
					}
				}
				// if it is a workflow
				else {
					setImports(ontmodel);
				}
			}
		}

		return ontmodel;
	}

	/**
	 * Checks whether an ontmodel is a WDO ontmodel.
	 * 
	 * @param ontmodel
	 * @return
	 */
	private boolean isWDO(OntModel ontmodel) {
		boolean ans = false;
		if (ontmodel != null) {
			ans = ontmodel.getNsPrefixURI("wdo") == null ? false : true;
		}
		return ans;
	}

	/**
	 * Checks whether an ontmodel is a workflow ontmodel.
	 * 
	 * @param ontmodel
	 * @return
	 */
	private boolean isWorkflow(OntModel ontmodel) {
		boolean ans = false;
		if (ontmodel != null) {
			ans = ontmodel.getNsPrefixURI("srcwdo") == null ? false : true;
		}
		return ans;
	}

	/**
	 * Checks whether the given URI belongs to a workflow OntModel
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isWorkflow(String uri) {
		boolean ans = false;
		if (uri != null) {
			ans = isWorkflow(this.getOntModel(uri));
		}
		return ans;
	}

	/**
	 * Checks whether the given URI belongs to an ontology OntModel
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isOntology(String uri) {
		boolean ans = false;
		if (uri != null) {
			OntModel ontmodel = this.getOntModel(uri);
			ans = (ontmodel != null && !isWorkflow(ontmodel));
		}
		return ans;
	}

	/**
	 * Initializes an Jena-OntModel as an Wdo, and sets it to be the base Wdo of
	 * this workspace.
	 * 
	 * @param ontmodel
	 *            The OntModel to set as base Wdo of this workspace.
	 */
	private void initializeBaseWDO(OntModel ontmodel) throws NotFoundException {
		OntModel wdo = this.getOntModel(WDO_Metamodel.WDO_URI);

		String docURI = this.getOntModelURI(ontmodel);
		Ontology docOnt = ontmodel.getOntology(docURI);
		docOnt.addImport(wdo.getOntology(WDO_Metamodel.WDO_URI));

		ontmodel.addLoadedImport(WDO_Metamodel.WDO_URI);
		ontmodel.setNsPrefix("wdo", WDO_Metamodel.WDO_URI + "#");
		ontmodel.setNsPrefix("pmlp", WDO_Metamodel.PMLP_URI + "#");

		baseWDO = ontmodel;
		ontmodelHierarchy = new OntModelHierarchy(this); // initialize ontmodel
															// hierarchy
															// structure

		// add wdoclass type to ontology
		OntClass wdocls = this.getOntModel(WDO_Metamodel.WDO_URI).getOntClass(
				WDO_Metamodel.WDOCLASS_URI);
		docOnt.addRDFType(wdocls);

		setImports(baseWDO);
	}

	/**
	 * Adds the imported documents as submodels of the specified OntModel
	 * 
	 * @param ontmodel
	 * @throws Exception
	 */
	private void setImports(OntModel ontmodel) throws NotFoundException {
		Set<String> importedURIs = ontmodel.listImportedOntologyURIs(false);
		for (Iterator<String> i = importedURIs.iterator(); i.hasNext();) {
			String importedURI = i.next();
			OntModel importedDoc = this.getOntModel(importedURI);
			if (importedDoc == null) {
				throw new NotFoundException(importedURI + " not found.");
			} else {
				ontmodel.addSubModel(importedDoc, false);
			}
		}
		ontmodel.rebind();
	}

	/**
	 * Get the loaded ontmodel specified.
	 * 
	 * @param uri
	 *            The URI of the loaded ontology to return
	 * @return The specified ontmodel, or null if not loaded in the workspace.
	 */
	public OntModel getOntModel(String uri) {
		// some URIs are encoded with an ending #. Remove it for comparison
		String temp = uri.split("#")[0];
		Model model = ontModelSpec.getDocumentManager().getModel(temp);
		// if not found, try again with # included in the end
		if (model == null) {
			model = ontModelSpec.getDocumentManager().getModel(temp + "#");
		}
		OntModel ontmodel = null;
		if (model != null) {
			try {
				ontmodel = (OntModel) model;
			} catch (Exception e) {
				ontmodel = ModelFactory
						.createOntologyModel(ontModelSpec, model);
			}
		}
		return ontmodel;
	}

	/**
	 * Returns the Ontology object of an OntModel document. If more than one,
	 * chooses the first one returned by the OntModel.listOntologies() iterator
	 * that meets the following criteria: 1) It is part of the base model of the
	 * ontmodel specified, i.e., not part of a submodel 2) It is not an imported
	 * ontology
	 * 
	 * @param ontmodel
	 *            The document from where to get the ontology object
	 * @return An ontology object, or null if non-existent.
	 */
	protected Ontology getFirstOntology(OntModel ontmodel) {
		Ontology ans = null;
		if (ontmodel != null) {

			for (ExtendedIterator<Ontology> i = ontmodel.listOntologies(); i
					.hasNext();) {
				Ontology temp = i.next();
				if (ontmodel.isInBaseModel(temp)) {
					String tempURI = temp.getURI().split("#")[0];
					Set<String> importedOntologies = ontmodel
							.listImportedOntologyURIs(true);
					if (!importedOntologies.contains(tempURI)
							&& !importedOntologies.contains(tempURI + "#")) {
						ans = temp;
						break;
					}
				}
			}

			if (ans == null) {
				OntClass type = ontmodel
						.getOntClass(WDO_Metamodel.WDOCLASS_URI);
				for (ExtendedIterator<Individual> i = ontmodel
						.listIndividuals(type); i.hasNext();) {
					Individual wdoind = i.next();
					if (ontmodel.isInBaseModel(wdoind)
							&& wdoind.canAs(Ontology.class)) {
						ans = wdoind.asOntology();
						break;
					}
				}
			}
		}
		return ans;
	}

	/**
	 * Get the document URI of the specified Jena OntModel.
	 * 
	 * @param ontmodel
	 *            The Jena OntModel for which to return the document URI
	 * @return The URI string
	 */
	public String getOntModelURI(OntModel ontmodel) {
		String uri = null;
		if (ontmodel != null) {
			Ontology ont = getFirstOntology(ontmodel); // assumming the first
														// ontology is always
														// the base ontology of
														// this ontmodel
			if (ont != null) {
				uri = ont.getURI();
				uri = uri.split("#")[0]; // if uri formatted with # at the end,
											// remove it
			}
		}
		return uri;
	}

	/**
	 * Get the URL for a given ontmodel.
	 * 
	 * @param ontmodel
	 * @return The URL for a given ontmodel, or null if non exists.
	 * @author agandara1, leonardo
	 */
	public String getOntModelURL(OntModel ontmodel) {
		String url = null;
		if (ontmodel != null) {
			String uri = getOntModelURI(ontmodel);
			OntDocumentManager docmgr = ontModelSpec.getDocumentManager();
			url = docmgr.getFileManager().getLocationMapper().getAltEntry(uri);
		}
		return url;
	}

	/**
	 * Sets a comment for the ontology object of the OntModel document specified
	 * 
	 * @param ontmodel
	 * @param comment
	 */
	public void setOntModelComment(OntModel ontmodel, String comment) {
		Ontology ont = getFirstOntology(ontmodel); // assumming the first
													// ontology is always the
													// base ontology of this
													// ontmodel
		if (ont != null) {
			if (comment != null) {
				ont.setComment(comment, "EN");
			} else {
				ont.setComment("", "EN");
			}
		}
		setModified(getOntModelURI(ontmodel), true);
	}

	/**
	 * Returns the comment for the ontology object of the OntModel document
	 * specified
	 * 
	 * @param ontmodel
	 * @return
	 */
	public String getOntModelComment(OntModel ontmodel) {
		String ans = null;
		Ontology ont = getFirstOntology(ontmodel); // assumming the first
													// ontology is always the
													// base ontology of this
													// ontmodel
		if (ont != null) {
			ans = ont.getComment("EN");
			if (ans == null || ans.isEmpty()) {
				ans = ont.getComment(null);
			}
		}
		return ans;
	}

	/**
	 * Lists the ontmodels of the ontologies or workflows loaded, not including
	 * the UL ontologies.
	 * 
	 * @param workflows
	 *            If true, returns the ontmodels of the workflow ontmodels,
	 *            otherwise returns the ontmodels of the ontologies.
	 * @return An iterator over the ontmodels selected.
	 */
	private Iterator<OntModel> listOntModels(boolean workflows) {
		ArrayList<OntModel> ontmodels = new ArrayList<OntModel>();
		if (baseWDO != null) {
			OntDocumentManager docmgr = ontModelSpec.getDocumentManager();
			for (Iterator<String> i = docmgr.listIgnoredImports(); i.hasNext();) {
				String onturi = i.next();
				OntModel ontmodel = this.getOntModel(onturi);
				if (ontmodel != null) {
					// if looking for workflow ontmodels, or looking for
					// ontology ontmodels
					if (this.isWorkflow(ontmodel) == workflows) {
						ontmodels.add(ontmodel);
					}
				}
			}
		}
		return ontmodels.iterator();
	}

	/**
	 * Lists the URIs of the ontologies or workflows loaded, not including the
	 * UL ontologies.
	 * 
	 * @param workflows
	 *            If true, returns the URIs of the workflow ontmodels,
	 *            otherwise, returns the URIs of the ontology ontmodels.
	 * @return An iterator over the URIs of the ontmodels selected.
	 */
	private Iterator<String> listOntModelURIs(boolean workflows) {
		ArrayList<String> ontURIs = new ArrayList<String>();
		for (Iterator<OntModel> i = this.listOntModels(workflows); i.hasNext();) {
			OntModel ontmodel = i.next();
			ontURIs.add(this.getOntModelURI(ontmodel));
		}
		return ontURIs.iterator();
	}

	/**
	 * Returns the list of ontologies loaded, not including the upper-level Wdo
	 * ontologies
	 * 
	 * @return An iterator over the ontmodels of the ontologies loaded
	 */
	public Iterator<OntModel> listOntologies() {
		return (this.listOntModels(false));
	}

	/**
	 * Lists the URIs of the ontologies loaded.
	 * 
	 * @return An iterator over a URI list.
	 */
	public Iterator<String> listOntologyURIs() {
		return (this.listOntModelURIs(false));
	}

	/**
	 * Returns the list of workflows loaded.
	 * 
	 * @return An iterator over the ontmodels of the workflows loaded
	 */
	public Iterator<OntModel> listWorkflows() {
		return (this.listOntModels(true));
	}

	/**
	 * Lists the URIs of the workflows loaded.
	 * 
	 * @return An iterator over a URI list.
	 */
	public Iterator<String> listWorkflowURIs() {
		return (this.listOntModelURIs(true));
	}

	/**
	 * Saves the specified ontmodel to a file located at the given URL. If the
	 * URL is not specified, tries to save to a previously registered saved
	 * location. If the URL is not specified, and previously saved location does
	 * not exist, throws an exception.
	 * 
	 * @param ontmodel
	 * @param url
	 * @throws Exception
	 */
	public void saveOntModelToFile(OntModel ontmodel, String url)
			throws Exception {
		if (ontmodel != null) {
			if (url == null) {
				url = getOntModelURL(ontmodel);
				if (url == null) {
					throw new Exception("Save location not specified.");
				}
			}
			String uri = getOntModelURI(ontmodel);
//			String lurl = url.toLowerCase();
//			int serverId = -1;
//			boolean saved = false;
//
//			if (lurl.startsWith(Namespace.NS_PROTOCOLS.http.toString())) {
//				
//				// if the uri is found on a ciserver - save there
//				// serverId is an identifier for a particular server known within
//				// the ciclient api
//				CIServerCache ciCache = CIServerCache.getInstance();
//				CIKnownServerTable ciServers = CIKnownServerTable.getInstance();
//
//				// we know this is an ht`tp address
//				serverId = ciCache.getServerFromURL(uri);
//				String resourceName = CIUtils.ciGetResourceName(uri);
//				// String projectName = CIUtils.ciGetProjectName(uri);
//				if (serverId != -1) {
//					// the uri is from a known server
//					// let's assure we have a connection
//					if (ciServers.ciGetServerAuthSession(serverId) == null) {
//						if (!ciServers.ciIsUsernamePasswordSet(serverId)) {
//							CIGetUP.showDialog(null, serverId);
//						}
//					}
//					CIClient ciclient = new CIClient(serverId);
//
//					// name the file
//					// Get today's date
//					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
//					String dts = formatter.format(new Date());
//					
//					// no local saves
//					Writer sw = new StringWriter();
//					ontmodel.write(sw, null, null);
//					String data = sw.toString();
//					String[] temp = url.split("/");			
//					
//					if(temp.length > 0){
//						CIReturnObject cro = CIGet.ciGetResourceIdFromURL(ciclient, url);
//						if(cro.gStatus.equals("0")){
//							if(isWorkflow(ontmodel)) {
//								cro = CIPut.ciUploadFileForURL(ciclient, url, data, false);
//							}
//							else  {
//								// if it is not a workflow, it must be a wdo we are creating
//								cro = CIPut.ciUploadFileForURL(ciclient, url, data, false);
//							}
//							if (cro.gStatus.compareTo("0") != 0) {
//								throw new Exception(
//										"Could not save to server: " + url);
//							}
//						} else {
//							// an error with a resource id of -1 means the
//							// resource was not found
//							if (cro.gResourceId.equals("-1")) {
//								// for a new item need to know the project to
//								// add it to
//								String projectName = CIGetProjectListDialog
//										.showDialog(null, null, ciclient);
//								if (projectName != null
//										&& !projectName.isEmpty()) {
//									if (isWorkflow(ontmodel)) {
//										cro = CIPut.ciUploadFile(ciclient,
//												projectName, resourceName,
//												data, CIClient.SAW_TYPE, true,
//												false);
//									} else {
//										// if it is not a workflow, it must be a
//										// wdo we are creating
//										cro = CIPut.ciUploadFile(ciclient,
//												projectName, resourceName,
//												data, CIClient.WDO_TYPE, true,
//												false);
//									}
//									if (!cro.gStatus.equals("0")) {
//										throw new Exception(
//												"Could not save to server: "
//														+ url);
//									}
//									System.out.println("url was " + url
//											+ " file url was " + cro.gFileURL);
//								} else {
//									throw new Exception(
//											"Must select a project to save to server: "
//													+ url);
//								}
//							} else {
//								throw new Exception(
//										"Could not save to server: " + url);
//							}
//						}
//						saved = true;
//
//						// trigger the triple store service
//						RDFAggregater_Service Service = new RDFAggregater_Service();
//						RDFAggregater proxy = Service
//								.getRDFAggregaterHttpPort();
//						if (proxy == null
//								|| !proxy.addDocumentAt(uri, dts + "@wdoit")
//										.equalsIgnoreCase("SUCCESS")) {
//							throw new Exception(
//									"File saved to server but triple store could not be notified.");
//						}
//					}
//				}
//			}
//			
//			// if saving to remote server failed or if the URL has a file protocol, save to local file
//			if (!saved) {
//				String filename = (url.startsWith(Namespace.NS_PROTOCOLS.file
//						.toString())) ? url.substring(5) : url;
//				FileOutputStream file = new FileOutputStream(filename, false);
//				ontmodel.getBaseModel().write(file, null, null);
//				file.close();
//			}
			
			FileOutputStream fileout;
			if (url.startsWith(Namespace.NS_PROTOCOLS.file.toString())) {
				fileout = new FileOutputStream(url.substring(5), false);
			}
			else {
				File file = new File(System.getProperty("user.home") + File.separator + "wdoit.temp");
				file.deleteOnExit();
				fileout = new FileOutputStream(file, false);
			}
			ontmodel.getBaseModel().write(fileout, null, null);
			fileout.close();

			// register latest saved URL with the URI of the model
			ontModelSpec.getDocumentManager().addAltEntry(uri, url);
			// update the modified bit for this model
			setModified(uri, false);
		}
	}

	/**
	 * List the children of an ontmodel (i.e., a workflow or an ontology) based
	 * on the workspace hierarchy maintained by the OntModelHierarchy class
	 * 
	 * @param ontmodelURI
	 * @return
	 */
	public Iterator<String> listOWLDocumentChildren(String ontmodelURI) {
		return ontmodelHierarchy.listChildren(ontmodelURI);
	}

	/**
	 * Gets the parent of an ontmodel (i.e., a workflow or an ontology) based on
	 * the workspace hierarchy maintained by the OntModelHierarchy class
	 * 
	 * @param ontmodelURI
	 * @return
	 */
	public String getOWLDocumentParent(String ontmodelURI) {
		String ans = null;
		if (ontmodelHierarchy != null) {
			ans = ontmodelHierarchy.getParent(ontmodelURI);
		}
		return ans;
	}

	protected void setModified(String uri, boolean b) {
		if (ontmodelHierarchy != null) {
			ontmodelHierarchy.setModified(uri, b);
		}
	}

	public boolean isModified(String uri) {
		boolean ans = false;
		if (ontmodelHierarchy != null) {
			ans = this.ontmodelHierarchy.isModified(uri);
		}
		return ans;
	}

	// /**
	// * @see OntModelHierarchy.getMaxInstanceNodeNo(String)
	// * @param uri
	// * @return
	// */
	// public int getMaxInstanceNodeNo(String uri) {
	// int ans = -1;
	// if (ontmodelHierarchy != null) {
	// ans = ontmodelHierarchy.getMaxInstanceNodeNo(uri);
	// }
	// return ans;
	// }
	//
	// /**
	// * @see OntModelHierarchy.incMaxInstanceNodeNo(String)
	// * @param uri
	// */
	// public void incMaxInstanceNodeNo(String uri) {
	// if (ontmodelHierarchy != null) {
	// ontmodelHierarchy.incMaxInstanceNodeNo(uri);
	// }
	// }

	/**
	 * Returns the short version of a URI
	 * 
	 * @return A string representing a short version of the full URI
	 */
	public static String shortURI(String uri) {
		String ans = null;
		if (uri != null) {
			int idx = uri.lastIndexOf("/");
			if (idx > 0) {
				ans = uri.substring(idx + 1);
			} else {
				idx = uri.lastIndexOf(":");
				if (idx > 0) {
					ans = uri.substring(idx + 1);
				} else {
					ans = uri;
				}
			}
			ans = ans.split("#")[0];
		}
		return ans;
	}

	/**
	 * Lists the URIs of all opened ontology and workflow docs.
	 * 
	 * @return An iterator over a URI list.
	 * @author agandara1
	 * @see listWorkflowURIs or listOntologyURIs if only want specific lists.
	 *      This call is for obtaining both
	 */
	public Iterator<String> listOpenURIs() {

		ArrayList<String> ontURIs = new ArrayList<String>();
		if (baseWDO != null) {
			OntDocumentManager docmgr = ontModelSpec.getDocumentManager();
			for (Iterator<String> i = docmgr.listIgnoredImports(); i.hasNext();) {
				String onturi = i.next();
				ontURIs.add(onturi);
			}
		}
		return ontURIs.iterator();
	}

	/**
	 * Reprocesses the loaded ontologies and workflows to update the ontmodel
	 * hierarchy. Note: The import relations between ontmodels should be
	 * resolved and binded before calling this method.
	 */
	public void updateOntModelHierarchy() {
		// reprocess hierarchy with all ontologies loaded in the workspace to
		// ensure that ontologies that were loaded before the baseWDO are
		// included
		for (Iterator<OntModel> i = this.listOntologies(); i.hasNext();) {
			OntModel temp = i.next();
			if (temp != null) {
				ontmodelHierarchy.addOntology(temp);
			}
		}
		// reprocess hierarchy with all workflows
		for (Iterator<OntModel> i = this.listWorkflows(); i.hasNext();) {
			OntModel temp = i.next();
			if (temp != null) {
				ontmodelHierarchy.addWorkflow(temp);
			}
		}
	}

	/**
	 * Maintain hierarchical structure of the loaded workspace The base WDO will
	 * be set as the root of the tree, with two children: Imported ontologies,
	 * and Workflows. Imported ontologies will show a hierarchical structure
	 * based on the owl:import statements of the OWL ontology documents.
	 * Workflows will show a hierarchical structure based on the wdo:detailedBy
	 * statements of an OWL workflow document.
	 * 
	 * @author Leonardo Salayandia
	 */
	private class OntModelHierarchy {
		final private Workspace workspace;
		final private String baseURI;
		private HashMap<String, Vector<String>> childrenHash;
		private HashMap<String, Boolean> modified;

		// private HashMap<String, Integer> instanceCount;

		protected OntModelHierarchy(Workspace workspace) {
			this.workspace = workspace;
			// initialize children hash
			childrenHash = new HashMap<String, Vector<String>>();
			Vector<String> baseWDOChildren = new Vector<String>();
			baseWDOChildren.add(Workspace.ONTOLOGIES_NODE_ID);
			baseWDOChildren.add(Workspace.WORKFLOWS_NODE_ID);
			baseURI = this.workspace.getOntModelURI(this.workspace.baseWDO);
			childrenHash.put(baseURI, baseWDOChildren);
			childrenHash.put(Workspace.ONTOLOGIES_NODE_ID, null);
			childrenHash.put(Workspace.WORKFLOWS_NODE_ID, null);
			for (Iterator<OntModel> i = this.workspace.listOntologies(); i
					.hasNext();) {
				addOntology(i.next());
			}
			// initialize modified hash
			modified = new HashMap<String, Boolean>();
			// initialize instance count hash
			// instanceCount = new HashMap<String, Integer>();
		}

		/**
		 * Add a workflow model to the workspace hierarchy structure
		 * 
		 * @param ontmodel
		 */
		protected void addWorkflow(OntModel ontmodel) {
			if (ontmodel != null) {
				String ontmodelURI = workspace.getOntModelURI(ontmodel);
				addChild(ontmodelURI, null);
				// instanceCount.put(ontmodelURI,
				// findMaxInstanceNodeNo(ontmodelURI));
				for (Iterator<String> iter = workspace.Saw
						.listDetailedByDependentSawURIs(ontmodel); iter
						.hasNext();) {
					String childURI = iter.next();
					if (workspace.isWorkflow(childURI)) {
						addChild(ontmodelURI, childURI);
						// instanceCount.put(childURI,
						// findMaxInstanceNodeNo(childURI));
					}
				}
				// set orphan workflows as children of "workflows"
				childrenHash.put(Workspace.WORKFLOWS_NODE_ID,
						new Vector<String>());
				Vector<String> orphans = new Vector<String>();
				for (Iterator<String> i = childrenHash.keySet().iterator(); i
						.hasNext();) {
					String childURI = i.next();
					if (!childURI.equals(baseURI)
							&& !childURI.equals(Workspace.ONTOLOGIES_NODE_ID)
							&& !childURI.equals(Workspace.WORKFLOWS_NODE_ID)) {
						boolean foundOrphan = true;
						for (Iterator<String> j = childrenHash.keySet()
								.iterator(); j.hasNext();) {
							String parentURI = j.next();
							if (!childURI.equals(parentURI)) {
								Vector<String> children = childrenHash
										.get(parentURI);
								if (children != null
										&& children.contains(childURI)) {
									foundOrphan = false;
									break;
								}
							}
						}
						if ((foundOrphan) && (!orphans.contains(childURI))) {
							orphans.add(childURI);
						}
					}
				}
				childrenHash.put(Workspace.WORKFLOWS_NODE_ID, orphans);
			}
		}

		/**
		 * Add an ontology or wdo to the workspace hierarchy structure
		 * 
		 * @param ontmodel
		 */
		protected void addOntology(OntModel ontmodel) {
			if (ontmodel != null) {
				String ontmodelURI = workspace.getOntModelURI(ontmodel);
				Set<String> importedURIs = ontmodel
						.listImportedOntologyURIs(false);
				String parentURI;
				if (ontmodelURI.equals(baseURI))
					parentURI = Workspace.ONTOLOGIES_NODE_ID;
				else
					parentURI = ontmodelURI;
				Iterator<String> i = importedURIs.iterator();
				if (!i.hasNext()) {
					addChild(parentURI, null);
				} else {
					for (; i.hasNext();) {
						String childURI = i.next();
						if (!childURI.equals(ontmodelURI)
								&& workspace.isOntology(childURI)) {
							addChild(parentURI, childURI);
						}
					}
				}
			}
		}

		private void addChild(String parentURI, String childURI) {
			if (parentURI != null) {
				String tempParentURI = parentURI.split("#")[0];
				Vector<String> children = childrenHash.get(tempParentURI);
				if (children == null) {
					children = new Vector<String>();
					childrenHash.put(tempParentURI, children);
				}
				if (childURI != null) {
					String tempChildURI = childURI.split("#")[0];
					if (!childURI.equals(tempParentURI)
							&& !children.contains(tempChildURI)) {
						children.add(tempChildURI);
					}
				}
			}
		}

		/**
		 * Lists the children (their URIs) of the model correponding to the
		 * specified URI. Only direct children are listed, i.e., not
		 * grandchildren, etc.
		 * 
		 * @param ontmodelURI
		 * @return
		 */
		protected Iterator<String> listChildren(String ontmodelURI) {
			Vector<String> children = childrenHash.get(ontmodelURI);
			if (children == null) {
				children = new Vector<String>();
			}
			return children.iterator();
		}

		/**
		 * Set the modified bit for the model corresponding to the specified URI
		 * 
		 * @param uri
		 * @param b
		 */
		protected void setModified(String uri, boolean b) {
			modified.put(uri, b);
		}

		/**
		 * Check the modified bit for the model corresponding to the specified
		 * URI
		 * 
		 * @param uri
		 * @return
		 */
		protected boolean isModified(String uri) {
			boolean ans = false;
			if (modified.containsKey(uri)) {
				ans = modified.get(uri);
			}
			return ans;
		}

		/**
		 * Get the URI of the parent model for the model that has the URI
		 * specified. If uri corresponds to the base WDO of the workspace, the
		 * parent will be null. If uri corresponds to an ontology, the parent
		 * URI will either be another ontology or the ONTOLOGY_NODE_ID. If uri
		 * corresponds to a workflow, the parent URI will either be another
		 * workflow or the WORKFLOW_NODE_ID.
		 * 
		 * @param uri
		 * @return
		 */
		protected String getParent(String uri) {
			String ans = null;
			if (uri != null && !uri.equals(baseURI)) {
				for (Iterator<String> i = childrenHash.keySet().iterator(); i
						.hasNext();) {
					String curr = i.next();
					Vector<String> children = childrenHash.get(curr);
					String temp = uri.split("#")[0]; // some uris have # at the
														// end, remove for
														// comparison.
					if (children != null
							&& (children.contains(temp) || children
									.contains(temp + "#"))) {
						ans = curr;
						break;
					}
				}
			}
			return ans;
		}

		// /**
		// * Calculates the next instance node number to be used to create
		// * a new instance in a SAW
		// * This is counted from the given SAW model looking for the max
		// * number (#) from i#
		// */
		// private int findMaxInstanceNodeNo(String sawURI){
		// int maxNodeNo = 0;
		// OntModel saw = workspace.getOntModel(sawURI);
		// if (saw != null){
		// for (Iterator<Individual> i = SAW.listSAWInstances(saw, null, false);
		// i.hasNext(); ) {
		// Individual sawInd = i.next();
		// String indName = sawInd.getLocalName();
		// if (indName != null) {
		// String temp[] = indName.split("i");
		// if (temp.length == 2) {
		// try {
		// Integer nodeNo = Integer.valueOf(temp[1]);
		// if (nodeNo > maxNodeNo) maxNodeNo = nodeNo;
		// }
		// catch (NumberFormatException e) {
		// // URI didn't correspond to the i# format, ignore and move to the
		// next
		// }
		// }
		// }
		// }
		// }
		// return maxNodeNo;
		// }
		//
		// /**
		// * Get the max instance node count for the specified workflow
		// * @param sawURI
		// * @return
		// */
		// protected int getMaxInstanceNodeNo(String sawURI) {
		// Integer ans = instanceCount.get(sawURI);
		// return (ans == null) ? -1 : ans;
		// }
		//
		// /**
		// * Increment the max instance node count by one for the specified
		// workflow
		// * @param sawURI
		// */
		// protected void incMaxInstanceNodeNo(String sawURI) {
		// instanceCount.put(sawURI, getMaxInstanceNodeNo(sawURI)+1);
		// }
	}
}