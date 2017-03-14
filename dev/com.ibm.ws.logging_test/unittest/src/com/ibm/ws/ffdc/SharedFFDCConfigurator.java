/**
 *
 */
package com.ibm.ws.ffdc;

import java.lang.reflect.Field;
import java.util.Map;

import com.ibm.ws.logging.internal.impl.BaseFFDCService;
import com.ibm.wsspi.logprovider.FFDCFilterService;

public class SharedFFDCConfigurator extends FFDCConfigurator {

    static Field incidents = null;

    public static void clearDelegates() {
        FFDCConfigurator.delegate = null;

        FFDCConfigurator.loggingConfig.set(null);
    }

    public static void setDelegate(FFDCFilterService mockservice) {
        FFDCConfigurator.delegate = mockservice;
    }

    public static FFDCFilterService getDelegate() {
        return FFDCConfigurator.getDelegate();
    }

    public static void clearFFDCIncidents() throws Exception {
        if (incidents == null) {
            incidents = BaseFFDCService.class.getDeclaredField("incidents");
            incidents.setAccessible(true);
        }

        @SuppressWarnings("rawtypes")
        Map map = (Map) incidents.get(getDelegate());
        map.clear();
    }
}