package com.ibm.ws.jsp22.webcontainerext;

import com.ibm.ws.jsp.webcontainerext.JspVersion;

public class JspVersionImpl implements JspVersion {

    public JspVersionImpl() {}

    @Override
    public String getJspVersionString() {
        return "2.2";
    }
}
