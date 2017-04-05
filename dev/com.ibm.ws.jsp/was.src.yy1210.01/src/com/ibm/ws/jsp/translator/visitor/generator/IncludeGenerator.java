//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//  Defect PK20187 Property 'useStringCast' (Adds implicit string cast for included resources) 2006/04/24 SDJ
//
//Changes
//PK65013	sartoris	07/07/2008	Need ability to customize pageContext variable.
//PM01539	pmdinh		11/12/2009	useStringCast enhancement to handle the include() with parameters

package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.translator.utils.JspTranslatorUtil;

public class IncludeGenerator extends PageTranslationTimeGenerator {
    public IncludeGenerator() {
        super(new String[] { "flush" });
    }

    public void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException {}

    public void endGeneration(int section, JavaCodeWriter writer) throws JspCoreException {
        if (section == CodeGenerationPhase.METHOD_SECTION) {
            boolean flush = false;
            String flushString = getAttributeValue("flush");

            if (flushString != null && flushString.equalsIgnoreCase("true")) {
                flush = true;
            }

            boolean isFragment = false;
            if (writer instanceof FragmentHelperClassWriter.FragmentWriter)
                isFragment = true;

            boolean isLiteral = true;
            String page = element.getAttribute("page");
            if (JspTranslatorUtil.isExpression(page) || JspTranslatorUtil.isELInterpreterInput(page, jspConfiguration))
                isLiteral = false;

            if (page.equals("")) {
                HashMap jspAttributes = (HashMap) persistentData.get("jspAttributes");
                if (jspAttributes != null) {
                    ArrayList jspAttributeList = (ArrayList) jspAttributes.get(element);

                    for (Iterator itr = jspAttributeList.iterator(); itr.hasNext();) {
                        AttributeGenerator.JspAttribute jspAttribute = (AttributeGenerator.JspAttribute) itr.next();
                        if (jspAttribute.getName().equals("page")) {
                            page = jspAttribute.getVarName();
                            isLiteral = false;
                            break;
                        }
                    }
                }
            }
            else {
                //PK65013 - start
            	String pageContextVar = Constants.JSP_PAGE_CONTEXT_ORIG;
                if (isTagFile && jspOptions.isModifyPageContextVariable()) {
                	pageContextVar = Constants.JSP_PAGE_CONTEXT_NEW;
                }
                //PK65013 - end
                page = GeneratorUtils.attributeValue(page, false, String.class, jspConfiguration, isTagFile, pageContextVar); //PK65013
            }

            writeDebugStartBegin(writer);

			//PK20187
			if( jspOptions.isUseStringCast() == true )
			{
				writer.print("org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, (String)" +page );
			}
			else
			{
				writer.print("org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, " + page);
			}
			//PK20187

            HashMap jspParams = (HashMap) persistentData.get("jspParams");
            if (jspParams != null) {
                ArrayList jspParamList = (ArrayList) jspParams.get(element);
                if (jspParamList != null) {
                    String separator;
                    if (isLiteral){																	//PM01539
                    	separator = page.indexOf('?') > 0 ? "\"&\"" : "\"?\"";
                    }																				//PM01539
                    else {																			//PM01539
                    	if( jspOptions.isUseStringCast() ){											//PM01539
                    		separator = "(((String) (" + page + ")).indexOf('?')>0? '&': '?')";    	//PM01539
                    	}																			//PM01539
                    	else {																		//PM01539
                    		separator = "((" + page + ").indexOf('?')>0? '&': '?')";
                    	}																			//PM01539
                    }																				//PM01539
                    for (Iterator itr = jspParamList.iterator(); itr.hasNext();) {
                        ParamGenerator.JspParam jspParam = (ParamGenerator.JspParam) itr.next();
                        writer.print(" + ");
                        writer.print(separator);
                        writer.print(" + ");
                        writer.print(jspParam.getName());
                        writer.print(" + \"=\" + ");
                        writer.print(jspParam.getValue());
                        separator = "\"&\"";
                    }
                }
            }
            writer.print(", out, " + flush + ");");
            writer.println();     
            writeDebugStartEnd(writer);
        }
    }
}
