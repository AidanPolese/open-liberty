/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security.config.ssl;

import java.util.Hashtable;
import java.util.Map;

import javax.net.ssl.SSLSession;

/**
 * Stores requests' SSL sessions so that they may be shared amongst portable
 * interceptors. We use this singleton instead of using a ThreadLocal
 * because we cannot guarantee that interceptors will be called under
 * the same thread for a single request.
 * <p/>
 * TODO: There may be an error where the interceptor does not remove the
 * registered session. We should have a daemon that cleans up old requests.
 * 
 * @version $Revision: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public final class SSLSessionManager {
    private final static Map requestSSLSessions = new Hashtable();

    public static SSLSession getSSLSession(int requestId) {
        return (SSLSession) requestSSLSessions.get(requestId);
    }

    public static void setSSLSession(int requestId, SSLSession session) {
        requestSSLSessions.put(requestId, session);
    }

    public static SSLSession clearSSLSession(int requestId) {
        return (SSLSession) requestSSLSessions.remove(requestId);
    }
}
