/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2007
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webservices.configuration;

/**
 * This class will be the factory interace that will be implemented by
 * classes wishing to return an instance of a class that is a wrapper for
 * web service implementations within an EJB module. This will be registered
 * with the WASWebServicesBind class and will be available via a static method
 * on that class. The EJB container will need the wrapper class in order for
 * the web services code to make any invocation within the EJB container.
 */

public interface WASEJBWrapperFactory {

    public Class getEJBWrapperClass();

}
