package org.apache.jasper.runtime;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

public class JcdiWrappedJspFactoryImpl extends JspFactoryImpl {
    
    JspFactory instance = null;
    
    public JcdiWrappedJspFactoryImpl(JspFactory impl) {
        instance = impl;
    }

    public JspApplicationContext getJspApplicationContext(ServletContext context) {
        return JcdiWrappedJspApplicationContextImpl.getInstance(context);  
    }
    
    public PageContext getPageContext(
            Servlet servlet,
            ServletRequest request,
            ServletResponse response,
            String errorPageURL,
            boolean needsSession,
            int bufferSize,
            boolean autoflush) {
        return instance.getPageContext(servlet, request, response, errorPageURL, needsSession, bufferSize, autoflush);
    }

    @Override
    public JspEngineInfo getEngineInfo() {
        return instance.getEngineInfo();
    }

    @Override
    public void releasePageContext(PageContext pc) {
        instance.releasePageContext(pc);
    }
    
}
