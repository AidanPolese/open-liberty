/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.kernel.boot.internal.FileUtils;

import test.common.SharedOutputManager;
import test.shared.TestUtils;

public class SharedBootstrapConfig extends BootstrapConfig {

    public static SharedBootstrapConfig createSharedConfig(SharedOutputManager outputMgr) {
        try {
            return new SharedBootstrapConfig();
        } catch (IOException e) {
            outputMgr.failWithThrowable("createSharedConfig", e);
            // unreachable: make compiler happy
            throw new RuntimeException(e);
        }
    }

    public static SharedBootstrapConfig createSharedConfig(SharedOutputManager outputMgr, String serverName) {
        try {
            return new SharedBootstrapConfig(serverName);
        } catch (IOException e) {
            outputMgr.failWithThrowable("createSharedConfig", e);
            // unreachable: make compiler happy
            throw new RuntimeException(e);
        }
    }

    private SharedBootstrapConfig(String serverName) throws IOException {
        this.processName = serverName;

        File root = TestUtils.createTempDirectory(serverName);
        if (root == null || !root.exists())
            throw new IllegalArgumentException("root directory does not exist");

        final String rootDirStr = root.getAbsolutePath();

        HashMap<String, String> map = new HashMap<String, String>();

        this.findLocations(serverName, rootDirStr, null, null, null);
        this.configure(map);
    }

    private SharedBootstrapConfig() throws IOException {
        this("defaultServer");
    }

    public void setInitProps(Map<String, String> initProps) {
        this.initProps = initProps;
    }

    public void cleanServerDir() {
        FileUtils.recursiveClean(getConfigFile(null));
    }
}