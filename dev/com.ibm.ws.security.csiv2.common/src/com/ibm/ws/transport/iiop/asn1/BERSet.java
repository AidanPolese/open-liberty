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

import java.io.IOException;
import java.util.Enumeration;

public class BERSet
    extends DERSet
{
    /**
     * create an empty sequence
     */
    public BERSet()
    {
    }

    /**
     * create a set containing one object
     */
    public BERSet(
        DEREncodable    obj)
    {
        super(obj);
    }

    /**
     * @param v - a vector of objects making up the set.
     */
    public BERSet(
        DEREncodableVector   v)
    {
        super(v, true);
    }

    /**
     * @param v - a vector of objects making up the set.
     */
    BERSet(
        DEREncodableVector   v,
        boolean              needsSorting)
    {
        super(v, needsSorting);
    }

    /*
     */
    void encode(
        DEROutputStream out)
        throws IOException
    {
        if (out instanceof ASN1OutputStream || out instanceof BEROutputStream)
        {
            out.write(SET | CONSTRUCTED);
            out.write(0x80);

            Enumeration e = getObjects();
            while (e.hasMoreElements())
            {
                out.writeObject(e.nextElement());
            }

            out.write(0x00);
            out.write(0x00);
        }
        else
        {
            super.encode(out);
        }
    }
}
