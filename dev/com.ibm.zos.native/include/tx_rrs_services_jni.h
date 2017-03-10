/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#include <jni.h>

//---------------------------------------------------------------------
// Registration and deregistration callback methods.
//---------------------------------------------------------------------

/**
 * Registration callback used to resolve java object references.
 *
 * @param env The JNI environment for the calling thread.
 * @param myClazz The class for which deregistration is taking place.
 * @param extraInfo The context information from the caller.
 *
 * @return JNI_OK on success; JNI_ERR or on error
 */
int javaEnvironmentRegistration(JNIEnv* env, jclass myClazz, jobjectArray extraInfo);

/**
 * Deregistration callback used java object reference cleanup.
 *
 * @param env The calling thread's JNI environment.
 * @param clazz The class for which deregistration is taking place.
 * @param extraInfo The context provided to the registration function.
 *
 * @return JNI_OK
 */
int javaEnvironmentDeregistration(JNIEnv* env, jclass clazz, jobjectArray extraInfo);

//-----------------------------------------------------------------------------
// JNI function declarations.
//-----------------------------------------------------------------------------

/**
 * Begins a transaction with RRS.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param transactionMode The transaction mode: Local, Global.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_beginTransaction)
JNIEXPORT jobject JNICALL
ntv_beginTransaction(JNIEnv* env,
                     jclass clazz,
                     jint transactionMode);

/**
 * Ends the unit of recovery pointed to by the specified UR token.
 * The direction of the outcome (commit or backout) is determined
 * by the specified action.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param action The direction of the outcome.
 * @param currentUrToken The unit of recovery token that identifies the UR to
 *        be ended.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_endUR)
JNIEXPORT jint JNICALL
ntv_endUR(JNIEnv* env,
          jclass clazz,
          jint action,
          jbyteArray currentUrToken);

/**
 * Backs out the unit of recovery currently on the thread.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 */
#pragma export(ntv_backoutUR)
JNIEXPORT jint JNICALL
ntv_backoutUR(JNIEnv* env,
              jclass clazz);

/**
 * Retrieve the current settings of RRS-related environment
 * attributes for the unit of recovery associated with the specified context.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param ctxToken The context token associated to the UR whose
 *        information is being requested.
 * @param infoOptions The information to be retrieved.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_retireveSideInformationFast)
JNIEXPORT jobject JNICALL
ntv_retireveSideInformationFast(JNIEnv* env,
                                jclass clazz,
                                jbyteArray ctxToken,
                                jint infoOptions);

/**
 * Retrieves data for a unit of recovery.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriToken The token that identifies the resource
 *        manager’s interest in the unit of recovery whose data is to
 *        be retrieved.
 * @param stateOptions The states RRS may return for the specified unit
 *        of recovery.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_retireveURData)
JNIEXPORT jobject JNICALL
ntv_retireveURData(JNIEnv* env,
                   jclass clazz,
                   jbyteArray uriToken,
                   jint stateOptions);

/**
 * Registers the resource manager with the system (registration
 * services).
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param unregOption The option that indicates how the system is to
 *        determine that the resource manager is ending unexpectedly.
 * @param globalData The global data for the resource manager.
 *        The system passes this data to all exit routines for the resource
 *        manager.
 * @param rmNamePrefix The prefix name to be used to build the resource manager name.
 * @param rmNamePrefixLength The prefix name length.
 * @param rmNameSTCK The timestamp (STCK) to be used to build the resource manager name.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_registerResourceManager)
JNIEXPORT jobject JNICALL
ntv_registerResourceManager(JNIEnv* env,
                            jclass clazz,
                            jint unregOption,
                            jbyteArray globalData,
                            jbyteArray rmNamePrefix,
                            jint rmNamePrefixLength,
                            jbyteArray rmNameSTCK);

/**
 * Unregisters the resource manager with the system (registration
 * services).
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmName The name that identifies the resource manager to be
 *        unregistered.
 * @param rmToken The token that identifies the resource manager to
 *        be unregistered.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_unregisterResourceManager)
JNIEXPORT jint JNICALL
ntv_unregisterResourceManager(JNIEnv* env,
                              jclass clazz,
                              jbyteArray rmName,
                              jbyteArray rmToken);

/**
 * Registers with the context services and RRS services exit managers.
 * For context services, no exits are registered. For RRS services, only
 * the required exits are registered (exit failed, commit, backout, and
 * prepare). The exit provided is in the server authorized function
 * module.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmName The resource manager name whose exit information is to be set.
 * @param rmToken The token that identifies the resource manager.
 * @param recovery Indicates whether or not this calls is being made for a
 *         recovering resource manager.
 *
 * @return The java object containing the service return code and the
 *         metadata logging allowed indicator.
 */
#pragma export(ntv_setExitInformation)
JNIEXPORT jobject JNICALL
ntv_setExitInformation(JNIEnv* env,
                       jclass clazz,
                       jbyteArray rmName,
                       jbyteArray rmToken,
                       jboolean recovery);

/**
 * Begins the resource manager's restart.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmToken The token that identifies the resource manager.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.s
 */
#pragma export(ntv_beginRestart)
JNIEXPORT jint JNICALL
ntv_beginRestart(JNIEnv* env,
                 jclass clazz,
                 jbyteArray rmToken);

/**
 * Ends resource manager restart.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmToken The token that identifies the resource manager.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_endRestart)
JNIEXPORT jint JNICALL
ntv_endRestart(JNIEnv* env,
               jclass clazz,
               jbyteArray rmToken);

/**
 * Retrieves the log name associated to the specified resource manager.
 * Usually called before the call to begin restart.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmToken The token that identifies the resource manager.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_retrieveLogName)
JNIEXPORT jobject JNICALL
ntv_retrieveLogName(JNIEnv* env,
                    jclass clazz,
                    jbyteArray rmToken);

/**
 * Sets the log name associated to the specified resource manager.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmToken The token that identifies the resource manager.
 * @param rmLogNameLength The resource manager’s log name length.
 * @param rm_logName The resource manager’s log name.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_setLogName)
JNIEXPORT jint JNICALL
ntv_setLogName(JNIEnv* env,
               jclass clazz,
               jbyteArray rmToken,
               jint rmLogNameLength,
               jbyteArray rm_logName);

/**
 * Retrieves the work identifier (UWID/XID) for a unit of recovery.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token for the unit of recovery interest that
 *        identifies the unit of recovery whose work id is to be retrieved.
 *        The token points into the native registry.
 * @param retrieve_option The option that indicates to retrieve the current UWID
 *        or the next UWID.
 * @param generate_Option The option that specifies the action RRS is to take if
 *        a UWID has not been set by a call to the SetWorkIdentifier service
 *        or generated by RRS.
 * @param uwidType The type of the UWID to be retrieved.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_retrieveWorkIdentifier)
JNIEXPORT jobject JNICALL
ntv_retrieveWorkIdentifier(JNIEnv* env,
                           jclass clazz,
                           jbyteArray uriRegistryToken,
                           jint retrieve_option,
                           jint generate_Option,
                           jint uwidType);

/**
 * Set the work identifier (UWID/XID) for a unit of recovery.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param ur_or_uriToken The token for the unit of recovery or unit of recovery
 *        interest that identifies the unit of recovery whose work id is to
 *        be set.
 * @param set_option The option that identifies the work id to be set (current/next).
 * @param workId_type The work ID type.
 * @param xidLength The work ID length.
 * @param xid The work ID.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_setWorkIdentifier)
JNIEXPORT jint JNICALL
ntv_setWorkIdentifier(JNIEnv* env,
                      jclass clazz,
                      jbyteArray ur_or_uriToken,
                      jint set_option,
                      jint workId_type,
                      jint xidLength,
                      jbyteArray xid);

/**
 * Expresses an interest, either protected or unprotected,
 * in a unit of recovery
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmToken The token that identifies the resource manager
 * @param ctx_token The token that identifies the context associated with the
 *        unit of recovery.
 * @param interestOptions The various options that determine how RRS
 *        will process this interest.
 * @param non_pdata The non persistent interest data for the
 *        resource manager’s interest.
 * @param ur_pdata The persistent interest data for the
 *        resource manager’s interest in the UR.
 * @param pdata_length The persistent data length.
 * @param xid The transaction ID (work id).
 * @param xidLength The transaction ID (work id) length.
 * @param parent_ur_token The token that identifies the unit of recovery
 *        associated to the parent when in cascaded mode.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_expressUrInterest)
JNIEXPORT jobject JNICALL
ntv_expressUrInterest(JNIEnv* env,
                      jclass clazz,
                      jbyteArray rmToken,
                      jbyteArray ctx_token,
                      jint interestOptions,
                      jbyteArray non_pdata,
                      jbyteArray ur_pdata,
                      jint pdata_length,
                      jbyteArray xid,
                      jint xidLength,
                      jbyteArray parent_ur_token);

/**
 * Retrieves side information for an interest in a unit of recovery.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token which is used to look up the URI token in
 *        the native registry.  The URI token represents an interest in the unit
 *        of recovery or unit of recovery whose information is to be retrieved.
 * @param info_ids The ids for the data to be retrieved.
 * @param info_id_count The number of ids passed to the routine.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_retrieveSideInformation)
JNIEXPORT jobject JNICALL
ntv_retrieveSideInformation(JNIEnv* env,
                            jclass clazz,
                            jbyteArray uriRegistryToken,
                            jintArray info_ids,
                            jint info_id_count);

/**
 * Retrieve information about the resource manager's interest
 * in an incomplete, protected unit of recovery.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmToken The token that identifies the resource manager whose
 *        unit of recovery interest data is to be retrieved.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_retrieveUrInterest)
JNIEXPORT jobject JNICALL
ntv_retrieveUrInterest(JNIEnv* env,
                       jclass clazz,
                       jbyteArray rmToken);

/**
 * Establishes environmental settings for RRS.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param stoken The space token of the address space for which the
 *        resource manager is establishing address space scope environment
 *        settings.
 * @param elementCount The number of elements in the environment-setting
 *        array, which consists of the environment id, environment value,
 *        and environment protection parameters.
 * @param envIds The one or more identifiers to set. Each identifier supplies
 *        an environment attribute that is to be set.
 * @param envIdValues The value for each identifier on the environment id parameter.
 * @param protectionValues The protection value for each identifier in the
 *        environment id parameter.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_setEnvironment)
JNIEXPORT jint JNICALL
ntv_setEnvironment(JNIEnv* env,
                   jclass clazz,
                   jbyteArray stoken,
                   jint elementCount,
                   jintArray envIds,
                   jintArray envIdValues,
                   jintArray protectionValues);

/**
 * Tell RRS to prepare the unit of recovery associated with the specified
 * unit of recovery interest.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the URI in the native
 *        registry.  The URI represents the resource manager’s interest in a unit of
 *        recovery.
 * @param ctxTokenRegistryToken The token used to look up the context token in
 *        the native registry.  The context token is used to express an interest
 *        in the context, in case the UR goes in-doubt and we have to remove
 *        it from the current task in the private owner context termination
 *        exit.
 * @param rmToken The resource manager token.
 * @param log_option The option that indicates how RRS is to process
 *        log entries for the unit of recovery.
 *
 * @return An object encapsulating the return codes from the ATR4APRP and
 *         CTX4EINT services, plus the context interest token returned from
 *         the CTX4EINT service if CTX4EINT and ATR4APRP completed successfully.
 */
#pragma export(ntv_prepareAgentUR)
JNIEXPORT jobject JNICALL
ntv_prepareAgentUR(JNIEnv* env,
                   jclass clazz,
                   jbyteArray uriRegistryToken,
                   jbyteArray ctxTokenRegistryToken,
                   jbyteArray rmToken,
                   jint log_option);

/**
 * Tell RRS to commit the unit of recovery associated with the specified
 * unit of recovery interest.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the unit of recovery
 *        interest (URI) token in the native registry.  The URI token identifies
 *        an instance of the resource manager’s interest in a unit of
 *        recovery.
 * @param ciRegistryToken The token used to look up the context interest token
 *        in the native registry.  A CI token will exist if the UR was in-doubt.
 * @param log_option The option that indicates how RRS is to process
 *        log entries for the unit of recovery.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_commitAgentUR)
JNIEXPORT jint JNICALL
ntv_commitAgentUR(JNIEnv* env,
                  jclass clazz,
                  jbyteArray uriRegistryToken,
                  jbyteArray ciRegistryToken,
                  jint log_option);

/**
 * Tells RRS to initiate and complete a syncpoint operation for the unit of
 * recovery associated with the specified UR interest.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the unit of recovery
 *        interest (URI) token in the native registry.  The URI token identifies
 *        an instance of the resource manager’s interest in a unit of
 *        recovery.
 * @param log_option The option that indicates how RRS is to process
 *        log entries for the unit of recovery.
 * @param commit_options The option that determines how RRS is to perform
 *        the delegated commit request.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_delegateCommitAgentUR)
JNIEXPORT jint JNICALL
ntv_delegateCommitAgentUR(JNIEnv* env,
                          jclass clazz,
                          jbyteArray uriRegistryToken,
                          jint log_option,
                          int commit_options);

/**
 * Tell RRS to back out the unit of recovery associated with the specified
 * unit of recovery interest.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the unit of recovery
 *        interest (URI) token in the native registry.  The URI token identifies
 *        an instance of the resource manager’s interest in a unit of
 *        recovery.
 * @param ciRegistryToken The token used to look up the context interest token
 *        in the native registry.  A CI token will exist if the UR was in-doubt.
 * @param log_option The option that indicates how RRS is to process
 *        log entries for the unit of recovery.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_backoutAgentUR)
JNIEXPORT jint JNICALL
ntv_backoutAgentUR(JNIEnv* env,
                   jclass clazz,
                   jbyteArray uriRegistryToken,
                   jbyteArray ciRegistryToken,
                   jint log_option);

/**
 * Tells RRS to delete the SDSRM’s interest in the specified
 * unit of recovery, and depending on the log_option value,
 * delete any log entries that exist.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the unit of recovery
 *        interest (URI) token in the native registry.  The URI token identifies
 *        an instance of the resource manager’s interest in a unit of
 *        recovery.
 * @param log_option The option that indicates how RRS is to process
 *        log entries for the unit of recovery.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_forgetAgentURInterest)
JNIEXPORT jint JNICALL
ntv_forgetAgentURInterest(JNIEnv* env,
                          jclass clazz,
                          jbyteArray uriRegistryToken,
                          jint log_option);

/**
 * Allows the resource manager to initiate asynchronous processing, and
 * return to RRS with a return code that indicates a deferred response.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the unit of recovery
 *        interest (URI) token in the native registry.  The URI token identifies
 *        an instance of the resource manager’s interest in a unit of
 *        recovery.
 * @param exit_number The exit number to asynchronously process.
 * @param completion_code the response code from the asynchronous exit
 *        routine that has 'completed' processing.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_postDeferredURExit)
JNIEXPORT jint JNICALL
ntv_postDeferredURExit(JNIEnv* env,
                       jclass clazz,
                       jbyteArray uriRegistryToken,
                       jint exit_number,
                       jint completion_code);

/**
 * Tells RRS how to process an interest in an incomplete unit of recovery.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the unit of recovery
 *        interest (URI) token in the native registry.  The URI token identifies
 *        an instance of the resource manager’s interest in a unit of
 *        recovery.
 * @param response_code The code that indicates how RRS is to respond to
 *        the unit of recovery interest.
 * @param non_pdata The non-persistent interest data.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_respondToRetrievedInterest)
JNIEXPORT jint JNICALL
ntv_respondToRetrievedInterest(JNIEnv* env,
                               jclass clazz,
                               jbyteArray uriRegistryToken,
                               jint response_code,
                               jbyteArray non_pdata);

/**
 * Sets persistent interest data for a protected interest in a
 * unit of recovery.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the unit of recovery
 *        interest (URI) token in the native registry.  The URI token identifies
 *        an instance of the resource manager’s interest in a unit of
 *        recovery.
 * @param pdataLength The persistent interest data length.
 * @param persistentData The persistent interest data.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_setPersistentInterestData)
JNIEXPORT jint JNICALL
ntv_setPersistentInterestData(JNIEnv* env,
                              jclass clazz,
                              jbyteArray uriRegistryToken,
                              jint pdataLength,
                              jbyteArray persistentData);

/**
 * Defines the role the resource manager will play in processing a UR.
 * It also allows for exit pre-voting.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the unit of recovery
 *        interest (URI) token in the native registry.  The URI token identifies
 *        an instance of the resource manager’s interest in a unit of
 *        recovery.
 * @param prepare_exitCode The code that specifies whether RRS
 *        is to invoke the prepare exit routine.
 * @param commit_exitCode The code that specifies whether RRS
 *        is to invoke the commit exit routine.
 * @param backout_exitCode The code that specifies whether RRS
 *        is to invoke the backout exit routine.
 * @param role The role the resource manager is to take for the
 *        specified UR interest.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_setSyncpointControls)
JNIEXPORT jint JNICALL
ntv_setSyncpointControls(JNIEnv* env,
                         jclass clazz,
                         jbyteArray uriRegistryToken,
                         jint prepare_exitCode,
                         jint commit_exitCode,
                         jint backout_exitCode,
                         jint role);

/**
 * Sets side information for an interest in a unit of recovery.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param uriRegistryToken The token used to look up the unit of recovery
 *        interest (URI) token in the native registry.  The URI token identifies
 *        an instance of the resource manager’s interest in a unit of
 *        recovery.
 * @param elementCount The number of elements to be set.
 * @param infoIds The identifiers that set the side information.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_setSideInformation)
JNIEXPORT jint JNICALL
ntv_setSideInformation(JNIEnv* env,
                       jclass clazz,
                       jbyteArray uriRegistryToken,
                       jint elementCount,
                       jintArray infoIds);


/**
 * Sets the metadata associated to the specified resource manager.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmToken The token that identifies the resource manager.
 * @param metadataLength The metadata length.
 * @param metadata The metadata.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_setRMMetadata)
JNIEXPORT jint JNICALL
ntv_setRMMetadata(JNIEnv* env,
                  jclass clazz,
                  jbyteArray rmToken,
                  jint metadataLength,
                  jbyteArray metadata);

/**
 * Retrieves the metadata associated to the specified resource manager.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmToken The token that identifies the resource manager.
 *
 * @return The java object containing data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_retrieveRMMetadata)
JNIEXPORT jobject JNICALL
ntv_retrieveRMMetadata(JNIEnv* env,
                       jclass clazz,
                       jbyteArray rmToken);

/**
 * Creates a privately managed context.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param rmToken The token that identifies the resource manager on whose
 *        behalf the context is to be created.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_beginContext)
JNIEXPORT jobject JNICALL
ntv_beginContext(JNIEnv* env,
                 jclass clazz,
                 jbyteArray rmToken);

/**
 * Switches the context on the current thread with the one specified.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param ctxRegistryToken The token into the native registry which contains
 *                         the context to be put on the thread.
 *
 * @return The java object containing all data returned by the service or
 *         null if the method detected an error.
 */
#pragma export(ntv_contextSwitch)
JNIEXPORT jobject JNICALL
ntv_contextSwitch(JNIEnv* env,
                  jclass clazz,
                  jbyteArray ctxRegistryToken);

/**
 * Ends the specified context.
 *
 * @param env The JNI environment reference provided by the JVM.
 * @param clazz The object instance this method was invoked against.
 * @param ctxRegistryToken The token into the native registry which contains the
 *                         context token that identifies the context to be ended.
 * @param completionType The type of completion for the context.
 *
 * @return 0 if the RRS service completed successfully. A number greater
 *         than zero if the RRS service returned a bad return code.
 *         -1 if there was a native failure during the invocation process.
 */
#pragma export(ntv_endContext)
JNIEXPORT jint JNICALL
ntv_endContext(JNIEnv* env,
               jclass clazz,
               jbyteArray ctxRegistryToken,
               jint completionType);

/**
 * Retrieves the current context token for the calling task.
 *
 * @return An object encapsulating the return code from the service, and the
 *         context token.
 */
#pragma export(ntv_retrieveCurrentContextToken)
JNIEXPORT jobject JNICALL
ntv_retrieveCurrentContextToken(JNIEnv* env,
                                jclass clazz);

