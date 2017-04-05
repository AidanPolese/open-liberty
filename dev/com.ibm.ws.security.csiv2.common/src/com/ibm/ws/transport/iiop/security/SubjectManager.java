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
package com.ibm.ws.transport.iiop.security;

import java.util.Hashtable;
import java.util.Map;

import javax.security.auth.Subject;

/**
 * Stores requests' subjects because get/setSlot does not seem to work in
 * OpenORB.
 * <p/>
 * TODO: There may be an error where the interceptor does not remove the
 * registered subjects. We should have a daemon that cleans up old requests.
 * 
 * @version $Revision: 451417 $ $Date: 2006-09-29 13:13:22 -0700 (Fri, 29 Sep 2006) $
 */
public final class SubjectManager {
    private final static Map requestSubjects = new Hashtable();

    public static Subject getSubject(int requestId) {
        return (Subject) requestSubjects.get(requestId);
    }

    public static void setSubject(int requestId, Subject subject) {
        requestSubjects.put(requestId, subject);
    }

    public static Subject clearSubject(int requestId) {
        return (Subject) requestSubjects.remove(requestId);
    }
}
