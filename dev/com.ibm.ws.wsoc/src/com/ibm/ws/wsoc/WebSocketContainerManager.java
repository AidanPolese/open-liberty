package com.ibm.ws.wsoc;

import java.util.concurrent.ConcurrentHashMap;

import com.ibm.ws.common.internal.encoder.Base64Coder;
import com.ibm.ws.wsoc.injection.InjectionProvider;
import com.ibm.ws.wsoc.injection.InjectionProvider12;
import com.ibm.ws.wsoc.util.ByteGenerator;
import com.ibm.ws.wsoc.util.IDGenerator;

public class WebSocketContainerManager {

    public final static String SERVER_CONTAINER_ATTRIBUTE = "javax.websocket.server.ServerContainer";

    private final IDGenerator idMaker = new IDGenerator();

    private final ByteGenerator maskKeyMaker = new ByteGenerator();

    private final ConcurrentHashMap depInjectionMap = new ConcurrentHashMap();

    public void releaseCC(Object key) {

        InjectionProvider12 ip12 = ServiceManager.getInjectionProvider12();
        if (ip12 != null) {

            ip12.releaseCC(key, depInjectionMap);
        } else {

            InjectionProvider ip = ServiceManager.getInjectionProvider();

            if (ip != null) {
                ip.releaseCC(key, depInjectionMap);
            }
        }
    }

    public ConcurrentHashMap getEndpointMap() {
        return depInjectionMap;
    }

    /** Singleton instance of this class */
    private static WebSocketContainerManager singletonInstance = null;

    public static final WebSocketContainerManager getRef() {
        if (null == singletonInstance) {
            createSingleton();
        }
        return singletonInstance;
    }

    static private synchronized void createSingleton() {
        if (null == singletonInstance) {
            singletonInstance = new WebSocketContainerManager();
        }
    }


    public synchronized String generateNewId() {
        String s = idMaker.getID();
        return s;
    }

    public synchronized byte[] generateNewMaskKey() {
        return maskKeyMaker.getID();

    }

    public synchronized String generateWebsocketKey() {
        String s = Base64Coder.encode(maskKeyMaker.getWebsocketKey());
        return s;
    }

}
