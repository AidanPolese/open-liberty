/**
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Material
 * 
 * Copyright IBM Corp. 2012
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
 * --------------- --------- -------- --------------------------------------------
 *                 06-Jul-03 pnickoll Original
 * 169897.5        07-Jul-03 pnickoll Updated to encompass the core API changes
 * 169897.10.1     31-Jul-03 pnickoll Changes to connection factory properties
 * 174531          18-Aug-03 djhoward Added Referenceable to interface
 * 176645.2        16-Sep-03 djhoward Changes to properties
 * 181796.6        05-Nov-03 djhoward Core SPI move to com.ibm.wsspi.sib.core
 * 188479.2        05-Feb-04 dcurrie  Remove TempQueue|TopicModelName
 * 188482.2        05-Feb-04 dcurrie  Add TemporaryQueue|TopicNamePrefix
 * 184390.8        19-Feb-04 kingdon  Add getPersistentMapping
 * 197250.1        13-Apr-04 pnickoll Added getters for various properties
 * 192474.1        14-Apr-04 pnickoll Added sharedDurableSubscriptions property
 * 203656          17-May-04 dcurrie  Code cleanup
 * 206397.5.1      07-Jul-04 pnickoll Add Target, TargetType and TargetSignificance properties
 * 201972.4        28-Jul-04 pnickoll Update core SPI exceptions
 * 206397.5.2      06-Aug-04 pnickoll Removed old remoteTargetGroup and remoteTargetType properties
 * 224794          06-Sep-04 dcurrie  Rename RemoteProtocol to TargetTransportChain
 * 247845.4        10-Feb-05 pnickoll Added multicast interface and subscription protocol properties
 * SIB0121.jms.1   28-Dec-06 jamessid Added getter for performance enhancing property
 * ============================================================================
 */
package com.ibm.ws.sib.api.jmsra;

import java.io.Serializable;

//Sanjay Liberty Changes
//import javax.resource.Referenceable;
//import javax.resource.ResourceException;



import javax.resource.Referenceable;
import javax.resource.ResourceException;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIException;

/**
 * Factory class for the creation of <code>JmsJcaConnection</code> objects.
 * Also provides methods for obtaining JMS properties set on the associated
 * managed connection factory. A new instance implementing this interface is
 * passed on the construction of each JMS connection factory.
 */
public interface JmsJcaConnectionFactory extends Serializable, Referenceable {

    /**
     * Creates a new <code>JmsJcaConnection</code>. This results in the
     * creation of a new core connection to a message engine using the
     * properties defined on the associated managed connection factory. The
     * default credentials from the managed connection factory will be used if
     * none are passed to the resource adapter by the container.
     * 
     * @return the connection
     * @throws javax.resource.ResourceException
     *             if the JCA runtime fails to allocate a connection
     * @throws SIException
     *             if the creation of the core connection fails
     * @throws SIErrorException
     *             if the creation of the core connection fails
     */
    public JmsJcaConnection createConnection() throws ResourceException,
            SIException, SIErrorException;

    /**
     * Creates a new <code>JmsJcaConnection</code>. This results in the
     * creation of a new core connection to a message engine using the
     * properties defined on the associated managed connection factory. The
     * application credentials provided will be used if none are passed to the
     * resource adapter by the container.
     * 
     * @param userName
     *            the application provided user name
     * @param password
     *            the application provided password
     * @return the connection
     * @throws javax.resource.ResourceException
     *             if the JCA runtime fails to allocate a connection
     * @throws SIException
     *             if the creation of the core connection fails
     * @throws SIErrorException
     *             if the creation of the core connection fails
     */
    public JmsJcaConnection createConnection(String userName, String password)
            throws ResourceException, SIException, SIErrorException;

    /**
     * Returns the JMS client ID set on the associated managed connection
     * factory or null if none was set.
     * 
     * @return the client ID
     */
    public String getClientID();

    /**
     * Returns the non persistent mapping set on the associated managed
     * connection factory or null if none was set.
     * 
     * @return the non persistent mapping
     */
    public String getNonPersistentMapping();

    /**
     * Returns the persistent mapping set on the associated managed connection
     * factory or null if none was set.
     * 
     * @return the persistent mapping
     */
    public String getPersistentMapping();

    /**
     * Returns true if this connection factory was created in a managed
     * environment. This is determined by whether or not a connection manager
     * was passed on its creation. This can be used by the JMS API to determine
     * whether or not to throw exceptions on methods that are not permitted in a
     * managed environment i.e. within the EJB or web container.
     * 
     * @return true iff this connection factory is managed
     */
    public boolean isManaged();

    /**
     * Returns the durable subscription home.
     * 
     * @return the durable subscription home
     */
    public String getDurableSubscriptionHome();

    /**
     * Returns string used to control read ahead optimisation.
     * 
     * @return the read ahead
     */
    public String getReadAhead();

    /**
     * Gets the prefix for temporary queue names.
     * 
     * @return the prefix
     */
    public String getTemporaryQueueNamePrefix();

    /**
     * Gets the prefix for temporary topic names.
     * 
     * @return the prefix
     */
    public String getTemporaryTopicNamePrefix();

    /**
     * Gets the busName
     * 
     * @return the busName
     */
    public String getBusName();

    /**
     * Gets the userName
     * 
     * @return the userName
     */
    public String getUserName();

    /**
     * Gets the password
     * 
     * @return the password
     */
    public String getPassword();

    /**
     * Gets the Target
     * 
     * @return the Target
     */
    public String getTarget();

    /**
     * Gets the TargetType
     * 
     * @return the TargetType
     */
    public String getTargetType();

    /**
     * Get thje target significance
     * 
     * @return the TargetSignificance
     */
    public String getTargetSignificance();

    /**
     * Gets the target inbound transport chain.
     * 
     * @return the target transport chain
     */
    public String getTargetTransportChain();

    /**
     * Gets the providerEndpoints
     * 
     * @return the providerEndpoints
     */
    public String getProviderEndpoints();

    /**
     * Gets the connectionProximity
     * 
     * @return the connectionProximity
     */
    public String getConnectionProximity();

    /**
     * Returns the shareDurableSubscription details
     * 
     * @return the shareDurableSubscription property
     */
    public String getShareDurableSubscriptions();

    /**
     * Returns the subscription protocol
     * 
     * @return the subscription protocol property
     */
    public String getSubscriptionProtocol();
    
    /**
     * Returns the multicast interface
     * 
     * @return the multicast interface property
     */
    public String getMulticastInterface();

    /** 
     * Returns the property indicating if the producer will modify the payload after setting it.
     * 
     * @return String containing the property value.  
     */
    public String getProducerDoesNotModifyPayloadAfterSet();

    /** 
     * Gets the property indicating if the consumer will modify the payload after getting it.
     * 
     * @return String containing the property value.  
     */
    public String getConsumerDoesNotModifyPayloadAfterGet();    
}
