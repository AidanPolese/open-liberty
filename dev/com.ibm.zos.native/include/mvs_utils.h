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
#ifndef _BBOZ_MVS_UTILS_H
#define _BBOZ_MVS_UTILS_H

#include <metal.h>
#include <string.h>

#include "common_defines.h"
#include "gen/ihaascb.h"
#include "mvs_psw.h"

/**
 * Mapping of IVSK output.
 */
typedef struct bbgz_ivsk bbgz_ivsk;
struct bbgz_ivsk {
    unsigned int key             : 4;
    unsigned int fetchProtection : 1;
    unsigned int _rsvd1          : 3;
};

/**
 * Creates a metal C environment using __cinit() and sets it current by
 * placing in into R12.
 *
 * @param mysysenv_p A pointer to a __csysenv_s structure which will be
 *                   populated before __cinit is called.  This storage
 *                   must remain allocated until the environment is
 *                   destroyed using termenv().
 * @param usertoken The user token to use on __cinit().
 * @param heapAnchor_p A pointer to the heap anchor if we are managing our own
 *                     heap.
 *
 * @return A pointer to the environment created, or NULL if the environment
 *         could not be created.
 */
void* initenv(struct __csysenv_s* mysysenv_p, long long usertoken, void* heapAnchor_p);

/**
 * Takes a previously allocated metal C environment and sets it onto the
 * current thread (puts it into R12).
 *
 * ** Important **
 * This function must be inlined, or else the compiler generated linkage will
 * restore the original contents of R12 on exit, making this useless.
 *
 * @param env_p A pointer to the metal C environment returned by initenv().
 */
#pragma inline(setenvintoR12)
#pragma prolog(setenvintoR12,"R12PROL")
#pragma epilog(setenvintoR12,"R12EPIL")
static void setenvintoR12(void* env_p) {
    __asm(" LGR 12,%0" : : "r"(env_p) : "r12");
}

/**
 * Gets the metal C environment pointer which is currently set in R12.
 *
 * @return A pointer to whatever is in R12 (hopefully a metal C environment).
 */
#pragma inline(getenvfromR12)
static void* getenvfromR12(void) {
    void* env_p;
    __asm(" LGR %0,12" : "=r"(env_p) : : "r12");
    return env_p;
}

/**
 * Destroys a metal C environment using __cterm().  The environment to
 * destroy must be set into R12 (by initenv or setenvintoR12).
 */
void termenv(void);

/**
 * Makes a printable hex string from the storage provided.
 *
 * @param data_p A pointer to the data to print.
 * @param length The length of the data, in bytes.
 *
 * @return A null terminated string, or NULL if failure.  The caller owns
 *         this storage which must be freed using the free() function.
 */
char* binaryToHexString(void* data_p, int length);

/**
 * Sleeps for the specified number of seconds.  This method should only be
 * used for debugging.
 *
 * @param seconds The number of seconds to sleep.
 */
void sleep(int seconds);

/**
 * Returns area containing the storage key that the provided storage is allocated in
 * and the fetch protection of the provide storage.
 *
 * @param storage_p A pointer to the storage to test.
 * @param output_p  A pointer to area the will get the storage key and fetch protection.
 *
 */
void getStorageKey(void* storage_p, bbgz_ivsk* output_p);

/**
 * Returns a pointer to the ASCB control block for the address space
 * represented by the input stoken.
 *
 * @param stoken_p A pointer to the stoken for the address space whose
 *                 ASCB is requested.
 *
 * @return A pointer to the ASCB for the requested address space, or NULL if
 *         the ASCB could not be retrieved.
 */
ascb* getAscbFromStoken(void* stoken_p);

// TODO: Make pre-processor macros for supervisor/problem state?
/**
 * Switches to supervisor state.
 */
void switchToSupervisorState(void);

/**
 * Switches to problem state.
 */
void switchToProblemState(void);

// TODO: Make pre-processor macros for key0/saved key?
/**
 * Switches execution to key 0.
 *
 * @return The previous key.
 */
unsigned char switchToKey0(void);

/**
 * Switches execution to the provided key
 *
 * @param key The key returned on switchToKey0.
 */
void switchToSavedKey(unsigned char key);

/*-------------------------------------------------------------------*/
/* Copy with key routines.                                           */
/*-------------------------------------------------------------------*/
/**
 * Copies from src in the provided key to dest in the current key for len
 * bytes.
 *
 * @param dest A pointer to the destination data
 * @param src A pointer to the source data
 * @param len The number of bytes to copy for
 * @param key The key that the src storage is in.
 *
 * @return A pointer to the destination storage.
 */
#pragma inline(memcpy_sk)
static void* memcpy_sk(void* dest, void* src, int len, unsigned char key)  {
    int len_remaining = len;
    int shifted_key = ((int)key) << 4;
    char* cur_dest = (char*)dest;
    char* cur_src = (char*)src;

    while (len_remaining > 0) {
        int len_to_copy_this_round = (len_remaining > 256 ? 255 : len_remaining - 1);
        len_remaining = len_remaining - (len_to_copy_this_round + 1);

        __asm(" LLGT 0,%0\n"
              " LLGT 1,%1\n"
              " MVCSK %2,%3" : :
              "m"(len_to_copy_this_round),"m"(shifted_key),
              "m"(*cur_dest),"m"(*cur_src) :
              "r0","r1");

        cur_dest = cur_dest + (len_to_copy_this_round + 1);
        cur_src = cur_src + (len_to_copy_this_round + 1);
    }

    return dest;
}

/**
 * Copies from src in the provided key to dest in the current key for a
 * maximum of len bytes.  The src string should be null terminated.  This
 * method behaves like strncpy() does with the exception of the storage
 * key.  Note that the performance of this method is poor and therefore
 * should only be used when the length of the storage cannot be computed
 * in any other way.  The preferred practice is to pass the length of the
 * storage and use memcpy_sk.
 *
 * @param dest A pointer to the destination data
 * @param src A pointer to the source data
 * @param max_len The maximum number of bytes to copy for
 * @param key The key that the src storage is in.
 *
 * @return A pointer to the destination storage.
 */
#pragma inline(strncpy_sk)
static char* strncpy_sk(char* dest, char* src, int max_len, unsigned char key) {
    char* end_dest = dest + max_len;
    char* cur_dest = dest;
    char* cur_src = src;
    char  done = 0;

    if (max_len > 0) {
        memset(dest, 0, max_len);

        while (!done) {
            memcpy_sk((void*) cur_dest, (void*) cur_src, 1, key);

            if (*cur_dest == 0) {
                done = 1;
            } else {
                cur_dest++;
                cur_src++;

                if (cur_dest == end_dest) {
                    done = 1;
                }
            }
        }
    }

    return dest;
}

/**
 * Copies from src in the current key to dest in the provided key for len
 * bytes.
 *
 * @param dest A pointer to the destination data
 * @param src A pointer to the source data
 * @param len The number of bytes to copy for
 * @param key The key that the dest storage is in.
 *
 * @return A pointer to the destination storage.
 */
#pragma inline(memcpy_dk)
static void* memcpy_dk(void* dest, void* src, int len, unsigned char key) {
    int len_remaining = len;
    int shifted_key = ((int)key) << 4;
    char* cur_dest = (char*)dest;
    char* cur_src = (char*)src;

    while (len_remaining > 0) {
        int len_to_copy_this_round = (len_remaining > 256 ? 255 : len_remaining - 1);
        len_remaining = len_remaining - (len_to_copy_this_round + 1);

        __asm(" LLGT 0,%0\n"
              " LLGT 1,%1\n"
              " MVCDK %2,%3" : :
              "m"(len_to_copy_this_round),"m"(shifted_key),
              "m"(*cur_dest),"m"(*cur_src) :
              "r0","r1");

        cur_dest = cur_dest + (len_to_copy_this_round + 1);
        cur_src = cur_src + (len_to_copy_this_round + 1);
    }

    return dest;
}

/**
 * Retrieve the JOBSTEP TCB TTOKEN via TCBTOKEN.
 *
 * @param ttoken    Output parm. Populated with the JOBSTEP TCB TTOKEN.
 *
 * @return The rc from TCBTOKEN TYPE=JOBSTEP
 */
int getJobstepTToken(TToken* ttoken);

#endif

