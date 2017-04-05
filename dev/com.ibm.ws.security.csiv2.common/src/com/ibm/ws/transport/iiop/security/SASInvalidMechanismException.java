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
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security;

import org.omg.CORBA.NO_PERMISSION;

/**
 * @version $Revision: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public class SASInvalidMechanismException extends SASException {

    public SASInvalidMechanismException() {
        super(2, new NO_PERMISSION());
    }

    /**
     * @param message the message used in the creation of the NO_PERMISSION exception.
     * @param noPermissionMinorCode the minor code used in the creation of the NO_PERMISSION exception.
     */
    public SASInvalidMechanismException(String message, int noPermissionMinorCode) {
        super(2, new org.omg.CORBA.NO_PERMISSION(message,
                        noPermissionMinorCode,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO));
    }

}
