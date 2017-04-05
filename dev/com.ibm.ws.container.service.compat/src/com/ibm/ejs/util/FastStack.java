// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.5 SERV1/ws/code/utils/src/com/ibm/ejs/util/FastStack.java, WAS.utils, WAS80.SERV1, h1116.09 5/20/10 09:51:13
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  FastStack.java
//
// Source File Description:
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d101420   ASV       20010508 rathnam  : New
// d117293   ASV       20020105 jmcgee   : Rename
// d146239.4 ASV       20021004 asisin   : Null array elements when removing
// d646139.1 WAS80     20100519 bkail    : Generify
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ejs.util;

public class FastStack<T> {

    private T[] stack;
    private int topOfStack;
    private int currentCapacity;

    public FastStack() {
        this(11);
    }

    public FastStack(int initialCapacity) {
        stack = createArray(initialCapacity);
        topOfStack = -1;
        currentCapacity = stack.length;
    }

    @SuppressWarnings("unchecked")
    private T[] createArray(int size) {
        return (T[]) new Object[size];
    }

    public T peek() {
        if (topOfStack >= 0)
            return (stack[topOfStack]);
        else
            return null;
    }

    public T push(T o) {
        ensureCapacity(topOfStack + 1);
        stack[++topOfStack] = o;
        return stack[topOfStack];
    }

    public T pop() {
        if (topOfStack >= 0) {
            // defect 146239.4 : Arvind Srinivasan 
            // Item was being popped, by the reference still remained in the stack
            //
            T result = stack[topOfStack];
            stack[topOfStack--] = null;
            //
            return result;
        }
        return null;
    }

    /**
     * Resetting a stack empties the stack.
     */
    public void reset() {
        // defect 146239.4 : Arvind Srinivasan
        //
        while (topOfStack >= 0)
            stack[topOfStack--] = null;
        //
    }

    private void ensureCapacity(int newCapacity) {
        if (newCapacity >= currentCapacity) {
            T[] newStack = createArray(currentCapacity + (2 * currentCapacity));
            System.arraycopy(stack, 0, newStack, 0, stack.length);
            stack = newStack;
            currentCapacity = stack.length;
        }
    }

    public int getTopOfStackIndex() {
        return topOfStack;
    }
}
