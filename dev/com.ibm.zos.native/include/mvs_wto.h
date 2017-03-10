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
 */
#ifndef _BBOZ_MVS_WTO_H
#define _BBOZ_MVS_WTO_H

#pragma enum(4)
typedef enum {
    WTO_OPERATOR_CONSOLE     = 1,    // operator console
    WTO_PROGRAMMER_HARDCOPY  = 2     // programmer and hardcopy
} WtoLocation;
#pragma enum(reset)

#ifdef __IBM_METAL__

/**
 * Prints a line using the WTO macro.
 *
 * @param message_p A pointer to a null terminated string.
 * @param cart_p A pointer to the 8 character cart identifier, or NULL if none.
 *
 * @return return code. 0 success, < 0 failure
 */
int write_to_operator(char* message_p, char* cart_p);

/**
 * Print a formatted string to wto.
 *
 * Note: max length of formatted string is 1024.
 *
 * @parms similar to printf
 *
 * @return the rc from write_to_programmer
 */
int printfWto(const char* str, ...) ;

/**
 * Prints a line to hardcopy and the JES log for the process using the WTO macro.
 *
 * @param message_p A pointer to a null terminated string.
 *
 * @return return code. 0 success, < 0 failure
 */
int write_to_programmer(char* message_p);

/**
 * Issue command response using multiline WTO as necessary
 *
 * @param message_p command response message to be issued (null terminated string).
 * @param responseCart MVS Command and response token (CART).
 * @param conId target console identifier.
 * @param rc output return code
 *
 * @retval 0 no errors
 * @retval -4 unable to obtain storage to process the request
 * @retval -8 input message was null
 * @retval -12 command response exceeded 1000 multi line WTOs
 *
 * @return > 0 return code from WTO
 *
 * @note made available as an unauthorized service (via UNAUTH_DEF).
 */
int
write_to_operator_response(const char* message_p, const long* responseCart, const int* conId, int* rc);

/**
 * Issue message to the requested location
 *
 * @param message_p message to be issued (null terminated string).
 * @param location  location where the message is to be issued
 *
 * @retval 0 no errors
 * @retval -4 unable to obtain storage to process the request
 * @retval -8 input message was null
 * @retval -12 command response exceeded 1000 multi line WTOs
 *
 * @return > 0 return code from WTO
 *
 * @note made available as an unauthorized service (via UNAUTH_DEF).
 */
int
write_to_operator_unauthorized_routine(char* message_p, const WtoLocation* location);
#endif // __IBM_METAL__

#endif
