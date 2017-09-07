/*
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt;

import com.ibm.ws.ssl.KeyStoreService;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.ssl.SSLSupport;

public interface MicroProfileJwtService {

    /**
     * @return
     */
    public AtomicServiceReference<SSLSupport> getSslSupportRef();

    /**
     * @return
     */
    public SSLSupport getSslSupport();

    /**
     * @return
     */
    AtomicServiceReference<KeyStoreService> getKeyStoreServiceRef();

}
