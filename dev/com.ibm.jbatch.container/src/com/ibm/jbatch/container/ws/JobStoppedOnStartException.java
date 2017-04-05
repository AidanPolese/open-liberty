package com.ibm.jbatch.container.ws;

import javax.batch.operations.JobStartException;

public class JobStoppedOnStartException extends JobStartException {
    
    private static final long serialVersionUID = 1L;

    public JobStoppedOnStartException() {
    }

    public JobStoppedOnStartException(String message) {
        super(message);
    }

    public JobStoppedOnStartException(Throwable cause) {
        super(cause);
    }

    public JobStoppedOnStartException(String message, Throwable cause) {
        super(message, cause);
    }
 
}
