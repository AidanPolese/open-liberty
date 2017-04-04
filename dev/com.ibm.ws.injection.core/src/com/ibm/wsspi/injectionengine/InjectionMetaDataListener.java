/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2008
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

/**
 * This interface will be implemented by components needing to be
 * notified upon the creation of InjectionMetaData. The InjectionMetaData
 * is available only after 'populateJavaNameSpace' has been called
 * for a given module or component.
 *
 */
public interface InjectionMetaDataListener {

    /**
     * This method will be called when InjectionMetaData has been created
     * for a module or component.
     */
    public void injectionMetaDataCreated(InjectionMetaData injectionMetaData) throws InjectionException;

}
