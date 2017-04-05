/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi;

import javax.enterprise.inject.spi.DeploymentException;

public class CDIDeploymentRuntimeException extends DeploymentException {

    private static final long serialVersionUID = 5729749912023008025L;

    public CDIDeploymentRuntimeException(String message) {
        super(message);
    }

    public CDIDeploymentRuntimeException(Throwable t) {
        super(t);
    }

    public CDIDeploymentRuntimeException(String message, Throwable t) {
        super(message, t);
    }

}
