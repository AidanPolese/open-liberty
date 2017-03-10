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

#ifndef UTIL_REGISTRY_H_
#define UTIL_REGISTRY_H_
/**@file
 * A registry to keep stuff in (and get it back!).
 *
 * The registry will be kept in key 2 non-fetch-protected storage.  Thus,
 * you can only modify the registry from authorized code but anyone can see
 * the stuff you keep in here.  The registry is in private storage so it is
 * not visible from other servers.
 */

#define REGISTRY_ELEMENT_DATA_LENGTH    36         //!< The length of the registry's data area.  36 was chosen so that
                                                   //!<   the entire RegistryElement is 64 bytes long.
#define REGISTRY_VERSION                1          //!< The current version of the registry.
#define REGISTRY_TOKEN_EYECATCHER       "BBGZREGT" //!< Eyecatcher for @c RegistryToken

/**
 * An enumeration of Data Types that can be stored in the registry.
 */
typedef enum {
    ENCLAVE       = 0, //!< WLM Enclave token.
    SAFNSC        = 1, //!< SAF native security credential.
    RRSCTX        = 2, //!< RRS context token
    RRSURI        = 3, //!< RRS UR interest token
    RRSCTXI       = 4, //!< RRS context interest token
    RRSRMNAME     = 5, //!< RRS resource manager name
    RRSRMTKN      = 6, //!< RRS resource manager token
    WOLATKN       = 7, //!< Token used by WOLA
    AIOCONN       = 8  //!< AIO Connection token
} RegistryDataType;
#define REGISTRY_NUM_DATA_TYPES 9 //!< The number of data types that are supported.

/**
 * A token to use to find data you put into the registry.
*/
#pragma pack(packed)
typedef struct {
    unsigned char    eyecatcher[8];   //!< Eyecatcher @c REGISTRY_TOKEN_EYECATCHER
    int              version;         //!< Version
    int              length;          //!< Token Length
    char*            elementPtr;      //!< Element pointer - do not use directly, use registry_get()
    int              instanceCounter; //!< Instance counter - must match counter in element
    int              flags;           //!< Flags
    RegistryDataType type;            //!< Type of data in element
    char             reserved1[31];   //!< Reserved space  (makes the struct an even 64 bytes)
} RegistryToken;
#pragma pack(reset)

/**
 * The data area of a registry element.
 */
#pragma pack(packed)
typedef struct {
    char data[REGISTRY_ELEMENT_DATA_LENGTH];
} RegistryDataArea;
#pragma pack(reset)

/**
 * Function type used to get told when a registry element is being
 * destroyed, in case some final cleanup is necessary for objects
 * referenced by the element's data area.
 *
 * @param dataArea A copy of the data area from the destroyed element.
 */
typedef void elementDestroyed_t(RegistryDataArea dataArea);

/**
 * Function that cleans up data stored in a SAFNSC type registry
 * element.
 *
 * @param dataArea A copy of the data area from the destroyed element.
 */
void destroySAFNSCDataArea(RegistryDataArea dataArea);

/**
 * Function that cleans up data stored in a AIOCONN type registry
 * element.
 *
 * @param dataArea A copy of the data area from the destroyed element.
 */
void destroyAIOCONNDataArea(RegistryDataArea dataArea);

/**
 * Put an entry in the registry.
 *
 * @param dataType       A type code to help identify the format of the data.  This is used for dump
 *                         formatting as well as to determine the appropriate deletion routine to call
 *                         to clean up the data area when the element is freed.
 * @param dataAreaPtr    The data to store in the new entry.
 * @param outputTokenPtr A pointer to an area of storage that the @c RegistryToken for the new element
 *                         will be copied into.
 *
 * @return 0 if a new element was successfully created and a corresponding token was copied into
 *         @c outputToken, or an error code if the operation failed.
 */
int registryPut(RegistryDataType dataType, RegistryDataArea* dataAreaPtr, RegistryToken* outputTokenPtr);

/**
 * Get data back from the registry AND keep it from going away.  This function will increment
 * a use count to prevent anyone from freeing the element while there are still users.  The
 * caller of this function is responsible for calling @registrySetUnused when they are done
 * using the data in the element.
 *
 * If someone tries to free the element via @registryFree while the use count is greater than
 * zero, the actual freeing of the element will be delayed until the use count is 0.  During
 * this time, no new users can access the element via @registryGetAndSetUsed.
 *
 * @param tokenPtr    A token you got back from @c registryPut.
 * @param dataAreaPtr Points to an area that the registry element's data area will be copied to,
 *                      assuming that the lock was successfully obtained.
 *
 * @return 0 if the element was successfully marked in use and data was copied to @c dataPtr,
 *         or an error code if the token was invalid, or the token was valid but could not be 
 *         marked in use because of a free or pending free
 */
int registryGetAndSetUsed(RegistryToken* tokenPtr, RegistryDataArea* dataAreaPtr);

/**
 * Indicate that a user is no longer using the element.  Must have been previously set to be in
 * use with @c registryGetAndSetUsed.
 *
 * @param tokenPtr A token you got back from @c registryPut.
 * @parm alreadyVerified indication that a verify has already been done.
 *
 * @return 0 if the lock was successfully released, 
 *         an error code if your token is bad, 
 *         an error code if it the token is good but the entry wasn't in use or was freed (oops!).
 */
int registrySetUnused(RegistryToken* tokenPtr, unsigned char alreadyVerified);

/**
 * Remove an entry from the registry.  The entry might not actually be immediately released
 * if it is in use, but you can't make a successful @c registryGetAndSetUsed call with this
 * token after calling @c registryFree.
 *
 * @param tokenPtr A token you got back from @c registryPut.
 * @parm alreadyVerified indication that a verify has already been done.
 *
 * @return 0 if the token was valid, or an error code otherwise.
 */
int registryFree(RegistryToken* tokenPtr, unsigned char alreadyVerified);

#endif /* REGISTRY_H_ */
