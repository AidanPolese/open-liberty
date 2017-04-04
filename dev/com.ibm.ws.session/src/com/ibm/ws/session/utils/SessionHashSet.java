/*COPYRIGHT_START***********************************************************
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *   IBM DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
 *   ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE. IN NO EVENT SHALL IBM BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 *   CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
 *   USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 *   OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 *   OR PERFORMANCE OF THIS SOFTWARE.
 *
 *  @(#) 1.1 SERV1/ws/code/web.session.core/src/com/ibm/ws/session/utils/SessionHashSet.java, WASCC.web.session.core, WASX.SERV1, o0901.11 10/13/06 15:53:08 [1/9/09 15:01:31]
 *
 * @(#)file   SessionHashSet.java
 * @(#)version   1.1
 * @(#)date      10/13/06
 *
 *COPYRIGHT_END*************************************************************/
package com.ibm.ws.session.utils;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SessionHashSet extends AbstractSet {

    Object[] keys;

    public SessionHashSet(Object[] keys) {
        this.keys = keys;
    }

    public Iterator iterator() {
        return getSessionHashIterator();
    }

    public int size() {
        if (keys == null) {
            return 0;
        }
        return keys.length;
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    private Iterator getSessionHashIterator() {
        if (size() == 0) {
            return sessionEmptyHashIterator;
        } else {
            return new SessionHashIterator();
        }
    }

    private static SessionEmptyHashIterator sessionEmptyHashIterator = new SessionEmptyHashIterator();

    // Internal class

    private static class SessionEmptyHashIterator implements Iterator {

        SessionEmptyHashIterator() {

        }

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class SessionHashIterator implements Iterator {

        int current = 0;;
        int end = keys.length;

        SessionHashIterator() {}

        // Does hasNext actually consume an entry without next?
        public boolean hasNext() {
            if (current < end) {
                return true;
            }
            return false;
        }

        public Object next() {

            if (current >= end) {
                throw new NoSuchElementException();
            }

            Object theObject = keys[current];
            current++;
            return theObject;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}
