/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2007, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * This interface is internal API and is being maintained for strict API
 * compatibility only. It will be removed in a subsequent release.
 */
@Deprecated
public interface EJBModuleInitializationCollaborator {

    public void starting(EJBModuleMetaData collabData);

    public void started(EJBModuleMetaData collabData);

    public void stopping(EJBModuleMetaData collabData);

    public void stopped(EJBModuleMetaData collabData);

}
