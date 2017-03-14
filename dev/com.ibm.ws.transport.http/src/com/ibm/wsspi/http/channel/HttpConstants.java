// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70. (C) COPYRIGHT International Business Machines Corp. 2004, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel;

/**
 * Public constants shared outside of the HTTP channel itself.
 * 
 * @ibm-private-in-use
 */
public interface HttpConstants {

    /** Key used in certain cases for session persistence */
    String SESSION_PERSISTENCE = "SessionPersistence";
    /** Key used on z/OS to mark the final outbound write of a message */
    String FINAL_WRITE_MARK = "HTTPFinalWrite";
    /** 342859 - Key used on z/OS to mark the initial read of a request message */
    String HTTPFirstRead = "HTTPFirstRead";
    /** 363633 - Store the read buffer size on z/OS for proxy use */
    String HTTPReadBufferSize = "zConfiguredHttpReadBufferSize";
    /** Key used on z/OS for an unlimited HTTP body size */
    String HTTPUnlimitedMessageMark = "UNLIMITED_HTTP_MESSAGE_SIZE";

}
