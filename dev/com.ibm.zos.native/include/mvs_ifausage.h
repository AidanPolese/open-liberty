/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
  *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_MVS_IFAUSAGE_H
#define _BBOZ_MVS_IFAUSAGE_H

typedef struct {
    int* returnCode_p;
    char*  owner_p;
    char*  name_p;
    char*  version_p;
    char*  id_p;
    char*  qualifier_p;
} regParms;

/**
 * Register a product with z/OS as unauthorized.
 * All parameters must be padded with blanks to their full length.  Null termination is optional.
 *
 * owner_p      : A pointer to a 16 byte EBCDIC string identifying the product owner (e.g. IBM)
 * name_p       : A pointer to a 16 byte EBCDIC string identifying the product (e.g. WAS FOR Z/OS)
 * version_p    : A pointer to an 8 byte EBCDIC string identifying the product version (e.g. 8.5)
 * id_p         : A pointer to an 8 byte EBDDIC string containing the product identifier (e.g. 5655-W65)
 * qualifier_p  : A pointer to an 8 byte EBCDIC string containing a product qualifier or feature name (e.g. WAS Z/OS)
 * returnCode_p : The return code from the IFAUSAGE service.  A zero is good.  A four means you've registered for something
 *                else already which could be ok.  An eight means you are calling from unauthorized code and tried to register for
 *                more than two products.  A negative one means we couldn't get below-the-bar storage for our parms.  Anything else is just really bad.
 *
 */
int registerProduct(regParms* parms);

/**
 * Register a product with z/OS as authorized.
 * All parameters must be padded with blanks to their full length.  Null termination is optional.
 *
 * owner_p      : A pointer to a 16 byte EBCDIC string identifying the product owner (e.g. IBM)
 * name_p       : A pointer to a 16 byte EBCDIC string identifying the product (e.g. WAS FOR Z/OS)
 * version_p    : A pointer to an 8 byte EBCDIC string identifying the product version (e.g. 8.5)
 * id_p         : A pointer to an 8 byte EBDDIC string containing the product identifier (e.g. 5655-W65)
 * qualifier_p  : A pointer to an 8 byte EBCDIC string containing a product qualifier or feature name (e.g. WAS Z/OS)
 * returnCode_p : The return code from the IFAUSAGE service.  A zero is good.  A four means you've registered for something
 *                else already which could be ok.  An eight means you are calling from unauthorized code and tried to register for
 *                more than two products.  A negative one means we couldn't get below-the-bar storage for our parms.  Anything else is just really bad.
 *
 */
void pc_registerProduct(regParms* parms);


/**
 * Deregister a product with z/OS.
 * All parameters must be padded with blanks to their full length.  Null termination is optional.
 *
 * owner_p      : A pointer to a 16 byte EBCDIC string identifying the product owner (e.g. IBM)
 * name_p       : A pointer to a 16 byte EBCDIC string identifying the product (e.g. WAS FOR Z/OS)
 * version_p    : A pointer to an 8 byte EBCDIC string identifying the product version (e.g. 8.5)
 * id_p         : A pointer to an 8 byte EBDDIC string containing the product identifier (e.g. 5655-W65)
 * qualifier_p  : A pointer to an 8 byte EBCDIC string containing a product qualifier or feature name (e.g. WAS Z/OS)
 * returnCode_p : The return code from the IFAUSAGE service.  A zero is good. An eight means you specified
 *                REQUEST=DEREGISTER for a product that has not first specified REQUEST=REGISTER. A negative
 *                one means we couldn't get below-the-bar storage for our parms.
 */
void pc_deregisterProduct(regParms* parms);

#endif
