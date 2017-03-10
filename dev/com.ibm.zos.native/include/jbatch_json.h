/*
  Copyright (c) 2009 Dave Gamble
 
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
 
  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.
 
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
*/

#ifndef cJSON__h
#define cJSON__h

#ifdef __cplusplus
extern "C"
{
#endif

/* cJSON Types: */
#define cJSON_False 0
#define cJSON_True 1
#define cJSON_NULL 2
#define cJSON_Number 3
#define cJSON_String 4
#define cJSON_Array 5
#define cJSON_Object 6
	
#define cJSON_IsReference 256
#define cJSON_StringIsConst 512

/* The cJSON structure: */
typedef struct cJSON {
	struct cJSON *next,*prev;	/* next/prev allow you to walk array/object chains. Alternatively, use GetArraySize/GetArrayItem/GetObjectItem */
	struct cJSON *child;		/* An array or object item will have a child pointer pointing to a chain of the items in the array/object. */

	int type;					/* The type of the item, as above. */

	char *valuestring;			/* The item's string, if type==cJSON_String */
	int valueint;				/* The item's number, if type==cJSON_Number */
	double valuedouble;			/* The item's number, if type==cJSON_Number */

	char *string;				/* The item's name string, if this item is the child of, or is in the list of subitems of an object. */
} cJSON;

typedef struct cJSON_Hooks {
      void *(*malloc_fn)(size_t sz);
      void (*free_fn)(void *ptr);
} cJSON_Hooks;

/* Supply malloc, realloc and free functions to cJSON */
extern void cJSON_InitHooks(cJSON_Hooks* hooks);


/* Supply a block of JSON, and this returns a cJSON object you can interrogate. Call cJSON_Delete when finished. */
extern cJSON *cJSON_Parse(const char *value);
/* Render a cJSON entity to text for transfer/storage. Free the char* when finished. */
extern char  *cJSON_Print(cJSON *item);
/* Render a cJSON entity to text for transfer/storage without any formatting. Free the char* when finished. */
extern char  *cJSON_PrintUnformatted(cJSON *item);
/* Render a cJSON entity to text using a buffered strategy. prebuffer is a guess at the final size. guessing well reduces reallocation. fmt=0 gives unformatted, =1 gives formatted */
extern char *cJSON_PrintBuffered(cJSON *item,int prebuffer,int fmt);
/* Delete a cJSON entity and all subentities. */
extern void   cJSON_Delete(cJSON *c);

/* Returns the number of items in an array (or object). */
extern int	  cJSON_GetArraySize(cJSON *array);
/* Retrieve item number "item" from array "array". Returns NULL if unsuccessful. */
extern cJSON *cJSON_GetArrayItem(cJSON *array,int item);
/* Get item "string" from object. Case insensitive. */
extern cJSON *cJSON_GetObjectItem(cJSON *object,const char *string);

/* For analysing failed parses. This returns a pointer to the parse error. You'll probably need to look a few chars back to make sense of it. Defined when cJSON_Parse() returns 0. 0 when cJSON_Parse() succeeds. */
extern const char *cJSON_GetErrorPtr(void);
	
/* These calls create a cJSON item of the appropriate type. */
extern cJSON *cJSON_CreateNull(void);
extern cJSON *cJSON_CreateTrue(void);
extern cJSON *cJSON_CreateFalse(void);
extern cJSON *cJSON_CreateBool(int b);
extern cJSON *cJSON_CreateNumber(double num);
extern cJSON *cJSON_CreateString(const char *string);
extern cJSON *cJSON_CreateArray(void);
extern cJSON *cJSON_CreateObject(void);

/* These utilities create an Array of count items. */
extern cJSON *cJSON_CreateIntArray(const int *numbers,int count);
extern cJSON *cJSON_CreateFloatArray(const float *numbers,int count);
extern cJSON *cJSON_CreateDoubleArray(const double *numbers,int count);
extern cJSON *cJSON_CreateStringArray(const char **strings,int count);

/* Append item to the specified array/object. */
extern void cJSON_AddItemToArray(cJSON *array, cJSON *item);
extern void	cJSON_AddItemToObject(cJSON *object,const char *string,cJSON *item);
extern void	cJSON_AddItemToObjectCS(cJSON *object,const char *string,cJSON *item);	/* Use this when string is definitely const (i.e. a literal, or as good as), and will definitely survive the cJSON object */
/* Append reference to item to the specified array/object. Use this when you want to add an existing cJSON to a new cJSON, but don't want to corrupt your existing cJSON. */
extern void cJSON_AddItemReferenceToArray(cJSON *array, cJSON *item);
extern void	cJSON_AddItemReferenceToObject(cJSON *object,const char *string,cJSON *item);

/* Remove/Detatch items from Arrays/Objects. */
extern cJSON *cJSON_DetachItemFromArray(cJSON *array,int which);
extern void   cJSON_DeleteItemFromArray(cJSON *array,int which);
extern cJSON *cJSON_DetachItemFromObject(cJSON *object,const char *string);
extern void   cJSON_DeleteItemFromObject(cJSON *object,const char *string);
	
/* Update array items. */
extern void cJSON_InsertItemInArray(cJSON *array,int which,cJSON *newitem);	/* Shifts pre-existing items to the right. */
extern void cJSON_ReplaceItemInArray(cJSON *array,int which,cJSON *newitem);
extern void cJSON_ReplaceItemInObject(cJSON *object,const char *string,cJSON *newitem);

/* Duplicate a cJSON item */
extern cJSON *cJSON_Duplicate(cJSON *item,int recurse);
/* Duplicate will create a new, identical cJSON item to the one you pass, in new memory that will
need to be released. With recurse!=0, it will duplicate any children connected to the item.
The item->next and ->prev pointers are always zero on return from Duplicate. */

/* ParseWithOpts allows you to require (and check) that the JSON is null terminated, and to retrieve the pointer to the final byte parsed. */
extern cJSON *cJSON_ParseWithOpts(const char *value,const char **return_parse_end,int require_null_terminated);

extern void cJSON_Minify(char *json);

/* Macros for creating things quickly. */
#define cJSON_AddNullToObject(object,name)		cJSON_AddItemToObject(object, name, cJSON_CreateNull())
#define cJSON_AddTrueToObject(object,name)		cJSON_AddItemToObject(object, name, cJSON_CreateTrue())
#define cJSON_AddFalseToObject(object,name)		cJSON_AddItemToObject(object, name, cJSON_CreateFalse())
#define cJSON_AddBoolToObject(object,name,b)	cJSON_AddItemToObject(object, name, cJSON_CreateBool(b))
#define cJSON_AddNumberToObject(object,name,n)	cJSON_AddItemToObject(object, name, cJSON_CreateNumber(n))
#define cJSON_AddStringToObject(object,name,s)	cJSON_AddItemToObject(object, name, cJSON_CreateString(s))

/* When assigning an integer value, it needs to be propagated to valuedouble too. */
#define cJSON_SetIntValue(object,val)			((object)?(object)->valueint=(object)->valuedouble=(val):(val))
#define cJSON_SetNumberValue(object,val)		((object)?(object)->valueint=(object)->valuedouble=(val):(val))

// ------------------------------------------------------------------------------------------------------------
// ------------------------------------------------------------------------------------------------------------
// ------------------------------------------------------------------------------------------------------------
//  HERE BE IBM-AUTHORED EXTENSIONS TO THE ORIGINAL CJSON CODE
// ------------------------------------------------------------------------------------------------------------
// ------------------------------------------------------------------------------------------------------------
// ------------------------------------------------------------------------------------------------------------

/**
 * Null-safe form of cJSON_CreateObject.
 * 
 * @return a new cJSON object, or NULL if it couldn't be allocated.
 *         If NULL a jnu_error message is issued.
 */
cJSON * cJSON_CreateObject_nsf(const char * fn, int line) ;

/**
 * Convenience macro.
 */
#define cJSON_CreateObject_ns() cJSON_CreateObject_nsf(__FUNCTION__,__LINE__)

/**
 * Null-safe form of cJSON_Delete.
 * 
 * @return delete the cJSON object, if not NULL.
 *         If NULL a jnu_error message is issued.
 */
void cJSON_Delete_nsf(cJSON * deleteMe, const char * fn, int line) ;

/**
 * Convenience macro.
 */
#define cJSON_Delete_ns(deleteMe) cJSON_Delete_nsf(deleteMe, __FUNCTION__,__LINE__)

/**
 * @return null-safe form of cJSON_GetObjectItem.
 */
cJSON * cJSON_GetObjectItem_ns(cJSON * jsonObj, const char * name) ;

/**
 * @return the printed JSON, or "" if NULL.
 */
const char * cJSON_Print_ns(cJSON * jsonObj) ;

/**
 * @return the printed JSON, or "" if NULL.
 */
const char * cJSON_PrintUnformatted_ns(cJSON * jsonObj) ;

/**
 * @return the string value associated with the given name.
 */
char * cJSON_GetObjectItemStringValue(cJSON * jsonObj, const char * name, char * dflt) ;

/**
 * @return the int value associated with the given name.
 */
int cJSON_GetObjectItemIntValue(cJSON * jsonObj, const char * name, int dflt) ;

/**
 * @return the bool value associated with the given name.
 */
int cJSON_GetObjectItemBoolValue(cJSON * jsonObj, const char * name, int dflt);

/**
 * @return -1 if jsonArry is NULL; otherwise cJSON_GetArraySize(jsonArr)
 */
int cJSON_GetArraySize_ns(cJSON * jsonArr) ;

/**
 * @return non-zero if jsonArr is NULL or empty; 0 otherwise.
 */
int cJSON_isArrayEmpty(cJSON * jsonArr) ;

/**
 * @return the item from the array, duplicated.  The given array is then freed.
 */
cJSON * cJSON_DupArrayItemAndFreeArray( cJSON * jsonArr, int item ) ;

/**
 * @return the field from the object, duplicated.  The given object is then freed.
 */
cJSON * cJSON_DupObjectItemAndFreeObject( cJSON * jsonObj, const char * name) ;

/**
 * Merge fields from src to dest.
 * Fields are dup'ed.
 *
 * @param dest fields copied to here
 * @param src fields copied from here
 * @param overwrite indicates whether to overwrite existing dest fields
 *
 */
void cJSON_MergeObjects( cJSON * dest, cJSON * src, int overwrite ) ;


#ifdef __cplusplus
}
#endif

#endif
