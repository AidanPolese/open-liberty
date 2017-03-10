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
package com.ibm.ws.kernel.boot.archive;

import java.io.File;

public interface UnixModeHelper {
    public int getUnixMode(File f);
}