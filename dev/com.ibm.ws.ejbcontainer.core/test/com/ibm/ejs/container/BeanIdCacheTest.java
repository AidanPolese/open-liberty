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
package com.ibm.ejs.container;

import org.junit.Assert;
import org.junit.Test;

public class BeanIdCacheTest
{

    /**
     * To test if the BeanIdCache clears its cache when given the same new size,
     * the following is done:
     * 1. Create two beans that are the same.
     * 2. Only add the first one to the cache
     * 3. Find on the first bean, returns the same bean
     * 4. Re-set the cache size to the same value (nothing should happen)
     * 5. Find on the second bean, but since it is not actually in the cache and the
     * first one is still, the first one is returned
     * 
     * @throws Exception
     */
    @Test
    public void testBeanIdCacheSize() throws Exception
    {
        final int size = 11;
        BeanIdCache cache = new BeanIdCache(size);

        BeanId bid1 = new BeanId();
        BeanId bid2 = new BeanId();

        cache.add(bid1);

        Assert.assertSame(bid1, cache.find(bid1));
        cache.setSize(size);
        Assert.assertSame(bid1, cache.find(bid2));
    }
}
