                   ??=ifndef __metal_h
                   ??=pragma filetag("IBM-1047")
                   ??=define __metal_h 1
                   #pragma nomargins nosequence
                   #pragma checkout (suspend)
 /***************************************************************
 * <metal.h> header file                                        *
 *                                                              *
 * LICENSED MATERIALS - PROPERTY OF IBM                         *
 *                                                              *
 * 5694-A01                                                     *
 *                                                              *
 * COPYRIGHT IBM CORP. 2007, 2009                               *
 *                                                              *
 * US GOVERNMENT USERS RESTRICTED RIGHTS - USE,                 *
 * DUPLICATION OR DISCLOSURE RESTRICTED BY GSA ADP              *
 * SCHEDULE CONTRACT WITH IBM CORP.                             *
 *                                                              *
 * STATUS = HSD7760                                             *
 ***************************************************************/
 
 /* _LONG_LONG is defined directly by the compiler
    if the LANGLVL(LONGLONG) option is specified. */
 #if defined(_LONG_LONG)
   #define __LL
 #endif
 
 #if defined(__C99_RESTRICT) && !defined(__NO_RESTRICT)
   #define __restrict__ restrict
 #else
   #define __restrict__
 #endif
 
 #undef __C99
 #undef __C99S
 #undef __C99C
 
 #if !defined(_NOISOC99_SOURCE)
   #if (__STDC_VERSION__ >= 199901L)
     #define __C99S
     #define __C99C
   #elif (__IBM_STDC_VERSION__ >= 199901L)
     #define __C99C
   #endif
   #if (defined(_ISOC99_SOURCE) || \
        defined(__C99S) || \
        defined(__C99C))
     #define __C99
   #endif
 #endif /* !defined(_NOISOC99_SOURCE) */
 
 #ifdef _LP64
 
   #ifdef __EXTENDED__
 
     #ifndef __ptr31
     #define __ptr31(t,n)    t *__ptr32 n;
     #endif
 
   #else
 
     #ifndef __ptr31
     #define __ptr31(t,n)    unsigned int n;
     #endif
 
   #endif
 #else
 
   #ifndef    __ptr31
   #define    __ptr31(t,n)  t *n;
   #endif
 
 #endif
 
 #ifndef __size_t
   #ifdef _LP64
     typedef unsigned long size_t;
   #else
     typedef unsigned int size_t;
   #endif /* _LP64 */
   #define __size_t 1
 #endif
 
 /********************************************************************/
 /*                     csysenv Control Block                        */
 /********************************************************************/
 
 struct __csysenv_s {
  int       __cseversion;      /* Control block version number       */
                               /* must be set.                       */
                               /* Set to __CSE_VERSION_1 for original*/
                               /* layout.
                               /* Set to __CSE_VERSION_2 when heap   */
                               /* service routines replacement is    */
                               /* desired.                           */
  int       __csesubpool;      /* for 31 bit storage                 */
  __ptr31(void, __csetcbowner) /* owning TCB for resources           */
                               /* default: TCB mode - caller tcb,    */
                               /*   SRB. XMEM - CMRO TCB             */
  int       __csereserved;     /* Reserved field                     */
  char      __csettknowner[16]; /* TCB token of owning TCB for       */
                               /* above the bar storage              */
                               /* default: caller tcbtoken           */
                               /*   SRB mode: tcbtoken must be       */
                               /*             specified              */
 
  unsigned int
            __cseheap31initsize; /* Minimum size, in bytes, to obtain
                                for the initial AMODE 31 heap storage.
                                If 0, defaults to 32768 bytes        */
  unsigned int
            __cseheap31incrsize; /* Minimum size, in bytes, to obtain
                                when expanding the AMODE 31 heap.
                                If 0, defaults to 32768 bytes        */
 
 #ifdef __LL
  unsigned long long
            __cseheap64initsize; /* Minimum size, in MB, to obtain
                                for the initial AMODE 64 heap storage.
                                If 0, defaults to 1 MB               */
  unsigned long long
            __cseheap64incrsize; /* Minimum size, in MB, to obtain
                                When expanding the AMODE 64 heap.
                                If 0, defaults to 1MB                */
  unsigned long long
            __cseheap64usertoken; /* usertoken for use with ?iarv64
                                to obtain above the bar storage      */
 #else
  unsigned int
            __cseheap64initsize_hh;
  unsigned int
            __cseheap64initsize; /* Minimum size, in MB, to obtain
                                for the initial AMODE 64 heap storage.
                                If 0, defaults to 1 MB               */
  unsigned int
            __cseheap64incrsize_hh;
  unsigned int
            __cseheap64incrsize; /* Minimum size, in MB, to obtain
                                When expanding the AMODE 64 heap.
                                If 0, defaults to 1MB                */
  unsigned int
            __cseheap64usertoken_hh;
  unsigned int
            __cseheap64usertoken;/* usertoken for use with ?iarv64
                                to obtain above the bar storage      */
 #endif
 
  unsigned int                 /* AMODE 64 Storage Attributes        */
             __cseheap64fprot:1, /* On, AMODE 64 heap storage is to be
                                  fetch protected
                                  Off, storage is not fetch
                                  protected                          */
             __cseheap64cntlauth:1; /* On, AMODE 64 heap storage has
                                  CONTROL=AUTH attribute
                                  Off, storage is CONTROL=UNAUTH     */
  int        __csereserved1[7]; /* Reserved for future use           */
 #if __METAL_CSYSENV_VERSION >=2
  void * (*__ptr64 __cseamode31malloc)(size_t);
                               /* AMODE 31 malloc() replacement rtn  */
  void   (*__ptr64 __cseamode31free)(void *);
                               /* AMODE 31 free() replacement routine*/
  void * (*__ptr64 __cseamode31realloc)(void *,size_t);
                               /* AMODE 31 realloc() replacement rtn */
  void * (*__ptr64 __cseamode64malloc)(size_t);
                               /* AMODE 64 malloc() replacement rtn  */
  void * (*__ptr64 __cseamode64malloc31)(size_t);
                               /* AMODE 64 __malloc31() replcmnt rtn */
  void   (*__ptr64 __cseamode64free)(void *);
                               /* AMODE 64 free() replacement routine*/
  void * (*__ptr64 __cseamode64realloc)(void *,size_t);
                               /* AMODE 64 realloc() replacement rtn */
  void *__ptr64
             __cseheapuserdata;/*                                    */
  int        __csereserved2[8];/* Reserved for future use            */
 #endif /* __METAL_CSYSENV_VERSION >=2 */
 };
 
 #define __CSE_VERSION_1 1
 #define __CSE_VERSION_2 2
 
 #if __METAL_CSYSENV_VERSION >=2
   #define __CSE_CURRENT_VERSION __CSE_VERSION_2
 #else
   #define __CSE_CURRENT_VERSION __CSE_VERSION_1
 #endif /* __METAL_CSYSENV_VERSION >=2 */
 
 typedef  unsigned long long __csysenv_t;
 
 #if __METAL_CSYSENV_VERSION >=2
  struct __csysenvtoken_s {
    char __csetreserved[8];
    void *__ptr64 __csetheapuserdata;
  };
 #endif /* __METAL_CSYSENV_VERSION >=2 */
 
 #ifndef __METAL_STATIC
 
 /********************************************************************/
 /*                        SDCA Control Block                        */
 /********************************************************************/
 
 struct __sdca_s
 {
   char __sdcaid[4];
   int  __sdcalen;
   int  __sdcaversion;
   __ptr31(struct __sys_libv31_s, __sdcalibv31)
  #ifdef _LP64
   struct __sys_libv64_s * __sdcalibv64;
  #else
   int __sdcalibv64_hh;
   int __sdcalibv64_lh;
  #endif
   int __sdcaretcode;
 };
 
 struct __sys_libv31_s
 {
   __ptr31(void, __libfunc[1024])
 };
 
 struct __sys_libv64_s
 {
   void * __libfunc[1024];
 };
 
 /********************************************************************/
 /*                     bcp  ecvt control block                      */
 /********************************************************************/
 
 struct __ecvt_s
 {
 
   int  __ecvtfiller1[216];
   __ptr31(struct __sdca_s,
                  __ecvtsdc)    /* SDC Anchor                        */
 
};
 
 /********************************************************************/
 /*                      bcp  cvt control block                      */
 /********************************************************************/
 
 struct __cvt_s
 {
 
   int  __cvtfiller1[35];
   __ptr31(struct __ecvt_s,
                  __cvtecvt)    /* pointer to the extended cvt.      */
 
 };
 
 typedef struct __cvt_s __CVT;
 
 #define __CVTPTR (*(struct __cvt_s * __ptr32 * __ptr32)16)
 
 /*------------------------------------------------------------------*/
 /* Macro used to address Metal C Runtime Library Services           */
 /*------------------------------------------------------------------*/
 #ifdef _LP64
   #define __METALCALL(op,i)                            \
       ((op * ) ( __CVTPTR ->                           \
                   __cvtecvt -> __ecvtsdc ->            \
                    __sdcalibv64 -> __libfunc[i] ))
 #else
   #define __METALCALL(op,i)                            \
       ((op * ) ( __CVTPTR ->                           \
                   __cvtecvt -> __ecvtsdc ->            \
                    __sdcalibv31 -> __libfunc[i] ))
 #endif  /* _LP64 */
 
 /*------------------------------------------------------------------*/
 /*          The order of these entries must be maintained.          */
 /*                Add new functions at the bottom!                  */
 /*------------------------------------------------------------------*/
 #define   abs       __METALCALL(___ABS       ,  1)
 #define   atoi      __METALCALL(___ATOI      ,  2)
 #define   atol      __METALCALL(___ATOL      ,  3)
 #define   atoll     __METALCALL(___ATOLL     ,  4)
 #define   calloc    __METALCALL(___CALLOC    ,  5)
 #define __cinit     __METALCALL(___CINIT     ,  6)
 #define __cterm     __METALCALL(___CTERM     ,  7)
 #define   div       __METALCALL(___DIV       ,  8)
 #define   free      __METALCALL(___FREE      ,  9)
 #define   isalnum   __METALCALL(___ISALNUM   , 10)
 #define   isalpha   __METALCALL(___ISALPHA   , 11)
 #define   isblank   __METALCALL(___ISBLANK   , 12)
 #define   iscntrl   __METALCALL(___ISCNTRL   , 13)
 #define   isdigit   __METALCALL(___ISDIGIT   , 14)
 #define   isgraph   __METALCALL(___ISGRAPH   , 15)
 #define   islower   __METALCALL(___ISLOWER   , 16)
 #define   isprint   __METALCALL(___ISPRINT   , 17)
 #define   ispunct   __METALCALL(___ISPUNCT   , 18)
 #define   isspace   __METALCALL(___ISSPACE   , 19)
 #define   isupper   __METALCALL(___ISUPPER   , 20)
 #define   isxdigit  __METALCALL(___ISXDIGIT  , 21)
 #define   labs      __METALCALL(___LABS      , 22)
 #define   ldiv      __METALCALL(___LDIV      , 23)
 #define   llabs     __METALCALL(___LLABS     , 24)
 #define   lldiv     __METALCALL(___LLDIV     , 25)
 #define   malloc    __METALCALL(___MALLOC    , 26)
 #define __malloc31  __METALCALL(___MALLOC31  , 27)
 #define   memccpy   __METALCALL(___MEMCCPY   , 28)
 #define   memchr    __METALCALL(___MEMCHR    , 29)
 #define   memcmp    __METALCALL(___MEMCMP    , 30)
 #define   memcpy    __METALCALL(___MEMCPY    , 31)
 #define   memmove   __METALCALL(___MEMMOVE   , 32)
 #define   memset    __METALCALL(___MEMSET    , 33)
 #define   rand      __METALCALL(___RAND      , 34)
 #define   rand_r    __METALCALL(___RAND_R    , 35)
 #define   realloc   __METALCALL(___REALLOC   , 36)
 #define   snprintf  __METALCALL(___SNPRINTF  , 37)
 #define   sprintf   __METALCALL(___SPRINTF   , 38)
 #define   srand     __METALCALL(___SRAND     , 39)
 #define   sscanf    __METALCALL(___SSCANF    , 40)
 #define   strcat    __METALCALL(___STRCAT    , 41)
 #define   strchr    __METALCALL(___STRCHR    , 42)
 #define   strcmp    __METALCALL(___STRCMP    , 43)
 #define   strcpy    __METALCALL(___STRCPY    , 44)
 #define   strcspn   __METALCALL(___STRCSPN   , 45)
 #define   strdup    __METALCALL(___STRDUP    , 46)
 #define   strlen    __METALCALL(___STRLEN    , 47)
 #define   strncat   __METALCALL(___STRNCAT   , 48)
 #define   strncmp   __METALCALL(___STRNCMP   , 49)
 #define   strncpy   __METALCALL(___STRNCPY   , 50)
 #define   strpbrk   __METALCALL(___STRPBRK   , 51)
 #define   strrchr   __METALCALL(___STRRCHR   , 52)
 #define   strspn    __METALCALL(___STRSPN    , 53)
 #define   strstr    __METALCALL(___STRSTR    , 54)
 #define   strtok    __METALCALL(___STRTOK    , 55)
 #define   strtok_r  __METALCALL(___STRTOK_R  , 56)
 #define   strtol    __METALCALL(___STRTOL    , 57)
 #define   strtoll   __METALCALL(___STRTOLL   , 58)
 #define   strtoul   __METALCALL(___STRTOUL   , 59)
 #define   strtoull  __METALCALL(___STRTOULL  , 60)
 #define   tolower   __METALCALL(___TOLOWER   , 61)
 #define   toupper   __METALCALL(___TOUPPER   , 62)
 #define   vsnprintf __METALCALL(___VSNPRINTF , 63)
 #define   vsprintf  __METALCALL(___VSPRINTF  , 64)
 #define   vsscanf   __METALCALL(___VSSCANF   , 65)
 #define   strtod    __METALCALL(___STRTOD    , 66)
 #define   strtof    __METALCALL(___STRTOF    , 67)
 #define   strtold   __METALCALL(___STRTOLD   , 68)
 /*------------------------------------------------------------------*/
 /*          The order of these entries must be maintained.          */
 /*               Add new functions above this point!                */
 /*------------------------------------------------------------------*/
 
 #define __METAL_TYPEDEF typedef
 
 #else  /* __METAL_STATIC */
 
   #define __METAL_TYPEDEF
   #define ___CINIT  __cinit
   #define ___CTERM  __cterm
 
   #pragma map(__cinit, "\174\174CINIT")
   #pragma map(__cterm, "\174\174CTERM")
 
 #endif /* __METAL_STATIC */
 
   __METAL_TYPEDEF __csysenv_t ___CINIT(struct __csysenv_s *);
   __METAL_TYPEDEF void     ___CTERM(__csysenv_t);
 
                   #pragma checkout(resume)
                   ??=endif  /* __metal_h */
