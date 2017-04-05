//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change history:
//Defect 219290 "PERF-ST: Taglib init suggestions for improved startup perf"  2004/07/27 Scott Johnson/Todd Kaplinger
//Defect 220325 "SVT:SVC: JSP messages in stdout do not have message numbers"  2004/07/30 Scott Johnson
//Defect 223085 "Replace tsx:repeat optimized tag with taglib version"  2004/08/10 Scott Johnson
//Defect 651265  "Trace Entry and Exit improvement" 05/12/2010 Anup Aggarwal 

package com.ibm.ws.jsp.taglib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.tagext.TagLibraryInfo;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.configuration.JspConfigurationManager;
import com.ibm.ws.jsp.configuration.JspXmlExtConfig;
import com.ibm.ws.jsp.inputsource.JspInputSourceFactoryImpl;
import com.ibm.ws.jsp.taglib.config.AvailabilityCondition;
import com.ibm.ws.jsp.taglib.config.AvailabilityConditionType;
import com.ibm.ws.jsp.taglib.config.GlobalTagLibConfig;
import com.ibm.ws.jsp.taglib.config.ImplicitTagLibConfig;
import com.ibm.ws.jsp.taglib.config.TagLibCacheConfigParser;
import com.ibm.ws.jsp.taglib.config.TldPathConfig;
import com.ibm.wsspi.jsp.context.JspClassloaderContext;
import com.ibm.wsspi.jsp.context.JspCoreContext;
import com.ibm.wsspi.jsp.resource.JspInputSource;
import com.ibm.wsspi.jsp.resource.JspInputSourceFactory;

public class GlobalTagLibraryCache extends Hashtable implements JspCoreContext, 
                                                                JspClassloaderContext {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257286933104505909L;
	static private Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.taglib.GlobalTagLibraryCache";
    static{
        logger = Logger.getLogger("com.ibm.ws.jsp");
    }
    private URL contextURL = null;
    private String root = "";
    private JspInputSourceFactory inputSourceFactory = null;
    private Map implicitTagLibPrefixMap = new HashMap();
    private Map optimizedTagConfigMap = new HashMap();
    private List eventListenerList = new ArrayList();
    private List globalTagLibConfigList = null;
    private ClassLoader loader = null; // defect 213137
    private JspConfigurationManager configManager = null;
    /* defect 223085 
    private final static String className = "com.ibm.ws.jsp.translator.optimizedtag.impl.TsxRepeatOptimizedTag";
    private final static  String shortName = "repeat";
    private final static String uri = "http://websphere.ibm.com/tags/tsx";
    private final static String version = "1.2";
    private OptimizedTagConfig optimizedTagConfig = new OptimizedTagConfig();
    */
    // defect 213137 - new constructor with ClassLoader arg
    public GlobalTagLibraryCache() {
        this(null);
    }
    
    public GlobalTagLibraryCache(ClassLoader loader) {
        final boolean isAnyTraceEnabled = com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled();
        if(isAnyTraceEnabled&&logger.isLoggable(Level.FINE)){
            logger.entering(CLASS_NAME, "GlobalTagLibraryCache", " loader: ["+loader+"]");
        }
    	this.loader=loader;
    	String installRoot = (String) java.security.AccessController.doPrivileged(
    					new java.security.PrivilegedAction() 
    					{public Object run() {return System.getProperty("was.install.root");}});
        root = new File(installRoot + File.separator + "lib").getPath();
        try {
            contextURL = new URL("file", null, getRealPath("/"));
        }
        catch (MalformedURLException e) {
            logger.logp(Level.WARNING, CLASS_NAME, "GlobalTagLibraryCache", "Failed to create context URL for docRoot: " + root, e);
        }
        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
            logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "contextURL: ["+contextURL+"]");
        }
        inputSourceFactory = new JspInputSourceFactoryImpl(null,contextURL, null, false, getClassLoader());
        TldParser tldParser = null;
        try {
            /* defect 223085 
            try
	        {
	          Class optClass = Class.forName(className, true, this.getJspClassloaderContext().getClassLoader());
              optimizedTagConfig.setOptClass(optClass);
              optimizedTagConfig.setShortName(shortName);
              optimizedTagConfig.setTlibUri(uri);
              optimizedTagConfig.setTlibversion(version);
              optimizedTagConfigMap.put(uri+version+shortName, optimizedTagConfig);
	        }
	        catch (ClassNotFoundException e)
	        {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                    logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "optimized class "+className +" is not available", e);
                }
	        }
            */
            final boolean JCDIEnabledForRuntime=false;//set this to false since this configuration isn't used during runtime checking
            configManager = new JspConfigurationManager(Collections.EMPTY_LIST, false, true, Collections.EMPTY_LIST, JCDIEnabledForRuntime);    
            tldParser = new TldParser(this, configManager, false);
            
            TagLibCacheConfigParser tagLibCacheConfigParser = new TagLibCacheConfigParser();
            tagLibCacheConfigParser.parse(this.getClass().getResourceAsStream("/com/ibm/ws/jsp/resources/taglibcacheconfig.xml"));
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "tagLibCacheConfigParser.getImplicitTagLibList(): ["+tagLibCacheConfigParser.getImplicitTagLibList()+"]");
            }
            for (Iterator itr = tagLibCacheConfigParser.getImplicitTagLibList().iterator(); itr.hasNext();) {
                ImplicitTagLibConfig implicitTagLibConfig = (ImplicitTagLibConfig)itr.next();                         
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                    logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "implicitTagLibConfig: ["+implicitTagLibConfig+"]");
                    logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "implicitTagLibConfig.getUri(): ["+implicitTagLibConfig.getUri()+"]");
                    logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "contains(implicitTagLibConfig.getUri(): ["+contains(implicitTagLibConfig.getUri())+"]");
                }
                if (contains(implicitTagLibConfig.getUri()) == false) {
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                        logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "implicitTagLibConfig.getLocation(): ["+implicitTagLibConfig.getLocation()+"]");
                    }
                    JspInputSource inputSource = inputSourceFactory.createJspInputSource(implicitTagLibConfig.getLocation());
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                        logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "inputSource.getRelativeURL(): ["+inputSource.getRelativeURL()+"]");
                        logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "inputSource.getAbsoluteURL(): ["+inputSource.getAbsoluteURL()+"]");
                        logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "inputSource.getContextURL(): ["+inputSource.getContextURL()+"]");
                    }
                    try {
                        TagLibraryInfo tli = tldParser.parseTLD(inputSource, "webinf");
                        put(implicitTagLibConfig.getUri(), tli);
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                            logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "putting into implicitTagLibPrefixMap, prefix: ["+implicitTagLibConfig.getPrefix()+"]  uri: ["+implicitTagLibConfig.getUri()+"]");
                        }
                        implicitTagLibPrefixMap.put(implicitTagLibConfig.getPrefix(), implicitTagLibConfig.getUri());
                    }
                    catch (JspCoreException e) {
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "GlobalTagLibraryCache", "jsp warning failed to load tld in jar. uri = ["+inputSource.getAbsoluteURL()+"]", e);
                        }
                    }
                }
            }
            globalTagLibConfigList = tagLibCacheConfigParser.getGlobalTagLibList();
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "globalTagLibConfigList: ["+globalTagLibConfigList+"]");
            }
            for (ListIterator itr = globalTagLibConfigList.listIterator(); itr.hasNext();) {
                GlobalTagLibConfig globalTagLibConfig = (GlobalTagLibConfig)itr.next();
                if (loadImplicitTldFromJar(globalTagLibConfig, tldParser) == false) {
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                        logger.logp(Level.WARNING, CLASS_NAME, "GlobalTagLibraryCache", "jsp warning failed to find jar ["+globalTagLibConfig.getJarName()+"]. Removing from list of available global tag libraries");
                    }
                    itr.remove();
                }
            }
            
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", " about to search for META-INF/taglibcacheconfig.xml");
            }
            for (Enumeration e = this.getClassLoader().getResources("META-INF/taglibcacheconfig.xml"); e.hasMoreElements();) {
                URL url = (URL)e.nextElement();
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                    logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "META-INF/taglibcacheconfig.xml found in {0} ", url.toExternalForm());
                }
                try {
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                        logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "about to open:["+ url.toExternalForm()+"]");
                    }
                    InputStream is = url.openStream();
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                        logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "about to parse inputstream:["+ is+"]");
                    }
                    tagLibCacheConfigParser.parse(is);
                    List configList = tagLibCacheConfigParser.getGlobalTagLibList();
                    for (ListIterator itr = configList.listIterator(); itr.hasNext();) {
                        GlobalTagLibConfig globalTagLibConfig = (GlobalTagLibConfig)itr.next();
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                            logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "globalTagLibConfig: ["+globalTagLibConfig+"]");
                            logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "globalTagLibConfig.getTldPathList() [" + globalTagLibConfig.getTldPathList()+"]");
                            for (Iterator itr2 = globalTagLibConfig.getTldPathList().iterator(); itr2.hasNext();) {
                            	TldPathConfig tldPathConfig = (TldPathConfig)itr2.next();
	                            logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "tldPathConfig.getTldPath() [" + tldPathConfig.getTldPath()+"]");
	                            logger.logp(Level.FINE, CLASS_NAME, "GlobalTagLibraryCache", "tldPathConfig.getUri() [" + tldPathConfig.getUri()+"]");			                            	
                            }                            
                        }
                        if (loadImplicitTldFromJar(globalTagLibConfig, tldParser) == false) {
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                                logger.logp(Level.WARNING, CLASS_NAME, "GlobalTagLibraryCache", "jsp warning failed to find jar ["+globalTagLibConfig.getJarName()+"]. Removing from list of available global tag libraries");
                            }
                            itr.remove();
                        }
                    }
                    globalTagLibConfigList.addAll(configList);
                }
                catch (IOException exc) {
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                        logger.logp(Level.WARNING, CLASS_NAME, "GlobalTagLibraryCache", "jsp warning failed to load META-INF/taglibcacheconfig.xml in jar. url = ["+url+"]", e);
                    }
                }
            }
            
        }
        catch (JspCoreException e) {
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.SEVERE)){
                logger.logp(Level.SEVERE, CLASS_NAME, "GlobalTagLibraryCache", "implicittaglibs failed to load ", e);
            }
        }
        catch (IOException e){
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.SEVERE)){
                logger.logp(Level.SEVERE, CLASS_NAME, "GlobalTagLibraryCache", "implicittaglibs failed to load ", e);
            }
        }
        if(isAnyTraceEnabled&&logger.isLoggable(Level.FINER)){
            logger.exiting(CLASS_NAME, "GlobalTagLibraryCache", "loader: ["+loader+"]");
        } //d651265
    }
    
    private boolean loadImplicitTldFromJar(GlobalTagLibConfig globalTagLibConfig, TldParser tldParser) {
        boolean isAnyTraceEnabled=com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled();
        if(isAnyTraceEnabled&&logger.isLoggable(Level.FINER)){
            logger.entering(CLASS_NAME,"loadImplicitTldFromJar", "globalTagLibConfig.getJarName(): ["+globalTagLibConfig.getJarName()+"]");
        }
        boolean jarFound = false;
        URL jarUrl = this.getClassLoader().getResource(globalTagLibConfig.getJarName());
        if (jarUrl != null) {
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                logger.logp(Level.FINE, CLASS_NAME, "loadImplicitTldFromJar", "jarUrl: ["+jarUrl.toExternalForm()+"]");
            }
            jarFound = true;
            for (Iterator itr = globalTagLibConfig.getTldPathList().iterator(); itr.hasNext();) {
                TldPathConfig tldPathConfig = (TldPathConfig)itr.next();
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                    logger.logp(Level.FINE, CLASS_NAME, "loadImplicitTldFromJar", "tldPathConfig.getUri(): ["+tldPathConfig.getUri()+"]");
                }
                if (tldPathConfig.getUri() != null) {
                    TagLibraryInfoProxy proxy = new TagLibraryInfoProxy(globalTagLibConfig.getJarName(), tldPathConfig.getTldPath(), tldPathConfig.containsListenerDefs(), this);
                    put(tldPathConfig.getUri(), proxy);
                }
                else {
                    JspInputSource tldInputSource = null;            
                                    
                    try {
                        URL url = new URL("jar:" + jarUrl.toString()+"!/");
                        tldInputSource = inputSourceFactory.createJspInputSource(url, tldPathConfig.getTldPath());
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                            logger.logp(Level.FINE, CLASS_NAME, "loadImplicitTldFromJar", "tldInputSource.getRelativeURL(): ["+tldInputSource.getRelativeURL()+"]");
                            logger.logp(Level.FINE, CLASS_NAME, "loadImplicitTldFromJar", "tldInputSource.getAbsoluteURL(): ["+tldInputSource.getAbsoluteURL()+"]");
                            logger.logp(Level.FINE, CLASS_NAME, "loadImplicitTldFromJar", "tldInputSource.getContextURL(): ["+tldInputSource.getContextURL()+"]");
                            logger.logp(Level.FINE, CLASS_NAME, "loadImplicitTldFromJar", "url: ["+url+"]");
                            logger.logp(Level.FINE, CLASS_NAME, "loadImplicitTldFromJar", "tldPathConfig.getTldPath(): ["+tldPathConfig.getTldPath()+"]");
                        }
                        TagLibraryInfoImpl tli = tldParser.parseTLD(tldInputSource, globalTagLibConfig.getJarName());
                        if (tli.getReliableURN() != null) {
                            if (containsKey(tli.getReliableURN()) == false) {
                                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                                    logger.logp(Level.FINE, CLASS_NAME, "loadImplicitTldFromJar", "Global jar tld loaded for {0}", tli.getReliableURN());
                                }
                                tli.setURI(tli.getReliableURN());
                                put(tli.getReliableURN(), tli);
                                tldPathConfig.setUri(tli.getReliableURN());
                                eventListenerList.addAll(tldParser.getEventListenerList());
                            }
                        }
                        else {
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                                logger.logp(Level.WARNING, CLASS_NAME, "loadImplicitTldFromJar", "jsp warning failed to find a uri tag in ["+tldInputSource.getAbsoluteURL()+"]");
                            }
                        }
                    }
                    catch (JspCoreException e) {
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "loadImplicitTldFromJar", "jsp warning failed to load tld in jar. uri = ["+tldInputSource.getAbsoluteURL()+"]", e);
                        }
                    }
                    catch (IOException e) {
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "loadImplicitTldFromJar", "jsp warning failed to load tld in jar. uri = ["+tldInputSource.getAbsoluteURL()+"]", e);
                        }
                    }
                }
            }
        }
        if(isAnyTraceEnabled&&logger.isLoggable(Level.FINE)){
            logger.exiting(CLASS_NAME, "loadImplicitTldFromJar", Boolean.valueOf(jarFound));
        }
        return jarFound;
    }
    
    public JspClassloaderContext getJspClassloaderContext() {
        return this;
    }

    public JspInputSourceFactory getJspInputSourceFactory() {
        return inputSourceFactory;
    }

    public String getRealPath(String path) {
        String realPath = "";
        if (path.startsWith("/"))
            realPath = root + path;
        else
            realPath = root + "/" + path;
        return realPath;
    }
    
    public Set getResourcePaths(String path,boolean searchMetsInfResources) {   	
    	// this object never searched META-INF resources so ignore the booelan
    	return this.getResourcePaths(path);
    }

    public Set getResourcePaths(String paths) {
        java.util.HashSet set = new java.util.HashSet();

        java.io.File rootPath = new java.io.File(root + paths);

        if (rootPath.exists()) {
            java.io.File[] fileList = rootPath.listFiles();
            if (fileList != null) {
                for (int i = 0; i < fileList.length; i++) {
                    String resourcePath = fileList[i].getPath();
                    resourcePath = resourcePath.substring(root.length());
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
    
    public ClassLoader getClassLoader() {
    	 // defect 213137 - return this.loader if it is not null
        if (this.loader!=null) {
        	return this.loader;
        }
        else {
        	return this.getClass().getClassLoader();
        }
    }

    public String getClassPath() {
        return "";
    }
    
    public Map getImplicitTagLibPrefixMap() {
        return implicitTagLibPrefixMap;
    }
    
    public Map getOptimizedTagConfigMap() {
        return optimizedTagConfigMap;
    }
    
    public List getEventListenerList() {
        return eventListenerList;
    }
    
    public Map getGlobalTagLibMapForWebApp(JspCoreContext context, JspXmlExtConfig jspConfig) {
        final boolean isAnyTraceEnabled=com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled();
        if(isAnyTraceEnabled&&logger.isLoggable(Level.FINE)){
            logger.entering(CLASS_NAME, "getGlobalTagLibMapForWebApp", "context: ["+context+"]  jspConfig: ["+jspConfig+"]");
        }//d651265
        Map globalTabLibMap = new HashMap();
        
        for (Iterator itr = implicitTagLibPrefixMap.values().iterator(); itr.hasNext();) {
            String uri = (String)itr.next();
            TagLibraryInfo tli = (TagLibraryInfo)get(uri);
            globalTabLibMap.put(uri, tli);                                    
        }
        
        for (Iterator itr = globalTagLibConfigList.iterator(); itr.hasNext();) {
            GlobalTagLibConfig globalTagLibConfig = (GlobalTagLibConfig)itr.next();
            for (Iterator itr2 = globalTagLibConfig.getTldPathList().iterator(); itr2.hasNext();) {
                boolean tldAvailable = false;
                TldPathConfig tldPathConfig = (TldPathConfig)itr2.next();
                if (tldPathConfig.getAvailabilityConditionList().size() > 0) {
                    for (Iterator itr3 = tldPathConfig.getAvailabilityConditionList().iterator(); itr3.hasNext();) {
                        AvailabilityCondition availabilityCondition = (AvailabilityCondition)itr3.next();
                        if (availabilityCondition.getType().equals(AvailabilityConditionType.webinfFileType)) {
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                                logger.logp(Level.FINE, CLASS_NAME, "getGlobalTagLibMapForWebApp", "WEB-INF File Availability Condition found for {0}", availabilityCondition.getValue());
                            }
                            
                            if (new File(context.getRealPath(availabilityCondition.getValue())).exists()) {
                                tldAvailable = true;
                                break;
                            }
                        }
                        else if (availabilityCondition.getType().equals(AvailabilityConditionType.servletClassNameType)) {
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                                logger.logp(Level.FINE, CLASS_NAME, "getGlobalTagLibMapForWebApp", "Servlet Class Name Availability Condition found for {0}", availabilityCondition.getValue());
                            }
                            
                            if (jspConfig.containsServletClassName(availabilityCondition.getValue())) {
                                tldAvailable = true;
                                break;
                            }
                        }
                    }
                }
                else {
                    tldAvailable = true;
                }
                if (tldAvailable) {
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                        logger.logp(Level.FINE, CLASS_NAME, "getGlobalTagLibMapForWebApp", "global tld {0} is available", tldPathConfig.getUri());
                    }
                    globalTabLibMap.put(tldPathConfig.getUri(), get(tldPathConfig.getUri()));                        
                }
            }
        }
        if(isAnyTraceEnabled&&logger.isLoggable(Level.FINE)){
            logger.exiting(CLASS_NAME, "getGlobalTagLibMapForWebApp");
        }
        return globalTabLibMap;
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
    
    private void loadTldFromJarInputStream(GlobalTagLibConfig globalTagLibConfig, TldParser tldParser) {
        Map entryMap = new HashMap();

        for (Iterator itr = globalTagLibConfig.getTldPathList().iterator(); itr.hasNext();) {
            TldPathConfig tldPathConfig = (TldPathConfig)itr.next();
            entryMap.put(tldPathConfig.getTldPath(), tldPathConfig);
        }

        JarInputStream jis = null;
        try {
            jis = new JarInputStream(globalTagLibConfig.getJarURL().openStream());
            JarEntry je = null;
            byte[] buf = new byte[1024];

            while ((je = jis.getNextJarEntry()) != null) {
                TldPathConfig tldPathConfig = (TldPathConfig)entryMap.get(je.getName());
                if (tldPathConfig != null) {
                    JspInputSource tldInputSource = null;

                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int read;
                        while ((read = jis.read(buf)) != -1) {
                            baos.write(buf, 0, read);
                        }

                        tldInputSource = inputSourceFactory.createJspInputSource(globalTagLibConfig.getJarURL(), tldPathConfig.getTldPath());
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                            logger.logp(Level.FINE, CLASS_NAME, "loadTldFromJarInputStream", "tldInputSource.getRelativeURL(): ["+tldInputSource.getRelativeURL()+"]");
                            logger.logp(Level.FINE, CLASS_NAME, "loadTldFromJarInputStream", "tldInputSource.getAbsoluteURL(): ["+tldInputSource.getAbsoluteURL()+"]");
                            logger.logp(Level.FINE, CLASS_NAME, "loadTldFromJarInputStream", "tldInputSource.getContextURL(): ["+tldInputSource.getContextURL()+"]");
                            logger.logp(Level.FINE, CLASS_NAME, "loadTldFromJarInputStream", "globalTagLibConfig.getJarURL(): ["+globalTagLibConfig.getJarURL()+"]");
                            logger.logp(Level.FINE, CLASS_NAME, "loadTldFromJarInputStream", "tldPathConfig.getTldPath(): ["+tldPathConfig.getTldPath()+"]");
                        }
                        TagLibraryInfoImpl tli = tldParser.parseTLD(tldInputSource, new ByteArrayInputStream(baos.toByteArray()), globalTagLibConfig.getJarURL().toExternalForm());
                        if (tli.getReliableURN() != null) {
                            if (containsKey(tli.getReliableURN()) == false) {
                                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                                    logger.logp(Level.FINE, CLASS_NAME, "loadTldFromJarInputStream", "Global jar tld loaded for {0}", tli.getReliableURN());
                                }
                                tli.setURI(tli.getReliableURN());
                                put(tli.getReliableURN(), tli);
                                tldPathConfig.setUri(tli.getReliableURN());
                                eventListenerList.addAll(tldParser.getEventListenerList());
                            }
                        }
                        else {
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                                logger.logp(Level.WARNING, CLASS_NAME, "loadTldFromJarInputStream", "jsp warning failed to find a uri tag in ["+tldInputSource.getAbsoluteURL()+"]");
                            }
                        }
                    }
                    catch (JspCoreException e) {
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "loadTldFromJarInputStream", "jsp warning failed to load tld in jar. uri = ["+tldInputSource.getAbsoluteURL()+"]", e);
                        }
                    }
                }
            }

        }
        catch (IOException ioe) {
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                logger.logp(Level.WARNING, CLASS_NAME, "loadTldFromJarInputStream", "jsp warning failed to load jar. url = ["+globalTagLibConfig.getJarURL()+"]", ioe);
            }
        }
        finally {
            if (jis != null) try{jis.close();}catch(IOException ioe){}
        }
    }
    
    public void addGlobalTagLibConfig(GlobalTagLibConfig globalTagLibConfig) {
        try {
            TldParser tldParser = new TldParser(this, configManager, false, globalTagLibConfig.getClassloader());
            loadTldFromJarInputStream(globalTagLibConfig, tldParser);
            globalTagLibConfigList.add(globalTagLibConfig);
        }
        catch (JspCoreException e) {
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.SEVERE)){
                logger.logp(Level.SEVERE, CLASS_NAME, "addGlobalTagLibConfig", "failed to create TldParser ", e);
            }
        }
    }
}
