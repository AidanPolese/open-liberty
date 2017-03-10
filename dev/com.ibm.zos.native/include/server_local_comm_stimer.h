/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Local Comm Stimer related information
 */

#ifndef SERVER_LOCAL_COMM_STIMER_H_
#define SERVER_LOCAL_COMM_STIMER_H_


#pragma pack(1)
typedef struct  LocalCommStimerParms {
    /** WRQ_PET at time of stimer set. */
    char               workQueuePet[16];                        /* 0x000 */

    /** Connection Handle Token. */
    char               workQConnectionHandleToken[16];          /* 0x010 */

    /** Pointer to target LOCL. */
    void*              bbgzlocl_p;                              /* 0x020 */

    /** STCK when started ticking. */
    unsigned long long startTime;                               /* 0x028 */

} LocalCommStimerParms_t;                                       /* 0x030 */
#pragma pack(reset)

#endif /* SERVER_LOCAL_COMM_STIMER_H_ */
