/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.jaas.common.internal;

import static org.junit.Assert.assertEquals;

import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import org.junit.Test;

/**
 *
 */
public class JAASLoginModuleConfigImplTest {

    @Test
    public void getFlag_OPTIONAL() throws Exception {
        assertEquals("Did not get expected OPTIONAL control flag",
                     LoginModuleControlFlag.OPTIONAL, JAASLoginModuleConfigImpl.setControlFlag("OPTIONAL"));
    }

    @Test
    public void getFlag_REQUIRED() throws Exception {
        assertEquals("Did not get expected REQUIRED control flag",
                     LoginModuleControlFlag.REQUIRED, JAASLoginModuleConfigImpl.setControlFlag("required"));
    }

    @Test
    public void getFlag_REQUISITE() throws Exception {
        assertEquals("Did not get expected REQUISITE control flag",
                     LoginModuleControlFlag.REQUISITE, JAASLoginModuleConfigImpl.setControlFlag("requisite"));
    }

    @Test
    public void getFlag_SUFFICIENT() throws Exception {
        assertEquals("Did not get expected SUFFICIENT control flag",
                     LoginModuleControlFlag.SUFFICIENT, JAASLoginModuleConfigImpl.setControlFlag("sufficient"));
    }

    @Test
    public void getFlag_default() throws Exception {
        assertEquals("Did not get expected REQUIRED control flag",
                     LoginModuleControlFlag.REQUIRED, JAASLoginModuleConfigImpl.setControlFlag("asdfi pweyrugo"));
        assertEquals("Did not get expected REQUIRED control flag",
                     LoginModuleControlFlag.REQUIRED, JAASLoginModuleConfigImpl.setControlFlag(null));
    }

}
