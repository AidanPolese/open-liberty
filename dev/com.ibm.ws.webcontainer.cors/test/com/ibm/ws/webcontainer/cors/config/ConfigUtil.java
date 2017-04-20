/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.cors.config;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ConfigUtil {
    private static final String CONFIG_DOMAIN = "domain";
    private static final String CONFIG_ALLOWED_ORIGINS = "allowedOrigins";
    private static final String CONFIG_ALLOWED_METHODS = "allowedMethods";
    private static final String CONFIG_ALLOWED_HEADERS = "allowedHeaders";
    private static final String CONFIG_MAX_AGE = "maxAge";
    private static final String CONFIG_ALLOW_CREDENTIALS = "allowCredentials";
    private static final String CONFIG_EXPOSE_HEADERS = "exposeHeaders";

    public static CorsConfig generateCorsConfig(String domain, String allowedOrigins, String allowedMethods, String allowedHeaders,
                                                Long maxAge, Boolean allowCredentials, String exposeHeaders) {
        if (domain == null || domain.isEmpty() ||
            allowedOrigins == null || allowedOrigins.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(CONFIG_DOMAIN, domain);
        properties.put(CONFIG_ALLOWED_ORIGINS, allowedOrigins);
        properties.put(CONFIG_ALLOWED_METHODS, allowedMethods);
        properties.put(CONFIG_ALLOWED_HEADERS, allowedHeaders);
        properties.put(CONFIG_MAX_AGE, maxAge);
        properties.put(CONFIG_ALLOW_CREDENTIALS, allowCredentials);
        properties.put(CONFIG_EXPOSE_HEADERS, exposeHeaders);

        CorsConfig config = new CorsConfig();
        config.activate(null, properties);

        return config;
    }
}
