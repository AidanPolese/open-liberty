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

import com.ibm.ws.transport.iiop.asn1.ASN1Encodable;
import com.ibm.ws.transport.iiop.asn1.ASN1EncodableVector;
import com.ibm.ws.transport.iiop.asn1.ASN1Sequence;
import com.ibm.ws.transport.iiop.asn1.ASN1TaggedObject;
import com.ibm.ws.transport.iiop.asn1.DERObject;
import com.ibm.ws.transport.iiop.asn1.DERSequence;

public class CRLDistPoint
    extends ASN1Encodable
{
    ASN1Sequence  seq = null;

    public static CRLDistPoint getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static CRLDistPoint getInstance(
        Object  obj)
    {
        if (obj instanceof CRLDistPoint)
        {
            return (CRLDistPoint)obj;
        }
        else if (obj instanceof ASN1Sequence)
        {
            return new CRLDistPoint((ASN1Sequence)obj);
        }

        throw new IllegalArgumentException("unknown object in factory");
    }

    public CRLDistPoint(
        ASN1Sequence seq)
    {
        this.seq = seq;
    }

    public CRLDistPoint(
        DistributionPoint[] points)
    {
        ASN1EncodableVector  v = new ASN1EncodableVector();

        for (int i = 0; i != points.length; i++)
        {
            v.add(points[i]);
        }

        seq = new DERSequence(v);
    }

    /**
     * Return the distribution points making up the sequence.
     *
     * @return DistributionPoint[]
     */
    public DistributionPoint[] getDistributionPoints()
    {
        DistributionPoint[]    dp = new DistributionPoint[seq.size()];

        for (int i = 0; i != seq.size(); i++)
        {
            dp[i] = DistributionPoint.getInstance(seq.getObjectAt(i));
        }

        return dp;
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * CRLDistPoint ::= SEQUENCE SIZE {1..MAX} OF DistributionPoint
     * </pre>
     */
    public DERObject toASN1Object()
    {
        return seq;
    }
}
