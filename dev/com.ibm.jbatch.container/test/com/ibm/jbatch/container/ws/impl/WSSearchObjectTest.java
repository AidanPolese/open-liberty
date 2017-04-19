package com.ibm.jbatch.container.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import test.common.SharedOutputManager;

import com.ibm.jbatch.container.ws.InstanceState;
import com.ibm.jbatch.container.ws.WSSearchObject;

public class WSSearchObjectTest {

    /**
     * A @Rule executes before and after each test (see
     * SharedOutputManager.apply()). The SharedOutputManager Rule captures and
     * restores (i.e. collects and purges) output streams (stdout/stderr) before
     * and after each test. The output is dumped if and only if the test failed.
     */
    @Rule
    public SharedOutputManager outputMgr = SharedOutputManager.getInstance();

    @Test
    public void testInstanceId() throws Exception {

        // Test single value
        WSSearchObject wsso = new WSSearchObject("10", null, null, null);
        assertTrue(wsso.getInstanceIdList().get(0) == (10));

        // Test list of values
        wsso = new WSSearchObject("9,12,17", null, null, null);
        List<Long> values = new ArrayList<Long>();
        values.add(9L);
        values.add(12L);
        values.add(17L);
        List<Long> instanceIdList = wsso.getInstanceIdList();
        for(int i = 0; i < instanceIdList.size(); i++) {
            assertTrue(values.contains(instanceIdList.get(i)));
        }
        

        // Test Range of values
        wsso = new WSSearchObject("10:15", null, null, null);
        assertEquals(10L, wsso.getStartInstanceId());
        assertEquals(15L, wsso.getEndInstanceId());

        // Test Greater Than
        wsso = new WSSearchObject(">100", null, null, null);
        assertEquals(100L, wsso.getGreaterThanInstanceId());

        // Test Less Than
        wsso = new WSSearchObject("<99", null, null, null);
        assertEquals(99L, wsso.getLessThanInstanceId());
    }

    @Test(expected = Exception.class)
    public void testInstanceRangeIncorrect() throws Exception {
        WSSearchObject wsso = new WSSearchObject("10:", null, null, null);
    }

    @Test
    public void testInstanceState() throws Exception {

        List<InstanceState> list = new ArrayList<InstanceState>();
        list.add(InstanceState.STOPPED);

        WSSearchObject wsso = new WSSearchObject(null, null, "STOPPED", null);
        assertTrue(wsso.getInstanceState().containsAll(list));

        wsso = new WSSearchObject(null, null, "STOPPED,FAILED,COMPLETED", null);
        list.clear();
        list.add(InstanceState.STOPPED);
        list.add(InstanceState.FAILED);
        list.add(InstanceState.COMPLETED);
        assertTrue(wsso.getInstanceState().containsAll(list));
    }

    @Test(expected = Exception.class)
    public void testInstanceStateIncorrectValue() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, null, "STOP", null);
    }

    @Test
    public void testExitStatus() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, null, null, "HELP");
        assertEquals("HELP", wsso.getExitStatus());
    }

    @Test
    public void testCreateTime() throws Exception {

        // Test Range value
        WSSearchObject wsso = new WSSearchObject(null, "2015-08-30:2015-09-01", null, null);
        assertNotNull(wsso.getStartCreateTime());
        assertNotNull(wsso.getEndCreateTime());

        // Test Greater Than
        wsso = new WSSearchObject(null, ">100d", null, null);
        assertEquals("100", wsso.getGreaterThanCreateTime());

        // Test Less Than
        wsso = new WSSearchObject(null, "<99d", null, null);
        assertEquals("99", wsso.getLessThanCreateTime());
    }
    
    @Test
    public void testCreateTime_SpecificDay() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, "2015-08-22", null, null);
        assertNotNull(wsso.getSpecificCreateTime());
    }
    
    @Test(expected = Exception.class)
    public void testCreateTime_BadDateInRange() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, "2015-08-22:2015-09-", null, null);
    }

    @Test
    public void testCreateTime_All() throws Exception {
        List<InstanceState> list = new ArrayList<InstanceState>();
        list.add(InstanceState.COMPLETED);

        WSSearchObject wsso = new WSSearchObject("11:17", "2015-08-24", "COMPLETED" , "HELP AGAIN!");
       
        assertEquals(11L, wsso.getStartInstanceId());
        assertEquals(17L, wsso.getEndInstanceId());
        assertNotNull(wsso.getSpecificCreateTime());
        assertTrue(wsso.getInstanceState().containsAll(list));
        assertEquals("HELP AGAIN!", wsso.getExitStatus());
    }

}
