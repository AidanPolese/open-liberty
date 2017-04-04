/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.failover;

import java.io.Serializable;

/**
 * SfFailoverKey is used to provide a serializable object that can be used
 * as a failover key. The key must provide a proper equals and hashcode
 * method so the this object can be used as a key in a HashMap implementation.
 */
public interface SfFailoverKey extends Serializable
{

}
