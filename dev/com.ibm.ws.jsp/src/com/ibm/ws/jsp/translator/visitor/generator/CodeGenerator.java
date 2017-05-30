//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.configuration.JspConfiguration;
import com.ibm.ws.jsp.translator.visitor.JspVisitorInputMap;
import com.ibm.ws.jsp.translator.visitor.validator.ValidateResult;
import com.ibm.wsspi.jsp.context.JspCoreContext;

public interface CodeGenerator {
    void init(JspCoreContext ctxt,
              Element element, 
              ValidateResult validatorResult,
              JspVisitorInputMap inputMap,
              ArrayList methodWriterList,
              FragmentHelperClassWriter fragmentHelperClassWriter,
              HashMap persistentData,
              JspConfiguration jspConfiguration, 
              JspOptions jspOptions) throws JspCoreException;
    void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException;
    void endGeneration(int section, JavaCodeWriter writer) throws JspCoreException;
    JavaCodeWriter getWriterForChild(int section, Node jspElement) throws JspCoreException;
}

