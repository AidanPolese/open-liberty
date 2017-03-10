/**
 * Misc functions involving the TIOT.
 *
 */

#ifndef SERVER_UTIL_TIOT_H_
#define SERVER_UTIL_TIOT_H_

/**
 * Apparently IEFTIOT1 (the TIOT macro) does not define a DSECT, so we can't 
 * use the dsect-mapping script in the build to automatically build the c mapping.
 *
 * See http://publibz.boulder.ibm.com/epubs/pdf/iea3d602.pdf for mapping and description.
 */
#pragma pack(packed)
typedef struct {
    char tioelngh;          //!< Length in bytes of this entry
    char tioestta;          //!< status byte
    char tioewtct;          //!< Number of devices requested for this data set
    char tioelink;          //!< 
    char tioeddnm[8];       //!< DD name
    char tioejfcb[3];       //!< SWA virtual address token
    char tioesttc;          //!< status bytes
} tiot_dd;
#pragma pack(reset)

#pragma pack(packed)
typedef struct {
    char tiocnjob[8];       //!< jobname
    char tiocstpn[8];       //!< stepname
    char tiocjstn[8];       //!< jobstep name
    tiot_dd tioentry[0];    //!< 0 or more DD entries. Last entry followed by a word of 0's (null-term'ed).
} tiot;
#pragma pack(reset)

/**
 * @return the TIOT ptr, retrieved from the TCBTIO field off the TCB.
 */
tiot * tiot_getTiot() ;

/**
 * @param tiot_p the TIOT
 * @param prevTioentry_p the previous tioentry, or NULL to get the first tioentry.
 *
 * @return the next tioentry after the given prevTioentry_p, or NULL if no more entries.
 */
tiot_dd * tiot_getNextTioentry(tiot * tiot_p, tiot_dd * prevTioentry_p) ;

/**
 * @param tiot_p the TIOT
 * @param ddname - The ddname, must be either null-termed or at most 8 bytes long
 *
 * @return 1 if the given DD is defined; 0 otherwise.
 */
int tiot_isDDDefined( tiot * tiot_p, const char * ddname ) ;


#endif /* SERVER_UTIL_TIOT_H_ */
