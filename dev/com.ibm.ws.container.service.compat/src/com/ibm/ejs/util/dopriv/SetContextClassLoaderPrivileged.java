// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.5 SERV1/ws/code/utils/src/com/ibm/ejs/util/dopriv/SetContextClassLoaderPrivileged.java, WAS.ejbcontainer, WASX.SERV1 4/30/09 10:17:10
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  SetContextClassLoaderPrivileged.java
//
// Source File Description:
//
//     Sets the context classloader while in privileged mode.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// 140003.3            20020814 rschnier : Initial impl
// 146034.6            20021015 rschnier : Set new only if new != current
// d171437.1 ASV51     20030711 jckrueg  : organize imports
// 369927    WAS61     20070320 bkail    : Use ThreadContextAccessor
// PK83186   WAS70     20090427 hthomann : Fix ClassLoader leak
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ejs.util.dopriv;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ibm.ws.util.ThreadContextAccessor;

/**
 * This class sets the context classloader while in privileged mode. Its purpose
 * is to eliminate the need to use an anonymous inner class in multiple modules
 * throughout the product, when the only privileged action required is to
 * set the context classloader on the current thread.
 */
public class SetContextClassLoaderPrivileged implements PrivilegedAction {
    //PK83186: made these instance vars private so that this class has complete control over the 
    //setting/clearing of these variables.
    private ClassLoader oldClassLoader, newClassLoader;

    // Instance var is public to allow fast setting/getting by caller if this object is reused
    public boolean wasChanged;

    // 369927 - update ctors to take a ThreadContextAccessor
    private final ThreadContextAccessor threadContextAccessor;

    public SetContextClassLoaderPrivileged(ThreadContextAccessor threadContextAccessor) {
        this.threadContextAccessor = threadContextAccessor;
    }

    public SetContextClassLoaderPrivileged(ThreadContextAccessor threadContextAccessor, ClassLoader newCL) {
        this(threadContextAccessor);
        newClassLoader = newCL;
    }

    // 369927
    /**
     * Acquires the current thread context classloader and sets a new context
     * classloader if <code>newClassLoader</code> is different from the
     * current one. The current context classloader is set stored in
     * <code>oldClassLoader</code>. If the classloader was changed, then
     * <code>wasChanged</code> is set to <code>true</code>; otherwise, it is
     * set to <code>false</code>.
     * 
     * <p>This calls <code>AccessController.doPrivileged</code> if necessary.
     * 
     * PK83186: Changed this method such that it takes the CL which the caller
     * would like to attempt to change to. Upon method exit, this method will
     * set the old/newClassLoader variables to null in order to avoid a
     * CL leak.
     * 
     * @param cl - the ClassLoader which the caller would like to change to.
     * 
     * @return <code>oldClassLoader</code>
     */
    public ClassLoader execute(ClassLoader cl) {

        //PK83186 start
        newClassLoader = cl;

        if (threadContextAccessor.isPrivileged()) {
            cl = (ClassLoader) run();
        } else {
            cl = (ClassLoader) AccessController.doPrivileged(this);
        }

        newClassLoader = null;
        oldClassLoader = null;

        return cl;
        //PK83186 end
    }

    // 369927

    // 146064.3
    public Object run() {
        Thread currentThread = Thread.currentThread();
        oldClassLoader = threadContextAccessor.getContextClassLoader(currentThread); // 369927

        // The following tests are done in a certain order to maximize performance
        if (newClassLoader == oldClassLoader) {
            wasChanged = false;
        } else if ((newClassLoader == null && oldClassLoader != null)
                   || (newClassLoader != null &&
                   (oldClassLoader == null || !(newClassLoader.equals(oldClassLoader))))) {
            // class loaders are different, change to new one
            threadContextAccessor.setContextClassLoader(currentThread, newClassLoader); // 369927
            wasChanged = true;
        } else
            wasChanged = false;

        return oldClassLoader;
    }
} // SetContextClassLoaderPrivileged

