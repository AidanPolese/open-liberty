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

#include <errno.h>
#include <stdlib.h>
#include <stdarg.h>
#include <stdio.h>      // vsnprintf
#include <string.h>     // strdup
#include <strings.h>    // strcasecmp
#include <unistd.h>     // sleep
#include <ctype.h>      // isspace, isdigit


#include "include/jbatch_utils.h"

/**
 * Print a line to stdout.
 *
 * @return 0
 */
int jnu_println(const char *str, ...) {

    va_list argp;

    va_start(argp, str);
    vfprintf(stdout, str, argp);
    va_end(argp);
    fprintf(stdout,"\n");

    return 0;
}


/**
 * Global indicates whether or not trace is enabled.
 * Set from env var "batchManagerZosTrace"
 */
static int jnu_TraceEnabled = -1;

/**
 * @return non-zero if trace is enabled; 0 otherwise.
 */
int jnu_isTraceEnabled() {

    if (jnu_TraceEnabled < 0) {
        jnu_TraceEnabled = jnu_parseInt( getenv("batchManagerZosTrace"), 0 );
    }

    return jnu_TraceEnabled;
}

/**
 * Print a trace msg.
 *
 * @return 0
 */
int jnu_trace(const char *fn, const char *str, ...) {

    if ( ! jnu_isTraceEnabled() ) {
        return 0;
    }

    va_list argp;

    fprintf(stdout, "trace: [%s] ", fn);

    va_start(argp, str);
    vfprintf(stdout, str, argp);
    va_end(argp);
    fprintf(stdout,"\n");

    return 0;
}


/**
 * Print an ERROR message.
 *
 * @return rc 
 */
int jnu_error(const char *fn, int rc, const char *str, ...) {

    va_list argp;

    fprintf(stdout, "ERROR: [%s] rc=%d  ", fn, rc);

    va_start(argp, str);
    vfprintf(stdout, str, argp);
    va_end(argp);
    fprintf(stdout,"\n");

    return rc;
}

/**
 * Print an INFO message.
 *
 * @return 0
 */
int jnu_info(const char * str, ...) {

    va_list argp;

    fprintf(stdout, "INFO: ");

    va_start(argp, str);
    vfprintf(stdout, str, argp);
    va_end(argp);
    fprintf(stdout,"\n");

    return 0;
}


/**
 * @return (s1 != NULL) ? s1 : s2;
 */
const char * jnu_ifNull( const char * s1, const char * s2) {
    return (s1 != NULL) ? s1 : s2;
}

/**
 * @return malloc'ed storage. Storage is init'ed to all zeros. 
 *         Issues an error message if malloc fails.
 */
void * jnu_malloc_nsf(size_t size, const char * fn, int line) {
    void * retMe = malloc(size);
    if (retMe == NULL) {
        jnu_error(fn, 0, "malloc(%d) returned NULL at line:%d. errno:%d", size, line, errno);
    } else {
        memset(retMe, 0, size);
    }
    return retMe;
}

/**
 * Free storage.  Issues an error message if freeMe is NULL.
 */
void jnu_free_nsf(void * freeMe, const char * fn, int line) {
    if (freeMe == NULL) {
        jnu_error(fn, 0, "attemped to free(NULL) at line:%d", line);
    } else {
        free(freeMe);
    }
}

/**
 * @return char ** containing the given strings. Must be free'd.
 *         Each string is dup'ed (must be free'd).
 *         The char ** is NULL term'ed.
 */
char ** jnu_mallocStringArray(int size, ...) {

    char ** retMe = (char **) jnu_malloc( sizeof(char *) * (size + 1) );
    if (retMe == NULL) {
        return NULL;
    }

    va_list argp;
    va_start(argp, size);

    for (int i=0; i < size; ++i) {
        retMe[i] = strdup( va_arg(argp, char *) ); // TODO: jnu_strdup with error checking
    }

    va_end(argp);

    retMe[size] = NULL;
    return retMe;
}

/**
 * Free's a string array previously allocated by jnu_mallocStringArray.
 *
 * @param strs array of strings to be free'd. Must be NULL term'd.
 */
void jnu_freeStringArray_nsf(char ** strs, const char * fn, int line) {
    if (strs == NULL) {
        jnu_error(fn, 0, "attemped to jnu_freeStringArray(NULL) at line:%d", line);
        return;
    }

    for (int i=0; strs[i] != NULL; ++i) {
        jnu_free_nsf(strs[i], fn, line);
    }

    jnu_free(strs);
}

/**
 * trace the args.
 * @return argc.
 */
int jnu_printArgs(int argc, char** argv) {
    for (int i=0; i < argc; ++i) {
        jnu_trace(__FUNCTION__, "argv[%d]: [%s]", i, argv[i]);
    }
    return argc;
}

/**
 * @return !strcasecmp(s1,s2)
 */
int jnu_strequals(const char *s1, const char *s2) {
    return (s1 == NULL || s2 == NULL) ? 0 : !strcasecmp(s1,s2);
}

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
char * jnu_strsep( char **stringp, const char *delim) {
	char *s;
	const char *spanp;
	int c, sc;
	char *tok;

	if ((s = *stringp) == NULL)
		return (NULL);
	for (tok = s;;) {
		c = *s++;
		spanp = delim;
		do {
			if ((sc = *spanp++) == c) {
				if (c == 0)
					s = NULL;
				else
					s[-1] = 0;
				*stringp = s;
				return (tok);
			}
		} while (sc != 0);
	}
	/* NOTREACHED */
}


/**
 *
 * @param str the string to split.  NOTE: contents are modified (delim chars replaced with NULLs)
 * @param delim delim chars
 * @param results output arg, parsed token ptrs are copied into here
 * @param max max tokens to parse
 *
 * @return the number of parsed tokens
 */
int jnu_split(char * str, char * delim, char **results, int max) {

    int i;
    for (i = 0; str != NULL && i < (max-1); ++i) {
        results[i] = jnu_strsep(&str,delim);
    }

    // max indicates the max number of tokens to parse.
    // So we actually want to call strsep at most max-1 times, since
    // the last token (max) is the remainder of str and we don't want
    // to call strsep again because we might find and parse another 
    // token from the remaining string.
    if (i == (max-1) && str != NULL) {
        results[i++] = str;
    }

    return i;
}


/**
 * TODO: move str functions to jbatch_string_utils.cpp
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
int jnu_pad(char* string, size_t len, unsigned char pad) {
    int padCount = 0;
    for (int i = strlen(string); i < len; i++, padCount++) {
        string[i] = pad;
    }
    return padCount;
}

/**
 * This method does 3 things:
 *
 * 1. strcpy(dest,src) - copies src to dest
 * 2. jnu_pad(dest, padLen, pad) - pads dest out to padLen bytes
 * 3. dest[padLen] = '\0' - null-terms dest
 * 
 * @return dest
 */
char * jnu_strcpypad(char * dest, char * src, unsigned char pad, size_t padLen) {
    strcpy(dest, src);
    jnu_pad(dest, padLen, pad);
    dest[padLen] = '\0';
    return dest;
}


/**
 * @return sleep(seconds)
 */
int jnu_sleep(int seconds) {
    // TODO: issue message in "--verbose" mode.
    
    jnu_trace(__FUNCTION__, "entry: calling sleep(%d)...", seconds);

    int rc = sleep(seconds);
    if (rc != 0) {
        jnu_error(__FUNCTION__,rc,"sleep(%d) interrupted. rc:%d", seconds, rc);
    }

    jnu_trace(__FUNCTION__, "exit: awoke from sleep(%d), rc:%", seconds, rc);
    return rc;
}

/**
 * @return non-zero if the given s is a parseable number; 0 otherwise.
 */
int jnu_isNumber( const char * s) {
    if (s == NULL) {
        return 0;
    }
    char * endPtr;
    strtol(s, &endPtr, 10);
    return (endPtr != s);
}

/**
 * @return the int parsed from the beginning of s; or dflt if s cannot be parsed.
 */
int jnu_parseInt( const char * s, int dflt) {
    if (s == NULL) {
        return dflt;
    }
    char * endPtr;
    int retMe = strtol(s, &endPtr, 10);

    return (endPtr != s) ? retMe : dflt;
}

/**
 * @return non-zero if s is all whitespace or ""; zero otherwise.
 */
int jnu_isallspaces(const char * s) {
    for (int i=0; i < strlen(s); ++i) {
        if (! isspace(s[i]) ) {
            return 0;
        }
    }
    return 1;
}

/**
 * @return non-zero if s is either NULL or "" or all whitespace; zero otherwise.
 */
int jnu_strIsEmpty( const char * s) {
    return (s == NULL || strlen(s) == 0 );
}

/**
 * @param s remove newline char from the end of this string, if present.
 *
 * @return s
 */
char * jnu_chomp( char * s ) {
    if ( jnu_strIsEmpty(s) ) {
        return s;
    }

    // Do this twice to remove \r\n if necessary
    for (int i=0; i < 2 && strlen(s) > 0; ++i) {
        char * last = &s[strlen(s)-1];
        if ( *last == '\n' || *last == '\r' ) {
            *last = '\0';
        }
    }

    return s;
}


/**
 * @param s remove newline char from the end of this string, if present.
 *
 * @return s
 */
char * jnu_trim(char * str){
      char *end;

      // Trim trailing space
      end = str + strlen(str) - 1;
      while(end > str && isspace(*end)) end--;

      // Write new terminator
      *(end+1) = 0;

      return str;
}

/**
 * @return non-zero if s begins with prefix; 0 otherwise.
 */
int jnu_strStartsWith( const char * s, const char * prefix ) {
    return (strstr(s,prefix) == s);
}

/**
 * @param f the file to read
 *
 * @return malloc'ed storage containing the file contents
 */
char * jnu_readFile( FILE * f ) {

    // Determine length of file
    fseek( f, 0L, SEEK_END );
    long fileLen = ftell(f);
    rewind(f);

    jnu_trace(__FUNCTION__, "inline JSL file length: %d", fileLen);

    char * retMe = (char *) jnu_malloc(fileLen + 1);
    if (retMe == NULL) {
        return NULL;
    }

    int rc = fread( retMe, fileLen, 1, f );
    if (rc != 1) {
        jnu_error(__FUNCTION__, 0, "fread(%d) returned %d, expected %d. errno:%d", fileLen, rc, 1, errno);
        jnu_free(retMe);
        return NULL;
    }

    return retMe;
}


/**
 * @param str the string to check
 *
 * @return non-zero if str contains all digit chars; 0 otherwise
 */
int jnu_isNumeric(char * str)
{
    while (*str)
       {
          if (!isdigit(*str)) {
             return 0;
          }
          else
             ++str;
       }
       return 1;
}



