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
import com.ibm.ws.transport.iiop.asn1.DERTaggedObject;

public class V2Form
    extends ASN1Encodable
{
    GeneralNames        issuerName;
    IssuerSerial        baseCertificateID;
    ObjectDigestInfo    objectDigestInfo;

    public static V2Form getInstance(
        ASN1TaggedObject obj,
        boolean          explicit)
    {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static V2Form getInstance(
        Object  obj)
    {
        if (obj == null || obj instanceof V2Form)
        {
            return (V2Form)obj;
        }
        else if (obj instanceof ASN1Sequence)
        {
            return new V2Form((ASN1Sequence)obj);
        }

        throw new IllegalArgumentException("unknown object in factory");
    }

    public V2Form(
        GeneralNames    issuerName)
    {
        this.issuerName = issuerName;
    }

    public V2Form(
        ASN1Sequence seq)
    {
        int    index = 0;

        if (!(seq.getObjectAt(0) instanceof ASN1TaggedObject))
        {
            index++;
            this.issuerName = GeneralNames.getInstance(seq.getObjectAt(0));
        }

        for (int i = index; i != seq.size(); i++)
        {
            ASN1TaggedObject o = (ASN1TaggedObject)seq.getObjectAt(i);
            if (o.getTagNo() == 0)
            {
                baseCertificateID = IssuerSerial.getInstance(o, false);
            }
            else if (o.getTagNo() == 1)
            {
                objectDigestInfo = ObjectDigestInfo.getInstance(o, false);
            }
        }
    }

    public GeneralNames getIssuerName()
    {
        return issuerName;
    }

    public IssuerSerial getBaseCertificateID()
    {
        return baseCertificateID;
    }

    public ObjectDigestInfo getObjectDigestInfo()
    {
        return objectDigestInfo;
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     *  V2Form ::= SEQUENCE {
     *       issuerName            GeneralNames  OPTIONAL,
     *       baseCertificateID     [0] IssuerSerial  OPTIONAL,
     *       objectDigestInfo      [1] ObjectDigestInfo  OPTIONAL
     *         -- issuerName MUST be present in this profile
     *         -- baseCertificateID and objectDigestInfo MUST NOT
     *         -- be present in this profile
     *  }
     * </pre>
     */
    public DERObject toASN1Object()
    {
        ASN1EncodableVector  v = new ASN1EncodableVector();

        if (issuerName != null)
        {
            v.add(issuerName);
        }

        if (baseCertificateID != null)
        {
            v.add(new DERTaggedObject(false, 0, baseCertificateID));
        }

        if (objectDigestInfo != null)
        {
            v.add(new DERTaggedObject(false, 1, objectDigestInfo));
        }

        return new DERSequence(v);
    }
}
