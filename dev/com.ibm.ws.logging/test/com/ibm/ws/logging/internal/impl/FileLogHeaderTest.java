/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import test.common.junit.matchers.RegexMatcher;

public class FileLogHeaderTest {
    private String printFileLogHeader(boolean trace, boolean javaLangInstrument) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(baos, true, "UTF-8");

            final String header = "header" + LoggingConstants.nl;
            FileLogHeader flh = new FileLogHeader(header, trace, javaLangInstrument);
            flh.print(out);
            byte[] bytes = baos.toByteArray();

            String s = new String(bytes, "UTF-8");
            Assert.assertThat(s, Matchers.containsString("header" + LoggingConstants.nl));
            Assert.assertThat(s, new RegexMatcher("\n$"));

            return s;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testPrintTrace() {
        Assert.assertThat(printFileLogHeader(false, false), Matchers.not(Matchers.containsString("trace.specification")));
        Assert.assertThat(printFileLogHeader(true, false), Matchers.containsString("trace.specification"));
    }

    @Test
    public void testPrintJavaLangInstrument() {
        Assert.assertThat(printFileLogHeader(false, false), Matchers.not(Matchers.containsString("java.lang.instrument")));
        Assert.assertThat(printFileLogHeader(true, false), Matchers.containsString("java.lang.instrument = false"));
        Assert.assertThat(printFileLogHeader(false, true), Matchers.not(Matchers.containsString("java.lang.instrument")));
        Assert.assertThat(printFileLogHeader(true, true), Matchers.not(Matchers.containsString("java.lang.instrument")));
    }
}
