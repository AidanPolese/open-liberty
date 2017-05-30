//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.optimizedtag;

import com.ibm.ws.jsp.JspOptions; //PK65013

public interface OptimizedTagContext {
    void writeSource(String source);
    void writeImport(String importId, String importSource);
    void writeDeclaration(String declarationId, String declarationSource);
    String createTemporaryVariable();
    boolean hasAttribute(String attrName);
    boolean isJspAttribute(String attrName);
    OptimizedTag getParent();
    boolean hasBody();
    boolean hasJspBody();
    JspOptions getJspOptions();  //PK65013
    boolean isTagFile(); //PK65013
}
