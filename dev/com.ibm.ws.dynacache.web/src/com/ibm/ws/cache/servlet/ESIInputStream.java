// Proprietary Statement:
//
// Licensed Material - Property of IBM
//
// 5655-I35 (C) Copyright IBM Corp. 2004
// All Rights Reserved.
// U.S. Government users - RESTRICTED RIGHTS - Use, Duplication, or
// Disclosure restricted by GSA-ADP schedule contract with IBM Corp.
// Status = H28W510
//
// Change Activity:
//
// $PQ91098 (248534) H28W601, 20050302,PDEK; Created for ESI Invalidator fix

package com.ibm.ws.cache.servlet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class ESIInputStream extends DataInputStream
{
    private final static TraceComponent tc = Tr.register(ESIInputStream.class, "WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");
    
    public ESIInputStream (InputStream in)
    {
        super(in);   
    }

    public ESIInputStream (byte[] in)                                 
    {
        super(new ByteArrayInputStream(in));
    }

}


