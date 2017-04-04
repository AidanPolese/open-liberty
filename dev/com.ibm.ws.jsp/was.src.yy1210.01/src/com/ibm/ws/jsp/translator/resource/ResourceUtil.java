//IBM Confidential OCO Source Material
//	5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//
// PK75069	11/06/08    mconcini	JSP getting compiled on every access
package com.ibm.ws.jsp.translator.resource;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.util.FileSystem;
import com.ibm.ws.util.WSUtil;

public class ResourceUtil {
	//	begin 213703: add logging for isoutdated checks		
	private static Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.translator.resource.ResourceUtil";
	
	// PK75069  
    private static final boolean isOS400= System.getProperty("os.name").toLowerCase().equals ("os/400") ;
	private static final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	
	static{
		logger = Logger.getLogger("com.ibm.ws.jsp");
	}
	//	end 213703: add logging for isoutdated checks

    public static void sync(File sourceFile, File generatedSourceFile, File classFile, String className, boolean keepgenerated, boolean keepGeneratedclassfiles) {
    	if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
			logger.logp(Level.FINEST, CLASS_NAME, "sync", "Synching for sourceFile [" + sourceFile +"] ts [" + sourceFile.lastModified()+ "]");
    	}
        if (keepGeneratedclassfiles == false) {
            boolean delete = classFile.delete();
			if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
				logger.logp(Level.FINEST, CLASS_NAME, "sync", (delete?"Deleted":"Unable to delete") + " classFile [" + classFile +"]");
			}
            File[] icList = generatedSourceFile.getParentFile().listFiles(new InnerclassFilenameFilter(className)); //205761
            for (int i=0;i<icList.length;i++) {
                if (icList[i].isFile()) {
                    boolean innerDelete = icList[i].delete();
					if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
						logger.logp(Level.FINEST, CLASS_NAME, "sync", (innerDelete?"Deleted":"Unable to delete")+" inner classFile [" + icList[i] +"]");
					}
                }
            }           
        }
        else {
            boolean rc = classFile.setLastModified(sourceFile.lastModified());
			if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
				logger.logp(Level.FINEST, CLASS_NAME, "sync", (rc?"Updated":"Unable to update") +" lastModified timestamp for classFile [" + classFile +"] [" + classFile.lastModified()+ "]");
			}
        }
        
        if (generatedSourceFile.exists()) {
            if (keepgenerated == false) {
                boolean delete = generatedSourceFile.delete();
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
					logger.logp(Level.FINEST, CLASS_NAME, "sync", (delete?"Deleted":"Unable to delete") + " generatedSourceFile [" + generatedSourceFile +"]");
				}
            }
            else {
				boolean rc = generatedSourceFile.setLastModified(sourceFile.lastModified());
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
					logger.logp(Level.FINEST, CLASS_NAME, "sync", (rc?"Updated":"Unable to update") +" lastModified timestamp for generatedSourceFile [" + generatedSourceFile +"] [" + generatedSourceFile.lastModified()+ "]");
				}
            }
        }
    }

    public static boolean isOutdated(File sourceFile, File generatedSourceFile, File classFile, File webinfClassFile) {
        boolean outdated = true;
        
        if (sourceFile != null) {
            if (sourceFile.exists() == false){
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
					logger.logp(Level.FINER, CLASS_NAME, "isOutdated", "sourceFile [" + sourceFile + "] does not exist");
				}
                return true;    // source file does not exist.
            }
            try {	
            	// PK75069 - call private jspCaseCheck() instead of FileSytem.uriCaseCheck()
                if (classFile.exists() && jspCaseCheck(classFile, classFile.getAbsolutePath())) {
                    if (sourceFile.lastModified() == classFile.lastModified()) {
                        outdated = false;
                    }
                    else{
                		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
                			logger.logp(Level.FINEST, CLASS_NAME, "isOutdated", "sourceFile [" + sourceFile + "]");
                			logger.logp(Level.FINEST, CLASS_NAME, "isOutdated", "classFile [" + classFile + "]");
                			logger.logp(Level.FINER, CLASS_NAME, "isOutdated", "sourceFile ts [" + sourceFile.lastModified() + "] differs from tempDirClassFile ts [" +classFile.lastModified() +"]. Recompile JSP.");
                		}
                    }
                // PK75069 - call private jspCaseCheck() instead of FileSytem.uriCaseCheck()
                } else if (webinfClassFile.exists() && jspCaseCheck(webinfClassFile, webinfClassFile.getAbsolutePath())) {
                    if (sourceFile.lastModified() == webinfClassFile.lastModified()) {
                        outdated = false;
                    }
                	else{
                		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
                			logger.logp(Level.FINEST, CLASS_NAME, "isOutdated", "sourceFile [" + sourceFile + "]");
                			logger.logp(Level.FINEST, CLASS_NAME, "isOutdated", "webinfClassFile [" + webinfClassFile + "]");
                			logger.logp(Level.FINEST, CLASS_NAME, "isOutdated", "sourceFile ts [" + sourceFile.lastModified() + "] differs from webinfClassFile ts [" + webinfClassFile.lastModified() +"]. Recompile JSP.");
                		}
                	}
                }
            } catch (IOException e) {
                //The IOException came from the FileSystem.uriCaseCheck - just say that it is outdated
                return true;
            }
            if (outdated && generatedSourceFile.getParentFile().exists() == false){
                boolean rc = generatedSourceFile.getParentFile().mkdirs();
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
					logger.logp(Level.FINEST, CLASS_NAME, "isOutdated", (rc?"Created":"Unable to create") +" directory for generated source file ["+generatedSourceFile.getParentFile() +"]");
				}
            }
        }
        else {
            outdated = false;
        }

        return (outdated);
    }


    public static boolean isTagFileOutdated(File sourceFile, File generatedSourceFile, File classFile, File webinfClassFile) {
        boolean outdated = true;
        if (sourceFile.exists() == false){
			if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
				logger.logp(Level.FINER, CLASS_NAME, "isTagFileOutdated", "sourceFile [" + sourceFile + "] does not exist");
			}
            return true;    // tag file does not exist.
        }
        
        // begin 213703: change outdated checks to mirror algorithm used in isOutdated.
        if (generatedSourceFile.exists()){
        	if(sourceFile.lastModified() == generatedSourceFile.lastModified()) {
	            outdated = false;
        	}
			else{
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
					logger.logp(Level.FINEST, CLASS_NAME, "isTagFileOutdated", "sourceFile [" + sourceFile + "]");
					logger.logp(Level.FINEST, CLASS_NAME, "isTagFileOutdated", "generatedSourceFile [" + generatedSourceFile + "]");
					logger.logp(Level.FINER, CLASS_NAME, "isTagfileOutdated", "sourceFile ts [" + sourceFile.lastModified() + "] differs from generatedSourceFile ts [" +generatedSourceFile.lastModified() +"]. Recompile tag file.");
				}
			}
        }
        else if (classFile.exists()){
        	if(sourceFile.lastModified() == classFile.lastModified()) {
            	outdated = false;
        	}
			else{
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
					logger.logp(Level.FINEST, CLASS_NAME, "isTagFileOutdated", "sourceFile [" + sourceFile + "]");
					logger.logp(Level.FINEST, CLASS_NAME, "isTagFileOutdated", "classFile [" + classFile + "]");
					logger.logp(Level.FINER, CLASS_NAME, "isTagfileOutdated", "sourceFile ts [" + sourceFile.lastModified() + "] differs from tempDirClassFile ts [" + classFile.lastModified() +"]. Recompile tag file.");
				}
			}
        }
        else if (webinfClassFile.exists()){ 
        	if(sourceFile.lastModified() == webinfClassFile.lastModified()) {
            	outdated = false;
        	}
			else{
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
					logger.logp(Level.FINEST, CLASS_NAME, "isTagFileOutdated", "sourceFile [" + sourceFile + "]");
					logger.logp(Level.FINEST, CLASS_NAME, "isTagFileOutdated", "webinfClassFile [" + webinfClassFile + "]");
					logger.logp(Level.FINER, CLASS_NAME, "isTagfileOutdated", "sourceFile ts [" + sourceFile.lastModified() + "] differs from webinfClassFile ts [" + webinfClassFile.lastModified() +"]. Recompile tag file.");
				}
			}
        }
		//end 213703: change outdated checks to mirror algorithm used in isOutdated.

        if(outdated && generatedSourceFile.getParentFile().exists() == false){
            boolean rc = generatedSourceFile.getParentFile().mkdirs();
			if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
				logger.logp(Level.FINEST, CLASS_NAME, "isTagFileOutdated", (rc?"Created":"Unable to create") +" directory for generated source file ["+generatedSourceFile.getParentFile() +"]");
			}

        }

        return (outdated);
    }

    public static void syncGeneratedSource(File sourceFile, File generatedSourceFile) {
        if (generatedSourceFile.exists()){
			boolean rc = generatedSourceFile.setLastModified(sourceFile.lastModified());
			if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
				logger.logp(Level.FINEST, CLASS_NAME, "syncGeneratedSource", (rc?"Updated":"Unable to update") +" lastModified timestamp for generatedSourceFile [" + generatedSourceFile +"] [" + generatedSourceFile.lastModified()+ "]");
			}
        }
            
    }

    public static void syncTagFile(File sourceFile, File generatedSourceFile, File classFile, boolean keepgenerated, boolean keepGeneratedclassfiles) {
        if (sourceFile.lastModified() == generatedSourceFile.lastModified()) {
            if (keepGeneratedclassfiles == false) {
                boolean delete = classFile.delete();
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
					logger.logp(Level.FINEST, CLASS_NAME, "syncTagFile", (delete?"Deleted":"Unable to delete") + " classFile [" + classFile +"]");
				}
            }
            else {
                boolean rc = classFile.setLastModified(sourceFile.lastModified());
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
					logger.logp(Level.FINEST, CLASS_NAME, "syncTagFile", (rc?"Updated":"Unable to update") +" lastModified timestamp for classFile [" + classFile +"] [" + classFile.lastModified()+ "]");
				}
            }
            if (generatedSourceFile.exists()) {
                if (keepgenerated == false){
					boolean delete = generatedSourceFile.delete();
					if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
						logger.logp(Level.FINEST, CLASS_NAME, "syncTagFile", (delete?"Deleted":"Unable to delete") + " generatedSourceFile [" + generatedSourceFile +"]");
					}
                }
                else{
					boolean rc = generatedSourceFile.setLastModified(sourceFile.lastModified());
					if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
						logger.logp(Level.FINEST, CLASS_NAME, "syncTagFile", (rc?"Updated":"Unable to update") +" lastModified timestamp for generatedSourceFile [" + generatedSourceFile +"] [" + generatedSourceFile.lastModified()+ "]");
					}
                }
            }
        }
    }

    // start PK75069
	private static boolean jspCaseCheck (File file, String matchString) throws java.io.IOException {  
		// private version of FileSystem.uriCaseCheck() for windows only

		if(isOS400){	//if OS/400 and not windows, still call the old method
			return FileSystem.uriCaseCheck(file, matchString);
		}		
		if (isWindows) {	
			//if Windows, check for class file only to avoid problems with windows shortnames
			//we already know the path is valid since classFile.exists() must return true to get here 
			matchString = WSUtil.resolveURI(matchString);
			
			matchString = matchString.replace ('/', File.separatorChar);
			int lastSeparator = matchString.lastIndexOf(File.separatorChar);
			matchString = matchString.substring(++lastSeparator);
			
			String canPath = file.getCanonicalPath();
			lastSeparator = canPath.lastIndexOf(File.separatorChar);
			canPath = canPath.substring(++lastSeparator);
			
			if(!matchString.equals(canPath)){
				return false;
			}
		}
		return true;
	} 
    // end PK75069
    
	//	defect 205761 begin    
	private static class InnerclassFilenameFilter implements FilenameFilter {
		String filename=null;
		public InnerclassFilenameFilter(String filename){
			this.filename=filename;
		}
		public boolean accept(File dir, String name) {
			int dollarIndex = name.indexOf("$");
			if (dollarIndex > -1) {
				String nameStart = name.substring(0, dollarIndex);
				if (this.filename.equals(nameStart)) {
					return true;
				}
			}
			return false;
		}
	}
	//	defect 205761 end    
}
