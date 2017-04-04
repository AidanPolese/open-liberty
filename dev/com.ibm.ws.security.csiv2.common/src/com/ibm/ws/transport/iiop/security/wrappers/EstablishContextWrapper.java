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
package com.ibm.ws.transport.iiop.security.wrappers;

import org.omg.CSI.EstablishContext;
import org.omg.CSI.IdentityToken;


/**
 * @version $Revision: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public class EstablishContextWrapper {

    private final EstablishContext establishMsg;
    private final IdentityToken identity_token;

    public EstablishContextWrapper(EstablishContext establishMsg) {
        this.establishMsg = establishMsg;
        this.identity_token = establishMsg.identity_token;
    }
}
