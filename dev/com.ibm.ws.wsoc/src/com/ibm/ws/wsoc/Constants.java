package com.ibm.ws.wsoc;

public class Constants {

    // Contants returned by the frame processing code, when asked to process a new buffer.
    // a non-negative return code is the position in the buffer where a new frame is assumed to start.
    // for this reason, these return codes are "int" and not an enumeration.
    // BP stands for Buffer Processing.
    public static final int BP_FRAME_ALREADY_COMPLETE = -3;
    public static final int BP_FRAME_IS_NOT_COMPLETE = -2;
    public static final int BP_FRAME_EXACTLY_COMPLETED = -1;

    public static final String HEADER_VALUE_UPGRADE = "Upgrade";
    public static final String HEADER_VALUE_WEBSOCKET = "websocket";
    public static final String HEADER_VALUE_FOR_SEC_WEBSOCKET_VERSION = "13";

    public static final String HEADER_NAME_HOST = "Host";
    public static final String HEADER_NAME_ORIGIN = "Origin";
    public static final String HEADER_NAME_UPGRADE = "Upgrade";
    public static final String HEADER_NAME_CONNECTION = "Connection";
    public static final String HEADER_NAME_SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
    public static final String HEADER_NAME_SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
    public static final String HEADER_NAME_SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    public static final String HEADER_NAME_SEC_WEBSOCKET_EXTENSIONS = "Sec-WebSocket-Extensions";

    public static final String MC_HEADER_NAME_SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";

    public static final long DEFAULT_MAX_MSG_SIZE = 32767;
    // javadoc says -1 for annotation max message sizie attribute means "unlimit", but is also the default, which doesn't play well
    // with there being a get/set max message size on the Session and Container object.  We will treat -1 as undefined, and the
    // Session and Container values will then have precedence.
    public static final long ANNOTATED_UNDEFINED_MAX_MSG_SIZE = -1;

    public static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    public static final int MAX_PING_SIZE = 125;
}
