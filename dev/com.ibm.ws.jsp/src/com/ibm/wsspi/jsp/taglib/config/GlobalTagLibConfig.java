//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.taglib.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to handle the enabling of a tag library that is available for all applications on a server.
 */
public class GlobalTagLibConfig {
    private String jarName = null;
    private URL jarURL = null;
    private ClassLoader classloader = null;
    private List tldPathList = null;
    
    public GlobalTagLibConfig() {
        tldPathList = new ArrayList();
    }
    
    /**
     * Returns a String containing the name of the jar
     * 
     * @return String - the name of the jar 
     */
    public String getJarName() {
        return jarName;
    }

    /**
     * Returns a List of all the tlds to be parsed within this jar
     * 
     * @return List - the tld files within the jar 
     */
    public List getTldPathList() {
        return tldPathList;
    }
    
    /**
     * Sets the jar name for this global tag library
     * 
     * @param string String - the name of the jar for this global tag library
     */
    public void setJarName(String string) {
        jarName = string;
    }

    /**
     * Sets the jar url for this global tag library
     * 
     * @param jarURL String - the url of the jar for this global tag library
     */
    public void setJarURL(URL jarURL) {
        this.jarURL = jarURL;
    }
    
    /**
     * Gets the jar url for this global tag library
     * 
     * @return URL - the url for this global tag library 
     */
    public URL getJarURL() {
        return jarURL;
    }
    
    /**
     * Gets the classloader for this global tag library
     * 
     * @return ClassLoader - the classloader for this global tag library 
     */
    public ClassLoader getClassloader() {
        return classloader;
    }

    /**
     * Sets the classloader for this global tag library
     * 
     * @param classloader ClassLoader - the classloader to be used for this global tag library 
     */
    public void setClassloader(ClassLoader classloader) {
        this.classloader = classloader;
    }
}
