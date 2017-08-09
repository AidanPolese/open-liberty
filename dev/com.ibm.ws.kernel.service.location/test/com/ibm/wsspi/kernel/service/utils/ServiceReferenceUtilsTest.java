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
package com.ibm.wsspi.kernel.service.utils;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Constants;

public class ServiceReferenceUtilsTest {
    @Test
    public void testGetId() {
        TestServiceReference ref = new TestServiceReference("test");
        ref.ranking = 1L;
        Assert.assertEquals(ref.id, ServiceReferenceUtils.getId(ref));
    }

    @Test
    public void testGetRanking() {
        TestServiceReference ref = new TestServiceReference("test");
        Assert.assertEquals("default ranking", (Integer) 0, ServiceReferenceUtils.getRanking(ref));
        ref.ranking = 1;
        Assert.assertEquals("assigned ranking should be respected", (Integer) 1, ServiceReferenceUtils.getRanking(ref));
        ref.ranking = 1L;
        Assert.assertEquals("invalid ranking should be ignored", (Integer) 0, ServiceReferenceUtils.getRanking(ref));
    }

    @Test
    public void testReverseSort() {
        TestServiceReference[] array = new TestServiceReference[10];

        for (int i = 0; i < array.length; i++) {
            // 0 9 1 8 2 7 3 6 4 5
            int ranking = (i & 1) == 0 ? i : array.length - i;

            TestServiceReference ref = new TestServiceReference(Integer.toString(ranking)) {
                @Override
                public Object getProperty(String key) {
                    Object result = super.getProperty(key);
                    if (key.equals(Constants.SERVICE_RANKING)) {
                        // Mutate the service ranking while sorting.
                        ranking = 0;
                    }
                    return result;
                }
            };

            ref.ranking = ranking;
            array[i] = ref;
        }

        ServiceReferenceUtils.sortByRankingOrder(array);

        for (int i = 0; i < array.length; i++) {
            Assert.assertEquals(Integer.toString(array.length - i - 1), array[i].getName());
        }
    }
}
