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
package com.ibm.ws.artifact.bundle.internal;

import com.ibm.wsspi.artifact.ArtifactNotifier;

/**
 * This is the simplest implementation of the notfier interface.
 * By returning false, it claims it is unable to support notification for any request.
 * This may need updating to allow web container to listen to web-inf etc.
 */
public class BundleNotifier implements ArtifactNotifier {

    /** {@inheritDoc} */
    @Override
    public boolean registerForNotifications(ArtifactNotification targets, ArtifactListener callbackObject) throws IllegalArgumentException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeListener(ArtifactListener listenerToRemove) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean setNotificationOptions(long interval, boolean useMBean) {
        return false;
    }

}
