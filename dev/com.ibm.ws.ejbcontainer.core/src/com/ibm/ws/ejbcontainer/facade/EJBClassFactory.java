/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2009
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.facade;

/**
 * Provides the EJB Class factory required to support an EJB facade, exposing
 * another component model as an EJB. <p>
 */
public interface EJBClassFactory
{
    /**
     * Dynamically generates and loads all of the EJB interface and
     * implementation classes defined by the specified EJBConfiguration
     * parameter. <p>
     * 
     * This method may not be called until the first time the EJB is accessed. <p>
     * 
     * It is expected that the EJB interfaces and implementation classes will
     * be dynamically generated when this method is called, and loaded with
     * the application ClassLoader using the defineApplicationClass method. <p>
     * 
     * After this method has been called, the ClassLoader.loadClass method of
     * the application ClassLoader should succeed for the EJB interface and
     * implementation classes. <p>
     **/
    public void loadEJBClasses(ClassLoader moduleClassLoader,
                               EJBConfiguration ejbConfig);
}
