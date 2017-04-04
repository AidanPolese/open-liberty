// IBM Confidential OCO Source Material
// 5639-D57,5630-A36,5630-A37,5724-D18 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//        PK57843        03/12/08     srpeters            THE JSP:INCLUDE TAG DOES NOT THROW EXCEPTION WHEN THE FILE IT
//        PK76117        12/11/08     mmulholl            Add com.ibm.ws.webcontainer.discernunavailableservlet
//        PK76656        12/11/08     mmulholl            Add com.ibm.ws.webcontainer.assumefiltersuccessonsecurityerror
//        PK75617        02/11/09     pmdinh              Add com.ibm.ws.webcontainer.ignoreinvalidquerystring
//		  PK78371		 01/13/09     pmdinh              Add com.ibm.ws.webcontainer.provideqstringtowelcomefile
//		  PK80362      	 03/04/09 	  anupag          	  Add com.ibm.ws.webcontainer.suppressheadersinrequest property
//        PK80340        02/25/09     mmulholl            Add com.ibm.ws.webcontainer.enabledefaultservletrequestpathelements
//        PK81452        05/18/09     mmolden             Add com.ibm.ws.webcontainer.copyattributeskeyset
//        PK83258        06/17/09     mmulholl            Add com.ibm.ws.webcontainer.defaulttracerequestbehavior & com.ibm.ws.webcontainer.defaultheadrequestbehavior
//        PK90190        06/21/09     sartoris            Add com.ibm.ws.jsp.getwriteronemptybuffer
//		  PK95332        09/09/09     pmdinh              Add com.ibm.ws.jsp.limitbuffer
//        F003449        11/18/09     mmulholl            Add com.ibm.ws.webcontainer.enablemultireadofpostdata
//        PM03788        01/12/10     anupag              Add com.ibm.ws.webcontainer.setunencodedhtmlinsenderror
//        PK99400        01/06/10     pmdinh              Add com.ibm.ws.webcontainer.filewrappereventslessdetail
//        PM03928        01/15/10     pmdinh              Add com.ibm.ws.webcontainer.disablesetcharacterencodingafterparametersread
//        PM22082        09/08/10     pmdinh              Add com.ibm.ws.jsp.allowdirectoryinclude
//        PM22919        09/29/10     pmdinh              Add com.ibm.ws.webcontainer.dispatcherrethrowserror
//        PM21451        03/25/11     mmulholl            Add com.ibm.ws.webcontainer.checkedringetrealpath
//        PM25931        11/12/10     anupag              Add com.ibm.ws.webcontainer.localedependentdateformatter
//        F011107  		 05/18/11	  pmdinh			  Add com.ibm.ws.webcontainer.enableexactmatchjsecuritycheck
//        PM47487        10/24/11     pmdinh              Add com.ibm.ws.webcontainer.returndefaultcontextpath
//        PM47661        11/16/11     pnicoluc            Add com.ibm.ws.jsp.expressionreturnemptystring
// 		  PM51151		 11/28/11	  pmdinh			  Add com.ibm.ws.webcontainer.asyncmaxsizetaskpool and com.ibm.ws.webcontainer.asyncpurgeinterval

//
package com.ibm.wsspi.webcontainer;

import java.util.Properties;

import com.ibm.ws.webcontainer.WebContainer;

/**
 *
 * 
 * WCCustomProperties contains static final strings of all the custom properties in the
 * webcontainer.
 * @ibm-private-in-use
 */

public class WCCustomProperties {
	public static Properties customProps = WebContainer.getWebContainerProperties();
	public static final String DO_NOT_SERVE_BY_CLASSNAME = customProps.getProperty("com.ibm.ws.webcontainer.donotservebyclassname");
	public static final boolean SUPPRESS_WSEP_HEADER = (Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.suppresserrorpageodrheader"))).booleanValue();
	public static final boolean REDIRECT_CONTEXT_ROOT = Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.redirectcontextroot")).booleanValue();
		
    public static final boolean ERROR_EXCEPTION_TYPE_FIRST = Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.enableerrorexceptiontypefirst")).booleanValue(); 
    public static final String PREPEND_SLASH_TO_RESOURCE = customProps.getProperty("prependslashtoresource");
    public static final String SESSION_REWRITE_IDENTIFIER = customProps.getProperty("sessionrewriteidentifier");
    public static final boolean KEEP_CONTENT_LENGTH = (Boolean.valueOf(customProps.getProperty("keepcontentlength")).booleanValue());
    public static final boolean SKIP_HEADER_FLUSH = (Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.skipheaderflush"))).booleanValue();
    public static final String CONTENT_TYPE_COMPATIBILITY = customProps.getProperty("com.ibm.ws.webcontainer.contenttypecompatibility");
    public static final boolean GET_SESSION_24_COMPATIBILITY = (Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.getsession2_4compatibility"))).booleanValue();
    public static final boolean OLD_DATE_FORMATTER = (Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.olddateformatter"))).booleanValue();
    public static final String OPTIMIZE_FILE_SERVING_SIZE_GLOBAL = customProps.getProperty("com.ibm.ws.webcontainer.optimizefileservingsize");
    public static final int SYNC_FILE_SERVING_SIZE_GLOBAL = Integer.valueOf(customProps.getProperty("syncfileservingsize", "-1")).intValue();
    public static final int MAPPED_BYTE_BUFFER_SIZE_GLOBAL = Integer.valueOf(customProps.getProperty("mappedbytebuffersize", "-1")).intValue();
    public static final boolean DISABLE_MULTI_THREAD_CONN_MGMT = Boolean.valueOf(customProps.getProperty("disablemultithreadedservletconnectionmgmt")).booleanValue();
    public static final boolean DECODE_URL_AS_UTF8 =Boolean.valueOf(customProps.getProperty("decodeurlasutf8","true")).booleanValue();
    public static final boolean EXPOSE_WEB_INF_ON_DISPATCH = Boolean.valueOf(customProps.getProperty("exposewebinfondispatch")).booleanValue();
    public static final boolean DIRECTORY_BROWSING_ENABLED = Boolean.valueOf(customProps.getProperty("directorybrowsingenabled")).booleanValue();
    public static final String DISALLOW_ALL_FILE_SERVING = customProps.getProperty("com.ibm.ws.webcontainer.disallowallfileserving");
    public static final boolean FILE_SERVING_ENABLED =Boolean.valueOf(customProps.getProperty("fileservingenabled","true")).booleanValue();
    public static final String DISALLOW_SERVE_SERVLETS_BY_CLASSNAME_PROP =	customProps.getProperty("com.ibm.ws.webcontainer.disallowserveservletsbyclassname");

	public static final boolean SERVE_SERVLETS_BY_CLASSNAME_ENABLED = Boolean.valueOf(customProps.getProperty("serveservletsbyclassnameenabled")).booleanValue(); 		//HEY YOU!!! BETTER NOT MESS WITH THE DEFAULT OF THIS PROPERTY
	public static final boolean REDIRECT_WITH_PATH_INFO = Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.redirectwithpathinfo")).booleanValue();
	public static final boolean REMOVE_TRAILING_SERVLET_PATH_SLASH = (Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.removetrailingservletpathslash"))).booleanValue(); //PK39337
	public static final String LISTENERS = customProps.getProperty("listeners");
	public static final boolean SERVLET_CASE_SENSITIVE = (Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.servletcasesensitive"))).booleanValue();  //PK42055
	public static final String ENABLE_IN_PROCESS_CONNECTIONS = customProps.getProperty("enableinprocessconnections");
    public static final boolean SUPPRESS_SERVLET_EXCEPTION_LOGGING = Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.suppressservletexceptionlogging")).booleanValue();
    
	public static String ERROR_PAGE_COMPATIBILITY = customProps.getProperty("com.ibm.ws.webcontainer.contenttypecompatibility");
	public static boolean MAP_FILTERS_TO_ASTERICK = Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.mapfilterstoasterisk")).booleanValue();
	public static boolean SUPPRESS_HTML_RECURSIVE_ERROR_OUTPUT =Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.suppresshtmlrecursiveerroroutput")).booleanValue();
	public static boolean THROW_MISSING_JSP_EXCEPTION = new Boolean (customProps.getProperty("com.ibm.ws.webcontainer.throwmissingjspexception")).booleanValue();  //PK57843

	//638627 had to change default to true for CTS test case
	//If the default servlet is the target of a RequestDispatch.include() and the requested
	//resource does not exist, then the default servlet MUST throw
	//FileNotFoundException. If the exception isn't caught and handled, and the
	//response hasn’t been committed, the status code MUST be set to 500.
    public static boolean MODIFIED_FNF_BEHAVIOR = new Boolean (WebContainer.getWebContainerProperties().getProperty
		       ("com.ibm.ws.webcontainer.modifiedfilenotfoundexceptionbehavior","true")).booleanValue();  //PK65408 
	public static int SERVLET_DESTROY_WAIT_TIME = Integer.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.servletdestroywaittime", "60")).intValue();
	
	public static final boolean FILE_WRAPPER_EVENTS = Boolean.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.filewrapperevents", "false")).booleanValue();
	
	public static final boolean DISABLE_SYSTEM_APP_GLOBAL_LISTENER_LOADING = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty
            ("com.ibm.ws.webcontainer.disablesystemappgloballistenerloading")).booleanValue();      //PK66137	    
	
	public static final boolean THROW_404_IN_PREFERENCE_TO_403 = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.throw404inpreferenceto403")).booleanValue(); // PK64302
	public static final boolean DISCERN_UNAVAILABLE_SERVLET = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.discernunavailableservlet")).booleanValue(); // PK76117
	public static final boolean ASSUME_FILTER_SUCCESS_ON_SECURITY_ERROR = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.assumefiltersuccessonsecurityerror")).booleanValue(); // PK76117
    public static final boolean IGNORE_INVALID_QUERY_STRING = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.ignoreinvalidquerystring")).booleanValue();			//PK75617
    public static final boolean PROVIDE_QSTRING_TO_WELCOME_FILE = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.provideqstringtowelcomefile", "false")).booleanValue();		//PK78371
	public static String SUPPRESS_HEADERS_IN_REQUEST = customProps.getProperty("com.ibm.ws.webcontainer.suppressheadersinrequest");    //PK80362
	 
	public static final boolean DISPATCHER_RETHROW_SER = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.dispatcherrethrowser","true")).booleanValue(); // PK79464
	public static final boolean ENABLE_DEFAULT_SERVLET_REQUEST_PATH_ELEMENTS = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.enabledefaultservletrequestpathelements")).booleanValue(); // PK80340
	public static final boolean COPY_ATTRIBUTES_KEY_SET = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.copyattributeskeyset")).booleanValue();   //PK81452	
	public static final boolean SUPPRESS_LAST_ZERO_BYTE_PACKAGE = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.suppresslastzerobytepackage")).booleanValue(); // PK82794
	public static final boolean DEFAULT_TRACE_REQUEST_BEHAVIOR = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.defaulttracerequestbehavior")).booleanValue(); // PK83258.2
	public static final boolean DEFAULT_HEAD_REQUEST_BEHAVIOR = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.defaultheadrequestbehavior")).booleanValue(); // PK83258.2
    public static final boolean INVOKE_FILTER_INIT_AT_START_UP = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.invokefilterinitatstartup", "false")).booleanValue();	//PK86553
    public static final boolean GET_WRITER_ON_EMPTY_BUFFER = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.jsp.getwriteronemptybuffer", "false")).booleanValue();	//PK90190    
    	
	
	public static final boolean IGNORE_SESSION_STATIC_FILE_REQUEST = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.ignoresessiononstaticfilerequest")).booleanValue(); // PK89213
	public static final boolean INVOKE_REQUEST_LISTENER_FOR_FILTER = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.invokerequestlistenerforfilter")).booleanValue(); // PK91120
    public static final boolean FINISH_RESPONSE_ON_CLOSE = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.finishresponseonclose")).booleanValue(); // PK89810
    public static final boolean LIMIT_BUFFER = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.jsp.limitbuffer")).booleanValue();			//PK95332
   
    //Start 7.0.0.9
	public static final boolean IGNORE_INJECTION_FAILURE = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.ignoreinjectionfailure","false")).booleanValue(); // 596191
	
	public static final String HTTPONLY_COOKIES = customProps.getProperty("com.ibm.ws.webcontainer.httponlycookies");					//F004323
	public static final boolean REINIT_SERVLET_ON_INIT_UNAVAILABLE_EXCEPTION = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.reinitservletoninitunavailableexception")).booleanValue(); //PM01373
	public static final boolean ENABLE_MULTI_READ_OF_POST_DATA = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.enablemultireadofpostdata")).booleanValue(); // F003449
	//End 7.0.0.9
	
	// Start 7.0.0.11
    public static final boolean SERVE_WELCOME_FILE_FROM_EDR = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.servewelcomefilefromextendeddocumentroot")).booleanValue(); // PM02985   
    public static final boolean FILE_WRAPPER_EVENTS_LESS_DETAIL= Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.filewrappereventslessdetail")).booleanValue();  // PK99400
    public static final boolean SET_UNENCODED_HTML_IN_SENDERROR = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.setunencodedhtmlinsenderror")).booleanValue(); // PM03788
    public static final boolean DISABLE_SET_CHARACTER_ENCODING_AFTER_PARAMETERS_READ = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.disablesetcharacterencodingafterparametersread")).booleanValue(); // PM03928
	public static final boolean THROW_EXCEPTION_FOR_ADDELRESOLVER = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.jsp.throwexceptionforaddelresolver")).booleanValue(); //PM05903
	public static final boolean ENABLE_JSP_MAPPING_OVERRIDE = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.enablejspmappingoverride")).booleanValue(); //PM07560
    public static final boolean ENABLE_DEFAULT_IS_EL_IGNORED_IN_TAG = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.jsp.enabledefaultiselignoredintag")).booleanValue(); // PM08060
	public static final boolean COMPLETE_RESPONSE_EARLY = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.completeresponseearly")).booleanValue();      //PM08760
	// End 7.0.0.11
	
	//Start 7.0.0.13
	public static final boolean TOLERATE_LOCALE_MISMATCH_FOR_SERVING_FILES = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.toleratelocalemismatchforservingfiles", "false")).booleanValue(); //PM10362
    public static final boolean ALLOW_PARTIAL_URL_TO_EDR = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.enablepartialurltoextendeddocumentroot")).booleanValue(); // PM17845  
	//End 7.0.0.13
	
	//Start 7.0.0.15
	//PM22082 SECINT property will not be published.
    public static final boolean ALLOW_DIRECTORY_INCLUDE =  Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.jsp.allowdirectoryinclude")).booleanValue();  //PM22082
    public static final boolean DISPATCHER_RETHROW_SERROR = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.dispatcherrethrowserror")).booleanValue();      //PM22919
    public static final boolean COMPLETE_DATA_RESPONSE = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.sendresponsetoclientwhenresponseiscomplete", "true")).booleanValue(); //PM18453
	public static final boolean COMPLETE_REDIRECT_RESPONSE = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.sendresponsetoclientaspartofsendredirect", "false")).booleanValue(); //PM18453
	public static final boolean KEEP_UNREAD_DATA = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.keepunreadpostdataafterresponsesenttoclient","false")).booleanValue(); //PM18453
	public static final boolean PARSE_UTF8_POST_DATA = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.parseutf8postdata","false")).booleanValue(); //PM20484
	public static final boolean CHECK_EDR_IN_GET_REALPATH = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.checkedringetrealpath", "false")).booleanValue(); //PM21451
	public static final boolean LOCALE_DEPENDENT_DATE_FORMATTER = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.localedependentdateformatter")).booleanValue(); //PM25931, this has been added if any customer is dependet on current behavior.
	//End 7.0.0.15

	// Start 7.0.0.19
    public static final boolean IFMODIFIEDSINCE_NEWER_THAN_FILEMODIFIED_TIMESTAMP = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.modifiedsincelaterthanfiletimestamp", "false")).booleanValue(); //PM36341
	public static final boolean ALLOW_QUERY_PARAM_WITH_NO_EQUAL = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.allowqueryparamwithnoequal")).booleanValue(); //PM35450

	//Begin 8.0
	//see WebAppRequestDispatcher where this is used for details.
	public static final boolean KEEP_ORIGINAL_PATH_ELEMENTS = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.keeporiginalpathelements","true")).booleanValue();
	public static final boolean LOG_SERVLET_CONTAINER_INITIALIZER_CLASSLOADER_ERRORS = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.logservletcontainerinitializerclassloadingerrors")).booleanValue(); //Servlet 3.0
    public static final boolean ALLOW_INCLUDE_SEND_ERROR = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.allowincludesenderror")).booleanValue(); //Servlet 3.0
	public static final boolean SERVLET_30_FNF_BEHAVIOR = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.servlet30filenotfoundbehavior","true")).booleanValue(); //Servlet 3.0;
	public static final boolean SKIP_META_INF_RESOURCES_PROCESSING = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.skipmetainfresourcesprocessing")).booleanValue(); //Servlet 3.0
	public static final int 	META_INF_RESOURCES_CACHE_SIZE = Integer.valueOf(customProps.getProperty("com.ibm.ws.webcontainer.metainfresourcescachesize", "20")).intValue();
	public static final boolean INIT_PARAM_CONFLICT_CHECK = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.initparamconflictcheck","true")).booleanValue(); //Servlet 3.0;
	public static final boolean CHECK_REQUEST_OBJECT_IN_USE = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.checkrequestobjectuse")).booleanValue(); 
	public static final boolean USE_WORK_MANAGER_FOR_ASYNC_CONTEXT_START =Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.useworkmanagerforasynccontextstart","true")).booleanValue(); //Servlet 3.0;
	public static final boolean RESET_BUFFER_ON_SET_STATUS =  Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.resetbufferonsetstatus")).booleanValue(); 
	
	public static final String X_POWERED_BY = WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.xpoweredby");
	public static final boolean DISABLE_X_POWERED_BY = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.disablexpoweredby")).booleanValue();
	
	public static final boolean DISABLE_SCI_FOR_PRE_V8_APPS = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.disableservletcontainerinitializersonprev8apps")).booleanValue();
	
	//Begin: Do not document
	public static final boolean CHECK_FORCE_WORK_REJECTED = 
		Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.checkforceworkrejected")).booleanValue();
	
	//End: Do not document
	
	public static final boolean JSF_DISABLE_ALTERNATE_FACES_CONFIG_SEARCH = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.jsf.disablealternatefacesconfigsearch"));   //JSF 2.0 for startup performance

	//undocumented
	public static final boolean THROW_EXCEPTION_WHEN_UNABLE_TO_COMPLETE_OR_DISPATCH = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.throwexceptionwhenunabletocompleteordispatch", "true")).booleanValue();
	//end undocumented
	
	//End 8.0

    //Start 8.0.0.1
    public static final boolean ENABLE_EXACT_MATCH_J_SECURITY_CHECK = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.enableexactmatchjsecuritycheck")).booleanValue();       //F011107

    //Start 8.0.0.2
    public static final boolean EXPRESSION_RETURN_EMPTY_STRING = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.jsp.expressionreturnemptystring")).booleanValue(); //PM47661
    
	// Start 8.0.0.3
    public static final boolean RETURN_DEFAULT_CONTEXT_PATH = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.returndefaultcontextpath","true")).booleanValue();  //PM47487     
    public static final int ASYNC_MAX_SIZE_TASK_POOL = Integer.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.asyncmaxsizetaskpool", "5000")).intValue();			//PM51151
	public static final int ASYNC_PURGE_INTERVAL = Integer.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.asyncpurgeinterval", "30000")).intValue();				    //PM51151  in milliseconds
    // Do not document INVOKE_FLUSH_AFTER_SERVICE
    public static final boolean INVOKE_FLUSH_AFTER_SERVICE = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.invokeflushafterservice" , "true")).booleanValue(); //PM50111    
    public static boolean LOG_MULTIPART_EXCEPTIONS_ON_PARSEPARAMETER = Boolean.valueOf(WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.logmultipartexceptionsonparseparameter")).booleanValue(); //724365.2  
    public static int MAX_PARAM_PER_REQUEST = Integer.valueOf( WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.maxparamperrequest", "10000")).intValue(); //724365 (PM53930)
	
    
    //Start 8.0.0.4
    public static final int MAX_DUPLICATE_HASHKEY_PARAMS = Integer.valueOf( WebContainer.getWebContainerProperties().getProperty("com.ibm.ws.webcontainer.maxduplicatehashkeyparams", "50")).intValue(); //728397 (PM58495)

}
