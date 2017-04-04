//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.taglib.config;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used in conjunction with a GlobalTagLibConfig and is used to provide specific path 
 * information for a tld file.
 */
public class TldPathConfig {
    private String tldPath = null;
    private String uri = null;
    private boolean containsListenerDefs = false;
    private List availabilityConditionList = null;    
    
    public TldPathConfig(String tldPath, String uri, String strContainsListenerDefs) {
        this.tldPath = tldPath;
        this.uri = uri;
        if (strContainsListenerDefs != null && strContainsListenerDefs.equalsIgnoreCase("true")) {
            containsListenerDefs = true;    
        }
        availabilityConditionList = new ArrayList();
    }
    
    /**
     * Gets the conditions as to when this tld is made available.
     * The condition can be the existence of a file within the web-inf directory or the existence of a servlet class.
     * @return List - a list of availability conditions
     */
    public List getAvailabilityConditionList() {
        return availabilityConditionList;
    }

    /**
     * Gets the relative path within the jar to the tld file
     * @return String - the relative path within the jar
     */
    public String getTldPath() {
        return tldPath;
    }
    
    /**
     * Gets the uri of the tld
     * @return String - the uri of the tld
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the uri for the tld
     * param string String - the uri for the tld
     */
    public void setUri(String string) {
        uri = string;
    }
    
    /**
     * Gets whether the tld contains any listener elements
     * return boolean - if the tld file contains any listener elements
     */
    public boolean containsListenerDefs() {
        return containsListenerDefs;
    }
}
