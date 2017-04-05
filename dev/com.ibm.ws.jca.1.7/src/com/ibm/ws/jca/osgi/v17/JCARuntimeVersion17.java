/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.osgi.v17;

import org.osgi.framework.Version;

import com.ibm.ws.jca.osgi.JCARuntimeVersion;

public class JCARuntimeVersion17 implements JCARuntimeVersion {

    @Override
    public Version getVersion() {
        return VERSION_1_7;
    }

}
