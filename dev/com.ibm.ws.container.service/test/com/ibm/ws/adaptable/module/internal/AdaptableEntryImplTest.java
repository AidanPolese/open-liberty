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
package com.ibm.ws.adaptable.module.internal;

import java.io.IOException;
import java.io.InputStream;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.artifact.ArtifactEntry;

@RunWith(JMock.class)
public class AdaptableEntryImplTest {

    private final Mockery mockery = new Mockery();

    /**
     * Test that we get an IOException (indicating a corrupt jar file) when we
     * call the adapt method. This IOException should be the nested exception
     * for the UnableToAdaptException that the adapt method throws.
     */
    @Test(expected = IOException.class)
    public void testAdaptBadInputStream() throws Throwable {
        final ArtifactEntry mockDelegate = mockery.mock(ArtifactEntry.class);
        AdaptableEntryImpl impl = new AdaptableEntryImpl(mockDelegate, null, null, null);

        mockery.checking(new Expectations() {
            {
                allowing(mockDelegate).getInputStream();
                will(throwException(new IOException("expected test IOException")));
            }
        });

        try {
            impl.adapt(InputStream.class);
        } catch (UnableToAdaptException ex) {
            //expected
            throw ex.getCause();
        }
    }
}
