// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

//  CHANGE HISTORY
//  Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//          PK27624        08/04/06     cjhoward            APPSERVER HANG GETTING TOO MANY OPEN FILES ERROR
//	        PM21451	       10/05/10     mmulholl            Add new constructor and methods to get and set the search path
//          PM28343        01/11/11      anupag              Addendun to PM17845

package com.ibm.ws.webcontainer.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.net.MalformedURLException;
import java.net.URL;


import javax.servlet.ServletContext;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.ibm.wsspi.webcontainer.WCCustomProperties;
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;

import com.ibm.ws.util.WSUtil;

@SuppressWarnings("unchecked")
public class ExtendedDocumentRootUtils {

	private static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.util");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.util.ExtendedDocumentRootUtils";

    private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    private Vector searchPath = new Vector();
    private boolean useContentLength = false;

    private ExtDocRootFile extDocRootFile;
    
    public ExtDocRootFile getExtDocRootFile() {
		return extDocRootFile;
	}

	public ExtendedDocumentRootUtils(ServletContext ctxt, String extendedDocumentRoot) {
        if (extendedDocumentRoot != null) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME,"ExtendedDocumentRootUtils", "extendedDocumentRoot --> " + extendedDocumentRoot);
            }
            createSearchPath(ctxt, extendedDocumentRoot);
        }
    }

    public ExtendedDocumentRootUtils(String baseDir, String extendedDocumentRoot) {
        if (extendedDocumentRoot != null) {
            if (baseDir != null) {
                baseDir = baseDir.replace('\\', '/');
                if (baseDir.endsWith("/") == false) {
                    baseDir = baseDir + "/";
                }
            }
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
                logger.logp(Level.FINE, CLASS_NAME,"ExtendedDocumentRootUtils", "baseDir --> ", baseDir);
                logger.logp(Level.FINE, CLASS_NAME,"ExtendedDocumentRootUtils", "extendedDocumentRoot --> " + extendedDocumentRoot);
            }
            createSearchPath(baseDir, extendedDocumentRoot);
        }

    }
	
    //PM21451 Start   
    public ExtendedDocumentRootUtils(Vector searchPath) {
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"ExtendedDocumentRootUtils", " EDR searchPath Vector size -->" + searchPath.size());
        }  
        setSearchPath(searchPath);
    }
    
    public Vector getSearchPath() {
		return searchPath;
    }

    public void setSearchPath(Vector searchPath) {		
		this.searchPath = searchPath;
    }
    //PM21451 End

    
    public boolean searchPathExists() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            String result = searchPath.isEmpty() == false ? "true" : "false";
            logger.logp(Level.FINE, CLASS_NAME,"searchPathExists", " ", result);
        }
        return (!searchPath.isEmpty());
    }

    private void createSearchPath(ServletContext ctx, String extendedDocumentRoot) {
        String baseDir = ctx.getRealPath("/../");
        createSearchPath(baseDir, extendedDocumentRoot);
    }

    private void createSearchPath(String baseDir, String extendedDocumentRoot) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {
            logger.logp(Level.FINE, CLASS_NAME,"createSearchPath", "baseDir --> " + baseDir);
            logger.logp(Level.FINE, CLASS_NAME,"createSearchPath", "extendedDocumentRoot --> " + extendedDocumentRoot);
        }

        StringTokenizer st = new StringTokenizer(extendedDocumentRoot, ",");

        while (st.hasMoreTokens()) {
            try {

                String currentSearchLocation = (st.nextToken().trim());
                if (currentSearchLocation != null) {

                    if ((isWindows && currentSearchLocation.indexOf(":") == 1) || (currentSearchLocation.startsWith("/"))) {
                        File f = new File(currentSearchLocation);
                        searchPath.addElement(f.toString());
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                            logger.logp(Level.FINE, CLASS_NAME,"createSearchPath", "add to searchPath --> " + f.toString());

                    }
                    else {
                        File realPath = new File(baseDir + currentSearchLocation);
                        String canonicalPath = realPath.getCanonicalPath();
                        searchPath.addElement(canonicalPath);
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                            logger.logp(Level.FINE, CLASS_NAME,"createSearchPath", "add to searchPath --> " + canonicalPath);

                    }
                }
            }
            catch (IOException io) {
                logger.logp(Level.SEVERE, CLASS_NAME,"createSearchPath", "exception.creating.search.path",io);
            }
        }
    }

    public void handleExtendedDocumentRoots(String filename) throws FileNotFoundException, IOException {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME,"handleExtendedDocumentRoots", "filename --> " + filename);
        boolean foundMatch = false;

        Enumeration altDocumentRoots = searchPath.elements();

        search : while (altDocumentRoots.hasMoreElements()) {
            String currDocumentRoot = altDocumentRoots.nextElement().toString();
            File currFile = new File(currDocumentRoot);

            if (currFile.isDirectory()) {
                File tmpFile = new File(currFile, filename);
                if (tmpFile.exists()) {
                    foundMatch = true;
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                        logger.logp(Level.FINE, CLASS_NAME,"handleExtendedDocumentRoots", "found match in directory --> " + tmpFile.toString());
                    handleCaseSensitivityCheck(tmpFile.toString(), filename);

                    this.extDocRootFile = new FileResource (tmpFile);
                    
	                useContentLength = true;
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                        logger.logp(Level.FINE, CLASS_NAME,"handleExtendedDocumentRoots", "useContentLength --> " + useContentLength);
                    break search;
                }

            }
            else if (currFile.exists()) {
                ZipFile zip = new ZipFile(currFile);
                ZipEntry zEntry = zip.getEntry(filename.substring(1).replace('\\', '/'));
                if (zEntry != null) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                        logger.logp(Level.FINE, CLASS_NAME,"handleExtendedDocumentRoots", "found match in zip or jar file --> " + currFile.toString());
                        foundMatch=true;
                        
                        
						String fullURL = "jar:" + currFile.toURI().toURL().toString()+"!"+filename.replace('\\', '/');
						URL url=null;
						try {
							url = new URL(fullURL);
						} catch (MalformedURLException e) {
				            logger.logp(Level.FINE, CLASS_NAME,"handleExtendedDocumentRoots", "MalformedURLException for URL : " + fullURL);
						}

                        this.extDocRootFile = new ZipFileResource (currFile, zEntry.getName(),url);


                        zip.close();
                        
	                break search;
                }

                else {
                     zip.close();
                }
            }
        }
        if (!foundMatch) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME,"handleExtendedDocumentRoots", "unable to locate resource --> " + filename);
            throw new FileNotFoundException(filename);
        }

    }
    
    public InputStream getInputStream() /*throws FileNotFoundException, IOException */ {
          if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) logger.logp(Level.FINE, CLASS_NAME,"getInputStream", "getInputStream for ExtendedDocumentRoot this -->" + this);
          try {
               return this.extDocRootFile.getIS();
          }

          catch (Exception e) {
               return null;
          }
    }

    public File getMatchedFile() {
          if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) logger.logp(Level.FINE, CLASS_NAME,"getMatchedFile", "file --> [" +  extDocRootFile.getMatch() +"]");
          return  extDocRootFile.getMatch();
    }

    public long getLastModifiedMatchedFile() {
          if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) logger.logp(Level.FINE, CLASS_NAME,"getLastModifiedMatchedFile", "file --> [" +  (( extDocRootFile.getMatch() ==null)?0: extDocRootFile.getMatch() .lastModified())+"]");
	  return(( extDocRootFile.getMatch() ==null)?0: extDocRootFile.getMatch() .lastModified());
    }

    public boolean useContentLength() {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.logp(Level.FINE, CLASS_NAME,"useContentLength", "length --> [" + useContentLength + "]");
        return useContentLength;
    }

    public ZipFile getMatchedZipFile() {
        if (this.extDocRootFile instanceof ZipFileResource) {
        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
            	logger.logp(Level.FINE, CLASS_NAME,"getMatchedZipFile", "is zip file");
             return ((ZipFileResource) this.extDocRootFile).getZipFile();
        } else if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
        	logger.logp(Level.FINE, CLASS_NAME,"getMatchedZipFile", "not zip file");

        return null;
    }
    
    public ZipEntry getMatchedEntry() {
        if (this.extDocRootFile instanceof ZipFileResource) {
        	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
            	logger.logp(Level.FINE, CLASS_NAME,"getMatchedEntry", "is zip file");
             return ((ZipFileResource) this.extDocRootFile).getZipEntry();
        } else if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
        	logger.logp(Level.FINE, CLASS_NAME,"getMatchedEntry", "not zip file");

        return null;
    }
    
    public Set<String> getResourcePaths(String filename) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.entering(CLASS_NAME,"getResourcePaths", "filename = " + filename);
        
    	Set paths = new HashSet<String> ();
    	
    	filename = WSUtil.resolveURI(filename.trim());
    	
        boolean isRootDirectory = filename.equals("/");
    	if (!isRootDirectory && filename.startsWith("/"))
            filename = filename.substring(1);
    	
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.logp(Level.FINE,CLASS_NAME,"getResourcePaths", "resolved fileName : " + filename);
   	
        Enumeration altDocumentRoots = searchPath.elements();

        search : while (altDocumentRoots.hasMoreElements()) {
            String currDocumentRoot = altDocumentRoots.nextElement().toString();
            File currFile = new File(currDocumentRoot);
            
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                logger.logp(Level.FINE,CLASS_NAME,"getResourcePaths", "check document root : " + currDocumentRoot);
            
            try {
            	
                if (currFile.isDirectory()) {
            
            	    // Don't search a directory with trailing file delimiter as it will return no hits
            	    // even if there is a directory of the searchName
            	    String searchName = filename;
            	    while (searchName.endsWith("/")) {
            		    searchName = searchName.substring(0,searchName.length()-1);
            	    }

            	
            	    File tmpFile = null;
            	    if (!isRootDirectory) {
            	        tmpFile  = new File(currFile, searchName);
                    } else {
                	    tmpFile  = currFile;
                    }	
                
            	    // if the search file is a directory get a list of its contents. 
                    if (tmpFile.isDirectory()) {
                    	
                    	if (!isRootDirectory) {
                            handleCaseSensitivityCheck(tmpFile.toString(), searchName);
                    	}    
                    	
                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                            logger.logp(Level.FINE, CLASS_NAME,"getResourcePaths", "found match in directory --> " + tmpFile.toString());
                         
                        // list all of the files and directories in the search directory
                        java.io.File[] fileList = tmpFile.listFiles();

                        if (fileList != null) {
                            for (int i = 0; i < fileList.length; i++) {
                                String resourcePath = fileList[i].getPath();
                                resourcePath = resourcePath.substring(currFile.toString().length());
                                resourcePath = resourcePath.replace('\\', '/');
                                
                                // if the resource is a directory append a trailing file delimiter
                                if (fileList[i].isDirectory()) {
                                    if (resourcePath.endsWith("/") == false) {
                                        resourcePath += "/";
                                    }
                                }
                                
                                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                                    logger.logp(Level.FINE, CLASS_NAME,"getResourcePaths", "add path --> " + resourcePath);

                                paths.add(resourcePath);
                            }
                        }
                } 
                	
            } else if (currFile.exists()) {
            	
            	// search file was not a directory so assume it is a zip file            	
                ZipFile zip = new ZipFile(currFile);
                ZipEntry zipEntry=null;
                String rootEntry = null;
                    
                // take off any leading file delimiter and add a trailing file delimiter if needed
                // the trailing slash makes sure we get a directory 
                if (!isRootDirectory) {
                    if (!filename.endsWith("/"))
                        filename += "/";
                        
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                        logger.logp(Level.FINE, CLASS_NAME,"getResourcePaths", "get Zip entry for  --> " + filename);
                       
                    zipEntry = zip.getEntry(filename);
                      
                    if (zipEntry!=null && zipEntry.isDirectory())
                        rootEntry = zipEntry.toString();
                        
                } else {
                	rootEntry = "";
                }
                                        	                    
                if (rootEntry!=null) {
                    	
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                        logger.logp(Level.FINE, CLASS_NAME,"getResourcePaths", "found match in zip file --> " + currFile.toString());
                        
    			    Enumeration zipEntries = zip.entries(); //get all of the entries in the jar
                        
    				while (zipEntries.hasMoreElements()){  //traverse all of the entries in the zip
    					ZipEntry currentZipEntry = ((ZipEntry)zipEntries.nextElement());
    					String currentEntry = currentZipEntry.toString();
    					// we only want files and directories directly under in the root (not sub-directories)
    					if (currentEntry.startsWith(rootEntry)) {
    							  						
    						String subEntry = currentEntry.substring(rootEntry.length()).replace('\\', '/');
    						
	                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
	                            logger.logp(Level.FINE, CLASS_NAME,"getResourcePaths", "check -->"  + subEntry);
     							
    						if (!subEntry.equals("")) {
    							    
    							int slashIndex = subEntry.indexOf("/");
    							   							
    						    if (slashIndex==-1 || slashIndex==subEntry.length()-1) {
    							    if (currentZipEntry.isDirectory() && !currentEntry.endsWith("/")) {
    								    currentEntry += "/";
    							    }
    							    if (!currentEntry.startsWith("/"))
    								    currentEntry = "/" + currentEntry;
    							    paths.add(currentEntry);
    		                        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
    		                            logger.logp(Level.FINE, CLASS_NAME,"getResourcePaths", "add path --> " + currentEntry);
    						    }
    						}
    					}    						
    				}
                }    

                zip.close();
            } else if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME,"getResourcePaths", "EDR not not found --> " + currFile.toString());	
            } catch (Exception e) {
            	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                    logger.logp(Level.FINE, CLASS_NAME,"getResourcePaths", "Exception searching : " + currDocumentRoot + " : " + e.getMessage());
            }                    
        }

        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
            logger.exiting(CLASS_NAME,"getResourcePaths");
    	
    	return paths;
    }


    private void handleCaseSensitivityCheck(String path, String strippedPathInfo) throws FileNotFoundException, IOException {
        // 94578, "Case Sensitive Security Matching bug":  On Windows and as400 only, filename of
        //         requested file must exactly match case or we will throw FNF exception.
        if (com.ibm.ws.util.FileSystem.isCaseInsensitive) {
            if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                logger.logp(Level.FINE, CLASS_NAME,"handleCaseSensitivityCheck", "file system is case insensitive");
            File caseFile = new File(path);
            if (!com.ibm.ws.util.FileSystem.uriCaseCheck(caseFile, strippedPathInfo)) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                    logger.logp(Level.FINE, CLASS_NAME,"handleCaseSensitivityCheck", "failed for --> [" + path + "]");
                throw new FileNotFoundException(path);
            }
        }
        // Addendum to PM17845 --> PM28343
        else if (WCCustomProperties.ALLOW_PARTIAL_URL_TO_EDR){
            File caseFile = new File(path);
            if (!com.ibm.wsspi.webcontainer.util.FileSystem.uriCaseCheck(caseFile, strippedPathInfo)) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE))
                            logger.logp(Level.FINE, CLASS_NAME,"handleCaseSensitivityCheck", "fail for --> [" + path + "]");
                    throw new FileNotFoundException(path);
                }
        } // Addendum to PM17845  --> PM28343

    }
    
}
