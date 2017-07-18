/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.kernel.service.util;

import java.io.File;
import java.net.URL;

import com.ibm.wsspi.kernel.service.utils.PathUtils;

public class UtilityTemplate {

    public File getUtilityJar() {
        URL home = getClass().getProtectionDomain().getCodeSource().getLocation();

        if (!home.getProtocol().equals("file")) {
            return null;
        }
        String path = PathUtils.normalize(home.getPath());
        return new File(path);
    }

    public String getInstallDir() {
        String installDir = System.getenv("WLP_INSTALL_DIR");
        if (installDir == null) {
            File utilityJarFile = getUtilityJar();

            if (utilityJarFile == null) {
                installDir = System.getProperty("user.dir") + File.separator;
            } else {
                installDir = utilityJarFile.getParentFile().getParentFile().getAbsolutePath() + File.separator;
            }
        } else {
            if (!(installDir.endsWith("/") || installDir.endsWith("\\"))) {
                installDir = installDir + File.separator;
            }
        }

        return installDir;
    }

    public String getUserDir() {
        String usrDir = System.getenv("WLP_USER_DIR");

        if (usrDir == null) {
            usrDir = getInstallDir() + "usr" + File.separator;
        } else {
            if (!(usrDir.endsWith("/") || usrDir.endsWith("\\"))) {
                usrDir = usrDir + File.separator;
            }
        }

        return usrDir;
    }

    /**
     * @param serverName The name of the server, if a specific server environment is being targeted. Otherwise null.
     * @return The effective WLP_OUTPUT_DIR for this environment
     */
    public String getOutputDir(String serverName) {
        String outputDir = System.getenv("WLP_OUTPUT_DIR");

        if (outputDir == null) {
            outputDir = getUserDir() + "servers" + File.separator;
        } else {
            if (!(outputDir.endsWith("/") || outputDir.endsWith("\\"))) {
                outputDir = outputDir + File.separator;
            }
        }

        return outputDir;
    }

}
