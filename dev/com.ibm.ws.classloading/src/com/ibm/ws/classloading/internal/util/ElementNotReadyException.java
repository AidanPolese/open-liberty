package com.ibm.ws.classloading.internal.util;

public class ElementNotReadyException extends ElementNotFetchedException {
    private static final long serialVersionUID = 1L;

    public ElementNotReadyException() {}

    public ElementNotReadyException(String message) {
        super(message);
    }

    public ElementNotReadyException(Throwable cause) {
        super(cause);
    }

    public ElementNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }

}
