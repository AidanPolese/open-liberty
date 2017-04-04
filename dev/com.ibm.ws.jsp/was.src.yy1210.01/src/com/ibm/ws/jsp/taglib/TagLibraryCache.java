//IBM Confidential OCO Source Material
//  5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//  The source code for this program is not published or otherwise divested
//  of its trade secrets, irrespective of what has been deposited with the
//  U.S. Copyright Office.
//  defect 396002 CTS: no jsp fatal translation error for  taglib after actions Scott Johnson 10/17/2006
// jsp2.1work
//415289 70FVT:useinmemory: Error received when hitting non-existent jsp    2007/01/17 09:32:07  Scott Johnson
// Defect PK68590 2008/08/28 sartoris   Unable to locate tld files that are in the loose lib dir (and not in a jar).
// Defect PK69220 2008/10/10 sartoris   Add a flag to be able turn off tld searching through an application.
// Defect PM03123 2010/01/19 pmdinh		Continue from PK68590 to support tld files that are in a jar for a loose lib config.
// Defect PM07608 2010/04/02 pmdinh     NPE when URI element is missing in TLD which deployed in RAD loose library env.

package com.ibm.ws.jsp.taglib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.tagext.TagFileInfo;
import javax.servlet.jsp.tagext.TagInfo;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.configuration.JspConfigurationManager;
import com.ibm.ws.jsp.configuration.JspXmlExtConfig;
import com.ibm.ws.jsp.translator.JspTranslator;
import com.ibm.ws.jsp.translator.JspTranslatorFactory;
import com.ibm.ws.jsp.translator.optimizedtag.OptimizedTag;
import com.ibm.ws.jsp.translator.optimizedtag.OptimizedTagConfig;
import com.ibm.ws.jsp.translator.optimizedtag.OptimizedTagConfigParser;
import com.ibm.ws.jsp.translator.utils.NameMangler;
import com.ibm.ws.jsp.translator.utils.TagFileId;
import com.ibm.ws.jsp.translator.visitor.JspVisitorInputMap;
import com.ibm.ws.jsp.translator.visitor.tagfilescan.TagFileScanResult;
import com.ibm.wsspi.jsp.context.translation.JspTranslationContext;
import com.ibm.wsspi.jsp.resource.JspInputSource;
import com.ibm.wsspi.jsp.resource.translation.TagFileResources;

public class TagLibraryCache extends Hashtable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3256719585204975926L;
    static private Logger logger;
    private static final String CLASS_NAME="com.ibm.ws.jsp.taglib.TagLibraryCache";
    static{
        logger = Logger.getLogger("com.ibm.ws.jsp");
    }

    static final String TAGFILE_SCAN_ID = "TagFileScan";

    public static final int ABS_URI = 0;
    public static final int ROOT_REL_URI = 1;
    public static final int NOROOT_REL_URI = 2;
    private JspTranslationContext ctxt = null;
    private TldParser tldParser = null;
    private Map tagClassMap = new HashMap();
    private Map tagFileResourcesMap = new HashMap();
    private Map implicitTagLibPrefixMap = new HashMap();
    private Map optimizedTagConfigMap = null;
    private Map looseLibMap = null;
    private List eventListenerList = new ArrayList();
    private JspConfigurationManager configManager = null;
    private String outputDir = null;
    private Map tagFileLockMap = null;
    private JspOptions jspOptions = null; //396002

    public TagLibraryCache(JspTranslationContext ctxt,
                           Map webxmlTagLibMap,
                           JspOptions jspOptions,
                           JspConfigurationManager configManager,
                           Map globalMap,
                           Map implicitMap,
                           Map optimizedTagMap) throws JspCoreException {
        this.ctxt = ctxt;
        this.configManager = configManager;
        this.jspOptions = jspOptions; // 396002
        outputDir = jspOptions.getOutputDir().getPath();
        // defect 208800
        if (jspOptions.getLooseLibMap() != null) {
            looseLibMap = jspOptions.getLooseLibMap();
        }
        tldParser = new TldParser(ctxt, configManager, false);
        List loadedLocations = loadWebXmlMap(webxmlTagLibMap);
        
        //PK69220 - start
        if(logger.isLoggable(Level.FINE)){
            logger.logp(Level.FINE, CLASS_NAME, "TagLibraryCache", "disableTldSearch is set to: " + jspOptions.isDisableTldSearch());
        }
        if (!jspOptions.isDisableTldSearch()) {
        //PK69220 - end
            loadLibJarMap(loadedLocations);
            loadWebInfMap("/WEB-INF", loadedLocations);
            loadWebInfTagFiles("/WEB-INF/tags");
        } //PK69220

        //PK68590 start
        if (looseLibMap != null) {
            for (Iterator j = looseLibMap.keySet().iterator(); j.hasNext();) {
                String looseKey = (String)j.next();
        		loadLooseLibTagFiles((String)looseLibMap.get(looseKey), loadedLocations, looseKey);  //PM07608	//PM03123
            }
        }
        //PK68590 end

        if (jspOptions.isUseImplicitTagLibs() &&
             (jspOptions.getTranslationContextClass() == null 
              	|| (jspOptions.getTranslationContextClass() != null && // 415289
              	    jspOptions.getTranslationContextClass().equals(Constants.IN_MEMORY_TRANSLATION_CONTEXT_CLASS)))) { 
            for (Iterator itr = globalMap.keySet().iterator(); itr.hasNext();) {
                String uri = (String)itr.next();
                if (containsKey(uri) == false) {
                    Object o = globalMap.get(uri);
                    if (o instanceof TagLibraryInfoImpl) {
                        TagLibraryInfoImpl impl = (TagLibraryInfoImpl)o;
                        TagLibraryInfoImpl tli = impl.copy("");
                        put(uri, tli);
                    }
                    else if (o instanceof TagLibraryInfoProxy) {
                        put(uri, o);
                    }
                }
            }
            implicitTagLibPrefixMap.putAll(implicitMap);
        }

        if (jspOptions.isUseOptimizedTags()) {
            JspInputSource inputSource = ctxt.getJspInputSourceFactory().createJspInputSource("/WEB-INF/optimizedtags.xml");
            try {
                InputStream is = inputSource.getInputStream();
                if (is != null) {
                    OptimizedTagConfigParser optimizedTagConfigParser = new OptimizedTagConfigParser(ctxt);
                    optimizedTagConfigMap = optimizedTagConfigParser.parse(is);
                }
                else {
                    optimizedTagConfigMap = new HashMap();
                }
            }
            catch (IOException e) {
                optimizedTagConfigMap = new HashMap();
            }
            optimizedTagConfigMap.putAll(optimizedTagMap);
        }
    }

    private List loadWebXmlMap(Map webxmlTagLibMap) throws JspCoreException {
        List loadedLocations = new ArrayList();
        for (Iterator itr = webxmlTagLibMap.keySet().iterator(); itr.hasNext();) {
            String taglibUri = (String)itr.next();
            if (containsKey(taglibUri) == false) {
                String taglibLocation = (String)webxmlTagLibMap.get(taglibUri);
                TagLibraryInfoImpl tli = null;
                String loadedLocation = taglibLocation;
                if (taglibLocation.endsWith(".jar")) {
                    tli = loadTaglibTldFromJar(taglibLocation);
                    loadedLocation = loadedLocation + "/META-INF/taglib.tld";
                }
                else {
                    JspInputSource inputSource = ctxt.getJspInputSourceFactory().createJspInputSource(taglibLocation);
                    //tli = loadSerializedTld(inputSource, inputSource);
                    //if (tli == null) {
                    try {
                        tli = tldParser.parseTLD(inputSource, "webinf");
                        //if (tli != null) {
                        //    serializeTld(inputSource, (TagLibraryInfoImpl)tli);
                        //}
                    }
                    catch (JspCoreException e) {
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "loadWebXmlMap", "jsp warning failed to load tld at ["+taglibLocation+"]");
                        }
                    }
                    //}
                }
                if (tli == null) {
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                        logger.logp(Level.WARNING, CLASS_NAME, "loadWebXmlMap", "jsp warning failed to load tld at ["+taglibLocation+"]");
                    }
                }
                else {
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                        logger.logp(Level.FINE, CLASS_NAME, "loadWebXmlMap", "webxml tld loaded for {0}", taglibUri);
                    }
                    put(taglibUri, tli);
                    tli.setURI(taglibUri);
                    loadedLocations.add(loadedLocation);
                    eventListenerList.addAll(tldParser.getEventListenerList());
                }
            }
        }
        return loadedLocations;
    }

    private void loadLibJarMap(List loadedLocations) {
    	// No need to search META-INF resources
        Set libSet = ctxt.getResourcePaths("/WEB-INF/lib",false);
        if (libSet != null) {
            Iterator it = libSet.iterator();
            while (it.hasNext()) {
                String resourcePath = (String) it.next();
                if (resourcePath.endsWith(".jar")) {
                    loadTldsFromJar(resourcePath, loadedLocations);
                }
            }
        }
    }

    protected void loadTldsFromJar(String resourcePath, List loadedLocations) {
        try {
            JspInputSource inputSource = ctxt.getJspInputSourceFactory().createJspInputSource(resourcePath);
            URL url = new URL("jar:"+inputSource.getAbsoluteURL().toExternalForm()+"!/");
            loadTldsFromJar(url, resourcePath, loadedLocations, null);
        }
        catch (MalformedURLException e) {
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                logger.logp(Level.WARNING, CLASS_NAME, "loadTldsFromJar", "jsp error failed to load tld in jar. uri = ["+resourcePath+"]", e);
            }
        }
    }

    public void loadTldsFromJar(URL url, String resourcePath, List loadedLocations, JspXmlExtConfig webAppConfig) {
    	if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
        	logger.logp(Level.FINE, CLASS_NAME, "loadTldsFromJar", "url ["+url+"]"+"resourcePath ["+resourcePath+"] loadedLocations ["+loadedLocations+"] webAppConfig ["+webAppConfig+"]" );
        }
        JarFile jarFile = null;
        InputStream stream = null;
        String name = null;
        try {
            JarURLConnection conn = (JarURLConnection)url.openConnection();
            conn.setUseCaches(false);
            jarFile = conn.getJarFile();

            String originatorId = jarFile.getName();
            originatorId = originatorId.substring(0, originatorId.indexOf(".jar"));
            if (originatorId.indexOf(File.separatorChar) != -1)
                originatorId = originatorId.substring(originatorId.lastIndexOf(File.separatorChar)+1);
            originatorId = NameMangler.mangleString(originatorId);

            Enumeration entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                name = entry.getName();
                if (name.startsWith("META-INF/") &&
                    name.endsWith(".tld") &&
                    loadedLocations.contains(resourcePath + "/" + name) == false) {
                    stream = jarFile.getInputStream(entry);
                    JspInputSource tldInputSource = ctxt.getJspInputSourceFactory().createJspInputSource(url, name);
                    //TagLibraryInfoImpl tli = loadSerializedTld(tldInputSource, inputSource);

                    //if (tli == null) {
                    try {
                        TagLibraryInfoImpl tli = tldParser.parseTLD(tldInputSource, stream, originatorId);
                        
                        //516822 - If no URI is defined in the tag, we still want to load it in case it has listeners
                        //use the resourcePath + "/" + name as the key
                        String uri = null;
                        if (tli.getReliableURN() != null && tli.getReliableURN().trim().equals("") == false) {
                            uri = tli.getReliableURN();
                        } else {
                            uri = resourcePath + "/" + name;
                        }
                        
                        //if (tli.getReliableURN() != null && tli.getReliableURN().trim().equals("") == false) {
                            tli.setURI(uri);
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                            	logger.logp(Level.FINE, CLASS_NAME, "loadTldsFromJar", "webAppConfig is "+webAppConfig);
                            }
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE) && webAppConfig!=null){
                                logger.logp(Level.FINE, CLASS_NAME, "loadTldsFromJar", "tli URN is "+ uri+
                                													   " :webAppConfig.getTagLibMap() is "+webAppConfig.getTagLibMap()+
                                													   " :webAppConfig.getTagLibMap().containsKey(uri) is "+webAppConfig.getTagLibMap().containsKey(uri)+
                                													   " :containsKey(uri) is "+containsKey(uri) );
                            }
                            if ((webAppConfig!=null && webAppConfig.getTagLibMap().containsKey(uri)==false)
                                    || (webAppConfig==null && containsKey(uri) == false)) {
                                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                                    logger.logp(Level.FINE, CLASS_NAME, "loadTldsFromJar", "jar tld loaded for {0}", uri);
                                }
                                put(uri, tli);
                                //serializeTld(tldInputSource, tli);
                                eventListenerList.addAll(tldParser.getEventListenerList());
                                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                                    logger.logp(Level.FINE, CLASS_NAME, "loadTldsFromJar", "tldParser.getEventListenerList() ["+ tldParser.getEventListenerList()+"]");
                                }
                            }
                        //}
                    }
                    catch (JspCoreException e) {
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "loadTldsFromJar", "jsp error failed to load tld in jar. uri = ["+resourcePath+"]", e);
                        }
                    }
                    //}

                    stream.close();
                    stream = null;
                }
            }
        }
        catch (Exception e) {
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                logger.logp(Level.WARNING, CLASS_NAME, "loadTldsFromJar", "jsp error failed to load tld in jar. uri = ["+resourcePath+"]", e);
            }
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (Throwable t) {}
            }
            if (jarFile != null) {
                try {
                    jarFile.close();
                }
                catch (Throwable t) {}
            }
        }
    }

    private void loadWebInfMap(String webInfPath, List loadedLocations) {
    	// No need to search META-INF resources
        Set libSet = ctxt.getResourcePaths(webInfPath,false);
        if (libSet != null) {
            Iterator it = libSet.iterator();
            while (it.hasNext()) {
                String resourcePath = (String) it.next();
                if (resourcePath.endsWith(".tld") && loadedLocations.contains(resourcePath) == false) {
                    try {
                        JspInputSource inputSource = ctxt.getJspInputSourceFactory().createJspInputSource(resourcePath);
                        //TagLibraryInfoImpl tli = loadSerializedTld(inputSource, inputSource);
                        //if (tli == null) {
                        TagLibraryInfoImpl tli = tldParser.parseTLD(inputSource, "webinf");
                        //516822 - If no URI is defined in the tag, we still want to load it in case it has listeners
                        //use the resourcePath as the key
                        String uri = null;
                        if (tli.getReliableURN() != null && tli.getReliableURN().trim().equals("") == false) {
                            uri = tli.getReliableURN();
                        } else {
                            uri = resourcePath;
                        }
                        //if (tli.getReliableURN() != null && tli.getReliableURN().trim().equals("") == false) {
                            tli.setURI(uri);
                            if (containsKey(uri) == false) {
                                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                                    logger.logp(Level.FINE, CLASS_NAME, "loadWebInfMap", "webinf tld loaded for {0}", uri);
                                }
                                put(uri, tli);
                                //serializeTld(inputSource, tli);
                                eventListenerList.addAll(tldParser.getEventListenerList());
                            }
                        //}
                        //}
                    }
                    catch (JspCoreException e) {
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "loadWebInfMap", "webinf tld failed to load for resourcePath =[" + resourcePath+ "]", e);
                        }
                    }
                }
                else if (resourcePath.endsWith("/")) {
                    loadWebInfMap(resourcePath.substring(0, resourcePath.lastIndexOf('/')), loadedLocations);
                }
            }
        }
    }

    private void loadWebInfTagFiles(String tagsDir) throws JspCoreException {
    	// No need to search META-INF resources
        Set libSet = ctxt.getResourcePaths(tagsDir,false);
        ArrayList list = new ArrayList();
        TagLibraryInfoImpl tli = null;
        //516671 - if there is an exception reading in the implicit.tld, we need to throw an exception at translation time.
        //therefore, we will not put any of the tags in the map and the app will get an error saying it can't find the tags.
        boolean succeeded=true;
        if (libSet != null) {
        	ImplicitTldParser ImplicitTldParser = new ImplicitTldParser(ctxt, configManager, false);
            for (Iterator it = libSet.iterator(); it.hasNext();) {
                String resourcePath = (String) it.next();
                if (resourcePath.endsWith("/")) {
                    loadWebInfTagFiles(resourcePath.substring(0, resourcePath.lastIndexOf('/')));
                }
                else if (resourcePath.endsWith("/implicit.tld")) { // must be named implicit.tld
                    try {
                        JspInputSource inputSource = ctxt.getJspInputSourceFactory().createJspInputSource(resourcePath);
                        tli = ImplicitTldParser.parseTLD(inputSource, "webinf");
                        if (tli != null) {
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                                logger.logp(Level.FINE, CLASS_NAME, "loadWebInfTagFiles", "Got TagLibraryInfoImpl for [{0}], TLD file [{1}]", new Object[]{tagsDir,resourcePath});
                            }
                        }
                    }
                    catch (JspCoreException e) {
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "loadWebInfTagFiles", "webinf tagfile directory tld failed to load for resourcePath =[" + resourcePath+ "]", e);
                        }
                        //Exception occurred, don't add any of the tags.
                        succeeded=false;
                    }
                }
            }
            if (succeeded) { //if there is an exception parsing the implicit.tld - don't load any of the tags so we throw a translation exception during runtime
                JspInputSource inputSource = ctxt.getJspInputSourceFactory().createJspInputSource(tagsDir);
                TagLibraryInfoImpl implicitTli = new ImplicitTagLibraryInfoImpl(tagsDir, inputSource);
    
                if (tli!=null) { // configure the implicit taglib with information from implicit.tld, if any
                				// 512316 - need to set tlib and jsp version in implicitTlibefore adding tag info to the map 
                	if (tli.getRequiredVersion()!=null)
                		implicitTli.setRequiredVersion(tli.getRequiredVersion());
                	if (tli.getTlibversion()!=null)
                		implicitTli.setTlibversion(tli.getTlibversion());
                }
                for (Iterator it = libSet.iterator(); it.hasNext();) {
                    String resourcePath = (String) it.next();
                    if (resourcePath.endsWith(".tag") || resourcePath.endsWith(".tagx")) {
                        try {
                            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)) {
                                logger.logp(Level.FINEST, CLASS_NAME, "loadWebInfTagFiles", "about to do tagfilescan for = ["+resourcePath+"]");
                            }
                            JspInputSource tagFileInputSource = ctxt.getJspInputSourceFactory().copyJspInputSource(implicitTli.getInputSource(), resourcePath);
                            JspTranslator jspTranslator = JspTranslatorFactory.getFactory().createTranslator(TAGFILE_SCAN_ID,
                                                                                                             tagFileInputSource,
                                                                                                             ctxt,
                                                                                                             configManager.createJspConfiguration(),
                                                                                                             jspOptions,  // 396002
                                                                                                             implicitTagLibPrefixMap);
    
                            JspVisitorInputMap  inputMap = new JspVisitorInputMap();
                            inputMap.put("TagLibraryInfo", implicitTli);
                            String name = resourcePath.substring(resourcePath.lastIndexOf('/')+1);
                            name = name.substring(0, name.indexOf(".tag"));
                            inputMap.put("TagFileName", name);
                            inputMap.put("TagFilePath", resourcePath);
    
                            HashMap results = jspTranslator.processVisitors(inputMap);
                            TagFileScanResult result = (TagFileScanResult)results.get("TagFileScan");
                            TagFileInfo tfi = new TagFileInfo(name, resourcePath, result.getTagInfo());
                            list.add(tfi);
                            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)) {
                                logger.logp(Level.FINEST, CLASS_NAME, "loadWebInfTagFiles", "TagLibraryCache TagFileInfo tfi= ["+tfi+"]");
                            }
                        }
                        catch (JspCoreException e) {
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                                logger.logp(Level.WARNING, CLASS_NAME, "loadWebInfTagFiles", "webinf tagfile failed to scan =[" + resourcePath+ "]", e);
                            }
                        }
                    }
                }
                if (list.size() > 0) {
                    implicitTli.setTagFiles(list);
                    list.clear();
                    implicitTli.setFunctions(list);
                    implicitTli.setTags(list);
                    
                    if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                        logger.logp(Level.FINE, CLASS_NAME, "loadWebInfTagFiles", "Adding ImplicitTagLibraryInfoImpl for [{0}]", tagsDir);
                        logger.logp(Level.FINE, CLASS_NAME, "loadWebInfTagFiles", "  ImplicitTagLibraryInfoImpl=[{0}]", implicitTli);
                    }
                    put(tagsDir, implicitTli);
                }
                else if (tli!=null) {
                	put(tagsDir, tli); // just put the info from implicit.tld even though there are no tag files
                }
            }
        }
    }

    private TagLibraryInfoImpl loadTaglibTldFromJar(String uri) {
        TagLibraryInfoImpl tli = null;

        if (looseLibMap != null && looseLibMap.containsKey(uri)) {
            String tldLocation = (String)looseLibMap.get(uri);
            try {
                URL looseLibURL = new File(tldLocation).toURL();
                JspInputSource inputSource = ctxt.getJspInputSourceFactory().createJspInputSource(looseLibURL, "META-INF/taglib.tld");
                tli = tldParser.parseTLD(inputSource, "webinf");
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                    logger.logp(Level.FINE, CLASS_NAME, "loadTaglibTldFromJar", "tld loaded for [{0}]", uri);
                }
            }
            catch (JspCoreException e) {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                    logger.logp(Level.WARNING, CLASS_NAME, "loadTaglibTldFromJar", "jsp error failed to parse loose library tld . location = ["+tldLocation+"]", e);
                }
            }
            catch (MalformedURLException e) {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                    logger.logp(Level.WARNING, CLASS_NAME, "loadTaglibTldFromJar", "jsp error failed to parse loose library tld . location = ["+tldLocation+"]", e);
                }
            }
        }
        else {
            InputStream stream = null;
            JarFile jarFile = null;
            try {
                JspInputSource inputSource = ctxt.getJspInputSourceFactory().createJspInputSource(uri);
                URL url = new URL("jar:"+inputSource.getAbsoluteURL().toExternalForm()+"!/");
                JarURLConnection conn = (JarURLConnection)url.openConnection();
                conn.setUseCaches(false);
                try {
                    jarFile = conn.getJarFile();
                }
                catch (IOException e) {
                    URL jarurl = ctxt.getJspClassloaderContext().getClassLoader().getResource(uri);
                    if (jarurl != null) {
                        jarurl = new URL("jar:"+jarurl.toExternalForm()+"!/");
                        conn = (JarURLConnection)jarurl.openConnection();
                        conn.setUseCaches(false);
                        jarFile = conn.getJarFile();
                    }
                    // begin  221334: throw exception if tld jar cannot be located.
                    else{
                        throw new JspCoreException ("jsp.error.unable.to.locate.tld.jar.file", new Object[]{uri});
                    }
                    // end  221334: throw exception if tld jar cannot be located.
                }

                Enumeration entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) entries.nextElement();
                    String name = entry.getName();
                    if (name.equals("META-INF/taglib.tld")) {
                        stream = jarFile.getInputStream(entry);
                        String originatorId = jarFile.getName();
                        originatorId = originatorId.substring(0, originatorId.indexOf(".jar"));
                        if (originatorId.indexOf(File.separatorChar) != -1)
                            originatorId = originatorId.substring(originatorId.lastIndexOf(File.separatorChar)+1);
                        originatorId = NameMangler.mangleString(originatorId);

                        JspInputSource tldInputSource = ctxt.getJspInputSourceFactory().createJspInputSource(url, "META-INF/taglib.tld");

                        //tli = loadSerializedTld(tldInputSource, inputSource);
                        //if (tli == null) {
                        tli = tldParser.parseTLD(tldInputSource, stream, originatorId);
                        if (tli != null) {
                            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                                logger.logp(Level.FINE, CLASS_NAME, "loadTaglibTldFromJar", "tld loaded for [{0}]", uri);
                            }
                            //serializeTld(tldInputSource, tli);
                        }
                        //}
                    }
                }
            }
            catch (JspCoreException e) {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                    logger.logp(Level.WARNING, CLASS_NAME, "loadTaglibTldFromJar", "jsp error failed to parse tld in jar. uri = ["+uri+"]", e);
                }
            }
            catch (IOException e) {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                    logger.logp(Level.WARNING, CLASS_NAME, "loadTaglibTldFromJar", "jsp error failed to parse tld in jar. uri = ["+uri+"]", e);
                }
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (Throwable t) {}
                }
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    }
                    catch (Throwable t) {}
                }
            }
        }
        return tli;
    }
    
    //PK68590 start
    private void loadLooseLibTagFiles(String looseLibDir, List loadedLocations, String looseKey) {    //PM07608  //PM03123
        if(logger.isLoggable(Level.FINE)){
            logger.logp(Level.FINE, CLASS_NAME, "loadLooseLibTagFiles", "looseLibDir {0}", looseLibDir);
        }
        
        File looseLibDirFile = new File(looseLibDir);
        if (looseLibDirFile == null)
            return;
                
        String [] list = looseLibDirFile.list();
        
        if (list == null){               						//PM03123  null if not denote a directory (i.e this is a file)
    		list = new String[1];								
    		list[0] = looseLibDir;								
    	}														//PM03123
        
        if (list != null) {
            TagLibraryInfoImpl tli = null;
            for (int j = 0; j < list.length; j++) {
                String resourcePath = (String) list[j];
                if (resourcePath.endsWith(".tld")) {
                    if(logger.isLoggable(Level.FINE)){
                            logger.logp(Level.FINE, CLASS_NAME, "loadLooseLibTagFiles", "tld located {0}", resourcePath);
                        }
                        try {
                            URL looseLibURL = new File(looseLibDir).toURL();
                            JspInputSource inputSource = ctxt.getJspInputSourceFactory().createJspInputSource(looseLibURL, resourcePath);
                            tli = tldParser.parseTLD(inputSource, "webinf");
                            if(logger.isLoggable(Level.FINE)){
                                logger.logp(Level.FINE, CLASS_NAME, "loadLooseLibTagFiles", "tli {0}", tli);
                            }
                        }
                        catch (JspCoreException e) {
                            if(logger.isLoggable(Level.WARNING)){
                                logger.logp(Level.WARNING, CLASS_NAME, "loadLooseLibTagFiles", "jsp error failed to parse loose library tld . location = ["+looseLibDir+"]", e);
                            }
                        }
                        catch (MalformedURLException e) {
                            if(logger.isLoggable(Level.WARNING)){
                                logger.logp(Level.WARNING, CLASS_NAME, "loadLooseLibTagFiles", "jsp error failed to parse loose library tld . location = ["+looseLibDir+"]", e);
                            }
                        }

                        if (tli != null) {
                            String looseLibURN = tli.getReliableURN();
                            //PM07608
                        	if (looseLibURN == null){
                        		looseKey = looseKey.replace('\\', '/');        // use URI defined in RAD looseLibConfig xml file
                        		looseLibURN = looseKey + "/" + resourcePath;
                        		if(logger.isLoggable(Level.FINE)){
                        			logger.logp(Level.FINE, CLASS_NAME, "loadLooseLibTagFiles", "jsp failed to find a uri sub-element in ["+resourcePath+"], default uri to \""+ looseLibURN+"\"");
                        		}
                        	}
                        	//PM07608
                            if(logger.isLoggable(Level.FINE)){
                                logger.logp(Level.FINE, CLASS_NAME, "loadLooseLibTagFiles", "Adding TagLibraryInfoImpl for [{0}]", looseLibURN);
                            }
                            
                            put(looseLibURN, tli);
                        }                  
                }
                //PM03123 - start
                else if (resourcePath.endsWith(".jar")){
                	if(logger.isLoggable(Level.FINE)){
                        logger.logp(Level.FINE, CLASS_NAME, "loadLooseLibTagFiles", "looseLibDir is a jar");
                    }
            		try{
            			URL url = new URL("jar:"+ new File(resourcePath).toURL() + "!/" );
            			loadTldsFromJar(url, resourcePath, loadedLocations, null);
            		}
            		catch (MalformedURLException e) {
                        if(logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "loadLooseLibTagFiles", "jsp error failed to parse loose library tld . location = ["+resourcePath+"]", e);
                        }
                    }
                }
                //PM03123 - end 
                else {
                    //not a tld...maybe it's a directory
                    StringBuffer nestedDir = new StringBuffer(looseLibDir);
                    if (looseLibDir.endsWith("/") || looseLibDir.endsWith("\\")) {
                        nestedDir.append(resourcePath);
                    } else {
                        nestedDir.append("/");
                        nestedDir.append(resourcePath);                     
                    }
                    if (new File(nestedDir.toString()).isDirectory()) {
                    	loadLooseLibTagFiles(nestedDir.toString(), loadedLocations, looseKey);    //PM07608		//PM03123
                    }
                }
            }
        }
    }
    //PK68590 end    

    public synchronized TagLibraryInfoImpl getTagLibraryInfo(String uri, String prefix, String jspUri) {
        int type = uriType(uri);
        TagLibraryInfoImpl tli = null;
        if (containsKey(uri)) {
            Object o = get(uri);
            if (o instanceof TagLibraryInfoImpl) {
                TagLibraryInfoImpl impl = (TagLibraryInfoImpl)o;
                tli = impl.copy(prefix);
            }
            else if (o instanceof TagLibraryInfoProxy) {
                TagLibraryInfoProxy proxy = (TagLibraryInfoProxy)o;
                tli = proxy.getTagLibraryInfoImpl(prefix);
                if(tli != null){    // defect 220310: ensure tli is not null.
                    put(uri, tli);
                }
            }
        }
        else {
            if (type == ROOT_REL_URI || type == NOROOT_REL_URI) {
                if (uri.endsWith(".jar")) {
                    TagLibraryInfoImpl impl = loadTaglibTldFromJar(uri);
                    if (impl != null) {
                        impl.setURI(uri);
                        put(uri, impl);
                        tli = impl.copy(prefix);
                    }
                }
                else {
                    try {
                        TagLibraryInfoImpl impl = null;
                        String path = uri;
                        if (type == NOROOT_REL_URI) {
                            path = jspUri.substring(0, jspUri.lastIndexOf("/") + 1);
                            path = path + uri;
                        }
                        JspInputSource tldInputSource = ctxt.getJspInputSourceFactory().createJspInputSource(path);
                        impl = tldParser.parseTLD(tldInputSource, "webinf");
                        if (impl != null) {
                            impl.setURI(uri);
                            put(uri, impl);
                            tli = impl.copy(prefix);
                        }
                    }
                    catch (JspCoreException e) {
                       if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)){
                            logger.logp(Level.WARNING, CLASS_NAME, "getTagLibraryInfo", "jsp error failed to parse tld in WEB-INF. uri = ["+uri+"]", e);
                       }
                    }
                }
            }
        }
        return (tli);
    }

    public synchronized TagFileResources getTagFileResources(TagFileResources tagFileResources) {
        if (tagFileResourcesMap.containsKey(tagFileResources.getInputSource().getAbsoluteURL().toExternalForm()) == false) {
            tagFileResourcesMap.put(tagFileResources.getInputSource().getAbsoluteURL().toExternalForm(), tagFileResources);
            return (tagFileResources);
        }
        else {
            return (TagFileResources)tagFileResourcesMap.get(tagFileResources.getInputSource().getAbsoluteURL().toExternalForm());
        }
    }

    public synchronized TagClassInfo getTagClassInfo(TagInfo tag) {
        return (TagClassInfo)tagClassMap.get(tag.getTagClassName());
    }

    public synchronized void addTagClassInfo(TagInfo ti, Class tagClass) {
        tagClassMap.put(ti.getTagClassName(), new TagClassInfo(tagClass));
    }

    public synchronized void addTagFileClassInfo(TagFileInfo tfi) {
        tagClassMap.put(tfi.getTagInfo().getTagClassName(), new TagFileClassInfo(tfi.getTagInfo()));
    }

    public synchronized void reloadTld(String tldFilePath, long timestamp) throws JspCoreException {
        String tliKey = null;
        for (Iterator itr = this.keySet().iterator(); itr.hasNext();) {
            String key = (String)itr.next();
            Object o = get(key);
            if (o instanceof TagLibraryInfoImpl) {
                TagLibraryInfoImpl tli = (TagLibraryInfoImpl)o;
                if (tli.getTldFilePath() != null && tli.getTldFilePath().equals(tldFilePath)) {
                    tliKey = key;
                    break;
                }
            }
        }

        if (tliKey != null) {
            TagLibraryInfoImpl oldTli = (TagLibraryInfoImpl)get(tliKey);
            if (oldTli.getLoadedTimestamp() < timestamp) {
                TagLibraryInfoImpl tli = tldParser.parseTLD(oldTli.getInputSource(), "webinf");
                tli.setURI(oldTli.getURI());
                put(tliKey, tli);
                oldTli = null;
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                    logger.logp(Level.FINE, CLASS_NAME, "reloadTld", "tld [{0}] reloaded", tldFilePath);
                }
            }
        }
    }

    public synchronized TagLibraryInfoImpl reloadImplicitTld(String tagDir) throws JspCoreException {
        TagLibraryInfoImpl tli  = null;
        if (containsKey(tagDir)) {
            remove(tagDir);
            loadWebInfTagFiles(tagDir);
            tli = (TagLibraryInfoImpl)get(tagDir);
        }
        return tli;
    }

    public Map getImplicitTagLibPrefixMap() {
        return implicitTagLibPrefixMap;
    }

    public List getEventListenerList() {
        return eventListenerList;
    }

    public synchronized OptimizedTag getOptimizedTag(String tlibUri, String tlibVersion, String shortName) {
        OptimizedTag optTag = null;

        String key = tlibUri + tlibVersion + shortName;
        if (optimizedTagConfigMap.containsKey(key)) {
            OptimizedTagConfig optTagConfig = (OptimizedTagConfig)optimizedTagConfigMap.get(key);
            try {
                optTag = (OptimizedTag)optTagConfig.getOptClass().newInstance();
            }
            catch (InstantiationException e) {
                logger.logp(Level.WARNING, CLASS_NAME, "getOptimizedTag", "failed to instantiate optimized tag [" + optTagConfig.getOptClass() +"]", e);
            }
            catch (IllegalAccessException e) {
                logger.logp(Level.WARNING, CLASS_NAME, "getOptimizedTag", "Illegal access of optimized tag [" + optTagConfig.getOptClass() +"]", e);
            }
        }

        return (optTag);
    }

    public synchronized Object getTagFileLock(List tagFileIdList) {
        Object lock = null;

        if (tagFileLockMap == null) {
            tagFileLockMap = new HashMap();
        }

        for (Iterator itr = tagFileIdList.iterator(); itr.hasNext();) {
            TagFileId tagFileId = (TagFileId) itr.next();
            Object o = tagFileLockMap.get(tagFileId.toString());
            if (o != null) {
                lock = o;
                break;
            }
        }

        if (lock == null) {
            lock = new Object();

            for (Iterator itr = tagFileIdList.iterator(); itr.hasNext();) {
                TagFileId tagFileId = (TagFileId) itr.next();
                tagFileLockMap.put(tagFileId.toString(), lock);
            }
        }

        return lock;
    }

    public synchronized void releaseTagFileLock(List tagFileIdList) {
        for (Iterator itr = tagFileIdList.iterator(); itr.hasNext();) {
            TagFileId tagFileId = (TagFileId) itr.next();
            tagFileLockMap.remove(tagFileId.toString());
        }
    }

    private TagLibraryInfoImpl loadSerializedTld(JspInputSource source, JspInputSource comparisonSource) {
        TagLibraryInfoImpl tli = null;

        String sourceFile = source.getAbsoluteURL().toExternalForm().replace('\\', '_');
        sourceFile = sourceFile.replace('/', '_');
        File serialixedTliFile = new File(outputDir + File.separator +
                                          NameMangler.mangleString(sourceFile) +
                                          ".ser");
        if (serialixedTliFile.exists()) {
            if (serialixedTliFile.lastModified() >= comparisonSource.getLastModified()) {
                ObjectInputStream ois = null;
                FileInputStream fis = null;

                try {
                    fis = new FileInputStream(serialixedTliFile);
                    ois = new ObjectInputStream(fis);
                    tli = (TagLibraryInfoImpl)ois.readObject();
                    if (tli != null)
                        System.out.println("tld loaded from " + serialixedTliFile.getPath());
                }
                catch (Exception e) {}
                finally {
                    try {
                        if (fis != null)
                            fis.close();
                        if (ois != null)
                            ois.close();
                    }
                    catch (IOException e){}
                }
            }
        }

        return tli;
    }

    private void serializeTld(JspInputSource source, TagLibraryInfoImpl tli) {

        String sourceFile = source.getAbsoluteURL().toExternalForm().replace('\\', '_');
        sourceFile = sourceFile.replace('/', '_');
        File serialixedTliFile = new File(outputDir + File.separator +
                                          NameMangler.mangleString(sourceFile) +
                                          ".ser");
        System.out.println("serialixedTliFile = " + serialixedTliFile.getPath());
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(serialixedTliFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(tli);
            System.out.println("tld serialized to " + serialixedTliFile.getPath());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fos != null)
                    fos.close();
                if (oos != null)
                    oos.close();
            }
            catch (IOException e){}
        }

    }

    private static int uriType(String uri) {
        if (uri.indexOf(':') != -1) {
            return ABS_URI;
        }
        else if (uri.startsWith("/")) {
            return ROOT_REL_URI;
        }
        else {
            return NOROOT_REL_URI;
        }
    }
}
