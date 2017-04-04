// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.jsf;

public class JSFConstants {

    public static String SERIALIZED_CONFIG = "com/ibm/ws/jsf/FacesConfigParser.ser";
    
    public static final String IBM_JSF_PACKAGE = "com.ibm.ws.jsf";
    
    public static String FACES_SERVLET_MAPPINGS = IBM_JSF_PACKAGE + ".servlet.mappings";
    
    //TODO: defect 209676: add ability to load JSF Configuration at webmodule startup 
    public static String LOAD_FACES_CONFIG_STARTUP = IBM_JSF_PACKAGE + ".LOAD_FACES_CONFIG_AT_STARTUP";
    //end: defect 209676:

    public static final String JSP_UPDATE_CHECK = IBM_JSF_PACKAGE + ".JSP_UPDATE_CHECK";
    
    public static String JSF_IMPL_CHECK = IBM_JSF_PACKAGE + ".JSF_IMPL_CHECK";
        
    public static final String JSP_URI_MATCHER = IBM_JSF_PACKAGE + ".JSP_URI_MATCHER";

    public static final String JSF_IMPL_ENABLED_PARAM = "com.ibm.ws.jsf.JSF_IMPL_ENABLED";

    public static final String DATA_TABLE_TAG_NULL_VAR = "com.ibm.wsspi.jsf.datatabletagnullvarattribute";  //PM05659 (original APAR was PK33787)

    public enum JSFImplEnabled{
        MyFaces, SunRI, Custom, None
    }

}
