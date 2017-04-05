// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

//  CHANGE HISTORY
// Defect       Date        Modified By     Description
//--------------------------------------------------------------------------------------
// PK75617      02/11/09    pmdinh          Add custom prop to suppress IllegalArgumentException for invalid query string
// PM35450      04/25/11    anupag          Provide an option to allow query parameter with no "="
// PM53950      12/08/11    anupag          sec/int defect 724365, add limit to parameters
// PM58495      02/25/12    anupag          provide limit to duplicate hash if unique keys (728397)
// PM57418      02/27/12    anupag          Translate messages added by PM53930 and PM58495 (724365.4)
//
package com.ibm.wsspi.webcontainer.util;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat; //724365.4
import java.util.Hashtable;
import java.util.HashSet; // 728397
import javax.servlet.http.HttpServletRequest;

import java.util.logging.Logger;
import java.util.logging.Level;
import com.ibm.ejs.ras.TraceNLS; //724365.4
import com.ibm.wsspi.webcontainer.logging.LoggerFactory;
import com.ibm.ws.webcontainer.webapp.WebAppRequestDispatcher;
import com.ibm.wsspi.webcontainer.WCCustomProperties;					//PK75617

/**
 *
 * 
 * RequestUtils provides methods for retrieving various data based on the current
 * request such as the parsing the query string and retrieving the current uri
 * depending on dispatch type.
 * 
 * @ibm-private-in-use
 * 
 * @since   WAS7.0
 *
 */
public class RequestUtils {
	protected static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.util");
	private static final String CLASS_NAME="com.ibm.wsspi.webcontainer.util.RequestUtils";
	private static TraceNLS nls = TraceNLS.getTraceNLS(RequestUtils.class, "com.ibm.ws.webcontainer.resources.Messages"); //724365.4

	private static final String SHORT_ENGLISH = "8859_1"; //a shortened ASCII encoding
	
	private static boolean ignoreInvalidQueryString = WCCustomProperties.IGNORE_INVALID_QUERY_STRING;       //PK75617
    private static boolean allowQueryParamWithNoEqual = WCCustomProperties.ALLOW_QUERY_PARAM_WITH_NO_EQUAL;       //PM35450
    private static final String EMPTY_STRING = ""; //PM35450
    private static int maxParamPerRequest = WCCustomProperties.MAX_PARAM_PER_REQUEST; // PM53930 (724365)
    private static final int maxDuplicateHashKeyParams = WCCustomProperties.MAX_DUPLICATE_HASHKEY_PARAMS; // PM58495 (728397)


    
   /**
    *
    * Parses a query string passed from the client to the
    * server and builds a <code>HashTable</code> object
    * with key-value pairs.
    * The query string should be in the form of a string
    * packaged by the GET or POST method, that is, it
    * should have key-value pairs in the form <i>key=value</i>,
    * with each pair separated from the next by a & character.
    *
    * <p>A key can appear more than once in the query string
    * with different values. However, the key appears only once in
    * the hashtable, with its value being
    * an array of strings containing the multiple values sent
    * by the query string.
    *
    * <p>The keys and values in the hashtable are stored in their
    * decoded form, so
    * any + characters are converted to spaces, and characters
    * sent in hexadecimal notation (like <i>%xx</i>) are
    * converted to ASCII characters.
    *
    * @param s		a string containing the query to be parsed
    *
    * @return		a <code>HashTable</code> object built
    * 			from the parsed key-value pairs
    *
    * @exception IllegalArgumentException	if the query string
    *						is invalid
    *
    */
    @SuppressWarnings("unchecked")
   static public Hashtable parseQueryString(String s)
   {
       return parseQueryString(s, SHORT_ENGLISH);
   }
    @SuppressWarnings("unchecked")
   static public Hashtable parseQueryString(String s, String encoding)
   {
	   if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){	//PK75617
           logger.entering(CLASS_NAME, "parseQueryString( query , encoding --> [" +  encoding +"])"); //PM35450.1
	   }																							//PK75617
       String valArray[] = null;
       int totalSize = 0; //PM53930
       int dupSize = 0; // 728397
       if (s == null)
       {
           throw new IllegalArgumentException("query string or post data is null");
       }
       Hashtable ht = new Hashtable();
       HashSet<Integer> key_hset = new HashSet<Integer>(); // 728397
       // @MD17415 Start 1 of 3: New Loop for finding key and value           @RWS1
       char [] ch = s.toCharArray();
       int lgth = ch.length;
       boolean encoding_is_ShortEnglish = true;                  // @RWS9
       // PK23256 begin
        /*if (encoding.indexOf("8859-1")==-1 && encoding.indexOf(SHORT_ENGLISH)==-1  ) // @RWS9
            encoding_is_ShortEnglish = false;                  // @RWS9*/
        
       if (!encoding.endsWith("8859_1")   
                && !encoding.endsWith("8859-1") 
                && (encoding.indexOf("8859-1-Windows") == -1) 
                ) { 
          encoding_is_ShortEnglish = false; 
       } 
        // PK23256 end
       int pair_start=0;
       int i = 0, j=0, equalSign=0;
       for (i=0; i<lgth; i++) {
           if (ch[i] == '&') {
               for (equalSign=pair_start; equalSign<i; equalSign++) {
                   if (ch[equalSign] == '=')
                       break;
               }
               if ((equalSign < i) || (allowQueryParamWithNoEqual && equalSign == i)) //PM35450 , parameter is blah and not blah=
               {   // equal sign found at offset equalSign
                   String key = parseName(ch,pair_start,equalSign);
                   //PM35450 Start
                   String value = null;
                   if (equalSign == i){
                       if(key != null) value = EMPTY_STRING;
                       else value = null;
                   }
                   else value = parseName(ch,equalSign+1,i);
                   //PM35450 End
                   if (ignoreInvalidQueryString && ((value == null) || (key ==null))){     	//PK75617
                	   pair_start = i +1;
                	   continue;
                   }																		//PK75617
                   if ( !encoding_is_ShortEnglish) {
                       try {
                           key = new String(key.getBytes(SHORT_ENGLISH),encoding);
                           value = new String(value.getBytes(SHORT_ENGLISH),encoding);
                       } catch ( UnsupportedEncodingException uee ) {
                          //No need to nls. SHORT_ENGLISH will always be supported
                           logger.logp(Level.SEVERE, CLASS_NAME,"parseQueryString", "unsupported exception", uee);
                           throw new IllegalArgumentException();
                       }
                   }
                   if (ht.containsKey(key)) {
                       String oldVals[] = (String []) ht.get(key);
                       valArray = new String[oldVals.length + 1];
                       for (j = 0; j < oldVals.length; j++)
                           valArray[j] = oldVals[j];
                       valArray[oldVals.length] = value;
                   } else {
			// 728397 Start                        
                       if(!(key_hset.add(key.hashCode()))){ 
                    	   	dupSize++;// if false then count as duplicate hashcodes for unique keys
    			   	if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
    	                    		logger.logp(Level.FINE, CLASS_NAME,"parseQueryString", "duplicate hashCode generated by key --> " + key);
    				}
    				if( dupSize > maxDuplicateHashKeyParams){							 
    					logger.logp(Level.SEVERE, CLASS_NAME,"parseQueryString", MessageFormat.format(nls.getString("Exceeding.maximum.hash.collisions"), new Object[]{maxDuplicateHashKeyParams}));
    			                	
    					throw new IllegalArgumentException();
    				}
	    		} // 728397 End 
                       valArray = new String[1];
                       valArray[0] = value;
                   }
		   // 724365(PM53930) Start
                   totalSize++;
		   if((maxParamPerRequest == -1) || ( totalSize < maxParamPerRequest)){
                	   ht.put(key, valArray);
                   }
                   else{
                	   // possibly 10000 big enough, will never be here 
                	   logger.logp(Level.SEVERE, CLASS_NAME,"parseQueryString", MessageFormat.format(nls.getString("Exceeding.maximum.parameters"), new Object[]{maxParamPerRequest, totalSize}));
                			  
                       throw new IllegalArgumentException();
                   }// 724365 End
               }
               pair_start = i+1;
           }
       }
       for (equalSign=pair_start; equalSign<i; equalSign++) {
           if (ch[equalSign] == '=')
               break;
       }
       if ((equalSign < i) || (allowQueryParamWithNoEqual && equalSign == i)) //PM35450 , parameter is blah and not blah=
       {   // equal sign found at offset equalSign
           String key = parseName(ch,pair_start,equalSign);
           //PM35450 Start
           String value = null;
           if (equalSign == i){
             if(key != null) value = EMPTY_STRING;
             else value = null;
           }
           else value = parseName(ch,equalSign+1,i);
           //PM35450 End
           if (ignoreInvalidQueryString && ((value == null) || (key ==null))){     					//PK75617 - start
        	   if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){	
        		   logger.exiting(CLASS_NAME, "parseQueryString(String, String)");
        	   }																						
        	   return ht;
           }																						//PK75617 -end
           if ( !encoding_is_ShortEnglish ) {
               try {
                   key = new String(key.getBytes(SHORT_ENGLISH),encoding);
                   value = new String(value.getBytes(SHORT_ENGLISH),encoding);
               } catch ( UnsupportedEncodingException uee ) {
                   logger.logp(Level.SEVERE, CLASS_NAME,"parseQueryString", "unsupported exception", uee);
                   throw new IllegalArgumentException();
               }
           }
           if (ht.containsKey(key)) {
               String oldVals[] = (String []) ht.get(key);
               valArray = new String[oldVals.length + 1];
               for (j = 0; j < oldVals.length; j++)
                   valArray[j] = oldVals[j];
               valArray[oldVals.length] = value;
           } else {
		 // 728397 Start               
               if(!(key_hset.add(key.hashCode()))){
            	   	dupSize++;	// if false then count as duplicate hashcodes for unique keys
			if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) {  
                    		logger.logp(Level.FINE, CLASS_NAME,"parseQueryString", "duplicate hashCode generated by key --> " + key);
			}
			if( dupSize > maxDuplicateHashKeyParams){							 
				logger.logp(Level.SEVERE, CLASS_NAME,"parseQueryString", MessageFormat.format(nls.getString("Exceeding.maximum.hash.collisions"), new Object[]{maxDuplicateHashKeyParams}));
		                	
				 throw new IllegalArgumentException();
			}
	       } // 728397 End 
               valArray = new String[1];
               valArray[0] = value;
           }
     	   // 724365(PM53930) Start
           totalSize++;
	   if((maxParamPerRequest == -1) || ( totalSize < maxParamPerRequest)){
               	   ht.put(key, valArray);
           }
           else{
               	   // possibly 10000 big enough, will never be here 
               	   logger.logp(Level.SEVERE, CLASS_NAME,"parseQueryString", MessageFormat.format(nls.getString("Exceeding.maximum.parameters"), new Object[]{maxParamPerRequest, totalSize}));
               			   
                   throw new IllegalArgumentException();
           }// 724365 End
       }
       // @MD17415 End 1 of 3: New Loop for finding key and value           @RWS1
       
//@MD17415 begin part 2 of 3 New Loop for finding key and value
//@MD17415 
//@MD17415         StringBuffer sb = new StringBuffer();
//@MD17415         StringTokenizer st = new StringTokenizer(s, "&");
//@MD17415         while (st.hasMoreTokens())
//@MD17415         {
//@MD17415             String pair = (String) st.nextToken();
//@MD17415             int pos = pair.indexOf('=');
//@MD17415             if (pos == -1)
//@MD17415             {
//@MD17415                 // XXX
//@MD17415                 // should give more detail about the illegal argument
//@MD17415                 // ignore invalid parameter value (eg) http://localhost/servlet/snoop?name&age=9 (ignore name)
//@MD17415                 //don't throw new IllegalArgumentException();
//@MD17415             }
//@MD17415             else
//@MD17415             {
//@MD17415                 String key = parseName(pair.substring(0, pos), sb);
//@MD17415                 String val = parseName(pair.substring(pos + 1, pair.length()), sb);
//@MD17415                 /**
//@MD17415                  * ajg
//@MD17415                  * convert post data to right format
//@MD17415                  * skip if client is ASCII (english)
//@MD17415                 *
//@MD17415                 * Performance enhancement:
//@MD17415                 *   added 1st condition, if !encoding.equals("ISO-8859-1"), to avoid
//@MD17415                 *   the costly conversion.
//@MD17415                 *   (Keith Smith)
//@MD17415                  */
//@MD17415                 if ((!encoding.equals("ISO-8859-1")) && (encoding.indexOf(SHORT_ENGLISH) == -1))
//@MD17415                 {
//@MD17415                     try
//@MD17415                     {
//@MD17415                         key = new String(key.getBytes(SHORT_ENGLISH), encoding);
//@MD17415                         val = new String(val.getBytes(SHORT_ENGLISH), encoding);
//@MD17415                     }
//@MD17415                     catch (UnsupportedEncodingException uee)
//@MD17415                     {
//      @MD17415                         com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(uee, "com.ibm.ws.webcontainer.servlet.RequestUtils.parseQueryString", "289");

//@MD17415                         throw new IllegalArgumentException();
//@MD17415                     }
//@MD17415                 }
//@MD17415                 if (ht.containsKey(key))
//@MD17415                 {
//@MD17415                     String oldVals[] = (String[]) ht.get(key);
//@MD17415                     valArray = new String[oldVals.length + 1];
//@MD17415                     for (int i = 0; i < oldVals.length; i++)
//@MD17415                         valArray[i] = oldVals[i];
//@MD17415                     valArray[oldVals.length] = val;
//@MD17415                 }
//@MD17415                 else
//@MD17415                 {
//@MD17415                     valArray = new String[1];
//@MD17415                     valArray[0] = val;
//@MD17415                 }
//@MD17415                 ht.put(key, valArray);
//@MD17415             }
//@MD17415         }
//@MD17415 end part 2 of 3 New Loop for finding key and value
       if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)){		//PK75617
		   logger.exiting(CLASS_NAME, "parseQueryString(String, String)");
	   }																								//PK75617
       return ht;
   }
   /*
        * Parse a name in the query string.
        */
   // @MD17415 begin part 3 of 3 New Loop for finding key and value
   static private String parseName(char [] ch, int startOffset, int endOffset) {
       int j = 0;
       int startOffsetLocal = startOffset;   // local variable  -  @RWS2 
       int endOffsetLocal = endOffset;       // local variable  -  @RWS2 
       char [] chLocal = ch;                 // local variable  -  @RWS7
       char [] c = new char [endOffsetLocal-startOffsetLocal];     // @RWS2
       for (int i = startOffsetLocal; i < endOffsetLocal; i++) {  // @RWS2
           switch (chLocal[i]) {                                   // @RWS7
           case '+' :
               c[j++] = ' ';
               break;
           case '%' :
               if (i+2 < endOffsetLocal) {   // @RWS2
                   int num1 = Character.digit(chLocal[++i],16);   //@RWS7
                   int num2 = Character.digit(chLocal[++i],16);   //@RWS7
                   if (num1 == -1 || num2 == -1)             //@RWS5
                   {																	//PK75617 starts
                	   if (ignoreInvalidQueryString)									
                	   {
                		   logger.logp(Level.WARNING, CLASS_NAME,"parseName", "invalid.query.string");
                		   return null;
                	   }																//PK75617 ends
                       throw new IllegalArgumentException(); //@RWS5
                   }																	//PK75617
                   // c[j++] = (char)(num1*16 + num2);       //@RWS5
                   c[j++] = (char)((num1<<4) | num2);       //@RWS8
               } else {   // allow '%' at end of value or second to last character (as original code does)
                   for (i=i; i<endOffsetLocal; i++)   // @RWS2
                       c[j++] = chLocal[i];           // @RWS7
               }
               break;
           default :
               c[j++] = chLocal[i];
               break;
           } 
       } 
       return new String(c,0,j);
   }
   // @MD17415 end part 3 of 3 New Loop for finding key and value
   
   /**
    * Used to retrive the "true" uri that represents the current request.
    * If include request_uri attribute is set, it returns that value.
    * Otherwise, it returns the default of req.getRequestUri
    * @param req
    * @return
    */
   public static String getURIForCurrentDispatch (HttpServletRequest req){
          String includeURI = (String) req.getAttribute(WebAppRequestDispatcher.REQUEST_URI_INCLUDE_ATTR);
          if (includeURI == null)
                 return req.getRequestURI();
          else 
                 return includeURI;
   }

}
