/**
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * Copyright IBM Corp. 2012,2014
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date      Origin   Description
 * -------------   ------    -------- ----------------------------------------
 * 169626.5        18-Jul-03 dcurrie  Creation 
 * 169626.6        21-Jul-03 pnickoll Updated test due to use properties on 
 *                                    core connection
 * 169626.6        21-Jul-03 dcurrie  Implemented cloneConnection and isEquivalent
 * 174369.1        11-Sep-03 dcurrie  Create ConsumerSession
 * 173765.6        25-Sep-03 djhoward Add additional inherited methods 
 *                                    based on 173765 core changes.
 * 174369.8        07-Oct-03 djhoward Remove APILevel call as part of unit testing
 * 174369.10       21-Oct-03 djhoward Continue MDB testing, getSIXAResource now 
 *                                    returns a new SIXAResourceStub instance.
 * 181796.6        05-Nov-03 djhoward Core SPI move to com.ibm.wsspi.sib.core
 * 184117          28-Nov-03 pnickoll Added the getDestinationConfiguration method
 *                                    from the SICoreConnection interface
 * 186193          23-Dec-03 dcurrie  Added new SICoreConnection methods
 * 182639          20-Jan-04 dcurrie  Added getMeUuid
 * 188161          23-Jan-04 dcurrie  Add new methods with Reliability
 * 188358          26-Jan-04 sambo    The unrecoverableReliability parameter has been moved
 * 190483          12-Feb-04 dcurrie  Added new createConsumerSession method
 * 192759.4        12-Mar-04 djhoward Added new methods from interface
 * 192759.7        15-Mar-04 djhoward Updated new methods to return stub objects
 * 188050.2        05-Apr-04 dcurrie  Add Synchronization support
 * 195758.4        08-Apr-04 pnickoll Updated with new interface
 * 199336          20-Apr-04 pnickoll Updated with new interface (removed getDestinationConfiguration method)
 * 199220          20-Apr-04 pnickoll Removed methods that are no longer in the interface and corrected javadoc
 * 192474.1        20-Apr-04 pnickoll Removed extra methods methods
 * 193585.3.4      28-Apr-04 pnickoll Change DestinationFilter's back to use DestinationType's instead
 * 184312.4.3      28-Apr-04 pnickoll Add method sendToExceptionDestination
 * 201476.3        06-May-04 pnickoll Changed sendToExceptionDestination signature
 * 209436.3        22-Jun-04 pnickoll Milestone 8 Core SPI updates
 * 201972.4        28-Jul-04 pnickoll Update core SPI exceptions
 * 219476.4        31-Aug-04 dcurrie  Z3 core SPI updates
 * LIDB3684.11.1.4 30-Mar-05 pnickoll Added checkMessagingRequired method
 * SIB0009.ra.01   17-Aug-05 pnickoll Added invokeCommand
 * 313337.2        20-Oct-05 pnickoll Added overridden createUncoordinatedTransaction method
 * 352706          06-Mar-06 ajw      Added new createConsumerSession method
 * 377093.3        12-Jul-06 pnickoll Added new overridden invokeCommand method 
 * SIB0137.ut.1    23-May-07 nyoung   addDestinationListener promoted to Core SPI       
 * SIB0113.ra.1    26-Sep-07 jamessid SICoreConnection interface has changed; updating stub accordingly
 * SIB0163.ra.1    29-Oct-07 nyoung   Add new parameter on createConsumerSession for Message Control
 * PM39926         31-May-11 anil5498 Provide mechanism to force close connection and avoid resetting (during connection destroy)
 * F011127            280611 chetbhat registerConsumerSetMonitor support
 * ============================================================================
 */

package com.ibm.ws.sib.api.jmsra.stubs;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.transaction.Synchronization;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.BifurcatedConsumerSession;
import com.ibm.wsspi.sib.core.BrowserSession;
import com.ibm.wsspi.sib.core.ConsumerSession;
import com.ibm.wsspi.sib.core.ConsumerSetChangeCallback;
import com.ibm.wsspi.sib.core.DestinationAvailability;
import com.ibm.wsspi.sib.core.DestinationListener;
import com.ibm.wsspi.sib.core.DestinationType;
import com.ibm.wsspi.sib.core.Distribution;
import com.ibm.wsspi.sib.core.OrderingContext;
import com.ibm.wsspi.sib.core.ProducerSession;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SICoreConnectionListener;
import com.ibm.wsspi.sib.core.SITransaction;
import com.ibm.wsspi.sib.core.SIUncoordinatedTransaction;
import com.ibm.wsspi.sib.core.SIXAResource;
import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.wsspi.sib.core.exception.SICommandInvocationFailedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SIDestinationLockedException;
import com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException;
import com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionAlreadyExistsException;
import com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionMismatchException;
import com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionNotFoundException;
import com.ibm.wsspi.sib.core.exception.SILimitExceededException;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;
import com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException;

/**
 * Stub class for SICoreConnection.
 */
public class SICoreConnectionStub implements SICoreConnection, Cloneable {

    private Subject _subject = null;

    private String _userName = null;

    private String _password = null;

    private Map _properties = null;

    private final Set _listeners = new HashSet();

    private boolean _remote = false;

    private static Set _connections = new HashSet();

    /**
     * Identifier shared by cloned connections and used to implement
     * equivalence.
     */
    private final int _id;

    /** Identifier used for next connection. */
    private static int _nextId = 0;

    /**
     * Constructor.
     * 
     * @param subject
     *            the subject associated with this connection
     * @param props
     *            the properties passed on creation of this connection
     */
    public SICoreConnectionStub(Subject subject, Map props) {
        this._subject = subject;
        this._properties = props;
        this._id = _nextId++;
        _connections.add(this);
    }

    /**
     * Constructor.
     * 
     * @param user
     *            the user name associated with this connection
     * @param password
     *            the password associated with this connection
     * @param props
     *            the properties passed on creation of this connection
     */
    public SICoreConnectionStub(String user, String password, Map props) {
        this._userName = user;
        this._password = password;
        this._properties = props;
        this._id = _nextId++;
        _connections.add(this);
    }

    /**
     * Constructor.
     * 
     * @param subject
     *            the subject associated with this connection
     * @param props
     *            the properties passed on creation of this connection
     * @param remote
     *            flag indicating whether the connection is remote
     */
    public SICoreConnectionStub(Subject subject, Map props, boolean remote) {
        this._subject = subject;
        this._properties = props;
        this._id = _nextId++;
        this._remote = remote;
        _connections.add(this);
    }

    /**
     * Constructor.
     * 
     * @param user
     *            the user name associated with this connection
     * @param password
     *            the password associated with this connection
     * @param props
     *            the properties passed on creation of this connection
     * @param remote
     *            flag indicating whether the connection is remote
     */
    public SICoreConnectionStub(String user, String password, Map props,
                                boolean remote) {
        this._userName = user;
        this._password = password;
        this._properties = props;
        this._id = _nextId++;
        this._remote = remote;
        _connections.add(this);
    }

    /**
     * Constructor used when cloning a connection.
     * 
     * @param userName
     *            the user name
     * @param password
     *            the password
     * @param subject
     *            the subject
     * @param properties
     *            the properties
     * @param id
     *            the identifier
     */
    private SICoreConnectionStub(String userName, String password,
                                 Subject subject, Map properties, int id) {
        this._userName = userName;
        this._password = password;
        this._subject = subject;
        this._properties = properties;
        this._id = id;
        _connections.add(this);
    }

    @Override
    public void close() {
        _connections.remove(this);
    }

    @Override
    public void close(boolean bForceFlag) {
        close();
    }

    @Override
    public SIUncoordinatedTransaction createUncoordinatedTransaction() {
        SIUncoordinatedTransaction uncoordinatedTransaction;
        if (_remote) {
            uncoordinatedTransaction = new RemoteSIUncoordinatedTransaction();
        } else {
            uncoordinatedTransaction = new LocalSIUncoordinatedTransaction();
        }
        return uncoordinatedTransaction;
    }

    @Override
    public SIUncoordinatedTransaction createUncoordinatedTransaction(boolean allowSubs) {
        SIUncoordinatedTransaction uncoordinatedTransaction;
        if (_remote) {
            uncoordinatedTransaction = new RemoteSIUncoordinatedTransaction();
        } else {
            uncoordinatedTransaction = new LocalSIUncoordinatedTransaction();
        }
        return uncoordinatedTransaction;
    }

    @Override
    public SIXAResource getSIXAResource() {
        return new SIXAResourceStub();
    }

    /**
     * Clones this connection to return a connection with the same properties
     * and that will return true when compared with this connection using
     * isEquivalentTo.
     * 
     * @return the clone
     */
    @Override
    public SICoreConnection cloneConnection() {
        return new SICoreConnectionStub(_userName, _password, _subject,
                        _properties, _id);
    }

    /**
     * Compares this connection with the connection passed in.
     * 
     * @param other
     *            the connection to compare with
     * @return <code>true</code> iff the connections are equivalent i.e. one
     *         was created as a clone of the other
     */
    @Override
    public boolean isEquivalentTo(SICoreConnection other) {
        boolean equal = false;

        if (other instanceof SICoreConnectionStub) {
            equal = (getId() == ((SICoreConnectionStub) other).getId());
        }

        return equal;
    }

    @Override
    public String getMeName() {
        return null;
    }

    @Override
    public void addConnectionListener(SICoreConnectionListener listener) {
        _listeners.add(listener);
    }

    @Override
    public void removeConnectionListener(SICoreConnectionListener listener) {
        _listeners.remove(listener);
    }

    @Override
    public SICoreConnectionListener[] getConnectionListeners() {
        return (SICoreConnectionListener[]) _listeners
                        .toArray(new SICoreConnectionListener[] {});
    }

    @Override
    public String getApiLevelDescription() {
        return null;
    }

    /**
     * Returns the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return _password;
    }

    /**
     * Returns the properties.
     * 
     * @return the set of properties
     */
    public Map getProperties() {
        return _properties;
    }

    /**
     * Returns the subject.
     * 
     * @return the subject ubject
     */
    public Subject getSubject() {
        return _subject;
    }

    /**
     * Returns the user name.
     * 
     * @return the user name
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * Returns the id.
     * 
     * @return the id
     */
    public int getId() {
        return _id;
    }

    /**
     * Returns the set of all connection instances.
     * 
     * @return the connections
     */
    public static Set getConnections() {
        return _connections;
    }

    /**
     * Sends a comms failure event to all the connection listeners with the
     * given exception.
     * 
     * @param exc
     */
    public void sendCommsFailure(SIConnectionLostException exc) {
        for (Iterator iterator = _listeners.iterator(); iterator.hasNext();) {
            SICoreConnectionListener listener = (SICoreConnectionListener) iterator
                            .next();
            listener.commsFailure(this, exc);
        }
    }

    /**
     * Sends an ME quiescing event to all the connection listeners.
     */
    public void sendMeQuiescing() {
        for (Iterator iterator = _listeners.iterator(); iterator.hasNext();) {
            SICoreConnectionListener listener = (SICoreConnectionListener) iterator
                            .next();
            listener.meQuiescing(this);
        }
    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createUniqueId()
     */
    @Override
    public byte[] createUniqueId() {
        return null;
    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#getApiMajorVersion()
     */
    @Override
    public long getApiMajorVersion() {
        return 0;
    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#getApiMinorVersion()
     */
    @Override
    public long getApiMinorVersion() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#getMeUuid()
     */
    @Override
    public String getMeUuid() {
        return null;
    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createOrderingContext()
     */
    @Override
    public OrderingContext createOrderingContext() {
        return null;
    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#deleteDurableSubscription(String, String)
     */
    @Override
    public void deleteDurableSubscription(String arg0, String arg1) {

        // Do nothing

    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#deleteTemporaryDestination(SIDestinationAddress)
     */
    @Override
    public void deleteTemporaryDestination(SIDestinationAddress arg0) {

        // Do nothing

    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#getResolvedUserid()
     */
    @Override
    public String getResolvedUserid() {
        return null;
    }

    private class RemoteSIUncoordinatedTransaction implements
                    SIUncoordinatedTransaction {

        @Override
        public void commit() {

            // Do nothing

        }

        @Override
        public void rollback() {

            // Do nothing

        }

    }

    private class LocalSIUncoordinatedTransaction extends
                    RemoteSIUncoordinatedTransaction implements Synchronization {

        @Override
        public void beforeCompletion() {

            // Do nothing

        }

        @Override
        public void afterCompletion(int status) {

            // Do nothing

        }

    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createBifurcatedConsumerSession(long)
     */
    @Override
    public BifurcatedConsumerSession createBifurcatedConsumerSession(long arg0) {
        return null;
    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#getDestinationConfiguration(SIDestinationAddress)
     */
    @Override
    public com.ibm.wsspi.sib.core.DestinationConfiguration getDestinationConfiguration(
                                                                                       SIDestinationAddress arg0) {
        return null;
    }

    /**
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createTemporaryDestination(Distribution, String)
     */
    @Override
    public SIDestinationAddress createTemporaryDestination(
                                                           com.ibm.wsspi.sib.core.Distribution arg0, String arg1) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createDurableSubscription(java.lang.String,
     * java.lang.String, com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.SelectionCriteria, boolean, boolean,
     * java.lang.String)
     */
    @Override
    public void createDurableSubscription(String subscriptionName,
                                          String durableSubscriptionHome,
                                          SIDestinationAddress destinationAddress,
                                          SelectionCriteria criteria, boolean supportsMultipleConsumers,
                                          boolean nolocal, String alternateUser)
                    throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SINotPossibleInCurrentConfigurationException,
                    SIDurableSubscriptionAlreadyExistsException {

        // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createConsumerSessionForDurableSubscription(java.lang.String,
     * java.lang.String, com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.SelectionCriteria, boolean, boolean,
     * com.ibm.websphere.sib.Reliability, boolean,
     * com.ibm.websphere.sib.Reliability, boolean, java.lang.String)
     */
    @Override
    public ConsumerSession createConsumerSessionForDurableSubscription(
                                                                       String subscriptionName, String durableSubscriptionHome,
                                                                       SIDestinationAddress destinationAddress,
                                                                       SelectionCriteria criteria, boolean supportsMultipleConsumers,
                                                                       boolean nolocal, Reliability reliability, boolean enableReadAhead,
                                                                       Reliability unrecoverableReliability, boolean bifurcatable,
                                                                       String alternateUser) throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SIDurableSubscriptionNotFoundException,
                    SIDurableSubscriptionMismatchException,
                    SIDestinationLockedException {

        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createProducerSession(com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.OrderingContext, java.lang.String)
     */
    @Override
    public ProducerSession createProducerSession(SIDestinationAddress destAddr,
                                                 DestinationType destType,
                                                 OrderingContext extendedMessageOrderingContext, String alternateUser)
                    throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SINotPossibleInCurrentConfigurationException,
                    SITemporaryDestinationNotFoundException, SIIncorrectCallException {

        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createProducerSession(com.ibm.websphere.sib.SIDestinationAddress,
     * java.lang.String, com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.OrderingContext, java.lang.String)
     */
    @Override
    public ProducerSession createProducerSession(SIDestinationAddress destAddr,
                                                 String discriminator, DestinationType destType,
                                                 OrderingContext extendedMessageOrderingContext, String alternateUser)
                    throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SINotPossibleInCurrentConfigurationException,
                    SITemporaryDestinationNotFoundException, SIIncorrectCallException,
                    SIDiscriminatorSyntaxException {

        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createProducerSession(
     * com.ibm.websphere.sib.SIDestinationAddress, java.lang.String,
     * com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.OrderingContext,
     * java.lang.String, boolean, boolean)
     */
    @Override
    public ProducerSession createProducerSession(
                                                 SIDestinationAddress destAddr, String discriminator,
                                                 DestinationType destType, OrderingContext extendedMessageOrderingContext,
                                                 String alternateUser, boolean fixedMessagePoint,
                                                 boolean preferLocalMessagePoint)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SINotPossibleInCurrentConfigurationException,
                    SITemporaryDestinationNotFoundException, SIIncorrectCallException,
                    SIDiscriminatorSyntaxException
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createConsumerSession(com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.SelectionCriteria,
     * com.ibm.websphere.sib.Reliability, boolean, boolean,
     * com.ibm.websphere.sib.Reliability, boolean, java.lang.String)
     */
    @Override
    public ConsumerSession createConsumerSession(SIDestinationAddress destAddr,
                                                 DestinationType destType, SelectionCriteria criteria,
                                                 Reliability reliability, boolean enableReadAhead, boolean nolocal,
                                                 Reliability unrecoverableReliability, boolean bifurcatable,
                                                 String alternateUser) throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException {

        return new ConsumerSessionStub();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createConsumerSession(
     * com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.SelectionCriteria,
     * com.ibm.websphere.sib.Reliability,
     * boolean, boolean, com.ibm.websphere.sib.Reliability,
     * boolean, java.lang.String, boolean, boolean)
     */
    @Override
    public ConsumerSession createConsumerSession(
                                                 SIDestinationAddress destAddr,
                                                 DestinationType destType, SelectionCriteria criteria,
                                                 Reliability reliability,
                                                 boolean enableReadAhead, boolean nolocal,
                                                 Reliability unrecoverableReliability, boolean bifurcatable,
                                                 String alternateUser, boolean ignoreInitialIndoubts,
                                                 boolean allowMessageGathering,
                                                 Map<String, String> messageControlProperties)
                    throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SIDestinationLockedException, SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#send(com.ibm.wsspi.sib.core.SIBusMessage,
     * com.ibm.wsspi.sib.core.SITransaction,
     * com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.OrderingContext, java.lang.String)
     */
    @Override
    public void send(SIBusMessage msg, SITransaction tran,
                     SIDestinationAddress destAddr, DestinationType destType,
                     OrderingContext extendedMessageOrderingContext, String alternateUser)
                    throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException {

        // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#receiveNoWait(com.ibm.wsspi.sib.core.SITransaction,
     * com.ibm.websphere.sib.Reliability,
     * com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.SelectionCriteria,
     * com.ibm.websphere.sib.Reliability, java.lang.String)
     */
    @Override
    public SIBusMessage receiveNoWait(SITransaction tran,
                                      Reliability unrecoverableReliability,
                                      SIDestinationAddress destAddr, DestinationType destType,
                                      SelectionCriteria criteria, Reliability reliability,
                                      String alternateUser) throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException {

        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#receiveWithWait(com.ibm.wsspi.sib.core.SITransaction,
     * com.ibm.websphere.sib.Reliability,
     * com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.SelectionCriteria,
     * com.ibm.websphere.sib.Reliability, long, java.lang.String)
     */
    @Override
    public SIBusMessage receiveWithWait(SITransaction tran,
                                        Reliability unrecoverableReliability,
                                        SIDestinationAddress destAddr, DestinationType destType,
                                        SelectionCriteria criteria, Reliability reliability, long timeout,
                                        String alternateUser) throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException {

        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createBrowserSession(com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.SelectionCriteria, java.lang.String)
     */
    @Override
    public BrowserSession createBrowserSession(
                                               SIDestinationAddress destinationAddress, DestinationType destType,
                                               SelectionCriteria criteria, String alternateUser)
                    throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException {

        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createBrowserSession(
     * com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.SelectionCriteria,
     * java.lang.String, boolean)
     */
    @Override
    public BrowserSession createBrowserSession(
                                               SIDestinationAddress destinationAddress,
                                               DestinationType destType, SelectionCriteria criteria,
                                               String alternateUser, boolean allowMessageGathering)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#sendToExceptionDestination(com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.wsspi.sib.core.SIBusMessage, int, java.lang.String[],
     * com.ibm.wsspi.sib.core.SITransaction, java.lang.String)
     */
    @Override
    public void sendToExceptionDestination(SIDestinationAddress address,
                                           SIBusMessage message, int reason, String[] inserts,
                                           SITransaction tran, String alternateUser)
                    throws SIConnectionUnavailableException,
                    SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException, SIIncorrectCallException,
                    SINotPossibleInCurrentConfigurationException {

        // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#checkMessagingRequired(com.ibm.websphere.sib.SIDestinationAddress,
     * com.ibm.websphere.sib.SIDestinationAddress, com.ibm.wsspi.sib.core.DestinationType,
     * java.lang.String)
     */
    @Override
    public SIDestinationAddress checkMessagingRequired(
                                                       SIDestinationAddress requestDestAddr,
                                                       SIDestinationAddress replyDestAddr,
                                                       DestinationType destinationType, String alternateUser)

    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#invokeCommand(java.lang.String, java.lang.String, java.io.Serializable)
     */
    @Override
    public Serializable invokeCommand(String key, String commandName, Serializable commandData)
                    throws SIConnectionDroppedException, SIConnectionUnavailableException, SIResourceException,
                    SIIncorrectCallException, SINotAuthorizedException, SICommandInvocationFailedException {

        return null;

    }

    @Override
    public ConsumerSession createConsumerSession(SIDestinationAddress destAddr,
                                                 DestinationType destType, SelectionCriteria criteria, Reliability reliability,
                                                 boolean enableReadAhead, boolean nolocal, Reliability unrecoverableReliability,
                                                 boolean bifurcatable, String alternateUser, boolean ignoreInitialIndoubts)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException, SIResourceException,
                    SIConnectionLostException, SILimitExceededException, SINotAuthorizedException,
                    SIIncorrectCallException, SIDestinationLockedException, SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException
    {
        return new ConsumerSessionStub();
    }

/*
 * (non-Javadoc)
 * 
 * @see com.ibm.wsspi.sib.core.SICoreConnection#invokeCommand(java.lang.String, java.lang.String, java.io.Serializable, com.ibm.wsspi.sib.core.SITransaction)
 */
    @Override
    public Serializable invokeCommand(String key, String commandName, Serializable commandData, SITransaction transaction) throws SIConnectionDroppedException, SIConnectionUnavailableException, SIResourceException, SIIncorrectCallException, SINotAuthorizedException, SICommandInvocationFailedException
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#addDestinationListener(java.lang.String, com.ibm.wsspi.sib.core.DestinationListener, com.ibm.wsspi.sib.core.DestinationType,
     * com.ibm.wsspi.sib.core.DestinationAvailability)
     */
    @Override
    public SIDestinationAddress[] addDestinationListener(String destinationNamePattern, DestinationListener destinationListener, DestinationType destinationType,
                                                         DestinationAvailability destinationAvailability) throws SIIncorrectCallException, SICommandInvocationFailedException, SIConnectionUnavailableException
    {
        return null;
    }

    @Override
    public boolean registerConsumerSetMonitor(
                                              SIDestinationAddress destinationAddress,
                                              String discriminatorExpression, ConsumerSetChangeCallback callback)
                    throws SIResourceException,
                    SINotPossibleInCurrentConfigurationException,
                    SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIIncorrectCallException, SICommandInvocationFailedException {
        return false;
    }

/*
 * (non-Javadoc)
 * 
 * @see com.ibm.wsspi.sib.core.SICoreConnection#createSharedConsumerSession(java.lang.String, com.ibm.websphere.sib.SIDestinationAddress, com.ibm.wsspi.sib.core.DestinationType,
 * com.ibm.wsspi.sib.core.SelectionCriteria, com.ibm.websphere.sib.Reliability, boolean, boolean, boolean, com.ibm.websphere.sib.Reliability, boolean, java.lang.String, boolean,
 * boolean, java.util.Map)
 */
    @Override
    public ConsumerSession createSharedConsumerSession(String subscriptionName, SIDestinationAddress destAddr, DestinationType destType, SelectionCriteria criteria,
                                                       Reliability reliability, boolean enableReadAhead, boolean supportsMultipleConsumers, boolean nolocal,
                                                       Reliability unrecoverableReliability, boolean bifurcatable, String alternateUser, boolean ignoreInitialIndoubts,
                                                       boolean allowMessageGathering, Map<String, String> messageControlProperties) throws SIConnectionUnavailableException, SIConnectionDroppedException, SIResourceException, SIConnectionLostException, SILimitExceededException, SINotAuthorizedException, SIIncorrectCallException, SIDestinationLockedException, SITemporaryDestinationNotFoundException, SINotPossibleInCurrentConfigurationException {
        // TODO Auto-generated method stub
        return null;
    }

}
