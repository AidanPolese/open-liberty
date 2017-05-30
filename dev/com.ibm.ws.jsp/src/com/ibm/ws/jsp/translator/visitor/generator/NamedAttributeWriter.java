//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.generator;

public class NamedAttributeWriter extends MethodWriter {
    private String attributeName = null;
    private String varName = null;
    
    public NamedAttributeWriter(String attributeName, String varName) {
        super();
        this.attributeName = attributeName;
        this.varName = varName;
    }
    
    public String getVarName() {
        return varName;
    }
    
    public String getAttributeName() {
        return attributeName;
    }
}
