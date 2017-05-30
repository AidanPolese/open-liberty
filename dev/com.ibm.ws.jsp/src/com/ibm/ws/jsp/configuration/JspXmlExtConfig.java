//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

/*
 * Created on May 6, 2003
 *
 * JspWebXmlConfig.java
 */
// defect 400645 "Batchcompiler needs to get webcon custom props"  2004/10/25 Scott Johnson
// 395182.2  70FVT: make servlet 2.3 compatible with JSP 2.1 for migration 2007/02/07 Scott Johnson
package com.ibm.ws.jsp.configuration;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.ibm.ws.jsp.JspOptions;

/**
 * @author Scott Johnson
 *
 * API for retrieving JSP configuration elements from both web.xml and the extensions document
 */
public interface JspXmlExtConfig
{
    public Map getTagLibMap();
    
    public List getJspPropertyGroups();
    
    public boolean isServlet24();
    public boolean isServlet24_or_higher();    
    public JspOptions getJspOptions();
    
    public List getJspFileExtensions();
    
    public boolean containsServletClassName(String servletClassName);
    
    //defect 400645
    public void setWebContainerProperties(Properties webConProperties);
    
    public Properties getWebContainerProperties();
    //defect 400645
    
    //only used during runtime when JCDI will use this to determine whether to wrap the ExpressionFactory
    public void setJCDIEnabledForRuntimeCheck(boolean b);
    public boolean isJCDIEnabledForRuntimeCheck();
}
