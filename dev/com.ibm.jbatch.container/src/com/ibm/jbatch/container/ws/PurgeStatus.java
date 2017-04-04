package com.ibm.jbatch.container.ws;

public enum PurgeStatus {
    
    /**
     * Job purge completed successfully
     */
    COMPLETED,
    
    /**
     * Job purge failed
     */
    FAILED,
    
    /**
     * Job purge failed because it was still active
     */
    STILL_ACTIVE,
    
    /**
     * Database purge failed, but the Job Logs were successfully purged
     */
    JOBLOGS_ONLY,
    
    /**
     * Job purge failed because the job isn't local
     */
    NOT_LOCAL;

}
