package red.dim.monitor.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import red.dim.monitor.core.method.Advice;
import red.dim.monitor.core.method.Aspect;
import red.dim.monitor.core.method.IMethodMonitor;
import red.dim.monitor.core.method.MethodMonitor;

/**
 * Created by dim on 18/03/12.
 */
public class Monitor {

    private static Monitor sMonitorInstant = new Monitor();
    private boolean setup = false;

    public static Monitor getInstance() {
        return sMonitorInstant;
    }

    private List<IMethodMonitor> monitors = new ArrayList<>();
    private HashMap<String, OperateMethod> methodMap = new HashMap<>();

    public static void setup(List<IMethodMonitor> monitors) {
        sMonitorInstant.monitors.addAll(monitors);
        sMonitorInstant.setup = true;
    }

    public void execute(List<Aspect> aspects, boolean reset) {
        if (reset) {
            reset();
        }
        for (Aspect aspect : aspects) {
            execute(aspect);
        }
    }

    private void reset() {
        synchronized (Monitor.class) {
            for (OperateMethod operateMethod : methodMap.values()) {
                operateMethod.reset();
            }
        }
    }

    private void execute(Aspect aspect) {
        if (!setup) {
            throw new IllegalStateException("not setup");
        }
        OperateMethod operateMethod = methodMap.get(aspect.target);
        if (operateMethod == null) {
            try {
                Class<?> targetClass = Class.forName(aspect.target);
                Field sMonitor = targetClass.getField("s_Monitor_1");
                OperateMethod method = new OperateMethod(monitors, aspect.advices);
                sMonitor.set(null, method);
                synchronized (Monitor.class) {
                    methodMap.put(aspect.target, method);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            operateMethod.addAdvices(aspect.advices);
        }
    }

    class OperateMethod extends MethodMonitor {

        OperateMethod(List<IMethodMonitor> methodListeners, List<Advice> advice) {
            super(methodListeners, advice);
        }

        void addAdvices(List<Advice> advices) {
            if (advices == null || advices.size() == 0) {
                return;
            }
            this.advices = advices;
        }

        void reset() {
            advices = new ArrayList<>();
        }
    }
}
