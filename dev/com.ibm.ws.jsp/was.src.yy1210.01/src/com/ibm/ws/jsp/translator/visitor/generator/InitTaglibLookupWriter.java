//IBM Confidential OCO Source Material
//5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
package com.ibm.ws.jsp.translator.visitor.generator;

public class InitTaglibLookupWriter extends MethodWriter {
    
	public InitTaglibLookupWriter(boolean isThreadTagPooling) {
        println();
        if (isThreadTagPooling) {
        	println("private java.util.HashMap initTaglibLookup(HttpServletRequest request) {");
        }
        else {
            println("private java.util.HashMap initTaglibLookup() {");
        }
        println("java.util.HashMap _jspx_TagLookup = new java.util.HashMap();");
    }
    
    public void complete() {
        println("return _jspx_TagLookup;");
        println("}");
    }
}
