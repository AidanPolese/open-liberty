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
 */
package com.ibm.ws.kernel.launch.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Container for the results of installing a set of bundles: A) A list of
 * Bundles that were successfully installed, B) A list of Strings naming bundles
 * that could not be located, and C) A map of String-Exception pairs for bundles
 * that caused exceptions when being loaded.
 * 
 * <p>
 * It is up to the caller to use this information to provide appropriate
 * diagnostics.
 */
public class BundleInstallStatus {
    private List<Bundle> bundlesToStart = null;
    private List<String> missingBundles = null;

    private Map<String, Throwable> installExceptions = null;

    public boolean bundlesMissing() {
        return missingBundles != null;
    }

    public boolean bundlesToStart() {
        return bundlesToStart != null;
    }

    public boolean installExceptions() {
        return installExceptions != null;
    }

    public List<String> getMissingBundles() {
        return missingBundles;
    }

    public List<Bundle> getBundlesToStart() {
        return bundlesToStart;
    }

    public Map<String, Throwable> getInstallExceptions() {
        return installExceptions;
    }

    public void addMissingBundle(String bundleKey) {
        if (missingBundles == null)
            missingBundles = new ArrayList<String>();

        missingBundles.add(bundleKey);
    }

    @Trivial
    public void addBundleToStart(Bundle bundle) {
        if (bundlesToStart == null)
            bundlesToStart = new ArrayList<Bundle>();

        bundlesToStart.add(bundle);
    }

    public void addInstallException(String bundleKey, Throwable e) {
        if (installExceptions == null)
            installExceptions = new HashMap<String, Throwable>();

        installExceptions.put(bundleKey, e);
    }

    public String listMissingBundles() {
        if (missingBundles == null)
            return null;

        StringBuilder list = new StringBuilder();
        Iterator<String> i = missingBundles.iterator();
        while (i.hasNext()) {
            list.append(i.next());
            if (i.hasNext())
                list.append(", ");
        }

        return list.toString();
    }

    public String traceInstallExceptions() {
        if (installExceptions == null)
            return null;

        StringBuilder strbuf = new StringBuilder();

        for (String key : installExceptions.keySet()) {
            strbuf.append("(");
            strbuf.append("bundle=").append(key);
            strbuf.append(", ");
            strbuf.append("ex=").append(installExceptions.get(key));
            strbuf.append("); ");
        }

        return strbuf.toString();
    }
}