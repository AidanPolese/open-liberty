/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.security.audit;

import java.util.List;
import java.util.Map;

import com.ibm.websphere.security.audit.AuditEvent;
import com.ibm.websphere.security.audit.InvalidConfigurationException;

/**
 *
 */
public interface AuditService {

    String AUDIT_SOURCE_NAME = "audit";
    // TODO: is this the value we want for location?
    String AUDIT_SOURCE_LOCATION = "server";
    String AUDIT_SOURCE_SEPARATOR = "|";
    String AUDIT_FILE_HANDLER_NAME = "AuditFileHandler";

    void sendEvent(AuditEvent event);

    boolean isAuditRequired(String eventType, String outcome);

    void auditStarted(String serviceName);

    void auditStopped(String serviceName);

    void registerEvents(String handlerName, List<Map<String, Object>> configuredEvents) throws InvalidConfigurationException;

    void unRegisterEvents(String handlerName);

    /**
     * Get the unique identifier String of this server. The format is:
     * "websphere: hostName:userDir:serverName"
     *
     * @return the unique identifier of this server
     */
    String getServerID();

    /**
     * @param configuredEvents
     * @return
     */
    boolean validateEventsAndOutcomes(String handlerName, List<Map<String, Object>> configuredEvents);

}
