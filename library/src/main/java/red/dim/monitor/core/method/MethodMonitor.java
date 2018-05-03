package red.dim.monitor.core.method;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by dim on 18/03/12.
 */
public class MethodMonitor implements IMethodMonitor {

    private static final String TAG = "MethodMonitor";

    protected List<Advice> advices = new ArrayList<>();
    private List<IMethodMonitor> methodListeners;

    public MethodMonitor(List<IMethodMonitor> methodListeners, List<Advice> advices) {
        this.methodListeners = methodListeners;
        if (advices == null) {
            return;
        }
        this.advices.addAll(advices);
    }

    public boolean hotMethodEnter(int method) {
        List<Advice> advices = this.advices;
        for (Advice advice : advices) {
            if (advice.methodId == method) {
                return advice.before;
            }
        }
        return false;
    }

    public boolean hotMethodReturn(int method) {
        List<Advice> advices = this.advices;
        for (Advice advice : advices) {
            if (advice.methodId == method) {
                return advice.after;
            }
        }
        return false;
    }


    public void methodEnter(Point point) {
        for (IMethodMonitor methodListener : methodListeners) {
            methodListener.methodEnter(point);
        }
    }

    public void methodReturn(Point point, int methodId) {
        for (IMethodMonitor methodListener : methodListeners) {
            methodListener.methodReturn(point, methodId);
        }
    }
}
