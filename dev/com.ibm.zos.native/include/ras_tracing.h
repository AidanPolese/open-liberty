/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_RAS_TRACING_H
#define _BBOZ_RAS_TRACING_H

#include <limits.h>
#include <stdarg.h>
#include <string.h>

#include "gen/ihapsa.h"
#include "gen/ikjtcb.h"
#include "gen/ihastcb.h"

#ifdef __IBM_METAL__
#ifdef ANGEL_COMPILE
#include "angel_task_data.h"
#else
#include "server_task_data.h"
#endif
#endif

// Format for the module trace point definitions:
// ------------------------------------------------
//  A tracepoint is generated from the TP(xx)
//  define below. It adds the "xx" from the TP(xx)
//  call to the RAS_MODULE_CONST value.  A module
//  issuing trace defines a RAS_MODULE_CONST value
//  unique to its module/part.  Consider the
//  following format of the tracepoint:
//
//    ccmmmttt   -Where "cc" is the Component/Function
//                      "mmm" is the module/part reference
//                      "ttt" is the TP(xx) value within the part.
//
//  Component/Function  "cc" value
//  ------------------  ----------
//  Angel                   01
//  Server                  02
//  Security                03
//  Utilities               04
//  Transaction             05
//  ServerTrace             06
//  Comm                    07
//
// ------------------------------------------------
//
// The comments on the right of the #define are the trace.specification setting. It is in the
// format of trace.specification=<component>.<module>=level
//
// This is the setting for ALL native trace:                                     //zos.native=all
//
// Note: All values need to be specified in hexadecimal. When a new constant is added here make
// sure you also add it to the trace_definitions table below.
//

#define RAS_COMP_ANGEL                           0x01000000                      //zos.native.01
#define RAS_COMP_SERVER                          0x02000000                      //zos.native.02
#define RAS_COMP_SECURITY                        0x03000000                      //zos.native.03
#define RAS_COMP_UTILITIES                       0x04000000                      //zos.native.04
#define RAS_COMP_TRANSACTION                     0x05000000                      //zos.native.05
#define RAS_COMP_SERVERTRACE                     0x06000000                      //zos.trace.06
#define RAS_COMP_COMM                            0x07000000                      //zos.native.07

#define RAS_MODULE_ANGEL_MAIN                    (RAS_COMP_ANGEL + 0x1000)       //zos.native.01.001
#define RAS_MODULE_ANGEL_FUNCTIONS               (RAS_COMP_ANGEL + 0x2000)       //zos.native.01.002
#define RAS_MODULE_ANGEL_SERVER_PC               (RAS_COMP_ANGEL + 0x3000)       //zos.native.01.003
#define RAS_MODULE_ANGEL_FIXED_SHIM_PC           (RAS_COMP_ANGEL + 0x4000)       //zos.native.01.004
#define RAS_MODULE_ANGEL_SGOO_SERVICES           (RAS_COMP_ANGEL + 0x5000)       //zos.native.01.005
#define RAS_MODULE_ANGEL_CHECK_MAIN              (RAS_COMP_ANGEL + 0x6000)       //zos.native.01.006

#define RAS_MODULE_SERVER_UNAUTH_TEST_FUNCTIONS  (RAS_COMP_SERVER + 0x1000)      //zos.native.02.001
#define RAS_MODULE_SERVER_COMMAND_FUNCTIONS      (RAS_COMP_SERVER + 0x2000)      //zos.native.02.002
#define RAS_MODULE_SERVER_LAUNCHER               (RAS_COMP_SERVER + 0x4000)      //zos.native.02.004
#define RAS_MODULE_SERVER_NATIVE_SERVICE_TRACKER (RAS_COMP_SERVER + 0x5000)      //zos.native.02.005
#define RAS_MODULE_SERVER_COMMAND_JNI            (RAS_COMP_SERVER + 0x6000)      //zos.native.02.006
#define RAS_MODULE_SERVER_UTIL_REGISTRY          (RAS_COMP_SERVER + 0x8000)      //zos.native.02.008
#define RAS_MODULE_SERVER_ASYNC_IO               (RAS_COMP_SERVER + 0x9000)      //zos.native.02.009
#define RAS_MODULE_MVS_AIO_SERVICES              (RAS_COMP_SERVER + 0xA000)      //zos.native.02.00A
#define RAS_MODULE_SERVER_THREAD_TERM_MGR        (RAS_COMP_SERVER + 0xB000)      //zos.native.02.00B
#define RAS_MODULE_SERVER_UTIL_SORTEDCACHE       (RAS_COMP_SERVER + 0xC000)      //zos.native.02.00C
#define RAS_MODULE_SERVER_WLM_SERVICES_JNI       (RAS_COMP_SERVER + 0xD000)      //zos.native.02.00D
#define RAS_MODULE_SERVER_WLM_SERVICES           (RAS_COMP_SERVER + 0xE000)      //zos.native.02.00E
#define RAS_MODULE_SERVER_PRODUCT_REGISTRATION_JNI (RAS_COMP_SERVER + 0xF000)    //zos.native.02.00F
#define RAS_MODULE_SERVER_DIANOSTICS_FUNCTIONS   (RAS_COMP_SERVER + 0x10000)     //zos.native.02.010
#define RAS_MODULE_SERVER_IPT_STUBS              (RAS_COMP_SERVER + 0x11000)     //zos.native.02.011
#define RAS_MODULE_SERVER_DUMP_FUNCTIONS_JNI     (RAS_COMP_SERVER + 0x12000)     //zos.native.02.012
#define RAS_MODULE_SERVER_PETVET                 (RAS_COMP_SERVER + 0x13000)     //zos.native.02.013
#define RAS_MODULE_SERVER_PLO_SERVICES           (RAS_COMP_SERVER + 0x15000)     //zos.native.02.015
#define RAS_MODULE_SERVER_BYTEBUFFER             (RAS_COMP_SERVER + 0x16000)     //zos.native.02.016
#define RAS_MODULE_SERVER_KERNEL_FUNCTIONS       (RAS_COMP_SERVER + 0x17000)     //zos.native.02.017
#define RAS_MODULE_SERVER_KERNEL_FUNCTIONS_JNI   (RAS_COMP_SERVER + 0x18000)     //zos.native.02.018

#define RAS_MODULE_SECURITY_SAF_AUTHORIZATION    (RAS_COMP_SECURITY + 0x1000)    //zos.native.03.001
#define RAS_MODULE_SECURITY_SAF_REGISTRY         (RAS_COMP_SECURITY + 0x2000)    //zos.native.03.002
#define RAS_MODULE_SECURITY_SAF_AUTHENTICATION   (RAS_COMP_SECURITY + 0x3000)    //zos.native.03.003
#define RAS_MODULE_SECURITY_SAF_CREDENTIALS      (RAS_COMP_SECURITY + 0x4000)    //zos.native.03.004
#define RAS_MODULE_SECURITY_SAF_AUTHZ            (RAS_COMP_SECURITY + 0x5000)    //zos.native.03.005
#define RAS_MODULE_SECURITY_SAF_AUTHZ_JNI        (RAS_COMP_SECURITY + 0x6000)    //zos.native.03.006
#define RAS_MODULE_SECURITY_SAF_ACEE             (RAS_COMP_SECURITY + 0x7000)    //zos.native.03.007
#define RAS_MODULE_SECURITY_SAF_SANDBOX          (RAS_COMP_SECURITY + 0x8000)    //zos.native.03.008
#define RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD_JNI (RAS_COMP_SECURITY + 0x9000)  //zos.native.03.009
#define RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD   (RAS_COMP_SECURITY + 0xA000)    //zos.native.03.00A

#define RAS_MODULE_BPX_LOAD                      (RAS_COMP_UTILITIES + 0x1000)   //zos.native.04.001
#define RAS_MODULE_MVS_WTO                       (RAS_COMP_UTILITIES + 0x2000)   //zos.native.04.002
#define RAS_MODULE_MVS_SVCDUMP_SERVICES          (RAS_COMP_UTILITIES + 0x3000)   //zos.native.04.003
#define RAS_MODULE_MVS_LATCH                     (RAS_COMP_UTILITIES + 0x4000)   //zos.native.04.004
#define RAS_MODULE_MVS_IFAUSAGE                  (RAS_COMP_UTILITIES + 0x5000)   //zos.native.04.005
#define RAS_MODULE_HEAP_MANAGEMENT               (RAS_COMP_UTILITIES + 0x6000)   //zos.native.04.006
#define RAS_MODULE_SERVER_UTILS_JNI              (RAS_COMP_UTILITIES + 0x7000)   //zos.native.04.007
#define RAS_MODULE_SERVER_SMF_JNI                (RAS_COMP_UTILITIES + 0x8000)   //zos.native.04.008
#define RAS_MODULE_SERVER_UTILS_UNAUTH           (RAS_COMP_UTILITIES + 0x9000)   //zos.native.04.009

#define RAS_MODULE_TX_AUTHORIZED_RRS_SERVICES    (RAS_COMP_TRANSACTION + 0x1000) //zos.native.05.001
#define RAS_MODULE_TX_RRS_SERVICES_JNI           (RAS_COMP_TRANSACTION + 0x2000) //zos.native.05.002

#define RAS_MODULE_SERVER_TRACING_FUNCTIONS      (RAS_COMP_SERVERTRACE + 0x1000) //zos.trace.06.001

#define RAS_MODULE_SERVER_LCOM_SERVICES_JNI      (RAS_COMP_COMM + 0x1000)        //zos.native.07.001
#define RAS_MODULE_SERVER_LCOM_SERVICES          (RAS_COMP_COMM + 0x2000)        //zos.native.07.002
#define RAS_MODULE_SERVER_LCOM_QUEUE             (RAS_COMP_COMM + 0x3000)        //zos.native.07.003
#define RAS_MODULE_SERVER_LCOM_CLIENT            (RAS_COMP_COMM + 0x4000)        //zos.native.07.004
#define RAS_MODULE_SERVER_LCOM_FOOT              (RAS_COMP_COMM + 0x5000)        //zos.native.07.005
#define RAS_MODULE_SERVER_WOLA_SERVICES_JNI      (RAS_COMP_COMM + 0x6000)        //zos.native.07.006
#define RAS_MODULE_SERVER_WOLA_SERVICES          (RAS_COMP_COMM + 0x7000)        //zos.native.07.007
#define RAS_MODULE_SERVER_WOLA_SHARED_MEMORY_ANCHOR (RAS_COMP_COMM + 0x8000)     //zos.native.07.008
#define RAS_MODULE_SERVER_WOLA_REGISTRATION      (RAS_COMP_COMM + 0x9000)        //zos.native.07.009
#define RAS_MODULE_SERVER_WOLA_SERVICE_QUEUES    (RAS_COMP_COMM + 0xA000)        //zos.native.07.00A
#define RAS_MODULE_SERVER_WOLA_SHARED_MEMORY_ANCHOR_SERVER (RAS_COMP_COMM + 0xB000)  //zos.native.07.00B
#define RAS_MODULE_SERVER_WOLA_REGISTRATION_SERVER (RAS_COMP_COMM + 0xC000)      //zos.native.07.00C
#define RAS_MODULE_SERVER_WOLA_MESSAGE           (RAS_COMP_COMM + 0xD000)        //zos.native.07.00D
#define RAS_MODULE_SERVER_LCOM_CLEANUP           (RAS_COMP_COMM + 0xE000)        //zos.native.07.00E
#define RAS_MODULE_SERVER_WOLA_UNAUTH_SERVICES   (RAS_COMP_COMM + 0xF000)        //zos.native.07.00F

#define TP(num) (RAS_MODULE_CONST + (num))

//
// Only define the trace component definitions for LE enabled C.
//
#ifndef __IBM_METAL__
typedef struct Trace_Component_Definition {
    int moduleId;
    char* name;
    char* groups;
} Trace_Component_Definition;

//
// The following table contains the trace component definitions that will be
// used by the Java trace code to register trace components. When adding a new component or module
// make sure you update this table to include its RAS #define constant, Name, and the trace groups
// it belongs to. It is important that the names follow the naming hierarchy. Also don't forget
// to increment the NUM_DEFINITIONS.
//
// The name column should be zos.native. followed by the part name with the
// _'s replaced with .'s
// example
// server_command_jni.c would be zos.native.server.command.jni

#define NUM_DEFINITIONS 68
const Trace_Component_Definition trace_definitions[NUM_DEFINITIONS] = {
   //Comp/Module Id                                Name (the dots are important)                             Groups (use comma delimited format)
   {RAS_COMP_ANGEL,                                "zos.native.angel",                                       "zNative,zAngel"},
   {RAS_COMP_SERVER,                               "zos.native.server",                                      "zNative,zServer"},
   {RAS_COMP_SECURITY,                             "zos.native.security",                                    "zNative,zSecurity"},
   {RAS_COMP_UTILITIES,                            "zos.native.utilities",                                   "zNative,zUtilities"},
   {RAS_COMP_TRANSACTION,                          "zos.native.transaction",                                 "zNative,zTransaction"},
   {RAS_COMP_COMM,                                 "zos.native.comm",                                        "zNative,zComm"},
   {RAS_MODULE_ANGEL_MAIN,                         "zos.native.angel.main",                                  "zNative,zAngel"},
   {RAS_MODULE_ANGEL_FUNCTIONS,                    "zos.native.angel.functions",                             "zNative,zAngel"},
   {RAS_MODULE_ANGEL_SERVER_PC,                    "zos.native.angel.server.pc",                             "zNative,zAngel"},
   {RAS_MODULE_ANGEL_FIXED_SHIM_PC,                "zos.native.angel.fixed.shim.pc",                         "zNative,zAngel"},
   {RAS_MODULE_ANGEL_SGOO_SERVICES,                "zos.native.angel.sgoo.services",                         "zNative,zAngel"},
   {RAS_MODULE_SERVER_UNAUTH_TEST_FUNCTIONS,       "zos.native.server.unauth.test.functions",                "zNative,zServer"},
   {RAS_MODULE_SERVER_COMMAND_FUNCTIONS,           "zos.native.server.command.functions",                    "zNative,zServer,zConsole"},
   {RAS_MODULE_SERVER_DIANOSTICS_FUNCTIONS,        "zos.native.server.diagnostics.functions",                "zNative,zServer,zDiagnostics"},
   {RAS_MODULE_SERVER_LAUNCHER,                    "zos.native.server.launcher",                             "zNative,zServer"},
   {RAS_MODULE_SERVER_NATIVE_SERVICE_TRACKER,      "zos.native.server.native.service.tracker",               "zNative,zServer,zCore"},
   {RAS_MODULE_SERVER_COMMAND_JNI,                 "zos.native.server.command.jni",                          "zNative,zServer,zConsole"},
   {RAS_MODULE_SERVER_TRACING_FUNCTIONS,           "zos.trace.server.tracing.functions",                     "zTrace"},
   {RAS_MODULE_SERVER_UTIL_REGISTRY,               "zos.native.server.util.registry",                        "zNative,zServer"},
   {RAS_MODULE_SERVER_ASYNC_IO,                    "zos.native.server.async.io",                             "zNative,zServer,zAsyncio"},
   {RAS_MODULE_MVS_AIO_SERVICES,                   "zos.native.mvs.aio.services",                            "zNative,zServer,zAsyncio"},
   {RAS_MODULE_SERVER_THREAD_TERM_MGR,             "zos.native.server.thread.term.manager",                  "zNative,zServer"},
   {RAS_MODULE_SERVER_UTIL_SORTEDCACHE,            "zos.native.server.util.sortedcache",                     "zNative,zServer"},
   {RAS_MODULE_SERVER_WLM_SERVICES_JNI,            "zos.native.server.wlm.services.jni",                     "zNative,zServer,zOSWLMServices"},
   {RAS_MODULE_SERVER_WLM_SERVICES,                "zos.native.server.wlm.services",                         "zNative,zServer,zOSWLMServices"},
   {RAS_MODULE_SERVER_PRODUCT_REGISTRATION_JNI,    "zos.native.server.product.registration.jni",             "zNative,zServer"},
   {RAS_MODULE_SECURITY_SAF_AUTHORIZATION,         "zos.native.security.saf.authorization",                  "zNative,zSecurity"},
   {RAS_MODULE_SECURITY_SAF_REGISTRY,              "zos.native.security.saf.registry",                       "zNative,zServer,zSecurity,UserRegistry"},
   {RAS_MODULE_SECURITY_SAF_AUTHENTICATION,        "zos.native.security.saf.authentication",                 "zNative,zSecurity,UserRegistry,Credentials"},
   {RAS_MODULE_SECURITY_SAF_CREDENTIALS,           "zos.native.security.saf.credentials",                    "zNative,zSecurity,UserRegistry,Credentials"},
   {RAS_MODULE_SECURITY_SAF_AUTHZ,                 "zos.native.security.saf.authz",                          "zNative,zSecurity,Security.Authorization"},
   {RAS_MODULE_SECURITY_SAF_AUTHZ_JNI,             "zos.native.security.saf.authz.jni",                      "zNative,zSecurity,Security.Authorization"},
   {RAS_MODULE_SECURITY_SAF_ACEE,                  "zos.native.security.saf.acee",                           "zNative,zSecurity,Security.Authorization"},
   {RAS_MODULE_SECURITY_SAF_SANDBOX,               "zos.native.security.saf.sandbox",                        "zNative,zSecurity,Security.Authorization,Credentials"},
   {RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD_JNI,    "zos.native.security.saf.sync.to.thread.jni",             "zNative,zSecurity,Security.Authorization"},
   {RAS_MODULE_SECURITY_SAF_SYNC_TO_THREAD,        "zos.native.security.saf.sync.to.thread",                 "zNative,zSecurity,Security.Authorization"},
   {RAS_MODULE_BPX_LOAD,                           "zos.native.bpx.load",                                    "zNative,zUtilities"},
   {RAS_MODULE_MVS_WTO,                            "zos.native.mvs.utils",                                   "zNative,zUtilities"},
   {RAS_MODULE_MVS_SVCDUMP_SERVICES,               "zos.native.mvs.svcdump.services",                        "zNative,zUtilities"},
   {RAS_MODULE_MVS_LATCH,                          "zos.native.mvs.latch",                                   "zNative,zUtilities"},
   {RAS_MODULE_MVS_IFAUSAGE,                       "zos.native.mvs.ifausage",                                "zNative,zUtilities"},
   {RAS_MODULE_TX_AUTHORIZED_RRS_SERVICES,         "zos.native.tx.authorized.rrs.services",                  "zNative,zTransaction"},
   {RAS_MODULE_TX_RRS_SERVICES_JNI,                "zos.native.tx.rrs.services.jni",                         "zNative,zTransaction"},
   {RAS_MODULE_SERVER_IPT_STUBS,                   "zos.native.server.ipt.stubs",                            "zNative,zServer"},
   {RAS_MODULE_SERVER_DUMP_FUNCTIONS_JNI,          "zos.native.server.dump.functions.jni",                   "zNative,zServer"},
   {RAS_MODULE_HEAP_MANAGEMENT,                    "zos.native.heap.management",                             "zNative,zUtilities"},
   {RAS_MODULE_SERVER_LCOM_SERVICES_JNI,           "zos.native.server.lcom.services.jni",                    "zNative,zComm,zOSLocalChannel"},
   {RAS_MODULE_SERVER_LCOM_SERVICES,               "zos.native.server.lcom.services",                        "zNative,zComm,zOSLocalChannel"},
   {RAS_MODULE_SERVER_LCOM_QUEUE,                  "zos.native.server.local.comm.queue",                     "zNative,zComm,zOSLocalChannel"},
   {RAS_MODULE_SERVER_LCOM_CLIENT,                 "zos.native.server.local.comm.client",                    "zNative,zComm,zOSLocalChannel"},
   {RAS_MODULE_SERVER_LCOM_FOOT,                   "zos.native.server.local.comm.client",                    "zNative,zComm,zOSLocalChannel"},
   {RAS_MODULE_SERVER_UTILS_JNI,                   "zos.native.server.utils.jni",                            "zNative,zUtilities"},
   {RAS_MODULE_SERVER_UTILS_UNAUTH,                "zos.native.server.utils.unauth",                         "zNative,zUtilities"},
   {RAS_MODULE_SERVER_SMF_JNI,                     "zos.native.server.utils.jni",                            "zNative,zUtilities"},
   {RAS_MODULE_SERVER_PETVET,                      "zos.native.server.petvet",                               "zNative,zServer,zAsyncio"},
   {RAS_MODULE_SERVER_WOLA_SERVICES_JNI,           "zos.native.server.wola.services.jni",                    "zNative,zComm,zosLocalAdapters"},
   {RAS_MODULE_SERVER_WOLA_SERVICES,               "zos.native.server.wola.services",                        "zNative,zComm,zosLocalAdapters"},
   {RAS_MODULE_SERVER_WOLA_SHARED_MEMORY_ANCHOR,   "zos.native.server.wola.shared.memory.anchor",            "zNative,zComm,zosLocalAdapters"},
   {RAS_MODULE_SERVER_WOLA_REGISTRATION,           "zos.native.server.wola.registration",                    "zNative,zComm,zosLocalAdapters"},
   {RAS_MODULE_SERVER_WOLA_SERVICE_QUEUES,         "zos.native.server.wola.service.queues",                  "zNative,zComm,zosLocalAdapters"},
   {RAS_MODULE_SERVER_WOLA_SHARED_MEMORY_ANCHOR_SERVER, "zos.native.server.wola.shared.memory.anchor.server","zNative,zComm,zosLocalAdapters"},
   {RAS_MODULE_SERVER_WOLA_REGISTRATION_SERVER,    "zos.native.server.wola.registration.server",             "zNative,zComm,zosLocalAdapters"},
   {RAS_MODULE_SERVER_WOLA_MESSAGE,                "zos.native.server.wola.message",                         "zNative,zComm,zosLocalAdapters"},
   {RAS_MODULE_SERVER_LCOM_CLEANUP,                "zos.native.server.local.comm.cleanup",                   "zNative,zComm,zOSLocalChannel"},
   {RAS_MODULE_SERVER_PLO_SERVICES,                "zos.native.server.plo.services",                         "zNative,zServer,zAsyncio"},
   {RAS_MODULE_SERVER_BYTEBUFFER,                  "zos.native.server.bytebuffer",                           "zNative,zServer,zAsyncio"},
   {RAS_MODULE_SERVER_KERNEL_FUNCTIONS,            "zos.native.server.kernel.functions",                     "zNative,zServer"},
   {RAS_MODULE_SERVER_KERNEL_FUNCTIONS_JNI,        "zos.native.server.kernel.functions.jni",                 "zNative,zServer"}
  };
#endif // End trace definitions

// ------------------------------------------------
// Trace levels
// ------------------------------------------------
enum trc_level {
    trc_level_none = 0,
    trc_level_exception = 1,
    trc_level_basic = 2,
    trc_level_detailed = 3,
    trc_level_expand_to_fullword = INT_MAX
};

// ------------------------------------------------
// Trace data types
// ------------------------------------------------
enum trc_key {
    trc_key_raw_data = 1,
    trc_key_ebcdic_string = 2,
    trc_key_int = 3,
    trc_key_double = 4,
    trc_key_pointer = 5,
    trc_key_long = 6,
    trc_key_short = 7,
    trc_key_char = 8,
    trc_key_hex_int = 9,
    trc_key_hex_long = 10,
    trc_key_Max = SHRT_MAX
};

#define  TRACE_DATA_ITEM(key, len, data, desc)     (short) key,                        (short) (len > 0x7FF8 ? 0x7FF8 : len), data,             (short) strlen(desc), desc
#define  TRACE_DATA_RAWDATA(len, address, desc)    (short) trc_key_raw_data,           (short) (len > 0x7FF8 ? 0x7FF8 : len), (void *) address, (short) strlen(desc), desc
#define  TRACE_DATA_STRING(string, desc)           (short) trc_key_ebcdic_string,      (short) strlen(string),                string,           (short) strlen(desc), desc
#define  TRACE_DATA_INT(value, desc)               (short) trc_key_int,                (short) sizeof(int),                   (int) value,      (short) strlen(desc), desc
#define  TRACE_DATA_HEX_INT(value, desc)           (short) trc_key_hex_int,            (short) sizeof(int),                   (int) value,      (short) strlen(desc), desc
#define  TRACE_DATA_PTR32(value, desc)             (short) trc_key_hex_int,            (short) sizeof(int),                   (int) value,      (short) strlen(desc), desc
#define  TRACE_DATA_DOUBLE(value, desc)            (short) trc_key_double,             (short) sizeof(double),                (double) value,   (short) strlen(desc), desc
#define  TRACE_DATA_PTR(value, desc)               (short) trc_key_pointer,            (short) sizeof(void *),                (void *) value,   (short) strlen(desc), desc
#define  TRACE_DATA_LONG(value, desc)              (short) trc_key_long,               (short) sizeof(long),                  (long) value,     (short) strlen(desc), desc
#define  TRACE_DATA_HEX_LONG(value, desc)          (short) trc_key_hex_long,           (short) sizeof(long),                  (long) value,     (short) strlen(desc), desc
#define  TRACE_DATA_SHORT(value, desc)             (short) trc_key_short,              (short) sizeof(short),                 (short) value,    (short) strlen(desc), desc
#define  TRACE_DATA_CHAR(value, desc)              (short) trc_key_char,               (short) sizeof(char),                  (char) value,     (short) strlen(desc), desc
#define  TRACE_DATA_END_PARMS                      (int) 0

#define  TRACE_DATA_FILE                           TRACE_DATA_STRING(__FILE__, "File")
#define  TRACE_DATA_FUNCTION                       TRACE_DATA_STRING(__FUNCTION__, "Function")
#define  TRACE_DATA_LINENUM(desc)                  TRACE_DATA_INT(__LINE__, "Line number")
#define  TRACE_DATA_BOOLEAN(value, description)    TRACE_DATA_STRING( (value) ? "true" : "false", description)

#define  TRACE_DESC_FUNCTION_ENTRY                 "Entry: " __FUNCTION__
#define  TRACE_DESC_FUNCTION_EXIT                  "Exit: " __FUNCTION__

#define TraceRecord(trace_level, trace_event, description, ...) {      \
    TraceWrite(trace_level, trace_event, description, TRACE_DATA_STRING(description, "Description"), __VA_ARGS__);    \
}

void TraceWriteV(enum trc_level usr_level, int usr_event, char* event_desc, va_list usr_var_list);

#pragma inline(TraceWrite)
static void TraceWrite(enum trc_level usr_level, int usr_event, char* event_desc, ...) {
    va_list arg_list;

    va_start(arg_list, event_desc);
    TraceWriteV(usr_level, usr_event, event_desc, arg_list);
    va_end(arg_list);
}

/**
 * The name of the name token used to store the address of the
 * unittest trace level and fileDescriptor which is referenced
 * by the metal C trace path.
 */
#define RAS_UNITTEST_TRACE_LEVEL_TOKEN_NAME "BBGZ_RAS_ZOSTEST"


#ifdef __IBM_METAL__
#ifdef ANGEL_COMPILE
#define TraceActive(in_level) (getTraceLevelFromAngelTaskData() >= in_level)
#else
#define TraceActive(in_level) (getTraceLevelFromServerTaskData() >= in_level)
#endif
#else // LE Enabled
extern unsigned char RAS_aggregate_trace_level;
#define TraceActive(in_level) (RAS_aggregate_trace_level >= in_level)
#endif

#endif
