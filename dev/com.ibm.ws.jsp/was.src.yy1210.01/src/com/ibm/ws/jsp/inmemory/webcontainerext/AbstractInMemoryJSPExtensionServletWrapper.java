//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

// Change History
// Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson
// PK76810 2009/01/14	ClassNotFoundException on z/OS - pmdinh

package com.ibm.ws.jsp.inmemory.webcontainerext;

import java.io.File;
import java.io.FilePermission;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.configuration.JspConfigurationManager;
import com.ibm.ws.jsp.inmemory.compiler.InMemoryJspCompilerResult;
import com.ibm.ws.jsp.taglib.TagLibraryCache;
import com.ibm.ws.jsp.translator.utils.JspTranslatorUtil;
import com.ibm.ws.jsp.webcontainerext.AbstractJSPExtensionServletWrapper;
import com.ibm.wsspi.jsp.context.translation.JspTranslationContext;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

public abstract class AbstractInMemoryJSPExtensionServletWrapper extends AbstractJSPExtensionServletWrapper {
    static protected Logger logger;

    private static final String CLASS_NAME = "com.ibm.ws.jsp.webcontainerext.InMemoryJSPExtensionServletWrapper";
    static {
        logger = Logger.getLogger("com.ibm.ws.jsp");
    }
    
    private static String separatorString = System.getProperty("line.separator"); 
    
    private InMemoryJspCompilerResult currentResult = null;
    
    public AbstractInMemoryJSPExtensionServletWrapper(IServletContext parent, 
            JspOptions options, 
            JspConfigurationManager configManager, 
            TagLibraryCache tlc,
            JspTranslationContext context, 
            CodeSource codeSource) throws Exception {
        super(parent, options, configManager, tlc, context, codeSource);
    }
    
    protected PermissionCollection createPermissionCollection() throws MalformedURLException {
        return Policy.getPolicy().getPermissions(codeSource);
    }
    
    protected void preinvokeCheckForTranslation(HttpServletRequest req) throws JspCoreException {
    }
    
    protected boolean translateJsp() throws JspCoreException {  	//PK76810
        currentResult = (InMemoryJspCompilerResult)JspTranslatorUtil.translateJspAndCompile(jspResources, 
                                                         tcontext, configManager.getConfigurationForUrl(inputSource.getRelativeURL()), 
                                                         options, 
                                                         tlc, 
                                                         false, 
                                                         Collections.EMPTY_LIST);
        if (currentResult.getCompilerReturnValue() != 0) {
            JspCoreException e = new JspCoreException("jsp.error.compile.failed", new Object[] { inputSource.getRelativeURL(),
                    separatorString + currentResult.getCompilerMessage() });
            throw e;
        }
        return true;												//PK76810
    }

    protected void createClassLoader() {
        if (jspResources == null) {
            jspResources = tcontext.getJspResourcesFactory().createJspResources(inputSource);
            if (servletConfig != null) {
                servletConfig.setClassName(jspResources.getPackageName() + "." + jspResources.getClassName());
            }
        }
        if (options.isDisableJspRuntimeCompilation() && options.isUseFullPackageNames()) {
            classloaderCreated = true;
            return;
        }
        URL[] urls = null;
        try {
            PermissionCollection permissionCollection = createPermissionCollection();
            String sourceDir = jspResources.getGeneratedSourceFile().getParentFile().toString() + File.separator + "*";
            permissionCollection.add(new FilePermission(sourceDir, "read"));

            if (options.isUseFullPackageNames() == false) {
                urls = new URL[4];
                urls[0] = jspResources.getGeneratedSourceFile().getParentFile().toURL();
                urls[1] = options.getOutputDir().toURL();
                urls[2] = new File(tcontext.getRealPath("/WEB-INF/classes")
                        + inputSource.getRelativeURL().substring(0, inputSource.getRelativeURL().lastIndexOf("/") + 1)).toURL();
                urls[3] = new File(tcontext.getRealPath("/WEB-INF/classes")).toURL();
            } else {
                urls = new URL[2];
                urls[0] = options.getOutputDir().toURL();
                urls[1] = new File(tcontext.getRealPath("/WEB-INF/classes")).toURL();
            }
            InMemoryJspClassLoader jspLoader = new InMemoryJspClassLoader(urls, 
                                                                            tcontext.getJspClassloaderContext(), 
                                                                            jspResources.getClassName(), 
                                                                            codeSource, 
                                                                            permissionCollection,
                                                                            currentResult.getResourcesList());
            if (servletConfig != null && jspResources.getPackageName().equals(Constants.JSP_FIXED_PACKAGE_NAME)) {
                try {
                    jspLoader.loadClass(jspResources.getPackageName() + "." + jspResources.getClassName(), true);
                } 
                catch (Throwable e1) {
                    servletConfig.setClassName(Constants.OLD_JSP_PACKAGE_NAME + "." + jspResources.getClassName());
                }
            }

            setTargetClassLoader(jspLoader);
            classloaderCreated = true;
        } 
        catch (MalformedURLException e) {
            logger.logp(Level.WARNING, CLASS_NAME, "createClassLoader", "failed to create JSP class loader", e);
        }
        currentResult = null;
    }
}
