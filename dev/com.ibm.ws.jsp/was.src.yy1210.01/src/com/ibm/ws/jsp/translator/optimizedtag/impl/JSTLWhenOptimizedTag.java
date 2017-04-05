//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.optimizedtag.impl;

import com.ibm.ws.jsp.translator.optimizedtag.OptimizedTag;
import com.ibm.ws.jsp.translator.optimizedtag.OptimizedTagContext;

public class JSTLWhenOptimizedTag implements OptimizedTag {
    private String test = null;
    
    public boolean doOptimization(OptimizedTagContext context) {
        boolean optimize = false;
        if (context.getParent() != null && context.getParent() instanceof JSTLChooseOptimizedTag) {
            optimize = true;
        }
        return optimize;
    }

    public void generateImports(OptimizedTagContext context) {
    }

    public void generateDeclarations(OptimizedTagContext context) {
    }

    public void generateStart(OptimizedTagContext context) {
        JSTLChooseOptimizedTag chooseTag = (JSTLChooseOptimizedTag)context.getParent();
        if (chooseTag.isFirstWhenSpecified()) {
            context.writeSource("} else if(");
        }
        else {
            context.writeSource("if (");
            chooseTag.setFirstWhenSpecified(true);
        }
        context.writeSource(test);
        context.writeSource(") {");
    }

    public void generateEnd(OptimizedTagContext context) {
    }

    public void setAttribute(String attrName, Object attrValue) {
        if (attrName.equals("test")) {
            test = (String)attrValue;
        }
    }
    
    public boolean canGenTagInMethod(OptimizedTagContext context) {
        return false;
    }
}
