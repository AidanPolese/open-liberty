package com.ibm.ws.jsp23.webcontainerext;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jsp.webcontainerext.JspVersion;
import com.ibm.ws.jsp.webcontainerext.JspVersionFactory;

@Component(property = { "service.vendor=IBM" })
public class JspVersionFactoryImpl implements JspVersionFactory {

    private static final JspVersionImpl jv = new JspVersionImpl();

    @Override
    public JspVersion getJspVersion() {
        // TODO Auto-generated method stub
        return jv;
    }

}
