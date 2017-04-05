/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1998
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer;

public interface EJBPMICollaboratorFactory {
    /**
     * Create an instance of PmiBean
     */
    public EJBPMICollaborator createPmiBean(EJBComponentMetaData data, String containerName);

    /**
     * Return a previously created object
     */
    public EJBPMICollaborator getPmiBean(String uniqueJ2eeName, String containerName);

    public void removePmiModule(Object mod);

}
