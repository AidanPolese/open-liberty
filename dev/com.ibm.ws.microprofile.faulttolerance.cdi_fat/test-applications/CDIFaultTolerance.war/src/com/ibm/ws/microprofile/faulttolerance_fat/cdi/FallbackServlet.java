/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.faulttolerance_fat.cdi;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.FallbackBean;
import com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans.FallbackBeanWithoutRetry;
import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;
import com.ibm.ws.microprofile.faulttolerance_fat.util.Connection;

import componenttest.app.FATServlet;

/**
 * Servlet implementation class Test
 */
@WebServlet("/fallback")
public class FallbackServlet extends FATServlet {
    private static final long serialVersionUID = 1L;

    @Inject
    FallbackBean bean;

    @Inject
    FallbackBeanWithoutRetry beanWithoutRetry;

    public void testFallback(HttpServletRequest request,
                             HttpServletResponse response) throws ServletException, IOException, ConnectException, NoSuchMethodException, SecurityException {
        //should be retried twice and then fallback and we get a result
        Connection connection = bean.connectA();
        String data = connection.getData();
        assertThat(data, equalTo("Fallback for: connectA - data!"));
        assertThat("Call count", bean.getConnectCountA(), is(3));
    }

    public void testFallbackWithoutRetry(HttpServletRequest request,
                                         HttpServletResponse response) throws ServletException, IOException, ConnectException, NoSuchMethodException, SecurityException {
        //should fallback immediately
        Connection connection = beanWithoutRetry.connectA();
        String data = connection.getData();
        assertThat(data, equalTo("Fallback for: connectA - data!"));
        assertThat("Call count", beanWithoutRetry.getConnectCountA(), is(1));
    }

    /**
     * This test should only pass if MP_Fault_Tolerance_NonFallback_Enabled is set to false
     */
    public void testFallbackRetryDisabled() throws ConnectException {
        Connection connection = bean.connectA();
        String data = connection.getData();
        assertThat(data, equalTo("Fallback for: connectA - data!"));
        // Connect count should only be 1 since retry is disabled
        assertThat("Call count", bean.getConnectCountA(), is(1));
    }

}
