package com.ibm.ws.jndi.iiop;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;

public enum MessageUtil {
    ;
    public static final String RESOURCE_BUNDLE_NAME = "com.ibm.ws.jndi.iiop.Messages";
    
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);

    @FFDCIgnore(Exception.class)
    public static String format(final String key, final String defaultMessage, Object... inserts) {
        String message = defaultMessage;
        try {
            try {
                message = BUNDLE.getString(key);
            } catch (MissingResourceException e) {
                message = defaultMessage;
            }
            if (inserts.length != 0)
                message = MessageFormat.format(message, inserts);
            return message;
        } catch (Exception ignored) {
            return message;
        }
    }

}
