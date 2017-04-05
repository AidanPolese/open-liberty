//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//
//PK65013   sartoris    07/07/2008  Need ability to customize pageContext variable.
//  Defect PM41476 07/28/2011 sartoris    Tags have the xmlns attribute when rendered.

package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.translator.utils.JspTranslatorUtil;

public class UninterpretedTagGenerator extends CodeGeneratorBase {
    private static final String SINGLE_QUOTE = "'";
    private static final String DOUBLE_QUOTE = "\\\"";
	static private Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.translator.visitor.generator.UninterpretedTagGenerator";
	static{
		logger = Logger.getLogger("com.ibm.ws.jsp");
	}
    
    public void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException {
        if (section == CodeGenerationPhase.METHOD_SECTION) {
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
    			logger.logp(Level.FINEST, CLASS_NAME, "startGeneration","section = ["+section+"]");
    		}
            writeDebugStartBegin(writer);
            writer.print("out.write(\"<");
            writer.print(element.getTagName());
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
    			logger.logp(Level.FINEST, CLASS_NAME, "startGeneration","element.getTagName() = ["+element.getTagName()+"]");
    		}
            NamedNodeMap nodeAttrs = element.getAttributes();
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
    			logger.logp(Level.FINEST, CLASS_NAME, "startGeneration","nodeAttrs = ["+nodeAttrs+"]");
    			logger.logp(Level.FINEST, CLASS_NAME, "startGeneration","nodeAttrs.getLength() = ["+nodeAttrs.getLength()+"]");
    		}
            for (int i = 0; i < nodeAttrs.getLength(); i++) {
                Attr attr = (Attr)nodeAttrs.item(i);
        		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
        			logger.logp(Level.FINEST, CLASS_NAME, "startGeneration","nodeAttrs attr = ["+attr+"]");
        		}

                //PM41476 start -  added if statement
                if (jspOptions.isRemoveXmlnsFromOutput() && attr.getName().startsWith("xmlns:") == true ) {
                    continue;
                }
                //PM41476 end

                if (attr.getName().equals("jsp:id") == false && 
                    attr.getName().equals("xmlns:jsp") == false) {
                    String quote = DOUBLE_QUOTE;
                    writer.print(" ");
                    writer.print(attr.getName());
                    writer.print("=");
                    String value = attr.getValue();
            		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
            			logger.logp(Level.FINEST, CLASS_NAME, "startGeneration","about to call isELInterpreterInput. value = ["+value+"]");
            		}
                    if (JspTranslatorUtil.isELInterpreterInput(value, jspConfiguration)) {
                        //PK65013 - start
                        String pageContextVar = Constants.JSP_PAGE_CONTEXT_ORIG;
                        if (isTagFile && jspOptions.isModifyPageContextVariable()) {
                            pageContextVar = Constants.JSP_PAGE_CONTEXT_NEW;
                        }
                        //PK65013 - end
                        value = GeneratorUtils.attributeValue(value, false, String.class, jspConfiguration, isTagFile, pageContextVar); //PK65013
                        writer.print("\\\"\" + ");
                        writer.print(value);
                        writer.print(" + \"\\\"");
                    }
                    else {
                        if (value.indexOf('"') != -1) {
                            quote = SINGLE_QUOTE;
                        }
                        writer.print(quote);
                        writer.print(value);
                        writer.print(quote);
                    }
                }                
            }
            
            if (element.hasChildNodes()) {
                writer.print(">\");");
                writer.println();     
            } 
            else {
                writer.print("/>\");");
                writer.println();     
            }
            writeDebugStartEnd(writer);
        }
    }

    public void endGeneration(int section, JavaCodeWriter writer)  throws JspCoreException {
        if (section == CodeGenerationPhase.METHOD_SECTION) {
            if (element.hasChildNodes()) {
                writeDebugEndBegin(writer);
                writer.print("out.write(\"</");
                writer.print(element.getTagName());
                writer.print(">\");");
                writer.println();     
                writeDebugEndEnd(writer);
            }
        }
    }
}
