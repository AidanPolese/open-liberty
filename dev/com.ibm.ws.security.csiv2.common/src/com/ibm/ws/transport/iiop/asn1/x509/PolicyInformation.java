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
import com.ibm.ws.transport.iiop.asn1.DERObject;
import com.ibm.ws.transport.iiop.asn1.DERObjectIdentifier;
import com.ibm.ws.transport.iiop.asn1.DERSequence;

public class PolicyInformation
    extends ASN1Encodable
{
    private DERObjectIdentifier   policyIdentifier;
    private ASN1Sequence          policyQualifiers;

    public PolicyInformation(
        ASN1Sequence seq)
    {
        policyIdentifier = (DERObjectIdentifier)seq.getObjectAt(0);

        if (seq.size() > 1)
        {
            policyQualifiers = (ASN1Sequence)seq.getObjectAt(1);
        }
    }

    public PolicyInformation(
        DERObjectIdentifier policyIdentifier)
    {
        this.policyIdentifier = policyIdentifier;
    }

    public PolicyInformation(
        DERObjectIdentifier policyIdentifier,
        ASN1Sequence        policyQualifiers)
    {
        this.policyIdentifier = policyIdentifier;
        this.policyQualifiers = policyQualifiers;
    }

    public static PolicyInformation getInstance(
        Object obj)
    {
        if (obj == null || obj instanceof PolicyInformation)
        {
            return (PolicyInformation)obj;
        }

        return new PolicyInformation(ASN1Sequence.getInstance(obj));
    }

    public DERObjectIdentifier getPolicyIdentifier()
    {
        return policyIdentifier;
    }

    public ASN1Sequence getPolicyQualifiers()
    {
        return policyQualifiers;
    }

    /*
     * PolicyInformation ::= SEQUENCE {
     *      policyIdentifier   CertPolicyId,
     *      policyQualifiers   SEQUENCE SIZE (1..MAX) OF
     *              PolicyQualifierInfo OPTIONAL }
     */
    public DERObject toASN1Object()
    {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(policyIdentifier);

        if (policyQualifiers != null)
        {
            v.add(policyQualifiers);
        }

        return new DERSequence(v);
    }
}
