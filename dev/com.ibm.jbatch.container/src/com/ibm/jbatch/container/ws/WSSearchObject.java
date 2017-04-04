/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.jbatch.container.ws;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Contains the different parameters and the parsing for the pre-purge search
 *
 */
public class WSSearchObject {

    // Search/Purge related variables
    long startInstanceId = -1;
    long endInstanceId = -1;
    long lessThanInstanceId = -1;
    long greaterThanInstanceId = -1;
    List<Long> instanceIdList;
    List<InstanceState> instanceState;
    String exitStatus;
    Date startCreateTime = null;
    Date endCreateTime = null;
    String lessThanCreateTime = null;
    String greaterThanCreateTime = null;
    Date specificCreateTime = null;
    Map<String, String> jobParams = null;
    Date lastUpdatedTime = null;
    String submitter = null;
    String appName = null;
    String jobName = null;

    // Non-Search/Purge related variables
    String authSubmitter = null;
    List<String> sortList;
    String sort = null;

    // Constructor
    public WSSearchObject(String instanceIdParams, String createTimeParams,
                          String instanceStateParams, String exitStatusParams) throws Exception {

        this(instanceIdParams, createTimeParams, instanceStateParams, exitStatusParams, null, null, null, null, null, null);
    }

    // Constructor
    public WSSearchObject(String instanceIdParams, String createTimeParams,
                          String instanceStateParams, String exitStatusParams, String lastUpdatedTimeParams,
                          String sortParams, Map<String, String> jobParams) throws Exception {

        this(instanceIdParams, createTimeParams, instanceStateParams, exitStatusParams, lastUpdatedTimeParams, sortParams, jobParams, null, null, null);
    }

    // Constructor
    public WSSearchObject(String instanceIdParams, String createTimeParams,
                          String instanceStateParams, String exitStatusParams, String lastUpdatedTimeParams,
                          String sortParams, Map<String, String> jobParams,
                          String submitterParams, String appNameParams, String jobNameParams) throws Exception {

        if (instanceIdParams != null)
            processInstanceIdParams(instanceIdParams);

        if (createTimeParams != null)
            processCreateTimeParams(createTimeParams);

        if (instanceStateParams != null)
            processInstanceStateParams(instanceStateParams);

        if (exitStatusParams != null)
            processExitStatusParams(exitStatusParams);

        if (lastUpdatedTimeParams != null)
            processLastUpdatedTimeParams(lastUpdatedTimeParams);

        if (jobParams != null)
            processJobParameter(jobParams);

        if (sortParams != null)
            processSortParams(sortParams);

        if (submitterParams != null)
            processSubmitterParams(submitterParams);

        if (appNameParams != null)
            processAppNameParams(appNameParams);

        if (jobNameParams != null)
            processJobNameParams(jobNameParams);
    }

    /**
     * Processes the sort parameters
     *
     * @param params
     * @throws Exception
     */
    private void processSortParams(String params) throws Exception {

        if (params != null)
            this.sortList = Arrays.asList(params.split(","));
    }

    /**
     * Processes the lastUpdatedTime parameters
     *
     * @param params
     * @throws Exception
     */
    private void processLastUpdatedTimeParams(String params) throws Exception {
        DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.lastUpdatedTime = dFormat.parse(params);
    }

    /**
     * Processes the exitStatus parameters
     *
     * @param params
     * @throws Exception
     */
    private void processExitStatusParams(String params) throws Exception {
        this.exitStatus = params;
    }

    public void processJobParameter(Map<String, String> jobParams) {
        this.jobParams = jobParams;
    }

    /**
     * Processes the instanceState parameters
     *
     * @param params
     * @throws Exception
     */
    private void processInstanceStateParams(String params) throws Exception {
        List<String> tempList = Arrays.asList(params.split(","));
        List<InstanceState> stateList = new ArrayList<InstanceState>(tempList.size());

        for (String value : tempList) {
            try {
                //Ensuring the params are all caps since InstanceState is an Enum (case sensitive)
                stateList.add(InstanceState.valueOf(value.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("An invalid instanceState parameter was used: " + value + ". Valid instanceState parameters are: " +
                                                   Arrays.toString(InstanceState.values()).replace("[", "").replace("]", ""));
            }
        }

        this.instanceState = stateList;
    }

    /**
     * Processes the instanceId parameters
     *
     * @param params
     * @throws Exception
     */
    private void processInstanceIdParams(String params) throws Exception {
        if (params.contains(":")) {
            String[] parts = params.split(":");
            this.startInstanceId = new Long(parts[0]);
            this.endInstanceId = new Long(parts[1]);
            if (isNegative(this.startInstanceId))
                throw new IllegalArgumentException("A negative startInstanceId value was entered: " + this.startInstanceId);
            if (isNegative(this.endInstanceId))
                throw new IllegalArgumentException("A negative endInstanceId value was entered: " + this.endInstanceId);
        } else if (params.contains("<")) {
            String part = params.substring(1, params.length());
            this.lessThanInstanceId = new Long(part);
            if (isNegative(this.lessThanInstanceId))
                throw new IllegalArgumentException("A negative lessThanInstanceId value was entered: " + this.lessThanInstanceId);
        } else if (params.contains(">")) {
            String part = params.substring(1, params.length());
            this.greaterThanInstanceId = new Long(part);
            if (isNegative(this.greaterThanInstanceId))
                throw new IllegalArgumentException("A negative greaterThanInstanceId value was entered: " + this.greaterThanInstanceId);
        } else if (params.contains(",")) {
            List<String> tempList = Arrays.asList(params.split(","));
            this.instanceIdList = new ArrayList<Long>(tempList.size());
            Long longValue;
            for (String value : tempList) {
                longValue = Long.parseLong(value);
                if (isNegative(longValue))
                    throw new IllegalArgumentException("A negative jobInstanceId value was entered: " + longValue);

                this.instanceIdList.add(longValue);
            }
        } else if (params != null) {
            List<String> tempList = Arrays.asList(params);
            this.instanceIdList = new ArrayList<Long>(tempList.size());
            Long longValue;
            for (String value : tempList) {
                longValue = Long.parseLong(value);
                if (isNegative(longValue))
                    throw new IllegalArgumentException("A negative jobInstanceId value was entered: " + longValue);

                this.instanceIdList.add(Long.parseLong(value));
            }
        }
    }

    /**
     * Processes the createTime parameters
     *
     * @param params
     * @throws Exception
     */
    private void processCreateTimeParams(String params) throws Exception {
        DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (params.contains(":")) {
            String[] parts = params.split(":");
            this.startCreateTime = setDayStartForDate(dFormat.parse(parts[0]));
            this.endCreateTime = setDayEndForDate(dFormat.parse(parts[1]));
        } else if (params.contains("<")) {
            this.lessThanCreateTime = params.substring(1, params.indexOf("d"));
        } else if (params.contains(">")) {
            this.greaterThanCreateTime = params.substring(1,
                                                          params.indexOf("d"));
        } else { // This handles a single date
            dFormat = new SimpleDateFormat("yyyy-MM-dd");
            this.specificCreateTime = dFormat.parse(params);
        }
    }

    private void processSubmitterParams(String params) {
        this.submitter = params;
    }

    private void processAppNameParams(String params) {
        this.appName = params;
    }

    private void processJobNameParams(String params) {
        this.jobName = params;
    }

    // Getters and Setters

    public long getStartInstanceId() {
        return startInstanceId;
    }

    public void setStartInstanceId(long startInstanceId) {
        this.startInstanceId = startInstanceId;
    }

    public long getEndInstanceId() {
        return endInstanceId;
    }

    public void setEndInstanceId(long endInstanceId) {
        this.endInstanceId = endInstanceId;
    }

    public long getLessThanInstanceId() {
        return lessThanInstanceId;
    }

    public void setLessThanInstanceId(long lessThanInstanceId) {
        this.lessThanInstanceId = lessThanInstanceId;
    }

    public long getGreaterThanInstanceId() {
        return greaterThanInstanceId;
    }

    public void setGreaterThanInstanceId(long greaterThanInstanceId) {
        this.greaterThanInstanceId = greaterThanInstanceId;
    }

    public List<InstanceState> getInstanceState() {
        return instanceState;
    }

    public void setInstanceState(List<InstanceState> instanceState) {
        this.instanceState = instanceState;
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(String exitStatus) {
        this.exitStatus = exitStatus;
    }

    public Date getStartCreateTime() {
        return startCreateTime;
    }

    public void setStartCreateTime(Date startCreateTime) {
        this.startCreateTime = startCreateTime;
    }

    public Date getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(Date endCreateTime) {
        this.endCreateTime = endCreateTime;
    }

    public String getLessThanCreateTime() {
        return lessThanCreateTime;
    }

    public void setLessThanCreateTime(String lessThanCreateTime) {
        this.lessThanCreateTime = lessThanCreateTime;
    }

    public String getGreaterThanCreateTime() {
        return greaterThanCreateTime;
    }

    public void setGreaterThanCreateTime(String greaterThanCreateTime) {
        this.greaterThanCreateTime = greaterThanCreateTime;
    }

    public List<Long> getInstanceIdList() {
        return instanceIdList;
    }

    public void setInstanceIdList(List<Long> instanceIdList) {
        this.instanceIdList = instanceIdList;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Date getSpecificCreateTime() {
        return specificCreateTime;
    }

    public void setSpecificCreateTime(Date specificCreateTime) {
        this.specificCreateTime = specificCreateTime;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getAuthSubmitter() {
        return authSubmitter;
    }

    public void setAuthSubmitter(String submitter) {
        this.authSubmitter = submitter;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Map<String, String> getJobParameters() {
        return jobParams;
    }

    public List<String> getSortList() {
        return sortList;
    }

    public void setSortList(List<String> sortList) {
        this.sortList = sortList;
    }

    /**
     * Helper method to print the content of the WSSearchObject
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("WSSearchObject content :: \n");
        sb.append("startInstanceId = " + this.startInstanceId + "\n");
        sb.append("endInstanceId = " + this.endInstanceId + "\n");
        sb.append("lessThanInstanceId = " + this.lessThanInstanceId + "\n");
        sb.append("greaterThanInstanceId = " + this.greaterThanInstanceId + "\n");
        sb.append("instanceState = " + this.instanceState + "\n");
        sb.append("exitStatus = " + this.exitStatus + "\n");
        sb.append("startCreateTime = " + this.startCreateTime + "\n");
        sb.append("endCreateTime = " + this.endCreateTime + "\n");
        sb.append("lessThanCreateTime = " + this.lessThanCreateTime + "\n");
        sb.append("greaterThanCreateTime = " + this.greaterThanCreateTime + "\n");
        sb.append("instanceIdList = " + this.instanceIdList + "\n");
        sb.append("specificCreateTime = " + this.specificCreateTime + "\n");
        sb.append("lastUpdatedTime = " + this.lastUpdatedTime + "\n");
        sb.append("sortList = " + this.sortList + "\n");
        sb.append("submitter = " + this.submitter + "\n");
        sb.append("authSubmitter = " + this.authSubmitter + "\n");
        sb.append("appName = " + this.appName + "\n");
        sb.append("jobName = " + this.jobName + "\n");
        if (jobParams != null) {
            for (Map.Entry<String, String> e : this.jobParams.entrySet()) {
                sb.append("jobParameter." + e.getKey() + "=" + e.getValue() + "\n");
            }
        }

        return sb.toString();
    }

    /**
     *
     * @param date
     * @return
     */
    private Date setDayEndForDate(Date date) {
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
    private Date setDayStartForDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTimeInMillis());
    }

    /**
     *
     * @param value
     * @return
     */
    private boolean isNegative(long value) {
        return (value < 0) ? true : false;
    }

}
