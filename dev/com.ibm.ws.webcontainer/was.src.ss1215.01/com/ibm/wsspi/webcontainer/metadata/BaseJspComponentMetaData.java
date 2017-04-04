//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//
//
//  Changes for defect 198932 "Support v5 JSP reload logic for v5 apps in 6.0"
//		added variables servlet2_2, servletEngineReloadEnabled, 
//			servletEngineReloadInterval, applicationDisplayName
package com.ibm.wsspi.webcontainer.metadata;

import java.util.List;
import java.util.Map;

public class BaseJspComponentMetaData {
    protected List jspPropertyGroups = null;
    protected Map jspTaglibs = null;

    public List getJspPropertyGroups() {
        return jspPropertyGroups;
    }
    
    public Map getJspTaglibs() {
        return jspTaglibs;
    }

    public void setJspPropertyGroups(List jspPropertyGroups) {
        this.jspPropertyGroups = jspPropertyGroups;
    }

    public void setJspTaglibs(Map jspTagLibs) {
        this.jspTaglibs = jspTagLibs;
    }

}
