/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.beanvalidation.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import javax.validation.ParameterNameProvider;

public class MockParameterNameProvider implements ParameterNameProvider {

    @Override
    public List<String> getParameterNames(Constructor<?> arg0) {
        return null;
    }

    @Override
    public List<String> getParameterNames(Method arg0) {
        return null;
    }

}
