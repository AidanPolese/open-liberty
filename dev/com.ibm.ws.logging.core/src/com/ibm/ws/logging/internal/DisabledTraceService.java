/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ws.logging.internal;

import java.util.logging.LogRecord;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.logging.WsTraceRouter;
import com.ibm.wsspi.logging.MessageRouter;
import com.ibm.wsspi.logprovider.LogProviderConfig;
import com.ibm.wsspi.logprovider.TrService;

public class DisabledTraceService implements TrService {
    @Override
    public void audit(TraceComponent tc, String s, Object... o) {}

    @Override
    public void debug(TraceComponent tc, String s, Object... o) {}

    @Override
    public void debug(TraceComponent tc, Object id, String msg, Object... objs) {}

    @Override
    public void dump(TraceComponent tc, String s, Object... o) {}

    @Override
    public void entry(TraceComponent tc, String s, Object... o) {}

    @Override
    public void entry(TraceComponent tc, Object id, String s, Object... o) {}

    @Override
    public void error(TraceComponent tc, String s, Object... o) {}

    @Override
    public void event(TraceComponent tc, String s, Object... o) {}

    @Override
    public void event(TraceComponent tc, Object id, String s, Object... o) {}

    @Override
    public void exit(TraceComponent tc, String s) {}

    @Override
    public void exit(TraceComponent tc, Object id, String s) {}

    @Override
    public void exit(TraceComponent tc, String s, Object o) {}

    @Override
    public void exit(TraceComponent tc, Object id, String s, Object o) {}

    @Override
    public void fatal(TraceComponent tc, String s, Object... o) {}

    @Override
    public void info(TraceComponent tc, String s, Object... o) {}

    @Override
    public void init(LogProviderConfig config) {}

    @Override
    public void register(TraceComponent tc) {}

    @Override
    public void stop() {}

    @Override
    public void warning(TraceComponent tc, String s, Object... o) {}

    @Override
    public void update(LogProviderConfig config) {}

    @Override
    public void setMessageRouter(MessageRouter msgRouter) {}

    @Override
    public void unsetMessageRouter(MessageRouter msgRouter) {}

    @Override
    public void publishLogRecord(LogRecord logRecord) {}

    @Override
    public void setTraceRouter(WsTraceRouter traceRouter) {}

    @Override
    public void unsetTraceRouter(WsTraceRouter traceRouter) {}
}
