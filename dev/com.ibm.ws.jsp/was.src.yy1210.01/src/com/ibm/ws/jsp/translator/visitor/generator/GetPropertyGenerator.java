//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//
//	Defect PK65013 2008/07/07  Need ability to customize pageContext variable.
package com.ibm.ws.jsp.translator.visitor.generator;

import java.lang.reflect.Method;

import org.w3c.dom.Element;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.bean.BeanRepository;
import com.ibm.ws.jsp.translator.JspTranslationException;

public class GetPropertyGenerator extends PageTranslationTimeGenerator {
    public GetPropertyGenerator() {
        super(new String[] {"name", "property"});
    }
    
    public void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException {}

    public void endGeneration(int section, JavaCodeWriter writer)  throws JspCoreException {
    	//PK65013 start
    	String pageContextVar = Constants.JSP_PAGE_CONTEXT_ORIG;
    	if (isTagFile) {
            if (jspOptions.isModifyPageContextVariable()) {
                pageContextVar = Constants.JSP_PAGE_CONTEXT_NEW;
            }
        }
    	//PK65013 end
        if (section == CodeGenerationPhase.METHOD_SECTION) {
            String name = getAttributeValue("name");
            String property = getAttributeValue("property");
            BeanRepository beanRepository = validatorResult.getBeanRepository();
            writeDebugStartBegin(writer);
            if (beanRepository.checkVariable(name)) {
                Class cls = beanRepository.getBeanType(name, element);
                String clsName = cls.getName();
                java.lang.reflect.Method meth = getReadMethod(cls, property, element);

                String methodName = meth.getName();
                //PK65013
                writer.println("out.print(org.apache.jasper.runtime.JspRuntimeLibrary.toString(" +
                               "(((" + clsName + ")"+pageContextVar+".findAttribute(" +
                               "\"" + name + "\"))." + methodName + "())));");
            }
            else {
                //PK65013
                writer.println("out.print(org.apache.jasper.runtime.JspRuntimeLibrary.toString(org.apache.jasper.runtime.JspRuntimeLibrary." +
                               "handleGetProperty("+pageContextVar+".findAttribute(" +
                               "\"" + name + "\"), \"" + property + "\")));");
            }
            writeDebugStartEnd(writer);
        }
    }
    
    public static Method getReadMethod(Class beanClass, String prop, Element element) throws JspCoreException {
        java.lang.reflect.Method method = null;
        Class type = null;
        try {
            java.beans.BeanInfo info;
            info = java.beans.Introspector.getBeanInfo(beanClass);
            if (info != null) {
                java.beans.PropertyDescriptor pd[] = info.getPropertyDescriptors();
                for (int i = 0; i < pd.length; i++) {
                    if (pd[i].getName().equals(prop)) {
                        method = pd[i].getReadMethod();
                        type = pd[i].getPropertyType();
                        break;
                    }
                }
            }
            else {
                throw new JspTranslationException(element, "jsp.error.beans.nobeaninfo");
            }
        }
        catch (Exception ex) {
            throw new JspCoreException(ex);
        }
        if (method == null) {
            if (type == null) {
                throw new JspTranslationException(element, "jsp.error.beans.noproperty");
            }
            else {
                throw new JspTranslationException(element, "jsp.error.beans.nomethod");
            }
        }

        return method;
    }
}
