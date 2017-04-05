//IBM Confidential OCO Source Material
//	5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//
//
//  Changes for defect 198932 "Support v5 JSP reload logic for v5 apps in 6.0"
//		added variables reloadEnabledSet, servlet2_2, servletEngineReloadEnabled, 
//			servletEngineReloadInterval
//  defect 215691 "add support for javaEncoding"  2004/07/12  Scott Johnson
//	defect 221843: "add support for autoResponseEncoding"
//  defect 223085 "Replace tsx:repeat optimized tag with taglib version"  2004/08/10 Scott Johnson
//	defect 247772 syncToOsThread changes, removed setSyncToThread and isSyncToThread, added isZOS 2005/01/14 Maxim Moldenhauer
//  defect PK13492 "CP943C ENCODING NOT WORKING PROPERLY ON HP"  2006/01/12 Scott Johnson
//  defect PK20187 Property 'useStringCast' (Adds implicit string cast for included resources) 2006/04/24 SDJ
//  defect PK28029 ieClassId has no effect on JSP plugin tag 
//  defect PK26741 RepeatTags do not accept 'index' to be 'int'
//  Defect PK29373 Provide backward compatibility with v5(Page scope variables)
//  defect 393421 - CTS:jsp translation error not occuring for wrong  <jsp:output> 2006/09/27 Scott Johnson
//  defect 393421.1 - CTS: default for fixing 393421 needs to be changed    2006/10/05Scott Johnson
//  defect 396002 CTS: no jsp fatal translation error for  taglib after actions Scott Johnson 10/17/2006
//  defect PK30879 "Unmatched end tags" exception not to be thrown in v6 (Aetna) Scott Johnson 10/18/2006
//  defect 400645 "Batchcompiler needs to get webcon custom props"  2004/10/25 Scott Johnson
//  defect PK31135 "Change in Iteration eval in v6 causes infinite loops in certain scenarios"
//  Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson
//  defect PK34989 Supercedes PK26679 (Provides an optional flag to trim text before creating CDATA section) 2007/02/13
//  Defect PK47738 v6 does not allow page authors to encode params within params tag(spec compliant) maintain backward compatibility with v5 which allows encoding of params.
//  Defect PK65013 2008/07/07 Need ability to customize pageContext variable.   sartoris
//  Defect PK69220 2008/10/10 sartoris   Add a flag to be able turn off tld searching through an application.
//  Defect PK72039 Add ability to continue to compile the rest of the JSPs during a batch compile failure  2008/10/21  Jay Sartoris
//  Defect PM04610 Need option to specify version 1.6 for the jdkSourceLevel attribute  2010/02/03  Jay Sartoris
//  Defect PM06063 2010/01/26 pmdinh    Add a flag to disable Feature 4147-24 "JSP 2.1 upgrade: Incorporate resource injection engine"
//  Defect 655818 Spec clarification - do not reuse property group configuration on included files (is-xml & page-encoding)
//  Defect PM21395 09/02/2010 pmdinh    Decode double quote in the attribute's value of a tag
//  Defect PM41476 07/28/2011 sartoris    Tags have the xmlns attribute when rendered.

package com.ibm.ws.jsp;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.wsspi.webcontainer.util.EncodingUtils;

public class JspOptions {
    static protected Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.JspOptions";
    static{
        logger = Logger.getLogger("com.ibm.ws.jsp", "com.ibm.ws.jsp.resources.messages");
    }
    public static boolean ALLOWJSPOUTPUTELEMENTMISMATCH = false;  //393421.1 note default needs to be 'false'!
    public static boolean ALLOWTAGLIBPREFIXREDEFINITION = false;  //396002 note default needs to be 'false'!
    public static boolean ALLOWTAGLIBPREFIXUSEBEFOREDEFINITION = false;  //396002 note default needs to be 'false'!
    public static boolean ALLOWUNMATCHEDENDTAG = false;  //PK30879 note default needs to be 'false'!
    protected boolean    allowJspOutputElementMismatch = ALLOWJSPOUTPUTELEMENTMISMATCH;      
    protected boolean    allowTaglibPrefixRedefinition = ALLOWTAGLIBPREFIXREDEFINITION;      
    protected boolean    allowTaglibPrefixUseBeforeDefinition = ALLOWTAGLIBPREFIXUSEBEFOREDEFINITION;      
    protected boolean    allowUnmatchedEndTag = ALLOWUNMATCHEDENDTAG;      
    protected boolean    autoResponseEncoding = false;
    protected boolean    classDebugInfo = false;
    /**
     * @deprecated
     *
     * compileWithAssert is replaced by {@link #jdkSourceLevel}
     */
    protected boolean    compileWithAssert = false;
    protected boolean    debugEnabled = false;
    protected boolean    deprecation = false;
    protected boolean    disableJspRuntimeCompilation = false;
    protected String     extendedDocumentRoot = null;
    protected String     preFragmentExtendedDocumentRoot = null;
    protected String     extensionProcessorClass = null;
    protected String     ieClassId = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";
    protected boolean    isZOS = false;
    protected String     javaEncoding = "UTF-8";
    protected String     jdkSourceLevel = "16";
    protected String     jspCompileClasspath = null;
    protected boolean    keepGenerated = false;
    protected boolean    keepGeneratedclassfiles = true;
    protected Map        looseLibMap = null;
    protected File       outputDir = null;
    protected boolean    reloadEnabled = Constants.DEFAULT_RELOAD_ENABLED;
    protected boolean    reloadEnabledSet = false;
    protected long       reloadInterval = Constants.DEFAULT_RELOAD_INTERVAL;
    protected boolean    servlet2_2 = false;
    protected boolean    servletEngineReloadEnabled = Constants.DEFAULT_RELOAD_ENABLED;
    protected long       servletEngineReloadInterval = Constants.DEFAULT_RELOAD_INTERVAL;
    protected boolean    syncToThread = false;  // LIDB2284-29
    protected boolean    trackDependencies = false;
    protected String     translationContextClass = null;
    protected boolean    useCDataTrim = false;      //PK34989
    protected boolean    disableURLEncodingForParamTag = false; //PK47738 added in 6.1Service/deprecated
    protected boolean    useDevMode = false;   // LIDB4293-2
    protected boolean    useDevModeSet = false;   // LIDB4293-2
    protected boolean    useFullPackageNames = false;
    protected boolean    useImplicitTagLibs = true;
    protected boolean    useInMemory = false;   // LIDB4293-2
    protected boolean    useIterationEval = false;  //PK31135
    protected boolean    useJDKCompiler = false; // defect jdkcompiler
    protected boolean    useJikes = false;
    protected boolean    useOptimizedTags = false;
    protected boolean    usePageTagPool = false;
    protected boolean    useRepeatInt = false;      //PK26741
    protected boolean    useScriptVarDupInit = false;   //PK29373
    protected boolean    useStringCast = false;    //PK20187
    protected boolean    useThreadTagPool = false;
    protected boolean    verbose = false;
    //@BLB Pretouch Begin
    protected int        prepareJSPs = Constants.PREPARE_JSPS_DEFAULT_MINLENGTH;
    protected boolean    prepareJSPsSet = false;
    protected String     prepareJSPsClassloadChanged = null;
    protected int        prepareJSPsClassload = Constants.PREPARE_JSPS_DEFAULT_STARTAT;
    protected int        prepareJSPThreadCount = Constants.PREPARE_JSPS_DEFAULT_THREADS;
    protected boolean    evalQuotedAndEscapedExpression = false; //PK53233
    protected boolean    convertExpression = false; //PK53703
    protected boolean    allowNullParentInTagFile = false; //PK62809  not documented
    protected boolean   modifyPageContextVariable = false; //PK65013

    protected boolean recompileJspOnRestart = false;
    protected boolean    convertAttrValueToString = false; //PK57873
    protected boolean    disableTldSearch = false; //PK69220
    protected boolean    compileAfterFailure = false; //PK72039
    protected boolean    disableResourceInjection = false;  //PM06063
    protected boolean reusePropertyGroupConfigOnInclude=false; //655818
    protected boolean    enableDoubleQuotesDecoding = false;   //PM21395
    protected boolean    enableCDIWrapper = false; //support wrapping ExpressionFactory for batch compiler
    protected boolean    removeXmlnsFromOutput = false;   //PM41476
    
    private static Boolean javaVersionAtLeast17 = null;
    private static final Object tmpLockObject = new Object();
    
    //@BLB Pretouch End
    // defect 400645
    String overriddenJspOptions = new String();
    
    public JspOptions() {
    }   
    
    public JspOptions(Properties jspParams) {
        populateOptions(jspParams);
    }
    
	public void populateOptions(Properties jspParams) {

		/*--------------------*/
		/* Load Option Values */
		/*--------------------*/
		
        String debugInfo = jspParams.getProperty("classdebuginfo");
        if (debugInfo != null)
        {
            if (debugInfo.equalsIgnoreCase("true"))
                this.classDebugInfo  = true;
            else if (debugInfo.equalsIgnoreCase("false"))
                this.classDebugInfo  = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for classDebugInfo = "+ debugInfo);
                }
            }
        }

        String debugProp = System.getProperty("was.debug.mode");
        if ((debugProp != null) && (debugProp.equalsIgnoreCase("true"))) {
            this.debugEnabled = true;
        }

        String deprecat = jspParams.getProperty("deprecation");
        if (deprecat != null) {
            if (deprecat.equalsIgnoreCase("true"))
                this.deprecation = true;
            else if (deprecat.equalsIgnoreCase("false"))
                this.deprecation = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for deprecation = "+ deprecat);
                }
            }
        }

        String javaEnc = jspParams.getProperty("javaEncoding");
        if (javaEnc != null) {
        	if (EncodingUtils.isCharsetSupported(javaEnc)) {
        		this.javaEncoding=javaEnc;
        	}
        	else {        		
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for javaEncoding ["+ javaEnc+"]. Defaulting to "+javaEncoding+".");
                }
        	}
        }
        
        // defect jdkcompiler begin
        String useJDKCompiler = jspParams.getProperty("useJDKCompiler");
        if (useJDKCompiler != null)
        {
            if (useJDKCompiler.equalsIgnoreCase("true"))
                this.useJDKCompiler = true;
            else if (useJDKCompiler.equalsIgnoreCase("false"))
                this.useJDKCompiler = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useJDKCompiler = "+ useJDKCompiler);
                }
            }
        }
        // defect jdkcompiler end
        
        String useJikesCompiler = jspParams.getProperty("useJikes");
        if (useJikesCompiler != null) {
            if (useJikesCompiler.equalsIgnoreCase("true"))
                this.useJikes = true;
            else if (useJikesCompiler.equalsIgnoreCase("false"))
                this.useJikes = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useJikes = "+ useJikesCompiler);
                }
            }
        }
        
        String useAssert = jspParams.getProperty("compileWithAssert");
        if (useAssert != null) {
            if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "compileWithAssert is deprecated.  Use jdkSourceLevel instead.");
            }
            if (useAssert.equalsIgnoreCase("true"))
                this.compileWithAssert = true;
            else if (useAssert.equalsIgnoreCase("false"))
                this.compileWithAssert = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for compileWithAssert = "+ useAssert);
                }
            }
        }
        
        String useJdkSourceLevel = jspParams.getProperty("jdkSourceLevel");
        if (useJdkSourceLevel != null) {
            if (useJdkSourceLevel.equals("13")
			 || useJdkSourceLevel.equals("14")
			 || useJdkSourceLevel.equals("15")
             || useJdkSourceLevel.equals("16")  //PM04610
             || useJdkSourceLevel.equals("17")) {
                if (useJdkSourceLevel.equals("17")) {
                    if (JspOptions.isJavaVersionAtLeast17()) {
                        //if 17 is only supported by the JDK compiler, set the useJDKCompiler to true
                        //this.useJDKCompiler=true;
                        //this.useJikes=false;
                    } else {
                        useJdkSourceLevel="16";
                    }
                }
                this.jdkSourceLevel = useJdkSourceLevel;
            }
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for jdkSourceLevel = "+ useJdkSourceLevel+".");
                }
            }
        }
		
		// Normalize compileWithAssert and jdkSourceLevel; compileWithAssert with value true means compile with
		// jdk 1.4 source level.  Only jdkSourceLevel will be used elsewhere in the JSP container.
		if (this.compileWithAssert==true){
			// if jdkSourceLevel was not set, then set it to 1.4 level
			if (useJdkSourceLevel!=null && useJdkSourceLevel.equals("13")){
				// if jdkSourceLevel was set to 1.3, then set it to 1.4 level to honor higher setting of compileWithAssert
				this.jdkSourceLevel="14";
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "jdkSourceLevel was set to '13'.  Setting jdkSourceLevel to '14' because compileWithAssert is true.");
                }				
			}
		}

        String disableComp = jspParams.getProperty("disableJspRuntimeCompilation");
        if ( disableComp != null ) {
            if ( disableComp.equalsIgnoreCase("true") )
                this.disableJspRuntimeCompilation = true;
            else if ( disableComp.equalsIgnoreCase("false") )
                this.disableJspRuntimeCompilation = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for disableJspRuntimeCompilation = "+ disableComp);
                }
            }
        }
        
        String keepgen = jspParams.getProperty("keepgenerated");
        if (keepgen != null)
        {
            if (keepgen.equalsIgnoreCase("true"))
                this.keepGenerated = true;
            else if (keepgen.equalsIgnoreCase("false"))
                this.keepGenerated = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for keepGenerated = "+ keepgen);
                }
            }
        }
        
        //JSF_IMPL_CHECK
        String recompileJspOnRestartString = jspParams.getProperty("recompileJspOnRestart");
        if (recompileJspOnRestartString != null)
        {
            if (recompileJspOnRestartString.equalsIgnoreCase("true"))
                this.recompileJspOnRestart = true;
            else if (recompileJspOnRestartString.equalsIgnoreCase("false"))
                this.recompileJspOnRestart = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for recompileJspOnRestart = "+ recompileJspOnRestartString);
                }
            }
        }
        
        //PK20187
		String usecast = jspParams.getProperty("useStringCast");
		if ( usecast != null) {
			 if ( usecast.equalsIgnoreCase("true") ) {
				 this.useStringCast = true;
		 	 }
		     else if ( usecast.equalsIgnoreCase("false") ) {
					this.useStringCast = false;
		     }
			 else {
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
					logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useStringCast = "+ usecast);
				}
			 }
		}
		//PK20187

		//PK29373
		String useScriptVarDupInitType = jspParams.getProperty("useScriptVarDupInit");
		if (useScriptVarDupInitType != null) {
			if (useScriptVarDupInitType.equalsIgnoreCase("true"))
				this.useScriptVarDupInit = true;
			else if (useScriptVarDupInitType.equalsIgnoreCase("false"))
				this.useScriptVarDupInit = false;
			else {
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
					logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useScriptVarDupInit = "+ useScriptVarDupInitType);
				}
			}
		}
        //PK29373

        //393421
        String allowJspOutputElementMismatchType = jspParams.getProperty("allowJspOutputElementMismatch");
        if (allowJspOutputElementMismatchType != null) {
            if (allowJspOutputElementMismatchType.equalsIgnoreCase("true"))
                this.allowJspOutputElementMismatch = true;
            else if (allowJspOutputElementMismatchType.equalsIgnoreCase("false"))
                this.allowJspOutputElementMismatch = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for allowJspOutputElementMismatch = "+ allowJspOutputElementMismatchType);
                }
            }
        }
        //393421

        //396002
        String allowTaglibPrefixRedefinitionType = jspParams.getProperty("allowTaglibPrefixRedefinition");
        if (allowTaglibPrefixRedefinitionType != null) {
            if (allowTaglibPrefixRedefinitionType.equalsIgnoreCase("true"))
                this.allowTaglibPrefixRedefinition = true;
            else if (allowTaglibPrefixRedefinitionType.equalsIgnoreCase("false"))
                this.allowTaglibPrefixRedefinition = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for allowTaglibPrefixRedefinition = "+ allowTaglibPrefixRedefinitionType);
                }
            }
        }
        //396002

        //396002
        String allowTaglibPrefixUseBeforeDefinitionType = jspParams.getProperty("allowTaglibPrefixUseBeforeDefinition");
        if (allowTaglibPrefixUseBeforeDefinitionType != null) {
            if (allowTaglibPrefixUseBeforeDefinitionType.equalsIgnoreCase("true"))
                this.allowTaglibPrefixUseBeforeDefinition = true;
            else if (allowTaglibPrefixUseBeforeDefinitionType.equalsIgnoreCase("false"))
                this.allowTaglibPrefixUseBeforeDefinition = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for allowTaglibPrefixUseBeforeDefinition = "+ allowTaglibPrefixUseBeforeDefinitionType);
                }
            }
        }
        //396002

        // PK30879
        String allowUnmatchedEndTagType = jspParams.getProperty("allowUnmatchedEndTag");
        if (allowUnmatchedEndTagType != null) {
            if (allowUnmatchedEndTagType.equalsIgnoreCase("true"))
                this.allowUnmatchedEndTag = true;
            else if (allowUnmatchedEndTagType.equalsIgnoreCase("false"))
                this.allowUnmatchedEndTag = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for allowUnmatchedEndTag = "+ allowUnmatchedEndTagType);
                }
            }
        }
        // PK30879

        //PK31135
		String useIterationEvalStr = jspParams.getProperty("useIterationEval");
		if (useIterationEvalStr != null) {
			if (useIterationEvalStr.equalsIgnoreCase("true"))
				this.useIterationEval = true;
			else if (useIterationEvalStr.equalsIgnoreCase("false"))
				this.useIterationEval = false;
			else {
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
					logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useIterationEval = "+ useIterationEvalStr);
				}
			}
		}//PK31135

        //LIDB4293-2
		String useInMemoryStr = jspParams.getProperty("useInMemory");
		if (useInMemoryStr != null) {
			if (useInMemoryStr.equalsIgnoreCase("true"))
				this.useInMemory = true;
			else if (useInMemoryStr.equalsIgnoreCase("false"))
				this.useInMemory = false;
			else {
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
					logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useInMemory = "+ useInMemoryStr);
				}
			}
		}//LIDB4293-2

        //LIDB4293-2
		String useDevModeStr = jspParams.getProperty("useDevMode");
		if (useDevModeStr != null) {
			if (useDevModeStr.equalsIgnoreCase("true")) {
				this.useDevMode = true;
				this.useDevModeSet = true;
			}
			else if (useDevModeStr.equalsIgnoreCase("false")) {
				this.useDevMode = false;
				this.useDevModeSet = true;
			}
			else {
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
					logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useDevMode = "+ useDevModeStr);
				}
			}
		}//LIDB4293-2

        String reloadEnable = jspParams.getProperty("reloadEnabled");
        if ( reloadEnable != null ) {
            if ( reloadEnable.equalsIgnoreCase("true") ) {
				this.reloadEnabled = true;
				reloadEnabledSet = true;
            }
            else if ( reloadEnable.equalsIgnoreCase("false") ) {
				this.reloadEnabled = false;
				reloadEnabledSet = true;
            }
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for reloadEnabled = "+ reloadEnable);
                }
            }
        }
        
        String strReloadInterval = jspParams.getProperty("reloadInterval");
        if ( strReloadInterval != null ) {
            try {
                reloadInterval = Long.valueOf(strReloadInterval).longValue() * 1000;
            }
            catch (NumberFormatException e) {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for reloadInterval = "+ reloadInterval);
                }
            }
        }
        
        String trackDep = jspParams.getProperty("trackDependencies");
        if ( trackDep != null ) {
            if ( trackDep.equalsIgnoreCase("true") )
                this.trackDependencies = true;
            else if ( trackDep.equalsIgnoreCase("false") )
                this.trackDependencies = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for trackDependencies = "+ trackDep);
                }
            }
        }


        String useImpTLibs = jspParams.getProperty("useImplicitTagLibs");
        if (useImpTLibs != null)
        {
            if (useImpTLibs.equalsIgnoreCase("true"))
                this.useImplicitTagLibs = true;
            else if (useImpTLibs.equalsIgnoreCase("false"))
                this.useImplicitTagLibs = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useImplicitTagLibs = "+ useImpTLibs);
                }
            }
        }

        String usePagePool = jspParams.getProperty("usePageTagPool");
        if (usePagePool != null)
        {
            if (usePagePool.equalsIgnoreCase("true"))
                this.usePageTagPool = true;
            else if (usePagePool.equalsIgnoreCase("false"))
                this.usePageTagPool = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for usePageTagPool = "+ usePagePool);
                }
            }
        }

        String useThreadPool = jspParams.getProperty("useThreadTagPool");
        if (useThreadPool != null)
        {
            if (useThreadPool.equalsIgnoreCase("true"))
                this.useThreadTagPool = true;
            else if (useThreadPool.equalsIgnoreCase("false"))
                this.useThreadTagPool = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useThreadTagPool = "+ useThreadPool);
                }
            }
        }

        String verbo = jspParams.getProperty("verbose");
        if (verbo != null) {
            if (verbo.equalsIgnoreCase("true"))
                this.verbose = true;
            else if (verbo.equalsIgnoreCase("false"))
                this.verbose = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for verbose = "+ verbo);
                }
            }
        }

		String useFullPackages = jspParams.getProperty("useFullPackageNames");
		if (useFullPackages != null) {
			if (useFullPackages.equalsIgnoreCase("true"))
				this.useFullPackageNames = true;
			else if (useFullPackages.equalsIgnoreCase("false"))
				this.useFullPackageNames = false;
			else {
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
					logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useFullPackageNames = "+ useFullPackages);
				}
			}
		}
		
		//PK26741
		String useRepeatIntType = jspParams.getProperty("useRepeatInt");
		if (useRepeatIntType != null) {
			if (useRepeatIntType.equalsIgnoreCase("true"))
				this.useRepeatInt = true;
			else if (useRepeatIntType.equalsIgnoreCase("false"))
				this.useRepeatInt = false;
			else {
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
					logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useRepeatInt = "+ useRepeatIntType);
				}
			}
		}
        //PK26741

        String dir = jspParams.getProperty("scratchdir");
        if (dir != null){
            outputDir = new File(dir);
            boolean exists = true;
            if(outputDir.exists() == false){
            	exists = outputDir.mkdirs();
            }
            if(!exists || (!(outputDir.canRead() && outputDir.canWrite()))){
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
					logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for scratchdir = "+ outputDir + " exists = " + outputDir.exists()+ " canRead = " + outputDir.canRead() +" canWrite = " + outputDir.canWrite() + ": defaulting to server temp directory");
				}
            	outputDir = null;	// will defer to default scratch dir.
            }
        }
		//PK34989
		String useCDataTrimType = jspParams.getProperty("useCDataTrim");
		if (useCDataTrimType != null) {
			if (useCDataTrimType.equalsIgnoreCase("true"))
				this.useCDataTrim = true;
			else if (useCDataTrimType.equalsIgnoreCase("false"))
				this.useCDataTrim = false;
			else {
				if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
					logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for useCDataTrim = "+ useCDataTrimType);
				}
			}
		}
        //PK34989
        
        //PK47738
        String disableURLEncodingForParamTagType = jspParams.getProperty("disableURLEncodingForParamTag");
        if (disableURLEncodingForParamTagType != null) {
            if (disableURLEncodingForParamTagType.equalsIgnoreCase("true"))
                this.disableURLEncodingForParamTag = true;
            else if (disableURLEncodingForParamTagType.equalsIgnoreCase("false"))
                this.disableURLEncodingForParamTag = false;
            else {
                if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for disableURLEncodingForParamTag = "+ disableURLEncodingForParamTagType);
                }
            }
        }
        //PK47738

        //PK53233 start
        String evalQuotedAndEscapedExpression = jspParams.getProperty("evalQuotedAndEscapedExpression");
        if (evalQuotedAndEscapedExpression != null) {
            if (evalQuotedAndEscapedExpression.equalsIgnoreCase("true"))
                this.evalQuotedAndEscapedExpression = true;
            else if (evalQuotedAndEscapedExpression.equalsIgnoreCase("false"))
                this.evalQuotedAndEscapedExpression = false;
            else {
                if(logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for evalQuotedAndEscapedExpression = "+ evalQuotedAndEscapedExpression);
                }
            }
        }//PK53233 end
        
        //PK62809 start
        // - The allowNullParentInTagFile is for safety...in case a tag needs to have setParent(null)..we can't think of why though.
        // - The allowNullParentInTagFile is not documented and we should leave it that way unless we run in to a problem..
        String allowNullParentInTagFile = jspParams.getProperty("allowNullParentInTagFile");
        if (allowNullParentInTagFile != null) {
            if (allowNullParentInTagFile.equalsIgnoreCase("true"))
                this.allowNullParentInTagFile = true;
            else if (allowNullParentInTagFile.equalsIgnoreCase("false"))
                this.allowNullParentInTagFile = false;
            else {
                if(logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for allowNullParentInTagFile = "+ allowNullParentInTagFile);
                }
            }
        }//PK62809 end
        
        //PK57873
        String convertAttrValueToString = jspParams.getProperty("convertAttrValueToString");
        if (convertAttrValueToString != null) {
             if (convertAttrValueToString.equalsIgnoreCase("true"))
                  this.convertAttrValueToString = true;
             else if (convertAttrValueToString.equalsIgnoreCase("false"))
                  this.convertAttrValueToString = false;
             else {
                  if(logger.isLoggable(Level.INFO)){
                       logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for convertAttrValueToString = "+ convertAttrValueToString);
                  }
             }
        }
        //PK57873
        
        String ieClassId = jspParams.getProperty("ieClassId");        
        if (ieClassId != null)
            this.ieClassId = ieClassId;

        String extendedDocumentRoot = jspParams.getProperty("extendedDocumentRoot");
        if (extendedDocumentRoot != null)
            this.extendedDocumentRoot = extendedDocumentRoot;

        String preFragmentExtendedDocumentRoot = jspParams.getProperty("preFragmentExtendedDocumentRoot");
        if (preFragmentExtendedDocumentRoot != null)
            this.preFragmentExtendedDocumentRoot = preFragmentExtendedDocumentRoot;

        String translationContextClass = jspParams.getProperty("translationContextClass");
        if (translationContextClass != null)
            this.translationContextClass = translationContextClass;
            
        String extensionProcessorClass = jspParams.getProperty("extensionProcessorClass");
        if (extensionProcessorClass != null)
            this.extensionProcessorClass = extensionProcessorClass;
            
        String jspCompileClasspath = jspParams.getProperty("jspCompileClasspath");
        if (jspCompileClasspath != null)
            this.jspCompileClasspath = jspCompileClasspath;
        
        
        Boolean useAutoResponseEncoding = (Boolean)jspParams.get("autoResponseEncoding");
        if (useAutoResponseEncoding != null)
        {
        	this.autoResponseEncoding = useAutoResponseEncoding.booleanValue();
        }
        
        //@blb pretouch begin
        String prepareJSPs = jspParams.getProperty("prepareJSPs");
        if (prepareJSPs != null) {
            prepareJSPsSet = true;
            //check for the integer value
            int temp;
            try {
                temp = Integer.parseInt(prepareJSPs);
                this.prepareJSPs = temp;
            } catch (java.lang.NumberFormatException nfex) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)) {
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions",  "Invalid value for prepareJSPs = " + prepareJSPs + ". Will use value ["+Constants.PREPARE_JSPS_DEFAULT_MINLENGTH+"]");
                }
            }
        }

        String prepareJSPsClassload = jspParams.getProperty("prepareJSPClassload");
        if (prepareJSPsClassload != null) {
            //check for the right values..true need to be changed
            if (prepareJSPsClassload.equalsIgnoreCase("CHANGED")) {
                this.prepareJSPsClassloadChanged = prepareJSPsClassload;
            } else {
                try {
                    int temp = Integer.parseInt(prepareJSPsClassload);
                    if (temp < 0)
                        temp = 0;
                    this.prepareJSPsClassload = temp;
                } catch (java.lang.NumberFormatException nfex) {
                    if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)) {
                        logger.logp(Level.INFO, CLASS_NAME, "populateOptions",
                                    "Invalid value for prepareJSPsClassload = "+ prepareJSPsClassload
                                    + ". Will use value ["+Constants.PREPARE_JSPS_DEFAULT_STARTAT+"]");
                    }
                }
            }
        }
        String prepareJSPThreadCount = jspParams.getProperty("prepareJSPThreadCount");
        if (prepareJSPThreadCount != null) {
            //check for the right values..true need to be changed
            try {
                int temp = Integer.parseInt(prepareJSPThreadCount);
                this.prepareJSPThreadCount =temp;
            } catch (java.lang.NumberFormatException nfex) {
                if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.INFO)) {
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions",
                                "Invalid value for prepareJSPThreadCount = " + prepareJSPThreadCount
                                + ". Will use value ["+Constants.PREPARE_JSPS_DEFAULT_THREADS+"]");
                }
            }
        }
        //@blb pretouch end

        //PK53703 - start
        String convertExp = jspParams.getProperty("convertExpression");
        if (convertExp != null) {
            if (convertExp.equalsIgnoreCase("true"))
                this.convertExpression = true;
            else if (convertExp.equalsIgnoreCase("false"))
                this.convertExpression = false;
            else {
                if(logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for convertExpression = "+ convertExp);
                }
            }
        }    
        //P53703 - end    
        
        //PK65013 - start
        String modifyPageContextVar = jspParams.getProperty("modifyPageContextVariable");
        if (modifyPageContextVar != null) {
            if (modifyPageContextVar.equalsIgnoreCase("true"))
                this.modifyPageContextVariable = true;
            else if (modifyPageContextVar.equalsIgnoreCase("false"))
                this.modifyPageContextVariable = false;
            else {
                if(logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for modifyPageContextVariable = "+ modifyPageContextVar);
                }
            }
        }
        //PK65013 - end
        
        //PK69220 - start
        String disTldSearch = jspParams.getProperty("disableTldSearch");
        if (disTldSearch != null) {
            if (disTldSearch.equalsIgnoreCase("true"))
                this.disableTldSearch = true;
            else if (disTldSearch.equalsIgnoreCase("false"))
                this.disableTldSearch = false;
            else {
                if(logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for disableTldSearch = "+ disTldSearch);
                }
            }
        }
        //PK69220 - end

        //PK72039 start
        String compileAF = jspParams.getProperty("compileAfterFailure");
        if (compileAF != null) {
            if (compileAF.equalsIgnoreCase("true"))
                this.compileAfterFailure = true;
            else if (compileAF.equalsIgnoreCase("false"))
                this.compileAfterFailure = false;
            else {
                if(logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for compileAfterFailure = "+ compileAF);
                }
            }
        }
        //PK72039 end

        //PM06063 start
        String disableRI = jspParams.getProperty("disableResourceInjection");
        if (disableRI != null){
        	if (disableRI.equalsIgnoreCase("true"))
        		this.disableResourceInjection = true;
        	else if (disableRI.equalsIgnoreCase("false"))
        		this.disableResourceInjection = false;
        	else {
        		if (logger.isLoggable(Level.INFO)){
        			logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for disableResourceInjection = "+ disableRI);
        		}
        	}
        }
        //PM06063 end
        
        //PM21395 start
        String enableDQDec = jspParams.getProperty("enableDoubleQuotesDecoding");
        if (enableDQDec != null){
            if (enableDQDec.equalsIgnoreCase("true"))
                this.enableDoubleQuotesDecoding = true;
            else if (enableDQDec.equalsIgnoreCase("false"))
                this.enableDoubleQuotesDecoding = false;
            else {
                if (logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for enableDoubleQuotesDecoding = "+ enableDQDec);
                }
            }
        }
        //PM21395 end
        String enableCDIWrap = jspParams.getProperty("enableCDIWrapper");
        if (enableCDIWrap != null){
            if (enableCDIWrap.equalsIgnoreCase("true"))
                this.enableCDIWrapper = true;
            else if (enableCDIWrap.equalsIgnoreCase("false"))
                this.enableCDIWrapper = false;
            else {
                if (logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for enableCDIWrapper = "+ enableCDIWrap);
                }
            }
        }

        
        //655818 start
        String reusePropGroupConfigOnInclude = jspParams.getProperty("reusePropertyGroupConfigOnInclude");
        if (reusePropGroupConfigOnInclude != null){
            if (reusePropGroupConfigOnInclude.equalsIgnoreCase("true"))
                this.reusePropertyGroupConfigOnInclude = true;
            else if (reusePropGroupConfigOnInclude.equalsIgnoreCase("false"))
                this.reusePropertyGroupConfigOnInclude = false;
            else {
                if (logger.isLoggable(Level.INFO)){
                    logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for reusePropertyGroupConfigOnInclude = "+ reusePropGroupConfigOnInclude);
                }
            }
        }
        //655818 end

        //PM41476 start
        String removeXmlns = jspParams.getProperty("removeXmlnsFromOutput");
        if (removeXmlns != null){
        	if (removeXmlns.equalsIgnoreCase("true"))
        		this.removeXmlnsFromOutput = true;
        	else if (removeXmlns.equalsIgnoreCase("false"))
        		this.removeXmlnsFromOutput = false;
        	else {
        		if (logger.isLoggable(Level.INFO)){
        			logger.logp(Level.INFO, CLASS_NAME, "populateOptions", "Invalid value for removeXmlnsFromOutput = "+ removeXmlns);
        		}
        	}
        }
        //PM41476 end


    	/*---------------------*/
    	/*      Fix-Ups        */
    	/*---------------------*/
    	
        // turn off track dependencies if reload enabled was specifically set
        if (isReloadEnabledSet() && !isReloadEnabled() && isTrackDependencies()) {
            setTrackDependencies(false);
        }
	
        // Development mode turns on several options
        if (this.useDevModeSet && this.useDevMode) {
        	setUseDevMode(true);
        }
	}

	
	/*---------------------*/
	/* Getters and Setters */
	/*---------------------*/
	
    public void setKeepGenerated(boolean keepGenerated) {
        this.keepGenerated = keepGenerated;
    }
    
    public boolean isKeepGenerated() {
        return keepGenerated;
    }

    //PK20187
	public void setUseStringCast(boolean b) {
		useStringCast = b;
	}
	public boolean isUseStringCast() {
		return useStringCast;
	}
    //PK20187

    public boolean isUsePageTagPool() {
        return usePageTagPool;
    }

    public void setUsePageTagPool(boolean usePageTagPool) {
        this.usePageTagPool = usePageTagPool;
    }
    
    public void setUseThreadTagPool(boolean useThreadTagPool) {
        this.useThreadTagPool = useThreadTagPool;
    }

    public boolean isUseThreadTagPool() {
        return useThreadTagPool;
    }

    public boolean isUseJDKCompiler() {
        return useJDKCompiler;
    }

    public void setUseJDKCompiler(boolean useJKDCompiler) {
        this.useJDKCompiler = useJKDCompiler;
    }
    
    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public File getOutputDir() {
        return (outputDir);
    }
    
    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    //PK29373
	public boolean isUseScriptVarDupInit() {
		return useScriptVarDupInit;
	}

	public void setUseScriptVarDupInit(boolean b) {
		useScriptVarDupInit = b;
	}
	//PK29373

    public void setClassDebugInfo(boolean classDebugInfo) {
        this.classDebugInfo = classDebugInfo;
    }
    
    //PK57873
    public boolean isConvertAttrValueToString() {
         return convertAttrValueToString;
    }

    public void setConvertAttrValueToString(boolean convert) {
         this.convertAttrValueToString = convert;
    }
    //End PK57873
    
    public boolean isClassDebugInfo() {
        return classDebugInfo;
    }

    public void setDeprecation(boolean deprecation) {
        this.deprecation = deprecation;
    }

    public boolean isDeprecation() {
        return deprecation;
    }
    
	/**
	 * @deprecated
	 *
	 * isCompileWithAssert is replaced by {@link #getJdkSourceLevel}
	 */
    public boolean isCompileWithAssert() {
        return compileWithAssert;
    }

	/**
	 * @deprecated
	 *
	 * setCompileWithAssert is replaced by {@link #setJdkSourceLevel}
	 */
    public void setCompileWithAssert(boolean compileWithAssert) {
        this.compileWithAssert = compileWithAssert;
    }

    public void setJdkSourceLevel(String jdkSourceLevel) {
        if (jdkSourceLevel.equals("17")) {
            if (JspOptions.isJavaVersionAtLeast17()) {
                //if 17 is only supported by the JDK compiler, set the useJDKCompiler to true
                //this.useJDKCompiler=true;
                //this.useJikes=false;
            } else {
                jdkSourceLevel="16";
            }
        }
        this.jdkSourceLevel = jdkSourceLevel;
    }

    public String getJdkSourceLevel() {
        return jdkSourceLevel;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public boolean isVerbose() {
        return verbose;
    }

	public void setIsZOS(boolean isZOS) {
		this.isZOS = isZOS;
	}
    
    public boolean isZOS(){
		return isZOS;
	}


    public boolean isTrackDependencies() {
        return trackDependencies;
    }

    public void setTrackDependencies(boolean trackDependencies) {
        this.trackDependencies = trackDependencies;
    }

    public void setDisableJspRuntimeCompilation(boolean disableJspRuntimeCompilation) {
        this.disableJspRuntimeCompilation = disableJspRuntimeCompilation;
    }

    public boolean isDisableJspRuntimeCompilation() {
        return disableJspRuntimeCompilation;
    }

    public void setReloadEnabled(boolean reloadEnabled) {
        this.reloadEnabled = reloadEnabled;
        this.reloadEnabledSet = true;
        if (isReloadEnabledSet() && !isReloadEnabled() && isTrackDependencies()) {
            setTrackDependencies(false);
        }
    }

    public boolean isReloadEnabled() {
        return reloadEnabled;
    }

    public boolean isUseImplicitTagLibs() {
        return useImplicitTagLibs;
    }

    public void setUseImplicitTagLibs(boolean useImplicitTagLibs) {
        this.useImplicitTagLibs = useImplicitTagLibs;
    }
    

    public boolean isUseOptimizedTags() {
        return useOptimizedTags;
    }

    public void setUseOptimizedTags(boolean b) {
        useOptimizedTags = b;
    }
    
    public long getReloadInterval() {
        return reloadInterval;
    }

    public void setReloadInterval(long l) {
        reloadInterval = l;
    }
    
    public Map getLooseLibMap() {
        return looseLibMap;
    }

    public void setLooseLibMap(Map map) {
        looseLibMap = map;
    }

    public boolean isKeepGeneratedclassfiles() {
        return keepGeneratedclassfiles;
    }

    public void setKeepGeneratedclassfiles(boolean b) {
        keepGeneratedclassfiles= b;
    }

    public boolean isUseJikes() {
        return useJikes;
    }

    public void setUseJikes(boolean b) {
        useJikes = b;
    }

	public boolean isUseFullPackageNames() {
		return useFullPackageNames;
	}

	public void setUseFullPackageNames(boolean b) {
		useFullPackageNames = b;
	}

	//PK26741
	public boolean isUseRepeatInt() {
		return useRepeatInt;
	}

	public void setUseRepeatInt(boolean b) {
		useRepeatInt = b;
	}
	//PK26741

    //393421
    public boolean isAllowJspOutputElementMismatch() {
        return allowJspOutputElementMismatch;
    }

    public void setAllowJspOutputElementMismatch(boolean b) {
        allowJspOutputElementMismatch = b;
    }
    //  393421

    //396002
    public boolean isAllowTaglibPrefixRedefinition() {
        return allowTaglibPrefixRedefinition;
    }

    public void setAllowTaglibPrefixRedefinition(boolean b) {
    	allowTaglibPrefixRedefinition = b;
    }
    //  396002

    // 396002
    public boolean isAllowTaglibPrefixUseBeforeDefinition() {
        return allowTaglibPrefixUseBeforeDefinition;
    }

    public void setAllowTaglibPrefixUseBeforeDefinition(boolean b) {
    	allowTaglibPrefixUseBeforeDefinition = b;
    }
    //  396002

    //PK30879
	public boolean isAllowUnmatchedEndTag() {
		return allowUnmatchedEndTag ;
	}

    public void setAllowUnmatchedEndTag(boolean b) {
    	allowUnmatchedEndTag = b;
    }
    //  PK30879

	//PK31135
	public boolean isUseIterationEval() {
		return useIterationEval;
	}

	public void setUseIterationEval(boolean b) {
		useIterationEval = b;
	}
	//PK31135

	//LIDB4293-2
	public boolean isUseInMemory() {
		return useInMemory;
	}

	public void setUseInMemory(boolean b) {
		useInMemory = b;
        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
            logger.logp(Level.FINEST, CLASS_NAME,"setUseInMemory", "getUseInMemory() [" + isUseInMemory()+"]");
        }
	}
	//LIDB4293-2

	//LIDB4293-2
	public boolean isUseDevMode() {
		return useDevMode;
	}

	public void setUseDevMode(boolean b) {
        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
            logger.logp(Level.FINEST, CLASS_NAME,"setUseDevMode", "arg [" + b+"]");
        }
		useDevMode = b;
		if (b) {
			setUseDevModeSet(true);
	    	setReloadEnabled(true);
	    	setReloadInterval(1);
	    	setUseInMemory(true);
	    	setTrackDependencies(true);
	    	setDisableJspRuntimeCompilation(false);
		}
        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINEST)){
            logger.logp(Level.FINEST, CLASS_NAME,"setUseDevMode", "getUseInMemory() [" + isUseInMemory()+"]");
        }
	}
	//LIDB4293-2

	//LIDB4293-2
	public boolean isUseDevModeSet() {
		return useDevModeSet;
	}

	public void setUseDevModeSet(boolean b) {
		useDevModeSet=b;
	}
	//LIDB4293-2

    //defect 400645
	public String getOverriddenJspOptions() {
		return overriddenJspOptions ;
	}

    public void setOverriddenJspOptions(String s) {
    	overriddenJspOptions = s;
    }
    //defect 400645

    public String getExtendedDocumentRoot() {
        return extendedDocumentRoot;
    }

    public void setExtendedDocumentRoot(String docRoot) {
        this.extendedDocumentRoot = docRoot;
    }
    
    public String getPreFragmentExtendedDocumentRoot() {
        return preFragmentExtendedDocumentRoot;
    }

    public void setPreFragmentExtendedDocumentRoot(String docRoot) {
        this.preFragmentExtendedDocumentRoot = docRoot;
    }

    public String getTranslationContextClass() {
        return translationContextClass;
    }

    public void setTranslationContextClass(String translationContextClass) {
        this.translationContextClass = translationContextClass;
    }

    public String getExtensionProcessorClass() {
        return extensionProcessorClass;
    }

    public void setExtensionProcessorClass(String extensionProcessorClass) {
        this.extensionProcessorClass = extensionProcessorClass;
    }

    public String getJspCompileClasspath() {
        return jspCompileClasspath;
    }

    public void setJspCompileClasspath(String jspCompileClasspath) {
        this.jspCompileClasspath = jspCompileClasspath;
    }

    public String getJavaEncoding() {
        return javaEncoding;
    }

    public void setJavaEncoding(String javaEncoding) {
        this.javaEncoding = javaEncoding;
    }

	public boolean isServlet2_2() {
		return servlet2_2;
	}

	public void setServlet2_2(boolean servlet2_2) {
		this.servlet2_2 = servlet2_2;
	}

	public boolean isServletEngineReloadEnabled() {
		return servletEngineReloadEnabled;
	}

	public void setServletEngineReloadEnabled(boolean servletEngineReloadEnabled) {
		this.servletEngineReloadEnabled = servletEngineReloadEnabled;
	}

	public boolean isReloadEnabledSet() {
		return reloadEnabledSet;
	}

	public void setReloadEnabledSet(boolean reloadEnabledSet) {
		this.reloadEnabledSet = reloadEnabledSet;
	}

	public long getServletEngineReloadInterval() {
		return servletEngineReloadInterval;
	}

	public void setServletEngineReloadInterval(long servletEngineReloadInterval) {
		this.servletEngineReloadInterval = servletEngineReloadInterval;
	}

	public boolean isAutoResponseEncoding() {
		return autoResponseEncoding;
	}

	public void setAutoResponseEncoding(boolean autoResponseEncoding) {
		this.autoResponseEncoding = autoResponseEncoding;
	}
	
    public int getPrepareJSPs() {
        return prepareJSPs;
    }

    public boolean isPrepareJSPsSet() {
        return prepareJSPsSet;
    }

    public String getPrepareJSPsClassloadChanged() {
        return prepareJSPsClassloadChanged;
    }

    public int getPrepareJSPsClassload() {
        return prepareJSPsClassload;
    }

    public int getPrepareJSPThreadCount() {
        return prepareJSPThreadCount;
    }
    
    //Start changes Defect PK28029
    public String getIeClassId() {
    	return ieClassId;
    }

    //PK53703 start
    public boolean isConvertExpression() {
        return convertExpression;
    }

    public void setConvertExpression(boolean convertExpression) {
    this.convertExpression = convertExpression;
    }
    //PK53703 - end    

    //End changes Defect PK28029
	//PK34989
	public boolean isUseCDataTrim() {
		return useCDataTrim;
	}

	public void setUseCDataTrim(boolean b) {
		useCDataTrim = b;
	}
	//PK34989
    
    //PK47738
    public boolean isDisableURLEncodingForParamTag() {
        return disableURLEncodingForParamTag;
    }

    public void setDisableURLEncodingForParamTag(boolean b) {
        disableURLEncodingForParamTag = b;
    }
    //PK47738

    //PK53233 start
    public boolean isEvalQuotedAndEscapedExpression() {
        return evalQuotedAndEscapedExpression;
    }

    public void setEvalQuotedAndEscapedExpression(boolean evalQuotedAndEscapedExpression) {
        this.evalQuotedAndEscapedExpression = evalQuotedAndEscapedExpression;
    }
    //PK53233 end
    
    //PK62809 start
    // - The allowNullParentInTagFile is for safety...in case a tag needs to have setParent(null)..we can't think of why though.
    // - The allowNullParentInTagFile is not documented and we should leave it that way unless we run in to a problem..
    public boolean isAllowNullParentInTagFile() {
        return allowNullParentInTagFile;
    }

    public void setAllowNullParentInTagFile(boolean allowNullParentInTagFile) {
        this.allowNullParentInTagFile = allowNullParentInTagFile;
    }
    //PK62809 end
    
    //PK65013 - start
    public boolean isModifyPageContextVariable() {
        return modifyPageContextVariable;
    }

    public void setModifyPageContextVariable(boolean modifyPageContextVar) {
        this.modifyPageContextVariable = modifyPageContextVar;
    }
    //PK65013 - end
    
    //PK69220 - start
    public boolean isDisableTldSearch() {
        return disableTldSearch;
    }

    public void setDisableTldSearch(boolean disTldSearch) {
        this.disableTldSearch = disTldSearch;
    }
    //PK69220 - end

    //PK72039 start
    public boolean isCompileAfterFailure() {
	return compileAfterFailure;
    }

    public void setCompileAfterFailure(boolean b) {
       	this.compileAfterFailure = b;
    }
    //PK72039 end

    //PM06063 start
    public boolean isDisableResourceInjection(){
    	return disableResourceInjection;
    }
    
    public void setDisableResourceInjection(boolean disRI){
    	this.disableResourceInjection = disRI;
    }
    //PM06063 end
    
    //PM21395 start
    public boolean isEnableDoubleQuotesDecoding(){
        return enableDoubleQuotesDecoding;
    }
    
    public void setEnableDoubleQuotesDecoding(boolean temp){
        this.enableDoubleQuotesDecoding = temp;
    }
    //PM21395 end

    public boolean isEnableCDIWrapper(){
        return enableCDIWrapper;
    }
    
    public void setEnableCDIWrapper(boolean temp){
        this.enableCDIWrapper = temp;
    }
    
    //655818 start
    public boolean isReusePropertyGroupConfigOnInclude(){
        return reusePropertyGroupConfigOnInclude;
    }
    
    public void setReusePropertyGroupConfigOnInclude(boolean reusePropGroupConfigOnInclude){
        this.reusePropertyGroupConfigOnInclude = reusePropGroupConfigOnInclude;
    }
    //655818 end

    //PM41476 start
    public boolean isRemoveXmlnsFromOutput(){
    	return removeXmlnsFromOutput;
    }
    
    public void setRemoveXmlnsFromOutput(boolean temp){
    	this.removeXmlnsFromOutput = temp;
    }
    //PM41476 end

    public String toString() {	//overrride Object's toString to assist in debugging.
    	String separatorString = System.getProperty("line.separator");
    	// defect 204907 start
    	String newOutputDir = null; 
    	if (outputDir!=null) 
    		newOutputDir = outputDir.toString();
    	if (newOutputDir!=null) {
			newOutputDir = outputDir.toString().replace('\\','/');
    	}
		// defect 204907 end
    	
    	return new String (""+separatorString+
                "allowJspOutputElementMismatch =       [" + allowJspOutputElementMismatch +"]"+separatorString+
                "allowTaglibPrefixRedefinition =       [" + allowTaglibPrefixRedefinition +"]"+separatorString+
                "allowTaglibPrefixUseBeforeDefinition =[" + allowTaglibPrefixUseBeforeDefinition +"]"+separatorString+
                "allowUnmatchedEndTag  =               [" + allowUnmatchedEndTag  +"]"+separatorString+
                "autoResponseEncoding =                [" + autoResponseEncoding +"]"+separatorString+
                "classDebugInfo =                      [" + classDebugInfo +"]"+separatorString+
                "compileWithAssert =                   [" + compileWithAssert +"]"+separatorString+
                "convertExpression =                   [" + convertExpression +"]"+separatorString+
                "debugEnabled =                        [" + debugEnabled +"]"+separatorString+
                "deprecation =                         [" + deprecation +"]"+separatorString+
                "disableJspRuntimeCompilation =        [" + disableJspRuntimeCompilation +"]"+separatorString+
                "evalQuotedAndEscapedExpression =       [" + evalQuotedAndEscapedExpression +"]"+separatorString+
                "extendedDocumentRoot =                [" + extendedDocumentRoot +"]"+separatorString+
                "extensionProcessorClass =             [" + extensionProcessorClass +"]"+separatorString+
                "ieClassId =                           [" + ieClassId +"]"+separatorString+
                "isZOS =                               [" + isZOS +"]"+separatorString+
                "javaEncoding =                        [" + javaEncoding +"]"+separatorString+
                "jdkSourceLevel =                      [" + jdkSourceLevel +"]"+separatorString+
                "jspCompileClasspath =                 [" + jspCompileClasspath +"]"+separatorString+
                "keepGenerated =                       [" + keepGenerated +"]"+separatorString+
                "keepGeneratedclassfiles =             [" + keepGeneratedclassfiles +"]"+separatorString+
                "looseLibMap =                         [" + looseLibMap +"]"+separatorString+
                "outputDir =                           [" + newOutputDir +"]"+separatorString+
                "prepareJSPs =                         [" + prepareJSPs +"]"+separatorString+
                "prepareJSPsSet =                      [" + prepareJSPsSet +"]"+separatorString+
                "prepareJSPsClassloadChanged =         [" + prepareJSPsClassloadChanged +"]"+separatorString+
                "prepareJSPsClassload =                [" + prepareJSPsClassload +"]"+separatorString+
                "prepareJSPThreadCount =               [" + prepareJSPThreadCount +"]"+separatorString+
                "reloadEnabled =                       [" + reloadEnabled +"]"+separatorString+
                "reloadEnabledSet =                    [" + reloadEnabledSet +"]"+separatorString+
                "reloadInterval =                      [" + reloadInterval +"]"+separatorString+
                "trackDependencies =                   [" + trackDependencies +"]"+separatorString+
                "translationContextClass =             [" + translationContextClass +"]"+separatorString+
    		"useCDataTrim =                        [" + useCDataTrim +"]"+separatorString+
                "useDevMode =                          [" + useDevMode +"]"+separatorString+
                "useDevModeSet =                       [" + useDevModeSet +"]"+separatorString+
                "useFullPackageNames =                 [" + useFullPackageNames +"]"+separatorString+
                "useImplicitTagLibs =                  [" + useImplicitTagLibs +"]"+separatorString+
                "useInMemory =                         [" + useInMemory +"]"+separatorString+
                "useIterationEval =                    [" + useIterationEval +"]"+separatorString+
                "useJDKCompiler =                      [" + useJDKCompiler +"]"+separatorString+
                "useJikes =                            [" + useJikes +"]"+separatorString+
                "usePageTagPool =                      [" + usePageTagPool +"]"+separatorString+
                "useRepeatInt =                        [" + useRepeatInt +"]"+separatorString+
                "useScriptVarDupInit =                 [" + useScriptVarDupInit +"]"+separatorString+
                "useStringCast =                       [" + useStringCast +"]"+separatorString+
                "useThreadTagPool =                    [" + useThreadTagPool +"]"+separatorString+
                "verbose =                             [" + verbose +"]"+separatorString+
    		"overridden JSP options =              [" + separatorString+overriddenJspOptions +"]"+separatorString+
                "modifyPageContextVariable =           [" + modifyPageContextVariable +"]"+separatorString+
                "disableTldSearch =                    [" + disableTldSearch +"]"+separatorString+
                "compileAfterFailure =                 [" + compileAfterFailure +"]"+separatorString+
                "disableResourceInjection =            [" + disableResourceInjection +"]"+separatorString+
                "enableDoubleQuotesDecoding =          [" + enableDoubleQuotesDecoding +"]"+separatorString+
                "enableCDIWrapper =                    [" + enableCDIWrapper +"]"+separatorString+
                "removeXmlnsFromOutput =               [" + removeXmlnsFromOutput +"]"+separatorString+
    	"");
    }

    public boolean isRecompileJspOnRestart() {
        return recompileJspOnRestart;
    }

    public void setRecompileJspOnRestart(boolean recompileJspOnRestart) {
        this.recompileJspOnRestart = recompileJspOnRestart;
    }

    public static boolean isJavaVersionAtLeast17() {
        if (javaVersionAtLeast17!=null) {
            return javaVersionAtLeast17.booleanValue();
        } else {
            synchronized(tmpLockObject) {
                if (javaVersionAtLeast17!=null) { //doesn't matter if we need to check again, as it will reproduce the same result
                    return javaVersionAtLeast17.booleanValue();
                }
                boolean returnValue=true;
                String compareTo = "17";
                int compareValue = 17;
                int compareLength = compareTo.length();
                
                String s = System.getProperty("java.version");
                try {
                    String characterIndex = "_";
                    if (s.indexOf(characterIndex)>-1) {
                        s = s.substring(0, s.indexOf(characterIndex));
                    }
                    characterIndex = "-";
                    if (s.indexOf(characterIndex)>-1) {
                        s = s.substring(0, s.indexOf(characterIndex));
                    }
                    StringTokenizer st = new StringTokenizer(s, ".");
                    String token;
                    int version = 0;
                    int count = 0;
                    while (st.hasMoreTokens() && count<compareLength) {
                        token = st.nextToken();
                        version=10*version+Integer.parseInt(token);
                        count++;
                    }
                    
                    if (version<compareValue) {
                        logger.logp(Level.WARNING, CLASS_NAME, "isJavaVersionAtLeast17", "jsp.jdk.not.at.17");
                        returnValue = false;
                    } else {
                        if(com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.FINE)){
                            logger.logp(Level.FINE, CLASS_NAME, "checkJaveVersionAgainst17", "Checking java version against 17 returns "+ returnValue);
                        }
                    }
                } catch (Exception e) {
                    //invalid java version - log exception and return true;
                    //assume it works
                    logger.logp(Level.WARNING, CLASS_NAME, "checkJaveVersionAgainst17", "jsp.jdk.version.exception", e);
                    //going to return true
                }
                javaVersionAtLeast17 = Boolean.valueOf(returnValue);
                return returnValue;
            }
        }
    }
    
}
