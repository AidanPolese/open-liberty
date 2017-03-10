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
#include "include/angel_check_main.h"
#include "include/angel_check_module.h"

const struct bbgzachk BBGZACHK = {
    .eyecatcher                 = "BBGZACHK",
    .angel_check_version        = 2,
    .angel_check                = angel_check
};
