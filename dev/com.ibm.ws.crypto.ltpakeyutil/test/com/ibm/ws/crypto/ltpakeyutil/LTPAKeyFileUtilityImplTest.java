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
package com.ibm.ws.crypto.ltpakeyutil;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class LTPAKeyFileUtilityImplTest {

    @Test
    public void testLTPAKeyGeneration() throws Exception {
    	LTPAKeyFileUtilityImpl creator = new LTPAKeyFileUtilityImpl();
        Properties keyInfo = creator.generateLTPAKeys("WebAS".getBytes(), "myRealm");

        // Check the secret key.
        Assert.assertNotNull(keyInfo.get(LTPAKeyFileUtility.KEYIMPORT_SECRETKEY));

        // Check the private key.
        Assert.assertNotNull(keyInfo.get(LTPAKeyFileUtility.KEYIMPORT_PRIVATEKEY));

        // Check the public key.
        Assert.assertNotNull(keyInfo.get(LTPAKeyFileUtility.KEYIMPORT_PUBLICKEY));

        // Check the realm.
        Assert.assertEquals("myRealm", keyInfo.get(LTPAKeyFileUtility.KEYIMPORT_REALM));

        // Check the host.
        Assert.assertNotNull(keyInfo.get(LTPAKeyFileUtility.CREATION_HOST_PROPERTY));

        // Check the version.
        Assert.assertNotNull(keyInfo.get(LTPAKeyFileUtility.LTPA_VERSION_PROPERTY));

        // Check the creation date.
        Assert.assertNotNull(keyInfo.get(LTPAKeyFileUtility.CREATION_DATE_PROPERTY));
    }

}
