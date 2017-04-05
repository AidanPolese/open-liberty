//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2003, 2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
// 
//@(#) 1.8 SERV1/ws/code/ssl.channel.impl/src/com/ibm/ws/ssl/channel/impl/SSLChannelConstants.java, WAS.channel.ssl, WASX.SERV1 11/16/05 10:37:04 [4/22/09 10:47:38]
//
// Change History:
// Date     UserId      Defect          Description
// --------------------------------------------------------------------------------
// 10/06/04 gilgen      237428          correct resource bundle name
// 11/16/05 clanzen     311968          enable NLS lookups with a new static variable
// 04/24/09 wigger      576358.1        remove unnecessary nls reference.

package com.ibm.ws.channel.ssl.internal;

/**
 * This purpose of this interface is to consolidate Strings used throughout
 * the SSL Channel to prevent future changes from rippling to all files.
 */
public interface SSLChannelConstants {

    /** Name of SSL resource bundle for NLS messages. */
    String SSL_BUNDLE = "com.ibm.ws.channel.ssl.internal.resources.SSLChannelMessages";
    /** Name associated with Trace output. */
    String SSL_TRACE_NAME = "SSLChannel";

    // Message keys in nlsprops file

    /** Invalid security properties found in config */
    String INVALID_SECURITY_PROPERTIES = "invalid.security.properties";
    /** Error occurred during a handshake */
    String HANDSHAKE_FAILURE = "handshake.failure";
    boolean DEFAULT_HANDSHAKE_FAILURE = false;
    /** Informational message that handshake error will no longer be logged */
    String HANDSHAKE_FAILURE_STOP_LOGGING = "handshake.failure.stop.logging";
    long DEFAULT_HANDSHAKE_FAILURE_STOP_LOGGING = 100;
    /** PI52696 */
    public static final String TIMEOUT_VALUE_IN_SSL_CLOSING_HANDSHAKE = "timeoutValueInSSLClosingHandshake";
}
