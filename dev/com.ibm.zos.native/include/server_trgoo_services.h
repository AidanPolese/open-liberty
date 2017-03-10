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
#ifndef SERVER_TRGOO_SERVICES_H_
#define SERVER_TRGOO_SERVICES_H_

#include "bbgztrgoo.h"

#define BBGZTRGOO_TOKEN_NAME "BBGZTRGOO       "

/**
 * Return a pointer to the TRGOO for this process.
 * @return a pointer to the TRGOO for this process, or NULL if none.
 */
bbgztrgoo* getTRGOO(void);

#endif /* SERVER_TRGOO_SERVICES_H_ */
