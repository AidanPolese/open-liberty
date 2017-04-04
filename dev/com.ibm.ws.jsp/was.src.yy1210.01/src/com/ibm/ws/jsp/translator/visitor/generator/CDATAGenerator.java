//IBM Confidential OCO Source Material
//  5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//Defect 227804  2004/08/31  Strange stepping behaviour against the enclosed JSP file
//Defect 232157  2004/09/16  jsp fragment trims whitespace around el variable

package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.configuration.JspConfiguration;
import com.ibm.ws.jsp.translator.visitor.JspVisitorInputMap;
import com.ibm.ws.jsp.translator.visitor.validator.ValidateResult;
import com.ibm.wsspi.jsp.context.JspCoreContext;

public class CDATAGenerator extends TextGenerator {
    protected CDATASection cdata = null;
    
    public CDATAGenerator(CDATASection cdata) {
        this.cdata = cdata;                                
    }
    
    public void init(JspCoreContext ctxt,
                     Element element, 
                     ValidateResult validatorResult,
                     JspVisitorInputMap inputMap,
                     ArrayList methodWriterList,
                     FragmentHelperClassWriter fragmentHelperClassWriter, 
                     HashMap persistentData,
                     JspConfiguration jspConfiguration,
                     JspOptions jspOptions,
                     boolean elIgnored) 
        throws JspCoreException {
        super.init(ctxt, element, validatorResult, inputMap, methodWriterList, fragmentHelperClassWriter, persistentData, jspConfiguration, jspOptions);
        this.elIgnored = elIgnored;
    }
                
    public void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException {
        if (section == CodeGenerationPhase.CLASS_SECTION ||
            section == CodeGenerationPhase.METHOD_SECTION) {

            if (section == CodeGenerationPhase.CLASS_SECTION) {                    
                nextStringNum = (Integer)persistentData.get("nextStringNum");
                if (nextStringNum == null) {
                    nextStringNum = new Integer(0);
                }
            }

            /* If the any sibling nodes are jsp:attribute or jsp:body then don't print out output. jsp:body will handle this */
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child instanceof Element) {
                    Element childElement = (Element)child;
                    if (childElement.getNamespaceURI() != null && childElement.getNamespaceURI().equals(Constants.JSP_NAMESPACE) &&
                        childElement.getLocalName().equals(Constants.JSP_ATTRIBUTE_TYPE)) {
                        return;                                    
                    }
                    else if (childElement.getNamespaceURI() != null && childElement.getNamespaceURI().equals(Constants.JSP_NAMESPACE) &&
                        childElement.getLocalName().equals(Constants.JSP_BODY_TYPE)) {
                        return;                                    
                    }
                }
            }

            if (element.getNamespaceURI() != null && 
                element.getNamespaceURI().equals(Constants.JSP_NAMESPACE) &&
                element.getLocalName().equals(Constants.JSP_ATTRIBUTE_TYPE)) {
                
                /* If the parent is a jsp:attribute and is literal text then we */
                /* need to change prefix and sufiix for code generated */
            
                HashMap jspAttributes = (HashMap)persistentData.get("jspAttributes");
                if (jspAttributes != null) {
                    Node parent = element.getParentNode();
                    ArrayList jspAttributeList = (ArrayList)jspAttributes.get(parent);
                    if (jspAttributeList != null) {
                        for (Iterator itr = jspAttributeList.iterator(); itr.hasNext();) {
                            AttributeGenerator.JspAttribute jspAttribute = (AttributeGenerator.JspAttribute)itr.next();
                            if (jspAttribute.getJspAttrElement().equals(element)) {
                                if (jspAttribute.isLiteral()) {
                                    attrName = jspAttribute.getVarName();
                                }
                                trim = jspAttribute.trim();
                            }
                        }
                    }
                }
            }

            String data = cdata.getData();
            // 232157 start
            if (trim)
                data = data.trim();
            // 232157 end
                
            char[] chars = data.toCharArray();

            int current = 0;
            int limit = chars.length;
            //if (data.trim().length() > 0 && section == CodeGenerationPhase.METHOD_SECTION) {
                //227804 Do not debug template text
                //writer.println("/* CDATAId[" + cdata.hashCode() + "] sb */");
            //}
            int newCurrentStringCount = nextStringNum.intValue();
            while (current < limit) {
                int from = current;
                int to = Math.min(current + MAXSIZE, limit);
                newCurrentStringCount = generateChunk(chars, writer, from, to, section, newCurrentStringCount);
                current = to;
            }
            if (section == CodeGenerationPhase.CLASS_SECTION) {
                persistentData.put("nextStringNum", new Integer(newCurrentStringCount));
            }
            //if (data.trim().length() > 0 && section == CodeGenerationPhase.METHOD_SECTION) {
                //227804 Do not debug template text
                //writer.println("/* CDATAId[" + cdata.hashCode() + "] se */");
            //}
        }
    }
}
