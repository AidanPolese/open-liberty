/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.logging.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Dictionary;
import java.util.Hashtable;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.ibm.wsspi.zos.command.processing.ModifyResults;

public class LoggingCommandHandlerTest {

    final Mockery context = new JUnit4Mockery();

    LoggingCommandHandler lh;
    ConfigurationAdmin configAdmin;
    Configuration config;

    @Before
    public void setup() throws Exception {
        configAdmin = context.mock(ConfigurationAdmin.class, "configAdmin");
        config = context.mock(Configuration.class, "config");

        lh = new LoggingCommandHandler();
        lh.configuredTraceSpec = "*=info=enabled";
        lh.setConfigAdmin(configAdmin);

    }

    @After
    public void tearDown() {
        lh = null;
    }

    @Test
    public void testLifecycle() {
        lh = new LoggingCommandHandler();

        // Test setters
        assertNull(lh.configAdmin);

        lh.configuredTraceSpec = "*=info=enabled";
        lh.setConfigAdmin(configAdmin);
        assertSame(configAdmin, lh.configAdmin);
    }

    @Test
    public void testGetHelp() throws Exception {
        assertEquals(LoggingCommandHandler.HELP_TEXT, lh.getHelp());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(LoggingCommandHandler.NAME, lh.getName());
    }

    @Test
    public void testTrimQuotes() {
        assertEquals("foo", lh.trimQuotes("foo"));
        assertEquals("foo", lh.trimQuotes("'foo'"));
        assertEquals("", lh.trimQuotes("'something that's not terminated"));
        assertEquals("", lh.trimQuotes("'"));
        assertEquals("", lh.trimQuotes("''"));
    }

    @Test
    public void testHandleModifyResetQuotes() throws Exception {

        final Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(LoggingCommandHandler.TRACE_SPEC_KEY, "*=all=disabled");

        final ModifyResults modifyResults = context.mock(ModifyResults.class);
        context.checking(new Expectations() {
            {
                oneOf(configAdmin).getConfiguration(LoggingCommandHandler.LOGGING_PID, null);
                will(returnValue(config));

                oneOf(config).getProperties();
                will(returnValue(props));
                oneOf(config).update(props);

                oneOf(modifyResults).setCompletionStatus(ModifyResults.PROCESSED_COMMAND);
                oneOf(modifyResults).setResponsesContainMSGIDs(false);
                oneOf(modifyResults).setResponses(null);
            }
        });

        String commandString = "logging='reset'";
        lh.handleModify(commandString, modifyResults);
    }

    @Test
    public void testHandleModifyResetNoQuotes() throws Exception {

        final Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(LoggingCommandHandler.TRACE_SPEC_KEY, "*=all=disabled");

        final ModifyResults modifyResults = context.mock(ModifyResults.class);
        context.checking(new Expectations() {
            {
                oneOf(configAdmin).getConfiguration(LoggingCommandHandler.LOGGING_PID, null);
                will(returnValue(config));

                oneOf(config).getProperties();
                will(returnValue(props));
                oneOf(config).update(props);

                oneOf(modifyResults).setCompletionStatus(ModifyResults.PROCESSED_COMMAND);
                oneOf(modifyResults).setResponsesContainMSGIDs(false);
                oneOf(modifyResults).setResponses(null);
            }
        });

        String commandString = "logging=reset";
        lh.handleModify(commandString, modifyResults);
    }

    @Test
    public void testHandleModifyNoEquals() throws Exception {
        final ModifyResults modifyResults = context.mock(ModifyResults.class);
        context.checking(new Expectations() {
            {
                oneOf(modifyResults).setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
                oneOf(modifyResults).setResponsesContainMSGIDs(false);
                oneOf(modifyResults).setResponses(null);
            }
        });

        lh.handleModify("logging", modifyResults);
    }

    @Test
    public void testHandleModifyNoSpec() throws Exception {
        final ModifyResults modifyResults = context.mock(ModifyResults.class);
        context.checking(new Expectations() {
            {
                oneOf(modifyResults).setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
                oneOf(modifyResults).setResponsesContainMSGIDs(false);
                oneOf(modifyResults).setResponses(null);
            }
        });

        lh.handleModify("logging=", modifyResults);
    }

    @Test
    public void testHandleModifySingleSpec() throws Exception {
        final String traceSpec = "zos.native=all";

        final ModifyResults modifyResults = context.mock(ModifyResults.class);
        setupTraceSpecExpectations(modifyResults, traceSpec);

        lh.handleModify("logging=" + wrapWithQuotes(traceSpec), modifyResults);
    }

    @Test
    public void testHandleModifyMultipleSpecs() throws Exception {
        final String traceSpec = "zos.native=all:Security=all:foo.bar=all=disabled";

        final ModifyResults modifyResults = context.mock(ModifyResults.class);
        setupTraceSpecExpectations(modifyResults, traceSpec);

        lh.handleModify("logging=" + wrapWithQuotes(traceSpec), modifyResults);
    }

    @Test
    public void testSetConfiguredTraceSpec() throws Exception {

        final Dictionary<Object, Object> props = new Hashtable<Object, Object>();
        props.put(LoggingCommandHandler.TRACE_SPEC_KEY, "*=all=disabled");

        context.checking(new Expectations() {
            {
                exactly(2).of(configAdmin).getConfiguration(LoggingCommandHandler.LOGGING_PID, null);
                will(returnValue(config));

                oneOf(config).getProperties();
                will(returnValue(props));
            }

        });

        lh = new LoggingCommandHandler();
        lh.setConfigAdmin(configAdmin);
        assertEquals("*=all=disabled", lh.configuredTraceSpec);

    }

    String wrapWithQuotes(String string) {
        StringBuilder sb = new StringBuilder();
        sb.append("'").append(string).append("'");
        return sb.toString();
    }

    void setupTraceSpecExpectations(final ModifyResults modifyResults, final String traceSpec) throws Exception {
        context.checking(new Expectations() {
            {
                oneOf(configAdmin).getConfiguration(LoggingCommandHandler.LOGGING_PID, null);
                will(returnValue(config));

                oneOf(config).getProperties();
                will(returnValue(new Hashtable<String, Object>()));

                oneOf(config).update(with(DictionaryMatcher.dictionaryHasEntry(LoggingCommandHandler.TRACE_SPEC_KEY, traceSpec)));

                oneOf(modifyResults).setCompletionStatus(ModifyResults.PROCESSED_COMMAND);
                oneOf(modifyResults).setResponsesContainMSGIDs(false);
                oneOf(modifyResults).setResponses(null);
            }
        });
    }
}

@SuppressWarnings("rawtypes")
class DictionaryMatcher extends TypeSafeMatcher<Dictionary> {

    @Factory
    public static Matcher<Dictionary> dictionaryHasEntry(Object key, Object value) {
        return new DictionaryMatcher(key, value);
    }

    final Object key;
    final Object value;

    public DictionaryMatcher(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void describeTo(Description desc) {
        desc.appendText("a dictionary that has entry " + key + " => " + value);
    }

    @Override
    public boolean matchesSafely(Dictionary dictionary) {
        Object val = dictionary.get(key);
        return val == value || (val != null && val.equals(value));
    }
}
