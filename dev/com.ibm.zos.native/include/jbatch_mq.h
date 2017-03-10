/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef __jbatch_mq_h__
#define __jbatch_mq_h__

#include "cmqc.h"

#define CORRELATOR_JOB_PROP "com_ibm_ws_batch_events_correlationId"

void mq_inquireQname(MQHCONN Hconn, MQHOBJ Hobj, MQCHAR48 qName);
int mq_inquireMaxMsgLength(MQHCONN Hconn, MQHOBJ Hobj);
void generateCorrelationId(MQBYTE* buff);

#endif
