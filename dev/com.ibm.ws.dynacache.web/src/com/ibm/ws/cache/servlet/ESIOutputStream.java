// Proprietary Statement:
//
// Licensed Material - Property of IBM
//
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) Copyright IBM Corp. 2004, 2009
// All Rights Reserved.
// U.S. Government users - RESTRICTED RIGHTS - Use, Duplication, or
// Disclosure restricted by GSA-ADP schedule contract with IBM Corp.
// Status = H28W510
//
// Change Activity:
//
// $PQ91098 (248534) H28W601, 20050302,PDEK; Created for ESI Invalidator on z
// 264177  H28W601, 20050329,PDEK; contactProxy() function should only be called on z
// F743-13024.1 H28W800, 20091026,bkail; getGlobalORB will return org.omg.CORBA.ORB

package com.ibm.ws.cache.servlet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class ESIOutputStream extends DataOutputStream
{
    private final static TraceComponent tc = Tr.register(ESIOutputStream.class, "WebSphere Dynamic Cache", "com.ibm.ws.cache.resources.dynacache");    
    
    private static boolean proxyIsUp = false;
    
    public ESIOutputStream (OutputStream out) throws IOException
    {
     
        super(out);
        
        if (tc.isEntryEnabled()) 
            Tr.entry(tc, "constructor");


        if (tc.isEntryEnabled()) 
            Tr.exit(tc, "constructor");
    }
    
	public void flush() throws IOException {
		if (tc.isEntryEnabled())
			Tr.entry(tc, "flush() " + proxyIsUp);

		super.out.flush();

		if (tc.isEntryEnabled())
			Tr.exit(tc, "flush()");

		return;

	}

	public ESIInputStream flushWithResponse() throws IOException {
		if (tc.isEntryEnabled())
			Tr.entry(tc, "flushWithResponse() " + proxyIsUp);

		super.out.flush(); // ... but just in case.

		if (tc.isEntryEnabled())
			Tr.exit(tc, "flushWithResponse()");

		return null;

	}
}
