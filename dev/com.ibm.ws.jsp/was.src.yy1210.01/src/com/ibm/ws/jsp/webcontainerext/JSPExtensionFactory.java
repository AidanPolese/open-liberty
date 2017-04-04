//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change history:
//Defect 219290 "PERF-ST: Taglib init suggestions for improved startup perf"  2004/07/27 Scott Johnson/Todd Kaplinger
//Defect PK04091 "PROVIDE A CONFIGURABLE PROPERTY FOR DETERMINING BUFFER SIZE."  2006/02/20 Scott Johnson
// APAR  PM21451  check for null returned from realPath 2011/03/25    mmulholl 

package com.ibm.ws.jsp.webcontainerext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.configuration.JspXmlExtConfig;
import com.ibm.ws.jsp.taglib.config.GlobalTagLibConfig;
import com.ibm.ws.jsp.webxml.WebXmlParser;
import com.ibm.wsspi.jsp.context.JspClassloaderContext;
import com.ibm.wsspi.webcontainer.extension.ExtensionProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

public class JSPExtensionFactory extends AbstractJSPExtensionFactory {
    static protected Logger logger;
    private static final String CLASS_NAME="com.ibm.ws.jsp.webcontainerext.JSPExtensionFactory";
    static {
        logger = Logger.getLogger("com.ibm.ws.jsp");
    }

//	  defect PK04091 - comment out this constructor    
//    public JSPExtensionFactory() {
//        super(BodyContentImpl.DEFAULT_TAG_BUFFER_SIZE);
//    }

    protected JspXmlExtConfig createConfig(IServletContext webapp) {
        InputStream is = null;
        WebXmlParser webXmlParser = null;
        try {
			webXmlParser = new WebXmlParser((java.io.File) webapp.getAttribute(Constants.TMP_DIR));
	        webXmlParser.setJCDIEnabledForRuntimeCheck(webapp.getWebAppConfig().isJCDIEnabled());
			// is = new java.io.FileInputStream(webapp.getRealPath("/WEB-INF/web.xml")); //PM21451	
			String path = webapp.getRealPath("/WEB-INF/web.xml");			
			if (path!=null) {
			    is = new java.io.FileInputStream(path);
			} else {
			    is=null;
			}
			webXmlParser.parseWebXml(is);
		} 
        catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
        catch (JspCoreException e) {
			e.printStackTrace();
		}                
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
		return webXmlParser;
    	
    }

    protected JspClassloaderContext createJspClassloaderContext(IServletContext webapp, JspXmlExtConfig webAppConfig) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)) {
            logger.logp(Level.FINER, CLASS_NAME, "createJspClassloaderContext", "returning a new JSPClassloaderContext()");
        }
    	final ClassLoader loader = webapp.getClassLoader();
    	return new JspClassloaderContext() {
    	    public ClassLoader getClassLoader() { return loader;}
    	    public String getClassPath() { return "";}
    	    public String getOptimizedClassPath() { return getClassPath();};
    	    public boolean isPredefineClassEnabled() {return false;}
    	    public byte[] predefineClass(String className, byte[] classData) {return classData;}
    	};
    }

    protected ExtensionProcessor createProcessor(IServletContext webapp,
                                                 JspXmlExtConfig webAppConfig,
                                                 JspClassloaderContext jspClassloaderContext) throws Exception {
        return new JSPExtensionProcessor(webapp, webAppConfig, globalTagLibraryCache, jspClassloaderContext);
    }
    
    public void addGlobalTagLibConfig(GlobalTagLibConfig globalTagLibConfig) {
        if (globalTagLibraryCache == null) {
            this.createGlobalTagLibraryCache();
        }
        globalTagLibraryCache.addGlobalTagLibConfig(globalTagLibConfig);
    }
}

