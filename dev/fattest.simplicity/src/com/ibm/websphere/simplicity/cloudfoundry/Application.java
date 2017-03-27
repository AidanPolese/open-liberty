/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.cloudfoundry;

import java.io.File;

public final class Application {

    private final String name;
    private final String directory;

    public Application(String appName, String appDir) {
        this.name = appName;
        this.directory = appDir;
    }

    public String getName() {
        return this.name;
    }

    public String getDirectory() {
        return this.directory;
    }

    public File getApplicationPath() {
        String absolutePath = System.getProperty("user.dir") + "/build/push";
        File appLocation = new File(absolutePath, this.directory);
        return appLocation;
    }

}
