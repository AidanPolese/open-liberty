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

package com.ibm.ws.transport.iiop.asn1.x509;

import java.math.BigInteger;
import java.util.Enumeration;

import com.ibm.ws.transport.iiop.asn1.ASN1Encodable;
import com.ibm.ws.transport.iiop.asn1.ASN1EncodableVector;
import com.ibm.ws.transport.iiop.asn1.ASN1Sequence;
import com.ibm.ws.transport.iiop.asn1.ASN1TaggedObject;
import com.ibm.ws.transport.iiop.asn1.DERInteger;
import com.ibm.ws.transport.iiop.asn1.DERObject;
import com.ibm.ws.transport.iiop.asn1.DERSequence;

public class RSAPublicKeyStructure
    extends ASN1Encodable
{
    private BigInteger  modulus;
    private BigInteger  publicExponent;

    public static RSAPublicKeyStructure getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static RSAPublicKeyStructure getInstance(
        Object obj)
    {
        if(obj == null || obj instanceof RSAPublicKeyStructure)
        {
            return (RSAPublicKeyStructure)obj;
        }

        if(obj instanceof ASN1Sequence)
        {
            return new RSAPublicKeyStructure((ASN1Sequence)obj);
        }

        throw new IllegalArgumentException("Invalid RSAPublicKeyStructure: " + obj.getClass().getName());
    }

    public RSAPublicKeyStructure(
        BigInteger  modulus,
        BigInteger  publicExponent)
    {
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }

    public RSAPublicKeyStructure(
        ASN1Sequence  seq)
    {
        Enumeration e = seq.getObjects();

        modulus = ((DERInteger)e.nextElement()).getPositiveValue();
        publicExponent = ((DERInteger)e.nextElement()).getPositiveValue();
    }

    public BigInteger getModulus()
    {
        return modulus;
    }

    public BigInteger getPublicExponent()
    {
        return publicExponent;
    }

    /**
     * This outputs the key in PKCS1v2 format.
     * <pre>
     *      RSAPublicKey ::= SEQUENCE {
     *                          modulus INTEGER, -- n
     *                          publicExponent INTEGER, -- e
     *                      }
     * </pre>
     * <p>
     */
    public DERObject toASN1Object()
    {
        ASN1EncodableVector  v = new ASN1EncodableVector();

        v.add(new DERInteger(getModulus()));
        v.add(new DERInteger(getPublicExponent()));

        return new DERSequence(v);
    }
}
