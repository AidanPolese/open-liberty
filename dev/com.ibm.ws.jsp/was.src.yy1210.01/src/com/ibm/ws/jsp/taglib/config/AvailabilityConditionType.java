//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.taglib.config;

public class AvailabilityConditionType {
    private final static int WEBINF_FILE = 0;
    private final static int SERVLET_CLASSNAME = 1;

    public final static AvailabilityConditionType webinfFileType=new AvailabilityConditionType(WEBINF_FILE);
    public final static AvailabilityConditionType servletClassNameType=new AvailabilityConditionType(SERVLET_CLASSNAME);
    
    private int key=0;
    
    private AvailabilityConditionType(int key) {
        this.key = key;
    }
}
