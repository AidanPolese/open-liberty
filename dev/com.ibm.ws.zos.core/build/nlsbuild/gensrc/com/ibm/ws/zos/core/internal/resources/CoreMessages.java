/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:35 EST 2017
 */

package com.ibm.ws.zos.core.internal.resources;

public class CoreMessages extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "ANGEL_NAME_TOO_LONG", "CWWKB0119E: The angel name specified in bootstrap.properties is greater than 54 characters." },
      { "ANGEL_NAME_UNSUPPORTED_CHARACTER", "CWWKB0120E: The angel name specified in bootstrap.properties contains the unsuppored character {0} at position {1}." },
      { "ANGEL_NOT_AVAILABLE", "CWWKB0101I: The angel process is not available.  No authorized services will be loaded.  The reason code is {0}." },
      { "ANGEL_NOT_AVAILABLE_NAME", "CWWKB0117W: The {0} angel process is not available. No authorized services will be loaded. The reason code is {1}." },
      { "AUTHORIZED_SERVICE_AVAILABLE", "CWWKB0103I: Authorized service group {0} is available." },
      { "AUTHORIZED_SERVICE_NOT_AVAILABLE", "CWWKB0104I: Authorized service group {0} is not available." },
      { "LIBRARY_DOES_NOT_EXIST", "CWWKB0106E: The z/OS native library {0} does not exist in the file system." },
      { "NOT_REGISTERED_WITH_REQUIRED_ANGEL", "CWWKB0116I: This server is not registered with an angel process even though it is configured to require registration with an angel process. This server is attempting to stop." },
      { "PRODUCT_DEREGISTRATION_SUCCESSFUL", "CWWKB0111I: {0} product {1} version {2} successfully deregistered from z/OS." },
      { "PRODUCT_DEREGISTRATION_UNSUCCESSFUL", "CWWKB0114E: {0} product {1} version {2} failed to deregister from z/OS. Return code = {3}." },
      { "PRODUCT_REGISTRATION_FAILED_BAD_PARM", "CWWKB0107E: {0} product {1} version {2} failed to register with z/OS due to problems translating required strings to EBCDIC." },
      { "PRODUCT_REGISTRATION_SUCCESSFUL", "CWWKB0108I: {0} product {1} version {2} successfully registered with z/OS." },
      { "PRODUCT_REGISTRATION_SUMMARY_AUTHORIZED", "CWWKB0112I: The number of successfully registered products with z/OS is {0}. The server will attempt to deregister these products from z/OS during server shutdown." },
      { "PRODUCT_REGISTRATION_SUMMARY_NOT_AUTHORIZED", "CWWKB0113I: The number of successfully registered products with z/OS is {0}. These products will deregister from z/OS when the address space terminates." },
      { "PRODUCT_REGISTRATION_UNSUCCESSFUL", "CWWKB0109E: {0} product {1} version {2} failed to register with z/OS, return code = {3}." },
      { "SERVER_CURRENT_UMASK", "CWWKB0121I: The server process UMASK value is set to {0}." },
      { "SERVER_NOT_AUTHORIZED_TO_CONNECT_TO_ANGEL", "CWWKB0102I: This server is not authorized to connect to the angel process.  No authorized services will be loaded." },
      { "SERVER_NOT_AUTHORIZED_TO_CONNECT_TO_ANGEL_NAME", "CWWKB0118W: This server is not authorized to connect to the {0} angel process. No authorized services will be loaded." },
      { "SERVER_SAFM_NOT_APF_AUTHORIZED", "CWWKB0110I: Module bbgzsafm is not APF authorized. No authorized services will be available." },
      { "SERVER_SAFM_NOT_SAF_AUTHORIZED", "CWWKB0115I: This server is not authorized to load module bbgzsafm.  No authorized services will be loaded." },
      { "UNABLE_TO_LOAD_UNAUTHORIZED_BPX4LOD", "CWWKB0105E: Unable to load the z/OS native code library {0}.  BPX4LOD failed, rv = {1} rc = {2} rsn = {3}." }
   };
}
