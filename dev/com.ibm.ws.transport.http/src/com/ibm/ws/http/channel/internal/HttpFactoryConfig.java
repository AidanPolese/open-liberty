// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.http.channel.internal;

import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * Object used for parsing and storing the configuration for an HTTP channel
 * factory instance.
 */
public class HttpFactoryConfig {

    /** RAS tracing variable */
    private static final TraceComponent tc = Tr.register(HttpFactoryConfig.class, HttpMessages.HTTP_TRACE_NAME, HttpMessages.HTTP_BUNDLE);

    /** Maximum message size typically allowed */
    private long msgSizeLimit = HttpConfigConstants.UNLIMITED;
    /** Special single larger buffer allowed beyond the standard size */
    private long msgSizeLargeBuffer = HttpConfigConstants.UNLIMITED;

    /**
     * Constructor for an HTTP channel factory config object.
     * 
     * @param props
     */
    public HttpFactoryConfig(Map<Object, Object> props) {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Parsing config for factory");
        }
        if (null != props) {
            parseMsgSize(props);
            parseMsgLargeBuffer(props);
        }
    }

    /**
     * Parse the standard limit on an incoming message size.
     * 
     * @param props
     */
    private void parseMsgSize(Map<Object, Object> props) {
        String value = (String) props.get(HttpConfigConstants.PROPNAME_MSG_SIZE_LIMIT);
        if (null != value) {
            try {
                this.msgSizeLimit = Long.parseLong(value);
                if (HttpConfigConstants.UNLIMITED > getMessageSize()) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                        Tr.event(tc, "Config: Invalid size, setting to unlimited: " + this.msgSizeLimit);
                    }
                    this.msgSizeLimit = HttpConfigConstants.UNLIMITED;
                }
            } catch (NumberFormatException e) {
                // no FFDC required
                if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                    Tr.event(tc, "Config: Invalid maximum message size; " + value);
                }
            }
        }
    }

    /**
     * Query what the current limit is on the incoming message size.
     * 
     * @return long (HttpConfigConstants.UNLIMITED if not set)
     */
    public long getMessageSize() {
        return this.msgSizeLimit;
    }

    /**
     * Query whether incoming messages have a size limit to check against.
     * 
     * @return boolean
     */
    public boolean areMessagesLimited() {
        return (HttpConfigConstants.UNLIMITED != getMessageSize());
    }

    /**
     * Parse the single larger buffer size that is allowed to reach beyond the
     * standard buffer size
     * 
     * @param props
     */
    private void parseMsgLargeBuffer(Map<Object, Object> props) {
        if (!areMessagesLimited()) {
            // if there isn't a standard size limit, ignore this extension to
            // that config option
            return;
        }
        String value = (String) props.get(HttpConfigConstants.PROPNAME_MSG_SIZE_LARGEBUFFER);
        if (null != value) {
            try {
                long limit = Long.parseLong(value);
                if (limit < getMessageSize() || HttpConfigConstants.UNLIMITED > limit) {
                    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                        Tr.debug(tc, "Config: Invalid large buffer limit: " + limit);
                    }
                    limit = getMessageSize();
                }
                this.msgSizeLargeBuffer = limit;
            } catch (NumberFormatException e) {
                // no FFDC required
                if (tc.isEventEnabled()) {
                    Tr.event(tc, "Config: Non-numeric large buffer size; " + value);
                }
            }
        }
    }

    /**
     * Query what the large buffer size is allowed to reach (single allowed per
     * all channels created by this factory).
     * 
     * @return long (HttpConfigConstants.UNLIMITED if not set)
     */
    public long getLargerBufferSize() {
        return this.msgSizeLargeBuffer;
    }

}