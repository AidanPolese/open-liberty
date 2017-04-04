// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.3 SERV1/ws/code/utils/src/com/ibm/ws/util/LockFreeIndexedStack.java, WAS.runtime.fw.fvt, WASX.SERV1 9/30/10 09:29:20
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  LockFreeIndexedStack.java
//
// Source File Description:
//    Concurrent Stack class with index embedded. Avoids locks by using compare and swap
//    which allows it to avoid synchronization.
//
// Change Activity:
//
// Reason     Version   Date     Userid    Change Description
// ---------- --------- -------- --------- -----------------------------------------
// d658590    WAS80     20100826 brbroge   New Part
// d658590.1  WAS80     20100924 brbroge   force pushWithLimit to respect maxSize==0
// d658590.2  WAS80     20100929 brbroge   remove redundant maxSize check
// ---------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.util;

import java.util.concurrent.atomic.AtomicReference;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Concurrent Stack class with index embedded. It is based on the algorithm
 * proposed by Kent Treiber.
 * <p>
 * Basic idea is to avoid locks by using compare-and-swap (CAS) operations
 * instead. The <code>push</code> and <code>pop</code> operations use a CAS in a
 * while loop.
 * </p>
 * 
 * @author Zhi Gan (ganzhi@cn.ibm.com)
 */
@Trivial
public class LockFreeIndexedStack<E> {
    final AtomicReference<StackNode<E>> top;

    public LockFreeIndexedStack() {
        top = new AtomicReference<StackNode<E>>(null);
    }

    /**
     * Remove all nodes from stack and return the old top node.
     * 
     * @return the old top node, which can be used to iterate over all cleaned
     *         stack elements.
     */
    public StackNode<E> clean() {
        do {
            final StackNode<E> oldTop = top.get();
            if (top.compareAndSet(oldTop, null))
                return oldTop;

        } while (true);
    }

    // Node definition for stack
    public static class StackNode<E> {
        final E data;
        StackNode<E> next;
        int index;

        public StackNode(E d) {
            super();
            this.data = d;
        }

        public StackNode<E> getNext() {
            return next;
        }

        public E getValue() {
            return data;
        }
    }

    /**
     * Pop data from the Stack.
     * 
     * @return topmost element of the stack, or null if stack is empty.
     */
    public E pop() {
        StackNode<E> oldTop, newTop;

        while (true) {
            oldTop = top.get();
            if (oldTop == null)
                return null;
            newTop = oldTop.next;
            if (top.compareAndSet(oldTop, newTop))
                break;
        }

        return oldTop.data;
    }

    /**
     * Pop data from the Stack if its size is larger than <code>minSize</code>
     * 
     * @param minSize
     *            Lower bound of the stack size when poping
     * 
     * @return topmost element of the stack, or null if stack is empty or size
     *         is not larger than <code>minSize</code>.
     */
    public E popWithLimit(int minSize) {
        StackNode<E> oldTop, newTop;

        while (true) {
            oldTop = top.get();
            if (oldTop == null)
                return null;

            if (oldTop.index + 1 <= minSize)
                return null;

            newTop = oldTop.next;
            if (top.compareAndSet(oldTop, newTop))
                break;
        }

        return oldTop.data;
    }

    /**
     * Push data onto Stack.
     * 
     * @param d
     *            data to be pushed onto the stack.
     */
    public void push(E d) {
        StackNode<E> oldTop, newTop;

        newTop = new StackNode<E>(d);

        while (true) {
            oldTop = top.get();
            newTop.next = oldTop;
            if (oldTop != null)
                newTop.index = oldTop.index + 1;
            else
                newTop.index = 0;
            if (top.compareAndSet(oldTop, newTop))
                return;
        }
    }

    /**
     * Push data onto Stack while keeping size of the stack under
     * <code>maxSize</code>
     * 
     * @param d
     *            data to be pushed onto the stack.
     * @param maxSize
     *            Maximal size of the stack.
     * 
     * @return <code>True</code> if succeed. False if the size limitation has
     *         been reached
     */
    public boolean pushWithLimit(E d, int maxSize) {
        StackNode<E> oldTop, newTop;

        newTop = new StackNode<E>(d);

        while (true) {
            oldTop = top.get();

            newTop.next = oldTop;
            if (oldTop != null) {
                newTop.index = oldTop.index + 1;
                if (newTop.index >= maxSize)
                    return false;
            } else {
                if (maxSize == 0)
                    return false;
                newTop.index = 0;
            }

            if (top.compareAndSet(oldTop, newTop))
                return true;
        }
    }

    /**
     * Check to see if Stack is empty.
     * 
     * @return true if stack is empty.
     */
    public boolean isEmpty() {
        if (top.get() == null) {
            return true;
        } else {
            return false;
        }
    }

    public int size() {
        final StackNode<E> oldTop = top.get();
        if (oldTop == null)
            return 0;
        else
            return oldTop.index + 1;
    }

    /**
     * Return copy of the top data on the Stack
     * 
     * @return copy of top of stack, or null if empty.
     */
    public E peek() {
        final StackNode<E> oldTop = top.get();
        if (oldTop == null) {
            return null;
        } else {
            return oldTop.data;
        }
    }
}
