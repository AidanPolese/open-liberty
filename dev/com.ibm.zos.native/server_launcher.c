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

//---------------------------------------------------------------------
// TODO: Tune the pools in performance
//---------------------------------------------------------------------
#pragma runopts(ENVAR("_CEE_ENVFILE_S=DD:STDENV","_EDC_ADD_ERRNO2=1","_EDC_PTHREAD_YIELD=-2"))
#pragma runopts(POSIX(ON),EXECOPS,TRAP(ON,NOSPIE))
#pragma runopts(HEAPPOOLS(ON))
#pragma runopts(HEAPPOOLS64(ON))

#ifndef _UNIX03_SOURCE
#define _UNIX03_SOURCE
#endif

#ifndef _XOPEN_SOURCE_EXTENDED
#define _XOPEN_SOURCE_EXTENDED 1
#endif

#include <ctype.h>
#include <dlfcn.h>
#include <errno.h>
#include <fnmatch.h>
#include <jni.h>
#include <libgen.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <unistd.h>
#include "include/server_nls_messages.h"
#include "include/gen/native_messages.h"
//
// The server_nls_messages.c code is included here as this same code is
// used within libzNativeServices.so which is not accessible here. I
// decided that rather than copy the code here directly to use an include
// so that the catalog code and any future changes to it would be common.
//
#include "server_nls_messages.c"

#ifndef PATH_MAX
#define PATH_MAX _POSIX_PATH_MAX
#endif

//---------------------------------------------------------------------
// Launcher:     $WLP_INSTALL_DIR/lib/native/zos/s390x/bbgzsrv
// LauncherHome: $WLP_INSTALL_DIR/lib/native/zos/s390x
// Server:       $WLP_USER_DIR/servers/${serverName}
//---------------------------------------------------------------------
#define INSTALL_RELATIVE "../../../.."
#define NATIVE_DIR "lib/native/zos/s390x"
#define NATIVE_NLSDIR "lib/native/zos/s390x/nls/%N.cat"

#define DEFAULT_ENV_FILE_NAME "default.env"
#define SERVER_ENV_FILE_NAME "server.env"
#define JAVA_ENV_FILE_NAME "java.env"
#define JAVA_OPTIONS_FILE_NAME "jvm.options"
#define SERVER_XML_FILE_NAME "server.xml"
#define DEFAULT_LOG_FILE "console.log"

#define LAUNCH_JAR "ws-launch.jar"
#define BOOTSTRAP_AGENT "bootstrap-agent.jar"
#define MAIN_CLASS "com/ibm/ws/kernel/boot/cmdline/EnvCheck"

//---------------------------------------------------------------------
// Mode value for directory creation (755)
//---------------------------------------------------------------------
const int mode = S_IRWXU|S_IRGRP|S_IXGRP|S_IXOTH|S_IROTH;

//---------------------------------------------------------------------
// Function prototype for CreateJavaVM
//---------------------------------------------------------------------
typedef jint (JNICALL* CreateJavaVM_t)(JavaVM** pvm, void** env, void* args);

//---------------------------------------------------------------------
// Globals to manage VM options
//---------------------------------------------------------------------
static JavaVMOption*     jvmOptions = NULL;
static jint              allocatedOptions = 0;
static jint              optionCount = 0;

//---------------------------------------------------------------------
// Module scoped methods
//---------------------------------------------------------------------

/**
 * Dump usage information.
 */
static void printUsage();

/**
 * Update the process's path variable to include the specified directory.
 *
 * @param name the name of the process's path variable to update
 * @param directory the directory to path variable
 * @param prepend put the directory at the head of the variables path
 *
 * @returns 0 on success
 */
static int updateEnvPath(const char* name, const char* directory, unsigned char prepend);

/**
 * Get the real path to the liberty install directory based on the
 * path to the launcher.  This is the equivalent of WLP_INSTALL_DIR.
 *
 * @param launcherPath the launcher path from argv[0]
 * @param destInstallDir buffer of at least PATH_MAX bytes where the
 *        real install directory path will be placed
 *
 * @return 0 on success, < 0 on error
 */
static int getInstallDir(const char* launcherPath, char* destInstallDir);

/**
 * Get the real path to the liberty user directory based on the install
 * path, the environment, and @c WLPUDIR path DD.  This is the equivalent
 * of WLP_USER_DIR.
 *
 * @param wlpInstallDir the real path to the liberty install
 * @param destUserDir buffer of at least PATH_MAX bytes where the user
 *        directory path will be placed
 *
 * @return 0 on success, < 0 on error
 */
static int getUserDir(const char* wlpInstallDir, char* destUserDir);

/**
 * Calculate the path to the liberty server directory based on the liberty
 * user directory and server name.
 *
 * @param wlpUserDir the path to the liberty user directory
 * @param serverName the name of the server
 * @param destServerDir buffer of at least PATH_MAX bytes where the server
 *        directory path will be placed
 *
 * @return 0 on success, < 0 if the server directory or configuration
 *        document doesn't exist
 */
static int getServerDir(const char* wlpUserDir, const char* serverName, char* destServerDir);

/**
 * Calculate the path to the liberty output directory.  If the output
 * directory environment variable WLP_OUTPUT_DIR is not set, we will default
 * to the servers directory.
 *
 * @param userDir the path to the liberty user directory
 * @param destOutputDir buffer of at least PATH_MAX bytes where the output
 *        directory path will be placed
 *
 * @return 0 on success, < 0 if the output directory does not exist
 */
static int createOutputDir(const char* userDir, char* destOutputDir);

/**
 * Calculate the path to the liberty server output directory.  This path
 * will be $WLP_OUTPUT_DIR/{serverName}.
 *
 * @param outputDir the path to the liberty user directory
 * @param serverName the name of the server
 * @param destServerOutputDir buffer of at least PATH_MAX bytes where the
 *        server output directory path will be placed
 *
 * @return 0 on success, < 0 if the output directory does not exist
 */
static int createServerOutputDir(const char* outputDir, const char* serverName, char* destServerOutputDir);

/**
 * Set X_PID_DIR and X_PID_FILE based on PID_DIR and PID_FILE environment variables.
 * If PID_DIR and PID_FILE are not set , use the default values.
 * Default PID_DIR = $WLP_OUTPUT_DIR/.pid
 * Default PID_FILE = $WLP_OUTPUT_DIR/.pid/servername.pid
 *
 * @param servername the name of the server
 * @param outputDir is the path to liberty output directory
 * @param destPIDOutputDir is the output PID_DIR directory
 * @param destPIDFile is the output PID_FILE directory
 *
 * @return 0 on success, < 0 if the PID_DIR or PID_FILE environment variable could not be set
 */
static int getPIDInfo(const char *servername, const char *destServerOutputDir, char *destPIDOutputDir, char *destPIDFile);

/**
 * If JVM_ARGS is set then pass those settings when we setup the JVM
 *
 * @return 0 on success, <0 if there was a problem setting the local arguments to pass to the JVM
 */
static int processJVM_ARGS();

/**
 * Parse the appropriate @c server.env files and use the contents to
 * populate the environment.
 *
 * @param wlpInstallDir the real path to the install directory
 * @param wlpServerDir the path to the server configuration directory
 *
 * @return 0 on success, < 0 on error
 */
static int processEnvironmentFiles(const char* wlpInstallDir, const char* wlpServerDir);

/**
 * Read the specified file as a @c server.env environment file.
 *
 * @param envFile the open file containing the environment entries
 *
 * @return 0 on success, < 0 on error
 */
static int processEnvironmentFile(FILE* envFile);

/**
 * Tokenize the environment file entry and set the variable into
 * the runtime environment.
 *
 * @param envFileEntry an environment file entry in EBCDIC
 *
 * @return 0 on success, < 0 on error
 */
static int setEnvironmentEntry(char* envFileEntry);

/**
 * Call @c setenv with the specified key and value.  If @c setenv
 * fails, issue a message.
 *
 * @param key the environment entry name in EBCDIC
 * @param value the environment entry value in EBCDIC
 *
 * @return 0 on success, < 0 on error
 */
static int setEnvironment(const char* key, const char* value);

/**
 * Read a line of text from the specified file into the provided buffer.  If
 * the line is longer than the buffer, the buffer will be null terminated and
 * the file position will remain unchanged.  New lines will not be returned
 * in the buffer.
 *
 * Shell style line comments that start with '#' and go to the end of the line
 * are removed.
 *
 * @param file the file to read from
 * @param buffer the buffer to store the text into
 * @param buflen the size of the buffer
 * @param isEbcdic non-0 if the file is in EBCDIC, 0 if the file is in ASCII
 *
 * @return 0 on success
 */
static int readLine(FILE* file, char* buffer, int buflen, unsigned char isEbcdic);

/**
 * Get the first character from the stream currently pointed to by @c file.  The
 * file position at entry will be preserved.
 *
 * @param file the stream to read a character from
 *
 * @return the character read from the stream or -1, -2, -3 on error
 */
static int getFirstCharacter(FILE* file);

/**
 * Determine JAVA_HOME by looking at the environment and default locations.
 *
 * @param wlpInstallDir the path to the liberty install directory
 * @param destJavaHome buffer of at least PATH_MAX bytes where the JAVA_HOME
 *        location path will be placed
 *
 * @return 0 on success, < 0 on error
 */
static int getJavaHome(const char* wlpInstallDir, char* destJavaHome);

/**
 * Add the JavaVMOptions data from the appropriate @c jvm.options file
 * into the global JavaVMOption array.
 *
 * @param wlpInstallDir the real path to the install directory
 * @param wlpServerDir the real path to the server directory
 */
static void processJavaVMOptions(const char* wlpInstallDir, const char* wlpServerDir);

/**
 * Read the JavaVMOptions data from the specified location and populate
 * the global JavaVMOption array.
 *
 * @param optionsFile the open file containing the JVM options
 */
static void processJavaVMOptionsFile(FILE* optionsFile);

/**
 * Check to see if file exists
 *
 * @param filepath
 * @return true if it exists
 */
static int file_exist(char *filename);

/**
 * Check if the specified character is valid as the first character
 * of an EBCDIC java options file.
 *
 * @param ch the code point of the first character in the file
 *
 * @return true if the first character of a file is valid for a
 *         java.options file in EBCDIC
 */
static unsigned char isValidFirstOptionCharEbcdic(int ch);

/**
 * Check if the specified character is valid as the first character
 * of an EBCDIC server environment file.
 *
 * @param ch the code point of the first character in the file
 *
 * @return true if the first character of a file is valid for a
 *         server.env file in EBCDIC
 */
static unsigned char isValidFirstEnvironmentCharEbcdic(int ch);

/**
 * Check if the specified character is valid as the first character
 * of an ASCII java options file.
 *
 * @param ch the code point of the first character in the file
 *
 * @return true if the first character of a file is valid for a
 *         java.options file in ASCII
 */
static unsigned char isValidFirstOptionCharAscii(int ch);

/**
 * Check if the specified character is valid as the first character
 * of an ASCII server environment file.
 *
 * @param ch the code point of the first character in the file
 *
 * @return true if the first character of a file is valid for a
 *         server.env file in ASCII
 */
static unsigned char isValidFirstEnvironmentCharAscii(int ch);

/**
 * Add an option to the global JavaVMOptions array.
 *
 * @param option the option string in EBCDIC
 * @param extra the "extra" data for the option array
 *
 * @return the current number of VM options after processing
 */
static int addJavaOption(char* option, void* extra);

/**
 * Expand the size of the global JavaVMOptions array.
 */
static int expandOptionsArray();

/**
 * Add the Liberty BCI agent to the runtime unless explicitly
 * told not to.
 *
 * @param wlpInstallDir the path to the install directory
 */
static void setupJavaAgent(const char* wlpInstallDir);

/**
 * Export a value for IBM_JAVA_OPTIONS that specifies our shared
 * class cache defaults.  If IBM_JAVA_OPTIONS is already set,
 * we won't override what's there.
 *
 * @param javaHome the real path to the java home directory
 * @param wlpOutputDir the path to the install directory
 */
static void setIbmClassCacheDefaults(const char* javaHome, const char* wlpOutputDir);

/**
 * Determine if the JVM supports the <tt>cacheDirPerm</tt> option on
 * <tt>-Xshareclasses</tt>.  Without this option, the shared class
 * cache can't be properly secured.
 *
 * @param javaHome the real path to java home
 *
 * @return 0 if <tt>cacheDirPerm</tt> is not a supported option
 */
static unsigned char isCacheDirPermSupported(const char* javaHome);

/**
 * Add a default file encoding if one was not previously set.
 */
static void setupDefaultFileEncoding();

/**
 * Invoke the Java main method.
 */
static int invokeMain(JNIEnv* env, int argc, char** argv, int scriptLaunch);

/**
 * Create a java.lang.String from an EBCDIC string.
 *
 * @param env the JNI environment for this thread
 * @param ebcdicString a null terminated string in EBCDIC
 *
 * @return a java.lang.String with the text from ebcdicString
 */
static jstring createJavaString(JNIEnv* env, const char* ebcdicString);

/**
 * Update the process's environment prior to JVM start.
 */
static void updateEnvironment();

/**
 * Update the process's umask if we're running without one.
 */
static void updateUmask();

/**
 * Set the process's current working directory to the specified
 * directory.
 *
 * @param dirPath the directory to change into
 *
 * @return 0 on success, < 0 on error
 */
static int setWorkingDirectory(const char* dirPath);

//---------------------------------------------------------------------
//---------------------------------------------------------------------
//
// The main method for the launcher.
//
//---------------------------------------------------------------------
//---------------------------------------------------------------------
int
main(int argc, char** argv) {
    char*             serverArgument = NULL;
    int               scriptLaunch = 0;
    char              nativeLibraryPath[PATH_MAX + 1] = "";
    void*             jvmLibraryHandle = NULL;

    // Path locations
    char              installDir[PATH_MAX + 1] = "";
    char              userDir[PATH_MAX + 1] = "";
    char              serverDir[PATH_MAX + 1] = "";
    char              outputDir[PATH_MAX + 1] = "";
    char              serverOutputDir[PATH_MAX + 1] = "";
    char              PIDOutputDir[PATH_MAX + 1] = "";
    char              PIDOutputFile[PATH_MAX + 1] = "";
    char              logOutputDir[PATH_MAX + 1] = "";
    char*             env_ptr;

    CreateJavaVM_t    CreateJavaVM = NULL;
    JavaVMInitArgs    jvmInitArgs;
    JavaVM*           jvm = NULL;
    JNIEnv*           env = NULL;
    jint              createReturnCode = 0;
    FILE*             fp_stdout;
    FILE*             fp_stderr;

    // Calculate WLP_INSTALL_DIR
    if (getInstallDir(argv[0], installDir) != 0) {
        //Message cannot be translated without determining WLP_INSTALL_DIR
        fprintf(stderr, "Unable to determine WLP_INSTALL_DIR\n");
        return -128;
    }

    // Prepend $WLP_INSTALL_DIR/lib/native/zos/s390x/nls to $NLSPATH
    char nlsCatalogPath[PATH_MAX + 1] = "";
    snprintf(nlsCatalogPath, sizeof(nlsCatalogPath), "%s/%s", installDir, NATIVE_NLSDIR);
    if (updateEnvPath("NLSPATH", nlsCatalogPath, 1)) {
        //Message cannot be translated without calculating NLSPATH
        fprintf(stderr, "Failed to prepend NLS path to NLSPATH\n");
        return -127;
    }

    // Calculate WLP_USER_DIR
    if (getUserDir(installDir, userDir) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_03, "Unable to determine WLP_USER_DIR\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        return -126;
    }

    // The command line option model for the launchers is nice and simple
    // and follows the GNU long option style where arguments start with a
    // double dash and arguments that take options use an equals sign to
    // associate the option with the argument.  Barring future changes,
    // the server name or location is the only argument that will not start
    // with a double dash.
    for (int i = 1; i < argc; i++) {
        // Skip arguments that start with '--'
        if (strncmp("--", argv[i], 2) == 0) {
            continue;
        }
        //If the argument is '-scriptLaunch' set the scriptLaunch flag
        //This means that the native proc has been launched from the server script
        //and the command port must be left enabled.
        if (strncmp("-scriptLaunch", argv[i], 14) == 0){
            scriptLaunch = 1;
            continue;
        }
        serverArgument = argv[i];
    }

    // No server name specified, use defaultServer
    if (serverArgument == NULL) {
        serverArgument = "defaultServer";
    }

    // Calculate SERVER_CONFIG_DIR
    if (getServerDir(userDir, serverArgument, serverDir) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_04, "Unable to determine SERVER_CONFIG_DIR\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        return -125;
    }

    // Read the install and server level server.env files
    processEnvironmentFiles(installDir, serverDir);

    // Make sure we don't run with a 0 umask accidentally
    updateUmask();

    // Update the environment: heavy weight threads, etc.
    updateEnvironment();

    // Determine the output directory
    // checks for WLP_OUTPUT_DIR
    if (createOutputDir(userDir, outputDir) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_08, "Unable to determine output directory\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        return -121;
    }

    // Determine the server output directory
    // ${WLP_OUTPUT_DIR}/${SERVER_NAME}
    if (createServerOutputDir(outputDir, serverArgument, serverOutputDir) != 0) {
        return -120;
    }

    // Determine what the LOG_FILE and LOG_DIR will be and then
    // redirect stdout and stderr

    env_ptr = getenv("LOG_DIR");

    if (env_ptr != NULL)  {
        snprintf(logOutputDir, sizeof(logOutputDir), "%s", env_ptr);
        //set X_LOG_DIR
        setEnvironment("X_LOG_DIR", logOutputDir);

        env_ptr = getenv("LOG_FILE");
        if (env_ptr == NULL) {
            snprintf(logOutputDir, sizeof(logOutputDir), "%s/%s", logOutputDir, DEFAULT_LOG_FILE);
            //set X_LOG_FILE
            setEnvironment("X_LOG_FILE", DEFAULT_LOG_FILE);
        }
        else {
            snprintf(logOutputDir, sizeof(logOutputDir), "%s/%s", logOutputDir, env_ptr);
            //set X_LOG_FILE
            setEnvironment("X_LOG_FILE", env_ptr);
        }
        fp_stdout = freopen(logOutputDir,"w+",stdout);
        if (fp_stdout == NULL) {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_42, "Unable to redirect stdout to LOG_DIR");
            perror(msg);
            return -8;
        }

        fp_stderr = freopen(logOutputDir,"w+",stderr);
        if (fp_stderr == NULL) {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_43, "Unable to redirect stderr to LOG_DIR");
            perror(msg);
            return -9;
        }
    } else {
        // set X_LOG_DIR and X_LOG_FILE
        snprintf(logOutputDir, sizeof(logOutputDir), "%s/logs", serverOutputDir);
        setEnvironment("X_LOG_DIR", logOutputDir);
        env_ptr = getenv("LOG_FILE");
        if (env_ptr == NULL) {
            setEnvironment("X_LOG_FILE", DEFAULT_LOG_FILE);
        } else {
            setEnvironment("X_LOG_FILE", env_ptr);
        }
    }
    //unset LOG_DIR and LOG_FILE
    setEnvironment("LOG_FILE",NULL);
    setEnvironment("LOG_DIR",NULL);

    // Resolve the location of JAVA_HOME.
    char javaHome[PATH_MAX + 1];
    if (getJavaHome(installDir, javaHome) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_05, "Failed to resolve JAVA_HOME\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        return -124;
    }

    // Prepend $JAVA_HOME/bin/classic to $LIBPATH (because of a Java weirdness)
    char jvmLibraryPath[PATH_MAX + 1] = "";
    snprintf(jvmLibraryPath, sizeof(jvmLibraryPath), "%s/bin/classic", javaHome);
    if (updateEnvPath("LIBPATH", jvmLibraryPath, 1)) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_06, "Failed to prepend JVM library path to LIBPATH\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        return -123;
    }

    // Prepend $WLP_INSTALL_DIR/lib/native/zos/s390x to $LIBPATH
    snprintf(nativeLibraryPath, sizeof(nativeLibraryPath), "%s/lib/native/zos/s390x", installDir);
    if (updateEnvPath("LIBPATH", nativeLibraryPath, 1)) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_07, "Failed to prepend WebSphere library path to LIBPATH\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        return -122;
    };

    // Setup PID_DIR and PID_FILE if they are not already set
    if (getPIDInfo(serverArgument,outputDir,PIDOutputDir, PIDOutputFile) !=0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_44,"Failed to set the server PID directory or PID file\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        return -116;
    }

    // Change the working directory to the server output directory
    if (setWorkingDirectory(serverOutputDir)) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_09, "Failed to change working directory to %s\n");
        fprintf(stderr, msg, serverOutputDir);
        fprintf(stderr, "\n");
        return -119;
    }

    // Setup the shared class cache defaults
    setIbmClassCacheDefaults(javaHome, outputDir);

    // Read the appropriate JVM options file
    processJavaVMOptions(installDir, serverDir);

    // If JVM_ARGS is set then add those to the options passed to the JVM
    if (processJVM_ARGS()) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_45,"When processing JVM_ARGS an unexpected EOF was encountered while processing `\"'");
        fprintf(stderr,msg);
        fprintf(stderr,"\n");
        return -115;
    }

    // Setup the default file encoding if one wasn't specified
    setupDefaultFileEncoding();

    // Setup the bootstrap agent
    setupJavaAgent(installDir);
    
    // If we did not launch from the server script, set an internal JVM property
    //to identify that this server was started from the native launcher
    if(scriptLaunch == 0){
        addJavaOption("-Dcom.ibm.ws.kernel.default.command.port.disabled=true", NULL);
    }

    // The invocation interface doesn't support the -jar command line option
    // so we emulate the support by forcing the classpath to be exactly the
    // single ws-launch jar file.  With that classpath in place, we can then
    // drive the class called out by the Main-Class attribute in the manifest.

    // Add -classpath:.../ws-launch.jar
    char classpath[PATH_MAX + strlen("-Djava.class.path=")];
    snprintf(classpath, PATH_MAX + strlen("-Djava.class.path="), "-Djava.class.path=%s/%s/%s", installDir, "lib", LAUNCH_JAR);
    addJavaOption(classpath, NULL);

    // Build the path to the JVM DLL and load it.  This will fail if the
    // DLL is not for a 64-bit JVM.
    char jvmPath[PATH_MAX + 1] = "";
    snprintf(jvmPath, sizeof(jvmPath), "%s/bin/classic/libjvm.so", javaHome);
    jvmLibraryHandle = dlopen(jvmPath, RTLD_LAZY);
    if (jvmLibraryHandle == NULL) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_10, "Failed to open %s: %s\n");
        fprintf(stderr, msg, jvmPath, dlerror());
        fprintf(stderr, "\n");
        return -118;
    }

    // Resolve the JNI_CreateJavaVM function in the JVM DLL.
    CreateJavaVM = (CreateJavaVM_t) dlsym(jvmLibraryHandle, "JNI_CreateJavaVM");
    if (CreateJavaVM == NULL) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_11, "Failed to resolve JNI_CreateJavaVM: %s\n");
        fprintf(stderr, msg, dlerror());
        fprintf(stderr, "\n");
        return -117;
    }

    // Request a JDK 1.6 compatible JNI interface
    jvmInitArgs.version = JNI_VERSION_1_6;
    jvmInitArgs.nOptions = optionCount;
    jvmInitArgs.options = jvmOptions;
    jvmInitArgs.ignoreUnrecognized = JNI_FALSE;

    createReturnCode = CreateJavaVM(&jvm, (void**) &env, &jvmInitArgs);
    if (createReturnCode != JNI_OK) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_12, "Failed to create JavaVM: %s\n");
        char buffer[20];
        sprintf(buffer, "%d", createReturnCode);
        fprintf(stderr, msg, buffer);
        fprintf(stderr, "\n");
        return -6;
    }

    // Export the WLP variables so the kernel can see the values
    setenv("WLP_USER_DIR", userDir, 1);

    // Call into the Liberty main class
    if (invokeMain(env, argc, argv, scriptLaunch) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_13, "Failed to invoke main class\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        return -7;
    }

}

//---------------------------------------------------------------------
// Update the path for the specified process environment variable
//---------------------------------------------------------------------
static int
updateEnvPath(const char* varName, const char* directory, unsigned char prepend) {
    int returnCode = 0;

    // Get the current varName's value
    char* oldPath = getenv(varName);

    // Calculate the size of the varName path
    unsigned long newPathLength = 0;
    if (oldPath != NULL) {
        newPathLength = strlen(directory) + strlen(":") + strlen(oldPath) + 1;
    } else {
        newPathLength = strlen(directory) + 1;
    }

    // Allocate the storage for the varName entry
    char* newPath = alloca(newPathLength);
    if (newPath == NULL) {
        return -1;
    }

    // Build the new varName entry and set it
    if (oldPath == NULL) {
        strcpy(newPath, directory);
    } else if (prepend) {
        sprintf(newPath, "%s:%s", directory, oldPath);
    } else {
        sprintf(newPath, "%s:%s", oldPath, directory);
    }
    returnCode = setenv(varName, newPath, 1);

    return returnCode;
}

static int
getPIDInfo(const char* serverName, const char* dirPath, char* destPIDOutputDir, char* destPIDFile ) {

    int returnCode = 0;
    char tempPIDFilePath[PATH_MAX + 1];

    char* PIDFileEnv = getenv("PID_FILE");
    char* PIDDirEnv = getenv("PID_DIR");

    // Put PID_DIR value into PIDOutputDir and PID_FILE into PIDOutputFile
    // Initialize the PID_DIR  and PID_FILE values if they aren't already set
    if (PIDFileEnv == NULL) {
        if (PIDDirEnv == NULL) {
            snprintf(destPIDOutputDir,PATH_MAX,"%s/.pid",dirPath);
            // make the .pid directory if it doesnot exist
            if (createAllDirectories(destPIDOutputDir) !=0 ){
                perror("PID DIR could not be created");
                return -1;
            }

        } else {
            snprintf(destPIDOutputDir,PATH_MAX,PIDDirEnv);
        }
        snprintf(destPIDFile,PATH_MAX,"%s/%s.pid",destPIDOutputDir,serverName);
    } else {
        // Calculate the real path of the PID_FILE
        if (realpath(PIDFileEnv, tempPIDFilePath) == NULL) {
            //PID_FILE does not point to a valid directory
            perror("Real path of PID could not be resolved");
            return -1;
        }
        snprintf(destPIDFile, PATH_MAX, PIDFileEnv);
        snprintf(destPIDOutputDir, PATH_MAX, "%s", dirname(PIDFileEnv));
    }

    // set X_PID_DIR and X_PID_FILE
    setEnvironment("X_PID_DIR", destPIDOutputDir);
    setEnvironment("X_PID_FILE", destPIDFile);

    // get the PID of the running process and set into the PID_FILE
    char pidvalue[25];
    FILE* pidFile = NULL;

    int pid = getpid();
    sprintf(pidvalue,"%d" , pid );
    pidFile = fopen(destPIDFile, "wt");
    int num = fputs(pidvalue,pidFile);
    fclose(pidFile);

    //unset PID_DIR and PID_FILE
    setEnvironment("PID_FILE",NULL);
    setEnvironment("PID_DIR",NULL);

    return returnCode;
}


//---------------------------------------------------------------------
// Process JVM_ARGS
//---------------------------------------------------------------------
static int processJVM_ARGS() {
// Define a macro to copy a character from one string to another and advance each
// strings character pointer
#define copychar(a,b) \
    *a=*b;            \
    a++;              \
    b++;

#define aspace(a)     \
    (a==' ' || a=='\t' || a=='\n')

    char* jvm_arg_ptr = getenv("JVM_ARGS");
    char* ptr;
    char* arg_ptr;
    char* arg_list;
    char* token;

    if (jvm_arg_ptr == NULL) {
        return 0;
    }

    // Allocate space for a string we can copy the JVM_ARGS to after we
    // take care of any characters with special meaning to them
    arg_list = alloca(strlen(jvm_arg_ptr)+1);
    arg_ptr = arg_list;
    *arg_ptr = '\0';

    // Parse the JVM_ARGS string which is a list of one or more arguments
    // separated by spaces.  Process the \ (escape) character
    // and preserve characters enclosed in " " and ' '.
    //
    // First remove any leading spaces, tabs, newlines
    ptr = jvm_arg_ptr;
    while (aspace(*ptr)) {
        ptr++;
    }

    token = arg_list;
    // Copy data from ptr to arg_ptr, consume extra spaces, copy all characters
    // contained within single or double quotes.  For consistency honor the
    // escape character as well
    while (*ptr != '\0') {
        if (*ptr == '"') {
            ptr++;
            while ((*ptr != '\0') && (*ptr != '"') ) {
                if (*ptr == '\\') {
                   char* temp = ptr;
                   temp++;
                   if (*temp != '\0') {
                       ptr=temp;
                       copychar(arg_ptr,ptr);
                       continue;
                   }
                }
                copychar(arg_ptr,ptr);
            }
            if (*ptr == '"') {
                ptr++;
            } else {
               return -1;
            }
        } else if (*ptr == '\'') {
            ptr++;
            while ((*ptr != '\0') && (*ptr != '\'') ) {
                copychar(arg_ptr,ptr);
            }
            if (*ptr == '\'') {
                ptr++;
            } else {
                return -1;
            }
        } else if (aspace(*ptr)) {
            *arg_ptr = '\0';
            arg_ptr++;
            ptr++;
            addJavaOption(token,NULL);
            token = arg_ptr;
            *arg_ptr = '\0';
            while (aspace(*ptr) ) {
                ptr++;
            }
        } else if (*ptr == '\\') {
            ptr++;
            if (*ptr != '\0') {
                copychar(arg_ptr,ptr);
            }
        } else {
            copychar(arg_ptr,ptr);
        }
    }
    copychar(arg_ptr,ptr);
    if (*token != '\0') {
        addJavaOption(token,NULL);
    }
return 0;
}

//---------------------------------------------------------------------
// Calculate WLP_INSTALL_DIR
//---------------------------------------------------------------------
static int
getInstallDir(const char* launcherPath, char* destInstallDir) {
    // Calculate the real path of the launcher
    char launcherRealPath[PATH_MAX + 1];
    char tempPath[PATH_MAX + 1];

    if (realpath(launcherPath, tempPath) == NULL) {
        //Message cannot be translated without determining WLP_INSTALL_DIR
        perror("Real path of server launcher could not be resolved");
        return -1;
    }
    snprintf(launcherRealPath, sizeof(launcherRealPath), launcherPath);

    // Get the directory of the launcher
    char* launcherDir = dirname(launcherRealPath);
    if (launcherDir == NULL) {
        //Message cannot be translated without determining WLP_INSTALL_DIR
        perror("Server launcher directory could not be resolved");
        return -2;
    }

    // Calculate the install root from the launcher home
    char installPath[strlen(launcherDir) + strlen(INSTALL_RELATIVE) + 2];
    snprintf(installPath, sizeof(installPath), "%s/%s", launcherDir, INSTALL_RELATIVE);
    if (realpath(installPath, tempPath) == NULL) {
        //Message cannot be translated without determining WLP_INSTALL_DIR
        perror("Real path of install directory could not be resolved");
        return -3;
    }
    snprintf(destInstallDir, PATH_MAX, installPath);

    struct stat info;
    char nativePath[PATH_MAX + 1];
    snprintf(nativePath, sizeof(nativePath), "%s/%s", installPath, NATIVE_DIR);
    if (stat(nativePath, &info) != 0) {
        //Message cannot be translated without determining WLP_INSTALL_DIR
        fprintf(stderr, "Invalid install tree format\n");
        return -2;
    }

    return 0;
}

//---------------------------------------------------------------------
// Calculate WLP_USER_DIR
//---------------------------------------------------------------------
static int
getUserDir(const char* wlpInstallDir, char* destUserDir) {
    FILE* jclUserDir = NULL;
    char tempUserPath[PATH_MAX + 1];

    // Start by looking for the user directory DD
    jclUserDir = fopen("DD:WLPUDIR", "rt");
    if (jclUserDir != NULL) {
        fldata_t fileInfo;
        if (fldata(jclUserDir, NULL, &fileInfo) != 0) {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_14, "Path to user directory could not be extracted from the DD\n");
            fprintf(stderr, msg);
            fprintf(stderr, "\n");
            fclose(jclUserDir);
            return -1;
        }

        // Make sure that WLPUDIR points to a good location
        if (realpath(fileInfo.__dsname, tempUserPath) == NULL) {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_15, "Real path of user directory could not be resolved from JCL");
            perror(msg);
            fclose(jclUserDir);
            return -2;
        }
        snprintf(destUserDir, PATH_MAX, fileInfo.__dsname);
        fclose(jclUserDir);

        // Set WLP_USER_DIR from DD:WLPUDIR
        if (setEnvironment("WLP_USER_DIR", destUserDir)) {
            return -3;
        }
        return 0;
    }

    // Check for the WLP_USER_DIR, then WLP_DEFAULT_USER_DIR environment variables
    char* wlpUserDirEnv = getenv("WLP_USER_DIR");
    if (wlpUserDirEnv == NULL) {
        wlpUserDirEnv = getenv("WLP_DEFAULT_USER_DIR");
        if (wlpUserDirEnv && setEnvironment("WLP_USER_DIR", wlpUserDirEnv)) {
            return -4;
        }
    }

    // Verify the user directory location
    if (wlpUserDirEnv != NULL) {
        if (realpath(wlpUserDirEnv, tempUserPath) == NULL) {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_16, "Real path of user directory could be resolved from environment");
            perror(msg);
            return -5;
        }
        snprintf(destUserDir, PATH_MAX, wlpUserDirEnv);
        return 0;
    }

    // Default to the 'usr' tree under the install
    snprintf(destUserDir, PATH_MAX, "%s/%s", wlpInstallDir, "usr");
    return 0;
}

//---------------------------------------------------------------------
// Calculate the server config directory
//---------------------------------------------------------------------
static int
getServerDir(const char* wlpUserDir, const char* serverName, char* destServerDir) {
    // Build the path name
    snprintf(destServerDir, PATH_MAX, "%s/servers/%s", wlpUserDir, serverName);

    // Verify that the directory exists
    struct stat info;
    if (stat(destServerDir, &info) != 0) {
        // TODO: Do we care about --create?
        if (errno == ENOENT || errno == ENOTDIR) {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_17, "Server configuration directory \"%s\" does not exist\n");
            fprintf(stderr, msg, destServerDir);
            fprintf(stderr, "\n");
        } else {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_18, "Unable to stat() the server configuration directory");
            perror(msg);
        }
        return -1;
    }

    // Verify that the server.xml exists
    char serverXmlPath[PATH_MAX + 1];
    snprintf(serverXmlPath, PATH_MAX, "%s/%s", destServerDir, SERVER_XML_FILE_NAME);
    if (stat(serverXmlPath, &info) != 0) {
        if (errno == ENOENT || errno == ENOTDIR) {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_19, "Server configuration \"%s\" does not exist");
            fprintf(stderr, msg, serverXmlPath);
            fprintf(stderr, "\n");
        } else {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_20, "Failed to stat ");
            int length = strlen(msg) + strlen(SERVER_XML_FILE_NAME);
            char buffer[length +2];
            sprintf(buffer, "%s:%s", msg, SERVER_XML_FILE_NAME);
            perror(buffer);
        }
        return -2;
    }

    return 0;
}

//---------------------------------------------------------------------
// Create Directory Path with intermediate directories
//---------------------------------------------------------------------
static int createAllDirectories(const char* dirPath) {
    char dir[PATH_MAX];
    char *ptr = NULL;
    size_t len;
    snprintf(dir, PATH_MAX, "%s", dirPath);
    len = strlen(dir);
    if ((len != 0) && (dir[len - 1] == '/')) {
        dir[len - 1] = '\0';
    }
    if ((mkdir(dir, mode) == 0) || (errno == EEXIST)) {
        return 0;
    }
    // recursive intermediate directories
    for (ptr = dir; *ptr; ptr++) {
        if (*ptr == '/') {
            *ptr = '\0';
            if (mkdir(dir, mode) != 0) {
                if (!(errno == EEXIST || errno == ENOENT)) {
                    return -1;
                }
            }
            *ptr = '/';
        }
    }
    // the last directory in the path
    if (mkdir(dir, mode) != 0) {
        if (!(errno == EEXIST || errno == ENOENT)) {
            return -1;
        }
    }

    return 0;
}

//---------------------------------------------------------------------
// Determine the output directory
//---------------------------------------------------------------------
static int
createOutputDir(const char* userDir, char* destOutputDir) {
    destOutputDir[0] = '\0';
    char tempOutputPath[PATH_MAX + 1];

    // Check WLP_OUTPUT_DIR then WLP_DEFAULT_OUTPUT_DIR
    const char* wlpOutputDir = getenv("WLP_OUTPUT_DIR");
    if (wlpOutputDir == NULL) {
        wlpOutputDir = getenv("WLP_DEFAULT_OUTPUT_DIR");
        if (wlpOutputDir && setEnvironment("WLP_OUTPUT_DIR", wlpOutputDir)) {
            return -1;
        }
    } else {
        // check if the specified output dir exists, else create it along with intermediate directories
        if (createAllDirectories(wlpOutputDir) != 0) {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_21, "Output directory could not be found or created");
            perror(msg);
            return -1;
        }
    }

    // If the environment variable wasn't specified, default to server
    char defaultOutputDir[strlen(userDir) + strlen("servers") + 2];
    if (wlpOutputDir == NULL) {
        snprintf(defaultOutputDir, sizeof(defaultOutputDir), "%s/%s", userDir, "servers");
        wlpOutputDir = defaultOutputDir;
    }

    // Get the real path to the output directory
    if (realpath(wlpOutputDir, tempOutputPath) == NULL) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_08, "Unable to determine output directory\n");
        perror(msg);
        return -2;
    }
    snprintf(destOutputDir, PATH_MAX, wlpOutputDir);
    return 0;
}

//---------------------------------------------------------------------
// Determine the output directory
//---------------------------------------------------------------------
static int
createServerOutputDir(const char* outputDir, const char* serverName, char* destServerOutputDir) {
    char tempServerOutputPath[PATH_MAX + 1];

    char serverOutputDir[strlen(outputDir) + strlen(serverName) + 2];
    snprintf(serverOutputDir, sizeof(serverOutputDir), "%s/%s", outputDir, serverName);
    // create the server output dir if it doesnt exist
    if (createAllDirectories(serverOutputDir) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_22, "Server output directory could not be found or created under WLP_OUTPUT_DIR");
        perror(msg);
        return -1;
    }

    // Get the real path to the output directory
    if (realpath(serverOutputDir, tempServerOutputPath) == NULL) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_08, "Unable to determine output directory\n");
        perror(msg);
        return -1;
    }
    snprintf(destServerOutputDir, sizeof(serverOutputDir), serverOutputDir);
    return 0;
}

//---------------------------------------------------------------------
// Read the server.env files and set them into the environment
//---------------------------------------------------------------------
static int
processEnvironmentFiles(const char* wlpInstallDir, const char* wlpServerDir) {
    char environmentPath[PATH_MAX + 1];
    FILE* environmentFile = NULL;
    int returnCode = 0;

    // Read the install defaults first.  If misused, this can override the
    // inherited environment
    // TODO: Filter to WLP_DEFAULT_* only?
    snprintf(environmentPath, sizeof(environmentPath), "%s/%s/%s", wlpInstallDir, "etc", DEFAULT_ENV_FILE_NAME);
    environmentFile = fopen(environmentPath, "rt");
    if (environmentFile != NULL) {
        if (processEnvironmentFile(environmentFile) != 0) {
            if (ferror(environmentFile)) {
                returnCode = -1;
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_23, "Error reading from %s: %s\n");
                char buffer[20];
                sprintf(buffer, "%lx", ferror(environmentFile));
                fprintf(stderr, msg, environmentPath, buffer);
                fprintf(stderr, "\n");
            }
        }
        fclose(environmentFile);
        environmentFile = NULL;
    }

    snprintf(environmentPath, sizeof(environmentPath), "%s/%s/%s", wlpInstallDir, "java", JAVA_ENV_FILE_NAME);
    environmentFile = fopen(environmentPath, "rt");
    if (environmentFile != NULL) {
        if (processEnvironmentFile(environmentFile) != 0) {
            if (ferror(environmentFile)) {
                returnCode = -1;
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_23, "Error reading from %s: %s\n");
                char buffer[20];
                sprintf(buffer, "%lx", ferror(environmentFile));
                fprintf(stderr, msg, environmentPath, buffer);
                fprintf(stderr, "\n");
            }
        }
        fclose(environmentFile);
        environmentFile = NULL;
    }

    // Try the install level next (overrides inherited environment and defaults)
    snprintf(environmentPath, sizeof(environmentPath), "%s/%s/%s", wlpInstallDir, "etc", SERVER_ENV_FILE_NAME);
    environmentFile = fopen(environmentPath, "rt");
    if (environmentFile != NULL) {
        if (processEnvironmentFile(environmentFile) != 0) {
            if (ferror(environmentFile)) {
                returnCode = -1;
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_23, "Error reading from %s: %s\n");
                char buffer[20];
                sprintf(buffer, "%lx", ferror(environmentFile));
                fprintf(stderr, msg, environmentPath, buffer);
                fprintf(stderr, "\n");
            }
        }
        fclose(environmentFile);
        environmentFile = NULL;
    }

    // Try the server level next (overrides inherited, default, and install environment)
    snprintf(environmentPath, sizeof(environmentPath), "%s/%s", wlpServerDir, SERVER_ENV_FILE_NAME);
    environmentFile = fopen(environmentPath, "rt");
    if (environmentFile != NULL) {
        if (processEnvironmentFile(environmentFile) != 0) {
            if (ferror(environmentFile)) {
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_23, "Error reading from %s: %s\n");
                char buffer[20];
                sprintf(buffer, "%lx", ferror(environmentFile));
                fprintf(stderr, msg, environmentPath, buffer);
                fprintf(stderr, "\n");
            }
        }
        fclose(environmentFile);
        environmentFile = NULL;
    }
}

//---------------------------------------------------------------------
// Read the server.env files and set them into the environment
//---------------------------------------------------------------------
static int
processEnvironmentFile(FILE* envFile) {
     // Save the starting position
    fpos_t startingPosition;
     if (fgetpos(envFile, &startingPosition) != 0) {
         char* msg = getTranslatedMessageById(SERVER_LAUNCHER_24, "Unable to get environment file position");
         perror(msg);
         return -1;
     }

    // There's a minor complication detecting the code page of the
    // environment file in ASCII because an ASCII 'm' is an EBCDIC '_'.
    // Both are valid environment variable characters.
    int firstChar;
    do {
        firstChar = fgetc(envFile);
    } while (firstChar == '_');

    // Restore the file position
    if (fsetpos(envFile, &startingPosition) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_25, "Unable to restore environment file position");
        perror(msg);
        return -2;
    }

    // Try to guess the code page of the file
    unsigned char ebcdicFile =
        isValidFirstEnvironmentCharEbcdic(firstChar) || !isValidFirstEnvironmentCharAscii(firstChar);

    // Read each line of the file (up to 4k/line)
    char line[4096];
    while (!feof(envFile)) {
        if (readLine(envFile, line, sizeof(line), ebcdicFile)) {
            return -1;
        }

        // Eat empty lines
        if (strlen(line) == 0) {
            continue;
        }

        if (setEnvironmentEntry(line) < 0) {
            return -2;
        }
    }

    return 0;
}

//---------------------------------------------------------------------
// Call setenv with the environment and value determined from the entry
//---------------------------------------------------------------------
static int
setEnvironmentEntry(char* envFileEntry) {
    // Find the equal sign and set it to '\0'
    char* separator = strchr(envFileEntry, '=');
    if (separator == NULL) {
        // not in the format of key value pair.
        // ignore the line and continue
        return 1;
    }

    // Replace the separator with a NUL
    *separator = '\0';

    // Setup key and value
    char* key = envFileEntry;
    char* value = separator + 1;

    // Try to remove wrapping quotes
    int valueLength = strlen(value);
    if (valueLength >=2 && *value == '"' && value[valueLength - 1] == '"') {
        value[valueLength - 1] = '\0';
        value++;
    }

    // Finally, call setenv
    if (setEnvironment(key, value)) {
        return -2;
    }

    return 0;
}

//---------------------------------------------------------------------
// Call setenv with the specified key and value
//---------------------------------------------------------------------
static int
setEnvironment(const char* key, const char* value) {
    // Call setenv
    if (setenv(key, value, 1)) {
        int buflen = strlen(key) + strlen(value) + 2;
        char buffer[buflen];
        snprintf(buffer, buflen, "%s=%s", key, value);
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_26, "Failed to set environment from \"%s\"\n");
        fprintf(stderr, msg, buffer);
        fprintf(stderr, "\n");
        return -1;
    }

    return 0;
}

//---------------------------------------------------------------------
// Read bytes from the specified stream into the array pointed to by
// buffer until the number of characters read is buflen - 1 bytes, a
// newline (ASCII or EBCDIC based on the assumeEbcic flag) is read,
// or end of file is encountered.
//
// New lines are not in the read line, comments are removed
//---------------------------------------------------------------------
static int
readLine(FILE* file, char* buffer, int buflen, unsigned char isEbcdic) {
    int i, ch;
    for (i = 0, ch = EOF, buffer[0] = '\0', buffer[buflen - 1] = '\0'; (i < buflen - 1) && (((ch = fgetc(file)) != EOF) || (buffer[i] = '\0')); i++) {
        buffer[i] = ch;
        if (isEbcdic && ch == '\n') {
            buffer[i] = '\0';
            break;
        }
#pragma convert("ISO8859-1")
        if (!isEbcdic && (ch == '\r' || ch == '\n')) {
#pragma convert(pop)
            buffer[i] = '\0';
            break;
        }
    }

    // Convert ASCII to EBCDIC
    if (!isEbcdic) {
        __atoe(buffer);
    }

    // Clean up white space at the end
    char* lineEnd = buffer + strlen(buffer);
    while (--lineEnd > buffer && isspace(*lineEnd)) {
        *lineEnd = '\0';
    }

    // Clean up white space at the front
    char* pos = &buffer[0];
    while (*pos != '\0' && isspace(*pos)) {
        pos++;
    }
    if (pos != &buffer[0]) {
        memmove(buffer, pos, strlen(pos) + 1);
    }

    // Eat comments if the first character is a #
    char* comment = strchr(buffer, '#');
    if (comment && (comment == &buffer[0])) {
        *comment = '\0';
    }

    return 0;
}

//---------------------------------------------------------------------
// Get the first character of a file stream
//---------------------------------------------------------------------
static int
getFirstCharacter(FILE* file) {
    int firstChar = -1;

    // Save the starting position
    fpos_t startingPosition;
    if (fgetpos(file, &startingPosition) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_27, "Unsuccessful fgetpos");
        perror(msg);
        return -2;
    }

    firstChar = fgetc(file);
    if (feof(file)) {
        firstChar = -1;
    }

    // Restore the file position
    if (fsetpos(file, &startingPosition) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_28, "Unsuccessful fsetpos");
        perror(msg);
        return -3;
    }

    return firstChar;
}


//---------------------------------------------------------------------
// Calculate JAVA_HOME
//---------------------------------------------------------------------
static int
getJavaHome(const char* wlpInstallDir, char* destJavaHome) {
    destJavaHome[0] = '\0';
    char tempJavaHome[PATH_MAX + 1];

    // Check JAVA_HOME, then WLP_DEFAULT_JAVA_HOME
    const char* envJavaHome = getenv("JAVA_HOME");
    if (envJavaHome == NULL) {
        envJavaHome = getenv("WLP_DEFAULT_JAVA_HOME");
        if (envJavaHome) {
            char substitutionPath[strlen(envJavaHome) + strlen(wlpInstallDir) + 1];
            substitutionPath[0] = '\0';
            char * stringPtr = strstr(envJavaHome, "@WLP_INSTALL_DIR@");
            if (stringPtr) {
                char * remainingDataPtr = stringPtr + strlen("@WLP_INSTALL_DIR@");
                // if @WLP_INSTALL_DIR@ in front
                if (stringPtr == envJavaHome) {
                    // Replace @WLP_INSTALL_DIR@
                    strncat(substitutionPath, wlpInstallDir, strlen(wlpInstallDir));
                    // Add on remaining part of envJavaHome
                    strncat(substitutionPath, remainingDataPtr, strlen(remainingDataPtr));
                    envJavaHome = substitutionPath;
                }
            }
        }
        if (envJavaHome && setEnvironment("JAVA_HOME", envJavaHome)) {
            return -2;
        }
    }

    // Verify that JAVA_HOME points to a valid location
    if (envJavaHome != NULL) {
        if (realpath(envJavaHome, tempJavaHome) == NULL) {
            char* msg = getTranslatedMessageById(SERVER_LAUNCHER_29, "JAVA_HOME location %s does not exist\n");
            fprintf(stderr, msg, envJavaHome);
            fprintf(stderr, "\n");
            return -3;
        }
        snprintf(destJavaHome, PATH_MAX, envJavaHome);
        return 0;
    }

    // JAVA_HOME wasn't set, look at $WLP_INSTALL_DIR/../java64
    char java64Path[PATH_MAX + strlen("../java64") + 2];
    sprintf(java64Path, "%s/%s", wlpInstallDir, "../java64");
    if (realpath(java64Path, destJavaHome)) {
        return 0;
    }

    // Well, let's try $WLP_INSTAL_DIR/../java
    char javaPath[PATH_MAX + strlen("../java") + 2];
    sprintf(javaPath, "%s/%s", wlpInstallDir, "../java");
    if (realpath(javaPath, destJavaHome)) {
        return 0;
    }

    return -1;
}

//---------------------------------------------------------------------
// Read the java options files and add the options.  The options file
// could be the JAVAOPTS DD in a PROC or the jvm.options file in the
// multiple server config directories.
//---------------------------------------------------------------------
static void
processJavaVMOptions(const char* wlpInstallDir, const char* wlpServerDir) {
        char JVMOptionsSharedPath[PATH_MAX + 1] = "";
        char * sharedAdditions = "usr/shared";
        snprintf(JVMOptionsSharedPath, sizeof(JVMOptionsSharedPath), "%s/%s/%s", wlpInstallDir, sharedAdditions, JAVA_OPTIONS_FILE_NAME);

        char JVMOptionsDropinsDefaultsPath[PATH_MAX + 1] = "";
        char * defaultAdditions = "configDropins/defaults";
        snprintf(JVMOptionsDropinsDefaultsPath, sizeof(JVMOptionsDropinsDefaultsPath), "%s/%s/%s", wlpServerDir, defaultAdditions, JAVA_OPTIONS_FILE_NAME);

        char JVMOptionsServerRootPath[PATH_MAX + 1] = "";
        snprintf(JVMOptionsServerRootPath, sizeof(JVMOptionsServerRootPath), "%s/%s", wlpServerDir, JAVA_OPTIONS_FILE_NAME);

        char JVMOptionsDropinsOverridesPath[PATH_MAX + 1] = "";
        char * overrideAdditions = "configDropins/overrides";
        snprintf(JVMOptionsDropinsOverridesPath, sizeof(JVMOptionsDropinsOverridesPath), "%s/%s/%s", wlpServerDir, overrideAdditions, JAVA_OPTIONS_FILE_NAME);

        char JVMOptionsETCPath[PATH_MAX + 1] = "";
        char * ETCAdditions = "etc";
        snprintf(JVMOptionsETCPath, sizeof(JVMOptionsETCPath), "%s/%s/%s", wlpInstallDir, ETCAdditions, JAVA_OPTIONS_FILE_NAME);
        int readEtc = 1;

		//If any of the four next files exist they are merged together to start up a server
		//with those options.  Otherwise etc/jvm.options is read
        if(file_exist(JVMOptionsSharedPath)){
            readEtc=0;
            FILE*  JVMOptionsSharedFile = fopen(JVMOptionsSharedPath, "rt");
            processJavaVMOptionsFile(JVMOptionsSharedFile);
            if (ferror(JVMOptionsSharedFile)) {
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_30, "Error reading java options from %s: %s\n");
                char buffer[20];
                sprintf(buffer, "%lx", ferror(JVMOptionsSharedFile));
                fprintf(stderr, msg, JVMOptionsSharedPath, buffer);
                fprintf(stderr, "\n");
            }
            fclose(JVMOptionsSharedFile);
        }
        if(file_exist(JVMOptionsDropinsDefaultsPath)){
            readEtc=0;
            FILE*  JVMOptionsDropinsDefaultsFile = fopen(JVMOptionsDropinsDefaultsPath, "rt");
            processJavaVMOptionsFile(JVMOptionsDropinsDefaultsFile);
            if (ferror(JVMOptionsDropinsDefaultsFile)) {
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_30, "Error reading java options from %s: %s\n");
                char buffer[20];
                sprintf(buffer, "%lx", ferror(JVMOptionsDropinsDefaultsFile));
                fprintf(stderr, msg, JVMOptionsDropinsDefaultsPath, buffer);
                fprintf(stderr, "\n");
            }
            fclose(JVMOptionsDropinsDefaultsFile);
        }
        if(file_exist(JVMOptionsServerRootPath)){
            readEtc=0;
            FILE*  JVMOptionsServerRootFile = fopen(JVMOptionsServerRootPath, "rt");
            processJavaVMOptionsFile(JVMOptionsServerRootFile);
            if (ferror(JVMOptionsServerRootFile)) {
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_30, "Error reading java options from %s: %s\n");
                char buffer[20];
                sprintf(buffer, "%lx", ferror(JVMOptionsServerRootFile));
                fprintf(stderr, msg, JVMOptionsServerRootPath, buffer);
                fprintf(stderr, "\n");
            }
            fclose(JVMOptionsServerRootFile);
        }
        if(file_exist(JVMOptionsDropinsOverridesPath)){
            readEtc=0;
            FILE*  JVMOptionsDropinsOverridesFile = fopen(JVMOptionsDropinsOverridesPath, "rt");
            processJavaVMOptionsFile(JVMOptionsDropinsOverridesFile);
            if (ferror(JVMOptionsDropinsOverridesFile)) {
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_30, "Error reading java options from %s: %s\n");
                char buffer[20];
                sprintf(buffer, "%lx", ferror(JVMOptionsDropinsOverridesFile));
                fprintf(stderr, msg, JVMOptionsDropinsOverridesPath, buffer);
                fprintf(stderr, "\n");
            }
            fclose(JVMOptionsDropinsOverridesFile);
        }
        //The etc/jvm.options is only to be read when none of the previous 4 are read
        if(readEtc==1 && file_exist(JVMOptionsETCPath)){
            FILE*  JVMOptionsETCFile = fopen(JVMOptionsETCPath, "rt");
            processJavaVMOptionsFile(JVMOptionsETCFile);
            if (ferror(JVMOptionsETCFile)) {
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_30, "Error reading java options from %s: %s\n");
                char buffer[20];
                sprintf(buffer, "%lx", ferror(JVMOptionsETCFile));
                fprintf(stderr, msg, JVMOptionsETCPath, buffer);
                fprintf(stderr, "\n");
            }
            fclose(JVMOptionsETCFile);
        }
}

static void
processJavaVMOptionsFile(FILE* optionsFile) {
    int firstChar = getFirstCharacter(optionsFile);
    unsigned char ebcdicFile = isValidFirstOptionCharEbcdic(firstChar) || !isValidFirstOptionCharAscii(firstChar);

    // Read each line of the file (up to 4k/line)
    char option[4096];
    while (!feof(optionsFile)) {
        // Read the line
        if (readLine(optionsFile, option, sizeof(option), ebcdicFile)) {
            break;
        }

        // Eat empty lines
        if (strlen(option) == 0) {
            continue;
        }

        addJavaOption(option, NULL);
    }
}

static int
file_exist(char *filename){
  struct stat   buffer;
  return (stat (filename, &buffer) == 0);
}

//---------------------------------------------------------------------
// Check for hyphen, hash, or new line
//---------------------------------------------------------------------
static unsigned char
isValidFirstOptionCharEbcdic(int ch) {
    switch (ch) {
        case ' ':
        case '#':
        case '-':
        case '\n':
        case '\t':
            return 1;
    }

    return 0;
}
//---------------------------------------------------------------------
// Check for a letter, number, or an underscore
//---------------------------------------------------------------------
static unsigned char
isValidFirstEnvironmentCharEbcdic(int ch) {
    // If we using for the environment, we need to support letters,
    // numbers, and the underscore
    if (isalnum(ch)) {
        return 1;
    } else if (ch == '_') {
        return 1;
    } else if (isValidFirstOptionCharEbcdic(ch) && ch != '-') {
        return 1;
    }
    return 0;
}

//---------------------------------------------------------------------
// Check for hyphen, hash, carriage return, or line feed
//---------------------------------------------------------------------
static unsigned char
isValidFirstOptionCharAscii(int ch) {
#pragma convert("ISO8859-1")
    switch (ch) {
        case ' ':
        case '#':
        case '-':
        case '\n':
        case '\r':
        case '\t':
            return 1;
    }
#pragma convert(pop)
    return 0;
}

//---------------------------------------------------------------------
// Check for a letter, number, or an underscore
//---------------------------------------------------------------------
static unsigned char
isValidFirstEnvironmentCharAscii(int ch) {
#pragma convert("ISO8859-1")
    // ASCII based encodings use contiguous code points for
    // letters and numbers
    if (ch >= 'A' && ch <= 'Z') {
        return 1;
    }
    if (ch >= 'a' && ch <= 'z') {
        return 1;
    }
    if (ch >= '0' && ch <= '9') {
        return 1;
    }
    if (ch == '_') {
        return 1;
    }
    if (ch == '=') {
        return 1;
    }
    if (isValidFirstOptionCharAscii(ch) && ch != '-') {
        return 1;
    }
#pragma convert(pop)
    return 0;
}

//---------------------------------------------------------------------
// Add the specified option data to the options array.
//---------------------------------------------------------------------
static int
addJavaOption(char* option, void* extra) {
    if (optionCount >= allocatedOptions) {
        expandOptionsArray();
    }
    jvmOptions[optionCount].extraInfo = extra;
    jvmOptions[optionCount].optionString = strdup(option);
    __etoa(jvmOptions[optionCount].optionString);

    return ++optionCount;
}

//---------------------------------------------------------------------
// Increase the size of the global Java options array.
//---------------------------------------------------------------------
static int
expandOptionsArray() {
    // Start with 8 options, TODO: malloc check
    if (allocatedOptions == 0) {
        allocatedOptions = 8;
        jvmOptions = malloc(allocatedOptions * sizeof(JavaVMOption));
        return allocatedOptions;
    }

    int newOptionCount = allocatedOptions + 4;
    JavaVMOption* newAllocation = malloc(newOptionCount * sizeof(JavaVMOption));
    for (int i = 0; i < allocatedOptions; i++) {
        newAllocation[i] = jvmOptions[i];
    }
    free(jvmOptions);
    allocatedOptions = newOptionCount;
    jvmOptions = newAllocation;

    return allocatedOptions;
}

//---------------------------------------------------------------------
// Add the -javaagent option unless WLP_SKIP_BOOTSTRAP_AGENT is set.
//---------------------------------------------------------------------
static void
setupJavaAgent(const char* wlpInstallDir) {
    // Enable performance to run without the agent for comparison
    if (getenv("WLP_SKIP_BOOTSTRAP_AGENT")) {
        return;
    }

    // Add -javaagent:.../bootstrap-agent.jar
    char jvmAgent[PATH_MAX + strlen("-javaagent:") + 4];
    snprintf(jvmAgent, sizeof(jvmAgent), "-javaagent:%s/%s/%s", wlpInstallDir, "lib", BOOTSTRAP_AGENT);
    addJavaOption(jvmAgent, NULL);
}

//---------------------------------------------------------------------
// If IBM_JAVA_OPTIONS isn't set, use it to set the shared class cache
// defaults.  We're doing it this way to remain consistent with the
// script based launcher.
//---------------------------------------------------------------------
static void
setIbmClassCacheDefaults(const char* javaHome, const char* wlpOutputDir) {
    // If the IBM_JAVA_OPTIONS environment variable is set, bail
    if (getenv("IBM_JAVA_OPTIONS")) {
        return;
    }

    // Don't setup the class cache by default if the JVM doesn't
    // support the cacheDirPerm option on shareclasses
    if (!isCacheDirPermSupported(javaHome)) {
        return;
    }

    // Try to use the same options as we have in the script launcher.
    const char* shareClassesOptionTemplate = "-Xshareclasses:name=liberty-%%u,nonfatal,cacheDirPerm=1000,cacheDir=%s/.classCache";

    // Fill in the output directory
    char shareClassesOption[strlen(shareClassesOptionTemplate) + strlen(wlpOutputDir) + 1];
    snprintf(shareClassesOption, sizeof(shareClassesOption), shareClassesOptionTemplate, wlpOutputDir);

    // Set the options
    addJavaOption(shareClassesOption, NULL);
    addJavaOption("-XX:ShareClassesEnableBCI", NULL);
    addJavaOption("-Xscmx60m", NULL);
    addJavaOption("-Xscmaxaot4m", NULL);
}

//---------------------------------------------------------------------
// Look for version.properties in the JDK / JRE tree and examine the
// "sdk.version" property for a level that supports cacheDirPerm
//---------------------------------------------------------------------
static unsigned char
isCacheDirPermSupported(const char* javaHome) {
    char propertiesPath[PATH_MAX + 1];
    FILE* propertiesFile = NULL;

    // Check $JAVA_HOME/lib/version.properties
    snprintf(propertiesPath, sizeof(propertiesPath), "%s/%s", javaHome, "lib/version.properties");
    propertiesFile = fopen(propertiesPath, "rt");

    // Check $JAVA_HOME/jre/lib/version.properties
    if (propertiesFile == NULL) {
        snprintf(propertiesPath, sizeof(propertiesPath), "%s/%s", javaHome, "jre/lib/version.properties");
        propertiesFile = fopen(propertiesPath, "rt");
    }

    // No file, no class cache; true for SDKs before 1.6.0
    if (propertiesFile == NULL) {
        return 0;
    }

    // Look in sdk.properties for the "sdk.version=" property
    char line[8192];
    unsigned char lineFound = 0;
    int versionLength = strlen("sdk.version=");
    while (fgets(line, sizeof(line), propertiesFile)) {
        if (strlen(line) < versionLength) {
            continue;
        }
        if (memcmp("sdk.version=", line, versionLength) == 0) {
            lineFound = 1;
            break;
        }
    }
    fclose(propertiesFile);
    propertiesFile = NULL;
    
    // Not supported if we didn't find the "sdk.version="
    if (!lineFound) {
        return 0;
    }

    // The patterns for JDKs that do not support cacheDirPerm
    const char* unsupportedVersions[] = {
        "sdk.version=*60-*",              // 1.6.0 (GA)
        "sdk.version=*60fp*",
        "sdk.version=*60ifix*",
        "sdk.version=*60sr[1-9]-*",       // 1.6.0, SR1-SR9
        "sdk.version=*60sr[1-9]fp*",
        "sdk.version=*60sr[1-9]ifix*",
        "sdk.version=*60_26-*",           // 1.6.0, R26 (GA)
        "sdk.version=*60_26fp*",
        "sdk.version=*60_26ifix*",
        NULL
    };

    // Supported unless we match a version that does not support
    int supported = 1;

    for (int i = 0; supported && unsupportedVersions[i] != NULL; i++) {
        supported = fnmatch(unsupportedVersions[i], line, 0);
    }

    return supported;
}

//---------------------------------------------------------------------
// Drive the main method in the ws-launch.jar.
//---------------------------------------------------------------------
static int
invokeMain(JNIEnv* env, int argc, char** argv, int scriptLaunch) {
    // Find the main class
    char* mainClassName = strdup(MAIN_CLASS);
    __etoa(mainClassName);

    jclass mainClass = (*env)->FindClass(env, mainClassName);
    if (mainClass == NULL) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_32, "Failed to load main class\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        if ((*env)->ExceptionOccurred(env)) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

#pragma convert("ISO8859-1")
    jmethodID mainMethodId = (*env)->GetStaticMethodID(env, mainClass, "main", "([Ljava/lang/String;)V");
#pragma convert(pop)
    if (mainMethodId == NULL) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_33, "Failed to locate main method\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        if ((*env)->ExceptionOccurred(env)) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

#pragma convert("ISO8859-1")
    jclass stringClass = (*env)->FindClass(env, "java/lang/String");
#pragma convert(pop)
    if (stringClass == NULL) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_34, "Failed to load java.lang.String class\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        if ((*env)->ExceptionOccurred(env)) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

    // Create the String[] with a length equal to the arg list (except argv[0] and '-scriptLaunch' if it exists)
    jobjectArray jargs = (*env)->NewObjectArray(env, argc - (1 + scriptLaunch), stringClass, NULL);
    if (jargs == NULL) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_35, "Failed to create main method's parameter list\n");
        fprintf(stderr, msg);
        fprintf(stderr, "\n");
        if ((*env)->ExceptionOccurred(env)) {
            (*env)->ExceptionDescribe(env);
        }
        return JNI_ERR;
    }

    // Pass along the launcher arguments except argv[0] and -scriptLaunch if it is set
    for (int i = 1; i < argc; i++) {
        //Add all but '-scriptLaunch' to the jargs array
        if ( strncmp("-scriptLaunch", argv[i], 14) != 0){
            jstring arg = createJavaString(env, argv[i]);
            if (arg == NULL) {
                char* msg = getTranslatedMessageById(SERVER_LAUNCHER_35, "Failed to create main method's parameter list\n");
                fprintf(stderr, msg);
                fprintf(stderr, "\n");
                if ((*env)->ExceptionOccurred(env)) {
                    (*env)->ExceptionDescribe(env);
                }
                return JNI_ERR;
            }


            (*env)->SetObjectArrayElement(env, jargs, i - 1, arg);
            if ((*env)->ExceptionOccurred(env)) {
                (*env)->ExceptionDescribe(env);
                return JNI_ERR;
            }
        }
    }

    // Call the main; this will block until the JVM terminates
    (*env)->CallStaticVoidMethod(env, mainClass, mainMethodId, jargs);

    return JNI_OK;
}

//---------------------------------------------------------------------
// Set the default file.encoding for the JVM if we don't see one set
//---------------------------------------------------------------------
static void
setupDefaultFileEncoding() {
#pragma convert("ISO8859-1")
    const char* encodingOption = "-Dfile.encoding=";
    int encodingOptionLength = strlen(encodingOption);
#pragma convert(pop)

    // Look in existing options for -Dfile.encoding={something}
    unsigned char foundEncoding = 0;
    for (int i = 0; i < optionCount; i++) {
        // Must be longer than encoding option to ensure something after =
        if (strlen(jvmOptions[i].optionString) <= encodingOptionLength) {
            continue;
        } else if (memcmp(encodingOption, jvmOptions[i].optionString, encodingOptionLength) == 0) {
            foundEncoding = 1;
            break;
        }
    }

    // If not found, specify iso8859-1
    if (!foundEncoding) {
        addJavaOption("-Dfile.encoding=iso8859-1", NULL);
    }
}

//---------------------------------------------------------------------
// Create a java string from an EBCDIC string
//---------------------------------------------------------------------
static jstring
createJavaString(JNIEnv* env, const char* ebcdicString) {
    char* tempString = strdup(ebcdicString);
    __etoa(tempString);

    jstring javaString = (*env)->NewStringUTF(env, tempString);
    if (javaString == NULL) {
        free(tempString);
        if ((*env)->ExceptionOccurred(env)) {
            (*env)->ExceptionDescribe(env);
        }
    }

    return javaString;
}

//---------------------------------------------------------------------
// Update some environment options
//---------------------------------------------------------------------
static void
updateEnvironment() {
    int returnCode = 0;

    // USS can propagate an enclave when a process is spun off.
    // This is bad if you spin off a process from something running
    // under one of our enclaves because we'll delete it when the
    // request/tran completes which may have nothing to do with the
    // spun off process and its enclave would get deleted out from
    // under it. USS provided an environment variable to allow us to
    // tell them to stop doing this.  So we need to set it.
    returnCode = setenv("_BPXK_WLM_PROPAGATE", "NO", 1);
    if (returnCode) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_36, "Unable to set _BPXK_WLM_PROPAGATE=NO");
        perror(msg);
    }

    // Force java threads to be heavy weight threads.
    // Medium weight threads which is the default for java threads
    // cause us problems when they end because the TCB is reused.
    // Since the TCB is reused our task level resource manager and
    // thread termination exits will not get control to cleanup
    // thread level resources.  Also, thread specific information
    // would be shared across threads causing all sort of problems.
    returnCode = setenv("JAVA_THREAD_MODEL", "HEAVY", 1);
    if (returnCode) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_37, "Unable to set JAVA_THREAD_MODEL=HEAVY");
        perror(msg);
    }

    // This env variable controls whether the JVM propagates
    // things like enclave and contexts to a sub-thread when
    // it creates one.  We don't want it to do that
    // because it can leave the wrong context on the subthread
    // and leaves an enclave in use essentially forever so that
    // our attempt to delete it when we
    // think we are done with it fails.
    returnCode = setenv("JAVA_PROPAGATE", "NO", 1);
    if (returnCode) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_38, "Unable to set JAVA_PROPAGATE=NO");
        perror(msg);
    }

    // Make sure that java honors the pthread_yield variable
    char* yieldValue = getenv("_EDC_PTHREAD_YIELD");
    if (yieldValue == NULL) {
        returnCode = setenv("_EDC_PTHREAD_YIELD", "-2", 1);
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_39, "Unable to set default value for _EDC_PTHREAD_YIELD");
        perror(msg);
    }
}

//---------------------------------------------------------------------
// If the umask of the process is 0, override the umask to 022.
// If the WLP_SKIP_UMASK variable is set then do not set umask
//---------------------------------------------------------------------
static void
updateUmask() {
    char* wlpSkipUmask = getenv("WLP_SKIP_UMASK");
    if (wlpSkipUmask == NULL) {
        // others dont have any permission
        mode_t defaultMask = S_IRWXO;

        // Get the current mask by setting a new mask
        mode_t initialMask = umask(defaultMask);
        if (initialMask != 0) {
            umask(initialMask | defaultMask);
        }
    }
}

//---------------------------------------------------------------------
// Change the working directory to the specified path
//---------------------------------------------------------------------
int
setWorkingDirectory(const char* dirPath) {
    if (chdir(dirPath) != 0) {
        char* msg = getTranslatedMessageById(SERVER_LAUNCHER_40, "Unable to set working directory");
        perror(msg);
        return -1;
    }

    return 0;
}

//---------------------------------------------------------------------
// Issue usage message when invoked with wrong number of parameters
//---------------------------------------------------------------------
static void printUsage() {
    char* msg = getTranslatedMessageById(SERVER_LAUNCHER_41, "Usage: bbgzsrv [liberty arguments] serverName\n");
    fprintf(stderr, msg);
    fprintf(stderr, "\n");
}
