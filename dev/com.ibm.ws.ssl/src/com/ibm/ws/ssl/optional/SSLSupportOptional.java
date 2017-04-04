/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ssl.optional;

import com.ibm.wsspi.ssl.SSLSupport;

/**
 * Marker interface for optionally configured SSL subsystem
 */
public interface SSLSupportOptional extends SSLSupport {

    public static final String KEYSTORE_IDS = "keystoreIds";

    public static final String REPERTOIRE_IDS = "repertoireIds";

    public static final String REPERTOIRE_PIDS = "repertoirePIDs";

}
