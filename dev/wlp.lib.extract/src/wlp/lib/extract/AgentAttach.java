package wlp.lib.extract;

import java.lang.management.ManagementFactory;

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
            Class<?> VirtualMachine = Class.forName("com.sun.tools.attach.VirtualMachine");
            //VirtualMachine vm = VirtualMachine.attach(jvmName);
            Object vm = VirtualMachine.getMethod("attach", String.class).invoke(null, jvmName);
            //vm.loadAgent(agent);
            vm.getClass().getMethod("loadAgent", String.class).invoke(vm, agent);
            //vm.detach();
            vm.getClass().getMethod("detach").invoke(vm);
        } catch (Exception e) {
            return e;
        }

        return null;
    }
}
