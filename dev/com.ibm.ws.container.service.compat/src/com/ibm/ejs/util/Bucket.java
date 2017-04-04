// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.4 SERV1/ws/code/utils/src/com/ibm/ejs/util/Bucket.java, WAS.utils, WASX.SERV1, aa1225.01 10/11/10 09:53:54
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2000, 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  Bucket.java
//
// Source File Description:
//
//     Bucket is the hash table bucket abstraction for FastHashtable,
//     implementing the basic operations needed. Each bucket is essentially
//     an unsorted colleciton of Element objects.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d218838   WAS60     20040818 tkb      : PERF: redesigned for performance/size
// d366845.3 EJB3      20060615 kjlaw    : add generics for EJB3 usage.
// F743-33394
//           WAS80     20101011 bkail    : Add clear
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ejs.util;

/**
 * Bucket is the hash table bucket abstraction for FastHashtable,
 * implementing the basic operations needed. Each bucket is essentially
 * an unsorted colleciton of Element objects. <p>
 **/
// d366845.3 add generic types support
final class Bucket<K, V> {
    /** The head/start of the list of elements in the Bucket. **/
    Element<K, V> ivHead;

    /** The number of elements currently in the Bucket. **/
    private int ivNumElements;

    /**
     * NOTE: FastHashtable lazily constructs Bucket objects using double-checked
     * locking. For this to be safe, the Bucket class must not have any
     * initialization, which includes explicit "= null" or "= 0" for member
     * variables.
     */
    Bucket() {
        // Do not add member variables that require initialization.
    }

    Element<K, V> findByKey(K key) {
        for (Element<K, V> e = ivHead; e != null; e = e.ivNext) {
            if (key.equals(e.ivKey)) {
                return e;
            }
        }

        return null;
    }

    Element<K, V> replaceByKey(K key, V object) {
        final Element<K, V> element = removeByKey(key);
        addByKey(key, object);
        return element;
    }

    void addByKey(K key, V object) {
        Element<K, V> newElement = new Element<K, V>(key, object);
        newElement.ivNext = ivHead;
        ivHead = newElement;
        ivNumElements++;
    }

    Element<K, V> removeByKey(K key) {
        Element<K, V> previous = null;

        for (Element<K, V> e = ivHead; e != null; e = e.ivNext) {
            if (key.equals(e.ivKey)) {
                if (previous == null)
                    ivHead = e.ivNext;
                else
                    previous.ivNext = e.ivNext;
                ivNumElements--;

                return e;
            }
            previous = e;
        }
        return null;
    }

    /**
     * Returns the number of elements in this Bucket / list. <p>
     * 
     * @return the number of elements in this Bucket.
     **/
    public int size() {
        return ivNumElements;
    }

    void clear() {
        ivNumElements = 0;
        ivHead = null;
    }

}
