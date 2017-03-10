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
#include <string.h>

#include "../include/server_util_tiot.h"
#include "include/CuTest.h"

extern void println(const char *, ...);
extern int tiot_strpad(char* string, size_t len, unsigned char pad) ;

/**
 * ...
 */
void test_strpad(CuTest * tc) {

    {
        char pad_area[8] = "1234567\0";

        tiot_strpad( pad_area, 8, ' ' );
        CuAssertMemEquals(tc,"1234567 ", pad_area,8);        

        tiot_strpad( pad_area, 8, ' ' );
        CuAssertMemEquals(tc,"1234567 ", pad_area,8);        
    }

    {
        char pad_area[9] = "12345678\0";
        tiot_strpad( pad_area, 8, ' ' );
        CuAssertMemEquals(tc,"12345678", pad_area,8);        
    }

    {
        char pad_area[8] = "1234";
        tiot_strpad( pad_area, 8, '8' );
        CuAssertMemEquals(tc,"12348888", pad_area,8);        
    }

}


/**
 * ...
 */
void test_isDDDefined(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    int rawTiot[] = { 0xC2C2C7E9, 0xE2D9E540, 0xC2C2C7E9, 0xE2D9E540,                     // |BBGZSRV BBGZSRV |.......@.......@| 
                      0x40404040, 0x40404040, 0x14010102, 0xE6D3D7E4,                     // |        ....WLPU|@@@@@@@@........| 
                      0xC4C9D940, 0x00006F00, 0x80F55000, 0x14010102,                     // |DIR ..?..5&.....|...@..o...P.....| 
                      0xE2E3C4D6, 0xE4E34040, 0x0000FF00, 0x80000000,                     // |STDOUT  ........|......@@........| 
                      0x14010102, 0xE2E3C4C5, 0xD9D94040, 0x00012F00,                     // |....STDERR  ....|..........@@../.| 
                      0x80000000, 0x14810100, 0x00000000, 0xF0F0F0F5,                     // |.....a......0005|................| 
                      0x00018F00, 0x81000000, 0x00000000, 0x14010102 };                   // |....a...........|................| 

    tiot * tiot_p = (tiot *) &rawTiot[0];

    CuAssertTrue(tc, tiot_isDDDefined(tiot_p, "WLPUDIR") );
    CuAssertTrue(tc, tiot_isDDDefined(tiot_p, "WLPUDIR ") );
    CuAssertTrue(tc, tiot_isDDDefined(tiot_p, "WLPUDIR xxx") );     // trailing xxx is chopped off
    CuAssertFalse(tc, tiot_isDDDefined(tiot_p, "WLPU") );
    CuAssertFalse(tc, tiot_isDDDefined(tiot_p, "WLPU    ") );

    CuAssertTrue(tc, tiot_isDDDefined(tiot_p, "STDOUT") );
    CuAssertTrue(tc, tiot_isDDDefined(tiot_p, "STDERR") );
    CuAssertFalse(tc, tiot_isDDDefined(tiot_p, "STD") );
    CuAssertFalse(tc, tiot_isDDDefined(tiot_p, NULL) );
    CuAssertFalse(tc, tiot_isDDDefined(tiot_p, "") );
    CuAssertFalse(tc, tiot_isDDDefined(tiot_p, "        ") );

    CuAssertFalse(tc, tiot_isDDDefined(NULL, "WLPUDIR") );
}

/**
 * ...
 */
void test_getNextTioentry(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // +--------------------------------------------------------------------------+
    // |OSet| A=00000000008dafd0 Length=00000c0 |     EBCDIC     |     ASCII      |
    // +----+-----------------------------------+----------------+----------------+
    // |0000|C2C2C7E9 E2D9E540 C2C2C7E9 E2D9E540|BBGZSRV BBGZSRV |.......@.......@|
    // |0010|40404040 40404040 14010102 E6D3D7E4|        ....WLPU|@@@@@@@@........|
    // |0020|C4C9D940 00006F00 80F55000 14010102|DIR ..?..5&.....|...@..o...P.....|
    // |0030|E2E3C4D6 E4E34040 0000FF00 80000000|STDOUT  ........|......@@........|
    // |0040|14010102 E2E3C4C5 D9D94040 00012F00|....STDERR  ....|..........@@../.|
    // |0050|80000000 14810100 00000000 F0F0F0F5|.....a......0005|................|
    // |0060|00018F00 81000000 00000000 14010102|....a...........|................|
    // |0070|E2E3C4D6 E4E34040 0000FF00 80000000|STDOUT  ........|......@@........|
    // |0080|14010102 E2E3C4C5 D9D94040 00012F00|....STDERR  ....|..........@@../.|
    // |0090|80000000 00000000 00000000 00000000|................|................|
    // |00a0|00000000 00000000 00000000 00000000|................|................|
    // |00b0|00000000 00000000 00000000 00000000|................|................|
    // +--------------------------------------------------------------------------+

    int rawTiot[] = { 0xC2C2C7E9, 0xE2D9E540, 0xC2C2C7E9, 0xE2D9E540,                     // |BBGZSRV BBGZSRV |.......@.......@| 
                      0x40404040, 0x40404040, 0x14010102, 0xE6D3D7E4,                     // |        ....WLPU|@@@@@@@@........| 
                      0xC4C9D940, 0x00006F00, 0x80F55000, 0x14010102,                     // |DIR ..?..5&.....|...@..o...P.....| 
                      0xE2E3C4D6, 0xE4E34040, 0x0000FF00, 0x80000000,                     // |STDOUT  ........|......@@........| 
                      0x14010102, 0xE2E3C4C5, 0xD9D94040, 0x00012F00,                     // |....STDERR  ....|..........@@../.| 
                      0x80000000, 0x14810100, 0x00000000, 0xF0F0F0F5,                     // |.....a......0005|................| 
                      0x00018F00, 0x81000000, 0x00000000, 0x14010102 };                   // |....a...........|................| 

    tiot * tiot_p = (tiot *) &rawTiot[0];

    CuAssertMemEquals(tc,"BBGZSRV ",tiot_p->tiocnjob,8);        
    CuAssertMemEquals(tc,"BBGZSRV ",tiot_p->tiocstpn,8);        
    CuAssertMemEquals(tc,"        ",tiot_p->tiocjstn,8);        

    tiot_dd * tiot_dd_p = tiot_getNextTioentry( tiot_p, NULL );

    CuAssertPtrNotNull(tc, tiot_dd_p );
    CuAssertIntEquals(tc, 0x14, (int)tiot_dd_p->tioelngh);
    CuAssertIntEquals(tc, 0x01, (int)tiot_dd_p->tioestta);
    CuAssertIntEquals(tc, 0x01, (int)tiot_dd_p->tioewtct);
    CuAssertIntEquals(tc, 0x02, (int)tiot_dd_p->tioelink);
    CuAssertMemEquals(tc,"WLPUDIR ",tiot_dd_p->tioeddnm,8);        

    tiot_dd_p = tiot_getNextTioentry( tiot_p, tiot_dd_p );
    CuAssertMemEquals(tc,"STDOUT  ",tiot_dd_p->tioeddnm,8);        

    tiot_dd_p = tiot_getNextTioentry( tiot_p, tiot_dd_p );
    CuAssertMemEquals(tc,"STDERR  ",tiot_dd_p->tioeddnm,8);        

    tiot_dd_p = tiot_getNextTioentry( tiot_p, tiot_dd_p );
    CuAssertIntEquals(tc, 0x14, (int)tiot_dd_p->tioelngh);
    CuAssertIntEquals(tc, 0x81, (int)tiot_dd_p->tioestta);
    CuAssertMemEquals(tc,"0005",&tiot_dd_p->tioeddnm[4],4);        
    // CuAssertMemEquals(tc,"\0\0\0\00005",tiot_dd_p->tioeddnm,8);        

    tiot_dd_p = tiot_getNextTioentry( tiot_p, tiot_dd_p );
    CuAssertPtrIsNull(tc, tiot_dd_p );

    CuAssertPtrIsNull(tc, tiot_getNextTioentry( NULL, NULL));
}



/**
 * The test suite getter method.
 *
 * @return The suite of tests in this part.
 */
CuSuite * server_util_tiot_test_suite() {

    CuSuite* suite = CuSuiteNew("server_util_tiot_test");

    SUITE_ADD_TEST(suite, test_strpad);
    SUITE_ADD_TEST(suite, test_isDDDefined);
    SUITE_ADD_TEST(suite, test_getNextTioentry);

    return suite;
}




