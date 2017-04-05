/*
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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * 159093           070303 jroots   Original
 * 161403           200303 tevans   Modify to cope with merged Synch & Asych Sessions
 * 162915           110403 tevans   Upgrade to 0.3a model
 * 163636           110403 tevans   Upgrade to 0.4 model
 * 166828           060603 tevans   Core MP rewrite
 * 169897.0         240603 jroots   Update for Milestone 3 Core API
 * 173765.0         080803 jroots   Added getApiMajor/MinorVersion methods
 * 173765.0         110803 jroots   Added matching exceptions
 * 173765.0         110803 jroots   Added missing params to createDurableSub
 * 173765.0         130803 jroots   Added SIObjectClosedException to cloneConnection
 * 173765.0         210803 jroots   Removal of recoverableExpress option
 * 173765.0         030903 jroots   Added createUniqueId
 * 173765.0         030903 jroots   Corrected Javadoc of close wrt exceptions
 * 173765.0         030903 jroots   Corrections to connectionListener methods
 * 177817           141003 jroots   Minor javadoc corrections
 * 179742           141003 jroots   Corrected deleteDurableSubscription sig
 * 179519           031103 jroots   Added SIDestinationWrongTypeException
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 179629.2.1       241103 jroots   Added getDestinationConfiguration
 * 180540.0         111203 jroots   Added support for SIDestinationAddress
 * 184433.1         171203 cwilkin  document invalid noLocal/cloned combination
 * 181718.4.1       221203 jroots   createUniqueId returns 12 bytes not 16
 * 182639.1         190104 gatfora  Adding getMeUuid method.
 * 187521           200104 dware    Move unrecoverableReliability to createConsumerSession
 * 188197           070204 mcobbett Added details of how to validate destination prefixes
 * 176658.3.6       110204 jroots   Added destination address version of createConsumerSession
 * 192759           090304 jroots   Milestone 7 Core SPI changes
 * 195758.0         050404 jroots   Milestone 7.5 Core SPI changes
 * 193585.3         220404 jroots   Remove DestinationFilter from the Core SPI
 * 184312.4         270404 jroots   Add sendToExceptionDestination method
 * 201476           040504 jroots   Fix sendToExceptionDestination method
 * 209436.0         150604 jroots   Milestone 8+ Core SPI changes
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite
 * 223986           170804 gatfora  Removal of SIErrorExceptions from method throws declarations
 * 199140           180804 gatfora  Cleanup javadoc
 * 219476.0         240804 jroots   Consolidated Z3 Core SPI changes
 * LIDB3684.11.1.1  210305 dware    Add checkMessagingRequired() method
 * 276259           130505 dware    Improve security related javadoc
 * SIB0009.core.01  150805 rjnorris Add invokeCommand() to CoreConnection
 * 299430           230805 nyoung   Change security javadoc on checkMessagingRequired
 * 305051           140905 dware    Improve security javadoc for createConsumerSessionForDurableSubscription
 * 296067.1         190905 nyoung   Remove reply dest receive security check for checkMessagingRequired.
 * 313337           131005 tevans   overload createUncoordinatedTransaction
 * 351339           010305 dware    Add ignoreInitialIndoubts option
 * 377093           110706 tpm      Transactional invokeCommand method
 * SIB0137.core.1   230507 nyoung   addDestinationListener promoted to Core SPI
 * SIB0113.core.1   240907 dware    Add SIB0113 methods for message control within a clustered destination
 * SIB0137.comms.2  250907 vaughton Add comms exceptions to addDestinationListener
 * SIB0163.core.1   241007 nyoung   Add new parameter to createConsumerSession for Message Control
 * 467999           141107 dware    Improve javadoc
 * 477450           231107 dware    Correct javadoc
 * SIB0163.mp.7     040108 nyoung   Reserve new Msg Control createConsumerSession parameter for future use
 * 569303           181208 dware    Fix javadoc for V7 common criteria
 * PM39926          310511 anil5498 Provide mechanism to force close connection and avoid resetting (during connection destroy) 
 * F011127          280611 chetbhat Adding registerConsumerSetMonitor 
 * ============================================================================
 */

package com.ibm.wsspi.sib.core;

import java.io.Serializable;
import java.util.Map;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.exception.SICommandInvocationFailedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SIDestinationLockedException;
import com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException;
import com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionAlreadyExistsException;
import com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionMismatchException;
import com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionNotFoundException;
import com.ibm.wsspi.sib.core.exception.SIInvalidDestinationPrefixException;
import com.ibm.wsspi.sib.core.exception.SILimitExceededException;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException;

/**
 * A "connection" represents a logical session between an application and a
 * messaging engine. It is not in one-to-one correspondence with physical
 * resources at the network layer.
 * <p>
 * An SICoreConnection will always have an authenticated user assigned to it, defined
 * at the time it was created (see SICoreConnectionFactory for details). This user is
 * used for authorization checks when appropriate unless specifically overridden
 * (see below).
 * <p>
 * Unless otherwise specified each method of this class has no security implications.
 */
public interface SICoreConnection {

    /**
     * Returns a unique id, created by the Messaging Engine. This may be used by the
     * application layer to generate, for example, unique "application" message ids,
     * with the value returned by this call used as the high order bits, and the low
     * order bits generated using a sequence number. This is preferable to message
     * ids being generated by the Messaging Engine on each send call, since that
     * would mean that each remote client send would require a return flow to make
     * the id available to the sending application. This approach also provides
     * flexibility in that the API layer can choose to associate unique stem ids
     * with objects not present in the Core API; for example, in the JMS case,
     * one stem per JMS Session.
     * 
     * @return an array of twelve bytes providing a unique identifier
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     */
    public byte[] createUniqueId()
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException;

    /**
     * Creates a temporary destination, that is, a destination whose lifetime is
     * scoped to that of the connection used to create it. The caller must indicate
     * via the distribution parameter whether a Queue or a TopicSpace is to be
     * created.
     * <p>
     * Optionally, the first part of the destination name may be specified
     * using the destinationPrefix parameter. Up to twelve
     * ({@link com.ibm.wsspi.sib.core.SICoreUtils#DESTINATION_PREFIX_MAX_LENGTH})
     * characters are permitted.
     * All characters within the destinationPrefix must be within a restricted set:
     * <ul>
     * <li>a-z (lower-case alphas)</li>
     * <li>A-Z (upper-case alphas)</li>
     * <li>0-9 (numerics)</li>
     * <li>. (period)</li>
     * <li>/ (slash)</li>
     * <li>% (percent)</li>
     * </ul>
     * <p>
     * Note that all messages sent to a temporary destination are non-recoverable,
     * irrespective of their nominal QualityOfService.
     * <p>
     * If running in a secure bus the connection's user must be assigned the Creator
     * role on the Prefix Destination (as identified by the destinationPrefix) to be
     * able to create a temporary destination with the specified prefix, otherwise
     * an SINotAuthorizedException will be thrown.
     * 
     * @param distribution indicates whether a Queue or TopicSpace should be created
     * @param destinationPrefix the first part of the destination's name. null is
     *            equivalent to an empty string prefix
     * @return an SIDestinationAddress representing the address of the new destination
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIInvalidDestinationPrefixException
     * @see com.ibm.wsspi.sib.core.SICoreUtils#DESTINATION_PREFIX_MAX_LENGTH
     * @see com.ibm.wsspi.sib.core.SICoreUtils#isDestinationPrefixValid
     */
    public SIDestinationAddress createTemporaryDestination(
                                                           Distribution distribution,
                                                           String destinationPrefix)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIInvalidDestinationPrefixException;

    /**
     * Deletes a destination created with SICoreConnection.createTemporaryDestination
     * from this SICoreConnection.
     * <p>
     * If the temporary destination still has consumers attached, then
     * SIDestinationLockedException is thrown. If the destination does not have
     * consumers attached, but has messages on it, then these messages are
     * discarded. (Note that these statements do not necessarily hold true for the
     * administrative deletion of non-temporary destinations.)
     * <p>
     * When deleteTemporaryDestination is called , it is <em>not</em>, in general,
     * possible for the bus to ensure that there are no producers still attached,
     * and so this does not cause SIDestinationLockedException to be thrown. After
     * some interval, the attached ProducerSessions will be closed; messages sent
     * between the deletion of the destination and the closing of the consumers will
     * be sent to an exception destination.
     * <p>
     * The possibility of an SINotAuthorizedException being thrown by this method has
     * been removed, there are no security implications with this method.
     * 
     * @param destAddr the address of the temporary destination to be deleted
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.websphere.sib.exception.SIDestinationLockedException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     */
    public void deleteTemporaryDestination(SIDestinationAddress destAddr)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException,
                    SINotAuthorizedException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SIIncorrectCallException;

    /**
     * Creates a new durable subscription with the name specified, on the ME
     * specified. If a durable subscription with the given name already exists on
     * the ME (whether for the same or a different TopicSpace), then
     * SIDurableSubscriptionAlreadyExistsException is thrown.
     * <p>
     * If the destination cannot be found, or is not a permanent TopicSpace,
     * then SINotPossibleInCurrentConfigurationException is thrown.
     * <p>
     * Cloned subscribers cannot be used in conjunction with noLocal behaviour, I.e.
     * it is not possible for the supportsMultipleConsumer and noLocal parameters to
     * both be true. If they are both set to true, SIIncorrectCallException
     * is thrown.
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that the
     * the DurableSubscription should be created using a different user to that
     * associated with the connection. In order to do this, the user owning the
     * connection must have IdentityAdopter authority, if they do not an
     * SINotAuthorizedException will be thrown. Otherwise the alternateUser will
     * be used in all subsequent authorisation checks performed for this subscription.
     * <p>
     * The specified user must be assigned the Receiver role on the destination
     * (as specified by destinationAddress) to be able to create a subscription, if not
     * the call will fail and an SINotAuthorizedException will be thrown.
     * <p>
     * If the Discriminator specified in the SelectionCriteria is not a wildcard
     * discriminator the user's authority will be checked against that discriminator at
     * this time, if they have not been assigned the Receiver role for this discriminator
     * this call will fail and an SINotAuthorizedException will be thrown. If the
     * discriminator is a wildcard discriminator, authorisation checks will be performed
     * on each potential message matching this subscription, if the user is not
     * authorized to receive any of these messages (as defined by their discriminator)
     * they will not be assigned to the subscription. No error will be reported in this
     * situation.
     * 
     * @param subscriptionName the name of the durable subscription to be created
     * @param durableSubscriptionHome the name of the ME on which the durable
     *            subscription is to be created
     * @param destinationAddress the address of the destination to be subscribed to
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param supportsMultipleConsumers indicates whether multiple consumers may
     *            attach to this subscription at the same time
     * @param nolocal true if the ConsumerSession should not be delivered messages
     *            from producers associated with this connection
     * @param alternateUser the name of the user used to create the durable
     *            subscription (may be null)
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionAlreadyExistsException
     */
    public void createDurableSubscription(
                                          String subscriptionName,
                                          String durableSubscriptionHome,
                                          SIDestinationAddress destinationAddress,
                                          SelectionCriteria criteria,
                                          boolean supportsMultipleConsumers,
                                          boolean nolocal,
                                          String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SINotPossibleInCurrentConfigurationException,
                    SIDurableSubscriptionAlreadyExistsException;

    /**
     * The createConsumerSessionForDurableSubscription method is used to create
     * a new ConsumerSession attached to an existing DurableSubscription. The
     * subscription is identified by its name, scoped by the ME on which it is
     * located (as identified by the durableSubscriptionHome parameter). If no
     * subscription is found then SIDurableSubscriptionNotFound exception is thrown.
     * If a subscription is found, then its properties are compared against the
     * destinationAdress, discriminator, selector, supportsMultipleconsumers, and
     * nolocal parameters to the method call. If they match then a new
     * ConsumerSession is created and returned. If there is a mismatch, then
     * SIDurableSubscriptionMismatchException is thrown.
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that
     * the ConsumerSession should be created using a different user to that
     * associated with the connection. In order to do this, the user owning the
     * connection must have IdentityAdopter authority, if they do not the existing
     * connection userid will be used. Otherwise the alternateUser will
     * be used in all subsequent user related checks performed for this method.
     * <p>
     * If security is enabled the user that the subscription was created with
     * must match this connection's user (or the one specified as the alternateUser
     * if supplied). If the user does not match an SIDurableSubscriptionMismatchException
     * is thrown
     * <p>
     * The possibility of an SINotAuthorizedException being thrown by this method has
     * been removed.
     * <p>
     * In other respects, the parameter and exception are similar to those of
     * the createConsumerSession method.
     * 
     * @param subscriptionName the name of the durable subscription
     * @param durableSubscriptionHome the name of the ME on which the durable
     *            subscription is to be found
     * @param destinationAddress the address of the destination to be subscribed to
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param supportsMultipleConsumers indicates whether multiple consumers may
     *            attach to this subscription at the same time
     * @param nolocal true if the ConsumerSession should not be delivered messages
     *            from producers associated with this connection
     * @param reliability the reliability with which the consumer wishes to receive
     *            messages (may be null
     * @param enableReadAhead indicates whether messages should be streamed to this
     *            consumer
     * @param unrecoverableReliability the reliability level that will be
     *            unrecoverable for messages delivered to this asynch consumer
     * @param bifurcatable whether the new ConsumerSession may be bifurcated
     * @param alternateUser the name of the user under whose authority operations
     *            of the ConsumerSession should be performed (may be null)
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionNotFoundException
     * @throws com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionMismatchException
     * @throws com.ibm.wsspi.sib.core.exception.SIDestinationLockedException
     */
    public ConsumerSession createConsumerSessionForDurableSubscription(
                                                                       String subscriptionName,
                                                                       String durableSubscriptionHome,
                                                                       SIDestinationAddress destinationAddress,
                                                                       SelectionCriteria criteria,
                                                                       boolean supportsMultipleConsumers,
                                                                       boolean nolocal,
                                                                       Reliability reliability,
                                                                       boolean enableReadAhead,
                                                                       Reliability unrecoverableReliability,
                                                                       boolean bifurcatable,
                                                                       String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SIDurableSubscriptionNotFoundException, SIDurableSubscriptionMismatchException,
                    SIDestinationLockedException;

    /**
     * Deletes a durable subscription that was created using
     * createDurableSubscription. Note that only durable subscriptions so created
     * can be deleted with this method; durable subscriptions that were
     * administratively defined must be administratively deleted (at least for the
     * time being). Note that a durable subscription that has active consumers
     * cannot be deleted; SIDestinationLockedException is thrown.
     * <p>
     * If running in a secure bus a durable subscription can only be deleted by the
     * user that the subscription was created under (as specified at
     * createDurableSubscription time), if this connection's user is not the same
     * the subscription will not be deleted and an SINotAuthorizedException will
     * be thrown.
     * 
     * @param subscriptionName the name of the durable subscription to be deleted
     * @param durableSubscriptionHome the name of the ME on which the durable
     *            subscription is located
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDurableSubscriptionNotFoundException
     * @throws com.ibm.wsspi.sib.core.exception.SIDestinationLockedException
     */
    public void deleteDurableSubscription(String subscriptionName,
                                          String durableSubscriptionHome)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SIDurableSubscriptionNotFoundException,
                    SIDestinationLockedException;

    /**
     * Closes the connection (and any Producer/Consumer objects created from it).
     * Any subsequent attempt to call methods on the SICoreConnection will result in
     * an SIObjectClosedException. Calling close on an already-closed
     * SICoreConnection has no effect. When an SICoreConnection is closed, any
     * uncompleted transactions created from the connection are rolled-back.
     * <p>
     * Note that close a connection automatically deregisters its
     * ConnectionListeners.
     * 
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     */
    public void close()
                    throws SIResourceException, SIConnectionLostException,
                    SIConnectionUnavailableException, SIConnectionDroppedException;

    /* PM39926-Start */
    /**
     * Closes the connection (and any Producer/Consumer objects created from it).
     * Any subsequent attempt to call methods on the SICoreConnection will result in
     * an SIObjectClosedException. Calling close on an already-closed
     * SICoreConnection has no effect. When an SICoreConnection is closed, any
     * uncompleted transactions created from the connection are rolled-back.
     * <p>
     * Note that close a connection automatically deregisters its
     * ConnectionListeners.
     * 
     * @param bForceFlag - Flag to indicate that connections have to be closed and cannot be reset.
     *            If marked reset the connection would not be released instead will be reused.
     *            Applicable only in case of JFap version 9 or above.
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     */
    public void close(boolean bResetFlag)
                    throws SIResourceException, SIConnectionLostException,
                    SIConnectionUnavailableException, SIConnectionDroppedException;

    /* PM39926-End */

    /**
     * Creates a transaction object, which may be used to group multiple sends,
     * receives, and other operations into a single unit of work. The
     * createUncoordinatedTransaction call should be issued when the transaction
     * will involve only a single messaging engine, and the application wishes to
     * demarcate units of work directly, i.e. when container transaction services
     * are not employed.
     * <p>
     * An SICoreTransaction may be used with the connection used to create it, or
     * any other connection to the same messaging engine.
     * p>
     * SIIncorrectCallException is thrown if an attempt is made to create an
     * uncoordinated transaction on a connection that is already listed in a global
     * transaction.
     * 
     * @return a newly created SIUncoordinatedTransaction
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     */
    public SIUncoordinatedTransaction createUncoordinatedTransaction()
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SIIncorrectCallException;

    /**
     * Creates a transaction object, which may be used to group multiple sends,
     * receives, and other operations into a single unit of work. The
     * createUncoordinatedTransaction call should be issued when the transaction
     * will involve only a single messaging engine, and the application wishes to
     * demarcate units of work directly, i.e. when container transaction services
     * are not employed.
     * <p>
     * An SICoreTransaction may be used with the connection used to create it, or
     * any other connection to the same messaging engine.
     * p>
     * SIIncorrectCallException is thrown if an attempt is made to create an
     * uncoordinated transaction on a connection that is already listed in a global
     * transaction.
     * 
     * @param allowSubordinateResources If set to true the transaction
     *            object returned will permit subordinate resource enlistment.
     * @return a newly created SIUncoordinatedTransaction
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     */
    public SIUncoordinatedTransaction
                    createUncoordinatedTransaction(boolean allowSubordinateResources)
                                    throws SIConnectionUnavailableException,
                                    SIConnectionDroppedException,
                                    SIResourceException,
                                    SIConnectionLostException,
                                    SILimitExceededException,
                                    SIIncorrectCallException;

    /**
     * Obtains an SIXAResource, which may be used to enlist the Messaging Engine to
     * which this connnection object is attached with an XA transaction.
     * <p>
     * This method should only be used by the API or mediations layer; it is not
     * intended for use by the end application.
     * 
     * @return an implementation of the SIXAResource interface
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     */
    public SIXAResource getSIXAResource()
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException;

    /**
     * Creates a ProducerSession, which is used to send messages to a destination.
     * The caller must name the destination to which it wishes to <i>attach</i> for
     * the purpose of sending messages.
     * <p>
     * Optionally, the caller can indicate the type that the application is
     * expecting for the destination (queue, topic space, etc.). If a
     * DestinationType is given on the createProducer call, and the configuration
     * of the destination itself is different, then SINotPossibleInCurrentConfiguration is
     * thrown. If the application is willing to attach to a destination of any type,
     * then it can pass null for this parameter. SINotPossibleInCurrentConfiguration is
     * also thrown if there is no destination (of whatever type) with the given name.
     * <p>
     * The extendedMessageOrderingContext parameter may be used to indicate that
     * the order of messages sent from multiple ProducerSessions should be
     * maintained. Normally (for clients other than the JMS implementation) this
     * parameter should be set to null.
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that the
     * operations of the ProducerSession should be performed under the authoriy of
     * a different user to that associated with the connection. In order to do this,
     * the user owning the connection must have IdentityAdopter authority. If the
     * connection's user does not have this authority the call fails with an
     * SINotAuthorizedException.
     * <p>
     * The specified user must have the Sender role assigned for the Destination
     * (destAddr) to be authorized to produce messages on the Destination, if they
     * do not the call will fail with an SINotAuthorizedException.
     * <p>
     * The user specified in the createProducerSession call is used in all
     * associated access control checks, and is set into the security id field of
     * all messages sent.
     * 
     * @param destAddr the address of the destination to which the application
     *            wishes to attach for producing messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param extendedMessageOrderingContext indicates that the order of messages
     *            from multiple ProducerSessions should be preserved (may be null)
     * @param alternateUser the name of the user under whose authority operations
     *            of the ProducerSession should be performed (may be null)
     * @return a newly created ProducerSession
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     */
    public ProducerSession createProducerSession(
                                                 SIDestinationAddress destAddr,
                                                 DestinationType destType,
                                                 OrderingContext extendedMessageOrderingContext,
                                                 String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SINotPossibleInCurrentConfigurationException,
                    SITemporaryDestinationNotFoundException,
                    SIIncorrectCallException;

    /**
     * Creates a ProducerSession that can only be used to send messages using a
     * single discriminator. The discriminator value in each message sent using the
     * created ProducerSession is set by the Core SPI implementation to the value
     * passed on the createProducerSession call, overwriting any value set directly
     * into the message by the Core SPI user.
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that the
     * operations of the ProducerSession should be performed under the authoriy of
     * a different user to that associated with the connection. In order to do this,
     * the user owning the connection must have IdentityAdopter authority. If the
     * connection's user does not have this authority the call fails with an
     * SINotAuthorizedException.
     * <p>
     * The specified user must have the Sender role assigned for both the Destination
     * (destAddr) and the Discriminator to be authorized to produce messages on
     * the Destination, if they do not the call will fail with an SINotAuthorizedException.
     * <p>
     * The user specified in the createProducerSession call is used in all
     * associated access control checks, and is set into the security id field of
     * all messages sent.
     * <p>
     * In all other respects this method behaves identically to the
     * createProducerSession method that does not take a discriminator.
     * 
     * @param destAddr the address of the destination to which the application
     *            wishes to attach for producing messages
     * @param discriminator the discriminator to be entered into all messages sent
     *            using this ProducerSession
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param extendedMessageOrderingContext indicates that the order of messages
     *            from multiple ProducerSessions should be preserved (may be null)
     * @return a newly created ProducerSession
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.websphere.sib.exception.core.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     */
    public ProducerSession createProducerSession(
                                                 SIDestinationAddress destAddr,
                                                 String discriminator,
                                                 DestinationType destType,
                                                 OrderingContext extendedMessageOrderingContext,
                                                 String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SINotPossibleInCurrentConfigurationException,
                    SITemporaryDestinationNotFoundException,
                    SIIncorrectCallException, SIDiscriminatorSyntaxException;

    /**
     * This method overloads the above createProducerSession method with the additional options,
     * fixedMessagePoint and preferLocalMessagePoint.
     * <p>
     * The fixedMessagePoint property indicates that any messages sent by this session will either
     * all go to the same queue point (true) or no such restriction will be applied (false),
     * allowing messages from the same session to be sent to different queue points. This property
     * was introduced in WebSphere Application Server V7.
     * <p>
     * The preferLocalMessagePoint property indicates whether a queue point on the connected messaging
     * Engine is preferred over any other queue points (true) (unless the local one is unable to
     * Accept messages at the time that they are sent). Or is any local queue point is treated
     * in the same way as remote queue points (false), in which case workload balancing of
     * messages will occur across all queue points. This property was introduced in WebSphere
     * Application Server V7.
     * <p>
     * All security related behaviour for this method matches the behaviour of
     * the createProducerSession method without the additional arguments
     * exactly.
     * <p>
     * 
     * @param destAddr the address of the destination to which the application
     *            wishes to attach for producing messages
     * @param discriminator the discriminator to be entered into all messages sent
     *            using this ProducerSession (may be null)
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param extendedMessageOrderingContext indicates that the order of messages
     *            from multiple ProducerSessions should be preserved (may be null)
     * @param fixedMessagePoint indicates if the producer should be bound to a specific
     *            queue point or should be allowed to send messages to any available queue point
     * @param preferLocalMessagePoint indicates if the producer should try to send messages to
     *            the local queue point if one is available
     * @return a newly created ProducerSession
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.websphere.sib.exception.core.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     */
    public ProducerSession createProducerSession(
                                                 SIDestinationAddress destAddr,
                                                 String discriminator,
                                                 DestinationType destType,
                                                 OrderingContext extendedMessageOrderingContext,
                                                 String alternateUser,
                                                 boolean fixedMessagePoint,
                                                 boolean preferLocalMessagePoint)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SINotPossibleInCurrentConfigurationException,
                    SITemporaryDestinationNotFoundException,
                    SIIncorrectCallException, SIDiscriminatorSyntaxException;

    /**
     * Creates a ConsumerSession, which is used to receive messages from a
     * destination on the Jetstream bus. This method is not used to consume from
     * durable subscriptions; the method createConsumerforDurableSubscription
     * should be used instead.
     * <p>
     * The caller may specify the reliability with which messages are to be
     * received. The effective quality of service will be no stronger than that of
     * the destination and no weaker than the weakest of the consumer and the
     * message.
     * <p>
     * The other parameter that affects quality-of-service is enableReadAhead. If
     * set to true, then messages may be sent to this consumer in advance of it
     * actually being ready to receive them. That is, Jetstream may stream messages
     * to the consumer. enableReadAhead is most appropriate for use in circumstances
     * where only one consumer can receive each message (pub/sub, temporary queues
     * etc.), since in the general case of Distribution=ONE destinations,
     * readAheadPermitted the assignment of messages to a consumer who may never
     * actually consume them will deny other eligible consumers the opportunity of
     * receiving those messages.
     * <p>
     * The initial state of the newly created ConsumerSession is "stopped".
     * <p>
     * Optionally, the caller can indicate the type that the application is
     * expecting for the destination (queue, topic space, etc.). If a
     * DestinationType is given on the createProducer call, and the type
     * of the destination itself is different, then
     * SINotPossibleInCurrentConfigurationException is
     * thrown. If the application is willing to attach to a destination of any
     * type, then it can pass null for this parameter.
     * SINotPossibleInCurrentConfigurationException is also thrown if there is no
     * destination (of whatever type) with the given name.
     * <p>
     * The unrecoverableReliability parameter is used by the client to indicate which
     * sorts of messages may be handled by the Core API such that they are not
     * recoverable, providing improved perfomance at reduced quality of service.
     * Effectively, it allows the caller to specify that the
     * transaction passed is optional for messages of a particular Reliability. A
     * value of None says that all messages must be recoverable; a value of Express
     * says that express messages may be unrecoverable but reliable and assured
     * messages must be recoverable; a value of reliable says that express and
     * reliable messages may be unrecoverable but assured messages must be
     * recoverable; and a value of assured says that all messages may be
     * unrecoverable.
     * <p>
     * The bifurcatable parameter is used to indicate whether or not the new
     * ConsumerSession may be bifurcated, that is, whether or not the method
     * createBifurcatedConsumerSession will be used, specifying the id of the new
     * ConsumerSession. If bifurcatable is set to false, then such a call to
     * createBifurcatedConsumerSession will throw SIIncorrectCallException. If
     * BifurcatedConsumerSessions are not being used then bifurcatable should be
     * set to false in order to allow all optimizations for the non-bifurcated case
     * to be applied.
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that the
     * operations of the ConsumerSession should be performed under the authoriy of
     * a different user to that associated with the connection. In order to do this,
     * the user owning the connection must have the IdentityAdopter role assigned.
     * If the connection's user does not have this authority the call fails with an
     * SINotAuthorizedException. The user specified in the createConsumerSession call
     * is used in all associated access control checks.
     * <p>
     * The specified user must have the Receiver role assigned for the Destination
     * (destAddr) to be authorized to consume messages from the Destination, if they
     * do not the call will fail with an SINotAuthorizedException.
     * <p>
     * If a non-wildcard Discriminator is set in the supplied SelectionCriteria the
     * user's authority will be checked against that discriminator at this time,
     * if they have not been assigned the Receiver role for this discriminator
     * this call will fail and an SINotAuthorizedException will be thrown. If the
     * discriminator is a wildcard discriminator, authorisation checks will be performed
     * on each potential message matching the SelectionCriteria, if the user is not
     * authorized to receive any of these messages (as defined by their discriminator)
     * they will not be returned to the consumer. No error will be reported in this
     * situation.
     * <p>
     * Any messages delivered to the consumer with a message reliability equal to or
     * lower than the unrecoverableReliability setting may be 'unrecoverable'.
     * Unrecoverable messages will not become re-available for consumption if this
     * consumer fails to complete the delete, e.g. through application failure, transaction
     * rollback or unlocking of the message. As for recoverable messages, if the consumer
     * is an asynchronous consumer any unrecoverable message will continue to be available
     * in the LockedMessageEnumeration until the point that it is deleted or unlocked by
     * the consumer. The Core SPI consuming application does not need to handle messages of
     * such reliabilities in any special way, this option simply gives the messaging provider
     * an opportunity to optimise the flow of messages at certian reliabilities if the consumer
     * is willing to sacrifice the default recoverable behaviour.
     * A value of Reliability.NONE indicates that all messages are recoverable.
     * <p>
     * If there is a need to create BifurcatedConsumerSessions associated with this
     * ConsumerSession the 'bifurcatable' parameter must be set to true. If this
     * ConsumerSession will be used as an asynchronous consumer and the maximum number
     * of active messages is to be set (on ConsumerSession.registerAsynchConsumerCallback)
     * then this ConsumerSession must also be 'bifurcatable'.
     * 
     * @param destAddr the address of the destination from which the application
     *            wishes to consume messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param reliability (NOT USED)
     * @param enableReadAhead indicates whether messages should be streamed to this
     *            consumer
     * @param nolocal true if the ConsumerSession should not be
     *            delivered messages from producers associated with this connection
     * @param unrecoverableReliability the reliability level that will be
     *            unrecoverable for messages delivered to this consumer (see above)
     * @param bifurcatable whether the new ConsumerSession may be bifurcated
     * @param alternateUser the name of the user under whose authority operations
     *            of the ConsumerSession should be performed (may be null)
     * @return a newly created ConsumerSession
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDestinationLockedException
     * @throws com.ibm.websphere.sib.exception.SITemporaryDestinationNotFoundException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     */
    public ConsumerSession createConsumerSession(
                                                 SIDestinationAddress destAddr,
                                                 DestinationType destType,
                                                 SelectionCriteria criteria,
                                                 Reliability reliability,
                                                 boolean enableReadAhead,
                                                 boolean nolocal,
                                                 Reliability unrecoverableReliability,
                                                 boolean bifurcatable,
                                                 String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * This method overloads the above createConsumerSession method with an additional option,
     * ignoreInitialIndoubts.
     * <p>
     * When ingnoreInitialIndoubts is set to true its behaviour matches the
     * createConsumerSession method without this option exactly.
     * <p>
     * When ignoreInitialIndoubts is set to false its behaviour matches the
     * createConsumerSession method without this option in all ways except the following:
     * <lu>
     * <li> The first message will not be delivered to the ConsumerSession until there are
     * no messages in removing state (messages deleted under a transaction but not yet
     * committed or locked) on the message point. Once all existing messages in this state have been
     * removed or made re-available the ConsumerSession reverts to the existing behaviour
     * and available messages are processed in the usual way from now on, with no further
     * blocking on 'removing' messages (as these must have been put into this state by this
     * ConsumerSession, see following bullets).
     * <li> If ignoreInitialIndoubts is set to false and there is already a ConsumerSession
     * attached to the message point the call will fail with an SIDestinationLockedException.
     * <li> If ignoreInitialIndoubts is set to true and a ConsumerSession that set this option
     * to false is already attached to the message point then this call will fail with
     * an SIDestinationLockedException.
     * </lu>
     * <p>
     * All security related behaviour for this method matches the behaviour of
     * the first createConsumerSession in this interface (see related methods in See Also).
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createConsumerSession(SIDestinationAddress destAddr, DestinationType destType, SelectionCriteria criteria, Reliability
     *      reliability, boolean enableReadAhead, boolean nolocal, Reliability unrecoverableReliability, boolean bifurcatable, String alternateUser)
     * @param destAddr the address of the destination from which the application
     *            wishes to consume messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param reliability (NOT USED)
     * @param enableReadAhead indicates whether messages should be streamed to this
     *            consumer
     * @param nolocal true if the ConsumerSession should not be
     *            delivered messages from producers associated with this connection
     * @param unrecoverableReliability the reliability level that will be
     *            unrecoverable for messages delivered to this consumer
     * @param bifurcatable whether the new ConsumerSession may be bifurcated
     * @param alternateUser the name of the user under whose authority operations
     *            of the ConsumerSession should be performed (may be null)
     * @param ignoreInitialIndoubts whether the ConsumerSession ignores uncommitted
     *            removing messages on its initial start
     * @return a newly created ConsumerSession
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDestinationLockedException
     * @throws com.ibm.websphere.sib.exception.SITemporaryDestinationNotFoundException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     */
    public ConsumerSession createConsumerSession(
                                                 SIDestinationAddress destAddr,
                                                 DestinationType destType,
                                                 SelectionCriteria criteria,
                                                 Reliability reliability,
                                                 boolean enableReadAhead,
                                                 boolean nolocal,
                                                 Reliability unrecoverableReliability,
                                                 boolean bifurcatable,
                                                 String alternateUser,
                                                 boolean ignoreInitialIndoubts)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * This method overloads the above createConsumerSession method with an additional option,
     * allowMessageGathering.
     * <p>
     * allowMessageGathering defines whether messages on all queue points or only a single
     * queue point are visible to the ConsumerSession. This property was introduced
     * in WebSphere Application Server V7.
     * <p>
     * All security related behaviour for this method matches the behaviour of
     * the first createConsumerSession in this interface (see related methods in See Also).
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createConsumerSession(SIDestinationAddress destAddr, DestinationType destType, SelectionCriteria criteria, Reliability
     *      reliability, boolean enableReadAhead, boolean nolocal, Reliability unrecoverableReliability, boolean bifurcatable, String alternateUser)
     * @param destAddr the address of the destination from which the application
     *            wishes to consume messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param reliability (NOT USED)
     * @param enableReadAhead indicates whether messages should be streamed to this
     *            consumer
     * @param nolocal true if the ConsumerSession should not be
     *            delivered messages from producers associated with this connection
     * @param unrecoverableReliability the reliability level that will be
     *            unrecoverable for messages delivered to this consumer
     * @param bifurcatable whether the new ConsumerSession may be bifurcated
     * @param alternateUser the name of the user under whose authority operations
     *            of the ConsumerSession should be performed (may be null)
     * @param ignoreInitialIndoubts whether the ConsumerSession ignores uncommitted
     *            removing messages on its initial start
     * @param allowMessageGathering allow consumption of messages from queue points
     *            other than the one directly connected to
     * @param messageControlProperties reserved for future use. Currently has no effect and
     *            is expected to be null.
     * @return a newly created ConsumerSession
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDestinationLockedException
     * @throws com.ibm.websphere.sib.exception.SITemporaryDestinationNotFoundException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     */
    public ConsumerSession createConsumerSession(
                                                 SIDestinationAddress destAddr,
                                                 DestinationType destType,
                                                 SelectionCriteria criteria,
                                                 Reliability reliability,
                                                 boolean enableReadAhead,
                                                 boolean nolocal,
                                                 Reliability unrecoverableReliability,
                                                 boolean bifurcatable,
                                                 String alternateUser,
                                                 boolean ignoreInitialIndoubts,
                                                 boolean allowMessageGathering,
                                                 Map<String, String> messageControlProperties)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * This method has been introduced in V9 as part of JMS 2.0 API for shared non-durable consumer
     * other createConsumer methods do not take subscriptionName as parameter which is must
     * for shared non durable.
     * <p>
     * allowMessageGathering defines whether messages on all queue points or only a single
     * queue point are visible to the ConsumerSession. This property was introduced
     * in WebSphere Application Server V7.
     * <p>
     * All security related behaviour for this method matches the behaviour of
     * the first createConsumerSession in this interface (see related methods in See Also).
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createConsumerSession(SIDestinationAddress destAddr, DestinationType destType, SelectionCriteria criteria, Reliability
     *      reliability, boolean enableReadAhead, boolean nolocal, Reliability unrecoverableReliability, boolean bifurcatable, String alternateUser)
     * @param subscriptionName subscription id of nondurable shared consumer
     * @param destAddr the address of the destination from which the application
     *            wishes to consume messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param reliability (NOT USED)
     * @param enableReadAhead indicates whether messages should be streamed to this
     *            consumer
     * @param nolocal true if the ConsumerSession should not be
     *            delivered messages from producers associated with this connection
     * @param unrecoverableReliability the reliability level that will be
     *            unrecoverable for messages delivered to this consumer
     * @param bifurcatable whether the new ConsumerSession may be bifurcated
     * @param alternateUser the name of the user under whose authority operations
     *            of the ConsumerSession should be performed (may be null)
     * @param ignoreInitialIndoubts whether the ConsumerSession ignores uncommitted
     *            removing messages on its initial start
     * @param allowMessageGathering allow consumption of messages from queue points
     *            other than the one directly connected to
     * @param messageControlProperties reserved for future use. Currently has no effect and
     *            is expected to be null.
     * @return a newly created ConsumerSession
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDestinationLockedException
     * @throws com.ibm.websphere.sib.exception.SITemporaryDestinationNotFoundException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     */
    public ConsumerSession createSharedConsumerSession(
                                                       String subscriptionName,
                                                       SIDestinationAddress destAddr,
                                                       DestinationType destType,
                                                       SelectionCriteria criteria,
                                                       Reliability reliability,
                                                       boolean enableReadAhead,
                                                       boolean supportsMultipleConsumers,
                                                       boolean nolocal,
                                                       Reliability unrecoverableReliability,
                                                       boolean bifurcatable,
                                                       String alternateUser,
                                                       boolean ignoreInitialIndoubts,
                                                       boolean allowMessageGathering,
                                                       Map<String, String> messageControlProperties)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * Sends a message to the specified destination. Calling SICoreConnection.send
     * is equivalent to calling SICoreConnection.createProducerSession followed by
     * ProducerSession.send followed by ProducerSession.close.
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that the
     * send should be performed under the authoriy of a different user to that
     * associated with the connection. In order to do this, the user owning the
     * connection must have IdentityAdopter authority. If the connection's user
     * does not have this authority the call fails with an SINotAuthorizedException.
     * <p>
     * The specified user must have the Sender role assigned for both the Destination
     * (destAddr) and the Discriminator (as set in the message) to be authorized to
     * produce messages on the Destination, if they do not the call will fail with
     * an SINotAuthorizedException.
     * <p>
     * The specified user is set into the security id field of the message being
     * sent.
     * 
     * @param msg the message to be sent
     * @param tran the transaction under which the send is to occur (may be null)
     * @param destAddr the address of the destination to which the application
     *            wishes to attach for producing messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param extendedMessageOrderingContext indicates that the order of messages
     *            from multiple ProducerSessions should be preserved (may be null)
     * @param alternateUser the name of the user under whose authority the send
     *            should be performed (may be null)
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     */
    public void send(
                     SIBusMessage msg,
                     SITransaction tran,
                     SIDestinationAddress destAddr,
                     DestinationType destType,
                     OrderingContext extendedMessageOrderingContext,
                     String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * Receives a message from the specified destination. Calling
     * SICoreConnection.receiveNoWait is equivalent to calling
     * SICoreConnection.createConsumerSession followed by
     * ConsumerSession.receiveNoWait followed by ConsumerSession.close.
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that the
     * operations of the ConsumerSession should be performed under the authoriy of
     * a different user to that associated with the connection. In order to do this,
     * the user owning the connection must have the IdentityAdopter role assigned.
     * If the connection's user does not have this authority the call fails with an
     * SINotAuthorizedException. The user specified in the createConsumerSession call
     * is used in all associated access control checks.
     * <p>
     * The specified user must have the Receiver role assigned for the Destination
     * (destAddr) to be authorized to consume messages from the Destination, if they
     * do not the call will fail with an SINotAuthorizedException.
     * <p>
     * If a non-wildcard Discriminator is set in the supplied SelectionCriteria the
     * user's authority will be checked against that discriminator at this time,
     * if they have not been assigned the Receiver role for this discriminator
     * this call will fail and an SINotAuthorizedException will be thrown. If the
     * discriminator is a wildcard discriminator, authorisation checks will be performed
     * on each potential message matching the SelectionCriteria, if the user is not
     * authorized to receive any of these messages (as defined by their discriminator)
     * they will not be returned by this call. No error will be reported in this
     * situation.
     * 
     * @param tran the transaction under which the receive is to occur (may be
     *            null)
     * @param unrecoverableReliability the reliability level that will be
     *            unrecoverable for this call
     * @param destAddr the address of the destination from which the application
     *            wishes to receive messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param reliability the reliability with which the consumer wishes to receive
     *            messages (may be null)
     * @param alternateUser the name of the user under whose authority the receive
     *            should be performed (may be null)
     * @return a message matching the discriminator and selector in the selection criteria
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDestinationLockedException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     */
    public SIBusMessage receiveNoWait(
                                      SITransaction tran,
                                      Reliability unrecoverableReliability,
                                      SIDestinationAddress destAddr,
                                      DestinationType destType,
                                      SelectionCriteria criteria,
                                      Reliability reliability,
                                      String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * Receives a message from the specified destination. Calling
     * SICoreConnection.receiveWithWait is equivalent to calling
     * SICoreConnection.createConsumerSession followed by
     * ConsumerSession.receiveWithWait followed by ConsumerSession.close.
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that the
     * operations of the ConsumerSession should be performed under the authoriy of
     * a different user to that associated with the connection. In order to do this,
     * the user owning the connection must have the IdentityAdopter role assigned.
     * If the connection's user does not have this authority the call fails with an
     * SINotAuthorizedException. The user specified in the createConsumerSession call
     * is used in all associated access control checks.
     * <p>
     * The specified user must have the Receiver role assigned for the Destination
     * (destAddr) to be authorized to consume messages from the Destination, if they
     * do not the call will fail with an SINotAuthorizedException.
     * <p>
     * If a non-wildcard Discriminator is set in the supplied SelectionCriteria the
     * user's authority will be checked against that discriminator at this time,
     * if they have not been assigned the Receiver role for this discriminator
     * this call will fail and an SINotAuthorizedException will be thrown. If the
     * discriminator is a wildcard discriminator, authorisation checks will be performed
     * on each potential message matching the SelectionCriteria, if the user is not
     * authorized to receive any of these messages (as defined by their discriminator)
     * they will not be returned by this call. No error will be reported in this
     * situation.
     * 
     * @param tran the transaction under which the receive is to occur (may be
     *            null)
     * @param unrecoverableReliability the reliability level that will be
     *            unrecoverable for this call
     * @param destAddr the address of the destination from which the application
     *            wishes to receive messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param reliability the reliability with which the consumer wishes to receive
     *            messages (may be null)
     * @param timeout the length of time to wait for arrival of a matching message
     *            (0 = infinite)
     * @param alternateUser the name of the user under whose authority the receive
     *            should be performed (may be null)
     * @return a message matching the discriminator and selector in the selection
     *         criteria
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.wsspi.sib.core.exception.SIDestinationLockedException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     */
    public SIBusMessage receiveWithWait(
                                        SITransaction tran,
                                        Reliability unrecoverableReliability,
                                        SIDestinationAddress destAddr,
                                        DestinationType destType,
                                        SelectionCriteria criteria,
                                        Reliability reliability,
                                        long timeout,
                                        String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SIDestinationLockedException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * Creates a BrowserSession, used for inspecting the contents of a destination.
     * In the interests of domain-neutrality, it is possible to create a
     * BrowserSession for a TopicSpace. However, such a BrowserSession's next method
     * will always return null. It is anticipated that most calls to
     * createBrowserSession will pass a DestinationType of QUEUE, to cause the core
     * API implementation to throw an SINotPossibleInCurrentConfigurationException
     * if the named destination is in fact a TopicSpace.
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that the
     * operations of the BrowserSession should be performed under the authoriy of
     * a different user to that associated with the connection. In order to do this,
     * the user owning the connection must have been assigned the IdentityAdopter role
     * for the Destination. If the connection's user does not have this authority the
     * call fails with an SINotAuthorizedException. The user specified in the
     * createBrowserSession call is used in all associated access control checks.
     * <p>
     * The specified user must have the Browser role assigned for the Destination
     * (destAddr) to be authorized to browse messages on the Destination, if they
     * do not the call will fail with an SINotAuthorizedException.
     * 
     * @param destinationAddress the destination to which the application wishes
     *            to attach for consuming messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param alternateUser the name of the user under whose authority operations
     *            of the BrowserSession should be performed (may be null)
     * @return a newly created BrowserSession
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     */
    public BrowserSession createBrowserSession(
                                               SIDestinationAddress destinationAddress,
                                               DestinationType destType,
                                               SelectionCriteria criteria,
                                               String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * This method overloads the above createBrowserSession method with an additional option,
     * allowMessageGathering.
     * <p>
     * allowMessageGathering defines whether messages on all queue points or only a single
     * queue point are visible to the BrowserSession. This property was introduced
     * in WebSphere Application Server V7.
     * <p>
     * All security related behaviour for this method matches the behaviour of
     * the createBrowserSession method without the allowMessageGathering behaviour
     * exactly (see related methods in See Also).
     * 
     * @see com.ibm.wsspi.sib.core.SICoreConnection#createBrowserSession(SIDestinationAddress destinationAddress, DestinationType destType, SelectionCriteria criteria, String
     *      alternateUser)
     * @param destinationAddress the destination to which the application wishes
     *            to attach for consuming messages
     * @param destType the type of destination expected by the application
     *            (may be null)
     * @param criteria the criteria against which messages will be matched
     *            encapsulating the discriminator and/or a selector string (may be null)
     * @param alternateUser the name of the user under whose authority operations
     *            of the BrowserSession should be performed (may be null)
     * @param allowMessageGathering allow browsing of messages on queue points
     *            other than the one directly connected to
     * @return a newly created BrowserSession
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     */
    public BrowserSession createBrowserSession(
                                               SIDestinationAddress destinationAddress,
                                               DestinationType destType,
                                               SelectionCriteria criteria,
                                               String alternateUser,
                                               boolean allowMessageGathering)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * Creates a new (logical) connection to a Messaging Engine, that is equivalent
     * to the connection on which the method is invoked, that is, a connection for
     * which isEquivalentTo returns true.
     * <p>
     * Note that this method only creates a single new object: the new connection;it
     * does not clone any ProducerSessions, ConsumerSessions and BrowserSession
     * associated with the original connection.
     * 
     * @see #isEquivalentTo
     * @return a new logical connection to a messaging engine
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     */
    public SICoreConnection cloneConnection()
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException;

    /**
     * Compares two connections to determine if they are equivalent. Two
     * connections are equivalent if, for all operations on the connections or on
     * ProducerSessions and ConsumerSessions created from them with identical method
     * calls, would have the same result, and the same effect on the bus.
     * <p>
     * In practical terms, two connections are equivalent if they have the same
     * user name or Subject, and are, or were, connected to the same Messaging
     * Engine over the same physical connection. (A consequence of this definition
     * is that closed connections may be compared, both to other closed connections,
     * and to open connections, and it is possible for a closed and an open
     * connection to be equivalent.)
     * <p>
     * An important application of isEquivalentTo is in determining whether a
     * transaction created from one connection may be used with methods on
     * another (or its ProducerSessions and ConsumerSessions).
     * <p>
     * Note that we do not simply override Object.equals, as
     * SICoreConnection objects do not in any meaningful sense represent "values",
     * and in particular are not intended for use as hash table keys.
     * 
     * @param rhs the SICoreConnection to be compared with the current object
     * @return true if the two connections are equivalent
     */
    public boolean isEquivalentTo(SICoreConnection rhs);

    /**
     * Returns the name of the Messaging Engine to which the SICoreConnection is
     * connected. (At least in Jetstream 1) this ME is constant for the life of the
     * connection.
     * 
     * @return the name of the ME to which conncetion has been made
     */
    public String getMeName();

    /**
     * Returns the String representation of the UUID of the Messaging Engine to which
     * the SICoreConnection is connected.
     * (At least in Jetstream 1) this ME is constant for the life of the
     * connection.
     * 
     * @return the String representation of the Uuid of the ME to
     *         which the connection has been made
     */
    public String getMeUuid();

    /**
     * Adds a connection listener for this connection. If null is passed, no action
     * is taken and no exception is thrown.
     * <p>
     * A connection listener is implemented by the API layer, in order to receive
     * notification of events in which it may be interested. A particularly
     * important class of events are exceptions that occur during asynchronous
     * delivery, as there is no other mechanism by which the client to the core API
     * can receive such exceptions.
     * 
     * @param listener the object to receive event notifications
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     */
    public void addConnectionListener(SICoreConnectionListener listener)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException;

    /**
     * Removes the specificed connection listener so that it no longer receives
     * event notifications from this connection. If null is passed, no action is
     * taken and no exception is thrown.
     * <p>
     * removeConnectionListener is ignored when invoked on a closed connection.
     * 
     * @param listener the listener object that is no longer to receive events
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     */
    public void removeConnectionListener(SICoreConnectionListener listener)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException;

    /**
     * Returns an array of all the connection listeners registered on this
     * connection.
     * 
     * @return all of this connection's ConnectionListeners (or an empty array)
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     */
    public SICoreConnectionListener[] getConnectionListeners()
                    throws SIConnectionUnavailableException, SIConnectionDroppedException;

    /**
     * Returns a string representing the API level supported by the messaging engine
     * to which connection has been made. This has the format
     * "<Major version>.<Minor version>"
     * 
     * @return the highest API level supported on this connection
     */
    public String getApiLevelDescription();

    /**
     * Returns a value representing the API level supported by the messaging engine
     * to which connection has been made, which should be used in conjunction with
     * the value returned from getMajorVersion. For example, in WAS 6.0,
     * getApiMajorVersion will return 6, and getApiMinorVersion will return 0.
     * 
     * @see #getApiMinorVersion
     * @return the highest API level supported on this connection
     */
    public long getApiMajorVersion();

    /**
     * Returns a value representing the API level supported by the messaging engine
     * to which connection has been made, which should be used in conjunction with
     * the value returned from getMinorVersion. For example, in WAS 6.0,
     * getApiMajorVersion will return 6, and getApiMinorVersion will return 0.
     * 
     * @see #getApiMajorVersion
     * @return the highest API level supported on this connection
     */
    public long getApiMinorVersion();

    /**
     * Returns the configuration of the named destination, which can be localized
     * anywhere on the bus.
     * <p>
     * When running in a secure bus a user must be assigned at least one of the following
     * roles to successfully request a destination's configuration, otherwise the call will
     * fail with an SINotAuthorizedException.
     * <lu>
     * <li> Sender
     * <li> Receiver
     * <li> Browser
     * </lu>
     * 
     * @param destAddr the address of the destination whose configuration is to be
     *            returned
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     * @throws com.ibm.wsspi.sib.core.exception.SITemporaryDestinationNotFoundException
     * @return the destination's configuration
     */
    public DestinationConfiguration getDestinationConfiguration(
                                                                SIDestinationAddress destAddr)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SITemporaryDestinationNotFoundException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * Creates a new OrderingContext, that may be used to ensure
     * messages ordering across multiple ProducerSessions or multiple
     * ConsumerSessions.
     * 
     * @return a new OrderingContext
     * @see com.ibm.wsspi.sib.core.OrderingContext
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     */
    public OrderingContext createOrderingContext()
                    throws SIConnectionUnavailableException, SIConnectionDroppedException;

    /**
     * Returns the userid associated with a connection. If a userid was specified
     * when the connnetion was created, then this id will be returned. If the
     * connection was created using a Subject, the corresponding user name will be
     * extracted and returned.
     * 
     * @return the userid associated with the connection
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     */
    public String getResolvedUserid()
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException;

    /**
     * This method is used to create a BifurcatedConsumerSession object, which is an
     * additional session representing an existing ConsumerSession. The ID should be
     * obtained using ConsumerSession.getId. There may be many
     * BifurcatedConsumerSessions representing the same consumer. The original
     * ConsumerSession must still be open at the time this method is called. The
     * method may be called on any connection that is connected to the same ME as
     * that of the original ConsumerSession
     * <p>
     * Bifurcated consumers have the ability to read, delete and unlock messages
     * that are currently locked to the parent ConsumerSession if it has an
     * asynchronous callback registered.
     * <p>
     * If running in a secure bus, the creating connection must have
     * the same Subject as that of the original ConsumerSession's connection. If
     * the subjects do not match, the user associated with the referenced
     * ConsumerSession must match this connection's user, the call will fail with
     * an SINotAuthorizedException.
     * 
     * @param id the identifier of the consumer, obtained from ConsumerSession.getId
     * @throws com.ibm.wsspi.sib.core.exception.SISessionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SISessionDroppedException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     */
    public BifurcatedConsumerSession createBifurcatedConsumerSession(
                                                                     long id)
                    throws SISessionUnavailableException, SISessionDroppedException,
                    SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException;

    /**
     * Sends a message to an exception destination. A new exception message is
     * generated by copying the message passed, and sent to the exception
     * destination named in the configuration of the destination identified by the
     * first parameter to the call. If a null SIDestinationAddress is passed in then
     * the message will be sent to the default Exception Destination defined for the
     * messaging engine the connection is connected to. The exception message will
     * contain the supplied reason and inserts, and the exception timestamp of the
     * message will be set to the current time.
     * <p>
     * The reason parameter should match one of the exception reasons defined
     * in com.ibm.websphere.sib.SIRCConstants. Most of the reasons are reserved to
     * Jetstream and other WebSphere system code; other users should use the generic
     * reason SIRC0001_DELIVERY_ERROR. Each "inserts" parameter to the method call
     * enables to parameterize the exception. In the case of SIRC0001_DELIVERY_ERROR
     * no inserts are required, and either null or an empty array may be passed.
     * (If a non-empty array is passed, this does not cause any kind of error at
     * the time of the sendToExceptionDestination call, but is likely to confuse
     * any exception handler consuming from the exception destination.)
     * <p>
     * The following behaviour only applies if running in a secure bus:
     * <p>
     * The optional alternateUser parameter may be used to indicate that the
     * send should be performed under the authoriy of a different user to that
     * associated with the connection. In order to do this, the user owning the
     * connection must have IdentityAdopter authority. If the connection's user
     * does not have this authority the call fails with an SINotAuthorizedException.
     * <p>
     * The specified user must have the Sender role assigned for the exception destination
     * (address)to be authorized to send messages on the destination, if they do not
     * the call will fail with an SINotAuthorizedException.
     * 
     * @param address the address of the destination to whose exception destination
     *            the message should be sent (may be null)
     * @param message the message that caused the exception or was unable to be delivered
     * @param reason the reason code to be inserted in the exception message.
     * @param inserts other parameters to the exception (may be null)
     * @param alternateUser the name of the user under whose authority the send
     *            should be performed (may be null)
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException
     * @throws com.ibm.websphere.sib.exception.SIResourceException
     * @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
     * @throws com.ibm.wsspi.sib.core.exception.SILimitExceededException
     * @throws com.ibm.wsspi.sib.core.exception.SINotAuthorizedException
     * @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
     * @throws com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException
     */
    public void sendToExceptionDestination(
                                           SIDestinationAddress address,
                                           SIBusMessage message,
                                           int reason,
                                           String[] inserts,
                                           SITransaction tran,
                                           String alternateUser)
                    throws SIConnectionUnavailableException, SIConnectionDroppedException,
                    SIResourceException, SIConnectionLostException, SILimitExceededException,
                    SINotAuthorizedException,
                    SIIncorrectCallException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * Performs the following checks to see if a messaging operation can be
     * avoided by the calling application
     * 
     * <ul>
     * <li>Check that the request destination is sendAllowed
     * <li>Check that the reply destination is receiveAllowed
     * <li>Check that neither destination (or any destination on an administered
     * routing path associated with either destination) is mediated
     * <li>Check that the reply destination does not have an associated forward
     * routing path
     * <li>Check that neither destination (or any destination on an administered
     * routing path associated with either destination) is on a different bus
     * to the connected Messaging Engine
     * <li>Check that the resolved destination for the request and the reply
     * destination both have queue points on the same Messaging Engine as the
     * one that the SICoreConnection is connected too
     * <li>Check that the request destination matches the DestinationType
     * supplied
     * <li>Check that the resolved request destination is of type Port
     * <li>Check that no loop exist in any forward routing path associated with
     * the request destination
     * <li>Check that the user has Sender authority for each destination (and
     * any destination on an administered routing path associated with either
     * destination).
     * <li>Check that the user has Sender authority for the reply destination.
     * </ul>
     * The above two security checks are only performed when running in a secure
     * bus, if either fail the call will failwith an SINotAuthorizedException.
     * The optional alternateUser parameter may be used to indicate that the
     * above two security checks should be performed under the authoriy of
     * a different user to that associated with the connection. In order to do this,
     * the user owning the connection must have IdentityAdopter authority. If the
     * connection's user does not have this authority the call fails with an
     * SINotAuthorizedException.
     * 
     * @param requestDestAddr Destination the request message would be sent to
     * @param replyDestAddr Destination the reply message would be sent to
     * @param destinationType Type of destination required for the request
     *            destination
     * @param alternateUser User that access checks are performed against
     *            (if null the user associated with the connection
     *            is used)
     * 
     * @return null if messaging is required. The SIDestinationAddress of
     *         the resolved request destination if messaging is not required
     * 
     * @throws SIConnectionDroppedException,
     * @throws SIConnectionUnavailableException,
     * @throws SIErrorException,
     * @throws SIIncorrectCallException,
     * @throws SITemporaryDestinationNotFoundException,
     * @throws SIResourceException,
     * @throws SINotAuthorizedException,
     * @throws SINotPossibleInCurrentConfigurationException;
     */
    public SIDestinationAddress checkMessagingRequired(SIDestinationAddress requestDestAddr,
                                                       SIDestinationAddress replyDestAddr,
                                                       DestinationType destinationType,
                                                       String alternateUser)

                    throws SIConnectionDroppedException,
                    SIConnectionUnavailableException,
                    SIIncorrectCallException,
                    SITemporaryDestinationNotFoundException,
                    SIResourceException,
                    SINotAuthorizedException,
                    SINotPossibleInCurrentConfigurationException;

    /**
     * This method may only be invoked if the user owning the
     * connection is SIBServerSubject otherwise a SINotAuthorizedException
     * will be returned to the caller.
     * 
     * @param key A String identifying the requestor
     * @param commandName The command to be invoked
     * @param commandData The data to pass on command invocation
     * 
     * @return the return value of the invoked command.
     * 
     * @throws SIConnectionDroppedException;
     * @throws SIConnectionUnavailableException;
     * @throws SIResourceException,
     * @throws SIIncorrectCallException;
     * @throws SINotAuthorizedException,
     * @throws SICommandInvocationFailedException;
     */
    public Serializable invokeCommand(String key, String commandName, Serializable commandData)
                    throws SIConnectionDroppedException,
                    SIConnectionUnavailableException,
                    SIResourceException,
                    SIIncorrectCallException,
                    SINotAuthorizedException,
                    SICommandInvocationFailedException;

    /**
     * Invoke a command within a transactional context.
     * 
     * This method may only be invoked if the user owning the
     * connection is SIBServerSubject otherwise a SINotAuthorizedException
     * will be returned to the caller.
     * 
     * @param key A String identifying the requestor
     * @param commandName The command to be invoked
     * @param commandData The data to pass on command invocation
     * @param transaction The transaction to be passed to the command invocation.
     * 
     * @return the return value of the invoked command.
     * 
     * @throws SIConnectionDroppedException;
     * @throws SIConnectionUnavailableException;
     * @throws SIResourceException,
     * @throws SIIncorrectCallException;
     * @throws SINotAuthorizedException,
     * @throws SICommandInvocationFailedException;
     */
    public Serializable invokeCommand(String key, String commandName, Serializable commandData, SITransaction transaction)
                    throws SIConnectionDroppedException,
                    SIConnectionUnavailableException,
                    SIResourceException,
                    SIIncorrectCallException,
                    SINotAuthorizedException,
                    SICommandInvocationFailedException;

    /**
     * This method will register a DestinationListener, and return any destination localized on the
     * connected messaging engine that match the destination type and availability that are already available.
     * Multiple destination listeners may be registered for each connection.
     * 
     * Security note:
     * Only those destinations for which the connected user is authorized to carry out the requested action
     * (as specified by the DestinationAvailability parameter) are returned from this method in order to avoid
     * leaking security sensitive information to rogue Core SPI applications.
     * 
     * @param destinationNamePattern The wildcarded name pattern (e.g. sca/myModule//*) that describes
     *            the group of destinations that we are interested in.
     * @param destinationListener DestinationListener callback that will be driven if new destinations
     *            matching the criteria are created on the connected messaging engine.
     * @param destinationType The type of destination that we are interested in. Note that initially
     *            only queue type destinations are supported.
     * @param destinationAvailability The type of destination capability that we are interested in
     *            i.e. SEND or RECEIVE.
     * 
     * @return SIDestinationAddress[] An array of SIDestinationAddress objects representing the destinations
     *         that match the requested type and availability, and which are already
     *         available on the connected messaging engine.
     * 
     * @throws SICommandInvocationFailedException;
     * @throws SIConnectionDroppedException
     * @throws SIConnectionLostException
     * @throws SIConnectionUnavailableException;
     * @throws SIIncorrectCallException;
     */
    public SIDestinationAddress[] addDestinationListener(String destinationNamePattern,
                                                         DestinationListener destinationListener,
                                                         DestinationType destinationType,
                                                         DestinationAvailability destinationAvailability)
                    throws SIIncorrectCallException,
                    SICommandInvocationFailedException,
                    SIConnectionUnavailableException,
                    SIConnectionLostException,
                    SIConnectionDroppedException;

    /**
     * Checks whether there are any potential consumers for messages published on the
     * specified topic(s). The potential set will be determined using an optimistic
     * approach that caters for wildcards and selectors by returning true if there is
     * any possibility of a match.
     * 
     * Additionally registers a callback that will be driven when the potential set of
     * consumers drops to zero or rises above zero.
     * 
     * Returns true if the potential set of consumers is currently greater than zero,
     * false if it is zero.
     * 
     * @param destinationAddress
     * @param discriminatorExpression
     * @param callback
     * @return
     * @throws SIResourceException
     * @throws SINotPossibleInCurrentConfigurationException
     */
    public boolean registerConsumerSetMonitor(
                                              SIDestinationAddress destinationAddress,
                                              String discriminatorExpression,
                                              ConsumerSetChangeCallback callback)
                    throws SIResourceException,
                    SINotPossibleInCurrentConfigurationException,
                    SIConnectionUnavailableException,
                    SIConnectionDroppedException,
                    SIIncorrectCallException,
                    SICommandInvocationFailedException;

}
