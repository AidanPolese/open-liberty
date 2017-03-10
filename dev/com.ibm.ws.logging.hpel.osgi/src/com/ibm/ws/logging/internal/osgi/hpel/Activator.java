/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal.osgi.hpel;

import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.ibm.ws.logging.hpel.config.HpelConfigurator;

/**
 * Activator for the HPEL bundle.
 */
public class Activator implements BundleActivator {
    private AbstractHPELConfigService[] hpelConfigService = null;

    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception {
        hpelConfigService =
                        new AbstractHPELConfigService[]
                        {
                         new AbstractHPELConfigService(context, "com.ibm.ws.logging.binaryLog") {
                             @Override
                             void forwardUpdated(Map<String, Object> map) {
                                 HpelConfigurator.updateLog(map);
                             }
                         },
                         new AbstractHPELConfigService(context, "com.ibm.ws.logging.binaryTrace") {
                             @Override
                             void forwardUpdated(Map<String, Object> map) {
                                 HpelConfigurator.updateTrace(map);
                             }
                         }
//                                           ,
//                                           new AbstractHPELConfigService(context, "com.ibm.ws.logging.textLog") {
//                                               @Override
//                                               void forwardUpdated(Map<String, Object> map) {
//                                                   HpelConfigurator.updateText(map);
//                                               }
//
//                                           }
                        };
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception {
        if (hpelConfigService != null) {
            for (AbstractHPELConfigService service : hpelConfigService) {
                service.stop();
            }
            hpelConfigService = null;
        }
    }

}
