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
package com.ibm.ws.classloading;

import static com.ibm.wsspi.classloading.ApiType.API;
import static com.ibm.wsspi.classloading.ApiType.IBMAPI;
import static com.ibm.wsspi.classloading.ApiType.SPEC;
import static com.ibm.wsspi.classloading.ApiType.THIRDPARTY;
import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.ibm.wsspi.classloading.ApiType;

public class ApiTypeTest {
    @Test
    public void testApiTypeParsing() {
        assertEquals(API, ApiType.fromString(null));
        assertEquals(null, ApiType.fromString(""));
        assertEquals(null, ApiType.fromString("ibmapi"));
        assertEquals(null, ApiType.fromString("IBMAPI"));
        assertEquals(SPEC, ApiType.fromString("spec"));
        assertEquals(IBMAPI, ApiType.fromString("ibm-api"));
        assertEquals(API, ApiType.fromString("api"));
        assertEquals(THIRDPARTY, ApiType.fromString("third-party"));
    }

    @Test
    public void testApiTypeSetParsing() {
        assertEquals(set(), ApiType.createApiTypeSet(""));
        assertEquals(set(), ApiType.createApiTypeSet("rubbish"));
        assertEquals(set(), ApiType.createApiTypeSet((String[]) null));
        assertEquals(set(), ApiType.createApiTypeSet(null, null));
        assertEquals(set(SPEC), ApiType.createApiTypeSet("spec"));
        assertEquals(set(SPEC, IBMAPI), ApiType.createApiTypeSet("spec,ibm-api"));
        assertEquals(set(SPEC, IBMAPI, API, THIRDPARTY), ApiType.createApiTypeSet("spec ibm-api,random,junk,api, third-party"));
        assertEquals(set(SPEC, IBMAPI, API, THIRDPARTY), ApiType.createApiTypeSet("spec ibm-api,random", "junk,api, third-party"));
    }

    private static Set<ApiType> set(ApiType... types) {
        return new HashSet<ApiType>(Arrays.asList(types));
    }
}
