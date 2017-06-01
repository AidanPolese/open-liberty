//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.context;

/**
 * The JspClassloaderContext interface allows the control of what ClassLoader is used to load JSP dependent
 * Classes (taglibrary classes etc.)
 */
public interface JspClassloaderContext {
    /**
     * @return ClassLoader The ClassLoader to be used to load JSP dependent classes.
     */
    ClassLoader getClassLoader();
    /**
     * @return String The classpath to be used to compile the generated servlet.
     */
    String getClassPath();
    /**
     * @return String The Optional Optimized classpath to be used to compile the generated servlet.
     */
    String getOptimizedClassPath();
    /**
     * @return boolean Indicates whether this classloader supports predefining classes.
     */
    boolean isPredefineClassEnabled();
    /**
     * @return byte[] Returns the predefined class bytes.
     */
    byte[] predefineClass(String className, byte[] classData);
}
