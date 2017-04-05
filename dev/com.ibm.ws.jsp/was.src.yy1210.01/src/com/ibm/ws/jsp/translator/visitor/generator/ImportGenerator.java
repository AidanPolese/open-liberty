//IBM Confidential OCO Source Material
//	5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//
//	Revisions:
//	Defect 203702  2004/05/17  Fix code gen for imports for better error messaging

package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.StringTokenizer;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ibm.ws.jsp.JspCoreException;

public class ImportGenerator extends CodeGeneratorBase {

	public void startGeneration(int section, JavaCodeWriter writer)
		throws JspCoreException {
		if (section == CodeGenerationPhase.IMPORT_SECTION) {
			NamedNodeMap attributes = element.getAttributes();
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Node attribute = attributes.item(i);
					String directiveName = attribute.getNodeName();
					String directiveValue = attribute.getNodeValue();
					if (directiveName.equals("import")) {
						StringTokenizer tokenizer = new StringTokenizer(directiveValue, ",");
						writeDebugStartBegin(writer);
						while (tokenizer.hasMoreTokens()) {
							writer.println("import " + (String) tokenizer.nextToken() + ";");
						}
						writeDebugStartEnd(writer);
					}
				}
			}
		}
	}

	public void endGeneration(int section, JavaCodeWriter writer)
		throws JspCoreException {
	}

}
