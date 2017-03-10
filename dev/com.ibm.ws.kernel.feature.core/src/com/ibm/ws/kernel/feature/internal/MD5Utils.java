/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.feature.internal;

import java.io.File;
import java.io.IOException;

/**
 * Replaced by HashUtils
 */
@Deprecated
public class MD5Utils {

    @Deprecated
    public static String getMD5String(String str) {
        return HashUtils.getMD5String(str);
    }

    @Deprecated
    public static String getFileMD5String(File file) throws IOException {
        return HashUtils.getFileMD5String(file);
    }
}
