/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef MVS_PSW_H_
#define MVS_PSW_H_

/**
 * PSW mapping used by extractPSW.
 */
typedef struct bbgz_psw bbgz_psw;
struct bbgz_psw {
    unsigned int _rsvd1 : 8;
    unsigned int key    : 4;
    unsigned int _rsvd2 : 3;
    unsigned int pbm_state : 1;
    unsigned int asc_control : 2;
    unsigned int cond_code   : 2;
    unsigned int _rsvd3 : 12;
};

/* Don't let metal C parts include this.  Metal C code cannot call this. */
/* TODO: Why isn't this in the unauthorized function module bbgzsufm? */
#ifndef __IBM_METAL__

/**
 * Stub to extract the psw from LE C.
 *
 * @param psw_p A pointer to a bbgz_psw struct to fill in.
 *
 * @return psw
 */
#pragma linkage(extractThePSW, OS64_NOSTACK)
bbgz_psw * extractThePSW(bbgz_psw* psw_p);

#else
/**
 * Extracts parts of the current PSW mapped by bbgz_psw.
 *
 * @param psw A pointer to a bbgz_psw struct to fill in.
 *
 * @return psw
 */
bbgz_psw * extractPSW(bbgz_psw* psw);

/**
 * Extracts parts of the PSW from the most recent entry on the linkage stack.
 * The caller must be authorized.
 *
 * @param psw A pointer to a bbgz_psw struct to fill in.
 *
 * @return psw
 */
bbgz_psw * extractPSWFromLinkageStack(bbgz_psw* psw);

#endif

#endif /* MVS_PSW_H_ */
