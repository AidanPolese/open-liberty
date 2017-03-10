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
package com.ibm.ws.kernel.provisioning.packages;

import java.util.Iterator;

/**
 *
 */
public interface SharedPackageInspector {

    public static interface PackageType {
        /** IBM API type="api" */
        boolean isUserDefinedApi();

        /** IBM API type="ibm-api" */
        boolean isIbmApi();

        /** IBM API type="internal" */
        boolean isInternalApi();

        /** IBM-API type="spec" or type="spec:osgi" */
        boolean isSpecApi();

        /** IBM-API type="spec" only */
        boolean isStrictSpecApi();

        /** IBM-API type="third-party" only */
        boolean isThirdPartyApi();

        /** IBM-API type="spec" or type="spec:osgi" or type="third-party" */
        boolean isSpecOrThirdPartyApi();

        /** IBM-API type="spec:osgi" */
        public boolean isSpecOsgiApi();

    }

    Iterator<String> listApiPackages();

    PackageType getExportedPackageType(String packageName);
}
