// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2011
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

//  CHANGE HISTORY
//  Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//          681568        01/18/11      mmulholl            create
//          PM21451       03/23/22      mmulholl            Add getDocumentRealPath support and constructor using vectors
package com.ibm.ws.webcontainer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector; //PM21451
import java.util.zip.ZipEntry;

import javax.servlet.ServletContext; 

import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.ws.webcontainer.webapp.WebAppConfiguration;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.ibm.wsspi.webcontainer.logging.LoggerFactory;

@SuppressWarnings("unchecked")
public class DocumentRootUtils {

	private static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.util");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.util.DocumentRootUtils";

    private boolean useContentLength = false;
    
    private File matchedFile;
    private ZipFileResource matchedZipFile;
    private URL matchedURL;
    
    private String edrRoot = null;
    private String pfedrRoot = null;
    
    private Vector edrSearchPath = null;        // PM21451
    private Vector pfedrSearchPath = null;      // PM21451
    
    
    private ServletContext ctxt;               // PM21451
    private String baseDir;
    
    private ExtendedDocumentRootUtils edr=null;
    private ExtendedDocumentRootUtils pfedr=null;
    private MetaInfResourcesFileUtils metaInfRes=null;
    
    private boolean searchpfEDR = false;
    private boolean searchEDR = false;
    private boolean searchMetaInfRes = true;
    
    boolean matchFromEDR = false;
    boolean matchFromMetaInfRes = false;
    boolean matchIsADirectory = false;
    
    private WebAppConfiguration appConfig;
    private String attributeType;
    public static final String STATIC_FILE = "staticFile";
    public static final String JSP = "jsp";
    private static final String PFEDR = "preFragmentExtendedDocumentRoot";
    public static final String EDR = "extendedDocumentRoot";
   

	public DocumentRootUtils(ServletContext context, String extendedDocumentRoot, String preFragmentExtendedDocumentRoot) {
		
		pfedrRoot = preFragmentExtendedDocumentRoot;
		edrRoot = extendedDocumentRoot;
		ctxt = context;
		baseDir=null;
		if (pfedrRoot!=null) {
			searchpfEDR = true;
		}
		
		if (edrRoot!=null) {
			searchEDR = true;
		}
		if(WCCustomProperties.SKIP_META_INF_RESOURCES_PROCESSING) {
		    searchMetaInfRes = false;
		}   
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"Document Root Utils created with context.","pfEDR = " + pfedrRoot + ", edr = " + edrRoot);
        }

    }
	
	// PM21451 Add new constructor
	public DocumentRootUtils(ServletContext context, Vector extendedDocumentRootSearchPath, Vector preFragmentExtendedDocumentRootSearchPath) {
		
		pfedrSearchPath = preFragmentExtendedDocumentRootSearchPath;
		edrSearchPath = extendedDocumentRootSearchPath;
		ctxt = context;
		baseDir=null;
		if (pfedrSearchPath!=null && !pfedrSearchPath.isEmpty()) {
			searchpfEDR = true;
		}
	    if (edrSearchPath!=null && !edrSearchPath.isEmpty()) {
			searchEDR = true;
		}
		init();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"Document Root Utils created with context.","pfEDR = " + pfedrRoot + ", edr = " + edrRoot);
        }

    }
	
	public DocumentRootUtils(ServletContext context, WebAppConfiguration config, String type) {
		appConfig = config;
		ctxt = context;
		baseDir=null;
		attributeType = type;
		searchpfEDR = true;
		searchEDR = true;
		ctxt = context;
		init();
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"Document Root Utils created with context and config.","type = " + attributeType);
        }
	}
       
    public DocumentRootUtils(String baseDirectory, String extendedDocumentRoot, String preFragmentExtendedDocumentRoot) {
		pfedrRoot = preFragmentExtendedDocumentRoot;
		edrRoot = extendedDocumentRoot;
		ctxt = null;
		baseDir = baseDirectory;
		if (pfedrRoot!=null) {
			searchpfEDR = true;
		}
		
		if (edrRoot!=null) {
			searchEDR = true;
		}
		
		init();
		
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"Document Root Utils created with baseDir : " + baseDir+".","pfEDR = " + pfedrRoot + ", edr = " + edrRoot);
        }
    }
    
    private void init() {
    	
    	getedr();
    	
    	getpfedr();
    	
		if(WCCustomProperties.SKIP_META_INF_RESOURCES_PROCESSING) {
		    searchMetaInfRes = false;
		}   

    }
	
    public boolean hasDocRoot() {
    	return (this.getedr() || this.getpfedr());    	
    }
    
    public boolean searchPathExists() {
    	boolean result=false;
        if (getedr())
        	result = edr.searchPathExists();
        if (!result && getpfedr()) {
        	result = pfedr.searchPathExists();
        }     
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"searchPathExists", " ", result);
        }
        return result;
    }
    
    public void handleDocumentRoots(String filename) throws FileNotFoundException, IOException {
    	handleDocumentRoots(filename,searchpfEDR,searchMetaInfRes,searchEDR);
    }
    
    public void handleDocumentRoots(String filename, Map<String, URL> metaInfCache) throws FileNotFoundException, IOException {
    	handleDocumentRoots(filename,searchpfEDR,searchMetaInfRes,searchEDR,metaInfCache);
    }
    
    public void handleDocumentRoots(String filename,boolean searchpfEDR, boolean searchMetaInf, boolean searchEDR) throws FileNotFoundException, IOException {
        handleDocumentRoots(filename,searchpfEDR,searchMetaInf,searchEDR,null);
    }    
    
    private void handleDocumentRoots(String filename,boolean searchpfEDR, boolean searchMetaInf, boolean searchEDR, Map<String, URL> metaInfCache) throws FileNotFoundException, IOException {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.entering(CLASS_NAME,"handleDocumentRoots","search: pfedr = " + searchpfEDR + ", MetaInf = " + searchMetaInf +", edr = " + searchEDR + ", filename --> " + filename );
        
        boolean foundMatch = false;
        
        matchedFile=null;
        matchedZipFile=null;
        matchedURL=null;
        matchFromEDR=false;
        matchFromMetaInfRes = false;
        matchIsADirectory = false;
        
        boolean trailingSlashRemoved = false;
    	while (filename.endsWith("/") || filename.endsWith("\\")) {
    		filename = filename.substring(0,filename.length()-1);
    		trailingSlashRemoved = true;
    	}
        
        if (searchpfEDR && getpfedr()) {
        	foundMatch = this.checkEDR(pfedr, filename);
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                logger.logp(Level.FINE,CLASS_NAME,"handleDocumentRoots", "match " + (foundMatch ? " found " : "not found") + " in preFragmentExtendedDocumentRoot : " + foundMatch);
        }
        
        if (!foundMatch && searchMetaInf && getMetaInfRes()) {
        	
        	if (metaInfCache!=null) {
    			synchronized(metaInfCache){
    				if (metaInfCache.containsKey(filename)){
    					matchedURL = metaInfCache.get(filename);
		        		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
	    	                logger.logp(Level.FINE, CLASS_NAME, "handleDocumentRoots", 
	    	                		"got cached META-INF name->[{0}], URL->[{1}]", 
	    	                		new Object [] {filename,matchedURL});
	    	            }
		        		// a metaInfcache was specified so we are only interested in finding the URL
		        		foundMatch=true;
    				}
    			}
        	}
        	if (!foundMatch) {
                metaInfRes.findInMetaInfResource(filename);
                matchedFile = metaInfRes.getMatchedStaticFile();
                matchedZipFile = metaInfRes.getMatchedZipFile();
                foundMatch = (matchedFile!=null || matchedZipFile!=null);
                if (foundMatch) {
            	    matchFromMetaInfRes = true;
            	    if (metaInfCache!=null) {
            	    	matchedURL = getURL();
            	    	if (matchedURL!=null) {
            			    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && logger.isLoggable(Level.FINE)) {
        	                    logger.logp(Level.FINE, CLASS_NAME, "handleDocumentRoots", 
        	                 		    "adding to META-INF cache name->[{0}], URL->[{1}]", 
        	                		    new Object [] {filename,matchedURL});
        	                }
            			    synchronized(metaInfCache){
            				    metaInfCache.put(filename, matchedURL);
            			    }    
            			}
            	    }
                } 
        	}    
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                logger.logp(Level.FINE,CLASS_NAME,"handleDocumentRoots", "match " + (foundMatch ? " found " : "not found") + " in META-INF/resources : " + foundMatch);
        }    
        
        if (!foundMatch && searchEDR) {           
    		if (getedr()) {
                foundMatch = this.checkEDR(edr, filename);
                if (foundMatch)
            	    matchFromEDR = true;
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                    logger.logp(Level.FINE,CLASS_NAME,"handleDocumentRoots", "match " + (foundMatch ? " found " : "not found") + " in ExtendedDocumentRoot : " + foundMatch);
            }        
        }
         
        // If a match is found check if file is a directory.
        // If it is not a directory and a trailing slash was removed then invalidate the match;
        if (foundMatch) {
        	if (matchedZipFile!=null) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                    logger.logp(Level.FINE,CLASS_NAME,"handleDocumentRoots", "found in zip : " + matchedZipFile.getZipFile()+ ", entry : " + matchedZipFile.getZipEntry().getName());
                }
        		ZipEntry matchedZipEntry = matchedZipFile.getZipFile().getEntry(matchedZipFile.getZipEntry().getName()+"/");
                if (matchedZipEntry!=null) {
                    matchIsADirectory = true;
                } 
        	} else if (matchedFile!=null) {
        		matchIsADirectory = matchedFile.isDirectory();
        	}
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                logger.logp(Level.FINE,CLASS_NAME,"handleDocumentRoots", "match " + (matchIsADirectory ? " is " : "is not") + " a directory");
            }
    		if (!matchIsADirectory && trailingSlashRemoved) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                    logger.logp(Level.FINE,CLASS_NAME,"handleDocumentRoots", "match found was for a directory but filename had a trailing slash");
                }    			
    			foundMatch = false;
    		}	
        }
        
        
        if (!foundMatch) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                logger.exiting(CLASS_NAME,"handleDocumentRoots", "file not found");
            matchedFile=null;
            matchedZipFile=null;
            matchFromEDR=false;
            matchFromMetaInfRes = false;
            matchIsADirectory = false;
            throw new FileNotFoundException(filename);
        }
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.exiting(CLASS_NAME,"handleExtendedRoots", "match found. Matched URL = " + (matchedURL == null ? "null." : matchedURL.toString()));
        
        return;

    }
    
    public InputStream getInputStream() { /*throws FileNotFoundException, IOException */ 
          if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
        	  logger.logp(Level.FINE, CLASS_NAME,"getInputStream", "getInputStream for ExtendedDocumentRoot this -->" + this);
          
          InputStream result = null;
          try {
  			if (matchedZipFile!=null)
				result = matchedZipFile.getIS();
			else if (matchedFile!=null)
				result = new FileInputStream(matchedFile);
                
           }
           catch (Exception e) {
               if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
             	  logger.logp(Level.FINE, CLASS_NAME,"getInputStream", "exception : " + e);
           }
           return result;
    }

    public boolean isDirectory() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) logger.logp(Level.FINE, CLASS_NAME,"isMatchADirectory", "result = " + matchIsADirectory);
    	return matchIsADirectory;
    }
    
    public File getMatchedFile() {
          if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) logger.logp(Level.FINE, CLASS_NAME,"getMatchedFile", "file --> [" +  matchedFile +"]");
          return  matchedFile;
    }

    public long getLastModifiedMatchedFile() {
          if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) logger.logp(Level.FINE, CLASS_NAME,"getLastModifiedMatchedFile", "file --> [" + ((matchedFile == null) ?  0 : matchedFile.lastModified())+"]");
	  return(( matchedFile ==null)?0: matchedFile.lastModified());
    }
    
    public String getFilePath() {
    	String path=null;
    	
    	try {
    	    if (matchedFile!=null) {
    		    path  = matchedFile.getCanonicalPath();
    	    } else if (matchedZipFile!=null) {
                path = matchedZipFile.getMatch().getCanonicalPath();    			
    	    }
    	} catch (IOException ioe) {
			Object[] args = { ioe };
            logger.logp(Level.SEVERE, CLASS_NAME,"getFilePath", "Engine.Exception.[{0}]", args);
    	}
    	return path;
    }

    public boolean useContentLength() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME,"useContentLength", "length --> [" + useContentLength + "]");
        return useContentLength;
    }
    
    public ZipFileResource getMatchedZipFileResource() {
    	
        if (matchedZipFile!=null) {
        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
            	logger.logp(Level.FINE, CLASS_NAME,"getMatchedZipFile", "is zip file");
             return matchedZipFile;
        } else if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
        	logger.logp(Level.FINE, CLASS_NAME,"getMatchedZipFile", "not zip file");

        return null;
    }    
    
    private URL getURL() {
		try {
			if (matchedURL == null) {
			    if (matchedZipFile!=null)
				   matchedURL = matchedZipFile.getURL();
			    else if (matchedFile!=null)
				   matchedURL = matchedFile.toURI().toURL();
			}    
		} catch (MalformedURLException e) {
			Object[] args = { e };
            logger.logp(Level.SEVERE, CLASS_NAME,"getURL", "Engine.Exception.[{0}]", args);
		}
		return matchedURL;

    }

    public URL getURL(String filename,Map<String, URL> metaInfCache) throws MalformedURLException {
    	
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.entering(CLASS_NAME,"getURL","filename = " + filename + " a metaInfCache was " + (metaInfCache == null ? "not provided." : "provided."));
    	
    	URL returnURL = null;
    	try {
    		// handleDocumentRoots only r
    	    this.handleDocumentRoots(filename, metaInfCache);
    	    returnURL = getURL();
   	    } catch (FileNotFoundException fnf) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME,"getURL", "file not found.");
    		returnURL=null;
    	} catch (IOException ioe) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME,"getURL", "IOException");
    		returnURL=null;
		}	
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.exiting(CLASS_NAME,"getURL"," URL = " + (returnURL == null ? "null." : returnURL.toString()));

		return returnURL;    
    }
    
       
    public Set<String> getResourcePaths(String filename, boolean searchMetaInf) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.entering(CLASS_NAME,"getResourcePaths", "filename = " + filename);
        
    	Set paths = new HashSet<String> ();
    	
    	if (getpfedr()) {
    		paths.addAll(pfedr.getResourcePaths(filename));
    	}
    	
    	if (searchMetaInf && getMetaInfRes()) {
            paths.addAll(metaInfRes.getResourcePaths(filename));
    	}
    	
    	if (getedr()) {
    		paths.addAll(edr.getResourcePaths(filename));
    	}
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.exiting(CLASS_NAME,"getResourcePaths");
    	
    	return paths;
    }
    
    //PM21451 Add getDocumentRealPath method
    // return the  path for the uri if it can be found in a document root. 
    public String getDocumentRealPath(String uri,Map<String, URL> metaInfCache)
    {    	
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) { 
			logger.entering(CLASS_NAME,"getDocumentRealPath", "uri: " + uri);
	    }  
	    
    	String path = null;   	
		                 
        try {      
        	
        	// look for the file but do not seach the EDR unless
        	// the custom property is set
        	boolean savedSearchEDR = searchEDR;
        	
        	if (!WCCustomProperties.CHECK_EDR_IN_GET_REALPATH) {
            	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) { 
                    logger.logp(Level.FINE,CLASS_NAME,"getDocumentRealPath", "Skip and EDR because the required custom property is not set");
                }
        		searchEDR = false;
        	}
        	
            this.handleDocumentRoots(uri,metaInfCache);
                  
            searchEDR = savedSearchEDR;
            
            //return file name - but only bother if it is not a zip file. 
            File foundFile = getMatchedFile();
            	
            if(foundFile != null){            	
                path = foundFile.toString();                  		
                if(uri.endsWith("\\") ||  uri.endsWith("/"))                   			              			
                	path = path + File.separatorChar; 		
            }  	                   	                   	                            
        }
        catch (Exception fne_io) {
        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) { 
                logger.logp(Level.FINE,CLASS_NAME,"getDocumentRealPath", "Unable to find file for uri --> " + uri);
            }
        }
        
	    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)) { 
			logger.exiting(CLASS_NAME,"getDocumentRealPath", "path: " + path);
	    }  
	    return path;
		
    }
        
    public boolean isMatchedFromEDR() {
    	return matchFromEDR;
    }
    
    public boolean isMatchedFromMetaInfRes() {
    	return matchFromMetaInfRes;
    }
    
    // PM21451 Add getpfedrSearchPath method
    public Vector getpfedrSearchPath() {
    	if (getpfedr())
		   return pfedr.getSearchPath();
    	else return null;
    }
    
    // PM21451 Add getedrSearchPath method
    public Vector getedrSearchPath() {
    	if (getedr())
		   return edr.getSearchPath();
    	else return null;
    }
     
    private String getRoot(String attributeName) {
        String root;
    	if (attributeType.equals(STATIC_FILE)) {
    			root = (String)appConfig.getFileServingAttributes().get(attributeName);
    	} else {
    		root = (String)appConfig.getJspAttributes().get(attributeName);
    	}    
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
			logger.logp(Level.FINE, CLASS_NAME,"getRoot", "Attribute name = " + attributeName + ", type = " + attributeType + ", value = " + root);
    	return root;
    }
    
    private boolean getMetaInfRes() {
    	
    	// Always create a new MetaInfResourcesFileUtils so that it picks up any new jars added
    	// to the WEB-INF/lib directory
    	if (searchMetaInfRes) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
    			logger.logp(Level.FINE, CLASS_NAME,"handleDocumentRoots", "create MetaInfResourcesFileUtils");
    		if (ctxt==null) {
    			metaInfRes = new MetaInfResourcesFileUtils(baseDir);
    		} else {
    			metaInfRes = new MetaInfResourcesFileUtils(ctxt);
    		} 
    		if (metaInfRes==null) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        			logger.logp(Level.FINE, CLASS_NAME,"handleDocumentRoots", "failed to create MetaInfResourcesFileUtils");
    			searchMetaInfRes = false;
    		}
     	}
    	return metaInfRes!=null;
    }

    
    // PM21451 change to only get an EDR once and to use a serach vector if one is available
    private boolean getpfedr() {

    	// The doc root location will not change in a running application
        // so only need to get the edr once.
    	if (searchpfEDR && pfedr==null) {
    			
    		if (pfedrSearchPath!=null) {
    			if (!pfedrSearchPath.isEmpty()) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        		        logger.logp(Level.FINE, CLASS_NAME,"getpfedr", "create preFragmentExtendedDocumentRoot using searchVector");     				
            	    pfedr = new ExtendedDocumentRootUtils(pfedrSearchPath);
    			}    
            } else {    			
    		    if (pfedrRoot == null) {
    			    pfedrRoot = this.getRoot(PFEDR);
    		    }
    		    if (pfedrRoot!=null) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        		        logger.logp(Level.FINE, CLASS_NAME,"getpfedr", "create preFragmentExtendedDocumentRoot", pfedrRoot);
                    if (ctxt==null) {
        			    pfedr = new ExtendedDocumentRootUtils(baseDir,pfedrRoot);
        		    } else {
        			    pfedr = new ExtendedDocumentRootUtils(ctxt,pfedrRoot);
        		    }
        		    if (!pfedr.searchPathExists())
        			    pfedr=null;
    		    }	    
    		}  
    		if (pfedr==null) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        		    logger.logp(Level.FINE, CLASS_NAME,"getpfedr", "failed to create preFragmentExtendedDocumentRoot", pfedrRoot);
        		searchpfEDR = false;
    		}	
    	}
        return (searchpfEDR && pfedr!=null);	
    }
    
    // PM21451 change to only get an EDR once and to use a serach vector if one is available
    private boolean getedr() {

    	// The doc root location will not change in a running application
        // so only need to get the edr once.
    	if (searchEDR && edr==null) {
    		
    		if (edrSearchPath!=null) {
    			if (!edrSearchPath.isEmpty()) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        		        logger.logp(Level.FINE, CLASS_NAME,"getpfedr", "create ExtendedDocumentRoot using searchVector");     				
            	    edr = new ExtendedDocumentRootUtils(edrSearchPath);
    			}    
            } else {    			
    		    if (edrRoot == null) {
    			    edrRoot = this.getRoot(EDR);
    		    }
    		    if (edrRoot!=null) {
        		    logger.logp(Level.FINE, CLASS_NAME,"getedr", "create ExtendedDocumentRoot", edrRoot);
                    if (ctxt==null) {
        			    edr = new ExtendedDocumentRootUtils(baseDir,edrRoot);
        		    } else {
        			    edr = new ExtendedDocumentRootUtils(ctxt,edrRoot);
        		    }
        		    if (!edr.searchPathExists())
        			    edr=null;
    		    }    
    		}       			
    		if (edr==null) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
        		    logger.logp(Level.FINE, CLASS_NAME,"getedr", "failed to create ExtendedDocumentRoot", edrRoot);
        		searchEDR = false;
    		}	
    	}
        return (searchEDR && edr!=null);	
    }
   
    private boolean checkEDR(ExtendedDocumentRootUtils edr,String filename) {
    	
    	boolean matched = false;
    	try {
    	    edr.handleExtendedDocumentRoots(filename);
    	    ExtDocRootFile edrFile = edr.getExtDocRootFile();
    	    if (edrFile  instanceof  ZipFileResource) {
    	        matchedZipFile = (ZipFileResource)edrFile;
    	    } else {
    	    	matchedFile = edr.getMatchedFile();
    	    }	
    	    matched = (matchedZipFile!=null || matchedFile != null);
    	    if (matched) {
    	    	useContentLength = edr.useContentLength();
    	    }	
    	} catch (Exception e) {
    		matched = false;
    	}
    	return matched;
    }
    
}
