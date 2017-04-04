package com.ibm.ws.jsp.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class JspMessages {
    private static ResourceBundle NLS_BUNDLE; 
    static {
        try{
            NLS_BUNDLE = ResourceBundle.getBundle("com.ibm.ws.jsp.resources.messages", Locale.getDefault());
        } catch (Exception e){
            NLS_BUNDLE = ResourceBundle.getBundle("com.ibm.ws.jsp.resources.messages");
        }
    }
  
    public static String getMessage(String key){
        return getMessage (key, null);
    }
  
    public static String getMessage(String key, Object[] args) {
        String msg = null;
        try {
            msg = NLS_BUNDLE.getString(key);
            if (args != null)
                msg = MessageFormat.format(msg, args);
        } catch (MissingResourceException e) {
            msg = key;
        }
        return (msg);
    }
}
