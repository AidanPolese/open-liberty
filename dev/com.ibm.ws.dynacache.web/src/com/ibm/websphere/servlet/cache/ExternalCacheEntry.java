// 1.8, 2/10/05
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N01, 5733-W60 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.websphere.servlet.cache;

import java.io.Serializable;
import java.util.Vector;

/**
 * This is a simple struct object that contains url, html and header
 * members for an external cache entry.
 * @ibm-api 
 */
public class ExternalCacheEntry implements Serializable {
    
    private static final long serialVersionUID = 1342185474L;
    
    /**
     * This is the host header as received in the request
     * @ibm-api 
     */
    public String host = null;

    /**
     * This is the uri part of the entry
     * @ibm-api 
     */
    public String uri = null;

    /**
     * This is the content (html) part of the entry
     * @ibm-api 
     */
    public byte[] content = null;

    /**
     * This hashtable contains the header fields needed for caching.
     * @ibm-api 
     */
    public Vector[] headerTable = null;
}
