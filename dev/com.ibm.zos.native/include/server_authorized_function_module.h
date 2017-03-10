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
#ifndef _BBOZ_SERVER_AUTHORIZED_FUNCTION_MODULE_H
#define _BBOZ_SERVER_AUTHORIZED_FUNCTION_MODULE_H

#include "bbgzasvt.h"

#define AUTH_DEF_INCLUDES
#include "server_authorized_functions.def"
#undef AUTH_DEF_INCLUDES
/*-------------------------------------------------------------------*/
/* Structure of the server authorized module vector table.           */
/*-------------------------------------------------------------------*/
struct bbgzsafm {
  struct bbgzasvt_header header;
#define AUTH_DEF(svc_name, auth_name, impl_name, arg_type) bbgzasve impl_name;
#include "server_authorized_functions.def"
#undef AUTH_DEF
  char end_eyecatcher[16];
};

#endif
