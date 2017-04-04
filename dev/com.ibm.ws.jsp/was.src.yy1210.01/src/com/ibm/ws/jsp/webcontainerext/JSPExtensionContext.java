//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//
//Changes
//PK76503 sartoris    12/17/2008  Threading issue when simultaneous requests call the getRealPath method and are using an extendedDocumentRoot
//PM30435 pmdinh      01/19/2011  Suppress FFDC for an unrelated resource when EDR is enabled.
//PM21451 mmulholl    03/25/2011    use new getRealPath    

package com.ibm.ws.jsp.webcontainerext;

import java.io.File;
import java.io.FileNotFoundException;				//PM30435
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.inputsource.JspInputSourceFactoryImpl;
import com.ibm.ws.jsp.translator.resource.JspResourcesFactoryImpl;
import com.ibm.ws.webcontainer.util.DocumentRootUtils;
import com.ibm.ws.webcontainer.util.MetaInfResourcesFileUtils;
import com.ibm.ws.webcontainer.util.ZipFileResource;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.jsp.compiler.JspCompilerFactory;
import com.ibm.wsspi.jsp.context.JspClassloaderContext;
import com.ibm.wsspi.jsp.context.translation.JspTranslationContext;
import com.ibm.wsspi.jsp.context.translation.JspTranslationEnvironment;
import com.ibm.wsspi.jsp.resource.JspInputSourceFactory;
import com.ibm.wsspi.jsp.resource.translation.JspResourcesFactory;

public class JSPExtensionContext implements JspTranslationContext {
    protected IServletContext context = null;
    protected DocumentRootUtils dru = null;
    protected URL contextURL = null;
    protected JspResourcesFactory jspResourcesFactory = null;
    protected JspInputSourceFactory jspInputSourceFactory = null;
    protected JspClassloaderContext jspClassloaderContext = null;
    protected JspCompilerFactory jspCompilerFactory = null;

    static private Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.webcontainerext.JSPExtensionContext";
    static {
        logger = Logger.getLogger("com.ibm.ws.jsp");
    }

    public JSPExtensionContext(IServletContext context,
    						   JspOptions jspOptions,
                               String extDocRoot,
                               String preFragExtDocRoot,
                               JspClassloaderContext jspClassloaderContext,
                               JspCompilerFactory jspCompilerFactory) {
        this.context = context;
        this.jspClassloaderContext = jspClassloaderContext;
        this.jspCompilerFactory = jspCompilerFactory;
        dru = new DocumentRootUtils(context, extDocRoot,preFragExtDocRoot);
        String docRoot = null;
        try {
        	docRoot = context.getRealPath("/");
            contextURL = new File(docRoot).toURL();
        }
        catch (MalformedURLException e) {
            logger.logp(Level.WARNING,"JSPExtensionContext","JSPExtensionContext", "Failed to create context URL for docRoot: "+ context.getRealPath("/") , e);
        }
        jspResourcesFactory = new JspResourcesFactoryImpl(jspOptions, this);
        jspInputSourceFactory = new JspInputSourceFactoryImpl(docRoot,contextURL, dru, false, jspClassloaderContext.getClassLoader(),context);
    }

    public String getRealPath(String path) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
            logger.entering(this.getClass().getName(), "getRealPath", path);
        }
        if (context != null) {
            String realPath = context.getRealPath(path,"jsp"); // PM21451
            if (new java.io.File(realPath).exists() == false) {
                    //PK76503 add synchronized block
                    synchronized (dru) {
                        try {
                            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {  //PK76503 added trace
                                logger.logp(Level.FINE,CLASS_NAME,"getRealPath", "Checking extendedDocumentRoot path: " + path);
                            }
                            dru.handleDocumentRoots(path);
                            // return jar name or file name if not in jar.
                            realPath = dru.getFilePath();
                            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) { //PK76503 added trace
                                logger.logp(Level.FINE,CLASS_NAME,"getRealPath", "Path was retrieved from the extendedDoucumentRoots realPath: " + realPath);
                            }
                        }
                        // PM30435 start
                        catch (FileNotFoundException fne_io){
                        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
                        		com.ibm.ws.ffdc.FFDCFilter.processException( fne_io, "com.ibm.ws.jsp.webcontainerext.JspExtensionContext.getResourceAsStream", "97", this);
                            }
                            // this may happen if resource does not exist
                            // follow behavior from above and just return path below
                        } 
                        // PM30435 end
                        catch (Exception e) {
                            com.ibm.ws.ffdc.FFDCFilter.processException( e, "com.ibm.ws.jsp.webcontainerext.JspExtensionContext.getResourceAsStream", "102", this);
                            // follow behavior from above and just return path below
                        }
                    }
            }
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
                logger.exiting(this.getClass().getName(), "getRealPath", realPath);
            }
            return realPath;
        }
        else {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
                logger.exiting(this.getClass().getName(), "getRealPath", path);
            }
            return path;
        }
    }

    public java.util.Set getResourcePaths(String paths) {
        return context.getResourcePaths(paths);
    }
    
    public java.util.Set getResourcePaths(String path,boolean searchMetaInfResources) {   	
    	return context.getResourcePaths(path,searchMetaInfResources);
    }

    public JspResourcesFactory getJspResourcesFactory() {
        return jspResourcesFactory;
    }

    public JspInputSourceFactory getJspInputSourceFactory() {
        return jspInputSourceFactory;
    }

    public JspClassloaderContext getJspClassloaderContext() {
        return jspClassloaderContext;
    }

    public JspCompilerFactory getJspCompilerFactory() {
        return jspCompilerFactory;
    }

    public void setJspTranslationEnviroment(JspTranslationEnvironment jspEnvironment) {
    }
}
