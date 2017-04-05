package com.ibm.tx.jta.impl;
/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002,2010 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer    Defect   Description                                                         */
/*  --------  ----------    ------   -----------                                                         */
/*  02-07-24   awilkins              Creation for JTS2                                                   */
/*  05/09/02   gareth       ------   Move to JTA implementation                                          */
/*  02-10-03   awilkins     1444     Thread safety - add synchronization                                 */
/*  21/02/03   gareth   LIDB1673.19  Make any unextended code final                                      */
/*  09/07/03   hursdlg      169606   Base on LongObjectHashMap utility                                   */ 
/*  20/08/03   hursdlg      174685   General performance of getLocalTID                                  */ 
/*  01/10/03   johawkes     178208.1 Use log generated recovery ids                                      */
/*  26/07/04   hursdlg      219483   Add reserver back for zOS                                           */
/*  27/07/05   hursdlg      292064   Add lookupTransaction                                               */
/*  06/01/06   johawkes    306998.12 Use TraceComponent.isAnyTracingEnabled()                            */
/*  17/05/07   johawkes     438575   Further componentization                                            */
/*  06/06/07   johawkes     443467   Moved                                                               */
/*  29/06/07   johawkes     446894.1 Added clear()                                                       */
/*  24/06/08   johawkes     531265   Base on ConcurrentHashMap (possibly over synchronized now)          */
/*  02/06/09   mallam       596067   package move                                                        */
/*  01/06/10   johawkes     654731   Change suggested by perf team                                       */
/*  23/09/10   johawkes     663227   Another change suggested by perf team                               */
/*  13/10/10   johawkes     673797   Backing out the last perf change which has a bug in                 */
/*  21/10/10   johawkes     663227.1 Yet another change suggested by perf team                           */
/* ***************************************************************************************************** */
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.ibm.tx.TranConstants;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.ConcurrentHashSet;
import com.ibm.tx.util.logging.TraceComponent;

/**
 * Maintains a table of local TIDs mapped to Transactions. Every transaction running on
 * the server should have a single entry in the table.  The table is based on the
 * com.ibm.ws.recoverylog.utils.RecoverableUnitIdTable class, except we restrict the
 * id values to integer only and this table is static as there is only one instance of
 * the TM starting transactions in the server.
 */
public class LocalTIDTable
{
    /*
     * Allocate values between 1 and MAX_INT only
     * We need to only allocate int values as the external spi for getLocalTID returns int
     * Also, 0 is reserved for holding specific service log data.
     */
    private static TransactionImpl[] noTxns = new TransactionImpl[0];

    protected static final ConcurrentHashMap<Integer, TransactionImpl> localTIDMap = new ConcurrentHashMap<Integer, TransactionImpl>(256, 0.75f, ConcurrentHashSet.getNumCHBuckets());

    private static int _baseSeed = (int)System.currentTimeMillis();

    private static final TraceComponent tc = Tr.register(LocalTIDTable.class
                                    ,TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    /**
     * Return the next available local tid
     * and associate it with the given transaction.
     * This method should be used during the
     * creation of a new transaction.
     * 
     * @param tran The transaction to be associated
     * with the local TID
     * 
     * As suggested by the performance team, the LocalTIDTable used to maintain
     * an AtomicLong to maintain a unique value for transaction IDs in multiple threads.
     * This is a bottleneck, especially on Power/AIX environments where AtomicLong is
     * more costly. The performance team has now provided an implementation that does
     * not require any j.u.c. classes, so is more lightweight on all systems, providing
     * 1%-1.5% performance improvement on the DayTrader benchmark.

     * @return The next available local TID 
     */
    public static int getLocalTID(TransactionImpl tran)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getLocalTID", tran);

        int id;
        while (true)
        {
            // Capture the current seed value. Do another increment
            // and shift by the difference of current seed. Due to
            // randomness of thread access and Java memory model, this
            // will improve the key space to reduce collisions. Using
            // this approach to avoid using Thread.currentThread() or
            // a ThreadLocal variable.
            final int currSeed = _baseSeed++;
            id = (++_baseSeed << (_baseSeed - currSeed)) & 0x7FFFFFFF;

            // Conditionally add the new local tid to the map
            // associating it with the given transaction.
            // This has been modified to use non-optimistic putIfAbsent()
            // to address a race condition if put() is used on its own.
            if (id > 0 && localTIDMap.putIfAbsent(id, tran) == null)
            {
                // We're done
                break;
            }

            // If there is a clash, generate a new local TID until
            // we find one that is free.
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "getLocalTID", id);
        return id;
    }
    
    /**
     * Remove the given local tid from the map.
     *  This method should be called once a
     *  transaction has completed.
     * 
     * @param localTID The local TID to remove from the table
     */
    public static void removeLocalTID(int localTID)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "removeLocalTID", localTID);

        localTIDMap.remove(localTID);

        if (tc.isEntryEnabled()) Tr.exit(tc, "removeLocalTID");
    }

    /**
     * Return an array of all the transactions currently
     *  running on the server.
     *  @return An array of all the server's transactions. 
     */
    public static TransactionImpl[] getAllTransactions()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "getAllTransactions");

        final Collection<TransactionImpl> txns = localTIDMap.values();

        if (txns != null)
        {
            if (tc.isEntryEnabled()) Tr.exit(tc, "getAllTransactions", txns);
            return txns.toArray(noTxns);
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "getAllTransactions", noTxns);
        return noTxns;
    }

    public static void clear()
    {
        localTIDMap.clear();
    }
}