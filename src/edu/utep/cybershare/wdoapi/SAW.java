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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.utep.cybershare.wdoapi.metamodel.WDO_Metamodel;
import edu.utep.cybershare.wdoapi.util.URI;

/**
 * @author Leonardo Salayandia
 * 
 */
public class SAW {
	private final Workspace workspace;

	protected SAW(Workspace wkspc) {
		this.workspace = wkspc;

	}

	/**
	 * Check ontmodel is a Semantic Abstract Workflow or not.
	 * 
	 * @param ontmodel
	 * @return
	 */
	public static boolean isSAW(OntModel ontmodel) {
		return (ontmodel == null) ? false
				: (getSAWSAWInstance(ontmodel) != null);
	}

	/**
	 * Get SAW instance that describes the Semantic Abstract Workflow contained
	 * in ontmodel
	 * 
	 * @param ontmodel
	 * @return
	 */
	public static OntClass getSAWSAWInstance(OntModel ontmodel) {
		OntClass ans = null;
		if (ontmodel != null) {
			OntClass type = ontmodel.getOntClass(WDO_Metamodel.SAW_URI);
			for (ExtendedIterator<OntClass> i = ontmodel.listNamedClasses(); i
					.hasNext();) {
				OntClass ins = i.next();
				if (ontmodel.isInBaseModel(ins)
						&& ins.hasSuperClass(type, true)) {
					ans = ins;
					break;
				}
			}
		}
		return ans;
	}

	/**
	 * Get label of ins
	 * 
	 * @param ins
	 * @return
	 */
	public static String getSAWInstanceLabel(OntClass ins) {
		String ans = "";
		if (ins != null) {
			RDFNode temp = getRelatedRDFNode(ins, WDO_Metamodel.HAS_NAME_URI);
			if (temp != null) {
				RDFVisitor tmpVisitor = new RDFVisitor() {
					@Override
					public Object visitBlank(Resource r, AnonId id) {
						return null;
					}

					@Override
					public Object visitLiteral(Literal l) {
						return l.getString();
					}

					@Override
					public Object visitURI(Resource r, String uri) {
						return null;
					}
				};
				ans = (String) temp.visitWith(tmpVisitor);
			}
		}
		return ans;
	}

	/**
	 * Set label of ins and sets the modified bit in the workspace.
	 * 
	 * @param ins
	 * @param name
	 */
	public void setSAWInstanceLabel(OntClass ins, String name) {
		if (ins != null) {
			String prev = SAW.getSAWInstanceLabel(ins);
			if ((prev == null && name != null)
					|| (prev != null && !prev.equals(name))) {
				OntModel ontmodel = ins.getOntModel();
				DatatypeProperty hasName = ontmodel
						.getDatatypeProperty(WDO_Metamodel.HAS_NAME_URI);
				ins.removeAll(hasName);
				ins.addLiteral(hasName, name);
				workspace.setModified(
						workspace.getOntModelURI(ins.getOntModel()), true);
			}
		}
	}

	/**
	 * Gets comment for ins This method is preferred over OntResource.getComment
	 * because it attempts twice to get the comment: 1) using this application's
	 * default "EN" language setting, and 2) not using the language setting for
	 * comments created using other applications.
	 * 
	 * @param ins
	 * @return
	 */
	public static String getSAWInstanceComment(OntClass ins) {
		String ans = null;
		if (ins != null) {
			ans = ins.getComment("EN");
			if (ans == null) {
				ans = ins.getComment(null);
			}
		}
		return ans;
	}

	/**
	 * Set comment for ins This method is preferred over OntResource.setComment
	 * because it uses the default "EN" language setting and sets the modified
	 * bit in the workspace.
	 * 
	 * @param ins
	 * @param comment
	 */
	public void setSAWInstanceComment(OntClass ins, String comment) {
		if (ins != null) {
			String prev = SAW.getSAWInstanceComment(ins);
			if ((prev == null && comment != null)
					|| (prev != null && !prev.equals(comment))) {
				ins.setComment(comment, "EN");
				workspace.setModified(
						workspace.getOntModelURI(ins.getOntModel()), true);
			}
		}
	}

	/**
	 * Gets the type of ins
	 * 
	 * @param ins
	 *            SAW instance for which to return its type
	 * @return type of the specified instance, null if the instance does not
	 *         have a type
	 */
	public static OntClass getSAWInstanceType(OntClass ins) {
		OntClass type = null;
		if (ins != null) {
			type = ins.listSuperClasses(true).next();
		}
		return type;
	}

	/**
	 * Set type of ins, replacing previous types
	 * 
	 * @param ins
	 * @param type
	 */
	public void setSAWInstanceType(OntClass ins, OntClass type) {
		if (type != null && ins != null) {
			if (!type.equals(getSAWInstanceType(ins))) {
				ins.setSuperClass(type);
				workspace.setModified(
						workspace.getOntModelURI(ins.getOntModel()), true);
			}
		}
	}

	/**
	 * Check whether ins has the given type. If subtypes, also returns instances
	 * of subclasses of type
	 * 
	 * @param type
	 * @param ins
	 * @param subtypes
	 * @return
	 */
	public static boolean isOfType(OntClass type, OntClass ins, boolean subtypes) {
		boolean ans = false;
		if (type != null && ins != null) {
			OntClass insType = getSAWInstanceType(ins);
			if (insType != null) {
				ans = (insType.equals(type));
				if (!ans && subtypes) {
					ans = (insType.hasSuperClass(type, false));
				}
			}
		}
		return ans;
	}

	/**
	 * Check whether ins has type wdo:SemanticAbstractWorkflow or a subclass of
	 * it
	 * 
	 * @param ins
	 * @return
	 */
	public static boolean isSemanticAbstractWorkflowType(OntClass ins) {
		boolean ans = false;
		if (ins != null) {
			OntModel ontmodel = ins.getOntModel();
			OntClass type = ontmodel.getOntClass(WDO_Metamodel.SAW_URI);
			ans = isOfType(type, ins, true);
		}
		return ans;
	}

	/**
	 * Check whether ins has type pmlp:Source or a subclass of it
	 * 
	 * @param ins
	 * @return
	 */
	public static boolean isPMLSourceType(OntClass ins) {
		boolean ans = false;
		if (ins != null) {
			OntModel ontmodel = ins.getOntModel();
			OntClass type = ontmodel.getOntClass(WDO_Metamodel.SOURCE_URI);
			ans = isOfType(type, ins, true);
		}
		return ans;
	}

	// /**
	// * Check whether ins has type pmlp:InferenceEngine or a subclass of it
	// * @param ins
	// * @return
	// */
	// public static boolean isInferenceEngineType(OntClass ins) {
	// boolean ans = false;
	// if (ins != null) {
	// OntModel ontmodel = ins.getOntModel();
	// OntClass type = ontmodel.getOntClass(WDO_Metamodel.INFERENCE_ENGINE_URI);
	// ans = isOfType(type, ins, true);
	// }
	// return ans;
	// }

	/**
	 * Check whether ins has type wdo:Data or a subclass of it
	 * 
	 * @param ins
	 * @return
	 */
	public static boolean isDataType(OntClass ins) {
		boolean ans = false;
		if (ins != null) {
			OntModel ontmodel = ins.getOntModel();
			OntClass type = ontmodel.getOntClass(WDO_Metamodel.DATA_URI);
			ans = isOfType(type, ins, true);
		}
		return ans;
	}

	/**
	 * Check whether ins has type wdo:Method or a sublass of it
	 * 
	 * @param ins
	 * @return
	 */
	public static boolean isMethodType(OntClass ins) {
		boolean ans = false;
		if (ins != null) {
			OntModel ontmodel = ins.getOntModel();
			OntClass type = ontmodel.getOntClass(WDO_Metamodel.METHOD_URI);
			ans = isOfType(type, ins, true);
		}
		return ans;
	}

	/**
	 * Check whether ins has type pmlp:Source or a subclass of it, and is acting
	 * as a source
	 * 
	 * @param ins
	 * @return
	 */
	public static boolean isSource(OntClass ins) {
		boolean ans = false;
		if (isPMLSourceType(ins)) {
			OntClass[] outputs = listHasOutput(ins);
			ans = (outputs != null && outputs.length > 0);
		}
		return ans;
	}

	/**
	 * Check whether ins has type pmlp:Source or a subclass of it, and is acting
	 * as a sink
	 * 
	 * @param ins
	 * @return
	 */
	public static boolean isSink(OntClass ins) {
		boolean ans = false;
		if (isPMLSourceType(ins)) {
			OntClass[] inputs = listHasInput(ins);
			ans = (inputs != null && inputs.length > 0);
		}
		return ans;
	}

	/**
	 * List instances included in saw that have the specified type If type not
	 * specified, returns all instances of the saw If subtypes, also return
	 * instances of subclasses of type
	 * 
	 * @param saw
	 * @param type
	 * @param subtypes
	 * @return iterator of instances; if saw and type are null, an empty
	 *         iterator is returned.
	 */
	public static Iterator<OntClass> listSAWInstances(OntModel saw,
			OntClass type, boolean subtypes) {
		ArrayList<OntClass> ans = new ArrayList<OntClass>();
		if (saw != null) {
			String ns = null;
			OntClass sawsawIns = getSAWSAWInstance(saw);
			if (sawsawIns != null) {
				ns = URI.getNameSpace(sawsawIns.getURI());
			}
			// if type is not null, apply type filter on search
			if (type != null && ns != null) {
				for (ExtendedIterator<OntClass> i = saw.listNamedClasses(); i
						.hasNext();) {
					OntClass ins = i.next();
					if (saw.isInBaseModel(ins) && isOfType(type, ins, subtypes)) {
						if (ns.equals(URI.getNameSpace(ins.getURI()))) {
							ans.add(ins);
						}
					}
				}
			}
			// if type is null and a saw was specified, return all individuals
			// of saw
			else if (type == null) {
				for (ExtendedIterator<OntClass> i = saw.listNamedClasses(); i
						.hasNext();) {
					OntClass ins = i.next();
					if (saw.isInBaseModel(ins)) {
						if (ns.equals(URI.getNameSpace(ins.getURI()))) {
							ans.add(ins);
						}
					}
				}
			}
		}
		return ans.iterator();
	}

	/**
	 * List instances of subclasses of wdo:Method included in saw
	 * 
	 * @param saw
	 * @return iterator of instances
	 */
	public Iterator<OntClass> listMethodSAWInstances(OntModel saw) {
		OntClass type = saw.getOntClass(WDO_Metamodel.METHOD_URI);
		return listSAWInstances(saw, type, true);
	}

	/**
	 * List instances of subclasses of wdo:Data included in saw
	 * 
	 * @param saw
	 * @return iterator of instances
	 */
	public Iterator<OntClass> listDataSAWInstances(OntModel saw) {
		OntClass type = saw.getOntClass(WDO_Metamodel.DATA_URI);
		return listSAWInstances(saw, type, true);
	}

	/**
	 * List instances of subclasses of pmlp:Source included in saw
	 * 
	 * @param saw
	 * @return
	 */
	public Iterator<OntClass> listSourceSAWInstances(OntModel saw) {
		OntClass type = saw.getOntClass(WDO_Metamodel.SOURCE_URI);
		return listSAWInstances(saw, type, true);
	}

	/**
	 * Get RDFNode related to ins through specified property Assuming only one
	 * instance related; if more, returns one randomly
	 * 
	 * @param ins
	 * @param propURI
	 * @return An RDFNode or null if none exist
	 */
	private static RDFNode getRelatedRDFNode(OntResource ins, String propURI) {
		RDFNode rdfNode = null;
		if (ins != null && propURI != null) {
			OntModel saw = ins.getOntModel();
			OntProperty prop = saw.getOntProperty(propURI);
			if (prop != null) {
				rdfNode = ins.getPropertyValue(prop);
			}
		}
		return rdfNode;
	}

	/**
	 * List RDFNodes related to ins through specified property
	 * 
	 * @param ins
	 * @param propURI
	 * @return An array of RDFNodes or null if none exist
	 */
	private static RDFNode[] listRelatedRDFNodes(OntClass ins, String propURI) {
		RDFNode rdfNodes[] = null;
		if (ins != null && propURI != null) {
			OntModel saw = ins.getOntModel();
			OntProperty prop = saw.getOntProperty(propURI);
			if (prop != null) {
				ArrayList<RDFNode> tmp = new ArrayList<RDFNode>();
				for (NodeIterator i = ins.listPropertyValues(prop); i.hasNext();) {
					tmp.add(i.nextNode());
				}
				if (tmp.size() > 0) {
					rdfNodes = new RDFNode[tmp.size()];
					tmp.toArray(rdfNodes);
				}
			}
		}
		return rdfNodes;
	}

	/**
	 * Get coordinate instance related to ins through hasLabelCoordinate or
	 * hasCoordinate If ins is of type wdo:Data, gets the coordinate instance
	 * for its label position Otherwise, gets coordinate instance for its node
	 * position
	 * 
	 * @param ins
	 * @return Instance that represents a coordinate
	 */
	private static Individual getCoordinateInstance(OntClass ins) {
		Individual indCoord = null;
		if (ins != null) {
			RDFNode temp = (isDataType(ins)) ? getRelatedRDFNode(ins,
					WDO_Metamodel.HAS_LABEL_COORDINATE_URI)
					: getRelatedRDFNode(ins, WDO_Metamodel.HAS_COORDINATE_URI);
			if (temp != null) {
				indCoord = temp.as(Individual.class);
			}
		}
		return indCoord;
	}

	/**
	 * Remove coordinate instance related to ins through hasLabelCoordinate or
	 * hasCoordinate If ins is of type wdo:Data, removes coordinate instance for
	 * its label position Otherwise, removes coordinate instance for its node
	 * position
	 * 
	 * @param ins
	 */
	private static void removeCoordinateInstance(OntClass ins) {
		if (ins != null) {
			OntModel saw = ins.getOntModel();
			Individual coord = getCoordinateInstance(ins);
			if (coord != null) {
				OntProperty hasCoord = (isDataType(ins)) ? saw
						.getOntProperty(WDO_Metamodel.HAS_LABEL_COORDINATE_URI)
						: saw.getOntProperty(WDO_Metamodel.HAS_COORDINATE_URI);
				if (hasCoord != null) {
					ins.removeProperty(hasCoord, coord);
				}
				coord.remove();
			}
		}
	}

	/**
	 * Get Java.awt.Point representing coordinate of ins
	 * 
	 * @param ins
	 * @return A coordinate point of the instance specified, null if non exists
	 */
	public static Point getInstanceCoordinate(OntClass ins) {
		Point coord = null;
		Individual coordInd = getCoordinateInstance(ins);
		if (coordInd != null) {
			RDFVisitor tmpVisitor = new RDFVisitor() {
				@Override
				public Object visitBlank(Resource r, AnonId id) {
					return null;
				}

				@Override
				public Object visitLiteral(Literal l) {
					return l.getInt();
				}

				@Override
				public Object visitURI(Resource r, String uri) {
					return null;
				}
			};
			RDFNode tmpx = getRelatedRDFNode(coordInd, WDO_Metamodel.X_URI);
			RDFNode tmpy = getRelatedRDFNode(coordInd, WDO_Metamodel.Y_URI);
			int x = (tmpx == null) ? 0 : (Integer) tmpx.visitWith(tmpVisitor);
			int y = (tmpy == null) ? 0 : (Integer) tmpy.visitWith(tmpVisitor);
			coord = new Point(x, y);
		}
		return coord;
	}

	/**
	 * Create coordinate instance with coordinate value provided and set it as
	 * coordinate of ins If ins is of type wdo:Data, sets has_label_coordinate
	 * Otherwise, sets has_coordinate Replaces any existing coordinate instance
	 * for ins.
	 * 
	 * @param ins
	 * @param coord
	 */
	public void setInstanceCoordinate(OntClass ins, Point coord) {
		if (ins != null && coord != null) {
			Point prev = getInstanceCoordinate(ins);
			if (!coord.equals(prev)) {
				OntModel ontmodel = ins.getOntModel();
				Individual coordInd = getCoordinateInstance(ins);
				DatatypeProperty xProp = ontmodel
						.getDatatypeProperty(WDO_Metamodel.X_URI);
				DatatypeProperty yProp = ontmodel
						.getDatatypeProperty(WDO_Metamodel.Y_URI);
				if (coordInd == null) {
					OntClass rectCoordCls = ontmodel
							.getOntClass(WDO_Metamodel.COORD_URI);
					coordInd = ontmodel.createIndividual(rectCoordCls);
				} else {
					coordInd.removeAll(xProp);
					coordInd.removeAll(yProp);
				}
				coordInd.addLiteral(xProp, coord.x);
				coordInd.addLiteral(yProp, coord.y);

				OntProperty hasCoord = (isDataType(ins)) ? ontmodel
						.getOntProperty(WDO_Metamodel.HAS_LABEL_COORDINATE_URI)
						: ontmodel
								.getOntProperty(WDO_Metamodel.HAS_COORDINATE_URI);
				ins.setPropertyValue(hasCoord, coordInd);

				workspace.setModified(workspace.getOntModelURI(ontmodel), true);
			}
		}
	}

	/**
	 * Get QName of ins The QName of the instance will be either:
	 * insTypeLabel:insLabel or insTypeLabel
	 * 
	 * @param ins
	 * @return A string
	 */
	public static String getSAWInstanceQName(OntClass ins) {
		OntClass insType = getSAWInstanceType(ins);
		String typeQName = (insType == null) ? "" : WDO.getClassQName(insType);
		String insLabel = SAW.getSAWInstanceLabel(ins);
		return (insLabel != null && !insLabel.isEmpty()) ? typeQName + ":"
				+ insLabel : typeQName;
	}

	/**
	 * Create a new Jena OntModel that represents a Semantic Abstract Workflow
	 * The new OntModel will have uri as the document's URI
	 * 
	 * @param uri
	 *            The URI to use as the OntModel's base URI
	 * @return The newly created SAW
	 */
	public OntModel createSAW(String uri) {
		OntModel saw = null;
		if (uri != null && !uri.isEmpty()) {
			// initialize SAW ontmodel
			saw = ModelFactory.createOntologyModel(this.workspace.ontModelSpec);
			OntClass sawcls = this.workspace.Wdo
					.getOntClass(WDO_Metamodel.SAW_URI);
			Ontology ont = saw.createOntology(uri);
			ont.addRDFType(sawcls);
			// add source Wdo as imported model
			OntModel baseWDO = this.workspace.getBaseWDO();
			String baseWDOURI = this.workspace.getOntModelURI(baseWDO);
			Ontology baseWDOOnt = baseWDO.getOntology(baseWDOURI);
			ont.addImport(baseWDOOnt);
			saw.addSubModel(baseWDO);
			saw.setNsPrefix("srcwdo", baseWDOURI + "#");
			saw.setNsPrefix("wdo", WDO_Metamodel.WDO_URI + "#");
			saw.setNsPrefix("pmlp", WDO_Metamodel.PMLP_URI + "#");

			OntDocumentManager docmgr = this.workspace.ontModelSpec
					.getDocumentManager();
			docmgr.addModel(uri, saw);
			docmgr.addIgnoreImport(uri);

			// create SAW SAW-instance
			OntClass sawsawInstance = createSAWInstance(saw, sawcls, null,
					false);
			OntProperty hasDefaultInstance = saw
					.getOntProperty(WDO_Metamodel.HAS_DEFAULT_INSTANCE_URI);
			sawsawInstance.setPropertyValue(hasDefaultInstance, ont);
			// add new workflow to the ont document hierarchy in the workspace
			this.workspace.updateOntModelHierarchy();
			// set the modified flag in the workspace for the new workflow
			this.workspace.setModified(uri, true);
		}
		return saw;
	}

	/**
	 * Relate formatIns to dataIns through the hasFormat property If formatIns
	 * is null, removes previously set relation
	 * 
	 * @param dataIns
	 * @param formatIns
	 */
	public void setFormat(OntClass dataIns, Individual formatIns) {
		if (dataIns != null) {
			Individual prev = getFormat(dataIns);
			if ((prev == null && formatIns != null)
					|| (prev != null && !prev.equals(formatIns))) {
				OntModel dataOntModel = dataIns.getOntModel();
				OntProperty hasFormat = dataOntModel
						.getOntProperty(WDO_Metamodel.HAS_FORMAT_URI);
				dataIns.setPropertyValue(hasFormat, formatIns);
				workspace.setModified(workspace.getOntModelURI(dataOntModel),
						true);
			}
		}
	}

	/**
	 * Get format instance that is related to dataIns through the property
	 * hasFormat
	 * 
	 * @param dataIns
	 * @return
	 */
	public static Individual getFormat(OntClass dataIns) {
		Individual ans = null;
		if (dataIns != null) {
			RDFNode temp = getRelatedRDFNode(dataIns,
					WDO_Metamodel.HAS_FORMAT_URI);
			if (temp != null) {
				ans = temp.as(Individual.class);
			}
		}
		return ans;
	}

	/**
	 * Relate ieIns to methodIns through the hasInferenceEngine property If
	 * ieIns is null, removes previously set relation
	 * 
	 * @param methodIns
	 * @param ieIns
	 */
	public void setInferenceEngine(OntClass methodIns, Individual ieIns) {
		if (methodIns != null) {
			Individual prev = getInferenceEngine(methodIns);
			if ((prev == null && ieIns != null)
					|| (prev != null && !prev.equals(ieIns))) {
				OntModel methodOntModel = methodIns.getOntModel();
				OntProperty hasIE = methodOntModel
						.getOntProperty(WDO_Metamodel.HAS_INFERENCE_ENGINE_URI);
				methodIns.setPropertyValue(hasIE, ieIns);
				workspace.setModified(workspace.getOntModelURI(methodOntModel),
						true);
			}
		}
	}

	/**
	 * Get Inference Engine instance related to methodIns through property
	 * hasInferenceEngine
	 * 
	 * @param methodIns
	 * @return
	 */
	public static Individual getInferenceEngine(OntClass methodIns) {
		Individual ans = null;
		if (methodIns != null) {
			RDFNode temp = getRelatedRDFNode(methodIns,
					WDO_Metamodel.HAS_INFERENCE_ENGINE_URI);
			if (temp != null) {
				ans = temp.as(Individual.class);
			}
		}
		return ans;
	}

	/**
	 * Relate srcIns to methodIns through pmlp:hasSource property If srcIns is
	 * null, removes previously set relation
	 * 
	 * @param methodIns
	 * @param srcIns
	 */
	public void setPMLSource(OntClass methodIns, Individual srcIns) {
		if (methodIns != null) {
			Individual prev = getPMLSource(methodIns);
			if ((prev == null && srcIns != null)
					|| (prev != null && !prev.equals(srcIns))) {
				OntModel methodOntModel = methodIns.getOntModel();
				OntProperty hasSrc = methodOntModel
						.getOntProperty(WDO_Metamodel.PML_HAS_SOURCE_URI);
				methodIns.setPropertyValue(hasSrc, srcIns);
				workspace.setModified(workspace.getOntModelURI(methodOntModel),
						true);
			}
		}
	}

	/**
	 * Get pml source instance related to methodIns through property
	 * pmlp:hasSource
	 * 
	 * @param methodIns
	 * @return
	 */
	public static Individual getPMLSource(OntClass methodIns) {
		Individual ans = null;
		if (methodIns != null) {
			RDFNode temp = getRelatedRDFNode(methodIns,
					WDO_Metamodel.PML_HAS_SOURCE_URI);
			if (temp != null) {
				ans = temp.as(Individual.class);
			}
		}
		return ans;
	}

	/**
	 * Relate methodIns to sawIns through isDetailedBy property A method
	 * instance can only be detailed by one SAW instance
	 * 
	 * @param methodIns
	 * @param sawIns
	 */
	public void setDetailedBy(OntClass methodIns, OntClass sawIns) {
		if (methodIns != null && sawIns != null) {
			OntModel methodOntModel = methodIns.getOntModel();
			Ontology methodOnt = workspace.getFirstOntology(methodOntModel);

			OntModel sawOntModel = sawIns.getOntModel();
			Ontology sawOnt = workspace.getFirstOntology(sawOntModel);

			Ontology prev = getDetailedBy(methodIns);
			if ((prev == null && sawOnt != null)
					|| (prev != null && !(prev.getURI())
							.equals(sawOnt.getURI()))) {
				methodOnt.addImport(sawOnt);
				methodOntModel.addSubModel(sawOntModel, true);

				OntProperty detailedBy = methodOntModel
						.getOntProperty(WDO_Metamodel.IS_DETAILED_BY_URI);
				methodIns.setPropertyValue(detailedBy, sawOnt);
				workspace.setModified(workspace.getOntModelURI(methodOntModel),
						true);
			}
		}
	}

	/**
	 * Remove relation methodInd -> detailed by -> sawInd
	 * 
	 * @param methodInd
	 */
	public void removeDetailedBy(OntClass methodInd) {
		if (methodInd != null) {
			OntModel methodOntModel = methodInd.getOntModel();
			Ontology methodOnt = workspace.getFirstOntology(methodOntModel);

			Ontology sawOnt = getDetailedBy(methodInd);
			if (sawOnt != null) {
				OntProperty detailedBy = methodOntModel
						.getOntProperty(WDO_Metamodel.IS_DETAILED_BY_URI);
				methodInd.setPropertyValue(detailedBy, null);

				OntModel sawOntModel = sawOnt.getOntModel();
				methodOnt.removeImport(sawOnt);
				methodOntModel.removeSubModel(sawOntModel, true);
				workspace.setModified(workspace.getOntModelURI(methodOntModel),
						true);
			}
		}
	}

	/**
	 * 
	 * @param methodInd
	 * @return
	 */
	public static Ontology getDetailedBy(OntClass methodInd) {
		Ontology ans = null;
		if (methodInd != null) {
			RDFNode temp = getRelatedRDFNode(methodInd,
					WDO_Metamodel.IS_DETAILED_BY_URI);
			if (temp != null) {
				ans = temp.as(Ontology.class);
			}
		}
		return ans;
	}

	/**
	 * Lists the URIs of SAWs that are needed as reference for the detailedBy
	 * relations included in the specified Saw
	 * 
	 * @param saw
	 * @return
	 */
	public Iterator<String> listDetailedByDependentSawURIs(OntModel saw) {
		ArrayList<String> detailedByModels = new ArrayList<String>();
		if (saw != null) {
			for (Iterator<OntClass> iter = this.listMethodSAWInstances(saw); iter
					.hasNext();) {
				Ontology ont = null;
				OntClass ins = iter.next();
				try {
					ont = getDetailedBy(ins);
				} catch (Exception ex) {
					System.out.println(ex.getMessage() + "\n"
							+ ex.getStackTrace().toString());
					this.workspace.getBaseWDO().addSubModel(saw, true);
					try {
						ont = getDetailedBy(ins);
					} catch (Exception ex2) {
						System.out.println("failed again: " + ex2.getMessage()
								+ "\n" + ex2.getStackTrace().toString());
					}
				}
				if (ont != null) {
					String ontURI = ont.getURI();
					if (!detailedByModels.contains(ontURI)) {
						detailedByModels.add(ontURI);
					}
				}
			}
		}
		return detailedByModels.iterator();
	}

	// /**
	// * Set an Saw individual to be abstracted by a given method individual.
	// * An Saw can be abstracted by several method instances.
	// * @param mbwInd
	// * @param methodInd
	// */
	// public static void setAbstractedBy(Individual mbwInd, Individual
	// methodInd) {
	// if (mbwInd != null && methodInd != null) {
	// OntModel mbwOntModel = mbwInd.getOntModel();
	// Ontology mbwOnt = workspace.getFirstOntology(mbwOntModel);
	// // ExtendedIterator iter = mbwOntModel.listOntologies();
	// // Ontology mbwOnt = (Ontology) iter.next();
	//
	// OntModel methodOntModel = methodInd.getOntModel();
	// Ontology methodOnt = workspace.getFirstOntology(methodOntModel);
	// // iter = methodOntModel.listOntologies();
	// // Ontology methodOnt = (Ontology) iter.next();
	//
	// mbwOnt.addImport(methodOnt);
	//
	// OntProperty abstractedBy =
	// mbwOntModel.getOntProperty(MBW_UL.ABSTRACTED_BY_URI);
	// mbwInd.addProperty(abstractedBy, methodInd);
	// workspace.setModified(workspace.getOntModelURI(mbwOntModel), true);
	// }
	// }
	//
	// public static Individual getAbstractedBy(Individual mbwInd) {
	// Individual ans = null;
	// if (mbwInd != null) {
	// RDFNode temp = getRelatedRDFNode(mbwInd, MBW_UL.ABSTRACTED_BY_URI);
	// if (temp != null) {
	// ans = (Individual) temp.as(Individual.class);
	// }
	// }
	// return ans;
	// }

	private static int getMaxSAWInstanceIndex(OntModel saw, OntClass type) {
		int maxIndex = -1;
		if (saw != null && type != null && !type.isAnon()) {
			String typeLocalName = URI.getLocalName(type.getURI());

			for (ExtendedIterator<OntClass> i = saw.listNamedClasses(); i
					.hasNext();) {
				OntClass cls = i.next();
				if (saw.isInBaseModel(cls) && cls.hasSuperClass(type)) {
					String clsuri = cls.getURI();
					int idx = clsuri.indexOf(typeLocalName + "-");
					if (idx >= 0) {
						Integer instanceNum = Integer.parseInt(clsuri
								.substring(idx + typeLocalName.length() + 1));
						if (instanceNum != null && instanceNum > maxIndex) {
							maxIndex = instanceNum;
						}
					}
				}
			}
			// for (ExtendedIterator<OntClass> i = type.listSubClasses(true);
			// i.hasNext(); ) {
			// OntClass cls = i.next();
			// if (!cls.isAnon() && saw.isInBaseModel(cls)) {
			// String clsuri = cls.getURI();
			// int idx = clsuri.indexOf(typeLocalName + "-");
			// if (idx >= 0) {
			// Integer instanceNum = Integer.parseInt(clsuri.substring(idx +
			// typeLocalName.length() + 1));
			// if (instanceNum != null && instanceNum > maxIndex) {
			// maxIndex = instanceNum;
			// }
			// }
			// }
			// }
		}
		return maxIndex;
	}

	private static int getMaxOWLInstanceIndex(OntModel saw, OntClass type) {
		int maxIndex = -1;
		if (saw != null && type != null && !type.isAnon()) {
			String typeLocalName = URI.getLocalName(type.getURI());
			for (ExtendedIterator<Individual> i = saw.listIndividuals(type); i
					.hasNext();) {
				Individual ins = i.next();
				if (!ins.isAnon() && saw.isInBaseModel(ins)) {
					String insuri = ins.getURI();
					int idx = insuri.indexOf(typeLocalName + "-");
					if (idx >= 0) {
						Integer instanceNum = Integer.parseInt(insuri
								.substring(idx + typeLocalName.length() + 1));
						if (instanceNum != null && instanceNum > maxIndex) {
							maxIndex = instanceNum;
						}
					}
				}
			}
		}
		return maxIndex;
	}

	/**
	 * Creates an individual in saw, and with the type and uri specified. If uri
	 * is null, assigns URI based on the URI of the SAW and the local name of
	 * the type
	 * 
	 * @param saw
	 *            The OntModel on which to create the individual
	 * @param type
	 *            The OntClass representing the type of the new individual
	 * @param uri
	 *            The URI to assign to the new individual
	 * @return The new individual
	 */
	private Individual createOWLInstance(OntModel saw, OntClass type, String uri) {
		Individual ins = null;
		if (saw != null && type != null) {
			if (uri == null) {
				String sawURI = workspace.getOntModelURI(saw);
				String typeLocalName = URI.getLocalName(type.getURI());
				int instanceIdx = getMaxOWLInstanceIndex(saw, type) + 1;
				uri = sawURI + "#" + typeLocalName + "-" + instanceIdx;
			}
			ins = saw.createIndividual(uri, type);
			workspace.setModified(workspace.getOntModelURI(saw), true);
		}
		return ins;
	}

	/**
	 * Creates an SAW instance of the type specified and with the given uri. If
	 * uri is null, assigns a URI based on the URI of the SAW and the localname
	 * of the type
	 * 
	 * @param saw
	 *            The OntModel on which to create the instance
	 * @param type
	 *            The OntClass representing the type of the new instance
	 * @param uri
	 *            The URI to assign to the new instance
	 * @return The new instance
	 */
	private OntClass createSAWInstance(OntModel saw, OntClass type, String uri,
			boolean defaultOWLInstance) {
		OntClass ins = null;
		if (saw != null && type != null && !type.isAnon()) {
			if (uri == null) {
				String sawURI = workspace.getOntModelURI(saw);
				String typeLocalName = URI.getLocalName(type.getURI());
				int instanceIdx = getMaxSAWInstanceIndex(saw, type) + 1;
				uri = sawURI + "#" + typeLocalName + "-" + instanceIdx;
			}
			ins = saw.createClass(uri);
			// Property subClassOf =
			// saw.getProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");
			// if (subClassOf == null) System.out.println("property is null");
			// saw.add(ins, subClassOf, type);
			ins.setSuperClass(type);
			if (defaultOWLInstance) {
				Individual owlins = createOWLInstance(saw, ins, uri
						+ "-default");
				OntProperty hasDefaultInstance = saw
						.getOntProperty(WDO_Metamodel.HAS_DEFAULT_INSTANCE_URI);
				ins.setPropertyValue(hasDefaultInstance, owlins);

			}
			workspace.setModified(workspace.getOntModelURI(saw), true);
		}
		return ins;
	}

	public Individual getDefaultOWLInstance(OntClass ins) {
		Individual ans = null;
		if (ins != null) {
			RDFNode temp = getRelatedRDFNode(ins,
					WDO_Metamodel.HAS_DEFAULT_INSTANCE_URI);
			if (temp != null) {
				ans = temp.as(Individual.class);
			}
		}
		return ans;
	}

	/**
	 * Create an instance in saw, with nodeType as its type, uri as its URI, and
	 * relate the new instance to a Coordinate instance with the coordinates
	 * specified by x and y.
	 * 
	 * @param saw
	 *            The OntModel where to create the new instance
	 * @param nodeType
	 *            the OntClass representing the type of the new instance,
	 *            intended to be a subclass of wdo:Method or pmlp:Source
	 * @param uri
	 *            The URI to assign to the new instance. If null, a URI is
	 *            generated automatically.
	 * @param coord
	 *            The value of the coordinate to assign to the new instance
	 * @return The new instance created
	 */
	public OntClass createNodeSAWInstance(OntModel saw, OntClass nodeType,
			String uri, Point coord) {
		OntClass ins = null;
		if (coord != null) {
			ins = createSAWInstance(saw, nodeType, uri, true);
			if (ins != null) {
				setInstanceCoordinate(ins, coord);
			}
		}
		return ins;
	}

	/**
	 * Create a pmlp:Source SAW instance
	 * 
	 * @param saw
	 * @param uri
	 * @param coord
	 * @return
	 */
	public OntClass createSourceSAWInstance(OntModel saw, String uri,
			Point coord) {
		OntClass ins = null;
		if (saw != null) {
			OntClass type = saw.getOntClass(WDO_Metamodel.SOURCE_URI);
			ins = createNodeSAWInstance(saw, type, uri, coord);
		}
		return ins;
	}

	/**
	 * Create pmlp:Source OWL instance
	 * 
	 * @param saw
	 * @param uri
	 * @return
	 */
	public Individual createSourceOWLInstance(OntModel saw, String uri) {
		Individual ins = null;
		if (saw != null && uri != null && !uri.isEmpty()) {
			OntClass type = saw.getOntClass(WDO_Metamodel.SOURCE_URI);
			ins = this.createOWLInstance(saw, type, uri);
		}
		return ins;
	}

	/**
	 * Create pmlp:InferenceEngine instance
	 * 
	 * @param saw
	 * @param uri
	 * @return
	 */
	public Individual createInferenceEngineIndividual(OntModel saw, String uri) {
		Individual ins = null;
		if (saw != null && uri != null && !uri.isEmpty()) {
			OntClass ieCls = saw
					.getOntClass(WDO_Metamodel.INFERENCE_ENGINE_URI);
			ins = this.createOWLInstance(saw, ieCls, uri);
		}
		return ins;
	}

	/**
	 * Create a pmlp:Format instance
	 * 
	 * @param saw
	 * @param uri
	 * @return
	 */
	public Individual createFormatIndividual(OntModel saw, String uri) {
		Individual ins = null;
		if (saw != null && uri != null && !uri.isEmpty()) {
			OntClass formatCls = saw.getOntClass(WDO_Metamodel.FORMAT_URI);
			ins = this.createOWLInstance(saw, formatCls, uri);
		}
		return ins;
	}

	/**
	 * Create SAW instance of type dataType, uri as its URI, and specified
	 * instances as input and output.
	 * 
	 * @param saw
	 * @param dataType
	 * @param uri
	 * @param fromMethod
	 *            If null, a new pmlp:Source instance is placed instead
	 * @param toMethod
	 *            If null, a pmlp:Source instance is placed instead
	 * @param fromCoord
	 *            Coordinate assigned to pmlp:Source instance created if
	 *            fromMethod was not provided
	 * @param toCoord
	 *            Coordinate assigned to pmlp:Source instance created if
	 *            toMethod was not provided
	 * @return New SAW instance
	 */
	public OntClass createDataIndividual(OntModel saw, OntClass dataType,
			String uri, OntClass fromMethod, OntClass toMethod,
			Point fromCoord, Point toCoord) {
		OntClass dataIns = createSAWInstance(saw, dataType, uri, true);
		if (dataIns != null) {
			// set from instance for new data individual
			// if fromMethod individual not provided, place new pmlp:Source
			// instance
			OntClass fromIns = (fromMethod != null) ? fromMethod
					: createSourceSAWInstance(saw, null, fromCoord);
			setIsOutputOf(saw, dataIns, fromIns);
			// set isInputTo instance for new data instance
			// if toMethod instance not provided, place new pmlp:Source instance
			OntClass toIns = (toMethod != null) ? toMethod
					: createSourceSAWInstance(saw, null, toCoord);
			setIsInputTo(saw, dataIns, toIns);
		}
		return dataIns;
	}

	/**
	 * Create relation dataIns -> IsOutputOf -> methodIns in saw Replaces any
	 * existing IsOutputOf relations that dataIns may have It also creates the
	 * inverse relation, methodIns -> Outputs -> dataIns
	 * 
	 * @param saw
	 * @param dataIns
	 * @param methodIns
	 */
	public void setIsOutputOf(OntModel saw, OntClass dataIns, OntClass methodIns) {
		if (dataIns != null && methodIns != null) {
			if (saw == null) {
				saw = dataIns.getOntModel();
			}
			OntProperty isOutputOf = saw
					.getOntProperty(WDO_Metamodel.IS_OUTPUT_OF_URI);
			OntProperty hasOutput = saw
					.getOntProperty(WDO_Metamodel.HAS_OUTPUT_URI);
			dataIns.setPropertyValue(isOutputOf, methodIns);
			methodIns.addProperty(hasOutput, dataIns);
			workspace.setModified(workspace.getOntModelURI(saw), true);
		}
	}

	/**
	 * Remove relation dataIns -> IsOutputOf -> methodIns in saw It also removes
	 * the inverse relation, methodIns -> Outputs -> dataIns
	 * 
	 * @param saw
	 * @param dataIns
	 * @param methodIns
	 */
	public void removeIsOutputOf(OntModel saw, OntClass dataIns,
			OntClass methodIns) {
		if (dataIns != null && methodIns != null) {
			if (saw == null) {
				saw = dataIns.getOntModel();
			}
			if (methodIns.equals(getIsOutputOf(dataIns))) {
				OntProperty isOutputOf = saw
						.getOntProperty(WDO_Metamodel.IS_OUTPUT_OF_URI);
				OntProperty hasOutput = saw
						.getOntProperty(WDO_Metamodel.HAS_OUTPUT_URI);
				dataIns.removeProperty(isOutputOf, methodIns);
				methodIns.removeProperty(hasOutput, dataIns);
				workspace.setModified(workspace.getOntModelURI(saw), true);
			}
		}
	}

	/**
	 * Get instance related to dataIns through property wdo:isOutputOf
	 * 
	 * @param dataIns
	 * @return
	 */
	public static OntClass getIsOutputOf(OntClass dataIns) {
		OntClass methodIns = null;
		if (dataIns != null) {
			RDFNode temp = getRelatedRDFNode(dataIns,
					WDO_Metamodel.IS_OUTPUT_OF_URI);
			if (temp != null) {
				methodIns = temp.as(OntClass.class);
			}
		}
		return methodIns;
	}

	/**
	 * 
	 * @param saw
	 * @param methodIns
	 * @param dataIns
	 */
	public void addHasOutput(OntModel saw, OntClass methodIns, OntClass dataIns) {
		setIsOutputOf(saw, dataIns, methodIns);
	}

	/**
	 * 
	 * @param saw
	 * @param methodIns
	 * @param dataIns
	 */
	public void removeHasOutput(OntModel saw, OntClass methodIns,
			OntClass dataIns) {
		removeIsOutputOf(saw, dataIns, methodIns);
	}

	/**
	 * 
	 * @param methodIns
	 * @return
	 */
	public static OntClass[] listHasOutput(OntClass methodIns) {
		OntClass dataIns[] = null;
		if (methodIns != null) {
			RDFNode temp[] = listRelatedRDFNodes(methodIns,
					WDO_Metamodel.HAS_OUTPUT_URI);
			if (temp != null) {
				dataIns = new OntClass[temp.length];
				for (int i = 0; i < temp.length; i++) {
					dataIns[i] = temp[i].as(OntClass.class);
				}
			}
		}
		return dataIns;
	}

	/**
	 * 
	 * @param saw
	 * @param dataIns
	 * @param methodIns
	 */
	public void setIsInputTo(OntModel saw, OntClass dataIns, OntClass methodIns) {
		if (dataIns != null && methodIns != null) {
			if (saw == null) {
				saw = dataIns.getOntModel();
			}
			OntProperty isInputTo = saw
					.getOntProperty(WDO_Metamodel.IS_INPUT_TO_URI);
			OntProperty hasInput = saw
					.getOntProperty(WDO_Metamodel.HAS_INPUT_URI);
			dataIns.setPropertyValue(isInputTo, methodIns);
			methodIns.addProperty(hasInput, dataIns);
			workspace.setModified(workspace.getOntModelURI(saw), true);
		}
	}

	/**
	 * 
	 * @param saw
	 * @param dataIns
	 * @param methodIns
	 */
	public void removeIsInputTo(OntModel saw, OntClass dataIns,
			OntClass methodIns) {
		if (dataIns != null && methodIns != null) {
			if (saw == null) {
				saw = dataIns.getOntModel();
			}
			if (methodIns.equals(getIsInputTo(dataIns))) {
				OntProperty isInputTo = saw
						.getOntProperty(WDO_Metamodel.IS_INPUT_TO_URI);
				OntProperty hasInput = saw
						.getOntProperty(WDO_Metamodel.HAS_INPUT_URI);
				dataIns.removeProperty(isInputTo, methodIns);
				methodIns.removeProperty(hasInput, dataIns);
			}
			workspace.setModified(workspace.getOntModelURI(saw), true);
		}
	}

	/**
	 * Get instance related to dataIns through property wdo:isInputTo
	 * 
	 * @param dataIns
	 * @return
	 */
	public static OntClass getIsInputTo(OntClass dataIns) {
		OntClass methodIns = null;
		if (dataIns != null) {
			RDFNode temp = getRelatedRDFNode(dataIns,
					WDO_Metamodel.IS_INPUT_TO_URI);
			if (temp != null) {
				methodIns = temp.as(OntClass.class);
			}
		}
		return methodIns;
	}

	/**
	 * 
	 * @param saw
	 * @param methodIns
	 * @param dataIns
	 */
	public void addHasInput(OntModel saw, OntClass methodIns, OntClass dataIns) {
		setIsInputTo(saw, dataIns, methodIns);
	}

	/**
	 * 
	 * @param saw
	 * @param methodIns
	 * @param dataIns
	 */
	public void removeHasInput(OntModel saw, OntClass methodIns,
			OntClass dataIns) {
		removeIsInputTo(saw, dataIns, methodIns);
	}

	/**
	 * 
	 * @param methodIns
	 * @return
	 */
	public static OntClass[] listHasInput(OntClass methodIns) {
		OntClass[] dataIns = null;
		if (methodIns != null) {
			RDFNode[] temp = listRelatedRDFNodes(methodIns,
					WDO_Metamodel.HAS_INPUT_URI);
			if (temp != null) {
				dataIns = new OntClass[temp.length];
				for (int i = 0; i < temp.length; i++) {
					dataIns[i] = temp[i].as(OntClass.class);
				}
			}
		}
		return dataIns;
	}

	/**
	 * Return the number of inputs connected to the specified method
	 * 
	 * @param methodIns
	 * @return
	 */
	public static int getHasInputCardinality(OntClass methodIns) {
		OntClass[] data = listHasInput(methodIns);
		return (data == null) ? 0 : data.length;
	}

	/**
	 * Return the numner of outputs connected to the specified method
	 * 
	 * @param methodIns
	 * @return
	 */
	public static int getHasOutputCardinality(OntClass methodIns) {
		OntClass[] data = listHasOutput(methodIns);
		return (data == null) ? 0 : data.length;
	}

	/**
	 * Return the number of inputs and outputs connected to the specified method
	 * 
	 * @param methodIns
	 * @return
	 */
	public static int getInputOutputCardinality(OntClass methodIns) {
		return getHasInputCardinality(methodIns)
				+ getHasOutputCardinality(methodIns);
	}

	/**
	 * 
	 * @param saw
	 * @param ins
	 */
	public void removeInstance(OntModel saw, OntClass ins) {
		if (ins != null) {
			if (saw == null) {
				saw = ins.getOntModel();
			}
			// remove relations of wdo:Data type individuals
			if (isDataType(ins)) {
				OntClass inputToMethodIns = SAW.getIsInputTo(ins);
				removeIsInputTo(saw, ins, inputToMethodIns);
				// if it is a sink that has no other data connections, remove it
				if (isPMLSourceType(inputToMethodIns)
						&& getInputOutputCardinality(inputToMethodIns) == 0) {
					removeInstance(saw, inputToMethodIns);
				}
				OntClass outputOfMethodIns = SAW.getIsOutputOf(ins);
				removeIsOutputOf(saw, ins, outputOfMethodIns);
				// if it is a source that has no other data connections, remove
				// it
				if (isPMLSourceType(outputOfMethodIns)
						&& getInputOutputCardinality(outputOfMethodIns) == 0) {
					removeInstance(saw, outputOfMethodIns);
				}
			}
			// remove relations of wdo:Method and pmlp:Source type individuals
			else if (isMethodType(ins) || isPMLSourceType(ins)) {
				OntClass[] inputFromDataIns = SAW.listHasInput(ins);
				if (inputFromDataIns != null) {
					for (int i = 0; i < inputFromDataIns.length; i++) {
						removeHasInput(saw, ins, inputFromDataIns[i]);
					}
				}
				OntClass[] outputsDataIns = SAW.listHasOutput(ins);
				if (outputsDataIns != null) {
					for (int i = 0; i < outputsDataIns.length; i++) {
						removeHasOutput(saw, ins, outputsDataIns[i]);
					}
				}
				// remove Coordinates
				SAW.removeCoordinateInstance(ins);
			}
			// remove default owl instance
			Individual ind = this.getDefaultOWLInstance(ins);
			if (ind != null) {
				ind.remove();
			}
			// once all relations have been removed, remove individual
			ins.remove();
			// set the SAW as modified in the workspace
			workspace.setModified(workspace.getOntModelURI(saw), true);
		}
	}
}
