/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.persistence.internal.eclipselink;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.platform.server.ServerLog;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.persistence.internal.PersistenceServiceConstants;

//TODO(151905) -- Cleanup. Poached from Liberty
@Trivial
public class Log extends ServerLog {

    public final static String LOG_PREFIX = "eclipselink.ps";
    private final static String EMPTY_CHANNEL = LOG_PREFIX;

    private static final TraceComponent _tc = Tr.register(Log.class, PersistenceServiceConstants.TRACE_GROUP);
    private final Map<String, LogChannel> _channels;

    public Log() {
        _channels = new ConcurrentHashMap<String, LogChannel>();

        // Register each category with eclipselink prefix as a WebSphere log channel
        for (String category : SessionLog.loggerCatagories) {
            _channels.put(category, new LogChannel(LOG_PREFIX + "." + category));
        }
        _channels.put(EMPTY_CHANNEL, new LogChannel(EMPTY_CHANNEL));
    }

    @Override
    @Trivial
    public void log(SessionLogEntry entry) {
        String category = entry.getNameSpace();
        int level = entry.getLevel();

        LogChannel channel = getLogChannel(category);
        if (channel.shouldLog(level)) {
            channel.log(entry, formatMessage(entry));
        }
    }

    @Override
    @Trivial
    public boolean shouldLog(int level, String category) {
        return getLogChannel(category).shouldLog(level);
    }

    @Trivial
    private LogChannel getLogChannel(String category) {
        if (category == null) {
            category = EMPTY_CHANNEL;
        }
        LogChannel channel = _channels.get(category);
        if (channel == null) {
            if (_tc.isDebugEnabled()) {
                Tr.debug(_tc, "Found an unmapped logging channel (" + category
                              + ") in log(...). Possibly something wrong in EclipseLink, remapping to base channel.");
                channel = _channels.get(EMPTY_CHANNEL);
            }
            channel = _channels.get(EMPTY_CHANNEL);
        }
        return channel;
    }
}
