/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.auth.data.internal;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.websphere.crypto.PasswordUtil;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.security.auth.data.AuthData;
import com.ibm.wsspi.kernel.service.utils.SerializableProtectedString;

/**
 * The auth data from server.xml.
 */
@Component(service = com.ibm.websphere.security.auth.data.AuthData.class,
                configurationPid = "com.ibm.ws.security.jca.internal.authdata.config",
                configurationPolicy = ConfigurationPolicy.REQUIRE,
                property = { "service.vendor=IBM" })
public class AuthDataImpl implements AuthData {

    protected static final String CFG_KEY_ID = "id";
    protected static final String CFG_KEY_DISPLAY_ID = "config.displayId";
    protected static final String CFG_KEY_USER = "user";
    protected static final String CFG_KEY_PASSWORD = "password";

    private String username;
    private String password;

    @Activate
    protected void activate(@Sensitive Map<String, Object> props) {
        username = (String) props.get(CFG_KEY_USER);
        SerializableProtectedString sps = (SerializableProtectedString) props.get(CFG_KEY_PASSWORD);
        String configuredPassword = sps == null ? "" : new String(sps.getChars());
        password = PasswordUtil.passwordDecode(configuredPassword);
    }

    /**
     * Gets the user name as defined in the configuration.
     * 
     * @return the user name.
     */
    @Override
    public String getUserName() {
        return username;
    }

    /**
     * Gets the password as a char[] as defined in the configuration.
     * 
     * @return the char[] representation of the password.
     */
    @Override
    @Sensitive
    public char[] getPassword() {
        return password.toCharArray();
    }

}
