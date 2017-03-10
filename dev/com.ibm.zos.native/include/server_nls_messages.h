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
 * Functions to handle native NLS translations
 *
 */

#ifndef SERVER_NLS_MESSAGES_H_
#define SERVER_NLS_MESSAGES_H_

char * getTranslatedMessageById(int messageId, char * defaultMessage);
void   closeMessageCatalog();

#endif

