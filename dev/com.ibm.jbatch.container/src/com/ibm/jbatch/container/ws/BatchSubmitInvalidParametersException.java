package com.ibm.jbatch.container.ws;

import com.ibm.jbatch.container.exception.BatchContainerRuntimeException;

//Defect 191113: The reason for this exception is to allow BatchJmsEndpointListener to
//distinguish between exceptions that it should vs. shouldn't roll back the message upon
public class BatchSubmitInvalidParametersException extends BatchContainerRuntimeException {
	
	private static final long serialVersionUID = 1L;

	public BatchSubmitInvalidParametersException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	
	public BatchSubmitInvalidParametersException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
