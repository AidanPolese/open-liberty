package com.ibm.jbatch.container.callback;

import com.ibm.jbatch.container.instance.WorkUnitDescriptor;


public interface IJobExecutionEndCallbackService {
	public void jobEnded(WorkUnitDescriptor ctx);
}
