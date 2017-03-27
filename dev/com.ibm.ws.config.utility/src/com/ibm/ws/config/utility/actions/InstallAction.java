/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility.actions;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ibm.websphere.crypto.InvalidPasswordEncodingException;
import com.ibm.websphere.crypto.PasswordUtil;
import com.ibm.websphere.crypto.UnsupportedCryptoAlgorithmException;
import com.ibm.ws.config.utility.ConfigUtilityAction;
import com.ibm.ws.config.utility.IFileUtility;
import com.ibm.ws.config.utility.TaskErrorException;
import com.ibm.ws.config.utility.utils.CommandUtils;
import com.ibm.ws.config.utility.utils.ConsoleWrapper;
import com.ibm.ws.config.utility.utils.RepositoryAccessUtility;
import com.ibm.ws.config.utility.utils.VariableSubstituter;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.repository.exceptions.RepositoryException;

/**
 *
 */
public class InstallAction implements ConfigUtilityAction {
    private final IFileUtility fileUtility;
    private final String scriptName;

    /** Constant: AES encoding */
    static final String AES_ENCODING = "aes";
    /** Constant: XOR encoding */
    static final String XOR_ENCODING = "xor";

    static final String ARG_OPT_INFO = "--info";

    //Use local file option
    static final String ARG_OPT_LOCAL = "--useLocalFile";

    //Encoding configurable
    static final String ARG_OPT_ENCODING = "--encoding";
    static final String ARG_OPT_KEY = "--key";

    //CreateConfigFile Option
    static final String ARG_REQ_CREATE_CONFIG_FILE = "--createConfigFile";

    static final String VAR_ADMIN_PASSWORD = "adminPassword";
    static final String VAR_KEYSTORE_PASSWORD = "keystorePassword";

    static final String VAR_HOST_NAME = "hostName";
    static final String VAR_HTTP_PORT = "httpPort";
    static final String VAR_HTTPS_PORT = "httpsPort";

    static final String VAR_WRITE_PATH = "writePath";
    static final String VAR_READ_PATH = "readPath";

    private final Map<String, String> defaultValues = new HashMap<String, String>();
    private final Map<String, String> multiValuedVariables = new HashMap<String, String>();;

    /** Platform-dependent slash and line separator */
    static final String SLASH = "/";
    static final String NL = System.getProperty("line.separator");
    public static final char EXTENSION_SEPARATOR = '.';
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';

    private ConsoleWrapper stdin;
    private PrintStream stdout;
    private PrintStream stderr;
    private final Collection<String> knownArgs = new HashSet<String>();
    private final Collection<String> promptableArgs = new HashSet<String>();
    private final Collection<String> encodableVariables = new HashSet<String>();

    public InstallAction(String scriptName, IFileUtility fileUtility) {
        this.scriptName = scriptName;
        this.fileUtility = fileUtility;

        // Set the set of promptable arguments (do not require a =value)
        promptableArgs.add(VAR_ADMIN_PASSWORD);
        promptableArgs.add(VAR_KEYSTORE_PASSWORD);

        // Variables which should be encoded
        encodableVariables.add(VAR_ADMIN_PASSWORD);
        encodableVariables.add(VAR_KEYSTORE_PASSWORD);

        // Set the set of known arguments
        knownArgs.add(ARG_OPT_LOCAL);
        knownArgs.add(ARG_OPT_INFO);
        knownArgs.add(ARG_REQ_CREATE_CONFIG_FILE);
        knownArgs.add(ARG_OPT_ENCODING);
        knownArgs.add(ARG_OPT_KEY);

        // Populate the default value map
        defaultValues.put(VAR_HOST_NAME, "*");
        defaultValues.put(VAR_HTTP_PORT, "9080");
        defaultValues.put(VAR_HTTPS_PORT, "9443");

        multiValuedVariables.put(VAR_READ_PATH, "</readDir>" + NL + "        <readDir>%s");
        multiValuedVariables.put(VAR_WRITE_PATH, "</writeDir>" + NL + "        <writeDir>%s");
    }

    /**
     * {@inheritDoc}
     *
     * @throws InstallException
     * @throws RepositoryException
     */
    @Override
    public void handleAction(ConsoleWrapper stdin, PrintStream stdout, PrintStream stderr, String[] args) throws TaskErrorException, RepositoryException, InstallException {
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;

        String snippetName;
        StringBuilder snippetText = null;
        Boolean localFile = false;

        localFile = checkForArgument(ARG_OPT_LOCAL, args);
        if (localFile) {
            snippetName = "localConfigSnippet";
            snippetText = readConfigSnippet(getArgumentValue(ARG_OPT_LOCAL, args, null));
        } else {
            if (args.length < 2) {
                abort(getMessage("missingConfigSnippetName"));
            }
            snippetName = args[1];

            if (!looksLikeSnippetName(snippetName)) {
                abort(getMessage("missingConfigSnippetName"));
            }

            stdout.println(getMessage("generate.download"));
            snippetText = retrieveConfigSnippet(snippetName);

        }

        if (snippetText == null) {
            abort(getMessage("download.invalidSnippet"));
        }

        // Get all variables in the config snippet
        List<String> snippetVariables = VariableSubstituter.getAllVariables(snippetText);

        // Validate arguments
        knownArgs.addAll(snippetVariables);
        validateArgumentList(args, snippetVariables, localFile);

        if (checkForArgument(ARG_OPT_INFO, args)) {
            if (!snippetName.equals("localConfigSnippet")) {
                stdout.println(NL + RepositoryAccessUtility.getConfigSnippetDescription(snippetName));
                stdout.println(getMessage("info.allVariables"));
                stdout.println(snippetVariables.toString());
                return;
            } else {
                stdout.println(getMessage("info.allVariables"));
                stdout.println(snippetVariables.toString());
                return;
            }
        }

        // Create a map between the variables and the values should be substitute with
        HashMap<String, String> substitutionMap = createVariableSubstitutionMap(args, snippetVariables);

        // Get and validate encoding options
        String encoding = getArgumentValue(ARG_OPT_ENCODING, args, XOR_ENCODING);
        String key = getArgumentValue(ARG_OPT_KEY, args, null);
        validateEncoding(encoding, key);

        // Encode passwords
        encodeAllEncodableVariables(substitutionMap, encoding, key);

        // Substitute variables in the config snippet
        StringBuilder sb = VariableSubstituter.substitute(snippetText, substitutionMap);

        // Write to file or stdout
        stdout.println(NL + createConfigFile(args, snippetName, sb.toString()) + NL);
        stdout.println(getMessage("generate.configureSecurity"));
    }

    @Override
    public String getActionName() {
        return "install";
    }

    private Boolean looksLikeSnippetName(String arg) {
        if (arg == null || arg.isEmpty() || arg.startsWith("-")) {
            return false;
        }
        return true;
    }

    private Boolean checkForArgument(String arg, String[] args) {
        String argName = getArgName(arg);
        for (int i = 0; i < args.length; i++) {
            String currentArgName = getArgName(args[i]);
            if (currentArgName.equalsIgnoreCase(argName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param configName
     * @return
     * @throws TaskErrorException
     * @throws InstallException
     */
    private StringBuilder retrieveConfigSnippet(String configName) throws TaskErrorException, InstallException {
        StringBuilder snippet = null;
        try {
            snippet = RepositoryAccessUtility.getConfigSnippet(configName);
        } catch (RepositoryException e) {
            abort(getMessage("download.failedRepoAccess"));
        } catch (IOException e) {
            abort(getMessage("download.ioError"));
        }

        return snippet;
    }

    private void abort(String message) throws TaskErrorException {
        stdout.println(getMessage("generate.abort"));
        throw new TaskErrorException(message);
    }

    /**
     * Validates that there are no unknown arguments or values specified
     * to the task.
     *
     * @param args the arguments to the task
     * @throws IllegalArgumentException if an argument is defined is unknown
     */
    private void validateArgumentList(String[] args, List<String> snippetVariables, Boolean isLocalFile) {

        if (isLocalFile) {
            if (args.length > 3 && checkForArgument(ARG_OPT_INFO, args)) {
                throw new IllegalArgumentException(getMessage("info.invalidUsage"));
            }

            // Arguments and values come in pairs (expect -password).
            // Anything outside of that pattern is invalid.
            // Loop through, jumping in pairs except when we encounter
            // -password -- that may be an interactive prompt which won't
            // define a value.
            for (int i = 1; i < args.length; i++) {
                String argName = getArgName(args[i]);
                String value = getValue(args[i]);

                if (!isKnownArgument(argName)) {
                    throw new IllegalArgumentException(getMessage("invalidArg", argName));
                } else {
                    if (!promptableArgs.contains(argName) && value == null && !isInfoArg(argName)) {
                        throw new IllegalArgumentException(getMessage("missingValue", argName));
                    }
                }
            }
        } else {
            if (args.length > 3 && isInfoArg(getArgName(args[1]))) {
                throw new IllegalArgumentException(getMessage("info.invalidUsage"));
            }

            // Arguments and values come in pairs (expect -password).
            // Anything outside of that pattern is invalid.
            // Loop through, jumping in pairs except when we encounter
            // -password -- that may be an interactive prompt which won't
            // define a value.
            for (int i = 2; i < args.length; i++) {
                String argName = getArgName(args[i]);
                String value = getValue(args[i]);

                if (!isKnownArgument(argName)) {
                    throw new IllegalArgumentException(getMessage("invalidArg", argName));
                } else {
                    if (!promptableArgs.contains(argName) && value == null && !isInfoArg(argName)) {
                        throw new IllegalArgumentException(getMessage("missingValue", argName));
                    }
                }
            }
        }
    }

    private String getMessage(String key, Object... args) {
        return CommandUtils.getMessage(key, args);
    }

    /**
     * Checks if the argument is a known argument to the task.
     *
     * @param arg
     * @return
     */
    private boolean isKnownArgument(String arg) {
        final String argName = getArgName(arg);
        for (String key : knownArgs) {
            if (key.equalsIgnoreCase(argName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the argument name for the given --name=value pair.
     *
     * @param arg
     * @return
     */
    private String getArgName(String arg) {
        if (isValueReplacementArg(arg)) {
            return arg.substring(3).split("=")[0];
        } else {
            return arg.split("=")[0];
        }
    }

    private HashMap<String, String> createVariableSubstitutionMap(String[] args, List<String> allVariables) {
        HashMap<String, String> substitutionMap = new HashMap<String, String>();
        String argumentValue;
        for (String var : allVariables) {
            if (multiValuedVariables.containsKey(var)) {
                List<String> valueList = getArgumentList(var, args, null);

                argumentValue = null;
                if (valueList != null) {
                    StringBuilder buffer = new StringBuilder();

                    buffer.append(valueList.get(0));

                    for (int i = 1; i < valueList.size(); i++) {
                        buffer.append(String.format(multiValuedVariables.get(var), valueList.get(i)));
                    }
                    argumentValue = buffer.toString();
                }
            } else {
                argumentValue = getArgumentValue(var, args, defaultValues.get(var));
            }

            substitutionMap.put(var, argumentValue);
        }

        return substitutionMap;
    }

    private void encodeAllEncodableVariables(HashMap<String, String> replacements, String encoding, String key) {
        for (Entry<String, String> entry : replacements.entrySet()) {
            if (encodableVariables.contains(entry.getKey()) && entry.getValue() != null && !entry.getValue().isEmpty()) {
                String encodedPassword = encodePassword(entry.getKey(), entry.getValue(), encoding, key);
                replacements.put(entry.getKey(), encodedPassword);
            }
        }
    }

    private boolean isValueReplacementArg(String arg) {
        return arg.startsWith("--v") || arg.startsWith("--V");
    }

    /**
     * Validate the encoding parameters specified.
     *
     * @param encoding
     * @param key
     * @throws TaskErrorException
     */
    private void validateEncoding(String encoding, String key) throws TaskErrorException {
        if (AES_ENCODING.equals(encoding) && (key == null)) {
            abort(getMessage("encoding.aesRequiresKey"));
        } else if (XOR_ENCODING.equals(encoding) && (key != null)) {
            abort(getMessage("encoding.xorDoesNotSupportKey"));
        } else if (!XOR_ENCODING.equals(encoding) && !AES_ENCODING.equals(encoding) && !isSupportedCustomEncoding(encoding)) {
            abort(getMessage("encoding.unsupportedEncoding", encoding));
        }
    }

    /**
     * Validate the specified custom encoding is supported.
     *
     * @param encoding
     */
    private boolean isSupportedCustomEncoding(String encoding) {
        if (encoding != null && encoding.length() > 0 && PasswordUtil.isValidCryptoAlgorithm(encoding)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the value for the specified argument String. If the default
     * value argument is null, it indicates the argument is required.
     *
     * No validation is done in the format of args as it was done previously.
     *
     * @param arg Argument name to resolve a value for
     * @param args List of arguments
     * @param defaultValue Default value if the argument is not specified
     * @return Value of the argument
     * @throws IllegalArgumentException if the argument is defined but no value
     *             is given.
     */
    private String getArgumentValue(String arg, String[] args, String defaultValue) {
        String argName = getArgName(arg);
        for (int i = 0; i < args.length; i++) {
            String currentArgName = getArgName(args[i]);
            if (currentArgName.equalsIgnoreCase(argName)) {
                String value = getValue(args[i]);
                if (value == null && promptableArgs.contains(argName)) {
                    return promptForText(arg);
                } else {
                    return value;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Gets the list of values for the specified argument String.
     *
     * No validation is done in the format of args as it was done previously.
     *
     * @param arg Argument name to resolve a value for
     * @param args List of arguments
     * @param defalt Default value if the argument is not specified
     * @return Value of the argument
     * @throws IllegalArgumentException if the argument is defined but no value
     *             is given.
     */
    protected List<String> getArgumentList(String arg, String[] args, List<String> defalt) {
        List<String> ret = new ArrayList<String>();
        String argName = getArgName(arg);

        for (int i = 0; i < args.length; i++) {
            String currentArgName = getArgName(args[i]);
            if (currentArgName.equalsIgnoreCase(argName)) {
                String value = getValue(args[i]);
                ret.add(value);
            }
        }

        if (ret.isEmpty()) {
            return defalt;
        } else {
            return ret;
        }
    }

    /**
     * Returns the value at i+1, guarding against ArrayOutOfBound exceptions.
     * If the next element is not a value but an argument flag (starts with -)
     * return null.
     *
     * @return String value as defined above, or {@code null} if at end of args.
     */
    private String getValue(String arg) {
        String[] split = arg.split("=");
        if (split.length == 1) {
            return null;
        } else if (split.length == 2) {
            // Handle case where value ends with equals
            if (arg.endsWith("=")) {
                return split[1] + "=";
            }
            return split[1];
        } else {
            // Handle DN case with multiple =s
            StringBuffer value = new StringBuffer();
            for (int i = 1; i < split.length; i++) {
                value.append(split[i]);
                if (i < (split.length - 1)) {
                    value.append("=");
                }
            }
            // Handle case where value ends with equals
            if (arg.endsWith("="))
                value.append("=");

            return value.toString();
        }
    }

    /**
     * Prompt the user to enter text to encode. Prompts
     * twice and compares to ensure it was entered correctly.
     *
     * @return Entered String
     */
    private String promptForText(String arg) {
        String read1 = stdin.readMaskedText(getMessage("password.enterText", arg) + " ");
        String read2 = stdin.readMaskedText(getMessage("password.reenterText", arg) + " ");
        if (read1 == null && read2 == null) {
            throw new IllegalArgumentException("Unable to read either entry. Aborting prompt.");
        } else if (read1 == null || read2 == null) {
            stdout.println(getMessage("password.readError"));
            return promptForText(arg);
        } else if (read1.equals(read2)) {
            return read1;
        } else {
            stdout.println(getMessage("password.entriesDidNotMatch"));
            return promptForText(arg);
        }
    }

    /**
     * Encodes the specified password. If the password can not be encoded, an
     * error message will be printed and {@code null} is returned.
     *
     * @param password
     * @param arg
     * @return
     */
    private String encodePassword(String password, String arg, String encoding, String key) {
        String encoded = null;
        try {
            encoded = PasswordUtil.encode(password, encoding, key);
        } catch (InvalidPasswordEncodingException e) {
            e.printStackTrace(stderr);
        } catch (UnsupportedCryptoAlgorithmException e) {
            e.printStackTrace(stderr);
        }
        if (encoded == null) {
            stdout.println(getMessage("common.encodeError", arg));
        }
        return encoded;
    }

    private StringBuilder readConfigSnippet(String filePath) throws TaskErrorException {
        if (filePath == null || filePath.isEmpty()) {
            abort(getMessage("fileUtility.emptyPath"));
        }

        File localConfigSnippet = new File(filePath);

        if (!fileUtility.exists(localConfigSnippet)) {
            abort(getMessage("fileUtility.fileNotFound"));
        }

        StringBuilder configSnippet = null;
        try {
            configSnippet = fileUtility.readFileToStringBuilder(localConfigSnippet);
        } catch (IOException e) {
            abort(getMessage("fileUtility.failedToRead"));
        }

        return configSnippet;
    }

    /**
     * This method will write the XML snippet to a file and provides an include snippet.
     *
     * @param serverDir Path to the root of the server. e.g. /path/to/wlp/usr/servers/myServer/
     * @param args The command-line arguments.
     * @param xmlSnippet The XML configuration the task produced.
     * @return An include snippet or the given xmlSnippet.
     * @throws TaskErrorException
     * @throws Exception
     */
    private String createConfigFile(String[] args, String snippetName, String xmlSnippet) throws TaskErrorException {
        String utilityName = this.scriptName;

        String targetFilepath = getArgumentValue(ARG_REQ_CREATE_CONFIG_FILE, args, null);

        if (targetFilepath == null || targetFilepath.isEmpty()) {
            //This will print to stdout
            return xmlSnippet;
        }

        // note that generateConfigFileName() will handle the case where targetFilepath == null
        File outputFile = generateConfigFileName(utilityName, snippetName, targetFilepath);

        fileUtility.createParentDirectory(stdout, outputFile);
        fileUtility.writeToFile(stderr, xmlSnippet, outputFile);

        String includeSnippet = "    <include location=\"" + getIncludePath(outputFile) + "\" />" + NL;

        return includeSnippet;
    }

    private boolean isInfoArg(String arg) {
        return arg != null && arg.equalsIgnoreCase(ARG_OPT_INFO);
    }

    private File generateConfigFileName(String utilityName, String snippetName, String targetFilepath) throws TaskErrorException {
        // if the file path is a directory, generate a file name
        File outputFile = new File(targetFilepath);
        if (fileUtility.isDirectory(outputFile)) {
            outputFile = new File(outputFile, utilityName + "-" + snippetName + "-include.xml");
        }

        // generate a new file name until we have no conflicts
        if (fileUtility.exists(outputFile)) {
            String filePath = InstallAction.removeExtension(outputFile.getPath());
            String fileExt = InstallAction.getExtension(outputFile.getPath());
            int counter = 1;
            do {
                outputFile = new File(filePath + counter + "." + fileExt);
                counter++;
            } while (fileUtility.exists(outputFile));
        }

        return outputFile;
    }

    /**
     * Returns the output file path. If the outputFile is a child of a
     * known directory, we want to use ${server.config.dir} (or similar)
     * so its not a full hard-coded path.
     *
     * @param outputFile The File of the config file to create.
     * @return The include path to be printed. Either an absolute or variable-relative path.
     */
    private String getIncludePath(File outputFile) {
        File fWLP_USER = new File(fileUtility.getUserDir());
        if (outputFile.getAbsolutePath().startsWith(fWLP_USER.getAbsolutePath())) {
            return outputFile.getAbsolutePath().replace(fWLP_USER.getAbsolutePath(), "${wlp.user.dir}");
        }
        File fWLP_INSTALL = new File(fileUtility.getInstallDir());
        if (outputFile.getAbsolutePath().startsWith(fWLP_INSTALL.getAbsolutePath())) {
            return outputFile.getAbsolutePath().replace(fWLP_INSTALL.getAbsolutePath(), "${wlp.install.dir}");
        }
        return outputFile.getAbsolutePath();
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return (lastSeparator > extensionPos ? -1 : extensionPos);
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }
}
