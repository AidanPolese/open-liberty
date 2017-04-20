package com.ibm.ws.resource.internal;

import org.junit.Assert;
import org.junit.Test;

public class ResourceRefConfigListTest {
    @Test
    public void testSize() {
        ResourceRefConfigListImpl rrcl = new ResourceRefConfigListImpl();
        Assert.assertEquals(0, rrcl.size());

        rrcl.findOrAddByName("name");
        Assert.assertEquals(1, rrcl.size());

        rrcl.findOrAddByName("name");
        Assert.assertEquals(1, rrcl.size());

        rrcl.findOrAddByName("name2");
        Assert.assertEquals(2, rrcl.size());
    }

    @Test
    public void testGetResourceRefConfig() {
        ResourceRefConfigListImpl rrcl = new ResourceRefConfigListImpl();

        rrcl.findOrAddByName("name");
        rrcl.findOrAddByName("name2");

        Assert.assertEquals("name", rrcl.getResourceRefConfig(0).getName());
        Assert.assertEquals("name2", rrcl.getResourceRefConfig(1).getName());
    }

    @Test
    public void testFindOrAddByName() {
        ResourceRefConfigListImpl rrcl = new ResourceRefConfigListImpl();

        Assert.assertEquals("name", rrcl.findOrAddByName("name").getName());
        Assert.assertEquals("name", rrcl.findOrAddByName("name").getName());
        Assert.assertEquals("name2", rrcl.findOrAddByName("name2").getName());
    }
}
