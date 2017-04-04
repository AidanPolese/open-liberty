// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

//  CHANGE HISTORY
//  Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//          382943         08/09/06     todkap              remove SUN dependencies from core webcontainer

package com.ibm.ws.webcontainer.util;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;

import com.ibm.ejs.ras.TraceNLS;


public class WASSystem {
    private static String PROPERTIES_PATH = "/com/ibm/ws/webcontainer/appserver.properties";
    private static Properties _props;

    private static TraceNLS nls = TraceNLS.getTraceNLS(WASSystem.class, "com.ibm.ws.webcontainer.resources.Messages");
    static{
        try {
            /*
             ** read the properties as a resource
             */
            _props = new Properties();
            InputStream in = getResourceAsStream(PROPERTIES_PATH);
            _props.load(in);
        }
        catch(Throwable th) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.util.WASSystem", "39");
        }
    }

    //no instance of this can be created.
    private WASSystem() {
    }

    @SuppressWarnings("unchecked")
    public static Enumeration getPropertyNames() {
        return _props.propertyNames();
    }

    public static String getProperty(String name) {
        return _props.getProperty(name);
    }

    public static URL getResource(String path) {
        return WASSystem.class.getResource(path);
    }

    public static InputStream getResourceAsStream(String path) {
        return WASSystem.class.getResourceAsStream(path);
    }

    @SuppressWarnings("unchecked")
    public static Object createObject(String classname) {
        try {
            Class aClass = Class.forName(classname);
            return(Object)aClass.newInstance();
        }
        catch( Throwable th ) {
            com.ibm.wsspi.webcontainer.util.FFDCWrapper.processException(th, "com.ibm.ws.webcontainer.util.WASSystem.createObject", "71");
            throw new IllegalStateException(MessageFormat.format(nls.getString("{0}.is.not.a.valid.class","{0} is not a valid class"), new Object[]{classname}));
        }
    }
}
