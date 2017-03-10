/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.wsspi.logprovider;

import java.io.File;

/**
 * Interface for pluggable FFDCFilter implementation.
 * 
 * Services that implement this interface and register as FFDCFilterService
 * providers will be delegated to by the static methods in
 * com.ibm.ws.ffdc.FFDCFilter.
 */
public interface FFDCFilterService {
    /**
     * Filter method used to process a Throwable from user code.
     * <p>
     * 
     * @param th
     *            The throwable from the caller which is to be processed.
     * @param sourceId
     *            The source Id is a unique identifier for the the source code.
     *            The code insertion tool uses the class+method name as the
     *            source identifier.
     * @param probeId
     *            The probe Id is a unique identifier with in the code. The
     *            source line will be used as the probe Id. The identifier needs
     *            to be unique within the file, when the file changes and the
     *            code location has changed within the source file, the files
     *            does not need to be updated. The probe Id simply needs to
     *            maintained unique within the file. If the resequencing of the
     *            source is needed, the tool can be used to resequence the probe
     *            Ids to be consistent with the source file.
     */
    void processException(Throwable th, String sourceId, String probeId);

    /**
     * Filter method used to process a Throwable from user code.
     * <p>
     * 
     * @param th
     *            The throwable from the caller which is to be processed.
     * @param sourceId
     *            The source Id is a unique identifier for the the source code.
     *            The code insertion tool uses the class+method name as the
     *            source identifier.
     * @param probeId
     *            The probe Id is a unique identifier with in the code. The
     *            source line will be used as the probe Id. The identifier needs
     *            to be unique within the file, when the file changes and the
     *            code location has changed within the source file, the files
     *            does not need to be updated. The probe Id simply needs to
     *            maintained unique within the file. If the resequencing of the
     *            source is needed, the tool can be used to resequence the probe
     *            Ids to be consistent with the source file.
     * @param callerThis
     *            The callerThis parameter corresponds to the invoker's this
     *            pointer. The pointer will be processed by the components
     *            diagnostic module.
     */
    void processException(Throwable th, String sourceId, String probeId, Object callerThis);

    /**
     * Filter method used to process a Throwable from user code.
     * <p>
     * 
     * @param th
     *            The exception from the caller which is to be processed
     * @param sourceId
     *            The source Id is a unique identifier for the the source code.
     *            The code insertion tool uses the class+method name as the
     *            source identifier.
     * @param probeId
     *            The probe Id is a unique identifier with in the code. The
     *            source line will be used as the probe Id. The identifier needs
     *            to be unique within the file, when the file changes and the
     *            code location has changed within the source file, the files
     *            does not need to be updated. The probe Id simply needs to
     *            maintained unique within the file. If the resequencing of the
     *            source is needed, the tool can be used to resequence the probe
     *            Ids to be consistent with the source file.
     * @param objectArray
     *            Array of objects supplied by the caller to be used during the
     *            processing of the diagnostic module.
     */
    void processException(Throwable th, String sourceId, String probeId, Object[] objectArray);

    /**
     * Filter method used to process a Throwable from user code.
     * <p>
     * 
     * @param th
     *            The exception from the caller which is to be processed
     * @param sourceId
     *            The source Id is a unique identifier for the the source code.
     *            The code insertion tool uses the class+method name as the
     *            source identifier.
     * @param probeId
     *            The probe Id is a unique identifier with in the code. The
     *            source line will be used as the probe Id. The identifier needs
     *            to be unique within the file, when the file changes and the
     *            code location has changed within the source file, the files
     *            does not need to be updated. The probe Id simply needs to
     *            maintained unique within the file. If the resequencing of the
     *            source is needed, the tool can be used to resequence the probe
     *            Ids to be consistent with the source file.
     * @param callerThis
     *            The callerThis parameter corresponds to the invokers this
     *            pointer. The pointer will be processed by the component's
     *            diagnostic module.
     * @param objectArray
     *            Array of object supplied by the caller to be used during the
     *            processing of the diagnostic module.
     */
    void processException(Throwable th, String sourceId, String probeId, Object callerThis, Object[] objectArray);

    /**
     * Initialize FFDC service from set of initial properties
     * 
     * @param config
     */
    void init(LogProviderConfig config);

    /**
     * Allow new incidents to be reported and if necessary logs to be rolled.
     */
    void rollLogs();

    /**
     * Stop FFDC service
     */
    void stop();

    /**
     * @return File object for the directory that ffdc logs should be placed in
     */
    File getFFDCLogLocation();

    /**
     * @param config
     */
    void update(LogProviderConfig config);
}
