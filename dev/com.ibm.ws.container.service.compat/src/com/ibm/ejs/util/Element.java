// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.4 SERV1/ws/code/utils/src/com/ibm/ejs/util/Element.java, WAS.utils, WASX.SERV1, aa1225.01 10/11/10 09:53:56 
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2000, 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  Element.java
//
// Source File Description:
//
//     Element is a "holder" object used by the FasthHashtable Bucket to store
//     the hashtable key alongside each cached object, and provides a 'next'
//     pointer to another Element for use as a linked list.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d218838   WAS60     20040818 tkb      : PERF: redesigned for performance/size
// d366845.3 EJB3      20060615 kjlaw    : add generics for EJB3 usage.
// F743-33365
//           WAS80     20101011 bkail    : Fix generics
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ejs.util;

/**
 * Element is a "holder" object used by the FasthHashtable Bucket to store
 * the hashtable key alongside each cached object, and provides a 'next'
 * pointer to another Element for use as a linked list. <p>
 * 
 * The key and associated object may not be modified once the Element is
 * created, however it is the responsibility of the Bucket to maintain the
 * 'next' pointer for the linked list. <p>
 **/
//d366845.3 add generic types support
final class Element<K, V> {
    /** The key associated with the contained object. **/
    final K ivKey;

    /** The contained object, associated with key. **/
    final V ivObject;

    /** Pointer to next element in the FastHashtable Bucket. **/
    Element<K, V> ivNext;

    /**
     * Construct an <code>Element</code> object, holding the specified
     * key and object. <p>
     * 
     * @param key the key associated with the object to be held.
     * @param object the object to store in the hashtable bucket.
     **/
    Element(K key, V object) {
        this.ivKey = key;
        this.ivObject = object;
    }

    public String toString() {
        return "Element: " + ivKey.toString() + " " + ivObject.toString();
    }

}
