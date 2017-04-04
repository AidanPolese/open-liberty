//IBM Confidential OCO Source Material
//  5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//  The source code for this program is not published or otherwise divested
//  of its trade secrets, irrespective of what has been deposited with the
//  U.S. Copyright Office.
//
//
//  Changes for defect 198932 "Support v5 JSP reload logic for v5 apps in 6.0"
//      added variables servlet2_2, servletEngineReloadEnabled, 
//          servletEngineReloadInterval, applicationDisplayName
package com.ibm.ws.jsp.runtime.metadata;

import java.util.Map;

import com.ibm.ws.jsp.Constants;
import com.ibm.wsspi.webcontainer.metadata.BaseJspComponentMetaData;

public class JspComponentMetaData extends BaseJspComponentMetaData {
    //in parent private List jspPropertyGroups = null;
    private Map looseLibs = null;
    //in parent private Map jspTaglibs = null;
    private boolean servletEngineReloadEnabled = Constants.DEFAULT_RELOAD_ENABLED;
    private boolean servlet2_2 = false;
    private long servletEngineReloadInterval = Constants.DEFAULT_RELOAD_INTERVAL;
    private String applicationDisplayName = null;
    private boolean syncToThread = false;

    /*
     * methods in parent
     * public List getJspPropertyGroups() {
     * return jspPropertyGroups;
     * }
     * 
     * public Map getJspTaglibs() {
     * return jspTaglibs;
     * }
     * 
     * public void setJspPropertyGroups(List jspPropertyGroups) {
     * this.jspPropertyGroups = jspPropertyGroups;
     * }
     * 
     * public void setJspTaglibs(Map jspTagLibs) {
     * this.jspTaglibs = jspTagLibs;
     * }
     */

    public Map getLooseLibs() {
        return looseLibs;
    }

    public void setLooseLibs(Map looseLibs) {
        this.looseLibs = looseLibs;
    }

    public boolean isServlet2_2() {
        return servlet2_2;
    }

    public boolean isServletEngineReloadEnabled() {
        return servletEngineReloadEnabled;
    }

    public void setServlet2_2(boolean servlet2_2) {
        this.servlet2_2 = servlet2_2;
    }

    public void setServletEngineReloadEnabled(boolean servletEngineReloadEnabled) {
        this.servletEngineReloadEnabled = servletEngineReloadEnabled;
    }

    public long getServletEngineReloadInterval() {
        return servletEngineReloadInterval;
    }

    public void setServletEngineReloadInterval(long servletEngineReloadInterval) {
        this.servletEngineReloadInterval = servletEngineReloadInterval;
    }

    public String getApplicationDisplayName() {
        return applicationDisplayName;
    }

    public void setApplicationDisplayName(String applicationDisplayName) {
        this.applicationDisplayName = applicationDisplayName;
    }

}