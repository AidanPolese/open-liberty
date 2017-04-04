/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javamail.j2ee;

import org.osgi.framework.ServiceRegistration;

/**
 * Interface to register an MBean for a JavaMail session
 */
public interface MailSessionRegistrar {

    /**
     * Register an MBean for a mail session
     */
    ServiceRegistration<?> registerJavaMailMBean(String mailSessionID);
}
