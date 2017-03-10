/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
#ifndef __jbatch_utils_h__
#define __jbatch_utils_h__

#include <stdlib.h>
#include <stdio.h>

/**
 * Print a line to stdout.
 *
 * @return 0
 */
int jnu_println(const char *str, ...) ;

/**
 * Print a trace msg.
 *
 * @return 0
 */
int jnu_trace(const char *fn, const char *str, ...) ;

/**
 * Print an ERROR message.
 *
 * @return rc 
 */
int jnu_error(const char *fn, int rc, const char *str, ...) ;

/**
 * Print an INFO message.
 *
 * @return 0
 */
int jnu_info(const char * str, ...) ;

/**
 * @return (s1 != NULL) ? s1 : s2;
 */
const char * jnu_ifNull( const char * s1, const char * s2) ;

/**
 * @return malloc'ed storage.  Issues an error message if malloc fails.
 */
void * jnu_malloc_nsf(size_t size, const char * fn, int line) ;

/**
 * Convenience macro.
 */
#define jnu_malloc(size) jnu_malloc_nsf(size, __FUNCTION__, __LINE__)

/**
 * Free storage.  Issues an error message if freeMe is NULL.
 */
void jnu_free_nsf(void * freeMe, const char * fn, int line) ;

/**
 * Convenience macro.
 */
#define jnu_free(freeMe) jnu_free_nsf(freeMe, __FUNCTION__, __LINE__)

/**
 * @return char ** containing the given strings. Must be free'd.
 *         Each string is dup'ed (must be free'd).
 *         The char ** is NULL term'ed.
 */
char ** jnu_mallocStringArray(int size, ...) ;

/**
 * Free's a string array previously allocated by jnu_mallocStringArray.
 *
 * @param strs array of strings to be free'd. Must be NULL term'd.
 */
void jnu_freeStringArray_nsf(char ** strs, const char * fn, int line) ;

/**
 * Convenience macro.
 */
#define jnu_freeStringArray(freeMe) jnu_freeStringArray_nsf(freeMe, __FUNCTION__, __LINE__)

/**
 * Trace arguments.
 */
int jnu_printArgs(int argc, char** argv) ;

/**
 * @return !strcasecmp(s1,s2)
 */
int jnu_strequals(const char *s1, const char *s2) ;

/**
 * 
 * Get next token from string *stringp, where tokens are possibly-empty
 * strings separated by characters from delim.
 *
 * Writes NULs into the string at *stringp to end tokens.
 * delim need not remain constant from call to call.
 * On return, *stringp points past the last NUL written (if there might
 * be further tokens), or is NULL (if there are definitely no more tokens).
 *
 * If *stringp is NULL, strsep returns NULL.
 */
char * jnu_strsep( char **stringp, const char *delim) ;

/**
 *
 * @param str the string to split.  NOTE: contents are modified (delim chars replaced with NULLs)
 * @param delim delim chars
 * @param results output arg, parsed token ptrs are copied into here
 * @param max max tokens to parse
 *
 * @return the number of parsed tokens
 */
int jnu_split(char * str, char * delim, char **results, int max) ;

/**
 *
 * Pad a string by appending the specified character to the end until the
 * string reaches the required length.
 *
 * Note: the resulting string is NOT null-terminated by this method.
 *
 * @param string the string to pad
 * @param len the length to pad out to
 * @param pad the character to use for padding
 *
 * @return the number of characters that were inserted to pad the string
 */
int jnu_pad(char* string, size_t len, unsigned char pad) ;

/**
 * This method does 3 things:
 *
 * 1. strcpy(dest,src) - copies src to dest
 * 2. jnu_pad(dest, padLen, pad) - pads dest out to padLen bytes
 * 3. dest[padLen] = '\0' - null-terms dest
 * 
 * @return dest
 */
char * jnu_strcpypad(char * dest, char * src, unsigned char pad, size_t padLen) ;

/**
 * @return sleep(seconds)
 */
int jnu_sleep(int seconds) ;

/**
 * @return non-zero if the given s is a parseable number; 0 otherwise.
 */
int jnu_isNumber( const char * s) ;

/**
 * @return the int parsed from the beginning of s; or dflt if s cannot be parsed.
 */
int jnu_parseInt( const char * s, int dflt) ;

/**
 * @return non-zero if s is all whitespace or ""; zero otherwise.
 */
int jnu_isallspaces(const char * s) ;

/**
 * @return non-zero if s is either NULL or ""; zero otherwise.
 */
int jnu_strIsEmpty( const char * s) ;

/**
 * @param s remove newline char from the end of this string, if present.
 *
 * @return s
 */
char * jnu_chomp( char * s ) ;

/**
 * @param s remove newline char from the end of this string, if present.
 *
 * @return s
 */
char * jnu_trim(char * str) ;

/**
 * @return non-zero if s begins with prefix; 0 otherwise.
 */
int jnu_strStartsWith( const char * s, const char * prefix ) ;

/**
 * @param f the file to read
 *
 * @return malloc'ed storage containing the file contents
 */
char * jnu_readFile( FILE * f ) ;
/**
 * @return non-zero if trace is enabled; 0 otherwise.
 */
int jnu_isTraceEnabled();

/**
 * @return non-zero if trace is enabled; 0 otherwise.
 */
int jnu_isTraceEnabled();

/**
 * @param str the string to check
 *
 * @return non-zero if str contains all digit chars; 0 otherwise
 */
int jnu_isNumeric( char * str );

#endif
