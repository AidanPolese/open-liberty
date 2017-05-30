//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Node;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;

public class ParamsGenerator extends CodeGeneratorBase {
    public void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException {
    }

    public void endGeneration(int section, JavaCodeWriter writer)  throws JspCoreException {
        if (section == CodeGenerationPhase.METHOD_SECTION) {
            HashMap jspParams = (HashMap)persistentData.get("jspParams");
            if (jspParams != null) {
                ArrayList jspParamList = (ArrayList)jspParams.get(element);
                if (jspParamList != null) {
                    Node parent = element.getParentNode();
                    if (parent.getNodeType() == Node.ELEMENT_NODE &&
                        parent.getNamespaceURI() != null &&
                        parent.getNamespaceURI().equals(Constants.JSP_NAMESPACE) &&
                        parent.getLocalName().equals(Constants.JSP_BODY_TYPE)) {
                        parent = parent.getParentNode();    
                    }
                    jspParams.remove(element);
                    jspParams.put(parent, jspParamList);
                }
            }
        }
    }
}
