package com.ibm.jbatch.container.util.artifacts;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

public class SubClass extends BaseClass {

	@Inject
	@BatchProperty
	String noReference = "default";
	

	/**
	 * @return the noReference
	 */
	public String getNoReference() {
		return noReference;
	}

	/**
	 * @return the stepCtx
	 */
	public StepContext getStepCtx() {
		return stepCtx;
	}

	@Inject
	StepContext stepCtx;
}
