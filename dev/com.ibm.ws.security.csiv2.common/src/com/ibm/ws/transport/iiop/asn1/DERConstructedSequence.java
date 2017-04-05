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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @deprecated use DERSequence.
 */
public class DERConstructedSequence
    extends ASN1Sequence
{
    public void addObject(
        DEREncodable obj)
    {
        super.addObject(obj);
    }

    public int getSize()
    {
        return size();
    }

    /*
     * A note on the implementation:
     * <p>
     * As DER requires the constructed, definite-length model to
     * be used for structured types, this varies slightly from the
     * ASN.1 descriptions given. Rather than just outputing SEQUENCE,
     * we also have to specify CONSTRUCTED, and the objects length.
     */
    void encode(
        DEROutputStream out)
        throws IOException
    {
        ByteArrayOutputStream   bOut = new ByteArrayOutputStream();
        DEROutputStream         dOut = new DEROutputStream(bOut);
        Enumeration             e = this.getObjects();

        while (e.hasMoreElements())
        {
            Object    obj = e.nextElement();

            dOut.writeObject(obj);
        }

        dOut.close();

        byte[]  bytes = bOut.toByteArray();

        out.writeEncoded(SEQUENCE | CONSTRUCTED, bytes);
    }
}
