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

import edu.utep.cybershare.wdoapi.SAW;

/**
 * @author Leonardo Salayandia
 * 
 */
public class SAW2Moml {

	private static final String tab = "    ";

	public SAW2Moml() {

	}

	public static void generateMoML(SAW saw, String filename) {
		// open file to write MOML
		File outfile = new File(filename);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					outfile.getAbsolutePath(), false));
			StringBuffer moml = new StringBuffer();
			moml.append("<?xml version=\"1.0\" standalone=\"no\"?>\n"
					+ "<!DOCTYPE entity PUBLIC \"-//UC Berkeley//DTD MoML 1//EN\"\n"
					+ tab
					+ "\"http://ptolemy.eecs.berkeley.edu/xml/dtd/MoML_1.dtd\">\n");
			// moml.append("<entity name=\"" + mbw.getNameSpace() +
			// "\" class=\"ptolemy.actor.TypedCompositeActor\">\n"
			// + tab +
			// "<property name=\"_createdBy\" class=\"ptolemy.kernel.attributes.VersionAttribute\" value=\"6.1.devel\">\n"
			// + tab + "</property>\n"
			// + tab +
			// "<property name=\"_windowProperties\" class=\"ptolemy.actor.gui.WindowPropertiesAttribute\" value=\"{bounds={285, 146, 830, 757}}\">\n"
			// + tab + "</property>\n"
			// + tab +
			// "<property name=\"_vergilSize\" class=\"ptolemy.actor.gui.SizeAttribute\" value=\"[600, 633]\">\n"
			// + tab + "</property>\n");

			// add director
			moml.append(tab
					+ "<property name=\"PN Director\" class=\"ptolemy.domains.pn.kernel.PNDirector\">\n"
					+ tab
					+ tab
					+ "<property name=\"timeResolution\" class=\"ptolemy.moml.SharedParameter\" value=\"1E-10\">\n"
					+ tab
					+ tab
					+ "</property>\n"
					+ tab
					+ tab
					+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org:director:2:1\">\n"
					+ tab
					+ tab
					+ "</property>\n"
					+ tab
					+ tab
					+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.domains.pn.kernel.PNDirector\">\n"
					+ tab
					+ tab
					+ tab
					+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:directorclass:2:1\">\n"
					+ tab
					+ tab
					+ tab
					+ "</property>\n"
					+ tab
					+ tab
					+ "</property>\n"
					+ tab
					+ tab
					+ "<property name=\"semanticType000\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#Director\">\n"
					+ tab
					+ tab
					+ "</property>\n"
					+ tab
					+ tab
					+ "<property name=\"semanticType111\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#Director\">\n"
					+ tab
					+ tab
					+ "</property>\n"
					+ tab
					+ tab
					+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"{115, 100}\">\n"
					+ tab + tab + "</property>\n" + tab + "</property>\n");

			// add entities, relations, and links
			// for (Iterator<WDOInstance> i = mbw.listRootMBWConstructs();
			// i.hasNext(); ) {
			// parseMBW(mbw, i.next(), moml);
			// }

			// finish off MOML file
			moml.append("</entity>");
			out.write(moml.toString());
			out.close();
		} catch (Exception e) {
			// MainUI.getInstance().showErrorMessage(e.getMessage());
		}
	}

	// private static WDOInstance parseMBW(Saw mbw, WDOInstance root,
	// StringBuffer moml) throws Exception {
	// WDOInstance ans = null;
	// if (root.isWFSequenceType()) {
	// WDOInstance pred = parseMBW(mbw, root.listPredecessorValues().next(),
	// moml);
	// WDOInstance succ = root.listSuccessorValues().next();
	// if (succ.isMethodType()) {
	// moml.append(addEntity(succ));
	// moml.append(addRelationAndLinks(pred,succ));
	// ans = succ;
	// }
	// WFSequence rootSeq = root.asWFSequence(null, null);
	// Concept predConcept = parseMBW(rootSeq.getPredecessor(), moml);
	// Concept succConcept = rootSeq.getSuccessor();
	// if (succConcept.isMethod()) {
	// Method m = succConcept.asMethod();
	// if (m.isAnonymous()) {
	// m = m.listImmediateParents().next().asMethod();
	// }
	// moml.append(addEntity(m));
	// moml.append(addRelationAndLinks(predConcept, m));
	// ans = m;
	// }
	// }
	// else if (root.isMethodType()) {
	// moml.append(addEntity(root));
	// ans = root;
	// Method m = root.asMethod();
	// if (m.isAnonymous()) {
	// m = m.listImmediateParents().next().asMethod();
	// }
	// moml.append(addEntity(m));
	// ans = m;
	// }
	// return ans;
	// }

	// private static String addEntity(WDOInstance ind) {
	// String entity = "";
	// if (ind != null) {
	// StringBuffer tmp = new StringBuffer();
	// tmp.append(tab + "<entity name=\"" + ind.getWDOType().toString() +
	// "\" class=\"ptolemy.actor.TypedCompositeActor\">\n"
	// + tab + tab +
	// "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org:actor:449:1\">\n"
	// + tab + tab + "</property>\n"
	// + tab + tab +
	// "<property name=\"documentation\" class=\"org.kepler.moml.DocumentationAttribute\">\n"
	// + tab + tab + "</property>\n"
	// + tab + tab +
	// "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.TypedCompositeActor\">\n"
	// + tab + tab + tab +
	// "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:449:1\">\n"
	// + tab + tab + tab + "</property>\n"
	// + tab + tab + "</property>\n"
	// + tab + tab +
	// "<property name=\"semanticType000\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#Actor\">\n"
	// + tab + tab + "</property>\n"
	// + tab + tab +
	// "<property name=\"semanticType111\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#GeneralPurpose\">\n"
	// + tab + tab + "</property>\n"
	// + tab + tab +
	// "<property name=\"semanticType222\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#Workflow\">\n"
	// + tab + tab + "</property>\n"
	// + tab + tab +
	// "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"[160.0, 215.0]\">\n"
	// + tab + tab + "</property>\n"
	// + tab + tab +
	// "<property name=\"output\" class=\"ptolemy.data.expr.Parameter\" value=\"\">\n"
	// + tab + tab + tab +
	// "<property name=\"style\" class=\"ptolemy.actor.gui.style.TextStyle\">\n"
	// + tab + tab + tab + tab +
	// "<property name=\"height\" class=\"ptolemy.data.expr.Parameter\" value=\"10\">\n"
	// + tab + tab + tab + tab + tab +
	// "<property name=\"_editorFactory\" class=\"ptolemy.vergil.toolbox.VisibleParameterEditorFactory\">\n"
	// + tab + tab + tab + tab + tab + "</property>\n"
	// + tab + tab + tab + tab + "</property>\n"
	// + tab + tab + tab + tab +
	// "<property name=\"width\" class=\"ptolemy.data.expr.Parameter\" value=\"30\">\n"
	// + tab + tab + tab + tab + "</property>\n"
	// + tab + tab + tab + "</property>\n"
	// + tab + tab + "</property>\n"
	// + tab + tab +
	// "<port name=\"input\" class=\"ptolemy.actor.TypedIOPort\">\n"
	// + tab + tab + tab + "<property name=\"input\"/>\n"
	// + tab + tab + "</port>\n"
	// + tab + tab +
	// "<port name=\"output\" class=\"ptolemy.actor.TypedIOPort\">\n"
	// + tab + tab + tab + "<property name=\"output\"/>\n"
	// + tab + tab + "</port>\n"
	// + tab + "</entity>\n");
	// entity = tmp.toString();
	// }
	// return entity;
	// }

	// private static String addRelationAndLinks(WDOInstance ind1, WDOInstance
	// ind2) {
	// String relation = "";
	// if (ind1 != null && ind2 != null) {
	// StringBuffer tmp = new StringBuffer();
	// String ind1name = ind1.getWDOType().toString();
	// String ind2name = ind2.getWDOType().toString();
	// String relationName = ind1name + ind2name + "Relation";
	// tmp.append(tab + "<relation name=\"" + relationName +
	// "\" class=\"ptolemy.actor.TypedIORelation\">\n"
	// + tab + tab +
	// "<property name=\"width\" class=\"ptolemy.data.expr.Parameter\" value=\"1\">\n"
	// + tab + tab + "</property>\n"
	// + tab + "</relation>\n"
	// + tab + "<link port=\"" + ind1name + ".output\" relation=\"" +
	// relationName + "\"/>\n"
	// + tab + "<link port=\"" + ind2name + ".input\" relation=\"" +
	// relationName + "\"/>)\n");
	// relation = tmp.toString();
	// }
	// return relation;
	// }
}

// import org.apache.xerces.dom.DocumentImpl;
// import org.apache.xml.serialize.OutputFormat;
// import org.apache.xml.serialize.XMLSerializer;
// public void Generate(Saw mbw, String momlFilename) throws Exception {
// FileWriter out = new FileWriter(momlFilename);
// out.flush();
// DocumentImpl d = new DocumentImpl();
//
// OutputFormat o = new OutputFormat(d);
// o.setIndent(5);
// o.setIndenting(true);
// o.setDoctype("lib/MoML_1.dtd", "lib/MoML_1.dtd");
// o.setDoctype("name of dtd file", "name of dtd file");
//
// XMLSerializer x = new XMLSerializer(o);
//
// x.serialize(d);
// out.flush();
// }
//
// private void CreateMethodElement(Method m) {
//
// }

