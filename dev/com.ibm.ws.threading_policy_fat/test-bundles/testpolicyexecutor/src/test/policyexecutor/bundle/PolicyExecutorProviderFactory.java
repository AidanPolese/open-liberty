package test.policyexecutor.bundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;

import com.ibm.ws.threading.PolicyExecutorProvider;
import com.ibm.wsspi.resource.ResourceFactory;
import com.ibm.wsspi.resource.ResourceInfo;

/**
 *
 */
@Component(name = "TestPolicyExecutorProvider", configurationPolicy = ConfigurationPolicy.IGNORE, immediate = true, property = { "jndiName=test/TestPolicyExecutorProvider" })
public class PolicyExecutorProviderFactory implements ResourceFactory {

    @Reference
    private PolicyExecutorProvider provider;

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.wsspi.resource.ResourceFactory#createResource(com.ibm.wsspi.resource.ResourceInfo)
     * //
     */
    @Override
    public Object createResource(ResourceInfo arg0) throws Exception {
        return provider;
    }

}
