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

/**
 * @file
 *
 * JNI assorted routines that interface with z/OS Workload Management Services
 * to perform unauthorzied tasks.

 */
#include <assert.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/__wlm.h>             // C interfaces to WLM services



// --------------------------------------------------------------------
// Unauthorized JNI function declaration and export
// --------------------------------------------------------------------



// --------------------------------------------------------------------
// Module scoped data
// --------------------------------------------------------------------

// Upcall information
static jclass wlmServiceResultsClass = NULL;
static jmethodID setResultsMethodID = NULL;


//----------------------------------------------------------------------
//  This function is called from Java when libbgzzfat.so is loaded by java
//----------------------------------------------------------------------
//#pragma export(JNI_OnLoad)
//JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM * jvm,void* reserved ) {
//    printf("\n***bbgzzfat -- Onload called***");
//}

/**
 * Test method
 *
 * web/ZFatNativeHelper.ntv_le_test(I)I
 *
 */
#pragma export(Java_test_common_zos_ZFatNativeHelper_ntv_1le_1test)
JNIEXPORT jint JNICALL Java_test_common_zos_ZFatNativeHelper_ntv_1le_1test(JNIEnv* env, jclass jobj, jint inInt) {
    int localInt = inInt+1;
    return localInt;
}


/*-------------------------------------------------------------------*/
/*                                                                   */
/* Extract current Enclave Token                                     */
/*                                                                   */
/*-------------------------------------------------------------------*/
#pragma pack(1)
typedef struct extract_return_data_struct
{
    int           returnCode;
    int           out_errno;
    int           out_errno2;
    unsigned int  _rsvd1;
    wlmetok_t     currentToken;
} ExtractReturnData;
#pragma pack(reset)

#pragma export(Java_test_common_zos_ZFatNativeHelper_ntv_1extractWorkUnit)
JNIEXPORT jbyteArray JNICALL Java_test_common_zos_ZFatNativeHelper_ntv_1extractWorkUnit(JNIEnv* env, jclass jobj) {
  ExtractReturnData  returnData;
  jbyteArray         javaReturnData = NULL;

  memset(&returnData, 0, sizeof(returnData));

  returnData.returnCode = ExtractWorkUnit( &(returnData.currentToken) );

  if (returnData.returnCode != 0) {
      returnData.out_errno = errno;
      returnData.out_errno2 = __errno2();
  }

  javaReturnData = (*env)->NewByteArray(env, sizeof(returnData));
  if (javaReturnData == NULL) return NULL;

  (*env)->SetByteArrayRegion(
      env,
      javaReturnData,
      0,
      sizeof(returnData),
      (jbyte*) &returnData);

  return javaReturnData;

}

/*-------------------------------------------------------------------*/
/*                                                                   */
/* Query the enclave for information (ex. Transaction class)         */
/*                                                                   */
/*-------------------------------------------------------------------*/
typedef struct query_return_data_struct
{
    int           returnCode;
    int           out_errno;
    int           out_errno2;
    int           __rsvd1;
    char          transactionClass[8];
    char          user[8];
} QueryReturnData;

#pragma export(Java_test_common_zos_ZFatNativeHelper_ntv_1queryWorkUnitClassification)
JNIEXPORT jbyteArray JNICALL Java_test_common_zos_ZFatNativeHelper_ntv_1queryWorkUnitClassification(JNIEnv* env, jclass jobj, jbyteArray etoken) {

  wlmetok_t                            nativeToken;
  struct sysec                         queryResults;
  int                                  queryResultsLength;
  QueryReturnData                      nativeResults;
  jbyteArray                           javaResults = NULL;

  //-------------------------------------------------------------------
  // When there's no enclave, there's no classification info
  //-------------------------------------------------------------------
  if (etoken == NULL) return NULL;

  (*env)->GetByteArrayRegion(
      env,
      etoken,
      0,
      sizeof(nativeToken),
      (jbyte*) &nativeToken);

  memset(&queryResults, 0, sizeof(queryResults));
  memset(&nativeResults, 0, sizeof(nativeResults));

  queryResultsLength = sizeof(queryResults);
  nativeResults.returnCode = QueryWorkUnitClassification(
      &nativeToken,
      &queryResults,
      &queryResultsLength);

  if (nativeResults.returnCode != 0)
  {
      nativeResults.out_errno = errno;
      nativeResults.out_errno2 = __errno2();
  } else {
      memcpy(
          &nativeResults.transactionClass,
          &queryResults.ecd_char_field1._ecdtrxc,
          sizeof(nativeResults.transactionClass));
      memcpy(
          &nativeResults.user,
          &queryResults.ecd_char_field1._ecduser,
          sizeof(nativeResults.user));
  }

  javaResults = (*env)->NewByteArray(env, sizeof(nativeResults));
  if (javaResults == NULL) return NULL;

  (*env)->SetByteArrayRegion(
      env,
      javaResults,
      0,
      sizeof(nativeResults),
      (jbyte*) &nativeResults);

  return javaResults;
}

