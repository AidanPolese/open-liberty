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
/**
 * @file
 * Constants for native product registration code
 *
 */

#ifndef SERVER_PRODUCT_REGISTRATION_H_
#define SERVER_PRODUCT_REGISTRATION_H_


// Return codes for our own problems.  All negative values are used
// since we return either IFAUSAGE return codes (0/4/8/12/20) or
// our own.  Thus negative return codes are parameter or set up
// problems and positive ones come direct from IFAUSAGE (look 'em up).

/** Product owner was a null or too long       */
#define PRODUCT_REGISTRATION_BAD_OWNER         -1

/** Product name was a null or too long        */
#define PRODUCT_REGISTRATION_BAD_NAME          -2

/** Product version was a null or too long     */
#define PRODUCT_REGISTRATION_BAD_VERSION       -3

/** Product ID was a null or too long          */
#define PRODUCT_REGISTRATION_BAD_ID            -4

/** Product Qualifier was null or too long     */
#define PRODUCT_REGISTRATION_BAD_QUALIFIER     -5

/** Unable to find IFAUSAGE function code      */
#define PRODUCT_REGISTRATION_FUNC_NOT_FOUND    -6

/** Unable to obtain 31 bit storage for IFAUSAGE parms */
#define PRODUCT_REGISTRATION_NO_31_BIT_STORAGE -7

/** Authorized PC failure while attempting to call IFAUSAGE=DEREGISTER */
#define PRODUCT_REGISTRATION_DEREG_AUTH_PC_FAILURE -8


#endif /* SERVER_PRODUCT_REGISTRATION_H_ */
