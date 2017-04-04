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
package com.ibm.ws.jaxb.tools.ant;

import java.io.PrintStream;

import org.apache.tools.ant.BuildException;

import com.ibm.ws.jaxb.tools.internal.JaxbToolsConstants;
import com.ibm.ws.jaxb.tools.internal.JaxbToolsUtil;
import com.sun.tools.xjc.XJC2Task;

/**
 *
 */
public class XJCTask extends XJC2Task {
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
            String errMsg = JaxbToolsUtil.formatMessage(JaxbToolsConstants.ERROR_PARAMETER_TARGET_MISSED_KEY);
            err.print(errMsg);
            return;
        }

        super.execute();
    }
}
