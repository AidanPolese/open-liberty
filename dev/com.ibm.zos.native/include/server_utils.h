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

#ifndef SERVER_UTILS_H_
#define SERVER_UTILS_H_

#define TIMEUSED_DATA_AREA_SIZE 32

typedef struct TimeusedData TimeusedData;
struct TimeusedData
{
    // doc from TIMEUSED macro
    //    Words 0-1 = Total time.
    //    Words 2-3 = Time on CP when TIME_ON_CP=YES.
    //    Words 4-5 = Offload time (unnormalized) when OFFLOAD_TIME=YES.
    //    Words 6-7 = Offload on CP when OFFLOAD_ON_CP=YES.
    //    NOTE: if ECT not available only words 0-1 will have a value.
    char data[TIMEUSED_DATA_AREA_SIZE];
};
int getTimeusedData(TimeusedData *);

#endif /* SERVER_UTILS_H_ */
