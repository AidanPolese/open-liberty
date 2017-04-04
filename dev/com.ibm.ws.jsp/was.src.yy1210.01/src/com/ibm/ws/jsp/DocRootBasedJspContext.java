//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
// 395182.2  70FVT: make servlet 2.3 compatible with JSP 2.1 for migration 2007/02/07 Scott Johnson

package com.ibm.ws.jsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.jsp.configuration.JspConfigurationManager;
import com.ibm.ws.jsp.inputsource.JspInputSourceFactoryImpl;
import com.ibm.ws.jsp.webxml.WebXmlParser;
import com.ibm.wsspi.jsp.context.JspClassloaderContext;
import com.ibm.wsspi.jsp.context.JspCoreContext;
import com.ibm.wsspi.jsp.resource.JspInputSourceFactory;

public class DocRootBasedJspContext implements JspCoreContext, JspClassloaderContext {

	static private Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.DocRootBasedJspContext";
	static {
		logger = Logger.getLogger("com.ibm.ws.jsp");
	}

    private URLClassLoader loader = null;
    private String docRoot = "";
    private JspConfigurationManager jspConfigurationManager = null;
    private URL contextURL = null;
    private JspInputSourceFactory jspInputSourceFactory = null;
    
    public DocRootBasedJspContext(String docRoot) throws JspCoreException {
        this.docRoot = docRoot;
        List urlList = new ArrayList();
        getWebAppURLs(docRoot, urlList);
        getManifestURLs(docRoot, urlList);
        String paths[] = (String[]) urlList.toArray(new String[0]);
            
        URL[] urls = new URL[paths.length]; 
        for (int i = 0; i < paths.length; i++) {
            try {
                urls[i] = (new File(paths[i])).toURL();
            }
            catch (MalformedURLException e) {
                throw new JspCoreException(e);
            }
        }
        loader = new URLClassLoader(urls, this.getClass().getClassLoader());
        WebXmlParser webXmlParser = new WebXmlParser(null);
        InputStream is = null;
        
        try {
            is = new FileInputStream(docRoot + File.separator + "WEB-INF" + File.separator + "web.xml");
            webXmlParser.parseWebXml(is);                
        }
        catch (IOException e) {
            throw new JspCoreException(e);    
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
        try {
            contextURL = new URL("file", null, getRealPath("/"));
        }
        catch (MalformedURLException e) {
			logger.logp(Level.WARNING, CLASS_NAME, "DocRootBasedJspContext", "Failed to create context URL for docRoot: " + docRoot, e);
        }
        jspConfigurationManager = new JspConfigurationManager(webXmlParser.getJspPropertyGroups(), webXmlParser.isServlet24(), webXmlParser.isServlet24_or_higher(), Collections.EMPTY_LIST, webXmlParser.isJCDIEnabledForRuntimeCheck());
        jspInputSourceFactory = new JspInputSourceFactoryImpl(docRoot,contextURL, null, true, loader);
    }
    
    public ClassLoader getClassLoader() {
        return (loader);
    }

    public String getClassPath() {
        String classpath = "";
        if (loader instanceof URLClassLoader) {
            URL[] urls = null;
            if (this.getClass().getClassLoader() instanceof URLClassLoader) {
                urls = ((URLClassLoader) this.getClass().getClassLoader()).getURLs();
                if (urls!=null) {
                    for (int i = 0; i < urls.length; i++) {
                        classpath += new File(urls[i].getFile()).getPath();
                        classpath += File.pathSeparator;
                    }
                }
            }
            urls = ((URLClassLoader) loader).getURLs();
            if (urls!=null) {
                for (int i = 0; i < urls.length; i++) {
                    classpath += new File(urls[i].getFile()).getPath();
                    if (i != (urls.length - 1))
                        classpath += File.pathSeparator;
                }
            }
        }
        return (classpath);
    }

    public String getRealPath(String path) {
        String realPath = "";
        if (path.startsWith("/"))
            realPath = docRoot + path;
        else
            realPath = docRoot + "/" + path;
        
        return (realPath);
    }

    public java.util.Set getResourcePaths(String path,boolean searchMetsInfResources) {   	
    	// this object never searched META-INF resources so ignore the booelan
    	return this.getResourcePaths(path);
    }

    
    public java.util.Set getResourcePaths(String path) {
        java.util.HashSet set = new java.util.HashSet();

        java.io.File root = new java.io.File(docRoot + path);

        if (root.exists()) {
            java.io.File[] fileList = root.listFiles();
            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    String resourcePath = fileList[i].getPath();
                    resourcePath = resourcePath.substring(docRoot.length());
                    resourcePath = resourcePath.replace('\\', '/');
                    if (fileList[i].isDirectory()) {
                        if (resourcePath.endsWith("/") == false) {
                            resourcePath += "/";
                        }
                    }
                    set.add(resourcePath);
                }
            }
        }
        return (set);
    }

    private static void getWebAppURLs(String dir, List urlList) {
        urlList.add(new File(dir + File.separator + "WEB-INF" + File.separator + "classes").toString());
        File webappDir = new File(dir + File.separator + "WEB-INF" + File.separator + "lib");

        if (webappDir.exists() && webappDir.isDirectory()) {
            File[] dirList = webappDir.listFiles();
            for (int i = 0; i < dirList.length; i++) {
                if (dirList[i].isFile() && (dirList[i].getName().endsWith(".jar") || dirList[i].getName().endsWith(".zip"))) {
                    urlList.add(dirList[i].toString());
                }
            }
        }
    }

    private static void getLibURLs(String dir, List urlList) {
        StringTokenizer st = new StringTokenizer(dir, File.pathSeparator);

        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            File file = new File(s);

            if (file.exists() && file.isDirectory()) {
                File[] dirList = file.listFiles();
                for (int i = 0; i < dirList.length; i++) {
                    if (dirList[i].isFile() && (dirList[i].getName().endsWith(".jar") || dirList[i].getName().endsWith(".zip"))) {
                        urlList.add(dirList[i].toString());
                    }
                }
            }
            else if (s.endsWith(".jar") || s.endsWith(".zip")) {
                urlList.add(file.toString());
            }
        }
    }

    private static void getManifestURLs(String path, List manifestPaths) {
        File f = new File(path);
        if (f.exists()) {
            if (f.isDirectory()) {
                File manifestFile = new File(path + File.separator + "META-INF" + File.separator + "MANIFEST.MF");
                if (manifestFile.exists()) {
                    FileInputStream fin = null;
                    try {
                        fin = new FileInputStream(manifestFile);
                        Manifest manifest = new Manifest(fin);
                        getManifestClassPaths(manifest, f.getParent(), manifestPaths);
                    }
                    catch (IOException e) {
                        //ignore IOExceptions
                    }
                    finally {
                        if (fin != null) {
                            try {
                                fin.close(); //make attempt to close. Manifest was locked at deletion time.
                            }
                            catch (IOException io) {}
                            fin = null;
                        }
                    }
                }
            }
            else {
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(f);
                    Manifest manifest = jarFile.getManifest();
                    if (manifest != null)
                        getManifestClassPaths(manifest, f.getParent(), manifestPaths);
                }
                catch (IOException e) {}
                finally {
                    if (jarFile != null) {
                        try {
                            jarFile.close(); // attempt to close jar file
                        }
                        catch (IOException io) {}
                        jarFile = null;
                    }
                }
            }
        }
    }

    private static void getManifestClassPaths(Manifest manifest, String archivePath, List manifestPaths) {
        Attributes main = manifest.getMainAttributes();
        String classPath = main.getValue(Attributes.Name.CLASS_PATH);
        if (classPath != null) {
            StringTokenizer st = new StringTokenizer(classPath, " ");
            while (st.hasMoreTokens()) {
                String path = archivePath + File.separator + st.nextToken();
                File file = new File(path);
                if (file.exists()) {
                    manifestPaths.add(file.toString());
                }
            }
        }
    }
    
    public JspInputSourceFactory getJspInputSourceFactory() {
        return jspInputSourceFactory;
    }
    
    public JspConfigurationManager getJspConfigurationManager() {
        return jspConfigurationManager;
    }
    
    public JspClassloaderContext getJspClassloaderContext() {
        return this;
    }
    
    public String getOptimizedClassPath() {
        return getClassPath();
    }

    public boolean isPredefineClassEnabled() {
        return false;
    }

    public byte[] predefineClass(String className, byte[] classData) {
        return null;
    }
    
}
