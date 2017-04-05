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

package com.ibm.ws.transport.iiop.asn1;

/**
 * Marker interface for CHOICE objects - if you implement this in a role your
 * own object any attempt to tag the object implicitly will convert the tag to
 * an explicit one as the encoding rules require.
 * <p>
 * If you use this interface your class should also implement the getInstance
 * pattern which takes a tag object and the tagging mode used.
 */
public interface ASN1Choice
{
    // marker interface
}
