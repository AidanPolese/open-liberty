//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.inputsource;

import java.net.URL;
import java.net.URLStreamHandler;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.servlet.ServletContext;

import com.ibm.ws.webcontainer.util.DocumentRootUtils;
import com.ibm.wsspi.jsp.resource.JspInputSource;
import com.ibm.wsspi.jsp.resource.JspInputSourceFactory;

public class JspInputSourceFactoryImpl implements JspInputSourceFactory {
    protected URL contextURL = null;
    protected DocumentRootUtils dru = null;
    protected boolean searchClasspathForResources = false;
    protected ClassLoader classloader = null;
	private String docRoot;
	private ServletContext servletContext;

    public JspInputSourceFactoryImpl(String docRoot, URL contextURL, 
                                     DocumentRootUtils dru,
                                     boolean searchClasspathForResources, 
                                     ClassLoader classloader) {
    	this.docRoot = docRoot;
        this.contextURL = contextURL;
        this.dru = dru;
        this.searchClasspathForResources = searchClasspathForResources;
        this.classloader = classloader;
    }
    
    public JspInputSourceFactoryImpl(String docRoot, URL contextURL, 
            DocumentRootUtils dru,
            boolean searchClasspathForResources, 
            ClassLoader classloader,
            ServletContext servletContext) {
		this.docRoot = docRoot;
		this.contextURL = contextURL;
		this.dru = dru;
		this.searchClasspathForResources = searchClasspathForResources;
		this.classloader = classloader;
		this.servletContext = servletContext;
	}
    
    public JspInputSource copyJspInputSource(JspInputSource base, String relativeURL) {
        JspInputSource jspInputSource = null;
        
        if (System.getSecurityManager() != null) {
            final String finalJspRelativeUrl = relativeURL; 
            final JspInputSource finalBase = base;
            final ServletContext finalServletContext = servletContext;
            jspInputSource = (JspInputSource)AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    URLStreamHandler urlStreamHandler = new JspURLStreamHandler(docRoot,finalJspRelativeUrl, 
                                                                                dru, 
                                                                                searchClasspathForResources, 
                                                                                classloader,
                                                                                finalServletContext);
                    return new JspInputSourceImpl((JspInputSourceImpl)finalBase, finalJspRelativeUrl, urlStreamHandler);
                }
            });
        }
        else {
            URLStreamHandler urlStreamHandler = new JspURLStreamHandler(docRoot,
            															relativeURL, 
                                                                        dru,
                                                                        searchClasspathForResources, 
                                                                        classloader,
                                                                        servletContext);
            jspInputSource = new JspInputSourceImpl((JspInputSourceImpl)base, relativeURL, urlStreamHandler);
        }
            
        return jspInputSource; 
    }

    public JspInputSource createJspInputSource(String relativeURL) {
        JspInputSource jspInputSource = null;
        
        if (System.getSecurityManager() != null) {
            final String finalJspRelativeUrl = relativeURL; 
            final ServletContext finalServletContext = servletContext;
            jspInputSource = (JspInputSource)AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    URLStreamHandler urlStreamHandler = new JspURLStreamHandler(docRoot,finalJspRelativeUrl, 
                                                                                dru, 
                                                                                searchClasspathForResources, 
                                                                                classloader,
                                                                                finalServletContext);
                    return new JspInputSourceImpl(contextURL, finalJspRelativeUrl, urlStreamHandler);
                }
            });
        }
        else {
            URLStreamHandler urlStreamHandler = new JspURLStreamHandler(docRoot,relativeURL, 
                                                                        dru,
                                                                        searchClasspathForResources, 
                                                                        classloader,
                                                                        servletContext);
            jspInputSource = new JspInputSourceImpl(contextURL, relativeURL, urlStreamHandler);
        }
            
        return jspInputSource; 
    }

    public JspInputSource createJspInputSource(URL contextURL, String relativeURL) {
        JspInputSource jspInputSource = null;
        
        if (System.getSecurityManager() != null) {
            final String finalJspRelativeUrl = relativeURL; 
            final URL finalContextURL = contextURL;
            final ServletContext finalServletContext = servletContext;
            jspInputSource = (JspInputSource)AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    URLStreamHandler urlStreamHandler = new JspURLStreamHandler(docRoot,
                    															finalJspRelativeUrl, 
                                                                                dru, 
                                                                                searchClasspathForResources, 
                                                                                classloader,
                                                                                finalServletContext);
                    return new JspInputSourceImpl(finalContextURL, finalJspRelativeUrl, urlStreamHandler);
                }
            });
        }
        else {
            URLStreamHandler urlStreamHandler = new JspURLStreamHandler(docRoot,
            															relativeURL, 
                                                                        dru,
                                                                        searchClasspathForResources, 
                                                                        classloader,
                                                                        servletContext);
            jspInputSource = new JspInputSourceImpl(contextURL, relativeURL, urlStreamHandler);
        }
            
        return jspInputSource; 
    }

}
