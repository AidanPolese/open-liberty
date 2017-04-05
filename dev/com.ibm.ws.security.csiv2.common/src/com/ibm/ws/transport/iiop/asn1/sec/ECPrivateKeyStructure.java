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

package com.ibm.ws.transport.iiop.asn1.sec;

import java.math.BigInteger;

import com.ibm.ws.transport.iiop.asn1.*;

/**
 * the elliptic curve private key object from SEC 1
 */
public class ECPrivateKeyStructure
    extends ASN1Encodable
{
    private ASN1Sequence  seq;

    public ECPrivateKeyStructure(
        ASN1Sequence  seq)
    {
        this.seq = seq;
    }

    public ECPrivateKeyStructure(
        BigInteger  key)
    {
        byte[]  bytes = key.toByteArray();

        if (bytes[0] == 0)
        {
            byte[]  tmp = new byte[bytes.length - 1];

            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }

        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(new DERInteger(1));
        v.add(new DEROctetString(bytes));

        seq = new DERSequence(v);
    }

    public BigInteger getKey()
    {
        ASN1OctetString  octs = (ASN1OctetString)seq.getObjectAt(1);

        BigInteger  k = new BigInteger(1, octs.getOctets());

        return k;
    }

    public DERObject toASN1Object()
    {
        return seq;
    }
}
