//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.List;

import org.w3c.dom.Node;

import com.ibm.ws.jsp.JspCoreException;

public interface TagGenerator {
    public MethodWriter generateTagStart() throws JspCoreException;
    public MethodWriter generateTagMiddle() throws JspCoreException;
    public MethodWriter generateTagEnd() throws JspCoreException;
    public MethodWriter getBodyWriter();
    public JavaCodeWriter getWriterForChild(int section, Node childElement) throws JspCoreException;
    public void generateImports(JavaCodeWriter writer);
    public void generateDeclarations(JavaCodeWriter writer);
    public void generateInitialization(JavaCodeWriter writer);
    public void generateFinally(JavaCodeWriter writer);
    public List generateSetters() throws JspCoreException;
    public void setParentTagInstanceInfo(CustomTagGenerator.TagInstanceInfo parentTagInstanceInfo);
    public void setIsInFragment(boolean isFragment);
    public boolean fragmentWriterUsed();
}
