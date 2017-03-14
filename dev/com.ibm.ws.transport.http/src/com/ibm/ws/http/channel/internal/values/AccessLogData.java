//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2008
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//@(#) 1.2 SERV1/ws/code/http.channel.impl/src/com/ibm/ws/http/channel/values/impl/AccessLogData.java, WAS.channel.http, WASX.SERV1, ff1301.02 9/20/11 16:56:42 [1/15/13 18:11:12]
//Change History:
//Date     UserId      Defect          Description
//--------------------------------------------------------------------------------
//08262011	cmmeyer		F009742			This file added to CMVC
//03022015      mansal          PI36010                 Added AccessLogElapsedRequestTime to the AccessLogData

package com.ibm.ws.http.channel.internal.values;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wsspi.genericbnf.GenericKeys;
import com.ibm.wsspi.genericbnf.KeyMatcher;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpResponseMessage;

public abstract class AccessLogData extends GenericKeys {
    /** Counter of the number of values defined so far */
    private static int NEXT_ORDINAL = 0;
    /** List keeping track of all the values, used by the corresponding matcher */
    private static final List<AccessLogData> allKeys = new ArrayList<AccessLogData>();
    /** Matcher used for these enum objects */
    private static final KeyMatcher myMatcher = new KeyMatcher(true);

    static {
        new AccessLogCurrentTime();
        new AccessLogElapsedTime();
        new AccessLogFirstLine();
        new AccessLogLocalIP();
        new AccessLogLocalPort();
        new AccessLogQueryString();
        new AccessLogRemoteHost();
        new AccessLogRemoteIP();
        new AccessLogRemoteUser();
        new AccessLogRequestCookie();
        new AccessLogRequestHeaderValue();
        new AccessLogRequestMethod();
        new AccessLogRequestProtocol();
        new AccessLogResponseHeaderValue();
        new AccessLogResponseSize();
        new AccessLogResponseSizeB();
        new AccessLogStartTime();
        new AccessLogStatus();
        new AccessLogURLPath();
        new AccessLogElapsedRequestTime(); //PI36010
    }

    /**
     * Constructor for a generic access log data object.
     * 
     * @param name
     */
    public AccessLogData(String name) {
        super(name, nextOrdinal());
        allKeys.add(this);
        myMatcher.add(this);
    }

    /**
     * Get the next ordinal value.
     * 
     * @return int
     */
    private static synchronized int nextOrdinal() {
        return NEXT_ORDINAL++;
    }

    /**
     * Allow access to the list containing all of the enumerated values.
     * 
     * @return List<AccessLogData>
     */
    public static List<AccessLogData> getAllKeys() {
        return allKeys;
    }

    /**
     * Find the enumerated object that matchs the input name using the given
     * offset and length into that name. If none exist, then a null value is
     * returned.
     * 
     * @param name
     * @param offset - starting point in that name
     * @param length - length to use from that starting point
     * @return AccessLogData
     */
    public static AccessLogData match(String name, int offset, int length) {
        if (null == name)
            return null;
        return (AccessLogData) myMatcher.match(name, offset, length);
    }

    /**
     * Find the enumerated object that matchs the input name using the given
     * offset and length into that name. If none exist, then a null value is
     * returned.
     * 
     * @param name
     * @param offset - starting point in that name
     * @param length - length to use from that offset
     * @return AccessLogData
     */
    public static AccessLogData match(byte[] name, int offset, int length) {
        if (null == name)
            return null;
        return (AccessLogData) myMatcher.match(name, offset, length);
    }

    public Object init(String rawToken) {
        return null;
    }

    /**
     * This abstract method is extended by all the specialized accessLogData
     * classes that implement their own set method. For instance the
     * AccessLogFirst class will implement the set method to set the
     * first line of the request in the access log entry
     * 
     * @param accessLogEntry StringBuilder for the line being built
     * @param response HttpResponseMessage to populate the fields in the line
     * @param request HttpRequestMessage to populate the fields in the line
     * @return Indicates if the status worked or not
     */
    public abstract boolean set(StringBuilder accessLogEntry, HttpResponseMessage response, HttpRequestMessage request, Object initData);

    protected void logSafe(StringBuilder accessLogEntry, String value) {
        if (value == null) {
            accessLogEntry.append('-');
        } else {
            accessLogEntry.append(value);
        }
    }
}
