package com.ibm.jbatch.container.util.artifacts;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

public class BaseClass extends SuperClass {

	@Inject
	JobContext jobCtx;

	@Inject
	@BatchProperty
	String baseString;

	/**
	 * @return the baseString
	 */
	public String getBaseString() {
		return baseString;
	}

	/**
	 * @return the jobCtx
	 */
	public JobContext getJobCtx() {
		return jobCtx;
	}

}
