package red.dim.monitor.plugin;

/**
 * Created by dim on 17/10/14.
 */

class MonitorExtension {

    public static final String NAME = "monitor";
    Set<String> packageMap = new HashSet<>();
    Set<String> blackPackageMap = new HashSet<>();

    MonitorExtension() {
        blackPackageList("red.dim.monitor.core");
    }

    MonitorExtension packageList(String key) {
        packageMap.add(key.replace(".", File.separator));
        return this;
    }

    MonitorExtension blackPackageList(String key) {
        blackPackageMap.add(key.replace(".", File.separator));
        return this;
    }
}
