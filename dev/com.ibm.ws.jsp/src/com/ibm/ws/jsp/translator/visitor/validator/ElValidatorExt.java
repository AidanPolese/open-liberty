/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.jsp.translator.visitor.validator;

import org.apache.jasper.compiler.ELNode;
import org.w3c.dom.Element;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.configuration.JspConfiguration;
import com.ibm.ws.jsp.translator.JspTranslationException;

/**
 *
 */
public interface ElValidatorExt {
    
    public void validateElFunction(ELNode.Nodes el,Element jspElement,String expression, ValidateResult result, ClassLoader loader, JspConfiguration jspConfiguration) throws JspCoreException;

    public void prepareExpression(ELNode.Nodes el,String expression, ValidateResult result, ClassLoader loader, JspConfiguration jspConfiguration) throws  JspTranslationException;
}
