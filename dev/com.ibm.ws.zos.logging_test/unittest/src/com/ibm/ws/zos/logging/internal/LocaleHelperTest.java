/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.logging.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.wsspi.logging.LogRecordExt;

/**
 *
 */
@RunWith(JMock.class)
public class LocaleHelperTest {

    /**
     * Mock environment for native methods.
     */
    private Mockery mockery = null;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        mockery = new JUnit4Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
    }

    @Test
    public void test_englishLocale() {
        String msg = "hello";

        assertSame(msg, new LocaleHelper().translateToEnglish(msg, mockery.mock(TempLogRecord.class)));
    }

    /**
     * Test LoggingHardcopyLogHandler publish with message and LogRecord german locale
     */
    @Test
    public void test_nonEnglishLocale() {

        Locale tmpDefault = Locale.getDefault();

        try {
            Locale.setDefault(Locale.GERMAN);

            final LogRecordExt mockLogRecord = mockery.mock(TempLogRecord.class);
            final String returnedMsg = "ich liebe dich!";

            mockery.checking(new Expectations() {
                {
                    oneOf(mockLogRecord).getFormattedMessage(Locale.ENGLISH);
                    will(returnValue(returnedMsg));
                }
            });

            assertEquals(returnedMsg, new LocaleHelper().translateToEnglish("i love you", (LogRecord) mockLogRecord));
        } finally {
            Locale.setDefault(tmpDefault);
        }
    }

}

/**
 * Need to define a class that extends LogRecord and implements LogRecordExt,
 * for mocking purposes.
 */
class TempLogRecord extends LogRecord implements LogRecordExt {

    public TempLogRecord(Level level, String msg) {
        super(level, msg);
    }

    @Override
    public String getFormattedMessage(Locale locale) {
        return null;
    }

}
