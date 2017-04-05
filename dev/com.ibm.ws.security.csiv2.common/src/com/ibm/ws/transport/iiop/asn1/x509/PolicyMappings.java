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

import java.util.Hashtable;
import java.util.Enumeration;

import com.ibm.ws.transport.iiop.asn1.ASN1Encodable;
import com.ibm.ws.transport.iiop.asn1.ASN1EncodableVector;
import com.ibm.ws.transport.iiop.asn1.ASN1Sequence;
import com.ibm.ws.transport.iiop.asn1.DERObject;
import com.ibm.ws.transport.iiop.asn1.DERObjectIdentifier;
import com.ibm.ws.transport.iiop.asn1.DERSequence;

/**
 * PolicyMappings V3 extension, described in RFC3280.
 * <pre>
 *    PolicyMappings ::= SEQUENCE SIZE (1..MAX) OF SEQUENCE {
 *      issuerDomainPolicy      CertPolicyId,
 *      subjectDomainPolicy     CertPolicyId }
 * </pre>
 *
 * @see <a href="http://www.faqs.org/rfc/rfc3280.txt">RFC 3280, section 4.2.1.6</a>
 */
public class PolicyMappings
    extends ASN1Encodable
{
   ASN1Sequence seq = null;

   /**
    * Creates a new <code>PolicyMappings</code> instance.
    *
    * @param seq an <code>ASN1Sequence</code> constructed as specified
    * in RFC 3280
    */
   public PolicyMappings (ASN1Sequence seq)
      {
         this.seq = seq;
      }

   /**
    * Creates a new <code>PolicyMappings</code> instance.
    *
    * @param mappings a <code>HashMap</code> value that maps
    * <code>String</code> oids
    * to other <code>String</code> oids.
    */
   public PolicyMappings (Hashtable mappings)
      {
         ASN1EncodableVector dev = new ASN1EncodableVector();
         Enumeration it = mappings.keys();

         while (it.hasMoreElements()) {
            String idp = (String) it.nextElement();
            String sdp = (String) mappings.get(idp);
            ASN1EncodableVector dv = new ASN1EncodableVector();
            dv.add(new DERObjectIdentifier(idp));
            dv.add(new DERObjectIdentifier(sdp));
            dev.add(new DERSequence(dv));
         }

         seq = new DERSequence(dev);
      }

   public DERObject toASN1Object()
      {
         return seq;
      }
}
