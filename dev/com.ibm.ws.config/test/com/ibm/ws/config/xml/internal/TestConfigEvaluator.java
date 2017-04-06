package com.ibm.ws.config.xml.internal;

import java.util.Dictionary;

import com.ibm.websphere.config.ConfigEvaluatorException;
import com.ibm.ws.config.admin.ConfigurationDictionary;
import com.ibm.ws.config.xml.internal.MetaTypeRegistry.RegistryEntry;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;

public class TestConfigEvaluator extends ConfigEvaluator {

    /**
     * @param retriever
     * @param registry
     * @param variableRegistry
     * @param wsLocation TODO
     */
    public TestConfigEvaluator(ConfigRetriever retriever, MetaTypeRegistry registry, ConfigVariableRegistry variableRegistry, WsLocationAdmin wsLocation) {
        super(retriever, registry, variableRegistry, new ServerXMLConfiguration(null, wsLocation, null));
    }

    /**
     * Evaluates the configuration element into a Dictionary object where the
     * values are of String or String[] type.
     * 
     * @throws ConfigEvaluatorException
     */
    public Dictionary<String, Object> evaluateToDictionary(ConfigElement config) throws ConfigEvaluatorException {
        return evaluateToDictionary(config, null);
    }

    /**
     * Evaluates the configuration element into a Dictionary object. Values are
     * converted into
     * types as defined by the MetaType information (if available). Otherwise,
     * values will be
     * of String or String[] type.
     * 
     * @throws ConfigEvaluatorException
     */
    public Dictionary<String, Object> evaluateToDictionary(ConfigElement config, RegistryEntry registryEntry) throws ConfigEvaluatorException {

        EvaluationResult result = evaluate(config, registryEntry);
        if (!result.isValid())
            return new ConfigurationDictionary();

        Dictionary<String, Object> map = result.getProperties();
        if (!result.getReferences().isEmpty()) {
            map.put(XMLConfigConstants.CFG_CONFIG_REFERENCES, result.getReferences());
        }
        return map;
    }

}