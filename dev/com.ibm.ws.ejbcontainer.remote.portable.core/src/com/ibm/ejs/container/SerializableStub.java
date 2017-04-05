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

import javax.rmi.CORBA.Stub;

/**
 * An extended stub that is capable of reconnecting itself when marshalled via
 * write_value/read_value.
 */
public abstract class SerializableStub
                extends Stub
{
    private final Class ivClass;

    public SerializableStub(Class klass)
    {
        ivClass = klass;
    }

    protected Object writeReplace()
    {
        return new SerializedStub(this, ivClass);
    }
}
