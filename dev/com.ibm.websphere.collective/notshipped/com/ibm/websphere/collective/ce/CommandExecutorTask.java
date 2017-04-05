/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.collective.ce;

import java.util.Map;
import java.util.Properties;

/**
 *
 */
public interface CommandExecutorTask {

    public static final String STDOUT = "stdout";
    public static final String STDERR = "stderr";
    public static final String RETURN_CODE = "returnCode";
    public static final String RESULT_OBJECT = "resultObject";
    public static final String RESULT_OBJECT_TYPE = "resultObjectType";
    public static final String MESSAGE_KEY = "messageKey";

    public String getName();

    public Properties getRequiredParameters();

    public Properties getOptionalParameters();

    public boolean isApplicableTo(Properties targetProperties);

    public Map<String, Object> execute(Map<String, Object> taskParameters) throws IllegalArgumentException;

    public boolean isSucceeded(Map<String, Object> resultMap);

    public long getDefaultTimeout();

}
