/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.metadata;

import com.ibm.ejs.container.EJBMethodInfoImpl;

/**
 *
 */
public class OSGiEJBMethodMetaDataImpl extends EJBMethodInfoImpl {
    String runAs = null;
    boolean useCallerPrincipal = false;
    boolean useSystemPrincipal = false;

    /**
     * @param slotSize
     */
    public OSGiEJBMethodMetaDataImpl(int slotSize) {
        super(slotSize);
    }

    @Override
    public String getRunAs() {
        return (this.runAs != null) ? this.runAs : super.getRunAs();
    }

    public void setRunAs(String runAsId) {
        this.useCallerPrincipal = false;
        this.useSystemPrincipal = false;
        this.runAs = runAsId;
    }

    /**
     * Return a boolean indicating if the identity for the execution of this
     * method is to come from the caller.
     * 
     * @return boolean indicating if the identity for the execution of this
     *         method is to come from the caller.
     */
    @Override
    public boolean isUseCallerPrincipal() {
        return useCallerPrincipal;
    }

    public void setUseCallerPrincipal() {
        this.useCallerPrincipal = true;
        this.useSystemPrincipal = false;
        this.runAs = null;
    }

    /**
     * Return a boolean indicating if the identity for the execution of this
     * method is the system principle.
     * 
     * @return boolean indicating if the identity for the execution of this
     *         method is the system principle.
     */
    @Override
    public boolean isUseSystemPrincipal() {
        return useSystemPrincipal;
    }

    public void setUseSystemPrincipal() {
        this.useCallerPrincipal = false;
        this.useSystemPrincipal = true;
        this.runAs = null;
    }
}
