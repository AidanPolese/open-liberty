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
package com.ibm.ws.transport.iiop.security.config.css;

import java.io.Serializable;

import com.ibm.ws.transport.iiop.security.config.tss.TSSTransportMechConfig;

/**
 * @version $Rev: 503274 $ $Date: 2007-02-03 10:19:18 -0800 (Sat, 03 Feb 2007) $
 */
public interface CSSTransportMechConfig extends Serializable {

    short getSupports();

    short getRequires();

    boolean canHandle(TSSTransportMechConfig transMech, String clientMech);

    String getCantHandleMsg();

    void toString(String spaces, StringBuilder buf);

    public abstract String getSslConfigName();

}
