//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

// Changes
//  Defect PM41476 07/28/2011 sartoris    Tags have the xmlns attribute when rendered.

package com.ibm.ws.jsp.translator.visitor.generator;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import com.ibm.ws.jsp.JspCoreException;

public class TagDependentGenerator extends CodeGeneratorBase {
    private static final String SINGLE_QUOTE = "'";
    private static final String DOUBLE_QUOTE = "\\\"";
    
    public void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException {
        if (section == CodeGenerationPhase.METHOD_SECTION) {
            writeDebugStartBegin(writer);
            writer.print("out.write(\"<");
            writer.print(element.getTagName());
            NamedNodeMap nodeAttrs = element.getAttributes();
            for (int i = 0; i < nodeAttrs.getLength(); i++) {
                Attr attr = (Attr)nodeAttrs.item(i);
                //PM41476 start -  added if statement
                if (jspOptions.isRemoveXmlnsFromOutput() && attr.getName().startsWith("xmlns:") == true ) {
                    continue;
                }
                //PM41476 end
                if (attr.getName().equals("jsp:id") == false && 
                    attr.getName().equals("xmlns:jsp") == false) {
                    String quote = DOUBLE_QUOTE;
                    writer.print(" ");
                    writer.print(attr.getName());
                    writer.print("=");
                    String value = attr.getValue();
                    if (value.indexOf('"') != -1) {
                        quote = SINGLE_QUOTE;
                    }
                    writer.print(quote);
                    writer.print(value);
                    writer.print(quote);
                }                
            }
            
            if (element.hasChildNodes()) {
                writer.print(">\");");
                writer.println();     
            } 
            else {
                writer.print("/>\");");
                writer.println();     
            }
            writeDebugStartEnd(writer);
        }
    }

    public void endGeneration(int section, JavaCodeWriter writer)  throws JspCoreException {
        if (section == CodeGenerationPhase.METHOD_SECTION) {
            if (element.hasChildNodes()) {
                writeDebugEndBegin(writer);
                writer.print("out.write(\"</");
                writer.print(element.getTagName());
                writer.print(">\");");
                writer.println();     
                writeDebugEndEnd(writer);
            }
        }
    }

}
