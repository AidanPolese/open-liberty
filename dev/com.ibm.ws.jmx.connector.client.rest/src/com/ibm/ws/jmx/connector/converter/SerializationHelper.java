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
package com.ibm.ws.jmx.connector.converter;

import com.ibm.ws.jmx.connector.datatypes.ConversionException;

public interface SerializationHelper {

    public Object readObject(Object in, int blen, byte[] binary) throws ClassNotFoundException, ConversionException;

}
