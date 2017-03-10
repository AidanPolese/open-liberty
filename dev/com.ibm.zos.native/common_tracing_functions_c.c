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
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#ifndef __IBM_METAL__
#include <time.h>
#else
#include <ieac.h>
#include "include/ieantc.h"
#endif
#include "include/common_defines.h"
#include "include/ras_tracing.h"



#pragma linkage(BPX4WRT,OS_NOSTACK)
void BPX4WRT(int fileDescriptor, void* buffer, int alet, int bufsize, int* retval, int* retcode, int* rsnval);
#pragma linkage(BPX4OPN,OS_NOSTACK)
void BPX4OPN(int length, char * path, int options, int mode, int* retval, int* retcode, int* rsnval);
#pragma linkage(BPX4CLO,OS_NOSTACK)
void BPX4CLO(int fileDescriptor, int* retval, int* retcode, int* rsnval);

//
// Work around variable length array compiler bug by using preprocessor
// instead of constants
//
#define MAX_OUTBUF  36
#define MAX_LINE    80
#define MAX_HDRBUF  5
#define MAX_OUTBUF  36
#define MAX_TXTBUF  17
#define MAX_ASCBUF  17

//File Open flags
//from: CEE.SCEEH.H(FCNTL)
#define O_WRONLY     0x01
#define O_APPEND     0x08
#define O_CREAT      0x80
#define O_SYNC       0x0100

//File modes in octal
//from: CEE.SCEEH.SYS.H(MODES)
#define S_IRWXU  0x01C0
#define S_IRWXG  0x0038
#define S_IROTH  0x0004
#define S_IFREG  0x03000000

/** The file descriptor for the unittest log file
*/
int RAS_unittest_trace_filedesc = 0;

/**
 * This is the trace level used by the unit test environment.
 * If it is configured the value would be from 0 to 3.
 */
int RAS_unittest_trace_level = -1;

/**
 * This is the fully qualified path of the location of the z native trace log
*/
char * RAS_unittest_trace_filename = NULL;

/**
 * ASCII translation table.
 */
static const char asciiTranslation[256] = {
    0x00, 0x01, 0x02, 0x03, 0x37, 0x2D, 0x2E, 0x2F, 0x16, 0x05, 0x15, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
    0x10, 0x11, 0x12, 0x13, 0x3C, 0x3D, 0x32, 0x26, 0x18, 0x19, 0x3F, 0x27, 0x1C, 0x1D, 0x1E, 0x1F,
    0x40, 0x5A, 0x7F, 0x7B, 0x5B, 0x6C, 0x50, 0x7D, 0x4D, 0x5D, 0x5C, 0x4E, 0x6B, 0x60, 0x4B, 0x61,
    0xF0, 0xF1, 0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0x7A, 0x5E, 0x4C, 0x7E, 0x6E, 0x6F,
    0x7C, 0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7, 0xC8, 0xC9, 0xD1, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6,
    0xD7, 0xD8, 0xD9, 0xE2, 0xE3, 0xE4, 0xE5, 0xE6, 0xE7, 0xE8, 0xE9, 0xAD, 0xE0, 0xBD, 0x5F, 0x6D,
    0x79, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96,
    0x97, 0x98, 0x99, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7, 0xA8, 0xA9, 0xC0, 0x4F, 0xD0, 0xA1, 0x07,
    0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x06, 0x17, 0x28, 0x29, 0x2A, 0x2B, 0x2C, 0x09, 0x0A, 0x1B,
    0x30, 0x31, 0x1A, 0x33, 0x34, 0x35, 0x36, 0x08, 0x38, 0x39, 0x3A, 0x3B, 0x04, 0x14, 0x3E, 0xFF,
    0x41, 0xAA, 0x4A, 0xB1, 0x9F, 0xB2, 0x6A, 0xB5, 0xBB, 0xB4, 0x9A, 0x8A, 0xB0, 0xCA, 0xAF, 0xBC,
    0x90, 0x8F, 0xEA, 0xFA, 0xBE, 0xA0, 0xB6, 0xB3, 0x9D, 0xDA, 0x9B, 0x8B, 0xB7, 0xB8, 0xB9, 0xAB,
    0x64, 0x65, 0x62, 0x66, 0x63, 0x67, 0x9E, 0x68, 0x74, 0x71, 0x72, 0x73, 0x78, 0x75, 0x76, 0x77,
    0xAC, 0x69, 0xED, 0xEE, 0xEB, 0xEF, 0xEC, 0xBF, 0x80, 0xFD, 0xFE, 0xFB, 0xFC, 0xBA, 0xAE, 0x59,
    0x44, 0x45, 0x42, 0x46, 0x43, 0x47, 0x9C, 0x48, 0x54, 0x51, 0x52, 0x53, 0x58, 0x55, 0x56, 0x57,
    0x8C, 0x49, 0xCD, 0xCE, 0xCB, 0xCF, 0xCC, 0xE1, 0x70, 0xDD, 0xDE, 0xDB, 0xDC, 0x8D, 0x8E, 0xDF
};

/**
 * Simple mapping of hex values to their character representation.
 */
const char hexout[] = "0123456789ABCDEF";


/**
 * Write the formatted double gutter "raw data" to the trace file.
 *
 * @param data the raw data
 * @param size the size of the raw data to trace
 */
static void writeDoubleGutterTrace(const char *data, int size, int fileDescriptor);


#ifdef __IBM_METAL__
/**
 * List form of the time macro to enable reentrancy.
 */
__asm(" TIME LINKAGE=SYSTEM,MF=L" : "DS"(time_list_form));
#endif


int openLogFile();
void closeLogFile(int fileDescriptor);

void writeMessageToStdOut(const char* message_p);

/**
 * Write a formatted trace record element to the trace file.
 *
 * @param message_p a pointer to the formatted trace string to write
 */
static void writeFormattedNativeTraceRecord(const char* message_p, int fileDescriptor) {

    int retval = 0;
    int retcode = 0;
    int rsnval = 0;
    int standardOut = 1;

    BPX4WRT(fileDescriptor, &message_p, 0, strlen(message_p), &retval, &retcode, &rsnval);

    if (retval == -1) {
        char errorMsg[256];
        snprintf(errorMsg, sizeof errorMsg, "%s%s%s%i:%i:%i", "Unable to write to log file descriptor: \n", fileDescriptor,"rc=", retval, retcode, rsnval);
        BPX4WRT(standardOut, &errorMsg, 0, strlen(errorMsg), &retval, &retcode, &rsnval);
        return;
    }
}

/**
 * Mapping of the packed decimal TOD format returned by the TIME macro.
 */
#pragma pack(packed)
typedef struct timeStamp {
    unsigned char hour;
    unsigned char min;
    unsigned char sec;
    unsigned char usec[3];
    unsigned short reserved1;
    unsigned char year[2];
    unsigned char month;
    unsigned char day;
    unsigned int reserved2;
} timeStamp;
#pragma pack(reset)

/**
 * Unpack packed decimal data.
 *
 * @param packed pointer to the packed decimal data
 * @param bytes the number bytes holding the packed data
 *
 * @return an binary integer form of the packed data
 */
static int
unpack(void* packed, int bytes) {
    unsigned char* data = (unsigned char*) packed;

    int value = 0;
    for (int i = 0; i < bytes; i++) {
        value *= 10;
        value += data[i] >> 4;
        value *= 10;
        value += data[i] & 0x0F;
    }

    return value;
}


/**
 * Convert a sequence of raw data (up to 16 bytes) into hex and write
 * the hex string to the current trace file.
 *
 * @param data the data to trace
 * @param data_length the length of the data area (will be truncated at 16 bytes)
 */
static void
writeFormattedRawOctets(const char* data, int data_length, int fileDescriptor) {
//  const int MAX_OUTBUF = 36; // 12345678 12345678 12345678 12345678
                               // ----+----+----+----+----+----+----+-
    char outbuf[MAX_OUTBUF];

    outbuf[0] = '\0';
    for (int i = 0, j = 0; i < data_length && j < (MAX_OUTBUF- 1); i++) {
        // Get hex characters
        outbuf[j++] = hexout[data[i] >> 4];
        outbuf[j++] = hexout[data[i] & 0x0f];

        // Spaces after every word
        if ((i % 4 == 3) && (i < data_length)) {
            outbuf[j++] = ' ';
        }

        // Move string terminator
        outbuf[j] = '\0';
    }
    writeFormattedNativeTraceRecord(outbuf, fileDescriptor);
}

/**
 * Write a formatted, double gutter trace of hex, EBCDIC, and ASCII to
 * the current trace file.
 *
 * @param data the data to trace
 * @param size the length of the data to trace
 */
static void
writeDoubleGutterTrace(const char* data, int size, int fileDescriptor) {
//  const int MAX_LINE   = 80;
//  const int MAX_HDRBUF = 5;
//  const int MAX_OUTBUF = 36;
//  const int MAX_TXTBUF = 17;
//  const int MAX_ASCBUF = 17;

    char line[MAX_LINE + 1];
    char hdrbuf[MAX_HDRBUF];
    char outbuf[MAX_OUTBUF];
    char txtbuf[MAX_TXTBUF];
    char ascbuf[MAX_ASCBUF];

    int i, j, k, l, x;

    outbuf[0] = '\0';
    txtbuf[0] = '\0';

    // Format the header
    writeFormattedNativeTraceRecord("\n", fileDescriptor);
    snprintf(line, MAX_LINE, "  +--------------------------------------------------------------------------+\n");
    writeFormattedNativeTraceRecord(line, fileDescriptor);
    snprintf(line, MAX_LINE, "  |OSet| A=%016.16lx Length=%7.7lx |     EBCDIC     |     ASCII      |\n", data, size);
    writeFormattedNativeTraceRecord(line, fileDescriptor);
    snprintf(line, MAX_LINE, "  +----+-----------------------------------+----------------+----------------+\n");
    writeFormattedNativeTraceRecord(line, fileDescriptor);

    // If tracing the PSA, only grab the first 16 bytes
    if (data == NULL) {
        size = 16;
    }

    snprintf(hdrbuf, MAX_HDRBUF, "%04x", 0);

    for (i = 0, j = 0, k = 0, l = 0; i < size; i++) {
        x = data[i] >> 4;                   // extract 1st nibble
        outbuf[j++] = hexout[x];            // translate 1st nibble
        x = data[i] & 0x0F;                 // extract 2nd nibble
        outbuf[j++] = hexout[x];            // translate 2nd nibble

        if (i % 4 == 3) outbuf[j++] = ' ';  // add blank separator

        if (isprint(data[i])) {             // Printable EBCDIC
            txtbuf[k++] = data[i];
            txtbuf[k] = '\0';
        } else {
            txtbuf[k++] = '.';
            txtbuf[k] = '\0';
        }

        if (isprint(asciiTranslation[data[i]])) { // Printable ASCII
            ascbuf[l++] = asciiTranslation[data[i]];
            ascbuf[l]   = '\0';
        } else {
            ascbuf[l++] = '.';
            ascbuf[l]   = '\0';
        }

        if (i % 16 == 15) {                 // new line: set header.
            outbuf[j - 1] = '\0';           // remove last blank
            snprintf(line, MAX_LINE, "  |%s|%s|%s|%s|\n", hdrbuf, outbuf, txtbuf, ascbuf);
            writeFormattedNativeTraceRecord(line, fileDescriptor);
            j = 0; k = 0; l = 0;
            txtbuf[0] = '\0';
            ascbuf[0] = '\0';
            snprintf(hdrbuf, MAX_HDRBUF, "%04x", i + 1);
        }
    }

    if (j > 0) {
        memset(outbuf + j, ' ', MAX_OUTBUF - 1 - j);
        outbuf[MAX_OUTBUF - 1] = '\0';
        memset(txtbuf + k, ' ', MAX_TXTBUF - 1 - k);
        txtbuf[MAX_TXTBUF - 1] = '\0';
        memset(ascbuf + l, ' ', MAX_ASCBUF - 1 - l);
        ascbuf[MAX_ASCBUF - 1] = '\0';
        snprintf(line, MAX_LINE, "  |%s|%s|%s|%s|\n", hdrbuf, outbuf, txtbuf, ascbuf);
        writeFormattedNativeTraceRecord(line, fileDescriptor);
    }

    snprintf(line, MAX_LINE, "  +--------------------------------------------------------------------------+");
    writeFormattedNativeTraceRecord(line, fileDescriptor);
}

#ifdef __IBM_METAL__
/**
 * Issue the MVS TIME macro to get the current local time and date.
 *
 * @param time_stamp a pointer to a 16 byte time stamp area
 *
 * @return the return code from the TIME macro
 */
static int
getLocalTimeStamp(timeStamp* time_stamp) {
    struct parm31 {
        timeStamp time_stamp;
        int return_code;
        char time_dynamic[sizeof(time_list_form)];
    };

    struct parm31* parm_p = __malloc31(sizeof(struct parm31));
    if (parm_p == NULL) {
        return -1;
    }

    memcpy(parm_p->time_dynamic, &time_list_form, sizeof(time_list_form));

    __asm(" SAM31\n"
          " SYSSTATE PUSH\n"
          " SYSSTATE AMODE64=NO,OSREL=ZOSV1R6\n"
          " TIME DEC,%0,DATETYPE=YYYYMMDD,ZONE=LT,LINKAGE=SYSTEM,MF=(E,%2)\n"
          " ST 15,%1\n"
          " SYSSTATE POP\n"
          " SAM64" :
          "=m"(parm_p->time_stamp), "=m"(parm_p->return_code) :
          "m"(parm_p->time_dynamic) :
          "r0", "r1", "r14" ,"r15");

    *time_stamp = parm_p->time_stamp;

    return parm_p->return_code;
}
#endif

/**
 * Put a formatted trace header into the specified area.
 *
 * @param data_str buffer to write header into
 * @param data_size the size of the header buffer
 * @param trace_point the trace point value specified by the caller
 */
static void
formatTraceHeader(char* data_str, int data_size, int trace_point, int tcbAddress, int executionState, int key)
{
    tcb* cur_tcb = (tcb* __ptr32) tcbAddress;

#ifdef __IBM_METAL__
    timeStamp time;
    getLocalTimeStamp(&time);
    // Trace: 2011/07/25 03:54:00.082038 t=6CCE88 key=P2 (04026004)
    snprintf(data_str, data_size,
    "Trace: %04d/%02d/%02d %02d:%02d:%02d.%06d t=%06X key=%c%d (%08X)",
    unpack(&time.year, sizeof(time.year)),
    unpack(&time.month, sizeof(time.month)),
    unpack(&time.day, sizeof(time.day)),
    unpack(&time.hour, sizeof(time.hour)),
    unpack(&time.min, sizeof(time.min)),
    unpack(&time.sec, sizeof(time.sec)),
    unpack(&time.usec, sizeof(time.usec)),
    cur_tcb,
    executionState ? 'P' : 'S',
    key,
    trace_point);
#else
    //format a time string
    time_t theTime;
    struct tm * timeinfo;
    char strTime [80];
    time(&theTime);
    timeinfo = localtime(&theTime);
    strftime(strTime,80,"%Y/%m/%d %I:%M:%S.000000", timeinfo);  //how to get usec microseconds?
    snprintf(data_str, data_size,
    "Trace: %s t=%06X key=%c%d (%08X)",
    strTime,
    cur_tcb,
    executionState ? 'P' : 'S',
    key,
    trace_point);
#endif

}

//
// Work around variable length array compiler bug by using preprocessor
// instead of constants
//
#define MAX_STR 4095


/**
 * Main trace formatting routine.  This will examine the variable argument list and
 * identify each piece of data to trace and will format it according to the trace
 * type.
 */
void
TraceWriteNativeV(enum trc_level usr_level, int trace_point, char* event_desc, va_list usr_var_list, int tcbAddress, int executionState, int key, int fileDescriptor)
{
    //const unsigned int MAX_STR = 4095;
    char data_str[MAX_STR + 1];

    unsigned short cur_length;
    unsigned short desc_length;
    void*    cur_data;
    char*    cur_desc;
    char*    cur_str;
    void*    ptr_value;
    int      int_value;
    float    flt_value;
    double   dbl_value;
    long     long_value;
    short    short_value;
    char     char_value;

    formatTraceHeader(data_str, MAX_STR, trace_point, tcbAddress, executionState, key);
    writeFormattedNativeTraceRecord(data_str, fileDescriptor);

    //-----------------------------------------------------------------
    // Get trace data for each item (in sets of 4 input arguments).
    // Each item consists of 4 parts: key, length, data, description.
    //-----------------------------------------------------------------
    short trc_key_local = va_arg(usr_var_list, short);
    while (trc_key_local > 0) {
        data_str[0] = '\0';                                       // Clear string
        switch (trc_key_local & 0x00FF) {
            case trc_key_raw_data:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                cur_data = (void *) va_arg(usr_var_list, void *); // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: data_address=%016lx, data_length=%d", cur_desc, (long) cur_data, cur_length);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                writeDoubleGutterTrace((char *) cur_data, cur_length, fileDescriptor);
                break;

            case trc_key_int:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                int_value = va_arg(usr_var_list, int);            // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: %d", cur_desc, int_value);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                break;

            case trc_key_hex_int:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                int_value = va_arg(usr_var_list, int);            // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: %x", cur_desc, int_value);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                break;

            case trc_key_ebcdic_string:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                cur_str = va_arg(usr_var_list, char *);           // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: %s", cur_desc, cur_str);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                break;

            case trc_key_double:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                dbl_value = va_arg(usr_var_list, double);         // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: %lf", cur_desc, dbl_value);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                break;

            case trc_key_pointer:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                ptr_value = va_arg(usr_var_list, void*);          // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: %p", cur_desc, ptr_value);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                break;

            case trc_key_long:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                long_value = va_arg(usr_var_list, long);          // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: %ld", cur_desc, long_value);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                break;

            case trc_key_hex_long:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                long_value = va_arg(usr_var_list, long);          // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: %lx", cur_desc, long_value);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                break;

            case trc_key_short:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                short_value = va_arg(usr_var_list, short);        // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: %hd", cur_desc, short_value);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                break;

            case trc_key_char:
                cur_length = va_arg(usr_var_list, short);         // get length of data
                char_value = va_arg(usr_var_list, char);          // get data
                desc_length = va_arg(usr_var_list, short);        // get length of description
                cur_desc = va_arg(usr_var_list, char *);          // get description of data
                snprintf(data_str, MAX_STR, "\n  %s: %c", cur_desc, char_value);
                writeFormattedNativeTraceRecord(data_str, fileDescriptor);
                break;
        }
        trc_key_local = va_arg(usr_var_list, short); // Get data key for next (or last) data item
    }

    // Finish the record with a new line
    writeFormattedNativeTraceRecord("\n", fileDescriptor);
}

void
writeMessageToStdOut(const char* message_p) {
    int retval = 0;
    int retcode = 0;
    int rsnval = 0;
    int standardOut = 1;

    BPX4WRT(standardOut, &message_p, 0, strlen(message_p), &retval, &retcode, &rsnval);
}

/*
 * Open the unittest log file
 */
int
openLogFile() {

    int retval = 0;
    int retcode = 0;
    int rsnval = 0;
    int options = O_CREAT |O_WRONLY | O_SYNC | O_APPEND;         // Write only and append if exists
    int mode = S_IFREG| S_IRWXU | S_IRWXG | S_IROTH;  // Regular file 775
    int standardOut = 1;
    int fileDescriptor = 0;

    //This should only be null in the metal c environment
    //if (RAS_unittest_trace_filename == NULL)
    //    getUnitTestTraceFileName();

    BPX4OPN((int) strlen(RAS_unittest_trace_filename),
            RAS_unittest_trace_filename,
            options,
            mode,
            &fileDescriptor,
            &retcode,
            &rsnval);

    if (fileDescriptor == -1) {
        char errorMsg[256];
        snprintf(errorMsg, sizeof errorMsg, "%s%s rc=%i:%i:%i \n", "Unable to open log file:", RAS_unittest_trace_filename, fileDescriptor, retcode, rsnval);
        BPX4WRT(standardOut, &errorMsg, 0, strlen(errorMsg), &retval, &retcode, &rsnval);
    }

    return fileDescriptor;
}

/*
 * Close the unittest log file
 */
void
closeLogFile(int fileDescriptor) {

    int retval = 0;
    int retcode = 0;
    int rsnval = 0;
    int standardOut = 1;

    BPX4CLO(fileDescriptor, &retval, &retcode, &rsnval);

       if (retval == -1) {
           char errorMsg[256];
           snprintf(errorMsg, sizeof errorMsg, "%s%s%s%i:%i:%i", "Unable to close log file: \n", RAS_unittest_trace_filename, "rc=", retval, retcode, rsnval);
           BPX4WRT(standardOut, &errorMsg, 0, strlen(errorMsg), &retval, &retcode, &rsnval);
           return;
       }
}

#ifdef __IBM_METAL__
/**
 * This routine gets the unittest trace level and filedescriptor
 * from the name token.
 *
 */
void
getUnitTestTraceDataFromNameToken(int * unitTestTraceLevel_ptr, int * fileDescriptor_ptr) {

    char name[16];
    char token[16];
    int  token_rc;

    memcpy(name, RAS_UNITTEST_TRACE_LEVEL_TOKEN_NAME, sizeof(name));

    //todo performance optimization, store these trace values in the address space prefix
    //area and obtain them from here instead of calling iean4rt. Since this operation
    //is only called if the trace gets past the trace guard (trace is enabled) this
    //doesn't impact the mainline path.

    iean4rt(IEANT_HOME_LEVEL,
            name,
            token,
            &token_rc);

    if (token_rc == 0) {
        memcpy(fileDescriptor_ptr, &(token[0]), 4);
        memcpy(unitTestTraceLevel_ptr, &(token[4]), 4);
    }
    else {
          *fileDescriptor_ptr = 0;
          *unitTestTraceLevel_ptr = 0;
    }
    
}
#endif
