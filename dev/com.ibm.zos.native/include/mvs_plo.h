/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
#ifndef _BBOZ_MVS_PLO_H
#define _BBOZ_MVS_PLO_H

/**
 * @file
 *
 * This part contains metal C wrappers around the PLO instruction.
 *
 * Here is an example of a compare-and-swap-and-store CSSTX, which performs a
 * compare-and-swap on a 16 byte area, and if successful, performs a store of
 * another 16 byte area concurrently with the compare-and-swap (explanation
 * follows the code):
 *
 * \code
 * PloCompareAndSwapAreaDoubleWord_t swapArea;
 * PloStoreAreaDoubleWord_t storeArea;
 * struct localCommClientAnchor* serverLOCL_p = connHandle_p->bbgzlscl_p->serverLOCL_p;
 *
 * unsigned long long* counter_p = ((unsigned long long*)(&(serverLOCL_p->_available4[0])));
 * unsigned long long* store_p = ((unsigned long long*)(&(serverLOCL_p->_available4[8])));
 *
 * swapArea.expectedValue = *counter_p;
 * swapArea.replaceValue = swapArea.expectedValue + 1;
 * swapArea.compare_p = counter_p;
 *
 * storeArea.storageLocation_p = store_p;
 * memcpy(&(storeArea.storeValue), "DEADBEEF", sizeof(storeArea.storeValue));
 *
 * int ploRc = ploCompareAndSwapAndStoreDoubleWord(serverLOCL_p, &swapArea, &storeArea);
 * \endcode
 *
 * In this example we are pretending that the server LOCL has a counter at the beginning
 * of _available4, and a string follows that.  If we successfully increment the counter
 * then we want to store the string following the counter.  We set up the swap area to
 * contain a pointer to the counter, as well as the current value of the counter.  We
 * store the value we want to update the counter with in the replace value section of
 * the swap area.  We then store the string that we want to store in the store area,
 * along with the address where the string should be stored.
 *
 * When we perform the PLO, if the swap is successful, the store will be performed and
 * ploRc will be 0.  Otherwise, neither the swap nor the store are performed.  The first
 * parameter is the lock word, and we are specifying the address of serverLOCL.  All PLO
 * instructions being performed with the same lock word will be serialized against each
 * other.  This is what enforces the atomic behavior of the compare-and-swap and the
 * store.
 *
 * These instructions are useful when serializing data structures shared by several
 * address spaces, which are more complicated than can be serialized by a simple compare
 * and swap, and are updated often enough that the overhead of an ENQ cannot be used.
 * A linked list which supports deletes in the middle of the list is an example of
 * a case where PLO could be used.
 */

#include <metal.h>

#define PLO_CS_AREA_MACRO(tag,type) typedef struct ploCompareAndSwapArea##tag { \
  /** Pointer to the area in storage that is to be compared. */  \
  void* compare_p; \
  /** The value that is expected (currently) at compare_p */  \
  type expectedValue; \
  /** The value that will be replace at compare_p if expected = current. */ \
  type replaceValue; \
} PloCompareAndSwapArea##tag##_t

/** A quad word definition for use by PLO. */
typedef struct ploQuadWord { char quad[16]; } PloQuadWord_t;

/** Storage area for a compare-and-swap of a full word. */
PLO_CS_AREA_MACRO(Word, unsigned int);

/** Storage area for a compare-and-swap of a double word. */
PLO_CS_AREA_MACRO(DoubleWord, unsigned long long);

/** Storage area for a compare-and-swap of a quad word. */
PLO_CS_AREA_MACRO(QuadWord, PloQuadWord_t);

#define PLO_ST_AREA_MACRO(tag,type) typedef struct ploStoreArea##tag { \
    /** Pointer to the area in storage that is to be written to. */ \
    void* storeLocation_p; \
    /** The value that should be stored. */ \
    type storeValue; \
} PloStoreArea##tag##_t

/** Storage area for a store of a full word. */
PLO_ST_AREA_MACRO(Word, unsigned int);

/** Storage area for a store of a double word. */
PLO_ST_AREA_MACRO(DoubleWord, unsigned long long);

/** Storage area for a store of a quad word. */
PLO_ST_AREA_MACRO(QuadWord, PloQuadWord_t);

#define PLO_LOAD_AREA_MACRO(tag,type) typedef struct ploLoadArea##tag { \
    /** Pointer to the area of storage that is to be loaded. */ \
    void* loadLocation_p; \
    /** The value that was loaded. */ \
    type loadValue; \
} PloLoadArea##tag##_t

/** Storage area for a load of a double word. */
PLO_LOAD_AREA_MACRO(DoubleWord, unsigned long long);

/**
 * PLO Compare and load, double word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare is performed using the compare_p parameter.  If the compare
 * and is successful, a load is performed using the load_p parameter.  The load is
 * performed concurrently with the compare.
 *
 * The compare-and-load areas are 8 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param compare_p Details for the compare portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare
 *               will occur, as well as the expected value.  During the PLO,
 *               if the storage at the specified location in memory contains the expected
 *               value, then the area requested by load_p is loaded.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified compare location in memory.
 * @param load_p Details for the load portion of the PLO.  The load will occur if
 *               the compare portion succeeds.  This structure contains a
 *               pointer to the location in memory that should be loaded, as well
 *               as an area where the loaded area is returned.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of compare_p is updated to contain the contents of storage at the compare location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 *
 */
int ploCompareAndLoadDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* compare_p, PloLoadAreaDoubleWord_t* load_p);

/**
 * PLO Compare and swap, double word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare-and-swap is performed using the swap_p parameter.
 *
 * The compare-and-swap area is 8 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param swap_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of swap_p is updated to contain the contents of storage at the swap location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 */
int ploCompareAndSwapDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p);

/**
 * PLO Compare and swap, quad word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare-and-swap is performed using the swap_p parameter.
 *
 * The compare-and-swap area is 16 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param swap_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of swap_p is updated to contain the contents of storage at the swap location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 */
int ploCompareAndSwapQuadWord(void* lock_p, PloCompareAndSwapAreaQuadWord_t* swap_p);

/**
 * PLO Double Compare and swap, double word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare is performed using the swap_p parameter and the swap2_p parameter.
 * If both compares are successful then the 2 replacement values are swapped in.
 *
 * The compare-and-swap areas are 8 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param swap_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory. The area is only replaced
 *               when the compare for this area and the next area are both successful.
 * @param swap2_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory. The area is only replaced
 *               when the compare for this area and the previous area are both successful.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of swap_p is updated to contain the contents of storage at the swap location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 *         Additionally, the expected value of swap2_p is updated to contain the contents
 *         of storage at the swap location at the time the PLO instruction was executed
 *         if the return code is non-zero.
 */
int ploDoubleCompareAndSwapDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p, PloCompareAndSwapAreaDoubleWord_t* swap2_p);

/**
 * PLO Compare and swap and store, double word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare-and-swap is performed using the swap_p parameter.  If the compare
 * and swap is successful, a store is performed using the store_p parameter.  The store is
 * performed concurrently with the compare-and-swap.
 *
 * The compare-and-swap and store areas are 8 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param swap_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory.
 * @param store_p Details for the store portion of the PLO.  The store will occur if
 *                the compare-and-swap portion succeeds.  This structure contains a
 *                pointer to the location in memory that should be updated, as well
 *                as the value that should be stored there.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of swap_p is updated to contain the contents of storage at the swap location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 */
int ploCompareAndSwapAndStoreDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p, PloStoreAreaDoubleWord_t* store_p);

/**
 * PLO Compare and swap and store, quad word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare-and-swap is performed using the swap_p parameter.  If the compare
 * and swap is successful, a store is performed using the store_p parameter.  The store is
 * performed concurrently with the compare-and-swap.
 *
 * The compare-and-swap and store areas are 16 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param swap_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory.
 * @param store_p Details for the store portion of the PLO.  The store will occur if
 *                the compare-and-swap portion succeeds.  This structure contains a
 *                pointer to the location in memory that should be updated, as well
 *                as the value that should be stored there.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of swap_p is updated to contain the contents of storage at the swap location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 */
int ploCompareAndSwapAndStoreQuadWord(void* lock_p, PloCompareAndSwapAreaQuadWord_t* swap_p, PloStoreAreaQuadWord_t* store_p);

/**
 * PLO Compare and swap and double store, double word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare-and-swap is performed using the swap_p parameter.  If the compare
 * and swap is successful, two stores are performed using the store1_p and store2_p
 * parameters.  The stores are performed concurrently with the compare-and-swap.
 *
 * The compare-and-swap and store areas are 8 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param swap_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory.
 * @param store1_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 * @param store2_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of swap_p is updated to contain the contents of storage at the swap location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 */
int ploCompareAndSwapAndDoubleStoreDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p,
                                              PloStoreAreaDoubleWord_t* store1_p, PloStoreAreaDoubleWord_t* store2_p);

/**
 * PLO Compare and swap and double store, quad word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare-and-swap is performed using the swap_p parameter.  If the compare
 * and swap is successful, two stores are performed using the store1_p and store2_p
 * parameters.  The stores are performed concurrently with the compare-and-swap.
 *
 * The compare-and-swap and store areas are 16 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param swap_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory.
 * @param store1_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 * @param store2_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of swap_p is updated to contain the contents of storage at the swap location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 */
int ploCompareAndSwapAndDoubleStoreQuadWord(void* lock_p, PloCompareAndSwapAreaQuadWord_t* swap_p,
                                            PloStoreAreaQuadWord_t* store1_p, PloStoreAreaQuadWord_t* store2_p);

/**
 * PLO Compare and swap and triple store, double word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare-and-swap is performed using the swap_p parameter.  If the compare
 * and swap is successful, three stores are performed using the store1_p, store2_p and store3_p
 * parameters.  The stores are performed concurrently with the compare-and-swap.
 *
 * The compare-and-swap and store areas are 8 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param swap_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory.
 * @param store1_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 * @param store2_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 * @param store3_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of swap_p is updated to contain the contents of storage at the swap location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 */
int ploCompareAndSwapAndTripleStoreDoubleWord(void* lock_p, PloCompareAndSwapAreaDoubleWord_t* swap_p,
                                              PloStoreAreaDoubleWord_t* store1_p, PloStoreAreaDoubleWord_t* store2_p,
                                              PloStoreAreaDoubleWord_t* store3_p);

/**
 * PLO Compare and swap and triple store, quad word area.
 *
 * A lock word is used to serialize concurrent access to PLO instructions using the same
 * lock word.  A compare-and-swap is performed using the swap_p parameter.  If the compare
 * and swap is successful, three stores are performed using the store1_p, store2_p and store3_p
 * parameters.  The stores are performed concurrently with the compare-and-swap.
 *
 * The compare-and-swap and store areas are 16 bytes in length.
 *
 * @param lock_p A pointer to a location in storage which is used to serialize concurrent
 *               users of PLO against the same location in storage.  For example, if PLO
 *               is being used to serialize a list, the lock_p might point to the head
 *               of the list for all operations modifying the list.
 * @param swap_p Details for the compare-and-swap portion of the PLO.  This structure
 *               contains a pointer to the location in memory where the compare-and-swap
 *               will occur, as well as the expected and replacement values.  During the
 *               PLO, if the storage at specified location in memory contains the expected
 *               value, then that storage will be replaced with the replacement value.
 *               Otherwise, the expected value will be replaced with the contents of
 *               storage at the specified location in memory.
 * @param store1_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 * @param store2_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 * @param store3_p Details for the store portion of the PLO.  The store will occur if
 *                 the compare-and-swap portion succeeds.  This structure contains a
 *                 pointer to the location in memory that should be updated, as well
 *                 as the value that should be stored there.
 *
 * @return 0 if the PLO is successful, non-zero if not.  Additionally, the expected value
 *         of swap_p is updated to contain the contents of storage at the swap location
 *         at the time the PLO instruction was executed if the return code is non-zero.
 */
int ploCompareAndSwapAndTripleStoreQuadWord(void* lock_p, PloCompareAndSwapAreaQuadWord_t* swap_p,
                                              PloStoreAreaQuadWord_t* store1_p, PloStoreAreaQuadWord_t* store2_p,
                                              PloStoreAreaQuadWord_t* store3_p);

#endif
