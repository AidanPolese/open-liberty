/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.common.config;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class CommonConfigUtils {
    public static final TraceComponent tc = Tr.register(CommonConfigUtils.class);

    public static String[] trim(final String[] originals) {
        if (originals == null || originals.length == 0) {
            return null;
        }
        String[] tmpResults = new String[originals.length];
        int numTrimmedEntries = 0;
        for (int i = 0; i < originals.length; i++) {
            String original = trim(originals[i]);
            if (original != null) {
                tmpResults[numTrimmedEntries++] = original;
            }
        }
        if (numTrimmedEntries == 0) {
            return null;
        }
        String[] results = new String[numTrimmedEntries];
        System.arraycopy(tmpResults, 0, results, 0, numTrimmedEntries);
        return results;
    }

    /**
     * Calls {@code java.lang.String.trim()} on the provided value.
     * 
     * @param original
     * @return {@code null} if {@code original} is {@code null} or empty after the {@code java.lang.String.trim()} operation.
     *         Otherwise returns the trimmed result.
     */
    public static String trim(String original) {
        if (original == null) {
            return null;
        }
        String result = original.trim();
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

}
