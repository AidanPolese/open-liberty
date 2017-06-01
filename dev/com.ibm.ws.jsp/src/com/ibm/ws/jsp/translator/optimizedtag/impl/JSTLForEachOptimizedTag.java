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
import com.ibm.ws.jsp.Constants; //PK65013

public class JSTLForEachOptimizedTag implements OptimizedTag {
    private String var = null;
    private String items = null;
    private String step = null;
    private String begin = null;
    private String end = null;
    
    String indexV = null, beginV = null, endV = null, stepV = null, iterV = null;
    
    public void generateImports(OptimizedTagContext context) {
        context.writeImport("javaUtilImport", "import java.util.*;");
    }
    
    public void generateDeclarations(OptimizedTagContext context) {
    }
    
    public void setAttribute(String attrName, Object attrValue) {
        if (attrName.equals("items")) {
            items = (String)attrValue;
        }
        else if (attrName.equals("var")) {
            var = (String)attrValue;
        }
        else if (attrName.equals("step")) {
            step = (String)attrValue;
        }
        else if (attrName.equals("begin")) {
            begin = (String)attrValue;
        }
        else if (attrName.equals("end")) {
            end = (String)attrValue;
        }
    }
    
    public void generateStart(OptimizedTagContext context) {
    	//PK65013 - start
        String pageContextVar = Constants.JSP_PAGE_CONTEXT_ORIG;
        JspOptions jspOptions = context.getJspOptions(); 
    	if (jspOptions != null) {
            if (context.isTagFile() && jspOptions.isModifyPageContextVariable()) {
                pageContextVar = Constants.JSP_PAGE_CONTEXT_NEW;
            }
        }
        //PK65013 - end

        if (context.hasAttribute("items")) {
            String itemsV = context.createTemporaryVariable();
            context.writeSource("Object " + itemsV + " = " + items +";");

            if (context.hasAttribute("begin")) {
                beginV = context.createTemporaryVariable();
                context.writeSource("int " + beginV + " = " + begin + ";");
            }
            if (context.hasAttribute("end")) {
                indexV = context.createTemporaryVariable();
                context.writeSource("int " + indexV + " = 0;");
                endV = context.createTemporaryVariable();
                context.writeSource("int " + endV + " = " + end + ";");
            }
            if (context.hasAttribute("step")) {
                stepV = context.createTemporaryVariable();
                context.writeSource("int " + stepV + " = " + step + ";");
            }

            iterV = context.createTemporaryVariable();
            context.writeSource("Iterator " + iterV + " = com.ibm.ws.jsp.translator.optimizedtag.impl.JSTLForEachIteratorSupport.createIterator("+itemsV+");");

            if (context.hasAttribute("begin")) {
                String tV = context.createTemporaryVariable();
                context.writeSource("for (int " + tV + "=" + beginV + ";" + tV + ">0 && " + iterV + ".hasNext(); " + tV + "--)");
                context.writeSource(iterV + ".next();");
            }

            context.writeSource("while (" + iterV + ".hasNext()){");
            if (context.hasAttribute("var")) {
                //PK65013 change pageContext variable to customizable one.
                context.writeSource(pageContextVar+".setAttribute(" + var + ", " + iterV + ".next());");
            }
        }
        else {
            if (context.hasAttribute("begin")) {
                beginV = context.createTemporaryVariable();
                context.writeSource("int " + beginV + " = " + begin + ";");
            }
            if (context.hasAttribute("end")) {
                endV = context.createTemporaryVariable();
                context.writeSource("int " + endV + " = " + end + ";");
            }
            if (context.hasAttribute("step")) {
                stepV = context.createTemporaryVariable();
                context.writeSource("int " + stepV + " = " + step + ";");
            }
            String index = context.createTemporaryVariable();
            context.writeSource("for (int " + index + " = " + beginV + "; " + index + " <= " + endV);
            if (context.hasAttribute("step")) {
                context.writeSource("; " + index + "+=" + stepV+ ") {");
            }
            else {
                context.writeSource("; " + index + "++) {");
            }

            if (context.hasAttribute("var")) {
                //PK65013 change pageContext variable to customizable one.
                context.writeSource(pageContextVar+".setAttribute("+var+", String.valueOf(" + index + "));");
            }
        }
    }
    
    public void generateEnd(OptimizedTagContext context) {
        if (context.hasAttribute("items")) {
            if (context.hasAttribute("step")) {
                String tV = context.createTemporaryVariable();
                context.writeSource("for (int " + tV + "=" + stepV + "-1;" + tV + ">0 && " + iterV + ".hasNext(); " + tV + "--)");
                context.writeSource(iterV + ".next();");
            }
            if (context.hasAttribute("end")) {
                if (context.hasAttribute("step")) {
                    context.writeSource(indexV + "+=" + stepV + ";");
                }
                else {
                    context.writeSource(indexV + "++;");
                }
                if (context.hasAttribute("begin")) {
                    context.writeSource("if(" + beginV + "+" + indexV + ">" + endV + ")");
                }
                else {
                    context.writeSource("if(" + indexV + ">" + endV + ")");
                }
                context.writeSource("break;");
            }
        }
        context.writeSource("}");
    }
    
    public boolean doOptimization(OptimizedTagContext context) {
        boolean optimize = true;
        
        if (context.hasAttribute("varStatus")) {
            optimize = false;
        }
        else if (context.isJspAttribute("var")) {
            optimize = false;
        }
        else if (context.isJspAttribute("items")) {
            optimize = false;
        }
        else if (context.isJspAttribute("step")) {
            optimize = false;
        }
        else if (context.isJspAttribute("begin")) {
            optimize = false;
        }
        else if (context.isJspAttribute("end")) {
            optimize = false;
        }

        return optimize;
    }
    
    public boolean canGenTagInMethod(OptimizedTagContext context) {
        return true;
    }
}
