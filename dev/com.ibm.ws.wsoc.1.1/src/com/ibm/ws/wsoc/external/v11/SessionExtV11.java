package com.ibm.ws.wsoc.external.v11;

import javax.websocket.MessageHandler.Partial;
import javax.websocket.MessageHandler.Whole;
import javax.websocket.Session;

import com.ibm.ws.wsoc.SessionImpl;
import com.ibm.ws.wsoc.external.SessionExt;

public class SessionExtV11 extends SessionExt implements Session {

    public SessionExtV11() {
        super();
    }

    @Override
    public void initialize(SessionImpl _impl) {
        super.initialize(_impl);
    }

    @Override
    public <T> void addMessageHandler(Class<T> clazz, Whole<T> handler) {
        super.getSessionImpl().addMessageHandler(clazz, handler);

    }

    @Override
    public <T> void addMessageHandler(Class<T> clazz, Partial<T> handler) {
        super.getSessionImpl().addMessageHandler(clazz, handler);
    }

}
