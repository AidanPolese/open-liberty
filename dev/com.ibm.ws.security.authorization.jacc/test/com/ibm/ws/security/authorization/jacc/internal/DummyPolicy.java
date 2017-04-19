/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.authorization.jacc.internal;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;

public class DummyPolicy extends Policy {
    public DummyPolicy() {}

    @Override
    public PermissionCollection getPermissions(CodeSource codeSource) {
        return null;
    }

    @Override
    public void refresh() {}

    @Override
    public PermissionCollection getPermissions(ProtectionDomain domain) {
        return null;
    }

    @Override
    public boolean implies(ProtectionDomain pd, Permission p) {
        return true;
    }

}
