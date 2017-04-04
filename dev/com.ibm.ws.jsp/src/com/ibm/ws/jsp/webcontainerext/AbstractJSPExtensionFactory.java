//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change history:
//Defect 219290 "PERF-ST: Taglib init suggestions for improved startup perf"  2004/07/27 Scott Johnson/Todd Kaplinger
//Defect PK04091 "PROVIDE A CONFIGURABLE PROPERTY FOR DETERMINING BUFFER SIZE."  2006/02/20 Scott Johnson
//Defect 651265  "Trace Entry and Exit improvement" 05/12/2010 Anup Aggarwal 
//
package com.ibm.ws.jsp.webcontainerext;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.configuration.JspXmlExtConfig;
import com.ibm.ws.jsp.taglib.GlobalTagLibraryCache;
import com.ibm.wsspi.jsp.context.JspClassloaderContext;
import com.ibm.wsspi.webcontainer.annotation.AnnotationHelperManager;
import com.ibm.wsspi.webcontainer.extension.ExtensionFactory;
import com.ibm.wsspi.webcontainer.extension.ExtensionProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

public abstract class AbstractJSPExtensionFactory implements ExtensionFactory {
    static protected final Logger logger = Logger.getLogger("com.ibm.ws.jsp");
    private static final String CLASS_NAME="com.ibm.ws.jsp.webcontainerext.JSPExtensionFactory";
    private static final long NANOS_IN_A_MILLISECONDS = 1000000L;
    protected static GlobalTagLibraryCache globalTagLibraryCache = null;
    private final static Object lock=new Object();
    
//	  defect PK04091 - comment out this constructor   
//    public AbstractJSPExtensionFactory (int bodyContentBufferSize) {
//        if (JspFactory.getDefaultFactory() == null) {
//            JspFactoryImpl factory = new JspFactoryImpl(bodyContentBufferSize);
//            if (System.getSecurityManager() != null) {
//                String basePackage = "org.apache.jasper.";
//                try {
//                    factory.getClass().getClassLoader().loadClass(
//                        basePackage + "runtime.JspFactoryImpl$PrivilegedGetPageContext");
//                    factory.getClass().getClassLoader().loadClass(
//                        basePackage + "runtime.JspFactoryImpl$PrivilegedReleasePageContext");
//                    factory.getClass().getClassLoader().loadClass(basePackage + "runtime.JspRuntimeLibrary");
//                    factory.getClass().getClassLoader().loadClass(
//                        basePackage + "runtime.JspRuntimeLibrary$PrivilegedIntrospectHelper");
//                    factory.getClass().getClassLoader().loadClass(
//                        basePackage + "runtime.ServletResponseWrapperInclude");
//                    //factory.getClass().getClassLoader().loadClass(basePackage + "servlet.JspServletWrapper");
//                }
//                catch (ClassNotFoundException ex) {
//                    System.out.println("Jasper JspRuntimeContext preload of class failed: " + ex.getMessage());
//                }
//            }
//            JspFactory.setDefaultFactory(factory);
//        }
//    }

    public ExtensionProcessor createExtensionProcessor(IServletContext webapp) throws Exception {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
         	logger.entering(CLASS_NAME,"createExtensionProcessor", " app contextPath --> "+ webapp.getContextPath());
	}// d651265

        createGlobalTagLibraryCache();

    	JspXmlExtConfig webAppConfig = createConfig(webapp);
    	JspClassloaderContext jspClassloaderContext = createJspClassloaderContext(webapp, webAppConfig);

        ExtensionProcessor extensionProcessor = createProcessor(webapp, webAppConfig, jspClassloaderContext);

        AnnotationHelperManager aHM =  new AnnotationHelperManager(webapp);    
        com.ibm.wsspi.webcontainer.annotation.AnnotationHelperManager.addInstance(webapp, aHM);
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
            logger.logp(Level.FINE,"JSPExtensionFactory","createExtensionProcessor", "Added AnnotationHelperManager of: " + aHM);
            logger.logp(Level.FINE,"JSPExtensionFactory","createExtensionProcessor", "with IServletContext of: " + webapp);
        }

        
	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
         	logger.exiting(CLASS_NAME,"createExtensionProcessor", " app contextPath --> "+ webapp.getContextPath());
	}// d651265

        return extensionProcessor;
    }

    public List getPatternList() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
         	logger.entering(CLASS_NAME,"getPatternList");
	}
        ArrayList extensionsSupported = new ArrayList();

        for (int i = 0; i < Constants.STANDARD_JSP_EXTENSIONS.length; i++) {
            extensionsSupported.add(Constants.STANDARD_JSP_EXTENSIONS[i]);
        }
	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){
         	logger.exiting(CLASS_NAME,"getPatternList");
	}
        return extensionsSupported;
    }

    protected abstract JspXmlExtConfig createConfig(IServletContext webapp);
    protected abstract JspClassloaderContext createJspClassloaderContext(IServletContext webapp, JspXmlExtConfig webAppConfig);
    protected abstract ExtensionProcessor createProcessor(IServletContext webapp, 
    		                                              JspXmlExtConfig webAppConfig, 
    		                                              JspClassloaderContext jspClassloaderContext) throws Exception;
    
    protected static void createGlobalTagLibraryCache() {
    	synchronized (lock) {
    		if (globalTagLibraryCache == null ) {
	            long start = System.nanoTime();        	
	            globalTagLibraryCache = new GlobalTagLibraryCache();
	            long end = System.nanoTime();
	            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) {
	                logger.logp(Level.FINE,"JSPExtensionFactory","createGlobalTagLibraryCache", "GlobalTagLibraryCache created in " + ((end - start) / NANOS_IN_A_MILLISECONDS) + " ms");
	            }
        	}
        }

    }
}
