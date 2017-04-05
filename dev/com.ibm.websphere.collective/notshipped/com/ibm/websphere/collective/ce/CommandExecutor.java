package com.ibm.websphere.collective.ce;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.ListenerNotFoundException;
import javax.management.NotificationListener;

/**
 * CommandExecutor defines the client interface to submit and manage the jobs in the command executor.
 * <p>
 * A job can be submitted as an asynchronous job or a synchronous job. A job consists of a single or multiple
 * predefined tasks against to single target or multiple targets.
 * An asynchronous job submission will return immediately after submitting the job. Client can subscribe to the
 * job notification and get notified for the job status.
 * 
 * A synchronous job submission will wait and return until the job is executed completely.
 * <p>
 * A target may consist of host name, user directory, or server name.
 * <p>
 * Example target:
 * <p>
 * <ul>
 * <li>${host-name}</li>
 * <li>${host-name}:wlp.user.dir</li>
 * <li>${host-name}:wlp.user.dir:${server-name}</li>
 * </ul>
 * <p>
 * A predefined task includes task name, required parameters, and optional parameters. Use a property to
 * specify the task parameters and values.
 */
public interface CommandExecutor {

    /**
     * Constants of the status.
     * <p>
     * SUCCEEDED - the job or task execution is succeeded.
     * FAILED - the job or task execution is failed.
     * CANCELED - the job or task execution is canceled.
     * NOT_STARTED - the job or task is waiting for execution.
     * RUNNING - the job or task execution is running.
     */
    public static final int SUCCEEDED = 0;
    public static final int FAILED = 1;
    public static final int CANCELED = 2;
    public static final int NOT_STARTED = 3;
    public static final int RUNNING = 4;

    /**
     * Keys for the job properties
     * <p>
     * jobId - generated id to reference the submitted job.
     * jobName - custom name for the job.
     * jobDescription - custom description for the job.
     * jobStatus - status of the job.
     * jobSubmissionTime - time when the job is submitted.
     */
    public static final String JOBPROPERTIES_JOBID_KEY = "jobId";
    public static final String JOBPROPERTIES_JOBNAME_KEY = "jobName";
    public static final String JOBPROPERTIES_JOBDESC_KEY = "jobDescription";
    public static final String JOBPROPERTIES_JOBSTATUS_KEY = "jobStatus";
    public static final String JOBPROPERTIES_JOBSUBMISSIONTIME_KEY = "jobSubmissionTime";

    /**
     * The asynchronous job submit operation submits an asynchronous job with a single task.
     * <p>
     * Job id is returned when the job is submitted.
     * 
     * @param taskParameters the parameters for the predefined tasks.
     * 
     * @see #getPredefinedTaskNames()
     * @see #getRequiredPredefinedTaskParameters(java.lang.String)
     * @see #getOptionalPredefinedTaskParameters(java.lang.String)
     * 
     * @param jobProperties the properties for the job. For example, job name, and job description.
     * @return job id when the job is submitted.
     * @throws IOException if there was any problem submitting the job
     * @throws IllegalArgumentException if the task parameters are invalid
     */
    public String submitAsynchronousJob(Map<String, Object> taskParameters, Map<String, Object> jobProperties) throws IOException, IllegalArgumentException;

    /**
     * The asynchronous job submit operation submits an asynchronous job with multiple tasks.
     * <p>
     * Job id is returned when the job is submitted.
     * 
     * @param taskList the list of tasks.
     * 
     * @see #getPredefinedTaskNames()
     * @see #getRequiredPredefinedTaskParameters(java.lang.String)
     * @see #getOptionalPredefinedTaskParameters(java.lang.String)
     * 
     * @param jobProperties the properties for the job. For example, job name, and job description.
     * @return job id when the job is submitted.
     * @throws IOException if there was any problem submitting the job
     * @throws IllegalArgumentException if the task parameters are invalid
     */
    public String submitAsynchronousJob(List<Map<String, Object>> taskList, Map<String, Object> jobProperties) throws IOException, IllegalArgumentException;

    /**
     * The synchronous job submit operation submits a synchronous job with single task.
     * <p>
     * Job id is returned when the job is submitted.
     * 
     * @param taskParameters the parameters for the predefined tasks.
     * 
     * @see #getPredefinedTaskNames()
     * @see #getRequiredPredefinedTaskParameters(java.lang.String)
     * @see #getOptionalPredefinedTaskParameters(java.lang.String)
     * 
     * @param jobProperties the properties for the job. For example, job name, and job description.
     * @param timeOut the expiration time for the job in milliseconds.
     * @return job id when the job finishes execution.
     * @throws IOException if there was any problem submitting the job
     * @throws IllegalArgumentException if the task parameters are invalid
     */
    public String submitSynchronousJob(Map<String, Object> taskParameters, Map<String, Object> jobProperties, long timeOut) throws IOException, IllegalArgumentException;

    /**
     * The synchronous job submit operation submits a synchronous job with multiple tasks.
     * <p>
     * Job id is returned when the job is submitted.
     * 
     * @param taskList the list of tasks.
     * 
     * @see #getPredefinedTaskNames()
     * @see #getRequiredPredefinedTaskParameters(java.lang.String)
     * @see #getOptionalPredefinedTaskParameters(java.lang.String)
     * 
     * @param jobProperties the properties for the job. For example, job name, and job description.
     * @param timeOut the expiration time for the job in milliseconds.
     * @return job id when the job finishes execution.
     * @throws IOException if there was any problem submitting the job
     * @throws IllegalArgumentException if the task parameters are invalid
     */
    public String submitSynchronousJob(List<Map<String, Object>> taskList, Map<String, Object> jobProperties, long timeOut) throws IOException, IllegalArgumentException;;

    /**
     * Adds a listener to the submitted jobs.
     * 
     * @param listener The listener object which will handle the
     *            job notifications emitted by the broadcaster.
     * 
     * @exception IllegalArgumentException Listener parameter is null.
     */
    public void addJobNotificationListener(NotificationListener listener) throws java.lang.IllegalArgumentException;

    /**
     * Removes a job listener.
     * 
     * @param listener A listener that was previously added to listen to the submitted jobs.
     * 
     * @exception ListenerNotFoundException The listener is not
     *                registered with the MBean.
     */
    public void removeJobNotificationListener(NotificationListener listener) throws ListenerNotFoundException;

    /**
     * The isSucceeded operation determines whether the given status is a succeeded status.
     * 
     * @param status the status of job or task.
     * @return {@code true} if the given status is a succeeded status, otherwise, return {@code false}.
     */
    public boolean isSucceeded(int status);

    /**
     * The isFailed operation determines whether the given status is a failed status.
     * 
     * @param status the status of job or task.
     * @return {@code true} if the given status is a failed status, otherwise, return {@code false}.
     */
    public boolean isFailed(int status);

    /**
     * The isCanceled operation determines whether the given status is a canceled status.
     * 
     * @param status the status of job or task.
     * @return {@code true} if the given status is a canceled status, otherwise, return {@code false}.
     */
    public boolean isCanceled(int status);

    /**
     * The isWaiting operation determines whether the given status is a waiting status.
     * 
     * @param status the status of job or task.
     * @return {@code true} if the given status is a waiting status, otherwise, return {@code false}.
     */
    public boolean isWaiting(int status);

    /**
     * The isRunning operation determines whether the given status is a running status.
     * 
     * @param status the status of job or task.
     * @return {@code true} if the given status is a running status, otherwise, return {@code false}.
     */
    public boolean isRunning(int status);

    /**
     * The getJobPropertyKeys operation retrieves the list of the job property keys.
     * 
     * @return the list of the job property keys.
     */
    public List<String> getJobPropertyKeys();

    /**
     * The getPredefinedTaskNames operation retrieves the list of the predefined task names.
     * 
     * @return the list of the predefined task names. Empty list is returned if the predefined task does not exist.
     */
    public List<String> getPredefinedTaskNames();

    /**
     * The getRequiredPredefinedTaskParameters operation retrieves the required parameters with the corresponding signatures.
     * 
     * @param taskName the task name of the predefined task.
     * @return the properties that include the required parameters and corresponding signatures. {@code null} is returned if the given predefined task does not exist.
     */
    public Properties getRequiredPredefinedTaskParameters(String taskName);

    /**
     * The getOptionalPredefinedTaskParameters operation retrieves the optional parameters with the corresponding signatures.
     * 
     * @param taskName the task name of the predefined task.
     * @return the properties that include the optional parameters and corresponding signatures. {@code null} is returned if the given predefined task does not exist.
     */
    public Properties getOptionalPredefinedTaskParameters(String taskName);

    /**
     * The getAllTaskId operation retrieves the list of the reference id for the tasks in the specific submitted job.
     * <p>
     * The task reference id is required to query the return code and the given parameters of the specific task in submitted job.
     * 
     * @param jobId the job id of the specific submitted job.
     * @return the optional parameters with the default values for the specific predefined task. {@code null} is returned if the given predefined task does not exist.
     * @throws IOException if there was any problem
     * @throws IllegalArgumentException if the job id does not exist
     */
    public List<String> getAllTaskId(String jobId) throws IOException, IllegalArgumentException;

    /**
     * The getTaskStatus operation retrieves the return code of the specific task in the submitted job.
     * 
     * @param jobId the job id of the specific submitted job.
     * @param taskId the task reference id of the task in the submitted job.
     * @return the status for the task.
     * @see #isSucceeded(int)
     * @see #isFailed(int)
     * @see #isCanceled(int)
     * @see #isWaiting(int)
     * @see #isRunning(int)
     * @throws IOException if there was any problem
     * @throws IllegalArgumentException if the task reference id does not exist
     */
    public int getTaskStatus(String jobId, String taskId) throws IOException, IllegalArgumentException;

    /**
     * The getTaskProperties operation retrieves the properties of the executed task in the submitted job.
     * The properties include the task parameters used in the specific task and the additional statistic data such as
     * the task start time, end time, etc.
     * 
     * @param jobId the job id of the specific submitted job.
     * @param taskId the task reference id of the task in the submitted job.
     * @return the task properties of the executed task.
     * @throws IOException if there was any problem
     * @throws IllegalArgumentException if the task reference id does not exist
     */
    public Map<String, Object> getTaskProperties(String jobId, String taskId) throws IOException, IllegalArgumentException;

    /**
     * The getTargetProperties operation retrieves the properties of the task executed on a specific target.
     * <p>
     * For example, standard output, and standard error of the task executed on a specific target.
     * 
     * @param jobId the job id of the specific submitted job.
     * @param taskId the task reference id of the task in the submitted job.
     * @param target the target host, instance, server to execute the task e.g. HOST_NAME:INSTANCE:SERVER_NAME
     * @return the target properties of the executed task.
     * @throws IOException if there was any problem
     * @throws IllegalArgumentException if the task reference id does not exist
     */
    public Map<String, Object> getTargetProperties(String jobId, String taskId, String target) throws IOException, IllegalArgumentException;

    /**
     * The getJobStatus operation retrieves the return code of the specific submitted job.
     * 
     * @param jobId the job id of the submitted job.
     * @return the status for the job.
     * @see #isSucceeded(int)
     * @see #isFailed(int)
     * @see #isCanceled(int)
     * @see #isWaiting(int)
     * @see #isRunning(int)
     * @throws IOException if there was any problem
     * @throws IllegalArgumentException if the job id does not exist
     */
    public int getJobStatus(String jobId) throws IOException, IllegalArgumentException;

    /**
     * The deleteJobResults operation removes the job results of the completed job.
     * 
     * @param jobId the job id of the waiting or completed job.
     * @return {@code true} if the job results are removed or the job results cannot be found. {@code false} if the job is not completed.
     * @throws IOException if there was any problem
     * @throws IllegalArgumentException if the job id does not exist
     */
    public boolean deleteJobResults(String jobId) throws IOException, IllegalArgumentException;

    /**
     * The cancelJob operation cancels the waiting job. The running and completed job are not allowed to cancel.
     * 
     * @param jobId the job id of the waiting job.
     * @return {@code true} if the waiting job was canceled, {@code false} if the job is running or completed.
     * @throws IOException if there was any problem
     * @throws IllegalArgumentException if the job id does not exist
     */
    public boolean cancelJob(String jobId) throws IOException, IllegalArgumentException;

    /**
     * The getJobProperties operation retrieves the properties for the specific job.
     * <p>
     * For example, the job start time, duration time, and end time.
     * 
     * @return the properties of the specific job.
     * @throws IOException if there was any problem
     * @throws IllegalArgumentException if the job id does not exist
     */
    public Map<String, Object> getJobProperties(String jobId) throws IOException, IllegalArgumentException;

    /**
     * The setJobName operation specify the name of the job in the job properties.
     * If the job properties is {@code null}, a new job properties will be created with the given job name.
     * 
     * @param jobProperties the job properties.
     * @param jobName the name of the job.
     * @throws IOException if there was any problem
     * @return the job properties which includes the job name.
     */
    public Map<String, Object> setJobName(Map<String, Object> jobProperties, String jobName);

    /**
     * The setJobDescription operation specify the description of the job in the job properties.
     * If the job properties is {@code null}, a new job properties will be created with the given job description.
     * 
     * @param jobProperties the job properties.
     * @param jobDescription the description of the job.
     * @return the job properties which includes the job description.
     */
    public Map<String, Object> setJobDescription(Map<String, Object> jobProperties, String jobDescription);

    /**
     * The getJobName operation retrieves job name from the given job properties.
     * 
     * @param jobProperties the job properties.
     * @return the job name from the given job properties. {@code null} is returned when the job name cannot be located in the job properties.
     */
    public String getJobName(Map<String, Object> jobProperties) throws IllegalArgumentException;

    /**
     * The getJobDescription operation retrieves job description from the given job properties.
     * 
     * @param jobProperties the job properties.
     * @return the job description from the given job properties. {@code null} is returned when the job description cannot be located in the job properties.
     */
    public String getJobDescription(Map<String, Object> jobProperties) throws IllegalArgumentException;

    /**
     * The getJobSubmissionTime operation retrieves job submission time from the given job properties.
     * 
     * @param jobProperties the job properties.
     * @return the job submission time from the given job properties. {@code 0} is returned when the job submission time cannot be located in the job properties.
     */
    public long getJobSubmissionTime(Map<String, Object> jobProperties);

    /**
     * The getTaskStartTime operation retrieves task start time from the given task properties.
     * 
     * @param taskProperties the task properties.
     * @return the task start time from the given job properties. {@code 0} is returned when the task start time cannot be located in the task properties.
     */
    public long getTaskStartTime(Map<String, Object> taskProperties);

    /**
     * The getTaskEndTime operation retrieves task end time from the given task properties.
     * 
     * @param taskProperties the task properties.
     * @return the task end time from the given job properties. {@code 0} is returned when the task end time cannot be located in the task properties.
     */
    public long getTaskEndTime(Map<String, Object> taskProperties);

    /**
     * The getCompletedJobList operation retrieves the list of the completed job id.
     * 
     * @return the list of the completed job id.
     * @throws IOException if there was any problem retrieving the list of the job id.
     * @throws IllegalArgumentException if the job id does not exist
     */
    public List<String> getCompletedJobList() throws IllegalArgumentException, IOException;

    /**
     * The getRunningJobList operation retrieves the list of the running job id.
     * 
     * @return the list of the running job id.
     * @throws IOException if there was any problem retrieving the list of the job id.
     * @throws IllegalArgumentException if the job id does not exist
     */
    public List<String> getRunningJobList() throws IllegalArgumentException, IOException;

    /**
     * The getWaitingJobList operation retrieves the list of the waiting job id.
     * 
     * @return the list of the waiting job id.
     * @throws IOException if there was any problem retrieving the list of the job id.
     * @throws IllegalArgumentException if the job id does not exist
     */
    public List<String> getWaitingJobList() throws IllegalArgumentException, IOException;

}