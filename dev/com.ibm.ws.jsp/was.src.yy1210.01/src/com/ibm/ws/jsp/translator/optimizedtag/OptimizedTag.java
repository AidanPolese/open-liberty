//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.optimizedtag;

public interface OptimizedTag {
    void generateImports(OptimizedTagContext context);
    void generateDeclarations(OptimizedTagContext context);
    void setAttribute(String attrName, Object attrValue);
    void generateStart(OptimizedTagContext context);
    void generateEnd(OptimizedTagContext context);
    boolean doOptimization(OptimizedTagContext context);
    boolean canGenTagInMethod(OptimizedTagContext context);
}
