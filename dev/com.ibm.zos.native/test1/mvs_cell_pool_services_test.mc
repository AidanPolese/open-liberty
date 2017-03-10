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
#include <metal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "../include/mvs_cell_pool_services.h"
#include "include/CuTest.h"

/**
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 *
 * Native unit tests for mvs_cell_pool_services.
 *
 */

extern void println(char * message_p, ...);

/**
 * This user data struct is hung off the cell pool anchor and passed around 
 * to the auto-grow and destroy functions in order to verify that things are
 * working as expected.
 */
struct CellPoolUserData {
    void * extent_p[3];
    int extent_count;
    void * deleted_extent_p[3];
    void * deleted_anchor_p;
    int delete_count;
};

/**
 * Auto-grow function. Used by test_autoGrowCellPool.
 *
 * Keeps track of the extents being allocated, in order to verify later that
 * (a) this function does indeed get called, and (b) that the extents are eventually
 * deleted by myDestroy.
 *
 * @param size_p - Output - The size of the new extent
 * @param cell_pool_id - The id of the cell pool to be grown
 *
 * @return the addr of the new extent.
 */
void * autoGrow(long long * size_p, long long cell_pool_id) {

    // Get the user data, where the test keeps track of things like how
    // many extents have been allocated (i.e how many times has this autoGrow
    // function been called).
    struct CellPoolUserData * user_data_p = (struct CellPoolUserData *) getCellPoolUserData(cell_pool_id);

    // Allocate another extent.
    long long storage_len = 2 * 1024;
    void * storage_p = malloc(storage_len);

    user_data_p->extent_p[ user_data_p->extent_count ] = storage_p;
    user_data_p->extent_count++;

    // Set the output parm size_p.
    *size_p = storage_len;

    println(__FUNCTION__ ": extent_count: %d", user_data_p->extent_count);
    println(__FUNCTION__ ": new storage_p: %x", storage_p);

    return storage_p;
}

/**
 * Destroy function.  Used by test test_autoGrowCellPool. 
 *
 * Keeps track of the storage being deleted in order to verify
 * later that all the correct extents/anchors were deleted.
 */
void myDestroy(unsigned char storageType, void* storage_p, long long id) {
    println(__FUNCTION__ ": entry, storageType: %d, storage_p %x", (int)storageType, storage_p);

    struct CellPoolUserData * user_data_p = (struct CellPoolUserData *) getCellPoolUserData(id);

    switch (storageType) {
        case CELL_POOL_ANCHOR_STORAGE_TYPE: 
            println(__FUNCTION__ ": deleting anchor storage_p %x", storage_p);

            // Remember the storage_p for verification later.
            user_data_p->deleted_anchor_p = storage_p;
            free(storage_p);

            break;

        case CELL_POOL_EXTENT_STORAGE_TYPE:
            // do nothing.
            println(__FUNCTION__ ": skipping extent storage %x", storage_p);
            break;

        case CELL_POOL_CELL_STORAGE_TYPE:
            println(__FUNCTION__ ": deleting cell storage_p %x", storage_p);

            // Remember the storage_p for verification later.
            user_data_p->deleted_extent_p[ user_data_p->delete_count ] = storage_p;
            user_data_p->delete_count++;
            free(storage_p);

            break;
    }

    println(__FUNCTION__ ": exit");
}

/**
 * Test auto-grow + user data + destroy.
 *
 * This test has the following steps:
 *   1. get storage
 *   2. build cell pool
 *   3. setup auto-grow and user data
 *   4. obtain enough cells to force auto-grow
 *   5. verify that auto-grow function was called (via user data)
 *   6. destroy and verify the auto-grow extents are destroyed (via user data)
 */
void test_autoGrowCellPool(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // 1. get anchor storage
    long long storage_len = 1024;
    long long cell_size = 128;
    void * anchor_p = malloc(storage_len);
    println(__FUNCTION__ ": initial anchor_p: %x", anchor_p);

    // 2. build cell pool
    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(buildCellPoolFlags));

    flags.autoGrowCellPool = 1;
    flags.skipInitialCellAllocation = 1;    // Must set this in order to safely destroy the cell pool later.
                                            // See comments in mvs_cell_pool_services.h for freeCellPoolStorage_t.

    println(__FUNCTION__ ": building cell pool...");
    long long cell_pool_id = buildCellPool(anchor_p,
                                           storage_len,
                                           cell_size,
                                           "TESTCP1",
                                           flags);  
    CuAssertTrue(tc, (cell_pool_id != 0));

    // 3. Setup auto-grow and user data.
    struct CellPoolUserData user_data;
    memset(&user_data, 0, sizeof(user_data));

    setCellPoolAutoGrowFunction(cell_pool_id, &(autoGrow) );
    setCellPoolUserData(cell_pool_id, (void *)&user_data);

    // 4. obtain enough cells to force auto-grow
    println(__FUNCTION__ ": grabbing cells...");
    for (int i=0; i < 20; ++i) {
        void * cell_p = getCellPoolCell( cell_pool_id );
        println(__FUNCTION__ ": cell_p: %x", cell_p);
        CuAssertTrue(tc, (cell_p != NULL));
    }

    // 5. verify that auto-grow function was called (via user data)
    CuAssertIntEquals(tc, 3, user_data.extent_count);
    
    // 6. destroy and verify the auto-grow extents are destroyed (via user data)
    destroyCellPool(cell_pool_id, &(myDestroy));

    CuAssertIntEquals(tc, 3, user_data.delete_count);
    CuAssertPtrEquals(tc, anchor_p, user_data.deleted_anchor_p);

    // Note: extents are deleted in the reverse order that they were created
    //       (because of the way growCellPool chains the extents).
    CuAssertPtrEquals(tc, user_data.extent_p[0], user_data.deleted_extent_p[2]);
    CuAssertPtrEquals(tc, user_data.extent_p[1], user_data.deleted_extent_p[1]);
    CuAssertPtrEquals(tc, user_data.extent_p[2], user_data.deleted_extent_p[0]);

    println(__FUNCTION__ ": exit");
}


/**
 * Test basic cell pool operations.
 */
void test_cellPoolBasics(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // 1. get storage
    long long storage_len = 2 * 1024;
    long long cell_size = 128;
    void * storage_p = malloc(storage_len);
    println(__FUNCTION__ ": initial storage_p: %x", storage_p);

    // 2. build cell pool
    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(buildCellPoolFlags));

    long long cell_pool_id = buildCellPool(storage_p,
                                           storage_len,
                                           cell_size,
                                           "TESTCP2",
                                           flags);  
    CuAssertTrue(tc, (cell_pool_id != 0));

    // 3. obtain some cells 
    println(__FUNCTION__ ": grabbing cells...");
    for (int i=0; i < 3; ++i) {
        void * cell_p = getCellPoolCell( cell_pool_id );
        println(__FUNCTION__ ": cell_p: %x", cell_p);
        CuAssertPtrNotNull(tc, cell_p);
    }

    // Delete the cell pool.
    free(storage_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Test building a cell pool with an initial storage_p that
 * is NOT on a quad word boundary.  Studies have shown that
 * non-quad-word-aligned cell pool anchors end up causing 0C6
 * abends under getCellPoolCell.
 *
 * NOTE: this test is disabled.  For now it's just a reminder
 * to make your cell pool storage_p quad-word aligned.
 * 
 */
void test_cellPoolBasicsNonQuadWordBoundary(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // 1. get storage
    long long storage_len = 2 * 1024;
    long long cell_size = 128;
    void * orig_storage_p = malloc(storage_len + 8);
    void * storage_p = (void *) ((long long)orig_storage_p | 0x08L); // Force off quad word boundary

    println(__FUNCTION__ ": initial storage_p: %x", storage_p);

    // 2. build cell pool
    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(buildCellPoolFlags));

    long long cell_pool_id = buildCellPool(storage_p,
                                           storage_len,
                                           cell_size,
                                           "TESTCP2",
                                           flags);  
    CuAssertTrue(tc, (cell_pool_id != 0));

    // 3. obtain some cells 
    println(__FUNCTION__ ": grabbing cells...");
    for (int i=0; i < 3; ++i) {
        void * cell_p = getCellPoolCell( cell_pool_id );
        println(__FUNCTION__ ": cell_p: %x", cell_p);
        CuAssertPtrNotNull(tc, cell_p);
    }

    // Delete the cell pool.
    free(orig_storage_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Test setting/getting user data
 */
void test_cellPoolUserData(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // 1. get storage
    long long storage_len = 2 * 1024;
    long long cell_size = 128;
    void * storage_p = malloc(storage_len);

    // 2. build cell pool
    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(buildCellPoolFlags));

    long long cell_pool_id = buildCellPool(storage_p,
                                           storage_len,
                                           cell_size,
                                           "TESTCP2",
                                           flags);  
    CuAssertTrue(tc, (cell_pool_id != 0));

    // 3. Test set/get user data.
    void * user_data = (void *) 0x1234;
    setCellPoolUserData(cell_pool_id, user_data);

    CuAssertPtrEquals(tc,user_data,getCellPoolUserData(cell_pool_id));

    // Delete the cell pool.
    free(storage_p);

    println(__FUNCTION__ ": exit");
}

/**
 * Test cell pool stats.
 */
void test_cellPoolStatus(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // 1. get storage
    long long storage_len = 2 * 1024;
    long long cell_size = 128;
    void * storage_p = malloc(storage_len);

    // 2. build cell pool
    buildCellPoolFlags flags;
    memset(&flags, 0, sizeof(buildCellPoolFlags));

    long long cell_pool_id = buildCellPool(storage_p,
                                           storage_len,
                                           cell_size,
                                           "TESTCP3",
                                           flags);  
    CuAssertTrue(tc, (cell_pool_id != 0));
    println(__FUNCTION__ ": built cell pool: %x", (void *)cell_pool_id);

    // 3. Get some cells
    for (int i=0; i < 4; ++i) {
        void * cell_p = getCellPoolCell( cell_pool_id );
        println(__FUNCTION__ ": obtained cell_p: %x", cell_p);
        CuAssertTrue(tc, (cell_p != NULL));
    }


    // 3. Verify the stats.
    CellPoolStatus_t cell_pool_status;
    getCellPoolStatistics(cell_pool_id, &cell_pool_status);

    println(__FUNCTION__ ": cell pool status: name:%s, totalCells:%d, availableCells:%d, cellSize:%d, numberOfExtents:%d", 
            cell_pool_status.poolName, 
            cell_pool_status.totalCells,
            cell_pool_status.availableCells,
            cell_pool_status.cellSize,
            cell_pool_status.numberOfExtents);

    CuAssertStrEquals(tc, "TESTCP3", &cell_pool_status.poolName[0] );
    CuAssertIntEquals(tc, 8, cell_pool_status.totalCells);
    CuAssertIntEquals(tc, 4, cell_pool_status.availableCells);
    CuAssertIntEquals(tc, cell_size, cell_pool_status.cellSize);
    CuAssertIntEquals(tc, 0, cell_pool_status.numberOfExtents);

    // Delete the cell pool.
    free(storage_p);

    println(__FUNCTION__ ": exit");
}

/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * mvs_cell_pool_services_test_suite() {

    CuSuite* suite = CuSuiteNew("mvs_cell_pool_services_test");

    SUITE_ADD_TEST(suite, test_autoGrowCellPool);
    SUITE_ADD_TEST(suite, test_cellPoolBasics);
    // SUITE_ADD_TEST(suite, test_cellPoolBasicsNonQuadWordBoundary);
    SUITE_ADD_TEST(suite, test_cellPoolUserData);
    SUITE_ADD_TEST(suite, test_cellPoolStatus);

    return suite;
}


