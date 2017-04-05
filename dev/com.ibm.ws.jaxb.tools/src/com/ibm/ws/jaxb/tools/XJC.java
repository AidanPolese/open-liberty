/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxb.tools;

import java.io.PrintStream;

import com.ibm.ws.jaxb.tools.internal.JaxbToolsConstants;
import com.ibm.ws.jaxb.tools.internal.JaxbToolsUtil;

/**
 * IBM Wrapper for XJC tool.
 */
public class XJC {
    private static final PrintStream err = System.err;

    public static void main(String args[]) throws java.lang.Throwable {
        if (isTargetRequired(args)) {
            String errMsg = JaxbToolsUtil.formatMessage(JaxbToolsConstants.ERROR_PARAMETER_TARGET_MISSED_KEY);
            err.println(errMsg);

            return;
        }

        com.sun.tools.xjc.Driver.main(args);
    }

    private static boolean isTargetRequired(String[] args) {
        boolean helpExisted = false;
        boolean versionExisted = false;
        boolean targetExisted = false;

        for (String arg : args) {
            if (arg.equals(JaxbToolsConstants.PARAM_HELP)) {
                helpExisted = true;
            } else if (arg.equals(JaxbToolsConstants.PARAM_VERSION)) {
                versionExisted = true;
            } else if (arg.equals(JaxbToolsConstants.PARAM_TARGET)) {
                targetExisted = true;
            }

            continue;
        }

        return args.length > 0 && !helpExisted && !versionExisted && !targetExisted;
    }
}
