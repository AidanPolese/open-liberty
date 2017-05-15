/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package wlp.lib.extract;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 *
 */
public class ReturnCode {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(SelfExtract.class.getName() + "Messages");
    public static final ReturnCode OK = new ReturnCode(0);
    public static final int NOT_FOUND = 1;
    public static final int UNREADABLE = 2;
    public static final int BAD_INPUT = 3;
    public static final int BAD_OUTPUT = 4;
    public static final int NOT_APPLICABLE_FEATURE = 5;

    private final int code;
    private final String msgKey;
    private final Object[] params;

    public ReturnCode(int code, String msgKey, Object[] params) {
        this.code = code;
        this.msgKey = msgKey;
        this.params = params;
    }

    /**
     * @param ok2
     */
    public ReturnCode(int code) {
        this(code, null, (Object[]) null);
    }

    /**
     * @param code
     * @param msgKey
     * @param params
     */
    public ReturnCode(int code, String msgKey, String params) {
        this(code, msgKey, new Object[] { params });
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        if (msgKey == null) {
            return "";
        }
        return MessageFormat.format(resourceBundle.getString(msgKey), params);
    }

    public String getMessageKey() {
        return msgKey;
    }

    public Object[] getParameters() {
        return (null == params || params.length == 0) ? new Object[0] : Arrays.copyOf(params, params.length);
    }
}