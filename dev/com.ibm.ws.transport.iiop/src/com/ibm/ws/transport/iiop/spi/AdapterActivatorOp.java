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
package com.ibm.ws.transport.iiop.spi;

/**
 * Like org.omg.PortableServer.AdapterActivatorOperations but supplies our ORBRef as well.
 */
public interface AdapterActivatorOp {

    boolean unknown_adapter(org.omg.PortableServer.POA parent, String name, ORBRef orbRef);

}
