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

public abstract class CodeGeneratorBase implements CodeGenerator {
    protected JspCoreContext ctxt = null;
    protected Element element = null;
    protected ValidateResult validatorResult = null;
    protected JspVisitorInputMap inputMap = null;
    protected ArrayList methodWriterList = null;
    protected FragmentHelperClassWriter fragmentHelperClassWriter = null;
    protected HashMap persistentData = null;
    protected boolean isTagFile = false;
    protected JspConfiguration jspConfiguration = null;
    protected JspOptions jspOptions = null;
    
    public CodeGeneratorBase() {}

    public void init(JspCoreContext ctxt, 
                     Element element, 
                     ValidateResult validatorResult,
                     JspVisitorInputMap inputMap,
                     ArrayList methodWriterList,
                     FragmentHelperClassWriter fragmentHelperClassWriter, 
                     HashMap persistentData,
                     JspConfiguration jspConfiguration,
                     JspOptions jspOptions) 
        throws JspCoreException {
        this.ctxt = ctxt;
        this.element = element;
        this.validatorResult = validatorResult;
        this.inputMap = inputMap;
        this.methodWriterList = methodWriterList;
        this.fragmentHelperClassWriter = fragmentHelperClassWriter;
        this.persistentData = persistentData;
        this.jspConfiguration = jspConfiguration;
        this.jspOptions = jspOptions;
        if (inputMap.containsKey("isTagFile")) {
            this.isTagFile = ((Boolean)inputMap.get("isTagFile")).booleanValue();
        }
    }
    
    public JavaCodeWriter getWriterForChild(int section, Node jspElement) throws JspCoreException {
        return null;
    }
    
    protected void writeDebugStartBegin(JavaCodeWriter writer) {
        writer.println("/* ElementId[" + element.hashCode() + "] sb */");
    }
    
    protected void writeDebugStartEnd(JavaCodeWriter writer) {
        writer.println("/* ElementId[" + element.hashCode() + "] se */");
    }
    
    protected void writeDebugEndBegin(JavaCodeWriter writer) {
        writer.println("/* ElementId[" + element.hashCode() + "] eb */");
    }
    
    protected void writeDebugEndEnd(JavaCodeWriter writer) {
        writer.println("/* ElementId[" + element.hashCode() + "] ee */");
    }
    
    public abstract void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException;
    public abstract void endGeneration(int section, JavaCodeWriter writer)  throws JspCoreException;
}
