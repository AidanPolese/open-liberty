package com.ibm.ws.wsoc;

public class WsocBufferException extends Exception {

    private static final long serialVersionUID = 403158310199997546L;

    String message = null;

    public WsocBufferException(String s) {
        super();
        message = s;
    }

    public WsocBufferException(Throwable cause) {
        super(cause);
    }

}
