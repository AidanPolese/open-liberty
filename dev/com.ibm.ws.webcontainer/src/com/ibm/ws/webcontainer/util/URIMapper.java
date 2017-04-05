//IBM Confidential OCO Source Material
//5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2003
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//

//Code added as part of LIDB 2283-4
//Code added as part of LIDB 2283-4
//  CHANGE HISTORY
//  Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//  PM06111       02/08/2010    mmulholl            Add new constructor to support URI Matching with string keys  

package com.ibm.ws.webcontainer.util;

import java.util.Iterator;

/**
 * @author asisin
 * 
 */
public class URIMapper {

    protected URIMatcher matcher;

    /**
     * This constructor will create a matcher thats optimized for speed
     * but will not scale very well for very large data sets.
     * 
     */
    public URIMapper() {
        matcher = new URIMatcher(false);
    }

    /**
     * Constructor with capability of contolling the scability vs speed
     * characteristics of the matcher.
     * 
     * @param scalable Set to true for scalable matcher at the expense of speed
     */
    public URIMapper(boolean scalable) {
        matcher = new URIMatcher(scalable);
    }

    /**
     * PM06111 Add new contructor
     * Constructor with capability of contolling the scability vs speed
     * characteristics of the matcher.
     * 
     * @param scalable Set to true for scalable matcher at the expense of speed
     * @param useStringKeys Set to true to indes paths using their string value and not hashCode
     */
    public URIMapper(boolean scalable, boolean useStringKeys) {
        matcher = new URIMatcher(scalable, useStringKeys);
    }

    /**
     * @see com.ibm.ws.core.RequestMapper#addMapping(String, Object)
     */
    public void addMapping(String path, Object target) throws Exception {
        matcher.put(path, target);
    }

    public Object replaceMapping(String path, Object target) throws Exception {
        return matcher.replace(path, target);
    }

    /**
     * @see com.ibm.ws.core.RequestMapper#removeMapping(String)
     */
    public void removeMapping(String path) {
        matcher.remove(path);
    }

    /**
     * @see com.ibm.ws.core.RequestMapper#targetMappings()
     */
    public Iterator targetMappings() {
        return matcher.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.core.RequestMapper#exists(java.lang.String)
     */
    public boolean exists(String path) {
        return matcher.exists(path);
    }

}
