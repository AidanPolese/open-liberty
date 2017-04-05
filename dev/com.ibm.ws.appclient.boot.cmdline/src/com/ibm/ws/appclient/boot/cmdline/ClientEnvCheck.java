/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.appclient.boot.cmdline;

import com.ibm.ws.appclient.boot.ClientLauncher;
import com.ibm.ws.kernel.boot.cmdline.EnvCheck;

/**
 * Check's the version of the Java running before starting the client or running commands,
 * if Java 5 (or below) is being used a translated error message is thrown.
 */
public class ClientEnvCheck extends EnvCheck {

    /**
     * @param args - will just get passed onto Launcher if version check is successful
     */
    public static void main(String[] args) {
        EnvCheck.main(args, new ClientLauncher());
    }
}
