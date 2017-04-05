/**
 * 
 */
package com.ibm.jbatch.container.util;

import java.util.List;

import com.ibm.jbatch.container.callback.IJobExecutionEndCallbackService;
import com.ibm.jbatch.container.callback.IJobExecutionStartCallbackService;
import com.ibm.jbatch.container.execution.impl.RuntimeWorkUnitExecution;
import com.ibm.jbatch.container.services.IBatchKernelService;

/**
 * @author skurz
 *
 */
public class BatchJobWorkUnit extends BatchWorkUnit {

	public BatchJobWorkUnit(IBatchKernelService batchKernel, RuntimeWorkUnitExecution runtimeExecution,
			List<IJobExecutionStartCallbackService> beforeCallbacks, List<IJobExecutionEndCallbackService> afterCallbacks) {
		super(batchKernel, runtimeExecution, beforeCallbacks, afterCallbacks);
	}
}
