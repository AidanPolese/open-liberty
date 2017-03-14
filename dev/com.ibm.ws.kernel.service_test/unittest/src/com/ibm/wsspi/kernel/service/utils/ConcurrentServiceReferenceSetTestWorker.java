/* /I/ /G/ /U/ /W/   <-- CMVC Keywords, replace / with %
 * %I% %G% %U% %W%
 *
 * ORIGINS: 27
 *
 * IBM Confidential OCO Source Material
 * 5724-J08 (C) COPYRIGHT International Business Machines Corp. 2005
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Reason           Date     Userid    Change Description
 * --------------- -------- --------- -------------------------------------------
 * LIDB3187-27     20051110  ehaaser   Basic test case for ZIOP implementation elements
 *
 */
package com.ibm.wsspi.kernel.service.utils;

import java.util.Iterator;
import java.util.Random;

/**
 * Runnable used to simulate creating a connection, creating requests and then destroying the connection.
 * Ensure clean up is correct.
 */
public class ConcurrentServiceReferenceSetTestWorker implements Runnable {

    private final Random rand = new Random();
    private final String name;
    private final ConcurrentServiceReferenceSetTest owner;

    public ConcurrentServiceReferenceSetTestWorker(String name, ConcurrentServiceReferenceSetTest owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public void run() {
        try {
            Iterator<String> iterator = owner.getServices();
            while (iterator.hasNext()) {
                String s = iterator.next();
                if (s.equals(name)) {
                    owner.removeReference(name);
                } else {
                    owner.addReference(name);
                }
                try {
                    Thread.sleep(rand.nextInt(2));
                } catch (InterruptedException e) {
                }
            }
            owner.addReference(name);
        } catch (Exception e) {
            owner.setException(e);
            e.printStackTrace(System.out);
        }

        // indicate that this connection thread is finished
        owner.finishThread();
    }

    public void setException(Exception e) {
        owner.setException(e);
    }
}
