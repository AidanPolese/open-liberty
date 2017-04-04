package com.ibm.tx.jta.impl;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/* ORIGINS: 27                                                                                           */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002,2014 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]
 /*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer  Defect         Description                                                     */
/*  --------  ----------  ------         -----------                                                     */
/*  05/09/02  gareth      ------         Move to JTA implementation                                      */
/*  30/09/02  gareth      1438           Improve container performance                                   */
/*  30/09/02  gareth      1412.1         Add VoteReadOnly optimization                                   */
/*  03/10/02  gareth      1443           TransactionState defaults to ACTIVE                             */
/*  03/10/02  gareth      1441           Use updated transaction states                                  */
/*  09/10/02  gareth      1440           Various code review changes                                     */
/*  09/10/02  hursdlg     1453           Update XID usage & RecoveryManager                              */
/*  11/10/02  awilkins    1452           XAException handling                                            */
/*  11/10/02  hursdlg     1432           Cleanup up sections                                             */
/*  14/10/02  mallam      1447           various updates                                                 */
/*  14/10/02  hursdlg     1460           Cancel timeouts prior to completion                             */
/*  14/10/02  hursdlg     1454           Reorder enlistResource for 1PC/2PC                              */
/*  15/10/02  hursdlg     1455           Add further recovery support                                    */
/*  17/10/02  awilkins    1467           Combine 1PC attempt w/ distributePrepare                        */
/*  17/10/02  hursdlg     1461           Clean up at end of transaction                                  */
/*  18/10/02  mallam      1469           Interceptor functions and timeout                               */
/*  18/10/02  awilkins    1465           Handling of heursitics                                          */
/*  23/10/02  hursdlg     1433           Prepare/commit/rollback split                                   */
/*  11/11/02  hursdlg     1473           In-doubt transaction timer                                      */
/*  14/11/02  gareth      1481           Implement LPS Basic                                             */
/*  18/11/02  beavenj     1502           Handle new recovery.log exceptios                               */
/*  18/11/02  mallam      1495           Async retries                                                   */
/*  19/11/02  hursdlg     1487           Perform recCoord narrow on replay_compl                         */
/*  21/11/02  awilkins    1507           JTS -> JTA. Thread local restructuring                          */
/*  21/11/02  hursdlg     1503           Multiple finishTransactions on rollback                         */
/*  25/11/02  hursdlg     1506           Take context off thread during completn                         */
/*  25/11/02  hursdlg     1503           Move local/globalTID for factory create                         */
/*  26/11/02  awilkins    1513           Repackage ejs.jts -> ws.Transaction                             */
/*  26/11/02  gareth      1482           Implement PMI statistics                                        */
/*  02/12/02  mallam      1524           NPE on enlistresource                                           */
/*  02/12/02  mallam      1525           no beforeCompletion when rollbackOnly                           */
/*  02/12/02  awilkins    1526           setRbO, regSync; no SystemException                             */
/*  02/12/02  hursdlg     1480           Subordinate synchronizations, etc                               */
/*  02/12/03  awilkins    1504           Non interop tx contexts                                         */
/*  04/12/02  hursdlg     1530           Delist to not remove resource entry                             */
/*  05/12/02  hursdlg     1519           Add toString                                                    */
/*  05/12/02  awilkins    1535           Implement getTxType on UOWCoordinator                           */
/*  09/12/02  awilkins    LIDB1673.16    Implement administrative finish of tx                           */
/*  09/12/02  hursdlg     LIDB1673.2.3   Support HeuristicRollback exception                             */
/*  10/12/02  gareth      LIDB1673.3     Explicit RAS for API/SPI's                                      */
/*  10/12/02  hursdlg     LIDB1673.2     Fix up LPSLoggingEnabled                                        */
/*  13/12/02  mallam      LIDB1673.18    Synchronous completion for usertrans                            */
/*  16/12/02  hursdlg     LIDB1673.2     Improve formatID tracing                                        */
/*  17/12/02  awilkins    LIDB1673.12    Implement hooks into LI850 code                                 */
/*  17/12/02  mallam      LIDB1673.xx    Further changes for passive timeout                             */
/*  07/01/03  hursdlg     LIDB1673.9     Initial support for WSCoordinator                               */
/*  17/01/03  mallam      LIDB1673.23    Prevent multiple active threads                                 */
/*  17/01/03  awilkins    1673.9         Tidy-up exception handling                                      */
/*  21/01/03  gareth      LIDB1673.1     Add JTA2 messages                                               */
/*  23/01/03  awilkins    LIDB1673.13    Store control ref for JTS prog model                            */
/*  28/01/03  hursdlg     LIDB1673.9.1   Update partner log support                                      */
/*  30/01/03  mallam      LIDB1673.24    Inactivity Timer                                                */
/*  03/02/03  awilkins    157440         Don't allow setRBO when tx is complete                          */
/*  14/02/03  awilkins    158466         Improve SystemException processing                              */
/*  17/02/03  hursdlg     LIDB1673.9.3   Support destroy callbacks                                       */
/*  21/02/03  gareth      LIDB1673.19    Make any unextended code final                                  */
/*  28/02/03  hursdlg     LIDB1673.19.1  Reduce Vector usage                                             */
/*  25/02/03  mallam      LIDB1673.9.2   Prolong finish                                                  */
/*  06/03/03  hursdlg     160279         NPE/CCE on subordinate recovery                                 */
/*  10/03/03  hursdlg     159733         No afterCompletion on recovery required                         */
/*  11/03/03  mallam      160280         CompleteTxTimeout - no exceptions rbonly                        */
/*  13/03/03  mallam      160808         Correct recovery of heuristic states                            */
/*  19/03/03  mallam      161125         Propagate heuristicRollback                                     */
/*  03-03-21  awilkins    161528         Fix JTA2 -> JTA2 comms in interop mode                          */
/*  21/03/03  hursdlg     161127         Reset state on finish                                           */
/*  31/03/03  hursdlg     161441         Rename prolongResolution to Finish                              */
/*  31/03/03  hursdlg     LIDB1673.22    Validate remote resource enlistment                             */
/*  02/04/03  hursdlg     162470         LogCursor.close() changes                                       */
/*  03/04/03  mallam      162554         No rollbackResources on setRBOnly                               */
/*  15/04/03  hursdlg     LIDB1673.22    Check LPS state on enlistment                                   */
/*  30/04/03  ihodges     132357         Check for null resources in enlist                              */
/*  11/06/03  hursdlg     169107         Update RecoveryLog interfaces                                   */
/*  30/06/03  hursdlg     170434         Log partner XA data at enlist time                              */
/*  03/07/03  hursdlg     171105         Ensure non-interop txn is initialized                           */
/*  09/07/03  hursdlg     169606         Updates for LocalTIDTable                                       */
/*  15/07/03  hursdlg     171555.2       Cache toString result                                           */
/*  15/07/03  mallam      171151         Allow enlist/regSync when marked rbonly                         */
/*  15/07/03  hursdlg     170422         Support finish of active transactions                           */
/*  18/07/03  johawkes    LIDB2110.12    JCA 1.5                                                         */
/*  28/07/03  hursdlg     172649         Check OBJECT_NOT_EXIST minor codes                              */
/*  28/07/03  hursdlg     172471         Handle ISE from logRecoveryEntry                                */
/*  31/07/03  hursdlg     173100         Use lastData on RecoverableUnitSection                          */
/*  03-07-31  irobins     173218         Add getRollbackOnly to UOWCoordinator                           */
/*  14/08/03  hursdlg     174223         Cancel timeout on operator finish                               */
/*  14/08/03  hursdlg     173163         Correct preparing state on finish                               */
/*  15/08/03  hursdlg     174113         Reorder recovery                                                */
/*  01/09/03  beavenj     175242         Remove 'deferred' flag from enlistResource                      */
/*  28/08/03  johawkes    174516         Distribute all ends before prepares                             */
/*  15/09/03  hursdlg     176247         Reorder enlistResource for performance                          */
/*  18/09/03  hursdlg     177194         Migrate to 8 byte recovery ids                                  */
/*  22/09/03  hursdlg     174209         Access RecoveryManager                                          */
/*  01/10/03  johawkes    178208.1       Use log generated recovery ids                                  */
/*  20/10/03  mallam      LIDB1673-13    New WCCM attributes                                             */
/*  28/10/03  johawkes    181147         Make XATerminators specific to RA                               */
/*  29/10/03  johawkes    181331         Flag recovered imported txns subordinate                        */
/*  20/11/03  johawkes    182862         Remove static partner log dependencies                          */
/*  24/11/03  awilkins    183479         Synchronization tiers                                           */
/*  27/11/03  johawkes    178502         Start an RA during XA recovery                                  */
/*  02/12/03  johawkes    184169         Remove static servant mgr dependencies                          */
/*  02/12/03  hursdlg     LIDB2775       Move perfmetrics here                                           */
/*  05/12/03  johawkes    184903         Refactor PartnerLogTable                                        */
/*  08/12/03  mallam      184605         resumeAssociation and timeout                                   */
/*  15/12/03  mallam      184757         Propagate rollback_only to superior                             */
/*  06/01/04  hursdlg     LIDB2775       zOS/distributed merge                                           */
/*  07/01/04  johawkes    LIDB2110       RA Uninstall                                                    */
/*  12/01/04  jstidder    LI3162         New exception for multiple 1PC enlist                           */
/*  20/01/04  johawkes    187679         recoverCommit/Rollback now public                               */
/*  30/01/04  johawkes    187239         Handle HeuristicHazard responses                                */
/*  30/01/04  johawkes    187697         Heuristic direction on retry timeout                            */
/*  04/02/04  johawkes    189497         Fix recovery for RA imported transactions                       */
/*  04/02/04  hursdlg     189567         Messages for heuristic completion                               */
/*  05/02/04  mallam      LIDB2775       Remove synchronous completion                                   */
/*  19/02/04  hursdlg     191317         Message WTRN0102                                                */
/*  19/02/04  hursdlg     LIDB2775       Move LPS to registeredresources                                 */
/*  23/02/04  johawkes    190337         Preserve heuristic outcome                                      */
/*  04/03/04  johawkes    191316         Log resources when setting LPS state                            */
/*  14/03/04  hursdlg     194240         RegisteredSyncs change                                          */
/*  17/03/04  johawkes    192653         Cancel timeouts on RA uninstall                                 */
/*  24/03/04  mallam      LIDB2775       ws390 code drop                                                 */
/*  30/03/04  johawkes    195926         Preserve SE after NOTA on start                                 */
/*  08/04/04  awilkins    196680         Begin LTC with correct boundary                                 */
/*  13/04/04  beavenj     LIDB1578.1     Initial supprort for ha-recovery                                */
/*  31/03/04  mallam      LIDB2775       Rework retry and after_completions                              */
/*  16/04/04  johawkes    198909         Use TxPrimaryKey                                                */
/*  19/04/04  johawkes    193919.1       New methods for adminconsole                                    */
/*  20/04/04  johawkes    193919.3       More new methods for adminconsole                               */
/*  22/04/04  awilkins    198904.1       getXid changes                                                  */
/*  15/04/04  beavenj     LIDB1578.3     Termination support for ha-recovery                             */
/*  26/04/04  awilkins    186747.4       Initial get/set TaskId implementation                           */
/*  27/04/04  johawkes    196302         Implement sync levels                                           */
/*  27/04/04  johawkes    200354         Manage partner log counts in recovery                           */
/*  27/04/04  mallam      197039         Prolong finish for heuristic on recovery                        */
/*  05/05/04  mallam      LIDB2775-103   supsend before afterCompletion                                  */
/*  26/04/04  dmatthew    LIDB1922       Initial WSAT support                                            */
/*  08/05/04  hursdlg     LIDB2775-39    Fix destroy log access                                          */
/*  14/05/04  awilkins    202175         Implement UOWScope                                              */
/*  17/05/04  mallam      203235         class cast exception                                            */
/*  21/05/04  beavenj     LIDB1578.7     FFDC                                                            */
/*  28/05/04  johawkes    204553         Added isJCAImportedAndPrepared()                                */
/*  01/06/04  mallam      LIDB2775       Add in native context                                           */
/*  02/06/04  johawkes    203903         Propagate heuristic commit properly                             */
/*  09/06/04  hursdlg     207916         Partial backout of commitXAResources                            */
/*  10/06/04  hursdlg     209073         Illuma code analysis                                            */
/*  15/06/04  johawkes    209345         Remove unnecessary code                                         */
/*  16/06/04  mallam      209954         Fix LPS commit called twice                                     */
/*  25/06/04  johawkes    199785         Fix partner log corruption on shutdown                          */
/*  28/06/04  mallam      LIDB2775       Timeout on zOS                                                  */
/*  01/07/04  hursdlg     210818.1       zOS performance                                                 */
/*  06/07/04  johawkes    213406         Fix internalPrepare trace                                       */
/*  12/07/04  mallam      LIDB2775       zOS c++ callbacks                                               */
/*  27/07/04  johawkes    218403         Fix removal from heuristic list                                 */
/*  27/07/04  johawkes    219412         Fix shutdown for JCA imported transactions                      */
/*  27/07/04  hursdlg     219483         z/OS bringup                                                    */
/*  29/07/04  johawkes    219736         Mark manually completed txs as such                             */
/*  30/07/04  johawkes    220077         distibuteAfter on ONE_PHASE_OPT_FAILED                          */
/*  10-08-04  beavenj     221931         Fix cleanup logic for recovery logs                             */
/*  10-08-04  kaczyns     LIDB2110       z/OS JCA 1.5 support                                            */
/*  13-08-04  hursdlg     LIDB2775       z/OS prepare cases                                              */
/*  17-08-04  kaczyns     224624         Wrong Xid on z/OS imported JCA tran                             */
/*  20-08-04  hursdlg     224508         Timeout checks                                                  */
/*  20-08-04  hursdlg     225132         Support for PQ88648/PQ89426 V4/V5 servers                       */
/*  25/08/04  johawkes    226534         Fix commit_one_phase & refactor                                 */
/*  25/08/04  hursdlg     225566         Update state after ONE_PHASE_OPT                                */
/*  25/08/04  kaczyns     225356         Move cleanupTransaction to forget phase                         */
/*  25-08-04  dmatthew    220510         WSAT HA code drop                                               */
/*  13/09/04  mallam      231085         zOS subord rollback, distributeforget                           */
/*  17/09/04  johawkes    232559         Change heuristic rollback behaviour in c1p                      */
/*  23/09/04  hursdlg     234143         Filter LogClosedException on HA shutdown                        */
/*  23/09/04  hursdlg     234158         Put tran in tables etc once reconstruct ok                      */
/*  13/10/04  hursdlg     238432         Missing subordinate heuristic cases                             */
/*  20/10/04  awilkins    240363         PMI - ensure that completed is called                           */
/*  10/11/04  mezarin     228499         z/OS JCA 1.5 optimization                                       */
/*  15/11/04  hursdlg     244434         z/OS XA readonly optimization                                   */
/*  16/11/04  hursdlg     244432         SystemException and distributeForget                            */
/*  10/12/04  hursdlg     244431         Process heuristic on xa c1p for zOS                             */
/*  14/12/04  hursdlg     246070         Set RBO after prepareXA with sysexcep                           */
/*  16/12/04  hursdlg     241020         Check state after prepare failure                               */
/*  21/12/04  hursdlg     248234         Restore call RR.distributeEnd from intRB                        */
/*  23/12/04  johawkes    231095         Added trace and removed unused code                             */
/*  19/11/04  hursdlg     1922-5         z/OS WSAT                                                       */
/*  10/01/05  johawkes    235532.1       Added hasSuspendedAssociations                                  */
/*  11/01/05  hursdlg     249308         Save JCA inbound Xid for recovery                               */
/*  25/01/05  dmatthew    251553         promote WSAT Data                                               */
/*  08/02/05  dmatthew    253503         fix WSAT recovery                                               */
/*  17/01/05  mallam      LIDB3645       Recovery-mode restart                                           */
/*  19/02/05  johawkes    LIDB3605-32    Secure protocol messages                                        */
/*  02/03/05  dmatthew    257583         WSAT log raw data WSAT recovery not java                        */
/*  30/03/05   mallam       250698    Bypass LPS check zOS when only RRS interests                       */
/*  12/04/05  hursdlg       266464       Log subordinate failed xa resources                             */
/*  20/04/05  hursdlg       265487       CommitNativeContext/Heuristic and JCA                           */
/*  20/05/05  johawkes    270718         Protect runAsSystem calls as privileged                         */
/*  05/07/05  johawkes    252569         Process Faults                                                  */
/*  12/07/05  hursdlg     281425         AfterCompletion status unknown on c1p heuristics                */
/*  15/08/05  kaczyns     LI3187         Timeout values for z/OS                                         */
/*  12/10/05  hursdlg     LI3187-3       Only promote wsat if not imported                               */
/*  27/10/05  johawkes    316435.1       getGlobalGlobalID                                               */
/*  03/11/05  johawkes    319724         resend prepared on timeout not replay                           */
/*  07/11/05  hursdlg     LIDB3187-3.4   Set subordinate flag prior to z/OS native setup                 */
/*  24/11/05  brailsfo    322670 (PK15024) rollbackOnly in inactivity timeout                            */
/*  09/11/05  kaczyns     LI3187         Set subordinate before native ctx init                          */
/*  06/12/05  hursdlg     329577         Remove redundant length checks on z/os enlistAsyncResource      */
/*  06/01/06  johawkes    306998.12      Use TraceComponent.isAnyTracingEnabled()                        */
/*  06/01/26  hursdlg     338736.1       Move z/os register serialization out to z/os code               */
/*  30/01/06  hursdlg     340103         Run isProviderInstalled with server subject                     */
/*  15/03/06  brailsfo    PK19059        Pass back original exception                                    */
/*  25/04/06  hursdlg     360413         Recovery retry changes                                          */
/*  09/08/06  awilkins    LIDB4244-4     Support for JTA and UOW Synchronization Registries              */
/*  06-09-13  awilkins    390331         put/getResource with null key should NPE                        */
/*  06-09-14  awilkins    390905         Report status as commiting when we're doing a C1P optimization  */
/*  06/03/06  kaczyns     LI4166         Cascaded Transactions                                           */
/*  06-10-16  mezarin     397825         Add committing_one_phase state                                  */
/*  06/11/08  johawkes    403854         distributeEnd in recoverRollback                                */
/*  06/11/13  johawkes    403854.1       check active before distributeEnd in recoverRollback            */
/*  12/12/06  johawkes    LIDB4171       Tolerate absence of recovery log                                */
//  07/04/12  johawkes    LIDB4171-35    Componentization
//  07/04/12  johawkes    430278         Further componentization
//  07/04/16  awilkins    415521         Add getUOWName()
//  07/05/01  johawkes    435869         Overrides
//  07/05/01  johawkes    434414         Remove WAS dependencies
//  07/05/21  hursdlg     396035         Recovery retry changes for heuristics
//  07/05/30  johawkes    440135         Various things reported by FindBugs
//  07/06/06  johawkes    443467         Moved
//  07/06/17  johawkes    444613         Repackaging
//  26/06/07  johawkes    446894         Fix JTM shutdown delay
//  26/06/07  johawkes    446894.1       Cache alarm manager
//  07/06/30  hursdlg     LI3968-1.2     New definition
//  07/07/23  johawkes    413023         Modify retry timeout behaviour
//  07/08/06  johawkes    451213.1       Move JCA stuff back here
//  07/08/15  johawkes    451213         Move LPS stuff back here
//  07/08/20  johawkes    459952         Move completeTxTimeout out of JTM
/*  07-08-24  johawkes    461798         Reconstruct correct classes in recovery                         */
/*  07-08-31  johawkes    463941         Defer some initialization                                       */
/*  07-11-06  brailsfo    PK53879.tran   Allow mediations to create LPS trans                            */
/*  07-11-21  awilkins    481738         Output stack of thread when tx times out                        */
/*  07/12/19  hursdlg     489664         Move tran shutdown logic to TransactionImpl                     */
/*  07-10-31  dgrittr     377762.2       Full cascaded support                                           */
/*  08/07/08  johawkes    532379         Cache ConfigurationProviderManager.getConfigurationProvider()   */
/*  09/06/02  mallam      596067         package move                                                    */
/*  09-08-19  mallam      602532.3       ltc bundle                                                      */
/*  09-12-23  tranteam    F923-4350      Tight branch coupling                                           */
/*  10-03-17  hursdlg     PM07874        Audit recovery logging                                          */
/*  10-03-23  johawkes    640174.1       Remove TimeoutInfo map                                          */
/*  10-04-23  brailsfo    PM12850        Preserve partner log info for all trans, regardless of state    */
/*  10-05-13  hursdlg     649934         Extra changes from PM07874 for remote cases                     */
/*  10-06-01  johawkes    654731         Change suggested by perf team                                   */
/*  10-10-15  ajw         PM24601        Add XAException to Heuristic Exceptions                         */
/*  11-02-16  johawkes    691590         Add property to disable 2PC                                     */
/*  11-02-15  hursdlg     690084         Update branch coupling support                                  */
/*  11-05-24  jstidder    PM39333        Log heuristics to console during recovery                       */
/*  13-04-17  jstidder    PM83253        Prevent tx silent rollback from client inactivity timeout       */
/*  13-08-09  slaterpa    752004         TRANSUMMARY trace                                               */
/*  13-12-12  johawkes    105657         Handle unwritable recovery logs                                 */
/*  14-03-06  sgormley/dmatthew PI12571  _xid multithreading make volatile+allow getXID without creation */
/*  15-12-01  alisdair    197326         Tell @TransactionScoped to call bean destructors                */
/* ***************************************************************************************************** */

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.HeuristicCommitException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.ibm.tx.TranConstants;
import com.ibm.tx.config.ConfigurationProvider;
import com.ibm.tx.config.ConfigurationProviderManager;
import com.ibm.tx.jta.OnePhaseXAResource;
import com.ibm.tx.jta.TransactionManagerFactory;
import com.ibm.tx.jta.impl.TimeoutManager.TimeoutInfo;
import com.ibm.tx.util.TMHelper;
import com.ibm.tx.util.alarm.Alarm;
import com.ibm.tx.util.alarm.AlarmListener;
import com.ibm.tx.util.alarm.AlarmManager;
import com.ibm.tx.util.logging.FFDCFilter;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.Transaction.TransactionScopeDestroyer;
import com.ibm.ws.Transaction.UOWCoordinator;
import com.ibm.ws.Transaction.JTA.HeuristicHazardException;
import com.ibm.ws.Transaction.JTA.JTAResource;
import com.ibm.ws.Transaction.JTA.StatefulResource;
import com.ibm.ws.Transaction.JTA.Util;
import com.ibm.ws.Transaction.JTA.XAReturnCodeHelper;
import com.ibm.ws.Transaction.JTS.Configuration;
import com.ibm.ws.Transaction.JTS.ResourceCallback;
import com.ibm.ws.recoverylog.spi.LogClosedException;
import com.ibm.ws.recoverylog.spi.RecoverableUnit;
import com.ibm.ws.recoverylog.spi.RecoverableUnitSection;
import com.ibm.ws.recoverylog.spi.RecoveryLog;
import com.ibm.ws.uow.UOWScopeLTCAware;

/**
 * TransactionImpl implements javax.transaction.Transaction interface.
 * This class represents a single transaction and manages the states
 * of all XAResource objects involved in that transaction.
 */
public class TransactionImpl implements Transaction, ResourceCallback, UOWScopeLTCAware, UOWCoordinator {
    //
    // Recovery log names.
    //
    public static final String TRANSACTION_LOG_NAME = "tranlog";
    public static final String PARTNER_LOG_NAME = "partnerlog";

    //
    // Recovery log section ids
    //
    public static final int TRAN_STATE_SECTION = 0;
    public static final int CORBA_RESOURCE_SECTION = 1;
    public static final int XARESOURCE_SECTION = 2;
    public static final int GLOBALID_SECTION = 3;
    public static final int RECOVERYCOORD_SECTION = 4;
    public static final int RESOURCE_WSC_SECTION = 5;
    public static final int RECCOORD_WSC_SECTION = 6;
    public static final int RESOURCE_ADAPTER_SECTION = 7;
    public static final int HEURISTIC_OUTCOME_SECTION = 8;
    public static final int RECCOORD_WSAT_SECTION = 9;
    public static final int WSAT_ASYNC_RESOURCE_SECTION = 10;

    //
    // Partner Recovery log section ids
    //
    public static final int NEXT_ID_SECTION = 30; /* @MD18134A */
    public static final int LOW_WATERMARK_SECTION = 31; /* @MD18134A */
    public static final int SERVER_STATE_SECTION = 32;
    public static final int CLASSPATH_SECTION = 33;
    public static final int XARESOURCEDATA_SECTION = 34;
    public static final int WSCOORDINATOR_SECTION = 35;
    public static final int JCAPROVIDER_SECTION = 36;
    public static final int RESOURCE_PRIORITY_SECTION = 37; /* LI3968A */

    //
    // Common Service Data log section ids
    //
    public static final int APPLID_DATA_SECTION = 253;
    public static final int EPOCH_DATA_SECTION = 254;
    public static final int SERVER_DATA_SECTION = 255;

    //
    // This transactions current status.
    //
    protected TransactionState _status;

    //
    // This transactions timeout value.
    //
    protected int _timeout;

    // Flag to indicate whether this is root or subordinate coordinator
    protected boolean _subordinate;

    //
    // Flag to indicate whether the only way this Transaction
    // should be resolved is via rollback of its resources.
    //
    protected volatile boolean _rollbackOnly;

    //
    // PK19059 starts here
    //
    // Original exception stored by RegisteredSyncs, and retrieved
    // in stage3Commitprocessing
    private volatile Throwable _originalException;
    // PK19059 ends here

    // Flag to indicate when this transaction has timed out
    //
    protected volatile boolean _timedOut;

    // Flag to indicate when this transaction has been rolled back from subordinate
    //
    protected boolean _subRollback;

    //
    // The XAResources enlisted as part of this transaction
    // and the association table of XA->JTAResources
    //
    protected RegisteredResources _resources;

    //
    // Resolver thread when one or more resources failed to respond to
    // outcome delivery
    //
    protected RetryAlarm _retryAlarm;

    //
    // The Synchronizations enlisted with this transaction
    //
    protected RegisteredSyncs _syncs;

    //
    // The Resource Callbacks enlisted with this transaction (only supported ones are afterFinished(destroy))
    //
    protected ArrayList<ResourceCallback> _destroyCallbacks;

    // counter used to prolong transaction finish for asynch/distributed completion
    private int _finishCount;

    //
    // Recovery log RecoverableUnit for this transaction.
    //
    protected RecoverableUnit _logUnit;

    //
    // The tranlog for the transaction (may be different for a Synchrony Recovery Agent)
    //
    protected RecoveryLog _tranLog; // 169107

    protected boolean _systemExceptionOccured;
    protected Exception _heuristicOnPrepare;

    protected int _retryAttempts;

    public final static int defaultRetryTime = 60;

    private static final String DISABLE_2PC_DEFAULT_PROPERTY = "com.ibm.tx.jta.disable2PC";

    protected long _expirationTime;

    protected volatile Xid _xid;
    protected int _localTID;
    private String _tranName; // 171555.2

    // JCA provider's recovery data
    protected JCARecoveryData _JCARecoveryData;

    // Xid supplied by the inbound JCA provider
    protected Xid _JCAXid; // @249308A

    // if -Dcom.ibm.tx.jta.disable2PC=true disable 2PC
    private static boolean _disable2PCDefault = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        @Override
        public Boolean run() {
            return Boolean.getBoolean(DISABLE_2PC_DEFAULT_PROPERTY);
        }
    });

    /**
     * @param disable2PCDefault the disable2PCDefault to set
     */
    public static void setDisable2PCDefault(boolean disable2PCDefault) {
        TransactionImpl._disable2PCDefault = disable2PCDefault;
    }

    // Flag to indicate if this transaction has been started in such a way as to be able
    // to support 2PC transactions (see the initialize method for details)
    protected boolean _disableTwoPhase = _disable2PCDefault;

    protected boolean _needsManualCompletion;

    protected FailureScopeController _failureScopeController;

    // Whether we've been reconstructed from the tranlog
    protected boolean _inRecovery;

    // Whether we do not need to retry recovery cycle - always start at false for recovery and normal running in case we need
    // to call recoverRollback from stoppingProvider.
    protected boolean _doNotRetryRecovery;

    private Map<Object, Object> _synchronizationRegistryResources;

    public static final long GLOBAL_TRANSACTION_LOCAL_ID_MODIFIER = 0x6000000000000000L;

    private Thread _mostRecentThread;

    protected static boolean _isZos;

    public ConfigurationProvider _configProvider = ConfigurationProviderManager.getConfigurationProvider();

    static final boolean auditRecovery = ConfigurationProviderManager.getConfigurationProvider().getAuditRecovery();

    // The boundary type (either bean method or ActivitySession) of the
    // LTC that was completed as a result of this transaction being
    // begun. If this value is null it indicates that there was no
    // LTC on the thread when the transaction begin was performed.
    private Byte _completedLTCBoundary;

    protected String _taskId;

    private TimeoutInfo _timeoutInfo;

    int _txType = UOWCoordinator.TXTYPE_INTEROP_GLOBAL;

    // Used to call back into the CDI code to destroy anything created within
    // a Transaction Scope associated with this transaction.
    private static volatile TransactionScopeDestroyer tScopeDestroyer = null;

    private static TraceComponent tc = Tr.register(com.ibm.tx.jta.impl.TransactionImpl.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);
    private static final TraceComponent tcSummary = Tr.register("TRANSUMMARY", TranConstants.SUMMARY_TRACE_GROUP, null);

    /**
     * Recovery Constructor
     */
    public TransactionImpl(FailureScopeController fsc) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "TransactionImpl", fsc);

        _failureScopeController = fsc;

        traceCreate();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "TransactionImpl");
    }

    protected TransactionImpl() {

    }

    /**
     * Default Constructor for locally created transaction
     */
    public TransactionImpl(int timeout) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "TransactionImpl", timeout);

        _failureScopeController = Configuration.getFailureScopeController();

        final TxPrimaryKey pk = initializeTran(timeout);
        _xid = new XidImpl(pk);

        traceCreate();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "TransactionImpl");
    }

    /**
     * Constructor when TransactionImpl is created
     * on importing a transaction through ExecutionContextHandler (JCA1.5)
     *
     * @param timeout Transaction timeout in seconds
     * @param xid The imported XID for the transaction
     */
    public TransactionImpl(int timeout, Xid xid, JCARecoveryData jcard) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "TransactionImpl", new Object[] { timeout, xid, jcard });

        _failureScopeController = Configuration.getFailureScopeController();

        _subordinate = true; /* @LI3187M */

        final TxPrimaryKey pk = initializeTran(timeout);
        _xid = new XidImpl(xid, pk);

        _JCARecoveryData = jcard;
        _JCAXid = new XidImpl(xid); // @249308A

        traceCreate();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "TransactionImpl");
    }

    /**
     * Constructor for use in Remote Transaction import, under story 151330
     */
    public TransactionImpl(int txType, int timeout) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "TransactionImpl", new Object[] { txType, timeout });

        _txType = txType;

        traceCreate();
        _status = createState(this);
        if (tc.isEntryEnabled())
            Tr.exit(tc, "TransactionImpl");
    }

    //
    // Non-native (ie non-zos) - allocate our own localTID
    // and build a key for the Xid.  Also save the expirationTime
    // for propagation on distributed transactions.
    //
    protected TxPrimaryKey initializeTran(int timeout) {
        _localTID = LocalTIDTable.getLocalTID(this);
        final TxPrimaryKey pk = new TxPrimaryKey(_localTID, Configuration.getCurrentEpoch());

        initialize(timeout);
        if (_timeout > 0) {
            // This value is only relevant for non-zOS systems
            _expirationTime = pk.getTimeStamp() + (_timeout * 1000l);
        }
        return pk;
    }

    protected void initialize(int timeout) {
        if (tc.isEventEnabled())
            Tr.event(tc, "(SPI) Transaction BEGIN occurred for TX: " + _localTID);

        // Let the failure scope controller know about this transaction so that it can
        // examine all its associated transaction when its asked to shutdown (either at
        // server shutdown or peer recovery termination)
        if (_failureScopeController != null) {
            _failureScopeController.registerTransaction(this, false);

            //
            // Determine if this transaction was created prior to the recovery log becoming available.
            // If this is the case then this transaction will only ever be able to support 1PC
            // transactions and hense only allow a single resource to be enlisted. The reason for this
            // concerns the APPLID. The APPLID is used in the creation of XIDs in order that websphere
            // can distinguish (on xa_recover) between those it created and those other TMs created.
            // In the HA world, the recovery log is not necessararly immediatly avilable when the
            // server starts. As a result, we allow 1PC work without the logs and are forced to
            // to prevent 2PC work until they are made available by the HA framework. Before the
            // logs are available, XIDs created by the server use the new default (unique, generated
            // by the server run) APPLID. When the logs are processed, the APPLID is retrieved from
            // the log and used in future generation of XIDs. Since the log stores only a single
            // APPLID at present all recoverable work must use this XID as its the only one that
            // the transaction service will recognize during xa_recover processing. We can't allow
            // 2PC interaction with the resources under an XID with a temporary APPLID.
            //
            if (_failureScopeController.getRecoveryManager() == null || _failureScopeController.getRecoveryManager().recoveryFailed()) {
                _disableTwoPhase = true;
            }
        } else {
            _disableTwoPhase = true;
        }

        if (tc.isDebugEnabled() && _disableTwoPhase)
            Tr.debug(tc, "No recovery log is currently available. Transaction can only support 1PC protocol");

        //
        // Create our transaction state object
        // which automatically starts in ACTIVE
        // state.
        //
        _status = createState(this);

        // LIDB1673.7.2: check for maximum timeout
        final int maximumTransactionTimeout = _configProvider.getMaximumTransactionTimeout();
        if (maximumTransactionTimeout > 0) {
            if (timeout > maximumTransactionTimeout || timeout == 0) {
                if (tc.isEventEnabled())
                    Tr.event(tc, "Timeout limited by maximumTransactionTimeout");
                timeout = maximumTransactionTimeout;
            }
        }

        //
        // If we have a timeout we need to add ourselves to
        // the manager so the thread is started.
        //
        if (timeout > 0) {
            _timeout = timeout;
            TimeoutManager.setTimeout(this, TimeoutManager.ACTIVE_TIMEOUT, timeout);
        }
    }

    /**
     * Get the time in milliseconds since the epoch
     * at which this transaction will timeout.
     *
     * @return The time at which this transaction will timeout
     */
    public long getExpirationTime() {
        return _expirationTime;
    }

    /**
     * Gets the original timeout, in seconds. If the transaction is not
     * 'active', or has already timed out, this will not be accurate.
     */
    public int getTimeout() /* @LI3187A */
    {
        return _timeout;
    }

    /**
     * Directs the TransactionImpl to recover its status, participants and subordinate status
     * and possible superior (via Corba or private interop protocol) after a failure, based
     * on the associated RecoverableUnit object.
     */
    public boolean reconstruct(RecoverableUnit log, RecoveryLog tranLog) throws SystemException // 169107
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "reconstruct", new Object[] { log, tranLog });

        boolean result = true;

        // Were we imported from an RA?
        final RecoverableUnitSection raSection = log.lookupSection(TransactionImpl.RESOURCE_ADAPTER_SECTION);
        if (raSection != null) {
            // We have a section so we must have been imported from an RA
            // We are therefore a subordinate
            _subordinate = true;

            final byte[] logData = raSection.lastData(); // @249308A

            // We have encoded the id for the JCA recovery data from the partner log and
            // maybe the original JCA imported Xid
            if (logData.length > 12 + 8) // We must have formatId and lengths encoded  @249308A
            { // @249308A
                _JCAXid = createJCAXid(logData, 8);
            } // @249308A
            else // no logged Xid, so use our own interposed one                           @249308A
            { // @249308A
                _JCAXid = _xid; // @249308A
            } // @249308A
              // Set the JCA recovery data
            final long recoveryId = Util.getLongFromBytes(logData, 0); // @249308C
            final PartnerLogData pld = _failureScopeController.getRecoveryManager().getPartnerLogTable().findEntry(recoveryId);

            if (pld instanceof JCARecoveryData) {
                _JCARecoveryData = (JCARecoveryData) pld;
                _JCARecoveryData.incrementCount();
                addDestroyCallback(this); // to decrement the count when we're done
            } else {
                final SystemException e = new SystemException();
                FFDCFilter.processException(e, "com.ibm.tx.jta.TransactionImpl.reconstruct", "632", this);
                Tr.fatal(tc, "WTRN0000_ERR_INT_ERROR", new Object[] { "reconstruct", this.getClass().getName(), e });
                throw e;
            }
        }

        final RecoveryManager recoveryManager = _failureScopeController.getRecoveryManager();

        // Use the result of the TransactionState reconstruction to decide whether
        // to continue with recovery of this transaction.
        _status = createState(this);
        final int state = _status.reconstruct(log);

        if (state == TransactionState.STATE_NONE ||
            state == TransactionState.STATE_COMMITTED ||
            state == TransactionState.STATE_ROLLED_BACK) {
            //
            // If the transaction is completed, then
            // ensure that the log record is discarded.
            //
            result = false;
        } else {
            // Need to see if we are a subordinate or a root.  We may be a subordinate created
            // either by a standard Corba interop protocol, in which case the log will have a
            // reference to the superior recovery coordinator (IOR) logged, or we may
            // have been created by the WS JTA2 private interop protocol, in which case the log
            // will have a reference to the superior global WSCoordinator logged.  The latter is
            // logged as a pointer to a partner log entry and the partner log entry holds the IOR.
            reconstructCoordinators(log);

            // Now handle the participants
            getResources().reconstruct(recoveryManager, log);

            // If we are a subordinate and the state is heuristic - prolongFinish until we get a forget
            if (_subordinate && (state == TransactionState.STATE_HEURISTIC_ON_COMMIT ||
                                 state == TransactionState.STATE_HEURISTIC_ON_ROLLBACK)) {
                prolongFinish();
            }

            // Now we have successfully built the TI, add it to the LocalTID table
            // and register with the FSC.
            _localTID = LocalTIDTable.getLocalTID(this);

            // Let the failure scope controller know about this transaction so that it can
            // examine all its associated transaction when its asked to shutdown (either at
            // server shutdown or peer recovery termination)
            _failureScopeController.registerTransaction(this, true);

            _inRecovery = true;
        }

        // Set up instance variables now all is complete.
        _logUnit = log;
        _tranLog = tranLog;

        if (tc.isEntryEnabled())
            Tr.exit(tc, "reconstruct", new Object[] { result, _subordinate });
        return result;
    }

    protected TransactionState createState(TransactionImpl tx) {
        return new TransactionState(tx);
    }

    // Overridden in WAS
    protected Xid createJCAXid(byte[] logData, int offset) {
        return new XidImpl(logData, offset);
    }

    @SuppressWarnings("unused")
    protected void reconstructCoordinators(RecoverableUnit log) throws SystemException {
        // Not used in JTM
    }

    /**
     * Directs the TransactionImpl to perform recovery actions based on its
     * reconstructed state after a failure, or after an in-doubt timeout has
     * occurred.
     * This method is called by the RecoveryManager during recovery, in which case
     * there is no terminator object, or during normal operation if the transaction
     * commit retry interval has been exceeded for the transaction.
     * If this method is called more times than the retry limit specified in
     * COMMITRETRY, then the global outcome of the transaction is taken from the
     * value of HEURISTICDIRECTION.
     *
     * This method is synchronized together with the Associations methods as it
     * needs to support concurrency between inbound requests and in-doubt timer
     * activity. On recovery, there will be no associations for non-subordinates.
     *
     * @return
     */
    synchronized public void recover() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "recover", this);

        final int state = _status.getState();
        if (_subordinate) {
            // For a subordinate, first check whether the global outcome is known locally.
            switch (state) {
                // Due to the possibility of recovery being attempted asynchronously to
                // an incoming superior request, we must cover the case where the
                // transaction has now actually committed already.
                case TransactionState.STATE_HEURISTIC_ON_COMMIT:
                case TransactionState.STATE_COMMITTED:
                case TransactionState.STATE_COMMITTING:
                    recoverCommit(true);
                    break;
                // Due to the possibility of recovery being attempted asynchronously to
                // an incoming superior request, we must cover the case where the
                // transaction has now actually rolled back already.
                case TransactionState.STATE_HEURISTIC_ON_ROLLBACK:
                case TransactionState.STATE_ROLLED_BACK:
                case TransactionState.STATE_ROLLING_BACK:
                    recoverRollback(true);
                    break;
                // For a subordinate, the replay_completion method is invoked on the superior.
                // If the number of times the replay_completion has been retried is greater
                // than the value specified by COMMITRETRY, then HEURISTICDIRECTION is used
                // to determine the transaction outcome.
                default:
                    // If we were imported from a JCA provider, check whether it's still installed.
                    // If so, we need do nothing here since we expect the RA to complete the transaction.
                    // Otherwise, we will complete using the configured direction.
                    if (_JCARecoveryData != null) {
                        final String id = _JCARecoveryData.getWrapper().getProviderId();
                        if (TMHelper.isProviderInstalled(id)) {
                            if (tc.isDebugEnabled())
                                Tr.debug(tc, "recover", "Do nothing. Expect provider " + id + " will complete.");
                            // Do nothing. RA is responsible for completing.
                        } else {
                            switch (_configProvider.getHeuristicCompletionDirection()) {
                                case ConfigurationProvider.HEURISTIC_COMPLETION_DIRECTION_COMMIT:
                                    Tr.error(tc, "WTRN0098_COMMIT_RA_UNINSTALLED", new Object[] { getTranName(), id });
                                    recoverCommit(false);
                                    break;
                                case ConfigurationProvider.HEURISTIC_COMPLETION_DIRECTION_MANUAL:
                                    // do nothing, administrative completion is required
                                    _needsManualCompletion = true;
                                    Tr.info(tc, "WTRN0101_MANUAL_RA_UNINSTALLED", new Object[] { getTranName(), id });
                                    break;
                                default:
                                    Tr.error(tc, "WTRN0099_ROLLBACK_RA_UNINSTALLED", new Object[] { getTranName(), id });
                                    recoverRollback(false);
                            }
                        }
                    } else {
                        retryCompletion();
                    }
                    break;
            }
        } else {
            // For a top-level Transaction, we will only recover in the case
            // where we have successfully prepared.  If the state is not committing,
            // then assume it is rollback.
            if (state == TransactionState.STATE_LAST_PARTICIPANT) {
                // LIDB1673-13 lps heuristic completion.
                // The transaction was attempting to complete its
                // 1PC resource when the server went down.
                // Use the lpsHeuristicCompletion flag to determine
                // how to complete the tx.
                switch (ConfigurationProviderManager.getConfigurationProvider().getHeuristicCompletionDirection()) {
                    case ConfigurationProvider.HEURISTIC_COMPLETION_DIRECTION_COMMIT:
                        Tr.error(tc, "WTRN0096_HEURISTIC_MAY_HAVE_OCCURED", getTranName());
                        recoverCommit(false);
                        break;
                    case ConfigurationProvider.HEURISTIC_COMPLETION_DIRECTION_MANUAL:
                        // do nothing!?
                        _needsManualCompletion = true;
                        Tr.info(tc, "WTRN0097_HEURISTIC_MANUAL_COMPLETION", getTranName());
                        break;
                    default:
                        Tr.error(tc, "WTRN0102_HEURISTIC_MAY_HAVE_OCCURED", getTranName());
                        recoverRollback(false);
                }
            } else if (state == TransactionState.STATE_COMMITTING)
                recoverCommit(false);
            else
                recoverRollback(false);
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "recover");
    }

    protected void retryCompletion() {
        // Not used in JTM
    }

    /**
     * Return a boolean indicating whether or not this
     * transaction is a subordinate. Note: an only subordinate
     * to a superior (ie one which receives commit_one_phase from
     * its superior during completion processing) is also considered
     * to be a superior.
     *
     * @return True if this transaction is a subordinate,
     *         false if it is a superior.
     */
    public boolean isSubordinate() {
        return _subordinate;
    }

    //----------------------------------------------------
    // JTA javax.transaction.Transaction interface.
    //----------------------------------------------------

    /**
     * Commit the transation associated with the target Transaction object
     *
     * This call should only be made from the root.
     *
     * @exception RollbackException Thrown to indicate that
     *                the transaction has been rolled back rather than committed.
     *
     * @exception HeuristicMixedException Thrown to indicate that a heuristic
     *                mix decision was made.
     *
     * @exception HeuristicRollbackException Thrown to indicate that a
     *                heuristic rollback decision was made.
     *
     * @exception SecurityException Thrown to indicate that the thread is
     *                not allowed to commit the transaction.
     *
     * @exception IllegalStateException Thrown if the transaction in the
     *                target object is inactive.
     *
     * @exception SystemException Thrown if the transaction manager
     *                encounters an unexpected error condition
     */
    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "commit (SPI)");

        try {
            processCommit();
        } catch (HeuristicHazardException hhe) {
            // Only throw heuristic hazard in the one phase case
            // modify the heuristic - note JTA 1.0.1 errata implies this may be reconsidered one day
            final HeuristicMixedException hme = new HeuristicMixedException(hhe.getLocalizedMessage());
            hme.initCause(hhe.getCause()); //Set the cause to be the cause of the HeuristicHazardException
            if (tc.isEntryEnabled())
                Tr.exit(tc, "commit", hme);
            throw hme;
        } finally {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "commit (SPI)");
        }
    }

    /**
     * Commit the transation associated with a subordinate which received
     * a commit_one_phase request from the superior. Any other
     * subordinate requests should call the internal methods directly.
     *
     * @exception RollbackException Thrown to indicate that
     *                the transaction has been rolled back rather than committed.
     *
     * @exception HeuristicMixedException Thrown to indicate that a heuristic
     *                mix decision was made.
     *
     * @exception HeuristicHazardException Thrown to indicate that a heuristic
     *                hazard decision was made.
     *
     * @exception HeuristicRollbackException Thrown to indicate that a
     *                heuristic rollback decision was made.
     *
     * @exception SecurityException Thrown to indicate that the thread is
     *                not allowed to commit the transaction.
     *
     * @exception IllegalStateException Thrown if the transaction in the
     *                target object is inactive.
     *
     * @exception SystemException Thrown if the transaction manager
     *                encounters an unexpected error condition
     */
    public void commit_one_phase() throws RollbackException, HeuristicMixedException, HeuristicHazardException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "commit_one_phase");

        // This call is only valid for a single subordinate - treat as a "superior"
        _subordinate = false;

        try {
            processCommit();
        } finally {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "commit_one_phase");
        }
    }

    private void processCommit() throws RollbackException, HeuristicMixedException, HeuristicHazardException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        int state = stage1CommitProcessing();

        try {
            stage2CommitProcessing(state);
        } finally {
            // Get state incase its updated by notifyCompletion
            state = _status.getState();
            notifyCompletion();
        }

        stage3CommitProcessing(state);
    }

    /**
     * @return
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException
     * @throws SystemException
     * @throws HeuristicHazardException
     */
    protected int stage1CommitProcessing() throws HeuristicMixedException, HeuristicRollbackException, SystemException, HeuristicHazardException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "stage1CommitProcessing");

        int state;
        if (prePrepare()) // cancel timers, distribute beforecompletion
        {
            try {
                if (!hasResources()) {
                    state = TransactionState.STATE_COMMITTED;
                    _status.setState(state);
                    postCompletion(Status.STATUS_COMMITTED);
                } else if (_resources.isOnlyAgent()) {
                    state = commitXAResources();
                } else {
                    state = prepareResources();
                }
            } catch (HeuristicMixedException hme) {
                notifyCompletion();

                if (tc.isEntryEnabled())
                    Tr.exit(tc, "stage1CommitProcessing", hme);
                throw hme;
            } catch (HeuristicHazardException hhe) {
                notifyCompletion();

                if (tc.isEntryEnabled())
                    Tr.exit(tc, "stage1CommitProcessing", hhe);
                throw hhe;
            } catch (HeuristicRollbackException hre) {
                notifyCompletion();

                if (tc.isEntryEnabled())
                    Tr.exit(tc, "stage1CommitProcessing", hre);
                throw hre;
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.stage1CommitProcessing", "1545", this);
                notifyCompletion();
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "stage1CommitProcessing", se);
                throw se;
            } catch (Throwable t) {
                FFDCFilter.processException(t, "com.ibm.tx.jta.TransactionImpl.stage1CommitProcessing", "1551", this);
                notifyCompletion();
                final SystemException se = new SystemException(t.getLocalizedMessage());
                se.initCause(t);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "stage1CommitProcessing", new Object[] { t, se });
                throw se;
            }
        } else {
            _status.setState(TransactionState.STATE_ROLLING_BACK);
            state = _status.getState();
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "stage1CommitProcessing", TransactionState.stateToString(state));
        return state;
    }

    protected void coreStage2CommitProcessing(int state) throws HeuristicHazardException, SystemException, HeuristicMixedException, HeuristicRollbackException, HeuristicCommitException {
        switch (state) {
            case TransactionState.STATE_COMMITTING:
                //
                // Prepare vote was COMMIT so we continue as planned.
                //
                internalCommit();
                break;

            case TransactionState.STATE_ROLLING_BACK:
                //
                // Prepare vote was ROLLBACK so we rollback
                // any non-read-only resources. RegisteredResources
                // takes into account which resources are read only
                // and which threw the rollback vote so we simply
                // delegate down.
                //
                internalRollback();
                break;

            case TransactionState.STATE_LAST_PARTICIPANT:
                //LIDB1673-13 lps heuristic completion
                needsLPSHeuristicCompletion();
                break;
        }
    }

    /**
     * @param state
     * @throws HeuristicMixedException
     * @throws HeuristicHazardException
     * @throws HeuristicRollbackException
     * @throws SystemException
     */
    protected void stage2CommitProcessing(int state) throws HeuristicMixedException, HeuristicHazardException, HeuristicRollbackException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "stage2CommitProcessing", TransactionState.stateToString(state));

        //
        // If vote was read-only then we don't need to do anything further.
        // Handle states of committing and rollingback.
        //
        try {
            coreStage2CommitProcessing(state);
        } catch (HeuristicMixedException hme) {
            // Add to list of heuristically completed transactions
            addHeuristic();

            if (tc.isEntryEnabled())
                Tr.exit(tc, "stage2CommitProcessing", hme);
            throw hme;
        } catch (HeuristicHazardException hhe) {
            // Add to list of heuristically completed transactions
            addHeuristic();

            if (tc.isEntryEnabled())
                Tr.exit(tc, "stage2CommitProcessing", hhe);
            throw hhe;
        } catch (HeuristicCommitException hce) {
            // Add to list of heuristically completed transactions
            addHeuristic();

            // Has to be mixed - we can only get this if we prepared and voted rollback
            // and then got heuristic commit on a rollback.
            final HeuristicMixedException hme = new HeuristicMixedException();
            if (tc.isEntryEnabled())
                Tr.exit(tc, "stage2CommitProcessing", hme);
            throw hme;
        } catch (HeuristicRollbackException hre) {
            // Add to list of heuristically completed transactions
            addHeuristic();

            if (tc.isEntryEnabled())
                Tr.exit(tc, "stage2CommitProcessing", hre);
            throw hre;
        } catch (SystemException se) {
            FFDCFilter.processException(se, "com.ibm.tx.jta.impl.TransactionImpl.stage2CommitProcessing", "1170", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "stage2CommitProcessing", se);
            throw se;
        } catch (Throwable t) {
            FFDCFilter.processException(t, "com.ibm.tx.jta.impl.TransactionImpl.stage2CommitProcessing", "1176", this);
            final SystemException se = new SystemException(t.getLocalizedMessage());
            if (tc.isEntryEnabled())
                Tr.exit(tc, "stage2CommitProcessing", new Object[] { t, se });
            throw se;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "stage2CommitProcessing");
    }

    protected void addHeuristic() {
        // Not used in JTM
    }

    /*
     * @throws SystemException
     *
     * @throws RollbackException, HeuristicMixedException, HeuristicHazardException
     */
    private void stage3CommitProcessing(int state) throws SystemException, RollbackException, HeuristicMixedException, HeuristicHazardException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "stage3CommitProcessing", TransactionState.stateToString(state));

        switch (state) {
            case TransactionState.STATE_ROLLING_BACK: // could be retries
            case TransactionState.STATE_ROLLED_BACK:
                //
                // As we rolled back we need to throw a RollbackException
                // unless the rollback was triggered by a SystemException or heuristic
                // in which case we should throw a SystemException or heuristic
                //
                if (_systemExceptionOccured) {
                    final SystemException se = new SystemException();
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "stage3CommitProcessing", se);
                    throw se;
                } else if (_heuristicOnPrepare != null) {
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "stage3CommitProcessing", _heuristicOnPrepare);
                    if (_heuristicOnPrepare instanceof HeuristicMixedException)
                        throw (HeuristicMixedException) _heuristicOnPrepare;

                    throw (HeuristicHazardException) _heuristicOnPrepare;
                }

                // PK19059 starts here
                final RollbackException rbe;
                if (getOriginalException() == null) {
                    rbe = new RollbackException();
                } else {
                    rbe = new RollbackException();
                    rbe.initCause(getOriginalException());
                }
                // PK19059 ends here
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "stage3CommitProcessing", rbe);
                throw rbe;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "stage3CommitProcessing");
    }

    /**
     * @throws SystemException
     * @throws HeuristicMixedException
     * @throws HeuristicHazardException
     * @throws HeuristicRollbackException
     * @throws HeuristicCommitException
     */
    private void needsLPSHeuristicCompletion() throws SystemException, HeuristicMixedException, HeuristicHazardException, HeuristicRollbackException, HeuristicCommitException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "needsLPSHeuristicCompletion");

        try {
            switch (_configProvider.getHeuristicCompletionDirection()) {
                case ConfigurationProvider.HEURISTIC_COMPLETION_DIRECTION_COMMIT:
                    Tr.error(tc, "WTRN0096_HEURISTIC_MAY_HAVE_OCCURED", getTranName());
                    _status.setState(TransactionState.STATE_COMMITTING);
                    internalCommit();
                    break;
                case ConfigurationProvider.HEURISTIC_COMPLETION_DIRECTION_MANUAL:
                    _needsManualCompletion = true;
                    Tr.info(tc, "WTRN0097_HEURISTIC_MANUAL_COMPLETION", getTranName());
                    distributeAfter(Status.STATUS_COMMITTED);
                    prolongFinish();
                    break;
                default:
                    Tr.error(tc, "WTRN0102_HEURISTIC_MAY_HAVE_OCCURED", getTranName());
                    _status.setState(TransactionState.STATE_ROLLING_BACK);
                    internalRollback();
                    break;
            }

            throw new HeuristicHazardException();
        } finally {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "needsLPSHeuristicCompletion");
        }
    }

    /**
     * Rollback the transaction associated with the target Transaction Object
     *
     * @exception IllegalStateException
     * @exception SystemException
     */
    @Override
    public void rollback() throws IllegalStateException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "rollback (SPI)");

        final int state = _status.getState();

        //
        // We are only called in this method for superiors.
        //
        if (state == TransactionState.STATE_ACTIVE) {
            //
            // Cancel timeout prior to completion phase
            //
            cancelAlarms();

            try {
                _status.setState(TransactionState.STATE_ROLLING_BACK);
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.rollback", "1587", this);
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Exception caught setting state to ROLLING_BACK!", se);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "rollback (SPI)");
                throw se;
            }

            try {
                internalRollback();
            } catch (HeuristicMixedException hme) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "HeuristicMixedException caught rollback processing", hme);
                // state change handled by notifyCompletion

                // Add to list of heuristically completed transactions
                addHeuristic();
            } catch (HeuristicHazardException hhe) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "HeuristicHazardException caught rollback processing", hhe);
                // state change handled by notifyCompletion

                // Add to list of heuristically completed transactions
                addHeuristic();
            } catch (HeuristicCommitException hce) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "HeuristicHazardException caught rollback processing", hce);
                // state change handled by notifyCompletion

                // Add to list of heuristically completed transactions
                addHeuristic();
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.rollback", "1626", this);
                if (tc.isEventEnabled())
                    Tr.event(tc, "SystemException caught during rollback", se);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "rollback (SPI)");
                throw se;
            } catch (Throwable ex) {
                FFDCFilter.processException(ex, "com.ibm.tx.jta.TransactionImpl.rollback", "1633", this);
                if (tc.isEventEnabled())
                    Tr.event(tc, "Exception caught during rollback", ex);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "rollback (SPI)");
                throw new SystemException(ex.getLocalizedMessage());
            } finally {
                notifyCompletion();
            }
        }
        //
        // Defect 1440
        //
        // We are not in ACTIVE state so we need to
        // throw the appropriate exception.
        //
        else if (state == TransactionState.STATE_NONE) {
            if (tc.isEventEnabled())
                Tr.event(tc, "No transaction available!");
            if (tc.isEntryEnabled())
                Tr.exit(tc, "rollback (SPI)");
            throw new IllegalStateException();
        } else {
            if (tc.isEventEnabled())
                Tr.event(tc, "Invalid transaction state:" + state);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "rollback (SPI)");
            throw new SystemException();
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "rollback (SPI)");
    }

    public int internalPrepare() throws HeuristicMixedException, HeuristicHazardException, HeuristicRollbackException, IllegalStateException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "internalPrepare");
        final int result;
        final int state = _status.getState();

        if (state == TransactionState.STATE_ACTIVE) {
            // Call beforeCompletion
            prePrepare();
            //  and then prepare - note prepareResources checks RBO state
            result = prepareResources();
        }
        //
        // Defect 1440
        //
        // We are not in ACTIVE state so we need to
        // throw the appropriate exception.
        //
        else if (state == TransactionState.STATE_NONE) {
            if (tc.isEventEnabled())
                Tr.event(tc, "No transaction available!");
            if (tc.isEntryEnabled())
                Tr.exit(tc, "internalPrepare");
            throw new IllegalStateException();
        } else {
            if (tc.isEventEnabled())
                Tr.event(tc, "Invalid transaction state: ", TransactionState.stateToString(state));
            if (tc.isEntryEnabled())
                Tr.exit(tc, "internalPrepare");
            throw new SystemException();
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "internalPrepare", result);
        return result;
    }

    protected int prepareResources() throws HeuristicMixedException, HeuristicHazardException, HeuristicRollbackException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "prepareResources");

        if (distributeEnd()) {
            try {
                _status.setState(TransactionState.STATE_PREPARING);
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.prepareResources", "446", this);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "prepareResources", se);
                throw se;
            }

            perfPreparing();

            try {
                // Optimization to allow 1PC (non-LA) and no logging
                int vote;
                boolean xaOKVote; // d250698
                try {
                    vote = getResources().distributePrepare(_subordinate, canOptimise());
                    xaOKVote = (vote == RegisteredResources.XA_OK); // d250698
                } catch (RollbackException rbe) {
                    FFDCFilter.processException(rbe, "com.ibm.tx.jta.TransactionImpl.prepareResources", "467", this);
                    setPrepareXAFailed(); // d266464
                    throw rbe;
                } catch (SystemException se) //6@D246070A
                {
                    FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.prepareResources", "473", this);
                    setPrepareXAFailed(); // d266464
                    throw se;
                }

                final boolean isXALastAgent = getResources().isLastAgentEnlisted();

                if (_isZos) {
                    vote = prepareZOSResources(isXALastAgent, vote);
                }

                if (isXALastAgent) {
                    vote = getResources().commitLastAgent(vote == RegisteredResources.XA_OK, xaOKVote); // d250698
                }

                //
                // Check how our Resources voted and set our state accordingly
                //
                try {
                    switch (vote) {
                        case RegisteredResources.XA_OK:
                            if (_subordinate)
                                _status.setState(TransactionState.STATE_PREPARED);
                            else
                                _status.setState(TransactionState.STATE_COMMITTING);
                            break;
                        case RegisteredResources.ONE_PHASE_OPT:
                            // Inform PMI of 1PC optimization
                            perfOnePhase();
                            // Follow through as if XA_RDONLY result
                        case RegisteredResources.XA_RDONLY:
                            _status.setState(TransactionState.STATE_COMMITTED);
                            postCompletion(Status.STATUS_COMMITTED);
                            break;
                        case RegisteredResources.ONE_PHASE_OPT_ROLLBACK:
                            _status.setState(TransactionState.STATE_ROLLED_BACK);

                            // Inform PMI of 1PC optimization and completion
                            perfOnePhase();

                            postCompletion(Status.STATUS_ROLLEDBACK);

                            break;
                        case RegisteredResources.ONE_PHASE_OPT_FAILED:
                            // State should be STATE_LAST_PARTICIPANT
                            // may need to check LPSLoggingEnabled flag
                            if (!_configProvider.isLoggingForHeuristicReportingEnabled())
                                _status.setState(TransactionState.STATE_LAST_PARTICIPANT);

                            // commit will take appropriate action after
                            // examining lpsHeuristicCompletion flag
                            break;
                    }
                } catch (SystemException se) {
                    FFDCFilter.processException(se, "com.ibm.tx.jta.impl.TransactionImpl.prepareResources", "1496", this);
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "Exception caught setting state after PREPARE", se);
                    throw se;
                }
            } catch (RollbackException rbe) {
                // A Resource has voted for Rollback so
                // set our state accordingly.
                FFDCFilter.processException(rbe, "com.ibm.tx.jta.impl.TransactionImpl.prepareResources", "1505", this);
                try {
                    _status.setState(TransactionState.STATE_ROLLING_BACK);
                } catch (SystemException se) {
                    FFDCFilter.processException(se, "com.ibm.tx.jta.impl.TransactionImpl.prepareResources", "1512", this);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "prepareResources", se);
                    throw se;
                }
            } catch (HeuristicMixedException he) {
                // A heuristic exception from distribute prepare (or commit if only one resource)
                // there may be prepared resources that must be rolled back

                try {
                    logHeuristic(true);
                    if (_status.getState() == TransactionState.STATE_PREPARING) /* @D241020A */
                    {
                        _status.setState(TransactionState.STATE_ROLLING_BACK);
                    }
                } catch (SystemException se) {
                    FFDCFilter.processException(se, "com.ibm.tx.jta.impl.TransactionImpl.prepareResources", "1533", this);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "prepareResources, se");
                    throw se;
                }
                if (!_subordinate) {
                    // in root, save exception to be thrown after we rollback
                    _heuristicOnPrepare = he;
                } else {
                    // re-throw expecting wrapper code to roll us back
                    throw he;
                }

            } catch (HeuristicHazardException he) {
                // A heuristic exception from distribute prepare (or commit if only one resource)
                // there may be prepared resources that must be rolled back

                try {
                    logHeuristic(true);
                    if (_status.getState() == TransactionState.STATE_PREPARING) /* @D241020A */
                    {
                        _status.setState(TransactionState.STATE_ROLLING_BACK);
                    }
                } catch (SystemException se) {
                    FFDCFilter.processException(se, "com.ibm.tx.jta.impl.TransactionImpl.prepareResources", "1564", this);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "prepareResources", se);
                    throw se;
                }
                if (!_subordinate) {
                    // in root, save exception to be thrown after we rollback
                    _heuristicOnPrepare = he;
                } else {
                    // re-throw expecting wrapper code to roll us back
                    throw he;
                }
            } catch (HeuristicRollbackException hre) {
                // A heuristic exception from distribute prepare indicates that we tried
                // and failed to perform a 1PC optimization.

                postCompletion(Status.STATUS_COMMITTED);

                // If we're a subordinate log the fact that we received a heuristic from a commit attempt.
                if (_subordinate) {
                    // We should not be doing a C1P on a subordinate during prepare
                    Tr.error(tc, "WTRN0057_HEURISTIC_ON_SUBORDINATE");
                    throw new SystemException();
                }

                throw hre;
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.impl.TransactionImpl.prepareResources", "1598", this);
                _systemExceptionOccured = true;

                try {
                    if (_status.getState() == TransactionState.STATE_PREPARING) /* @D241020A */
                    {
                        _status.setState(TransactionState.STATE_ROLLING_BACK);
                    }
                } catch (SystemException se2) {
                    FFDCFilter.processException(se2, "com.ibm.tx.jta.impl.TransactionImpl.prepareResources", "1611", this);
                    if (tc.isEntryEnabled())
                        Tr.exit(tc, "prepareResources", se2);
                    throw se2;
                }
            } // end catch
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "prepareResources");
        return _status.getState();
    }

    protected void perfOnePhase() {
        // Not used in JTM
    }

    @SuppressWarnings("unused")
    protected int prepareZOSResources(boolean isXALastAgent, int vote) throws RollbackException, HeuristicMixedException, SystemException {
        return vote;
    }

    protected boolean canOptimise() {
        return !_subordinate;
    }

    protected void perfPreparing() {
        // Not used in JTM
    }

    /**
     * Indicate that the prepare XA phase failed.
     */
    protected void setPrepareXAFailed() // d266464A
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setPrepareXAFailed");

        setRBO(); // Ensure native context is informed

        if (tc.isEntryEnabled())
            Tr.exit(tc, "setPrepareXAFailed");
    }

    /**
     * Drive beforeCompletion against sync objects registered with
     * this transaction.
     */
    protected boolean prePrepare() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "prePrepare");

        //
        // Cancel timeout prior to completion phase
        //
        cancelAlarms();

        //
        // Inform the Synchronisations we are about to complete
        //
        if (!_rollbackOnly) {
            if (_syncs != null) {
                _syncs.distributeBefore();
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "prePrepare", !_rollbackOnly);
        return !_rollbackOnly;
    }

    /**
     * Stop all active timers associated with this transaction.
     */
    protected void cancelAlarms() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "cancelAlarms");

        if (_timeout > 0) {
            TimeoutManager.setTimeout(this, TimeoutManager.CANCEL_TIMEOUT, 0);
            _timeout = 0;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "cancelAlarms");
    }

    /**
     * Distribute end to all XA resources
     */
    protected boolean distributeEnd() throws SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "distributeEnd");

        if (!getResources().distributeEnd(XAResource.TMSUCCESS)) {
            setRBO();
        }

        if (_rollbackOnly) {
            try {
                _status.setState(TransactionState.STATE_ROLLING_BACK);
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.distributeEnd", "1731", this);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "distributeEnd", se);
                throw se;
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "distributeEnd", !_rollbackOnly);
        return !_rollbackOnly;
    }

    protected int commitXAResources() throws HeuristicMixedException, HeuristicHazardException, HeuristicRollbackException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "commitXAResources");

        if (distributeEnd()) {
            // We're performing a 1PC optimization so we should jump
            // straight to committing state as there's no prepare
            // phase. The table of valid state transistions doesn't
            // let us move directly from active to committing so,
            // we have to go via preparing.
            _status.setState(TransactionState.STATE_COMMITTING_ONE_PHASE);

            // Inform PMI that we are committing. I (Andy) think that
            // the perf metrics code requires its methods to be driven
            // in sequence so we call preparing and then immediately
            // call committing. Later on we call onePhase to indicate
            // the we've used a 1PC optimization to complete the tran.
            // implementation
            perfPreparing();
            perfCommitting();

            int status = Status.STATUS_COMMITTED;

            try {
                getResources().flowCommitOnePhase(false);

                // Inform PMI that we've used 1PC
                perfOnePhase();

                _status.setState(TransactionState.STATE_COMMITTED);
                // if heuristic is thrown, notifyCompletion will set state
            } catch (HeuristicMixedException hme) {
                // FFDC in RegisteredResources
                processC1PHeuristic();
                status = Status.STATUS_UNKNOWN; // @281425A
                throw hme;
            } catch (HeuristicHazardException hhe) {
                // FFDC in RegisteredResources
                processC1PHeuristic();
                status = Status.STATUS_UNKNOWN; // @281425A
                throw hhe;
            } catch (HeuristicRollbackException hre) {
                // FFDC in RegisteredResources
                processC1PHeuristic();
                status = Status.STATUS_UNKNOWN; // @281425A
                throw hre;
            } catch (RollbackException rbe) {
                // No FFDC Code needed.

                // A Resource has voted for Rollback so
                // set our state accordingly.
                status = Status.STATUS_ROLLEDBACK;
                _status.setState(TransactionState.STATE_ROLLED_BACK);
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.impl.TransactionImpl.commitXAResources", "1816", this);
                _status.setState(TransactionState.STATE_ROLLED_BACK);
                throw se;
            } // end catch
            finally {
                postCompletion(status);
                // If sys excep occurred on a forget, then throw that
                // to match old code...
                if (_systemExceptionOccured) {
                    throw new SystemException();
                }
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "commitXAResources");
        return _status.getState();
    }

    protected void processC1PHeuristic() {
        // Not used in JTM
    }

    public void internalCommit() throws HeuristicMixedException, HeuristicHazardException, HeuristicRollbackException, SystemException {
        internalCommit(false, false); /* @377762.2A4 */
    }

    public void internalCommit(boolean cascaded, boolean boolafter) throws HeuristicMixedException, HeuristicHazardException, HeuristicRollbackException, SystemException /*
                                                                                                                                                                           * 377762.
                                                                                                                                                                           * 2A
                                                                                                                                                                           */
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "internalCommit");

        if (cascaded && boolafter) /* @377762.2A5 */
        {
            postCompletion(Status.STATUS_COMMITTED, cascaded, boolafter);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "internalCommit");
            return;
        }
        // Inform PMI of commit
        perfCommitting();

        HeuristicMixedException savedHme = null;
        try {
            if (_isZos) {
                savedHme = internalZOSCommit();
            }

            getResources().distributeCommit();
        } catch (HeuristicMixedException hme) {
            // FFDC in RegisteredResources
            logHeuristic(true);
            throw hme;
        } catch (HeuristicHazardException hhe) {
            // FFDC in RegisteredResources
            logHeuristic(true);
            throw hhe;
        } catch (HeuristicRollbackException hre) {
            // FFDC in RegisteredResources
            logHeuristic(true);
            throw hre;
        } finally {
            postCompletion(Status.STATUS_COMMITTED, cascaded, boolafter); /* @377762.2C */
            if (savedHme != null) // override any exception from distributeCommit
            {
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "internalCommit", savedHme);
                throw savedHme;
            }

            if (tc.isEntryEnabled())
                Tr.exit(tc, "internalCommit");
        }
    }

    protected HeuristicMixedException internalZOSCommit() {
        // Not used in JTM
        return null;
    }

    protected void perfCommitting() {
        // Not used in JTM
    }

    public void internalRollback() throws HeuristicMixedException, HeuristicHazardException, HeuristicCommitException, SystemException /* @377762.2A4 */
    {
        internalRollback(false, false);
    }

    public void internalRollback(boolean cascaded, boolean cascafter) throws HeuristicMixedException, HeuristicHazardException, HeuristicCommitException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "internalRollback");

        if (cascaded && cascafter) /* @377762.2A5 */
        {
            postCompletion(Status.STATUS_ROLLEDBACK, cascaded, cascafter);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "internalRollback");
            return;
        }
        SystemException ex = null;
        try {
            HeuristicMixedException savedHme = null; /* @225356A */

            if (_isZos) {
                try {
                    internalZOSRollback();
                } catch (HeuristicMixedException hme) {
                    savedHme = hme;
                } catch (SystemException se) {
                    ex = se;
                }
            }

            // Fully distribute ends
            // Don't care if we had some failures so ignore return value
            getResources().distributeEnd(XAResource.TMSUCCESS);

            getResources().distributeRollback();

            if (savedHme != null)
                throw savedHme; /* @225356A */
        } catch (HeuristicMixedException hme) {
            // FFDC in RegisteredResources
            logHeuristic(false);
            throw hme;
        } catch (HeuristicHazardException hhe) {
            // FFDC in RegisteredResources
            logHeuristic(false);
            throw hhe;
        } catch (HeuristicCommitException hce) {
            // FFDC in RegisteredResources
            logHeuristic(false);
            throw hce;
        } finally {
            postCompletion(Status.STATUS_ROLLEDBACK, cascaded, cascafter); /* @377762.2C */
            if (ex != null) // override any exception from distributeRollback
            {
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "internalRollback", ex);
                throw ex;
            }

            if (tc.isEntryEnabled())
                Tr.exit(tc, "internalRollback");
        }
    }

    @SuppressWarnings("unused")
    protected void internalZOSRollback() throws HeuristicMixedException, SystemException {
        // Not used in JTM
    }

    //
    // To be called on recovery only.
    //
    // For superior we need to forget the transaction after a heuristic, but not for a subordinate as the
    // superior should poll us and pick up the result.  We then forget when requested by the superior.
    // The parameter is used to control when we wait for the superior after
    // a heuristic.  eg in cases where we give up retries we won't wait.
    //
    protected void recoverCommit(boolean waitForHeuristic) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "recoverCommit", new Object[] { waitForHeuristic, _doNotRetryRecovery });

        if (!_doNotRetryRecovery) {
            auditTransaction();
            final int state = _status.getState();
            try {
                if (state != TransactionState.STATE_COMMITTING &&
                    state != TransactionState.STATE_HEURISTIC_ON_COMMIT &&
                    state != TransactionState.STATE_COMMITTED)
                    _status.setState(TransactionState.STATE_COMMITTING);

                getResources().distributeCommit();
            } catch (HeuristicMixedException hme) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Exception caught during recoverCommit processing", hme);
                logHeuristicInRecovery(true);
                addHeuristic(); // PM39333
                // state change handled by notifyCompletion
            } catch (HeuristicHazardException hhe) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Exception caught during recoverCommit processing", hhe);
                logHeuristicInRecovery(true);
                addHeuristic(); // PM39333
                // state change handled by notifyCompletion
            } catch (HeuristicRollbackException hre) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Exception caught during recoverCommit processing", hre);
                logHeuristicInRecovery(true);
                addHeuristic(); // PM39333
                // state change handled by notifyCompletion
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.recoverCommit", "1096", this);
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Exception caught during recoverCommit processing", se);
                Tr.error(tc, "WTRN0058_RECOVERY_EXCEPTION_IN_COMMIT", new java.lang.Object[] { getTranName(), se });
            } finally {
                // If we get into heuristic state on a subordinate, we need to wait for a forget
                // Note: if we were already in heuristic state on reconstruct we prolong there
                // in case a forget arrives before we get into recovery.
                if (waitForHeuristic && state != TransactionState.STATE_HEURISTIC_ON_COMMIT &&
                    TransactionState.STATE_HEURISTIC_ON_COMMIT == _status.getState()) {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "Transaction state now heuristic");
                    prolongFinish();
                }

                _doNotRetryRecovery = true; // Reset - postCompletion may set it again...

                // Issue any forgets to local resources if need be (this may set retry)
                // and determine if we need to retry recovery again
                postCompletion(Status.STATUS_COMMITTED);
                notifyCompletion();
            }
        }

        handleHeuristicOnCommit(waitForHeuristic);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "recoverCommit");
    }

    protected void handleHeuristicOnCommit(boolean waitForHeuristic) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "handleHeuristicOnCommit", waitForHeuristic);

        // If no resources to complete - check if we are a subordinate
        // with outstanding heuristic indication.  We may have missed a forget from
        // a superior as we do not log, it so on recovery may still be waiting for it.
        // Really when we get a forget we should remove our subordinate and heuristic
        // status from the log.  Check of superior has gone.
        if (_doNotRetryRecovery && waitForHeuristic &&
            TransactionState.STATE_HEURISTIC_ON_COMMIT == _status.getState()) {
            if (tc.isEventEnabled())
                Tr.event(tc, "recoverCommit", "Checking if we can forget transaction");

            if (_JCARecoveryData == null) {
                // JCA must always send a forget...
                notifyCompletion();
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "handleHeuristicOnCommit");
    }

    //
    // To be called on recovery or from stoppingProvider (waitForHeuristic = false) only.
    //
    // For superior we need to forget the transaction after a heuristic, but not for a subordinate as the
    // superior should poll us and pick up the result.  We then forget when requested by the superior.
    // The parameter is used to control when we wait for the superior after
    // a heuristic.  eg in cases where we give up retries
    // or we know the superior has gone we won't wait.
    //
    public void recoverRollback(boolean waitForHeuristic) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "recoverRollback", new java.lang.Object[] { waitForHeuristic, _doNotRetryRecovery });

        if (!_doNotRetryRecovery) {
            auditTransaction();
            final int state = _status.getState();
            try {
                if (state != TransactionState.STATE_ROLLING_BACK &&
                    state != TransactionState.STATE_HEURISTIC_ON_ROLLBACK &&
                    state != TransactionState.STATE_ROLLED_BACK)
                    _status.setState(TransactionState.STATE_ROLLING_BACK);

                if (state == TransactionState.STATE_ACTIVE) {
                    getResources().distributeEnd(XAResource.TMSUCCESS);
                }

                getResources().distributeRollback();
            } catch (HeuristicMixedException hme) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Exception caught during recoverRollback processing", hme);
                logHeuristicInRecovery(false);
                addHeuristic(); // PM39333
                // state change handled by notifyCompletion
            } catch (HeuristicCommitException hce) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Exception caught during recoverRollback processing", hce);
                logHeuristicInRecovery(false);
                addHeuristic(); // PM39333
                // state change handled by notifyCompletion
            } catch (HeuristicHazardException hhe) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Exception caught during recoverRollback processing", hhe);
                logHeuristicInRecovery(false);
                addHeuristic(); // PM39333
                // state change handled by notifyCompletion
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.recoverRollback", "1142", this);
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Exception caught during recoverRollback processing", se);
                Tr.error(tc, "WTRN0059_RECOVERY_EXCEPTION_IN_ROLLBACK", new java.lang.Object[] { getTranName(), se });
            } finally {
                // If we get into heuristic state on a subordinate, we need to wait for a forget
                // Note: if we were already in heuristic state on reconstruct we prolong there
                // in case a forget arrives before we get into recovery.
                if (waitForHeuristic && state != TransactionState.STATE_HEURISTIC_ON_ROLLBACK &&
                    TransactionState.STATE_HEURISTIC_ON_ROLLBACK == _status.getState()) {
                    if (tc.isDebugEnabled())
                        Tr.debug(tc, "Transaction state now heuristic");
                    prolongFinish();
                }

                _doNotRetryRecovery = true; // Reset - postCompletion may set it again...

                // Issue any forgets to local resources if need be (this may set retry)
                // and determine if we need to retry recovery again
                postCompletion(Status.STATUS_ROLLEDBACK);
                notifyCompletion();
            }
        }

        handleHeuristicOnRollback(waitForHeuristic);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "recoverRollback");
    }

    protected void handleHeuristicOnRollback(boolean waitForHeuristic) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "handleHeuristicOnRollback", waitForHeuristic);

        // If no resources to complete - check if we are a subordinate
        // with outstanding heuristic indication.  We may have missed a forget from
        // a superior as we do not log, it so on recovery may still be waiting for it.
        // Really when we get a forget we should remove our subordinate and heuristic
        // status from the log.  Check of superior has gone.
        if (_doNotRetryRecovery && waitForHeuristic &&
            TransactionState.STATE_HEURISTIC_ON_ROLLBACK == _status.getState()) {
            if (tc.isEventEnabled())
                Tr.event(tc, "recoverRollback", "Checking if we can forget transaction");

            if (_JCARecoveryData == null) {
                // JCA must always send a forget...
                notifyCompletion();
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "handleHeuristicOnRollback");
    }

    protected void setRBO() {
        if (!_rollbackOnly) {
            if (tcSummary.isDebugEnabled())
                Tr.debug(tcSummary, "Transaction setRollbackOnly.", new Object[] { this, _xid, Util.stackToDebugString(new Throwable()) });
            _rollbackOnly = true;
        }
    }

    /**
     * // PK19059 starts here
     * /**
     * stores the original exception when called from RegisteredSyncs
     */
    public void setOriginalException(Throwable originalException) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setOriginalException");
        _originalException = originalException;
        if (tc.isEntryEnabled())
            Tr.exit(tc, "setOriginalException");
    }

    /**
     * returns the exception stored by RegisteredSyncs
     *
     * @return Throwable the original exceptions
     */
    public Throwable getOriginalException() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getOriginalException");
        if (tc.isEntryEnabled())
            Tr.exit(tc, "getOriginalException", _originalException);
        return _originalException;
    }

    // PK19059 ends here

    /**
     * Modify the transaction such that the only possible outcome
     * of the transaction is to rollback.
     *
     * @exception IllegalStateException
     */
    @Override
    public void setRollbackOnly() throws IllegalStateException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setRollbackOnly (SPI)");

        final int state = _status.getState();

        if (state == TransactionState.STATE_NONE ||
            state == TransactionState.STATE_COMMITTED ||
            state == TransactionState.STATE_ROLLED_BACK) {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "setRollbackOnly (SPI)");
            throw new IllegalStateException("No transaction available");
        }
        setRBO();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "setRollbackOnly (SPI)");
    }

    /**
     * Enlist the resouce with the transaction associated with the target
     * TransactionImpl object.
     *
     * This is the default version of the method and does not take in the
     * XA resource class name or info objects required during recovery.
     * This means that the resource cannot be logged or recovered and so
     * cannot be a two phase capable resource. Only one phase capable
     * resources may be enlisted by this method. We prevent registration
     * of two phase capable resources by throwing an exception.
     *
     * This method defers to the implementation version of the method
     * and passes null fields for the recovery parameters.
     *
     * The intention is that that this method should be used as little
     * as possible. Future calls should only be made directly to the
     * implementation version of this method, passing null recovery
     * information when registering a one phase capable resource.
     *
     * @return <i>true</i> if the resource was enlisted successfully; otherwise <i>false</i>.
     *
     * @param xaRes the XAResource object associated with XAConnection
     *
     * @exception RollbackException
     * @exception IllegalStateException
     * @exception SystemException
     */
    @Override
    public boolean enlistResource(XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "enlistResource (SPI)", xaRes);

        //
        // Check the transaction is in a valid state to allow a new resource to be enlisted.
        //
        switch (_status.getState()) {
            case TransactionState.STATE_ACTIVE: {
                if (!_timedOut)
                    break;
            }
            case TransactionState.STATE_ROLLED_BACK:
            case TransactionState.STATE_ROLLING_BACK:
            case TransactionState.STATE_HEURISTIC_ON_ROLLBACK: {
                RollbackException rbe = new RollbackException("Transaction rolled back");
                FFDCFilter.processException(rbe, "com.ibm.tx.jta.TransactionImpl.enlistResource", "1760", this);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "enlistResource (SPI)", rbe);
                throw rbe;
            }
            default: {
                //
                // We do NOT allow enlistResource during preparing state.
                //
                final IllegalStateException ise = new IllegalStateException("Transaction is inactive or prepared");
                FFDCFilter.processException(ise, "com.ibm.tx.jta.TransactionImpl.enlistResource", "1769", this);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "enlistResource (SPI)", ise);
                throw ise;
            }
        }

        if (xaRes == null) {
            final IllegalStateException ise = new IllegalStateException("Cannot enlist a null resource");
            if (tc.isEntryEnabled())
                Tr.exit(tc, "enlistResource", ise);
            throw ise;
        }

        try {
            if (xaRes instanceof OnePhaseXAResource) {
                getResources().enlistResource(xaRes);
            } else {

                final int recoveryId = TransactionManagerFactory.getTransactionManager().registerResourceInfo("unused", new DirectEnlistXAResourceInfo(xaRes));
                enlistResource(xaRes, recoveryId);
            }
        } catch (IllegalStateException ise) {
            FFDCFilter.processException(ise, "com.ibm.tx.jta.TransactionImpl.enlistResource", "1858", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "enlistResource (SPI)", ise);
            throw ise;
        } catch (Throwable t) {
            FFDCFilter.processException(t, "com.ibm.tx.jta.TransactionImpl.enlistResource", "1868", this);
            this.setRollbackOnly();
            final RollbackException rbe = new RollbackException(t.getMessage());
            rbe.initCause(t);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "enlistResource (SPI)", rbe);
            throw rbe;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "enlistResource (SPI)", Boolean.TRUE);
        return true;
    }

    /**
     * Enlist the resouce with the transaction associated with the target
     * TransactionImpl object.
     *
     * This is the implementation version of the method and does take in the
     * XA resource class name and info objects required during recovery. If
     * these fields are provided (ie are not passed in as nulls) the resource
     * will be logged and recovered as required. If these fields are null,
     * the resource must a one phase capable resource or an exception will
     * be thrown.
     *
     * @return <i>true</i> if the resource was enlisted successfully; otherwise <i>false</i>.
     *
     * @param xaRes the XAResource object associated with XAConnection
     *
     * @exception RollbackException
     * @exception IllegalStateException
     * @exception SystemException
     */
    public boolean enlistResource(XAResource xaRes, int recoveryIndex) throws RollbackException, IllegalStateException, SystemException {
        return enlistResource(xaRes, recoveryIndex, XAResource.TMNOFLAGS);
    }

    public boolean enlistResource(XAResource xaRes, int recoveryIndex, int branchCoupling) throws RollbackException, IllegalStateException, SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "enlistResource (SPI)", new Object[] { xaRes, recoveryIndex, branchCoupling });

        //
        // Check the transaction is in a valid state to allow a new resource to be enlisted.
        //
        switch (_status.getState()) {
            case TransactionState.STATE_ACTIVE:
                if (!_timedOut)
                    break;
            case TransactionState.STATE_ROLLED_BACK:
            case TransactionState.STATE_ROLLING_BACK:
            case TransactionState.STATE_HEURISTIC_ON_ROLLBACK:
                final RollbackException rbe = new RollbackException("Transaction rolled back");
                FFDCFilter.processException(rbe, "com.ibm.tx.jta.TransactionImpl.enlistResource", "1895", this);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "enlistResource (SPI)", rbe);
                throw rbe;
            default:
                //
                // We do NOT allow enlistResource during preparing state.
                //
                final IllegalStateException ise = new IllegalStateException("Transaction is inactive or prepared");
                FFDCFilter.processException(ise, "com.ibm.tx.jta.TransactionImpl.enlistResource", "1904", this);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "enlistResource (SPI)", ise);
                throw ise;
        }

        // XAResource is null throw exception.
        if (xaRes == null) {
            final String msg = "XAResource reference is null";
            final IllegalStateException ise = new IllegalStateException(msg);
            FFDCFilter.processException(ise, "com.ibm.tx.jta.TransactionImpl.enlistResource", "1914", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "enlistResource (SPI)", ise);
            throw ise;
        }

        // If the new resource is a two phase resource but no recovery information has
        // been supplied then stop it being enlisted by throwing an exception.
        if (recoveryIndex < 0) {
            final String msg = "Illegal attempt to enlist 2PC XAResource without recovery information";
            final IllegalStateException ise = new IllegalStateException(msg);
            FFDCFilter.processException(ise, "com.ibm.tx.jta.TransactionImpl.enlistResource", "1934", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "enlistResource (SPI)", ise);
            throw ise;
        }

        // Ensure the resource recoveryId is valid. The recovery data is
        // passed to the JTAXAResourceImpl wrapper for logging at
        // prepare time.
        final PartnerLogTable plt = _failureScopeController.getPartnerLogTable();
        final PartnerLogData pld = plt.getEntry(recoveryIndex);

        if (pld instanceof XARecoveryData) {
            final XARecoveryData recData = (XARecoveryData) pld;

            try {
                getResources().enlistResource(xaRes, recData, branchCoupling);
            } catch (RollbackException rbe) {
                FFDCFilter.processException(rbe, "com.ibm.tx.jta.TransactionImpl.enlistResource", "2035", this);
                this.setRollbackOnly();
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "enlistResource (SPI)", rbe);
                throw rbe;
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.enlistResource", "2042", this);
                this.setRollbackOnly();
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "enlistResource (SPI)", se);
                throw se;
            } catch (Throwable t) {
                FFDCFilter.processException(t, "com.ibm.tx.jta.TransactionImpl.enlistResource", "2049", this);
                this.setRollbackOnly();
                final SystemException se = new SystemException(t.getLocalizedMessage());
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "enlistResource (SPI)", t);
                throw se;
            }
        } else {
            final String msg = "XAResource recovery data token invalid";
            final SystemException se = new SystemException(msg);
            FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.enlistResource", "1955", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "enlistResource (SPI)", se);
            throw se;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "enlistResource (SPI)", Boolean.TRUE);
        return true;
    }

    /**
     * Enlist a remote resouce with the transaction associated with the target
     * TransactionImpl object. Typically, this remote resource represents a
     * downstream suborindate server.
     *
     * @param remoteRes the remote resource wrapped as a JTAResource
     */
    public void enlistResource(JTAResource remoteRes) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "enlistResource (SPI): args: ", remoteRes);

        getResources().addRes(remoteRes);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "enlistResource (SPI)");
    }

    /**
     * This method allows the transactions RegisteredResources object to initiate
     * a change in state of the transaction after all 2PC resources have been
     * successfully prepared in a LPS enabled transaction. Once the final 1PC
     * resource has then been completed RegisteredResources.distributePrepare() will
     * return with the direction in which the 1PC completed.
     *
     * @exception SystemException
     *                Thrown if the state change is invalid.
     */
    public void logLPSState() throws SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "logLPSState");

        // Only log if configured for this server.
        if (_configProvider.isLoggingForHeuristicReportingEnabled())
            _status.setState(TransactionState.STATE_LAST_PARTICIPANT);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "logLPSState");
    }

    /**
     * Log the fact that we have encountered a heuristic outcome.
     *
     * @param commit boolean to indicate whether we were committing or rolling
     *            back
     * @exception SystemException
     *                Thrown if the state change is invalid.
     */
    protected void logHeuristic(boolean commit) throws SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "logHeuristic", commit);

        if (_subordinate && _status.getState() != TransactionState.STATE_PREPARING) {
            getResources().logHeuristic(commit);
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "logHeuristic");
    }

    // Optional logHeuristic case for recovery when we ignore the system exception
    // if we couldnt update the state in the log
    private void logHeuristicInRecovery(boolean commit) {
        try {
            logHeuristic(commit);
        } catch (SystemException se) {
            FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.logHeuristicInRecovery", "2955", this);
        }
    }

    /**
     * Delist the resource specified from the transaction associated
     * with the target Transaction. XAResource.end is invoked and
     * 2PC callbacks are delivered at transaction completion on the
     * resource (ie the resource is not removed from the registration
     * list)
     *
     * @param xaRes The XAResouce object associated with the XAConnection
     * @param flag TMSUSPEND, TMFAIL or TMSUCCESS flag to xa_end
     *
     * @exception IllegalStateException Thrown if the transaction in the
     *                target object is inactive
     * @exception SystemException Thrown if the transaction manager encounters
     *                an unexpected error condition.
     *
     */
    @Override
    public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException {
        if (tc.isEntryEnabled())
            Tr.event(tc, "delistResource (SPI)", new Object[] { xaRes, Util.printFlag(flag) });

        //
        // Check the transaction is in a valid state to allow a resource to be delisted.
        //
        if (_status.getState() != TransactionState.STATE_ACTIVE) {
            final String msg = "Transaction is inactive or prepared";
            final IllegalStateException ise = new IllegalStateException(msg);
            FFDCFilter.processException(ise, "com.ibm.tx.jta.TransactionImpl.delistResource", "2212", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "delistResource (SPI)", ise);
            throw ise;
        }

        // XAResource is null throw exception.
        if (xaRes == null) {
            final String msg = "XAResource reference is null";
            final IllegalStateException ise = new IllegalStateException(msg);
            FFDCFilter.processException(ise, "com.ibm.tx.jta.TransactionImpl.delistResource", "2224", this);
            if (tc.isEntryEnabled())
                Tr.exit(tc, "delistResource (SPI)", ise);
            throw ise;
        }

        // try to end transaction association using specified flag.
        boolean delisted = false;
        try {
            delisted = getResources().delistResource(xaRes, flag);
        } catch (SystemException se) {
            FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.delistResource", "1928", this);
            throw se;
        } finally {
            if (tc.isEntryEnabled())
                Tr.exit(tc, "delistResource", new Object[] { "(SPI)", delisted });
        }

        return delisted;
    }

    @Override
    public void registerSynchronization(javax.transaction.Synchronization sync) throws RollbackException, IllegalStateException {
        registerSynchronization(sync, RegisteredSyncs.SYNC_TIER_NORMAL);
    }

    public void registerSynchronization(javax.transaction.Synchronization sync,
                                        int tier) throws RollbackException, IllegalStateException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "registerSynchronization (SPI)", new Object[] { this,
                                                                         sync, tier });

        //
        // Check the transaction is in a valid state to allow a new
        // synchronization to be registered.
        //
        switch (_status.getState()) {
            case TransactionState.STATE_ACTIVE:
                if (!_timedOut)
                    break;
            case TransactionState.STATE_ROLLED_BACK:
            case TransactionState.STATE_ROLLING_BACK:
            case TransactionState.STATE_HEURISTIC_ON_ROLLBACK:
                final RollbackException rbe = new RollbackException("Transaction rolled back");
                FFDCFilter.processException(rbe, "com.ibm.tx.jta.TransactionImpl.registerSynchronization", "2289", this);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "registerSynchronization (SPI)", rbe);
                throw rbe;
            default:
                //
                // We do NOT allow registerSynchronization during preparing state.
                //
                final IllegalStateException ise = new IllegalStateException("Transaction is inactive or prepared");
                FFDCFilter.processException(ise, "com.ibm.tx.jta.TransactionImpl.registerSynchronization", "2297", this);
                if (tc.isEntryEnabled())
                    Tr.exit(tc, "registerSynchronization (SPI)", ise);
                throw ise;
        }

        getSyncs().add(sync, tier);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "registerSynchronization (SPI)");
    }

    /**
     * Obtain the status of the transaction associated with the target object
     *
     * @return the transaction status
     */
    @Override
    public int getStatus() {
        int state = Status.STATUS_UNKNOWN;

        switch (_status.getState()) {
            case TransactionState.STATE_NONE:
                state = Status.STATUS_NO_TRANSACTION;
                break;
            case TransactionState.STATE_ACTIVE:
                if (_rollbackOnly)
                    state = Status.STATUS_MARKED_ROLLBACK;
                else
                    state = Status.STATUS_ACTIVE;
                break;
            case TransactionState.STATE_PREPARING:
            case TransactionState.STATE_LAST_PARTICIPANT:
                state = Status.STATUS_PREPARING;
                break;
            case TransactionState.STATE_PREPARED:
                state = Status.STATUS_PREPARED;
                break;
            case TransactionState.STATE_COMMITTING:
            case TransactionState.STATE_COMMITTING_ONE_PHASE:
                state = Status.STATUS_COMMITTING;
                break;
            case TransactionState.STATE_HEURISTIC_ON_COMMIT:
            case TransactionState.STATE_COMMITTED:
                state = Status.STATUS_COMMITTED;
                break;
            case TransactionState.STATE_ROLLING_BACK:
                state = Status.STATUS_ROLLING_BACK;
                break;
            case TransactionState.STATE_HEURISTIC_ON_ROLLBACK:
            case TransactionState.STATE_ROLLED_BACK:
                state = Status.STATUS_ROLLEDBACK;
                break;
        }

        if (tc.isDebugEnabled())
            Tr.debug(tc, "getStatus (SPI)", Util.printStatus(state));
        return state;
    }

    public RegisteredResources getResources() {
        if (_resources == null) {
            _resources = new RegisteredResources(this, _disableTwoPhase);
        }
        return _resources;
    }

    public RegisteredSyncs getSyncs() {
        if (_syncs == null) {
            _syncs = new RegisteredSyncs(this);
        }
        return _syncs;
    }

    public TransactionState getTransactionState() {
        return _status;
    }

    /**
     * Returns a global identifier that represents the TransactionImpl's transaction.
     *
     * @return The global transaction identifier.
     */
    public XidImpl getXidImpl() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getXidImpl", this);
        return getXidImpl(true);
    }

    /**
     * Returns a global identifier that represents the TransactionImpl's transaction.
     *
     * @return The global transaction identifier.
     */
    public XidImpl getXidImpl(boolean createIfAbsent) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getXidImpl", new Object[] { this, createIfAbsent });

        if (createIfAbsent && (_xid == null)) {
            // Create an XID as this transaction identifier.
            _xid = new XidImpl(new TxPrimaryKey(_localTID, Configuration.getCurrentEpoch()));
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "getXidImpl", _xid);
        return (XidImpl) _xid;
    }

    // For recovery only
    public void setXidImpl(XidImpl xid) {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "setXidImpl", new Object[] { xid, this });
        _xid = xid;
    }

    @Override
    public Xid getXid() {
        return getXidImpl();
    }

    public String getTranName() {
        // Return the same as used for MBean support
        return Util.toHexString(getTID()).toUpperCase();
    }

    /**
     * Returns the internal identifier for the transaction.
     *
     * @return The local identifier.
     */
    public long getLocalTID() {
        return _localTID;
    }

    public RecoverableUnit getLog() throws SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getLog");

        // If this is a recovered transaction then we already have a reference for
        // the recovery log (be it a peer or the local one). Otherwise, we need
        // to create an RU for the first time. Its safe to use the local FSM and
        // hense the local log as we only ever do this creation on the local server.
        if (_logUnit == null) {
            //
            // We need to create an entry in the recovery
            // log for this transaction if we have logging enabled.
            //
            _tranLog = _failureScopeController.getTransactionLog();
            if (_tranLog != null) {
                try {
                    _logUnit = _tranLog.createRecoverableUnit();
                } catch (Exception e) {
                    FFDCFilter.processException(e, "com.ibm.tx.jta.TransactionImpl.getLog", "2134", this);
                    Tr.error(tc, "WTRN0066_LOG_WRITE_ERROR", e);
                    _rollbackOnly = true;
                    throw new SystemException(e.getLocalizedMessage());
                }
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "getLog", _logUnit);
        return _logUnit;
    }

    //
    // Check for failed resources, heuristics, retries and
    // call afterCompletion.
    // This method is called inline by commit/rollback at the end of the transaction.
    //
    protected void postCompletion(int status) {
        postCompletion(status, false, false); /* @377762.2A4 */
    }

    protected void postCompletion(int status, boolean cascaded, boolean cascafter) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "postCompletion", status);

        // call back into the CDI transaction scope code, telling it to destroy bean instances
        // created within the scope of this tran.
        try {
            if (tScopeDestroyer != null) {
                tScopeDestroyer.destroy();
            }
        } catch (RuntimeException e) {
            // WELD currently eats (and logs) runtime errors coming out of bean instance destructors
            // so catching here is just a precaution.
            FFDCFilter.processException(e, "com.ibm.tx.jta.impl.TransactionImpl.postCompletion", "2738", this);
        }

        if (cascaded && cascafter) /* @377762.2A14 */
        {
            try {
                distributeAfter(status);
            } catch (SystemException e) {
                FFDCFilter.processException(e, "com.ibm.tx.jta.impl.TransactionImpl.postCompletion", "2870", this);
                _systemExceptionOccured = true;
            }
            if (tc.isEntryEnabled())
                Tr.exit(tc, "postCompletion");
            return;
        }
        perfCompleted(status);

        try {
            if (_resources != null &&
                HeuristicOutcome.isHeuristic(_resources.getHeuristicOutcome())) {
                try {
                    _resources.distributeForget(); // may set retry flag
                } catch (SystemException se) {
                    _systemExceptionOccured = true;
                }
            }

            if (_isZos && postZOSCompletion()) {
            } else if (requireRetry()) {
                // Need to retry to complete resources - handle recovery and normal txns separately
                if (_inRecovery) {
                    // Called from recoverCommit or recoverRollback
                    // Need to retry to complete resources (either commit/rollback or forget)

                    // First time through - reset count in case used by replay_completion
                    // during prepared phase
                    if (_retryAlarm == null) {
                        _retryAttempts = 0;
                        // Create dummy retryAlarm so it can be used by operator to finish transactions
                        _retryAlarm = new RetryAlarm();
                    }

                    final int heuristicRetryLimit = _configProvider.getHeuristicRetryLimit();

                    // See if we should retry
                    if (heuristicRetryLimit <= 0 || _retryAttempts < heuristicRetryLimit) {
                        _retryAttempts++;
                        // inhibit forgetTransaction
                        prolongFinish();
                        _doNotRetryRecovery = false;
                    } else {
                        Tr.warning(tc, "WTRN0055_GIVING_UP_OUTCOME_DELIVERY", getTranName());
                        // do clean up ...
                        // destroy remaining resources
                        getResources().destroyResources();
                    }
                } else {
                    _retryAlarm = new RetryAlarm();
                    _retryAlarm.start();
                }
            }
        } finally {
            try {
                if (!cascaded) { /* @377762.2A2 */
                    distributeAfter(status);
                }
            } catch (SystemException e) {
                FFDCFilter.processException(e, "com.ibm.tx.jta.impl.TransactionImpl.postCompletion", "2870", this);
                _systemExceptionOccured = true;
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "postCompletion");
    }

    protected boolean postZOSCompletion() {
        // Not used in JTM
        return false;
    }

    @SuppressWarnings("unused")
    protected void perfCompleted(int status) {
        // Not used in JTM
    }

    //
    // Distribute the after completion callbacks.
    //
    // This method is called inline by commit/rollback at the end of the transaction.
    //
    protected void distributeAfter(int status) throws SystemException {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "distributeAfter", status);

        // Take the transaction off the thread to allow container
        // mediated dispatches on components requiring a tx from
        // callbacks.
        TransactionManagerFactory.getTransactionManager().suspend(); //LIDB2775-103

        if (_syncs != null) {
            _syncs.distributeAfter(status);
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "distributeAfter");
    }

    /**
     * Delay completing transaction for asynchronous work.
     */
    public final synchronized void prolongFinish() {
        _finishCount++;
        if (tc.isDebugEnabled())
            Tr.debug(tc, "prolongFinish", _finishCount);
    }

    /**
     * Notify of completion of asynchronous work. When all work has
     * completed, the transaction is forgotten. (Note: completion here
     * is nearer to the jts1 finished concept)
     */
    public final synchronized void notifyCompletion() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "notifyCompletion", "finishCount=" + _finishCount);

        if (--_finishCount < 0) {
            // may need to make final state transition here ?
            try {
                switch (_status.getState()) {
                    case TransactionState.STATE_COMMITTING:
                    case TransactionState.STATE_HEURISTIC_ON_COMMIT:
                        _status.setState(TransactionState.STATE_COMMITTED);
                        break;
                    case TransactionState.STATE_ROLLING_BACK:
                    case TransactionState.STATE_HEURISTIC_ON_ROLLBACK:
                        _status.setState(TransactionState.STATE_ROLLED_BACK);
                        break;

                    // one phase commit that resulted in retries ...
                    case TransactionState.STATE_PREPARING:
                    case TransactionState.STATE_COMMITTING_ONE_PHASE:
                        _status.setState(TransactionState.STATE_COMMITTED);
                } // end switch
            } catch (SystemException se) {
                FFDCFilter.processException(se, "com.ibm.tx.jta.TransactionImpl.notifyCompletion", "2416", this);
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "SystemException setting state during completion processing", se);
                // swallow this exception ... we're called in finally blocks!
            } finally {
                forgetTransaction(true);
            }
        } // end if
        if (tc.isEntryEnabled())
            Tr.exit(tc, "notifyCompletion");

    } // end notifyCompletion

    //
    // forgetTransaction is called when a transaction has completed and also if the recovery cycle is
    // terminated.  In the latter case, we need to remove any knowledge of the transaction in memory
    // but leave information in the log on disk.
    //
    protected void forgetTransaction(boolean transactionCompleted) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "forgetTransaction", transactionCompleted);

        if (_isZos) {
            forgetZOSTransaction();
        }

        //
        // Call any resource callbacks for destroy
        //
        if (_destroyCallbacks != null) {
            for (int i = 0; i < _destroyCallbacks.size(); i++) {
                _destroyCallbacks.get(i).destroy();
            }
            _destroyCallbacks.clear();
            _destroyCallbacks = null;
        }

        //
        // Remove any log record if we have really completed the transaction
        //
        if (_logUnit != null && transactionCompleted) {
            try {
                _tranLog.removeRecoverableUnit(_logUnit.identity());
            } catch (Exception e) {
                if (e instanceof LogClosedException && _failureScopeController.getTransactionLog() == null) {
                    if (tc.isEventEnabled())
                        Tr.event(tc, "Transaction log has been shut down");
                } else {
                    FFDCFilter.processException(e, "com.ibm.tx.jta.TransactionImpl.forgetTransaction", "1199", this);
                    Tr.error(tc, "WTRN0066_LOG_WRITE_ERROR", e);
                }
                // Transaction has run, not a lot we can do other than wait for recovery so carry on
            }

            _logUnit = null;
        }

        //
        // Remove the LocalTID from the table - this should also remove the last reference to this
        // in asynchronous mode to allow the transactionImpl to be garbage collected as the TM will
        // have already freed its reference.  (Note: Recovery Agent transactions are handled differently)
        //
        LocalTIDTable.removeLocalTID(_localTID);

        if (_isZos) {
            removeZOSTransaction();
        }

        // Let the failure scope controller know that this transaction is complete so that it
        // no longer worries about it when its asked to shutdown (either at server shutdown or
        // peer recovery termination)
        if (_failureScopeController != null) {
            _failureScopeController.deregisterTransaction(this, _inRecovery);
        }

        // Remove entry from list of txns imported from an RA
        if (_JCARecoveryData != null) {
            TxExecutionContextHandler.removeTxn(getXid());
        }

        _status.reset(); // Reset state to NONE

        if (tc.isEntryEnabled())
            Tr.exit(tc, "forgetTransaction");
    }

    protected void removeZOSTransaction() {
        // Not used in JTM
    }

    protected void forgetZOSTransaction() {
        // Not used in JTM
    }

    /**
     * Implementation of the UOWCoordinator interface.
     *
     * @return true
     */
    @Override
    public boolean isGlobal() {
        return true;
    }

    @Override
    public byte[] getTID() // JTA2 add
    {
        return getXidImpl().getOtidBytes(); // return all the XID tid bytes
    }

    @Override
    public boolean getRollbackOnly() {
        return _rollbackOnly;
    }

    @Override
    public int getTxType() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getTxType", _txType);
        return _txType;
    }

    /**
     * equals returns true if they are of same global transaction.
     *
     * @param t
     *
     * @return
     */
    @Override
    public final boolean equals(java.lang.Object t) {
        if (t == this) {
            return true;
        }

        if (t instanceof TransactionImpl) {
            final TransactionImpl tran = (TransactionImpl) t;

            if (tran.getLocalTID() == this.getLocalTID()) {
                return true;
            }
        }

        return false;
    }

    /**
     * hashCode returns the int portion of the local transaction identifier.
     *
     * Note: we only allocate int values for the localTID
     *
     * @return
     */
    @Override
    public final int hashCode() {
        return _localTID;
    }

    /*
     * Sets the boundary of the LTC that was completed as a
     * result of this Transaction being begun. When this
     * transaction completes an LTC with the same boundary
     * is placed on the thread.
     */
    @Override
    public void setCompletedLTCBoundary(Byte completedLTCBoundary) {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "setCompletedLTCBoundary", new Object[] { completedLTCBoundary });
        _completedLTCBoundary = completedLTCBoundary;
    }

    @Override
    public Byte getCompletedLTCBoundary() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getCompletedLTCBoundary", _completedLTCBoundary);
        return _completedLTCBoundary;
    }

    /**
     * Return a string representing this Transaction for debuging
     *
     * @return the string representation of this Transaction
     */
    @Override
    public String toString() {
        // d171555.2 - cache the result once there is a localTID set.  Note: TransactionImpl may get traced
        // and thus call toString prior to a _localTID setting, so in this case we do not cache the result.
        // In the case of a non-interop transction we will never cache as _localTID always remains 0.
        String result = _tranName;
        if (result == null) {
            final String tail = (_localTID == 0) ? "" : "#tid=" + _localTID;
            result = getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(this)) + tail;
            if (_localTID != 0)
                _tranName = result;
        }
        return result;
    }

    /**
     * Called on the timeout thread.
     * If the context is not on this server then rollback the transaction
     * else just mark it for rollback.
     */
    public synchronized void timeoutTransaction(boolean initial) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "timeoutTransaction", initial);

        if (tc.isEventEnabled())
            Tr.event(tc, "(SPI) Transaction TIMEOUT occurred for TX: " + getLocalTID());

        _timedOut = true; // mark
        _rollbackOnly = true; // for the case of server quiesce?

        // inactivity timeout may have happened ... check status
        if (_status.getState() == TransactionState.STATE_ACTIVE) {
            // If there is no txn timeout, or we are into completion phase the timeout setting may be zero
            if (_timeout == 0)
                _timeout = 10;
            TimeoutManager.setTimeout(this, TimeoutManager.REPEAT_TIMEOUT, _timeout);
        }

        // Story 132693: JDBC4.1 allows connections to be aborted. Only call abort on the initial
        // call, not on retries.
        if (initial)
            abortTransactionParticipants();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "timeoutTransaction");
    }

    /**
     * The abortTransactionParticipants() method allows the exploitation of
     * JDBC 4.1 Connection abort functionality. The participants in a transaction
     * can be aborted if a transaction times out.
     */
    protected synchronized void abortTransactionParticipants() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "abortTransactionParticipants");

        this.getResources().abort();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "abortTransactionParticipants");
    }

    public boolean isTimedOut() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "isTimedOut", _timedOut);

        return _timedOut;
    }

    public void subRollback() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "subRollback");
        _subRollback = true;
        if (tc.isEntryEnabled())
            Tr.exit(tc, "subRollback");
    }

    public boolean isSubRollback() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "isSubRollback", _subRollback);
        return _subRollback;
    }

    /**
     * Internal class to manage asynchronous retries via AlarmManager
     * mechanism.
     */
    public class RetryAlarm implements AlarmListener {
        private int _count;
        private int _wait;
        private Alarm _alarm;
        private final AlarmManager _alarmManager = _configProvider.getAlarmManager();
        private boolean _finished;

        public RetryAlarm() {
            if (tc.isDebugEnabled())
                Tr.debug(tc, "RetryAlarm");
        }

        public void start() {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "RetryAlarm.start");

            _count = 0;

            final int heuristicRetryInterval = _configProvider.getHeuristicRetryInterval();

            _wait = (heuristicRetryInterval <= 0) ? TransactionImpl.defaultRetryTime : heuristicRetryInterval;

            // inhibit forgetTransaction
            prolongFinish();

            // create an alarm for retry activity
            if (getResources().retryImmediately()) {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Retrying immediately");
                _alarm = _alarmManager.scheduleDeferrableAlarm(0L, this);
            } else {
                if (tc.isDebugEnabled())
                    Tr.debug(tc, "Retrying in " + _wait + " seconds");
                _alarm = _alarmManager.scheduleDeferrableAlarm(_wait * 1000L, this);
            }

            if (tc.isEntryEnabled())
                Tr.exit(tc, "RetryAlarm.start");
        }

        /**
         * Called when the transaction needs to be deleted by operator
         * intervention.
         */
        public synchronized void stop() {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "RetryAlarm.stop");
            if (!_finished) {
                _finished = true;
                if (_alarm != null) {
                    _alarm.cancel();
                    _alarm = null;
                } // end if

                Tr.error(tc, "WTRN0077_OPERATOR_CANCELLED", getTranName());

                // destroy remaining resources ?
                getResources().destroyResources();

                // now do clean up ... call after_completion etc
                finishTransaction();
                getResources().updateHeuristicOutcome(StatefulResource.HEURISTIC_MIXED);

            } // end if !_finished

            if (tc.isEntryEnabled())
                Tr.exit(tc, "RetryAlarm.stop");
        }

        /**
         * AlarmListener interface method.
         * Called when the retry interval has expired.
         * Retries completion processing of the resources and if further
         * retries are necessary creates a new alarm.
         * <p>
         * When no further retries are needed, completes the transaction
         * by calling after_completion, setting the transaction state and
         * calling forgetTransaction to delete the log entry and local id.
         * <p>
         * Finally notifies this object to release any threads that are waiting.
         */
        @Override
        public synchronized void alarm(Object alarmContext) {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "RetryAlarm.alarm");
            if (!_finished) {
                try {
                    boolean retryRequired = getResources().distributeOutcome();
                    retryRequired |= getResources().distributeForget();
                    if (retryRequired) {
                        _count++;

                        final int heuristicRetryLimit = _configProvider.getHeuristicRetryLimit();

                        if (heuristicRetryLimit > 0 && _count >= heuristicRetryLimit) {
                            Tr.warning(tc, "WTRN0055_GIVING_UP_OUTCOME_DELIVERY", getTranName());
                            // do clean up ... after_completion etc
                            // destroy remaining resources ?
                            getResources().destroyResources();
                            finishTransaction();
                            _finished = true;
                            getResources().updateHeuristicOutcome(StatefulResource.HEURISTIC_MIXED);
                            return;
                        } // end if

                        if (_count % 10 == 0 && _wait < Integer.MAX_VALUE / 2)
                            _wait *= 2;

                        // create an alarm for further retry activity
                        if (getResources().retryImmediately()) {
                            if (tc.isDebugEnabled())
                                Tr.debug(tc, "Retrying immediately");
                            _alarm = _alarmManager.scheduleDeferrableAlarm(0L, this);
                        } else {
                            if (tc.isDebugEnabled())
                                Tr.debug(tc, "Retrying in " + _wait + " seconds");
                            _alarm = _alarmManager.scheduleDeferrableAlarm(_wait * 1000L, this);
                        }
                    } else // finished!
                    {
                        _finished = true;
                        // clean up ... after_completion etc
                        finishTransaction();
                    }
                } catch (Throwable ex) {
                    FFDCFilter.processException(ex, "com.ibm.tx.jta.TransactionImpl.RetryAlarm", "1327", this);
                    // probably want to save this exception for sync use
                    // discard if async?
                    // give up at this point?  ... could be transient?

                    _finished = true;
                    // what cleanup to do? ... after_completion, destroy resources?`
                    finishTransaction();
                }
            } // end if _finished

            if (tc.isEntryEnabled())
                Tr.exit(tc, "RetryAlarm.alarm");
        }

        /**
         * Complete transaction processing.
         * Utility method called from alarm (retry) thread.
         */
        private void finishTransaction() {
            if (tc.isEntryEnabled())
                Tr.entry(tc, "RetryAlarm.finishTransaction");

            notifyCompletion();

            if (tc.isEntryEnabled())
                Tr.exit(tc, "RetryAlarm.finishTransaction");
        }

    } // end RetryAlarm

    public void putResource(Object key, Object value) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "putResource", new Object[] { key, value, this });

        if (key == null) {
            final NullPointerException npe = new NullPointerException();

            if (tc.isEntryEnabled())
                Tr.exit(tc, "putResource", npe);
            throw npe;
        }

        if (_synchronizationRegistryResources == null) {
            _synchronizationRegistryResources = new HashMap<Object, Object>();
        }

        _synchronizationRegistryResources.put(key, value);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "putResource");
    }

    public Object getResource(Object key) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "getResource", new Object[] { key, this });

        if (key == null) {
            final NullPointerException npe = new NullPointerException();

            if (tc.isEntryEnabled())
                Tr.exit(tc, "getResource", npe);
            throw npe;
        }

        if (_synchronizationRegistryResources == null) {
            _synchronizationRegistryResources = new HashMap<Object, Object>();
        }

        final Object resource = _synchronizationRegistryResources.get(key);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "getResource", resource);
        return resource;
    }

    public long getLocalId() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getLocalId", this);
        return _localTID | GLOBAL_TRANSACTION_LOCAL_ID_MODIFIER;
    }

    public void registerInterposedSynchronization(Synchronization sync) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "registerInterposedSynchronization", new Object[] { sync, this });

        getSyncs().add(sync, RegisteredSyncs.SYNC_TIER_INNER);

        if (tc.isEntryEnabled())
            Tr.exit(tc, "registerInterposedSynchronization");
    }

    public String getUOWName() {
        return getTranName();
    }

    /**
     * Tell whether this transaction has resources which are being retried.
     *
     * @return
     */
    public boolean requireRetry() {
        return _resources != null && _resources.requireRetry();
    }

    public boolean isRAImport() {
        final boolean result = _JCARecoveryData != null;

        if (tc.isDebugEnabled())
            Tr.debug(tc, "isRAImport", Boolean.valueOf(result));
        return result;
    }

    /**
     * @return
     */
    public JCARecoveryData getJCARecoveryData() {
        return _JCARecoveryData;
    }

    /**
     * @param jcard
     */
    public void setJCARecoveryData(JCARecoveryData jcard) {
        _JCARecoveryData = jcard;
    }

    public Xid getJCAXid() // @249308AA
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getJCAXid", new Object[] { this, _JCAXid });
        return _JCAXid;
    }

    @Override
    public void destroy() {
        // Decrement partner log counts
        if (_JCARecoveryData != null) {
            _JCARecoveryData.decrementCount();
        }
    }

    public void addDestroyCallback(ResourceCallback callback) {
        if (_destroyCallbacks == null) {
            _destroyCallbacks = new ArrayList<ResourceCallback>();
        }
        _destroyCallbacks.add(callback);
    }

    /**
     * Returns true if this tx was imported by the given RA and is prepared
     *
     * @param providerId
     * @return
     */
    public boolean isJCAImportedAndPrepared(String providerId) {
        return _JCARecoveryData != null &&
               _status.getState() == TransactionState.STATE_PREPARED &&
               _JCARecoveryData.getWrapper().getProviderId().equals(providerId);
    }

    public boolean hasResources() {
        return _resources != null;
    }

    public void enableLPS() {
        // LPS always enabled
    }

    public void setMostRecentThread(Thread thread) {
        _mostRecentThread = thread;
    }

    public Thread getMostRecentThread() {
        return _mostRecentThread;
    }

    public void shutdown() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "shutdown", this);

        // PM12850 makes this search unnecessary - code commented out for now, as per Dave G's agreement
        // If there are any compensation scopes registered with us, we need to
        // keep those entries in the partnerLog, even if the transaction is still
        // active and marked for rollback. This is because the cscope will have
        // recorderd the XID when registering, and will ask us for an outcome on recovery.

        // Look for enlisted CScope resources which will have logged this xid
//        for (JTAResource resource : getResources().getResourceObjects())                           // PK66133
//        {                                                                                          // PK66133
//            if (resource instanceof JTAXAResourceImpl)                                             // PK66133
//            {                                                                                      // PK66133
//                // Extract the recoveryIds from the resources in use..                             // PK66133
//                final long rmid = ((JTAXAResourceImpl)resource).getRecoveryId();                   // PK66133
//                PartnerLogData pld;                                                                // PK66133
//                if (_inRecovery)                                                                   // PK66133
//                {                                                                                  // PK66133
//                    pld = _failureScopeController.getRecoveryManager().getPartnerLogTable().findEntry(rmid);  // PK66133
//                }                                                                                  // PK66133
//                else                                                                               // PK66133
//                {                                                                                  // PK66133
//                    pld = _failureScopeController.getPartnerLogTable().findEntry(rmid);            // PK66133
//                }                                                                                  // PK66133
//                // CScopes have log early flag set                                                 // PK66133
//                if (pld != null && pld.getLogEarly())                                              // PK66133
//                {                                                                                  // PK66133
//                    // Set the recovery entry in the table to not recovered                        // PK66133
//                    pld.setRecovered(false);                                                       // PK66133
//                }                                                                                  // PK66133
//            }                                                                                      // PK66133
//        }                                                                                          // PK66133

        // PM12850 was written to handle the case where a resource has prepared, but
        // returned a failure in response to the prepare request. The rollback reuqest
        // did not complete before the server was quiesced, and because the tran was
        // marked rollbackOnly we deleted the partnerlog data. So on server restart we
        // did not call recover on the resource, so the resource was left indoubt.
        // So this change has removed the check for !rollbackOnly before preserving
        // the partnerlog data.

        // In WAS we need to.....
        preserveSuperiorCoordinator();

        // Cause the provider to remain logged if we were imported from an RA
        if (isRAImport()) {
            getJCARecoveryData().setRecovered(false);
        }

        // Cycle through all resources in case we are a pure interop case or
        // we may be an LPS case with only 1 XA Resource - so we cannot just
        // assume transactions with 1 resource will not get to the log.
        for (JTAResource resource : getResources().getResourceObjects()) {
            // If the object is a local XA resource then we need to handle it
            if (resource instanceof JTAXAResourceImpl) {
                // Extract the recoveryIds from the resources in use..
                final long rmid = ((JTAXAResourceImpl) resource).getRecoveryId();

                // Set the recovery entry in the table to not recovered
                PartnerLogData pld;
                if (_inRecovery) {
                    pld = _failureScopeController.getRecoveryManager().getPartnerLogTable().findEntry(rmid);
                } else {
                    pld = _failureScopeController.getPartnerLogTable().findEntry(rmid);
                }
                if (pld != null) {
                    pld.setRecovered(false);
                }
            } else {
                // In WAS we need to.....
                preserveSubordinateResource(resource);
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "shutdown");
    }

    protected void preserveSuperiorCoordinator() {
        // Not used in JTM
    }

    @SuppressWarnings("unused")
    protected void preserveSubordinateResource(JTAResource resource) {
        // Not used in JTM
    }

    // UOWScope interface
    @Override
    public String getTaskId() {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getTaskId", _taskId);
        return _taskId;
    }

    @Override
    public void setTaskId(String taskId) {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "setTaskId", new Object[] { taskId, this });
        _taskId = taskId;
    }

    public boolean auditSendCompletion(JTAResource resource, boolean outcome) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditSendCompletion", new Object[] { this, resource, outcome });

        boolean result = false;
        if (_inRecovery && auditRecovery && resource instanceof JTAXAResourceImpl) {
            if (outcome) {
                Tr.audit(tc, "WTRN0137_REC_TXN_COMMIT", new Object[] { _localTID, printXID(resource), resource.describe() });
            } else {
                Tr.audit(tc, "WTRN0138_REC_TXN_ROLLBACK", new Object[] { _localTID, printXID(resource), resource.describe() });
            }
            result = true;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditSendCompletion", result);
        return result;
    }

    public void auditCompletionResponse(int code, JTAResource resource, boolean outcome) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditCompletionResponse", new Object[] { this, code, resource, outcome });

        if (_inRecovery && auditRecovery && resource instanceof JTAXAResourceImpl) {
            if (outcome) {
                if (code == XAResource.XA_OK) {
                    Tr.audit(tc, "WTRN0140_REC_TXN_COMMITED", new Object[] { _localTID, printXID(resource), resource.describe() });

                } else // what about XAER_NOTA
                {
                    Tr.audit(tc, "WTRN0141_REC_TXN_COMMITERR", new Object[] { _localTID, printXID(resource), resource.describe(), XAReturnCodeHelper.convertXACode(code) });
                }
            } else {
                if (code == XAResource.XA_OK) {
                    Tr.audit(tc, "WTRN0142_REC_TXN_ROLLED", new Object[] { _localTID, printXID(resource), resource.describe() });
                } else // what about XAER_NOTA
                {
                    Tr.audit(tc, "WTRN0143_REC_TXN_ROLLEDERR", new Object[] { _localTID, printXID(resource), resource.describe(), XAReturnCodeHelper.convertXACode(code) });
                }
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditCompletionResponse");
    }

    public boolean auditSendForget(JTAResource resource) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditSendForget", new Object[] { this, resource });

        boolean result = false;
        if (_inRecovery && auditRecovery && resource instanceof JTAXAResourceImpl) {
            Tr.audit(tc, "WTRN0139_REC_TXN_FORGET", new Object[] { _localTID, printXID(resource), resource.describe() });
            result = true;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditSendForget", result);
        return result;
    }

    public void auditForgetResponse(int code, JTAResource resource) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditForgetResponse", new Object[] { this, code, resource });

        if (_inRecovery && auditRecovery && resource instanceof JTAXAResourceImpl) {
            if (code == XAResource.XA_OK) {
                Tr.audit(tc, "WTRN0144_REC_TXN_FORGOT", new Object[] { _localTID, printXID(resource), resource.describe() });
            } else // what about XAER_NOTA
            {
                Tr.audit(tc, "WTRN0145_REC_TXN_FORGETERR", new Object[] { _localTID, resource.getXID(), resource.describe(), XAReturnCodeHelper.convertXACode(code) });
            }
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditForgetResponse");
    }

    public void auditTransaction() {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "auditTransaction", this);

        if (_inRecovery && auditRecovery) {
            Tr.audit(tc, "WTRN0136_RECOVERING_TRAN", new Object[] { getTranName(), _localTID, Util.printStatus(getStatus()) });
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "auditTransaction");
    }

    private String printXID(JTAResource r) {
        return ((XidImpl) (r.getXID())).printOtid();
    }

    public TimeoutInfo setTimeoutInfo(TimeoutInfo timeoutInfo) {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "setTimeoutInfo", timeoutInfo);

        final TimeoutInfo ret = _timeoutInfo;
        _timeoutInfo = timeoutInfo;

        if (tc.isEntryEnabled())
            Tr.exit(tc, "setTimeoutInfo", ret);
        return ret;
    }

    public TimeoutInfo getTimeoutInfo() {
        return _timeoutInfo;
    }

    protected void traceCreate() {
        if (tcSummary.isDebugEnabled())
            Tr.debug(tcSummary, "Transaction created.", new Object[] { this, _xid, Util.stackToDebugString(new Throwable()) });
    }

    public static void registerTransactionScopeDestroyer(TransactionScopeDestroyer tScopeDestroyer) {
        TransactionImpl.tScopeDestroyer = tScopeDestroyer;
    }
}
