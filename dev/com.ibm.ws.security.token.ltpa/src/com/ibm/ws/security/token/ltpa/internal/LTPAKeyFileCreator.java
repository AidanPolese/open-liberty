/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.token.ltpa.internal;

import java.util.Properties;

import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.crypto.ltpakeyutil.LTPAKeyFileUtility;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;

/**
 * Utility class to create the LTPA keys file.
 */
public interface LTPAKeyFileCreator extends LTPAKeyFileUtility {

    /**
     * Create the LTPA keys file at the specified location using
     * the specified password bytes.
     * <p>
     * Access the keyFile using the WsLocationAdmin
     *
     * @param locService
     * @param keyFile
     * @param keyPasswordBytes
     * @return A Properties object containing the various attributes created for the LTPA keys
     * @throws Exception
     */
    public Properties createLTPAKeysFile(WsLocationAdmin locService, String keyFile, @Sensitive byte[] keyPasswordBytes) throws Exception;

}