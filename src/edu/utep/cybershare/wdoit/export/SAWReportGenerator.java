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

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.*;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

import edu.utep.cybershare.wdoapi.SAW;
import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoit.context.State;

/**
 * @author Leonardo Salayandia
 * 
 */
public class SAWReportGenerator {
	private static final String WORKFLOW_URI_MARKER = "<!--WorkflowURI-->";
	private static final String WORKFLOW_COMMENT_MARKER = "<!--WorkflowComment-->";
	private static final String WORKFLOW_GRAPH_IMAGE_MARKER = "<!--WorkflowGraphImage-->";

	private static final String IMPORTED_ONTOLOGIES_MARKER = "<!--ImportedOntologies-->";
	private static final String IMPORTED_ONTOLOGY_REGION_BEGIN_MARKER = "<!--ImportedOntologyRegionBegins-->";
	private static final String IMPORTED_ONTOLOGY_REGION_END_MARKER = "<!--ImportedOntologyRegionEnds-->";
	private static final String IMPORTED_ONTOLOGY_NAME_MARKER = "<!--ImportedOntologyName-->";

	private static final String DATA_MARKER = "<!--DataConcepts-->";
	private static final String METHODS_MARKER = "<!--MethodConcepts-->";
	private static final String CONCEPT_REGION_BEGIN_MARKER = "<!--ConceptRegionBegins-->";
	private static final String CONCEPT_REGION_END_MARKER = "<!--ConceptRegionEnds-->";
	private static final String CONCEPT_NAME_MARKER = "<!--ConceptName-->";
	private static final String CONCEPT_COMMENT_MARKER = "<!--ConceptComment-->";

	private static final String REPORT_INDEX_FILENAME_MARKER = "<!--ReportIndexFilename-->";

	private String reportTemplate;
	private String ontologyTemplate;
	private String conceptTemplate;

	/**
	 * Initialize the SAWReportGenerator by providing the template to use. The
	 * template should include the markers declared in this class as static
	 * variables.
	 * 
	 * @param templateText
	 *            The template text to generate the report
	 * @throws Exception
	 */
	public SAWReportGenerator(String templateText) throws Exception {

		// Extract imported ontology region from template
		int beginRegion = templateText
				.indexOf(IMPORTED_ONTOLOGY_REGION_BEGIN_MARKER);
		int endRegion = templateText
				.indexOf(IMPORTED_ONTOLOGY_REGION_END_MARKER)
				+ IMPORTED_ONTOLOGY_REGION_END_MARKER.length();
		if (beginRegion != -1 && endRegion != -1) {
			ontologyTemplate = templateText.substring(beginRegion, endRegion);
			ontologyTemplate = ontologyTemplate.replaceAll(
					IMPORTED_ONTOLOGY_REGION_BEGIN_MARKER, "");
			ontologyTemplate = ontologyTemplate.replaceAll(
					IMPORTED_ONTOLOGY_REGION_END_MARKER, "");

			templateText = templateText.substring(0, beginRegion)
					+ templateText.substring(endRegion, templateText.length());
		} else {
			throw new Exception(
					"Imported Ontology Region not defined in template");
		}

		// Extract concept region from template
		beginRegion = templateText.indexOf(CONCEPT_REGION_BEGIN_MARKER);
		endRegion = templateText.indexOf(CONCEPT_REGION_END_MARKER)
				+ CONCEPT_REGION_END_MARKER.length();
		if (beginRegion != -1 && endRegion != -1) {
			conceptTemplate = templateText.substring(beginRegion, endRegion);
			conceptTemplate = conceptTemplate.replaceAll(
					CONCEPT_REGION_BEGIN_MARKER, "");
			conceptTemplate = conceptTemplate.replaceAll(
					CONCEPT_REGION_END_MARKER, "");

			templateText = templateText.substring(0, beginRegion)
					+ templateText.substring(endRegion, templateText.length());
		} else {
			throw new Exception("Concept Region not defined in template");
		}

		reportTemplate = templateText;
	}

	/**
	 * Generates a report for the specified Saw
	 * 
	 * @param wdo
	 *            Wdo for which to generate the report
	 * @param graphFilename
	 *            Name of image file for workflow graph
	 * @return The report as a string
	 */
	@SuppressWarnings("unchecked")
	public String GenerateReport(OntModel workflow, String graphFilename,
			String reportIndexFilename) {
		String report = null;
		if (workflow != null) {
			State state = State.getInstance();

			// fill ontology uri
			Matcher reportMatcher = Pattern.compile(WORKFLOW_URI_MARKER)
					.matcher(reportTemplate);
			report = reportMatcher.replaceAll(state.getOntModelURI(workflow));

			// fill ontology comment
			String workflowComment = state.getOWLDocumentComment(workflow);
			if (workflowComment == null) {
				workflowComment = "";
			}
			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern.compile(WORKFLOW_COMMENT_MARKER));
			report = reportMatcher.replaceAll(workflowComment);

			// fill workflow graph image name
			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern
					.compile(WORKFLOW_GRAPH_IMAGE_MARKER));
			report = reportMatcher.replaceAll(graphFilename);

			// fill report index filename
			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern
					.compile(REPORT_INDEX_FILENAME_MARKER));
			report = reportMatcher.replaceAll(reportIndexFilename);

			// process imported ontologies
			String importedOntologies = "";
			for (Iterator i = workflow.listImportedOntologyURIs().iterator(); i
					.hasNext();) {
				String importedOntologyURI = (String) i.next();
				String current = ontologyTemplate.substring(0); // get a new
																// copy of the
																// ontology
																// template

				// set name of current imported ontology
				Matcher ontologyMatcher = Pattern.compile(
						IMPORTED_ONTOLOGY_NAME_MARKER).matcher(current);
				current = ontologyMatcher.replaceAll(importedOntologyURI);

				importedOntologies = importedOntologies + current;
			}

			// process data concepts
			String dataConcepts = "";
			HashMap<String, OntClass> dataOntClasses = new HashMap<String, OntClass>();
			for (Iterator<OntClass> i = state.listDataIndividuals(workflow); i
					.hasNext();) {
				OntClass ind = i.next();
				OntClass indType = SAW.getSAWInstanceType(ind);
				if (!indType.isAnon()) {
					dataOntClasses.put(indType.getURI(), indType);
				}
			}
			for (Iterator<OntClass> i = dataOntClasses.values().iterator(); i
					.hasNext();) {
				OntClass cls = i.next();
				String current = conceptTemplate.substring(0); // get a new copy
																// of the
																// concept
																// template

				// set name of current concept
				String conceptName = WDO.getClassQName(cls);
				Matcher conceptMatcher = Pattern.compile(CONCEPT_NAME_MARKER)
						.matcher(current);
				current = conceptMatcher.replaceAll(conceptName);

				// set comment of current concept
				String conceptComment = WDO.getClassComment(cls);
				if (conceptComment == null) {
					conceptComment = "";
				}
				conceptMatcher.reset(current);
				conceptMatcher.usePattern(Pattern
						.compile(CONCEPT_COMMENT_MARKER));
				current = conceptMatcher.replaceAll(conceptComment);

				dataConcepts = dataConcepts + current;
			}

			// process method concepts
			String methodConcepts = "";
			HashMap<String, OntClass> methodOntClasses = new HashMap<String, OntClass>();
			for (Iterator<OntClass> i = state.listMethodIndividuals(workflow); i
					.hasNext();) {
				OntClass ind = i.next();
				OntClass indType = SAW.getSAWInstanceType(ind);
				if (!indType.isAnon()) {
					methodOntClasses.put(indType.getURI(), indType);
				}
			}
			for (Iterator<OntClass> i = methodOntClasses.values().iterator(); i
					.hasNext();) {
				OntClass cls = i.next();
				String current = conceptTemplate.substring(0); // get a new copy
																// of the
																// concept
																// template

				// set name of current concept
				String conceptName = WDO.getClassQName(cls);
				Matcher conceptMatcher = Pattern.compile(CONCEPT_NAME_MARKER)
						.matcher(current);
				current = conceptMatcher.replaceAll(conceptName);

				// set comment of current concept
				String conceptComment = WDO.getClassComment(cls);
				if (conceptComment == null) {
					conceptComment = "";
				}
				conceptMatcher.reset(current);
				conceptMatcher.usePattern(Pattern
						.compile(CONCEPT_COMMENT_MARKER));
				current = conceptMatcher.replaceAll(conceptComment);

				methodConcepts = methodConcepts + current;
			}

			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern
					.compile(IMPORTED_ONTOLOGIES_MARKER));
			report = reportMatcher.replaceAll(importedOntologies);

			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern.compile(DATA_MARKER));
			report = reportMatcher.replaceAll(dataConcepts);

			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern.compile(METHODS_MARKER));
			report = reportMatcher.replaceAll(methodConcepts);
		}

		return report;
	}
}
