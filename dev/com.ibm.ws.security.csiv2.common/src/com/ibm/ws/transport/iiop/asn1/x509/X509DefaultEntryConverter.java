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

import java.io.IOException;

import com.ibm.ws.transport.iiop.asn1.DERBMPString;
import com.ibm.ws.transport.iiop.asn1.DERIA5String;
import com.ibm.ws.transport.iiop.asn1.DERObject;
import com.ibm.ws.transport.iiop.asn1.DERObjectIdentifier;
import com.ibm.ws.transport.iiop.asn1.DERPrintableString;
import com.ibm.ws.transport.iiop.asn1.DERUTF8String;

/**
 * The default converter for X509 DN entries when going from their
 * string value to
 */
public class X509DefaultEntryConverter
    extends X509NameEntryConverter
{
    /**
     * Apply default coversion for the given value depending on the oid
     * and the character range of the value.
     *
     * @param oid the object identifier for the DN entry
     * @param value the value associated with it
     * @return the ASN.1 equivalent for the string value.
     */
    public DERObject getConvertedValue(
        DERObjectIdentifier  oid,
        String               value)
    {
        if (value.length() != 0 && value.charAt(0) == '#')
        {
            try
            {
                return convertHexEncoded(value, 1);
            }
            catch (IOException e)
            {
                throw new RuntimeException("can't recode value for oid " + oid.getId(), e);
            }
        }
        else if (oid.equals(X509Name.EmailAddress))
        {
            return new DERIA5String(value);
        }
        else if (canBePrintable(value))
        {
            return new DERPrintableString(value);
        }
        else if (canBeUTF8(value))
        {
            return new DERUTF8String(value);
        }

        return new DERBMPString(value);
    }
}
