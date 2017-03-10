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
#ifndef _BBOZ_MVS_ESTAE_H
#define _BBOZ_MVS_ESTAE_H

/*-------------------------------------------------------------------*/
/* Structure used by the establish_estaex_with_retry method.  The    */
/* struct is used to store the registers to be used on retry and     */
/* the retry address.                                                */
/*-------------------------------------------------------------------*/
typedef struct retry_parms retry_parms;
struct retry_parms /**< Data used by the default ESTAE retry routine */
{
  char regs64[8 * 16];  /**< Retry registers, set by SET_RETRY_POINT */
  void* retry_addr; /**< Address to retry to, set by SET_RETRY_POINT */
  struct {
      int nodump : 1; /**< If set, don't take a dump when retrying */
      int _available : 31;
  } setrp_opts;
};


/**
 * Macro used to set the location of the retry point when the ESTAE
 * is established using the establish_estaex_with_retry method.
 *
 * If an ABEND occurs while the ESTAE is in effect, control will
 * return to the instruction after the SET_RETRY_POINT macro.
 *
 * \see establish_esatex_with_retry
 *
 * @param a The retry_parms struct which was supplied on the
 *          establish_estaex_with_retry method.
 */
#define SET_RETRY_POINT(a) \
  __asm(" STMG 0,15,%0\n"   \
        " BASR 14,0\n"      \
        " AHI  14,-2\n"     \
        " STG  14,%1" :     \
        "=m"(a.regs64[0]),"=m"(a.retry_addr) : : \
        "r14")


/**
 * Function establishes an ESTAE which allows retry to a particular
 * point in the code.  Sample:
 *
 *\code
 *  void my_func()
 *  {
 *    int estaex_rc = -1;
 *    int estaex_rsn = -1;
 *
 *    volatile int already_tried_it = 0;
 *    struct retry_parms findBGVT_retry;
 *
 *    establish_estaex_with_retry(&findBGVT_retry,
 *                                &estaex_rc,
 *                                &estaex_rsn);
 *    if (estaex_rc == 0)
 *    {
 *      memset(&findBGVT_retry, 0, sizeof(findBGVT_retry));
 *      SET_RETRY_POINT(findBGVT_retry);
 *
 *      if (already_tried_it == 0)
 *      {
 *        already_tried_it = 1;
 *
 *        do_something();
 *      }
 *      else
 *      {
 *        print("Got control for retry, something went wrong");
 *      }
 *
 *      remove_estaex(&estaex_rc, &estaex_rsn);
 *    }
 *  }
 *\endcode
 *
 *  In this example, if something goes wrong in do_something(),
 *  we'll branch to the code after SET_RETRY_POINT but the
 *  already_tried_it variable will be set to 1, indicating that the
 *  retry code got control.
 *
 *  Note that you can reset the retry point and it will over-write
 *  the previous retry point.  This is useful for multi-step
 *  recovery and RESMGRs.
 *
 *  @param retry_p The storage that will be used to store information about the
 *                 ESTAEX retry point.
 *  @param estaex_rc_p A pointer to a field where the ESTAEX return code will be
 *                     returned.  RC 0 is success.
 *  @param estaex_rsn_p A pointer to a field where the ESTAEX reason code will
 *                      be returned.
 */
void establish_estaex_with_retry(retry_parms* retry_p,
                                 int* estaex_rc_p,
                                 int* estaex_rsn_p);

/**
 * Function removes the most recently established ESTAE.
 *
 *  @param estaex_rc_p A pointer to a field where the ESTAEX return code will be
 *                     returned.  RC 0 is success.
 *  @param estaex_rsn_p A pointer to a field where the ESTAEX reason code will
 *                      be returned.
 */
void remove_estaex(int* estaex_rc_p, int* estaex_rsn_p);

#endif
