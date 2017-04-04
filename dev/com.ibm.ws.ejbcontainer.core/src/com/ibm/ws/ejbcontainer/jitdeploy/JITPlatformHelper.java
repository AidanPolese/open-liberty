/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.jitdeploy;

import java.io.File;
import java.security.AccessController;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ejs.util.dopriv.SystemGetPropertyPrivileged;
import com.ibm.ws.ffdc.FFDCFilter;

/**
 * Just In Time Deployment utility methods, which are of general purpose use
 * for dealing with ASM, generating code, or managing class bytes. <p>
 */
final class JITPlatformHelper
{
    private static final String CLASS_NAME = JITPlatformHelper.class.getName();
    private static final TraceComponent tc = Tr.register(JITPlatformHelper.class,
                                                         JITUtils.JIT_TRACE_GROUP,
                                                         JITUtils.JIT_RSRC_BUNDLE);
    private static String svLogLocation = null;

    /**
     * Returns the path name of the logs directory under User Install Root.
     * The platform specific separator character is used and the path does
     * not end with a separator character. <p>
     *
     * Insures the thread is privileged to access the system property
     * the first time the method is called, then caches the value. <p>
     **/
    // d457086
    static String getLogLocation()
    {
        if (svLogLocation == null)
        {
            try
            {
                String userInstallRoot = AccessController.doPrivileged
                                (new SystemGetPropertyPrivileged("user.install.root", null));
                if (userInstallRoot == null)
                    userInstallRoot = AccessController.doPrivileged
                                    (new SystemGetPropertyPrivileged("was.install.root", ""));
                svLogLocation = userInstallRoot + File.separator;
            } catch (Throwable ex)
            {
                FFDCFilter.processException(ex, CLASS_NAME + ".USER_INSTALL_ROOT", "419");
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "USER_INSTALL_ROOT() failed : " + ex.getMessage());
                svLogLocation = "";
            }
            svLogLocation = svLogLocation + "logs";
        }

        return svLogLocation;
    }

}
