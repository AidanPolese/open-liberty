//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
/*
 * Change history:
 * defect 216949 "JSP Batch compiler doesn't support debug mode"  2004/07/16  Scott Johnson
 * Defect 227804  2004/08/31  Strange stepping behaviour against the enclosed JSP file
 * Defect 358784  2006/04/03 BT61: SRVE0068E No function mapped to the name fn:length
 * feature LIDB4147-9 "Integrate Unified Expression Language"  2006/08/14  Scott Johnson
 * defect 396002 CTS: no jsp fatal translation error for  taglib after actions Scott Johnson 10/17/2006
 * Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson
 * jsp2.1work
 * defect 395182.2  70FVT: make servlet 2.3 compatible with JSP 2.1 for migration 2007/02/07 Scott Johnson
 * Defect PK31450 2007/05/08 Jsp compiled classes in the temp dir are lost when the server suspends especially when there are multiple servant regions
 * Defect PK31450 includes changes for 448201, 453382, 453730
 * Defect PK69410 2008/08/28 NPE in z/OS Multiple Servants - pmdinh
 * Defect PK76810 2008/12/11 ClassNotFoundException on z/OS - pmdinh
 * Defect PM12828 2010/05/06 NPE when translation JSP with custom tag - sartoris
*/

package com.ibm.ws.jsp.translator.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.tagext.TagFileInfo;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.configuration.JspConfiguration;
import com.ibm.ws.jsp.taglib.TagLibraryCache;
import com.ibm.ws.jsp.taglib.TagLibraryInfoImpl;
import com.ibm.ws.jsp.translator.JspTranslationException;
import com.ibm.ws.jsp.translator.JspTranslator;
import com.ibm.ws.jsp.translator.JspTranslatorFactory;
import com.ibm.ws.jsp.translator.visitor.JspVisitorInputMap;
import com.ibm.ws.jsp.translator.visitor.smap.SmapVisitorResult;
import com.ibm.wsspi.jsp.compiler.JspCompiler;
import com.ibm.wsspi.jsp.compiler.JspCompilerResult;
import com.ibm.wsspi.jsp.context.translation.JspTranslationContext;
import com.ibm.wsspi.jsp.resource.JspInputSource;
import com.ibm.wsspi.jsp.resource.translation.JspResources;
import com.ibm.wsspi.jsp.resource.translation.TagFileResources;
import com.ibm.wsspi.jsp.resource.JspClassFactory; //448201
import com.ibm.ws.jsp.translator.utils.FileLocker; //448201


public class JspTranslatorUtil { 
    private static final String JSP_TRANSLATION_ID = "JspTranslation";
    private static final String DEBUG_JSP_TRANSLATION_ID = "DebugJspTranslation";
    private static final String TAGFILE_TRANSLATION_ID = "TagFileTranslation";
    private static final String DEBUG_TAGFILE_TRANSLATION_ID = "DebugTagFileTranslation";
    
    private static final String IN_MEMORY_JSP_TRANSLATION_ID = "InMemoryJspTranslation";
    private static final String IN_MEMORY_DEBUG_JSP_TRANSLATION_ID = "InMemoryDebugJspTranslation";
    private static final String IN_MEMORY_TAGFILE_TRANSLATION_ID = "InMemoryTagFileTranslation";
    private static final String IN_MEMORY_DEBUG_TAGFILE_TRANSLATION_ID = "InMemoryDebugTagFileTranslation";
    private static String separatorString = System.getProperty("line.separator");  // Defect 211450

	static private Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.translator.utils.JspTranslatorUtil";
	static{
		logger = Logger.getLogger("com.ibm.ws.jsp");
	}

    public static JspCompilerResult translateJspAndCompile(
        JspResources jspResources,
        JspTranslationContext context,
        JspConfiguration config,
        JspOptions options,
        TagLibraryCache tlc,
        boolean forceTagFileTranslation,
        List compilerOptions)
        throws JspCoreException {
        
        //PK31450
        boolean fileLock = true;
        boolean isZOS = options.isZOS();
        JspCompilerResult compileResult = null;

        long startTranslationTime = System.currentTimeMillis();
        FileLocker zosFileLocker = null; //453730
        if (isZOS && !options.isUseInMemory()) {  //453730  //PK76810 skip if use in memory
            zosFileLocker = (FileLocker) new JspClassFactory().getInstanceOf("FileLocker"); //448201
            
            //PK76810  - Starts
        	if(zosFileLocker != null){
        		fileLock = zosFileLocker.obtainFileLock(jspResources.getInputSource().getRelativeURL());
        		if (logger.isLoggable(Level.FINE))																	
        			logger.logp(Level.FINE,CLASS_NAME,"translateJspAndCompile","obtainFileLock ["+jspResources.getInputSource().getRelativeURL()+"] -> ["+ fileLock + "]");
        	}
        	//PK76810 - ends
        }
        //PK31450
        if (fileLock){			  //PK76810 locks prior to translation and unlocks after finish compiling
        	try {                 //PK76810
        		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
        			logger.logp(Level.FINE, CLASS_NAME, "translateJspAndCompile","begin translation phase uri =[" + jspResources.getInputSource().getRelativeURL() +"]");
        		}
        
        		JspTranslationResult translationResult=null;
        		try {
        			translationResult =	translateJsp(jspResources,context,config,options,tlc,forceTagFileTranslation, compilerOptions);
        			// defect 203252 begin
        		} catch (JspTranslationException e) {
        			String filePath=e.getFilePath();
        			JspTranslationException finalException = null;
        			if (filePath!=null && !filePath.equals(jspResources.getInputSource().getRelativeURL())) {
        				finalException = new JspTranslationException("jsp.error.exception.caught.translating.included.file", new Object[] {jspResources.getInputSource().getRelativeURL(),separatorString + e.getLocalizedMessage()+separatorString });  // Defect 211450
        			} 
        			else {
        				String msg=jspResources.getInputSource().getRelativeURL()+":  "+separatorString +e.getLocalizedMessage()+separatorString ;  // Defect 211450
        				finalException = new JspTranslationException("jsp.error.exception.caught.translating", new Object[] {msg});
        			}
        			finalException.setStackTrace(e.getStackTrace()); 
        			throw finalException;
        		}
        		// defect 203252 end

        		long endTranslationTime = System.currentTimeMillis();
        		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
        			logger.logp(Level.FINE, CLASS_NAME, "translateJspAndCompile","completed translation phase uri =[" + jspResources.getInputSource().getRelativeURL() +"] time = " + (endTranslationTime - startTranslationTime) +"ms");
        		}
        		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
        			logger.logp(Level.FINEST, CLASS_NAME, "translateJspAndCompile","begin compile phase uri =[" + jspResources.getInputSource().getRelativeURL() +"]");
        		}

        		if (translationResult.getTagFileCompileResult() != null && translationResult.getTagFileCompileResult().getCompilerReturnValue() != 0) {
        			compileResult = translationResult.getTagFileCompileResult();
        		}
        		else {
        			JspResources[] dependencies = null;
        			if (translationResult.getTagFileDependencyList().size() > 0) {
        				dependencies = new JspResources[translationResult.getTagFileDependencyList().size()]; 
        				dependencies = (JspResources[])translationResult.getTagFileDependencyList().toArray(dependencies);
        			}

        			JspCompiler compiler = context.getJspCompilerFactory().createJspCompiler(); 
        			compileResult = compiler.compile(new JspResources[] {jspResources}, dependencies, translationResult.getJspLineIds(), compilerOptions);

                // Defect 211450  - don't log compiler errors because they get logged by webcontainer. Only log if
                //                  deprecation or verbose is on and no error occurred. 
                /*if (compileResult.getCompilerReturnValue() != 0) {
                    logOutput = true;
                }
                else {*/
        			boolean logOutput = false;
        			if (compileResult.getCompilerReturnValue() == 0) {
        				if (options.isVerbose() || options.isDeprecation() || (compilerOptions!=null && (compilerOptions.contains("-verbose") || compilerOptions.contains("-deprecation")))) {
        					logOutput = true;
        				}
        				if (translationResult.hasSmap()) {
        					SmapGenerator smapGenerator = translationResult.getSmapGenerator(jspResources.getInputSource().getAbsoluteURL().toExternalForm());
        					installSmap(jspResources, smapGenerator);
        				}
        				jspResources.sync();
                    //syncTagFileFiles(options, translationResult);
                    //translatedTagFileFiles.clear();
        			}
                
        			if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
        				logger.logp(Level.FINEST, CLASS_NAME, "translateJspAndCompile","complete compile phase uri =[" + jspResources.getInputSource().getRelativeURL() +"]");
        			}
        			if (logOutput) {
        				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)) {
        					logger.logp(Level.WARNING, CLASS_NAME, "translateJspAndCompile","[" + compileResult.getCompilerMessage() + "]");
        				}
        			}
                //448201
        		}
        	}				//PK76810
        	finally {		//PK76810
        		if(isZOS){
        			if (zosFileLocker != null) {  //453382
        				zosFileLocker.releaseFileLock(jspResources.getInputSource().getRelativeURL());			//PK76810
						if (logger.isLoggable(Level.FINE))														//PK76810 
							logger.logp(Level.FINE,CLASS_NAME,"translateJspAndCompile","releaseFileLock"); 		//PK76810
                    }
        		}
            }				//PK76810 - Start
        }
        else{
        	if(logger.isLoggable(Level.FINE)){
    			logger.logp(Level.FINE, CLASS_NAME, "translateJspAndCompile","Failed translation phase uri =[" + jspResources.getInputSource().getRelativeURL() +"]");
    		}	
        }					//PK76810 - End
        
        return (compileResult);
    }

    public static JspTranslationResult translateJsp(
        JspResources jspResources,
        JspTranslationContext context,
        JspConfiguration config,
        JspOptions options,
        TagLibraryCache tlc,
        boolean forceTagFileTranslation,
        List compilerOptions)
        throws JspCoreException {

        JspTranslationResult translationResult = new JspTranslationResult();

        Integer JspIdConsumerCounter=new Integer(-1);
        String JspIdConsumerPrefix=null;
        List tagFileDependencyIdList = new ArrayList();
        JspVisitorInputMap inputMap = new JspVisitorInputMap();
        inputMap.put("TagFileDependencies", tagFileDependencyIdList);
        inputMap.put("TagLibraryCache", tlc);
        inputMap.put("JspFiles", jspResources);
        inputMap.put("JspOptions", options);
        inputMap.put("JspUri", jspResources.getInputSource().getRelativeURL());
        inputMap.put("JspIdConsumerCounter", JspIdConsumerCounter);
        inputMap.put("JspIdConsumerPrefix", JspIdConsumerPrefix);

        String jspVisitorCollectionId = null;
        String tagFileVisitorCollectionId = null;
        if (!options.isUseInMemory()) {
	        jspVisitorCollectionId = JSP_TRANSLATION_ID;
	        tagFileVisitorCollectionId = TAGFILE_TRANSLATION_ID;

        if (options.isDebugEnabled()) {
            jspVisitorCollectionId = DEBUG_JSP_TRANSLATION_ID;
            tagFileVisitorCollectionId = DEBUG_TAGFILE_TRANSLATION_ID;
        }
        } else {
	        jspVisitorCollectionId = IN_MEMORY_JSP_TRANSLATION_ID;
	        tagFileVisitorCollectionId = IN_MEMORY_TAGFILE_TRANSLATION_ID;
	
	        if (options.isDebugEnabled()) {
	            jspVisitorCollectionId = IN_MEMORY_DEBUG_JSP_TRANSLATION_ID;
	            tagFileVisitorCollectionId = IN_MEMORY_DEBUG_TAGFILE_TRANSLATION_ID;
	        }
        }

        JspTranslator jspTranslator = JspTranslatorFactory.getFactory().createTranslator(jspVisitorCollectionId, jspResources.getInputSource(), context, config, options, tlc.getImplicitTagLibPrefixMap());
        HashMap resultMap = jspTranslator.processVisitors(inputMap);
        
        if (resultMap.containsKey("Smap")) {
            SmapVisitorResult smapResult = (SmapVisitorResult)resultMap.get("Smap");
            translationResult.addSmapGenerator(jspResources.getInputSource().getAbsoluteURL().toExternalForm(), smapResult.getSmapGenerator());
        }
        
        translationResult.addJspLineIds(jspResources, jspTranslator.getDocument());
        
        if (tagFileDependencyIdList.size() > 0) {
            Object lock = tlc.getTagFileLock(tagFileDependencyIdList);
            try {
                synchronized (lock) {
                    List tagFilesToCompile = new ArrayList();
                    List tagFilesNotToCompile = new ArrayList();
                    Collection tagFileIds = new ArrayList();
                    for (Iterator itr = tagFileDependencyIdList.iterator(); itr.hasNext();) {
                        TagFileId tagFileId = (TagFileId) itr.next();
                        TagLibraryInfoImpl tli = tlc.getTagLibraryInfo(tagFileId.getUri(), tagFileId.getPrefix(), jspResources.getInputSource().getRelativeURL());
                        TagFileInfo tfi = tli.getTagFile(tagFileId.getTagName());
                        JspInputSource tagFileInputSource = context.getJspInputSourceFactory().copyJspInputSource(tli.getInputSource(), tfi.getPath());
                        
                        TagFileResources tagFileResources = context.getJspResourcesFactory().createTagFileResources(tagFileInputSource, tfi);
                        tagFileResources = tlc.getTagFileResources(tagFileResources);
                        translationResult.getTagFileDependencyList().add(tagFileResources);
        
                        if (forceTagFileTranslation && tagFileResources.getGeneratedSourceFile().getParentFile().exists() == false) {
                            tagFileResources.getGeneratedSourceFile().getParentFile().mkdirs();
                        }
                        if (forceTagFileTranslation || tagFileResources.isOutdated() ) {
                            JspConfiguration tagConfiguration = config.createEmptyJspConfiguration();
                            if (tli!=null) { //I think this should always be the case
                                if (tli.getRequiredVersion()!=null) {
                                    tagConfiguration.setJspVersion(tli.getRequiredVersion());
                                }
                            }
                            tagConfiguration.setElIgnored(config.elIgnored());
                            JspTranslator tagFileTranslator =
                                JspTranslatorFactory.getFactory().createTranslator(tagFileVisitorCollectionId, tagFileInputSource, context, tagConfiguration, options, tlc.getImplicitTagLibPrefixMap());
                            JspVisitorInputMap tagFileInputMap = new JspVisitorInputMap();
                            tagFileInputMap.put("JspOptions", options);
                            tagFileInputMap.put("TagLibraryCache", tlc);
                            tagFileInputMap.put("TagFileFiles", tagFileResources);
                            tagFileInputMap.put("TagFileInfo", tfi);
                            tagFileInputMap.put("isTagFile", new Boolean(true));
                            tagFileInputMap.put("JspUri", jspResources.getInputSource().getRelativeURL());
                            HashMap results = tagFileTranslator.processVisitors(tagFileInputMap);
                            if (results.containsKey("Smap")) {
                                SmapVisitorResult smapResult = (SmapVisitorResult)results.get("Smap");
                                translationResult.addSmapGenerator(tagFileResources.getInputSource().getAbsoluteURL().toExternalForm(), smapResult.getSmapGenerator());
                            }
                            JspTranslationResult.loadJspIdList(tagFileResources, tagFileTranslator.getDocument().getDocumentElement(), tagFileIds);
                            tagFileResources.syncGeneratedSource();
                            tagFilesToCompile.add(tagFileResources);
                        }
                        else {
                            tagFilesNotToCompile.add(tagFileResources);
                        }
                    }
                    
                    if (tagFilesToCompile.size() > 0) {
                        JspResources[] resourcesToCompile = new JspResources[tagFilesToCompile.size()]; 
                        resourcesToCompile = (JspResources[])tagFilesToCompile.toArray(resourcesToCompile);
                        
                        JspCompiler compiler = context.getJspCompilerFactory().createJspCompiler(); 

                        JspResources[] dependencies = null;
                        if (tagFilesNotToCompile.size() > 0) {
                            dependencies = new JspResources[tagFilesNotToCompile.size()]; 
                            dependencies = (JspResources[])tagFilesNotToCompile.toArray(dependencies);
                        }
                        
                        JspCompilerResult compileResult = compiler.compile(resourcesToCompile, dependencies, tagFileIds, compilerOptions);
                        translationResult.setTagFileCompileResult(compileResult);
                        syncTagFileFiles(options, tagFilesToCompile, translationResult);
                    }
                }
            }
            finally {
                tlc.releaseTagFileLock(tagFileDependencyIdList);                
            }
        }
        
        return translationResult;
    }

    public static void syncTagFileFiles(JspOptions options, List translatedTagFiles, JspTranslationResult translationResult) throws JspCoreException {
        for (Iterator itr = translatedTagFiles.iterator(); itr.hasNext();) {
            TagFileResources tagFileResources = (TagFileResources) itr.next();
            synchronized (tagFileResources) {
                tagFileResources.sync();
                if (translationResult.hasSmap()) {
                    SmapGenerator smapGenerator = translationResult.getSmapGenerator(tagFileResources.getInputSource().getAbsoluteURL().toExternalForm());
                    installSmap(tagFileResources, smapGenerator); 
                }
            }
        }
    }
    
    public static void installSmap(JspResources jspResources, SmapGenerator smapGenerator) throws JspCoreException {
		int inx=jspResources.getGeneratedSourceFile().getPath().lastIndexOf(".java");
		String classFilePath = jspResources.getGeneratedSourceFile().getPath().substring(0,inx)+".class";
        File classFile = new File(classFilePath);
        if (smapGenerator != null) {
            try {
                SDEInstaller.install(classFile, smapGenerator);
            }
            catch (IOException e) {
                throw new JspCoreException(e);
            }
        }
    }

    public static void printTagFileFiles(List translatedTagFileFiles) throws JspCoreException {
        for (Iterator itr = translatedTagFileFiles.iterator(); itr.hasNext();) {
            TagFileResources tagFileResources = (TagFileResources) itr.next();
            System.out.println("JspTranslatorUtil printTagFileFiles() " + tagFileResources.toString());
        }
    }

    public static boolean isExpression(String token) {
		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
			logger.logp(Level.FINER, CLASS_NAME, "isExpression(String token)","isExpression(String token)  token = ["+token+"]");
		}
		boolean bool=isExpression(token, true);
		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
			logger.logp(Level.FINER, CLASS_NAME, "isExpression(String token)","isExpression(String token)  returning = ["+bool+"]");
		}
        return bool;
    }

    public static boolean isExpression(String token, boolean isXml) {
        String openExpr;
        String closeExpr;
		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
			logger.logp(Level.FINER, CLASS_NAME, "isExpression(String token, boolean isXml)","isExpression(String token, boolean isXml) value= ["+token+"] isXml= ["+isXml+"]");
		}
        if (isXml) {
            openExpr = Constants.OPEN_EXPR_XML;
            closeExpr = Constants.CLOSE_EXPR_XML;
        }
        else {
            openExpr = Constants.OPEN_EXPR;
            closeExpr = Constants.CLOSE_EXPR;
        }
		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
			logger.logp(Level.FINER, CLASS_NAME, "isExpression(String token, boolean isXml)","isExpression(String token, boolean isXml) openExpr= ["+openExpr+"] closeExpr= ["+closeExpr+"]");
		}
        if (token.startsWith(openExpr) && token.endsWith(closeExpr)) {
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
    			logger.logp(Level.FINER, CLASS_NAME, "isExpression(String token, boolean isXml)","isExpression(String token, boolean isXml) returning true");
    		}
            return true;
        }
        else {
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
    			logger.logp(Level.FINER, CLASS_NAME, "isExpression(String token, boolean isXml)","isExpression(String token, boolean isXml) returning false");
    		}
            return false;
        }
    }

    public static boolean isELInterpreterInput(String token, JspConfiguration jspConfig) {
		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
			logger.logp(Level.FINER, CLASS_NAME, "isELInterpreterInput(String, JspConfiguration)","isELInterpreterInput(String, JspConfiguration) value= ["+token+"]");
		}
		//isDeferredSyntaxAllowedAsLiteral will be true for <2.1 apps ... handled in the following
        return JspTranslatorUtil.isELInterpreterInput(token, jspConfig, !jspConfig.isDeferredSyntaxAllowedAsLiteral()); 
    }

    public static boolean isELInterpreterInput(String token, JspConfiguration jspConfig, boolean checkDeferred) {
    	//Exception e = new Exception("My exception");
    	//e.printStackTrace(System.out);
        // jsp2.1ELwork 
		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
			logger.logp(Level.FINER, CLASS_NAME, "isELInterpreterInput(String, JspConfiguration, boolean)","isELInterpreterInput(String, JspConfiguration, boolean) value= ["+token+"]");
			logger.logp(Level.FINER, CLASS_NAME, "isELInterpreterInput(String, JspConfiguration, boolean)","isELInterpreterInput(String, JspConfiguration, boolean) jspConfiguration.elIgnored() =[" + jspConfig.elIgnored() +"]");
			logger.logp(Level.FINER, CLASS_NAME, "isELInterpreterInput(String, JspConfiguration, boolean)","isELInterpreterInput(String, JspConfiguration, boolean) checkDeferred =[" + checkDeferred +"]");
		}
        boolean isELValue = false;
        if (jspConfig.elIgnored() == false) {
            int immediateIndex = token.indexOf("${");
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
    			logger.logp(Level.FINER, CLASS_NAME, "isELInterpreterInput(String, JspConfiguration, boolean)","isELInterpreterInput(String, JspConfiguration, boolean) index of ${= ["+immediateIndex+"]");
    		}
            int deferredIndex = token.indexOf("#{");
    		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
    			logger.logp(Level.FINER, CLASS_NAME, "isELInterpreterInput(String, JspConfiguration, boolean)","isELInterpreterInput(String, JspConfiguration, boolean) index of #{= ["+deferredIndex+"]");
    		}

    		int index=-1;
            if (immediateIndex != -1) {
            	index=immediateIndex;            	
            }
            else if ( deferredIndex != -1) {
            	//if (checkDeferred && !jspConfig.isDeferredSyntaxAllowedAsLiteral()) {
                //checkDeferred should have been passed in correctly.  Do not change as we want to pass in true from BaseTagGenerator for old JSF apps
                if (checkDeferred) {
            		index=deferredIndex;
            	}            		
            }
            if (index!=-1) {
                char prevChar = ' ';
                if (index > 0) {
                    prevChar = token.charAt(index - 1);
                }
        		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
        			logger.logp(Level.FINER, CLASS_NAME, "isELInterpreterInput(String, JspConfiguration, boolean)","isELInterpreterInput(String, JspConfiguration, boolean) prevChar= ["+prevChar+"]");
        		}
                if (prevChar != '\\') {
                    isELValue = true;
                }
        		if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
        			logger.logp(Level.FINER, CLASS_NAME, "isELInterpreterInput(String, JspConfiguration, boolean)","isELInterpreterInput(String, JspConfiguration, boolean) isELValue= ["+isELValue+"]");
        		}
            }
        }
        return (isELValue);
    }
    
    /*
     * Checks to see if the given attribute value represents a runtime or EL
     * expression.
     */
    public static boolean isExpression(String token, boolean isXml, JspConfiguration jspConfig, boolean checkDeferred ) {
    	boolean isExpression=JspTranslatorUtil.isExpression(token, isXml);
    	boolean isELInput=JspTranslatorUtil.isELInterpreterInput(token, jspConfig, checkDeferred);
        if (isExpression || isELInput )
            return true;
        else
            return false;
    }

    public static void printAllElements(HashMap processedDocuments) {
        Document document;
        for (Iterator itr = processedDocuments.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            document = (Document) processedDocuments.get(key);
            printElements(document.getDocumentElement(), 0);
        }
    }

    public static void printElements(Element element, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.println("Element - " + element.getNodeName());

        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr) attrs.item(i);
            for (int j = 0; j < level; j++) {
                System.out.print("\t");
            }
            System.out.println("Attr - " + attr.getName() + " : " + attr.getValue() + " : " + attr.getNamespaceURI());
        }

        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n instanceof Element) {
                Element childElement = (Element) n;
                System.out.println();
                printElements(childElement, level + 1);
            }
            else if (n instanceof CDATASection) {
                System.out.println();
                CDATASection cdata = (CDATASection) n;
                for (int j = 0; j < level + 1; j++) {
                    System.out.print("\t");
                }
                String s = cdata.getData();
                s = s.replaceAll("\r", "");
                s = s.replaceAll("\n", "{cr}");
                System.out.println("CDATA - [" + s + "]");
            }
        }
    }
    
    //LIDB4147-9 Begin
    //PM12828 - need to account for a case where type is [Lcom.ibm.sample.TagsAttr;
    public static Class toClass(String type, ClassLoader loader)
			throws ClassNotFoundException {
    	        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINER)){
                    logger.logp(Level.FINER, CLASS_NAME, "toClass(String, ClassLoader)"," type= ["+type+"] loader= ["+loader+"]");
                }

		Class c = null;
		int i0 = type.indexOf('[');
		int dims = 0;
		if (i0 > 0) {
			// This is an array. Count the dimensions
			for (int i = 0; i < type.length(); i++) {
				if (type.charAt(i) == '[')
					dims++;
			}
			type = type.substring(0, i0);
		}else if (i0==0) {
			//PM12828 start
			//check for a type like this "[Lcom.ibm.sample.TagsAttr;" This means it is an array type.
			//a two dimensional array might look like [[Lcom.ibm.sample.TagsAttr;
			int bracketL = type.indexOf("[L");
			if (bracketL>-1 && type.endsWith(";")) {
				type = type.substring(bracketL+2,(type.length() - 1));
				dims+=(bracketL+1);
			}
		}
		//PM12828 end
		
		if ("boolean".equals(type))
			c = boolean.class;
		else if ("char".equals(type))
			c = char.class;
		else if ("byte".equals(type))
			c = byte.class;
		else if ("short".equals(type))
			c = short.class;
		else if ("int".equals(type))
			c = int.class;
		else if ("long".equals(type))
			c = long.class;
		else if ("float".equals(type))
			c = float.class;
		else if ("double".equals(type))
			c = double.class;
        else if ("void".equals(type)) {
            return Void.class;
        }
		else if (type.indexOf('[') < 0)
			c = loader.loadClass(type);

		if (dims == 0)
			return c;

		if (dims == 1)
			return java.lang.reflect.Array.newInstance(c, 1).getClass();

		// Array of more than i dimension
		return java.lang.reflect.Array.newInstance(c, new int[dims]).getClass();
	}
    //LIDB4147-9 End
    
    public static boolean hasJspBody(Element element) {
        boolean hasJspBody = false;
        boolean jspBodyFound = false;
        
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                Element childElement = (Element)child;
                if (childElement.getNamespaceURI() != null && childElement.getNamespaceURI().equals(Constants.JSP_NAMESPACE) &&
                    childElement.getLocalName().equals(Constants.JSP_BODY_TYPE)) {
                    if (childElement.hasChildNodes()) {                            
                        hasJspBody = true;
                        break;
                    }
                }
            }
        }
        return hasJspBody;
    }
    
    public static boolean hasBody(Element element) {
        boolean hasBody = false;
        
        NodeList children = element.getChildNodes();
        boolean attrOrBodyTagFound = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element)child;
                if (childElement.getNamespaceURI() != null && childElement.getNamespaceURI().equals(Constants.JSP_NAMESPACE)) {
                    if (childElement.getLocalName().equals(Constants.JSP_ATTRIBUTE_TYPE) || 
                        childElement.getLocalName().equals(Constants.JSP_BODY_TYPE)) {
                        attrOrBodyTagFound = true;    
                    }
                    else {
                        hasBody = true;
                        break;                        
                    }
                }
                else {
                    hasBody = true;
                    break;                        
                }
            }
            else if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
                hasBody = true;
            }
        }
        if (attrOrBodyTagFound && hasBody) {
            hasBody = false;                        
        }
        return hasBody;
    }

    // jsp2.1ELwork
    public static boolean booleanValue(String s) {
    	boolean allowYesToMeanTrue = true;
        return booleanValue(s, allowYesToMeanTrue);
        }
    
    public static boolean booleanValue(String s, boolean allowYesToMeanTrue) {
		boolean b = false;
		if (s != null) {
			if (s.equalsIgnoreCase("yes") && allowYesToMeanTrue) {
				b = true;
			} else {
				b = Boolean.valueOf(s).booleanValue();
			}
		}
		return b;
	}
}
