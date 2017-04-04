/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.jbatch.container.ws.smf;

import java.util.Date;

import com.ibm.jbatch.container.execution.impl.RuntimePartitionExecution;
import com.ibm.jbatch.container.execution.impl.RuntimeSplitFlowExecution;
import com.ibm.jbatch.container.execution.impl.RuntimeStepExecution;
import com.ibm.jbatch.container.execution.impl.RuntimeWorkUnitExecution;
import com.ibm.jbatch.container.ws.WSJobExecution;
import com.ibm.jbatch.container.ws.WSTopLevelStepExecution;
import com.ibm.jbatch.jsl.model.Step;

public interface ZosJBatchSMFLogging {

    public int buildAndWriteJobEndRecord(WSJobExecution jobExecution,
                                         RuntimeWorkUnitExecution runtimeJobExecution,
                                         String jobStoreRefId,
                                         String persistenceType,
                                         byte[] timeUsedBefore,
                                         byte[] timeUsedAfter);

    public int buildAndWriteStepEndRecord(WSTopLevelStepExecution stepExecution,
                                          WSJobExecution jobExecution,
                                          RuntimeWorkUnitExecution runtimeWorkUnitExecution,
                                          int partitionPlanCount,
                                          int partitionCount,
                                          String jobStoreRefId,
                                          String persistenceType,
                                          Step step,
                                          boolean isPartitionedStep,
                                          byte[] timeUsedBefore,
                                          byte[] timeUsedAfter);

    public int buildAndWriteDeciderEndRecord(String exitStatus,
                                             RuntimeWorkUnitExecution execution,
                                             WSJobExecution jobExecution,
                                             String jobStoreRefId,
                                             String persistenceType,
                                             String decisionRef,
                                             Date startTime,
                                             Date endTime,
                                             byte[] timeUsedBefore,
                                             byte[] timeUsedAfter);

    public int buildAndWriteFlowEndRecord(RuntimeSplitFlowExecution runtimeSplitFlowExecution,
                                          WSJobExecution jobExecution,
                                          String jobStoreRefId,
                                          String persistenceType);

    public int buildAndWritePartitionEndRecord(RuntimePartitionExecution runtimeWorkUnitExecution,
                                               WSJobExecution jobExecution,
                                               RuntimeStepExecution runtimeStepExecution,
                                               String jobStoreRefId,
                                               String persistenceType,
                                               byte[] timeUsedBefore,
                                               byte[] timeUsedAfter);

    public byte[] getTimeUsedData();

}
