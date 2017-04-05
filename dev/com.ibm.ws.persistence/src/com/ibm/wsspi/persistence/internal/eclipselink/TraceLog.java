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
package com.ibm.wsspi.persistence.internal.eclipselink;

import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.platform.server.ServerLog;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.persistence.internal.PersistenceServiceConstants;

@Trivial
public class TraceLog extends ServerLog {
    private final TraceComponent _tc = Tr.register(LogChannel.class, PersistenceServiceConstants.TRACE_GROUP);

    @Override
    public void log(SessionLogEntry entry) {
        if (_tc.isDebugEnabled()) {
            Tr.debug(_tc, formatMessage(entry));
        }
    }
}
