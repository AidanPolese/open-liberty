package com.ibm.jbatch.container.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import com.ibm.jbatch.container.ws.InstanceState;
import com.ibm.jbatch.container.ws.WSSearchObject;

import test.common.SharedOutputManager;

public class JPAQueryHelperTest {

    private static final String BASEQUERY = "SELECT x from JobInstanceEntity x";

    /**
     * A @Rule executes before and after each test (see
     * SharedOutputManager.apply()). The SharedOutputManager Rule captures and
     * restores (i.e. collects and purges) output streams (stdout/stderr) before
     * and after each test. The output is dumped if and only if the test failed.
     */
    @Rule
    public SharedOutputManager outputMgr = SharedOutputManager.getInstance();

    @Test
    public void testInstanceIdRange() throws Exception {
        WSSearchObject wsso = new WSSearchObject("10:15", null, null, null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.instanceId BETWEEN :startInstanceId AND :endInstanceId", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("startInstanceId"));
        assertTrue(jqh.parameterMap.containsKey("endInstanceId"));
        assertEquals(10L, jqh.parameterMap.get("startInstanceId"));
        assertEquals(15L, jqh.parameterMap.get("endInstanceId"));
    }

    @Test
    public void testNULLWSSO() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, null, null, null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);
        assertEquals(BASEQUERY, jqh.getQuery());
    }

    @Test
    public void testInstanceIdGreaterThan() throws Exception {
        WSSearchObject wsso = new WSSearchObject(">10", null, null, null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.instanceId >= :greaterThanInstanceId", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("greaterThanInstanceId"));
        assertEquals(10L, jqh.parameterMap.get("greaterThanInstanceId"));
    }

    @Test
    public void testInstanceIdLessThan() throws Exception {
        WSSearchObject wsso = new WSSearchObject("<15", null, null, null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.instanceId <= :lessThanInstanceId", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("lessThanInstanceId"));
        assertEquals(15L, jqh.parameterMap.get("lessThanInstanceId"));
    }

    @Test
    public void testInstanceIdList() throws Exception {
        List<Long> values = new ArrayList<Long>();
        values.add(1L);
        values.add(2L);
        values.add(3L);
        values.add(4L);
        WSSearchObject wsso = new WSSearchObject("1,2,3,4", null, null, null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertTrue(jqh.parameterMap.containsKey("instanceIdList"));
        assertEquals(BASEQUERY + " WHERE x.instanceId IN :instanceIdList", jqh.getQuery());
        List<Long> instanceIdList = (List<Long>) jqh.parameterMap.get("instanceIdList");
        assertTrue(wsso.getInstanceIdList().containsAll(values));

    }

    @Test
    public void testExitStatus() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, null, null, "ABC");
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.exitStatus like :exitStatus", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("exitStatus"));
        assertEquals("ABC", jqh.parameterMap.get("exitStatus"));
    }

    @Test
    public void testExitStatusWildCard() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, null, null, "*ABC");
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.exitStatus like :exitStatus", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("exitStatus"));
        assertEquals("%ABC", jqh.parameterMap.get("exitStatus"));

        wsso = new WSSearchObject(null, null, null, "ABC*");
        jqh = new JPAQueryHelper(wsso);

        assertTrue(jqh.parameterMap.containsKey("exitStatus"));
        assertEquals("ABC%", jqh.parameterMap.get("exitStatus"));

        wsso = new WSSearchObject(null, null, null, "*ABC*");
        jqh = new JPAQueryHelper(wsso);

        assertTrue(jqh.parameterMap.containsKey("exitStatus"));
        assertEquals("%ABC%", jqh.parameterMap.get("exitStatus"));
    }

    @Test
    public void testCreateTime() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, "2015-08-24:2015-09-01", null, null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.createTime BETWEEN :startCreateTime AND :endCreateTime", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("startCreateTime"));
        assertNotNull(jqh.parameterMap.containsValue("startCreateTime"));
        assertTrue(jqh.parameterMap.containsKey("endCreateTime"));
        assertNotNull(jqh.parameterMap.containsValue("endCreateTime"));

    }

    @Test
    public void testCreateTimeGreaterThan() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, ">2d", null, null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.createTime >= :greaterThanCreateTime", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("greaterThanCreateTime"));
        assertNotNull(jqh.parameterMap.get("greaterThanCreateTime"));
    }

    @Test
    public void testCreateTimeLessThan() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, "<2d", null, null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.createTime <= :lessThanCreateTime", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("lessThanCreateTime"));
        assertNotNull(jqh.parameterMap.get("lessThanCreateTime"));
    }

    @Test
    public void testCreateTimeSpecificDay() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, "2015-09-01", null, null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.createTime BETWEEN :specificCreateTimeStart AND :specificCreateTimeEnd", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("specificCreateTimeStart"));
        assertNotNull(jqh.parameterMap.get("specificCreateTimeStart"));
        assertTrue(jqh.parameterMap.containsKey("specificCreateTimeEnd"));
        assertNotNull(jqh.parameterMap.get("specificCreateTimeEnd"));
    }

    @Test
    public void testInstanceState() throws Exception {

        List<InstanceState> values = new ArrayList<InstanceState>();
        values.add(InstanceState.COMPLETED);
        values.add(InstanceState.SUBMITTED);
        values.add(InstanceState.STOPPED);
        values.add(InstanceState.FAILED);
        values.add(InstanceState.ABANDONED);
        WSSearchObject wsso = new WSSearchObject(null, null, "COMPLETED, SUBMITTED, STOPPED, FAILED , ABANDONED", null);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.instanceState IN :instanceStateList", jqh.getQuery());

        List<InstanceState> instanceStateList = (List<InstanceState>) jqh.parameterMap.get("instanceStateList");
        assertTrue(wsso.getInstanceState().containsAll(values));
    }

    @Test
    public void testAll() throws Exception {
        WSSearchObject wsso = new WSSearchObject("9:21", "2015-09-01", "COMPLETED, SUBMITTED, STOPPED, FAILED , ABANDONED", "ABC");
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY
                     + " WHERE x.instanceId BETWEEN :startInstanceId AND :endInstanceId AND x.createTime BETWEEN :specificCreateTimeStart AND :specificCreateTimeEnd AND x.instanceState IN :instanceStateList "
                     + "AND x.exitStatus like :exitStatus", jqh.getQuery());

        assertTrue(jqh.parameterMap.containsKey("startInstanceId"));
        assertTrue(jqh.parameterMap.containsKey("endInstanceId"));
        assertEquals(9L, jqh.parameterMap.get("startInstanceId"));
        assertEquals(21L, jqh.parameterMap.get("endInstanceId"));

        List<InstanceState> values = new ArrayList<InstanceState>();
        values.add(InstanceState.COMPLETED);
        values.add(InstanceState.SUBMITTED);
        values.add(InstanceState.STOPPED);
        values.add(InstanceState.FAILED);
        values.add(InstanceState.ABANDONED);
        List<InstanceState> instanceStateList = (List<InstanceState>) jqh.parameterMap.get("instanceStateList");
        assertTrue(wsso.getInstanceState().containsAll(values));

        assertTrue(jqh.parameterMap.containsKey("exitStatus"));
        assertEquals("ABC", jqh.parameterMap.get("exitStatus"));

        assertTrue(jqh.parameterMap.containsKey("specificCreateTimeStart"));
        assertNotNull(jqh.parameterMap.get("specificCreateTimeStart"));
        assertTrue(jqh.parameterMap.containsKey("specificCreateTimeEnd"));
        assertNotNull(jqh.parameterMap.get("specificCreateTimeEnd"));
    }

    @Test
    public void testThreeParams() throws Exception {
        WSSearchObject wsso = new WSSearchObject("9:21", null, "COMPLETED, SUBMITTED, STOPPED, FAILED , ABANDONED", "ABC");
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.instanceId BETWEEN :startInstanceId AND :endInstanceId AND x.instanceState IN :instanceStateList "
                     + "AND x.exitStatus like :exitStatus", jqh.getQuery());

        assertTrue(jqh.parameterMap.containsKey("startInstanceId"));
        assertTrue(jqh.parameterMap.containsKey("endInstanceId"));
        assertEquals(9L, jqh.parameterMap.get("startInstanceId"));
        assertEquals(21L, jqh.parameterMap.get("endInstanceId"));

        List<InstanceState> values = new ArrayList<InstanceState>();
        values.add(InstanceState.COMPLETED);
        values.add(InstanceState.SUBMITTED);
        values.add(InstanceState.STOPPED);
        values.add(InstanceState.FAILED);
        values.add(InstanceState.ABANDONED);
        List<InstanceState> instanceStateList = (List<InstanceState>) jqh.parameterMap.get("instanceStateList");
        assertTrue(wsso.getInstanceState().containsAll(values));

        assertTrue(jqh.parameterMap.containsKey("exitStatus"));
        assertEquals("ABC", jqh.parameterMap.get("exitStatus"));
    }

    @Test
    public void testTwoParams() throws Exception {
        WSSearchObject wsso = new WSSearchObject(null, null, "COMPLETED, SUBMITTED, STOPPED, FAILED , ABANDONED", "ABC");
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.instanceState IN :instanceStateList AND x.exitStatus like :exitStatus", jqh.getQuery());

        List<InstanceState> values = new ArrayList<InstanceState>();
        values.add(InstanceState.COMPLETED);
        values.add(InstanceState.SUBMITTED);
        values.add(InstanceState.STOPPED);
        values.add(InstanceState.FAILED);
        values.add(InstanceState.ABANDONED);
        List<InstanceState> instanceStateList = (List<InstanceState>) jqh.parameterMap.get("instanceStateList");
        assertTrue(wsso.getInstanceState().containsAll(values));

        assertTrue(jqh.parameterMap.containsKey("exitStatus"));
        assertEquals("ABC", jqh.parameterMap.get("exitStatus"));
    }

    @Test
    public void testSubmitter() throws Exception {
        String submitter = "SarahSubmitter";
        WSSearchObject wsso = new WSSearchObject(null, null, null, null);
        wsso.setSubmitter(submitter);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.submitter like :submitter", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("submitter"));
        assertEquals(submitter, jqh.parameterMap.get("submitter"));
    }

    @Test
    public void testAuthSubmitter() throws Exception {
        String authSubmitter = "SarahSubmitter";
        WSSearchObject wsso = new WSSearchObject(null, null, null, null);
        wsso.setAuthSubmitter(authSubmitter);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.submitter = :authSubmitter", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("authSubmitter"));
        assertEquals(authSubmitter, jqh.parameterMap.get("authSubmitter"));
    }

    @Test
    public void testBothSubmitters() throws Exception {
        String submitter = "AliceSubmitter";
        String authSubmitter = "BobSubmitter";
        WSSearchObject wsso = new WSSearchObject(null, null, null, null);
        wsso.setSubmitter(submitter);
        wsso.setAuthSubmitter(authSubmitter);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.submitter like :submitter AND x.submitter = :authSubmitter", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("submitter"));
        assertEquals(submitter, jqh.parameterMap.get("submitter"));
        assertTrue(jqh.parameterMap.containsKey("authSubmitter"));
        assertEquals(authSubmitter, jqh.parameterMap.get("authSubmitter"));
    }

    @Test
    public void testAppName() throws Exception {
        String appName = "MyApp";
        WSSearchObject wsso = new WSSearchObject(null, null, null, null);
        wsso.setAppName(appName);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.amcName like :appName", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("appName"));
        assertEquals(appName + "#%", jqh.parameterMap.get("appName"));
    }

    @Test
    public void testAppNameWithPound() throws Exception {
        String appName = "MyApp#MyApp.ear";
        WSSearchObject wsso = new WSSearchObject(null, null, null, null);
        wsso.setAppName(appName);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.amcName like :appName", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("appName"));
        assertEquals(appName, jqh.parameterMap.get("appName"));
    }

    @Test
    public void testJobName() throws Exception {
        String jobName = "myJobName";
        WSSearchObject wsso = new WSSearchObject(null, null, null, null);
        wsso.setJobName(jobName);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE x.jobName like :jobName", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("jobName"));
        assertEquals(jobName, jqh.parameterMap.get("jobName"));
    }

    @Test
    public void testJobParameter() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("testParam", "true");
        WSSearchObject wsso = new WSSearchObject(null, null, null, null, null, null, params);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);

        assertEquals(BASEQUERY + " WHERE EXISTS (SELECT e from JobExecutionEntityV2 e JOIN e.jobParameterElements p "
                     + "WHERE p.name like :jobParamName AND p.value like :jobParamValue AND e.jobInstance = x)",
                     jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("jobParamName"));
        assertEquals("testParam", jqh.parameterMap.get("jobParamName"));
        assertTrue(jqh.parameterMap.containsKey("jobParamValue"));
        assertEquals("true", jqh.parameterMap.get("jobParamValue"));
    }

    @Test
    public void testJobParameterNoValue() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("testParam", null);
        WSSearchObject wsso = new WSSearchObject(null, null, null, null, null, null, params);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);
        assertEquals(BASEQUERY + " WHERE EXISTS (SELECT e from JobExecutionEntityV2 e JOIN e.jobParameterElements p "
                     + "WHERE p.name like :jobParamName AND p.value like :jobParamValue AND e.jobInstance = x)",
                     jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("jobParamName"));
        assertEquals("testParam", jqh.parameterMap.get("jobParamName"));
        assertTrue(jqh.parameterMap.containsKey("jobParamValue"));
        assertEquals("%", jqh.parameterMap.get("jobParamValue"));
    }

    @Test
    public void testJobParameterMulti() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("testParam", "true");
        params.put("param2", "false");
        WSSearchObject wsso = new WSSearchObject(null, null, null, null, null, null, params);
        JPAQueryHelper jqh = new JPAQueryHelper(wsso);
        assertEquals(BASEQUERY + " WHERE EXISTS (SELECT e from JobExecutionEntityV2 e JOIN e.jobParameterElements p JOIN e.jobParameterElements p1 "
                     + "WHERE p.name like :jobParamName AND p.value like :jobParamValue AND e.jobInstance = x "
                     + "AND p1.name like :jobParamName1 AND p1.value like :jobParamValue1)", jqh.getQuery());
        assertTrue(jqh.parameterMap.containsKey("jobParamName"));
        assertTrue(jqh.parameterMap.containsKey("jobParamValue"));
        assertTrue(jqh.parameterMap.containsKey("jobParamName1"));
        assertTrue(jqh.parameterMap.containsKey("jobParamValue1"));

        // The order of the parameters in the map is not guaranteed, so we have to check both possibilities
        assertTrue("testParam".equals(jqh.parameterMap.get("jobParamName")) ||
                   "testParam".equals(jqh.parameterMap.get("jobParamName1")));
        assertTrue("true".equals(jqh.parameterMap.get("jobParamValue")) ||
                   "true".equals(jqh.parameterMap.get("jobParamValue1")));
        assertTrue("param2".equals(jqh.parameterMap.get("jobParamName")) ||
                   "param2".equals(jqh.parameterMap.get("jobParamName1")));
        assertTrue("false".equals(jqh.parameterMap.get("jobParamValue")) ||
                   "false".equals(jqh.parameterMap.get("jobParamValue1")));
    }

    /**
     * Helper method to subtract days from the current date
     *
     * @param days
     * @return
     */
    private static Date subtractDaysFromCurrentDate(int days) {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    /**
     *
     * @param date
     * @return
     */
    private static Date setDayEndForDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return (new Date(cal.getTimeInMillis() - 1L));
    }

    /**
     *
     * @param date
     * @return
     */
    private static Date setDayStartForDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTimeInMillis());
    }

}
