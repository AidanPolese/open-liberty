//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.tagfilescan;

import javax.servlet.jsp.tagext.TagInfo;

import com.ibm.ws.jsp.translator.visitor.JspVisitorResult;

public class TagFileScanResult extends JspVisitorResult {
    TagInfo ti = null;
    
    public TagFileScanResult(String jspVisitorId) {
        super(jspVisitorId);
    }
    
    public TagInfo getTagInfo() {
        return (ti);
    }
    
    void setTagInfo(TagInfo ti) {
        this.ti = ti;
    }
}
