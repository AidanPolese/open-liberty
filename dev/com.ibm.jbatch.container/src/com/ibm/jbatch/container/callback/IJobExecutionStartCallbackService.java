package com.ibm.jbatch.container.callback;

import com.ibm.jbatch.container.instance.WorkUnitDescriptor;


public interface IJobExecutionStartCallbackService {
	public void jobStarted(WorkUnitDescriptor ctx);
}
