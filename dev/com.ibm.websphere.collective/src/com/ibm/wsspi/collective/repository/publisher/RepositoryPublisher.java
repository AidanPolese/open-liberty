/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.collective.repository.publisher;

import org.osgi.service.event.EventAdmin;

/**
 * The RepositoryPublisher service will listen to the {@link #ROOT_PUBLISH_TOPIC} topic and handle all requests for publishing. If the requests for publishing
 * requires status, a status event will be posted to the {@link #ROOT_STATUS_TOPIC} topic.
 * <p>
 * The RepositoryPublisher will be registered into the OSGi service registry
 * when the RepositoryPublisher is available to receive events. The presence
 * of this service in the SCR can be used to block event publishing until the
 * RepositoryPublisher is ready to process requests. Doing this is optional.
 * <p>
 * <h3>Terminology</h3>
 * <ul>
 * <li>Publish Event - a request (in the form of an OSGi {@link Event}) from a
 * component to publish data to the repository. A Publish Event is always a
 * write operation, and may either update or delete content within the
 * repository.</li>
 * <li>Status Event - an OSGi {@link Event} fired by the RepositoryPublisher
 * to report the requested Publish Event has completed, and to indicate the
 * status of the operation. A Status Event must be requested by the Publish
 * Event.</li>
 * </ul>
 * It is strongly recommended that events intended for this service are
 * delivered asynchronously using {@link EventAdmin#postEvent(Event)} as
 * no guarantees are made with respect to the amount of time it takes to
 * receive and process a Publish Event.
 * <p>
 * EventAdmin will ensure that the order of the delivered events will be preserved.
 * For example:
 * <p>
 * postEvent(A);<br>
 * postEvent(C);<br>
 * postEvent(B);<br>
 * <p>
 * The events will be delivered to the RepositoryPublisher in the order: A -> C -> B.
 * <p>
 * The RepositoryPublisher supports publishing information about MBeans as well as
 * arbitrary data. The topic to which the Publish Event is posted indicates the
 * type of information to be published (either MBean or arbitrary data).
 * Publishing MBean data results in a specific path syntax in the repository
 * while publishing arbitrary data allows for non-MBean information to be
 * published to an arbitrary path relative to the server's path within the
 * repository.
 * <p>
 * <h2>Publishing Information About MBeans</h2>
 * MBeans can directly expose their attributes to the collective controller
 * by publishing their information via Publish Events. Doing so will result
 * in the data being stored in the repository in the following format:
 * <br> {@code serverNode/sys.mbeans/mbean_object_name/attributes/attribute_name (attribute_value)} <p>
 * The path within the repository is constructed using the payload of the
 * Publish Event.
 * <p>
 * An MBean will be automatically published if the following conditions are met:
 * <ul>
 * <li>The MBean exists in the OSGi Service Registry with the "jmx.objectname" property defined.</li>
 * <li>The MBean implements javax.management.NotificationBroadcaster (or javax.management.NotificationEmitter).</li>
 * <li>The MBean indicates it emits AttributeChangeNotifications via NotificationBroadcaster.getNotificationInfo()</li>
 * </ul>
 * <p>
 * <h3>Examples</h3>
 * <p>
 * Create or update an MBean attribute
 * <p>
 * Map&ltString,Object&gt eventProps = new HashMap&ltString,Object&gt();<br>
 * eventProps.put({@link #KEY_OPERATION}, {@link #OPERATION_UPDATE});<br>
 * eventProps.put({@link #MBEAN_OBJECT_NAME}, "objectName");<br>
 * eventProps.put({@link #MBEAN_ATTRIBUTE_NAME}, "attribute");<br>
 * eventProps.put({@link #MBEAN_ATTRIBUTE_VALUE}, "value");<br>
 * eventAdmin.postEvent(new Event({@link #PUBLISH_MBEAN_TOPIC}, eventProps));<br>
 * <p>
 * Deleting an MBean attribute
 * <p>
 * Map&ltString,String&gt eventProps = new HashMap&ltString,String&gt();<br>
 * eventProps.put({@link #KEY_OPERATION}, {@link #OPERATION_DELETE});<br>
 * eventProps.put({@link #MBEAN_OBJECT_NAME}, "objectName");<br>
 * eventProps.put({@link #MBEAN_ATTRIBUTE_NAME}, "attribute");<br>
 * eventAdmin.postEvent(new Event({@link #PUBLISH_MBEAN_TOPIC}, eventProps));<br>
 * <p>
 * Deleting an MBean
 * <p>
 * Map&ltString,String&gt eventProps = new HashMap&ltString,String&gt();<br>
 * eventProps.put({@link #KEY_OPERATION}, {@link #OPERATION_DELETE});<br>
 * eventProps.put({@link #MBEAN_OBJECT_NAME}, "objectName");<br>
 * eventAdmin.postEvent(new Event({@link #PUBLISH_MBEAN_TOPIC}, eventProps));<br>
 * <p>
 * <h2>Publishing Arbitrary Data</h2>
 * All management capabilities should be exposed via MBeans. However, certain
 * types of information do not fit within the MBean model, such as a server
 * started / stopped state. In such cases, arbitrary (non-MBean) information
 * can be published to the repository. <b>Note:</b> This information will only be
 * available when the server is part of a collective, and has no meaning in a single
 * server environment.
 * <h3>Examples</h3>
 * <p>
 * Create or update arbitrary data
 * <p>
 * Map&ltString,Object&gt eventProps = new HashMap&ltString,Object&gt();<br>
 * eventProps.put({@link #KEY_OPERATION}, {@link #OPERATION_UPDATE});<br>
 * eventProps.put({@link #DATA_NAME}, "myData/data1");<br>
 * eventProps.put({@link #DATA_VALUE}, "value");<br>
 * eventAdmin.postEvent(new Event({@link #PUBLISH_DATA_TOPIC}, eventProps));<br>
 * <p>
 * Deleting arbitrary data
 * <p>
 * Map&ltString,String&gt eventProps = new HashMap&ltString,String&gt();<br>
 * eventProps.put({@link #KEY_OPERATION}, {@link #OPERATION_DELETE});<br>
 * eventProps.put({@link #DATA_NAME}, "myData/data1");<br>
 * eventAdmin.postEvent(new Event({@link #PUBLISH_DATA_TOPIC}, eventProps));<br>
 * 
 * @ibm-spi
 */
public interface RepositoryPublisher {

    /**
     * The root topic to which the publisher will listen for Publish Events.
     * <p>
     * All Publish Events must be sent to one of the publishing topics:
     * <ul>
     * <li>{@link #PUBLISH_MBEAN_TOPIC}</li>
     * <li>{@link #PUBLISH_DATA_TOPIC}</li>
     * </ul>
     * <p>
     * Events posted to this topic may have one of the following properties:
     * <ul>
     * <li>{@link #KEY_OPERATION}</li>
     * </ul>
     * See each supported property for more details.
     */
    String ROOT_PUBLISH_TOPIC = "com/ibm/wsspi/collective/repository/publish/";

    /**
     * Indicates the operation to perform.
     * <p>
     * If this property is not specified, {@link #OPERATION_UPDATE} is assumed.
     */
    String KEY_OPERATION = "operation";

    /**
     * Indicates the operation to perform is an update.
     * <p>
     * If the node does not yet exist, it will be created with the
     * requested value (including all parent nodes).
     */
    String OPERATION_UPDATE = "UPDATE";

    /**
     * Indicates the operation to perform is a simple update.
     * <p>
     * If the node does not yet exist, it will not be created.
     */
    String OPERATION_UPDATE_ONLY = "UPDATE_ONLY";

    /**
     * Indicates the operation to perform is a deletion.
     * <p>
     * The specified node and all of its children will be removed
     * (recursive deletion).
     */
    String OPERATION_DELETE = "DELETE";

    /**
     * Indicates whether a Status Event should be sent for the Publish Event.
     * <p>
     * If this property is not specified, no Status Event will be sent.
     * Note the value of this property is not checked, only whether the
     * property has been set.
     */
    String KEY_SEND_STATUS_EVENT = "sendStatusEvent";

    /**
     * Simple value constant to use with {@link #KEY_SEND_STATUS_EVENT}.
     */
    String SEND_STATUS_EVENT = "true";

    /**
     * The topic for MBean Publish Events.
     * <p>
     * Events posted to this topic may have the following properties:
     * <ul>
     * <li>{@link #MBEAN_OBJECT_NAME}</li>
     * <li>{@link #MBEAN_ATTRIBUTE_NAME}</li>
     * <li>{@link #MBEAN_ATTRIBUTE_VALUE}</li>
     * </ul>
     * See each property for details.
     */
    String PUBLISH_MBEAN_TOPIC = ROOT_PUBLISH_TOPIC + "mbean";

    /**
     * The MBean's object name.
     * <p>
     * This attribute is required for all MBean Publish Events.
     */
    String MBEAN_OBJECT_NAME = "mbeanObjectName";

    /**
     * An MBean's attribute name.
     * <p>
     * This attribute is required for MBean UPDATE Publish Events.
     * If this attribute is omitted for MBean DELETE Publish Events, all
     * of the information about the MBean will be deleted. If this attribute
     * is specified for MBean DELETE Publish Events, then only the specified
     * attribute will be deleted.
     */
    String MBEAN_ATTRIBUTE_NAME = "mbeanAttributeName";

    /**
     * An MBean's attribute value.
     * <p>
     * This attribute is required for MBean UPDATE Publish Events.
     * This attribute is ignored for MBean DELETE Publish Events.
     */
    String MBEAN_ATTRIBUTE_VALUE = "mbeanAttributeValue";

    /**
     * The topic for Arbitrary Data Publish Events.
     * <p>
     * Events posted to this topic may have the following properties:
     * <ul>
     * <li>{@link #DATA_NAME}</li>
     * <li>{@link #DATA_VALUE}</li>
     * </ul>
     * See each property for details.
     */
    String PUBLISH_DATA_TOPIC = ROOT_PUBLISH_TOPIC + "data";

    /**
     * The name of the data to store.
     * <p>
     * This attribute is required for all Arbitrary Data Publish Events.
     * The name must take the form of a relative path. If the name begins
     * with a leading slash, then the event will be ignored.
     */
    String DATA_NAME = "dataName";

    /**
     * The value to store.
     * <p>
     * This attribute is required for Arbitrary Data UPDATE Publish Events.
     * This attribute is ignored for Arbitrary Data DELETE Publish Events.
     */
    String DATA_VALUE = "dataValue";

    /**
     * The root topic to which the publisher will post the Status Events.
     * <p>
     * If a component wishes to learn of the status of their Publish Event,
     * it must set {@link #KEY_SEND_STATUS_EVENT} in the Publish Event properties,
     * and establish an EventHandler which will listen on the {@link ROOT_STATUS_TOPIC}.
     * <p>
     * Events posted to this topic will have all of the properties from the
     * original Publish Event, and may have the following properties:
     * <ul>
     * <li>{@link #KEY_STATUS_ERROR_MESSAGE}</li>
     * </ul>
     * If the {@link #KEY_STATUS_ERROR_MESSAGE} property is not set then the
     * operation completed successfully.
     */
    String ROOT_STATUS_TOPIC = "com/ibm/wsspi/collective/repository/publishStatus/";

    /**
     * The topic for MBean Status Events.
     */
    String STATUS_MBEAN_TOPIC = ROOT_STATUS_TOPIC + "mbean";

    /**
     * The topic for Arbitrary Data Status Events.
     */
    String STATUS_DATA_TOPIC = ROOT_STATUS_TOPIC + "data";

    /**
     * Indicates the error message (if any) which was captured
     * while handling the Publish Event.
     * <p>
     * If this property is not set, then the Publish Event completed
     * successfully.
     */
    String KEY_STATUS_ERROR_MESSAGE = "errorMessage";

}
