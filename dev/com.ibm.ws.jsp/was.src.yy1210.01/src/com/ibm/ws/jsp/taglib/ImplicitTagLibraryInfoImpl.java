//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
// jsp2.1work
// defect 414623 FVT:Default JSP version should be 2.0 not 2.1 2007/01/12 10:15:37 Scott Johnson                                                                                        

package com.ibm.ws.jsp.taglib;

import com.ibm.wsspi.jsp.resource.JspInputSource;

public class ImplicitTagLibraryInfoImpl extends TagLibraryInfoImpl {
    public ImplicitTagLibraryInfoImpl(String directoryName, JspInputSource inputSource) {
        super("", directoryName, "webinf", inputSource);
        this.shortname = directoryName;
        if (shortname.startsWith("/WEB-INF/")) {
            shortname = shortname.substring(9);
        }
        if (shortname.endsWith("/")) {
            shortname = shortname.substring(0, shortname.lastIndexOf('/')-1);
        }
        shortname = shortname.replaceAll("/", "-");
        this.tlibversion = "1.0";
        this.jspversion = "2.0";
    }
}
