/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

#ifndef UTIL_JNI_H_
#define UTIL_JNI_H_

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

/**
 * @file
 * JNI utility functions and macros.
 */

/**
 * Creates a Java exception of the given type with the given message and sets it as the active
 * exception in the JNI environment, to be thrown when control returns to Java.
 *
 * @param env       The JNIEnv object.
 * @param name      The class name of the exception (e.g. "java/lang/NullPointerException").
 * @param msg       The exception message.
 */
#pragma inline(setNewExceptionByName)
static void setNewExceptionByName(JNIEnv* env, const char* name, const char* msg) {
    char* asciiName = alloca(strlen(name) + 1);    
    char* asciiMsg = alloca(strlen(msg) + 1);    
    strcpy(asciiName, name);
    strcpy(asciiMsg, msg);
    __etoa(asciiName);
    __etoa(asciiMsg);
    jclass cls = (*env)->FindClass(env, asciiName);
    // if cls is NULL, an exception has already been thrown 
    if (cls != NULL) {
        (*env)->ThrowNew(env, cls, asciiMsg);
    }
    // free the local ref 
    (*env)->DeleteLocalRef(env, cls);
}

/**
 * Creates a new java/lang/RuntimeException indicating that a PC routine failed and sets it as
 * the active exception in the JNI environment, to be thrown when control returns to Java.
 *
 * @param env          The JNIEnv object.
 * @param functionName The PC routine that failed.
 * @param rc           The PC routine return code.
 */
#pragma inline(setNewPCRoutineFailedException)
static void setNewPCRoutineFailedException(JNIEnv* env, const char* functionName, int rc) {
    char* msg = "The PC routine %s failed with rc x%x";
    char* msgBuff = alloca(strlen(msg) + strlen(functionName) + 10);    // +10 for the rc and some breathing room.
    sprintf(msgBuff, msg, functionName, rc);
    setNewExceptionByName(env, "java/lang/RuntimeException", msgBuff);
}

/**
 * "Throws" a Java exception out of a JNI_try block.  This is done by creating a new Java exception
 * of the given type with the given message, setting it as the active exception in the JNI environment,
 * and then breaking out of the JNI_try switch block.
 */
#define JNI_throwByName(env, name, msg)    \
    setNewExceptionByName(env, name, msg); \
    break;

/**
 * "Throws" a java/lang/NullPointerException exception out of a JNI_try block.  This is done by creating
 * a new java/lang/NullPointerException with the given message, setting it as the active exception in the
 * JNI environment, and then breaking out of the JNI_try switch block.
 *
 * @param env       The JNIEnv object.
 * @param msg       The exception message.
 */
#define JNI_throwNullPointerException(env, msg)                        \
    setNewExceptionByName(env, "java/lang/NullPointerException", msg); \
    break;

/**
 * "Throws" a java/lang/IllegalArgumentException exception out of a JNI_try block.  This is done by creating
 * a new java/lang/IllegalArgumentException with the given message, setting it as the active exception in the
 * JNI environment, and then breaking out of the JNI_try switch block.
 *
 * @param env       The JNIEnv object.
 * @param msg       The exception message.
 */
#define JNI_throwIllegalArgumentException(env, msg)                        \
    setNewExceptionByName(env, "java/lang/IllegalArgumentException", msg); \
    break;

/**
 * "Throws" an exception out of a JNI_try block indicating that a PC routine invocation failed.  This is
 * done by creating a new java/lang/RuntimeException with a message indicating the failed PC routine
 * name and return code, setting it as the active exception in the JNI environment, and then breaking out
 * of the JNI_try switch block.
 *
 * @param env          The JNIEnv object.
 * @param functionName The PC routine that failed.
 * @param rc The PC routine return code
 */
#define JNI_throwPCRoutineFailedException(env, functionName, rc) \
    setNewPCRoutineFailedException(env, functionName, rc);       \
    break;

/**
 * Begins a pseudo JNI try-catch block.
 *
 * The JNI_try and associated JNI_* macros provide pseudo try-catch functionality 
 * around JNI calls. The try-catch is implemented as a switch{} block with break 
 * statements in it when JNI exceptions are detected.
 *
 * The typical usage pattern is:
 *
 *      JNI_try {
 *          JNI_GetByteArrayElements(...);
 *
 *          <other code that uses JNI data>
 *
 *      } JNI_catch(env);
 *
 * If an exception occurs under one of the JNI_* macros, control will jump to JNI_catch.
 * JNI_catch will detect the exception, save it, and clear it.  This allows the code
 * to do JNI clean up, a la:
 * 
 *      JNI_ReleaseByteArrayElements(...);
 *
 * After cleaning up, issue:
 *
 *      JNI_reThrow(env);
 *
 * This will re-raise the exception, if one occurred.
 *
 * !!NOTE: DO NOT USE THESE JNI_TRY/CATCH MACROS WITHIN LOOPS LIKE "for" OR "while"!!
 *         THE "break" STATEMENT WON'T WORK RIGHT -- IT WILL ONLY BREAK YOU OUT OF THE
 *         LOOP, NOT OUT OF THE SWITCH BLOCK USED BY JNI_TRY/JNI_CATCH!
 */
#define JNI_try                 \
    switch (0) {                \
        default:

/**
 * End a JNI pseudo try-catch block.
 * Detects, saves, and clears any pending Java exception.  Use JNI_reThrow
 * to re-raise the exception.
 *
 * @param env   The JNIEnv object.
 */
#define JNI_catch(env)                                          \
    } /* end switch */                                          \
    jthrowable jni_try_t = (*env)->ExceptionOccurred(env);      \
    if (jni_try_t != NULL) {                                    \
        (*env)->ExceptionClear(env);                            \
    }

/**
 * End a JNI pseudo try-catch block.
 * Same as JNI_catch, but doesn't re-declare jni_try_t, so you can have
 * multiple try/catch blocks in one function.
 *
 * @param env   The JNIEnv object.
 */
#define JNI_catch2(env)                                         \
    } /* end switch */                                          \
    jni_try_t = (*env)->ExceptionOccurred(env);                 \
    if (jni_try_t != NULL) {                                    \
        (*env)->ExceptionClear(env);                            \
    }

/**
 * Rethrows the exception that was detected, saved, and cleared by 
 * JNI_catch, if one occurred.  If an exception is indeed rethrown, 
 * then this macro issues a return statement to exit the native method
 * immediately.  If we don't exit immediately, then we increase the
 * likelihood of executing code that might clear the exception on us
 * (e.g. the TraceRecord code).
 *
 * @param env   The JNIEnv object.
 */
#define JNI_reThrowAndReturn(env)       \
    if (jni_try_t != NULL ) {           \
        (*env)->Throw(env, jni_try_t);  \
        return;                         \
    }

/**
 * Wraps the call to JNIEnv->GetByteArrayElements and detects failures.
 * Must be used in conjunction with JNI_try.
 *
 * @param cvar      The c variable (type jbyte *).
 * @param env       The JNIEnv object.
 * @param jvar      The java object (type jbyteArray).
 * @param npeMsg    A string message for the NullPointerException that is raised
 *                  if jvar is NULL.  If npeMsg is NULL, then no NullPointerException
 *                  will be raised (i.e. NULL jvar is allowed).
 * @param cdefault  A c variable that is assigned to cvar if jvar is NULL.
 *                  Note: if npeMsg is defined, then this parm is ignored. 
 *                  A NPE will be raised if jvar is NULL.
 *
 * Possible failures: OutOfMemoryError.
 */
#define JNI_GetByteArrayElements(cvar, env, jvar, npeMsg, cdefault)     \
    if (jvar != NULL) {                                                 \
        cvar = (*env)->GetByteArrayElements(env, jvar, NULL);           \
        if (cvar == NULL) {                                             \
            break; /* exception - break out of JNI_try */               \
        }                                                               \
    } else if (npeMsg != NULL) {                                        \
        JNI_throwNullPointerException(env, npeMsg);                     \
    } else {                                                            \
        cvar = cdefault;                                                \
    }

/**
 * Wraps the call to JNIEnv->GetArrayLength and detects failures.
 * Must be used in conjunction with JNI_try.
 *
 * @param cvar      The c variable (type jsize).
 * @param env       The JNIEnv object.
 * @param jvar      The java object (type jbyteArray).
 * @param npeMsg    A string message for the NullPointerException that is raised
 *                  if jvar is NULL.  If npeMsg is NULL, then no NullPointerException
 *                  will be raised (i.e. NULL jvar is allowed).
 * @param cdefault  A c variable that is assigned to cvar if jvar is NULL.
 *                  Note: if npeMsg is defined, then this parm is ignored. 
 *                  A NPE will be raised if jvar is NULL.
 *
 * Possible failures: NullPointerException if jvar is NULL.
 */
#define JNI_GetArrayLength(cvar, env, jvar, npeMsg, cdefault)           \
    if (jvar != NULL) {                                                 \
        cvar = (*env)->GetArrayLength(env, jvar);                       \
    } else if (npeMsg != NULL) {                                        \
        JNI_throwNullPointerException(env, npeMsg);                     \
    } else {                                                            \
        cvar = cdefault;                                                \
    }

/**
 * Wraps the call to JNIEnv->NewByteArray and detects failures.
 * Must be used in conjunction with JNI_try.
 *
 * @param cvar      The c variable (type jbyte *).
 * @param env       The JNIEnv object.
 * @param len       The length to allocate.
 *
 * Possible failures: OutOfMemoryError.
 */
#define JNI_NewByteArray(cvar, env, len)    \
    cvar = (*env)->NewByteArray(env, len);  \
    if (cvar == NULL) {                     \
        break;  /* exception occurred */    \
    }

/**
 * Wraps the call to JNIEnv->SetByteArrayRegion and detects failures
 * Must be used in conjunction with JNI_try.
 *
 * @param env       The JNIEnv object.
 * @param jvar      The java object (type jbyteArray).
 * @param begin     The index at which to begin copying.
 * @param len       The length to copy.
 * @param cvar      The c variable (type jbyte *).
 *
 * Possible failures: ArrayIndexOutOfBoundsException.
 */
#define JNI_SetByteArrayRegion(env, jvar, begin, len, cvar)     \
    (*env)->SetByteArrayRegion(env, jvar, begin, len, cvar);    \
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {              \
        break;                                                  \
    }

/**
 * Wraps the call to JNIEnv->ReleaseByteArrayElements().  
 * 
 * @param env       The JNIEnv object.
 * @param jvar      The java object (type jbyteArray).
 * @param cvar      The c variable (type jbyte *).
 * @param cdefault  A c variable that *may have been* assigned to cvar by 
 *                  JNI_GetByteArrayElements, in the event that jvar is NULL.
 *                  This macro checks if cvar == cdefault, and if so, it does 
 *                  NOT call ReleaseByteArrayElements.
 */
#define JNI_ReleaseByteArrayElements(env, jvar, cvar, cdefault) \
    if (cvar != NULL && cvar != (jbyte*) cdefault) {            \
        (*env)->ReleaseByteArrayElements(env, jvar, cvar, 0);   \
    }

 /**
  * Wraps the call to JNIEnv->Get<PrimitiveType>ArrayRegion and detects failures.
  * Must be used in conjunction with JNI_try.
  *
  * @param cvar      The c variable (type jbyte *, jlong *, ...).
  * @param env       The JNIEnv object.
  * @param jvar      The java object (type jbyteArray, jlongArray, ...).
  * @param start     The starting index.
  * @param len       The number of elements to be copied.
  * @param npeMsg    A string message for the NullPointerException that is raised
  *                  if jvar is NULL.  If npeMsg is NULL, then no NullPointerException
  *                  will be raised (i.e. NULL jvar is allowed).
  *
  * Possible failures: OutOfMemoryError.
  */
#define JNIGetxxxArrayRegion(tag, env, jvar, start, len, cvar, npeMsg)   \
     if (jvar != NULL) {                                                 \
         (*env)->Get##tag##ArrayRegion(env, jvar, start, len, cvar);     \
     } else if (npeMsg != NULL) {                                        \
         JNI_throwNullPointerException(env, npeMsg);                     \
     }


 /**
  * Wraps the call to JNIEnv->New<PrimitiveType>Array and detects failures.
  * Must be used in conjunction with JNI_try.
  *
  * @param cvar      The c variable (type jbyte *, jlong *, ...).
  * @param env       The JNIEnv object.
  * @param len       The length to allocate.
  *
  * Possible failures: OutOfMemoryError.
  */
 #define JNI_NewxxxArray(tag, cvar, env, len)    \
     cvar = (*env)->New##tag##Array(env, len);   \
     if (cvar == NULL) {                         \
         break;  /* exception occurred */        \
     }


#endif /* UTIL_JNI_H_ */
