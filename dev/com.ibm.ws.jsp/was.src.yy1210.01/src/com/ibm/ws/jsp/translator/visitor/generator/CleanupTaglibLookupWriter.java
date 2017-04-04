//IBM Confidential OCO Source Material
//5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
package com.ibm.ws.jsp.translator.visitor.generator;

public class CleanupTaglibLookupWriter extends MethodWriter {
    public CleanupTaglibLookupWriter(boolean isThreadTagPooling) {
        println();
        if (isThreadTagPooling) {
        	println("private void cleanupTaglibLookup(HttpServletRequest request, java.util.HashMap _jspx_TagLookup) {");
        }
        else {
            println("private void cleanupTaglibLookup(java.util.HashMap _jspx_TagLookup) {");
        }
    }

    public void complete() {
        println("}");
    }
}
