/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxws.tools.ant;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.tools.ant.BuildException;

import com.ibm.ws.jaxws.tools.internal.JaxWsToolsConstants;
import com.ibm.ws.jaxws.tools.internal.JaxWsToolsUtil;
import com.sun.tools.ws.ant.WsImport2;

/**
 *
 */
public class WsImportTask extends WsImport2 {
    private static final PrintStream err = System.err;

    private boolean targetExisted = false;

    @Override
    public void setTarget(String version) {
        super.setTarget(version);
        this.targetExisted = true;
    }

    @Override
    public void execute() throws BuildException {
        if (!targetExisted) {
            String errMsg = JaxWsToolsUtil.formatMessage(JaxWsToolsConstants.ERROR_PARAMETER_TARGET_MISSED_KEY);
            err.print(errMsg);
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                System.setProperty("javax.xml.accessExternalSchema", "all");
                return null;
            }
        });

        super.execute();
    }

}
