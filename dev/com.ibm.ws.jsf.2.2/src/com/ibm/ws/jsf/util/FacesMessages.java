// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.jsf.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FacesMessages {
    protected static ResourceBundle bundle = null;
    
	//	Log instance for this class
    protected static final Logger logger = Logger.getLogger("com.ibm.ws.jsf");
    private static final String CLASS_NAME="com.ibm.ws.jsf.util.FacesMessages";
    
    static {
        try {
            bundle = ResourceBundle.getBundle("com.ibm.ws.jsf.resources.messages", Locale.getDefault());
        }
        catch (Exception e) {
			if(logger.isLoggable(Level.WARNING)){
				logger.logp(Level.WARNING, CLASS_NAME, "static", "Failed to load resource bundle com.ibm.ws.jsf.resources.messages locale "+ Locale.getDefault(), e);
			}
        }
        if(bundle == null){
            try {
                bundle = ResourceBundle.getBundle("com.ibm.ws.jsf.resources.messages", Locale.US);
            }
            catch (Exception e) {
                if(logger.isLoggable(Level.WARNING)){
                    logger.logp(Level.WARNING, CLASS_NAME, "static", "Failed to load default resource bundle com.ibm.ws.jsf.resources.messages locale "+ Locale.US, e);
                }
            }
        }
    }

    public static String getMsg(String key) {
        return getMsg(key, null);    
    }
    
    public static String getMsg(String key, Object[] args) {
        String msg = null;
        try {
            msg = bundle.getString(key);
            if (args != null)
                msg = MessageFormat.format(msg, args);
        }
        catch (MissingResourceException e) {
            msg = key;
        }
        return (msg);
    }
}
