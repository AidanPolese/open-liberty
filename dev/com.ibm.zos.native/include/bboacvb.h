 /*--------------------------------------------------------------------
 *
 *   Header name: bboacvb.h
 *   Component:   WAS z/OS
 *
 *   Descriptive name:  WAS Optimized local adapters Convert to Binary
 *
 *   Proprietary statement:
 *
 * IBM Confidential
 * OCO Source Materials
 * 5655-N01 Copyright IBM Corp. 2013
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has been
 * deposited with the U.S. Copyright Office.
 * Status = H28W800
 *
 *
 *   Created by:  WAS on z/OS Optimized local adapters
 *
 *   Serialization:
 *
 *   Function:
 *
 *    This header provides mappings for packed decimal to binary conversion
 *
 *   External classification: none
 *   End of external classification:
 *
 *   Method of access:
 *     C++: #include bboacvb.h
 *
 *
 *   Deleted by: User or System
 *
 *   Frequency:
 *
 *   Dependencies:  None
 *
 *   Distribution library:  n/a
 *
 *   Change activity:
 * $PM88133,     H28W800, 20130501, PDFG: Created
 *--------------------------------------------------------------------*/
#ifndef _BBOA_PRIVATE_BBOACVB_H_INCLUDE
#define _BBOA_PRIVATE_BBOACVB_H_INCLUDE

 /* Extra builtins */
 /* The compiler on our system is at z/OS 1.12, but the include file */
 /* for builtins is much lower (pre 1.9).  We'd like to use the      */
 /* builtin for convert to binary, so we've copied the relevent      */
 /* sections of builtin.h here, along with appropriate guards to     */
 /* prevent us from using them when we're not supposed to.           */
 #include <builtins.h>

 #ifndef __cvb
 #ifdef __cplusplus
 extern "C"{
 #endif

 /*
  * _MI_BUILTIN is predefined by the compiler for langlvl non-ansi.
  * You can explicitly un/define the macro.
  * You cannot take the address of these functions.
  */
 #ifdef _MI_BUILTIN
   /**************************************/
   /**  builtins for 1.9                **/
   /**************************************/
    #if (__COMPILER_VER__ >= 0x41090000)

      #ifdef __cplusplus
      extern "builtin"{
      #else
        #pragma linkage(__cvb, builtin)
        #pragma linkage(__cvd, builtin)
        #pragma linkage(__zap, builtin)
      #endif

      #ifdef _NO_PROTO
        int  __cvb();
        void __cvd();
        int  __zap();
      #else
        int  __cvb(char *op2);
        void __cvd(int op1, char *op2);
        int  __zap(unsigned char *op1, unsigned char len1,
                   unsigned char *op2, unsigned char len2);
      #endif

      #ifdef __cplusplus
      }
      #endif
    #endif /* __COMPILER_VER__ >= 0x41090000 */
 #endif /* _MI_BUILTIN */

 #ifdef __cplusplus
 }
 #endif
 #endif /* __cvb */

 /* ----------------------------------------------------------------- */
 /* Convert a packed decimal number to decimal.                       */
 /* The builtin packed decimal types are available in C but not C++.  */
 /* ----------------------------------------------------------------- */
 int convertPackedDecimalToInt(void* ptrToPackedDecimal, int digits)
 {
   int intValue = 0;
   char pdHolder[8];
   
   int bytesToCopy = ((digits + 1) + ((digits + 1) % 2)) / 2;
   memset(pdHolder, 0, sizeof(pdHolder) - bytesToCopy);
   memcpy(&(pdHolder[sizeof(pdHolder) - bytesToCopy]), ptrToPackedDecimal,
          bytesToCopy);
   if ((digits % 2) == 0)
   {
     pdHolder[sizeof(pdHolder) - bytesToCopy] &= 0x0F;
   }  

   return __cvb(pdHolder);
 }

#endif 
