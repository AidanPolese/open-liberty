//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.optimizedtag.impl;

import com.ibm.ws.jsp.translator.optimizedtag.OptimizedTag;
import com.ibm.ws.jsp.translator.optimizedtag.OptimizedTagContext;

public class JSTLOtherwiseOptimizedTag implements OptimizedTag {
    public boolean doOptimization(OptimizedTagContext context) {
        return true;
    }

    public void generateImports(OptimizedTagContext context) {
    }

    public void generateDeclarations(OptimizedTagContext context) {
    }

    public void generateStart(OptimizedTagContext context) {
        context.writeSource("} else {");
    }

    public void generateEnd(OptimizedTagContext context) {
    }

    public void setAttribute(String attrName, Object attrValue) {
    }

    public boolean canGenTagInMethod(OptimizedTagContext context) {
        return false;
    }
}
