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

import com.ibm.ws.transport.iiop.asn1.DERObjectIdentifier;

/**
 * The KeyPurposeId object.
 * <pre>
 *     KeyPurposeId ::= OBJECT IDENTIFIER
 * </pre>
 */
public class KeyPurposeId
    extends DERObjectIdentifier
{
    private static final String id_kp = "1.3.6.1.5.5.7.3";

    private KeyPurposeId(
        String  id)
    {
        super(id);
    }

    public static final KeyPurposeId anyExtendedKeyUsage = new KeyPurposeId(X509Extensions.ExtendedKeyUsage.getId() + ".0");
    public static final KeyPurposeId id_kp_serverAuth = new KeyPurposeId(id_kp + ".1");
    public static final KeyPurposeId id_kp_clientAuth = new KeyPurposeId(id_kp + ".2");
    public static final KeyPurposeId id_kp_codeSigning = new KeyPurposeId(id_kp + ".3");
    public static final KeyPurposeId id_kp_emailProtection = new KeyPurposeId(id_kp + ".4");
    public static final KeyPurposeId id_kp_ipsecEndSystem = new KeyPurposeId(id_kp + ".5");
    public static final KeyPurposeId id_kp_ipsecTunnel = new KeyPurposeId(id_kp + ".6");
    public static final KeyPurposeId id_kp_ipsecUser = new KeyPurposeId(id_kp + ".7");
    public static final KeyPurposeId id_kp_timeStamping = new KeyPurposeId(id_kp + ".8");
    public static final KeyPurposeId id_kp_OCSPSigning = new KeyPurposeId(id_kp + ".9");

    //
    // microsoft key purpose ids
    //
    public static final KeyPurposeId id_kp_smartcardlogon = new KeyPurposeId("1.3.6.1.4.1.311.20.2.2");
}
