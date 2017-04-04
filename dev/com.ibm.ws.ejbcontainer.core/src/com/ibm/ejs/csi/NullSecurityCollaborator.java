/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1999, 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.csi;

import java.security.Identity;

/**
 * This class remains for serialization compatibility only.
 */
@SuppressWarnings("deprecation")
public class NullSecurityCollaborator
{
    // d454046.1 - private class for creating singleton 
    // unauthenticated Identity object. 
    private static class UnauthenticatedIdentity extends java.security.Identity
    {
        private static final long serialVersionUID = -8903829931892409420L;

        private UnauthenticatedIdentity(String identity)
        {
            super(identity);
        }
    }

    // d454046.1 - the singleton unauthenticated Identity object.
    public static final Identity UNAUTHENTICATED = new UnauthenticatedIdentity("UNAUTHENTICATED");
} // SecurityCollaborator
