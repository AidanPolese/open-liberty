/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.util;

import java.io.IOException;
import java.io.ObjectInputStream;

// TODO - remove; see design issue 57448.
public class WsObjectInputStream extends ObjectInputStream {
    protected WsObjectInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
}
