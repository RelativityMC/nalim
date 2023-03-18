package one.nalim;

import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TestUtil {

    public static void init() {
    }

    static {
        openJvmciPackages(ByteBuddyAgent.install());
    }

    private static void openJvmciPackages(Instrumentation inst) {
        Optional<Module> jvmciModule = ModuleLayer.boot().findModule("jdk.internal.vm.ci");
        if (jvmciModule.isEmpty()) {
            throw new IllegalStateException("JVMCI module not found. Use -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI");
        }

        Set<Module> unnamed = Set.of(
                TestUtil.class.getModule()
        );

        Map<String, Set<Module>> extraExports = Map.of(
                "jdk.vm.ci.code", unnamed,
                "jdk.vm.ci.code.site", unnamed,
                "jdk.vm.ci.hotspot", unnamed,
                "jdk.vm.ci.meta", unnamed,
                "jdk.vm.ci.runtime", unnamed
        );

        inst.redefineModule(jvmciModule.get(), Collections.emptySet(), extraExports,
                Collections.emptyMap(), Collections.emptySet(), Collections.emptyMap());
    }

}
