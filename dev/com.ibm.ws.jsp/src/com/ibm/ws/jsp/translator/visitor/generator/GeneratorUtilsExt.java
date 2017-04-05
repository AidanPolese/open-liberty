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
//  Revisions:
//  Defect PI59436 03/22/2015   hmpadill    EL expressions returning null in EL 3.0 could produce NPE

package com.ibm.ws.jsp.translator.visitor.generator;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.translator.visitor.validator.ValidateResult;

/**
 *
 */
public interface GeneratorUtilsExt {
    
    public void generateELFunctionCode(JavaCodeWriter writer, ValidateResult validatorResult) throws JspCoreException;
    
    /**
     * Produces a String representing a call to the EL interpreter.
     * @param expression a String containing zero or more "${}" expressions
     * @param expectedType the expected type of the interpreted result
     * @param defaultPrefix Default prefix, or literal "null"
     * @param fnmapvar Variable pointing to a function map.
     * @param XmlEscape True if the result should do XML escaping
     * @param pageContextVar Variable for PageContext variable name in generated Java code.
     * @return a String representing a call to the EL interpreter.
     */
    public String interpreterCall(boolean isTagFile, String expression, Class expectedType, String fnmapvar, boolean XmlEscape, String pageContextVar); //PI59436
    
    public String getClassFileVersion();
}
