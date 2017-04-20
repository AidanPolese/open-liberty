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
package com.ibm.ws.metadata.ejb;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import com.ibm.ejs.container.ContainerConfigConstants;

public class CheckEJBAppConfigHelperHelper {
    public static void setValidationFailable() {
        System.setProperty(ContainerConfigConstants.checkAppConfigProp, "true");
        CheckEJBAppConfigHelper.refreshCheckEJBAppConfigSetting();
    }

    public static void unsetValidationFailable() {
        System.getProperties().remove(ContainerConfigConstants.checkAppConfigProp);
        CheckEJBAppConfigHelper.refreshCheckEJBAppConfigSetting();
    }

    public static void failable(PrivilegedExceptionAction<?> action) throws Exception {
        setValidationFailable();
        try {
            action.run();
        } catch (PrivilegedActionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException(cause);
        } finally {
            unsetValidationFailable();
        }
    }
}
