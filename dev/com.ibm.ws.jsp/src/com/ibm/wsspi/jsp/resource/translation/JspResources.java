//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.resource.translation;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.ibm.wsspi.jsp.resource.JspInputSource;

/**
 * Implementions of this interface are use by the JSP Container to handle JSP Resources 
 * such as the jsps input source, the generated source file and the class name used for the
 * generated servlet. It is also used by the JSP container to check if a jsp is outdated and
 * synchronize the resources if a translation occurs. 
 */
public interface JspResources {
    /**
     * Returns the jsp input source used to generated the jsp servlet.
     * 
     * @return JspInputSource
     */
    JspInputSource getInputSource();
    
    /**
     * Returns the File object for the generated jsp source file.
     * @return File
     */
    File getGeneratedSourceFile();
    
    /**
     * Returns the classname to be used when generation the jsp servlet.
     * 
     * @return String 
     */
    String getClassName();
    
    
	/**
	 * Returns the package name to be used when generation the jsp servlet.
	 * 
	 * @return String 
	 */
	String getPackageName();
    
    /**
     * The JSP Container calls this method to check if a jsp needs to be translated.
     * 
     * @return boolean
     */
    boolean isOutdated();
    
    /**
     * The JSP Container calls this method once a translation has occured to synchronzed the
     * generated source files with the input source.
     */
    void sync();
    
    /**
     * When running in the WebSphere runtime the JSP Container will call this method
     * to set the current request ohject that is being used.
     * 
     * @param request
     */
    void setCurrentRequest(HttpServletRequest request);

    /**
     * The JSP Container will call this method to determine if the jsp has been
     * externally translated and needs to be reloaded.
     * 
     * @return boolean
     */
    boolean isExternallyTranslated();
}
