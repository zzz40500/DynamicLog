package red.dim.monitor.plugin.entity;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by dim on 17/10/14.
 */
public class MonitorClass {
    public String className;
    private List<MonitorMethod> monitorMethods = new ArrayList<>();

    public void setClassName(String className) {
        this.className = className;
    }

    public void addMethod(MonitorMethod item) {
        monitorMethods.add(item);
    }

    public List<MonitorMethod> getMonitorMethods() {
        return monitorMethods;
    }

    public String getClassName() {
        return className;
    }

    public boolean isEmpty() {
        return monitorMethods.isEmpty();
    }
}
