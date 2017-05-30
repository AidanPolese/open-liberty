//IBM Confidential OCO Source Material
//	5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2007 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.taglib;

import java.util.HashMap;

import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagInfo;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.wsspi.jsp.context.JspCoreContext;

public class TagFileClassInfo extends TagClassInfo {
    private TagInfo ti = null;
    
    public TagFileClassInfo(TagInfo ti) {
        super();
        this.ti = ti;
        implementsIterationTag = false;
        implementsBodyTag = false;
        implementsTryCatchFinally = false;
        implementsSimpleTag = true;
        implementsDynamicAttributes = ti.hasDynamicAttributes();
    }
    
    public String getTagClassName() {
        return ti.getTagClassName();
    }
    
    public String getSetterMethodName(String attributeName) throws JspCoreException {
        String setterMethodName = null;
        
        if (setterMethodNameMap == null) {
            setterMethodNameMap = new HashMap();
        }
        
        setterMethodName = (String)setterMethodNameMap.get(attributeName);
        
        if (setterMethodName == null) { 
            char chars[] = attributeName.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            setterMethodName = new String(chars);
            setterMethodName = "set" + setterMethodName;
            setterMethodNameMap.put(attributeName, setterMethodName);
        }
        return (setterMethodName);
    }
    
    public String getPropertyEditorClassName(String attributeName) throws JspCoreException {
        return (null);
    }
    
    //PK36246 && 417178 override method in TagClassInfo, but since we aren't loading a class right now, we don't have to worry about the classpath in the context
    public String getParameterClassName(String attributeName, JspCoreContext context) throws JspCoreException {
        String parameterClassName = null;
        
        if (parameterClassNameMap == null) {
            parameterClassNameMap = new HashMap();
        }
        
        parameterClassName = (String)parameterClassNameMap.get(attributeName);
        
        if (parameterClassName == null) {
            TagAttributeInfo[] attributeInfos = ti.getAttributes();
            for (int i = 0; i < attributeInfos.length; i++) {
                if (attributeInfos[i].getName().equals(attributeName)) {
                    //PK69319
                	if(attributeInfos[i].isFragment()){
                		parameterClassName = "javax.servlet.jsp.tagext.JspFragment";
                	} else{
                		parameterClassName = attributeInfos[i].getTypeName();
                	}
                    parameterClassNameMap.put(attributeName, parameterClassName);
                    break;
                }
            }
        }
        return (parameterClassName);
    }
}
