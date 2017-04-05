/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

import java.io.Serializable;

import javax.rmi.PortableRemoteObject;

/**
 * A wrapper for a stub. When a stub is read using read_value, the ORB will
 * not reconnect the stub. When rmic compatibility is enabled, stubs for
 * interfaces that are not RMI/IDL abstract interfaces will extend
 * SerializableStub, which has a writeReplace method to substitute an instance
 * of this object. The ORB will reconnect stubs stored in instance fields of
 * this serialized object.
 */
public class SerializedStub
                implements Serializable
{
    private static final long serialVersionUID = 3019532699780090519L;

    private final Object ivStub;
    private final Class ivClass;

    SerializedStub(Object stub, Class klass)
    {
        ivStub = stub;
        ivClass = klass;
    }

    private Object readResolve()
    {
        return PortableRemoteObject.narrow(ivStub, ivClass);
    }
}
