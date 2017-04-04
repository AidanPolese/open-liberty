package org.apache.jasper.runtime;

import javax.el.ExpressionFactory;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspApplicationContext;

import org.apache.webbeans.el.WrappedExpressionFactory;

import com.ibm.wsspi.webcontainer.WCCustomProperties;

public class JcdiWrappedJspApplicationContextImpl extends JspApplicationContextImpl implements JspApplicationContext {

    private final static String KEY = JcdiWrappedJspApplicationContextImpl.class.getName();  //use JspApplicationContextImpl as the key
    
    private WrappedExpressionFactory wrappedExpressionFactory = null;
    
    public JcdiWrappedJspApplicationContextImpl() {
    }

    public ExpressionFactory getExpressionFactory() {
        if (wrappedExpressionFactory==null) {
            wrappedExpressionFactory = new WrappedExpressionFactory(JspApplicationContextImpl.expressionFactory); 
        }
        return wrappedExpressionFactory;
    }

    public static JspApplicationContextImpl getInstance(ServletContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ServletContext was null");
        }
        JcdiWrappedJspApplicationContextImpl impl = (JcdiWrappedJspApplicationContextImpl) context.getAttribute(KEY);
        if (impl == null) {
            impl = new JcdiWrappedJspApplicationContextImpl();
            context.setAttribute(KEY, impl);
        }
        //PM05903 Start
            if ( WCCustomProperties.THROW_EXCEPTION_FOR_ADDELRESOLVER 
                && context.getAttribute("com.ibm.ws.jsp.servletContextListeners.contextInitialized")!= null) {          
                    impl.listenersContextInitialized = true;            
            }//PM05903 End

        return impl;
    }
    
}
