/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.collective.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ibm.websphere.ras.annotation.Sensitive;

/**
 * Small helper class to mask potential passwords in a string.
 * 
 * 
 */
public class PasswordMaskUtil {

    /**
     * This method will mask the password value if the key contains
     * --.*password or
     * --.*pwd
     * 
     * @param hostAuthInfo
     * @return hostAuthInfoWithMaskPassword
     */
    public static Map<String, Object> maskPasswordsInMap(@Sensitive Map<String, Object> hostAuthInfo) {
        Map<String, Object> hostAuthInfoWithMaskPassword = new HashMap<String, Object>();
        if (hostAuthInfo == null)
            return hostAuthInfoWithMaskPassword;
        // iterate all map element if key has key work password or pwd, mask the value to *****
        Set<Entry<String, Object>> entries = hostAuthInfo.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = null;
            if (key.toLowerCase().endsWith("password") || (key.toLowerCase().endsWith("pwd"))) {
                value = "*****";
            } else {
                value = hostAuthInfo.get(key);
            }
            hostAuthInfoWithMaskPassword.put(key, value);
        }
        return hostAuthInfoWithMaskPassword;
    }
}
