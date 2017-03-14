// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.http.channel.exception;

import java.io.IOException;

public class WriteBeyondContentLengthException extends IOException {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3258407331258708534L;

    public WriteBeyondContentLengthException() {
        super();
    }

    public WriteBeyondContentLengthException(String message) {
        super(message);
    }
}
