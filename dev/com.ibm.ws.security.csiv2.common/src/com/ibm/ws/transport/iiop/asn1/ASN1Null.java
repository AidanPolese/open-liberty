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

/**
 * A NULL object.
 */
public abstract class ASN1Null
    extends DERObject
{
    public ASN1Null()
    {
    }

    public int hashCode()
    {
        return 0;
    }

    public boolean equals(
        Object o)
    {
        if ((o == null) || !(o instanceof ASN1Null))
        {
            return false;
        }

        return true;
    }

    abstract void encode(DEROutputStream out)
        throws IOException;
}
