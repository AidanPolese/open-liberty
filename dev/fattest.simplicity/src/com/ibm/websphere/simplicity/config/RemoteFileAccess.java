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
package com.ibm.websphere.simplicity.config;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;

/**
 * ConfigElement representing the remoteFileAccess element
 * See https://developer.ibm.com/wasdev/docs/using-file-service-and-file-transfer-mbeans-with-liberty/
 */
public class RemoteFileAccess extends ConfigElement {

    public static final String WLP_INSTALL_DIR_VAR = "${wlp.install.dir}";
    public static final String WLP_USER_DIR_VAR = "${wlp.user.dir}";
    public static final String SERVER_CONFIG_DIR_VAR = "${server.config.dir}";
    public static final String SERVER_OUTPUT_DIR_VAR = "${server.output.dir}";

    @XmlElement(name = "writeDir")
    private Set<String> writeDirs;

    @XmlElement(name = "readDir")
    private Set<String> readDirs;

    public Set<String> getWriteDirs() {
        if (this.writeDirs == null) {
            this.writeDirs = new TreeSet<String>();
        }
        return this.writeDirs;
    }

    public Set<String> getReadDirs() {
        if (this.readDirs == null) {
            this.readDirs = new TreeSet<String>();
        }
        return this.readDirs;
    }

    @Override
    public RemoteFileAccess clone() throws CloneNotSupportedException {
        RemoteFileAccess clone = (RemoteFileAccess) super.clone();

        if (this.writeDirs != null) {
            clone.writeDirs = new TreeSet<String>();
            for (String writeDir : this.writeDirs)
                clone.writeDirs.add(writeDir);
        }

        if (this.readDirs != null) {
            clone.readDirs = new TreeSet<String>();
            for (String readDir : this.readDirs)
                clone.readDirs.add(readDir);
        }

        return clone;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("RemoteFileAccess{");
        if (this.writeDirs != null) {
            for (String writeDir : writeDirs) {
                buf.append("writeDir=\"" + writeDir + "\" ");
            }
        }

        if (this.readDirs != null) {
            for (String readDir : readDirs) {
                buf.append("readDir=\"" + readDir + "\" ");
            }
        }
        buf.append("}");

        return buf.toString();
    }
}
