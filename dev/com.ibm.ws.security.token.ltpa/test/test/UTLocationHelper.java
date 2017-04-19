/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

import test.common.SharedLocationManager;
import test.common.SharedOutputManager;

import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;

@Ignore("This is not a test class")
public class UTLocationHelper {
    /** Test data directory: note the space! always test paths with spaces. */
    public static final String TEST_DATA_DIR = "test-resources/test data";
    public static final String TEST_SERVER = "com.ibm.ws.security.token.ltpa_test";

    public static WsLocationAdmin getLocationManager() {
        SharedLocationManager.resetWsLocationAdmin();
        try {
            WsLocationAdmin locSvc = (WsLocationAdmin) SharedLocationManager.getLocationInstance();
            if (locSvc == null) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("server.output.dir", TEST_DATA_DIR + "/output");
                SharedLocationManager.createLocations(TEST_DATA_DIR, TEST_SERVER, map);
                locSvc = (WsLocationAdmin) SharedLocationManager.getLocationInstance();
            }
            return locSvc;
        } catch (Throwable t) {
            SharedOutputManager.getInstance().failWithThrowable("getLocationManager", t);
            return null; // unreachable
        }
    }

}
