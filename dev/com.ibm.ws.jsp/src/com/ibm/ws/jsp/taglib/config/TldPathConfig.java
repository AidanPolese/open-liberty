//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.taglib.config;

import java.util.ArrayList;
import java.util.List;

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
    
    public List getAvailabilityConditionList() {
        return availabilityConditionList;
    }

    public String getTldPath() {
        return tldPath;
    }
    
    public String getUri() {
        return uri;
    }

    public void setUri(String string) {
        uri = string;
    }
    
    public boolean containsListenerDefs() {
        return containsListenerDefs;
    }
}
