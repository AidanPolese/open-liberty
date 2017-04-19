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
package com.ibm.ws.container.service.metadata.internal;

import org.junit.Assert;
import org.junit.Test;

public class IndexListTest {
    @Test
    public void testReserve() {
        IndexList list = new IndexList();
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, list.reserve());
        }
    }

    @Test
    public void testUnreserveForward() {
        IndexList list = new IndexList();
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, list.reserve());
        }

        for (int i = 0; i < 10; i++) {
            list.unreserve(i);
        }

        for (int i = 10; --i >= 0;) {
            Assert.assertEquals(i, list.reserve());
        }
        for (int i = 10; i < 20; i++) {
            Assert.assertEquals(i, list.reserve());
        }
    }

    @Test
    public void testUnreserveBackward() {
        IndexList list = new IndexList();
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, list.reserve());
        }

        for (int i = 10; --i >= 0;) {
            list.unreserve(i);
        }

        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(i, list.reserve());
        }
    }
}
