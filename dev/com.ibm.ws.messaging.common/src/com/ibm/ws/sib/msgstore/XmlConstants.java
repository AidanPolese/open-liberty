/*
 * 
 * 
 * ============================================================================
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2012,2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date        Origin       Description
 * --------------- ----------  -----------  --------------------------------------------
 *                 271003      van Leersum  Original
 * 288073          130705      schofiel     Dump consolidation
 * 335528          040106      schofiel     Items not cached following restore - dump size
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore;

public interface XmlConstants {
    public static final String XML_LOGICALLY_DELETED = "logicallyDeleted";
    public static final String XML_CAN_EXPIRE_SILENTLY = "canExpireSilently";
    public static final String XML_PRIORITY = "priority";
    public static final String XML_CLASS = "class";
    public static final String XML_STORAGE_STRATEGY = "storageStrategy";
    public static final String XML_STORE_ALWAYS = "StoreAlways";
    public static final String XML_STORE_EVENTUALLY = "StoreEventually";
    public static final String XML_STORE_MAYBE = "StoreMaybe";
    public static final String XML_STORE_NEVER = "StoreNever";
    public static final String XML_EXPIRY_TIME = "expiryTime";
    public static final String XML_SEQUENCE = "sequence";
    public static final String XML_TRANID = "tranID";
    public static final String XML_LOCKID = "lockID";
    public static final String XML_REFERRED_ID = "referredID";
    public static final String XML_STATE = "state";
    public static final String XML_ID = "id";
    public static final String XML_BACKOUT_COUNT = "backoutCount";
    public static final String XML_UNLOCK_COUNT = "unlockCount";
    public static final String XML_SIZE = "size";

    public static final String XML_ITEMS = "items";
    public static final String XML_ITEM_STREAMS = "itemStreams";
    public static final String XML_REFERENCE_STREAMS = "referenceStreams";
    public static final String XML_REFERENCES = "references";

    public static final String XML_MESSAGE_STORE = "MessageStore";
    public static final String XML_ITEM_STORAGE_MANAGER = "itemStorageManager";
    public static final String XML_STORED_ITEM_MANAGER = "storedItemManager";
    public static final String XML_UNSTORED_ITEM_MANAGER = "unstoredItemManager";
    public static final String XML_ITEM_MAP = "itemMap";
    public static final String XML_EXPIRER = "expirer";
    public static final String XML_DELIVERYDELAYMANAGER = "deliveryDelayManager";
    public static final String XML_CACHE_LOADER = "cacheLoader";
    public static final String XML_STORED_EXCEPTION = "storedException";
    public static final String XML_ROOT_MEMBERSHIP = "root";
    public static final String XML_ITEM = "item";
    public static final String XML_ITEM_STREAM = "itemStream";
    public static final String XML_REFERENCE_STREAM = "referenceStream";
    public static final String XML_REFERENCE = "reference";
    public static final String XML_CURSORS = "cursors";
    public static final String XML_SUBLIST = "sublist";

    public static final String XML_DATA_STORE = "dataStore";
    public static final String XML_FILE_STORE = "fileStore";
}
