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

package com.ibm.ws.transport.iiop.asn1.oiw;

import com.ibm.ws.transport.iiop.asn1.DERObjectIdentifier;

public interface OIWObjectIdentifiers
{
    // id-SHA1 OBJECT IDENTIFIER ::=
    //   {iso(1) identified-organization(3) oiw(14) secsig(3) algorithms(2) 26 }    //
    static final DERObjectIdentifier    idSHA1                  = new DERObjectIdentifier("1.3.14.3.2.26");

    static final DERObjectIdentifier    dsaWithSHA1             = new DERObjectIdentifier("1.3.14.3.2.27");

    // ElGamal Algorithm OBJECT IDENTIFIER ::=
    // {iso(1) identified-organization(3) oiw(14) dirservsig(7) algorithm(2) encryption(1) 1 }
    //
    static final DERObjectIdentifier    elGamalAlgorithm        = new DERObjectIdentifier("1.3.14.7.2.1.1");

}
