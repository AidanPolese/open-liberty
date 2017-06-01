//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.generator;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;

import com.ibm.ws.jsp.JspCoreException;

public class ScriptletGenerator extends CodeGeneratorBase {
    public void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException {
        if (section == CodeGenerationPhase.METHOD_SECTION) {
            for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                Node n = element.getChildNodes().item(i);
                if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
                    CDATASection cdata = (CDATASection)n;
                    String data = cdata.getData();
                    data = data.replaceAll("&gt;", ">");
                    data = data.replaceAll("&lt;", "<");
                    data = data.replaceAll("&amp;", "&");
                    char[] chars = data.toCharArray();
                    writeDebugStartBegin(writer);
                    writer.printMultiLn(new String(GeneratorUtils.removeQuotes(chars)));
                    writeDebugStartEnd(writer);
                }
            }
        }
    }

    public void endGeneration(int section, JavaCodeWriter writer)  throws JspCoreException {}
}
