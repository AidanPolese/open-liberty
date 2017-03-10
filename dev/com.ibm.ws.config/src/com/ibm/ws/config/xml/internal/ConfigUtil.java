/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *
 * Change activity:
 *
 * Issue Date Name Description
 * ----------- ----------- -------- ------------------------------------
 *
 */

package com.ibm.ws.config.xml.internal;

import java.io.Closeable;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

class ConfigUtil {

    private static final TraceComponent tc = Tr.register(ConfigUtil.class, XMLConfigConstants.TR_GROUP, XMLConfigConstants.NLS_PROPS);

    @FFDCIgnore(Throwable.class)
    static public void closeIO(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
        }
    }

    static public boolean exists(final File file) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return file.exists();
            }
        });
    }

    static public void delete(final File file) {
        boolean success = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return file.delete();
            }
        });
        if (!success && tc.isWarningEnabled()) {
            Tr.warning(tc, "warn.file.delete.failed", file);
        }
    }

    static public void mkdirs(final File file) {
        boolean success = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return file.mkdirs();
            }
        });
        if (!success && tc.isWarningEnabled()) {
            Tr.warning(tc, "warn.file.mkdirs.failed", file);
        }
    }

    static public String getSystemProperty(final String name) {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(name);
            }
        });
    }
}
