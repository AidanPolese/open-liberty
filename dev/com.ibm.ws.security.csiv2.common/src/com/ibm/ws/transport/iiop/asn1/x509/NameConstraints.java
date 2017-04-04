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

import java.util.Enumeration;

import com.ibm.ws.transport.iiop.asn1.ASN1Encodable;
import com.ibm.ws.transport.iiop.asn1.ASN1EncodableVector;
import com.ibm.ws.transport.iiop.asn1.ASN1Sequence;
import com.ibm.ws.transport.iiop.asn1.ASN1TaggedObject;
import com.ibm.ws.transport.iiop.asn1.DERObject;
import com.ibm.ws.transport.iiop.asn1.DERSequence;
import com.ibm.ws.transport.iiop.asn1.DERTaggedObject;

public class NameConstraints
    extends ASN1Encodable
{
    ASN1Sequence    permitted, excluded;

    public NameConstraints(
        ASN1Sequence    seq)
    {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements())
        {
            ASN1TaggedObject    o = (ASN1TaggedObject)e.nextElement();
            switch (o.getTagNo())
            {
            case 0:
                permitted = ASN1Sequence.getInstance(o, false);
                break;
            case 1:
                excluded = ASN1Sequence.getInstance(o, false);
                break;
            }
        }
    }

    public ASN1Sequence getPermittedSubtrees()
    {
        return permitted;
    }

    public ASN1Sequence getExcludedSubtrees()
    {
        return excluded;
    }

    /*
     * NameConstraints ::= SEQUENCE {
     *      permittedSubtrees       [0]     GeneralSubtrees OPTIONAL,
     *      excludedSubtrees        [1]     GeneralSubtrees OPTIONAL }
     */
    public DERObject toASN1Object()
    {
        ASN1EncodableVector   v = new ASN1EncodableVector();

        if (permitted != null)
        {
            v.add(new DERTaggedObject(false, 0, permitted));
        }

        if (excluded != null)
        {
            v.add(new DERTaggedObject(false, 1, excluded));
        }

        return new DERSequence(v);
    }
}
