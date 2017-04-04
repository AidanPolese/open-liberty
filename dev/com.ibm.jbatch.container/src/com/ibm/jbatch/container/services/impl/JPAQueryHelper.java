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
package com.ibm.jbatch.container.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import com.ibm.jbatch.container.persistence.jpa.JobInstanceEntity;
import com.ibm.jbatch.container.ws.WSSearchConstants;
import com.ibm.jbatch.container.ws.WSSearchObject;

/**
 * This Class helps construct Dynamic JPA queries given the URL parameters in the WSSearchObject
 */
public class JPAQueryHelper {

    private final String BASE_QUERY = "SELECT x from JobInstanceEntity x";
    private final StringBuilder query = new StringBuilder().append(BASE_QUERY);
    private final StringBuilder whereClause = new StringBuilder();
    private boolean addAND = false;
    StringBuilder orderClause = new StringBuilder();
    private final WSSearchObject wsso;
    private int instanceVersion = 1;

    // Make visible for unit test.
    Map<String, Object> parameterMap = new HashMap<String, Object>();

    /**
     * CTOR
     */
    public JPAQueryHelper(WSSearchObject wsso) {
        this.wsso = wsso;
        if (wsso != null) {
            processParameters();
        }
    }

    /**
     * Processes the different parameters passed via the WSSearchObject
     *
     * @param wsso
     * @return
     */
    private void processParameters() {
        // Process the parameters

        processInstanceIdParams();
        processCreateTimeParams();
        processInstanceStateParams();
        processExitStatusParams();
        processSubmitterParams();
        processAuthSubmitter();
        processLastUpdatedTimeParams();
        processAppNameParams();
        processJobNameParams();
        processJobParameter();
        processSortParams();

        // If we built any snippets add them to the WHERE clause
        if (whereClause.length() != 0) {
            query.append(" WHERE " + whereClause);
        }
        // If there's a sort being done on the results, we'll have an ORDER BY clause
        if (orderClause.length() != 0) {
            query.append(" ORDER BY " + orderClause);
        }

    }

    /**
     * Handles ANDing portions of the WHERE clause
     *
     * @param addAND
     * @param query
     */
    private void handleSQLForAND() {
        if (addAND == true) {
            whereClause.append(" AND ");
        }
        addAND = true;
    }

    /**
     * Processes the sort parameters
     *
     * @param wsso
     */
    private void processSortParams() {

        List<String> sortList = wsso.getSortList();

        if (sortList != null) {

            String delim = "";
            for (String field : sortList) {

                // items prefixed with "-" mean sort descending
                boolean desc = false;
                if (field.startsWith("-")) {
                    field = field.substring(1);
                    desc = true;
                }

                if (WSSearchConstants.VALID_SORT_FIELDS.contains(field)) {

                    orderClause.append(delim);
                    orderClause.append("x." + field);

                    if (desc)
                        orderClause.append(" DESC");

                    delim = ",";

                    if (field.equals("lastUpdatedTime")) {
                        instanceVersion = 2;
                    }
                }
            }
        }
    }

    /**
     * Processes the lastUpdatedTime parameters
     *
     * @param wsso
     */
    private void processLastUpdatedTimeParams() {
        if (wsso.getLastUpdatedTime() != null) {
            instanceVersion = 2;
            handleSQLForAND();
            whereClause.append("x.lastUpdatedTime BETWEEN :lastUpdatedTimeStart AND :lastUpdatedTimeEnd");
            parameterMap.put("lastUpdatedTimeStart", setDayStartForDate(wsso.getLastUpdatedTime()));
            parameterMap.put("lastUpdatedTimeEnd", setDayEndForDate(wsso.getLastUpdatedTime()));
        }
    }

    /**
     * Processes the createTime parameters
     *
     * @param wsso
     * @return
     */
    private void processCreateTimeParams() {
        if (wsso.getStartCreateTime() != null && wsso.getEndCreateTime() != null) {
            handleSQLForAND();
            whereClause.append("x.createTime BETWEEN :startCreateTime AND :endCreateTime");
            parameterMap.put("startCreateTime", wsso.getStartCreateTime());
            parameterMap.put("endCreateTime", wsso.getEndCreateTime());
        } else if (wsso.getSpecificCreateTime() != null) {
            handleSQLForAND();
            whereClause.append("x.createTime BETWEEN :specificCreateTimeStart AND :specificCreateTimeEnd");
            parameterMap.put("specificCreateTimeStart", setDayStartForDate(wsso.getSpecificCreateTime()));
            parameterMap.put("specificCreateTimeEnd", setDayEndForDate(wsso.getSpecificCreateTime()));
        } else if (wsso.getLessThanCreateTime() != null) {
            handleSQLForAND();
            whereClause.append("x.createTime <= :lessThanCreateTime");
            parameterMap.put("lessThanCreateTime", setDayEndForDate(subtractDaysFromCurrentDate(new Integer(wsso.getLessThanCreateTime()))));
        } else if (wsso.getGreaterThanCreateTime() != null) {
            handleSQLForAND();
            whereClause.append("x.createTime >= :greaterThanCreateTime");
            parameterMap.put("greaterThanCreateTime", setDayStartForDate(subtractDaysFromCurrentDate(new Integer(wsso.getGreaterThanCreateTime()))));
        }
    }

    /**
     * Processes the instanceId parameters
     *
     * @param wsso
     * @return
     */
    private void processInstanceIdParams() {
        if (wsso.getStartInstanceId() != -1 && wsso.getEndInstanceId() != -1) {
            handleSQLForAND();
            whereClause.append("x.instanceId BETWEEN :startInstanceId AND :endInstanceId");
            parameterMap.put("startInstanceId", wsso.getStartInstanceId());
            parameterMap.put("endInstanceId", wsso.getEndInstanceId());
        } else if (wsso.getLessThanInstanceId() != -1) {
            handleSQLForAND();
            whereClause.append("x.instanceId <= :lessThanInstanceId");
            parameterMap.put("lessThanInstanceId", wsso.getLessThanInstanceId());
        } else if (wsso.getGreaterThanInstanceId() != -1) {
            handleSQLForAND();
            whereClause.append("x.instanceId >= :greaterThanInstanceId");
            parameterMap.put("greaterThanInstanceId", wsso.getGreaterThanInstanceId());
        } else if (wsso.getInstanceIdList() != null && wsso.getInstanceIdList().size() > 0) {
            handleSQLForAND();
            whereClause.append("x.instanceId IN :instanceIdList");
            parameterMap.put("instanceIdList", wsso.getInstanceIdList());
        }
    }

    /**
     * Processes the instanceState parameters
     *
     * @param wsso
     * @return
     */
    private void processInstanceStateParams() {
        if (wsso.getInstanceState() != null && wsso.getInstanceState().size() > 0) {
            handleSQLForAND();
            whereClause.append("x.instanceState IN :instanceStateList");
            parameterMap.put("instanceStateList", wsso.getInstanceState());
        }
    }

    /**
     * Processes the exitStatus parameters
     *
     * @param wsso
     * @return
     */
    private void processExitStatusParams() {
        String wildcard = null;
        if (wsso.getExitStatus() != null) {
            handleSQLForAND();
            wildcard = wsso.getExitStatus().replaceAll("\\*", "%");
            whereClause.append("x.exitStatus like :exitStatus");
            parameterMap.put("exitStatus", wildcard);
        }
    }

    /**
     * Processes the submitter parameter
     *
     * @param wsso
     * @return
     */
    private void processSubmitterParams() {
        String wildcard = null;
        if (wsso.getSubmitter() != null) {
            handleSQLForAND();
            wildcard = wsso.getSubmitter().replaceAll("\\*", "%");
            whereClause.append("x.submitter like :submitter");
            parameterMap.put("submitter", wildcard);
        }
    }

    /**
     * Processes the submitter variable added by the auth service
     *
     * @param wsso
     * @return
     */
    private void processAuthSubmitter() {

        if (wsso.getAuthSubmitter() != null) {
            handleSQLForAND();
            whereClause.append("x.submitter = :authSubmitter");
            parameterMap.put("authSubmitter", wsso.getAuthSubmitter());
        }
    }

    /**
     * Processes the appName parameter
     *
     * @param wsso
     * @return
     */
    private void processAppNameParams() {
        /**
         * The app name field in the repository is stored in the format [app name]#[ear/war filename]
         * We assume the user is giving us one of two things:
         * 1) The full repository entry including the # separator and all terms
         * 2) No # separator, in which case we assume they are matching on the first term (app name)
         */
        String wildcard = null;
        if (wsso.getAppName() != null) {
            handleSQLForAND();
            wildcard = wsso.getAppName().replaceAll("\\*", "%");

            // If the input does not include the # separator, assume they want to match the first term (app name).
            if (!wildcard.contains("#")) {
                wildcard = wildcard.concat("#%");
            }

            whereClause.append("x.amcName like :appName");
            parameterMap.put("appName", wildcard);
        }
    }

    /**
     * Processes the jobName parameter
     *
     * @param wsso
     * @return
     */
    private void processJobNameParams() {
        String wildcard = null;
        if (wsso.getJobName() != null) {
            handleSQLForAND();
            wildcard = wsso.getJobName().replaceAll("\\*", "%");
            whereClause.append("x.jobName like :jobName");
            parameterMap.put("jobName", wildcard);
        }
    }

    private void processJobParameter() {
        Map<String, String> jobParams = wsso.getJobParameters();
        if (jobParams == null || jobParams.isEmpty())
            return;

        int i = 0;
        StringBuilder subquery1 = new StringBuilder();
        StringBuilder subquery2 = new StringBuilder();

        // Iterate over the list of parameters to search on.
        for (String paramName : jobParams.keySet()) {
            String paramValue = jobParams.get(paramName);
            if (paramName != null) {
                paramName = paramName.replaceAll("\\*", "%");

                if (paramValue != null) {
                    paramValue = paramValue.replaceAll("\\*", "%");
                } else {
                    // If no value was specified, match any value
                    // This shouldn't happen now, but still good as a null check
                    paramValue = "%";
                }

                String queryParam = "jobParamName" + ((i == 0) ? "" : i);
                String queryValue = "jobParamValue" + ((i == 0) ? "" : i);

                // Matching a parameter to an instance requires querying a join of the execution and parameter tables
                if (i == 0) {
                    subquery1.append("(SELECT e from JobExecutionEntityV2 e"
                                     + " JOIN e.jobParameterElements p");
                    subquery2.append(" WHERE p.name like :" + queryParam
                                     + " AND p.value like :" + queryValue
                                     + " AND e.jobInstance = x");
                } else {
                    // Additional parameters require inner joins on the parameter table
                    String pnum = "p" + i;
                    subquery1.append(" JOIN e.jobParameterElements " + pnum);
                    subquery2.append(" AND " + pnum + ".name like :" + queryParam
                                     + " AND " + pnum + ".value like :" + queryValue);

                }

                parameterMap.put(queryParam, paramName);
                parameterMap.put(queryValue, paramValue);
                i++;
            }
        }
        handleSQLForAND();
        whereClause.append("EXISTS " + subquery1.toString() + subquery2.toString() + ")");
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

    /**
     * Getter for the query parameter
     *
     * @return
     */
    public String getQuery() {
        if (instanceVersion == 2) {
            return query.toString().replace("JobInstanceEntity", "JobInstanceEntityV2");
        } else {
            return query.toString();
        }
    }

    /**
     * @param query
     */
    public void setQueryParameters(TypedQuery<JobInstanceEntity> query) {
        Iterator<String> iterator = parameterMap.keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            Object value = parameterMap.get(name);
            query.setParameter(name, value);
        }
    }

}
