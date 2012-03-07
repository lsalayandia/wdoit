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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.*;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.utep.cybershare.wdoapi.WDO;
import edu.utep.cybershare.wdoit.context.State;

/**
 * @author Leonardo Salayandia
 * 
 */
public class WDOReportGenerator {
	private static final String ONTOLOGY_URI_MARKER = "<!--OntologyURI-->";
	private static final String ONTOLOGY_COMMENT_MARKER = "<!--OntologyComment-->";

	private static final String DATA_MARKER = "<!--DataConcepts-->";
	private static final String METHODS_MARKER = "<!--MethodConcepts-->";
	private static final String ROLES_MARKER = "<!--Roles-->";

	private static final String CONCEPT_REGION_BEGIN_MARKER = "<!--ConceptRegionBegins-->";
	private static final String CONCEPT_REGION_END_MARKER = "<!--ConceptRegionEnds-->";
	private static final String CONCEPT_NAME_MARKER = "<!--ConceptName-->";
	private static final String CONCEPT_COMMENT_MARKER = "<!--ConceptComment-->";
	private static final String CONCEPT_CHILD_REGION_BEGIN_MARKER = "<!--ConceptChildrenRegionBegins-->";
	private static final String CONCEPT_CHILD_REGION_END_MARKER = "<!--ConceptChildrenRegionEnds-->";
	private static final String CONCEPT_PARENT_REGION_BEGIN_MARKER = "<!--ConceptParentsRegionBegins-->";
	private static final String CONCEPT_PARENT_REGION_END_MARKER = "<!--ConceptParentsRegionEnds-->";
	private static final String CONCEPT_RELATIONSHIP_REGION_BEGIN_MARKER = "<!--ConceptRelationshipsRegionBegins-->";
	private static final String CONCEPT_RELATIONSHIP_REGION_END_MARKER = "<!--ConceptRelationshipsRegionEnds-->";

	// NOTE: this version of Wdo-It! assumes only input/ouput roles are
	// possible, and therefore, does not include role section in report.
	private static final String ROLE_REGION_BEGIN_MARKER = "<!--RoleRegionBegins-->";
	private static final String ROLE_REGION_END_MARKER = "<!--RoleRegionEnds-->";
	private static final String ROLE_NAME_MARKER = "<!--RoleName-->";
	// private static final String ROLE_COMMENT_MARKER = "<!--RoleComment-->";

	private static final String REPORT_INDEX_FILENAME_MARKER = "<!--ReportIndexFilename-->";

	private String reportTemplate;
	private String conceptTemplate;
	private String roleTemplate;

	/**
	 * Initialize the WDOReportGenerator by providing the template text to use.
	 * The template should include the markers declared in this class as static
	 * variables.
	 * 
	 * @param templateText
	 *            The template to use to gerenerate the report
	 * @throws Exception
	 */
	public WDOReportGenerator(String templateText) throws Exception {

		// Extract concept region from template
		int beginRegion = templateText.indexOf(CONCEPT_REGION_BEGIN_MARKER);
		int endRegion = templateText.indexOf(CONCEPT_REGION_END_MARKER)
				+ CONCEPT_REGION_END_MARKER.length();
		if (beginRegion != -1 || endRegion != -1) {
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

		// Extract role region from template
		beginRegion = templateText.indexOf(ROLE_REGION_BEGIN_MARKER);
		endRegion = templateText.indexOf(ROLE_REGION_END_MARKER)
				+ ROLE_REGION_END_MARKER.length();
		if (beginRegion != -1 || endRegion != -1) {
			roleTemplate = templateText.substring(beginRegion, endRegion);
			roleTemplate = roleTemplate
					.replaceAll(ROLE_REGION_BEGIN_MARKER, "");
			roleTemplate = roleTemplate.replaceAll(ROLE_REGION_END_MARKER, "");

			templateText = templateText.substring(0, beginRegion)
					+ templateText.substring(endRegion, templateText.length());
		} else {
			throw new Exception("Role Region not defined in template");
		}

		reportTemplate = templateText;
	}

	/**
	 * Generates a report for the specified Wdo
	 * 
	 * @param wdo
	 *            Wdo for which to generate the report
	 * @return The report as a string
	 */
	public String GenerateReport(OntModel ontmodel, String reportIndexFilename) {
		String report = null;
		if (ontmodel != null) {
			State state = State.getInstance();

			// fill ontology uri
			Matcher reportMatcher = Pattern.compile(ONTOLOGY_URI_MARKER)
					.matcher(reportTemplate);
			report = reportMatcher.replaceAll(state.getOntModelURI(ontmodel));

			// fill ontology comment
			String ontologyComment = state.getOWLDocumentComment(ontmodel);
			if (ontologyComment == null) {
				ontologyComment = "";
			}
			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern.compile(ONTOLOGY_COMMENT_MARKER));
			report = reportMatcher.replaceAll(ontologyComment);

			// fill report index filename
			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern
					.compile(REPORT_INDEX_FILENAME_MARKER));
			report = reportMatcher.replaceAll(reportIndexFilename);

			// process concepts
			ArrayList<Iterator<OntClass>> conceptLists = new ArrayList<Iterator<OntClass>>();
			conceptLists.add(state.listDataSubClasses()); // data concepts
			conceptLists.add(state.listMethodSubClasses()); // method concepts
			String dataConcepts = null;
			String methodConcepts = null;
			for (Iterator<Iterator<OntClass>> i = conceptLists.iterator(); i
					.hasNext();) {
				Iterator<OntClass> conceptList = i.next();

				String concepts = "";
				for (Iterator<OntClass> j = conceptList; j.hasNext();) {
					OntClass cls = j.next();
					if (ontmodel.isInBaseModel(cls)) { // only get the classes
														// that are defined in
														// the ontmodel being
														// processed.
						String current = conceptTemplate.substring(0); // get a
																		// new
																		// copy
																		// of
																		// the
																		// concept
																		// template

						// process children
						int beginRegion = current
								.indexOf(CONCEPT_CHILD_REGION_BEGIN_MARKER);
						int endRegion = current
								.indexOf(CONCEPT_CHILD_REGION_END_MARKER)
								+ CONCEPT_CHILD_REGION_END_MARKER.length();
						if (beginRegion != -1 || endRegion != -1) {
							String childTemplate = current.substring(
									beginRegion, endRegion);
							childTemplate = childTemplate.replaceAll(
									CONCEPT_CHILD_REGION_BEGIN_MARKER, "");
							childTemplate = childTemplate.replaceAll(
									CONCEPT_CHILD_REGION_END_MARKER, "");

							Matcher childMatcher = Pattern.compile(
									CONCEPT_NAME_MARKER).matcher(childTemplate);
							String children = "";
							for (ExtendedIterator<OntClass> k = cls
									.listSubClasses(true); k.hasNext();) { // immediate
																			// children
								OntClass child = k.next();
								if (!child.isAnon()) {
									String childName = WDO.getClassQName(child);
									children = children
											+ childMatcher
													.replaceAll(childName);
								}
							}

							current = current.substring(0, beginRegion)
									+ children
									+ current.substring(endRegion,
											current.length());
						}

						// process parents
						beginRegion = current
								.indexOf(CONCEPT_PARENT_REGION_BEGIN_MARKER);
						endRegion = current
								.indexOf(CONCEPT_PARENT_REGION_END_MARKER)
								+ CONCEPT_PARENT_REGION_END_MARKER.length();
						if (beginRegion != -1 || endRegion != -1) {
							String parentTemplate = current.substring(
									beginRegion, endRegion);
							parentTemplate = parentTemplate.replaceAll(
									CONCEPT_PARENT_REGION_BEGIN_MARKER, "");
							parentTemplate = parentTemplate.replaceAll(
									CONCEPT_PARENT_REGION_END_MARKER, "");

							Matcher parentMatcher = Pattern.compile(
									CONCEPT_NAME_MARKER)
									.matcher(parentTemplate);
							String parents = "";
							for (ExtendedIterator<OntClass> k = cls
									.listSuperClasses(true); k.hasNext();) { // immediate
																				// parents
								OntClass parent = k.next();
								if (!parent.isAnon()) {
									String parentName = WDO
											.getClassQName(parent);
									parents = parents
											+ parentMatcher
													.replaceAll(parentName);
								}
							}

							current = current.substring(0, beginRegion)
									+ parents
									+ current.substring(endRegion,
											current.length());
						}

						// process related concepts
						beginRegion = current
								.indexOf(CONCEPT_RELATIONSHIP_REGION_BEGIN_MARKER);
						endRegion = current
								.indexOf(CONCEPT_RELATIONSHIP_REGION_END_MARKER)
								+ CONCEPT_RELATIONSHIP_REGION_END_MARKER
										.length();
						if (beginRegion != -1 || endRegion != -1) {
							String relationshipsTemplate = current.substring(
									beginRegion, endRegion);
							relationshipsTemplate = relationshipsTemplate
									.replaceAll(
											CONCEPT_RELATIONSHIP_REGION_BEGIN_MARKER,
											"");
							relationshipsTemplate = relationshipsTemplate
									.replaceAll(
											CONCEPT_RELATIONSHIP_REGION_END_MARKER,
											"");

							String relationships = "";

							for (Iterator<OntClass> k = state
									.listRightClasses(cls); k.hasNext();) {
								OntClass relatedClass = k.next();
								String roleName = "";
								if (state.isDataSubClass(cls)) {
									roleName = "IS_INPUT_TO";
								} else {
									roleName = "HAS_OUTPUT";
								}
								// set related role name
								Matcher relationshipsMatcher = Pattern.compile(
										ROLE_NAME_MARKER).matcher(
										relationshipsTemplate);
								String tmp = relationshipsMatcher
										.replaceAll(roleName);
								// set related concept name
								relationshipsMatcher.reset(tmp);
								relationshipsMatcher.usePattern(Pattern
										.compile(CONCEPT_NAME_MARKER));
								tmp = relationshipsMatcher
										.replaceAll(relatedClass.toString());

								relationships = relationships + tmp;
							}

							for (Iterator<OntClass> k = state
									.listLeftClasses(cls); k.hasNext();) {
								OntClass relatedClass = k.next();
								String roleName = "";
								if (state.isDataSubClass(cls)) {
									roleName = "IS_OUTPUT_OF";
								} else {
									roleName = "HAS_INPUT";
								}
								// set related role name
								Matcher relationshipsMatcher = Pattern.compile(
										ROLE_NAME_MARKER).matcher(
										relationshipsTemplate);
								String tmp = relationshipsMatcher
										.replaceAll(roleName);
								// set related concept name
								relationshipsMatcher.reset(tmp);
								relationshipsMatcher.usePattern(Pattern
										.compile(CONCEPT_NAME_MARKER));
								tmp = relationshipsMatcher
										.replaceAll(relatedClass.toString());

								relationships = relationships + tmp;
							}

							current = current.substring(0, beginRegion)
									+ relationships
									+ current.substring(endRegion,
											current.length());
						}

						// set name of current concept
						String conceptName = WDO.getClassQName(cls);
						Matcher conceptMatcher = Pattern.compile(
								CONCEPT_NAME_MARKER).matcher(current);
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

						concepts = concepts + current;
					}
				}
				if (dataConcepts == null) {
					dataConcepts = concepts;
				} else {
					methodConcepts = concepts;
				}
			}

			// process roles
			String roles = "";
			// for (Iterator<Property> i = wdo.listProperties(); i.hasNext(); )
			// {
			// Property p = i.next();
			// String current = roleTemplate.substring(0); // get a new copy of
			// the role template
			//
			// // set name of current role
			// Matcher propertyMatcher =
			// Pattern.compile(ROLE_NAME_MARKER).matcher(current);
			// current = propertyMatcher.replaceAll(p.toString());
			//
			// // set comment of current role
			// String conceptComment = p.getComment();
			// if (conceptComment == null) {
			// conceptComment = "";
			// }
			// propertyMatcher.reset(current);
			// propertyMatcher.usePattern(Pattern.compile(ROLE_COMMENT_MARKER));
			// current = propertyMatcher.replaceAll(conceptComment);
			//
			// roles = roles + current;
			// }

			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern.compile(DATA_MARKER));
			report = reportMatcher.replaceAll(dataConcepts);

			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern.compile(METHODS_MARKER));
			report = reportMatcher.replaceAll(methodConcepts);

			reportMatcher.reset(report);
			reportMatcher.usePattern(Pattern.compile(ROLES_MARKER));
			report = reportMatcher.replaceAll(roles);

		}

		return report;
	}
}
