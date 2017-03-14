/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal;

import java.util.Set;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

public class InternalPackageChecker<T> implements Action {

    private final Set<String> internalPackages;

    /**
     * @param internalPackages
     */
    public InternalPackageChecker(Set<String> internalPackages) {
        this.internalPackages = internalPackages;
    }

    public void describeTo(Description description) {
        description.appendText("testing internal package");
    }

    public Object invoke(Invocation invocation) throws Throwable {
        return internalPackages.contains(invocation.getParameter(0));
    }

    public static <T> Action checkIfPackageIsInSet(Set<String> internalPackages) {
        return new InternalPackageChecker<T>(internalPackages);
    }
}
