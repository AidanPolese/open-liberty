// IBM Confidential OCO Source Material
// 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2002
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.jsp.jsx.tags;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class CallTagTEI extends TagExtraInfo {
    public VariableInfo[] getVariableInfo(TagData data) {
        if (data.getAttributeString("id") != null) {
            VariableInfo info1 = new VariableInfo(data.getAttributeString("id"), "String", true, VariableInfo.AT_END);
            VariableInfo[] info = { info1 };
            return info;
        }
        else {
            VariableInfo[] info = {};
            return info;
        }
    }
}
