package red.dim.monitor.core.method;
/**
 * Created by dim on 18/03/12.
 */
public interface IMethodMonitor {
    void methodEnter(Point point);
    void methodReturn(Point point, int methodId);
}
