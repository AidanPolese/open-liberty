package wlp.lib.extract;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * Created by nottinga on 1/20/17.
 */
public class AgentAttach {
    public static Object attach(String agent) {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();

        int index = jvmName.indexOf('@');
        if (index > -1) {
            jvmName = jvmName.substring(0, index);
        }

        try {
            VirtualMachine vm = VirtualMachine.attach(jvmName);
            vm.loadAgent(agent);
            vm.detach();
        } catch (Exception e) {
            return e;
        }

        return null;
    }
}
