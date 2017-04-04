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
package com.ibm.ws.transport.iiop.spi;

import java.util.Map;

import org.omg.PortableServer.POA;

/**
 * @version $Revision: 465172 $ $Date: 2006-10-18 01:16:14 -0700 (Wed, 18 Oct 2006) $
 */
public interface ORBRef extends ClientORBRef {

    POA getPOA();

    Map<String, Object> getExtraConfig();

}
