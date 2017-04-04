/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000, 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

import java.util.Properties;

import javax.naming.Context;

public interface ComponentBindingInfoAccessor
                extends java.io.Serializable
{
    /**
     * getJavaNameSpaceContext returns the Context which can be used to
     * bind the JNS entires.
     */
    public Context getJavaNameSpaceContext();

    /**
     * getDeploymentData returns Bean deployment data as specified in
     * EJB specification.
     */
    public Object getDeploymentData();//89981

    /**
     * Return the message listener port name for this MDB is bound to.
     * 
     * @return String that is the message listener port name.
     */
    public String getMessageListenerPortName(); // d641277

    /**
     * Returns the message destination JNDI name for this MDB.
     * 
     * @return String that is the message destination JNDI name.
     */
    public String getMessageDestinationJndiName(); // d641277

    /**
     * Get the activation config properties for this MDB.
     * 
     * @return a java.util.Properties object where the key is
     *         the activation config property name and the value
     *         is the activation config property value. Note, a
     *         null reference is possible if there are no activation
     *         config properties found in the DD or in annotations
     *         for this MDB (e.g. if this a MDB 2.0 bean where
     *         activation config did not exist).
     */
    public Properties getActivationConfigProperties(); // d641277
}
