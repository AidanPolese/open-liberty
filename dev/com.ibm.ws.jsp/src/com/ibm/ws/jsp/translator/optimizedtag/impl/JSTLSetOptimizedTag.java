//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//
//Changes
//PK65013	sartoris	07/07/2008	Need ability to customize pageContext variable.

package com.ibm.ws.jsp.translator.optimizedtag.impl;

import com.ibm.ws.jsp.translator.optimizedtag.OptimizedTag;
import com.ibm.ws.jsp.translator.optimizedtag.OptimizedTagContext;
import com.ibm.ws.jsp.JspOptions;  //PK65013
import com.ibm.ws.jsp.Constants;  //PK65013

public class JSTLSetOptimizedTag implements OptimizedTag {
    private String value = null;
    private String var = null;
    private String scope = null;
    private String target = null;
    private String property = null;
    
    public boolean canGenTagInMethod(OptimizedTagContext context) {
        return true;
    }

    public boolean doOptimization(OptimizedTagContext context) {
        boolean optimize = true;
        
        if (context.hasBody() || context.hasJspBody()) {
            optimize = false;
        }
        else if (context.isJspAttribute("value")) {
            optimize = false;
        }
        else if (context.isJspAttribute("var")) {
            optimize = false;
        }
        else if (context.isJspAttribute("scope")) {
            optimize = false;
        }
        else if (context.isJspAttribute("target")) {
            optimize = false;
        }
        else if (context.isJspAttribute("property")) {
            optimize = false;
        }
        
        return optimize;
    }

    public void generateImports(OptimizedTagContext context) {
    }

    public void generateDeclarations(OptimizedTagContext context) {
    }

    public void setAttribute(String attrName, Object attrValue) {
        if (attrName.equals("value")) {
            value = (String)attrValue;
        }
        else if (attrName.equals("var")) {
            var = (String)attrValue;
        }
        else if (attrName.equals("scope")) {
            scope = (String)attrValue;
        }
        else if (attrName.equals("target")) {
            target = (String)attrValue;
        }
        else if (attrName.equals("property")) {
            property = (String)attrValue;
        }
    }
    
    public void generateStart(OptimizedTagContext context) {
        String scopeV = null;
    	//PK65013 - start
        String pageContextVar = Constants.JSP_PAGE_CONTEXT_ORIG;
        JspOptions jspOptions = context.getJspOptions(); 
    	if (jspOptions != null) {
    		if (context.isTagFile() && jspOptions.isModifyPageContextVariable()) {
    			pageContextVar = Constants.JSP_PAGE_CONTEXT_NEW;
    		}
        }
        //PK65013 - end

        
        if (context.hasAttribute("scope")) {
            if (scope.equals("page")) {
                scopeV = "PageContext.PAGE_SCOPE";
            }
            else if (scope.equals("request")) {
                scopeV = "PageContext.REQUEST_SCOPE";
            }
            else if (scope.equals("session")) {
                scopeV = "PageContext.SESSION_SCOPE";
            }
            else if (scope.equals("application")) {
                scopeV = "PageContext.APPLICATION_SCOPE";
            }
        }
        
        String valueV = context.createTemporaryVariable();
        context.writeSource("Object " + valueV + " = " + value + ";");
        if (context.hasAttribute("var")) {
            context.writeSource("if ("+valueV+" != null) {");
            if (scopeV == null) {
                //PK65013 change pageContext variable to customizable one.
                context.writeSource("   "+pageContextVar+".setAttribute("+var+", "+valueV+");");
            }
            else {
                //PK65013 change pageContext variable to customizable one.
                context.writeSource("   "+pageContextVar+".setAttribute("+var+", "+valueV+", "+scopeV+");");
            }
            context.writeSource("} else {");
            if (scopeV == null) {
                //PK65013 change pageContext variable to customizable one.
                context.writeSource("   "+pageContextVar+".removeAttribute("+var+");");
            }
            else {
                //PK65013 change pageContext variable to customizable one.
                context.writeSource("   "+pageContextVar+".removeAttribute("+var+", "+scopeV+");");
            }
            context.writeSource("}");
        }
        else if (context.hasAttribute("target")) {
            String targetV = context.createTemporaryVariable();
            context.writeSource("Object " + targetV + " = " + target + ";");
            String propertyV = context.createTemporaryVariable();
            context.writeSource("String " + propertyV + " = " + property + ";");
            //PK65013 change pageContext variable to customizable one.
            context.writeSource("com.ibm.ws.jsp.translator.optimizedtag.impl.JSTLSetUtil.setProperty("+pageContextVar+", "+targetV+", "+valueV+", "+propertyV+");");
        }
    }

    public void generateEnd(OptimizedTagContext context) {
    }
}
